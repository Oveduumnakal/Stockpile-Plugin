/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.List;
import java.util.function.Consumer;

/**
 * The plugin-facing callbacks the panel invokes, gathered into one named-method interface
 * (implemented by the plugin) instead of a long positional constructor parameter list (#183).
 * Several callbacks previously shared identical functional types &mdash; three {@code Runnable}s,
 * two {@code Consumer<Consumer<String>>} exporters &mdash; so transposing them at the call site
 * still compiled but silently wired the wrong action; a named method makes that a compile error and
 * lets a new feature add a method rather than another positional lambda.
 */
public interface PanelActions
{
	/** Tracks {@code itemId}, honouring how the user asked for it to be added. */
	void addItem(int itemId, TrackItemMode mode);

	/** Stops tracking {@code itemId} and removes it from the list entirely. */
	void removeItem(int itemId);

	/**
	 * Stops tracking {@code itemId} but keeps it open in the detail view as a read-only preview
	 * (#138), so untracking from the detail header does not bounce back to the main list.
	 */
	void untrackToPreview(int itemId);

	/** Notifies the plugin that {@code itemId}'s acquisition lots were edited and must be persisted. */
	void acquisitionsEdited(int itemId);

	/** Requests a fresh market/detail data load for {@code itemId}. */
	void requestDetailData(int itemId);

	/** Clears all acquisition lots recorded for {@code itemId}. */
	void clearAcquisitions(int itemId);

	/** Notifies the plugin that {@code itemId}'s notification rules were edited and must be persisted. */
	void notificationsEdited(int itemId);

	/** Stops tracking every item and clears all tracked state. */
	void clearAll();

	/** @return the examine text for {@code itemId}, or a placeholder when none is cached. */
	String examineLookup(int itemId);

	/** Moves the item at index {@code from} to index {@code to} in the manual order. */
	void reorder(int from, int to);

	/** Replaces the manual ordering with the given item-id order. */
	void setGlobalOrder(List<Integer> order);

	/** Toggles the compact (two-row) view for the whole tracked list. */
	void toggleCompactView();

	/** Sets the active sort mode for the tracked list. */
	void setSortMode(SortMode mode);

	/** Flips the current sort between ascending and descending. */
	void toggleSortDirection();

	/** Sets whether {@code itemId} is marked as a favourite. */
	void setFavorite(int itemId, boolean favorite);

	/** Sets whether {@code itemId} is shown on the in-game screen overlay. */
	void setOnOverlay(int itemId, boolean onOverlay);

	/** Sets whether {@code itemId} is displayed as a compact row. */
	void setItemCompact(int itemId, boolean compact);

	/** Sets whether the category {@code group} is collapsed in the list. */
	void setGroupCollapsed(String group, boolean collapsed);

	/** Builds the share token for the tracked list and hands it back through {@code callback}. */
	void exportList(Consumer<String> callback);

	/** Imports the tracked list encoded in {@code data}, reporting the outcome through {@code callback}. */
	void importList(String data, Consumer<String> callback);

	/** Builds the acquisitions CSV and hands it back through {@code callback}. */
	void exportCsv(Consumer<String> callback);

	/** @return the portfolio value-history points the chart plots. */
	List<long[]> portfolioHistory();

	/**
	 * @return the number of aggregate portfolio-history points, for the chart button's cheap
	 *         "enough to plot?" check without pulling the whole series each rebuild (#184).
	 */
	int portfolioPointCount();

	/** Marks the current release's "What's New" notice as seen so it stops showing. */
	void whatsNewSeen();
}
