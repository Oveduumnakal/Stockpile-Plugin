/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * The full state of one item being tracked: its identity and quantity, the
 * latest wiki prices (high/low/average) and their deltas, per-window summary
 * stats and price history, GE metadata (buy limit, alch values), and the
 * acquisition lots that back its cost-basis profit calculations.
 *
 * <p>Price-history {@code series*} lists are {@code transient}: they are fetched
 * at runtime and not persisted with the rest of the item. The value/profit
 * accessors derive figures from {@code quantity}, the current prices, and the
 * {@link AcquisitionRecord} lots.
 */
@Data
public class TrackedItem
{
	private final int itemId;
	private final String name;
	private int quantity;

	private boolean tradeable = true;
	private boolean stackable;
	private boolean priceLoadFailed;

	private boolean favorite;
	private String category;
	private boolean onOverlay;

	private long highPrice;
	private long lowPrice;
	private long avgPrice;
	private transient boolean priceCacheHydrated;

	private long latestHighTime;
	private long latestLowTime;

	private int highDelta;
	private int lowDelta;
	private int avgDelta;
	private long prevHighPrice;
	private long prevLowPrice;
	private long prevAvgPrice;
	private boolean hasDeltas;

	private boolean costBasisInitialized;
	private List<AcquisitionRecord> acquisitions = new ArrayList<>();
	private List<NotificationRule> notifications = new ArrayList<>();

	/**
	 * Per-source suspension state (#179): for each {@link SuspensionSource}, this item's units
	 * currently held in that suspension and — for sources that expire — when the newest was taken.
	 * A unit here has left the held containers but is still owned, its lot kept open at basis until
	 * it resolves. All transient: sell/trade/ground suspensions are session-only, while death and
	 * pouch are re-seeded on login from {@code PersistedItem} (see {@link SuspensionSource#persisted()}),
	 * so Gson never touches this map. Legacy records default to empty — the safe additive default.
	 */
	private transient Map<SuspensionSource, SuspensionState> suspensions;

	/** Mutable per-source suspension counter and its (optional) recovery-expiry timestamp. */
	private static final class SuspensionState
	{
		private int quantity;
		private Instant at;
	}

	/**
	 * @return the suspension map, lazily created. Lazy because Gson deserializes a legacy record
	 *         through {@code Unsafe} without running field initializers, leaving the field null.
	 */
	private Map<SuspensionSource, SuspensionState> suspensions()
	{
		if (suspensions == null)
			suspensions = new EnumMap<>(SuspensionSource.class);

		return suspensions;
	}

	/** @return this item's units currently suspended by {@code source}. */
	public int getSuspended(SuspensionSource source)
	{
		SuspensionState state = suspensions == null ? null : suspensions.get(source);
		return state == null ? 0 : state.quantity;
	}

	/** @return when {@code source}'s newest suspension was taken, or {@code null} when none is held. */
	public Instant getSuspendedAt(SuspensionSource source)
	{
		SuspensionState state = suspensions == null ? null : suspensions.get(source);
		return state == null ? null : state.at;
	}

	/**
	 * Suspends {@code qty} more units under {@code source}, updating the timestamp per the source's
	 * {@link SuspensionSource#stampMode()} so death can't reset its recovery clock while ground can.
	 */
	public void addSuspended(SuspensionSource source, int qty)
	{
		if (qty <= 0)
			return;

		SuspensionState state = suspensions().computeIfAbsent(source, k -> new SuspensionState());
		state.quantity += qty;
		if (source.stampMode() == SuspensionSource.StampMode.REFRESH
				|| (source.stampMode() == SuspensionSource.StampMode.STAMP_IF_EMPTY && state.at == null))
			state.at = Instant.now();
	}

	/**
	 * Restores up to {@code qty} units from {@code source}'s suspension, clearing the entry (and its
	 * timestamp) once it empties. Returns the number actually restored.
	 */
	public int reduceSuspended(SuspensionSource source, int qty)
	{
		SuspensionState state = suspensions == null ? null : suspensions.get(source);
		if (state == null || qty <= 0)
			return 0;

		int restored = Math.min(qty, state.quantity);
		state.quantity -= restored;
		if (state.quantity == 0)
			suspensions.remove(source);

		return restored;
	}

	/** Sets {@code source}'s suspended count outright, stamping per policy; drops the entry when 0. */
	public void setSuspended(SuspensionSource source, int qty)
	{
		clearSuspended(source);
		addSuspended(source, qty);
	}

	/** Drops {@code source}'s entire suspension — count and timestamp. */
	public void clearSuspended(SuspensionSource source)
	{
		if (suspensions != null)
			suspensions.remove(source);
	}

	/**
	 * Seeds {@code source}'s suspension to {@code qty} at timestamp {@code at} when restoring persisted
	 * (death/pouch) state on login, so the recovery-expiry clock resumes from where it was saved rather
	 * than restarting now. A non-positive {@code qty} clears the entry.
	 */
	public void restoreSuspended(SuspensionSource source, int qty, Instant at)
	{
		clearSuspended(source);
		if (qty <= 0)
			return;

		SuspensionState state = new SuspensionState();
		state.quantity = qty;
		state.at = at;
		suspensions().put(source, state);
	}

	/** Units bought toward the GE buy limit in the current 4-hour window (transient; set from the plugin). */
	private transient int limitBought;

	/** Epoch-second when the current GE buy-limit window resets, or 0 when none (transient). */
	private transient long limitResetEpoch;

	private boolean notificationsInitialized;

	private TrackItemMode mode = TrackItemMode.TRACK;
	private Map<TimeWindow, PriceStats> windowStats = new EnumMap<>(TimeWindow.class);

	private transient List<WikiRealtimePriceClient.PricePoint> series5m = new ArrayList<>();
	private transient List<WikiRealtimePriceClient.PricePoint> series1h = new ArrayList<>();
	private transient List<WikiRealtimePriceClient.PricePoint> series6h = new ArrayList<>();
	private transient List<WikiRealtimePriceClient.PricePoint> series24h = new ArrayList<>();

	private int buyLimit;
	private long geValue;
	private long highAlch;
	private long lowAlch;
	private boolean metadataLoaded;

	/**
	 * Selects the price-history series whose sampling granularity best fits the
	 * given window: 1h points for a week, 6h for a month, 24h for quarter/half/year,
	 * and 5m points for anything shorter.
	 *
	 * @param window the time window being displayed
	 * @return the backing point list (live, not a copy)
	 */
	public List<WikiRealtimePriceClient.PricePoint> getSeriesFor(TimeWindow window)
	{
		switch (window)
		{
			case WEEK:
				return series1h;
			case MONTH:
				return series6h;
			case MONTH3:
			case MONTH6:
			case YEAR:
				return series24h;
			default:
				return series5m;
		}
	}

	/**
	 * @return the stack size to render this item's icon at: the tracked quantity for
	 *         stackable items, else 1 (plain single sprite)
	 */
	public int iconStackSize()
	{
		return stackable ? Math.max(1, quantity) : 1;
	}

	/** @return the tracked quantity valued at the high (instant-buy) price. */
	public long getHighValue()
	{
		return (long) quantity * highPrice;
	}

	/** @return the tracked quantity valued at the low (instant-sell) price. */
	public long getLowValue()
	{
		return (long) quantity * lowPrice;
	}

	/** @return the tracked quantity valued at the average price. */
	public long getAvgValue()
	{
		return (long) quantity * avgPrice;
	}

	/** @return whether any live price is known for this item. */
	public boolean hasPrices()
	{
		return highPrice > 0 || lowPrice > 0;
	}

	/** @return whether this item has prices from a live fetch rather than persisted cache hydration. */
	public boolean hasLivePrices()
	{
		return hasPrices() && !priceCacheHydrated;
	}

	/** @return total gp paid for the lots still held (unsold acquisitions). */
	public long getCostBasis()
	{
		return acquisitions.stream()
				.filter(r -> r.getSoldAt() == null)
				.mapToLong(r -> (long) r.getQuantity() * r.getBoughtAt())
				.sum();
	}

	/** @return profit already locked in from sold lots: sum of qty * (sold - bought). */
	public long getRealizedProfit()
	{
		return acquisitions.stream()
				.filter(r -> r.getSoldAt() != null)
				.mapToLong(r -> (long) r.getQuantity() * (r.getSoldAt() - r.getBoughtAt()))
				.sum();
	}

	/**
	 * @return total gp paid across every logged lot, held and sold. Unlike {@link #getCostBasis()}
	 *         (held lots only), this stays fixed as lots are sold, so it is the running invested
	 *         baseline for the portfolio value chart.
	 */
	public long getInvestedCostBasis()
	{
		return acquisitions.stream()
				.mapToLong(r -> (long) r.getQuantity() * r.getBoughtAt())
				.sum();
	}

	/** @return realized sale proceeds: sum of qty * sold price across sold lots (0 while nothing is sold). */
	public long getRealizedProceeds()
	{
		return acquisitions.stream()
				.filter(r -> r.getSoldAt() != null)
				.mapToLong(r -> (long) r.getQuantity() * r.getSoldAt())
				.sum();
	}

	/** @return total units across the lots still held (unsold acquisitions). */
	public int getRecordQuantitySum()
	{
		return acquisitions.stream()
				.filter(r -> r.getSoldAt() == null)
				.mapToInt(AcquisitionRecord::getQuantity)
				.sum();
	}

	/**
	 * @return units suspended across every source (GE sell, trade, ground, death,
	 *         hunting pouch): owned and still covered by open lots, but held outside
	 *         the containers that {@code quantity} counts
	 */
	public int getTotalSuspendedQuantity()
	{
		if (suspensions == null)
			return 0;

		int total = 0;
		for (SuspensionState state : suspensions.values())
			total += state.quantity;

		return total;
	}

	/**
	 * @return the suspended units valued at the average price. Suspended units are still
	 *         owned and their lots still open, so value/profit figures that subtract an
	 *         open-lot cost basis must add this back — otherwise every in-flight sell,
	 *         trade, drop, or death reads as a loss for its duration
	 */
	public long getSuspendedValue()
	{
		return (long) getTotalSuspendedQuantity() * avgPrice;
	}

	/**
	 * Total profit if the held lots were valued at {@code markPrice}: realized
	 * profit from sold lots plus the unrealized gain/loss on still-held lots.
	 *
	 * @param markPrice the per-unit price used to mark held lots to market
	 * @return realized plus unrealized profit in gp
	 */
	public long getProfitAt(long markPrice)
	{
		return getRealizedProfit() + acquisitions.stream()
				.filter(r -> r.getSoldAt() == null)
				.mapToLong(r -> (long) r.getQuantity() * (markPrice - r.getBoughtAt()))
				.sum();
	}
}
