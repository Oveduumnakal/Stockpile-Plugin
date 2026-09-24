/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for {@link NotificationEvaluator}, the rule engine extracted from {@code StockpilePlugin} so it
 * can run without a client (#373).
 */
public class NotificationEvaluatorTest
{
	private final NotificationEvaluator evaluator = new NotificationEvaluator(() -> 100, () -> 5);

	private static TrackedItem item(long high, long low, long avg)
	{
		TrackedItem item = new TrackedItem(560, "Test item");
		item.setHighPrice(high);
		item.setLowPrice(low);
		item.setAvgPrice(avg);
		item.getWindowStats().put(TimeWindow.LIVE, new PriceStats(high, low, avg, 0));
		return item;
	}

	private static NotificationRule rule(NotificationMetric metric, TimeWindow window, NotificationOperation op,
			String value)
	{
		NotificationRule rule = new NotificationRule();
		rule.setMetric(metric);
		rule.setTimeWindow(window);
		rule.setOperation(op);
		rule.setValue(value);
		return rule;
	}

	@Test
	public void numericRuleComparesTheWindowReading()
	{
		TrackedItem item = item(220, 200, 210);

		assertEquals(Boolean.TRUE, evaluator.evaluate(item,
				rule(NotificationMetric.HIGH, TimeWindow.LIVE, NotificationOperation.GTE, "200")));
		assertEquals(Boolean.FALSE, evaluator.evaluate(item,
				rule(NotificationMetric.LOW, TimeWindow.LIVE, NotificationOperation.GTE, "1k")));
	}

	@Test
	public void quantityRuleReadsTheHeldQuantity()
	{
		TrackedItem item = item(0, 0, 0);
		item.setQuantity(30);

		assertEquals(Boolean.TRUE, evaluator.evaluate(item,
				rule(NotificationMetric.QUANTITY, null, NotificationOperation.LTE, "30")));
	}

	@Test
	public void categoricalRuleMatchesTheRatingCaseInsensitively()
	{
		TrackedItem item = item(220, 200, 210);
		item.getWindowStats().put(TimeWindow.H24, new PriceStats(220, 200, 210, 10_000));

		assertEquals(Boolean.TRUE, evaluator.evaluate(item,
				rule(NotificationMetric.LIQUIDITY, null, NotificationOperation.EQ, " high ")));
	}

	@Test
	public void aWindowWithNoDataCannotBeEvaluated()
	{
		assertNull("no stats means no reading, so the rule neither fires nor re-arms",
				evaluator.evaluate(item(220, 200, 210),
						rule(NotificationMetric.AVERAGE, TimeWindow.YEAR, NotificationOperation.GTE, "1")));
	}

	@Test
	public void anImplausibleDeltaIsIgnored()
	{
		TrackedItem item = item(1_000, 1_000, 1_000);
		item.getWindowStats().put(TimeWindow.WEEK, new PriceStats(1, 1, 1, 5));

		assertNull("a near-zero window average is noise, not a 99,900% move", evaluator.evaluate(item,
				rule(NotificationMetric.DELTA_PCT, TimeWindow.WEEK, NotificationOperation.GTE, "10%")));
	}

	@Test
	public void anIncompleteRuleCannotBeEvaluated()
	{
		assertNull(evaluator.evaluate(item(1, 1, 1), new NotificationRule()));
	}

	/** A sample {@code hoursAgo} old with the given average prices, for the series-backed ratings. */
	private static WikiRealtimePriceClient.PricePoint point(long hoursAgo, long avgHigh, long avgLow)
	{
		long ts = System.currentTimeMillis() / 1000L - hoursAgo * 3600L;
		return new WikiRealtimePriceClient.PricePoint(ts, avgHigh, avgLow, 0, 0);
	}

	private Boolean check(TrackedItem item, NotificationMetric metric, TimeWindow window,
			NotificationOperation op, String value)
	{
		return evaluator.evaluate(item, rule(metric, window, op, value));
	}

	@Test
	public void priceAndVolumeMetricsReadTheChosenWindow()
	{
		TrackedItem item = item(0, 0, 0);
		item.getWindowStats().put(TimeWindow.H24, new PriceStats(300, 250, 275, 4_000));

		assertEquals(Boolean.TRUE, check(item, NotificationMetric.HIGH, TimeWindow.H24, NotificationOperation.EQ,
				"300"));
		assertEquals(Boolean.TRUE, check(item, NotificationMetric.LOW, TimeWindow.H24, NotificationOperation.LT,
				"251"));
		assertEquals(Boolean.TRUE, check(item, NotificationMetric.AVERAGE, TimeWindow.H24, NotificationOperation.GT,
				"274"));
		assertEquals(Boolean.FALSE, check(item, NotificationMetric.VOLUME, TimeWindow.H24, NotificationOperation.GTE,
				"5k"));
		assertNull("no stats for that window", check(item, NotificationMetric.VOLUME, TimeWindow.WEEK,
				NotificationOperation.GTE, "1"));
	}

	@Test
	public void itemProfitMarksHeldLotsAtTheWindowAverage()
	{
		TrackedItem item = item(0, 0, 0);
		item.getAcquisitions().add(new AcquisitionRecord(10, 100, null));
		item.getWindowStats().put(TimeWindow.H24, new PriceStats(130, 110, 120, 1));

		assertEquals("10 x (120 - 100) = 200", Boolean.TRUE, check(item, NotificationMetric.ITM_PROFIT,
				TimeWindow.H24, NotificationOperation.EQ, "200"));
		assertNull("no average means no mark price", check(item, NotificationMetric.ITM_PROFIT, TimeWindow.WEEK,
				NotificationOperation.GTE, "0"));
	}

	@Test
	public void highAlchProfitNetsTheRuneCost()
	{
		TrackedItem item = item(0, 0, 0);
		item.getWindowStats().put(TimeWindow.H24, new PriceStats(1_000, 1_000, 1_000, 1));
		item.setHighAlch(1_500);

		assertEquals("1,500 - 1,000 - 100 nature - 5 x 5 fire = 375", Boolean.TRUE, check(item,
				NotificationMetric.HA_PROFIT, TimeWindow.H24, NotificationOperation.EQ, "375"));

		item.setHighAlch(0);
		assertNull("an item with no alch value", check(item, NotificationMetric.HA_PROFIT, TimeWindow.H24,
				NotificationOperation.GTE, "0"));
	}

	@Test
	public void deltaPercentComparesTheLivePriceWithTheWindowAverage()
	{
		TrackedItem item = item(0, 0, 110);
		item.getWindowStats().put(TimeWindow.WEEK, new PriceStats(100, 100, 100, 1));

		assertEquals(Boolean.TRUE, check(item, NotificationMetric.DELTA_PCT, TimeWindow.WEEK,
				NotificationOperation.GTE, "10%"));
		assertEquals(Boolean.FALSE, check(item, NotificationMetric.DELTA_PCT, TimeWindow.WEEK,
				NotificationOperation.GT, "10.5"));

		item.setAvgPrice(0);
		assertNull("no live price, no change", check(item, NotificationMetric.DELTA_PCT, TimeWindow.WEEK,
				NotificationOperation.GTE, "0%"));
	}

	@Test
	public void anUnparseableTargetCannotBeEvaluated()
	{
		TrackedItem item = item(200, 200, 200);

		assertNull(check(item, NotificationMetric.HIGH, TimeWindow.LIVE, NotificationOperation.GTE, "lots"));
		assertNull(check(item, NotificationMetric.HIGH, TimeWindow.LIVE, NotificationOperation.GTE, null));
		assertNull(check(item, NotificationMetric.DELTA_PCT, TimeWindow.LIVE, NotificationOperation.GTE, "some%"));
	}

	@Test
	public void thirtyDayRangeAlwaysReadsTheMonthSeries()
	{
		TrackedItem item = item(0, 0, 199);
		item.setSeries6h(List.of(point(24, 150, 100), point(48, 200, 180), point(24 * 40, 10_000, 1)));

		assertEquals("a live price at 99% of the month's range, whatever window the rule names", Boolean.TRUE,
				check(item, NotificationMetric.RANGE_30D, TimeWindow.LIVE, NotificationOperation.EQ, "Highest"));
		assertNull("a categorical rule needs a value", check(item, NotificationMetric.RANGE_30D, null,
				NotificationOperation.EQ, null));
	}

	@Test
	public void volatilityRatesTheWeekSeries()
	{
		TrackedItem item = item(0, 0, 0);
		item.setSeries1h(List.of(point(1, 100, 100), point(2, 101, 99)));

		assertEquals(Boolean.TRUE, check(item, NotificationMetric.VOLATILITY, null, NotificationOperation.EQ, "low"));

		item.setSeries1h(List.of(point(1, 100, 0)));
		assertNull("one sample can't be rated", check(item, NotificationMetric.VOLATILITY, null,
				NotificationOperation.EQ, "Low"));
	}

	@Test
	public void liquidityWithNoDayStatsCannotBeRated()
	{
		assertNull(check(item(1, 1, 1), NotificationMetric.LIQUIDITY, null, NotificationOperation.EQ, "Low"));
	}
}
