/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Verifies the Profit sort orders items by the same estimated profit their rows display (#173),
 * and that each item's key is computed exactly once per sort rather than per comparison (#322).
 */
public class SortModeTest
{
	private TrackedItem item(String name, int qty, long avg, AcquisitionRecord... lots)
	{
		TrackedItem t = new TrackedItem(560, name);
		t.setQuantity(qty);
		t.setAvgPrice(avg);
		t.setCostBasisInitialized(true);
		t.setAcquisitions(Arrays.asList(lots));
		t.refreshCosts();
		return t;
	}

	@Test
	public void profitSortMatchesDisplayedProfit()
	{
		TrackedItem realized = item("All sold at +5M", 0, 10_000,
				new AcquisitionRecord(1_000, 5_000, 10_000L));
		TrackedItem unrealized = item("Held at +10k", 100, 200,
				new AcquisitionRecord(100, 100, null));

		assertEquals("realized profit is what its row shows",
				5_000_000, realized.getProfitAt(realized.getAvgPrice()));
		assertEquals("unrealized profit is what its row shows",
				10_000, unrealized.getProfitAt(unrealized.getAvgPrice()));

		List<TrackedItem> items = new ArrayList<>(Arrays.asList(unrealized, realized));
		SortMode.PROFIT.sort(items, false);

		assertEquals("the +5M item sorts above the +10k item, matching the figures",
				"All sold at +5M", items.get(0).getName());
	}

	@Test
	public void inFlightSellDoesNotSwingTheKeyNegative()
	{
		TrackedItem winner = item("Held at +10k", 100, 200,
				new AcquisitionRecord(100, 100, null));

		TrackedItem selling = item("Sell in flight", 0, 200,
				new AcquisitionRecord(100, 100, null));
		selling.setSuspended(SuspensionSource.SELL, 100);

		assertEquals("suspended units keep marking to market, not read as a loss",
				10_000, selling.getProfitAt(selling.getAvgPrice()));

		assertEquals("both items carry the same +10k profit, so neither is forced below the other",
				winner.getProfitAt(winner.getAvgPrice()), selling.getProfitAt(selling.getAvgPrice()));

		List<TrackedItem> items = new ArrayList<>(Arrays.asList(winner, selling));
		SortMode.PROFIT.sort(items, false);
		assertEquals(2, items.size());
	}

	@Test
	public void itemsWithoutCostBasisSortLast()
	{
		TrackedItem known = item("Has basis", 100, 200,
				new AcquisitionRecord(100, 100, null));
		TrackedItem unknown = item("No basis", 100, 200,
				new AcquisitionRecord(100, 100, null));
		unknown.setCostBasisInitialized(false);

		List<TrackedItem> items = new ArrayList<>(Arrays.asList(unknown, known));
		SortMode.PROFIT.sort(items, false);

		assertEquals("uninitialised cost basis always sorts last", "No basis", items.get(1).getName());
	}

	/** A tracked item that records how many times its profit key was asked for. */
	private static final class CountingItem extends TrackedItem
	{
		private int profitCalls;

		CountingItem(int itemId, String name)
		{
			super(itemId, name);
		}

		@Override
		public long getProfitAt(long markPrice)
		{
			profitCalls++;
			return super.getProfitAt(markPrice);
		}
	}

	/**
	 * {@code Comparator.comparingLong} re-invokes its extractor for both operands of every comparison
	 * and does not memoize, so the key - which streams the whole acquisitions list twice - used to run
	 * about {@code 2n log n} times per sort, on the EDT, on every panel rebuild (#322). It now comes
	 * from the client-thread cost snapshot, so a sort streams the list zero times (#315).
	 */
	@Test
	public void sortingNeverStreamsTheAcquisitionsList()
	{
		List<TrackedItem> items = new ArrayList<>();
		for (int i = 0; i < 32; i++)
		{
			CountingItem item = new CountingItem(560 + i, "Item " + i);
			item.setQuantity(100);
			item.setAvgPrice(200 + i);
			item.setCostBasisInitialized(true);
			item.setAcquisitions(Arrays.asList(new AcquisitionRecord(100, 100, null)));
			item.refreshCosts();
			item.profitCalls = 0;
			items.add(item);
		}

		SortMode.PROFIT.sort(items, false);

		for (TrackedItem item : items)
			assertEquals(item.getName(), 0, ((CountingItem) item).profitCalls);
	}

	/** The materialized keys must not change the order the comparator produces. */
	@Test
	public void reversingFlipsTheOrder()
	{
		TrackedItem low = item("Low", 100, 200, new AcquisitionRecord(100, 100, null));
		TrackedItem high = item("High", 100, 1000, new AcquisitionRecord(100, 100, null));

		List<TrackedItem> items = new ArrayList<>(Arrays.asList(low, high));
		SortMode.PROFIT.sort(items, false);
		assertEquals("High", items.get(0).getName());

		SortMode.PROFIT.sort(items, true);
		assertEquals("Low", items.get(0).getName());
	}

	/** MANUAL keeps the user's drag order, so sorting is a no-op. */
	@Test
	public void manualLeavesTheListAlone()
	{
		TrackedItem first = item("B", 100, 1000, new AcquisitionRecord(100, 100, null));
		TrackedItem second = item("A", 100, 200, new AcquisitionRecord(100, 100, null));

		List<TrackedItem> items = new ArrayList<>(Arrays.asList(first, second));
		SortMode.MANUAL.sort(items, false);
		assertEquals("B", items.get(0).getName());
	}

	private static List<String> names(List<TrackedItem> items)
	{
		List<String> names = new ArrayList<>();
		for (TrackedItem t : items)
			names.add(t.getName());

		return names;
	}

	/** An item whose live average is {@code avg} against a 24h average of {@code dayAvg} (0 = no stats). */
	private TrackedItem changing(String name, long avg, long dayAvg)
	{
		TrackedItem t = item(name, 1, avg);
		if (dayAvg > 0)
			t.getWindowStats().put(TimeWindow.H24, new PriceStats(dayAvg, dayAvg, dayAvg, 1));

		return t;
	}

	@Test
	public void nameSortsCaseInsensitivelyAndAscendingByDefault()
	{
		List<TrackedItem> items = new ArrayList<>(Arrays.asList(item("banana", 1, 1), item("Apple", 1, 1),
				item("cherry", 1, 1)));

		SortMode.NAME.sort(items, false);
		assertEquals(Arrays.asList("Apple", "banana", "cherry"), names(items));

		SortMode.NAME.sort(items, true);
		assertEquals(Arrays.asList("cherry", "banana", "Apple"), names(items));
	}

	@Test
	public void valueSortsDescendingAndKeepsUnpricedItemsLast()
	{
		List<TrackedItem> items = new ArrayList<>(Arrays.asList(item("Unpriced", 5, 0), item("Small", 1, 500),
				item("Big", 2, 1_000)));

		SortMode.VALUE.sort(items, false);
		assertEquals(Arrays.asList("Big", "Small", "Unpriced"), names(items));

		SortMode.VALUE.sort(items, true);
		assertEquals("unpriced stays last in either direction", Arrays.asList("Small", "Big", "Unpriced"),
				names(items));
	}

	@Test
	public void dayChangeSortsByPercentAndKeepsItemsWithoutABaselineLast()
	{
		List<TrackedItem> items = new ArrayList<>(Arrays.asList(changing("No baseline", 500, 0),
				changing("Down", 90, 100), changing("Up big", 150, 100), changing("Up", 105, 100),
				changing("No price", 0, 100)));

		SortMode.CHANGE_24H.sort(items, false);
		assertEquals(Arrays.asList("Up big", "Up", "Down", "No baseline", "No price"), names(items));

		SortMode.CHANGE_24H.sort(items, true);
		assertEquals(Arrays.asList("Down", "Up", "Up big", "No baseline", "No price"), names(items));
	}

	@Test
	public void onlyNameDefaultsToAscending()
	{
		assertFalse(SortMode.NAME.descending(false));
		assertTrue(SortMode.NAME.descending(true));
		assertTrue(SortMode.VALUE.descending(false));
		assertTrue(SortMode.PROFIT.descending(false));
		assertFalse(SortMode.CHANGE_24H.descending(true));
		assertNull(SortMode.MANUAL.comparator(new ArrayList<>(), false));
		assertEquals("24h Change", SortMode.CHANGE_24H.toString());
	}
}
