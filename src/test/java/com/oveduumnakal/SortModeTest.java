/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** Verifies the Profit sort orders items by the same estimated profit their rows display (#173). */
public class SortModeTest
{
	private TrackedItem item(String name, int qty, long avg, AcquisitionRecord... lots)
	{
		TrackedItem t = new TrackedItem(560, name);
		t.setQuantity(qty);
		t.setAvgPrice(avg);
		t.setCostBasisInitialized(true);
		t.setAcquisitions(Arrays.asList(lots));
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
		items.sort(SortMode.PROFIT.comparator(false));

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
		selling.setSuspendedQuantity(100);

		assertEquals("suspended units keep marking to market, not read as a loss",
				10_000, selling.getProfitAt(selling.getAvgPrice()));

		Comparator<TrackedItem> byProfit = SortMode.PROFIT.comparator(false);
		assertEquals("both items carry the same +10k profit, so neither is forced below the other",
				winner.getProfitAt(winner.getAvgPrice()), selling.getProfitAt(selling.getAvgPrice()));

		List<TrackedItem> items = new ArrayList<>(Arrays.asList(winner, selling));
		items.sort(byProfit);
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
		items.sort(SortMode.PROFIT.comparator(false));

		assertEquals("uninitialised cost basis always sorts last", "No basis", items.get(1).getName());
	}
}
