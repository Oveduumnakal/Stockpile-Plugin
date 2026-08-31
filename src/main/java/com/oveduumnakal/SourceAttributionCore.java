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
 * {@linkplain #exportDurable serialize} as the persisted GE buy ledger. They live
 * in a separate deque, so the per-tick {@link #attribute}/{@link #expire} scans stay
 * O(ordinary open claims) no matter how many uncollected buys are outstanding (#259),
 * with no allocation when idle; the class touches no client types, so it is
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
		int quantity;

		Claim(AcquisitionSource source, int itemId, int quantity, long unitPrice, int expiryTick)
		{
			this.source = source;
			this.itemId = itemId;
			this.quantity = quantity;
			this.unitPrice = unitPrice;
			this.expiryTick = expiryTick;
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
	 * Durable (GE buy) claims, kept apart from {@link #claims} so the per-tick {@link #attribute} and
	 * {@link #expire} scans never walk them &mdash; an uncollected buy can outlive many ticks without
	 * inflating the per-tick cost (#259). Matched only by {@link #attributeDurable}.
	 */
	private final Deque<Claim> durableClaims = new ArrayDeque<>();

	/**
	 * Registers a detector's expectation that {@code quantity} units of
	 * {@code itemId} are about to change hands at {@code unitPrice} gp each.
	 */
	void claim(AcquisitionSource source, int itemId, int quantity, long unitPrice, int currentTick)
	{
		if (quantity <= 0)
			return;

		claims.addLast(new Claim(source, itemId, quantity, unitPrice, currentTick + CLAIM_TTL_TICKS));
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

		durableClaims.addLast(new Claim(source, itemId, quantity, unitPrice, 0));
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
			if (c.itemId != itemId || c.expiryTick < currentTick)
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
		if (durableClaims.isEmpty() || quantity <= 0)
			return Collections.emptyList();

		List<long[]> consumed = new ArrayList<>();
		int remaining = quantity;
		Iterator<Claim> it = durableClaims.iterator();
		while (it.hasNext() && remaining > 0)
		{
			Claim c = it.next();
			if (c.itemId != itemId)
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
	 * Prunes orphaned durable (GE buy) claims (#259): caps each item's durable claims, oldest-first, to the
	 * quantity still outstanding in the live GE buy offers ({@code outstandingByItem}). A buy that was never
	 * collected keeps its offer open across logins, so its claim survives; buys collected long ago (their offer
	 * slot now empty) leave orphaned claims that would misprice a later FIFO collection, so the excess beyond
	 * what is still outstanding is dropped. Items absent from {@code outstandingByItem} are treated as zero
	 * outstanding. Idempotent: re-running with the same offers prunes nothing further.
	 *
	 * @return {@code true} if any durable claim quantity was pruned
	 */
	boolean reconcileDurable(Map<Integer, Integer> outstandingByItem)
	{
		if (durableClaims.isEmpty())
			return false;

		Map<Integer, Integer> durableByItem = new LinkedHashMap<>();
		for (Claim c : durableClaims)
			durableByItem.merge(c.itemId, c.quantity, Integer::sum);

		boolean pruned = false;
		for (Map.Entry<Integer, Integer> entry : durableByItem.entrySet())
		{
			int itemId = entry.getKey();
			int outstanding = outstandingByItem.getOrDefault(itemId, 0);
			int excess = entry.getValue() - outstanding;
			if (excess <= 0)
				continue;

			Iterator<Claim> it = durableClaims.iterator();
			while (it.hasNext() && excess > 0)
			{
				Claim c = it.next();
				if (c.itemId != itemId)
					continue;

				int take = Math.min(excess, c.quantity);
				c.quantity -= take;
				excess -= take;
				pruned = true;
				if (c.quantity <= 0)
					it.remove();
			}
		}

		return pruned;
	}

	/**
	 * Discards expired ordinary claims; call once per tick. Durable (GE buy) claims have no TTL and
	 * are never touched here. No-op (and allocation-free) when idle.
	 */
	void expire(int currentTick)
	{
		if (claims.isEmpty())
			return;

		claims.removeIf(c -> c.expiryTick < currentTick);
	}

	/** Drops every open claim (logout, plugin shutdown), durable ones included. */
	void clear()
	{
		claims.clear();
		durableClaims.clear();
	}

	/** Drops only the durable (GE buy) claims, so a reload can replace them without disturbing live claims. */
	void clearDurable()
	{
		durableClaims.clear();
	}

	/**
	 * Serializes the open durable (GE buy) claims as the persisted GE buy ledger: item id to a
	 * FIFO list of {@code {quantity, unitPrice}} chunks. This is the on-disk shape the plugin has
	 * always written, so no schema change results from folding the ledger into the core (#180).
	 */
	Map<Integer, List<long[]>> exportDurable()
	{
		Map<Integer, List<long[]>> ledger = new LinkedHashMap<>();
		for (Claim c : durableClaims)
		{
			ledger.computeIfAbsent(c.itemId, k -> new ArrayList<>())
					.add(new long[]{c.quantity, c.unitPrice});
		}

		return ledger;
	}

	/**
	 * Rebuilds the durable (GE buy) claims from a persisted ledger; the reloaded chunks are GE
	 * trades. Call after {@link #clearDurable} so a login replaces rather than duplicates them.
	 *
	 * <p>A chunk that is {@code null} or shorter than {@code [quantity, unitPrice]} is skipped
	 * rather than indexed into: the value is valid JSON of the wrong shape, so it parses cleanly
	 * and would otherwise throw here, inside the login block (#329).
	 */
	void importDurable(Map<Integer, List<long[]>> ledger)
	{
		for (Map.Entry<Integer, List<long[]>> e : ledger.entrySet())
		{
			if (e.getKey() == null || e.getValue() == null)
				continue;

			for (long[] chunk : e.getValue())
			{
				if (chunk == null || chunk.length < 2)
					continue;

				durableClaims.addLast(new Claim(AcquisitionSource.GE_TRADE, e.getKey(), (int) chunk[0], chunk[1], 0));
			}
		}
	}
}
