/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.Comparator;
import java.util.function.Predicate;

/**
 * How the tracked items list is ordered. {@link #MANUAL} keeps the user's drag
 * order; every other mode sorts for display only (within each group when
 * grouping is active) and disables drag reordering. Each mode has a natural
 * direction (Name ascending, value-like modes descending) that the reverse flag
 * flips; items missing the sort key always sort last, regardless of direction.
 *
 * <p>Public because it is the return type of a {@code @ConfigItem} accessor: the
 * RuneLite config proxy lives in another module and must be able to access it, or
 * the plugin fails to start with an {@link IllegalAccessError}.
 */
public enum SortMode
{
	MANUAL("Manual"),
	NAME("Name"),
	VALUE("Value"),
	PROFIT("Profit"),
	CHANGE_24H("24h Change");

	private final String label;

	SortMode(String label)
	{
		this.label = label;
	}

	/**
	 * @param reversed whether to flip this mode's natural direction
	 * @return the display comparator, or {@code null} for {@link #MANUAL}
	 */
	Comparator<TrackedItem> comparator(boolean reversed)
	{
		boolean descending = descending(reversed);
		switch (this)
		{
			case NAME:
				return directed(Comparator.comparing(TrackedItem::getName, String.CASE_INSENSITIVE_ORDER),
						item -> true, descending);
			case VALUE:
				return directed(Comparator.comparingLong(TrackedItem::getAvgValue),
						item -> item.getAvgValue() > 0, descending);
			case PROFIT:
				return directed(Comparator.comparingLong(SortMode::profitKey),
						TrackedItem::isCostBasisInitialized, descending);
			case CHANGE_24H:
				return directed(Comparator.comparingDouble(SortMode::changeKey),
						SortMode::hasChange, descending);
			default:
				return null;
		}
	}

	/** @return whether this mode's effective direction is descending once {@code reversed} is applied. */
	boolean descending(boolean reversed)
	{
		return (this != NAME) ^ reversed;
	}

	/**
	 * Applies the sort direction to an ascending {@code key} comparator while always sorting items
	 * that lack the key ({@code hasKey} false) last, whichever direction is active.
	 */
	private static Comparator<TrackedItem> directed(Comparator<TrackedItem> key,
			Predicate<TrackedItem> hasKey, boolean descending)
	{
		Comparator<TrackedItem> ordered = descending ? key.reversed() : key;
		return Comparator.comparing((TrackedItem item) -> !hasKey.test(item)).thenComparing(ordered);
	}

	/**
	 * @return the same estimated profit the item's rows, totals, and notification metric display
	 *         ({@link TrackedItem#getProfitAt(long)} at the average price): realized profit plus the
	 *         unrealized mark-to-market on held lots. Only meaningful once the cost basis is
	 *         initialized. The old {@code getAvgValue() - getCostBasis()} omitted realized profit and
	 *         mixed container quantity with all-open-lot cost, so the sort disagreed with every
	 *         displayed figure and swung negative for the duration of an in-flight sell (#173).
	 */
	private static long profitKey(TrackedItem item)
	{
		return item.getProfitAt(item.getAvgPrice());
	}

	/** @return whether the item has both a current price and a 24h baseline to compute a change from. */
	private static boolean hasChange(TrackedItem item)
	{
		PriceStats stats = item.getWindowStats().get(TimeWindow.H24);
		long baseline = stats == null ? 0 : stats.getAvg();
		return item.getAvgPrice() > 0 && baseline > 0;
	}

	/** @return the percent change of the current price vs the 24h average (0 when either side is unknown). */
	private static double changeKey(TrackedItem item)
	{
		PriceStats stats = item.getWindowStats().get(TimeWindow.H24);
		long baseline = stats == null ? 0 : stats.getAvg();
		long current = item.getAvgPrice();
		if (current <= 0 || baseline <= 0)
			return 0;

		return (double) (current - baseline) / baseline;
	}

	@Override
	public String toString()
	{
		return label;
	}
}
