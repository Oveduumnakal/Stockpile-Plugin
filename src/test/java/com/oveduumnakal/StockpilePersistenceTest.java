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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

	@Test
	public void trackedItemsRoundTripWithLotsRulesAndSuspensions()
	{
		StockpilePersistence.PersistedItem item = new StockpilePersistence.PersistedItem();
		item.itemId = 4151;
		item.quantity = 3;
		item.costBasisInitialized = true;
		AcquisitionRecord sold = new AcquisitionRecord(1, 1_500_000, 1_600_000L, AcquisitionSource.GE_TRADE);
		sold.setSellSource(AcquisitionSource.GE_TRADE);
		item.acquisitions = List.of(new AcquisitionRecord(3, 1_450_000, null, AcquisitionSource.GATHER), sold);
		NotificationRule rule = new NotificationRule();
		rule.setMetric(NotificationMetric.HIGH);
		rule.setTimeWindow(TimeWindow.H24);
		rule.setOperation(NotificationOperation.GTE);
		rule.setValue("1.7M");
		rule.setRepeat(true);
		item.notifications = List.of(rule);
		item.notificationsInitialized = true;
		item.favorite = true;
		item.category = "Weapons";
		item.onOverlay = true;
		item.compact = true;
		item.deathSuspendedQuantity = 2;
		item.deathSuspendedAt = 1_700_000_000L;
		item.pouchSuspendedQuantity = 4;

		persistence.saveItems(List.of(item));
		StockpilePersistence.PersistedItem loaded = persistence.loadItems().get(0);

		assertEquals(4151, loaded.itemId);
		assertEquals(3, loaded.quantity);
		assertTrue(loaded.costBasisInitialized);
		assertEquals(item.acquisitions, loaded.acquisitions);
		assertEquals(AcquisitionSource.GE_TRADE, loaded.acquisitions.get(1).sellSourceOrUnknown());
		assertEquals(List.of(rule), loaded.notifications);
		assertTrue(loaded.notificationsInitialized && loaded.favorite && loaded.onOverlay && loaded.compact);
		assertEquals("Weapons", loaded.category);
		assertEquals(2, loaded.deathSuspendedQuantity);
		assertEquals(Long.valueOf(1_700_000_000L), loaded.deathSuspendedAt);
		assertEquals(4, loaded.pouchSuspendedQuantity);
	}

	@Test
	public void categoriesRoundTripWithTheirCollapseState()
	{
		StockpilePersistence.CategoryData data = new StockpilePersistence.CategoryData();
		data.categories = List.of(new CategoryState("Ores", true), new CategoryState("Food", false));
		data.favoritesCollapsed = true;

		persistence.saveCategories(data);
		StockpilePersistence.CategoryData loaded = persistence.loadCategories();

		assertEquals(data.categories, loaded.categories);
		assertTrue(loaded.favoritesCollapsed);
		assertFalse(loaded.uncategorizedCollapsed);
	}

	@Test
	public void comparisonsRoundTripInOrder()
	{
		StockpilePersistence.SavedComparison runes = new StockpilePersistence.SavedComparison();
		runes.name = "Runes";
		runes.itemIds = List.of(560, 561, 565);

		persistence.saveComparisons(List.of(runes));
		List<StockpilePersistence.SavedComparison> loaded = persistence.loadComparisons();

		assertEquals(1, loaded.size());
		assertEquals("Runes", loaded.get(0).name);
		assertEquals(List.of(560, 561, 565), loaded.get(0).itemIds);
	}

	@Test
	public void thePriceCacheRoundTrips()
	{
		StockpilePersistence.CachedPrice price = new StockpilePersistence.CachedPrice();
		price.high = 210;
		price.low = 190;
		price.avg = 200;
		price.highTime = 11;
		price.lowTime = 12;

		persistence.savePriceCache(Map.of(560, price));
		StockpilePersistence.CachedPrice loaded = persistence.loadPriceCache().get(560);

		assertEquals(210, loaded.high);
		assertEquals(190, loaded.low);
		assertEquals(200, loaded.avg);
		assertEquals(11, loaded.highTime);
		assertEquals(12, loaded.lowTime);
	}

	@Test
	public void portfolioHistoryRoundTripsPerItem()
	{
		Map<Integer, List<long[]>> history = new HashMap<>();
		history.put(560, List.of(new long[]{1_700_000_000L, 5_000, 4_000}, new long[]{1_700_003_600L, 5_100, 4_000}));

		persistence.savePortfolioHistory(history);
		Map<Integer, List<long[]>> loaded = persistence.loadPortfolioHistory();

		assertEquals(1, loaded.size());
		assertEquals(2, loaded.get(560).size());
		assertArrayEquals(new long[]{1_700_003_600L, 5_100, 4_000}, loaded.get(560).get(1));
	}
}
