/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link MarketMath}: the Grand Exchange sell tax's exemption, rate and cap, the
 * high/low alchemy profit formulas including their fire-rune counts, and percent change with
 * its one-decimal rounding and non-positive guard.
 */
public class MarketMathTest
{
	/** Prices below 50 gp are exempt from the sell tax entirely. */
	@Test
	public void geTaxIsWaivedBelowFiftyGp()
	{
		assertEquals(0, MarketMath.geTax(0));
		assertEquals(0, MarketMath.geTax(1));
		assertEquals(0, MarketMath.geTax(49));
	}

	/** At and above 50 gp the tax is 2% of the price, rounded down. */
	@Test
	public void geTaxIsTwoPercentRoundedDown()
	{
		assertEquals(1, MarketMath.geTax(50));
		assertEquals(2, MarketMath.geTax(100));
		assertEquals(20, MarketMath.geTax(1000));
		assertEquals(1, MarketMath.geTax(99));
	}

	/** The tax stops growing at 5M gp per item, however expensive the item is. */
	@Test
	public void geTaxIsCappedAtFiveMillion()
	{
		assertEquals(5_000_000L, MarketMath.geTax(250_000_000L));
		assertEquals(4_999_999L, MarketMath.geTax(249_999_999L));
		assertEquals(5_000_000L, MarketMath.geTax(2_000_000_000L));
	}

	/** A negative price cannot reach the exemption threshold, so it is untaxed. */
	@Test
	public void geTaxOnANegativePriceIsZero()
	{
		assertEquals(0, MarketMath.geTax(-100));
	}

	/** High alch subtracts the item, one nature rune and five fire runes from the alch value. */
	@Test
	public void highAlchProfitSubtractsItemAndRuneCost()
	{
		assertEquals(1200 - 800 - 100 - 5 * 4, MarketMath.highAlchProfit(1200, 800, 100, 4));
		assertEquals(5, MarketMath.HIGH_ALCH_FIRE_RUNES);
	}

	/** Low alch uses three fire runes rather than five. */
	@Test
	public void lowAlchProfitUsesThreeFireRunes()
	{
		assertEquals(600 - 400 - 100 - 3 * 4, MarketMath.lowAlchProfit(600, 400, 100, 4));
		assertEquals(3, MarketMath.LOW_ALCH_FIRE_RUNES);
	}

	/** An item costing more than it alchs for yields a negative profit rather than clamping to zero. */
	@Test
	public void alchProfitCanBeNegative()
	{
		assertEquals(-100, MarketMath.highAlchProfit(1000, 1000, 100, 0));
		assertEquals(-500, MarketMath.lowAlchProfit(500, 900, 100, 0));
	}

	/** With free runes the profit is simply the alch value less the item cost. */
	@Test
	public void alchProfitWithZeroRunePricesIsAlchLessItem()
	{
		assertEquals(400, MarketMath.highAlchProfit(1200, 800, 0, 0));
		assertEquals(200, MarketMath.lowAlchProfit(600, 400, 0, 0));
	}

	/** A rise, a fall and a flat price all report against the baseline. */
	@Test
	public void changePctReportsSignedChange()
	{
		assertEquals(10.0, MarketMath.changePct(110, 100), 0.0001);
		assertEquals(-10.0, MarketMath.changePct(90, 100), 0.0001);
		assertEquals(0.0, MarketMath.changePct(100, 100), 0.0001);
	}

	/** The result is rounded to one decimal place. */
	@Test
	public void changePctRoundsToOneDecimal()
	{
		assertEquals(0.3, MarketMath.changePct(1003, 1000), 0.0001);
		assertEquals(33.3, MarketMath.changePct(4, 3), 0.0001);
		assertEquals(-66.7, MarketMath.changePct(1, 3), 0.0001);
	}

	/** A non-positive current or baseline price has no meaningful percent change. */
	@Test
	public void changePctIsNaNForNonPositiveInput()
	{
		assertTrue(Double.isNaN(MarketMath.changePct(0, 100)));
		assertTrue(Double.isNaN(MarketMath.changePct(100, 0)));
		assertTrue(Double.isNaN(MarketMath.changePct(-5, 100)));
		assertTrue(Double.isNaN(MarketMath.changePct(100, -5)));
	}
}
