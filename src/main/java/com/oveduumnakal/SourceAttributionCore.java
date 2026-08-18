/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pure, client-free core of source-attributed pricing (#64). Detectors observe
 * game events and register {@linkplain #claim claims} — "I expect a quantity
 * change of this item, from this source, at this unit price" — and the quantity
 * sync {@linkplain #attribute attributes} each detected delta against them.
 * Deltas no claim matches fall back to {@link AcquisitionSource#UNKNOWN}, whose
 * pricing is the caller's legacy policy.
 *
 * <p>Ordinary claims expire after a few ticks so a stale expectation can never
 * mis-price an unrelated later change. GE buys instead register {@linkplain
 * #claimDurable durable} claims (#180): a GE fill and its collected inventory
 * gain can be many ticks apart and must survive a logout, so durable claims
 * carry no TTL, are matched by their own {@link #attributeDurable} path, and
 * {@linkplain #exportDurable serialize} as the persisted GE buy ledger. All
 * operations are O(open claims) with no allocation when idle, keeping the
 * per-tick cost negligible; the class touches no client types, so it is
 * unit-testable in isolation.
 */
class SourceAttributionCore
{
	/** How many ticks a claim stays valid before {@link #expire} discards it. */
	static final int CLAIM_TTL_TICKS = 3;

	/** One registered expectation of a quantity change. */
	private static final class Claim
	{
		final AcquisitionSource source;
		final int itemId;
		final long unitPrice;
		final int expiryTick;
		final boolean durable;
		int quantity;

		Claim(AcquisitionSource source, int itemId, int quantity, long unitPrice, int expiryTick, boolean durable)
		{
			this.source = source;
			this.itemId = itemId;
			this.quantity = quantity;
			this.unitPrice = unitPrice;
			this.expiryTick = expiryTick;
			this.durable = durable;
		}
	}

	/** The outcome of attributing one delta: its source and, when known, a unit price. */
	static final class Attribution
	{
		static final Attribution UNKNOWN = new Attribution(AcquisitionSource.UNKNOWN, null);

		private final AcquisitionSource source;
		private final Long unitPrice;

		Attribution(AcquisitionSource source, Long unitPrice)
		{
			this.source = source;
			this.unitPrice = unitPrice;
		}

		AcquisitionSource source()
		{
			return source;
		}

		/** @return the observed unit price, or {@code fallback} when the source didn't carry one. */
		long unitPriceOr(long fallback)
		{
			return unitPrice == null ? fallback : unitPrice;
		}
	}

	private final Deque<Claim> claims = new ArrayDeque<>();

	/**
	 * Registers a detector's expectation that {@code quantity} units of
	 * {@code itemId} are about to change hands at {@code unitPrice} gp each.
	 */
	void claim(AcquisitionSource source, int itemId, int quantity, long unitPrice, int currentTick)
	{
		if (quantity <= 0)
			return;

		claims.addLast(new Claim(source, itemId, quantity, unitPrice, currentTick + CLAIM_TTL_TICKS, false));
	}

	/**
	 * Registers a durable expectation — a GE buy fill (#180) — that {@code quantity} units of
	 * {@code itemId} were acquired at {@code unitPrice} gp each. Unlike {@link #claim}, a durable
	 * claim never expires and is matched only by {@link #attributeDurable}, so the arbitrary gap
	 * between a fill and the collected inventory gain can never strand it.
	 */
	void claimDurable(AcquisitionSource source, int itemId, int quantity, long unitPrice)
	{
		if (quantity <= 0)
			return;

		claims.addLast(new Claim(source, itemId, quantity, unitPrice, 0, true));
	}

	/**
	 * Attributes a detected quantity change of {@code quantity} units (magnitude,
	 * direction-agnostic) of {@code itemId}, consuming the oldest live matching
	 * claim — partially when the claim is larger than the delta.
	 *
	 * @return the claim's attribution, or {@link Attribution#UNKNOWN} when nothing matches
	 */
	Attribution attribute(int itemId, int quantity, int currentTick)
	{
		if (claims.isEmpty() || quantity <= 0)
			return Attribution.UNKNOWN;

		Iterator<Claim> it = claims.iterator();
		while (it.hasNext())
		{
			Claim c = it.next();
			if (c.durable || c.itemId != itemId || c.expiryTick < currentTick)
				continue;

			if (c.quantity <= quantity)
				it.remove();
			else
				c.quantity -= quantity;

			return new Attribution(c.source, c.unitPrice);
		}

		return Attribution.UNKNOWN;
	}

	/**
	 * Attributes a detected gain of {@code quantity} units of {@code itemId} against the durable
	 * (GE buy) claims only, consuming them oldest-first and partially draining a claim larger than
	 * the delta. Because durable claims may carry differing unit prices, the consumed portions are
	 * returned as {@code {quantity, unitPrice}} chunks in FIFO order for the caller to price into
	 * lots; an empty list means nothing matched.
	 */
	List<long[]> attributeDurable(int itemId, int quantity)
	{
		if (claims.isEmpty() || quantity <= 0)
			return Collections.emptyList();

		List<long[]> consumed = new ArrayList<>();
		int remaining = quantity;
		Iterator<Claim> it = claims.iterator();
		while (it.hasNext() && remaining > 0)
		{
			Claim c = it.next();
			if (!c.durable || c.itemId != itemId)
				continue;

			int take = Math.min(remaining, c.quantity);
			consumed.add(new long[]{take, c.unitPrice});
			remaining -= take;
			c.quantity -= take;
			if (c.quantity <= 0)
				it.remove();
		}

		return consumed;
	}

	/**
	 * Discards expired ordinary claims; call once per tick. Durable (GE buy) claims have no TTL and
	 * are never touched here. No-op (and allocation-free) when idle.
	 */
	void expire(int currentTick)
	{
		if (claims.isEmpty())
			return;

		claims.removeIf(c -> !c.durable && c.expiryTick < currentTick);
	}

	/** Drops every open claim (logout, plugin shutdown), durable ones included. */
	void clear()
	{
		claims.clear();
	}

	/** Drops only the durable (GE buy) claims, so a reload can replace them without disturbing live claims. */
	void clearDurable()
	{
		claims.removeIf(c -> c.durable);
	}

	/**
	 * Serializes the open durable (GE buy) claims as the persisted GE buy ledger: item id to a
	 * FIFO list of {@code {quantity, unitPrice}} chunks. This is the on-disk shape the plugin has
	 * always written, so no schema change results from folding the ledger into the core (#180).
	 */
	Map<Integer, List<long[]>> exportDurable()
	{
		Map<Integer, List<long[]>> ledger = new LinkedHashMap<>();
		for (Claim c : claims)
		{
			if (!c.durable)
				continue;

			ledger.computeIfAbsent(c.itemId, k -> new ArrayList<>())
					.add(new long[]{c.quantity, c.unitPrice});
		}

		return ledger;
	}

	/**
	 * Rebuilds the durable (GE buy) claims from a persisted ledger; the reloaded chunks are GE
	 * trades. Call after {@link #clearDurable} so a login replaces rather than duplicates them.
	 */
	void importDurable(Map<Integer, List<long[]>> ledger)
	{
		for (Map.Entry<Integer, List<long[]>> e : ledger.entrySet())
		{
			for (long[] chunk : e.getValue())
				claims.addLast(new Claim(AcquisitionSource.GE_TRADE, e.getKey(), (int) chunk[0], chunk[1], 0, true));
		}
	}
}
