/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One user-defined tracked-item category: its display {@code name} and whether
 * its accordion group is currently {@code collapsed} in the panel. The ordered
 * list of these is the source of truth for category order, naming, and
 * collapsed state, persisted separately from the tracked items themselves.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryState
{
	/** Group key for the special "Favorites" pseudo-group (pinned above all categories). */
	public static final String FAVORITES_KEY = "__favorites__";

	/** Group key for the catch-all "Uncategorized" group (items with no category). */
	public static final String UNCATEGORIZED_KEY = "__uncategorized__";

	/** The longest category name kept; anything past it is cut, so a pasted name cannot swamp the panel. */
	static final int MAX_NAME_LENGTH = 40;

	private String name;
	private boolean collapsed;

	/**
	 * Makes a category name safe to store and render: drops {@code <}, {@code >} and control characters,
	 * collapses runs of whitespace, and caps the length at {@link #MAX_NAME_LENGTH}.
	 *
	 * <p>Category names reach Swing labels, and Swing renders any label text beginning with
	 * {@code <html>} as HTML - including {@code <img src="http://...">}, which it fetches on the EDT.
	 * Share codes carry category names and are meant to be pasted from other players, so without this a
	 * code could make every importer's client request an arbitrary URL, revealing their IP (#375).
	 *
	 * @param name the raw name, typed or imported
	 * @return the cleaned name, or {@code null} when nothing usable is left
	 */
	static String sanitizeName(String name)
	{
		if (name == null)
			return null;

		String cleaned = name
				.replaceAll("[<>\\p{Cntrl}]", "")
				.replaceAll("\\s+", " ")
				.trim();
		if (cleaned.length() > MAX_NAME_LENGTH)
			cleaned = cleaned.substring(0, MAX_NAME_LENGTH).trim();

		return cleaned.isEmpty() ? null : cleaned;
	}
}
