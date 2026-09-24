/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.List;
import java.util.function.Consumer;

import net.runelite.client.game.ItemManager;

/**
 * The seam a {@link DetailView} uses to reach the state and callbacks it does not own itself (#110).
 * The detail view was extracted from {@link StockpilePanel} so a second live instance (the #109
 * dashboard window) can exist; everything the extracted component still needs from its host &mdash;
 * shared services, the plugin edit callbacks, and Back navigation &mdash; is supplied through this
 * interface rather than direct field access. {@link StockpilePanel} implements it by delegating to
 * the fields and callbacks it already holds.
 */
public interface DetailViewHost
{
	/** @return the live plugin config (colours, toggles, section visibility) the detail view reads. */
	StockpileConfig config();

	/** @return the shared item manager, for icon images and item lookups. */
	ItemManager itemManager();

	/** @return the examine text for {@code itemId}, or {@code null}/empty when none is cached. */
	String examine(int itemId);

	/** @return the tracked item backing {@code itemId}, or {@code null} if it is not tracked. */
	TrackedItem trackedItem(int itemId);

	/** @return the current nature-rune price used for high-alch profit figures. */
	long natureRunePrice();

	/** @return the current fire-rune price used for high-alch profit figures. */
	long fireRunePrice();

	/** Asks the plugin to (re)fetch the detailed price/history data for {@code itemId}. */
	void requestDetailData(int itemId);

	/** Signals that the acquisitions log for {@code itemId} was edited in-view. */
	void acquisitionsEdited(int itemId);

	/**
	 * Applies {@code mutation} to the item's acquisition list on the client thread, then signals the
	 * edit and runs {@code onApplied} back on the EDT.
	 *
	 * <p>The list is client-thread state: the FIFO cost-basis engine adds, removes and re-prices lots
	 * from there while GE offers fill and containers change. The editor used to mutate the very same
	 * {@code ArrayList} from the EDT, so two threads did structural writes with no synchronisation -
	 * an interleaving can silently drop a record or overwrite a quantity, which corrupts cost basis
	 * and every profit figure derived from it, and it bites exactly when someone edits the log while
	 * offers are filling (#315). {@code buildShareToken} and {@code buildAcquisitionsCsv} already hop
	 * for this reason and say so; the editor never got the same treatment.
	 *
	 * @param itemId the item whose log to edit; a no-op when it is not tracked
	 * @param mutation applied to the live list on the client thread
	 * @param onApplied run on the EDT once the mutation has been applied
	 */
	void editAcquisitions(int itemId, Consumer<List<AcquisitionRecord>> mutation, Runnable onApplied);

	/** Clears the acquisitions log for {@code itemId}. */
	void clearAcquisitions(int itemId);

	/** Signals that the notifications for {@code itemId} were edited in-view. */
	void notificationsEdited(int itemId);

	/** Tracks {@code itemId} from the detail header Track button (#138), honouring the add mode. */
	void addItem(int itemId, TrackItemMode mode);

	/** Untracks {@code itemId} but keeps it open as a read-only preview (#138). */
	void untrackToPreview(int itemId);

	/** Pops {@code itemId} out into its own standalone detail window, focusing an existing one (#109). */
	void popOut(int itemId);

	/** Adds {@code itemId} to the compare set, opening or focusing the compare window (#280). */
	void addToCompare(int itemId);

	/**
	 * Switches the detail view to show {@code itemId} in place, from the dashboard toolbar's search bar
	 * (#109). A pop-out window rebinds itself to the new item (tracked, or a read-only preview when it is
	 * not tracked); the sidebar shows the item's detail.
	 */
	void switchDetailItem(int itemId);

	/**
	 * Invoked by the detail view's Back control. The sidebar returns to the main list; the dashboard
	 * window disposes itself.
	 */
	void onBack();
}
