/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Verifies CSV formatting, profit computation for closed lots, and quoting for {@link AcquisitionCsvExporter}. */
public class AcquisitionCsvExporterTest
{
	private TrackedItem item(int id, String name, AcquisitionRecord... records)
	{
		TrackedItem t = new TrackedItem(id, name);
		t.setAcquisitions(Arrays.asList(records));
		return t;
	}

	@Test
	public void headerOnlyWhenNoLots()
	{
		String csv = AcquisitionCsvExporter.toCsv(Collections.singletonList(item(560, "Nature rune")));
		assertEquals("Item,Item ID,Quantity,Bought At,Sold At,Realized Profit\n", csv);
	}

	@Test
	public void openLotHasBlankSoldAndProfit()
	{
		String csv = AcquisitionCsvExporter.toCsv(Collections.singletonList(
				item(560, "Nature rune", new AcquisitionRecord(100, 95, null))));
		String[] lines = csv.split("\n");
		assertEquals("Nature rune,560,100,95,,", lines[1]);
	}

	@Test
	public void closedLotComputesRealizedProfit()
	{
		String csv = AcquisitionCsvExporter.toCsv(Collections.singletonList(
				item(560, "Nature rune", new AcquisitionRecord(100, 90, 110L))));
		String[] lines = csv.split("\n");
		assertEquals("Nature rune,560,100,90,110,2000", lines[1]);
	}

	@Test
	public void namesWithCommasAreQuoted()
	{
		String csv = AcquisitionCsvExporter.toCsv(Collections.singletonList(
				item(2, "Cannonball, stack", new AcquisitionRecord(1, 5, null))));
		assertTrue(csv.contains("\"Cannonball, stack\",2,1,5,,"));
	}
}
