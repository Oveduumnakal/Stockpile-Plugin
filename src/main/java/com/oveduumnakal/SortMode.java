/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
	/** The {@code "Manual"} option. */
	MANUAL("Manual"),
	/** The {@code "Name"} option. */
	NAME("Name"),
	/** The {@code "Value"} option. */
	VALUE("Value"),
	/** The {@code "Profit"} option. */
	PROFIT("Profit"),
	/** The {@code "24h Change"} option. */
	CHANGE_24H("24h Change");

	private final String label;

	SortMode(String label)
	{
		this.label = label;
	}

	/**
	 * Sorts {@code items} in place for display, or leaves the list untouched for {@link #MANUAL}.
	 *
	 * <p>The expensive keys are materialized once per item before the sort rather than recomputed
	 * inside the comparator. {@code Comparator.comparingLong} re-invokes its extractor for
	 * <em>both operands of every comparison</em> and does not memoize, so {@link #PROFIT} — whose
	 * key streams the item's whole acquisitions list twice — cost roughly {@code 2n log n} full
	 * passes over every lot, on the EDT, on every panel rebuild (#322). Materializing first makes
	 * that {@code n}.
	 *
	 * @param reversed whether to flip this mode's natural direction
	 */
	void sort(List<TrackedItem> items, boolean reversed)
	{
		if (this == MANUAL)
			return;

		items.sort(comparator(items, reversed));
	}

	/**
	 * @param items the exact list about to be sorted, used to materialize the per-item keys
	 * @param reversed whether to flip this mode's natural direction
	 * @return the display comparator, or {@code null} for {@link #MANUAL}
	 */
	Comparator<TrackedItem> comparator(List<TrackedItem> items, boolean reversed)
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
			{
				Map<TrackedItem, Long> keys = materialize(items, SortMode::profitKey);
				return directed(Comparator.comparingLong(item -> keys.get(item)),
						TrackedItem::isCostBasisInitialized, descending);
			}
			case CHANGE_24H:
			{
				Map<TrackedItem, Double> keys = materialize(items, SortMode::changeKey);
				return directed(Comparator.comparingDouble(item -> keys.get(item)),
						SortMode::hasChange, descending);
			}
			default:
				return null;
		}
	}

	/**
	 * @return each item's sort key, computed exactly once. Keyed by identity rather than item id so
	 *         two instances of the same id (preview, pop-out, Compare) cannot collide.
	 */
	private static <T> Map<TrackedItem, T> materialize(List<TrackedItem> items,
			Function<TrackedItem, T> key)
	{
		Map<TrackedItem, T> keys = new IdentityHashMap<>(items.size());
		for (TrackedItem item : items)
			keys.put(item, key.apply(item));

		return keys;
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
		return Comparator.comparingInt((TrackedItem item) -> hasKey.test(item) ? 0 : 1).thenComparing(ordered);
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
		return item.getCosts().getProfitAtAvg();
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

	/**
	 * Returns the display label shown in the UI.
	 *
	 * @return the display label
	 */
	@Override
	public String toString()
	{
		return label;
	}
}
