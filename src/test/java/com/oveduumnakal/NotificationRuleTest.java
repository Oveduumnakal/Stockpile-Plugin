/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.OptionalDouble;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests for {@link NotificationRule}'s threshold parsing and formatting: k/m/b
 * suffixes, comma grouping, percent handling (explicit {@code %} and bare
 * fractions), and unparseable input.
 */
public class NotificationRuleTest
{
	private static final double DELTA = 1e-9;

	@Test
	public void parseNumericAcceptsPlainAndGroupedDigits()
	{
		assertEquals(500, NotificationRule.parseNumeric("500").getAsDouble(), DELTA);
		assertEquals(1_500, NotificationRule.parseNumeric("1,500").getAsDouble(), DELTA);
	}

	@Test
	public void parseNumericAcceptsSuffixes()
	{
		assertEquals(5_000, NotificationRule.parseNumeric("5k").getAsDouble(), DELTA);
		assertEquals(5_000_000, NotificationRule.parseNumeric("5M").getAsDouble(), DELTA);
		assertEquals(1_000_000_000, NotificationRule.parseNumeric("1b").getAsDouble(), DELTA);
		assertEquals(2_500, NotificationRule.parseNumeric("2.5k").getAsDouble(), DELTA);
		assertEquals(-5_000, NotificationRule.parseNumeric("-5k").getAsDouble(), DELTA);
	}

	@Test
	public void parseNumericRejectsBlankAndGarbage()
	{
		assertFalse(NotificationRule.parseNumeric(null).isPresent());
		assertFalse(NotificationRule.parseNumeric("").isPresent());
		assertFalse(NotificationRule.parseNumeric("   ").isPresent());
		assertFalse(NotificationRule.parseNumeric("abc").isPresent());
	}

	@Test
	public void parsePercentAcceptsExplicitPercent()
	{
		assertEquals(5, NotificationRule.parsePercent("5%").getAsDouble(), DELTA);
		assertEquals(1.5, NotificationRule.parsePercent("1.5%").getAsDouble(), DELTA);
	}

	@Test
	public void parsePercentTreatsBareFractionAsPercent()
	{
		assertEquals(5, NotificationRule.parsePercent("0.05").getAsDouble(), DELTA);
		assertEquals(10, NotificationRule.parsePercent("10").getAsDouble(), DELTA);
	}

	@Test
	public void parsePercentRejectsBlankAndGarbage()
	{
		assertFalse(NotificationRule.parsePercent(null).isPresent());
		assertFalse(NotificationRule.parsePercent("").isPresent());
		assertFalse(NotificationRule.parsePercent("abc%").isPresent());
	}

	@Test
	public void formatPercentDropsDecimalForWholeValues()
	{
		assertEquals("5%", NotificationRule.formatPercent(5.0));
		assertEquals("2.5%", NotificationRule.formatPercent(2.5));
		assertEquals("0%", NotificationRule.formatPercent(0.0));
	}

	@Test
	public void parseThenFormatRoundTrips()
	{
		OptionalDouble parsed = NotificationRule.parsePercent("2.5%");
		assertEquals("2.5%", NotificationRule.formatPercent(parsed.getAsDouble()));
	}
}
