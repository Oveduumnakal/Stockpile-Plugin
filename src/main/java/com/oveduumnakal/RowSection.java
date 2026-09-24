/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * One display section of the tracked list (#275): an optional group header ({@code title}/{@code key}/
 * {@code collapsed}) and its filtered items. A flat, header-less list is a single section with a null title.
 *
 * <p>Also owns how the list is split into sections and the structural signature the panel uses to decide
 * between a full rebuild and an in-place refresh. Both were private to {@code StockpilePanel}; they live
 * here, free of Swing, so they can be tested directly (#391).
 */
final class RowSection
{
	/** Header text for the favorites pseudo-group. */
	static final String FAVORITES_TITLE = "★ Favorites";

	/** Header text for items in no (or an unknown) category. */
	static final String UNCATEGORIZED_TITLE = "Uncategorized";

	final String title;
	final String key;
	final boolean collapsed;
	final List<TrackedItem> items;

	RowSection(String title, String key, boolean collapsed, List<TrackedItem> items)
	{
		this.title = title;
		this.key = key;
		this.collapsed = collapsed;
		this.items = items;
	}

	/** @return whether the list is grouped: any favorite item, or any user category defined. */
	static boolean groupingActive(List<TrackedItem> items, List<CategoryState> categories)
	{
		return !categories.isEmpty() || items.stream().anyMatch(TrackedItem::isFavorite);
	}

	/**
	 * Computes the ordered, filtered display sections: a single flat section when no grouping is active,
	 * otherwise the Favorites pseudo-group (pinned on top), each user category in order, then Uncategorized.
	 * A favorite shows only under Favorites; an item whose category no longer exists falls to Uncategorized.
	 * Empty groups are skipped.
	 *
	 * @param filter the tracked-list name filter; items it rejects are left out of every section
	 */
	static List<RowSection> plan(List<TrackedItem> items, List<CategoryState> categories,
			boolean favoritesCollapsed, boolean uncategorizedCollapsed, Predicate<TrackedItem> filter)
	{
		List<RowSection> sections = new ArrayList<>();

		if (!groupingActive(items, categories))
		{
			List<TrackedItem> visible = new ArrayList<>();
			for (TrackedItem item : items)
				if (filter.test(item))
					visible.add(item);

			if (!visible.isEmpty())
				sections.add(new RowSection(null, null, false, visible));

			return sections;
		}

		Set<String> categoryNames = new HashSet<>();
		for (CategoryState cat : categories)
			categoryNames.add(cat.getName());

		List<TrackedItem> favorites = new ArrayList<>();
		for (TrackedItem item : items)
			if (item.isFavorite() && filter.test(item))
				favorites.add(item);

		if (!favorites.isEmpty())
			sections.add(new RowSection(FAVORITES_TITLE, CategoryState.FAVORITES_KEY, favoritesCollapsed, favorites));

		for (CategoryState cat : categories)
		{
			List<TrackedItem> inCategory = new ArrayList<>();
			for (TrackedItem item : items)
				if (!item.isFavorite() && cat.getName().equals(item.getCategory()) && filter.test(item))
					inCategory.add(item);

			if (!inCategory.isEmpty())
				sections.add(new RowSection(cat.getName(), cat.getName(), cat.isCollapsed(), inCategory));
		}

		List<TrackedItem> uncategorized = new ArrayList<>();
		for (TrackedItem item : items)
		{
			String cat = item.getCategory();
			boolean uncat = cat == null || cat.isEmpty() || !categoryNames.contains(cat);
			if (!item.isFavorite() && uncat && filter.test(item))
				uncategorized.add(item);
		}

		if (!uncategorized.isEmpty())
			sections.add(new RowSection(UNCATEGORIZED_TITLE, CategoryState.UNCATEGORIZED_KEY, uncategorizedCollapsed,
					uncategorized));

		return sections;
	}

	/**
	 * @param globals the render-wide scaffolding flags, already encoded by the caller
	 * @param compactView whether the whole list is in compact view
	 * @return a signature of the render's structure (the globals, group order/collapse, and each rendered
	 *         row's id and compact shape) for the in-place gate (#275); value-only data such as prices,
	 *         quantities, deltas and group totals is excluded so it can be refreshed in place. A collapsed
	 *         group contributes its header only.
	 */
	static String signature(String globals, List<RowSection> sections, boolean compactView)
	{
		StringBuilder sb = new StringBuilder(globals);
		for (RowSection s : sections)
		{
			sb.append(";H:")
					.append(s.title)
					.append(':')
					.append(s.key)
					.append(':')
					.append(s.collapsed);
			if (s.collapsed)
				continue;

			for (TrackedItem item : s.items)
				sb.append(";I:").append(item.getItemId())
						.append(':').append(compactView || item.isCompact() ? 1 : 0);
		}

		return sb.toString();
	}
}
