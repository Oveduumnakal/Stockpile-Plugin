/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

/** The category management operations the panel invokes; implemented by the plugin. */
public interface CategoryActions
{
	void setItemCategory(int itemId, String category);

	void create(String name);

	void rename(String oldName, String newName);

	void delete(String name);

	void reorder(String name, int targetIndex);
}
