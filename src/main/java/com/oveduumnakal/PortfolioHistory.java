/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A bounded, thinned time series of total portfolio value (and cost basis) for the
 * "portfolio value over time" chart. Each point is {@code {epochSeconds, totalValue,
 * costBasis}}.
 *
 * <p>To stay inside config-size limits, recent history is kept at hourly resolution
 * (at most one point per hour for the last {@value #HOURLY_HOURS} hours) and older
 * history is collapsed to one point per day, up to {@value #DAILY_DAYS} days;
 * anything older is dropped. Points are stored as {@code long[]} so persistence is a
 * plain primitive list with no schema shape to guard.
 */
public final class PortfolioHistory
{
	/** Hours of recent history kept at one-point-per-hour resolution. */
	static final int HOURLY_HOURS = 48;

	/** Days of older history kept at one-point-per-day resolution. */
	static final int DAILY_DAYS = 90;

	private static final long HOUR = 3600L;

	private static final long DAY = 86_400L;

	private final List<long[]> points = new ArrayList<>();

	/** Replaces the series with {@code stored} (as loaded from config); ignores malformed rows. */
	public void load(List<long[]> stored)
	{
		points.clear();
		if (stored == null)
			return;

		stored.stream()
				.filter(p -> p != null && p.length >= 3)
				.forEach(p -> points.add(new long[]{p[0], p[1], p[2]}));

		points.sort((a, b) -> Long.compare(a[0], b[0]));
	}

	/**
	 * Records a snapshot at {@code epochSeconds}. Within the same hour as the latest
	 * point the value is updated in place (so a burst of refreshes yields one hourly
	 * point); otherwise a new point is appended. Old points are then thinned.
	 */
	public void record(long epochSeconds, long totalValue, long costBasis)
	{
		if (!points.isEmpty())
		{
			long[] last = points.get(points.size() - 1);
			if (last[0] / HOUR == epochSeconds / HOUR)
			{
				last[0] = epochSeconds;
				last[1] = totalValue;
				last[2] = costBasis;
				thin(epochSeconds);
				return;
			}
		}

		points.add(new long[]{epochSeconds, totalValue, costBasis});
		thin(epochSeconds);
	}

	/** Collapses points older than the hourly window to one-per-day and drops points beyond the daily window. */
	private void thin(long nowSeconds)
	{
		long dropBefore = nowSeconds - DAILY_DAYS * DAY;
		long hourlyCutoff = nowSeconds - HOURLY_HOURS * HOUR;

		Map<Long, long[]> dailyKept = new LinkedHashMap<>();
		List<long[]> recent = new ArrayList<>();

		for (long[] p : points)
		{
			if (p[0] < dropBefore)
				continue;

			if (p[0] < hourlyCutoff)
				dailyKept.put(p[0] / DAY, p);
			else
				recent.add(p);
		}

		List<long[]> merged = new ArrayList<>(dailyKept.values());
		merged.addAll(recent);
		merged.sort((a, b) -> Long.compare(a[0], b[0]));

		points.clear();
		points.addAll(merged);
	}

	/** @return the stored points in chronological order (defensive copy). */
	public List<long[]> points()
	{
		List<long[]> copy = new ArrayList<>(points.size());
		points.forEach(p -> copy.add(new long[]{p[0], p[1], p[2]}));
		return copy;
	}

	/** @return whether any points are stored. */
	public boolean isEmpty()
	{
		return points.isEmpty();
	}
}
