/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Verifies the variant-family name resolution of {@link VariantFamily}. */
public class VariantFamilyTest
{
	@Test
	public void doseFamilyListsAllFourDosesInOrder()
	{
		List<String> siblings = VariantFamily.siblingNames("Prayer potion(2)");
		assertEquals(Arrays.asList(
				"prayer potion(1)", "prayer potion(2)", "prayer potion(3)", "prayer potion(4)"), siblings);
	}

	@Test
	public void cookingChainFiresOnARawName()
	{
		List<String> siblings = VariantFamily.siblingNames("Raw lobster");
		assertEquals(Arrays.asList("raw lobster", "lobster", "cooked lobster"), siblings);
	}

	@Test
	public void cookingChainFiresOnABurntName()
	{
		List<String> siblings = VariantFamily.siblingNames("Burnt shark");
		assertEquals(Arrays.asList("raw shark", "shark", "cooked shark"), siblings);
	}

	@Test
	public void bareCookedNameIsNotAFamily()
	{
		assertTrue(VariantFamily.siblingNames("Lobster").isEmpty());
		assertFalse(VariantFamily.hasFamily("Lobster"));
	}

	@Test
	public void unrelatedItemsHaveNoFamily()
	{
		assertFalse(VariantFamily.hasFamily("Abyssal whip"));
		assertFalse(VariantFamily.hasFamily("Coins"));
		assertFalse(VariantFamily.hasFamily("Ring of dueling(8)"));
		assertFalse(VariantFamily.hasFamily(null));
	}

	@Test
	public void hasFamilyMatchesSiblingResolution()
	{
		assertTrue(VariantFamily.hasFamily("Saradomin brew(1)"));
		assertTrue(VariantFamily.hasFamily("Raw swordfish"));
	}

	@Test
	public void cookingChainOffersACookedPrefixedCandidate()
	{
		assertEquals(
				Arrays.asList("raw pyre fox", "pyre fox", "cooked pyre fox"),
				VariantFamily.siblingNames("Raw pyre fox"));
	}

	@Test
	public void cookingChainFiresOnACookedPrefixedName()
	{
		assertEquals(
				Arrays.asList("raw pyre fox", "pyre fox", "cooked pyre fox"),
				VariantFamily.siblingNames("Cooked pyre fox"));
		assertTrue(VariantFamily.hasFamily("Cooked pyre fox"));
	}

	@Test
	public void everyEntryPointYieldsTheSameChain()
	{
		List<String> fromRaw = VariantFamily.siblingNames("Raw sunlight antelope");
		assertEquals(fromRaw, VariantFamily.siblingNames("Cooked sunlight antelope"));
		assertEquals(fromRaw, VariantFamily.siblingNames("Burnt sunlight antelope"));
	}

	@Test
	public void chainKeepsRawBeforeCooked()
	{
		List<String> siblings = VariantFamily.siblingNames("Raw karambwan");
		assertEquals(0, siblings.indexOf("raw karambwan"));
		assertTrue(siblings.indexOf("cooked karambwan") > siblings.indexOf("raw karambwan"));
	}

	@Test
	public void noBurntNameIsEverEmitted()
	{
		for (String entry : Arrays.asList("Raw lobster", "Cooked pyre fox", "Burnt shark"))
		{
			for (String sibling : VariantFamily.siblingNames(entry))
				assertFalse(entry + " emitted a burnt name", sibling.startsWith("burnt "));
		}
	}

	@Test
	public void aCookedPrefixIsNeededForACookedNameToCount()
	{
		assertFalse(VariantFamily.hasFamily("Cooked"));
		assertFalse(VariantFamily.hasFamily("Cookedmeat"));
	}
}
