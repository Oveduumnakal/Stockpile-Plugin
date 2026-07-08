/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.Comparator;

/**
 * How the tracked items list is ordered. {@link #MANUAL} keeps the user's drag
 * order; every other mode sorts for display only (within each group when
 * grouping is active) and disables drag reordering. Value-like modes sort
 * descending; items missing the sort key sort last.
 */
enum SortMode
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

	/** @return the display comparator for this mode, or {@code null} for {@link #MANUAL}. */
	Comparator<TrackedItem> comparator()
	{
		switch (this)
		{
			case NAME:
				return Comparator.comparing(TrackedItem::getName, String.CASE_INSENSITIVE_ORDER);
			case VALUE:
				return Comparator.comparingLong(TrackedItem::getAvgValue).reversed();
			case PROFIT:
				return Comparator.comparingLong(SortMode::profitKey).reversed();
			case CHANGE_24H:
				return Comparator.comparingDouble(SortMode::changeKey).reversed();
			default:
				return null;
		}
	}

	/** @return an item's estimated profit, or {@code Long.MIN_VALUE} when it has no cost basis to sort by. */
	private static long profitKey(TrackedItem item)
	{
		if (!item.isCostBasisInitialized())
			return Long.MIN_VALUE;

		return item.getAvgValue() - item.getCostBasis();
	}

	/**
	 * @return the percent change of the current price vs the 24h average, or negative
	 *         infinity when either side is unknown so those items sort last
	 */
	private static double changeKey(TrackedItem item)
	{
		PriceStats stats = item.getWindowStats().get(TimeWindow.H24);
		long baseline = stats == null ? 0 : stats.getAvg();
		long current = item.getAvgPrice();
		if (current <= 0 || baseline <= 0)
			return Double.NEGATIVE_INFINITY;

		return (double) (current - baseline) / baseline;
	}

	@Override
	public String toString()
	{
		return label;
	}
}
