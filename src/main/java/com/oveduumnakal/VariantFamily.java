/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Resolves an item's variant family from its display name (#302): the ordered sibling
 * names of a potion's dose line ({@code (1)}–{@code (4)}) or a cooking chain
 * ({@code Raw X → X → Burnt X}), so "Compare all variants" can fill the Compare set
 * with the whole family in one gesture.
 *
 * <p>Grouping is purely name-based over the tradeable-item corpus (the caller maps the
 * returned names to ids). The cooking chain only fires on a {@code Raw }/{@code Burnt }
 * prefixed name — a bare cooked name (e.g. {@code Lobster}) is indistinguishable from a
 * non-food item, so it is left out rather than risk a false family on everything.
 * Client-free and unit-testable.
 */
final class VariantFamily
{
	/** Highest dose a standard tradeable potion holds; shared with {@link DoseFamily}. */
	private static final int MAX_DOSES = 4;

	private static final String RAW_PREFIX = "raw ";
	private static final String BURNT_PREFIX = "burnt ";

	private VariantFamily()
	{
	}

	/**
	 * Resolves the ordered, lowercased sibling names of {@code name}'s variant family.
	 *
	 * @param name the item's display name
	 * @return the family's sibling names in natural order (dose {@code (1)}→{@code (4)}, or
	 *         raw→cooked→burnt), or an empty list when {@code name} carries no recognised family
	 */
	static List<String> siblingNames(String name)
	{
		if (name == null)
			return Collections.emptyList();

		DoseFamily.Parsed dose = DoseFamily.parse(name);
		if (dose != null)
		{
			List<String> doses = new ArrayList<>(MAX_DOSES);
			for (int d = 1; d <= MAX_DOSES; d++)
				doses.add(dose.base + "(" + d + ")");

			return doses;
		}

		String lower = name.trim().toLowerCase(Locale.ROOT);
		String core = null;
		if (lower.startsWith(RAW_PREFIX))
			core = lower.substring(RAW_PREFIX.length()).trim();
		else if (lower.startsWith(BURNT_PREFIX))
			core = lower.substring(BURNT_PREFIX.length()).trim();

		if (core == null || core.isEmpty())
			return Collections.emptyList();

		List<String> chain = new ArrayList<>(3);
		chain.add(RAW_PREFIX + core);
		chain.add(core);
		chain.add(BURNT_PREFIX + core);
		return chain;
	}

	/**
	 * @param name the item's display name
	 * @return whether {@code name} belongs to a recognised variant family, used to gate the
	 *         "Compare all variants" menu entry
	 */
	static boolean hasFamily(String name)
	{
		return !siblingNames(name).isEmpty();
	}
}
