/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Small helpers for comparing two item-count snapshots keyed by item id (#179). The
 * "walk every id in either map and act on the signed change" idiom was hand-rolled at
 * four call sites (container sync, trade-offer suspension, shop claims, session stats);
 * this collapses it to one place so a fix or an off-by-one can't drift between them.
 */
final class ItemDeltas
{
	private ItemDeltas()
	{
	}

	/** Receives one item id and its signed {@code after − before} change. */
	@FunctionalInterface
	interface DeltaAction
	{
		void accept(int itemId, int delta);
	}

	/**
	 * Invokes {@code action} for every id present in either {@code before} or {@code after} with the
	 * signed {@code after − before} count change, skipping ids whose count is unchanged.
	 */
	static void forEachDelta(Map<Integer, Integer> before, Map<Integer, Integer> after, DeltaAction action)
	{
		for (int id : keyUnion(before, after))
		{
			int delta = after.getOrDefault(id, 0) - before.getOrDefault(id, 0);
			if (delta != 0)
				action.accept(id, delta);
		}
	}

	/** @return the union of both maps' keys — every id present in {@code before} or {@code after}. */
	static Set<Integer> keyUnion(Map<Integer, ?> before, Map<Integer, ?> after)
	{
		Set<Integer> ids = new HashSet<>(before.keySet());
		ids.addAll(after.keySet());
		return ids;
	}
}
