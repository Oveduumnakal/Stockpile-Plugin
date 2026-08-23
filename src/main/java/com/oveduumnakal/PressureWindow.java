/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.time.Duration;

/**
 * The look-back period for the Buy/Sell Pressure block, backed by a
 * {@link TimeWindow} that supplies both the aggregation {@code duration} and the
 * price-history granularity used to sum instant-buy vs instant-sell volume. The
 * {@code label} is the name shown in the config dropdown.
 */
public enum PressureWindow
{
	/** The {@code "24 Hours"} option. */
	DAY("24 Hours", TimeWindow.H24),
	/** The {@code "1 Week"} option. */
	WEEK("1 Week", TimeWindow.WEEK),
	/** The {@code "1 Month"} option. */
	MONTH("1 Month", TimeWindow.MONTH);

	private final String label;
	private final TimeWindow window;

	PressureWindow(String label, TimeWindow window)
	{
		this.label = label;
		this.window = window;
	}

	/**
	 * Returns the time window this pressure period maps to.
	 *
	 * @return the backing {@link TimeWindow}
	 */
	public TimeWindow window()
	{
		return window;
	}

	/**
	 * Returns the look-back duration of this pressure period.
	 *
	 * @return the duration of the backing time window
	 */
	public Duration duration()
	{
		return window.getDuration();
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
}
