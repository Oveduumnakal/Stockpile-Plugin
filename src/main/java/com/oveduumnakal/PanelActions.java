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

	void removeItem(int itemId);

	/**
	 * Stops tracking {@code itemId} but keeps it open in the detail view as a read-only preview
	 * (#138), so untracking from the detail header does not bounce back to the main list.
	 */
	void untrackToPreview(int itemId);

	void acquisitionsEdited(int itemId);

	void requestDetailData(int itemId);

	void clearAcquisitions(int itemId);

	void notificationsEdited(int itemId);

	void clearAll();

	/** @return the examine text for {@code itemId}, or a placeholder when none is cached. */
	String examineLookup(int itemId);

	/** Moves the item at index {@code from} to index {@code to} in the manual order. */
	void reorder(int from, int to);

	void setGlobalOrder(List<Integer> order);

	void toggleCompactView();

	void setSortMode(SortMode mode);

	void toggleSortDirection();

	void setFavorite(int itemId, boolean favorite);

	void setOnOverlay(int itemId, boolean onOverlay);

	void setGroupCollapsed(String group, boolean collapsed);

	/** Builds the share token for the tracked list and hands it back through {@code callback}. */
	void exportList(Consumer<String> callback);

	/** Imports the tracked list encoded in {@code data}, reporting the outcome through {@code callback}. */
	void importList(String data, Consumer<String> callback);

	/** Builds the acquisitions CSV and hands it back through {@code callback}. */
	void exportCsv(Consumer<String> callback);

	/** @return the portfolio value-history points the chart plots. */
	List<long[]> portfolioHistory();

	void whatsNewSeen();
}
