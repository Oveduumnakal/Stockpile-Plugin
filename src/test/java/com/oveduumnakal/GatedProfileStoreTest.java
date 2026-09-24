/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for {@link GatedProfileStore}: writes are dropped while the store is closed between a logout
 * and the next profile load, and reads always pass through (#377).
 */
public class GatedProfileStoreTest
{
	private final Map<String, String> backing = new HashMap<>();

	private final GatedProfileStore store = new GatedProfileStore(new ProfileConfigStore()
	{
		@Override
		public String get(String group, String key)
		{
			return backing.get(group + "." + key);
		}

		@Override
		public void set(String group, String key, String value)
		{
			backing.put(group + "." + key, value);
		}
	});

	@Test
	public void startsClosedSoNothingIsWrittenBeforeAProfileLoads()
	{
		store.set("g", "k", "account A");

		assertNull(backing.get("g.k"));
	}

	@Test
	public void writesOnlyWhileOpen()
	{
		store.open();
		store.set("g", "k", "account A");
		store.close();
		store.set("g", "k", "account A, written after logout");

		assertEquals("a write in the logout-to-reload gap is dropped", "account A", backing.get("g.k"));
	}

	@Test
	public void readsPassThroughWhileClosed()
	{
		backing.put("g.k", "stored");

		assertEquals("stored", store.get("g", "k"));
	}
}
