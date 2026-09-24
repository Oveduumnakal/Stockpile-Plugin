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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import net.runelite.api.GrandExchangeOffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit net for {@link DeltaDetectors} (#334): the per-tick source detectors, which had no direct
 * coverage at all while they lived inside {@code StockpilePlugin} and needed a live client to reach.
 * Drives them through a hand-rolled {@link DetectorHost} and a recording {@link CostBasisLedger}, so
 * each detector's decision - what it claims, at what source, at what price, and what it declines to
 * touch - is asserted in isolation.
 */
public class DeltaDetectorsTest
{
	private static final int TICK = 100;
	private static final int COINS = 995;
	private static final int ORE = 440;
	private static final int BAR = 2349;
	private static final int NATURE_RUNE = 561;
	private static final int RAW_FISH = 359;
	private static final int BURNT_FISH = 343;
	private static final int POTION_4 = 2434;
	private static final int POTION_2 = 2436;

	/** One recorded {@link CostBasisLedger#claim} call. */
	private static final class Claim
	{
		private final AcquisitionSource source;
		private final int itemId;
		private final int quantity;
		private final long unitPrice;

		Claim(AcquisitionSource source, int itemId, int quantity, long unitPrice)
		{
			this.source = source;
			this.itemId = itemId;
			this.quantity = quantity;
			this.unitPrice = unitPrice;
		}
	}

	/** A no-op {@link LedgerHost}, enough to construct a real ledger without a client. */
	private static final class NoHost implements LedgerHost
	{
		private final Map<Integer, TrackedItem> items = new HashMap<>();

		@Override
		public int currentTick()
		{
			return TICK;
		}

		@Override
		public boolean sourcePricing()
		{
			return true;
		}

		@Override
		public FallbackPricing fallbackPricing()
		{
			return FallbackPricing.AVG;
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
			return false;
		}

		@Override
		public boolean isDestroyedAmmo(int itemId)
		{
			return false;
		}

		@Override
		public boolean isRecoverableAmmo(int itemId)
		{
			return false;
		}

		@Override
		public boolean isEmptyContainer(int itemId)
		{
			return false;
		}

		@Override
		public GrandExchangeOffer[] openGeOffers()
		{
			return null;
		}
	}

	/** A ledger that records what the detectors ask of it instead of applying it. */
	private static final class RecordingLedger extends CostBasisLedger
	{
		private final List<Claim> claims = new ArrayList<>();
		private final Map<Integer, Long> processingOutputs = new LinkedHashMap<>();
		private final Map<Integer, Long> decantOutputs = new LinkedHashMap<>();
		private final Map<Integer, Long> consumedOutputs = new LinkedHashMap<>();
		private int emptied;

		RecordingLedger(LedgerHost host)
		{
			super(host, new NoopPersistence());
		}

		@Override
		void claim(AcquisitionSource source, int itemId, int quantity, long unitPrice, int currentTick)
		{
			claims.add(new Claim(source, itemId, quantity, unitPrice));
		}

		@Override
		void queueProcessingOutput(int itemId, long totalCost)
		{
			processingOutputs.put(itemId, totalCost);
		}

		@Override
		void queueDecantOutput(int itemId, long totalCost)
		{
			decantOutputs.put(itemId, totalCost);
		}

		@Override
		void queueConsumedOutput(int itemId, long totalCost)
		{
			consumedOutputs.put(itemId, totalCost);
		}

		@Override
		boolean hasProcessingOutput(int itemId)
		{
			return processingOutputs.containsKey(itemId);
		}

		@Override
		boolean hasDecantOrConsumedOutput(int itemId)
		{
			return decantOutputs.containsKey(itemId) || consumedOutputs.containsKey(itemId);
		}

		@Override
		void addPotionEmptied(int count)
		{
			emptied += count;
		}

		@Override
		void clearProcessingOutput()
		{
			processingOutputs.clear();
		}

		@Override
		void clearDecantAndConsumedOutput()
		{
			decantOutputs.clear();
			consumedOutputs.clear();
		}

		@Override
		void resetPotionEmptied()
		{
			emptied = 0;
		}

		@Override
		int potionEmptiedTick()
		{
			return -1;
		}
	}

	/** A persistence stub that neither reads nor writes config. */
	private static final class NoopPersistence extends StockpilePersistence
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

	/** A controllable {@link DetectorHost}: settable signal ticks and an in-memory tracked table. */
	private static final class FakeDetectorHost implements DetectorHost
	{
		private boolean sourcePricing = true;
		private final Map<Integer, TrackedItem> tracked = new HashMap<>();
		private final Map<Integer, String> names = new HashMap<>();
		private final Set<Integer> destroyed = new HashSet<>();
		private final Set<Integer> spellcastRunes = new HashSet<>();
		private final Set<Integer> ammo = new HashSet<>();
		private long untrackedValue = 7;
		private int processingXp = -100;
		private int magicXp = -100;
		private int gatherXp = -100;
		private int thievingXp = -100;
		private int reward = -100;

		@Override
		public int currentTick()
		{
			return TICK;
		}

		@Override
		public boolean sourcePricing()
		{
			return sourcePricing;
		}

		@Override
		public boolean isTracked(int itemId)
		{
			return tracked.containsKey(itemId);
		}

		@Override
		public TrackedItem trackedItem(int itemId)
		{
			return tracked.get(itemId);
		}

		@Override
		public String itemName(int itemId)
		{
			return names.getOrDefault(itemId, "Item " + itemId);
		}

		@Override
		public long untrackedInputValue(int itemId)
		{
			return untrackedValue;
		}

		@Override
		public boolean isSpellcastRune(int itemId)
		{
			return spellcastRunes.contains(itemId);
		}

		@Override
		public boolean isDestroyedProduct(int itemId)
		{
			return destroyed.contains(itemId);
		}

		@Override
		public boolean isAmmo(int itemId)
		{
			return ammo.contains(itemId);
		}

		@Override
		public int processingXpTick()
		{
			return processingXp;
		}

		@Override
		public int magicXpTick()
		{
			return magicXp;
		}

		@Override
		public int gatherXpTick()
		{
			return gatherXp;
		}

		@Override
		public int thievingXpTick()
		{
			return thievingXp;
		}

		@Override
		public int rewardContainerTick()
		{
			return reward;
		}
	}

	private final FakeDetectorHost host = new FakeDetectorHost();

	private final RecordingLedger ledger = new RecordingLedger(new NoHost());

	private final DeltaDetectors detectors = new DeltaDetectors(host, ledger);

	/** Registers a tracked item with one open lot of {@code qty} bought at {@code boughtAt}. */
	private TrackedItem track(int itemId, String name, int qty, long boughtAt)
	{
		TrackedItem item = new TrackedItem(itemId, name);
		item.setQuantity(qty);
		item.setAvgPrice(boughtAt);
		item.setCostBasisInitialized(true);
		item.setAcquisitions(new ArrayList<>(
				Arrays.asList(new AcquisitionRecord(qty, boughtAt, null, AcquisitionSource.MANUAL))));
		host.tracked.put(itemId, item);
		host.names.put(itemId, name);
		return item;
	}

	/** A tick's net deltas, as {@code id, delta} pairs. */
	private static Map<Integer, Integer> deltas(int... pairs)
	{
		Map<Integer, Integer> map = new LinkedHashMap<>();
		for (int i = 0; i < pairs.length; i += 2)
			map.put(pairs[i], pairs[i + 1]);

		return map;
	}

	/** @return the single claim recorded, failing when there was not exactly one. */
	private Claim only()
	{
		assertEquals(1, ledger.claims.size());
		return ledger.claims.get(0);
	}

	/** A smelt: one ore consumed, one bar produced, on a processing-XP tick. */
	@Test
	public void processingRecipeCarriesTheInputBasisOntoTheOutput()
	{
		track(ORE, "Iron ore", 10, 100);
		track(BAR, "Iron bar", 0, 0);
		host.processingXp = TICK;

		detectors.correlateProcessing(deltas(ORE, -2, BAR, 1));

		Claim claim = only();
		assertEquals(AcquisitionSource.PROCESSING, claim.source);
		assertEquals(ORE, claim.itemId);
		assertEquals(2, claim.quantity);
		assertEquals(100, claim.unitPrice);
		assertEquals(Long.valueOf(200), ledger.processingOutputs.get(BAR));
	}

	/** Without a processing-XP tick the same deltas are not a recipe. */
	@Test
	public void withoutProcessingXpNothingIsClaimed()
	{
		track(ORE, "Iron ore", 10, 100);
		track(BAR, "Iron bar", 0, 0);

		detectors.correlateProcessing(deltas(ORE, -2, BAR, 1));

		assertTrue(ledger.claims.isEmpty());
		assertTrue(ledger.processingOutputs.isEmpty());
	}

	/** An untracked input contributes its fallback value to the carried basis but opens no claim. */
	@Test
	public void anUntrackedInputContributesItsFallbackValue()
	{
		track(BAR, "Iron bar", 0, 0);
		host.untrackedValue = 50;
		host.processingXp = TICK;

		detectors.correlateProcessing(deltas(ORE, -3, BAR, 1));

		assertTrue(ledger.claims.isEmpty());
		assertEquals(Long.valueOf(150), ledger.processingOutputs.get(BAR));
	}

	/** Burning a fish destroys it: claimed without any XP signal, at 0, on both sides. */
	@Test
	public void aDestroyedOutputClaimsBothSidesAtZeroWithNoXp()
	{
		track(RAW_FISH, "Raw shark", 5, 900);
		track(BURNT_FISH, "Burnt shark", 0, 0);
		host.destroyed.add(BURNT_FISH);

		detectors.correlateProcessing(deltas(RAW_FISH, -1, BURNT_FISH, 1));

		assertEquals(2, ledger.claims.size());
		for (Claim claim : ledger.claims)
		{
			assertEquals(AcquisitionSource.BURNED, claim.source);
			assertEquals(0, claim.unitPrice);
		}
	}

	/** Ammo is fuel for a shot, never a recipe input, so a destroyed product never books it as a loss. */
	@Test
	public void ammoIsNeverBookedAsADestroyedInput()
	{
		track(NATURE_RUNE, "Dart", 100, 5);
		host.ammo.add(NATURE_RUNE);
		host.destroyed.add(BURNT_FISH);

		detectors.correlateProcessing(deltas(NATURE_RUNE, -10, BURNT_FISH, 1));

		assertTrue(ledger.claims.isEmpty());
	}

	/** A rune spent on a Magic tick is the cast's fuel, claimed as CAST at 0 rather than a recipe input. */
	@Test
	public void aSpellcastRuneIsClaimedAsCastAtZero()
	{
		track(NATURE_RUNE, "Nature rune", 100, 200);
		track(ORE, "Iron ore", 10, 100);
		track(BAR, "Iron bar", 0, 0);
		host.spellcastRunes.add(NATURE_RUNE);
		host.processingXp = TICK;

		detectors.correlateProcessing(deltas(NATURE_RUNE, -1, ORE, -1, BAR, 1));

		assertEquals(2, ledger.claims.size());
		assertEquals(AcquisitionSource.CAST, ledger.claims.get(0).source);
		assertEquals(0, ledger.claims.get(0).unitPrice);
		assertEquals(AcquisitionSource.PROCESSING, ledger.claims.get(1).source);
		assertEquals(ORE, ledger.claims.get(1).itemId);
	}

	/** A cast that produces no item at all is not a recipe, so its remaining inputs stay unclaimed. */
	@Test
	public void aCastWithNoOutputLeavesItsInputsUnclaimed()
	{
		track(ORE, "Iron ore", 10, 100);
		host.magicXp = TICK;

		detectors.correlateProcessing(deltas(ORE, -1));

		assertTrue(ledger.claims.isEmpty());
	}

	/** Two outputs in one tick are unattributable and left to the fallback. */
	@Test
	public void aMultiOutputTickIsNotARecipe()
	{
		track(ORE, "Iron ore", 10, 100);
		host.processingXp = TICK;

		detectors.correlateProcessing(deltas(ORE, -2, BAR, 1, RAW_FISH, 1));

		assertTrue(ledger.claims.isEmpty());
	}

	/** A dose-conserving swap is a decant: the basis follows the liquid, dose-weighted. */
	@Test
	public void aDoseConservingSwapIsADecant()
	{
		track(POTION_4, "Prayer potion(4)", 1, 8000);
		track(POTION_2, "Prayer potion(2)", 0, 0);

		detectors.correlateDecant(deltas(POTION_4, -1, POTION_2, 2));

		Claim claim = only();
		assertEquals(AcquisitionSource.DECANT, claim.source);
		assertEquals(POTION_4, claim.itemId);
		assertEquals(8000, claim.unitPrice);
		assertEquals(Long.valueOf(8000), ledger.decantOutputs.get(POTION_2));
		assertTrue(ledger.consumedOutputs.isEmpty());
	}

	/** Drinking a dose loses doses but leaves some: the FULL basis follows, since a sip realizes nothing. */
	@Test
	public void drinkingADoseCarriesTheFullBasisDown()
	{
		track(POTION_4, "Prayer potion(4)", 1, 8000);
		track(POTION_2, "Prayer potion(3)", 0, 0);

		detectors.correlateDecant(deltas(POTION_4, -1, POTION_2, 1));

		Claim claim = only();
		assertEquals(AcquisitionSource.CONSUMED, claim.source);
		assertEquals(Long.valueOf(8000), ledger.consumedOutputs.get(POTION_2));
		assertTrue(ledger.decantOutputs.isEmpty());
	}

	/** Every dose gone is a last sip, left to the ledger's loss path, and recorded as an emptied vessel. */
	@Test
	public void aLastDoseIsLeftToTheLossPath()
	{
		track(POTION_2, "Prayer potion(1)", 1, 2000);
		host.names.put(POTION_2, "Prayer potion(1)");

		detectors.correlateDecant(deltas(POTION_2, -1));

		assertTrue(ledger.claims.isEmpty());
		assertEquals(1, ledger.emptied);
	}

	/** A processing-XP tick is a recipe, not a decant, so the dose pass stands down. */
	@Test
	public void aProcessingXpTickIsNotADecant()
	{
		track(POTION_4, "Prayer potion(4)", 1, 8000);
		track(POTION_2, "Prayer potion(2)", 0, 0);
		host.processingXp = TICK;

		detectors.correlateDecant(deltas(POTION_4, -1, POTION_2, 2));

		assertTrue(ledger.claims.isEmpty());
	}

	/** An XP-less single-output tick with a tracked product is a combine, carrying the ingredients' basis. */
	@Test
	public void anXplessCombineCarriesTheIngredientBasis()
	{
		track(ORE, "Sunlight moth", 4, 300);
		track(BAR, "Sunlight moth mix", 0, 0);

		detectors.correlateCombine(deltas(ORE, -2, BAR, 1));

		Claim claim = only();
		assertEquals(AcquisitionSource.PROCESSING, claim.source);
		assertEquals(300, claim.unitPrice);
		assertEquals(Long.valueOf(600), ledger.processingOutputs.get(BAR));
	}

	/** Coin movement means a shop or trade, not a combine. */
	@Test
	public void coinMovementRulesOutACombine()
	{
		track(ORE, "Sunlight moth", 4, 300);
		track(BAR, "Sunlight moth mix", 0, 0);

		detectors.correlateCombine(deltas(COINS, -50, ORE, -2, BAR, 1));

		assertTrue(ledger.claims.isEmpty());
	}

	/** An untracked product gives nothing to carry basis onto, so the combine is declined. */
	@Test
	public void anUntrackedCombineOutputIsDeclined()
	{
		track(ORE, "Sunlight moth", 4, 300);

		detectors.correlateCombine(deltas(ORE, -2, BAR, 1));

		assertTrue(ledger.claims.isEmpty());
	}

	/** A dose swap is also an XP-less single-output tick, so the combine pass must skip its ids. */
	@Test
	public void aDoseSwapIsNotRepricedAsACombine()
	{
		track(POTION_4, "Prayer potion(4)", 1, 8000);
		track(POTION_2, "Prayer potion(2)", 0, 0);

		Map<Integer, Integer> tick = deltas(POTION_4, -1, POTION_2, 2);
		detectors.correlateDecant(tick);
		int afterDecant = ledger.claims.size();
		detectors.correlateCombine(tick);

		assertEquals(afterDecant, ledger.claims.size());
	}

	/** A gathering-XP tick books the tick's gains as free. */
	@Test
	public void gatheringXpClaimsGainsAtZero()
	{
		track(ORE, "Iron ore", 0, 0);
		host.gatherXp = TICK;

		detectors.correlateGathering(deltas(ORE, 5));

		Claim claim = only();
		assertEquals(AcquisitionSource.GATHER, claim.source);
		assertEquals(5, claim.quantity);
		assertEquals(0, claim.unitPrice);
	}

	/** Reward loot wins over a coincident gathering-XP tick, so the loot is not mislabelled as gathered. */
	@Test
	public void gatheringYieldsToACoincidentRewardSignal()
	{
		track(ORE, "Iron ore", 0, 0);
		host.gatherXp = TICK;
		host.reward = TICK;

		detectors.correlateGathering(deltas(ORE, 5));

		assertTrue(ledger.claims.isEmpty());
	}

	/** A reward signal books the tick's gains as free loot. */
	@Test
	public void aRewardSignalClaimsGainsAtZero()
	{
		track(ORE, "Iron ore", 0, 0);
		host.reward = TICK;

		detectors.correlateReward(deltas(ORE, 3));

		assertEquals(AcquisitionSource.REWARD, only().source);
	}

	/** A Thieving-XP tick books the tick's gains as free, and yields to reward loot the same way. */
	@Test
	public void thievingClaimsGainsAtZeroAndYieldsToReward()
	{
		track(ORE, "Coins pouch", 0, 0);
		host.thievingXp = TICK;

		detectors.correlateThieving(deltas(ORE, 2));
		assertEquals(AcquisitionSource.THIEVING, only().source);

		ledger.claims.clear();
		host.reward = TICK;
		detectors.correlateThieving(deltas(ORE, 2));
		assertTrue(ledger.claims.isEmpty());
	}

	/** A gain already carrying a recipe's basis keeps it rather than being re-claimed at 0. */
	@Test
	public void aQueuedProcessingOutputIsNotReclaimedAsGathered()
	{
		track(BAR, "Iron bar", 0, 0);
		host.gatherXp = TICK;
		ledger.queueProcessingOutput(BAR, 200);

		detectors.correlateGathering(deltas(BAR, 1));

		assertTrue(ledger.claims.isEmpty());
	}

	/** Coins never participate in any source detector. */
	@Test
	public void coinsAreNeverClaimed()
	{
		track(COINS, "Coins", 0, 0);
		host.gatherXp = TICK;
		host.reward = TICK;
		host.thievingXp = TICK;

		detectors.correlateGathering(deltas(COINS, 500));
		detectors.correlateReward(deltas(COINS, 500));
		detectors.correlateThieving(deltas(COINS, 500));

		assertTrue(ledger.claims.isEmpty());
	}

	/** With Source-Based Pricing off every detector is a no-op. */
	@Test
	public void sourcePricingOffDisablesEveryDetector()
	{
		track(ORE, "Iron ore", 10, 100);
		track(BAR, "Iron bar", 0, 0);
		host.sourcePricing = false;
		host.processingXp = TICK;
		host.gatherXp = TICK;
		host.reward = TICK;
		host.thievingXp = TICK;

		Map<Integer, Integer> tick = deltas(ORE, -2, BAR, 1);
		detectors.correlateProcessing(tick);
		detectors.correlateDecant(tick);
		detectors.correlateCombine(tick);
		detectors.correlateGathering(tick);
		detectors.correlateReward(tick);
		detectors.correlateThieving(tick);

		assertTrue(ledger.claims.isEmpty());
		assertTrue(ledger.processingOutputs.isEmpty());
	}
}
