/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link WikiRealtimePriceClient#computeStats}, the static aggregation half of the
 * wiki client: window cutoff filtering, the volume-weighted average, the zero-volume midpoint
 * fallback, and the all-zero result for absent data. No HTTP is involved.
 */
public class WikiRealtimePriceClientTest
{
	/** Seconds since the epoch right now, so fixtures can be placed relative to the window cutoff. */
	private static long now()
	{
		return System.currentTimeMillis() / 1000L;
	}

	/** A sample {@code ageSeconds} old with the given high/low prices and volumes. */
	private static WikiRealtimePriceClient.PricePoint point(long ageSeconds, long high, long low,
			long highVol, long lowVol)
	{
		return new WikiRealtimePriceClient.PricePoint(now() - ageSeconds, high, low, highVol, lowVol);
	}

	@Test
	public void nullAndEmptySeriesGiveAllZeroStats()
	{
		for (List<WikiRealtimePriceClient.PricePoint> points
				: Arrays.asList(null, Collections.<WikiRealtimePriceClient.PricePoint>emptyList()))
		{
			PriceStats stats = WikiRealtimePriceClient.computeStats(points, TimeWindow.H24);
			assertEquals(0, stats.getHigh());
			assertEquals(0, stats.getLow());
			assertEquals(0, stats.getAvg());
			assertEquals(0, stats.getVolume());
		}
	}

	@Test
	public void pointsOlderThanTheWindowAreIgnored()
	{
		List<WikiRealtimePriceClient.PricePoint> points = new ArrayList<>();
		points.add(point(60 * 60 * 48, 5000, 4000, 10, 10));
		points.add(point(60, 110, 90, 10, 10));

		PriceStats stats = WikiRealtimePriceClient.computeStats(points, TimeWindow.H24);
		assertEquals(110, stats.getHigh());
		assertEquals(90, stats.getLow());
		assertEquals(20, stats.getVolume());
	}

	@Test
	public void aZeroDurationWindowKeepsEveryPoint()
	{
		List<WikiRealtimePriceClient.PricePoint> points = new ArrayList<>();
		points.add(point(60L * 60 * 24 * 400, 100, 100, 5, 5));
		points.add(point(60, 100, 100, 5, 5));

		assertEquals(20, WikiRealtimePriceClient.computeStats(points, TimeWindow.LIVE).getVolume());
	}

	@Test
	public void theAverageIsWeightedByVolume()
	{
		List<WikiRealtimePriceClient.PricePoint> points = new ArrayList<>();
		points.add(point(60, 200, 100, 9, 1));

		PriceStats stats = WikiRealtimePriceClient.computeStats(points, TimeWindow.H24);
		assertEquals(Math.round((200.0 * 9 + 100.0 * 1) / 10), stats.getAvg());
		assertEquals(10, stats.getVolume());
	}

	@Test
	public void withNoVolumeTheAverageFallsBackToTheHighLowMidpoint()
	{
		List<WikiRealtimePriceClient.PricePoint> points = new ArrayList<>();
		points.add(point(60, 200, 100, 0, 0));

		PriceStats stats = WikiRealtimePriceClient.computeStats(points, TimeWindow.H24);
		assertEquals(0, stats.getVolume());
		assertEquals(0, stats.getHigh());
		assertEquals(0, stats.getLow());
		assertEquals(0, stats.getAvg());
	}

	/** A side counts toward high/low only when it carries both a price and a volume. */
	@Test
	public void aSideWithNoVolumeDoesNotCountTowardItsAverage()
	{
		List<WikiRealtimePriceClient.PricePoint> points = new ArrayList<>();
		points.add(point(60, 200, 100, 5, 0));

		PriceStats stats = WikiRealtimePriceClient.computeStats(points, TimeWindow.H24);
		assertEquals(200, stats.getHigh());
		assertEquals(0, stats.getLow());
		assertEquals(200, stats.getAvg());
		assertEquals(5, stats.getVolume());
	}

	/** High and low are plain means across the qualifying samples, independent of the weighted average. */
	@Test
	public void highAndLowAverageTheirQualifyingSamples()
	{
		List<WikiRealtimePriceClient.PricePoint> points = new ArrayList<>();
		points.add(point(120, 100, 80, 1, 1));
		points.add(point(60, 200, 120, 1, 1));

		PriceStats stats = WikiRealtimePriceClient.computeStats(points, TimeWindow.H24);
		assertEquals(150, stats.getHigh());
		assertEquals(100, stats.getLow());
		assertEquals(4, stats.getVolume());
	}

	/** An all-zero series produces zero stats rather than a divide-by-zero. */
	@Test
	public void anAllZeroSeriesGivesZeroStats()
	{
		List<WikiRealtimePriceClient.PricePoint> points = new ArrayList<>();
		points.add(point(60, 0, 0, 0, 0));

		PriceStats stats = WikiRealtimePriceClient.computeStats(points, TimeWindow.H24);
		assertEquals(0, stats.getHigh());
		assertEquals(0, stats.getLow());
		assertEquals(0, stats.getAvg());
		assertEquals(0, stats.getVolume());
	}
}
