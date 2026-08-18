/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.time.Duration;

/**
 * The kinds of suspension a tracked item's units can be held in (#179): a unit that has left the
 * held containers but is still owned, its lot kept open at its original basis until it resolves.
 * Each source carries its own lifecycle policy so one generic engine can suspend, restore, expire,
 * and persist all of them instead of the five hand-cloned pipelines this replaces.
 *
 * <p>The policy captures the (previously drifted) per-source behaviour explicitly:
 * <ul>
 *   <li>{@link #SELL} and {@link #TRADE} — a GE sell / player-trade offer; they never time out and
 *       close at the realized transaction price, driven by the offer/trade events, so they carry no
 *       expiry and no fixed close source.</li>
 *   <li>{@link #GROUND} — a dropped or fired-ammo unit; refreshes its timestamp on each drop and, if
 *       not re-picked-up within {@link #expiry}, closes at 0 as a {@link AcquisitionSource#GROUND} loss.</li>
 *   <li>{@link #DEATH} — a unit lost to death; timestamps once (so a second death can't reset the
 *       first's clock, #168) and closes at 0 as {@link AcquisitionSource#DEATH} on gravestone loss or
 *       {@link #expiry}. Persisted, so it survives a relog into the recovery window.</li>
 *   <li>{@link #POUCH} — a unit filled into a fur/meat pouch; never times out and only ever
 *       un-suspends when the pouch is emptied. Persisted, since a pouch keeps its contents across a
 *       logout.</li>
 * </ul>
 */
enum SuspensionSource
{
	/** Units placed into a GE sell offer; realize at the fill price, no expiry, session-only. */
	SELL(StampMode.NONE, null, null, false),

	/** Units placed into a player-trade offer; close at the apportioned trade price, no expiry, session-only. */
	TRADE(StampMode.NONE, null, null, false),

	/** Units dropped on the ground (or fired as recoverable ammo); refresh-stamped, expire to a 0-gp loss. */
	GROUND(StampMode.REFRESH, Duration.ofMinutes(10), AcquisitionSource.GROUND, false),

	/** Units lost to a death; stamped once, expire to a 0-gp loss, persisted across a relog. */
	DEATH(StampMode.STAMP_IF_EMPTY, Duration.ofMinutes(65), AcquisitionSource.DEATH, true),

	/** Units filled into a fur/meat hunting pouch; never expire, only un-suspend on empty, persisted. */
	POUCH(StampMode.NONE, null, null, true);

	/** How a source updates its suspension timestamp when more units are suspended. */
	enum StampMode
	{
		/** No timestamp is kept (the source has no expiry sweep). */
		NONE,

		/** Stamp only when the entry was empty, so later additions can't reset the recovery clock (#168). */
		STAMP_IF_EMPTY,

		/** Re-stamp on every addition (the newest suspension bounds the expiry window). */
		REFRESH;
	}

	private final StampMode stampMode;
	private final Duration expiry;
	private final AcquisitionSource closeSource;
	private final boolean persisted;

	SuspensionSource(StampMode stampMode, Duration expiry, AcquisitionSource closeSource, boolean persisted)
	{
		this.stampMode = stampMode;
		this.expiry = expiry;
		this.closeSource = closeSource;
		this.persisted = persisted;
	}

	StampMode stampMode()
	{
		return stampMode;
	}

	/**
	 * @return how long an unrecovered suspension survives before the expiry sweep closes it, or
	 *         {@code null} when it never expires
	 */
	Duration expiry()
	{
		return expiry;
	}

	/**
	 * @return the acquisition source a timed-out suspension closes as (at 0 gp), or {@code null} for
	 *         realize-at-price sources
	 */
	AcquisitionSource closeSource()
	{
		return closeSource;
	}

	/** @return whether this source's suspension survives a relog and is written through {@code PersistedItem}. */
	boolean persisted()
	{
		return persisted;
	}
}
