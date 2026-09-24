/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for {@link AcquisitionsTableModel}'s client-thread edit seam (#315, #374): edits find their lot
 * by identity, and the table reads a snapshot rather than the live list.
 */
public class AcquisitionsTableModelTest
{
	/** Holds the model's last edit so a test can run it later, after simulating engine activity. */
	private Consumer<List<AcquisitionRecord>> pending;

	private final AcquisitionsTableModel model = new AcquisitionsTableModel(new StockpileConfig()
	{
	}, (itemId, mutation, onApplied) -> pending = mutation, () -> 560, false);

	private TrackedItem item(AcquisitionRecord... lots)
	{
		TrackedItem item = new TrackedItem(560, "Test item");
		for (AcquisitionRecord lot : lots)
			item.getAcquisitions().add(lot);

		item.refreshCosts();
		model.setItem(item);
		return item;
	}

	@Test
	public void anEditReachesItsLotEvenAfterAnEarlierLotIsRemoved()
	{
		AcquisitionRecord a = new AcquisitionRecord(1, 10, null);
		AcquisitionRecord b = new AcquisitionRecord(2, 20, null);
		AcquisitionRecord c = new AcquisitionRecord(3, 30, null);
		TrackedItem item = item(a, b, c);

		model.setValueAt("99", 1, 0);
		item.getAcquisitions().remove(a);
		pending.accept(item.getAcquisitions());

		assertEquals("the edited lot changed (#374)", 99, b.getQuantity());
		assertEquals("the lot that slid into row 1 did not", 3, c.getQuantity());
	}

	@Test
	public void anEditToALotTheEngineRemovedIsDropped()
	{
		AcquisitionRecord a = new AcquisitionRecord(1, 10, null);
		AcquisitionRecord b = new AcquisitionRecord(2, 20, null);
		TrackedItem item = item(a, b);

		model.setValueAt("99", 0, 0);
		item.getAcquisitions().remove(a);
		pending.accept(item.getAcquisitions());

		assertEquals(2, b.getQuantity());
	}

	@Test
	public void theTableReadsTheSnapshotNotTheLiveList()
	{
		TrackedItem item = item(new AcquisitionRecord(1, 10, null));

		item.getAcquisitions().add(new AcquisitionRecord(2, 20, null));

		assertEquals("rows change only on the next client-thread refresh", 1, model.getRowCount());
		assertNull(model.recordAt(1));
		item.refreshCosts();
		assertEquals(2, model.getRowCount());
	}

	@Test
	public void rowOfComparesByIdentity()
	{
		AcquisitionRecord a = new AcquisitionRecord(1, 10, null);
		AcquisitionRecord twin = new AcquisitionRecord(1, 10, null);
		item(a, twin);

		assertEquals("equal-valued lots are told apart", 1, model.rowOf(twin));
	}
}
