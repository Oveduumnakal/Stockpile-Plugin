/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Holds the last wiki {@code /latest} price map and coalesces fetches of it (#381).
 *
 * <p>{@code /latest} is the full all-items dump - hundreds of KB - and it used to be downloaded on
 * every preview, add, pop-out, compare and import as well as by the scheduled refresh, so tracking ten
 * items in a row meant ten full downloads. Ad-hoc callers now reuse a map younger than
 * {@link #REUSE_WINDOW}, and skip fetching while one is already in flight, since that fetch's result is
 * applied to every live item - including the one just added - when it lands. Thread-safe: fetches
 * finish on the executor, reads happen on the client thread.
 */
class LatestPriceCache
{
	/** How old a cached {@code /latest} map may be and still serve an ad-hoc refresh. */
	static final Duration REUSE_WINDOW = Duration.ofSeconds(30);

	private volatile Map<Integer, WikiRealtimePriceClient.ItemPrices> prices = Collections.emptyMap();

	private volatile long fetchedAtMillis;

	private final AtomicBoolean inFlight = new AtomicBoolean();

	/**
	 * @param nowMillis the current time
	 * @return the cached map when it is non-empty and within {@link #REUSE_WINDOW}, else {@code null}
	 */
	Map<Integer, WikiRealtimePriceClient.ItemPrices> fresh(long nowMillis)
	{
		Map<Integer, WikiRealtimePriceClient.ItemPrices> cached = prices;
		if (cached.isEmpty() || nowMillis - fetchedAtMillis >= REUSE_WINDOW.toMillis())
			return null;

		return cached;
	}

	/**
	 * Claims the right to fetch.
	 *
	 * @param force whether to fetch even if another fetch is in flight (the scheduled refresh)
	 * @return whether the caller should fetch
	 */
	boolean startFetch(boolean force)
	{
		return inFlight.compareAndSet(false, true) || force;
	}

	/**
	 * Records a finished fetch. An empty (failed) result is not cached, so the next ad-hoc refresh
	 * tries the network again.
	 *
	 * @param result the fetched map, empty on failure
	 * @param nowMillis the time the fetch finished
	 */
	void finishFetch(Map<Integer, WikiRealtimePriceClient.ItemPrices> result, long nowMillis)
	{
		if (!result.isEmpty())
		{
			prices = result;
			fetchedAtMillis = nowMillis;
		}

		inFlight.set(false);
	}
}
