/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

/**
 * How a tracked-item row surfaces its quick actions (remove, favorite, overlay, per-item
 * compact, open in dashboard, view detail). {@link #HOVER_BUTTONS} keeps the current hover
 * strip; {@link #RIGHT_CLICK_MENU} moves the actions into the row's right-click menu and
 * hides the hover controls; {@link #BOTH} offers both delivery paths at once.
 *
 * <p>Public because it is the return type of a {@code @ConfigItem} accessor: the RuneLite
 * config proxy lives in another module and must be able to access it, or the plugin fails
 * to start with an {@link IllegalAccessError}.
 */
public enum QuickActionDelivery
{
	/** The {@code "Hover buttons"} option: the current hover strip only. */
	HOVER_BUTTONS("Hover buttons"),
	/** The {@code "Right-click menu"} option: actions in the row menu, no hover controls. */
	RIGHT_CLICK_MENU("Right-click menu"),
	/** The {@code "Both"} option: hover strip and the row right-click menu. */
	BOTH("Both");

	private final String label;

	QuickActionDelivery(String label)
	{
		this.label = label;
	}

	/** @return whether the hover-revealed action buttons are shown in this mode. */
	boolean showsHoverButtons()
	{
		return this != RIGHT_CLICK_MENU;
	}

	/** @return whether the row right-click menu carries the full quick-action set in this mode. */
	boolean showsRowMenuActions()
	{
		return this != HOVER_BUTTONS;
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
