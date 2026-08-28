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
 * ({@code Raw X → cooked X}), so "Compare all variants" can fill the Compare set with
 * the whole family in one gesture.
 *
 * <p>Grouping is purely name-based over the tradeable-item corpus (the caller maps the
 * returned names to ids and silently drops the ones that do not resolve). The cooked
 * form is named two different ways in game — bare ({@code Lobster}) for the classic
 * foods and {@code Cooked }-prefixed ({@code Cooked pyre fox}) for the hunter meats —
 * so the chain offers BOTH candidates and lets the caller keep whichever exists. Only
 * one of them ever does, so the family cannot gain a duplicate column.
 *
 * <p>No burnt name is emitted: the only tradeable items whose name starts with
 * {@code Burnt } are {@code Burnt bones} and {@code Burnt page}, so a burnt food could
 * never resolve to a Compare column (#309). {@code Burnt } stays an ENTRY point — a
 * player who right-clicks a burnt item still gets its family — it is just never a
 * result.
 *
 * <p>The chain fires on a {@code Raw }/{@code Cooked }/{@code Burnt } prefixed name. A
 * bare cooked name (e.g. {@code Lobster}) is indistinguishable from a non-food item, so
 * it is left out rather than risk a false family on everything. Client-free and
 * unit-testable.
 */
final class VariantFamily
{
	/** Highest dose a standard tradeable potion holds; shared with {@link DoseFamily}. */
	private static final int MAX_DOSES = 4;

	private static final String RAW_PREFIX = "raw ";
	private static final String COOKED_PREFIX = "cooked ";
	private static final String BURNT_PREFIX = "burnt ";

	private VariantFamily()
	{
	}

	/**
	 * Resolves the ordered, lowercased sibling names of {@code name}'s variant family.
	 *
	 * @param name the item's display name
	 * @return the family's sibling names in natural order (dose {@code (1)}→{@code (4)}, or
	 *         raw→cooked, the cooked step offering a bare and a {@code Cooked }-prefixed
	 *         candidate), or an empty list when {@code name} carries no recognised family
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
		else if (lower.startsWith(COOKED_PREFIX))
			core = lower.substring(COOKED_PREFIX.length()).trim();
		else if (lower.startsWith(BURNT_PREFIX))
			core = lower.substring(BURNT_PREFIX.length()).trim();

		if (core == null || core.isEmpty())
			return Collections.emptyList();

		List<String> chain = new ArrayList<>(3);
		chain.add(RAW_PREFIX + core);
		chain.add(core);
		chain.add(COOKED_PREFIX + core);
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
