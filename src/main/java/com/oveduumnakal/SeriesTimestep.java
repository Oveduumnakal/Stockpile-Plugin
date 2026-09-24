/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.time.Duration;

import lombok.Getter;

/**
 * The wiki timeseries sampling steps the plugin fetches, each with how long one fetch stays useful.
 *
 * <p>The freshness window matches the step's own granularity: refetching the {@code 24h} series
 * every minute cannot produce new data, since it only gains a point once a day. Every completed
 * price refresh used to reissue all four for every tracked item regardless (#320).
 */
enum SeriesTimestep
{
	/** Five-minute samples, roughly the last day. */
	FIVE_MIN("5m", Duration.ofMinutes(5)),
	/** Hourly samples, feeding the 1-week window. */
	HOUR("1h", Duration.ofHours(1)),
	/** Six-hourly samples, feeding the 1-month window. */
	SIX_HOUR("6h", Duration.ofHours(6)),
	/** Daily samples, feeding the 3-month, 6-month and 1-year windows. */
	DAY("24h", Duration.ofHours(24));

	/** The {@code timestep} query value the wiki API expects. */
	@Getter
	private final String label;

	/** How long a fetch of this step stays fresh before it is worth reissuing. */
	@Getter
	private final Duration freshness;

	SeriesTimestep(String label, Duration freshness)
	{
		this.label = label;
		this.freshness = freshness;
	}
}
