/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

/**
 * Vertical spacing of the GE Estimates value rows. {@link #DEFAULT} keeps the
 * roomier estimate-row spacing; {@link #COMPACT} tightens it to match the
 * tracked items list.
 */
public enum EstimatesSpacing
{
	DEFAULT("Default"),
	COMPACT("Compact");

	private final String label;

	EstimatesSpacing(String label)
	{
		this.label = label;
	}

	@Override
	public String toString()
	{
		return label;
	}
}
