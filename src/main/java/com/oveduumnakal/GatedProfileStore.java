/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

/**
 * A {@link ProfileConfigStore} whose writes can be switched off, so state loaded for one RS profile is
 * never written under another's key (#377).
 *
 * <p>RuneLite re-keys the RS profile on {@code AccountHashChanged}, which fires on the next login
 * <em>before</em> {@code LOGGED_IN} reloads Stockpile's state. Between logout and that reload the
 * previous account's items, ledger and portfolio history are still in memory, so any persist landing
 * in the gap - a throttled portfolio snapshot, a cost-basis-init write, an executor save queued before
 * logout - used to write account A's data under account B's key, where the reload then read it back as
 * B's. Reads always pass through; writes are dropped while the store is closed.
 */
class GatedProfileStore implements ProfileConfigStore
{
	private final ProfileConfigStore delegate;

	private volatile boolean writable;

	/**
	 * @param delegate the real store; starts closed until {@link #open()} is called for a loaded profile
	 */
	GatedProfileStore(ProfileConfigStore delegate)
	{
		this.delegate = delegate;
	}

	/** Allows writes: the in-memory state now belongs to the active RS profile. */
	void open()
	{
		writable = true;
	}

	/** Drops writes until the next {@link #open()}: the in-memory state belongs to a logged-out profile. */
	void close()
	{
		writable = false;
	}

	/** @return whether writes currently reach the underlying store */
	boolean isOpen()
	{
		return writable;
	}

	@Override
	public String get(String group, String key)
	{
		return delegate.get(group, key);
	}

	@Override
	public void set(String group, String key, String value)
	{
		if (writable)
			delegate.set(group, key, value);
	}
}
