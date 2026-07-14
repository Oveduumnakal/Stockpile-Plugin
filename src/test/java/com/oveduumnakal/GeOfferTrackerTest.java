/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for {@link GeOfferTracker}: placement vs. incremental fills vs. cancellation,
 * under-offer buy pricing, the login-replay baseline guard, and slot reset on empty.
 */
public class GeOfferTrackerTest
{
	private final GeOfferTracker tracker = new GeOfferTracker();

	/** Convenience: a buy-side offer event. */
	private GeOfferTracker.Event buy(int slot, int total, int sold, long spent)
	{
		return tracker.onOffer(slot, 560, true, false, false, total, sold, spent);
	}

	/** Convenience: a sell-side offer event. */
	private GeOfferTracker.Event sell(int slot, int total, int sold, long spent)
	{
		return tracker.onOffer(slot, 560, false, false, false, total, sold, spent);
	}

	@Test
	public void freshSellPlacementEmitsPlaced()
	{
		GeOfferTracker.Event e = sell(0, 100, 0, 0);
		assertEquals(GeOfferTracker.Type.PLACED, e.type);
		assertEquals(GeOfferTracker.Kind.SELL, e.kind);
		assertEquals(100, e.quantity);
	}

	@Test
	public void incrementalBuyFillsCarryPerFillPrice()
	{
		buy(0, 100, 0, 0);

		GeOfferTracker.Event first = buy(0, 100, 40, 40 * 90L);
		assertEquals(GeOfferTracker.Type.FILL, first.type);
		assertEquals(GeOfferTracker.Kind.BUY, first.kind);
		assertEquals(40, first.quantity);
		assertEquals(90, first.unitPrice);

		GeOfferTracker.Event second = buy(0, 100, 100, 40 * 90L + 60 * 80L);
		assertEquals(60, second.quantity);
		assertEquals(80, second.unitPrice);
	}

	@Test
	public void underOfferBuyPriceReflectsActualSpend()
	{
		buy(0, 10, 0, 0);
		GeOfferTracker.Event e = buy(0, 10, 10, 10 * 45L);
		assertEquals(45, e.unitPrice);
	}

	@Test
	public void noProgressUpdateEmitsNothing()
	{
		buy(0, 100, 40, 40 * 90L);
		assertNull(buy(0, 100, 40, 40 * 90L));
	}

	@Test
	public void cancelReportsReturnedQuantity()
	{
		sell(0, 100, 0, 0);
		sell(0, 100, 30, 30 * 120L);

		GeOfferTracker.Event c = tracker.onOffer(0, 560, false, true, false, 100, 30, 30 * 120L);
		assertEquals(GeOfferTracker.Type.CANCELLED, c.type);
		assertEquals(70, c.quantity);
	}

	@Test
	public void loginReplayOfPartialOfferSeedsBaselineWithoutFill()
	{
		GeOfferTracker.Event replay = buy(0, 100, 40, 40 * 90L);
		assertNull(replay);

		GeOfferTracker.Event next = buy(0, 100, 70, 40 * 90L + 30 * 90L);
		assertEquals(GeOfferTracker.Type.FILL, next.type);
		assertEquals(30, next.quantity);
	}

	@Test
	public void emptyResetsSlotSoReuseStartsFresh()
	{
		buy(0, 100, 100, 100 * 90L);
		assertNull(tracker.onOffer(0, 560, true, false, true, 0, 0, 0));

		GeOfferTracker.Event placed = sell(0, 50, 0, 0);
		assertEquals(GeOfferTracker.Type.PLACED, placed.type);
		assertEquals(50, placed.quantity);
	}

	@Test
	public void seededSellOfferDoesNotReplayAsPlacementAndStillCancels()
	{
		tracker.seed(0, 560, 0, 0);

		assertNull(sell(0, 10, 0, 0));

		GeOfferTracker.Event cancel = tracker.onOffer(0, 560, false, true, false, 10, 0, 0);
		assertEquals(GeOfferTracker.Type.CANCELLED, cancel.type);
		assertEquals(GeOfferTracker.Kind.SELL, cancel.kind);
		assertEquals(10, cancel.quantity);
	}
}
