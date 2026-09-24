/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemQuantityChanged;
import net.runelite.api.events.ItemSpawned;

/**
 * The ground-item source detector (#65): pairs a tick's ground spawns, despawns and stack changes with
 * that tick's inventory deltas to tell the player's own drops (suspended, not sold), re-pickups of them
 * (un-suspended), drops that vanished (closed as lost at 0) and loot picked up off the floor (claimed as
 * {@link AcquisitionSource#GROUND} at 0).
 *
 * <p>The last piece of the #334 detector extraction: it lived in {@code StockpilePlugin} and needed a live
 * client to reach, so it had no direct coverage. Everything the client provides now comes through
 * {@link DetectorHost}; the ground events themselves are plain RuneLite value objects.
 */
class GroundDetector
{
	private final DetectorHost host;

	private final CostBasisLedger ledger;

	/** This tick's ground spawns/despawns/stack changes, correlated against the inventory deltas. */
	private final List<ItemSpawned> tickSpawns = new ArrayList<>();
	private final List<ItemDespawned> tickDespawns = new ArrayList<>();
	private final List<ItemQuantityChanged> tickQuantityChanges = new ArrayList<>();

	/** Ground items this player dropped: the {@code TileItem} → how many of its units are ours. */
	private final Map<TileItem, Integer> myDrops = new HashMap<>();

	GroundDetector(DetectorHost host, CostBasisLedger ledger)
	{
		this.host = host;
		this.ledger = ledger;
	}

	/** Buffers a spawn of a tracked item for this tick's correlation. */
	void onSpawn(ItemSpawned event)
	{
		if (host.isTracked(host.canonicalize(event.getItem().getId())))
			tickSpawns.add(event);
	}

	/** Buffers a despawn of one of our drops or of a tracked item, for #65's pickup/lost-drop correlation. */
	void onDespawn(ItemDespawned event)
	{
		if (isRelevant(event.getItem()))
			tickDespawns.add(event);
	}

	/** Buffers a ground-stack quantity change so drops onto an existing stack correlate like spawns. */
	void onQuantityChanged(ItemQuantityChanged event)
	{
		if (isRelevant(event.getItem()))
			tickQuantityChanges.add(event);
	}

	/** @return whether a pile is one we dropped or holds a tracked item. */
	private boolean isRelevant(TileItem item)
	{
		return myDrops.containsKey(item) || host.isTracked(host.canonicalize(item.getId()));
	}

	/**
	 * Correlates this tick's ground-item activity with the pending inventory deltas: a spawn (or stack
	 * increase) on the player's tile matching a pending removal is our drop — its units queue for ground
	 * suspension and the {@code TileItem} is remembered; a despawn of a remembered drop with no matching
	 * pickup closes its units as lost at 0; a despawn matching a pending addition that isn't ours is a loot
	 * pickup, claimed as a {@link AcquisitionSource#GROUND} acquisition at 0. Runs before the quantity sync
	 * consumes the deltas, and empties this tick's buffers.
	 *
	 * @param pendingDeltas this tick's net inventory deltas by canonical id; read, never modified
	 * @return whether any lost drop was closed, so the caller persists and refreshes once
	 */
	boolean correlate(Map<Integer, Integer> pendingDeltas)
	{
		if (tickSpawns.isEmpty() && tickDespawns.isEmpty() && tickQuantityChanges.isEmpty())
			return false;

		WorldPoint myLocation = host.playerLocation();

		for (ItemSpawned spawn : tickSpawns)
			gained(spawn.getItem(), spawn.getTile(), spawn.getItem().getQuantity(), myLocation, pendingDeltas);

		boolean lossClosed = false;
		for (ItemQuantityChanged change : tickQuantityChanges)
		{
			int delta = change.getNewQuantity() - change.getOldQuantity();
			if (delta > 0)
				gained(change.getItem(), change.getTile(), delta, myLocation, pendingDeltas);
			else
				lossClosed |= taken(change.getItem(), -delta, pendingDeltas);
		}

		for (ItemDespawned despawn : tickDespawns)
			lossClosed |= taken(despawn.getItem(), despawn.getItem().getQuantity(), pendingDeltas);

		clearTick();
		return lossClosed;
	}

	/**
	 * Handles a ground pile gaining units: on our tile against a pending removal, it's our drop. Gated by
	 * the Source-Based Pricing toggle — when off, no new ground suspensions are taken, so a drop closes
	 * classically at the average price; drops suspended while the toggle was on still resolve through the
	 * un-suspend/lost paths.
	 */
	private void gained(TileItem item, Tile tile, int gained, WorldPoint myLocation,
			Map<Integer, Integer> pendingDeltas)
	{
		if (!host.sourcePricing())
			return;

		if (myLocation == null || !myLocation.equals(tile.getWorldLocation()))
			return;

		int canonicalId = host.canonicalize(item.getId());
		if (!host.isTracked(canonicalId))
			return;

		int queued = ledger.pendingGroundSuspend(canonicalId);
		int pendingRemoval = -pendingDeltas.getOrDefault(canonicalId, 0) - queued;
		if (pendingRemoval <= 0)
			return;

		int qty = Math.min(gained, pendingRemoval);
		ledger.queueGroundSuspend(canonicalId, qty);
		myDrops.merge(item, qty, Integer::sum);
	}

	/**
	 * Handles a ground pile losing units: a remembered drop with a matching pending addition is a
	 * re-pickup (the greedy un-suspend consumes it during the sync); with no matching addition its units
	 * close as lost at 0. An unfamiliar pile matching a pending addition is a loot pickup, claimed as
	 * {@code GROUND} at 0.
	 *
	 * @return whether a lost drop was closed
	 */
	private boolean taken(TileItem item, int taken, Map<Integer, Integer> pendingDeltas)
	{
		int canonicalId = host.canonicalize(item.getId());
		Integer ours = myDrops.get(item);
		int pendingAddition = pendingDeltas.getOrDefault(canonicalId, 0);

		if (ours != null)
		{
			int resolved = Math.min(ours, taken);
			boolean lossClosed = false;
			if (pendingAddition > 0)
				ledger.queueGroundUnsuspend(canonicalId, Math.min(resolved, pendingAddition));
			else
				lossClosed = ledger.closeGroundLost(canonicalId, resolved);

			if (resolved >= ours)
				myDrops.remove(item);
			else
				myDrops.put(item, ours - resolved);

			return lossClosed;
		}

		if (pendingAddition > 0 && host.isTracked(canonicalId))
			ledger.claim(AcquisitionSource.GROUND, canonicalId, Math.min(taken, pendingAddition), 0,
					host.currentTick());

		return false;
	}

	/** @return how many units of the pile are known to be this player's drop (0 when it isn't ours). */
	int ourUnits(TileItem item)
	{
		return myDrops.getOrDefault(item, 0);
	}

	/** Drops this tick's buffered ground events (a scene load, or teardown). */
	void clearTick()
	{
		tickSpawns.clear();
		tickDespawns.clear();
		tickQuantityChanges.clear();
	}

	/** Forgets which piles are ours (their suspensions were closed or the scene was reloaded). */
	void forgetDrops()
	{
		myDrops.clear();
	}
}
