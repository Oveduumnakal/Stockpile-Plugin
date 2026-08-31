/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.List;

/**
 * The plugin seam {@link GeIntegration} calls back through (#334).
 *
 * <p>The Grand Exchange widget work is self-contained apart from a handful of plugin actions — open
 * or preview an item, track or untrack it, read an item's latest prices, and fetch a wiki series in
 * the background. Naming them here keeps the widget code free of the plugin's own state.
 */
interface GeIntegrationHost
{
	/** @return whether {@code canonicalId} is in the tracked list. */
	boolean isTracked(int canonicalId);

	/** Opens the tracked item's detail view in the side panel. */
	void openTrackedDetail(int canonicalId);

	/** Opens an untracked item as the panel's view-only preview. */
	void previewItem(int canonicalId);

	/** Brings the Stockpile side panel to the front. */
	void focusPanel();

	/** Adds {@code canonicalId} to the tracked list in the default mode. */
	void trackItem(int canonicalId);

	/** Removes {@code canonicalId} from the tracked list. */
	void untrackItem(int canonicalId);

	/**
	 * @param canonicalId the item to look up
	 * @return the item's latest instant {@code [high, low]} prices, or {@code null} when no live
	 *         instance of it is held
	 */
	long[] latestPrices(int canonicalId);

	/** Fetches one wiki timeseries; called on the background executor, never the client thread. */
	List<WikiRealtimePriceClient.PricePoint> fetchSeries(int canonicalId, String timestep);

	/** Runs {@code task} on the background executor. */
	void runInBackground(Runnable task);

	/** Runs {@code task} on the client thread. */
	void runOnClientThread(Runnable task);
}
