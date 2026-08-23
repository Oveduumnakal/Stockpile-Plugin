/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

/**
 * Whether an entry is a full tracked item or a watch-only one.
 *
 * <ul>
 *   <li>{@link #TRACK} &ndash; counts toward quantities, value, and profit totals.</li>
 *   <li>{@link #VIEW} &ndash; shown for its prices/charts only, excluded from totals.</li>
 * </ul>
 */
public enum TrackItemMode
{
	/** Watch-only: shown for prices/charts, excluded from totals. */
	VIEW,
	/** Fully tracked: counts toward quantities, value, and profit totals. */
	TRACK
}
