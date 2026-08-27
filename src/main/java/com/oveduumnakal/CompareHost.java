/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.List;

import net.runelite.client.game.ItemManager;

/**
 * The seam a {@link CompareView} uses to reach the shared services and callbacks it does not own
 * itself (#280). Mirrors {@link DetailViewHost}: the compare view was built to live inside a pop-out
 * {@link CompareWindow}, and everything it still needs from the plugin &mdash; config, the item
 * manager, the live rune prices for alch figures, and the remove/clear actions &mdash; is supplied
 * through this interface rather than direct field access.
 */
interface CompareHost
{
	/** @return the live plugin config (colours, section-visibility toggles) the compare view reads. */
	StockpileConfig config();

	/** @return the shared item manager, for icon images and item lookups. */
	ItemManager itemManager();

	/** @return the current nature-rune price used for high-alch profit figures. */
	long natureRunePrice();

	/** @return the current fire-rune price used for high-alch profit figures. */
	long fireRunePrice();

	/** Removes {@code itemId} from the compare set (its column disappears; the window closes if empty). */
	void removeFromCompare(int itemId);

	/** Moves {@code itemId} to {@code toIndex} in the compare order, reordering the columns (drag-reorder). */
	void moveCompare(int itemId, int toIndex);

	/** Clears the whole compare set and closes the window. */
	void clearCompare();

	/** Saves the current compare set under {@code name} (overwriting an existing one of that name) (#303). */
	void saveComparison(String name);

	/** Replaces the current compare set with the saved comparison named {@code name} (#303). */
	void loadComparison(String name);

	/** Deletes the saved comparison named {@code name} (#303). */
	void deleteComparison(String name);

	/** Replaces the current compare set with the items in an imported shared code (#303). */
	void importComparison(List<Integer> itemIds);
}
