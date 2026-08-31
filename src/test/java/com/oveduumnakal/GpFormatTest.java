/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link GpFormat}: compact K/M/B abbreviation, single-decimal
 * narrowing, full grouped formatting including sign handling, and that grouping
 * survives concurrent use (#326 - the panel formats on the EDT while overlays
 * format on the client thread).
 */
public class GpFormatTest
{
	@Test
	public void shortValueKeepsSmallValuesAsGroupedDigits()
	{
		assertEquals("0", GpFormat.shortValue(0));
		assertEquals("999", GpFormat.shortValue(999));
		assertEquals("-5", GpFormat.shortValue(-5));
	}

	@Test
	public void shortValueAbbreviatesToThreeSignificantFigures()
	{
		assertEquals("1K", GpFormat.shortValue(1_000));
		assertEquals("1.5K", GpFormat.shortValue(1_500));
		assertEquals("2.34K", GpFormat.shortValue(2_340));
		assertEquals("12.3K", GpFormat.shortValue(12_340));
		assertEquals("234K", GpFormat.shortValue(234_000));
		assertEquals("1M", GpFormat.shortValue(1_000_000));
		assertEquals("1.23M", GpFormat.shortValue(1_230_000));
		assertEquals("2.1B", GpFormat.shortValue(2_100_000_000L));
	}

	@Test
	public void shortValueKeepsNegativeSign()
	{
		assertEquals("-1.5K", GpFormat.shortValue(-1_500));
		assertEquals("-2.1B", GpFormat.shortValue(-2_100_000_000L));
	}

	@Test
	public void shortValue1dpCapsMantissaToOneDecimal()
	{
		assertEquals("2.3K", GpFormat.shortValue1dp(2_340));
		assertEquals("1.5K", GpFormat.shortValue1dp(1_500));
		assertEquals("950", GpFormat.shortValue1dp(950));
	}

	@Test
	public void gpSuffixVariants()
	{
		assertEquals("1.5K gp", GpFormat.shortGp(1_500));
		assertEquals("1,234,567 gp", GpFormat.fullGp(1_234_567));
	}

	@Test
	public void abbreviationDropsTrailingZeros()
	{
		assertEquals("2K", GpFormat.shortValue(2_000));
		assertEquals("2.5M", GpFormat.shortValue(2_500_000));
	}

	@Test
	public void signedShortAddsPlusOnlyForPositives()
	{
		assertEquals("+1.5K", GpFormat.signedShort(1_500));
		assertEquals("+2.1B", GpFormat.signedShort(2_100_000_000L));
		assertEquals("-350K", GpFormat.signedShort(-350_000));
		assertEquals("0", GpFormat.signedShort(0));
	}

	@Test
	public void signedGroupedAddsPlusOnlyForPositives()
	{
		assertEquals("+1,234", GpFormat.signedGrouped(1_234));
		assertEquals("-1,234", GpFormat.signedGrouped(-1_234));
		assertEquals("0", GpFormat.signedGrouped(0));
	}

	/**
	 * The overlay render loop and the panel rebuild call these from different threads. A shared
	 * {@code NumberFormat} would garble digits between calls or throw out of {@code DigitList};
	 * every thread must see exactly what a single-threaded call produces.
	 */
	@Test
	public void groupingIsSafeUnderConcurrentUse() throws Exception
	{
		final long value = 1_234_567_890L;
		final String expected = GpFormat.grouped(value);
		ExecutorService pool = Executors.newFixedThreadPool(8);
		try
		{
			List<Callable<String>> work = new ArrayList<>();
			for (int i = 0; i < 8; i++)
			{
				work.add(() ->
				{
					StringBuilder mismatch = new StringBuilder();
					for (int n = 0; n < 20_000; n++)
					{
						String actual = GpFormat.grouped(value);
						if (!expected.equals(actual))
							mismatch.append(actual).append(' ');

						GpFormat.fullGp(value);
						GpFormat.signedGrouped(value);
						GpFormat.shortValue(n);
					}

					return mismatch.toString();
				});
			}

			for (Future<String> result : pool.invokeAll(work))
				assertEquals("", result.get());
		}
		finally
		{
			pool.shutdown();
			pool.awaitTermination(30, TimeUnit.SECONDS);
		}
	}
}
