/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.runelite.api.gameval.ItemID;

/**
 * The per-tick source detectors that read a tick's net item deltas and attribute them (#334).
 *
 * <p>Extracted from {@code StockpilePlugin} in the shape {@link CostBasisLedger} and
 * {@link SourceAttributionCore} already use: everything the client provides comes through
 * {@link DetectorHost}, so the logic itself is pure and testable. It was previously the subtlest
 * code in the project with no direct coverage at all, since reaching it needed a live client.
 *
 * <p>Order matters and is the caller's responsibility: processing, then the dose swap, then
 * combine, then reward, then gather and thieving. Each later pass skips ids an earlier one has
 * already queued or claimed, which is what gives the documented precedence
 * (Processing &gt; Reward &gt; Gather/Thieving &gt; Unknown).
 */
class DeltaDetectors
{
	private final DetectorHost host;

	private final CostBasisLedger ledger;

	/** Ids claimed by this tick's dose-swap pass, which the combine detector must not re-claim. */
	private final Set<Integer> doseSwapClaimedIds = new HashSet<>();

	DeltaDetectors(DetectorHost host, CostBasisLedger ledger)
	{
		this.host = host;
		this.ledger = ledger;
	}

	/**
	 * Pairs this tick's consumed inputs with the produced output when a processing-skill
	 * XP gain identifies a recipe action (#69), transferring the summed input cost: tracked
	 * inputs contribute (and close at) their FIFO open-lot cost, untracked inputs their
	 * fallback market value, and the total is carried onto the output's new lot(s) by
	 * {@link CostBasisLedger} so their basis sums exactly to it. Multi-output ticks
	 * are unattributable and left to the fallback; tracked inputs with no tracked output
	 * close at 0. A worthless, non-tradeable output is a destroyed product and is handled
	 * without an XP signal — a burn or crush gives none — closing each tracked input as a
	 * realized loss at 0 (#144): a crushed gem tags the input {@link AcquisitionSource#CRUSHED},
	 * any other destroyed product {@link AcquisitionSource#BURNED}. When the player tracks the
	 * destroyed byproduct itself, its gain is booked at 0-cost under that same source (#172).
	 * Coins never participate.
	 *
	 * <p>Runes removed on a Magic XP tick are the cast's fuel rather than one of its ingredients,
	 * so they are claimed as {@link AcquisitionSource#CAST} at 0 and never enter the input set (#235):
	 * a superheated bar carries the ore's basis alone, and a combat spell leaves no inputs to pair.
	 * A cast that yields no item at all is not a recipe either, so its remaining inputs are left
	 * unclaimed rather than booked as processing — an alched item is sold for coins, not processed,
	 * and belongs to the {@link AcquisitionSource#ALCHEMY} claim or the fallback path.
	 */
	void correlateProcessing(Map<Integer, Integer> deltas)
	{
		ledger.clearProcessingOutput();
		if (!host.sourcePricing() || deltas.isEmpty())
			return;

		List<int[]> inputs = new ArrayList<>();
		int outputId = 0;
		int outputQty = 0;
		int outputKinds = 0;
		for (Map.Entry<Integer, Integer> entry : deltas.entrySet())
		{
			int itemId = entry.getKey();
			int delta = entry.getValue();
			if (itemId == ItemID.COINS || delta == 0)
				continue;

			if (delta < 0)
			{
				if (host.isSpellcastRune(itemId))
				{
					if (host.isTracked(itemId))
						ledger.claim(AcquisitionSource.CAST, itemId, -delta, 0, host.currentTick());

					continue;
				}

				inputs.add(new int[]{itemId, -delta});
			}
			else
			{
				outputKinds++;
				outputId = itemId;
				outputQty = delta;
			}
		}

		if (inputs.isEmpty() || outputKinds > 1)
			return;

		if (outputKinds == 0 && host.currentTick() - host.magicXpTick() <= 1)
			return;

		if (outputKinds == 1 && host.isDestroyedProduct(outputId))
		{
			AcquisitionSource loss = DestroyedOutputSources.sourceFor(outputId);
			for (int[] input : inputs)
			{
				if (host.isAmmo(input[0]))
					continue;

				if (host.isTracked(input[0]))
					ledger.claim(loss, input[0], input[1], 0, host.currentTick());
			}

			if (host.isTracked(outputId))
				ledger.claim(loss, outputId, outputQty, 0, host.currentTick());

			return;
		}

		if (host.currentTick() - host.processingXpTick() > 1)
			return;

		pairProcessingRecipe(inputs, outputId, outputQty, outputKinds == 1 && host.isTracked(outputId));
	}

	/**
	 * Closes a recipe's consumed inputs under {@link AcquisitionSource#PROCESSING} at their FIFO
	 * open-lot cost and queues the summed basis in {@code pendingProcessingOutput} so the matching
	 * gain opens the produced lot carrying it. Untracked inputs contribute their fallback value.
	 * When the output is untracked there is nothing to carry the basis onto, so the inputs close at
	 * 0 and no output is queued. Shared by the XP-gated {@link #correlateProcessing(Map)} path and the
	 * XP-less combine detector {@link #correlateCombine(Map)} (#231).
	 */
	private void pairProcessingRecipe(List<int[]> inputs, int outputId, int outputQty, boolean trackedOutput)
	{
		long totalCost = 0;
		for (int[] input : inputs)
		{
			TrackedItem tracked = host.trackedItem(input[0]);
			if (tracked == null)
			{
				totalCost += host.untrackedInputValue(input[0]) * input[1];
				continue;
			}

			long basis = ProcessingBasis.openLotCost(tracked.getAcquisitions(), input[1]);
			totalCost += basis;
			ledger.claim(AcquisitionSource.PROCESSING, input[0], input[1],
					trackedOutput ? basis / input[1] : 0, host.currentTick());
		}

		if (trackedOutput && outputQty > 0)
			ledger.queueProcessingOutput(outputId, totalCost);
	}

	/**
	 * Pairs a dose family's consumed lots with the doses it produces on a single XP-less tick, so
	 * cost basis follows the liquid across the item-id change rather than being realized as a sale.
	 * Groups the tick's non-coin deltas into dose families ({@link DoseFamily}) and hands each family
	 * to {@link #correlateDoseSwapFamily(List)}, which distinguishes two cases:
	 * <ul>
	 *   <li><b>Decant</b> (#220) — consumed doses equal produced doses: a pure swap, so the combined
	 *       input basis is distributed dose-weighted ({@link DecantBasis}) onto the produced ids under
	 *       {@link AcquisitionSource#DECANT}. Up, down, and mixed-basis inputs all merge.</li>
	 *   <li><b>Consume-down</b> (#218) — a dose is drunk (consumed doses exceed produced doses, but
	 *       some remain): the <em>full</em> input basis follows onto the lower-dose id under
	 *       {@link AcquisitionSource#CONSUMED}, since using a dose realizes no profit or loss.</li>
	 * </ul>
	 * Both queue the carried basis in {@code pendingDecantOutput} / {@code pendingConsumedOutput} so the
	 * matching gain opens the output lot carrying it; both close the consumed lots at their FIFO cost
	 * (no P/L). Untracked inputs contribute their fallback value; untracked outputs drop their share.
	 * Runs after {@link #correlateProcessing(Map)} — which a processing-XP tick handles instead — and before
	 * the source detectors, whose gains skip any id already queued in {@code pendingProcessingOutput},
	 * {@code pendingDecantOutput}, or {@code pendingConsumedOutput}. Gated by the Source-Based Pricing toggle.
	 */
	void correlateDecant(Map<Integer, Integer> deltas)
	{
		ledger.clearDecantAndConsumedOutput();
		doseSwapClaimedIds.clear();
		ledger.resetPotionEmptied();
		if (!host.sourcePricing() || deltas.isEmpty()
				|| host.currentTick() - host.processingXpTick() <= 1)
			return;

		Map<String, List<int[]>> families = new HashMap<>();
		for (Map.Entry<Integer, Integer> entry : deltas.entrySet())
		{
			int itemId = entry.getKey();
			int delta = entry.getValue();
			if (itemId == ItemID.COINS || delta == 0)
				continue;

			DoseFamily.Parsed parsed = DoseFamily.parse(host.itemName(itemId));
			if (parsed == null)
				continue;

			families.computeIfAbsent(parsed.base, k -> new ArrayList<>()).add(new int[]{itemId, delta, parsed.doses});
		}

		for (List<int[]> members : families.values())
			correlateDoseSwapFamily(members);
	}

	/**
	 * Applies the dose-family basis transfer to one family's members ({@code {id, delta, doses}}):
	 * a dose-conserving swap (consumed doses equal produced doses) is a <b>decant</b> under
	 * {@link AcquisitionSource#DECANT}; a swap that loses doses while leaving some (a dose drunk) is
	 * a <b>consume-down</b> under {@link AcquisitionSource#CONSUMED}. Anything else — no consumption,
	 * or every dose gone (a last dose drunk, left to the {@link CostBasisLedger#applyDelta} loss path) — is skipped.
	 * The consumed lots close at their FIFO cost and the summed basis is distributed onto the produced
	 * ids: dose-weighted for a decant, but in full for a consume-down since a used dose is not a loss.
	 */
	private void correlateDoseSwapFamily(List<int[]> members)
	{
		long consumedDoses = 0;
		long producedDoses = 0;
		for (int[] member : members)
			if (member[1] < 0)
				consumedDoses += (long) -member[1] * member[2];
			else
				producedDoses += (long) member[1] * member[2];

		if (consumedDoses == 0 || producedDoses == 0 || producedDoses > consumedDoses)
		{
			if (consumedDoses > 0 && producedDoses == 0)
			{
				int emptied = members.stream().filter(m -> m[1] < 0).mapToInt(m -> -m[1]).sum();
				ledger.addPotionEmptied(emptied);
			}

			return;
		}

		boolean decant = consumedDoses == producedDoses;
		AcquisitionSource source = decant ? AcquisitionSource.DECANT : AcquisitionSource.CONSUMED;
		for (int[] member : members)
			doseSwapClaimedIds.add(member[0]);

		long totalBasis = 0;
		for (int[] member : members)
		{
			if (member[1] >= 0)
				continue;

			int itemId = member[0];
			int qty = -member[1];
			TrackedItem tracked = host.trackedItem(itemId);
			if (tracked == null)
			{
				totalBasis += host.untrackedInputValue(itemId) * qty;
				continue;
			}

			long basis = ProcessingBasis.openLotCost(tracked.getAcquisitions(), qty);
			totalBasis += basis;
			ledger.claim(source, itemId, qty, basis / qty, host.currentTick());
		}

		List<int[]> outputs = new ArrayList<>();
		for (int[] member : members)
			if (member[1] > 0)
				outputs.add(new int[]{member[0], member[1] * member[2]});

		Map<Integer, Long> shares = DecantBasis.distribute(totalBasis, outputs);
		for (Map.Entry<Integer, Long> share : shares.entrySet())
		{
			if (!host.isTracked(share.getKey()))
				continue;

			if (decant)
				ledger.queueDecantOutput(share.getKey(), share.getValue());
			else
				ledger.queueConsumedOutput(share.getKey(), share.getValue());
		}
	}

	/**
	 * Pairs an XP-less combine — a tick that consumes one or more ingredients and produces a single
	 * tradeable output with no skill XP and no coin movement — as {@link AcquisitionSource#PROCESSING},
	 * so the ingredients' cost basis carries onto the product instead of both sides falling to
	 * Unknown at market value (#231). Handles the class of "mix"/combine recipes that grant no XP,
	 * such as combining a Sunlight moth with Raw pyre fox meat into a Sunlight moth mix.
	 *
	 * <p>Runs after {@link #correlateDecant(Map)} and reuses its
	 * {@link #pairProcessingRecipe(List, int, int, boolean)} basis transfer. The XP-gated {@link
	 *     #correlateProcessing(Map)} already claims recipes that emit XP, and
	 * a destroyed output ({@link DetectorHost#isDestroyedProduct}) is claimed there before the XP gate, so both
	 * are excluded here. A dose swap (decant/consume-down) is also a no-XP single-output tick, so any
	 * id claimed by the dose-swap pass ({@code doseSwapClaimedIds}) is skipped, and a finished-potion
	 * tick — where the freed vessel is the only gain — is left to the empty-container byproduct path.
	 * The output must be tracked; an untracked product gives nothing to carry basis onto and would only
	 * risk mislabelling an unrelated inventory shuffle. Gated by the Source-Based Pricing toggle.
	 */
	void correlateCombine(Map<Integer, Integer> deltas)
	{
		if (!host.sourcePricing() || deltas.isEmpty()
				|| deltas.getOrDefault(ItemID.COINS, 0) != 0)
			return;

		int tick = host.currentTick();
		if (tick - host.processingXpTick() <= 1 || tick - host.magicXpTick() <= 1 || tick - host.gatherXpTick() <= 1
				|| tick - host.thievingXpTick() <= 1 || tick - host.rewardContainerTick() <= 1
				|| tick - ledger.potionEmptiedTick() <= 1)
			return;

		List<int[]> inputs = new ArrayList<>();
		int outputId = 0;
		int outputQty = 0;
		int outputKinds = 0;
		for (Map.Entry<Integer, Integer> entry : deltas.entrySet())
		{
			int itemId = entry.getKey();
			int delta = entry.getValue();
			if (itemId == ItemID.COINS || delta == 0 || doseSwapClaimedIds.contains(itemId))
				continue;

			if (delta < 0)
			{
				inputs.add(new int[]{itemId, -delta});
			}
			else
			{
				outputKinds++;
				outputId = itemId;
				outputQty = delta;
			}
		}

		if (inputs.isEmpty() || outputKinds != 1 || host.isDestroyedProduct(outputId) || !host.isTracked(outputId))
			return;

		pairProcessingRecipe(inputs, outputId, outputQty, true);
	}

	/**
	 * Attributes this tick's unclaimed inventory gains to {@link AcquisitionSource#GATHER} at
	 * 0 when a gathering-skill XP drop (Hunter, Mining, Fishing, Woodcutting, Farming) marks
	 * them as gathered from the world at no cost (#213) — Sunfire splinters, antlers, ores,
	 * fish, logs, harvested herbs. Runs after {@link #correlateProcessing(Map)} (so a paired recipe
	 * output, already queued in {@code pendingProcessingOutput}, is skipped and keeps its
	 * transferred basis) and before the quantity sync consumes the deltas. A gain with no
	 * gathering XP this tick stays unclaimed and takes the unknown-source path. Coins never
	 * participate. Gated by the Source-Based Pricing toggle.
	 *
	 * <p>Yields to {@link #correlateReward(Map)}: when a reward-loot signal
	 * ({@link DetectorHost#rewardContainerTick()}) fired this tick, the gains are reward loot, not gathered — some
	 *     reward interactions (e.g. the
	 * Tempoross reward pool) also grant gathering XP on the same tick, which would otherwise let a
	 * GATHER claim win the FIFO over the correct REWARD one (#215).
	 */
	void correlateGathering(Map<Integer, Integer> deltas)
	{
		if (!host.sourcePricing() || host.currentTick() - host.gatherXpTick() > 1 || deltas.isEmpty())
			return;

		if (host.currentTick() - host.rewardContainerTick() <= 1)
			return;

		for (Map.Entry<Integer, Integer> entry : deltas.entrySet())
		{
			int itemId = entry.getKey();
			int delta = entry.getValue();
			if (delta <= 0 || itemId == ItemID.COINS || ledger.hasProcessingOutput(itemId)
					|| ledger.hasDecantOrConsumedOutput(itemId))
				continue;

			if (host.isTracked(itemId))
				ledger.claim(AcquisitionSource.GATHER, itemId, delta, 0, host.currentTick());
		}
	}

	/**
	 * Claims this tick's tracked inventory gains as a free {@link AcquisitionSource#REWARD} at 0
	 * when a reward-loot signal fired on the same tick ({@link DetectorHost#rewardContainerTick()}) — a reward/loot
	 * container change ({@code REWARD_CONTAINERS}), a Huntsman's loot-sack open, or a "you found some
	 * loot" chat line — i.e. loot taken from a raids chest, clue casket, reward pool or similar (#215).
	 * Takes precedence over {@link #correlateGathering(Map)}, which yields when this signal is present so a
	 * coincident gathering-XP tick can't mislabel the loot. Runs before the quantity sync consumes the
	 * deltas; a paired recipe output already queued in {@code pendingProcessingOutput} is skipped and
	 * keeps its transferred basis. A gain with no reward signal this tick stays unclaimed and takes the
	 * unknown-source path. Coins never participate. Gated by the Source-Based Pricing toggle.
	 */
	void correlateReward(Map<Integer, Integer> deltas)
	{
		if (!host.sourcePricing() || host.currentTick() - host.rewardContainerTick() > 1 || deltas.isEmpty())
			return;

		for (Map.Entry<Integer, Integer> entry : deltas.entrySet())
		{
			int itemId = entry.getKey();
			int delta = entry.getValue();
			if (delta <= 0 || itemId == ItemID.COINS || ledger.hasProcessingOutput(itemId)
					|| ledger.hasDecantOrConsumedOutput(itemId))
				continue;

			if (host.isTracked(itemId))
				ledger.claim(AcquisitionSource.REWARD, itemId, delta, 0, host.currentTick());
		}
	}

	/**
	 * Attributes this tick's unclaimed inventory gains to {@link AcquisitionSource#THIEVING} at
	 * 0 when a Thieving XP drop marks them as stolen at no cost (#217) — pickpocket loot, stall
	 * produce, chest hauls. An exact mirror of {@link #correlateGathering(Map)}: it runs after
	 * {@link #correlateReward(Map)} (so reward loot keeps its REWARD claim) and before the quantity sync
	 * consumes the deltas; a paired recipe output already queued in {@code pendingProcessingOutput}
	 * is skipped and keeps its transferred basis. A gain with no Thieving XP this tick stays
	 * unclaimed and takes the unknown-source path. Coins never participate. Gated by the
	 * Source-Based Pricing toggle.
	 *
	 * <p>Yields to {@link #correlateReward(Map)}: when a reward-loot signal
	 * ({@link DetectorHost#rewardContainerTick()}) fired this tick, the gains are reward loot, not stolen, so a
	 *     coincident Thieving-XP tick can't
	 * mislabel them (precedence: Processing &gt; Reward &gt; Gather/Thieving &gt; Unknown).
	 */
	void correlateThieving(Map<Integer, Integer> deltas)
	{
		if (!host.sourcePricing() || host.currentTick() - host.thievingXpTick() > 1 || deltas.isEmpty())
			return;

		if (host.currentTick() - host.rewardContainerTick() <= 1)
			return;

		for (Map.Entry<Integer, Integer> entry : deltas.entrySet())
		{
			int itemId = entry.getKey();
			int delta = entry.getValue();
			if (delta <= 0 || itemId == ItemID.COINS || ledger.hasProcessingOutput(itemId)
					|| ledger.hasDecantOrConsumedOutput(itemId))
				continue;

			if (host.isTracked(itemId))
				ledger.claim(AcquisitionSource.THIEVING, itemId, delta, 0, host.currentTick());
		}
	}
}
