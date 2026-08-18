/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Tests the shared item-count diff helpers ({@link ItemDeltas}) that replace the four hand-rolled union loops. */
public class ItemDeltasTest
{
	private Map<Integer, Integer> counts(int... idQtyPairs)
	{
		Map<Integer, Integer> map = new HashMap<>();
		for (int i = 0; i < idQtyPairs.length; i += 2)
			map.put(idQtyPairs[i], idQtyPairs[i + 1]);

		return map;
	}

	@Test
	public void forEachDeltaReportsGainsLossesAndSkipsUnchanged()
	{
		Map<Integer, Integer> before = counts(560, 10, 4151, 5, 995, 100);
		Map<Integer, Integer> after = counts(560, 13, 4151, 2, 995, 100, 383, 7);

		Map<Integer, Integer> seen = new HashMap<>();
		ItemDeltas.forEachDelta(before, after, seen::put);

		assertEquals("unchanged ids (995) are skipped", 3, seen.size());
		assertEquals(Integer.valueOf(3), seen.get(560));
		assertEquals(Integer.valueOf(-3), seen.get(4151));
		assertEquals("an id only in after counts as a full gain", Integer.valueOf(7), seen.get(383));
	}

	@Test
	public void forEachDeltaTreatsAMissingAfterAsAFullLoss()
	{
		Map<Integer, Integer> seen = new HashMap<>();
		ItemDeltas.forEachDelta(counts(560, 8), counts(), seen::put);

		assertEquals(Integer.valueOf(-8), seen.get(560));
	}

	@Test
	public void keyUnionCoversBothSides()
	{
		Set<Integer> union = new TreeSet<>(ItemDeltas.keyUnion(counts(1, 0, 2, 0), counts(2, 0, 3, 0)));

		assertEquals(3, union.size());
		assertTrue(union.containsAll(new TreeSet<>(counts(1, 0, 2, 0, 3, 0).keySet())));
	}
}
