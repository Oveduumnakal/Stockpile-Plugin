/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/** Cell renderer for the notifications table, applying the panel's fonts/colors and centered alignment. */
class NotifCellRenderer extends DefaultTableCellRenderer
{
	NotifCellRenderer()
	{
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setText(value == null ? "" : value.toString());
		if (!isSelected)
		{
			setBackground(table.getBackground());
			setForeground(Color.WHITE);
		}

		return this;
	}
}
