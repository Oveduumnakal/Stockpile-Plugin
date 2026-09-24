/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

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
}
