/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.ItemID;

/**
 * The player-trade source detector (#66): snapshots both sides of the trade window as they change,
 * suspends the items we offer while the trade is open, and books the completed exchange as
 * {@link AcquisitionSource#PLAYER_TRADE} - received items buy in, and given items close, at the gp on the
 * other side apportioned across them by market value.
 *
 * <p>The last piece of the #334 detector extraction, moved out of {@code StockpilePlugin} behind
 * {@link DetectorHost} so it can be tested without a client.
 */
class TradeDetector
{
	/** Gp value of one platinum token, the coin-equivalent currency for trades above max cash. */
	static final long PLATINUM_TOKEN_GP = 1_000L;

	private final DetectorHost host;

	private final CostBasisLedger ledger;

	/** Latest captured trade-offer sides (canonical id → qty), read when the trade completes. */
	private final Map<Integer, Integer> myOffer = new HashMap<>();
	private final Map<Integer, Integer> theirOffer = new HashMap<>();

	TradeDetector(DetectorHost host, CostBasisLedger ledger)
	{
		this.host = host;
		this.ledger = ledger;
	}

	/**
	 * Snapshots one side of the trade window (canonical id → quantity) as its container changes. For our
	 * own side, diffs the new offer against the previous snapshot and queues the change so the matching
	 * inventory removal suspends (rather than closes) the offered lots, and a later withdrawal
	 * un-suspends them.
	 *
	 * @param mine whether this is our side of the trade rather than the partner's
	 */
	void onOfferChanged(ItemContainer container, boolean mine)
	{
		Map<Integer, Integer> side = mine ? myOffer : theirOffer;
		Map<Integer, Integer> previous = mine ? new HashMap<>(side) : null;
		side.clear();
		if (container != null)
		{
			for (Item item : container.getItems())
			{
				if (item.getId() > 0)
					side.merge(host.canonicalize(item.getId()), item.getQuantity(), Integer::sum);
			}
		}

		if (mine)
			queueSuspension(previous, side);
	}

	/**
	 * Turns the change in our own offer into pending suspend/un-suspend intents: items added to the offer
	 * left our inventory and should suspend, items withdrawn returned and should un-suspend. Only tracked,
	 * non-currency items queue — coins and platinum tokens are the trade's numerator, not a lot, and
	 * untracked items never flow through {@link CostBasisLedger#applyDelta} to consume the intent.
	 */
	private void queueSuspension(Map<Integer, Integer> before, Map<Integer, Integer> after)
	{
		if (!host.sourcePricing())
			return;

		ItemDeltas.forEachDelta(before, after, (id, delta) ->
		{
			if (isTradeCurrency(id) || !host.isTracked(id))
				return;

			if (delta > 0)
				ledger.queueTradeSuspend(id, delta);
			else
				ledger.queueTradeUnsuspend(id, -delta);
		});
	}

	/**
	 * Books the completed trade's item movements: items received buy in at the gp we gave apportioned
	 * across them by market value, and items given close at the gp we received apportioned the same way.
	 * Pure item-for-item legs price at 0; coins and platinum tokens are the numerator, never an
	 * apportionment target.
	 *
	 * <p>The two sides settle differently. Received items only enter our inventory now, so they are
	 * registered as claims for the imminent additions to match. Given items already left our inventory
	 * when they were offered (suspended, not closed), so there is no delta to match — they are closed
	 * here directly against their trade suspension.
	 */
	void onTradeAccepted()
	{
		long gpPaid = tradeGp(myOffer);
		long gpReceived = tradeGp(theirOffer);

		claimReceivedItems(theirOffer, gpPaid);
		closeGivenItems(myOffer, gpReceived);
		reset();
	}

	/** Forgets both captured sides (a completed trade, or a fresh login). */
	void reset()
	{
		myOffer.clear();
		theirOffer.clear();
	}

	/** @return whether the item is trade currency — coins or platinum tokens — rather than a lot-bearing leg. */
	static boolean isTradeCurrency(int itemId)
	{
		return itemId == ItemID.COINS || itemId == ItemID.PLATINUM;
	}

	/** @return one trade side's money in gp: coins plus platinum tokens at 1,000 gp each. */
	static long tradeGp(Map<Integer, Integer> side)
	{
		return side.getOrDefault(ItemID.COINS, 0) + PLATINUM_TOKEN_GP * side.getOrDefault(ItemID.PLATINUM, 0);
	}

	/** Builds one trade side's non-currency apportionment legs, each weighted by its unit market value. */
	private List<TradeApportioner.Leg> legs(Map<Integer, Integer> side)
	{
		List<TradeApportioner.Leg> legs = new ArrayList<>();
		for (Map.Entry<Integer, Integer> entry : side.entrySet())
		{
			if (!isTradeCurrency(entry.getKey()) && entry.getValue() > 0)
				legs.add(new TradeApportioner.Leg(entry.getKey(), entry.getValue(), marketUnitValue(entry.getKey())));
		}

		return legs;
	}

	/** Claims received items as buys at the apportioned per-unit price, matched by their inventory additions. */
	private void claimReceivedItems(Map<Integer, Integer> side, long gp)
	{
		List<TradeApportioner.Leg> legs = legs(side);
		Map<Integer, Long> prices = TradeApportioner.apportion(legs, gp);
		for (TradeApportioner.Leg leg : legs)
		{
			if (host.isTracked(leg.itemId))
				ledger.claim(AcquisitionSource.PLAYER_TRADE, leg.itemId, leg.quantity, prices.get(leg.itemId),
						host.currentTick());
		}
	}

	/**
	 * Closes given items as sells at the apportioned per-unit price, realizing them against the trade
	 * suspension taken when they were offered. Any leg whose suspension has not landed yet — a same-tick
	 * offer+accept where "Accepted trade." outran the offer's inventory decrease — is parked by the ledger
	 * and retried after the container sync, exactly as the GE sell path does, so the sale is never dropped
	 * (#175).
	 */
	private void closeGivenItems(Map<Integer, Integer> side, long gp)
	{
		List<TradeApportioner.Leg> legs = legs(side);
		Map<Integer, Long> prices = TradeApportioner.apportion(legs, gp);
		for (TradeApportioner.Leg leg : legs)
		{
			if (host.isTracked(leg.itemId))
				ledger.realizeTradeSale(leg.itemId, leg.quantity, prices.get(leg.itemId));
		}
	}

	/** @return an item's unit market value for apportionment weights: the tracked avg, or the guide price. */
	private long marketUnitValue(int itemId)
	{
		TrackedItem tracked = host.trackedItem(itemId);
		if (tracked != null && tracked.getAvgPrice() > 0)
			return tracked.getAvgPrice();

		return host.guidePrice(itemId);
	}
}
