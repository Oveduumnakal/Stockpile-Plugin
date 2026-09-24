/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for {@link CategoryState#sanitizeName}: the cleaning every typed or imported category name
 * goes through before it is stored and rendered (#375).
 */
public class CategoryStateTest
{
	@Test
	public void htmlCannotSurviveIntoAName()
	{
		String cleaned = CategoryState.sanitizeName("<html><img src='http://example.com/x.png'>Loot");

		assertEquals("htmlimg src='http://example.com/x.png'Lo", cleaned);
		assertEquals("no angle bracket is left for Swing to parse", -1, cleaned.indexOf('<'));
	}

	@Test
	public void whitespaceAndControlCharactersAreTidied()
	{
		assertEquals("Herbs and Seeds", CategoryState.sanitizeName("  Herbs \t and\u0000 \n Seeds  "));
	}

	@Test
	public void longNamesAreCapped()
	{
		String name = CategoryState.sanitizeName("x".repeat(500));

		assertEquals(CategoryState.MAX_NAME_LENGTH, name.length());
	}

	@Test
	public void nothingUsableMeansNull()
	{
		assertNull(CategoryState.sanitizeName(null));
		assertNull(CategoryState.sanitizeName("   "));
		assertNull(CategoryState.sanitizeName("<<>>"));
	}
}
