/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/** Verifies the invested cost basis and running value building blocks used by the portfolio chart. */
public class TrackedItemTest
{
	private TrackedItem itemWith(int qty, long avg, AcquisitionRecord... lots)
	{
		TrackedItem t = new TrackedItem(560, "Nature rune");
		t.setQuantity(qty);
		t.setAvgPrice(avg);
		t.setAcquisitions(Arrays.asList(lots));
		return t;
	}

	@Test
	public void heldLotsMarkToMarket()
	{
		TrackedItem t = itemWith(100, 125, new AcquisitionRecord(100, 100, null));
		assertEquals(10_000, t.getInvestedCostBasis());
		assertEquals(0, t.getRealizedProceeds());
		assertEquals("100 held @125", 12_500, t.getAvgValue() + t.getRealizedProceeds());
	}

	@Test
	public void sellingAtALossKeepsCostBasisButDropsValue()
	{
		TrackedItem t = itemWith(50, 125,
				new AcquisitionRecord(50, 100, 50L),
				new AcquisitionRecord(50, 100, null));

		assertEquals("invested cost basis stays fixed across the sale", 10_000, t.getInvestedCostBasis());
		assertEquals(2_500, t.getRealizedProceeds());

		long value = t.getAvgValue() + t.getRealizedProceeds();
		assertEquals("50 held @125 + 50 sold @50", 8_750, value);
		assertEquals("value minus invested cost equals total P/L",
				t.getProfitAt(125), value - t.getInvestedCostBasis());
	}

	@Test
	public void totalSuspendedSumsEverySource()
	{
		TrackedItem t = itemWith(40, 125, new AcquisitionRecord(100, 100, null));
		t.setSuspended(SuspensionSource.SELL, 25);
		t.setSuspended(SuspensionSource.TRADE, 15);
		t.setSuspended(SuspensionSource.GROUND, 12);
		t.setSuspended(SuspensionSource.DEATH, 8);

		assertEquals(60, t.getTotalSuspendedQuantity());
		assertEquals("open lots cover held plus suspended units",
				t.getRecordQuantitySum(), t.getQuantity() + t.getTotalSuspendedQuantity());
	}

	@Test
	public void suspendingUnitsIsValueNeutral()
	{
		TrackedItem t = itemWith(100, 125, new AcquisitionRecord(100, 100, null));
		long before = t.getAvgValue() + t.getSuspendedValue() + t.getRealizedProceeds();

		t.setQuantity(40);
		t.setSuspended(SuspensionSource.SELL, 60);
		long during = t.getAvgValue() + t.getSuspendedValue() + t.getRealizedProceeds();

		assertEquals(7_500, t.getSuspendedValue());
		assertEquals("moving units into an offer must not dent the running value", before, during);
	}

	@Test
	public void hydratedPricesAreNotLiveSessionPrices()
	{
		TrackedItem t = itemWith(100, 125, new AcquisitionRecord(100, 100, null));
		t.setLowPrice(120);
		t.setHighPrice(130);
		t.setPriceCacheHydrated(true);

		assertEquals(true, t.hasPrices());
		assertEquals(false, t.hasLivePrices());

		t.setPriceCacheHydrated(false);

		assertEquals(true, t.hasLivePrices());
	}

	@Test
	public void realizeSourcesCarryNoTimestamp()
	{
		TrackedItem t = itemWith(0, 125);
		t.addSuspended(SuspensionSource.SELL, 5);
		t.addSuspended(SuspensionSource.TRADE, 3);
		t.addSuspended(SuspensionSource.POUCH, 2);

		assertEquals(5, t.getSuspended(SuspensionSource.SELL));
		assertNull("sell has no expiry sweep, so no timestamp", t.getSuspendedAt(SuspensionSource.SELL));
		assertNull(t.getSuspendedAt(SuspensionSource.TRADE));
		assertNull(t.getSuspendedAt(SuspensionSource.POUCH));
	}

	@Test
	public void groundAndDeathStampATimestamp()
	{
		TrackedItem t = itemWith(0, 125);
		t.addSuspended(SuspensionSource.GROUND, 4);
		t.addSuspended(SuspensionSource.DEATH, 6);

		assertEquals(Instant.class, t.getSuspendedAt(SuspensionSource.GROUND).getClass());
		assertEquals(Instant.class, t.getSuspendedAt(SuspensionSource.DEATH).getClass());
	}

	@Test
	public void deathStampsOnceAcrossAdditions()
	{
		TrackedItem t = itemWith(0, 125);
		t.addSuspended(SuspensionSource.DEATH, 5);
		Instant first = t.getSuspendedAt(SuspensionSource.DEATH);

		t.addSuspended(SuspensionSource.DEATH, 3);

		assertEquals("a second death can't reset the first's recovery clock (#168)",
				8, t.getSuspended(SuspensionSource.DEATH));
		assertSame("the timestamp is untouched by the later addition", first,
				t.getSuspendedAt(SuspensionSource.DEATH));
	}

	@Test
	public void reduceCapsAtAvailableAndClearsTimestampWhenEmptied()
	{
		TrackedItem t = itemWith(0, 125);
		t.addSuspended(SuspensionSource.DEATH, 5);

		assertEquals("reduce returns only what was there", 5, t.reduceSuspended(SuspensionSource.DEATH, 8));
		assertEquals(0, t.getSuspended(SuspensionSource.DEATH));
		assertNull("emptying clears the timestamp", t.getSuspendedAt(SuspensionSource.DEATH));

		t.addSuspended(SuspensionSource.DEATH, 2);
		assertEquals("a fresh suspension re-stamps", Instant.class,
				t.getSuspendedAt(SuspensionSource.DEATH).getClass());
	}

	@Test
	public void restoreSeedsQuantityAndSavedTimestamp()
	{
		TrackedItem t = itemWith(0, 125);
		Instant saved = Instant.ofEpochSecond(1_700_000_000L);
		t.restoreSuspended(SuspensionSource.DEATH, 4, saved);

		assertEquals(4, t.getSuspended(SuspensionSource.DEATH));
		assertSame("restore resumes the saved clock, not now()", saved,
				t.getSuspendedAt(SuspensionSource.DEATH));

		t.restoreSuspended(SuspensionSource.DEATH, 0, saved);
		assertEquals("a non-positive restore clears the entry", 0, t.getSuspended(SuspensionSource.DEATH));
	}

	@Test
	public void nonPositiveSuspensionsAreIgnored()
	{
		TrackedItem t = itemWith(0, 125);

		t.addSuspended(SuspensionSource.GROUND, 0);
		t.addSuspended(SuspensionSource.GROUND, -3);

		assertEquals(0, t.getSuspended(SuspensionSource.GROUND));
		assertNull("nothing suspended, nothing stamped", t.getSuspendedAt(SuspensionSource.GROUND));
		assertEquals(0, t.reduceSuspended(SuspensionSource.GROUND, 5));
		t.addSuspended(SuspensionSource.GROUND, 4);
		assertEquals("a non-positive reduce restores nothing", 0, t.reduceSuspended(SuspensionSource.GROUND, 0));
		assertEquals(4, t.getSuspended(SuspensionSource.GROUND));
	}

	@Test
	public void settingOrRestoringZeroDropsTheSuspension()
	{
		TrackedItem t = itemWith(0, 125);
		t.addSuspended(SuspensionSource.DEATH, 3);
		t.addSuspended(SuspensionSource.POUCH, 2);

		t.setSuspended(SuspensionSource.DEATH, 0);
		t.restoreSuspended(SuspensionSource.POUCH, 0, Instant.now());

		assertEquals(0, t.getTotalSuspendedQuantity());
		assertNull(t.getSuspendedAt(SuspensionSource.DEATH));
	}

	@Test
	public void eachWindowReadsItsSamplingSeries()
	{
		TrackedItem t = itemWith(0, 125);
		List<WikiRealtimePriceClient.PricePoint> fiveMin = new ArrayList<>();
		List<WikiRealtimePriceClient.PricePoint> hour = new ArrayList<>();
		List<WikiRealtimePriceClient.PricePoint> sixHour = new ArrayList<>();
		List<WikiRealtimePriceClient.PricePoint> day = new ArrayList<>();
		t.setSeries5m(fiveMin);
		t.setSeries1h(hour);
		t.setSeries6h(sixHour);
		t.setSeries24h(day);

		assertSame(fiveMin, t.getSeriesFor(TimeWindow.LIVE));
		assertSame(fiveMin, t.getSeriesFor(TimeWindow.H24));
		assertSame(hour, t.getSeriesFor(TimeWindow.WEEK));
		assertSame(sixHour, t.getSeriesFor(TimeWindow.MONTH));
		assertSame(day, t.getSeriesFor(TimeWindow.MONTH3));
		assertSame(day, t.getSeriesFor(TimeWindow.MONTH6));
		assertSame(day, t.getSeriesFor(TimeWindow.YEAR));
	}

	@Test
	public void onlyStackableItemsDrawTheirQuantityOnTheIcon()
	{
		TrackedItem t = itemWith(250, 125);

		assertEquals(1, t.iconStackSize());

		t.setStackable(true);
		assertEquals(250, t.iconStackSize());

		t.setQuantity(0);
		assertEquals("an empty stack still draws one", 1, t.iconStackSize());
	}
}
