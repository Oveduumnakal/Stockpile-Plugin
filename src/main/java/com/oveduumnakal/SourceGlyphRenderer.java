/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.function.IntSupplier;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renders the acquisitions table's read-only source column as a monochrome {@link SourceGlyph},
 * tinted to the theme and brightened (with a faint background tint) while the cell is hovered.
 */
class SourceGlyphRenderer extends JComponent implements TableCellRenderer
{
	/** Idle glyph tint; the hover state brightens it to white. */
	private static final Color IDLE = new Color(170, 170, 170);

	private final IntSupplier hoverRow;
	private final IntSupplier hoverCol;
	private AcquisitionSource source = AcquisitionSource.UNKNOWN;
	private boolean hovered;
	private Color background = Color.BLACK;

	SourceGlyphRenderer(IntSupplier hoverRow, IntSupplier hoverCol)
	{
		this.hoverRow = hoverRow;
		this.hoverCol = hoverCol;
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		source = value instanceof AcquisitionSource ? (AcquisitionSource) value : AcquisitionSource.UNKNOWN;
		hovered = row == hoverRow.getAsInt() && column == hoverCol.getAsInt();
		background = isSelected ? table.getSelectionBackground()
				: hovered ? StockpileColors.TINT_VOLUME : table.getBackground();
		setToolTipText(source.toString());
		return this;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setColor(background);
		g2.fillRect(0, 0, getWidth(), getHeight());
		SourceGlyph.draw(g2, source, getWidth(), getHeight(), hovered ? Color.WHITE : IDLE);
		g2.dispose();
	}
}
