/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Verifies the price/quantity decomposition and baseline lifecycle of {@link SessionStats}. */
public class SessionStatsTest
{
	private Map<Integer, long[]> snap(long... idQtyPrice)
	{
		Map<Integer, long[]> m = new HashMap<>();
		for (int i = 0; i < idQtyPrice.length; i += 3)
			m.put((int) idQtyPrice[i], new long[]{idQtyPrice[i + 1], idQtyPrice[i + 2]});

		return m;
	}

	@Test
	public void noBaselineYieldsZero()
	{
		SessionStats stats = new SessionStats();
		assertFalse(stats.hasBaseline());

		SessionStats.Delta d = stats.delta(snap(560, 100, 95));
		assertEquals(0, d.getTotal());
	}

	@Test
	public void purePriceMovement()
	{
		SessionStats stats = new SessionStats();
		stats.reset(snap(560, 100, 95));
		assertTrue(stats.hasBaseline());

		SessionStats.Delta d = stats.delta(snap(560, 100, 100));
		assertEquals(500, d.getTotal());
		assertEquals(500, d.getPrice());
		assertEquals(0, d.getQuantity());
	}

	@Test
	public void pureQuantityMovement()
	{
		SessionStats stats = new SessionStats();
		stats.reset(snap(560, 100, 95));

		SessionStats.Delta d = stats.delta(snap(560, 150, 95));
		assertEquals(50 * 95, d.getTotal());
		assertEquals(0, d.getPrice());
		assertEquals(50 * 95, d.getQuantity());
	}

	@Test
	public void mixedComponentsSumToTotal()
	{
		SessionStats stats = new SessionStats();
		stats.reset(snap(560, 100, 95, 4151, 1, 2_000_000));

		SessionStats.Delta d = stats.delta(snap(560, 120, 100, 4151, 2, 2_100_000));
		assertEquals(d.getTotal(), d.getPrice() + d.getQuantity());
	}

	@Test
	public void droppedItemCountsAsQuantityLoss()
	{
		SessionStats stats = new SessionStats();
		stats.reset(snap(560, 100, 95));

		SessionStats.Delta d = stats.delta(new HashMap<>());
		assertEquals(-100 * 95, d.getTotal());
		assertEquals(0, d.getPrice());
		assertEquals(-100 * 95, d.getQuantity());
	}

	@Test
	public void clearDropsBaseline()
	{
		SessionStats stats = new SessionStats();
		stats.reset(snap(560, 100, 95));
		stats.clear();
		assertFalse(stats.hasBaseline());
	}
}
