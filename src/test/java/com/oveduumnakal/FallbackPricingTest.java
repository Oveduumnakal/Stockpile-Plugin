/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/** Verifies the legacy {@code AutoAddMode} → {@link FallbackPricing} migration mapping (#219). */
public class FallbackPricingTest
{
	@Test
	public void pricingModesMapToTheirEquivalent()
	{
		assertEquals(FallbackPricing.HIGH, FallbackPricing.fromLegacyMode("HIGH"));
		assertEquals(FallbackPricing.LOW, FallbackPricing.fromLegacyMode("LOW"));
		assertEquals(FallbackPricing.AVG, FallbackPricing.fromLegacyMode("AVG"));
		assertEquals(FallbackPricing.ZERO, FallbackPricing.fromLegacyMode("ZERO"));
	}

	@Test
	public void offKeepsAutoAddOffButDefaultsPricingToAvg()
	{
		assertEquals(FallbackPricing.AVG, FallbackPricing.fromLegacyMode("OFF"));
	}

	@Test
	public void alreadyMigratedOrUnknownValuesAreLeftAlone()
	{
		assertNull(FallbackPricing.fromLegacyMode("true"));
		assertNull(FallbackPricing.fromLegacyMode("false"));
		assertNull(FallbackPricing.fromLegacyMode(null));
		assertNull(FallbackPricing.fromLegacyMode("garbage"));
	}

	@Test
	public void selectPicksTheModesPriceFromTheCandidates()
	{
		assertEquals(100, FallbackPricing.HIGH.select(100, 50, 75));
		assertEquals(50, FallbackPricing.LOW.select(100, 50, 75));
		assertEquals(75, FallbackPricing.AVG.select(100, 50, 75));
		assertEquals(0, FallbackPricing.ZERO.select(100, 50, 75));
	}

	@Test
	public void selectResolvesConsistentlyWhenOnlyOneGuidePriceIsKnown()
	{
		long guide = 42;
		assertEquals("HIGH collapses to the single guide price",
				guide, FallbackPricing.HIGH.select(guide, guide, guide));
		assertEquals("LOW collapses to the single guide price",
				guide, FallbackPricing.LOW.select(guide, guide, guide));
		assertEquals("AVG collapses to the single guide price",
				guide, FallbackPricing.AVG.select(guide, guide, guide));
		assertEquals("ZERO still seeds nothing", 0, FallbackPricing.ZERO.select(guide, guide, guide));
	}
}
