/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.time.Duration;

/**
 * A look-back period over which prices and volumes are summarized, from the
 * latest 5-minute snapshot ({@link #LIVE}) up to a {@link #YEAR}.
 *
 * <p>Each constant carries three forms: a compact {@code label} for tight chips
 * (e.g. {@code "1mo"}), a spelled-out {@code longLabel} for headers
 * (e.g. {@code "1 Month"}), and a {@code duration} used to bound queries.
 * {@link #NONE} and {@link #LIVE} have a zero duration; {@link #NONE} is a
 * not-applicable placeholder.
 */
public enum TimeWindow
{
	/** Not-applicable placeholder with a zero duration. */
	NONE("None", Duration.ZERO, "None"),
	/** The latest 5-minute snapshot (no averaging window). */
	LIVE("Ltst", Duration.ZERO, "5 Minute"),
	/** The trailing 5-minute average. */
	M5("5m", Duration.ofMinutes(5), "5 Minute Avg"),
	/** The trailing 1 hour. */
	H1("1h", Duration.ofHours(1), "1 Hour"),
	/** The trailing 3 hours. */
	H3("3h", Duration.ofHours(3), "3 Hour"),
	/** The trailing 6 hours. */
	H6("6h", Duration.ofHours(6), "6 Hour"),
	/** The trailing 12 hours. */
	H12("12h", Duration.ofHours(12), "12 Hour"),
	/** The trailing 24 hours. */
	H24("24h", Duration.ofHours(24), "24 Hour"),
	/** The trailing week. */
	WEEK("1w", Duration.ofDays(7), "1 Week"),
	/** The trailing 30 days. */
	MONTH("1mo", Duration.ofDays(30), "1 Month"),
	/** The trailing 90 days. */
	MONTH3("3mo", Duration.ofDays(90), "3 Month"),
	/** The trailing 180 days. */
	MONTH6("6mo", Duration.ofDays(180), "6 Month"),
	/** The trailing 365 days. */
	YEAR("1y", Duration.ofDays(365), "1 Year");

	private final String label;
	private final Duration duration;
	private final String longLabel;

	TimeWindow(String label, Duration duration, String longLabel)
	{
		this.label = label;
		this.duration = duration;
		this.longLabel = longLabel;
	}

	/**
	 * Returns the compact chip label (e.g. {@code "1mo"}).
	 *
	 * @return the compact label
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Returns the spelled-out header label (e.g. {@code "1 Month"}).
	 *
	 * @return the long label
	 */
	public String getLongLabel()
	{
		return longLabel;
	}

	/**
	 * Returns the look-back duration used to bound queries.
	 *
	 * @return the duration ({@link Duration#ZERO} for {@link #NONE} and {@link #LIVE})
	 */
	public Duration getDuration()
	{
		return duration;
	}

	/**
	 * Returns the compact label.
	 *
	 * @return the compact chip label
	 */
	@Override
	public String toString()
	{
		return label;
	}
}
