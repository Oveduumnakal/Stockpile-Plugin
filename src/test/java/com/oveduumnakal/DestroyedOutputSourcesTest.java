/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import org.junit.Test;

import net.runelite.api.gameval.ItemID;

import static org.junit.Assert.assertEquals;

/** Verifies the data-driven destroyed-output → loss-source mapping (#182). */
public class DestroyedOutputSourcesTest
{
	@Test
	public void crushedGemstoneTagsCrushed()
	{
		assertEquals(AcquisitionSource.CRUSHED, DestroyedOutputSources.sourceFor(ItemID.CRUSHED_GEMSTONE));
	}

	@Test
	public void anyOtherDestroyedOutputFallsBackToBurned()
	{
		assertEquals("an unmapped output id defaults to BURNED",
				AcquisitionSource.BURNED, DestroyedOutputSources.sourceFor(ItemID.COINS));
		assertEquals("an absent/invalid id defaults to BURNED",
				AcquisitionSource.BURNED, DestroyedOutputSources.sourceFor(-1));
	}
}
