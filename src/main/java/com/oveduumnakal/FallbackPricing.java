/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

/**
 * Which price seeds the cost basis of an unknown-source quantity change — an
 * auto-added item, a mobile/offline resync, or anything else no detector observed
 * (#219). Split out from the old {@code AutoAddMode} so the fallback price is chosen
 * independently of the on/off {@link StockpileConfig#autoAddItems() auto-add gate}.
 *
 * <ul>
 *   <li>{@link #HIGH}/{@link #LOW}/{@link #AVG} &ndash; seed at the item's current
 *       high, low, or average price.</li>
 *   <li>{@link #ZERO} &ndash; seed at a zero cost basis (pure gain).</li>
 * </ul>
 *
 * <p>The {@code label} is the human-readable name shown in the config dropdown.
 */
public enum FallbackPricing
{
	/** The {@code "High"} option. */
	HIGH("High"),
	/** The {@code "Low"} option. */
	LOW("Low"),
	/** The {@code "Avg"} option. */
	AVG("Avg"),
	/** The {@code "Zero"} option. */
	ZERO("Zero");

	private final String label;

	FallbackPricing(String label)
	{
		this.label = label;
	}

	/**
	 * Returns the display label shown in the UI.
	 *
	 * @return the display label
	 */
	@Override
	public String toString()
	{
		return label;
	}

	/**
	 * The single fallback-pricing policy, shared by every call site so a new value or a change to
	 * HIGH/LOW/AVG meaning is decided once (#181). Selects among the candidate prices supplied for
	 * one item: {@link #HIGH} → {@code high}, {@link #LOW} → {@code low}, {@link #ZERO} → {@code 0},
	 * {@link #AVG} → {@code avg}. Callers that only have a single guide price (an item with no
	 * {@link TrackedItem}) pass it for all three, so the choice still resolves consistently.
	 *
	 * @param high the item's current high price
	 * @param low  the item's current low price
	 * @param avg  the item's current average price (also the default)
	 * @return the price this mode seeds the cost basis with
	 */
	long select(long high, long low, long avg)
	{
		switch (this)
		{
			case HIGH:
				return high;
			case LOW:
				return low;
			case ZERO:
				return 0;
			case AVG:
			default:
				return avg;
		}
	}

	/**
	 * Maps a legacy combined {@code AutoAddMode} name (HIGH/LOW/AVG/ZERO/OFF) to the
	 * fallback price it migrates to (#219). OFF carried no pricing choice, so it lands on
	 * {@link #AVG} (today's default). Returns {@code null} for a value already migrated to a
	 * boolean, a fresh install's {@code null}, or any unrecognised string — the caller then
	 * leaves the setting untouched, keeping the migration idempotent.
	 */
	static FallbackPricing fromLegacyMode(String legacy)
	{
		if (legacy == null)
			return null;

		switch (legacy)
		{
			case "HIGH":
				return HIGH;
			case "LOW":
				return LOW;
			case "ZERO":
				return ZERO;
			case "AVG":
			case "OFF":
				return AVG;
			default:
				return null;
		}
	}
}
