/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import javax.swing.table.AbstractTableModel;

/**
 * Swing table model backing the editable acquisitions log: one row per
 * {@link AcquisitionRecord} with quantity, buy price, sell price, and derived
 * profit columns. Edits are parsed here and committed through
 * {@link AcquisitionEditor} on the client thread, which owns the list (#315).
 */
class AcquisitionsTableModel extends AbstractTableModel
{
	private static final String[] COLS_FULL = {"Qty", "Bought", "Sold", "Profit"};
	private static final String[] COLS_NO_PROFIT = {"Qty", "Bought", "Sold"};

	/** Read-only text column showing each lot's acquisition source; only in the expanded view. */
	static final String SOURCE_COL = "Source";

	/** Read-only symbol column showing each lot's acquisition source; the compact view's trailing column. */
	static final String SYMBOL_COL = "";

	private final StockpileConfig config;
	private final AcquisitionEditor editAcquisitions;
	private final IntSupplier detailItemId;
	private final boolean expanded;
	private TrackedItem item;

	/** The column set last announced via {@code fireTableStructureChanged}, to skip redundant resets. */
	private String[] lastFiredCols;

	AcquisitionsTableModel(StockpileConfig config, AcquisitionEditor editAcquisitions,
			IntSupplier detailItemId, boolean expanded)
	{
		this.config = config;
		this.editAcquisitions = editAcquisitions;
		this.detailItemId = detailItemId;
		this.expanded = expanded;
	}

	/**
	 * Swaps the backing item, announcing a full structure reset only when the column
	 * set actually changed — a plain data change keeps the table's layout, column
	 * widths, and renderers, so refresh-in-place doesn't collapse the detail card.
	 */
	void setItem(TrackedItem item)
	{
		this.item = item;

		String[] cols = cols();
		if (Arrays.equals(cols, lastFiredCols))
		{
			fireTableDataChanged();
			return;
		}

		lastFiredCols = cols;
		fireTableStructureChanged();
	}

	/**
	 * @return the active column set: the profit column only when configured, plus a trailing source
	 *         column — a text label in the expanded view, a symbol in the compact view.
	 */
	private String[] cols()
	{
		String[] base = config.showItemProfitRow() ? COLS_FULL : COLS_NO_PROFIT;
		String[] withSource = new String[base.length + 1];
		System.arraycopy(base, 0, withSource, 0, base.length);
		withSource[base.length] = expanded ? SOURCE_COL : SYMBOL_COL;
		return withSource;
	}

	/** @return whether column {@code c} is the expanded view's read-only text source column. */
	private boolean isSourceColumn(int c)
	{
		return expanded && c == getColumnCount() - 1;
	}

	/** @return whether column {@code c} is the compact view's read-only source-symbol column. */
	boolean isSymbolColumn(int c)
	{
		return !expanded && c == getColumnCount() - 1;
	}

	/**
	 * @return the source shown for a lot: for a sold lot how it left the collection
	 *         (the sell source — GE, Alchemy, Shop, …), otherwise how it entered
	 */
	private AcquisitionSource displaySource(AcquisitionRecord rec)
	{
		return rec.getSoldAt() != null ? rec.sellSourceOrUnknown() : rec.sourceOrUnknown();
	}

	/**
	 * @return the rows the table shows: the item's lots as of the last client-thread refresh. The live
	 *         list belongs to the client thread, where the FIFO engine adds, closes and merges lots as
	 *         offers fill, so the table reads a snapshot instead of racing it (#374).
	 */
	private List<AcquisitionRecord> rows()
	{
		return item == null ? Collections.emptyList() : item.getLotsSnapshot();
	}

	/** @return the lot shown in {@code row}, or {@code null} when there is no such row. */
	AcquisitionRecord recordAt(int row)
	{
		List<AcquisitionRecord> rows = rows();
		return row >= 0 && row < rows.size() ? rows.get(row) : null;
	}

	/**
	 * @return the row showing exactly {@code rec}, or -1. By identity: {@link AcquisitionRecord} compares
	 *         by value, so two lots with the same quantity and prices would otherwise be confused.
	 */
	int rowOf(AcquisitionRecord rec)
	{
		List<AcquisitionRecord> rows = rows();
		for (int i = 0; i < rows.size(); i++)
		{
			if (rows.get(i) == rec)
				return i;
		}

		return -1;
	}

	/** @return the source label for the lot in {@code row}, for the compact table's tooltip. */
	String sourceLabelAt(int row)
	{
		AcquisitionRecord rec = recordAt(row);
		return rec == null ? "" : displaySource(rec).toString();
	}

	/** @return whether the lot in {@code row} was closed at an estimated price rather than an observed sale. */
	boolean isSellEstimated(int row)
	{
		AcquisitionRecord rec = recordAt(row);
		return rec != null && rec.isSellEstimated();
	}

	@Override
	public int getRowCount()
	{
		return rows().size();
	}

	@Override
	public int getColumnCount()
	{
		return cols().length;
	}

	@Override
	public String getColumnName(int c)
	{
		return cols()[c];
	}

	@Override
	public boolean isCellEditable(int r, int c)
	{
		return c < 3;
	}

	/** @return a lot's realised profit, or its unrealised profit at the current low price while unsold. */
	long rowProfit(AcquisitionRecord rec)
	{
		if (rec.getSoldAt() != null)
			return (long) rec.getQuantity() * (rec.getSoldAt() - rec.getBoughtAt());

		if (item != null && item.getLowPrice() > 0)
			return (long) rec.getQuantity() * (item.getLowPrice() - rec.getBoughtAt());

		return 0;
	}

	@Override
	public Object getValueAt(int r, int c)
	{
		AcquisitionRecord rec = recordAt(r);
		if (rec == null)
			return "";

		if (isSymbolColumn(c))
			return displaySource(rec);

		if (isSourceColumn(c))
			return displaySource(rec).toString();

		switch (c)
		{
			case 0: return rec.getQuantity();
			case 1: return rec.getBoughtAt();
			case 2: return rec.getSoldAt() == null ? "" : rec.getSoldAt();
			case 3: return rowProfit(rec);
			default: return "";
		}
	}

	/**
	 * Commits a cell edit.
	 *
	 * <p>The parse and validation happen here on the EDT, but the record is only written on the
	 * client thread, which owns the list - the FIFO engine adds, removes and re-prices lots from
	 * there while offers fill (#315).
	 *
	 * <p>The lot is captured by identity, not row index. The index was re-checked only against the
	 * list's size, so if the engine removed or merged an earlier lot between the edit and its
	 * application, row {@code r} pointed at a different lot and the edit landed there (#374). Now the
	 * edit reaches the lot the user edited, or nothing if that lot is gone.
	 */
	@Override
	public void setValueAt(Object value, int r, int c)
	{
		AcquisitionRecord target = recordAt(r);
		if (target == null || c > 2)
			return;

		String s = value == null ? "" : value.toString().trim();
		final Integer quantity;
		final Long price;
		try
		{
			quantity = c == 0 ? Math.max(0, Integer.parseInt(s)) : null;
			price = c == 1 || (c == 2 && !s.isEmpty()) ? Math.max(0L, Long.parseLong(s)) : null;
		}
		catch (NumberFormatException ex)
		{
			return;
		}

		editAcquisitions.edit(detailItemId.getAsInt(), records ->
		{
			if (records.stream().noneMatch(rec -> rec == target))
				return;

			switch (c)
			{
				case 0:
					target.setQuantity(quantity);
					target.setSource(AcquisitionSource.MANUAL);
					break;
				case 1:
					target.setBoughtAt(price);
					target.setSource(AcquisitionSource.MANUAL);
					break;
				default:
					target.setSoldAt(price);
					target.setSellSource(price == null ? null : AcquisitionSource.MANUAL);
					break;
			}
		}, () ->
		{
			int row = rowOf(target);
			if (row >= 0)
				fireTableRowsUpdated(row, row);
		});
	}

	/** The client-thread edit seam the model commits through; see {@link DetailViewHost#editAcquisitions}. */
	interface AcquisitionEditor
	{
		/**
		 * Applies {@code mutation} to the item's acquisition list on the client thread.
		 *
		 * @param itemId the item whose log to edit
		 * @param mutation applied to the live list on the client thread
		 * @param onApplied run on the EDT once the mutation has been applied
		 */
		void edit(int itemId, Consumer<List<AcquisitionRecord>> mutation, Runnable onApplied);
	}
}
