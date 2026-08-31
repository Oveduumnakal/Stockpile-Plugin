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

	/**
	 * Pops {@code itemId} out into its own standalone, resizable detail window (#109), or focuses the
	 * existing window if one is already open for it. Both tracked items and read-only previews.
	 */
	void popOut(int itemId);

	/** Adds {@code itemId} to the compare set (#280), opening or focusing the compare window. */
	void addToCompare(int itemId);

	/**
	 * Adds every variant of {@code itemId} — its potion dose line or cooking chain (#302) — to the compare
	 * set (up to the cap), opening or focusing the compare window.
	 */
	void addVariantsToCompare(int itemId);

	/** Opens the item-less Stockpile dashboard window (#109), or focuses it if already open. */
	void openDashboard();

	/** Opens the compare window (#280) or focuses it, showing the empty prompt when nothing is compared yet. */
	void openCompare();

	/** Notifies the plugin that {@code itemId}'s acquisition lots were edited and must be persisted. */
	void acquisitionsEdited(int itemId);

	/**
	 * Applies {@code mutation} to {@code itemId}'s acquisition lots on the client thread, which owns
	 * them, then runs {@code onApplied} on the EDT. See {@link DetailViewHost#editAcquisitions} for
	 * why the panel may not touch the list directly (#315).
	 *
	 * @param itemId the item whose log to edit
	 * @param mutation applied to the live list on the client thread
	 * @param onApplied run on the EDT once the mutation has been applied
	 */
	void editAcquisitions(int itemId, Consumer<List<AcquisitionRecord>> mutation, Runnable onApplied);

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

	/**
	 * Moves one item to a new position in the manual order.
	 *
	 * <p>Both parameters are {@code int} but they are not the same kind of number: the first is an
	 * <em>item id</em>, the second a <em>list index</em>. The javadoc used to describe two indices,
	 * which would have compiled fine at a call site passing a row index and then silently no-opped,
	 * since no item would carry that id (#336).
	 *
	 * @param itemId the id of the item to move
	 * @param toIndex the position to move it to, clamped to the list bounds
	 */
	void reorder(int itemId, int toIndex);

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
