/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

/**
 * The client seam {@link DeltaDetectors} reads through, in the same shape as {@link LedgerHost}.
 *
 * <p>The detector suite is the subtlest logic in the plugin and had zero direct test coverage,
 * because reaching it needed a live {@code Client} (#334). Everything the delta detectors actually
 * need from the client is here — the tick counter, a few item predicates, and the tick each
 * relevant signal last fired on — so the detectors themselves are pure and testable.
 */
interface DetectorHost
{
	/** @return the current client tick, against which every detector's signal window is measured. */
	int currentTick();

	/** @return whether Source-Based Pricing is on; every detector is a no-op when it is not. */
	boolean sourcePricing();

	/** @return whether {@code itemId} is in the tracked list. */
	boolean isTracked(int itemId);

	/** @return the tracked item for {@code itemId}, or {@code null} when it is not tracked. */
	TrackedItem trackedItem(int itemId);

	/** @return {@code itemId}'s display name, for dose-family parsing. */
	String itemName(int itemId);

	/** @return an untracked processing input's per-unit value under the configured fallback pricing. */
	long untrackedInputValue(int itemId);

	/** @return whether {@code itemId} is a rune consumed by casting a spell rather than a processing input. */
	boolean isSpellcastRune(int itemId);

	/**
	 * @return whether {@code itemId} is a worthless destroyed processing product — untradeable, absent
	 *         from the GE mapping, and with no market value (burnt food, a crushed gem)
	 */
	boolean isDestroyedProduct(int itemId);

	/** @return whether {@code itemId} is ammunition, which a recipe never consumes as an input. */
	boolean isAmmo(int itemId);

	/** @return the tick a processing/crafting skill last granted XP. */
	int processingXpTick();

	/** @return the tick Magic last granted XP, marking a cast rather than a recipe. */
	int magicXpTick();

	/** @return the tick a gathering skill last granted XP. */
	int gatherXpTick();

	/** @return the tick Thieving last granted XP. */
	int thievingXpTick();

	/** @return the tick a reward/loot container or chat line last signalled loot. */
	int rewardContainerTick();
}
