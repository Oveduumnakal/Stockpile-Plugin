/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oveduumnakal;

import java.awt.image.BufferedImage;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.GameState;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GrandExchangeOfferChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Notification;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

@Slf4j
/**
 * Plugin entry point: wires up the side panel and overlays and drives all
 * tracking logic.
 *
 * <p>Responsibilities: persisting and restoring the tracked-item set; counting
 * each item across the watched inventory/bank containers (and the rune pouch);
 * polling the wiki for live prices, metadata, and history; maintaining each
 * item's cost-basis lots (FIFO acquire/close) for profit; and evaluating
 * user-defined notification rules. It subscribes to the relevant game events and
 * marshals UI work onto the Swing thread and network work onto a background
 * executor.
 */
@PluginDescriptor(
		name = "Stockpile",
		description = "Track item quantities across your inventory and bank with live GE prices",
		tags = {"items", "bank", "inventory", "price", "ge", "tracker"}
)
public class StockpilePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private StockpileConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private WikiRealtimePriceClient wikiPriceClient;

	@Inject
	private Notifier notifier;

	@Inject
	private Gson gson;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private StockpileHighlightOverlay highlightOverlay;

	@Inject
	private StockpileGroundOverlay groundOverlay;

	/** Maximum number of items shown in the on-screen overlay (fixed for now). */
	static final int OVERLAY_MAX = 5;

	/** One independently-draggable overlay box per slot; they start grouped in the same snap corner. */
	private final List<StockpileScreenOverlay> screenOverlays = new ArrayList<>();

	private static final int[] RUNE_POUCH_TYPE_VARBITS = {
			VarbitID.RUNE_POUCH_TYPE_1, VarbitID.RUNE_POUCH_TYPE_2, VarbitID.RUNE_POUCH_TYPE_3,
			VarbitID.RUNE_POUCH_TYPE_4, VarbitID.RUNE_POUCH_TYPE_5, VarbitID.RUNE_POUCH_TYPE_6
	};
	private static final int[] RUNE_POUCH_QUANTITY_VARBITS = {
			VarbitID.RUNE_POUCH_QUANTITY_1, VarbitID.RUNE_POUCH_QUANTITY_2, VarbitID.RUNE_POUCH_QUANTITY_3,
			VarbitID.RUNE_POUCH_QUANTITY_4, VarbitID.RUNE_POUCH_QUANTITY_5, VarbitID.RUNE_POUCH_QUANTITY_6
	};
	private static final ImmutableSet<Integer> RUNE_POUCH_VARBITS;
	static
	{
		ImmutableSet.Builder<Integer> b = ImmutableSet.builder();
		for (int v : RUNE_POUCH_TYPE_VARBITS)
			b.add(v);

		for (int v : RUNE_POUCH_QUANTITY_VARBITS)
			b.add(v);

		RUNE_POUCH_VARBITS = b.build();
	}

	private static final ImmutableSet<Integer> TRACKED_CONTAINERS = ImmutableSet.of(
			InventoryID.INV,
			InventoryID.WORN,
			InventoryID.BANK,
			InventoryID.LOOTING_BAG,
			InventoryID.SEED_BOX,
			InventoryID.SEED_VAULT,
			InventoryID.TACKLE_BOX,
			InventoryID.FORESTRY_KIT,
			InventoryID.HUNTSMANS_KIT,
			InventoryID.BARBARIAN_KNAPSACK
	);

	private final Map<Integer, TrackedItem> trackedItems = new LinkedHashMap<>();

	/** Ordered user-defined categories (names + collapsed state); the source of truth for category order. */
	private final List<CategoryState> categories = new ArrayList<>();
	private boolean favoritesCollapsed;
	private boolean uncategorizedCollapsed;

	/**
	 * Transient, non-persisted item backing the read-only detail preview (view-only
	 * button); not in {@link #trackedItems}.
	 */
	private TrackedItem previewItem;

	/** The item shown on the currently-open GE offer screen, or -1 when no offer screen is up (GE integration). */
	private int currentGeItem = -1;
	/** The native-style button injected onto the GE offer screen in Button mode, or null. */
	private Widget geButton;

	private final Map<Integer, Map<Integer, Integer>> containerCounts = new HashMap<>();

	private final Map<Integer, Integer> runePouchCounts = new HashMap<>();

	private final Set<Integer> seenContainersSinceLogin = new HashSet<>();

	private boolean runePouchSeenSinceLogin = false;
	private int geLoginTick = -1;
	private boolean pendingQuantitySync = false;
	private final Map<Integer, Integer> pendingItemDeltas = new HashMap<>();

	private final Map<TileItem, Tile> groundItems = new HashMap<>();

	private StockpilePanel panel;
	private NavigationButton navButton;
	private ScheduledFuture<?> priceRefreshTask;
	private Instant lastPriceRefresh = null;

	/** The newest un-rendered panel snapshot; non-null means a rebuild drainer is already queued. */
	private final AtomicReference<Runnable> pendingRebuild = new AtomicReference<>();

	private static final int NATURE_RUNE_ID = 561;
	private static final int FIRE_RUNE_ID = 554;

	private volatile Map<Integer, WikiRealtimePriceClient.ItemMapping> itemMappings = Collections.emptyMap();

	private volatile boolean mappingsLoaded;

	/** Matches detector claims to observed quantity deltas; see {@link SourceAttributionCore}. */
	private final SourceAttributionCore sourceAttribution = new SourceAttributionCore();

	/** Derives discrete increments from the raw GE offer stream; see {@link GeOfferTracker}. */
	private final GeOfferTracker geOfferTracker = new GeOfferTracker();

	/** Per-item FIFO of GE buy fills awaiting collection: each entry is {@code {quantity, unitPrice}}. Persisted. */
	private final Map<Integer, Deque<long[]>> pendingGeBuys = new HashMap<>();

	/** Per-item rolling GE buy-limit window: {@code {firstBuyEpochSec, boughtInWindow}}. Persisted. */
	private final Map<Integer, long[]> geBuyLimits = new HashMap<>();

	/** Units of a just-placed GE sell awaiting their placement inventory decrease (session-only). */
	private final Map<Integer, Integer> pendingSellSuspend = new HashMap<>();

	/** Units of a cancelled GE sell awaiting their return inventory increase (session-only). */
	private final Map<Integer, Integer> pendingSellUnsuspend = new HashMap<>();

	/**
	 * Per-item FIFO of GE sell fills that outran their suspension: each entry is
	 * {@code {quantity, unitPrice}}. A same-tick fill realizes before the placement
	 * inventory decrease has moved {@link #pendingSellSuspend} into {@code suspendedQuantity},
	 * so the shortfall is parked here and drained once the units suspend (session-only).
	 */
	private final Map<Integer, Deque<long[]>> pendingSellRealize = new HashMap<>();

	private static final Type GE_LEDGER_TYPE = new TypeToken<Map<Integer, List<long[]>>>(){}.getType();

	private static final Type GE_LIMITS_TYPE = new TypeToken<Map<Integer, long[]>>(){}.getType();

	/** The rolling GE buy-limit window length. */
	private static final Duration BUY_LIMIT_WINDOW = Duration.ofHours(4);

	/**
	 * Ticks after login during which {@code GrandExchangeOfferChanged} events are treated as the
	 * login offer sync (pre-existing offers) rather than user actions. The client delivers the GE
	 * offers with the login packet within a tick or two, while the player cannot open the GE and
	 * abort an offer anywhere near this fast — so the window reliably separates the two.
	 */
	private static final int GE_LOGIN_SYNC_TICKS = 5;

	/** How often at most the GE ledger/window are rewritten to config during activity. */
	private static final Duration GE_STATE_SAVE_INTERVAL = Duration.ofMinutes(1);

	private Instant lastGeStateSave;

	/**
	 * Builds the side panel (wiring its callbacks back to this plugin), registers
	 * the nav button and overlays, restores persisted items, and kicks off the
	 * metadata fetch and recurring price refresh.
	 */
	@Override
	protected void startUp() throws Exception
	{
		panel = new StockpilePanel(
				itemManager,
				config,
				this::addTrackedItem,
				this::removeTrackedItem,
				this::onAcquisitionsEdited,
				this::requestDetailData,
				this::clearAcquisitions,
				this::onNotificationsEdited,
				this::clearAllTrackedItems,
				this::examineFor,
				this::reorderTrackedItem,
				this::setGlobalOrder,
				this::toggleCompactView,
				this::setSortMode,
				this::toggleSortReversed,
				this::setFavorite,
				this::setOnOverlay,
				this::setGroupCollapsed,
				new CategoryActions()
				{
					@Override
					public void setItemCategory(int itemId, String category)
					{
						StockpilePlugin.this.setItemCategory(itemId, category);
					}

					@Override
					public void create(String name)
					{
						createCategory(name);
					}

					@Override
					public void rename(String oldName, String newName)
					{
						renameCategory(oldName, newName);
					}

					@Override
					public void delete(String name)
					{
						deleteCategory(name);
					}

					@Override
					public void reorder(String name, int targetIndex)
					{
						reorderCategory(name, targetIndex);
					}

					@Override
					public String autoCategorize(boolean includeCategorized)
					{
						return StockpilePlugin.this.autoCategorize(includeCategorized);
					}
				}
		);

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");

		navButton = NavigationButton.builder()
				.tooltip("Stockpile")
				.icon(icon)
				.priority(6)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);
		overlayManager.add(highlightOverlay);
		overlayManager.add(groundOverlay);
		for (int slot = 0; slot < OVERLAY_MAX; slot++)
		{
			StockpileScreenOverlay overlay = new StockpileScreenOverlay(this, config, itemManager, slot);
			screenOverlays.add(overlay);
			overlayManager.add(overlay);
		}

		clientThread.invokeLater(() ->
		{
			loadCategories();
			loadPersistedItems();
			loadGeState();

			refreshPanel();
			clientThread.invokeLater(this::hydratePriceCache);
		});
		executor.execute(this::fetchItemMappings);
		scheduleRefresh();
	}

	/**
	 * @return the examine text for the given item id from the wiki mapping, or
	 *         {@code null} when the item isn't GE-tradeable or the mapping hasn't
	 *         loaded yet
	 */
	private String examineFor(int itemId)
	{
		WikiRealtimePriceClient.ItemMapping mapping = itemMappings.get(itemId);
		return mapping == null ? null : mapping.getExamine();
	}

	/** Fetches GE item metadata in the background, keeping the previous map on failure. */
	private void fetchItemMappings()
	{
		Map<Integer, WikiRealtimePriceClient.ItemMapping> mappings = wikiPriceClient.fetchMapping();
		if (mappings.isEmpty())
			return;

		itemMappings = mappings;
		mappingsLoaded = true;

		clientThread.invokeLater(this::resolveTradeabilityForAll);
	}

	/**
	 * Re-evaluates GE-tradeability for every tracked item and the preview item now
	 * that the wiki mapping is available, then refreshes the panel. Items absent from
	 * the mapping are not on the Grand Exchange, so they are marked non-tradeable and
	 * any stale price-load failure is cleared.
	 */
	private void resolveTradeabilityForAll()
	{
		trackedItems.values().forEach(this::resolveTradeable);

		if (previewItem != null)
			resolveTradeable(previewItem);

		refreshPanel();
	}

	/**
	 * Narrows an item's tradeable flag using the wiki mapping: an item that the game
	 * composition reports as tradeable but which is absent from the Grand Exchange
	 * mapping (e.g. coins, burnt food) is reclassified as non-tradeable so it shows
	 * "Item not tradeable" rather than a price-load failure. No-op until the mapping
	 * has loaded, so a slow fetch never mislabels a genuinely tradeable item.
	 */
	private void resolveTradeable(TrackedItem item)
	{
		item.setStackable(itemManager.getItemComposition(item.getItemId()).isStackable());

		if (!mappingsLoaded)
			return;

		if (item.isTradeable() && !itemMappings.containsKey(item.getItemId()))
		{
			item.setTradeable(false);
			item.setPriceLoadFailed(false);
		}
	}

	/** Tears down the nav button, overlays, panel, and refresh task and clears all in-memory state. */
	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
		overlayManager.remove(highlightOverlay);
		overlayManager.remove(groundOverlay);
		screenOverlays.forEach(overlayManager::remove);
		screenOverlays.clear();
		panel.shutdown();
		groundItems.clear();
		clientThread.invokeLater(this::hideGeButton);
		currentGeItem = -1;
		persistPriceCache();
		if (priceRefreshTask != null)
		{
			priceRefreshTask.cancel(false);
			priceRefreshTask = null;
		}

		persistGeState();
		trackedItems.clear();
		containerCounts.clear();
		runePouchCounts.clear();
		sourceAttribution.clear();
		geOfferTracker.clear();
		pendingSellSuspend.clear();
		pendingSellUnsuspend.clear();
		pendingSellRealize.clear();
		lastPriceRefresh = null;
	}

	@Provides
	StockpileConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StockpileConfig.class);
	}

	/** (Re)schedules the recurring GE price refresh at the configured rate (min 30s), replacing any prior task. */
	private void scheduleRefresh()
	{
		if (priceRefreshTask != null)
			priceRefreshTask.cancel(false);

		int rate = Math.max(30, config.priceRefreshSeconds());
		priceRefreshTask = executor.scheduleAtFixedRate(
				this::refreshGePrices, 0, rate, TimeUnit.SECONDS
		);
	}

	private static final Type PERSIST_TYPE = new TypeToken<List<PersistedItem>>(){}.getType();

	private static final Type PRICE_CACHE_TYPE = new TypeToken<Map<Integer, CachedPrice>>(){}.getType();

	/** How often at most the price cache is rewritten to config during regular refreshes. */
	private static final Duration PRICE_CACHE_SAVE_INTERVAL = Duration.ofMinutes(5);

	/** When the price cache was last written, to throttle per-refresh saves. */
	private Instant lastPriceCacheSave;

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
	}

	private static final Type CATEGORIES_TYPE = new TypeToken<CategoryData>(){}.getType();

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

	/** Restores tracked items from the per-profile JSON written by {@link #persistTrackedItems()}. */
	private void loadPersistedItems()
	{
		String saved = configManager.getRSProfileConfiguration(
				StockpileConfig.GROUP, StockpileConfig.KEY_TRACKED_ITEMS);
		if (saved == null || saved.trim().isEmpty())
			return;

		String trimmed = saved.trim();
		if (trimmed.startsWith("["))
		{
			try
			{
				List<PersistedItem> list = gson.fromJson(trimmed, PERSIST_TYPE);
				if (list != null)
				{
					for (PersistedItem p : list)
					{
						addTrackedItem(p.itemId, p.quantity, p.acquisitions, p.notifications,
							p.notificationsInitialized, p.costBasisInitialized, false, TrackItemMode.TRACK);
						applyPersistedGrouping(p.itemId, p.favorite, p.category, p.onOverlay);
					}
				}

				return;
			}
			catch (JsonSyntaxException e)
			{
				log.warn("Failed to parse persisted item JSON; ignoring", e);
				return;
			}
		}
	}

	/**
	 * Applies a persisted item's favorite/category/overlay grouping after it has been added.
	 * Enqueued on the client thread so it runs after the matching {@link #addTrackedItem}
	 * body (which is itself client-thread-deferred), guaranteeing the item exists.
	 */
	private void applyPersistedGrouping(int itemId, boolean favorite, String category, boolean onOverlay)
	{
		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null)
				return;

			tracked.setFavorite(favorite);
			tracked.setCategory(category);
			tracked.setOnOverlay(onOverlay);
		});
	}

	/** Restores the category definitions and group collapsed state from per-profile JSON. */
	private void loadCategories()
	{
		categories.clear();
		favoritesCollapsed = false;
		uncategorizedCollapsed = false;

		String saved = configManager.getRSProfileConfiguration(
				StockpileConfig.GROUP, StockpileConfig.KEY_CATEGORIES);
		if (saved == null || saved.trim().isEmpty())
			return;

		try
		{
			CategoryData data = gson.fromJson(saved.trim(), CATEGORIES_TYPE);
			if (data == null)
				return;

			if (data.categories != null)
				data.categories.stream()
						.filter(c -> c != null && c.getName() != null && !c.getName().trim().isEmpty())
						.forEach(categories::add);

			favoritesCollapsed = data.favoritesCollapsed;
			uncategorizedCollapsed = data.uncategorizedCollapsed;
		}
		catch (JsonSyntaxException e)
		{
			log.warn("Failed to parse persisted category JSON; ignoring", e);
		}
	}

	/** Serializes the category definitions and group collapsed state to per-profile config. */
	private void persistCategories()
	{
		CategoryData data = new CategoryData();
		data.categories = new ArrayList<>(categories);
		data.favoritesCollapsed = favoritesCollapsed;
		data.uncategorizedCollapsed = uncategorizedCollapsed;

		configManager.setRSProfileConfiguration(
				StockpileConfig.GROUP, StockpileConfig.KEY_CATEGORIES, gson.toJson(data, CATEGORIES_TYPE));
	}

	/** Serializes the current tracked items (quantity, cost basis, notifications, grouping) to per-profile config. */
	void persistTrackedItems()
	{
		List<PersistedItem> list = new ArrayList<>();
		for (TrackedItem item : trackedItems.values())
		{
			PersistedItem p = new PersistedItem();
			p.itemId = item.getItemId();
			p.quantity = item.getQuantity();
			p.costBasisInitialized = item.isCostBasisInitialized();
			p.acquisitions = item.getAcquisitions();
			p.notifications = item.getNotifications();
			p.notificationsInitialized = item.isNotificationsInitialized();
			p.favorite = item.isFavorite();
			p.category = item.getCategory();
			p.onOverlay = item.isOnOverlay();
			list.add(p);
		}

		configManager.setRSProfileConfiguration(
				StockpileConfig.GROUP, StockpileConfig.KEY_TRACKED_ITEMS, gson.toJson(list, PERSIST_TYPE));
	}

	/** Tracks an item by id with defaults (full tracking mode, no preset cost basis). */
	private void addTrackedItem(int itemId)
	{
		addTrackedItem(itemId, TrackItemMode.TRACK);
	}

	/** Tracks an item by id in the given mode, routing {@link TrackItemMode#VIEW} to a read-only preview instead. */
	private void addTrackedItem(int itemId, TrackItemMode mode)
	{
		if (mode == TrackItemMode.VIEW)
		{
			previewItem(itemId);
			return;
		}

		addTrackedItem(itemId, 0, null, null, false, false, true, mode);
	}

	/**
	 * Opens a read-only detail preview for an untracked item without adding it to
	 * the tracked list or persisting anything. Builds a transient {@link TrackedItem},
	 * shows it in the detail view, then fetches its prices and history in the
	 * background. Runs on the client thread.
	 */
	private void previewItem(int itemId)
	{
		clientThread.invokeLater(() ->
		{
			TrackedItem preview = previewItem;
			if (preview == null || preview.getItemId() != itemId)
			{
				var composition = itemManager.getItemComposition(itemId);
				preview = new TrackedItem(itemId, composition.getName());
				preview.setTradeable(composition.isTradeable());
				preview.setMode(TrackItemMode.VIEW);
				applyItemMetadata(preview);
				previewItem = preview;
			}

			final TrackedItem shown = preview;
			SwingUtilities.invokeLater(() -> panel.showPreview(shown));
			requestDetailData(itemId);
			refreshGePrices();
		});
	}

	/**
	 * @return the tracked item for {@code itemId}, or the transient preview item when it
	 *         matches; otherwise {@code null}
	 */
	private TrackedItem lookupItem(int itemId)
	{
		TrackedItem tracked = trackedItems.get(itemId);
		if (tracked == null && previewItem != null && previewItem.getItemId() == itemId)
			return previewItem;

		return tracked;
	}

	/** Tracks an item with a preset quantity and acquisition history (e.g. a restore), using default notifications. */
	private void addTrackedItem(int itemId, int initialQuantity, List<AcquisitionRecord> records,
			boolean costBasisInitialized)
	{
		addTrackedItem(itemId, initialQuantity, records, null, false, costBasisInitialized, true, TrackItemMode.TRACK);
	}

	/**
	 * Canonical add: creates a {@link TrackedItem} (resolving its name/tradeable
	 * flag from the item composition), seeds its quantity, acquisitions, and
	 * notifications, registers it, and persists/refreshes. No-op if already
	 * tracked. Runs on the client thread.
	 *
	 * @param initialQuantity        starting count
	 * @param records                preset acquisition lots, or {@code null}
	 * @param notifications          preset notification rules, or {@code null}
	 * @param notificationsInitialized whether default rules have already been seeded
	 * @param costBasisInitialized   whether a cost basis has already been established
	 * @param syncOnAdd              recount from containers immediately when in TRACK mode
	 * @param mode                   tracking vs. view-only
	 */
	private void addTrackedItem(int itemId, int initialQuantity, List<AcquisitionRecord> records,
			List<NotificationRule> notifications, boolean notificationsInitialized,
			boolean costBasisInitialized, boolean syncOnAdd, TrackItemMode mode)
	{
		clientThread.invokeLater(() ->
		{
			if (trackedItems.containsKey(itemId))
				return;

			var composition = itemManager.getItemComposition(itemId);
			TrackedItem tracked = new TrackedItem(itemId, composition.getName());
			tracked.setTradeable(composition.isTradeable());
			resolveTradeable(tracked);
			tracked.setQuantity(initialQuantity);
			tracked.setMode(mode == null ? TrackItemMode.TRACK : mode);
			if (records != null)
				tracked.setAcquisitions(new ArrayList<>(records));

			if (notifications != null)
				tracked.setNotifications(new ArrayList<>(notifications));

			tracked.setNotificationsInitialized(notificationsInitialized);
			tracked.setCostBasisInitialized(costBasisInitialized);
			trackedItems.put(itemId, tracked);

			if (syncOnAdd && tracked.getMode() == TrackItemMode.TRACK)
				syncQuantitiesForItem(tracked);

			persistTrackedItems();
			refreshPanel();
			refreshGePrices();
		});
	}

	/** Stops tracking an item, then persists and refreshes. Runs on the client thread. */
	private void removeTrackedItem(int itemId)
	{
		clientThread.invokeLater(() ->
		{
			trackedItems.remove(itemId);
			persistTrackedItems();
			refreshPanel();
		});
	}

	/**
	 * Moves a tracked item to a new position in the list, persisting the new order so it
	 * survives restarts. {@code targetIndex} is clamped to the list bounds; a no-op if the
	 * item is unknown or already at that position. Runs on the client thread.
	 */
	private void reorderTrackedItem(int itemId, int targetIndex)
	{
		clientThread.invokeLater(() ->
		{
			List<TrackedItem> ordered = new ArrayList<>(trackedItems.values());

			int from = -1;
			for (int i = 0; i < ordered.size(); i++)
			{
				if (ordered.get(i).getItemId() == itemId)
				{
					from = i;
					break;
				}
			}

			if (from < 0)
				return;

			int to = Math.max(0, Math.min(targetIndex, ordered.size() - 1));
			if (to == from)
				return;

			ordered.add(to, ordered.remove(from));

			trackedItems.clear();
			ordered.forEach(item -> trackedItems.put(item.getItemId(), item));

			persistTrackedItems();
			refreshPanel();
		});
	}

	/** Persists the chosen sort mode; the resulting {@link ConfigChanged} rebuilds the panel. */
	private void setSortMode(SortMode mode)
	{
		configManager.setConfiguration(StockpileConfig.GROUP, StockpileConfig.KEY_SORT_MODE, mode);
	}

	/** Flips the persisted sort direction; the resulting {@link ConfigChanged} rebuilds the panel. */
	private void toggleSortReversed()
	{
		configManager.setConfiguration(StockpileConfig.GROUP, StockpileConfig.KEY_SORT_REVERSED,
				!config.sortReversed());
	}

	/** Flips the persisted compact-view flag; the resulting {@link ConfigChanged} rebuilds the panel. */
	private void toggleCompactView()
	{
		configManager.setConfiguration(StockpileConfig.GROUP, StockpileConfig.KEY_COMPACT_VIEW,
				!config.compactView());
	}

	/** Sets an item's favorite flag (pinning it to the top "Favorites" group), then persists and refreshes. */
	private void setFavorite(int itemId, boolean favorite)
	{
		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null)
				return;

			tracked.setFavorite(favorite);
			persistTrackedItems();
			refreshPanel();
		});
	}

	/**
	 * Adds/removes an item from the on-screen overlay set, enforcing the {@link #OVERLAY_MAX}
	 * cap (an add beyond the cap is ignored), then persists and refreshes.
	 */
	private void setOnOverlay(int itemId, boolean on)
	{
		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null || tracked.isOnOverlay() == on)
				return;

			if (on && overlayItemCount() >= OVERLAY_MAX)
				return;

			tracked.setOnOverlay(on);
			persistTrackedItems();
			refreshPanel();
		});
	}

	/** @return how many tracked items are currently flagged for the on-screen overlay. */
	private int overlayItemCount()
	{
		return (int) trackedItems.values()
				.stream()
				.filter(TrackedItem::isOnOverlay)
				.count();
	}

	/** @return the tracked items shown on the overlay (in tracked order), capped at {@link #OVERLAY_MAX}. */
	List<TrackedItem> getOverlayItems()
	{
		return trackedItems.values().stream()
				.filter(TrackedItem::isOnOverlay)
				.limit(OVERLAY_MAX)
				.collect(Collectors.toList());
	}

	/** Sets a list group's collapsed state (a category name, or a special-group key), then persists and refreshes. */
	private void setGroupCollapsed(String groupKey, boolean collapsed)
	{
		clientThread.invokeLater(() ->
		{
			if (CategoryState.FAVORITES_KEY.equals(groupKey))
				favoritesCollapsed = collapsed;
			else if (CategoryState.UNCATEGORIZED_KEY.equals(groupKey))
				uncategorizedCollapsed = collapsed;
			else
				categories.stream()
						.filter(c -> c.getName().equals(groupKey))
						.findFirst()
						.ifPresent(c -> c.setCollapsed(collapsed));

			persistCategories();
			refreshPanel();
		});
	}

	/** Assigns an item to a category (null/blank clears it to Uncategorized), then persists and refreshes. */
	private void setItemCategory(int itemId, String category)
	{
		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null)
				return;

			tracked.setCategory(category == null || category.trim().isEmpty() ? null : category.trim());
			persistTrackedItems();
			refreshPanel();
		});
	}

	/**
	 * Auto-assigns tracked items to wiki-derived categories (see {@link ItemCategoryClassifier}),
	 * creating any missing categories. Non-destructive unless {@code includeCategorized} is set:
	 * by default only uncategorized items are touched, so manual assignments are preserved. The
	 * mutation runs on the client thread; the returned message reports the outcome.
	 *
	 * @return a user-facing summary of how many items were categorized
	 */
	String autoCategorize(boolean includeCategorized)
	{
		long willChange = trackedItems.values().stream()
				.filter(t -> inAutoCategorizeScope(t, includeCategorized))
				.filter(t -> !ItemCategoryClassifier.classify(t.getName()).equals(t.getCategory()))
				.count();

		clientThread.invokeLater(() -> applyAutoCategorize(includeCategorized));

		if (willChange == 0)
			return "Nothing to categorize — everything already matches.";

		return "Auto-categorized " + willChange + " item(s).";
	}

	/** @return whether the item is in scope: always when re-categorizing, otherwise only when uncategorized. */
	private boolean inAutoCategorizeScope(TrackedItem item, boolean includeCategorized)
	{
		return includeCategorized || item.getCategory() == null || item.getCategory().trim().isEmpty();
	}

	/** Applies auto-categorization on the client thread: classify each in-scope item, create categories, assign. */
	private void applyAutoCategorize(boolean includeCategorized)
	{
		boolean changed = false;
		List<CategoryState> created = new ArrayList<>();
		for (TrackedItem tracked : trackedItems.values())
		{
			if (!inAutoCategorizeScope(tracked, includeCategorized))
				continue;

			String target = ItemCategoryClassifier.classify(tracked.getName());
			if (target.equals(tracked.getCategory()))
				continue;

			if (categories.stream().noneMatch(c -> c.getName().equalsIgnoreCase(target)))
			{
				CategoryState category = new CategoryState(target, false);
				categories.add(category);
				created.add(category);
			}

			tracked.setCategory(target);
			changed = true;
		}

		if (changed)
		{
			orderGeneratedCategories(created);
			persistCategories();
			persistTrackedItems();
			refreshPanel();
		}
	}

	/**
	 * Orders an auto-categorize run's generated categories alphabetically after any
	 * pre-existing (manually ordered) ones, then keeps "Other" at the very end.
	 */
	private void orderGeneratedCategories(List<CategoryState> created)
	{
		categories.removeAll(created);
		created.stream()
				.sorted(Comparator.comparing(CategoryState::getName, String.CASE_INSENSITIVE_ORDER))
				.forEach(categories::add);

		List<CategoryState> other = categories.stream()
				.filter(c -> ItemCategoryClassifier.OTHER.equalsIgnoreCase(c.getName()))
				.collect(Collectors.toList());
		categories.removeAll(other);
		categories.addAll(other);
	}

	/** Creates a new category (ignoring blanks and case-insensitive duplicates), then persists and refreshes. */
	private void createCategory(String name)
	{
		clientThread.invokeLater(() ->
		{
			String trimmed = name == null ? "" : name.trim();
			if (trimmed.isEmpty() || categories.stream().anyMatch(c -> c.getName().equalsIgnoreCase(trimmed)))
				return;

			categories.add(new CategoryState(trimmed, false));
			persistCategories();
			refreshPanel();
		});
	}

	/** Renames a category and re-points its items, ignoring blanks and clashes, then persists and refreshes. */
	private void renameCategory(String oldName, String newName)
	{
		clientThread.invokeLater(() ->
		{
			String trimmed = newName == null ? "" : newName.trim();
			if (trimmed.isEmpty())
				return;

			CategoryState target = null;
			for (CategoryState c : categories)
			{
				if (c.getName().equals(oldName))
					target = c;
				else if (c.getName().equalsIgnoreCase(trimmed))
					return;
			}

			if (target == null)
				return;

			target.setName(trimmed);
			trackedItems.values().stream()
					.filter(t -> oldName.equals(t.getCategory()))
					.forEach(t -> t.setCategory(trimmed));

			persistCategories();
			persistTrackedItems();
			refreshPanel();
		});
	}

	/** Deletes a category, moving its items to Uncategorized, then persists and refreshes. */
	private void deleteCategory(String name)
	{
		clientThread.invokeLater(() ->
		{
			if (!categories.removeIf(c -> c.getName().equals(name)))
				return;

			trackedItems.values().stream()
					.filter(t -> name.equals(t.getCategory()))
					.forEach(t -> t.setCategory(null));

			persistCategories();
			persistTrackedItems();
			refreshPanel();
		});
	}

	/** Moves a category to a new position in the ordered list, then persists and refreshes. */
	private void reorderCategory(String name, int targetIndex)
	{
		clientThread.invokeLater(() ->
		{
			int from = -1;
			for (int i = 0; i < categories.size(); i++)
			{
				if (categories.get(i).getName().equals(name))
				{
					from = i;
					break;
				}
			}

			if (from < 0)
				return;

			int to = Math.max(0, Math.min(targetIndex, categories.size() - 1));
			if (to == from)
				return;

			categories.add(to, categories.remove(from));
			persistCategories();
			refreshPanel();
		});
	}

	/**
	 * Reorders the tracked items to match the given id order (drag reorder), then persists and
	 * refreshes. Applies the new order only when it is a faithful permutation of the current
	 * set, so a stale or partial drag result cannot drop items.
	 */
	private void setGlobalOrder(List<Integer> orderedIds)
	{
		clientThread.invokeLater(() ->
		{
			Map<Integer, TrackedItem> reordered = new LinkedHashMap<>();
			for (Integer id : orderedIds)
			{
				TrackedItem tracked = trackedItems.get(id);
				if (tracked != null)
					reordered.put(id, tracked);
			}

			if (reordered.size() != trackedItems.size())
				return;

			trackedItems.clear();
			trackedItems.putAll(reordered);
			persistTrackedItems();
			refreshPanel();
		});
	}

	/** Removes every tracked item, then persists and refreshes. Runs on the client thread. */
	private void clearAllTrackedItems()
	{
		clientThread.invokeLater(() ->
		{
			trackedItems.clear();
			persistTrackedItems();
			refreshPanel();
		});
	}

	/** Fetches just the 5m series for an item in the background and recomputes its window stats. */
	private void requestSeries(int itemId, boolean refreshAfter)
	{
		executor.execute(() ->
		{
			List<WikiRealtimePriceClient.PricePoint> points = wikiPriceClient.fetchTimeseries(itemId, "5m");
			clientThread.invokeLater(() ->
			{
				TrackedItem tracked = trackedItems.get(itemId);
				if (tracked == null)
					return;

				tracked.setSeries5m(points);
				recomputeWindowStats(tracked);
				if (refreshAfter)
					refreshPanel();
			});
		});
	}

	/**
	 * Fetches all four history series (5m/1h/6h/24h) plus metadata for the
	 * detail view in the background, then updates stats, alch rune prices, and the
	 * detail panel on the appropriate threads.
	 */
	private void requestDetailData(int itemId)
	{
		executor.execute(() ->
		{
			List<WikiRealtimePriceClient.PricePoint> s5 = wikiPriceClient.fetchTimeseries(itemId, "5m");
			List<WikiRealtimePriceClient.PricePoint> s1h = wikiPriceClient.fetchTimeseries(itemId, "1h");
			List<WikiRealtimePriceClient.PricePoint> s6 = wikiPriceClient.fetchTimeseries(itemId, "6h");
			List<WikiRealtimePriceClient.PricePoint> s24 = wikiPriceClient.fetchTimeseries(itemId, "24h");
			clientThread.invokeLater(() ->
			{
				TrackedItem tracked = lookupItem(itemId);
				if (tracked == null)
					return;

				tracked.setSeries5m(s5);
				tracked.setSeries1h(s1h);
				tracked.setSeries6h(s6);
				tracked.setSeries24h(s24);
				applyItemMetadata(tracked);
				recomputeWindowStats(tracked);

				final long nature = runePrice(NATURE_RUNE_ID);
				final long fire = runePrice(FIRE_RUNE_ID);
				SwingUtilities.invokeLater(() ->
				{
					panel.setAlchRunePrices(nature, fire);
					panel.refreshDetailData(itemId);
				});
				refreshPanel();
			});
		});
	}

	/** Rebuilds an item's per-window {@link PriceStats} from its current prices (LIVE) and history series. */
	private void recomputeWindowStats(TrackedItem tracked)
	{
		Map<TimeWindow, PriceStats> stats = new EnumMap<>(TimeWindow.class);
		for (TimeWindow w : TimeWindow.values())
		{
			if (w == TimeWindow.NONE)
				continue;

			if (w == TimeWindow.LIVE)
			{
				stats.put(w, new PriceStats(tracked.getHighPrice(), tracked.getLowPrice(), tracked.getAvgPrice(), 0));
			}
			else
			{
				List<WikiRealtimePriceClient.PricePoint> series = tracked.getSeriesFor(w);
				if (series.isEmpty())
					series = tracked.getSeries5m();

				stats.put(w, WikiRealtimePriceClient.computeStats(series, w));
			}
		}

		tracked.setWindowStats(stats);
	}

	/** Copies cached GE metadata (buy limit, value, alch values) onto an item, if available. */
	private void applyItemMetadata(TrackedItem tracked)
	{
		resolveTradeable(tracked);

		WikiRealtimePriceClient.ItemMapping mapping = itemMappings.get(tracked.getItemId());
		if (mapping == null)
			return;

		tracked.setBuyLimit(mapping.getLimit());
		tracked.setGeValue(mapping.getValue());
		tracked.setHighAlch(mapping.getHighAlch());
		tracked.setLowAlch(mapping.getLowAlch());
		tracked.setMetadataLoaded(true);
	}

	/** @return a price for a rune (for alch calc): the tracked average if present, else the GE price. */
	private long runePrice(int itemId)
	{
		TrackedItem tracked = trackedItems.get(itemId);
		if (tracked != null && tracked.getAvgPrice() > 0)
			return tracked.getAvgPrice();

		return Math.max(0, itemManager.getItemPrice(itemId));
	}

	/** Clears an item's acquisition lots (resetting its cost basis) and persists/refreshes. */
	private void clearAcquisitions(int itemId)
	{
		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null)
				return;

			tracked.getAcquisitions().clear();
			persistTrackedItems();
			refreshPanel();
		});
	}

	/** Fetches the latest prices for all items in the background, then applies them on the client thread. */
	private void refreshGePrices()
	{
		executor.execute(() ->
		{
			Map<Integer, WikiRealtimePriceClient.ItemPrices> all = wikiPriceClient.fetchAll();

			clientThread.invokeLater(() -> applyGePrices(all));
		});
	}

	/**
	 * Applies freshly fetched prices to every tracked item: records per-side
	 * deltas against the previous values, updates the LIVE window stats, seeds a
	 * cost basis on first successful price if one wasn't set, then re-evaluates
	 * notifications and refreshes the panel (including the open detail view).
	 * A failed (empty) fetch only triggers a plain refresh.
	 */
	private void applyGePrices(Map<Integer, WikiRealtimePriceClient.ItemPrices> all)
	{
		boolean fetchFailed = all.isEmpty();

		for (TrackedItem item : trackedItems.values())
		{
			WikiRealtimePriceClient.ItemPrices prices = all.get(item.getItemId());
			if (prices != null)
			{
				applyLivePrices(item, prices);

				if (!item.isCostBasisInitialized())
				{
					if (item.getQuantity() > 0 && item.getAcquisitions().isEmpty())
						addOpenAcquisition(item, item.getQuantity(), autoAddPrice(item),
								AcquisitionSource.UNKNOWN);

					item.setCostBasisInitialized(true);
					persistTrackedItems();
				}
			}
			else if (!item.hasPrices() && item.isTradeable() && mappingsLoaded)
				item.setPriceLoadFailed(true);
		}

		if (previewItem != null)
		{
			WikiRealtimePriceClient.ItemPrices prices = all.get(previewItem.getItemId());
			if (prices != null)
				applyLivePrices(previewItem, prices);
			else if (!previewItem.hasPrices() && previewItem.isTradeable() && mappingsLoaded)
				previewItem.setPriceLoadFailed(true);
		}

		if (fetchFailed)
		{
			refreshPanel();
			return;
		}

		lastPriceRefresh = Instant.now();
		if (lastPriceCacheSave == null
				|| Duration.between(lastPriceCacheSave, Instant.now()).compareTo(PRICE_CACHE_SAVE_INTERVAL) >= 0)
			persistPriceCache();

		evaluateNotifications();
		refreshPanel(true);

		final int detailId = panel.getDetailItemId();
		for (TrackedItem item : trackedItems.values())
		{
			if (item.isTradeable() && item.hasPrices())
			{
				if (item.getItemId() == detailId)
					requestDetailData(item.getItemId());
				else
					requestSeries(item.getItemId(), false);
			}
		}

		if (previewItem != null && previewItem.getItemId() == detailId
				&& previewItem.isTradeable() && previewItem.hasPrices())
			requestDetailData(previewItem.getItemId());
	}

	/**
	 * Writes every priced tracked item's current prices to the RS profile config.
	 * Called throttled from refreshes and unconditionally at shutdown.
	 */
	private void persistPriceCache()
	{
		Map<Integer, CachedPrice> cache = new HashMap<>();
		for (TrackedItem item : trackedItems.values())
		{
			if (!item.hasPrices())
				continue;

			CachedPrice p = new CachedPrice();
			p.high = item.getHighPrice();
			p.low = item.getLowPrice();
			p.avg = item.getAvgPrice();
			p.highTime = item.getLatestHighTime();
			p.lowTime = item.getLatestLowTime();
			cache.put(item.getItemId(), p);
		}

		if (cache.isEmpty())
			return;

		lastPriceCacheSave = Instant.now();
		configManager.setRSProfileConfiguration(
				StockpileConfig.GROUP, StockpileConfig.KEY_PRICE_CACHE, gson.toJson(cache, PRICE_CACHE_TYPE));
	}

	/**
	 * Hydrates tracked items from the persisted price cache so the panel shows
	 * last-known values (dimmed by the existing staleness treatment once their trade
	 * times age past the threshold) instead of placeholders. Live fetches simply
	 * overwrite these; items that already have prices are never touched. Runs on the
	 * client thread after the persisted items have been restored.
	 */
	private void hydratePriceCache()
	{
		String saved = configManager.getRSProfileConfiguration(
				StockpileConfig.GROUP, StockpileConfig.KEY_PRICE_CACHE);
		if (saved == null || saved.trim().isEmpty())
			return;

		Map<Integer, CachedPrice> cache;
		try
		{
			cache = gson.fromJson(saved, PRICE_CACHE_TYPE);
		}
		catch (JsonSyntaxException e)
		{
			log.warn("Failed to parse persisted price cache; ignoring", e);
			return;
		}

		if (cache == null || cache.isEmpty())
			return;

		boolean hydrated = false;
		for (Map.Entry<Integer, CachedPrice> entry : cache.entrySet())
		{
			TrackedItem item = trackedItems.get(entry.getKey());
			if (item == null || item.hasPrices())
				continue;

			CachedPrice p = entry.getValue();
			item.setHighPrice(p.high);
			item.setLowPrice(p.low);
			item.setAvgPrice(p.avg);
			item.setLatestHighTime(p.highTime);
			item.setLatestLowTime(p.lowTime);
			hydrated = true;
		}

		if (hydrated)
			refreshPanel();
	}

	/**
	 * Applies a freshly fetched price set to an item: records per-side deltas, updates
	 * current prices, and refreshes its LIVE window stats.
	 */
	private void applyLivePrices(TrackedItem item, WikiRealtimePriceClient.ItemPrices prices)
	{
		if (item.hasPrices())
		{
			item.setHighDelta(Long.compare(prices.getHigh(), item.getHighPrice()));
			item.setLowDelta(Long.compare(prices.getLow(), item.getLowPrice()));
			item.setAvgDelta(Long.compare(prices.avg(), item.getAvgPrice()));
			item.setPrevHighPrice(item.getHighPrice());
			item.setPrevLowPrice(item.getLowPrice());
			item.setPrevAvgPrice(item.getAvgPrice());
			item.setHasDeltas(true);
		}

		item.setHighPrice(prices.getHigh());
		item.setLowPrice(prices.getLow());
		item.setAvgPrice(prices.avg());
		item.setLatestHighTime(prices.getHighTime());
		item.setLatestLowTime(prices.getLowTime());
		item.setPriceLoadFailed(false);
		item.getWindowStats().put(TimeWindow.LIVE,
				new PriceStats(prices.getHigh(), prices.getLow(), prices.avg(), 0));
	}

	/**
	 * Reacts to this plugin's config changes: resolves detail-section slot
	 * conflicts, reschedules the refresh when the interval changes, and otherwise
	 * just repaints the panel. Ignores other plugins' groups.
	 */
	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!StockpileConfig.GROUP.equals(event.getGroup()))
			return;

		if (SECTION_SLOT_KEYS.contains(event.getKey()))
		{
			swapConflictingSection(event);
			refreshPanel();
			return;
		}

		switch (event.getKey())
		{
			case StockpileConfig.KEY_TRACKED_ITEMS:
				return;
			case StockpileConfig.KEY_PRICE_REFRESH_SECONDS:
				scheduleRefresh();
				return;
			case StockpileConfig.KEY_SCREEN_OVERLAY_ON_TOP:
				rebucketScreenOverlays();
				return;
			default:
				refreshPanel();
		}
	}

	/** Removes and re-adds the screen overlays so the manager re-buckets them into their (config-driven) layer. */
	private void rebucketScreenOverlays()
	{
		screenOverlays.forEach(overlayManager::remove);
		screenOverlays.forEach(overlayManager::add);
	}

	private static final Set<String> SECTION_SLOT_KEYS = Set.of(
			StockpileConfig.KEY_SHOW_ITEM_VALUES,
			StockpileConfig.KEY_SHOW_COLLECTION_VALUES,
			StockpileConfig.KEY_SHOW_MARKET_INFO,
			StockpileConfig.KEY_SHOW_PRICE_OVERVIEW,
			StockpileConfig.KEY_SHOW_PRICE_GRAPH,
			StockpileConfig.KEY_SHOW_VOLUME_GRAPH,
			StockpileConfig.KEY_SHOW_ALCH_INFO,
			StockpileConfig.KEY_SHOW_NOTIFICATIONS,
			StockpileConfig.KEY_SHOW_ITEM_LOG,
			StockpileConfig.KEY_SHOW_LINKS);

	/**
	 * Keeps detail-section slots unique: when a section is moved to a slot already
	 * occupied by another, the other section is swapped into the vacated slot.
	 */
	private void swapConflictingSection(ConfigChanged event)
	{
		SectionSlot newSlot;
		SectionSlot oldSlot;
		try
		{
			newSlot = SectionSlot.valueOf(event.getNewValue());
			oldSlot = SectionSlot.valueOf(event.getOldValue());
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			return;
		}

		if (newSlot == SectionSlot.NONE || newSlot == oldSlot)
			return;

		for (String key : SECTION_SLOT_KEYS)
		{
			if (key.equals(event.getKey()))
				continue;

			SectionSlot other = configManager.getConfiguration(
					StockpileConfig.GROUP, key, SectionSlot.class);
			if (other == newSlot)
			{
				configManager.setConfiguration(StockpileConfig.GROUP, key, oldSlot);
				return;
			}
		}
	}

	/** Adds a "Track Item" / "Stop Tracking" right-click option to item menu entries, when enabled. */
	@Subscribe
	public void onMenuOpened(MenuOpened event)
	{
		if (!config.addContextMenuOption())
			return;

		final MenuEntry[] entries = event.getMenuEntries();
		for (int idx = entries.length - 1; idx >= 0; --idx)
		{
			final MenuEntry entry = entries[idx];
			int itemId = getItemIdFromMenuEntry(entry);
			if (itemId <= 0)
				continue;

			final int canonicalId = itemManager.canonicalize(itemId);
			final boolean tracked = trackedItems.containsKey(canonicalId);

			client.getMenu().createMenuEntry(1)
					.setOption(tracked
							? ColorUtil.prependColorTag("Stop Tracking", config.stopTrackingColor())
							: ColorUtil.prependColorTag("Track Item", config.trackItemColor()))
					.setTarget(entry.getTarget())
					.setType(MenuAction.RUNELITE)
					.onClick(e ->
					{
						if (tracked)
							removeTrackedItem(canonicalId);
						else
							addTrackedItem(canonicalId);
					});
			return;
		}
	}

	/** @return the item id behind a menu entry (ground item or inventory/bank widget), or -1 if none. */
	private int getItemIdFromMenuEntry(MenuEntry entry)
	{
		switch (entry.getType())
		{
			case GROUND_ITEM_FIRST_OPTION:
			case GROUND_ITEM_SECOND_OPTION:
			case GROUND_ITEM_THIRD_OPTION:
			case GROUND_ITEM_FOURTH_OPTION:
			case GROUND_ITEM_FIFTH_OPTION:
			case EXAMINE_ITEM_GROUND:
				return entry.getIdentifier();
			default:
				break;
		}

		Widget w = entry.getWidget();
		if (w == null)
			return -1;

		int interfaceId = WidgetUtil.componentToInterface(w.getId());
		if (interfaceId == InterfaceID.INVENTORY
				|| interfaceId == InterfaceID.BANKMAIN
				|| interfaceId == InterfaceID.BANKSIDE)
		{
			return w.getItemId();
		}

		return -1;
	}

	/**
	 * Tracks per-container item counts as inventory/bank/etc. change, accumulating
	 * the deltas to apply on the next client tick. The first sight of a container
	 * after login only seeds a baseline (no deltas); seeing the bank can trigger a
	 * full reconcile for auto-add.
	 */
	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		int containerId = event.getContainerId();
		if (!TRACKED_CONTAINERS.contains(containerId))
			return;

		boolean firstSync = seenContainersSinceLogin.add(containerId);

		Map<Integer, Integer> oldCounts = containerCounts.getOrDefault(containerId, Collections.emptyMap());
		Map<Integer, Integer> newCounts = new HashMap<>();
		ItemContainer container = event.getItemContainer();
		if (container != null)
		{
			for (Item item : container.getItems())
			{
				if (item.getId() > 0)
					newCounts.merge(itemManager.canonicalize(item.getId()), item.getQuantity(), Integer::sum);
			}
		}

		if (!firstSync)
		{
			Set<Integer> allIds = new HashSet<>(oldCounts.keySet());
			allIds.addAll(newCounts.keySet());
			for (int itemId : allIds)
			{
				int delta = newCounts.getOrDefault(itemId, 0) - oldCounts.getOrDefault(itemId, 0);
				if (delta != 0)
					pendingItemDeltas.merge(itemId, delta, Integer::sum);
			}

			pendingQuantitySync = true;
		}

		containerCounts.put(containerId, newCounts);

		if (firstSync && containerId == InventoryID.BANK && config.autoAddItems() != AutoAddMode.OFF)
			reconcileAllQuantities();

		refreshPanel();
	}

	/**
	 * Per-tick work: flushes any pending quantity sync, evaluates notifications,
	 * and (when ground highlighting is on) reorders tracked items' "Take" menu
	 * entries to the bottom so they don't get in the way of normal actions.
	 */
	@Subscribe
	public void onClientTick(ClientTick event)
	{
		sourceAttribution.expire(client.getTickCount());
		if (pendingQuantitySync)
		{
			pendingQuantitySync = false;
			syncQuantitiesFromContainers();
		}

		flushPendingSellRealize();

		evaluateNotifications();

		if (!config.highlightTrackedItems().ground() || client.isMenuOpen())
			return;

		final MenuEntry[] entries = client.getMenu().getMenuEntries();
		final List<MenuEntry> normal = new ArrayList<>(entries.length);
		final List<MenuEntry> trackedTakes = new ArrayList<>();

		for (MenuEntry entry : entries)
		{
			if (entry.getType() == MenuAction.GROUND_ITEM_THIRD_OPTION
					&& isTracked(itemManager.canonicalize(entry.getIdentifier())))
			{
				trackedTakes.add(entry);
			}
			else
			{
				normal.add(entry);
			}
		}

		if (trackedTakes.isEmpty())
			return;

		normal.addAll(trackedTakes);
		client.getMenu().setMenuEntries(normal.toArray(new MenuEntry[0]));
	}

	/**
	 * Grand Exchange integration: each tick, detects the item on the open offer setup/details
	 * screen and, per {@link StockpileConfig#geIntegration()}, either auto-opens it in Stockpile
	 * or injects a "View in Stockpile" button. Only acts when the shown item changes.
	 */
	@Subscribe
	public void onGameTick(GameTick event)
	{
		GeIntegrationMode mode = config.geIntegration();
		boolean wantButton = mode == GeIntegrationMode.BUTTON || mode == GeIntegrationMode.BOTH;
		if (!wantButton)
			hideGeButton();

		if (mode == GeIntegrationMode.OFF)
		{
			currentGeItem = -1;
			return;
		}

		int item = currentGeOfferItem();
		if (item != currentGeItem)
		{
			currentGeItem = item;
			hideGeButton();

			if (item > 0 && (mode == GeIntegrationMode.AUTO || mode == GeIntegrationMode.BOTH))
				openGeItemInStockpile(item);
		}

		if (wantButton && item > 0 && geButton == null)
			injectGeButton();
	}

	/** Hides and forgets the injected GE button, if one is currently on the offer interface. */
	private void hideGeButton()
	{
		if (geButton == null)
			return;

		geButton.setHidden(true);
		geButton = null;
	}

	/** Forces the GE button to be re-injected against a freshly (re)built offer interface. */
	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == InterfaceID.GE_OFFERS)
			geButton = null;
	}

	/** Clears GE-integration state when the offer interface closes. */
	@Subscribe
	public void onWidgetClosed(WidgetClosed event)
	{
		if (event.getGroupId() == InterfaceID.GE_OFFERS)
		{
			currentGeItem = -1;
			geButton = null;
		}
	}

	/** @return the item shown on the visible GE offer setup/details screen, or -1 when none is open. */
	private int currentGeOfferItem()
	{
		int item = itemInGeContainer(InterfaceID.GeOffers.SETUP);
		if (item > 0)
			return item;

		return itemInGeContainer(InterfaceID.GeOffers.DETAILS);
	}

	/** @return the first item id found in the given GE container's subtree, or -1 when hidden/absent. */
	private int itemInGeContainer(int componentId)
	{
		Widget container = client.getWidget(componentId);
		if (container == null || container.isHidden())
			return -1;

		return scanForItem(container);
	}

	/** Recursively searches a widget subtree for the first child holding a real item id. */
	private int scanForItem(Widget widget)
	{
		if (widget == null)
			return -1;

		if (widget.getItemId() > 0 && isRealItem(widget.getItemId()))
			return widget.getItemId();

		Widget[][] groups = {widget.getStaticChildren(), widget.getDynamicChildren(), widget.getNestedChildren()};
		for (Widget[] group : groups)
		{
			if (group == null)
				continue;

			for (Widget child : group)
			{
				int id = scanForItem(child);
				if (id > 0)
					return id;
			}
		}

		return -1;
	}

	/**
	 * @return whether {@code itemId} resolves to a real, defined item. Empty widget
	 * slots are backed by placeholder items (e.g. id 6512) whose composition name is
	 * the literal string "null"; those must not open a preview.
	 */
	private boolean isRealItem(int itemId)
	{
		String name = itemManager.getItemComposition(itemId).getName();
		return name != null && !name.isEmpty() && !"null".equalsIgnoreCase(name);
	}

	/** Opens the item in Stockpile's view-only preview, switching to/focusing the panel when configured. */
	private void openGeItemInStockpile(int itemId)
	{
		if (itemId <= 0)
			return;

		int canonicalId = itemManager.canonicalize(itemId);
		if (trackedItems.containsKey(canonicalId))
			SwingUtilities.invokeLater(() -> panel.openTrackedDetail(canonicalId));
		else
			previewItem(canonicalId);

		if (config.geFocusPanel())
			SwingUtilities.invokeLater(() -> clientToolbar.openPanel(navButton));
	}

	/** Injects a native-style "View in Stockpile" button onto the visible GE offer container. */
	private void injectGeButton()
	{
		Widget container = client.getWidget(InterfaceID.GeOffers.SETUP);
		if (container == null || container.isHidden())
			container = client.getWidget(InterfaceID.GeOffers.DETAILS);

		if (container == null || container.isHidden())
			return;

		Widget button = container.createChild(-1, WidgetType.TEXT);
		button.setText("View in Stockpile");
		button.setFontId(495);
		button.setTextColor(0xff981f);
		button.setTextShadowed(true);
		button.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		button.setXTextAlignment(WidgetTextAlignment.RIGHT);
		button.setOriginalX(10);
		button.setOriginalY(10);
		button.setOriginalWidth(120);
		button.setOriginalHeight(18);
		button.setHasListener(true);
		button.setAction(0, "View in Stockpile");
		button.setOnOpListener((JavaScriptCallback) e -> openGeItemInStockpile(currentGeItem));
		button.setOnMouseOverListener((JavaScriptCallback) e -> button.setTextColor(0xffffff));
		button.setOnMouseLeaveListener((JavaScriptCallback) e -> button.setTextColor(0xff981f));
		button.revalidate();

		geButton = button;
	}

	/** Records a ground item and its tile so the ground overlay can outline it. */
	@Subscribe
	public void onItemSpawned(ItemSpawned event)
	{
		groundItems.put(event.getItem(), event.getTile());
	}

	/** Forgets a ground item once it despawns. */
	@Subscribe
	public void onItemDespawned(ItemDespawned event)
	{
		groundItems.remove(event.getItem());
	}

	/**
	 * Resets transient and per-login state on game-state transitions: clears
	 * ground items on each load, and on login wipes the count caches and reloads
	 * the persisted tracked items.
	 */
	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOADING:
				groundItems.clear();
				break;
			case LOGGED_IN:
				trackedItems.clear();
				previewItem = null;
				containerCounts.clear();
				runePouchCounts.clear();
				seenContainersSinceLogin.clear();
				runePouchSeenSinceLogin = false;
				geLoginTick = client.getTickCount();
				pendingQuantitySync = false;
				pendingItemDeltas.clear();
				loadCategories();
				loadPersistedItems();
				loadGeState();
				geOfferTracker.clear();
				pendingSellSuspend.clear();
				pendingSellUnsuspend.clear();
				pendingSellRealize.clear();
				refreshPanel();
				break;
			case LOGIN_SCREEN:

				refreshPanel();
				break;
			default:
				break;
		}
	}

	/** Resets the session baseline when the RS profile (account) changes, so stats restart per account. */
	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged event)
	{
		SwingUtilities.invokeLater(panel::clearSessionBaseline);
	}

	/**
	 * Mirrors rune pouch contents (held in varbits, not a normal container) into
	 * the quantity counts, accumulating deltas like a container change.
	 */
	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (RUNE_POUCH_VARBITS.contains(event.getVarbitId()))
		{
			Map<Integer, Integer> oldPouch = new HashMap<>(runePouchCounts);
			syncRunePouch();
			boolean firstSync = !runePouchSeenSinceLogin;
			runePouchSeenSinceLogin = true;
			if (!firstSync)
			{
				Set<Integer> allIds = new HashSet<>(oldPouch.keySet());
				allIds.addAll(runePouchCounts.keySet());
				for (int itemId : allIds)
				{
					int delta = runePouchCounts.getOrDefault(itemId, 0) - oldPouch.getOrDefault(itemId, 0);
					if (delta != 0)
						pendingItemDeltas.merge(itemId, delta, Integer::sum);
				}

				pendingQuantitySync = true;
			}

			refreshPanel();
		}
	}

	/** Rebuilds {@link #runePouchCounts} by reading the rune pouch type/quantity varbits. */
	private void syncRunePouch()
	{
		runePouchCounts.clear();
		EnumComposition runeEnum = client.getEnum(EnumID.RUNEPOUCH_RUNE);
		for (int i = 0; i < RUNE_POUCH_TYPE_VARBITS.length; i++)
		{
			int typeId = client.getVarbitValue(RUNE_POUCH_TYPE_VARBITS[i]);
			int qty    = client.getVarbitValue(RUNE_POUCH_QUANTITY_VARBITS[i]);
			if (typeId == 0 || qty <= 0)
				continue;

			int itemId = runeEnum.getIntValue(typeId);
			runePouchCounts.merge(itemId, qty, Integer::sum);
		}
	}

	/**
	 * Consumes GE offer progress to price trades and track the buy limit. Buy fills are
	 * ledgered until the items are collected; a sell's placement suspends the offered units
	 * and its fills realize them at the true price; a cancellation restores the remainder.
	 *
	 * <p>Just after login the offer sync replays pre-existing offers here rather than at
	 * container sync (whose offers array isn't populated yet). Within that window the state
	 * is rebuilt via {@link #primeGeStateFromLogin()} and the events are swallowed so they
	 * aren't replayed as fresh placements or fills.
	 */
	@Subscribe
	public void onGrandExchangeOfferChanged(GrandExchangeOfferChanged event)
	{
		GrandExchangeOffer offer = event.getOffer();
		if (offer == null)
			return;

		if (geLoginTick >= 0 && client.getTickCount() - geLoginTick <= GE_LOGIN_SYNC_TICKS)
		{
			primeGeStateFromLogin();
			return;
		}

		GrandExchangeOfferState state = offer.getState();
		boolean buying = state == GrandExchangeOfferState.BUYING
				|| state == GrandExchangeOfferState.BOUGHT
				|| state == GrandExchangeOfferState.CANCELLED_BUY;
		boolean cancelled = state == GrandExchangeOfferState.CANCELLED_BUY
				|| state == GrandExchangeOfferState.CANCELLED_SELL;
		boolean empty = state == GrandExchangeOfferState.EMPTY;

		GeOfferTracker.Event e = geOfferTracker.onOffer(event.getSlot(), offer.getItemId(),
				buying, cancelled, empty, offer.getTotalQuantity(), offer.getQuantitySold(), offer.getSpent());
		if (e != null)
			handleGeEvent(e);
	}

	/** Applies one derived GE event: ledger a buy, suspend/realize/restore a sell, and record the buy limit. */
	private void handleGeEvent(GeOfferTracker.Event e)
	{
		if (e.kind == GeOfferTracker.Kind.BUY)
		{
			if (e.type == GeOfferTracker.Type.FILL)
			{
				pendingGeBuys.computeIfAbsent(e.itemId, k -> new ArrayDeque<>())
						.addLast(new long[]{e.quantity, e.unitPrice});
				recordBuyLimit(e.itemId, e.quantity);
				scheduleGeStateSave();
			}

			return;
		}

		switch (e.type)
		{
			case PLACED:
				pendingSellSuspend.merge(e.itemId, e.quantity, Integer::sum);
				break;
			case FILL:
				realizeSell(e.itemId, e.quantity, e.unitPrice);
				break;
			case CANCELLED:
				pendingSellUnsuspend.merge(e.itemId, e.quantity, Integer::sum);
				break;
			default:
				break;
		}
	}

	/** Closes {@code qty} suspended units of a sold item at the realized GE price, then persists and refreshes. */
	private void realizeSell(int itemId, int qty, long unitPrice)
	{
		TrackedItem tracked = trackedItems.get(itemId);
		if (tracked == null)
			return;

		int realized = Math.min(qty, tracked.getSuspendedQuantity());
		if (realized > 0)
		{
			closeFifo(tracked, realized, unitPrice);
			tracked.setSuspendedQuantity(tracked.getSuspendedQuantity() - realized);
			persistTrackedItems();
			refreshPanel();
		}

		int shortfall = qty - realized;
		if (shortfall > 0)
			pendingSellRealize.computeIfAbsent(itemId, k -> new ArrayDeque<>())
					.addLast(new long[]{shortfall, unitPrice});

		scheduleGeStateSave();
	}

	/**
	 * Closes any GE sell fill that outran its suspension, now that the placement inventory
	 * decrease has moved the units into {@code suspendedQuantity}. Runs each tick after the
	 * container sync; unmatched fills stay parked and retry on a later tick.
	 */
	private void flushPendingSellRealize()
	{
		if (pendingSellRealize.isEmpty())
			return;

		boolean changed = false;
		Iterator<Map.Entry<Integer, Deque<long[]>>> it = pendingSellRealize.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<Integer, Deque<long[]>> entry = it.next();
			TrackedItem tracked = trackedItems.get(entry.getKey());
			if (tracked == null)
			{
				it.remove();
				continue;
			}

			Deque<long[]> queue = entry.getValue();
			while (!queue.isEmpty() && tracked.getSuspendedQuantity() > 0)
			{
				long[] chunk = queue.peekFirst();
				int realize = (int) Math.min(chunk[0], tracked.getSuspendedQuantity());
				closeFifo(tracked, realize, chunk[1]);
				tracked.setSuspendedQuantity(tracked.getSuspendedQuantity() - realize);
				chunk[0] -= realize;
				changed = true;
				if (chunk[0] <= 0)
					queue.removeFirst();
			}

			if (queue.isEmpty())
				it.remove();
		}

		if (changed)
		{
			persistTrackedItems();
			scheduleGeStateSave();
			refreshPanel();
		}
	}

	/** Accumulates a GE purchase into the item's rolling buy-limit window, rolling the window over when it expires. */
	private void recordBuyLimit(int itemId, int qty)
	{
		long now = Instant.now().getEpochSecond();
		long[] window = geBuyLimits.get(itemId);
		if (window == null || now >= window[0] + BUY_LIMIT_WINDOW.getSeconds())
			geBuyLimits.put(itemId, new long[]{now, qty});
		else
			window[1] += qty;
	}

		/**
	 * Prices one item's net container delta: GE routing first (restore a cancelled sell,
	 * drain the buy ledger, or suspend a placed sell), then the source-attribution claim,
	 * then the classic fallback.
	 */
	private void applyDelta(TrackedItem tracked, int delta)
	{
		int itemId = tracked.getItemId();
		if (delta > 0)
		{
			int remaining = delta;
			remaining = consumeSellUnsuspend(tracked, remaining);
			remaining = consumeBuyLedger(tracked, remaining);
			if (remaining > 0)
			{
				SourceAttributionCore.Attribution a = attributeDelta(itemId, remaining);
				addOpenAcquisition(tracked, remaining, a.unitPriceOr(autoAddPrice(tracked)), a.source());
			}
		}
		else
		{
			int mag = consumeSellSuspend(tracked, -delta);
			if (mag > 0)
			{
				SourceAttributionCore.Attribution a = attributeDelta(itemId, mag);
				closeFifo(tracked, mag, a.unitPriceOr(tracked.getAvgPrice()));
			}
		}
	}

	/** Restores up to {@code qty} cancelled-sell units to held (un-suspends), returning the unconsumed remainder. */
	private int consumeSellUnsuspend(TrackedItem tracked, int qty)
	{
		Integer pending = pendingSellUnsuspend.get(tracked.getItemId());
		if (pending == null || pending <= 0)
			return qty;

		int take = Math.min(qty, Math.min(pending, tracked.getSuspendedQuantity()));
		if (take <= 0)
			return qty;

		tracked.setSuspendedQuantity(tracked.getSuspendedQuantity() - take);
		int left = pending - take;
		if (left > 0)
			pendingSellUnsuspend.put(tracked.getItemId(), left);
		else
			pendingSellUnsuspend.remove(tracked.getItemId());

		return qty - take;
	}

	/**
	 * Consumes up to {@code qty} from the item's GE buy ledger into priced lots, returning
	 * the unconsumed remainder.
	 */
	private int consumeBuyLedger(TrackedItem tracked, int qty)
	{
		Deque<long[]> ledger = pendingGeBuys.get(tracked.getItemId());
		if (ledger == null || ledger.isEmpty())
			return qty;

		int remaining = qty;
		while (remaining > 0 && !ledger.isEmpty())
		{
			long[] chunk = ledger.peekFirst();
			int take = (int) Math.min(remaining, chunk[0]);
			addOpenAcquisition(tracked, take, chunk[1], AcquisitionSource.GE_TRADE);
			remaining -= take;
			chunk[0] -= take;
			if (chunk[0] <= 0)
				ledger.removeFirst();
		}

		if (ledger.isEmpty())
			pendingGeBuys.remove(tracked.getItemId());

		scheduleGeStateSave();
		return remaining;
	}

	/** Suspends up to {@code qty} units for a just-placed GE sell (no close), returning the unconsumed remainder. */
	private int consumeSellSuspend(TrackedItem tracked, int qty)
	{
		Integer pending = pendingSellSuspend.get(tracked.getItemId());
		if (pending == null || pending <= 0)
			return qty;

		int take = Math.min(qty, pending);
		tracked.setSuspendedQuantity(tracked.getSuspendedQuantity() + take);
		int left = pending - take;
		if (left > 0)
			pendingSellSuspend.put(tracked.getItemId(), left);
		else
			pendingSellSuspend.remove(tracked.getItemId());

		scheduleGeStateSave();
		return qty - take;
	}

	/**
	 * Post-login GE reconciliation, run for each offer event inside the {@link #GE_LOGIN_SYNC_TICKS}
	 * login window (when the offers array is finally populated, unlike at container sync). Seeds the
	 * offer tracker's baselines from the live offers so an offer that already existed at login is not
	 * replayed as a fresh placement or fill, drops the stale session sell-routing maps, and rebuilds
	 * {@code suspendedQuantity} from those offers so a later cancel un-suspends correctly instead of
	 * logging a phantom acquisition. Idempotent, so repeating it as the array fills in is safe.
	 */
	private void primeGeStateFromLogin()
	{
		GrandExchangeOffer[] offers = client.getGrandExchangeOffers();
		if (offers != null)
		{
			for (int slot = 0; slot < offers.length; slot++)
			{
				GrandExchangeOffer offer = offers[slot];
				if (offer == null || offer.getState() == GrandExchangeOfferState.EMPTY)
					continue;

				geOfferTracker.seed(slot, offer.getItemId(), offer.getQuantitySold(), offer.getSpent());
			}
		}

		pendingSellSuspend.clear();
		pendingSellUnsuspend.clear();
		pendingSellRealize.clear();
		reconcileSuspendedFromOffers();
	}

	/**
	 * Rewrites {@code suspendedQuantity} from the live open sell offers so offline fills or
	 * cancels self-heal at login; released units are then re-priced by {@link #reconcileAllQuantities}.
	 */
	private void reconcileSuspendedFromOffers()
	{
		Map<Integer, Integer> openSell = new HashMap<>();
		GrandExchangeOffer[] offers = client.getGrandExchangeOffers();
		if (offers != null)
		{
			for (GrandExchangeOffer offer : offers)
			{
				if (offer != null && offer.getState() == GrandExchangeOfferState.SELLING)
					openSell.merge(offer.getItemId(), offer.getTotalQuantity() - offer.getQuantitySold(), Integer::sum);
			}
		}

		for (TrackedItem tracked : trackedItems.values())
			tracked.setSuspendedQuantity(openSell.getOrDefault(tracked.getItemId(), 0));
	}

	/** Sets the item's transient buy-limit fields from its window, clearing them when the window has expired. */
	private void applyBuyLimitFields(TrackedItem item)
	{
		long[] window = geBuyLimits.get(item.getItemId());
		if (window == null || Instant.now().getEpochSecond() >= window[0] + BUY_LIMIT_WINDOW.getSeconds())
		{
			item.setLimitBought(0);
			item.setLimitResetEpoch(0);
			return;
		}

		item.setLimitBought((int) window[1]);
		item.setLimitResetEpoch(window[0] + BUY_LIMIT_WINDOW.getSeconds());
	}

	/** Persists the GE buy ledger and buy-limit windows to the RS profile config. */
	private void persistGeState()
	{
		Map<Integer, List<long[]>> ledger = new HashMap<>();
		for (Map.Entry<Integer, Deque<long[]>> e : pendingGeBuys.entrySet())
			ledger.put(e.getKey(), new ArrayList<>(e.getValue()));

		lastGeStateSave = Instant.now();
		configManager.setRSProfileConfiguration(StockpileConfig.GROUP, StockpileConfig.KEY_GE_BUY_LEDGER,
				gson.toJson(ledger, GE_LEDGER_TYPE));
		configManager.setRSProfileConfiguration(StockpileConfig.GROUP, StockpileConfig.KEY_GE_BUY_LIMITS,
				gson.toJson(geBuyLimits, GE_LIMITS_TYPE));
	}

	/** Restores the GE buy ledger and buy-limit windows from the RS profile config, defaulting to empty. */
	private void loadGeState()
	{
		pendingGeBuys.clear();
		geBuyLimits.clear();

		String ledgerJson = configManager.getRSProfileConfiguration(
				StockpileConfig.GROUP, StockpileConfig.KEY_GE_BUY_LEDGER);
		if (ledgerJson != null && !ledgerJson.trim().isEmpty())
		{
			try
			{
				Map<Integer, List<long[]>> ledger = gson.fromJson(ledgerJson, GE_LEDGER_TYPE);
				if (ledger != null)
				{
					for (Map.Entry<Integer, List<long[]>> e : ledger.entrySet())
						pendingGeBuys.put(e.getKey(), new ArrayDeque<>(e.getValue()));
				}
			}
			catch (JsonSyntaxException ex)
			{
				log.warn("Failed to parse GE buy ledger; ignoring", ex);
			}
		}

		String limitsJson = configManager.getRSProfileConfiguration(
				StockpileConfig.GROUP, StockpileConfig.KEY_GE_BUY_LIMITS);
		if (limitsJson != null && !limitsJson.trim().isEmpty())
		{
			try
			{
				Map<Integer, long[]> limits = gson.fromJson(limitsJson, GE_LIMITS_TYPE);
				if (limits != null)
					geBuyLimits.putAll(limits);
			}
			catch (JsonSyntaxException ex)
			{
				log.warn("Failed to parse GE buy limits; ignoring", ex);
			}
		}
	}

	/** Persists the GE state at most once per {@link #GE_STATE_SAVE_INTERVAL}. */
	private void scheduleGeStateSave()
	{
		if (lastGeStateSave == null
				|| Duration.between(lastGeStateSave, Instant.now()).compareTo(GE_STATE_SAVE_INTERVAL) >= 0)
			persistGeState();
	}

	/**
	 * Attributes a quantity change against the open detector claims, honouring the
	 * Source-Based Pricing kill switch: when disabled, everything is
	 * {@link AcquisitionSource#UNKNOWN} and priced by the classic fallbacks.
	 */
	private SourceAttributionCore.Attribution attributeDelta(int itemId, int quantity)
	{
		if (!config.sourcePricing())
			return SourceAttributionCore.Attribution.UNKNOWN;

		return sourceAttribution.attribute(itemId, quantity, client.getTickCount());
	}

	/** @return the cost-basis price to seed an auto-added lot with, per the configured {@link AutoAddMode}. */
	private long autoAddPrice(TrackedItem tracked)
	{
		switch (config.autoAddItems())
		{
			case HIGH:
				return tracked.getHighPrice();
			case LOW:
				return tracked.getLowPrice();
			case ZERO:
				return 0;
			case AVG:
			case OFF:
			default:
				return tracked.getAvgPrice();
		}
	}

	/**
	 * Adds {@code qty} units to an item's held lots at {@code boughtAt} gp.
	 *
	 * <p>First it reverses any equal-and-opposite "wash" closes (a prior sell at
	 * the same price, which a re-acquire should cancel), then merges into an
	 * existing open lot at the same price, or appends a new lot.
	 */
	private void addOpenAcquisition(TrackedItem tracked, int qty, long boughtAt, AcquisitionSource source)
	{
		if (qty <= 0)
			return;

		List<AcquisitionRecord> records = tracked.getAcquisitions();

		int undoBudget = qty;
		Iterator<AcquisitionRecord> it = records.iterator();
		while (it.hasNext() && undoBudget > 0)
		{
			AcquisitionRecord r = it.next();
			Long sold = r.getSoldAt();
			if (sold != null && r.getBoughtAt() == boughtAt && sold == boughtAt)
			{
				int undo = Math.min(r.getQuantity(), undoBudget);
				r.setQuantity(r.getQuantity() - undo);
				if (r.getQuantity() == 0)
					it.remove();

				undoBudget -= undo;
			}
		}

		for (AcquisitionRecord r : records)
		{
			if (r.getSoldAt() == null && r.getBoughtAt() == boughtAt && r.sourceOrUnknown() == source)
			{
				r.setQuantity(r.getQuantity() + qty);
				return;
			}
		}

		records.add(new AcquisitionRecord(qty, boughtAt, null, source));
	}

	/**
	 * Merges {@code qty} into an existing closed (sold) lot with the same
	 * bought/sold prices, to avoid fragmenting the log.
	 *
	 * @return {@code true} if a matching lot absorbed the quantity
	 */
	private boolean mergeClosed(List<AcquisitionRecord> records, int qty, long boughtAt, long soldAtPrice)
	{
		for (AcquisitionRecord r : records)
		{
			Long sold = r.getSoldAt();
			if (sold != null && r.getBoughtAt() == boughtAt && sold == soldAtPrice)
			{
				r.setQuantity(r.getQuantity() + qty);
				return true;
			}
		}

		return false;
	}

	/**
	 * Closes {@code amount} units of held inventory at {@code soldAtPrice},
	 * oldest lot first (FIFO).
	 *
	 * <p>It first cancels any just-added open lots bought at the same price (a
	 * buy immediately followed by a sell nets out), then realizes the remaining
	 * amount across the oldest open lots, splitting a lot when only part of it is
	 * sold and merging into matching closed lots where possible.
	 */
	private void closeFifo(TrackedItem tracked, int amount, long soldAtPrice)
	{
		List<AcquisitionRecord> records = tracked.getAcquisitions();
		int remaining = amount;

		Iterator<AcquisitionRecord> cancelIt = records.iterator();
		while (cancelIt.hasNext() && remaining > 0)
		{
			AcquisitionRecord r = cancelIt.next();
			if (r.getSoldAt() == null && r.getBoughtAt() == soldAtPrice)
			{
				int cancel = Math.min(r.getQuantity(), remaining);
				r.setQuantity(r.getQuantity() - cancel);
				if (r.getQuantity() == 0)
					cancelIt.remove();

				remaining -= cancel;
			}
		}

		int i = 0;
		while (i < records.size() && remaining > 0)
		{
			AcquisitionRecord r = records.get(i);
			if (r.getSoldAt() != null)
			{
				i++;
				continue;
			}

			if (r.getQuantity() <= remaining)
			{
				int closeQty = r.getQuantity();
				remaining -= closeQty;
				if (mergeClosed(records, closeQty, r.getBoughtAt(), soldAtPrice))
				{
					records.remove(i);
				}
				else
				{
					r.setSoldAt(soldAtPrice);
					i++;
				}
			}
			else
			{
				int closeQty = remaining;
				r.setQuantity(r.getQuantity() - closeQty);
				remaining = 0;
				if (!mergeClosed(records, closeQty, r.getBoughtAt(), soldAtPrice))
					records.add(i, new AcquisitionRecord(closeQty, r.getBoughtAt(), soldAtPrice, r.getSource()));
			}
		}
	}

	/**
	 * Applies the accumulated per-item container deltas to tracked items: positive
	 * deltas open new lots (auto-add), negative deltas close lots FIFO, and each
	 * item's quantity is adjusted. No-op when auto-add is off. Persists/refreshes
	 * if anything changed.
	 */
	private void syncQuantitiesFromContainers()
	{
		if (pendingItemDeltas.isEmpty())
			return;

		Map<Integer, Integer> deltas = new HashMap<>(pendingItemDeltas);
		pendingItemDeltas.clear();
		if (config.autoAddItems() == AutoAddMode.OFF || trackedItems.isEmpty())
			return;

		boolean changed = false;
		for (TrackedItem tracked : trackedItems.values())
		{
			if (tracked.getMode() != TrackItemMode.TRACK)
				continue;

			Integer delta = deltas.get(tracked.getItemId());
			if (delta == null || delta == 0)
				continue;

			applyDelta(tracked, delta);

			tracked.setQuantity(tracked.getQuantity() + delta);
			changed = true;
		}

		if (changed)
		{
			persistTrackedItems();
			refreshPanel();
		}
	}

	/**
	 * Recounts every tracked item from scratch across all containers plus the rune
	 * pouch, and reconciles each item's lots to match the true on-hand total
	 * (opening or closing lots as needed). Used to catch up after login when full
	 * container state first becomes available.
	 */
	private void reconcileAllQuantities()
	{
		pendingItemDeltas.clear();
		if (client.getGameState() != GameState.LOGGED_IN || trackedItems.isEmpty())
			return;

		for (int containerId : TRACKED_CONTAINERS)
		{
			ItemContainer container = client.getItemContainer(containerId);
			if (container == null)
				continue;

			Map<Integer, Integer> counts = containerCounts.computeIfAbsent(containerId, k -> new HashMap<>());
			counts.clear();
			for (Item item : container.getItems())
			{
				if (item.getId() > 0)
					counts.merge(itemManager.canonicalize(item.getId()), item.getQuantity(), Integer::sum);
			}
		}

		syncRunePouch();
		reconcileSuspendedFromOffers();

		boolean changed = false;
		for (TrackedItem tracked : trackedItems.values())
		{
			if (tracked.getMode() != TrackItemMode.TRACK)
				continue;

			int total = runePouchCounts.getOrDefault(tracked.getItemId(), 0)
					+ containerCounts.values().stream()
					.mapToInt(c -> c.getOrDefault(tracked.getItemId(), 0))
					.sum();
			int owned = total + tracked.getSuspendedQuantity();
			int logDelta = owned - tracked.getRecordQuantitySum();
			if (logDelta > 0)
			{
				addOpenAcquisition(tracked, logDelta, autoAddPrice(tracked), AcquisitionSource.UNKNOWN);
				changed = true;
			}
			else if (logDelta < 0)
			{
				closeFifo(tracked, -logDelta, tracked.getAvgPrice());
				changed = true;
			}

			if (tracked.getQuantity() != total)
			{
				tracked.setQuantity(total);
				changed = true;
			}
		}

		if (changed)
			persistTrackedItems();
	}

	/** Recounts a single item across all containers and the rune pouch and sets its quantity. */
	private void syncQuantitiesForItem(TrackedItem tracked)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
			return;

		for (int containerId : TRACKED_CONTAINERS)
		{
			ItemContainer container = client.getItemContainer(containerId);
			if (container == null)
				continue;

			Map<Integer, Integer> counts = containerCounts.computeIfAbsent(containerId, k -> new HashMap<>());
			counts.clear();
			for (Item item : container.getItems())
			{
				if (item.getId() > 0)
					counts.merge(itemManager.canonicalize(item.getId()), item.getQuantity(), Integer::sum);
			}
		}

		syncRunePouch();

		int total = runePouchCounts.getOrDefault(tracked.getItemId(), 0)
				+ containerCounts.values().stream()
				.mapToInt(c -> c.getOrDefault(tracked.getItemId(), 0))
				.sum();
		tracked.setQuantity(total);
	}

	/** @return whether the given (canonical) item id is currently tracked. */
	boolean isTracked(int itemId)
	{
		return trackedItems.containsKey(itemId);
	}

	/** Callback after the user edits an item's acquisitions: re-derives its quantity from the lots and persists. */
	void onAcquisitionsEdited(int itemId)
	{
		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null)
				return;

			tracked.setCostBasisInitialized(true);
			tracked.setQuantity(tracked.getRecordQuantitySum());
			persistTrackedItems();
			refreshPanel();
		});
	}

	/** Callback after the user edits an item's notification rules: just persists the change. */
	private void onNotificationsEdited(int itemId)
	{
		clientThread.invokeLater(() ->
		{
			if (trackedItems.containsKey(itemId))
				persistTrackedItems();
		});
	}

	/**
	 * Maximum plausible Δ% for a notification: changes beyond this magnitude
	 * indicate a sparse/stale window average (a near-zero denominator) rather than
	 * a real move, and are ignored so a one-shot rule isn't fired on noise.
	 */
	private static final double MAX_DELTA_PCT = 1000.0;

	private static final long GLOW_PERIOD_SLOW_MS = 2000;
	private static final long GLOW_PERIOD_MEDIUM_MS = 1500;
	private static final long GLOW_PERIOD_FAST_MS = 1000;
	private static final float GLOW_MIN_ALPHA = 0.2f;
	private static final float GLOW_MAX_ALPHA = 1f;

	/** @return the current highlight alpha, a sine "breathing" pulse whose period depends on the glow speed config. */
	float breathingAlpha()
	{
		long period;
		switch (config.glowEffect())
		{
			case SLOW:
				period = GLOW_PERIOD_SLOW_MS;
				break;
			case MEDIUM:
				period = GLOW_PERIOD_MEDIUM_MS;
				break;
			case FAST:
				period = GLOW_PERIOD_FAST_MS;
				break;
			default:
				return GLOW_MAX_ALPHA;
		}

		double phase = (System.currentTimeMillis() % period) / (double) period;
		double wave = (Math.sin(phase * 2 * Math.PI) + 1) / 2;
		return GLOW_MIN_ALPHA + (GLOW_MAX_ALPHA - GLOW_MIN_ALPHA) * (float) wave;
	}

	/** @return the live map of on-screen ground items to their tiles (used by the ground overlay). */
	Map<TileItem, Tile> getGroundItems()
	{
		return groundItems;
	}

	/** Refreshes the panel without flagging a price update (no change indicators). */
	private void refreshPanel()
	{
		refreshPanel(false);
	}

	/**
	 * Pushes the current tracked items and totals to the panel on the Swing thread.
	 *
	 * <p>Rebuilds are coalesced: the snapshot is published to {@link #pendingRebuild}
	 * (last writer wins) and a drainer is enqueued only when none is pending, so no
	 * matter how fast game events arrive, at most one rebuild sits in the EDT queue
	 * and only the newest snapshot is rendered. Without this, per-tick events queue
	 * full rebuilds faster than one completes and the panel's {@code removeAll} —
	 * which scans the pending queue per removed child — live-locks the EDT (#120).
	 *
	 * @param pricesUpdated whether this refresh follows a price change, enabling
	 *                      the per-row change indicators
	 */
	private void refreshPanel(boolean pricesUpdated)
	{
		final Instant refresh = lastPriceRefresh;
		final PriceIndicatorMode indicatorMode = pricesUpdated
				? config.priceChangeIndicator()
				: PriceIndicatorMode.OFF;
		for (TrackedItem item : trackedItems.values())
			applyBuyLimitFields(item);

		final List<TrackedItem> items = new ArrayList<>(trackedItems.values());

		final GameState gs = client.getGameState();
		final boolean loggedIn = gs == GameState.LOGGED_IN || gs == GameState.LOADING;
		final List<CategoryState> categorySnapshot = new ArrayList<>(categories);
		final boolean favCollapsed = favoritesCollapsed;
		final boolean uncatCollapsed = uncategorizedCollapsed;
		Runnable rebuild = () -> panel.rebuild(items, refresh, indicatorMode, loggedIn,
				categorySnapshot, favCollapsed, uncatCollapsed);
		if (pendingRebuild.getAndSet(rebuild) != null)
			return;

		SwingUtilities.invokeLater(() ->
		{
			Runnable newest = pendingRebuild.getAndSet(null);
			if (newest != null)
				newest.run();
		});
	}

	/**
	 * Evaluates every item's notification rules and fires the configured notifier
	 * for any that are met. A once rule is removed after firing; a repeat rule stays
	 * and re-arms edge-triggered — it fires again only after its condition has gone
	 * false and come back true, and the first evaluation after a (re)load primes it
	 * without firing. Skipped when notifications are disabled or being edited.
	 */
	private void evaluateNotifications()
	{
		Notification style = config.notificationStyle();
		if (!style.isEnabled())
			return;

		if (panel.isEditingNotifications())
			return;

		boolean changed = false;
		for (TrackedItem item : trackedItems.values())
		{
			Iterator<NotificationRule> it = item.getNotifications().iterator();
			while (it.hasNext())
			{
				NotificationRule rule = it.next();
				Boolean condition = evaluateRule(item, rule);
				if (condition == null)
					continue;

				if (rule.isRepeat())
				{
					boolean fire = condition && Boolean.FALSE.equals(rule.getLastCondition());
					rule.setLastCondition(condition);
					if (fire)
						notifier.notify(style, notificationText(item, rule));

					continue;
				}

				if (condition)
				{
					notifier.notify(style, notificationText(item, rule));
					it.remove();
					changed = true;
				}
			}
		}

		if (changed)
		{
			persistTrackedItems();
			refreshPanel();
		}
	}

	/**
	 * Evaluates a single rule against an item.
	 *
	 * @return {@code TRUE}/{@code FALSE} for the condition, or {@code null} when it
	 *         can't be evaluated yet (incomplete rule or missing/unparseable data)
	 */
	private Boolean evaluateRule(TrackedItem item, NotificationRule rule)
	{
		NotificationMetric metric = rule.getMetric();
		if (metric == null || rule.getOperation() == null)
			return null;

		if (metric.isCategorical())
		{
			String current = categoryValue(item, metric);
			if (current == null || rule.getValue() == null)
				return null;

			return current.equalsIgnoreCase(rule.getValue().trim());
		}

		TimeWindow window = metric.locksTimeframeToMonth() ? TimeWindow.MONTH : rule.getTimeWindow();
		OptionalDouble current = numericValue(item, metric, window);
		if (!current.isPresent())
			return null;

		OptionalDouble target = metric.getKind() == NotificationMetric.Kind.PERCENT
				? NotificationRule.parsePercent(rule.getValue())
				: NotificationRule.parseNumeric(rule.getValue());
		if (!target.isPresent())
			return null;

		return rule.getOperation().test(current.getAsDouble(), target.getAsDouble());
	}

	/**
	 * Resolves the current numeric reading of a metric for an item over a window
	 * (price, volume, profit, HA profit, Δ% vs. the window average, or quantity).
	 *
	 * @return the value, or empty when the underlying data is missing or unreliable
	 */
	private OptionalDouble numericValue(TrackedItem item, NotificationMetric metric, TimeWindow window)
	{
		if (metric == NotificationMetric.QUANTITY)
			return OptionalDouble.of(item.getQuantity());

		PriceStats s = item.getWindowStats().get(window);
		long avg = s == null ? 0 : s.getAvg();
		switch (metric)
		{
			case HIGH:
				return s == null ? OptionalDouble.empty() : OptionalDouble.of(s.getHigh());
			case LOW:
				return s == null ? OptionalDouble.empty() : OptionalDouble.of(s.getLow());
			case AVERAGE:
				return s == null ? OptionalDouble.empty() : OptionalDouble.of(s.getAvg());
			case VOLUME:
				return s == null ? OptionalDouble.empty() : OptionalDouble.of(s.getVolume());
			case ITM_PROFIT:
				return avg <= 0 ? OptionalDouble.empty() : OptionalDouble.of(item.getProfitAt(avg));
			case HA_PROFIT:
				if (avg <= 0 || item.getHighAlch() <= 0)
					return OptionalDouble.empty();

				return OptionalDouble.of(item.getHighAlch() - avg - runePrice(NATURE_RUNE_ID)
						- 5 * runePrice(FIRE_RUNE_ID));
			case DELTA_PCT:
			{
				long current = item.getAvgPrice();
				if (current <= 0 || avg <= 0)
					return OptionalDouble.empty();

				double pct = Math.round(((double) (current - avg) / avg) * 1000.0) / 10.0;
				return Math.abs(pct) > MAX_DELTA_PCT ? OptionalDouble.empty() : OptionalDouble.of(pct);
			}
			default:
				return OptionalDouble.empty();
		}
	}

	/**
	 * Resolves the current categorical rating of a metric for an item
	 * (volatility, liquidity, or 30-day range position) via {@link MarketClassifier}.
	 *
	 * @return the rating label, or {@code null} when it can't be classified
	 */
	private String categoryValue(TrackedItem item, NotificationMetric metric)
	{
		switch (metric)
		{
			case VOLATILITY:
				return MarketClassifier.volatility(item.getSeriesFor(TimeWindow.WEEK));
			case LIQUIDITY:
			{
				PriceStats s = item.getWindowStats().get(TimeWindow.H24);
				return MarketClassifier.liquidity(s == null ? 0 : s.getVolume());
			}
			case RANGE_30D:
			{
				long[] range = MarketClassifier.thirtyDayRange(item.getSeriesFor(TimeWindow.MONTH));
				return MarketClassifier.rangePosition(range[0], range[1], item.getAvgPrice());
			}
			default:
				return null;
		}
	}

	/** Builds the user-facing notification message, e.g. {@code "Stockpile: Coal - High >= 200"}. */
	private String notificationText(TrackedItem item, NotificationRule rule)
	{
		NotificationMetric metric = rule.getMetric();
		String valueDisplay;
		if (metric.isCategorical())
		{
			valueDisplay = rule.getValue();
		}
		else if (metric.getKind() == NotificationMetric.Kind.PERCENT)
		{
			OptionalDouble v = NotificationRule.parsePercent(rule.getValue());
			valueDisplay = v.isPresent() ? NotificationRule.formatPercent(v.getAsDouble()) : rule.getValue();
		}
		else
		{
			OptionalDouble v = NotificationRule.parseNumeric(rule.getValue());
			valueDisplay = v.isPresent()
					? String.format(Locale.US, "%,d", Math.round(v.getAsDouble()))
					: rule.getValue();
		}

		return "Stockpile: " + item.getName() + " - " + metric.getDisplayName()
				+ " " + rule.getOperation().getSymbol() + " " + valueDisplay;
	}
}
