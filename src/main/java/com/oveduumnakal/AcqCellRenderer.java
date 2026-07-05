/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.awt.Color;
import java.awt.Component;
import java.util.function.IntSupplier;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/** Cell renderer for the acquisitions table, coloring the profit column and formatting gp values. */
class AcqCellRenderer extends DefaultTableCellRenderer
{
	private final boolean profit;
	private final boolean expanded;
	private final IntSupplier hoverRow;
	private final IntSupplier hoverCol;

	AcqCellRenderer(boolean profit, boolean expanded, IntSupplier hoverRow, IntSupplier hoverCol)
	{
		this.profit = profit;
		this.expanded = expanded;
		this.hoverRow = hoverRow;
		this.hoverCol = hoverCol;
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (!(value instanceof Number))
		{
			setText(value == null ? "" : value.toString());
			if (!isSelected)
			{
				setBackground(table.getBackground());
				setForeground(Color.WHITE);
			}

			return this;
		}

		long v = ((Number) value).longValue();

		boolean shortForm = !expanded && Math.abs(v) >= (profit ? 1000 : 10000);
		String text = shortForm
				? GpFormat.shortValue(v)
				: GpFormat.grouped(v);
		if (profit && v > 0)
			text = "+" + text;

		setText(text);

		Color fg = profit ? (v > 0 ? StockpileColors.HIGH : v < 0 ? StockpileColors.LOW : Color.WHITE) : Color.WHITE;
		setForeground(isSelected ? table.getSelectionForeground() : fg);

		boolean hovered = shortForm && row == hoverRow.getAsInt() && column == hoverCol.getAsInt();
		if (isSelected)
		{
			setBackground(table.getSelectionBackground());
		}
		else if (hovered)
		{
			setForeground(fg);
			setBackground(profit
					? (v >= 0 ? StockpileColors.TINT_HIGH : StockpileColors.TINT_LOW)
					: StockpileColors.TINT_VOLUME);
		}
		else
		{
			setBackground(table.getBackground());
		}

		return this;
	}
}
