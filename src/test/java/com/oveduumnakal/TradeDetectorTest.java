/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import org.junit.Before;
import org.junit.Test;

import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.ItemID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link TradeDetector} (#334, #66): items we offer suspend and withdrawn ones un-suspend,
 * currency and untracked items never queue, and a completed trade books received items as
 * {@link AcquisitionSource#PLAYER_TRADE} buys and given items as sells, each at the gp on the other side
 * apportioned by market value.
 */
public class TradeDetectorTest
{
	private static final int WHIP = 4151;

	private static final int NOTED_WHIP = 4152;

	private static final int RUNE = 560;

	private static final int SHARK = 385;

	private static final int TICK = 100;

	private final DetectorHost host = mock(DetectorHost.class);

	private final CostBasisLedger ledger = mock(CostBasisLedger.class);

	private final TradeDetector detector = new TradeDetector(host, ledger);

	@Before
	public void setUp()
	{
		when(host.canonicalize(anyInt())).thenAnswer(invocation ->
		{
			int id = invocation.getArgument(0);
			return id == NOTED_WHIP ? WHIP : id;
		});
		when(host.isTracked(WHIP)).thenReturn(true);
		when(host.isTracked(RUNE)).thenReturn(true);
		when(host.sourcePricing()).thenReturn(true);
		when(host.currentTick()).thenReturn(TICK);
	}

	/** A trade-window side holding {@code id, quantity} pairs. */
	private static ItemContainer side(int... pairs)
	{
		Item[] items = new Item[pairs.length / 2 + 1];
		for (int i = 0; i < pairs.length; i += 2)
			items[i / 2] = new Item(pairs[i], pairs[i + 1]);

		items[items.length - 1] = new Item(-1, 0);
		ItemContainer container = mock(ItemContainer.class);
		when(container.getItems()).thenReturn(items);
		return container;
	}

	private static TrackedItem priced(int itemId, long avg)
	{
		TrackedItem item = new TrackedItem(itemId, "Item " + itemId);
		item.setAvgPrice(avg);
		return item;
	}

	@Test
	public void offeringSuspendsAndWithdrawingUnsuspendsTrackedItems()
	{
		detector.onOfferChanged(side(WHIP, 1, RUNE, 100, ItemID.COINS, 5_000, SHARK, 3), true);
		verify(ledger).queueTradeSuspend(WHIP, 1);
		verify(ledger).queueTradeSuspend(RUNE, 100);

		detector.onOfferChanged(side(WHIP, 1, RUNE, 40), true);
		verify(ledger).queueTradeUnsuspend(RUNE, 60);
		verify(ledger, never()).queueTradeSuspend(ItemID.COINS, 5_000);
		verify(ledger, never()).queueTradeSuspend(SHARK, 3);
	}

	@Test
	public void notedItemsSuspendUnderTheUnnotedId()
	{
		detector.onOfferChanged(side(NOTED_WHIP, 2), true);

		verify(ledger).queueTradeSuspend(WHIP, 2);
	}

	@Test
	public void thePartnersSideAndPricingOffQueueNothing()
	{
		detector.onOfferChanged(side(WHIP, 1), false);
		when(host.sourcePricing()).thenReturn(false);
		detector.onOfferChanged(side(WHIP, 1), true);

		verify(ledger, never()).queueTradeSuspend(anyInt(), anyInt());
	}

	@Test
	public void receivedItemsBuyInAtTheCoinsAndPlatinumWeGave()
	{
		detector.onOfferChanged(side(ItemID.COINS, 500_000, ItemID.PLATINUM, 1_500), true);
		detector.onOfferChanged(side(WHIP, 2), false);

		detector.onTradeAccepted();

		verify(ledger).claim(AcquisitionSource.PLAYER_TRADE, WHIP, 2, 1_000_000L, TICK);
	}

	@Test
	public void givenItemsCloseAtTheGpWeReceived()
	{
		detector.onOfferChanged(side(WHIP, 1), true);
		detector.onOfferChanged(side(ItemID.COINS, 2_500_000), false);

		detector.onTradeAccepted();

		verify(ledger).realizeTradeSale(WHIP, 1, 2_500_000L);
	}

	@Test
	public void theGpSplitsByMarketValueUsingTrackedThenGuidePrices()
	{
		when(host.trackedItem(WHIP)).thenReturn(priced(WHIP, 900));
		when(host.trackedItem(RUNE)).thenReturn(priced(RUNE, 0));
		when(host.guidePrice(RUNE)).thenReturn(100L);
		detector.onOfferChanged(side(ItemID.COINS, 1_000), true);
		detector.onOfferChanged(side(WHIP, 1, RUNE, 1), false);

		detector.onTradeAccepted();

		verify(ledger).claim(AcquisitionSource.PLAYER_TRADE, WHIP, 1, 900L, TICK);
		verify(ledger).claim(AcquisitionSource.PLAYER_TRADE, RUNE, 1, 100L, TICK);
	}

	@Test
	public void anItemForItemSwapPricesBothSidesAtZero()
	{
		detector.onOfferChanged(side(WHIP, 1), true);
		detector.onOfferChanged(side(RUNE, 500, SHARK, 10), false);

		detector.onTradeAccepted();

		verify(ledger).claim(AcquisitionSource.PLAYER_TRADE, RUNE, 500, 0L, TICK);
		verify(ledger).realizeTradeSale(WHIP, 1, 0L);
		verify(ledger, never()).claim(AcquisitionSource.PLAYER_TRADE, SHARK, 10, 0L, TICK);
	}

	@Test
	public void aTradeIsBookedOnceAndResetClearsTheSides()
	{
		detector.onOfferChanged(side(ItemID.COINS, 100), true);
		detector.onOfferChanged(side(WHIP, 1), false);
		detector.onTradeAccepted();
		detector.onTradeAccepted();

		verify(ledger).claim(AcquisitionSource.PLAYER_TRADE, WHIP, 1, 100L, TICK);

		detector.onOfferChanged(side(WHIP, 1), false);
		detector.reset();
		detector.onTradeAccepted();
		verify(ledger).claim(any(), anyInt(), anyInt(), anyLong(), anyInt());
	}

	@Test
	public void anEmptyWindowClearsItsSide()
	{
		detector.onOfferChanged(side(WHIP, 1), false);
		detector.onOfferChanged(null, false);
		detector.onTradeAccepted();

		verify(ledger, never()).claim(any(), anyInt(), anyInt(), anyLong(), anyInt());
	}

	@Test
	public void coinsAndPlatinumAreCurrencyWorthTheirGp()
	{
		assertTrue(TradeDetector.isTradeCurrency(ItemID.COINS));
		assertTrue(TradeDetector.isTradeCurrency(ItemID.PLATINUM));
		assertFalse(TradeDetector.isTradeCurrency(WHIP));
		assertEquals(1_002_500L, TradeDetector.tradeGp(java.util.Map.of(ItemID.COINS, 2_500, ItemID.PLATINUM, 1_000)));
	}
}
