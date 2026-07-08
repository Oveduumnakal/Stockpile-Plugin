/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Verifies hourly de-duplication, daily collapsing, and bounded retention of {@link PortfolioHistory}. */
public class PortfolioHistoryTest
{
	private static final long HOUR = 3600L;

	private static final long DAY = 86_400L;

	@Test
	public void samplesInOneHourCollapseToOnePoint()
	{
		PortfolioHistory history = new PortfolioHistory();
		long base = 300 * DAY;
		history.record(base, 100, 50);
		history.record(base + 600, 110, 50);
		history.record(base + 1200, 120, 50);

		List<long[]> points = history.points();
		assertEquals(1, points.size());
		assertEquals(120, points.get(0)[1]);
	}

	@Test
	public void distinctHoursAppendPoints()
	{
		PortfolioHistory history = new PortfolioHistory();
		long base = 10 * DAY;
		history.record(base, 100, 0);
		history.record(base + HOUR, 200, 0);
		history.record(base + 2 * HOUR, 300, 0);
		assertEquals(3, history.points().size());
	}

	@Test
	public void oldPointsCollapseToOnePerDay()
	{
		PortfolioHistory history = new PortfolioHistory();
		long start = 100 * DAY;

		for (int h = 0; h < 72; h++)
			history.record(start + h * HOUR, h, 0);

		long now = start + 71 * HOUR;
		long hourlyCutoff = now - PortfolioHistory.HOURLY_HOURS * HOUR;

		List<long[]> points = history.points();
		long oldCount = points.stream().filter(p -> p[0] < hourlyCutoff).count();
		long recentCount = points.stream().filter(p -> p[0] >= hourlyCutoff).count();

		assertTrue("older-than-48h points collapse to at most one per day", oldCount <= 2);
		assertTrue("recent points stay hourly", recentCount >= 24);
	}

	@Test
	public void pointsBeyondDailyWindowAreDropped()
	{
		PortfolioHistory history = new PortfolioHistory();
		history.record(0, 100, 0);
		history.record((PortfolioHistory.DAILY_DAYS + 5) * DAY, 200, 0);

		List<long[]> points = history.points();
		assertEquals(1, points.size());
		assertEquals(200, points.get(0)[1]);
	}

	@Test
	public void loadRestoresSortedPoints()
	{
		PortfolioHistory history = new PortfolioHistory();
		history.load(java.util.Arrays.asList(
				new long[]{200, 20, 0},
				new long[]{100, 10, 0}));

		List<long[]> points = history.points();
		assertEquals(100, points.get(0)[0]);
		assertEquals(200, points.get(1)[0]);
	}
}
