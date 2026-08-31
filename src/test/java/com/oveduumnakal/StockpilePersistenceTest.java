/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the corrupt-value fallbacks {@link StockpilePersistence}'s javadoc promises: every loader
 * must return its documented empty or {@code null} default for a missing, blank, wrongly-shaped or
 * syntactically invalid value rather than throwing, since these all run inside the login block.
 */
public class StockpilePersistenceTest
{
	/** The values a hand-edited or truncated config write can realistically leave behind. */
	private static final String[] CORRUPT = {null, "", "   ", "\n\t ", "not json at all", "{", "[",
		"[{\"itemId\":}]", "12345", "\"a string\"", "true", "null"};

	/** An in-memory {@link ProfileConfigStore} standing in for the RS-profile config. */
	private static final class MapConfig implements ProfileConfigStore
	{
		private final Map<String, String> values = new HashMap<>();

		@Override
		public String get(String group, String key)
		{
			return values.get(group + "." + key);
		}

		@Override
		public void set(String group, String key, String value)
		{
			values.put(group + "." + key, value);
		}
	}

	private final MapConfig config = new MapConfig();

	private final StockpilePersistence persistence = new StockpilePersistence(config, new Gson());

	/** Stores {@code value} under the given Stockpile config key. */
	private void put(String key, String value)
	{
		config.set(StockpileConfig.GROUP, key, value);
	}

	@Test
	public void loadItemsReturnsAnEmptyListForEveryCorruptValue()
	{
		for (String value : CORRUPT)
		{
			put(StockpileConfig.KEY_TRACKED_ITEMS, value);
			List<StockpilePersistence.PersistedItem> items = persistence.loadItems();
			assertNotNull(String.valueOf(value), items);
			assertTrue(String.valueOf(value), items.isEmpty());
		}
	}

	@Test
	public void loadComparisonsReturnsAnEmptyListForEveryCorruptValue()
	{
		for (String value : CORRUPT)
		{
			put(StockpileConfig.KEY_SAVED_COMPARISONS, value);
			List<StockpilePersistence.SavedComparison> saved = persistence.loadComparisons();
			assertNotNull(String.valueOf(value), saved);
			assertTrue(String.valueOf(value), saved.isEmpty());
		}
	}

	@Test
	public void loadCategoriesReturnsNullForEveryCorruptValue()
	{
		for (String value : CORRUPT)
		{
			put(StockpileConfig.KEY_CATEGORIES, value);
			assertNull(String.valueOf(value), persistence.loadCategories());
		}
	}

	@Test
	public void loadPriceCacheReturnsAnEmptyMapForEveryCorruptValue()
	{
		for (String value : CORRUPT)
		{
			put(StockpileConfig.KEY_PRICE_CACHE, value);
			Map<Integer, StockpilePersistence.CachedPrice> cache = persistence.loadPriceCache();
			assertNotNull(String.valueOf(value), cache);
			assertTrue(String.valueOf(value), cache.isEmpty());
		}
	}

	@Test
	public void loadPortfolioHistoryReturnsNullForEveryCorruptValue()
	{
		for (String value : CORRUPT)
		{
			put(StockpileConfig.KEY_PORTFOLIO_HISTORY, value);
			assertNull(String.valueOf(value), persistence.loadPortfolioHistory());
		}
	}

	@Test
	public void loadGeLedgerReturnsAnEmptyMapForEveryCorruptValue()
	{
		for (String value : CORRUPT)
		{
			put(StockpileConfig.KEY_GE_BUY_LEDGER, value);
			Map<Integer, List<long[]>> ledger = persistence.loadGeLedger();
			assertNotNull(String.valueOf(value), ledger);
			assertTrue(String.valueOf(value), ledger.isEmpty());
		}
	}

	@Test
	public void loadGeBuyLimitsReturnsAnEmptyMapForEveryCorruptValue()
	{
		for (String value : CORRUPT)
		{
			put(StockpileConfig.KEY_GE_BUY_LIMITS, value);
			Map<Integer, long[]> limits = persistence.loadGeBuyLimits();
			assertNotNull(String.valueOf(value), limits);
			assertTrue(String.valueOf(value), limits.isEmpty());
		}
	}

	/** The pre-#152 aggregate array format cannot be split per item, so it is discarded rather than loaded. */
	@Test
	public void loadPortfolioHistoryDiscardsTheLegacyAggregateArray()
	{
		put(StockpileConfig.KEY_PORTFOLIO_HISTORY, "[[1700000000,500,400]]");
		assertNull(persistence.loadPortfolioHistory());
	}

	/** Every loader round-trips what its matching saver wrote. */
	@Test
	public void savedStateRoundTrips()
	{
		Map<Integer, List<long[]>> ledger = new HashMap<>();
		ledger.put(560, Collections.singletonList(new long[]{100, 95}));
		Map<Integer, long[]> limits = new HashMap<>();
		limits.put(560, new long[]{1700000000L, 12000});
		persistence.saveGeState(ledger, limits);

		Map<Integer, List<long[]>> loadedLedger = persistence.loadGeLedger();
		assertEquals(1, loadedLedger.size());
		assertEquals(95, loadedLedger.get(560).get(0)[1]);
		assertEquals(12000, persistence.loadGeBuyLimits().get(560)[1]);
	}
}
