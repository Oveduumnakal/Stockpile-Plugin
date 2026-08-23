/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * A named selection of {@link TimeWindow}s shown as columns in the price
 * overview grid, ranging from a short {@link #RECENT} set to the full
 * {@link #DETAILED} set. Each preset carries a display {@code label} and its
 * immutable set of windows (see {@link #getWindows()}).
 */
public enum OverviewPreset
{
	/** The {@code "Recent"} option. */
	RECENT("Recent", EnumSet.of(
			TimeWindow.LIVE, TimeWindow.H1, TimeWindow.H12, TimeWindow.H24)),
	/** The {@code "Standard"} option. */
	STANDARD("Standard", EnumSet.of(
			TimeWindow.LIVE, TimeWindow.H1, TimeWindow.H24, TimeWindow.WEEK, TimeWindow.MONTH)),
	/** The {@code "Detailed"} option. */
	DETAILED("Detailed", EnumSet.of(
			TimeWindow.LIVE, TimeWindow.H1, TimeWindow.H3, TimeWindow.H6, TimeWindow.H12,
			TimeWindow.H24, TimeWindow.WEEK, TimeWindow.MONTH, TimeWindow.MONTH3,
			TimeWindow.MONTH6, TimeWindow.YEAR));

	private final String label;
	private final Set<TimeWindow> windows;

	OverviewPreset(String label, Set<TimeWindow> windows)
	{
		this.label = label;
		this.windows = Collections.unmodifiableSet(windows);
	}

	/**
	 * Returns the time windows shown by this preset.
	 *
	 * @return the unmodifiable set of windows
	 */
	public Set<TimeWindow> getWindows()
	{
		return windows;
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
