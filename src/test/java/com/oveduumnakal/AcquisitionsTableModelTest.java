/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.event.TableModelEvent;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

	@Test
	public void compactColumnsEndInASymbolAndExpandedInASourceLabel()
	{
		item(new AcquisitionRecord(1, 10, null));

		assertEquals(List.of("Qty", "Bought", "Sold", "Profit", ""), columns(model));

		AcquisitionsTableModel expanded = new AcquisitionsTableModel(new StockpileConfig()
		{
			@Override
			public boolean showItemProfitRow()
			{
				return false;
			}
		}, (itemId, mutation, onApplied) -> pending = mutation, () -> 560, true);
		expanded.setItem(new TrackedItem(560, "Test item"));

		assertEquals("no profit column when the row is hidden", List.of("Qty", "Bought", "Sold", "Source"),
				columns(expanded));
		assertFalse(expanded.isSymbolColumn(3));
		assertTrue(model.isSymbolColumn(4));
	}

	@Test
	public void reSettingTheItemKeepsTheColumnLayout()
	{
		List<Integer> events = new ArrayList<>();
		model.addTableModelListener(e -> events.add(e.getFirstRow()));

		item(new AcquisitionRecord(1, 10, null));
		item(new AcquisitionRecord(2, 20, null));

		assertEquals("a structure reset first, then a plain data change",
				List.of(TableModelEvent.HEADER_ROW, 0), events);
	}

	@Test
	public void cellsShowAnOpenLotWithUnrealisedProfitAtTheLowPrice()
	{
		TrackedItem item = item(new AcquisitionRecord(4, 100, null, AcquisitionSource.GE_TRADE));
		item.setLowPrice(130);

		assertEquals(4, model.getValueAt(0, 0));
		assertEquals(100L, model.getValueAt(0, 1));
		assertEquals("", model.getValueAt(0, 2));
		assertEquals("4 x (130 - 100)", 120L, model.getValueAt(0, 3));
		assertEquals(AcquisitionSource.GE_TRADE, model.getValueAt(0, 4));
		assertEquals("", model.getValueAt(0, 9));
		assertEquals("", model.getValueAt(3, 0));
	}

	@Test
	public void anOpenLotWithNoPriceYetShowsZeroProfit()
	{
		item(new AcquisitionRecord(4, 100, null));

		assertEquals(0L, model.getValueAt(0, 3));
	}

	@Test
	public void aSoldLotShowsItsRealisedProfitAndSellSource()
	{
		AcquisitionRecord sold = new AcquisitionRecord(2, 100, 250L, AcquisitionSource.GATHER);
		sold.setSellSource(AcquisitionSource.ALCHEMY);
		item(sold, new AcquisitionRecord(1, 50, 40L));

		assertEquals(250L, model.getValueAt(0, 2));
		assertEquals(300L, model.getValueAt(0, 3));
		assertEquals("a sold lot shows how it left", AcquisitionSource.ALCHEMY, model.getValueAt(0, 4));
		assertEquals(AcquisitionSource.ALCHEMY.toString(), model.sourceLabelAt(0));
		assertFalse(model.isSellEstimated(0));
		assertTrue("a close with no known sale is an estimate", model.isSellEstimated(1));
		assertEquals(-10L, model.getValueAt(1, 3));
		assertEquals("", model.sourceLabelAt(5));
		assertFalse(model.isSellEstimated(5));
	}

	@Test
	public void onlyQuantityAndPricesAreEditable()
	{
		item(new AcquisitionRecord(1, 10, null));

		assertTrue(model.isCellEditable(0, 0));
		assertTrue(model.isCellEditable(0, 2));
		assertFalse(model.isCellEditable(0, 3));
		assertFalse(model.isCellEditable(0, 4));
	}

	@Test
	public void quantityAndBuyPriceEditsMarkTheLotManual()
	{
		AcquisitionRecord lot = new AcquisitionRecord(5, 100, null, AcquisitionSource.GE_TRADE);
		TrackedItem item = item(lot);

		model.setValueAt(" -3 ", 0, 0);
		pending.accept(item.getAcquisitions());
		assertEquals("negative quantities clamp to 0", 0, lot.getQuantity());
		assertEquals(AcquisitionSource.MANUAL, lot.sourceOrUnknown());

		lot.setSource(AcquisitionSource.GE_TRADE);
		model.setValueAt("120", 0, 1);
		pending.accept(item.getAcquisitions());
		assertEquals(120, lot.getBoughtAt());
		assertEquals(AcquisitionSource.MANUAL, lot.sourceOrUnknown());
	}

	@Test
	public void aSoldPriceEditClosesOrReopensTheLot()
	{
		AcquisitionRecord lot = new AcquisitionRecord(5, 100, null, AcquisitionSource.GATHER);
		TrackedItem item = item(lot);

		model.setValueAt("150", 0, 2);
		pending.accept(item.getAcquisitions());
		assertEquals(Long.valueOf(150), lot.getSoldAt());
		assertEquals(AcquisitionSource.MANUAL, lot.sellSourceOrUnknown());

		model.setValueAt("  ", 0, 2);
		pending.accept(item.getAcquisitions());
		assertNull("a blank sold price reopens the lot", lot.getSoldAt());
		assertEquals(AcquisitionSource.UNKNOWN, lot.sellSourceOrUnknown());
		assertEquals("the buy side is untouched", AcquisitionSource.GATHER, lot.sourceOrUnknown());
	}

	@Test
	public void invalidEditsNeverReachTheEditor()
	{
		item(new AcquisitionRecord(5, 100, null));

		model.setValueAt("lots", 0, 0);
		model.setValueAt(null, 0, 1);
		model.setValueAt("12", 0, 3);
		model.setValueAt("12", 4, 0);

		assertNull(pending);
	}

	@Test
	public void anAppliedEditRepaintsItsLotsCurrentRow()
	{
		AcquisitionRecord a = new AcquisitionRecord(1, 10, null);
		AcquisitionRecord b = new AcquisitionRecord(2, 20, null);
		List<Runnable> applied = new ArrayList<>();
		List<Integer> editedItems = new ArrayList<>();
		AcquisitionsTableModel tracking = new AcquisitionsTableModel(new StockpileConfig()
		{
		}, (itemId, mutation, onApplied) ->
		{
			editedItems.add(itemId);
			pending = mutation;
			applied.add(onApplied);
		}, () -> 4151, false);
		TrackedItem item = new TrackedItem(4151, "Whip");
		item.getAcquisitions().add(a);
		item.getAcquisitions().add(b);
		item.refreshCosts();
		tracking.setItem(item);
		List<Integer> repainted = new ArrayList<>();
		tracking.addTableModelListener(e -> repainted.add(e.getFirstRow()));

		tracking.setValueAt("7", 1, 0);
		item.getAcquisitions().remove(a);
		item.refreshCosts();
		pending.accept(item.getAcquisitions());
		applied.get(0).run();

		assertEquals("the edit is sent for the detail view's item", List.of(4151), editedItems);
		assertEquals("row 0 now holds the edited lot", List.of(0), repainted);
	}

	private static List<String> columns(AcquisitionsTableModel m)
	{
		List<String> names = new ArrayList<>();
		for (int c = 0; c < m.getColumnCount(); c++)
			names.add(m.getColumnName(c));

		return names;
	}
}
