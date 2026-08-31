/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import net.runelite.client.config.ConfigManager;

/**
 * The RS-profile config seam {@link StockpilePersistence} reads and writes through, in the same
 * shape as the plugin's other host interfaces. It exists so the persistence layer's corrupt-value
 * fallbacks — the behaviour that class's javadoc promises — can be exercised without a live
 * {@link ConfigManager}, which has no constructor a test can reach.
 */
interface ProfileConfigStore
{
	/**
	 * @param group the config group
	 * @param key the config key within the group
	 * @return the stored value, or {@code null} when the key is unset
	 */
	String get(String group, String key);

	/**
	 * Stores {@code value} under {@code group}/{@code key} for the active RS profile.
	 *
	 * @param group the config group
	 * @param key the config key within the group
	 * @param value the serialized value to store
	 */
	void set(String group, String key, String value);
}
