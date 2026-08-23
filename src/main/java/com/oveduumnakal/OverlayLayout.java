/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

/**
 * How items are drawn in the on-screen tracked-items overlay &ndash; the dense
 * two-row {@link #COMPACT} layout, or a {@link #STANDARD} replica of the panel's
 * standard row. The {@code label} is the name shown in the config dropdown.
 */
public enum OverlayLayout
{
	/** The {@code "Compact"} option. */
	COMPACT("Compact"),
	/** The {@code "Standard"} option. */
	STANDARD("Standard");

	private final String label;

	OverlayLayout(String label)
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
