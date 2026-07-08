/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.HashMap;
import java.util.Map;

/**
 * Turns the raw, cumulative {@code GrandExchangeOfferChanged} stream into discrete
 * per-slot increments — a placement, an incremental fill, or a cancellation — so the
 * plugin can price GE activity. Pure and client-free (callers pass the offer's
 * primitive fields), so it is unit-testable like {@link SourceAttributionCore}.
 *
 * <p>Each slot's {@code quantitySold}/{@code spent} are cumulative, so a fill's real
 * increment is the difference from the last event for that slot. The first event seen
 * per slot after a (re)login seeds a baseline and emits nothing, so an offer's existing
 * progress isn't replayed as fresh fills.
 */
class GeOfferTracker
{
	/** Which side an offer is: a buy adds items on collection, a sell removes them on placement. */
	enum Kind { BUY, SELL }

	/** What a single offer event means for pricing. */
	enum Type { PLACED, FILL, CANCELLED }

	/** One discrete change derived from an offer event. */
	static final class Event
	{
		final Type type;
		final Kind kind;
		final int itemId;
		final int quantity;
		final long unitPrice;

		Event(Type type, Kind kind, int itemId, int quantity, long unitPrice)
		{
			this.type = type;
			this.kind = kind;
			this.itemId = itemId;
			this.quantity = quantity;
			this.unitPrice = unitPrice;
		}
	}

	/** Cumulative progress last seen for one GE slot, used to compute each event's increment. */
	private static final class SlotState
	{
		int itemId;
		int lastQuantitySold;
		long lastSpent;
	}

	private final Map<Integer, SlotState> slots = new HashMap<>();

	/**
	 * Records an offer event and returns what it means, or {@code null} when it carries
	 * no actionable change (baseline seed, empty slot, or a no-progress update).
	 *
	 * @param buying whether the state is a buy side ({@code BUYING}/{@code BOUGHT}/{@code CANCELLED_BUY})
	 * @param cancelled whether the state is a cancellation ({@code CANCELLED_BUY}/{@code CANCELLED_SELL})
	 * @param empty whether the slot is now empty (offer collected/removed)
	 */
	Event onOffer(int slot, int itemId, boolean buying, boolean cancelled, boolean empty,
			int totalQuantity, int quantitySold, long spent)
	{
		if (empty)
		{
			slots.remove(slot);
			return null;
		}

		Kind kind = buying ? Kind.BUY : Kind.SELL;
		SlotState state = slots.get(slot);
		if (state == null)
		{
			state = new SlotState();
			state.itemId = itemId;
			state.lastQuantitySold = quantitySold;
			state.lastSpent = spent;
			slots.put(slot, state);

			if (quantitySold == 0 && !cancelled)
				return new Event(Type.PLACED, kind, itemId, totalQuantity, 0);

			return null;
		}

		int qtyDelta = quantitySold - state.lastQuantitySold;
		long coinsDelta = spent - state.lastSpent;
		state.lastQuantitySold = quantitySold;
		state.lastSpent = spent;

		if (cancelled)
		{
			int returned = totalQuantity - quantitySold;
			return returned > 0 ? new Event(Type.CANCELLED, kind, itemId, returned, 0) : null;
		}

		if (qtyDelta <= 0)
			return null;

		long unitPrice = coinsDelta / qtyDelta;
		return new Event(Type.FILL, kind, itemId, qtyDelta, unitPrice);
	}

	/** Drops all slot state (logout, plugin shutdown). */
	void clear()
	{
		slots.clear();
	}
}
