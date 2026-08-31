/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.function.Consumer;

/** The category management operations the panel invokes; implemented by the plugin. */
public interface CategoryActions
{
	/** Assigns {@code itemId} to {@code category} (or clears it when {@code category} is null). */
	void setItemCategory(int itemId, String category);

	/** Creates a new empty category named {@code name}. */
	void create(String name);

	/** Renames the category {@code oldName} to {@code newName}. */
	void rename(String oldName, String newName);

	/** Deletes the category {@code name}, leaving its items uncategorized. */
	void delete(String name);

	/** Moves the category {@code name} to {@code targetIndex} in the display order. */
	void reorder(String name, int targetIndex);

	/**
	 * Auto-assigns tracked items to generated categories from the bundled wiki
	 * category snapshot.
	 *
	 * <p>Asynchronous: the count and the mutation both run on the client thread, which owns the
	 * tracked-item map, and {@code onResult} is delivered back on the EDT once they have. It cannot
	 * return the summary directly, because counting on the caller's thread is what #319 was.
	 *
	 * @param includeCategorized when {@code true} also re-categorizes items already in a
	 *                           category; otherwise only uncategorized items are touched
	 * @param onResult receives a user-facing summary of how many items were categorized, on the EDT
	 */
	void autoCategorize(boolean includeCategorized, Consumer<String> onResult);
}
