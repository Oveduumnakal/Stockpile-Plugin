/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemQuantityChanged;
import net.runelite.api.events.ItemSpawned;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link GroundDetector} (#334, #65): the player's own drops are suspended rather than sold,
 * re-pickups un-suspend them, drops that vanish close as lost, loot off the floor is claimed as
 * {@link AcquisitionSource#GROUND} at 0, and nothing happens off the player's tile or with Source-Based
 * Pricing off. The host and ledger are Mockito mocks, so each test checks exactly the ledger calls made.
 */
public class GroundDetectorTest
{
	private static final int ITEM = 560;

	private static final int NOTED = 561;

	private static final int TICK = 100;

	private static final WorldPoint HERE = new WorldPoint(3200, 3200, 0);

	private static final WorldPoint ELSEWHERE = new WorldPoint(3210, 3200, 0);

	private final DetectorHost host = mock(DetectorHost.class);

	private final CostBasisLedger ledger = mock(CostBasisLedger.class);

	private final GroundDetector detector = new GroundDetector(host, ledger);

	private final Map<Integer, Integer> deltas = new HashMap<>();

	@Before
	public void setUp()
	{
		when(host.canonicalize(anyInt())).thenAnswer(invocation ->
		{
			int id = invocation.getArgument(0);
			return id == NOTED ? ITEM : id;
		});
		when(host.isTracked(ITEM)).thenReturn(true);
		when(host.sourcePricing()).thenReturn(true);
		when(host.playerLocation()).thenReturn(HERE);
		when(host.currentTick()).thenReturn(TICK);
	}

	private static TileItem pile(int itemId, int quantity)
	{
		TileItem item = mock(TileItem.class);
		when(item.getId()).thenReturn(itemId);
		when(item.getQuantity()).thenReturn(quantity);
		return item;
	}

	private static Tile tile(WorldPoint at)
	{
		Tile tile = mock(Tile.class);
		when(tile.getWorldLocation()).thenReturn(at);
		return tile;
	}

	/** Drops {@code pile} on our tile against an inventory removal of the same size, and correlates. */
	private void dropped(TileItem pile, int quantity)
	{
		deltas.put(ITEM, -quantity);
		detector.onSpawn(new ItemSpawned(tile(HERE), pile));
		detector.correlate(deltas);
		deltas.clear();
	}

	@Test
	public void aSpawnOnOurTileMatchingARemovalIsOurDrop()
	{
		TileItem drop = pile(ITEM, 5);
		deltas.put(ITEM, -5);

		detector.onSpawn(new ItemSpawned(tile(HERE), drop));
		boolean lossClosed = detector.correlate(deltas);

		verify(ledger).queueGroundSuspend(ITEM, 5);
		assertEquals(5, detector.ourUnits(drop));
		assertFalse(lossClosed);
		assertEquals("the deltas are read, not consumed", Integer.valueOf(-5), deltas.get(ITEM));
	}

	@Test
	public void aDropIsCappedAtTheRemovalNotYetQueued()
	{
		when(ledger.pendingGroundSuspend(ITEM)).thenReturn(2);
		TileItem drop = pile(NOTED, 10);
		deltas.put(ITEM, -5);

		detector.onSpawn(new ItemSpawned(tile(HERE), drop));
		detector.correlate(deltas);

		verify(ledger).queueGroundSuspend(ITEM, 3);
		assertEquals("noted drops count under the unnoted id", 3, detector.ourUnits(drop));
	}

	@Test
	public void aSpawnElsewhereWithoutARemovalOrWithPricingOffIsNotOurs()
	{
		deltas.put(ITEM, -5);
		detector.onSpawn(new ItemSpawned(tile(ELSEWHERE), pile(ITEM, 5)));
		detector.correlate(deltas);

		deltas.clear();
		detector.onSpawn(new ItemSpawned(tile(HERE), pile(ITEM, 5)));
		detector.correlate(deltas);

		when(host.sourcePricing()).thenReturn(false);
		deltas.put(ITEM, -5);
		detector.onSpawn(new ItemSpawned(tile(HERE), pile(ITEM, 5)));
		detector.correlate(deltas);

		verify(ledger, never()).queueGroundSuspend(anyInt(), anyInt());
	}

	@Test
	public void withNoLocalPlayerNothingIsOurs()
	{
		when(host.playerLocation()).thenReturn(null);
		deltas.put(ITEM, -5);

		detector.onSpawn(new ItemSpawned(tile(HERE), pile(ITEM, 5)));
		detector.correlate(deltas);

		verify(ledger, never()).queueGroundSuspend(anyInt(), anyInt());
	}

	@Test
	public void untrackedPilesAreNeverBuffered()
	{
		TileItem shark = pile(385, 1);

		detector.onSpawn(new ItemSpawned(tile(HERE), shark));
		detector.onDespawn(new ItemDespawned(tile(HERE), shark));
		detector.onQuantityChanged(new ItemQuantityChanged(shark, tile(HERE), 1, 2));

		assertFalse(detector.correlate(deltas));
		verify(host, never()).playerLocation();
	}

	@Test
	public void pickingOurDropBackUpUnsuspendsIt()
	{
		TileItem drop = pile(ITEM, 5);
		dropped(drop, 5);
		deltas.put(ITEM, 5);

		detector.onDespawn(new ItemDespawned(tile(HERE), drop));
		detector.correlate(deltas);

		verify(ledger).queueGroundUnsuspend(ITEM, 5);
		assertEquals("a fully picked-up drop is forgotten", 0, detector.ourUnits(drop));
	}

	@Test
	public void ourDropVanishingUnpickedClosesAsLost()
	{
		when(ledger.closeGroundLost(ITEM, 5)).thenReturn(true);
		TileItem drop = pile(ITEM, 5);
		dropped(drop, 5);

		detector.onDespawn(new ItemDespawned(tile(HERE), drop));
		boolean lossClosed = detector.correlate(deltas);

		verify(ledger).closeGroundLost(ITEM, 5);
		assertTrue("the caller persists once", lossClosed);
	}

	@Test
	public void takingPartOfOurStackUnsuspendsOnlyThoseUnits()
	{
		TileItem drop = pile(ITEM, 5);
		dropped(drop, 5);
		deltas.put(ITEM, 3);

		detector.onQuantityChanged(new ItemQuantityChanged(drop, tile(HERE), 5, 2));
		detector.correlate(deltas);

		verify(ledger).queueGroundUnsuspend(ITEM, 3);
		assertEquals(2, detector.ourUnits(drop));
	}

	@Test
	public void droppingOntoAnExistingStackCountsTheIncrease()
	{
		TileItem stack = pile(ITEM, 3);
		deltas.put(ITEM, -4);

		detector.onQuantityChanged(new ItemQuantityChanged(stack, tile(HERE), 3, 7));
		detector.correlate(deltas);

		verify(ledger).queueGroundSuspend(ITEM, 4);
		assertEquals(4, detector.ourUnits(stack));
	}

	@Test
	public void lootPickedUpOffTheFloorIsClaimedAtZero()
	{
		deltas.put(ITEM, 3);

		detector.onDespawn(new ItemDespawned(tile(ELSEWHERE), pile(ITEM, 5)));
		detector.correlate(deltas);

		verify(ledger).claim(AcquisitionSource.GROUND, ITEM, 3, 0, TICK);
	}

	@Test
	public void aDespawnWithNoMatchingGainClaimsNothing()
	{
		detector.onDespawn(new ItemDespawned(tile(ELSEWHERE), pile(ITEM, 5)));
		detector.correlate(deltas);

		verify(ledger, never()).claim(any(), anyInt(), anyInt(), anyLong(), anyInt());
	}

	@Test
	public void eachTicksEventsAreCorrelatedOnce()
	{
		deltas.put(ITEM, -5);
		detector.onSpawn(new ItemSpawned(tile(HERE), pile(ITEM, 5)));
		detector.correlate(deltas);

		assertFalse(detector.correlate(deltas));
		verify(ledger).queueGroundSuspend(ITEM, 5);
	}

	@Test
	public void clearingTheTickAndForgettingDropsDropsEverything()
	{
		TileItem drop = pile(ITEM, 5);
		dropped(drop, 5);
		detector.onSpawn(new ItemSpawned(tile(HERE), pile(ITEM, 1)));

		detector.clearTick();
		detector.forgetDrops();

		assertEquals(0, detector.ourUnits(drop));
		assertFalse(detector.correlate(deltas));
	}

	@Test
	public void nothingBufferedMeansNoWork()
	{
		DetectorHost idle = mock(DetectorHost.class);

		assertFalse(new GroundDetector(idle, ledger).correlate(deltas));
		verifyNoInteractions(idle);
	}
}
