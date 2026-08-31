/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link ChartUtil}'s pure plotting maths: the 1/2/2.5/5 axis step search and its
 * tick-count contract, the degenerate flat-series guard, and value-to-pixel clamping.
 */
public class ChartUtilTest
{
	/** The chosen axis always spans the data and lands on a round step. */
	@Test
	public void niceAxisBracketsTheData()
	{
		double[] axis = ChartUtil.niceAxis(37, 913, 3, 8);
		assertTrue(axis[0] <= 37);
		assertTrue(axis[1] >= 913);
	}

	/** The tick count stays inside the requested band whenever a nice step can hit it. */
	@Test
	public void niceAxisRespectsTheTickBand()
	{
		long[][] ranges = {{0, 10}, {0, 1}, {5, 7}, {100, 250}, {0, 1_000_000}, {-500, 500}};
		for (long[] r : ranges)
		{
			double[] axis = ChartUtil.niceAxis(r[0], r[1], 3, 8);
			int ticks = (int) axis[2];
			assertTrue("ticks=" + ticks + " for " + r[0] + ".." + r[1], ticks >= 3 && ticks <= 8);
			assertTrue(axis[0] <= r[0]);
			assertTrue(axis[1] >= r[1]);
		}
	}

	/** A flat series is widened by one so the axis has a non-zero range instead of dividing by zero. */
	@Test
	public void niceAxisWidensAFlatSeries()
	{
		double[] axis = ChartUtil.niceAxis(500, 500, 3, 8);
		assertTrue(axis[1] > axis[0]);
		assertTrue(axis[2] >= 1);
	}

	/** An inverted range is treated the same way as a flat one rather than producing a negative axis. */
	@Test
	public void niceAxisHandlesAnInvertedRange()
	{
		double[] axis = ChartUtil.niceAxis(900, 100, 3, 8);
		assertTrue(axis[1] > axis[0]);
		assertTrue(axis[2] >= 1);
	}

	/** A range far larger than the 10^12 step search still yields a usable axis via the bestStep fallback. */
	@Test
	public void niceAxisFallsBackForARangeBeyondTheStepSearch()
	{
		double[] axis = ChartUtil.niceAxis(0, 900_000_000_000_000L, 4, 6);
		assertTrue(axis[1] >= 900_000_000_000_000L);
		assertTrue(axis[2] >= 1);
	}

	/** A tick count of at least one is always reported, even for the narrowest data. */
	@Test
	public void niceAxisNeverReportsZeroTicks()
	{
		assertTrue(ChartUtil.niceAxis(0, 0, 1, 2)[2] >= 1);
		assertTrue(ChartUtil.niceAxis(-1, 0, 1, 2)[2] >= 1);
	}

	/** A value inside the axis maps proportionally between the plot's top and bottom pixels. */
	@Test
	public void clampedYMapsProportionally()
	{
		assertEquals(100, ChartUtil.clampedY(0, 0, 100, 0, 100, 100));
		assertEquals(0, ChartUtil.clampedY(100, 0, 100, 0, 100, 100));
		assertEquals(50, ChartUtil.clampedY(50, 0, 100, 0, 100, 100));
	}

	/** Values outside the axis clamp to the plot edges rather than drawing off-panel. */
	@Test
	public void clampedYClampsToThePlotEdges()
	{
		assertEquals(0, ChartUtil.clampedY(500, 0, 100, 0, 100, 100));
		assertEquals(100, ChartUtil.clampedY(-500, 0, 100, 0, 100, 100));
	}

	/** The nearest plotted index wins; an empty series reports -1. */
	@Test
	public void closestIndexFindsTheNearestPoint()
	{
		long[] times = {0, 50, 100};
		assertEquals(0, ChartUtil.closestIndex(0, 3, i -> times[(int) i], 0, 100, 0, 100.0));
		assertEquals(1, ChartUtil.closestIndex(48, 3, i -> times[(int) i], 0, 100, 0, 100.0));
		assertEquals(2, ChartUtil.closestIndex(200, 3, i -> times[(int) i], 0, 100, 0, 100.0));
		assertEquals(-1, ChartUtil.closestIndex(10, 0, i -> 0L, 0, 100, 0, 100.0));
	}
}
