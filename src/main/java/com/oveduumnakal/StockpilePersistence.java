/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import net.runelite.client.config.ConfigManager;

/**
 * The client-free persistence layer (#111): the Gson-serializable snapshots and every read/write of
 * Stockpile state to the RS-profile config. Extracted verbatim from {@code StockpilePlugin} so the
 * plugin keeps only the orchestration (building snapshots from live {@link TrackedItem}s and applying
 * loaded ones on the client thread) while the JSON shape, config keys, and corrupt-value handling
 * live in one testable place. Loaders default to empty/{@code null} on a missing or unparseable value
 * exactly as the originals did, so history/state simply rebuilds rather than throwing. Reads and
 * writes go through {@link ProfileConfigStore} so those fallbacks are testable without a live
 * {@link ConfigManager}.
 */
@Slf4j
class StockpilePersistence
{
	private static final Type PERSIST_TYPE = new TypeToken<List<PersistedItem>>(){}.getType();

	private static final Type PRICE_CACHE_TYPE = new TypeToken<Map<Integer, CachedPrice>>(){}.getType();

	private static final Type CATEGORIES_TYPE = new TypeToken<CategoryData>(){}.getType();

	private static final Type SAVED_COMPARISONS_TYPE = new TypeToken<List<SavedComparison>>(){}.getType();

	private static final Type PORTFOLIO_HISTORY_TYPE = new TypeToken<Map<Integer, List<long[]>>>(){}.getType();

	private static final Type GE_LEDGER_TYPE = new TypeToken<Map<Integer, List<long[]>>>(){}.getType();

	private static final Type GE_LIMITS_TYPE = new TypeToken<Map<Integer, long[]>>(){}.getType();

	/**
	 * Last-known prices for one tracked item, stored as JSON in the RS profile config
	 * so the panel can show (staleness-dimmed) values immediately at startup instead
	 * of placeholders until the first wiki fetch lands. Package-private so
	 * {@code PersistedSchemaSnapshotTest} can guard its shape; any field change fails
	 * the schema snapshot until it is regenerated and explained in the PR.
	 */
	static class CachedPrice
	{
		long high;
		long low;
		long avg;
		long highTime;
		long lowTime;
	}

	/**
	 * Serializable snapshot of a tracked item, stored as JSON in the RS profile config.
	 * Package-private so {@code PersistenceCompatTest} can freeze its legacy shape and
	 * {@code PersistedSchemaSnapshotTest} can guard it; any field change fails the
	 * schema snapshot until it is regenerated and explained in the PR.
	 */
	static class PersistedItem
	{
		int itemId;
		int quantity;
		boolean costBasisInitialized;
		List<AcquisitionRecord> acquisitions;
		List<NotificationRule> notifications;
		boolean notificationsInitialized;
		boolean favorite;
		String category;
		boolean onOverlay;
		boolean compact;
		int deathSuspendedQuantity;
		Long deathSuspendedAt;
		int pouchSuspendedQuantity;
	}

	/**
	 * Serializable snapshot of the category definitions and special-group collapsed state.
	 * Package-private so {@code PersistenceCompatTest} can freeze its legacy shape and
	 * {@code PersistedSchemaSnapshotTest} can guard it; any field change fails the
	 * schema snapshot until it is regenerated and explained in the PR.
	 */
	static class CategoryData
	{
		List<CategoryState> categories;
		boolean favoritesCollapsed;
		boolean uncategorizedCollapsed;
	}

	/**
	 * Serializable snapshot of one named, saved item comparison (#303): a user-given name and the
	 * canonical item ids it compares, in display order. Package-private so
	 * {@code PersistedSchemaSnapshotTest} can guard it; any field change fails the schema snapshot
	 * until it is regenerated and explained in the PR.
	 */
	static class SavedComparison
	{
		String name;
		List<Integer> itemIds;
	}

	private final ProfileConfigStore config;
	private final Gson gson;

	StockpilePersistence(ConfigManager configManager, Gson gson)
	{
		this(backedBy(configManager), gson);
	}

	StockpilePersistence(ProfileConfigStore config, Gson gson)
	{
		this.config = config;
		this.gson = gson;
	}

	/** Adapts a live {@link ConfigManager} to the {@link ProfileConfigStore} seam. */
	private static ProfileConfigStore backedBy(ConfigManager configManager)
	{
		return new ProfileConfigStore()
		{
			@Override
			public String get(String group, String key)
			{
				return configManager.getRSProfileConfiguration(group, key);
			}

			@Override
			public void set(String group, String key, String value)
			{
				configManager.setRSProfileConfiguration(group, key, value);
			}
		};
	}

	/** Serializes the tracked-item snapshots to per-profile config. */
	void saveItems(List<PersistedItem> items)
	{
		config.set(
				StockpileConfig.GROUP, StockpileConfig.KEY_TRACKED_ITEMS, gson.toJson(items, PERSIST_TYPE));
	}

	/** @return the persisted tracked-item snapshots, or an empty list when missing, non-array, or corrupt. */
	List<PersistedItem> loadItems()
	{
		String saved = config.get(
				StockpileConfig.GROUP, StockpileConfig.KEY_TRACKED_ITEMS);
		if (saved == null || saved.trim().isEmpty())
			return new ArrayList<>();

		String trimmed = saved.trim();
		if (!trimmed.startsWith("["))
			return new ArrayList<>();

		try
		{
			List<PersistedItem> list = gson.fromJson(trimmed, PERSIST_TYPE);
			return list == null ? new ArrayList<>() : list;
		}
		catch (JsonSyntaxException e)
		{
			log.warn("Failed to parse persisted item JSON; ignoring", e);
			return new ArrayList<>();
		}
	}

	/** Serializes the category definitions and group collapsed state to per-profile config. */
	void saveCategories(CategoryData data)
	{
		config.set(
				StockpileConfig.GROUP, StockpileConfig.KEY_CATEGORIES, gson.toJson(data, CATEGORIES_TYPE));
	}

	/** @return the persisted category data, or {@code null} when missing or corrupt. */
	CategoryData loadCategories()
	{
		String saved = config.get(
				StockpileConfig.GROUP, StockpileConfig.KEY_CATEGORIES);
		if (saved == null || saved.trim().isEmpty())
			return null;

		try
		{
			return gson.fromJson(saved.trim(), CATEGORIES_TYPE);
		}
		catch (JsonSyntaxException e)
		{
			log.warn("Failed to parse persisted category JSON; ignoring", e);
			return null;
		}
	}

	/** Serializes the named saved comparisons to per-profile config (#303). */
	void saveComparisons(List<SavedComparison> comparisons)
	{
		config.set(StockpileConfig.GROUP, StockpileConfig.KEY_SAVED_COMPARISONS,
				gson.toJson(comparisons, SAVED_COMPARISONS_TYPE));
	}

	/** @return the persisted saved comparisons, or an empty list when missing, non-array, or corrupt (#303). */
	List<SavedComparison> loadComparisons()
	{
		String saved = config.get(
				StockpileConfig.GROUP, StockpileConfig.KEY_SAVED_COMPARISONS);
		if (saved == null || saved.trim().isEmpty())
			return new ArrayList<>();

		String trimmed = saved.trim();
		if (!trimmed.startsWith("["))
			return new ArrayList<>();

		try
		{
			List<SavedComparison> list = gson.fromJson(trimmed, SAVED_COMPARISONS_TYPE);
			return list == null ? new ArrayList<>() : list;
		}
		catch (JsonSyntaxException e)
		{
			log.warn("Failed to parse persisted comparison JSON; ignoring", e);
			return new ArrayList<>();
		}
	}

	/** Serializes the per-item price cache to per-profile config. */
	void savePriceCache(Map<Integer, CachedPrice> cache)
	{
		config.set(
				StockpileConfig.GROUP, StockpileConfig.KEY_PRICE_CACHE, gson.toJson(cache, PRICE_CACHE_TYPE));
	}

	/** @return the persisted price cache, or an empty map when missing or corrupt. */
	Map<Integer, CachedPrice> loadPriceCache()
	{
		String saved = config.get(
				StockpileConfig.GROUP, StockpileConfig.KEY_PRICE_CACHE);
		if (saved == null || saved.trim().isEmpty())
			return new HashMap<>();

		try
		{
			Map<Integer, CachedPrice> cache = gson.fromJson(saved, PRICE_CACHE_TYPE);
			return cache == null ? new HashMap<>() : cache;
		}
		catch (JsonSyntaxException e)
		{
			log.warn("Failed to parse persisted price cache; ignoring", e);
			return new HashMap<>();
		}
	}

	/** Serializes the per-item portfolio history to per-profile config. */
	void savePortfolioHistory(Map<Integer, List<long[]>> seriesByItem)
	{
		config.set(StockpileConfig.GROUP, StockpileConfig.KEY_PORTFOLIO_HISTORY,
				gson.toJson(seriesByItem, PORTFOLIO_HISTORY_TYPE));
	}

	/**
	 * @return the persisted per-item portfolio history, or {@code null} when missing, corrupt, or in
	 *         the pre-#152 aggregate array format (which can't be split per item and is discarded).
	 */
	Map<Integer, List<long[]>> loadPortfolioHistory()
	{
		String saved = config.get(StockpileConfig.GROUP,
				StockpileConfig.KEY_PORTFOLIO_HISTORY);
		if (saved == null || !saved.trim().startsWith("{"))
			return null;

		try
		{
			return gson.fromJson(saved.trim(), PORTFOLIO_HISTORY_TYPE);
		}
		catch (JsonSyntaxException e)
		{
			log.warn("Failed to parse persisted portfolio history; ignoring", e);
			return null;
		}
	}

	/** Persists the GE buy ledger and buy-limit windows to the RS profile config. */
	void saveGeState(Map<Integer, List<long[]>> ledger, Map<Integer, long[]> limits)
	{
		config.set(StockpileConfig.GROUP, StockpileConfig.KEY_GE_BUY_LEDGER,
				gson.toJson(ledger, GE_LEDGER_TYPE));
		config.set(StockpileConfig.GROUP, StockpileConfig.KEY_GE_BUY_LIMITS,
				gson.toJson(limits, GE_LIMITS_TYPE));
	}

	/** @return the persisted GE buy ledger (item id → FIFO buy chunks), or an empty map when missing or corrupt. */
	Map<Integer, List<long[]>> loadGeLedger()
	{
		String ledgerJson = config.get(
				StockpileConfig.GROUP, StockpileConfig.KEY_GE_BUY_LEDGER);
		if (ledgerJson == null || ledgerJson.trim().isEmpty())
			return new HashMap<>();

		try
		{
			Map<Integer, List<long[]>> ledger = gson.fromJson(ledgerJson, GE_LEDGER_TYPE);
			return ledger == null ? new HashMap<>() : ledger;
		}
		catch (JsonSyntaxException ex)
		{
			log.warn("Failed to parse GE buy ledger; ignoring", ex);
			return new HashMap<>();
		}
	}

	/** @return the persisted GE buy-limit windows, or an empty map when missing or corrupt. */
	Map<Integer, long[]> loadGeBuyLimits()
	{
		String limitsJson = config.get(
				StockpileConfig.GROUP, StockpileConfig.KEY_GE_BUY_LIMITS);
		if (limitsJson == null || limitsJson.trim().isEmpty())
			return new HashMap<>();

		try
		{
			Map<Integer, long[]> limits = gson.fromJson(limitsJson, GE_LIMITS_TYPE);
			return limits == null ? new HashMap<>() : limits;
		}
		catch (JsonSyntaxException ex)
		{
			log.warn("Failed to parse GE buy limits; ignoring", ex);
			return new HashMap<>();
		}
	}
}
