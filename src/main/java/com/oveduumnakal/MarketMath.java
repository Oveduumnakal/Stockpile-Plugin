/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

/**
 * Small pure market-math helpers shared by the item detail view and the compare view (#280): the
 * Grand Exchange sell tax, high/low alchemy profit, and percent price change against a baseline.
 * Extracted so both views compute these identical figures from one source rather than each
 * duplicating the formulas.
 */
final class MarketMath
{
	/** The Grand Exchange sell tax is 2% of the sale price. */
	private static final double GE_TAX_RATE = 0.02;

	/** The Grand Exchange sell tax is waived on items priced below this many gp. */
	private static final long GE_TAX_MIN_PRICE = 50L;

	/** The Grand Exchange sell tax is capped at this many gp per item. */
	private static final long GE_TAX_CAP = 5_000_000L;

	/** High-alchemy consumes five fire runes per cast. */
	private static final int HIGH_ALCH_FIRE_RUNES = 5;

	/** Low-alchemy consumes three fire runes per cast. */
	private static final int LOW_ALCH_FIRE_RUNES = 3;

	private MarketMath()
	{
	}

	/**
	 * @param avgPrice the unit sale price in gp
	 * @return the Grand Exchange sell tax on a unit at {@code avgPrice} (per the live GE tax rules)
	 */
	static long geTax(long avgPrice)
	{
		if (avgPrice < GE_TAX_MIN_PRICE)
			return 0;

		long tax = (long) Math.floor(avgPrice * GE_TAX_RATE);
		return Math.min(tax, GE_TAX_CAP);
	}

	/**
	 * @param highAlch the item's high-alchemy value in gp
	 * @param itemAvg the item's average price in gp
	 * @param naturePrice the nature-rune price in gp
	 * @param firePrice the fire-rune price in gp
	 * @return the profit from high-alching one unit (alch value minus item cost and rune cost)
	 */
	static long highAlchProfit(long highAlch, long itemAvg, long naturePrice, long firePrice)
	{
		return highAlch - itemAvg - naturePrice - HIGH_ALCH_FIRE_RUNES * firePrice;
	}

	/**
	 * @param lowAlch the item's low-alchemy value in gp
	 * @param itemAvg the item's average price in gp
	 * @param naturePrice the nature-rune price in gp
	 * @param firePrice the fire-rune price in gp
	 * @return the profit from low-alching one unit (alch value minus item cost and rune cost)
	 */
	static long lowAlchProfit(long lowAlch, long itemAvg, long naturePrice, long firePrice)
	{
		return lowAlch - itemAvg - naturePrice - LOW_ALCH_FIRE_RUNES * firePrice;
	}

	/**
	 * @param current the current price in gp
	 * @param baseline the baseline (window-average) price in gp
	 * @return the signed percent change of {@code current} against {@code baseline}, rounded to one
	 *     decimal place, or {@link Double#NaN} when either price is non-positive
	 */
	static double changePct(long current, long baseline)
	{
		if (current <= 0 || baseline <= 0)
			return Double.NaN;

		return Math.round(((double) (current - baseline) / baseline) * 1000.0) / 10.0;
	}
}
