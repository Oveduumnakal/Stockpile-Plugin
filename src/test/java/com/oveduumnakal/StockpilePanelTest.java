/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Verifies the session baseline snapshot only captures live, not cache-hydrated, prices, and that
 * the coin icon cache is keyed by sprite rather than by the raw total (#323).
 */
public class StockpilePanelTest
{
	private TrackedItem item(int id, int quantity, long avgPrice, boolean live)
	{
		TrackedItem item = new TrackedItem(id, "Item " + id);
		item.setQuantity(quantity);
		item.setHighPrice(avgPrice);
		item.setLowPrice(avgPrice);
		item.setAvgPrice(avgPrice);
		item.setPriceCacheHydrated(!live);
		return item;
	}

	@Test
	public void cacheHydratedItemsAreExcludedFromTheBaseline()
	{
		Map<Integer, long[]> snapshot = StockpilePanel.liveSessionSnapshot(
				Collections.singletonList(item(560, 100, 125, false)));

		assertTrue("a cache-hydrated item must not seed the session baseline", snapshot.isEmpty());
	}

	@Test
	public void livePricedItemsSeedTheBaseline()
	{
		Map<Integer, long[]> snapshot = StockpilePanel.liveSessionSnapshot(
				Collections.singletonList(item(560, 100, 130, true)));

		assertEquals(1, snapshot.size());
		assertEquals(100, snapshot.get(560)[0]);
		assertEquals(130, snapshot.get(560)[1]);
	}

	@Test
	public void onlyLiveItemsSurviveInAMixedHolding()
	{
		Map<Integer, long[]> snapshot = StockpilePanel.liveSessionSnapshot(Arrays.asList(
				item(560, 10, 100, true),
				item(4151, 1, 1_000, false)));

		assertEquals(1, snapshot.size());
		assertTrue("the live item is captured", snapshot.containsKey(560));
		assertFalse("the cache-hydrated item is skipped", snapshot.containsKey(4151));
	}

	/**
	 * The coin icon cache used to be keyed by the portfolio total, which moves on almost every price
	 * refresh - a new ImageIcon roughly once a minute, forever. Bucketing to the sprite thresholds
	 * caps it at ten entries, since that is all ItemManager distinguishes here.
	 */
	@Test
	public void coinSpriteQuantityBucketsToTheSpriteThresholds()
	{
		assertEquals(1, StockpilePanel.coinSpriteQuantity(0));
		assertEquals(1, StockpilePanel.coinSpriteQuantity(1));
		assertEquals(4, StockpilePanel.coinSpriteQuantity(4));
		assertEquals(5, StockpilePanel.coinSpriteQuantity(24));
		assertEquals(25, StockpilePanel.coinSpriteQuantity(25));
		assertEquals(250, StockpilePanel.coinSpriteQuantity(999));
		assertEquals(1000, StockpilePanel.coinSpriteQuantity(9_999));
		assertEquals(10_000, StockpilePanel.coinSpriteQuantity(10_000));
	}

	/** Every value above the top threshold shares one sprite, so a growing portfolio adds no entries. */
	@Test
	public void largeTotalsAllShareTheTopSprite()
	{
		assertEquals(10_000, StockpilePanel.coinSpriteQuantity(1_000_000L));
		assertEquals(10_000, StockpilePanel.coinSpriteQuantity(2_147_483_647L));
		assertEquals(10_000, StockpilePanel.coinSpriteQuantity(9_000_000_000L));
	}

	/** A negative or zero total still resolves to the smallest sprite rather than falling through. */
	@Test
	public void nonPositiveTotalsUseTheSmallestSprite()
	{
		assertEquals(1, StockpilePanel.coinSpriteQuantity(-5));
	}
}
