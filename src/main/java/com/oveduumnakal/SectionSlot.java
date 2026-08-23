/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

/**
 * An ordinal position ({@link #FIRST}..{@link #NINTH}) assigned to a detail-view
 * section to control its order, or {@link #NONE} to hide it. Used by the config
 * so each section can be placed independently. The {@code label} is the name
 * shown in the config dropdown.
 */
public enum SectionSlot
{
	/** The {@code "None"} option. */
	NONE("None"),
	/** The {@code "1st"} option. */
	FIRST("1st"),
	/** The {@code "2nd"} option. */
	SECOND("2nd"),
	/** The {@code "3rd"} option. */
	THIRD("3rd"),
	/** The {@code "4th"} option. */
	FOURTH("4th"),
	/** The {@code "5th"} option. */
	FIFTH("5th"),
	/** The {@code "6th"} option. */
	SIXTH("6th"),
	/** The {@code "7th"} option. */
	SEVENTH("7th"),
	/** The {@code "8th"} option. */
	EIGHTH("8th"),
	/** The {@code "9th"} option. */
	NINTH("9th"),
	/** The {@code "10th"} option. */
	TENTH("10th");

	private final String label;

	SectionSlot(String label)
	{
		this.label = label;
	}

	/**
	 * Returns whether this slot is the not-shown placeholder.
	 *
	 * @return {@code true} if this is {@link #NONE}
	 */
	public boolean isNone()
	{
		return this == NONE;
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
