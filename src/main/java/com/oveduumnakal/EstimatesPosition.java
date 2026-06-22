/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

public enum EstimatesPosition
{
	TOP("Top"),
	BOTTOM("Bottom");

	private final String label;

	EstimatesPosition(String label)
	{
		this.label = label;
	}

	@Override
	public String toString()
	{
		return label;
	}
}
