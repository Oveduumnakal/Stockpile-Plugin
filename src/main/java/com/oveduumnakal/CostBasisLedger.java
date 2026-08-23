/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;

/**
 * The cost-basis / GE trade ledger (#255), extracted from {@code StockpilePlugin}. Owns the FIFO lot
 * engine that turns container quantity deltas into open and closed {@link AcquisitionRecord}s, plus
 * the GE-offer pricing, source-attributed suspensions, and buy-limit windows built on top of it.
 *
 * <p>Mostly client-free: the logic operates on plain values and domain {@link TrackedItem}s, reaching
 * the live client/config/panel only through a {@link LedgerHost} seam. Detectors that still live in
 * the plugin feed it observed events through the {@code queue*}/{@code signal*}/{@link #claim} mutators
 * and drive its per-tick sweeps. That keeps the attribution and FIFO behaviour — which CI cannot
 * smoke-test — unit-testable in isolation.
 */
class CostBasisLedger
{
	/** How many ticks after a death removals still count as death losses (respawn wipe + lag). */
	private static final int DEATH_LOSS_WINDOW_TICKS = 15;

	/** How many ticks past the first consumed death loss the same death may keep consuming. */
	private static final int DEATH_LOSS_BATCH_GRACE_TICKS = 1;

	/** Once an expired gravestone has been gone this many ticks, its remaining suspensions close as lost. */
	private static final int GRAVE_RECOVERY_GRACE_TICKS = 5;

	/** The rolling GE buy-limit window length. */
	private static final Duration BUY_LIMIT_WINDOW = Duration.ofHours(4);

	/** How often at most the GE ledger/window are rewritten to config during activity. */
	private static final Duration GE_STATE_SAVE_INTERVAL = Duration.ofMinutes(1);

	private final LedgerHost host;

	private final StockpilePersistence persistence;

	/**
	 * Matches detector claims to observed quantity deltas, and holds GE buy fills awaiting
	 * collection as durable claims (#180); see {@link SourceAttributionCore}.
	 */
	private final SourceAttributionCore sourceAttribution = new SourceAttributionCore();

	/** Derives discrete increments from the raw GE offer stream; see {@link GeOfferTracker}. */
	private final GeOfferTracker geOfferTracker = new GeOfferTracker();

	/** Per-item rolling buy-limit window: {@code {windowStartEpochSeconds, quantityBought}}. Persisted. */
	private final Map<Integer, long[]> geBuyLimits = new HashMap<>();

	/** Units of a just-placed GE sell awaiting the container decrease that suspends them. */
	private final Map<Integer, Integer> pendingSellSuspend = new HashMap<>();

	/** Units of a cancelled GE sell awaiting the container increase that un-suspends them. */
	private final Map<Integer, Integer> pendingSellUnsuspend = new HashMap<>();

	/**
	 * Realize-at-price settlements (GE sell fills, accepted trades) that outran their suspension,
	 * parked per {@link SuspensionSource} until the offer/trade removal lands and suspends the units,
	 * each chunk {@code {quantity, unitPrice}}. Drained by {@link #flushPendingRealize()} every tick.
	 */
	private final Map<SuspensionSource, Map<Integer, Deque<long[]>>> pendingRealize =
			new EnumMap<>(SuspensionSource.class);

	/** Units dropped on the floor this tick awaiting the container decrease that suspends them. */
	private final Map<Integer, Integer> pendingGroundSuspend = new HashMap<>();

	/** Units re-picked-up from our own drops awaiting the container increase that un-suspends them. */
	private final Map<Integer, Integer> pendingGroundUnsuspend = new HashMap<>();

	/** Units offered into a player trade awaiting the container decrease that suspends them. */
	private final Map<Integer, Integer> pendingTradeSuspend = new HashMap<>();

	/** Units withdrawn from a player trade awaiting the container increase that un-suspends them. */
	private final Map<Integer, Integer> pendingTradeUnsuspend = new HashMap<>();

	/** Per-output transferred basis of a processing action awaiting its produced units (#69). */
	private final Map<Integer, Long> pendingProcessingOutput = new HashMap<>();

	/** Per-output transferred basis of a decant awaiting its produced potions (#220). */
	private final Map<Integer, Long> pendingDecantOutput = new HashMap<>();

	/** Per-output transferred basis of a drunk dose awaiting the lower-dose potion (#218). */
	private final Map<Integer, Long> pendingConsumedOutput = new HashMap<>();

	private Instant lastGeStateSave;

	/** The tick a fur/meat pouch was "Fill"ed on; -1 when none (#214). */
	private int pouchFillTick = -1;

	/** The tick a fur/meat pouch was emptied to the bank on; -1 when none (#214). */
	private int pouchDepositTick = -1;

	/** The tick a potion was "Empty"-clicked (discarded) on; -1 when none (#232). */
	private int potionDiscardTick = -1;

	/** The tick empty vessels were freed by finishing a potion/drink; -1 when none (#218). */
	private int potionEmptiedTick = -1;

	/** How many empty vessels were freed on {@link #potionEmptiedTick}. */
	private int potionEmptiedCount = 0;

	/** The tick the local player died, opening the death-loss window; -1 when none (#70). */
	private int deathTick = -1;

	/** The tick the current death first consumed a loss, bounding it to its own batch; -1 when none. */
	private int deathLossTick = -1;

	/** True once the player's gravestone has been observed active, so its later loss is a real transition. */
	private boolean graveSeen = false;

	/** The tick the observed gravestone vanished (collected or expired), pending the grace check; -1 when none. */
	private int graveGoneTick = -1;

	CostBasisLedger(LedgerHost host, StockpilePersistence persistence)
	{
		this.host = host;
		this.persistence = persistence;
	}

	/**
	 * Registers a detector's expectation that {@code quantity} units of {@code itemId} are about to
	 * change hands at {@code unitPrice} gp each; see {@link SourceAttributionCore#claim}.
	 */
	void claim(AcquisitionSource source, int itemId, int quantity, long unitPrice, int currentTick)
	{
		sourceAttribution.claim(source, itemId, quantity, unitPrice, currentTick);
	}

	/** Discards expired detector claims; call once per tick. */
	void expireClaims(int currentTick)
	{
		sourceAttribution.expire(currentTick);
	}

	/**
	 * Drives the GE offer tracker with one raw offer event and applies each discrete increment it
	 * derives (placement, fill, or cancellation); see {@link GeOfferTracker#onOffer}.
	 */
	void onGeOffer(int slot, int itemId, boolean buying, boolean cancelled, boolean empty,
			int totalQuantity, int quantitySold, long spent)
	{
		for (GeOfferTracker.Event e : geOfferTracker.onOffer(slot, itemId, buying, cancelled, empty,
				totalQuantity, quantitySold, spent))
			handleGeEvent(e);
	}

	/**
	 * Applies one derived GE event: ledger a buy, suspend/realize/restore a sell, and record
	 * the buy limit. With Source-Based Pricing off, no new pricing state is created — buys
	 * aren't ledgered (their additions price classically) and placements don't suspend (their
	 * removals close classically at the average price) — while fills and cancels still drain
	 * suspensions taken while the toggle was on, so nothing is stranded. Buy-limit tracking
	 * is informational, not pricing, and stays on either way.
	 */
	private void handleGeEvent(GeOfferTracker.Event e)
	{
		if (e.kind == GeOfferTracker.Kind.BUY)
		{
			if (e.type == GeOfferTracker.Type.FILL)
			{
				recordBuyLimit(e.itemId, e.quantity);
				if (host.sourcePricing())
				{
					sourceAttribution.claimDurable(AcquisitionSource.GE_TRADE, e.itemId, e.quantity, e.unitPrice);
					scheduleGeStateSave();
				}
			}

			return;
		}

		switch (e.type)
		{
			case PLACED:
				if (host.sourcePricing())
					pendingSellSuspend.merge(e.itemId, e.quantity, Integer::sum);

				break;
			case FILL:
				realizeSell(e.itemId, e.quantity, e.unitPrice);
				break;
			case CANCELLED:
				pendingSellUnsuspend.merge(e.itemId, e.quantity, Integer::sum);
				break;
			default:
				break;
		}
	}

	/** Realizes a completed GE sell fill against its SELL suspension, then debounces a GE-state save. */
	private void realizeSell(int itemId, int qty, long unitPrice)
	{
		realize(SuspensionSource.SELL, itemId, qty, unitPrice);
		scheduleGeStateSave();
	}

	/**
	 * Realizes a completed player trade against its {@link SuspensionSource#TRADE} suspension —
	 * the same shortfall-parking race fix the GE sell path carries (#175), so a same-tick offer+accept
	 * that outruns the offer's inventory decrease no longer drops the sale.
	 */
	void realizeTradeSale(int itemId, int qty, long unitPrice)
	{
		realize(SuspensionSource.TRADE, itemId, qty, unitPrice);
	}

	/**
	 * Closes {@code qty} suspended units of a settled sale at its realized {@code unitPrice}, booking the
	 * source's {@link SuspensionSource#realizeSource()}. Any part whose suspension has not yet landed (the
	 * settlement event outran the container removal) is parked and retried by {@link #flushPendingRealize()}.
	 */
	private void realize(SuspensionSource source, int itemId, int qty, long unitPrice)
	{
		TrackedItem tracked = host.trackedItem(itemId);
		if (tracked == null)
			return;

		int realized = Math.min(qty, tracked.getSuspended(source));
		if (realized > 0)
		{
			closeFifo(tracked, realized, unitPrice, source.realizeSource());
			tracked.reduceSuspended(source, realized);
			host.persistTrackedItems();
			host.refreshPanel();
		}

		int shortfall = qty - realized;
		if (shortfall > 0 && host.sourcePricing())
			pendingRealize.computeIfAbsent(source, k -> new HashMap<>())
					.computeIfAbsent(itemId, k -> new ArrayDeque<>())
					.addLast(new long[]{shortfall, unitPrice});
	}

	/**
	 * Closes any settled sale that outran its suspension, now that the offer/trade removal has moved the
	 * units into their {@link SuspensionSource} suspension. Runs each tick after the container sync;
	 * unmatched settlements stay parked and retry on a later tick.
	 */
	void flushPendingRealize()
	{
		if (pendingRealize.isEmpty())
			return;

		boolean changed = false;
		boolean geChanged = false;
		Iterator<Map.Entry<SuspensionSource, Map<Integer, Deque<long[]>>>> sources =
				pendingRealize.entrySet().iterator();
		while (sources.hasNext())
		{
			Map.Entry<SuspensionSource, Map<Integer, Deque<long[]>>> sourceEntry = sources.next();
			SuspensionSource source = sourceEntry.getKey();
			if (flushRealizeSource(source, sourceEntry.getValue()))
			{
				changed = true;
				geChanged |= source == SuspensionSource.SELL;
			}

			if (sourceEntry.getValue().isEmpty())
				sources.remove();
		}

		if (changed)
		{
			host.persistTrackedItems();
			host.refreshPanel();
			if (geChanged)
				scheduleGeStateSave();
		}
	}

	/**
	 * Drains one source's parked settlements against its now-landed suspensions, dropping emptied queues.
	 *
	 * @return whether any parked units were realized (a lot closed)
	 */
	private boolean flushRealizeSource(SuspensionSource source, Map<Integer, Deque<long[]>> queues)
	{
		boolean changed = false;
		Iterator<Map.Entry<Integer, Deque<long[]>>> it = queues.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<Integer, Deque<long[]>> entry = it.next();
			TrackedItem tracked = host.trackedItem(entry.getKey());
			if (tracked == null)
			{
				it.remove();
				continue;
			}

			Deque<long[]> queue = entry.getValue();
			while (!queue.isEmpty() && tracked.getSuspended(source) > 0)
			{
				long[] chunk = queue.peekFirst();
				int realize = (int) Math.min(chunk[0], tracked.getSuspended(source));
				closeFifo(tracked, realize, chunk[1], source.realizeSource());
				tracked.reduceSuspended(source, realize);
				chunk[0] -= realize;
				changed = true;
				if (chunk[0] <= 0)
					queue.removeFirst();
			}

			if (queue.isEmpty())
				it.remove();
		}

		return changed;
	}

	/** Accumulates a GE purchase into the item's rolling buy-limit window, rolling the window over when it expires. */
	private void recordBuyLimit(int itemId, int qty)
	{
		long now = Instant.now().getEpochSecond();
		long[] window = geBuyLimits.get(itemId);
		if (window == null || now >= window[0] + BUY_LIMIT_WINDOW.getSeconds())
			geBuyLimits.put(itemId, new long[]{now, qty});
		else
			window[1] += qty;
	}

	/**
	 * Prices one item's net container delta. On a gain: restore trade/sell suspensions, then open
	 * positively-detected same-tick production — processing and decant outputs, which carry transferred
	 * cost basis — <em>before</em> draining the GE buy ledger, so a lingering buy for that same id can't
	 * steal a decant/processing output (#220); then the remaining GE and suspension routing, the
	 * source-attribution claim, and finally the classic fallback.
	 */
	void applyDelta(TrackedItem tracked, int delta)
	{
		int itemId = tracked.getItemId();
		if (delta > 0)
		{
			int remaining = delta;
			remaining = consumeTradeUnsuspend(tracked, remaining);
			remaining = consumeSellUnsuspend(tracked, remaining);
			remaining = consumeProcessingOutput(tracked, remaining);
			remaining = consumeDecantOutput(tracked, remaining);
			remaining = consumeConsumedOutput(tracked, remaining);
			remaining = consumeEmptyContainerByproduct(tracked, remaining);
			remaining = consumeBuyLedger(tracked, remaining);
			remaining = consumeDeathUnsuspend(tracked, remaining);
			remaining = consumeGroundUnsuspend(tracked, remaining);
			remaining = consumeFiredAmmoRecovery(tracked, remaining);
			remaining = consumePouchUnsuspend(tracked, remaining);
			if (remaining > 0 && isPouchDepositTick())
			{
				addOpenAcquisition(tracked, remaining, 0, AcquisitionSource.GATHER);
				remaining = 0;
			}

			if (remaining > 0)
			{
				SourceAttributionCore.Attribution a = attributeDelta(itemId, remaining);
				addOpenAcquisition(tracked, remaining, a.unitPriceOr(fallbackPrice(tracked)), a.source());
			}
		}
		else
		{
			int mag = consumePouchSuspend(tracked, -delta);
			mag = consumeTradeSuspend(tracked, mag);
			mag = consumeSellSuspend(tracked, mag);
			mag = consumeGroundSuspend(tracked, mag);
			mag = consumeDeathLoss(tracked, mag);
			if (mag > 0)
			{
				SourceAttributionCore.Attribution a = attributeDelta(itemId, mag);
				boolean unclaimed = a.source() == AcquisitionSource.UNKNOWN && host.sourcePricing();
				if (unclaimed && isPotionDiscardTick())
					closeFifo(tracked, mag, 0, AcquisitionSource.GROUND);
				else if (unclaimed && host.isConsumable(itemId))
					closeFifo(tracked, mag, 0, AcquisitionSource.CONSUMED);
				else if (unclaimed && host.isDestroyedAmmo(itemId))
					closeFifo(tracked, mag, 0, AcquisitionSource.DESTROYED);
				else if (unclaimed && host.isRecoverableAmmo(itemId))
					suspendFiredAmmo(tracked, mag);
				else
					closeFifo(tracked, mag, a.unitPriceOr(tracked.getAvgPrice()), a.source());
			}
		}
	}

	/**
	 * Suspends removals in the post-death window (#70): the units were lost to the
	 * death, so quantities drop but the lots stay open pending gravestone/Death's
	 * Office recovery. Consumption is bounded to the death's own container batch —
	 * the first tick that consumes, plus a one-tick grace for a split
	 * inventory/equipment sync — so ordinary removals later in the window (eating
	 * after respawning, dropping an item) close normally instead of being misbooked
	 * as 0-gp death losses. The suspension timestamp is only set when none exists,
	 * so a second death can't reset the first's recovery-expiry clock. Returns the
	 * unconsumed remainder (0 when consumed).
	 */
	private int consumeDeathLoss(TrackedItem tracked, int qty)
	{
		int tick = host.currentTick();
		if (qty <= 0 || !host.sourcePricing() || deathTick < 0
				|| tick - deathTick > DEATH_LOSS_WINDOW_TICKS)
			return qty;

		if (deathLossTick >= 0 && tick - deathLossTick > DEATH_LOSS_BATCH_GRACE_TICKS)
		{
			deathTick = -1;
			return qty;
		}

		if (deathLossTick < 0)
			deathLossTick = tick;

		tracked.addSuspended(SuspensionSource.DEATH, qty);
		return 0;
	}

	/**
	 * Greedily restores an addition from death suspension — a recovery reactivates
	 * the suspended lots with their basis intact, opening nothing new. Returns the
	 * unconsumed remainder.
	 */
	private int consumeDeathUnsuspend(TrackedItem tracked, int qty)
	{
		if (qty <= 0 || tracked.getSuspended(SuspensionSource.DEATH) <= 0)
			return qty;

		int restore = tracked.reduceSuspended(SuspensionSource.DEATH, qty);
		return qty - restore;
	}

	/**
	 * Closes every suspension that outlived its source's {@link SuspensionSource#expiry()} as an
	 * unrecovered loss at 0 gp, booked under that source's {@link SuspensionSource#closeSource()}.
	 * One sweep now covers ground drops and death losses alike (#179); the gravestone-grace fast
	 * path that closes a death sooner stays in {@link #closeVanishedGraveLosses()}.
	 */
	void expireSuspensions()
	{
		Instant now = Instant.now();
		boolean changed = false;
		for (TrackedItem tracked : host.trackedItems())
		{
			for (SuspensionSource source : SuspensionSource.values())
			{
				if (source.expiry() == null)
					continue;

				int qty = tracked.getSuspended(source);
				Instant at = tracked.getSuspendedAt(source);
				if (qty <= 0 || at == null || !at.isBefore(now.minus(source.expiry())))
					continue;

				closeFifo(tracked, qty, 0, source.closeSource());
				tracked.clearSuspended(source);
				changed = true;
			}
		}

		if (changed)
		{
			host.persistTrackedItems();
			host.refreshPanel();
		}
	}

	/**
	 * Records the local player's gravestone visibility (#70). A grave that vanishes after its
	 * duration ran out ({@code durationExpired}) has expired and its items are lost, so this arms the
	 * grace check in {@link #closeVanishedGraveLosses()}. A grave that vanishes with time still on the
	 * clock was collected — its returning items un-suspend themselves, so no loss is armed.
	 */
	void onGravestoneVisibility(boolean present, boolean durationExpired)
	{
		if (present)
		{
			graveSeen = true;
			graveGoneTick = -1;
		}
		else if (graveSeen)
		{
			graveSeen = false;
			if (durationExpired)
				graveGoneTick = host.currentTick();
		}
	}

	/**
	 * Once an expired gravestone has been gone for {@link #GRAVE_RECOVERY_GRACE_TICKS},
	 * closes any death suspension it left standing as lost at 0 (#70). The grace absorbs a
	 * last-tick collection whose items are still landing; anything still suspended after it
	 * is a genuine loss, so the collection log reflects it the moment the grave expires
	 * rather than after the blunt {@link SuspensionSource#DEATH death} expiry fallback.
	 */
	void closeVanishedGraveLosses()
	{
		if (graveGoneTick < 0 || host.currentTick() - graveGoneTick < GRAVE_RECOVERY_GRACE_TICKS)
			return;

		graveGoneTick = -1;
		boolean changed = false;
		for (TrackedItem tracked : host.trackedItems())
		{
			if (tracked.getSuspended(SuspensionSource.DEATH) <= 0)
				continue;

			closeFifo(tracked, tracked.getSuspended(SuspensionSource.DEATH), 0, AcquisitionSource.DEATH);
			tracked.clearSuspended(SuspensionSource.DEATH);
			changed = true;
		}

		if (changed)
		{
			host.persistTrackedItems();
			host.refreshPanel();
		}
	}

	/**
	 * Moves up to this tick's correlated drop quantity of a removal into ground
	 * suspension — the units left the containers but sit on the floor, still owned,
	 * lots untouched. Returns the unconsumed remainder.
	 */
	private int consumeGroundSuspend(TrackedItem tracked, int qty)
	{
		Integer pending = pendingGroundSuspend.get(tracked.getItemId());
		if (qty <= 0 || pending == null || pending <= 0)
			return qty;

		int take = Math.min(qty, pending);
		int left = pending - take;
		if (left > 0)
			pendingGroundSuspend.put(tracked.getItemId(), left);
		else
			pendingGroundSuspend.remove(tracked.getItemId());

		tracked.addSuspended(SuspensionSource.GROUND, take);
		return qty - take;
	}

	/**
	 * Suspends fired recoverable ammo on the ground path (#234): the units left the ammo slot but landed
	 * on the target's tile, still owned with their basis intact. They un-suspend when picked back up
	 * ({@link #consumeFiredAmmoRecovery}), or close as a 0-gp {@link AcquisitionSource#GROUND} loss once the
	 * suspension outlives the {@link SuspensionSource#GROUND ground} expiry ({@link #expireSuspensions}) — which also
	 * covers the shots that broke on impact and were never really recoverable. Reuses the drop machinery's
	 * suspension counter rather than a menu/animation hook, so an Ava's-device catch (no delta) is a no-op.
	 */
	private void suspendFiredAmmo(TrackedItem tracked, int qty)
	{
		tracked.addSuspended(SuspensionSource.GROUND, qty);
	}

	/**
	 * Restores an addition from ground suspension, but only up to what an actual
	 * re-pickup of one of our dropped {@code TileItem}s queued — so a same-item pickup
	 * from an unrelated source (a monster drop while our own is on the floor) can't
	 * cancel the suspension and instead gets its own 0-cost ground lot. A re-pickup of
	 * our drop is the net no-op that opens no new lot. Returns the unconsumed remainder.
	 */
	private int consumeGroundUnsuspend(TrackedItem tracked, int qty)
	{
		Integer pending = pendingGroundUnsuspend.get(tracked.getItemId());
		int suspended = tracked.getSuspended(SuspensionSource.GROUND);
		if (qty <= 0 || pending == null || pending <= 0 || suspended <= 0)
			return qty;

		int restore = Math.min(qty, Math.min(pending, suspended));
		int left = pending - restore;
		if (left > 0)
			pendingGroundUnsuspend.put(tracked.getItemId(), left);
		else
			pendingGroundUnsuspend.remove(tracked.getItemId());

		tracked.reduceSuspended(SuspensionSource.GROUND, restore);
		return qty - restore;
	}

	/**
	 * Restores picked-up ammo from ground suspension (#234): a fired-ammo lot lands on the target's tile
	 * with no {@code TileItem} of ours to key off, so its recovery can't route through
	 * {@link #consumeGroundUnsuspend}. When a gain of recoverable ammo finds units still suspended on the
	 * ground, it un-suspends them at their original basis — the net no-op that opens no new lot — instead of
	 * the phantom 0-gp {@link AcquisitionSource#GROUND} re-buy that would otherwise collapse the stack's cost
	 * basis. Runs after {@link #consumeGroundUnsuspend} so a hand-dropped stack resolves through its own
	 * {@code TileItem} first. Returns the unconsumed remainder.
	 */
	private int consumeFiredAmmoRecovery(TrackedItem tracked, int qty)
	{
		int suspended = tracked.getSuspended(SuspensionSource.GROUND);
		if (qty <= 0 || suspended <= 0 || !host.sourcePricing() || !host.isRecoverableAmmo(tracked.getItemId()))
			return qty;

		int restore = tracked.reduceSuspended(SuspensionSource.GROUND, qty);
		return qty - restore;
	}

	/**
	 * Moves a removal into fur/meat-pouch suspension when it lands on the tick the pouch was
	 * "Fill"ed — the units left the inventory into the pouch but stay owned, lots (and their
	 * original source/basis) intact, until the pouch is emptied. Consumes the whole removal,
	 * since a Fill click's only container effect is the furs/meats leaving the inventory.
	 * Returns the unconsumed remainder (0 while the fill tick is live) (#214).
	 */
	private int consumePouchSuspend(TrackedItem tracked, int qty)
	{
		if (qty <= 0 || !host.sourcePricing() || pouchFillTick < 0
				|| host.currentTick() - pouchFillTick > 1)
			return qty;

		tracked.addSuspended(SuspensionSource.POUCH, qty);
		return 0;
	}

	/**
	 * Restores an addition from fur/meat-pouch suspension on an empty-to-bank tick, up to what
	 * was filled in — those units re-enter tracked containers as the net no-op that reopens no
	 * lot, keeping their original source and basis. Any surplus beyond the suspended amount is
	 * left for the caller to book as freshly-gathered {@code GATHER}. Returns the unconsumed
	 * remainder (#214).
	 */
	private int consumePouchUnsuspend(TrackedItem tracked, int qty)
	{
		if (qty <= 0 || tracked.getSuspended(SuspensionSource.POUCH) <= 0 || !isPouchDepositTick())
			return qty;

		int restore = tracked.reduceSuspended(SuspensionSource.POUCH, qty);
		return qty - restore;
	}

	/** @return whether a fur/meat pouch was emptied to the bank on (or one tick before) this tick (#214). */
	private boolean isPouchDepositTick()
	{
		return host.sourcePricing() && pouchDepositTick >= 0 && host.currentTick() - pouchDepositTick <= 1;
	}

	/** @return whether a potion was "Empty"-clicked on (or one tick before) this tick, discarding it (#232). */
	private boolean isPotionDiscardTick()
	{
		return potionDiscardTick >= 0 && host.currentTick() - potionDiscardTick <= 1;
	}

	/**
	 * Moves up to {@code qty} of a removal into trade suspension — the units were placed into a
	 * player-trade offer, so they left the containers but stay owned with their lots intact until
	 * the trade finalizes or is withdrawn. Returns the unconsumed remainder.
	 */
	private int consumeTradeSuspend(TrackedItem tracked, int qty)
	{
		Integer pending = pendingTradeSuspend.get(tracked.getItemId());
		if (qty <= 0 || pending == null || pending <= 0)
			return qty;

		int take = Math.min(qty, pending);
		int left = pending - take;
		if (left > 0)
			pendingTradeSuspend.put(tracked.getItemId(), left);
		else
			pendingTradeSuspend.remove(tracked.getItemId());

		tracked.addSuspended(SuspensionSource.TRADE, take);
		return qty - take;
	}

	/**
	 * Restores an addition from trade suspension — an offered item withdrawn from the trade
	 * returns to the inventory, a net no-op that opens no new lot. Bounded by both the queued
	 * withdrawal and the units actually suspended. Returns the unconsumed remainder.
	 */
	private int consumeTradeUnsuspend(TrackedItem tracked, int qty)
	{
		Integer pending = pendingTradeUnsuspend.get(tracked.getItemId());
		int suspended = tracked.getSuspended(SuspensionSource.TRADE);
		if (qty <= 0 || pending == null || pending <= 0 || suspended <= 0)
			return qty;

		int restore = Math.min(qty, Math.min(pending, suspended));
		int left = pending - restore;
		if (left > 0)
			pendingTradeUnsuspend.put(tracked.getItemId(), left);
		else
			pendingTradeUnsuspend.remove(tracked.getItemId());

		tracked.reduceSuspended(SuspensionSource.TRADE, restore);
		return qty - restore;
	}

	/**
	 * Opens the output lot(s) of a processing action (#69), carrying the transferred
	 * input basis so their cost sums <em>exactly</em> to it.
	 */
	private int consumeProcessingOutput(TrackedItem tracked, int qty)
	{
		return consumeCarriedOutput(tracked, qty, pendingProcessingOutput, AcquisitionSource.PROCESSING);
	}

	/**
	 * Opens the output lot(s) of a decant (#220), carrying the combined dose-weighted input
	 * basis so the swapped potion keeps its cost — no profit is realized on the swap.
	 */
	private int consumeDecantOutput(TrackedItem tracked, int qty)
	{
		return consumeCarriedOutput(tracked, qty, pendingDecantOutput, AcquisitionSource.DECANT);
	}

	/**
	 * Opens the lower-dose potion left after a dose is drunk (#218), carrying the full basis of the
	 * higher-dose lot so using a dose realizes no profit or loss — the cost simply follows the liquid.
	 */
	private int consumeConsumedOutput(TrackedItem tracked, int qty)
	{
		return consumeCarriedOutput(tracked, qty, pendingConsumedOutput, AcquisitionSource.CONSUMED);
	}

	/**
	 * Opens the empty vessel(s) freed by finishing a potion or drink this tick (#218) at 0 — a
	 * leftover byproduct, not a purchase. Bounded to the number of vessels emptied, so any vials bought
	 * separately still price normally. The source matches the event: {@link AcquisitionSource#GROUND}
	 * when the potion was discarded via "Empty" (#232), so the whole drop sits under one glyph, else
	 * {@link AcquisitionSource#CONSUMED} for a drunk-dry potion. Returns the remainder.
	 */
	private int consumeEmptyContainerByproduct(TrackedItem tracked, int qty)
	{
		if (qty <= 0 || potionEmptiedCount <= 0 || host.currentTick() != potionEmptiedTick
				|| !host.isEmptyContainer(tracked.getItemId()))
			return qty;

		int free = Math.min(qty, potionEmptiedCount);
		potionEmptiedCount -= free;
		AcquisitionSource source = isPotionDiscardTick() ? AcquisitionSource.GROUND : AcquisitionSource.CONSUMED;
		addOpenAcquisition(tracked, free, 0, source);
		return qty - free;
	}

	/**
	 * Opens {@code qty} newly-produced units carrying the basis queued in {@code carried} for this
	 * item, tagged with {@code source}. An uneven split gives the remainder units one extra gp each
	 * — 13 gp across 60 units becomes 13 units at 1 gp plus 47 at 0 gp — since a single integer
	 * per-unit price can't hold a sub-gp basis. Consumes the whole addition (returns 0) so it
	 * bypasses the fallback auto-add.
	 */
	int consumeCarriedOutput(TrackedItem tracked, int qty, Map<Integer, Long> carried, AcquisitionSource source)
	{
		Long totalCost = carried.remove(tracked.getItemId());
		if (qty <= 0 || totalCost == null)
			return qty;

		long base = totalCost / qty;
		int remainder = (int) (totalCost % qty);
		if (remainder > 0)
			addOpenAcquisition(tracked, remainder, base + 1, source);

		addOpenAcquisition(tracked, qty - remainder, base, source);
		return 0;
	}

	/** Restores up to {@code qty} cancelled-sell units to held (un-suspends), returning the unconsumed remainder. */
	private int consumeSellUnsuspend(TrackedItem tracked, int qty)
	{
		Integer pending = pendingSellUnsuspend.get(tracked.getItemId());
		if (pending == null || pending <= 0)
			return qty;

		int take = Math.min(qty, Math.min(pending, tracked.getSuspended(SuspensionSource.SELL)));
		if (take <= 0)
			return qty;

		tracked.reduceSuspended(SuspensionSource.SELL, take);
		int left = pending - take;
		if (left > 0)
			pendingSellUnsuspend.put(tracked.getItemId(), left);
		else
			pendingSellUnsuspend.remove(tracked.getItemId());

		return qty - take;
	}

	/**
	 * Consumes up to {@code qty} from the item's GE buy ledger into priced lots, returning
	 * the unconsumed remainder.
	 */
	private int consumeBuyLedger(TrackedItem tracked, int qty)
	{
		List<long[]> chunks = sourceAttribution.attributeDurable(tracked.getItemId(), qty);
		if (chunks.isEmpty())
			return qty;

		int remaining = qty;
		for (long[] chunk : chunks)
		{
			addOpenAcquisition(tracked, (int) chunk[0], chunk[1], AcquisitionSource.GE_TRADE);
			remaining -= (int) chunk[0];
		}

		scheduleGeStateSave();
		return remaining;
	}

	/** Suspends up to {@code qty} units for a just-placed GE sell (no close), returning the unconsumed remainder. */
	private int consumeSellSuspend(TrackedItem tracked, int qty)
	{
		Integer pending = pendingSellSuspend.get(tracked.getItemId());
		if (pending == null || pending <= 0)
			return qty;

		int take = Math.min(qty, pending);
		tracked.addSuspended(SuspensionSource.SELL, take);
		int left = pending - take;
		if (left > 0)
			pendingSellSuspend.put(tracked.getItemId(), left);
		else
			pendingSellSuspend.remove(tracked.getItemId());

		scheduleGeStateSave();
		return qty - take;
	}

	/**
	 * Post-login GE reconciliation, run for each offer event inside the login window (when the offers
	 * array is finally populated, unlike at container sync). Seeds the offer tracker's baselines from
	 * the live offers so an offer that already existed at login is not replayed as a fresh placement or
	 * fill, drops the stale session sell-routing maps, and rebuilds {@code suspendedQuantity} from those
	 * offers so a later cancel un-suspends correctly instead of logging a phantom acquisition.
	 * Idempotent, so repeating it as the array fills in is safe.
	 */
	void primeGeStateFromLogin()
	{
		GrandExchangeOffer[] offers = host.openGeOffers();
		if (offers != null)
		{
			for (int slot = 0; slot < offers.length; slot++)
			{
				GrandExchangeOffer offer = offers[slot];
				if (offer == null || offer.getState() == GrandExchangeOfferState.EMPTY)
					continue;

				geOfferTracker.seed(slot, offer.getItemId(), offer.getQuantitySold(), offer.getSpent());
			}
		}

		pendingSellSuspend.clear();
		pendingSellUnsuspend.clear();
		pendingRealize.clear();
		seedCancelledSellReturns(offers);
		reconcileSuspendedFromOffers();
	}

	/**
	 * Queues the uncollected remainder of every cancelled sell offer as a pending un-suspend,
	 * so those units stay suspended (they are still the player's, sitting in the collection
	 * box) and collecting them restores the original lots instead of opening fresh ones.
	 * Runs after the login prime clears the pending maps, so re-priming stays idempotent.
	 */
	private void seedCancelledSellReturns(GrandExchangeOffer[] offers)
	{
		if (offers == null)
			return;

		for (GrandExchangeOffer offer : offers)
		{
			if (offer == null || offer.getState() != GrandExchangeOfferState.CANCELLED_SELL)
				continue;

			int returned = offer.getTotalQuantity() - offer.getQuantitySold();
			if (returned > 0)
				pendingSellUnsuspend.merge(offer.getItemId(), returned, Integer::sum);
		}
	}

	/**
	 * Rewrites {@code suspendedQuantity} from the live open sell offers plus the pending
	 * cancelled-sell returns (units cancelled but not yet collected, which are still the
	 * player's), so offline fills or cancels self-heal at login; released units are then
	 * re-priced by the caller's reconcile.
	 *
	 * <p>With Source-Based Pricing off no offer suspends: placements made while off were
	 * already closed classically (re-suspending them would double-count), and any leftover
	 * suspension from while the toggle was on zeroes here, letting the reconcile close those
	 * lots at the average price — the classic removal semantics the toggle promises.
	 */
	void reconcileSuspendedFromOffers()
	{
		if (!host.sourcePricing())
		{
			for (TrackedItem tracked : host.trackedItems())
				tracked.clearSuspended(SuspensionSource.SELL);

			return;
		}

		Map<Integer, Integer> openSell = new HashMap<>();
		GrandExchangeOffer[] offers = host.openGeOffers();
		if (offers != null)
		{
			for (GrandExchangeOffer offer : offers)
			{
				if (offer != null && offer.getState() == GrandExchangeOfferState.SELLING)
					openSell.merge(offer.getItemId(), offer.getTotalQuantity() - offer.getQuantitySold(), Integer::sum);
			}
		}

		for (TrackedItem tracked : host.trackedItems())
			tracked.setSuspended(SuspensionSource.SELL, openSell.getOrDefault(tracked.getItemId(), 0)
					+ pendingSellUnsuspend.getOrDefault(tracked.getItemId(), 0));
	}

	/** Sets the item's transient buy-limit fields from its window, clearing them when the window has expired. */
	void applyBuyLimitFields(TrackedItem item)
	{
		long[] window = geBuyLimits.get(item.getItemId());
		if (window == null || Instant.now().getEpochSecond() >= window[0] + BUY_LIMIT_WINDOW.getSeconds())
		{
			item.setLimitBought(0);
			item.setLimitResetEpoch(0);
			return;
		}

		item.setLimitBought((int) window[1]);
		item.setLimitResetEpoch(window[0] + BUY_LIMIT_WINDOW.getSeconds());
	}

	/** Persists the GE buy ledger (the durable claims) and buy-limit windows to the RS profile config. */
	void persist()
	{
		lastGeStateSave = Instant.now();
		persistence.saveGeState(sourceAttribution.exportDurable(), geBuyLimits);
	}

	/**
	 * Restores the GE buy ledger (as durable claims) and buy-limit windows from the RS profile
	 * config, defaulting to empty.
	 */
	void load()
	{
		sourceAttribution.clearDurable();
		geBuyLimits.clear();

		sourceAttribution.importDurable(persistence.loadGeLedger());
		geBuyLimits.putAll(persistence.loadGeBuyLimits());
	}

	/** Persists the GE state at most once per {@link #GE_STATE_SAVE_INTERVAL}. */
	private void scheduleGeStateSave()
	{
		if (lastGeStateSave == null
				|| Duration.between(lastGeStateSave, Instant.now()).compareTo(GE_STATE_SAVE_INTERVAL) >= 0)
			persist();
	}

	/**
	 * Attributes a quantity change against the open detector claims, honouring the
	 * Source-Based Pricing kill switch: when disabled, everything is
	 * {@link AcquisitionSource#UNKNOWN} and priced by the classic fallbacks.
	 */
	private SourceAttributionCore.Attribution attributeDelta(int itemId, int quantity)
	{
		if (!host.sourcePricing())
			return SourceAttributionCore.Attribution.UNKNOWN;

		return sourceAttribution.attribute(itemId, quantity, host.currentTick());
	}

	/**
	 * @return the cost-basis price to seed an unknown-source change with (an auto-add or any
	 * delta no detector observed), per the configured {@link FallbackPricing}.
	 */
	long fallbackPrice(TrackedItem tracked)
	{
		return host.fallbackPricing()
				.select(tracked.getHighPrice(), tracked.getLowPrice(), tracked.getAvgPrice());
	}

	/**
	 * Adds {@code qty} units to an item's held lots at {@code boughtAt} gp.
	 *
	 * <p>First it reverses any equal-and-opposite "wash" closes (a prior sell at
	 * the same price, which a re-acquire should cancel), then merges into an
	 * existing open lot at the same price, or appends a new lot.
	 */
	void addOpenAcquisition(TrackedItem tracked, int qty, long boughtAt, AcquisitionSource source)
	{
		if (qty <= 0)
			return;

		List<AcquisitionRecord> records = tracked.getAcquisitions();

		int undoBudget = qty;
		Iterator<AcquisitionRecord> it = records.iterator();
		while (it.hasNext() && undoBudget > 0)
		{
			AcquisitionRecord r = it.next();
			Long sold = r.getSoldAt();
			if (sold != null && r.getBoughtAt() == boughtAt && sold == boughtAt)
			{
				int undo = Math.min(r.getQuantity(), undoBudget);
				r.setQuantity(r.getQuantity() - undo);
				if (r.getQuantity() == 0)
					it.remove();

				undoBudget -= undo;
			}
		}

		for (AcquisitionRecord r : records)
		{
			if (r.getSoldAt() == null && r.getBoughtAt() == boughtAt && r.sourceOrUnknown() == source)
			{
				r.setQuantity(r.getQuantity() + qty);
				return;
			}
		}

		records.add(new AcquisitionRecord(qty, boughtAt, null, source));
	}

	/**
	 * Merges {@code qty} into an existing closed (sold) lot with the same
	 * bought/sold prices and sell provenance, to avoid fragmenting the log.
	 *
	 * @return {@code true} if a matching lot absorbed the quantity
	 */
	private boolean mergeClosed(List<AcquisitionRecord> records, int qty, long boughtAt, long soldAtPrice,
			AcquisitionSource sellSource)
	{
		for (AcquisitionRecord r : records)
		{
			Long sold = r.getSoldAt();
			if (sold != null && r.getBoughtAt() == boughtAt && sold == soldAtPrice
					&& r.sellSourceOrUnknown() == sellSource)
			{
				r.setQuantity(r.getQuantity() + qty);
				return true;
			}
		}

		return false;
	}

	/**
	 * Closes {@code amount} units of held inventory at {@code soldAtPrice},
	 * oldest lot first (FIFO), recording {@code sellSource} as the sale's
	 * provenance — {@link AcquisitionSource#UNKNOWN} marks the price as an
	 * estimate rather than an observed sale.
	 *
	 * <p>It first cancels any just-added open lots bought at the same price (a
	 * buy immediately followed by a sell nets out), then realizes the remaining
	 * amount across the oldest open lots, splitting a lot when only part of it is
	 * sold and merging into matching closed lots where possible.
	 */
	void closeFifo(TrackedItem tracked, int amount, long soldAtPrice, AcquisitionSource sellSource)
	{
		List<AcquisitionRecord> records = tracked.getAcquisitions();
		int remaining = amount;

		Iterator<AcquisitionRecord> cancelIt = records.iterator();
		while (cancelIt.hasNext() && remaining > 0)
		{
			AcquisitionRecord r = cancelIt.next();
			if (r.getSoldAt() == null && r.getBoughtAt() == soldAtPrice)
			{
				int cancel = Math.min(r.getQuantity(), remaining);
				r.setQuantity(r.getQuantity() - cancel);
				if (r.getQuantity() == 0)
					cancelIt.remove();

				remaining -= cancel;
			}
		}

		remaining = realizeOpenLots(records, remaining, soldAtPrice, sellSource, sellSource);
		realizeOpenLots(records, remaining, soldAtPrice, sellSource, null);
	}

	/**
	 * Realizes up to {@code remaining} units across the open lots oldest-first,
	 * closing (or splitting) each at {@code soldAtPrice} with {@code sellSource} and
	 * merging into a matching closed lot where possible. When {@code onlySource} is
	 * non-null, only lots that entered from that source are eligible — so a sell
	 * closes its own source's buys before any others (#137), with the caller running
	 * a matched pass followed by an unrestricted one.
	 *
	 * @return the units still unrealized after this pass
	 */
	private int realizeOpenLots(List<AcquisitionRecord> records, int remaining, long soldAtPrice,
			AcquisitionSource sellSource, AcquisitionSource onlySource)
	{
		int i = 0;
		while (i < records.size() && remaining > 0)
		{
			AcquisitionRecord r = records.get(i);
			if (r.getSoldAt() != null || (onlySource != null && r.sourceOrUnknown() != onlySource))
			{
				i++;
				continue;
			}

			if (r.getQuantity() <= remaining)
			{
				int closeQty = r.getQuantity();
				remaining -= closeQty;
				if (mergeClosed(records, closeQty, r.getBoughtAt(), soldAtPrice, sellSource))
				{
					records.remove(i);
				}
				else
				{
					r.setSoldAt(soldAtPrice);
					r.setSellSource(sellSource);
					i++;
				}
			}
			else
			{
				int closeQty = remaining;
				r.setQuantity(r.getQuantity() - closeQty);
				remaining = 0;
				if (!mergeClosed(records, closeQty, r.getBoughtAt(), soldAtPrice, sellSource))
				{
					AcquisitionRecord closed = new AcquisitionRecord(closeQty, r.getBoughtAt(), soldAtPrice,
							r.getSource());
					closed.setSellSource(sellSource);
					records.add(i, closed);
				}
			}
		}

		return remaining;
	}

	/**
	 * Closes a ground pile's units as lost at 0 (#234): the floor items were never recovered, so their
	 * suspended lots close under {@link AcquisitionSource#GROUND}. Bounded to what is actually suspended.
	 * Mutates only &mdash; the caller persists and refreshes once (#185), so several piles expiring in one
	 * tick don't each re-serialize the whole item list.
	 *
	 * @return whether any units were closed
	 */
	boolean closeGroundLost(int itemId, int qty)
	{
		TrackedItem tracked = host.trackedItem(itemId);
		if (tracked == null)
			return false;

		int lost = Math.min(qty, tracked.getSuspended(SuspensionSource.GROUND));
		if (lost <= 0)
			return false;

		tracked.reduceSuspended(SuspensionSource.GROUND, lost);
		closeFifo(tracked, lost, 0, AcquisitionSource.GROUND);
		return true;
	}

	/**
	 * Closes every remaining ground suspension as lost — floor items rarely survive a logout — and
	 * drops the pending ground routing maps. Persists and refreshes once after the sweep (#185). The
	 * caller clears its own drop tracking.
	 */
	void closeAllGroundSuspensions()
	{
		boolean changed = false;
		for (TrackedItem tracked : host.trackedItems())
		{
			if (tracked.getSuspended(SuspensionSource.GROUND) > 0)
				changed |= closeGroundLost(tracked.getItemId(), tracked.getSuspended(SuspensionSource.GROUND));
		}

		if (changed)
		{
			host.persistTrackedItems();
			host.refreshPanel();
		}

		pendingGroundSuspend.clear();
		pendingGroundUnsuspend.clear();
	}

	/** @return the units currently queued for ground suspension for {@code itemId}. */
	int pendingGroundSuspend(int itemId)
	{
		return pendingGroundSuspend.getOrDefault(itemId, 0);
	}

	/** Queues {@code qty} units of {@code itemId} for ground suspension (a correlated own-drop). */
	void queueGroundSuspend(int itemId, int qty)
	{
		pendingGroundSuspend.merge(itemId, qty, Integer::sum);
	}

	/** Queues {@code qty} units of {@code itemId} for ground un-suspension (a correlated re-pickup). */
	void queueGroundUnsuspend(int itemId, int qty)
	{
		pendingGroundUnsuspend.merge(itemId, qty, Integer::sum);
	}

	/** Queues {@code qty} units of {@code itemId} for trade suspension (offered into a player trade). */
	void queueTradeSuspend(int itemId, int qty)
	{
		pendingTradeSuspend.merge(itemId, qty, Integer::sum);
	}

	/** Queues {@code qty} units of {@code itemId} for trade un-suspension (withdrawn from a trade). */
	void queueTradeUnsuspend(int itemId, int qty)
	{
		pendingTradeUnsuspend.merge(itemId, qty, Integer::sum);
	}

	/** Queues {@code totalCost} transferred basis for a processing output {@code itemId} (#69). */
	void queueProcessingOutput(int itemId, long totalCost)
	{
		pendingProcessingOutput.put(itemId, totalCost);
	}

	/** @return whether a processing output is already queued for {@code itemId}. */
	boolean hasProcessingOutput(int itemId)
	{
		return pendingProcessingOutput.containsKey(itemId);
	}

	/** Queues {@code totalCost} transferred basis for a decant output {@code itemId} (#220). */
	void queueDecantOutput(int itemId, long totalCost)
	{
		pendingDecantOutput.put(itemId, totalCost);
	}

	/** Queues {@code totalCost} transferred basis for a drunk-dose output {@code itemId} (#218). */
	void queueConsumedOutput(int itemId, long totalCost)
	{
		pendingConsumedOutput.put(itemId, totalCost);
	}

	/** @return whether a decant or drunk-dose output is already queued for {@code itemId}. */
	boolean hasDecantOrConsumedOutput(int itemId)
	{
		return pendingDecantOutput.containsKey(itemId) || pendingConsumedOutput.containsKey(itemId);
	}

	/** Marks that a fur/meat pouch was "Fill"ed this tick (#214). */
	void signalPouchFill()
	{
		pouchFillTick = host.currentTick();
	}

	/** Marks that a fur/meat pouch was emptied to the bank this tick (#214). */
	void signalPouchDeposit()
	{
		pouchDepositTick = host.currentTick();
	}

	/** Marks that a potion was "Empty"-clicked (discarded) this tick (#232). */
	void signalPotionDiscard()
	{
		potionDiscardTick = host.currentTick();
	}

	/** Records {@code count} empty vessels freed by finishing a potion/drink this tick (#218). */
	void addPotionEmptied(int count)
	{
		potionEmptiedTick = host.currentTick();
		potionEmptiedCount += count;
	}

	/** Resets the running count of empty vessels freed this tick (a fresh detection pass). */
	void resetPotionEmptied()
	{
		potionEmptiedCount = 0;
	}

	/** @return the tick empty vessels were last freed on, or -1 when none — for the combine detector's guard. */
	int potionEmptiedTick()
	{
		return potionEmptiedTick;
	}

	/** Drops the queued processing outputs before a detection pass recomputes them (#69). */
	void clearProcessingOutput()
	{
		pendingProcessingOutput.clear();
	}

	/** Drops the queued decant and drunk-dose outputs before a detection pass recomputes them (#218, #220). */
	void clearDecantAndConsumedOutput()
	{
		pendingDecantOutput.clear();
		pendingConsumedOutput.clear();
	}

	/** Marks the local player's death, opening the death-loss suspension window (#70). */
	void signalDeath()
	{
		deathTick = host.currentTick();
		deathLossTick = -1;
		graveGoneTick = -1;
	}

	/** Clears the transient session ledger state carried at login (matches the pre-#255 login reset). */
	void resetForLogin()
	{
		geOfferTracker.clear();
		pendingSellSuspend.clear();
		pendingSellUnsuspend.clear();
		pendingRealize.clear();
		pendingGroundSuspend.clear();
		pendingGroundUnsuspend.clear();
		pendingTradeSuspend.clear();
		pendingTradeUnsuspend.clear();
		pendingProcessingOutput.clear();
		pendingDecantOutput.clear();
		pendingConsumedOutput.clear();
		pouchFillTick = -1;
		pouchDepositTick = -1;
		potionDiscardTick = -1;
		potionEmptiedTick = -1;
		potionEmptiedCount = 0;
		deathTick = -1;
	}

	/** Clears the transient session ledger state at shutdown (matches the pre-#255 shutdown reset). */
	void resetForShutdown()
	{
		sourceAttribution.clear();
		geOfferTracker.clear();
		pendingSellSuspend.clear();
		pendingSellUnsuspend.clear();
		pendingRealize.clear();
		pendingTradeSuspend.clear();
		pendingTradeUnsuspend.clear();
	}
}
