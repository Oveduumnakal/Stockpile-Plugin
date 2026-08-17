/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.runelite.api.gameval.ItemID;

/**
 * The destroyed-output-id &rarr; {@link AcquisitionSource} mapping as data rather than control flow
 * (#182). When a processing recipe emits a single destroyed product (see
 * {@code isDestroyedProduct}), the flavour of loss its inputs are tagged with depends on which
 * product came out: a crushed gemstone is a {@link AcquisitionSource#CRUSHED}, anything else is a
 * generic {@link AcquisitionSource#BURNED}. Adding a future distinct-loss flavour (a new failed-craft
 * dust, an exploded vial, a broken tool) is a new table entry here, not another inline conditional in
 * {@code correlateProcessing}, and a second crushed-style output id can no longer silently mis-tag.
 */
final class DestroyedOutputSources
{
	private DestroyedOutputSources()
	{
	}

	/** Output ids with a distinct loss flavour; anything absent falls back to {@link AcquisitionSource#BURNED}. */
	private static final Map<Integer, AcquisitionSource> BY_OUTPUT_ID = ImmutableMap.of(
			ItemID.CRUSHED_GEMSTONE, AcquisitionSource.CRUSHED
	);

	/**
	 * @param outputId the item id of the single destroyed product a recipe emitted
	 * @return the loss source that product's inputs should be tagged with, defaulting to
	 *         {@link AcquisitionSource#BURNED} for any output not in the table
	 */
	static AcquisitionSource sourceFor(int outputId)
	{
		return BY_OUTPUT_ID.getOrDefault(outputId, AcquisitionSource.BURNED);
	}
}
