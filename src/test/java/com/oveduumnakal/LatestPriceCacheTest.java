/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link LatestPriceCache}: ad-hoc refreshes reuse a fresh {@code /latest} map and coalesce
 * behind an in-flight fetch instead of each downloading the full dump (#381).
 */
public class LatestPriceCacheTest
{
	private static final long WINDOW = LatestPriceCache.REUSE_WINDOW.toMillis();

	private final LatestPriceCache cache = new LatestPriceCache();

	private final Map<Integer, WikiRealtimePriceClient.ItemPrices> prices =
			Collections.singletonMap(560, new WikiRealtimePriceClient.ItemPrices(200, 190, 1, 1));

	@Test
	public void aFreshMapIsReusedAndAStaleOneIsNot()
	{
		cache.startFetch(false);
		cache.finishFetch(prices, 1_000);

		assertSame(prices, cache.fresh(1_000 + WINDOW - 1));
		assertNull("past the window the caller fetches again", cache.fresh(1_000 + WINDOW));
	}

	@Test
	public void aFailedFetchIsNotCached()
	{
		cache.startFetch(false);
		cache.finishFetch(Collections.emptyMap(), 1_000);

		assertNull(cache.fresh(1_001));
	}

	@Test
	public void adHocFetchesCoalesceBehindOneInFlight()
	{
		assertTrue(cache.startFetch(false));
		assertFalse("a second ad-hoc caller waits for the first result", cache.startFetch(false));
		assertTrue("the scheduled refresh still fetches", cache.startFetch(true));

		cache.finishFetch(prices, 1_000);
		assertTrue("once it lands, fetching is allowed again", cache.startFetch(false));
	}
}
