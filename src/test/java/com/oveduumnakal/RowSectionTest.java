/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link RowSection} (#391): how the tracked list splits into Favorites, categories and
 * Uncategorized, how the name filter and collapse state apply, and when the structural signature
 * changes - which decides whether the panel rebuilds its rows or refreshes them in place (#275).
 */
public class RowSectionTest
{
	private static final Predicate<TrackedItem> ALL = item -> true;

	private static TrackedItem item(int id, String name, String category, boolean favorite)
	{
		TrackedItem item = new TrackedItem(id, name);
		item.setCategory(category);
		item.setFavorite(favorite);
		return item;
	}

	private static List<CategoryState> categories(String... names)
	{
		List<CategoryState> list = new ArrayList<>();
		for (String name : names)
			list.add(new CategoryState(name, false));

		return list;
	}

	private static List<String> titles(List<RowSection> sections)
	{
		List<String> titles = new ArrayList<>();
		for (RowSection s : sections)
			titles.add(s.title);

		return titles;
	}

	private static List<Integer> ids(RowSection section)
	{
		List<Integer> ids = new ArrayList<>();
		for (TrackedItem item : section.items)
			ids.add(item.getItemId());

		return ids;
	}

	@Test
	public void withNoFavoritesOrCategoriesTheListIsOneFlatSection()
	{
		List<TrackedItem> items = List.of(item(1, "Coal", null, false), item(2, "Iron ore", "Ores", false));

		List<RowSection> sections = RowSection.plan(items, categories(), false, false, ALL);

		assertFalse(RowSection.groupingActive(items, categories()));
		assertEquals(1, sections.size());
		assertNull("a flat list has no header", sections.get(0).title);
		assertEquals(List.of(1, 2), ids(sections.get(0)));
	}

	@Test
	public void groupsRunFavoritesThenCategoriesInOrderThenUncategorized()
	{
		List<TrackedItem> items = List.of(item(1, "Coal", "Ores", false), item(2, "Lobster", "Food", true),
				item(3, "Shark", "Food", false), item(4, "Rune", null, false), item(5, "Iron ore", "Ores", false));

		List<RowSection> sections = RowSection.plan(items, categories("Food", "Ores"), false, false, ALL);

		assertEquals(List.of(RowSection.FAVORITES_TITLE, "Food", "Ores", RowSection.UNCATEGORIZED_TITLE),
				titles(sections));
		assertEquals("a favorite shows only under Favorites", List.of(2), ids(sections.get(0)));
		assertEquals(List.of(3), ids(sections.get(1)));
		assertEquals("items keep their list order within a group", List.of(1, 5), ids(sections.get(2)));
		assertEquals(List.of(4), ids(sections.get(3)));
		assertEquals(CategoryState.FAVORITES_KEY, sections.get(0).key);
		assertEquals("Food", sections.get(1).key);
		assertEquals(CategoryState.UNCATEGORIZED_KEY, sections.get(3).key);
	}

	@Test
	public void aFavoriteAloneTurnsGroupingOn()
	{
		List<TrackedItem> items = List.of(item(1, "Coal", null, true), item(2, "Iron ore", null, false));

		List<RowSection> sections = RowSection.plan(items, categories(), false, false, ALL);

		assertTrue(RowSection.groupingActive(items, categories()));
		assertEquals(List.of(RowSection.FAVORITES_TITLE, RowSection.UNCATEGORIZED_TITLE), titles(sections));
	}

	@Test
	public void emptyGroupsAreSkippedAndUnknownCategoriesFallToUncategorized()
	{
		List<TrackedItem> items = List.of(item(1, "Coal", "Deleted", false), item(2, "Iron ore", "", false));

		List<RowSection> sections = RowSection.plan(items, categories("Food"), false, false, ALL);

		assertEquals(List.of(RowSection.UNCATEGORIZED_TITLE), titles(sections));
		assertEquals(List.of(1, 2), ids(sections.get(0)));
	}

	@Test
	public void theFilterDropsItemsAndAnyGroupItEmpties()
	{
		List<TrackedItem> items = List.of(item(1, "Coal", "Ores", true), item(2, "Iron ore", "Ores", false),
				item(3, "Shark", "Food", false));
		Predicate<TrackedItem> ore = item -> item.getName().contains("ore");

		List<RowSection> grouped = RowSection.plan(items, categories("Food", "Ores"), false, false, ore);
		List<RowSection> flat = RowSection.plan(List.of(item(3, "Shark", null, false)), categories(), false, false,
				ore);

		assertEquals(List.of("Ores"), titles(grouped));
		assertEquals(List.of(2), ids(grouped.get(0)));
		assertTrue("a flat list the filter empties has no sections", flat.isEmpty());
	}

	@Test
	public void collapseStateComesFromEachGroupsOwnFlag()
	{
		List<CategoryState> cats = List.of(new CategoryState("Food", true), new CategoryState("Ores", false));
		List<TrackedItem> items = List.of(item(1, "Coal", "Ores", false), item(2, "Shark", "Food", false),
				item(3, "Rune", null, true), item(4, "Air", null, false));

		List<RowSection> sections = RowSection.plan(items, cats, true, false, ALL);

		assertTrue(sections.get(0).collapsed);
		assertTrue(sections.get(1).collapsed);
		assertFalse(sections.get(2).collapsed);
		assertFalse(sections.get(3).collapsed);
	}

	@Test
	public void theSignatureIgnoresValuesButTracksStructure()
	{
		TrackedItem coal = item(1, "Coal", "Ores", false);
		List<RowSection> before = RowSection.plan(List.of(coal), categories("Ores"), false, false, ALL);
		String sig = RowSection.signature("G", before, false);

		coal.setQuantity(500);
		coal.setAvgPrice(150);
		assertEquals("a value change refreshes in place", sig, RowSection.signature("G", before, false));

		coal.setCompact(true);
		assertNotEquals("a row switching to compact rebuilds", sig, RowSection.signature("G", before, false));
		coal.setCompact(false);

		assertNotEquals("the globals are part of it", sig, RowSection.signature("G1", before, false));
		assertNotEquals("so is list-wide compact view", sig, RowSection.signature("G", before, true));

		List<RowSection> moreRows = RowSection.plan(List.of(coal, item(2, "Iron ore", "Ores", false)),
				categories("Ores"), false, false, ALL);
		assertNotEquals("a new row rebuilds", sig, RowSection.signature("G", moreRows, false));
	}

	@Test
	public void aCollapsedGroupSignsOnlyItsHeader()
	{
		List<CategoryState> collapsed = List.of(new CategoryState("Ores", true));
		String one = RowSection.signature("G", RowSection.plan(List.of(item(1, "Coal", "Ores", false)), collapsed,
				false, false, ALL), false);
		String two = RowSection.signature("G", RowSection.plan(List.of(item(1, "Coal", "Ores", false),
				item(2, "Iron ore", "Ores", false)), collapsed, false, false, ALL), false);

		assertEquals("rows hidden under a collapsed header don't force a rebuild", one, two);
		assertEquals("G;H:Ores:Ores:true", one);
	}
}
