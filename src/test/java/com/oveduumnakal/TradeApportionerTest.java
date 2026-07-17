/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Verifies the gp-splitting rules of {@link TradeApportioner}. */
public class TradeApportionerTest
{
	@Test
	public void singleItemTakesAllTheGp()
	{
		Map<Integer, Long> prices = TradeApportioner.apportion(
				List.of(new TradeApportioner.Leg(4151, 1, 1_500_000)), 10_000_000);
		assertEquals(Long.valueOf(10_000_000), prices.get(4151));
	}

	@Test
	public void gpSplitsProportionallyToMarketValue()
	{
		Map<Integer, Long> prices = TradeApportioner.apportion(
				List.of(
						new TradeApportioner.Leg(1, 1, 3_000_000),
						new TradeApportioner.Leg(2, 1, 1_000_000)),
				8_000_000);
		assertEquals(Long.valueOf(6_000_000), prices.get(1));
		assertEquals(Long.valueOf(2_000_000), prices.get(2));
	}

	@Test
	public void quantityWeighsIntoTheSplit()
	{
		Map<Integer, Long> prices = TradeApportioner.apportion(
				List.of(
						new TradeApportioner.Leg(1, 3, 1_000_000),
						new TradeApportioner.Leg(2, 1, 1_000_000)),
				4_000_000);
		assertEquals(Long.valueOf(1_000_000), prices.get(1));
		assertEquals(Long.valueOf(1_000_000), prices.get(2));
	}

	@Test
	public void itemForItemPricesAtZero()
	{
		Map<Integer, Long> prices = TradeApportioner.apportion(
				List.of(new TradeApportioner.Leg(1, 1, 1_500_000)), 0);
		assertEquals(Long.valueOf(0), prices.get(1));
	}

	@Test
	public void unvaluedLegsSplitEvenlyPerUnit()
	{
		Map<Integer, Long> prices = TradeApportioner.apportion(
				List.of(
						new TradeApportioner.Leg(1, 3, 0),
						new TradeApportioner.Leg(2, 1, 0)),
				4_000);
		assertEquals(Long.valueOf(1_000), prices.get(1));
		assertEquals(Long.valueOf(1_000), prices.get(2));
	}

	@Test
	public void emptyLegsYieldNothing()
	{
		assertTrue(TradeApportioner.apportion(List.of(), 5_000).isEmpty());
	}
}
