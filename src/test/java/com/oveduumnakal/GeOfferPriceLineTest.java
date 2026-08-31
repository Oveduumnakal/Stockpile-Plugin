/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * Tests for {@link GeIntegration#latestSeriesHighLow(List)}: the newest-priced-sample
 * scan behind the GE offer window's market-price line, shared by its 5m and 1h sources (#142).
 */
public class GeOfferPriceLineTest
{
	/** @return a 5m price point carrying only the average high/low (volumes/timestamp unused here). */
	private static WikiRealtimePriceClient.PricePoint point(long high, long low)
	{
		return new WikiRealtimePriceClient.PricePoint(0L, high, low, 0L, 0L);
	}

	@Test
	public void nullOrEmptySeriesYieldsZeroes()
	{
		assertArrayEquals(new long[]{0, 0}, GeIntegration.latestSeriesHighLow(null));
		assertArrayEquals(new long[]{0, 0}, GeIntegration.latestSeriesHighLow(new ArrayList<>()));
	}

	@Test
	public void takesTheNewestSample()
	{
		List<WikiRealtimePriceClient.PricePoint> series = Arrays.asList(
				point(100, 90),
				point(120, 110),
				point(130, 115));
		assertArrayEquals(new long[]{130, 115}, GeIntegration.latestSeriesHighLow(series));
	}

	@Test
	public void fallsBackPerSidePastEmptyBuckets()
	{
		List<WikiRealtimePriceClient.PricePoint> series = Arrays.asList(
				point(100, 90),
				point(0, 110),
				point(130, 0));
		assertArrayEquals(new long[]{130, 110}, GeIntegration.latestSeriesHighLow(series));
	}

	@Test
	public void unpricedSideStaysZero()
	{
		List<WikiRealtimePriceClient.PricePoint> series = Arrays.asList(
				point(0, 90),
				point(0, 110));
		assertArrayEquals(new long[]{0, 110}, GeIntegration.latestSeriesHighLow(series));
	}
}
