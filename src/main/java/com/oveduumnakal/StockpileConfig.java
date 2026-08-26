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

import java.awt.Color;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Notification;
import net.runelite.client.config.Range;

/**
 * RuneLite configuration for the Stockpile plugin.
 *
 * <p>Defines every user-facing setting as a defaulted {@code @ConfigItem}
 * accessor, grouped into {@code @ConfigSection}s: main view, tracked-item row
 * display, GE estimates, tracking/highlighting, context menu, the detail view,
 * the on-screen overlay, and GE integration. The
 * {@code KEY_*} constants are the persisted setting keys (also used directly by
 * the plugin when reading/writing config), and {@link #GROUP} names the config
 * group. Each accessor's behavior is described by its annotation; the per-item
 * {@code name}/{@code description} are the source of truth shown in the UI.
 */
@ConfigGroup(StockpileConfig.GROUP)
public interface StockpileConfig extends Config
{
	/** RuneLite config group name ({@code "stockpile"}). */
	String GROUP = "stockpile";

	/** Persisted config key {@code "trackedItemIds"}. */
	String KEY_TRACKED_ITEMS = "trackedItemIds";
	/** Persisted config key {@code "priceCache"}. */
	String KEY_PRICE_CACHE = "priceCache";
	/** Persisted config key {@code "sourcePricing"}. */
	String KEY_SOURCE_PRICING = "sourcePricing";
	/** Persisted config key {@code "geBuyLedger"}. */
	String KEY_GE_BUY_LEDGER = "geBuyLedger";
	/** Persisted config key {@code "geBuyLimits"}. */
	String KEY_GE_BUY_LIMITS = "geBuyLimits";
	/** Persisted config key {@code "trackedCategories"}. */
	String KEY_CATEGORIES = "trackedCategories";

	/** Persisted config key {@code "priceRefreshSeconds"}. */
	String KEY_PRICE_REFRESH_SECONDS = "priceRefreshSeconds";
	/** Persisted config key {@code "priceChangeIndicator"}. */
	String KEY_PRICE_CHANGE_INDICATOR = "priceChangeIndicator";

	/** Persisted config key {@code "showColHigh"}. */
	String KEY_SHOW_COL_HIGH = "showColHigh";
	/** Persisted config key {@code "showColLow"}. */
	String KEY_SHOW_COL_LOW = "showColLow";
	/** Persisted config key {@code "showColAvg"}. */
	String KEY_SHOW_COL_AVG = "showColAvg";
	/** Persisted config key {@code "showColVolume"}. */
	String KEY_SHOW_COL_VOLUME = "showColVolume";
	/** Persisted config key {@code "showQuantityValue"}. */
	String KEY_SHOW_QUANTITY_VALUE = "showQuantityValue";
	/** Persisted config key {@code "row1Data"}. */
	String KEY_ROW_1_DATA = "row1Data";
	/** Persisted config key {@code "row2Data"}. */
	String KEY_ROW_2_DATA = "row2Data";
	/** Persisted config key {@code "row3Data"}. */
	String KEY_ROW_3_DATA = "row3Data";
	/** Persisted config key {@code "showItemProfitRow"}. */
	String KEY_SHOW_ITEM_PROFIT_ROW = "showItemProfitRow";
	/** Persisted config key {@code "stalePriceThresholdMinutes"}. */
	String KEY_STALE_PRICE_THRESHOLD = "stalePriceThresholdMinutes";
	/** Persisted config key {@code "compactView"}. */
	String KEY_COMPACT_VIEW = "compactView";
	/** Persisted config key {@code "sortMode"}. */
	String KEY_SORT_MODE = "sortMode";
	/** Persisted config key {@code "sortReversed"}. */
	String KEY_SORT_REVERSED = "sortReversed";
	/** Persisted config key {@code "portfolioHistory"}. */
	String KEY_PORTFOLIO_HISTORY = "portfolioHistory";

	/** Persisted config key {@code "lastSeenVersion"}. */
	String KEY_LAST_SEEN_VERSION = "lastSeenVersion";
	/** Persisted config key {@code "versionFirstSeen"}. */
	String KEY_VERSION_FIRST_SEEN = "versionFirstSeen";
	/** Persisted config key {@code "whatsNewDismissed"}. */
	String KEY_WHATS_NEW_DISMISSED = "whatsNewDismissed";
	/** Persisted config key {@code "showScreenOverlay"}. */
	String KEY_SHOW_SCREEN_OVERLAY = "showScreenOverlay";
	/** Persisted config key {@code "screenOverlayLayout"}. */
	String KEY_SCREEN_OVERLAY_LAYOUT = "screenOverlayLayout";
	/** Persisted config key {@code "screenOverlayOnTop"}. */
	String KEY_SCREEN_OVERLAY_ON_TOP = "screenOverlayOnTop";

	/** Persisted config key {@code "showItemValues"}. */
	String KEY_SHOW_ITEM_VALUES = "showItemValues";
	/** Persisted config key {@code "showCollectionValues"}. */
	String KEY_SHOW_COLLECTION_VALUES = "showCollectionValues";
	/** Persisted config key {@code "showMarketInfo"}. */
	String KEY_SHOW_MARKET_INFO = "showMarketInfo";
	/** Persisted config key {@code "showPriceOverview"}. */
	String KEY_SHOW_PRICE_OVERVIEW = "showPriceOverview";
	/** Persisted config key {@code "showPriceGraph"}. */
	String KEY_SHOW_PRICE_GRAPH = "showPriceGraph";
	/** Persisted config key {@code "showVolumeGraph"}. */
	String KEY_SHOW_VOLUME_GRAPH = "showVolumeGraph";
	/** Persisted config key {@code "showAlchInfo"}. */
	String KEY_SHOW_ALCH_INFO = "showAlchInfo";
	/** Persisted config key {@code "showNotifications"}. */
	String KEY_SHOW_NOTIFICATIONS = "showNotifications";
	/** Persisted config key {@code "showItemLog"}. */
	String KEY_SHOW_ITEM_LOG = "showItemLog";
	/** Persisted config key {@code "showLinks"}. */
	String KEY_SHOW_LINKS = "showLinks";
	/** Persisted config key {@code "buySellPressureWindow"}. */
	String KEY_PRESSURE_WINDOW = "buySellPressureWindow";
	/** Persisted config key {@code "priceOverviewPreset"}. */
	String KEY_PRICE_OVERVIEW_ROWS = "priceOverviewPreset";
	/** Persisted config key {@code "autoAddItems"}. */
	String KEY_AUTO_ADD_ITEMS = "autoAddItems";
	/** Persisted config key {@code "fallbackPricing"}. */
	String KEY_FALLBACK_PRICING = "fallbackPricing";
	/** Persisted config key {@code "notificationStyle"}. */
	String KEY_NOTIFICATION_STYLE = "notificationStyle";

	/** Persisted config key {@code "showGeEstimates"}. */
	String KEY_SHOW_GE_ESTIMATES = "showGeEstimates";
	/** Persisted config key {@code "geEstimatesPosition"}. */
	String KEY_GE_ESTIMATES_POSITION = "geEstimatesPosition";
	/** Persisted config key {@code "geEstimatesFormat"}. */
	String KEY_GE_ESTIMATES_FORMAT = "geEstimatesFormat";
	/** Persisted config key {@code "geEstimatesSpacing"}. */
	String KEY_GE_ESTIMATES_SPACING = "geEstimatesSpacing";
	/** Persisted config key {@code "showEstHigh"}. */
	String KEY_SHOW_EST_HIGH = "showEstHigh";
	/** Persisted config key {@code "showEstLow"}. */
	String KEY_SHOW_EST_LOW = "showEstLow";
	/** Persisted config key {@code "showEstAvg"}. */
	String KEY_SHOW_EST_AVG = "showEstAvg";
	/** Persisted config key {@code "showEstProfit"}. */
	String KEY_SHOW_EST_PROFIT = "showEstProfit";
	/** Persisted config key {@code "showSession"}. */
	String KEY_SHOW_SESSION = "showSession";

	/** Persisted config key {@code "contextMenuEnabled"}. */
	String KEY_CONTEXT_MENU_ENABLED = "contextMenuEnabled";
	/** Persisted config key {@code "contextMenuKey"}. */
	String KEY_CONTEXT_MENU_KEY = "contextMenuKey";
	/** Persisted config key {@code "contextMenuTrack"}. */
	String KEY_CONTEXT_MENU_TRACK = "contextMenuTrack";
	/** Persisted config key {@code "contextMenuView"}. */
	String KEY_CONTEXT_MENU_VIEW = "contextMenuView";
	/** Persisted config key {@code "contextMenuDashboard"}. */
	String KEY_CONTEXT_MENU_DASHBOARD = "contextMenuDashboard";
	/** Persisted config key {@code "promptCategoryOnTrack"}. */
	String KEY_PROMPT_CATEGORY_ON_TRACK = "promptCategoryOnTrack";
	/** Persisted config key {@code "highlightTrackedItems"}. */
	String KEY_HIGHLIGHT_TRACKED_ITEMS = "highlightTrackedItems";
	/** Persisted config key {@code "highlightColor"}. */
	String KEY_HIGHLIGHT_COLOR = "highlightColor";
	/** Persisted config key {@code "glowEffect"}. */
	String KEY_GLOW_EFFECT = "glowEffect";
	/** Persisted config key {@code "geIntegration"}. */
	String KEY_GE_INTEGRATION = "geIntegration";
	/** Persisted config key {@code "geFocusPanel"}. */
	String KEY_GE_FOCUS_PANEL = "geFocusPanel";
	/** Persisted config key {@code "geShowMarketPrices"}. */
	String KEY_GE_SHOW_MARKET_PRICES = "geShowMarketPrices";
	/** Persisted config key {@code "geShowTrackButton"}. */
	String KEY_GE_SHOW_TRACK_BUTTON = "geShowTrackButton";

	/** Top-level panel behavior: price refresh, change indicator, and global toggles. */
	@ConfigSection(
			name = "Main View Settings",
			description = "Top-level main view settings",
			position = 0
	)
	String mainViewSection = "mainView";

	/** Which columns and rows each tracked-item entry shows in the list. */
	@ConfigSection(
			name = "Tracked Item Display",
			description = "Controls what each tracked item row shows",
			position = 1
	)
	String trackedItemSection = "trackedItem";

	/** Placement, format, spacing, and rows of the estimated GE sell-value block. */
	@ConfigSection(
			name = "GE Estimates Display",
			description = "Controls the Estimated GE Sell Value section",
			position = 2
	)
	String geEstimatesSection = "geEstimates";

	/** Highlight colors/mode and the glow effect for tracked items. */
	@ConfigSection(
			name = "Tracking",
			description = "Highlighting and tracking behavior",
			position = 3
	)
	String trackingSection = "tracking";

	/** The shift+right-click Stockpile options added to item context menus. */
	@ConfigSection(
			name = "Context Menu",
			description = "Right-click item menu options (Track, View, Open in Dashboard)",
			position = 4
	)
	String contextMenuSection = "contextMenu";

	/** Order, visibility, and contents of the per-item detail view sections. */
	@ConfigSection(
			name = "Detailed View",
			description = "Order, visibility, and contents of the item detail view sections",
			position = 5
	)
	String detailViewSection = "detailView";

	/** The in-game on-screen overlay of selected tracked items. */
	@ConfigSection(
			name = "On-screen Overlay",
			description = "Show selected tracked items as a draggable in-game overlay",
			position = 6
	)
	String overlaySection = "overlay";

	/** How the open Grand Exchange offer ties into the Stockpile view. */
	@ConfigSection(
			name = "GE Integration",
			description = "How the open Grand Exchange offer ties into the Stockpile view",
			position = 7
	)
	String geIntegrationSection = "geIntegration";

	/**
	 * How often to refresh GE prices from the API. Minimum 30 seconds.
	 */
	@Range(min = 30)
	@ConfigItem(
			keyName = KEY_PRICE_REFRESH_SECONDS,
			name = "Price Refresh (s)",
			description = "How often to refresh GE prices from the API. Minimum 30 seconds.",
			section = mainViewSection,
			position = 0
	)
	default int priceRefreshSeconds()
	{
		return 60;
	}

	/**
	 * How to display the pulse indicator for price changes.
	 */
	@ConfigItem(
			keyName = KEY_PRICE_CHANGE_INDICATOR,
			name = "Price Change Indicator",
			description = "How to display the pulse indicator for price changes",
			section = mainViewSection,
			position = 1
	)
	default PriceIndicatorMode priceChangeIndicator()
	{
		return PriceIndicatorMode.CHANGE;
	}

	/**
	 * Show the High column in the tracked items list.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_COL_HIGH,
			name = "Show High Value",
			description = "Show the High column in the tracked items list",
			section = trackedItemSection,
			position = 0
	)
	default boolean showColHigh()
	{
		return true;
	}

	/**
	 * Show the Low column in the tracked items list.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_COL_LOW,
			name = "Show Low Value",
			description = "Show the Low column in the tracked items list",
			section = trackedItemSection,
			position = 1
	)
	default boolean showColLow()
	{
		return true;
	}

	/**
	 * Show the Avg column in the tracked items list.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_COL_AVG,
			name = "Show Avg Value",
			description = "Show the Avg column in the tracked items list",
			section = trackedItemSection,
			position = 2
	)
	default boolean showColAvg()
	{
		return true;
	}

	/**
	 * Show the Volume column in the tracked items list.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_COL_VOLUME,
			name = "Show Volume",
			description = "Show the Volume column in the tracked items list",
			section = trackedItemSection,
			position = 3
	)
	default boolean showColVolume()
	{
		return true;
	}

	/**
	 * Show the quantity value next to the item name.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_QUANTITY_VALUE,
			name = "Show Quantity Value",
			description = "Show the quantity value next to the item name",
			section = trackedItemSection,
			position = 4
	)
	default boolean showQuantityValue()
	{
		return true;
	}

	/**
	 * Price data shown on the first row. None hides the row.
	 */
	@ConfigItem(
			keyName = KEY_ROW_1_DATA,
			name = "Row 1 Data",
			description = "Price data shown on the first row. None hides the row.",
			section = trackedItemSection,
			position = 5
	)
	default TimeWindow row1Data()
	{
		return TimeWindow.LIVE;
	}

	/**
	 * Price data shown on the second row. None hides the row.
	 */
	@ConfigItem(
			keyName = KEY_ROW_2_DATA,
			name = "Row 2 Data",
			description = "Price data shown on the second row. None hides the row.",
			section = trackedItemSection,
			position = 6
	)
	default TimeWindow row2Data()
	{
		return TimeWindow.H24;
	}

	/**
	 * Price data shown on the third row. None hides the row.
	 */
	@ConfigItem(
			keyName = KEY_ROW_3_DATA,
			name = "Row 3 Data",
			description = "Price data shown on the third row. None hides the row.",
			section = trackedItemSection,
			position = 7
	)
	default TimeWindow row3Data()
	{
		return TimeWindow.WEEK;
	}

	/**
	 * Show the Est. Profit row below each tracked item using only that item's cost basis.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_ITEM_PROFIT_ROW,
			name = "Show Profit",
			description = "Show the Est. Profit row below each tracked item using only that item's cost basis",
			section = trackedItemSection,
			position = 8
	)
	default boolean showItemProfitRow()
	{
		return true;
	}

	/**
	 * Dim the Ltst high or low when its last trade is older than this many minutes.
	 */
	@Range(min = 1)
	@ConfigItem(
			keyName = KEY_STALE_PRICE_THRESHOLD,
			name = "Stale Price (min)",
			description = "Dim the Ltst high or low when its last trade is older than this many minutes.",
			section = trackedItemSection,
			position = 9
	)
	default int stalePriceThresholdMinutes()
	{
		return 60;
	}

	/**
	 * Show tracked items as compact two-row entries. Toggleable from the tracked list header.
	 */
	@ConfigItem(
			keyName = KEY_COMPACT_VIEW,
			name = "Compact View",
			description = "Show tracked items as compact two-row entries. Toggleable from the tracked list header.",
			section = trackedItemSection,
			position = 10
	)
	default boolean compactView()
	{
		return false;
	}

	/**
	 * Order of the tracked items list. Any mode except Manual sorts for display only and disables drag reordering.
	 * Also toggleable from the tracked list header.
	 */
	@ConfigItem(
			keyName = KEY_SORT_MODE,
			name = "Sort By",
			description = "Order of the tracked items list. Any mode except Manual sorts for display only "
					+ "and disables drag reordering. Also toggleable from the tracked list header.",
			section = trackedItemSection,
			position = 11
	)
	default SortMode sortMode()
	{
		return SortMode.MANUAL;
	}

	/**
	 * Reverses the sort direction of the tracked items list (flips each mode's default ascending/descending order).
	 * Also toggleable from the tracked list header.
	 */
	@ConfigItem(
			keyName = KEY_SORT_REVERSED,
			name = "Reverse Sort",
			description = "Reverses the sort direction of the tracked items list (flips each mode's "
					+ "default ascending/descending order). Also toggleable from the tracked list header.",
			section = trackedItemSection,
			position = 12
	)
	default boolean sortReversed()
	{
		return false;
	}

	/**
	 * Position of the Item Current Values section, or None to hide it.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_ITEM_VALUES,
			name = "Show Item Values",
			description = "Position of the Item Current Values section, or None to hide it",
			section = detailViewSection,
			position = 0
	)
	default SectionSlot showItemValues()
	{
		return SectionSlot.FIRST;
	}

	/**
	 * Position of the Collection Current Values section, or None to hide it.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_COLLECTION_VALUES,
			name = "Show Collection Values",
			description = "Position of the Collection Current Values section, or None to hide it",
			section = detailViewSection,
			position = 1
	)
	default SectionSlot showCollectionValues()
	{
		return SectionSlot.SECOND;
	}

	/**
	 * Position of the Market Info section, or None to hide it.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_MARKET_INFO,
			name = "Show Market Info",
			description = "Position of the Market Info section, or None to hide it",
			section = detailViewSection,
			position = 2
	)
	default SectionSlot showMarketInfo()
	{
		return SectionSlot.THIRD;
	}

	/**
	 * Position of the Price Overview section, or None to hide it.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_PRICE_OVERVIEW,
			name = "Show Price Overview",
			description = "Position of the Price Overview section, or None to hide it",
			section = detailViewSection,
			position = 3
	)
	default SectionSlot showPriceOverview()
	{
		return SectionSlot.FOURTH;
	}

	/**
	 * Position of the Price Graph section, or None to hide it.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_PRICE_GRAPH,
			name = "Show Price Graph",
			description = "Position of the Price Graph section, or None to hide it",
			section = detailViewSection,
			position = 4
	)
	default SectionSlot showPriceGraph()
	{
		return SectionSlot.FIFTH;
	}

	/**
	 * Position of the Volume Graph section, or None to hide it.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_VOLUME_GRAPH,
			name = "Show Volume Graph",
			description = "Position of the Volume Graph section, or None to hide it",
			section = detailViewSection,
			position = 5
	)
	default SectionSlot showVolumeGraph()
	{
		return SectionSlot.SIXTH;
	}

	/**
	 * Position of the Alchemy Info section, or None to hide it.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_ALCH_INFO,
			name = "Show Alch Info",
			description = "Position of the Alchemy Info section, or None to hide it",
			section = detailViewSection,
			position = 6
	)
	default SectionSlot showAlchInfo()
	{
		return SectionSlot.SEVENTH;
	}

	/**
	 * Position of the per-item notification rule editor, or None to hide it. Does not enable or disable notifications
	 * — use the "Notifications" setting for that.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_NOTIFICATIONS,
			name = "Show Notifications",
			description = "Position of the per-item notification rule editor, or None to hide it. "
					+ "Does not enable or disable notifications — use the \"Notifications\" setting for that.",
			section = detailViewSection,
			position = 7
	)
	default SectionSlot showNotifications()
	{
		return SectionSlot.NINTH;
	}

	/**
	 * Position of the Item Collection Log section, or None to hide it.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_ITEM_LOG,
			name = "Show Item Log",
			description = "Position of the Item Collection Log section, or None to hide it",
			section = detailViewSection,
			position = 9
	)
	default SectionSlot showItemLog()
	{
		return SectionSlot.TENTH;
	}

	/**
	 * Position of the Links section (Wiki / Live Prices), or None to hide it.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_LINKS,
			name = "Show Links",
			description = "Position of the Links section (Wiki / Live Prices), or None to hide it",
			section = detailViewSection,
			position = 10
	)
	default SectionSlot showLinks()
	{
		return SectionSlot.EIGHTH;
	}

	/**
	 * Look-back period for the Buy/Sell Pressure bar in the Market Info section.
	 */
	@ConfigItem(
			keyName = KEY_PRESSURE_WINDOW,
			name = "Buy/Sell Pressure Window",
			description = "Look-back period for the Buy/Sell Pressure bar in the Market Info section",
			section = detailViewSection,
			position = 11
	)
	default PressureWindow buySellPressureWindow()
	{
		return PressureWindow.DAY;
	}

	/**
	 * How many time-window rows the Price Overview shows. Recent: 5m, 1h, 12h, 24hr. Standard: 5m, 1h, 24hr, 1wk, 1mo.
	 * Detailed: all windows.
	 */
	@ConfigItem(
			keyName = KEY_PRICE_OVERVIEW_ROWS,
			name = "Price Overview Rows",
			description = "How many time-window rows the Price Overview shows. "
					+ "Recent: 5m, 1h, 12h, 24hr. Standard: 5m, 1h, 24hr, 1wk, 1mo. Detailed: all windows.",
			section = detailViewSection,
			position = 10
	)
	default OverviewPreset priceOverviewRows()
	{
		return OverviewPreset.DETAILED;
	}

	/**
	 * Automatically add collection-log entries from inventory/bank changes. When off, items are only tracked once you
	 * add them yourself (manual edits still work). The price a change with no observed source buys in at is set
	 * separately by "Fallback Pricing".
	 */
	@ConfigItem(
			keyName = KEY_AUTO_ADD_ITEMS,
			name = "Auto Add Items",
			description = "Automatically add collection-log entries from inventory/bank changes. When off, items "
					+ "are only tracked once you add them yourself (manual edits still work). The price a change "
					+ "with no observed source buys in at is set separately by \"Fallback Pricing\".",
			section = detailViewSection,
			position = 11
	)
	default boolean autoAddItems()
	{
		return true;
	}

	/**
	 * The price an unknown-source change buys in at — mobile/offline sessions and anything no detector observed
	 * (observed sources like GE offers price themselves). High/Low/Avg use the latest matching price, Zero buys in at
	 * 0.
	 */
	@ConfigItem(
			keyName = KEY_FALLBACK_PRICING,
			name = "Fallback Pricing",
			description = "The price an unknown-source change buys in at — mobile/offline sessions and anything no "
					+ "detector observed (observed sources like GE offers price themselves). High/Low/Avg use the "
					+ "latest matching price, Zero buys in at 0.",
			section = detailViewSection,
			position = 12
	)
	default FallbackPricing fallbackPricing()
	{
		return FallbackPricing.AVG;
	}

	/**
	 * Master switch and delivery style for per-item notifications. Set to Off to disable all item notifications;
	 * otherwise use the gear to choose how they are delivered. Independent of "Show Notifications", which only
	 * controls where the rule editor appears.
	 */
	@ConfigItem(
			keyName = KEY_NOTIFICATION_STYLE,
			name = "Notifications",
			description = "Master switch and delivery style for per-item notifications. Set to Off to disable "
					+ "all item notifications; otherwise use the gear to choose how they are delivered. "
					+ "Independent of \"Show Notifications\", which only controls where the rule editor appears.",
			section = detailViewSection,
			position = 13
	)
	default Notification notificationStyle()
	{
		return Notification.ON;
	}

	/**
	 * Show the Estimated GE Sell Value section.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_GE_ESTIMATES,
			name = "Show GE Estimates Section",
			description = "Show the Estimated GE Sell Value section",
			section = geEstimatesSection,
			position = 0
	)
	default boolean showGeEstimates()
	{
		return true;
	}

	/**
	 * Top: under the search bar above the tracked items list. Bottom: below the tracked items list.
	 */
	@ConfigItem(
			keyName = KEY_GE_ESTIMATES_POSITION,
			name = "Position",
			description = "Top: under the search bar above the tracked items list. "
					+ "Bottom: below the tracked items list.",
			section = geEstimatesSection,
			position = 1
	)
	default EstimatesPosition geEstimatesPosition()
	{
		return EstimatesPosition.BOTTOM;
	}

	/**
	 * How GE Estimate prices are formatted. Short abbreviates with k/m/b and shows a full-value tooltip on hover.
	 */
	@ConfigItem(
			keyName = KEY_GE_ESTIMATES_FORMAT,
			name = "Price Format",
			description = "How GE Estimate prices are formatted. Short abbreviates with k/m/b and shows a "
					+ "full-value tooltip on hover.",
			section = geEstimatesSection,
			position = 2
	)
	default ValueFormat geEstimatesFormat()
	{
		return ValueFormat.FULL;
	}

	/**
	 * Vertical spacing of the estimate rows. Default keeps the roomier layout; Compact tightens the rows to match the
	 * tracked items list.
	 */
	@ConfigItem(
			keyName = KEY_GE_ESTIMATES_SPACING,
			name = "Spacing",
			description = "Vertical spacing of the estimate rows. Default keeps the roomier layout; "
					+ "Compact tightens the rows to match the tracked items list.",
			section = geEstimatesSection,
			position = 3
	)
	default EstimatesSpacing geEstimatesSpacing()
	{
		return EstimatesSpacing.DEFAULT;
	}

	/**
	 * Show the row containing the estimated high value.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_EST_HIGH,
			name = "Show High Estimate",
			description = "Show the row containing the estimated high value",
			section = geEstimatesSection,
			position = 4
	)
	default boolean showEstHigh()
	{
		return true;
	}

	/**
	 * Show the row containing the estimated low value.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_EST_LOW,
			name = "Show Low Estimate",
			description = "Show the row containing the estimated low value",
			section = geEstimatesSection,
			position = 5
	)
	default boolean showEstLow()
	{
		return true;
	}

	/**
	 * Show the row containing the estimated average value.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_EST_AVG,
			name = "Show Avg Estimate",
			description = "Show the row containing the estimated average value",
			section = geEstimatesSection,
			position = 6
	)
	default boolean showEstAvg()
	{
		return true;
	}

	/**
	 * Show the row containing the estimated profit.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_EST_PROFIT,
			name = "Show Profit",
			description = "Show the row containing the estimated profit",
			section = geEstimatesSection,
			position = 7
	)
	default boolean showEstProfit()
	{
		return true;
	}

	/**
	 * Show the row containing the value gained/lost since login.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_SESSION,
			name = "Show Session",
			description = "Show the row containing the value gained/lost since login",
			section = geEstimatesSection,
			position = 8
	)
	default boolean showSession()
	{
		return true;
	}

	/**
	 * Show the Stockpile options section on an item's right-click menu (held with the Context Menu Key).
	 */
	@ConfigItem(
			keyName = KEY_CONTEXT_MENU_ENABLED,
			name = "Enable Context Menu",
			description = "Show the Stockpile options on an item's right-click menu (held with the Context Menu Key)",
			section = contextMenuSection,
			position = 0
	)
	default boolean contextMenuEnabled()
	{
		return true;
	}

	/**
	 * The key held while right-clicking an item to show the Stockpile options section.
	 */
	@ConfigItem(
			keyName = KEY_CONTEXT_MENU_KEY,
			name = "Context Menu Key",
			description = "The key held while right-clicking an item to show the Stockpile options",
			section = contextMenuSection,
			position = 1
	)
	default Keybind contextMenuKey()
	{
		return Keybind.SHIFT;
	}

	/**
	 * Include the Track / Untrack option in the Stockpile context-menu section.
	 */
	@ConfigItem(
			keyName = KEY_CONTEXT_MENU_TRACK,
			name = "Track / Untrack",
			description = "Include the Track / Untrack option in the context-menu section",
			section = contextMenuSection,
			position = 2
	)
	default boolean contextMenuTrack()
	{
		return true;
	}

	/**
	 * Include the View in Stockpile option in the Stockpile context-menu section.
	 */
	@ConfigItem(
			keyName = KEY_CONTEXT_MENU_VIEW,
			name = "View in Stockpile",
			description = "Include the View in Stockpile option in the context-menu section",
			section = contextMenuSection,
			position = 3
	)
	default boolean contextMenuView()
	{
		return true;
	}

	/**
	 * Include the Open in Dashboard option in the Stockpile context-menu section.
	 */
	@ConfigItem(
			keyName = KEY_CONTEXT_MENU_DASHBOARD,
			name = "Open in Dashboard",
			description = "Include the Open in Dashboard option in the context-menu section",
			section = contextMenuSection,
			position = 4
	)
	default boolean contextMenuDashboard()
	{
		return true;
	}

	/**
	 * When you track an item, ask which category to put it in (choose an existing one, create a new one, or skip to
	 * Uncategorized). Applies only to explicit tracking, not auto-added or view-only items.
	 */
	@ConfigItem(
			keyName = KEY_PROMPT_CATEGORY_ON_TRACK,
			name = "Prompt Category on Track",
			description = "When you track an item, ask which category to put it in (choose an existing one, "
					+ "create a new one, or skip to Uncategorized). Applies only to explicit tracking, not "
					+ "auto-added or view-only items.",
			section = trackingSection,
			position = 8
	)
	default boolean promptCategoryOnTrack()
	{
		return true;
	}

	/**
	 * Price quantity changes by how they occurred (GE offers, pickups, shops, alchemy...) as those detectors arrive.
	 * Off restores classic pricing: the Auto Add price for additions and the average price for removals. Activity
	 * already in flight when switched off (an open GE offer, an unrecovered death) still settles as detected.
	 */
	@ConfigItem(
			keyName = KEY_SOURCE_PRICING,
			name = "Source-Based Pricing",
			description = "Price quantity changes by how they occurred (GE offers, pickups, shops, alchemy...) "
					+ "as those detectors arrive. Off restores classic pricing: the Auto Add price for "
					+ "additions and the average price for removals. Activity already in flight when "
					+ "switched off (an open GE offer, an unrecovered death) still settles as detected.",
			section = trackingSection,
			position = 6
	)
	default boolean sourcePricing()
	{
		return true;
	}

	/**
	 * Where to outline tracked items.
	 */
	@ConfigItem(
			keyName = KEY_HIGHLIGHT_TRACKED_ITEMS,
			name = "Highlight Tracked Items",
			description = "Where to outline tracked items",
			section = trackingSection,
			position = 3
	)
	default HighlightMode highlightTrackedItems()
	{
		return HighlightMode.GROUND;
	}

	/**
	 * Color used to outline the highlighted tracked item.
	 */
	@ConfigItem(
			keyName = KEY_HIGHLIGHT_COLOR,
			name = "Highlight Color",
			description = "Color used to outline the highlighted tracked item",
			section = trackingSection,
			position = 4
	)
	default Color highlightColor()
	{
		return new Color(0xfb, 0xcd, 0x2b);
	}

	/**
	 * Speed of the highlight's breathing/glow effect. Off results in a solid color.
	 */
	@ConfigItem(
			keyName = KEY_GLOW_EFFECT,
			name = "Glow Effect",
			description = "Speed of the highlight's breathing/glow effect. Off results in a solid color.",
			section = trackingSection,
			position = 5
	)
	default GlowSpeed glowEffect()
	{
		return GlowSpeed.MEDIUM;
	}

	/**
	 * Show the items selected (via the manage view) as a draggable in-game overlay.
	 */
	@ConfigItem(
			keyName = KEY_SHOW_SCREEN_OVERLAY,
			name = "Show on Screen",
			description = "Show the items selected (via the manage view) as a draggable in-game overlay",
			section = overlaySection,
			position = 0
	)
	default boolean showScreenOverlay()
	{
		return true;
	}

	/**
	 * Compact two-row entries, or a replica of the standard tracked-item row.
	 */
	@ConfigItem(
			keyName = KEY_SCREEN_OVERLAY_LAYOUT,
			name = "Overlay Layout",
			description = "Compact two-row entries, or a replica of the standard tracked-item row",
			section = overlaySection,
			position = 1
	)
	default OverlayLayout screenOverlayLayout()
	{
		return OverlayLayout.STANDARD;
	}

	/**
	 * Keep the overlay above open interfaces. When off, it renders behind windows like the bank or Grand Exchange.
	 */
	@ConfigItem(
			keyName = KEY_SCREEN_OVERLAY_ON_TOP,
			name = "Overlay Always On Top",
			description = "Keep the overlay above open interfaces. When off, it renders behind windows "
					+ "like the bank or Grand Exchange.",
			section = overlaySection,
			position = 2
	)
	default boolean screenOverlayOnTop()
	{
		return false;
	}

	/**
	 * Open the current Grand Exchange offer item in Stockpile's view-only mode: via an injected button, automatically,
	 * both, or off.
	 */
	@ConfigItem(
			keyName = KEY_GE_INTEGRATION,
			name = "Interaction",
			description = "Open the current Grand Exchange offer item in Stockpile's view-only mode: "
					+ "via an injected button, automatically, both, or off",
			section = geIntegrationSection,
			position = 0
	)
	default GeIntegrationMode geIntegration()
	{
		return GeIntegrationMode.BOTH;
	}

	/**
	 * When a GE offer opens the item in Stockpile, switch to and focus the Stockpile panel. When off, the item is
	 * loaded silently (shown next time you open Stockpile).
	 */
	@ConfigItem(
			keyName = KEY_GE_FOCUS_PANEL,
			name = "Force Focus",
			description = "When a GE offer opens the item in Stockpile, switch to and focus the Stockpile "
					+ "panel. When off, the item is loaded silently (shown next time you open Stockpile).",
			section = geIntegrationSection,
			position = 1
	)
	default boolean geFocusPanel()
	{
		return true;
	}

	/**
	 * Show the item's latest 5-minute High/Low market prices as a line on the open Grand Exchange offer window.
	 * Independent of the Interaction mode above.
	 */
	@ConfigItem(
			keyName = KEY_GE_SHOW_MARKET_PRICES,
			name = "Show 5m Prices",
			description = "Show the item's latest 5-minute High/Low market prices as a line on the open "
					+ "Grand Exchange offer window. Independent of the Interaction mode above.",
			section = geIntegrationSection,
			position = 2
	)
	default boolean geShowMarketPrices()
	{
		return true;
	}

	/**
	 * Show a Track/Untrack button beside the History button on the open Grand Exchange offer window, toggling tracking
	 * of the offer's item. Independent of the Interaction mode above.
	 */
	@ConfigItem(
			keyName = KEY_GE_SHOW_TRACK_BUTTON,
			name = "Track Button",
			description = "Show a Track/Untrack button beside the History button on the open Grand Exchange "
					+ "offer window, toggling tracking of the offer's item. Independent of the Interaction mode above.",
			section = geIntegrationSection,
			position = 3
	)
	default boolean geShowTrackButton()
	{
		return true;
	}
}
