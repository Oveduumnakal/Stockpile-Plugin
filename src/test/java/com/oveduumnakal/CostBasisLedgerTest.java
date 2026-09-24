/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.time.Duration;
import java.time.Instant;
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
import net.runelite.api.GrandExchangeOfferState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
		}

		@Override
		public void refreshPanel()
		{
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
	private static class NoopPersistence extends StockpilePersistence
	{
		NoopPersistence()
		{
			super((ProfileConfigStore) null, null);
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

	/** A persistence stub that records how many GE-state writes it was asked for (#328). */
	private static final class CountingPersistence extends NoopPersistence
	{
		private int saves;
		private Map<Integer, List<long[]>> lastLedger = new HashMap<>();
		private Map<Integer, long[]> lastLimits = new HashMap<>();

		@Override
		void saveGeState(Map<Integer, List<long[]>> ledger, Map<Integer, long[]> limits)
		{
			saves++;
			lastLedger = ledger;
			lastLimits = limits;
		}
	}

	/** A persistence stub that hands back deliberately malformed buy-limit windows (#329). */
	private static final class CorruptLimitsPersistence extends NoopPersistence
	{
		@Override
		Map<Integer, long[]> loadGeBuyLimits()
		{
			Map<Integer, long[]> limits = new HashMap<>();
			limits.put(1381, new long[]{});
			limits.put(1383, new long[]{7});
			limits.put(1385, null);
			limits.put(ITEM, new long[]{Instant.now().getEpochSecond(), 300});
			return limits;
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

	/** A mocked live GE offer slot as the client reports it at login (#385). */
	private static GrandExchangeOffer offer(GrandExchangeOfferState state, int total, int sold, int spent)
	{
		GrandExchangeOffer offer = mock(GrandExchangeOffer.class);
		when(offer.getItemId()).thenReturn(ITEM);
		when(offer.getState()).thenReturn(state);
		when(offer.getTotalQuantity()).thenReturn(total);
		when(offer.getQuantitySold()).thenReturn(sold);
		when(offer.getSpent()).thenReturn(spent);
		return offer;
	}

	/** The instant {@code minutes} ago, for seeding a suspension's age. */
	private static Instant minutesAgo(long minutes)
	{
		return Instant.now().minus(Duration.ofMinutes(minutes));
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
	public void aPartialClaimPricesOnlyItsUnitsAndTheRestFallsBack()
	{
		TrackedItem t = item(0, 100);
		ledger.claim(AcquisitionSource.SHOP, ITEM, 5, 200, host.tick);

		ledger.applyDelta(t, 10);

		assertEquals("the gain splits into two lots (#372)", 2, openCount(t));
		for (AcquisitionRecord r : t.getAcquisitions())
		{
			assertEquals(5, r.getQuantity());
			if (r.sourceOrUnknown() == AcquisitionSource.SHOP)
				assertEquals("the claimed half at the shop price", 200, r.getBoughtAt());
			else
				assertEquals("the rest at the avg fallback", 100, r.getBoughtAt());
		}
	}

	@Test
	public void aPartialClaimOnARemovalClosesOnlyItsUnitsAtTheClaimPrice()
	{
		TrackedItem t = item(3, 100, new AcquisitionRecord(3, 50, null, AcquisitionSource.GE_TRADE));
		ledger.claim(AcquisitionSource.ALCHEMY, ITEM, 1, 300, host.tick);

		ledger.applyDelta(t, -3);

		assertEquals(2, closedCount(t));
		assertEquals("1 unit at the alch value plus 2 at the avg estimate", 250 + 2 * 50, t.getRealizedProfit());
	}

	@Test
	public void reAcquireAtAPastBreakEvenPriceKeepsThatSaleInTheLog()
	{
		AcquisitionRecord pastSale = new AcquisitionRecord(3, 100, 100L);
		TrackedItem t = item(0, 100, pastSale);

		ledger.addOpenAcquisition(t, 3, 100, AcquisitionSource.GE_TRADE);

		assertEquals("the historical break-even sale is not erased (#371)", 1, closedCount(t));
		assertEquals(3, firstClosed(t).getQuantity());
		assertEquals("the re-buy opens its own lot", 1, openCount(t));
		assertEquals(3, firstOpen(t).getQuantity());
	}

	@Test
	public void sellingAtANewerLotsPriceClosesTheOldestLotFirst()
	{
		AcquisitionRecord older = new AcquisitionRecord(5, 150, null, AcquisitionSource.GE_TRADE);
		AcquisitionRecord newer = new AcquisitionRecord(5, 100, null, AcquisitionSource.GE_TRADE);
		TrackedItem t = item(10, 100, older, newer);

		ledger.closeFifo(t, 5, 100, AcquisitionSource.GE_TRADE);

		AcquisitionRecord closed = firstClosed(t);
		assertEquals("the sale is recorded, not deleted (#371)", 1, closedCount(t));
		assertEquals("FIFO closes the older lot", 150, closed.getBoughtAt());
		assertEquals(Long.valueOf(100), closed.getSoldAt());
		assertEquals(-250, t.getRealizedProfit());
		assertEquals("the newer lot stays held at its basis", 500, t.getCostBasis());
	}

	@Test
	public void aZeroGpLossClosesTheOldestLotRatherThanDeletingAFreeOne()
	{
		AcquisitionRecord bought = new AcquisitionRecord(2, 200, null, AcquisitionSource.GE_TRADE);
		AcquisitionRecord caught = new AcquisitionRecord(2, 0, null, AcquisitionSource.GATHER);
		TrackedItem t = item(4, 200, bought, caught);

		ledger.closeFifo(t, 1, 0, AcquisitionSource.CONSUMED);

		assertEquals("eating leaves a closed row (#371)", 1, closedCount(t));
		assertEquals("the oldest lot is the one consumed", 200, firstClosed(t).getBoughtAt());
		assertEquals(-200, t.getRealizedProfit());
		assertEquals("both free lots survive", 2, caught.getQuantity());
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
		assertEquals("150 gp gross less the 2% GE tax (#380)", 147, (long) sold.getSoldAt());
		assertEquals("basis is preserved", 100, sold.getBoughtAt());
	}

	@Test
	public void geSellPricesFollowTheTaxRules()
	{
		assertEquals("under 50 gp the tax is waived", 49, CostBasisLedger.afterGeTax(49));
		assertEquals("2% rounded down", 50 - 1, CostBasisLedger.afterGeTax(50));
		assertEquals(1_000_000 - 20_000, CostBasisLedger.afterGeTax(1_000_000));
		assertEquals("capped at 5M per item", 1_000_000_000L - 5_000_000L,
				CostBasisLedger.afterGeTax(1_000_000_000L));
	}

	@Test
	public void instantFillParksTheShortfallAndFlushClosesItAfterTheSuspend()
	{
		TrackedItem t = item(10, 100, new AcquisitionRecord(10, 100, null, AcquisitionSource.GATHER));

		ledger.onGeOffer(1, ITEM, false, false, false, 10, 0, 0);
		ledger.onGeOffer(1, ITEM, false, false, false, 10, 10, 1500);

		assertEquals("nothing closed yet — the fill outran its suspension (#107 Finding 3)", 0, closedCount(t));

		ledger.applyDelta(t, -10);
		ledger.flushPendingRealize();

		AcquisitionRecord sold = firstClosed(t);
		assertEquals(0, t.getSuspended(SuspensionSource.SELL));
		assertEquals("the parked fill closes once the units suspend", 1, closedCount(t));
		assertEquals(147, (long) sold.getSoldAt());
	}

	@Test
	public void tradeGivenClosesAgainstItsSuspension()
	{
		TrackedItem t = item(10, 100, new AcquisitionRecord(10, 100, null, AcquisitionSource.GATHER));

		ledger.queueTradeSuspend(ITEM, 10);
		ledger.applyDelta(t, -10);
		assertEquals("offered units suspend, not closed", 10, t.getSuspended(SuspensionSource.TRADE));

		ledger.realizeTradeSale(ITEM, 10, 150);

		AcquisitionRecord sold = firstClosed(t);
		assertEquals(0, t.getSuspended(SuspensionSource.TRADE));
		assertEquals("the given lot closes at the apportioned trade price", 1, closedCount(t));
		assertEquals(150, (long) sold.getSoldAt());
		assertEquals(AcquisitionSource.PLAYER_TRADE, sold.sellSourceOrUnknown());
	}

	@Test
	public void sameTickTradeAcceptParksShortfallThenFlushClosesIt()
	{
		TrackedItem t = item(10, 100, new AcquisitionRecord(10, 100, null, AcquisitionSource.GATHER));

		ledger.queueTradeSuspend(ITEM, 10);
		ledger.realizeTradeSale(ITEM, 10, 150);

		assertEquals("nothing closed yet — the accept outran the offer's decrease (#175)", 0, closedCount(t));
		assertEquals(0, t.getSuspended(SuspensionSource.TRADE));

		ledger.applyDelta(t, -10);
		ledger.flushPendingRealize();

		AcquisitionRecord sold = firstClosed(t);
		assertEquals("the parked sale closes once the units suspend", 1, closedCount(t));
		assertEquals(0, t.getSuspended(SuspensionSource.TRADE));
		assertEquals(150, (long) sold.getSoldAt());
		assertEquals(AcquisitionSource.PLAYER_TRADE, sold.sellSourceOrUnknown());
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

	private static AcquisitionRecord firstOpenWithSource(TrackedItem t, AcquisitionSource source)
	{
		for (AcquisitionRecord r : t.getAcquisitions())
			if (r.getSoldAt() == null && r.sourceOrUnknown() == source)
				return r;

		return null;
	}

	/**
	 * #287 regression: a decant output's transferred basis, queued the same tick an earlier source (here a
	 * sell-unsuspend) fully absorbs the positive delta, must survive the zero-qty consumeDecantOutput call and
	 * still price the real output when it lands a later tick — not fall through to the fallback. The 500 avg
	 * fallback differs from the 100 gp/unit carried basis so the two outcomes are distinguishable.
	 */
	@Test
	public void queuedDecantBasisSurvivesAFullyConsumedTickAndPricesTheLaterOutput()
	{
		TrackedItem t = item(10, 500, new AcquisitionRecord(10, 100, null, AcquisitionSource.GATHER));

		ledger.onGeOffer(1, ITEM, false, false, false, 3, 0, 0);
		ledger.applyDelta(t, -3);
		assertEquals("a 3-unit sell suspends", 3, t.getSuspended(SuspensionSource.SELL));

		ledger.queueDecantOutput(ITEM, 300);

		ledger.onGeOffer(1, ITEM, false, true, false, 3, 0, 0);
		ledger.applyDelta(t, 3);
		assertEquals("the cancel's +3 is fully eaten by the sell-unsuspend before consumeDecantOutput ran", 0,
				t.getSuspended(SuspensionSource.SELL));
		assertTrue("the queued decant basis is still parked, not discarded",
				ledger.hasDecantOrConsumedOutput(ITEM));

		host.tick++;
		ledger.applyDelta(t, 3);

		AcquisitionRecord decant = firstOpenWithSource(t, AcquisitionSource.DECANT);
		assertEquals("the real output lands a later tick as a DECANT lot", 3, decant.getQuantity());
		assertEquals("priced at the carried basis, not the 500 avg fallback", 100, decant.getBoughtAt());
		assertFalse("the basis is consumed by the landing output", ledger.hasDecantOrConsumedOutput(ITEM));
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

	/**
	 * A buy-limit window that is null or shorter than {@code [start, quantity]} is valid JSON of the
	 * wrong shape, so it parses cleanly and used to throw when indexed - inside the login block, which
	 * aborted the rest of it. Load must skip those and still take the well-formed ones (#329).
	 */
	@Test
	public void loadSkipsMalformedBuyLimitWindows()
	{
		CostBasisLedger corrupt = new CostBasisLedger(host, new CorruptLimitsPersistence());
		corrupt.load();

		TrackedItem tracked = item(0, 100);
		corrupt.applyBuyLimitFields(tracked);
		assertEquals(300, tracked.getLimitBought());

		for (int id : new int[]{1381, 1383, 1385})
		{
			TrackedItem other = new TrackedItem(id, "Other");
			corrupt.applyBuyLimitFields(other);
			assertEquals(0, other.getLimitBought());
		}
	}

	/**
	 * The GE state used to be written on a one-minute debounce and flushed unconditionally only at
	 * plugin shutdown. Logging out or hopping accounts inside that window lost the fill's durable
	 * claim, because the next login's load() overwrites memory with whatever was last on disk - and
	 * the collected units then priced at fallbackPrice instead of what they actually cost (#328).
	 */
	@Test
	public void aBuyFillPersistsTheLedgerImmediately()
	{
		CountingPersistence saved = new CountingPersistence();
		CostBasisLedger eager = new CostBasisLedger(host, saved);

		eager.onGeOffer(0, ITEM, true, false, false, 5, 0, 0);
		assertEquals("placing an offer alone writes nothing", 0, saved.saves);

		eager.onGeOffer(0, ITEM, true, false, false, 5, 5, 500);
		assertEquals("the fill is written the moment it lands", 1, saved.saves);
		assertEquals(5, saved.lastLedger.get(ITEM).get(0)[0]);
		assertEquals(100, saved.lastLedger.get(ITEM).get(0)[1]);
	}

	/** A second fill is written too - the debounce used to swallow everything for the next minute. */
	@Test
	public void everyBuyFillPersists()
	{
		CountingPersistence saved = new CountingPersistence();
		CostBasisLedger eager = new CostBasisLedger(host, saved);

		eager.onGeOffer(0, ITEM, true, false, false, 10, 0, 0);
		eager.onGeOffer(0, ITEM, true, false, false, 10, 4, 400);
		eager.onGeOffer(0, ITEM, true, false, false, 10, 10, 1000);
		assertEquals(2, saved.saves);
	}

	/** Buy-limit windows are persisted even with source pricing off, where no durable claim is made. */
	@Test
	public void buyLimitWindowsPersistWithoutSourcePricing()
	{
		host.sourcePricing = false;
		CountingPersistence saved = new CountingPersistence();
		CostBasisLedger eager = new CostBasisLedger(host, saved);

		eager.onGeOffer(0, ITEM, true, false, false, 5, 0, 0);
		eager.onGeOffer(0, ITEM, true, false, false, 5, 5, 500);
		assertEquals(1, saved.saves);
		assertEquals(5, saved.lastLimits.get(ITEM)[1]);
	}

	@Test
	public void loginPrimeSeedsExistingFillsSoTheyAreNotReplayed()
	{
		TrackedItem t = item(0, 999);
		host.offers = new GrandExchangeOffer[]{offer(GrandExchangeOfferState.BUYING, 10, 4, 400)};

		ledger.primeGeStateFromLogin();
		ledger.onGeOffer(0, ITEM, true, false, false, 10, 4, 400);
		ledger.applyDelta(t, 4);

		assertEquals("a fill from before login is not replayed as a fresh buy", 999, firstOpen(t).getBoughtAt());

		ledger.onGeOffer(0, ITEM, true, false, false, 10, 6, 600);
		ledger.applyDelta(t, 2);

		assertTrue("the next real fill still prices at the buy price", t.getAcquisitions()
				.stream()
				.anyMatch(r -> r.getBoughtAt() == 100 && r.sourceOrUnknown() == AcquisitionSource.GE_TRADE));
	}

	@Test
	public void loginPrimeDoesNotReplayAnOpenSellAsAFreshPlacement()
	{
		TrackedItem t = item(5, 100, new AcquisitionRecord(15, 100, null, AcquisitionSource.GATHER));
		host.offers = new GrandExchangeOffer[]{offer(GrandExchangeOfferState.SELLING, 10, 0, 0)};

		ledger.primeGeStateFromLogin();
		ledger.onGeOffer(0, ITEM, false, false, false, 10, 0, 0);
		ledger.applyDelta(t, -2);

		assertEquals("the pre-login sell is not suspended a second time", 10, t.getSuspended(SuspensionSource.SELL));
		assertEquals("an unrelated removal closes normally", 1, closedCount(t));
	}

	@Test
	public void loginPrunesDurableBuysThatWereCollectedOffline()
	{
		CountingPersistence saved = new CountingPersistence();
		CostBasisLedger eager = new CostBasisLedger(host, saved);
		eager.onGeOffer(0, ITEM, true, false, false, 5, 0, 0);
		eager.onGeOffer(0, ITEM, true, false, false, 5, 5, 500);
		host.offers = new GrandExchangeOffer[]{null, offer(GrandExchangeOfferState.EMPTY, 0, 0, 0)};

		eager.primeGeStateFromLogin();

		assertEquals("the pruned ledger is written", 2, saved.saves);
		assertNull("no claim survives for a buy no longer in the GE", saved.lastLedger.get(ITEM));
	}

	@Test
	public void loginKeepsDurableBuysStillSittingInTheGe()
	{
		CountingPersistence saved = new CountingPersistence();
		CostBasisLedger eager = new CostBasisLedger(host, saved);
		eager.onGeOffer(0, ITEM, true, false, false, 5, 0, 0);
		eager.onGeOffer(0, ITEM, true, false, false, 5, 5, 500);
		host.offers = new GrandExchangeOffer[]{offer(GrandExchangeOfferState.BOUGHT, 5, 5, 500)};

		eager.primeGeStateFromLogin();

		assertEquals("nothing pruned, nothing rewritten", 1, saved.saves);
		TrackedItem t = item(0, 999);
		eager.applyDelta(t, 5);
		assertEquals("collecting after login still prices at the buy price", 100, firstOpen(t).getBoughtAt());
	}

	@Test
	public void loginRebuildsSellSuspensionFromOpenSellOffers()
	{
		TrackedItem t = item(3, 100, new AcquisitionRecord(10, 100, null, AcquisitionSource.GATHER));
		host.offers = new GrandExchangeOffer[]{
			offer(GrandExchangeOfferState.SELLING, 10, 3, 300),
			offer(GrandExchangeOfferState.BUYING, 4, 0, 0)
		};

		ledger.primeGeStateFromLogin();

		assertEquals("only the unsold part of the open sell stays suspended", 7, t.getSuspended(SuspensionSource.SELL));
	}

	@Test
	public void cancelledSellAtLoginStaysSuspendedUntilCollected()
	{
		TrackedItem t = item(0, 100, new AcquisitionRecord(10, 100, null, AcquisitionSource.GATHER));
		host.offers = new GrandExchangeOffer[]{offer(GrandExchangeOfferState.CANCELLED_SELL, 10, 4, 400)};

		ledger.primeGeStateFromLogin();

		assertEquals("the uncollected return is still the player's", 6, t.getSuspended(SuspensionSource.SELL));

		ledger.applyDelta(t, 6);

		assertEquals(0, t.getSuspended(SuspensionSource.SELL));
		assertEquals("collecting restores the original lot, no fresh acquisition", 1, t.getAcquisitions().size());
	}

	@Test
	public void expiredGroundSuspensionClosesAsAZeroGpGroundLoss()
	{
		TrackedItem t = item(2, 100, new AcquisitionRecord(5, 100, null, AcquisitionSource.GATHER));
		t.restoreSuspended(SuspensionSource.GROUND, 3, minutesAgo(11));

		ledger.expireSuspensions();

		AcquisitionRecord lost = firstClosed(t);
		assertEquals(0, t.getSuspended(SuspensionSource.GROUND));
		assertEquals(3, lost.getQuantity());
		assertEquals(0L, (long) lost.getSoldAt());
		assertEquals(AcquisitionSource.GROUND, lost.sellSourceOrUnknown());
	}

	@Test
	public void suspensionsInsideTheirWindowDoNotExpire()
	{
		TrackedItem t = item(2, 100, new AcquisitionRecord(5, 100, null, AcquisitionSource.GATHER));
		t.restoreSuspended(SuspensionSource.GROUND, 3, minutesAgo(9));
		t.restoreSuspended(SuspensionSource.SELL, 1, minutesAgo(600));

		ledger.expireSuspensions();

		assertEquals(3, t.getSuspended(SuspensionSource.GROUND));
		assertEquals("a GE sell never times out", 1, t.getSuspended(SuspensionSource.SELL));
		assertEquals(0, closedCount(t));
	}

	@Test
	public void deathSuspensionExpiresAfterItsRecoveryWindow()
	{
		TrackedItem t = item(0, 100, new AcquisitionRecord(2, 100, null, AcquisitionSource.GE_TRADE));
		t.restoreSuspended(SuspensionSource.DEATH, 2, minutesAgo(66));

		ledger.expireSuspensions();

		assertEquals(0, t.getSuspended(SuspensionSource.DEATH));
		assertEquals(AcquisitionSource.DEATH, firstClosed(t).sellSourceOrUnknown());
		assertEquals(0L, (long) firstClosed(t).getSoldAt());
	}

	@Test
	public void expiredGravestoneClosesDeathLossesOnlyAfterTheGrace()
	{
		TrackedItem t = item(4, 100, new AcquisitionRecord(4, 100, null, AcquisitionSource.GE_TRADE));
		ledger.signalDeath();
		ledger.applyDelta(t, -4);
		ledger.onGravestoneVisibility(true, false);
		host.tick = 200;
		ledger.onGravestoneVisibility(false, true);

		host.tick = 204;
		ledger.closeVanishedGraveLosses();
		assertEquals("still inside the grace for a last-tick collection", 4, t.getSuspended(SuspensionSource.DEATH));

		host.tick = 205;
		ledger.closeVanishedGraveLosses();
		assertEquals(0, t.getSuspended(SuspensionSource.DEATH));
		assertEquals(AcquisitionSource.DEATH, firstClosed(t).sellSourceOrUnknown());
	}

	@Test
	public void collectedGravestoneArmsNoLoss()
	{
		TrackedItem t = item(4, 100, new AcquisitionRecord(4, 100, null, AcquisitionSource.GE_TRADE));
		ledger.signalDeath();
		ledger.applyDelta(t, -4);
		ledger.onGravestoneVisibility(true, false);
		ledger.onGravestoneVisibility(false, false);

		host.tick += 50;
		ledger.closeVanishedGraveLosses();

		assertEquals("a grave that vanished with time left was collected", 4, t.getSuspended(SuspensionSource.DEATH));
		assertEquals(0, closedCount(t));
	}

	@Test
	public void aQueuedDropSuspendsOnlyItsUnitsAndKeepsTheRestQueued()
	{
		TrackedItem t = item(10, 100, new AcquisitionRecord(10, 100, null, AcquisitionSource.GATHER));
		ledger.queueGroundSuspend(ITEM, 5);

		ledger.applyDelta(t, -3);

		assertEquals(3, t.getSuspended(SuspensionSource.GROUND));
		assertEquals("the unconsumed drop stays queued", 2, ledger.pendingGroundSuspend(ITEM));
		assertEquals("dropped units keep their lot open", 0, closedCount(t));
	}

	@Test
	public void aRemovalLargerThanTheQueuedDropClosesTheRest()
	{
		TrackedItem t = item(10, 100, new AcquisitionRecord(10, 100, null, AcquisitionSource.GATHER));
		ledger.queueGroundSuspend(ITEM, 2);

		ledger.applyDelta(t, -5);

		assertEquals(2, t.getSuspended(SuspensionSource.GROUND));
		assertEquals(0, ledger.pendingGroundSuspend(ITEM));
		assertEquals("the three unexplained units close", 3, firstClosed(t).getQuantity());
	}

	@Test
	public void logoutClosesEveryGroundSuspensionAsLost()
	{
		TrackedItem t = item(7, 100, new AcquisitionRecord(10, 100, null, AcquisitionSource.GATHER));
		t.addSuspended(SuspensionSource.GROUND, 3);
		ledger.queueGroundSuspend(ITEM, 4);

		ledger.closeAllGroundSuspensions();

		assertEquals(0, t.getSuspended(SuspensionSource.GROUND));
		assertEquals(0, ledger.pendingGroundSuspend(ITEM));
		assertEquals(AcquisitionSource.GROUND, firstClosed(t).sellSourceOrUnknown());
		assertEquals(3, firstClosed(t).getQuantity());
	}

	@Test
	public void closeGroundLostIgnoresUntrackedAndUnsuspendedItems()
	{
		TrackedItem t = item(5, 100, new AcquisitionRecord(5, 100, null, AcquisitionSource.GATHER));

		assertFalse(ledger.closeGroundLost(ITEM + 1, 3));
		assertFalse(ledger.closeGroundLost(ITEM, 3));
		assertEquals(0, closedCount(t));
	}

	@Test
	public void loginResetDropsSessionRoutingButKeepsBuyClaims()
	{
		TrackedItem t = item(0, 999);
		ledger.onGeOffer(0, ITEM, true, false, false, 5, 0, 0);
		ledger.onGeOffer(0, ITEM, true, false, false, 5, 5, 500);
		ledger.queueGroundSuspend(ITEM, 3);

		ledger.resetForLogin();

		assertEquals(0, ledger.pendingGroundSuspend(ITEM));
		ledger.applyDelta(t, 5);
		assertEquals("a buy filled before the relog still prices the collection", 100, firstOpen(t).getBoughtAt());
	}

	@Test
	public void shutdownResetDropsBuyClaims()
	{
		TrackedItem t = item(0, 999);
		ledger.onGeOffer(0, ITEM, true, false, false, 5, 0, 0);
		ledger.onGeOffer(0, ITEM, true, false, false, 5, 5, 500);

		ledger.resetForShutdown();
		ledger.applyDelta(t, 5);

		assertEquals("with the claims gone the gain falls back to the average", 999, firstOpen(t).getBoughtAt());
	}
}
