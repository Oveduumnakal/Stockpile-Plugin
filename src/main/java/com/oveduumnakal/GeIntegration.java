/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.SpritePixels;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.ImageUtil;

/**
 * The Grand Exchange offer-screen integration (#334): the injected "View in Stockpile" button
 * (#140), the Track/Untrack button in the title bar (#139), and the rewritten market price line in
 * the offer's info block (#142), plus the widget scans that locate where each goes.
 *
 * <p>Extracted from {@code StockpilePlugin}, where it was ~400 self-contained lines of widget work
 * mixed in with everything else. It owns the injected widgets' whole lifecycle, which is what made
 * #324 possible to reason about: the widgets are created once per interface load and reused across
 * item changes rather than hidden and re-injected, since a hidden widget stays a child of its
 * container. The handful of plugin actions it needs come through {@link GeIntegrationHost}.
 */
class GeIntegration
{
	/** Height the info block is grown to once the market line replaces the native one (#142). */
	private static final int GE_DESC_HEIGHT = 80;

	/** The GE window title bar's orange, matched so the Track button reads as part of the interface. */
	private static final int GE_TITLE_ORANGE = 0xff981f;

	/** Sprite id the bundled Stockpile icon is registered under for the injected button (#140). */
	private static final int STOCKPILE_GE_SPRITE_ID = -21140;

	/** Rendered size of the injected Stockpile button, in pixels. */
	private static final int GE_ICON_SIZE = 25;

	/** Muted GE-title orange for the Track button's outline box (#139). */
	private static final int GE_TRACK_BORDER = 0xcc7d1a;

	/** The native "Actively traded price" segment of the GE info block, rewritten every tick (#288). */
	private static final Pattern GE_ACTIVE_PRICE_LINE =
		Pattern.compile("(?: / |<br>)?Actively traded price:[^<]*");

	private final Client client;

	private final ItemManager itemManager;

	private final StockpileConfig config;

	private final GeIntegrationHost host;

	/** The item on the open offer screen, or -1 when none is shown. */
	private int currentGeItem = -1;

	/** The injected "View in Stockpile" graphic, or null when none is on the interface. */
	private Widget geButton;

	/** The injected Track/Untrack button's border box, or null when none is on the interface. */
	private Widget geTrackButton;

	/** The injected Track/Untrack button's text, or null when none is on the interface. */
	private Widget geTrackLabel;

	/** The item the cached market line belongs to, or -1 when nothing is cached. */
	private int geLineItem = -1;

	/** The cached market line's high price. */
	private long geLineHigh;

	/** The cached market line's low price. */
	private long geLineLow;

	/** The cached market line's source label (5m / 1h / Latest), or null when unresolved. */
	private String geLineSource;

	GeIntegration(Client client, ItemManager itemManager, StockpileConfig config, GeIntegrationHost host)
	{
		this.client = client;
		this.itemManager = itemManager;
		this.config = config;
		this.host = host;
	}

	/**
	 * Per-tick GE work: resolves the item on the open offer screen and, per
	 * {@link StockpileConfig#geIntegration()}, auto-opens it in Stockpile or keeps the injected
	 * button and Track/Untrack button in place, refreshing the market price line.
	 *
	 * <p>The injected widgets are reused across item changes rather than torn down and rebuilt.
	 * They used to be hidden and re-injected on every change, and hiding a widget leaves it a child
	 * of the container - so browsing N items left up to 3N orphaned children on a live interface,
	 * each still walked by the client's widget tree (#324). Neither widget depends on the item: the
	 * Stockpile button's sprite is fixed and its handler reads the current item at click time, and
	 * the Track/Untrack button only needs its label refreshed, which {@link #applyGeTrackLabel()}
	 * does in place. Not re-injecting also drops {@link #injectGeTrackButton()}'s full recursive scan
	 * of the GE toplevel from every item change to once per interface load.
	 */
	void onGameTick()
	{
		GeIntegrationMode mode = config.geIntegration();
		boolean wantButton = mode == GeIntegrationMode.BUTTON || mode == GeIntegrationMode.BOTH;
		boolean wantPrices = config.geShowMarketPrices();
		boolean wantTrack = config.geShowTrackButton();
		if (!wantButton)
			hideGeButton();

		if (!wantTrack)
			hideGeTrackButton();

		if (mode == GeIntegrationMode.OFF && !wantPrices && !wantTrack)
		{
			currentGeItem = -1;
			return;
		}

		int item = currentGeOfferItem();
		if (item != currentGeItem)
		{
			currentGeItem = item;
			geLineItem = -1;
			geLineHigh = 0;
			geLineLow = 0;
			geLineSource = null;
			applyGeTrackLabel();

			if (item > 0 && wantPrices)
				requestGeLinePrices(item);

			if (item > 0 && (mode == GeIntegrationMode.AUTO || mode == GeIntegrationMode.BOTH))
				openGeItemInStockpile(item);
		}

		if (wantButton && item > 0 && geButton == null)
			injectGeButton();

		if (wantTrack && item > 0 && geTrackButton == null)
			injectGeTrackButton();

		setGeWidgetsVisible(item > 0);

		if (wantPrices && item > 0)
			applyGeHighLowLine();
	}

	/** Forgets the injected widgets when the offer interface is (re)built; they die with it. */
	void onInterfaceLoaded()
	{
		geButton = null;
		geTrackButton = null;
		geTrackLabel = null;
	}

	/** Clears all GE state when the offer interface closes. */
	void onInterfaceClosed()
	{
		currentGeItem = -1;
		onInterfaceLoaded();
	}

	/** Drops the injected widgets and the sprite override at plugin shutdown. */
	void shutDown()
	{
		hideGeButton();
		hideGeTrackButton();
		unregisterGeButtonSprite();
	}

	/**
	 * Shows or hides the injected GE widgets without destroying them, for when the interface is on a
	 * screen with no item (the offer list). Reusing them this way is what keeps a browsing session
	 * from stacking orphaned children on the container (#324).
	 */
	void setGeWidgetsVisible(boolean visible)
	{
		for (Widget widget : new Widget[]{geButton, geTrackButton, geTrackLabel})
		{
			if (widget == null || widget.isHidden() == !visible)
				continue;

			widget.setHidden(!visible);
			widget.revalidate();
		}
	}

	/**
	 * Hides and forgets the injected GE button, if one is currently on the offer interface.
	 *
	 * <p>Only called when the feature is switched off or the interface goes away, not on an item
	 * change - a hidden widget stays a child of its container, so calling this per item change is
	 * what accumulated orphans (#324).
	 */
	void hideGeButton()
	{
		if (geButton == null)
			return;

		geButton.setHidden(true);
		geButton = null;
	}

	/** Hides and forgets the injected Track/Untrack button, if one is currently on the offer interface (#139). */
	void hideGeTrackButton()
	{
		if (geTrackButton == null)
			return;

		geTrackButton.setHidden(true);
		geTrackButton = null;
		if (geTrackLabel != null)
		{
			geTrackLabel.setHidden(true);
			geTrackLabel = null;
		}
	}

	/** @return the item shown on the visible GE offer setup/details screen, or -1 when none is open. */
	int currentGeOfferItem()
	{
		int item = itemInGeContainer(InterfaceID.GeOffers.SETUP);
		if (item > 0)
			return item;

		return itemInGeContainer(InterfaceID.GeOffers.DETAILS);
	}

	/** @return the first item id found in the given GE container's subtree, or -1 when hidden/absent. */
	int itemInGeContainer(int componentId)
	{
		Widget container = client.getWidget(componentId);
		if (container == null || container.isHidden())
			return -1;

		return scanForItem(container);
	}

	/** Recursively searches a widget subtree for the first child holding a real item id. */
	int scanForItem(Widget widget)
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
	boolean isRealItem(int itemId)
	{
		String name = itemManager.getItemComposition(itemId).getName();
		return name != null && !name.isEmpty() && !"null".equalsIgnoreCase(name);
	}

	/** Opens the item in Stockpile's view-only preview, switching to/focusing the panel when configured. */
	void openGeItemInStockpile(int itemId)
	{
		if (itemId <= 0)
			return;

		int canonicalId = itemManager.canonicalize(itemId);
		if (host.isTracked(canonicalId))
			host.openTrackedDetail(canonicalId);
		else
			host.previewItem(canonicalId);

		if (config.geFocusPanel())
			host.focusPanel();
	}

	/**
	 * Registers the bundled Stockpile icon as a custom sprite override so it can be drawn on the
	 * injected GE button (#140). Scaled to {@link #GE_ICON_SIZE} for a crisp render at button size.
	 * A no-op when sprite overrides are unavailable (e.g. before the client is ready).
	 */
	void registerGeButtonSprite(BufferedImage icon)
	{
		Map<Integer, SpritePixels> overrides = client.getSpriteOverrides();
		if (overrides == null || icon == null)
			return;

		BufferedImage scaled = ImageUtil.resizeImage(icon, GE_ICON_SIZE, GE_ICON_SIZE);
		overrides.put(STOCKPILE_GE_SPRITE_ID, ImageUtil.getImageSpritePixels(scaled, client));
	}

	/** Removes the Stockpile GE-button sprite override on shutdown (#140). */
	void unregisterGeButtonSprite()
	{
		Map<Integer, SpritePixels> overrides = client.getSpriteOverrides();
		if (overrides != null)
			overrides.remove(STOCKPILE_GE_SPRITE_ID);
	}

	/**
	 * Injects the Stockpile icon as a "View in Stockpile" button onto the visible GE offer container
	 * (#140). The icon-only graphic sits where the old text link did; the "View in Stockpile" text now
	 * lives on the hover action/tooltip. Clicking opens the offer's item in Stockpile's detail view;
	 * hover brightens the icon to full opacity.
	 */
	void injectGeButton()
	{
		Widget container = client.getWidget(InterfaceID.GeOffers.SETUP);
		if (container == null || container.isHidden())
			container = client.getWidget(InterfaceID.GeOffers.DETAILS);

		if (container == null || container.isHidden())
			return;

		Widget button = container.createChild(-1, WidgetType.GRAPHIC);
		button.setSpriteId(STOCKPILE_GE_SPRITE_ID);
		button.setOpacity(60);
		button.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		button.setOriginalX(10);
		button.setOriginalY(8);
		button.setOriginalWidth(GE_ICON_SIZE);
		button.setOriginalHeight(GE_ICON_SIZE);
		button.setHasListener(true);
		button.setAction(0, "View in Stockpile");
		button.setOnOpListener((JavaScriptCallback) e -> openGeItemInStockpile(currentGeItem));
		button.setOnMouseOverListener((JavaScriptCallback) e -> button.setOpacity(0));
		button.setOnMouseLeaveListener((JavaScriptCallback) e -> button.setOpacity(60));
		button.revalidate();

		geButton = button;
	}

	/**
	 * Injects a Track/Untrack button in the GE window's title bar, immediately left of the close (X)
	 * button (#139): a muted-orange outline box framing bold Track/Untrack text (the "Grand Exchange"
	 * header font/weight) whose colour reflects the tracked state. The close button is located at
	 * runtime so the button sits in the same section and row as the X, not in the offer content.
	 * The text box is inset 3px inside the border so the label clears the outline.
	 */
	void injectGeTrackButton()
	{
		Widget close = findGeCloseButton();
		if (close == null)
			return;

		Widget parent = close.getParent();
		if (parent == null)
			return;

		int width = 58;
		int boxHeight = 18;
		int gap = 6;
		int closeWidth = close.getWidth() > 0 ? close.getWidth() : close.getOriginalWidth();
		int closeHeight = close.getHeight() > 0 ? close.getHeight() : 21;
		boolean fromRight = close.getXPositionMode() == WidgetPositionMode.ABSOLUTE_RIGHT;
		int borderX = fromRight
				? close.getOriginalX() + closeWidth + gap
				: Math.max(0, close.getOriginalX() - width - gap);
		int y = close.getOriginalY() + Math.max(0, (closeHeight - boxHeight) / 2);
		int xMode = close.getXPositionMode();
		int yMode = close.getYPositionMode();

		Widget border = parent.createChild(-1, WidgetType.RECTANGLE);
		border.setFilled(false);
		border.setTextColor(GE_TRACK_BORDER);
		border.setOpacity(0);
		border.setXPositionMode(xMode);
		border.setYPositionMode(yMode);
		border.setOriginalX(borderX);
		border.setOriginalY(y);
		border.setOriginalWidth(width);
		border.setOriginalHeight(boxHeight);
		border.setHasListener(true);
		border.setOnOpListener((JavaScriptCallback) e -> toggleGeTracking());
		border.revalidate();

		Widget label = parent.createChild(-1, WidgetType.TEXT);
		label.setFontId(FontID.BOLD_12);
		label.setTextShadowed(true);
		label.setXPositionMode(xMode);
		label.setYPositionMode(yMode);
		label.setOriginalX(borderX + 3);
		label.setOriginalY(y);
		label.setOriginalWidth(width - 6);
		label.setOriginalHeight(boxHeight);
		label.setXTextAlignment(WidgetTextAlignment.CENTER);
		label.setYTextAlignment(WidgetTextAlignment.CENTER);
		label.setHasListener(true);
		label.setOnOpListener((JavaScriptCallback) e -> toggleGeTracking());
		label.setOnMouseOverListener((JavaScriptCallback) e -> label.setTextColor(0xffffff));
		label.setOnMouseLeaveListener((JavaScriptCallback) e -> applyGeTrackLabel());
		label.revalidate();

		geTrackButton = border;
		geTrackLabel = label;
		applyGeTrackLabel();
	}

	/**
	 * Locates the GE window's close (X) button by walking to the top-level ancestor of the open offer
	 * container and searching its subtree for a visible widget with a "Close" action (#139). Confined
	 * to the GE window's toplevel so it doesn't match some other interface's close button.
	 */
	Widget findGeCloseButton()
	{
		Widget container = client.getWidget(InterfaceID.GeOffers.SETUP);
		if (container == null || container.isHidden())
			container = client.getWidget(InterfaceID.GeOffers.DETAILS);

		if (container == null || container.isHidden())
			return null;

		Widget root = container;
		while (root.getParent() != null)
			root = root.getParent();

		return scanForCloseAction(root);
	}

	/** Recursively searches a widget subtree for the first visible widget carrying a "Close" action. */
	Widget scanForCloseAction(Widget widget)
	{
		if (widget == null || widget.isHidden())
			return null;

		String[] actions = widget.getActions();
		if (actions != null)
		{
			for (String action : actions)
			{
				if ("Close".equalsIgnoreCase(action))
					return widget;
			}
		}

		Widget[][] groups = {widget.getStaticChildren(), widget.getDynamicChildren(), widget.getNestedChildren()};
		for (Widget[] group : groups)
		{
			if (group == null)
				continue;

			for (Widget child : group)
			{
				Widget found = scanForCloseAction(child);
				if (found != null)
					return found;
			}
		}

		return null;
	}

	/** Sets the Track/Untrack text, action, and resting colour (green/red) from the offer's tracked state. */
	void applyGeTrackLabel()
	{
		if (geTrackLabel == null || currentGeItem <= 0)
			return;

		int canonicalId = itemManager.canonicalize(currentGeItem);
		boolean tracked = host.isTracked(canonicalId);
		String label = tracked ? "Untrack" : "Track";
		geTrackLabel.setText(label);
		geTrackLabel.setTextColor(GE_TITLE_ORANGE);
		geTrackLabel.setAction(0, label);
		if (geTrackButton != null)
			geTrackButton.setAction(0, label);
	}

	/**
	 * Toggles tracking of the open GE offer's item (#139). The add/remove is deferred to the client
	 * thread, so the label refresh is enqueued after it — otherwise it would read the pre-toggle state
	 * and only correct itself on the next mouse-leave.
	 */
	void toggleGeTracking()
	{
		if (currentGeItem <= 0)
			return;

		int canonicalId = itemManager.canonicalize(currentGeItem);
		if (host.isTracked(canonicalId))
			host.untrackItem(canonicalId);
		else
			host.trackItem(canonicalId);

		host.runOnClientThread(this::applyGeTrackLabel);
	}

	/**
	 * Swaps the "Actively traded price" text inside the open GE offer's info block (the single
	 * {@code SETUP_DESC}/{@code DETAILS_DESC} text widget) for one compact market line — High, Low
	 * and Avg together — in place, so the line count never changes and nothing else moves (#142).
	 * Re-applied each tick so the game's own redraw does not win; idempotent because once the native
	 * text is gone the rewrite is skipped. No-op until the shown item's data has been fetched and priced.
	 */
	void applyGeHighLowLine()
	{
		if (geLineItem != currentGeItem || (geLineHigh <= 0 && geLineLow <= 0))
			return;

		Widget desc = client.getWidget(InterfaceID.GeOffers.SETUP_DESC);
		if (desc == null || desc.isHidden())
			desc = client.getWidget(InterfaceID.GeOffers.DETAILS_DESC);

		if (desc == null || desc.isHidden())
			return;

		String text = desc.getText();
		String rebuilt = injectPriceLines(text);
		if (rebuilt.equals(text))
			return;

		desc.setText(rebuilt);
		desc.setOriginalHeight(GE_DESC_HEIGHT);
		desc.revalidate();
	}

	/**
	 * Swaps the "Actively traded price: N" segment of the GE info-block for the two market lines,
	 * always on their own rows: a leading "Buy limit: N /" that RuneLite inlines on buy offers is
	 * split off onto its own line, and any trailing convenience-fee line is kept. Returns the text
	 * unchanged when there is no native segment to replace, leaving an already-rewritten block
	 * alone (#142).
	 *
	 * @param desc the current info-block text (may be null)
	 * @return the rewritten text, or the original when nothing was replaced
	 */
	String injectPriceLines(String desc)
	{
		if (desc == null || !desc.contains("Actively traded price"))
			return desc;

		String replacement = Matcher.quoteReplacement("<br>" + priceLines());
		return GE_ACTIVE_PRICE_LINE.matcher(desc).replaceAll(replacement);
	}

	/**
	 * @return one market row — High and Low together — coloured per side and prefixed with the
	 *         resolved source ({@code 5m}/{@code 1h}/{@code Latest}) (#142).
	 */
	String priceLines()
	{
		String prefix = geLineSource == null ? "" : geLineSource + " ";
		return prefix + "High: " + colourGp(geLineHigh, "64dc64")
				+ "  Low: " + colourGp(geLineLow, "dc6464");
	}

	/**
	 * Resolves the open GE offer item's market prices in the background and caches them for the
	 * info-block line, overwriting it in place once they arrive (#142). Falls back down a chain:
	 * the latest priced 5m sample, then the latest priced 1h sample, then the item's latest instant
	 * high/low; whichever lands first sets the row-label prefix (5m / 1h / Latest).
	 */
	void requestGeLinePrices(int itemId)
	{
		int canonical = itemManager.canonicalize(itemId);
		host.runInBackground(() ->
		{
			String source = "5m";
			long[] highLow = latestSeriesHighLow(host.fetchSeries(canonical, "5m"));
			if (highLow[0] <= 0 && highLow[1] <= 0)
			{
				source = "1h";
				highLow = latestSeriesHighLow(host.fetchSeries(canonical, "1h"));
			}

			final String seriesSource = source;
			final long[] seriesHighLow = highLow;
			host.runOnClientThread(() ->
			{
				if (itemId != currentGeItem)
					return;

				long high = seriesHighLow[0];
				long low = seriesHighLow[1];
				String label = seriesSource;
				if (high <= 0 && low <= 0)
				{
					long[] latest = host.latestPrices(canonical);
					if (latest != null)
					{
						high = latest[0];
						low = latest[1];
						label = "Latest";
					}
				}

				geLineItem = itemId;
				geLineHigh = high;
				geLineLow = low;
				geLineSource = label;
				applyGeHighLowLine();
			});
		});
	}

	/**
	 * Scans a price series newest-first for the most recent priced average high and low,
	 * returned as {@code [high, low]} (each 0 when the series holds no priced sample) (#142).
	 *
	 * @param series the price points, oldest first (may be null or empty)
	 * @return a two-element array of the latest non-zero high and low
	 */
	static long[] latestSeriesHighLow(List<WikiRealtimePriceClient.PricePoint> series)
	{
		long high = 0;
		long low = 0;
		if (series != null)
		{
			for (int i = series.size() - 1; i >= 0 && (high == 0 || low == 0); i--)
			{
				WikiRealtimePriceClient.PricePoint point = series.get(i);
				if (high == 0 && point.getAvgHighPrice() > 0)
					high = point.getAvgHighPrice();

				if (low == 0 && point.getAvgLowPrice() > 0)
					low = point.getAvgLowPrice();
			}
		}

		return new long[]{high, low};
	}

	/** @return a full grouped {@code "1,234 gp"} in the given colour, or a muted dash when unpriced (#142). */
	private static String colourGp(long value, String colour)
	{
		if (value <= 0)
			return "<col=969696>—</col>";

		return "<col=" + colour + ">" + GpFormat.grouped(value) + " gp</col>";
	}
}
