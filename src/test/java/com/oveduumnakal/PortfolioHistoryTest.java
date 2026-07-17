/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Verifies hourly de-duplication, 3-hour-bucket collapsing, and bounded retention of {@link PortfolioHistory}. */
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
	public void oldPointsCollapseToThreeHourBuckets()
	{
		PortfolioHistory history = new PortfolioHistory();
		long start = 100 * DAY;
		int hours = 10 * 24;

		for (int h = 0; h < hours; h++)
			history.record(start + h * HOUR, h, 0);

		long now = start + (hours - 1) * HOUR;
		long hourlyCutoff = now - PortfolioHistory.HOURLY_HOURS * HOUR;
		long bucket = PortfolioHistory.BUCKET_HOURS * HOUR;

		List<long[]> points = history.points();
		List<long[]> old = points.stream()
				.filter(p -> p[0] < hourlyCutoff)
				.collect(java.util.stream.Collectors.toList());
		long recentCount = points.stream().filter(p -> p[0] >= hourlyCutoff).count();

		int oldHours = (int) (hourlyCutoff - start) / (int) HOUR;
		long distinctBuckets = old.stream().map(p -> p[0] / bucket).distinct().count();
		assertEquals("no two old points share a 3-hour bucket", distinctBuckets, old.size());
		assertTrue("old history is thinned below hourly", old.size() < oldHours);
		assertTrue("recent week stays hourly", recentCount >= 160);
	}

	@Test
	public void pointsBeyondRetentionWindowAreDropped()
	{
		PortfolioHistory history = new PortfolioHistory();
		history.record(0, 100, 0);
		history.record((PortfolioHistory.RETENTION_DAYS + 5) * DAY, 200, 0);

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
