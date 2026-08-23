/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The item attribute a {@link NotificationRule} watches &ndash; a price, profit,
 * volume, percent change, quantity, or a categorical rating.
 *
 * <p>Each constant pairs a short {@code label} (used in the rule chip) with a
 * longer {@code displayName} (dropdown + tooltip) and an even shorter
 * {@code abbreviation} (the notifications-table row cell, which is too narrow for
 * the full name), plus a {@link Kind} that drives how the rule's value is entered
 * and compared. Categorical metrics additionally carry their allowed
 * {@code options}. The {@code locks*}/{@code is*} predicates capture per-metric
 * UI constraints (e.g. {@link #RANGE_30D} only makes sense over a month).
 */
public enum NotificationMetric
{
	/** Current high (instant-buy) price. */
	HIGH("High", "High", "High", Kind.NUMERIC),
	/** Current low (instant-sell) price. */
	LOW("Low", "Low", "Low", Kind.NUMERIC),
	/** Current average price. */
	AVERAGE("Average", "Average", "Avg", Kind.NUMERIC),
	/** Estimated item (buy/sell) profit. */
	ITM_PROFIT("Itm Profit", "Item Profit", "Itm P", Kind.NUMERIC),
	/** Estimated high-alchemy profit. */
	HA_PROFIT("HA Profit", "HA Profit", "HA P", Kind.NUMERIC),
	/** Daily trade volume. */
	VOLUME("Volume", "Volume", "Vol", Kind.NUMERIC),
	/** Percent price change over the chosen window. */
	DELTA_PCT("Δ%", "Price Change", "Δ%", Kind.PERCENT),
	/** Held quantity (live inventory count). */
	QUANTITY("Quantity", "Quantity", "Qty", Kind.QUANTITY),
	/** Price volatility rating (Low/Medium/High). */
	VOLATILITY("Volatility", "Volatility", "Volat", Kind.CATEGORY, "Low", "Medium", "High"),
	/** Market liquidity rating (Low/Medium/High). */
	LIQUIDITY("Liquidity", "Liquidity", "Liq", Kind.CATEGORY, "Low", "Medium", "High"),
	/** Position of the current price within its 30-day range. */
	RANGE_30D("30d Range", "30 Day Range", "30d R", Kind.CATEGORY,
			"Lowest", "Low", "Low Avg", "Average", "High Avg", "High", "Highest");

	/** The value domain of a metric, controlling input and comparison semantics. */
	public enum Kind
	{
		/** A plain numeric value (gp). */
		NUMERIC,
		/** A percentage value. */
		PERCENT,
		/** An item quantity. */
		QUANTITY,
		/** A categorical rating chosen from fixed options. */
		CATEGORY
	}

	private final String label;
	private final String displayName;
	private final String abbreviation;
	private final Kind kind;
	private final List<String> options;

	NotificationMetric(String label, String displayName, String abbreviation, Kind kind, String... options)
	{
		this.label = label;
		this.displayName = displayName;
		this.abbreviation = abbreviation;
		this.kind = kind;
		this.options = options.length == 0
				? Collections.emptyList()
				: Collections.unmodifiableList(Arrays.asList(options));
	}

	/**
	 * Returns the short label shown in the rule chip.
	 *
	 * @return the short label
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Returns the long name shown in the dropdown and tooltip.
	 *
	 * @return the display name
	 */
	public String getDisplayName()
	{
		return displayName;
	}

	/** @return the terse form shown in the narrow notifications-table row (full name is in the tooltip/dropdown). */
	public String getAbbreviation()
	{
		return abbreviation;
	}

	/**
	 * Returns the value domain that drives input and comparison semantics.
	 *
	 * @return the metric kind
	 */
	public Kind getKind()
	{
		return kind;
	}

	/**
	 * Returns the allowed categorical options, empty for non-categorical metrics.
	 *
	 * @return the unmodifiable option list
	 */
	public List<String> getOptions()
	{
		return options;
	}

	/**
	 * Returns whether this metric is compared against fixed categorical options.
	 *
	 * @return {@code true} if the metric's kind is {@link Kind#CATEGORY}
	 */
	public boolean isCategorical()
	{
		return kind == Kind.CATEGORY;
	}

	/** Categorical metrics compare by exact match, so the operator is forced to "=". */
	public boolean locksOperationToEquals()
	{
		return kind == Kind.CATEGORY;
	}

	/** {@link #RANGE_30D} is inherently a 30-day metric, so its timeframe is pinned to a month. */
	public boolean locksTimeframeToMonth()
	{
		return this == RANGE_30D;
	}

	/** Quantity is a live inventory count with no timeframe to choose. */
	public boolean isTimeframeDisabled()
	{
		return this == QUANTITY;
	}

	/**
	 * Returns the short label.
	 *
	 * @return the short chip label
	 */
	@Override
	public String toString()
	{
		return label;
	}
}
