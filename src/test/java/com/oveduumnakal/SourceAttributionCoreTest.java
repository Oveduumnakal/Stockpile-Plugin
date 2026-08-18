/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link SourceAttributionCore}: claim matching (full and partial),
 * per-item isolation, oldest-first consumption, tick expiry, the
 * {@link AcquisitionSource#UNKNOWN} fallback, and the durable GE buy claims
 * (#180) — their own matching path, TTL immunity, and ledger serialization.
 */
public class SourceAttributionCoreTest
{
	private final SourceAttributionCore core = new SourceAttributionCore();

	@Test
	public void unmatchedDeltaIsUnknown()
	{
		SourceAttributionCore.Attribution a = core.attribute(560, 100, 10);
		assertEquals(AcquisitionSource.UNKNOWN, a.source());
		assertEquals(42, a.unitPriceOr(42));
	}

	@Test
	public void claimMatchesAndCarriesItsPrice()
	{
		core.claim(AcquisitionSource.GE_TRADE, 560, 100, 95, 10);

		SourceAttributionCore.Attribution a = core.attribute(560, 100, 10);
		assertEquals(AcquisitionSource.GE_TRADE, a.source());
		assertEquals(95, a.unitPriceOr(0));

		assertEquals(AcquisitionSource.UNKNOWN, core.attribute(560, 100, 10).source());
	}

	@Test
	public void largerClaimSurvivesPartialConsumption()
	{
		core.claim(AcquisitionSource.SHOP, 560, 100, 5, 10);

		assertEquals(AcquisitionSource.SHOP, core.attribute(560, 60, 10).source());
		assertEquals(AcquisitionSource.SHOP, core.attribute(560, 40, 10).source());
		assertEquals(AcquisitionSource.UNKNOWN, core.attribute(560, 1, 10).source());
	}

	@Test
	public void claimsAreIsolatedPerItem()
	{
		core.claim(AcquisitionSource.GROUND, 560, 10, 0, 10);

		assertEquals(AcquisitionSource.UNKNOWN, core.attribute(4151, 10, 10).source());
		assertEquals(AcquisitionSource.GROUND, core.attribute(560, 10, 10).source());
	}

	@Test
	public void oldestClaimIsConsumedFirst()
	{
		core.claim(AcquisitionSource.GE_TRADE, 560, 10, 95, 10);
		core.claim(AcquisitionSource.SHOP, 560, 10, 80, 10);

		assertEquals(95, core.attribute(560, 10, 10).unitPriceOr(0));
		assertEquals(80, core.attribute(560, 10, 10).unitPriceOr(0));
	}

	@Test
	public void expiredClaimsNeverMatch()
	{
		core.claim(AcquisitionSource.ALCHEMY, 560, 10, 60, 10);

		int afterTtl = 10 + SourceAttributionCore.CLAIM_TTL_TICKS + 1;
		assertEquals(AcquisitionSource.UNKNOWN, core.attribute(560, 10, afterTtl).source());
	}

	@Test
	public void expireDiscardsOnlyStaleClaims()
	{
		core.claim(AcquisitionSource.GE_TRADE, 560, 10, 95, 10);
		core.claim(AcquisitionSource.SHOP, 4151, 10, 80, 20);

		core.expire(10 + SourceAttributionCore.CLAIM_TTL_TICKS + 1);

		assertEquals(AcquisitionSource.UNKNOWN, core.attribute(560, 10, 15).source());
		assertEquals(AcquisitionSource.SHOP, core.attribute(4151, 10, 21).source());
	}

	@Test
	public void clearDropsEverything()
	{
		core.claim(AcquisitionSource.GE_TRADE, 560, 10, 95, 10);
		core.clear();

		assertEquals(AcquisitionSource.UNKNOWN, core.attribute(560, 10, 10).source());
	}

	@Test
	public void durableClaimMatchesOnlyItsOwnPath()
	{
		core.claimDurable(AcquisitionSource.GE_TRADE, 560, 100, 95);

		assertEquals("the ordinary path never consumes a durable claim",
				AcquisitionSource.UNKNOWN, core.attribute(560, 100, 10).source());

		List<long[]> chunks = core.attributeDurable(560, 100);
		assertEquals(1, chunks.size());
		assertEquals(100, chunks.get(0)[0]);
		assertEquals(95, chunks.get(0)[1]);
	}

	@Test
	public void durableClaimSurvivesTickExpiry()
	{
		core.claimDurable(AcquisitionSource.GE_TRADE, 560, 10, 95);

		core.expire(10_000);

		assertEquals("a durable claim has no TTL and outlives any expire()",
				10, core.attributeDurable(560, 10).get(0)[0]);
	}

	@Test
	public void attributeDurableDrainsChunksOldestFirst()
	{
		core.claimDurable(AcquisitionSource.GE_TRADE, 560, 4, 95);
		core.claimDurable(AcquisitionSource.GE_TRADE, 560, 6, 80);

		List<long[]> chunks = core.attributeDurable(560, 8);
		assertEquals(2, chunks.size());
		assertEquals(4, chunks.get(0)[0]);
		assertEquals(95, chunks.get(0)[1]);
		assertEquals(4, chunks.get(1)[0]);
		assertEquals(80, chunks.get(1)[1]);

		assertEquals("the older claim is spent, 2 of the newer remain", 2, core.attributeDurable(560, 8).get(0)[0]);
	}

	@Test
	public void exportImportRoundTripsTheLedger()
	{
		core.claimDurable(AcquisitionSource.GE_TRADE, 560, 5, 95);
		core.claimDurable(AcquisitionSource.GE_TRADE, 4151, 3, 20);

		Map<Integer, List<long[]>> exported = core.exportDurable();

		SourceAttributionCore restored = new SourceAttributionCore();
		restored.importDurable(exported);

		assertEquals(5, restored.attributeDurable(560, 5).get(0)[0]);
		assertEquals(20, restored.attributeDurable(4151, 3).get(0)[1]);
	}

	@Test
	public void clearDurableKeepsOrdinaryClaims()
	{
		core.claim(AcquisitionSource.SHOP, 560, 10, 80, 10);
		core.claimDurable(AcquisitionSource.GE_TRADE, 560, 10, 95);

		core.clearDurable();

		assertTrue("the durable claim is gone", core.attributeDurable(560, 10).isEmpty());
		assertEquals("the ordinary claim is untouched", AcquisitionSource.SHOP, core.attribute(560, 10, 10).source());
	}

	@Test
	public void unmatchedDurableDeltaIsEmpty()
	{
		Map<Integer, List<long[]>> empty = new HashMap<>();
		core.importDurable(empty);

		assertTrue(core.attributeDurable(560, 10).isEmpty());
	}
}
