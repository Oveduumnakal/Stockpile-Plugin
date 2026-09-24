/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.OptionalDouble;
import java.util.function.LongSupplier;

/**
 * Evaluates one {@link NotificationRule} against a {@link TrackedItem}: resolves the rule's metric to
 * a current reading (a window price, volume, profit, Δ%, quantity, or a categorical rating) and tests
 * it against the rule's threshold. Extracted from {@code StockpilePlugin} so the rule engine can be
 * unit-tested without a client (#373). Client thread only: {@code ITM_PROFIT} streams the item's lots.
 */
class NotificationEvaluator
{
	/**
	 * Maximum plausible Δ% for a notification: changes beyond this magnitude
	 * indicate a sparse/stale window average (a near-zero denominator) rather than
	 * a real move, and are ignored so a one-shot rule isn't fired on noise.
	 */
	static final double MAX_DELTA_PCT = 1000.0;

	private final LongSupplier natureRunePrice;

	private final LongSupplier fireRunePrice;

	/**
	 * @param natureRunePrice the current nature rune price, for the high-alch profit metric
	 * @param fireRunePrice the current fire rune price, for the high-alch profit metric
	 */
	NotificationEvaluator(LongSupplier natureRunePrice, LongSupplier fireRunePrice)
	{
		this.natureRunePrice = natureRunePrice;
		this.fireRunePrice = fireRunePrice;
	}

	/**
	 * Evaluates a single rule against an item.
	 *
	 * @return {@code TRUE}/{@code FALSE} for the condition, or {@code null} when it
	 *         can't be evaluated yet (incomplete rule or missing/unparseable data)
	 */
	Boolean evaluate(TrackedItem item, NotificationRule rule)
	{
		NotificationMetric metric = rule.getMetric();
		if (metric == null || rule.getOperation() == null)
			return null;

		if (metric.isCategorical())
		{
			String current = categoryValue(item, metric);
			if (current == null || rule.getValue() == null)
				return null;

			return current.equalsIgnoreCase(rule.getValue().trim());
		}

		TimeWindow window = metric.locksTimeframeToMonth() ? TimeWindow.MONTH : rule.getTimeWindow();
		OptionalDouble current = numericValue(item, metric, window);
		if (!current.isPresent())
			return null;

		OptionalDouble target = metric.getKind() == NotificationMetric.Kind.PERCENT
				? NotificationRule.parsePercent(rule.getValue())
				: NotificationRule.parseNumeric(rule.getValue());
		if (!target.isPresent())
			return null;

		return rule.getOperation().test(current.getAsDouble(), target.getAsDouble());
	}

	/**
	 * Resolves the current numeric reading of a metric for an item over a window
	 * (price, volume, profit, HA profit, Δ% vs. the window average, or quantity).
	 *
	 * @return the value, or empty when the underlying data is missing or unreliable
	 */
	private OptionalDouble numericValue(TrackedItem item, NotificationMetric metric, TimeWindow window)
	{
		if (metric == NotificationMetric.QUANTITY)
			return OptionalDouble.of(item.getQuantity());

		PriceStats s = item.getWindowStats().get(window);
		long avg = s == null ? 0 : s.getAvg();
		switch (metric)
		{
			case HIGH:
				return s == null ? OptionalDouble.empty() : OptionalDouble.of(s.getHigh());
			case LOW:
				return s == null ? OptionalDouble.empty() : OptionalDouble.of(s.getLow());
			case AVERAGE:
				return s == null ? OptionalDouble.empty() : OptionalDouble.of(s.getAvg());
			case VOLUME:
				return s == null ? OptionalDouble.empty() : OptionalDouble.of(s.getVolume());
			case ITM_PROFIT:
				return avg <= 0 ? OptionalDouble.empty() : OptionalDouble.of(item.getProfitAt(avg));
			case HA_PROFIT:
				if (avg <= 0 || item.getHighAlch() <= 0)
					return OptionalDouble.empty();

				return OptionalDouble.of(MarketMath.highAlchProfit(item.getHighAlch(), avg,
						natureRunePrice.getAsLong(), fireRunePrice.getAsLong()));
			case DELTA_PCT:
			{
				double pct = MarketMath.changePct(item.getAvgPrice(), avg);
				if (Double.isNaN(pct))
					return OptionalDouble.empty();

				return Math.abs(pct) > MAX_DELTA_PCT ? OptionalDouble.empty() : OptionalDouble.of(pct);
			}
			default:
				return OptionalDouble.empty();
		}
	}

	/**
	 * Resolves the current categorical rating of a metric for an item
	 * (volatility, liquidity, or 30-day range position) via {@link MarketClassifier}.
	 *
	 * @return the rating label, or {@code null} when it can't be classified
	 */
	private String categoryValue(TrackedItem item, NotificationMetric metric)
	{
		switch (metric)
		{
			case VOLATILITY:
				return MarketClassifier.volatility(item.getSeriesFor(TimeWindow.WEEK));
			case LIQUIDITY:
			{
				PriceStats s = item.getWindowStats().get(TimeWindow.H24);
				return MarketClassifier.liquidity(s == null ? 0 : s.getVolume());
			}
			case RANGE_30D:
			{
				long[] range = MarketClassifier.thirtyDayRange(item.getSeriesFor(TimeWindow.MONTH));
				return MarketClassifier.rangePosition(range[0], range[1], item.getAvgPrice());
			}
			default:
				return null;
		}
	}
}
