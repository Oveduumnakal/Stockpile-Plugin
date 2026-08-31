/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.Arrays;
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

	/** @return the source label for the lot in {@code row}, for the compact table's tooltip. */
	String sourceLabelAt(int row)
	{
		if (item == null || row < 0 || row >= item.getAcquisitions().size())
			return "";

		return displaySource(item.getAcquisitions().get(row)).toString();
	}

	/** @return whether the lot in {@code row} was closed at an estimated price rather than an observed sale. */
	boolean isSellEstimated(int row)
	{
		if (item == null || row < 0 || row >= item.getAcquisitions().size())
			return false;

		AcquisitionRecord rec = item.getAcquisitions().get(row);
		return rec.isSellEstimated();
	}

	@Override
	public int getRowCount()
	{
		return item == null ? 0 : item.getAcquisitions().size();
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
		AcquisitionRecord rec = item.getAcquisitions().get(r);
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
	 * there while offers fill (#315). The row index is re-checked inside the mutation, since the
	 * engine may have closed or removed a lot between the edit and its application.
	 */
	@Override
	public void setValueAt(Object value, int r, int c)
	{
		if (item == null || r < 0 || r >= item.getAcquisitions().size() || c > 2)
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
			if (r >= records.size())
				return;

			AcquisitionRecord rec = records.get(r);
			switch (c)
			{
				case 0:
					rec.setQuantity(quantity);
					rec.setSource(AcquisitionSource.MANUAL);
					break;
				case 1:
					rec.setBoughtAt(price);
					rec.setSource(AcquisitionSource.MANUAL);
					break;
				default:
					rec.setSoldAt(price);
					rec.setSellSource(price == null ? null : AcquisitionSource.MANUAL);
					break;
			}
		}, () -> fireTableRowsUpdated(r, r));
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
