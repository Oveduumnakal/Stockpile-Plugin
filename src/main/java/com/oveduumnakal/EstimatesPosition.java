/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

/**
 * Where the GE value estimate block sits within an item's detail card &ndash;
 * above ({@link #TOP}) or below ({@link #BOTTOM}) the other sections. The
 * {@code label} is the name shown in the config dropdown.
 */
public enum EstimatesPosition
{
	/** The {@code "Top"} option. */
	TOP("Top"),
	/** The {@code "Bottom"} option. */
	BOTTOM("Bottom");

	private final String label;

	EstimatesPosition(String label)
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
}
