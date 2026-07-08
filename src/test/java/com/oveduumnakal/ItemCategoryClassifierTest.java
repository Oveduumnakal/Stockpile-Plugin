/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** Spot-checks the rule table and fallback behaviour of {@link ItemCategoryClassifier}. */
public class ItemCategoryClassifierTest
{
	@Test
	public void classifiesCommonItems()
	{
		assertEquals("Runes", ItemCategoryClassifier.classify("Nature rune"));
		assertEquals("Runes", ItemCategoryClassifier.classify("Pure essence"));
		assertEquals("Seeds", ItemCategoryClassifier.classify("Ranarr seed"));
		assertEquals("Potions", ItemCategoryClassifier.classify("Prayer potion(4)"));
		assertEquals("Herbs", ItemCategoryClassifier.classify("Grimy ranarr weed"));
		assertEquals("Logs", ItemCategoryClassifier.classify("Magic logs"));
		assertEquals("Ores & Bars", ItemCategoryClassifier.classify("Runite ore"));
		assertEquals("Ores & Bars", ItemCategoryClassifier.classify("Adamantite bar"));
		assertEquals("Ammo", ItemCategoryClassifier.classify("Dragon arrow"));
		assertEquals("Food", ItemCategoryClassifier.classify("Raw shark"));
		assertEquals("Weapons", ItemCategoryClassifier.classify("Abyssal whip"));
		assertEquals("Armour", ItemCategoryClassifier.classify("Rune platebody"));
		assertEquals("Gems", ItemCategoryClassifier.classify("Uncut diamond"));
		assertEquals("Bones", ItemCategoryClassifier.classify("Dragon bones"));
		assertEquals("Hunter", ItemCategoryClassifier.classify("Red chinchompa"));
	}

	@Test
	public void unmatchedFallsToOther()
	{
		assertEquals("Other", ItemCategoryClassifier.classify("Coins"));
		assertEquals("Other", ItemCategoryClassifier.classify("Clue scroll"));
	}

	@Test
	public void nullAndBlankAreOther()
	{
		assertEquals("Other", ItemCategoryClassifier.classify(null));
		assertEquals("Other", ItemCategoryClassifier.classify("   "));
	}

	/** "Rune platebody" ends in neither " rune" nor " runes", so it lands in Armour, not Runes. */
	@Test
	public void moreSpecificRuleWinsOverBroaderOne()
	{
		assertEquals("Armour", ItemCategoryClassifier.classify("Rune platebody"));
	}
}
