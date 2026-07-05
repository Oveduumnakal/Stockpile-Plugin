/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.function.Consumer;
import java.util.function.IntSupplier;
import javax.swing.table.AbstractTableModel;

/**
 * Swing table model backing the editable acquisitions log: one row per
 * {@link AcquisitionRecord} with quantity, buy price, sell price, and derived
 * profit columns. Edits are written straight back to the item's records.
 */
class AcquisitionsTableModel extends AbstractTableModel
{
	private static final String[] COLS_FULL = {"Qty", "Bought", "Sold", "Profit"};
	private static final String[] COLS_NO_PROFIT = {"Qty", "Bought", "Sold"};

	private final StockpileConfig config;
	private final Consumer<Integer> onAcquisitionsEdited;
	private final IntSupplier detailItemId;
	private TrackedItem item;

	AcquisitionsTableModel(StockpileConfig config, Consumer<Integer> onAcquisitionsEdited, IntSupplier detailItemId)
	{
		this.config = config;
		this.onAcquisitionsEdited = onAcquisitionsEdited;
		this.detailItemId = detailItemId;
	}

	void setItem(TrackedItem item)
	{
		this.item = item;
		fireTableStructureChanged();
	}

	/** @return the active column set, including the profit column only when configured. */
	private String[] cols()
	{
		return config.showItemProfitRow() ? COLS_FULL : COLS_NO_PROFIT;
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
		switch (c)
		{
			case 0: return rec.getQuantity();
			case 1: return rec.getBoughtAt();
			case 2: return rec.getSoldAt() == null ? "" : rec.getSoldAt();
			case 3: return rowProfit(rec);
			default: return "";
		}
	}

	@Override
	public void setValueAt(Object value, int r, int c)
	{
		if (item == null || r < 0 || r >= item.getAcquisitions().size())
			return;

		AcquisitionRecord rec = item.getAcquisitions().get(r);
		String s = value == null ? "" : value.toString().trim();
		try
		{
			switch (c)
			{
				case 0:
					rec.setQuantity(Math.max(0, Integer.parseInt(s)));
					break;
				case 1:
					rec.setBoughtAt(Math.max(0, Long.parseLong(s)));
					break;
				case 2:
					if (s.isEmpty())
						rec.setSoldAt(null);
					else
						rec.setSoldAt(Math.max(0, Long.parseLong(s)));

					break;
				default:
					return;
			}

			fireTableRowsUpdated(r, r);
			onAcquisitionsEdited.accept(detailItemId.getAsInt());
		}
		catch (NumberFormatException ex)
		{
			// Ignore unparseable input and keep the prior cell value.
		}
	}
}
