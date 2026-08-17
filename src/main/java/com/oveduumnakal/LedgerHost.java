/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.Collection;

import net.runelite.api.GrandExchangeOffer;

/**
 * The thin seam between {@link CostBasisLedger} and the RuneLite client (#255). The ledger owns the
 * cost-basis lot engine, GE pricing, suspension bookkeeping, and buy-limit windows as client-free
 * logic; everything it still needs from the live client, config, or panel comes through this
 * interface, so the core takes plain values and stays unit-testable with a hand-rolled host.
 *
 * <p>Implemented by {@code StockpilePlugin}, whose methods forward to {@code client}/{@code config}/
 * the panel. The only client type that crosses the seam is {@link GrandExchangeOffer}, read by the
 * login reconciliation; all other callbacks trade in primitives and domain {@link TrackedItem}s.
 */
interface LedgerHost
{
	/** @return the current game tick ({@code client.getTickCount()}). */
	int currentTick();

	/** @return whether Source-Based Pricing is enabled ({@code config.sourcePricing()}). */
	boolean sourcePricing();

	/** @return the configured cost-basis fallback policy for unknown-source changes. */
	FallbackPricing fallbackPricing();

	/** @return the tracked item for {@code itemId}, or {@code null} when it is not tracked. */
	TrackedItem trackedItem(int itemId);

	/** @return the live tracked items, for the expiry sweeps and login reconciliation. */
	Collection<TrackedItem> trackedItems();

	/** Persists the tracked items after the ledger has mutated their lots. */
	void persistTrackedItems();

	/** Refreshes the side panel after a ledger change the user should see. */
	void refreshPanel();

	/** @return whether {@code itemId} is a consumable whose unclaimed removal closes at 0. */
	boolean isConsumable(int itemId);

	/** @return whether {@code itemId} is ammo destroyed on use (a cannonball, a thrown chinchompa). */
	boolean isDestroyedAmmo(int itemId);

	/** @return whether {@code itemId} is recoverable ranged/thrown ammo that lands on the target's tile. */
	boolean isRecoverableAmmo(int itemId);

	/** @return whether {@code itemId} is an empty vessel freed by finishing a potion or drink. */
	boolean isEmptyContainer(int itemId);

	/** @return the live GE offers ({@code client.getGrandExchangeOffers()}), or {@code null} before login. */
	GrandExchangeOffer[] openGeOffers();
}
