/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import net.runelite.api.GrandExchangeOffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Unit net for {@link CostBasisLedger} (#255): the FIFO lot engine, GE buy ledger, sell
 * suspend/realize (including the #107 Finding 3 instant-fill race), death suspend/recover, and the
 * source-matched close. Drives the ledger through a hand-rolled {@link LedgerHost} so the attribution
 * behaviour CI cannot smoke-test is exercised in isolation, with a no-op persistence stub.
 */
public class CostBasisLedgerTest
{
	private static final int ITEM = 560;

	/** A controllable {@link LedgerHost}: fixed tick, toggled pricing, an in-memory item table. */
	private static final class FakeHost implements LedgerHost
	{
		private int tick = 100;
		private boolean sourcePricing = true;
		private FallbackPricing fallbackPricing = FallbackPricing.AVG;
		private final Map<Integer, TrackedItem> items = new HashMap<>();
		private final Set<Integer> consumables = new HashSet<>();
		private final Set<Integer> recoverableAmmo = new HashSet<>();
		private GrandExchangeOffer[] offers = null;
		private int persistCalls;
		private int refreshCalls;

		@Override
		public int currentTick()
		{
			return tick;
		}

		@Override
		public boolean sourcePricing()
		{
			return sourcePricing;
		}

		@Override
		public FallbackPricing fallbackPricing()
		{
			return fallbackPricing;
		}

		@Override
		public TrackedItem trackedItem(int itemId)
		{
			return items.get(itemId);
		}

		@Override
		public Collection<TrackedItem> trackedItems()
		{
			return items.values();
		}

		@Override
		public void persistTrackedItems()
		{
			persistCalls++;
		}

		@Override
		public void refreshPanel()
		{
			refreshCalls++;
		}

		@Override
		public boolean isConsumable(int itemId)
		{
			return consumables.contains(itemId);
		}

		@Override
		public boolean isDestroyedAmmo(int itemId)
		{
			return false;
		}

		@Override
		public boolean isRecoverableAmmo(int itemId)
		{
			return recoverableAmmo.contains(itemId);
		}

		@Override
		public boolean isEmptyContainer(int itemId)
		{
			return false;
		}

		@Override
		public GrandExchangeOffer[] openGeOffers()
		{
			return offers;
		}
	}

	/** A persistence stub that neither reads nor writes config, so the ledger is testable client-free. */
	private static final class NoopPersistence extends StockpilePersistence
	{
		NoopPersistence()
		{
			super(null, null);
		}

		@Override
		void saveGeState(Map<Integer, List<long[]>> ledger, Map<Integer, long[]> limits)
		{
		}

		@Override
		Map<Integer, List<long[]>> loadGeLedger()
		{
			return new HashMap<>();
		}

		@Override
		Map<Integer, long[]> loadGeBuyLimits()
		{
			return new HashMap<>();
		}
	}

	private final FakeHost host = new FakeHost();

	private final CostBasisLedger ledger = new CostBasisLedger(host, new NoopPersistence());

	private TrackedItem item(int qty, long avg, AcquisitionRecord... lots)
	{
		TrackedItem t = new TrackedItem(ITEM, "Test item");
		t.setQuantity(qty);
		t.setAvgPrice(avg);
		t.setHighPrice(avg);
		t.setLowPrice(avg);
		t.setCostBasisInitialized(true);
		t.setAcquisitions(new ArrayList<>(Arrays.asList(lots)));
		host.items.put(ITEM, t);
		return t;
	}

	private static int openCount(TrackedItem t)
	{
		int n = 0;
		for (AcquisitionRecord r : t.getAcquisitions())
			if (r.getSoldAt() == null)
				n++;

		return n;
	}

	private static int closedCount(TrackedItem t)
	{
		return t.getAcquisitions().size() - openCount(t);
	}

	private static AcquisitionRecord firstOpen(TrackedItem t)
	{
		for (AcquisitionRecord r : t.getAcquisitions())
			if (r.getSoldAt() == null)
				return r;

		return null;
	}

	private static AcquisitionRecord firstClosed(TrackedItem t)
	{
		for (AcquisitionRecord r : t.getAcquisitions())
			if (r.getSoldAt() != null)
				return r;

		return null;
	}

	@Test
	public void unattributedGainOpensAFallbackPricedLot()
	{
		TrackedItem t = item(0, 100);
		ledger.applyDelta(t, 5);

		AcquisitionRecord lot = firstOpen(t);
		assertEquals("one open lot", 1, openCount(t));
		assertEquals("priced at the avg fallback", 100, lot.getBoughtAt());
		assertEquals(5, lot.getQuantity());
		assertEquals(AcquisitionSource.UNKNOWN, lot.sourceOrUnknown());
	}

	@Test
	public void reAcquireReversesAnEqualOppositeCloseAndReopensThePosition()
	{
		AcquisitionRecord wash = new AcquisitionRecord(3, 100, 100L);
		TrackedItem t = item(0, 100, wash);

		ledger.addOpenAcquisition(t, 3, 100, AcquisitionSource.GE_TRADE);

		AcquisitionRecord lot = firstOpen(t);
		assertEquals("the closed wash-sale is undone", 0, closedCount(t));
		assertEquals("and the 3 units are held open again at their basis", 1, openCount(t));
		assertEquals(3, lot.getQuantity());
		assertEquals(100, lot.getBoughtAt());
	}

	@Test
	public void sellClosesItsOwnSourcesBuyFirst()
	{
		AcquisitionRecord gather = new AcquisitionRecord(5, 100, null, AcquisitionSource.GATHER);
		AcquisitionRecord bought = new AcquisitionRecord(5, 100, null, AcquisitionSource.GE_TRADE);
		TrackedItem t = item(10, 100, gather, bought);

		ledger.closeFifo(t, 5, 150, AcquisitionSource.GE_TRADE);

		AcquisitionRecord stillOpen = firstOpen(t);
		AcquisitionRecord sold = firstClosed(t);
		assertEquals("the matched GE lot closes before the older GATHER lot (#137)", 1, openCount(t));
		assertEquals(AcquisitionSource.GATHER, stillOpen.sourceOrUnknown());
		assertEquals(1, closedCount(t));
		assertEquals(150, (long) sold.getSoldAt());
	}

	@Test
	public void geBuyFillPricesTheCollectedGainAndCountsTheLimit()
	{
		TrackedItem t = item(0, 999);

		ledger.onGeOffer(0, ITEM, true, false, false, 5, 0, 0);
		ledger.onGeOffer(0, ITEM, true, false, false, 5, 5, 500);

		ledger.applyDelta(t, 5);

		AcquisitionRecord lot = firstOpen(t);
		assertEquals(1, openCount(t));
		assertEquals("collected units price at the true buy price, not the fallback", 100, lot.getBoughtAt());
		assertEquals(AcquisitionSource.GE_TRADE, lot.sourceOrUnknown());

		ledger.applyBuyLimitFields(t);
		assertEquals("the fill counts toward the 4h buy limit", 5, t.getLimitBought());
	}

	@Test
	public void placedSellSuspendsThenFillRealizesAtTheTruePrice()
	{
		TrackedItem t = item(10, 100, new AcquisitionRecord(10, 100, null, AcquisitionSource.GATHER));

		ledger.onGeOffer(1, ITEM, false, false, false, 10, 0, 0);
		ledger.applyDelta(t, -10);

		assertEquals("units suspend, not closed", 10, t.getSuspended(SuspensionSource.SELL));
		assertEquals(1, openCount(t));
		assertEquals(0, closedCount(t));

		ledger.onGeOffer(1, ITEM, false, false, false, 10, 10, 1500);

		AcquisitionRecord sold = firstClosed(t);
		assertEquals(0, t.getSuspended(SuspensionSource.SELL));
		assertEquals("the sold lot closes at the realized price", 1, closedCount(t));
		assertEquals(150, (long) sold.getSoldAt());
		assertEquals("basis is preserved", 100, sold.getBoughtAt());
	}

	@Test
	public void instantFillParksTheShortfallAndFlushClosesItAfterTheSuspend()
	{
		TrackedItem t = item(10, 100, new AcquisitionRecord(10, 100, null, AcquisitionSource.GATHER));

		ledger.onGeOffer(1, ITEM, false, false, false, 10, 0, 0);
		ledger.onGeOffer(1, ITEM, false, false, false, 10, 10, 1500);

		assertEquals("nothing closed yet — the fill outran its suspension (#107 Finding 3)", 0, closedCount(t));

		ledger.applyDelta(t, -10);
		ledger.flushPendingSellRealize();

		AcquisitionRecord sold = firstClosed(t);
		assertEquals(0, t.getSuspended(SuspensionSource.SELL));
		assertEquals("the parked fill closes once the units suspend", 1, closedCount(t));
		assertEquals(150, (long) sold.getSoldAt());
	}

	@Test
	public void cancelledSellUnsuspendsCleanly()
	{
		TrackedItem t = item(10, 100, new AcquisitionRecord(10, 100, null, AcquisitionSource.GATHER));

		ledger.onGeOffer(1, ITEM, false, false, false, 10, 0, 0);
		ledger.applyDelta(t, -10);
		assertEquals(10, t.getSuspended(SuspensionSource.SELL));

		ledger.onGeOffer(1, ITEM, false, true, false, 10, 0, 0);
		ledger.applyDelta(t, 10);

		assertEquals("units un-suspend with no phantom acquisition", 0, t.getSuspended(SuspensionSource.SELL));
		assertEquals(1, openCount(t));
		assertEquals(0, closedCount(t));
	}

	@Test
	public void deathSuspendsLossesThenRecoveryRestoresBasis()
	{
		TrackedItem t = item(4, 100, new AcquisitionRecord(4, 100, null, AcquisitionSource.GE_TRADE));

		ledger.signalDeath();
		ledger.applyDelta(t, -4);

		assertEquals("death losses suspend rather than close at 0", 4, t.getSuspended(SuspensionSource.DEATH));
		assertEquals(1, openCount(t));
		assertEquals(0, closedCount(t));

		ledger.applyDelta(t, 4);

		AcquisitionRecord lot = firstOpen(t);
		assertEquals(0, t.getSuspended(SuspensionSource.DEATH));
		assertEquals("basis intact, no fresh lot", 1, openCount(t));
		assertEquals(100, lot.getBoughtAt());
		assertEquals(AcquisitionSource.GE_TRADE, lot.sourceOrUnknown());
	}

	@Test
	public void sourcePricingOffZeroesSuspensionsOnReconcile()
	{
		TrackedItem t = item(5, 100);
		t.setSuspended(SuspensionSource.SELL, 5);
		host.sourcePricing = false;

		ledger.reconcileSuspendedFromOffers();

		assertEquals("the classic path holds no suspensions", 0, t.getSuspended(SuspensionSource.SELL));
	}

	@Test
	public void gainWithPricingOffStaysUnknownAtFallback()
	{
		host.sourcePricing = false;
		TrackedItem t = item(0, 250);

		ledger.applyDelta(t, 2);

		AcquisitionRecord lot = firstOpen(t);
		assertEquals(1, openCount(t));
		assertEquals(250, lot.getBoughtAt());
		assertEquals(AcquisitionSource.UNKNOWN, lot.sourceOrUnknown());
		assertNull(lot.getSoldAt());
		assertFalse(t.getAcquisitions().isEmpty());
	}
}
