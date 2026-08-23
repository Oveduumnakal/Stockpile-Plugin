/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

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
	 * @param includeCategorized when {@code true} also re-categorizes items already in a
	 *                           category; otherwise only uncategorized items are touched
	 * @return a user-facing summary of how many items were categorized
	 */
	String autoCategorize(boolean includeCategorized);
}
