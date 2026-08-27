/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.AsyncBufferedImage;

/**
 * The side-by-side compare grid (#280): one trimmed stat column per item, with a shared row-label
 * column pinned down the left so the same rows line up across every item. Each column reads its
 * figures from its {@link TrackedItem} the same way {@link DetailView} does &mdash; window stats,
 * {@link MarketClassifier} ratings, {@link MarketMath} tax/alch/change &mdash; so the numbers match
 * the detail view without duplicating any pricing logic. Which sections appear honours the same
 * section-visibility config the detail view uses.
 *
 * <p>This component is the scrollable strip of item columns; {@link #rowHeader()} returns the label
 * column, which {@link CompareWindow} pins as the scroll pane's row header so it stays put while the
 * columns scroll horizontally. All construction happens on the Swing EDT.
 */
final class CompareView extends JPanel
{
	/** Width of the pinned label column in pixels. */
	private static final int LABEL_COL_W = 84;

	/** Width of each item column in pixels. */
	private static final int ITEM_COL_W = 132;

	/** Height of a column's top header block (icon, name, remove) in pixels. */
	private static final int HEADER_H = 52;

	/** Height of a single value row in pixels. */
	private static final int ROW_H = 18;

	/** Height of a section-title row in pixels. */
	private static final int SECTION_H = 22;

	/** Height of the trend sparkline row in pixels. */
	private static final int SPARK_H = 44;

	/** Item-icon edge length in pixels. */
	private static final int ICON_SIZE = 32;

	private final CompareHost host;

	private final JPanel labelColumn = new JPanel();

	/**
	 * @param host the seam supplying config, the item manager, rune prices, and the remove/clear actions
	 */
	CompareView(CompareHost host)
	{
		this.host = host;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		labelColumn.setLayout(new BoxLayout(labelColumn, BoxLayout.Y_AXIS));
		labelColumn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		fixWidth(labelColumn, LABEL_COL_W);
	}

	/** @return the pinned label column, for use as the scroll pane's row header. */
	JComponent rowHeader()
	{
		return labelColumn;
	}

	/**
	 * Rebuilds the grid for {@code entries}, honouring the current section-visibility config. The
	 * label column and every item column are rebuilt from one shared row list so the rows stay aligned.
	 *
	 * @param entries the items to compare, in display order
	 */
	void setEntries(List<Entry> entries)
	{
		removeAll();
		labelColumn.removeAll();

		List<RowDef> rows = buildRows();
		boolean spark = host.config().showPriceGraph() != SectionSlot.NONE;

		labelColumn.add(headerSpacer(LABEL_COL_W));
		for (RowDef row : rows)
			labelColumn.add(rowLabel(row));

		if (spark)
			labelColumn.add(fixHeight(mutedLabel("Trend", SwingConstants.LEFT), LABEL_COL_W, SPARK_H));

		labelColumn.add(Box.createVerticalGlue());

		if (entries.isEmpty())
		{
			JLabel empty = mutedLabel("Add items to compare.", SwingConstants.LEFT);
			empty.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
			add(empty);
		}
		else
		{
			for (Entry entry : entries)
				add(buildColumn(entry, rows, spark));
		}

		add(Box.createHorizontalGlue());

		revalidate();
		repaint();
	}

	/** Builds one item's column: the header block, then a value cell for every shared row. */
	private JPanel buildColumn(Entry entry, List<RowDef> rows, boolean spark)
	{
		JPanel column = new JPanel();
		column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
		column.setBackground(ColorScheme.DARK_GRAY_COLOR);
		column.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, StockpileColors.DIVIDER));
		fixWidth(column, ITEM_COL_W);

		column.add(buildColumnHeader(entry));
		for (RowDef row : rows)
			column.add(buildCell(row, entry));

		if (spark)
			column.add(fixHeight(new Sparkline(entry.item.getSeriesFor(TimeWindow.H24)), ITEM_COL_W, SPARK_H));

		column.add(Box.createVerticalGlue());
		return column;
	}

	/** Builds a column's header: the item icon, its (ellipsised) name, and a remove control. */
	private JPanel buildColumnHeader(Entry entry)
	{
		JPanel header = new JPanel(new BorderLayout(2, 0));
		header.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		header.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 2));
		fixHeight(header, ITEM_COL_W, HEADER_H);

		JLabel icon = new JLabel();
		icon.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
		AsyncBufferedImage image = host.itemManager().getImage(entry.item.getItemId());
		image.addTo(icon);
		header.add(icon, BorderLayout.WEST);

		JLabel name = new JLabel(entry.item.getName());
		name.setFont(smallFont());
		name.setForeground(entry.tracked ? Color.WHITE : StockpileColors.MUTED);
		name.setToolTipText(entry.tracked ? entry.item.getName() : entry.item.getName() + " (not tracked)");
		header.add(name, BorderLayout.CENTER);

		JButton remove = new JButton("×");
		remove.setFont(FontManager.getRunescapeBoldFont());
		remove.setForeground(StockpileColors.MUTED);
		remove.setBorderPainted(false);
		remove.setContentAreaFilled(false);
		remove.setFocusPainted(false);
		remove.setMargin(new Insets(0, 0, 0, 0));
		remove.setToolTipText("Remove from compare");
		remove.addActionListener(e -> host.removeFromCompare(entry.item.getItemId()));
		header.add(remove, BorderLayout.EAST);

		return header;
	}

	/** Builds the label-column entry for one shared row: a section title or a muted row label. */
	private JComponent rowLabel(RowDef row)
	{
		if (row.isSection())
		{
			JLabel section = new JLabel(row.label, SwingConstants.LEFT);
			section.setFont(smallFont());
			section.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
			section.setBackground(ColorScheme.DARK_GRAY_COLOR);
			section.setOpaque(true);
			section.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 4));
			return fixHeight(section, LABEL_COL_W, SECTION_H);
		}

		JLabel label = mutedLabel(row.label, SwingConstants.LEFT);
		label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 4));
		return fixHeight(label, LABEL_COL_W, ROW_H);
	}

	/** Builds one item column's cell for a shared row: a section spacer or the computed value label. */
	private JComponent buildCell(RowDef row, Entry entry)
	{
		if (row.isSection())
		{
			JPanel spacer = new JPanel();
			spacer.setBackground(ColorScheme.DARK_GRAY_COLOR);
			return fixHeight(spacer, ITEM_COL_W, SECTION_H);
		}

		Cell cell = row.value.apply(entry);
		JLabel label = new JLabel(cell.text, SwingConstants.CENTER);
		label.setFont(smallFont());
		label.setForeground(cell.color);
		label.setToolTipText(cell.tooltip);
		return fixHeight(label, ITEM_COL_W, ROW_H);
	}

	/**
	 * Builds the shared row list for the current config: a section title followed by its value rows,
	 * for each section whose visibility toggle is not {@link SectionSlot#NONE}.
	 */
	private List<RowDef> buildRows()
	{
		StockpileConfig cfg = host.config();
		List<RowDef> rows = new ArrayList<>();

		if (cfg.showPriceOverview() != SectionSlot.NONE)
		{
			rows.add(RowDef.section("Price"));
			rows.add(RowDef.value("Price", e -> gpCell(e.item.getAvgPrice(), StockpileColors.AVG)));
			rows.add(RowDef.value("Buy", e -> gpCell(e.item.getHighPrice(), StockpileColors.HIGH)));
			rows.add(RowDef.value("Sell", e -> gpCell(e.item.getLowPrice(), StockpileColors.LOW)));
			rows.add(RowDef.value("24h Δ", e -> changeCell(e.item)));
		}

		if (cfg.showVolumeGraph() != SectionSlot.NONE)
			rows.add(RowDef.value("Vol 24h", e -> volumeCell(e.item)));

		if (cfg.showMarketInfo() != SectionSlot.NONE)
		{
			rows.add(RowDef.section("Market"));
			rows.add(RowDef.value("Buy limit", e -> countCell(e.item.getBuyLimit())));
			rows.add(RowDef.value("GE tax", e -> taxCell(e.item)));
			rows.add(RowDef.value("Volatility", e -> ratingCell(MarketClassifier.volatility(
					e.item.getSeriesFor(TimeWindow.WEEK)))));
			rows.add(RowDef.value("Liquidity", e -> ratingCell(MarketClassifier.liquidity(vol24(e.item)))));
			rows.add(RowDef.value("30d range", e -> rangeCell(e.item)));
		}

		if (cfg.showAlchInfo() != SectionSlot.NONE)
		{
			rows.add(RowDef.section("Alchemy"));
			rows.add(RowDef.value("High alch", e -> gpCell(e.item.getHighAlch(), Color.WHITE)));
			rows.add(RowDef.value("High profit", e -> alchProfitCell(e.item, true)));
			rows.add(RowDef.value("Low alch", e -> gpCell(e.item.getLowAlch(), Color.WHITE)));
			rows.add(RowDef.value("Low profit", e -> alchProfitCell(e.item, false)));
		}

		if (cfg.showCollectionValues() != SectionSlot.NONE)
		{
			rows.add(RowDef.section("Holdings"));
			rows.add(RowDef.value("Qty", this::qtyCell));
			rows.add(RowDef.value("Value", this::holdingValueCell));
			rows.add(RowDef.value("Profit", this::holdingProfitCell));
		}

		return rows;
	}

	/** @return a compact gp cell, or a placeholder when the value is non-positive. */
	private Cell gpCell(long value, Color color)
	{
		if (value <= 0)
			return Cell.placeholder();

		return new Cell(GpFormat.shortValue1dp(value), color, GpFormat.fullGp(value));
	}

	/** @return a compact count cell (buy limit, etc.), or a placeholder when zero. */
	private Cell countCell(long value)
	{
		if (value <= 0)
			return Cell.placeholder();

		return new Cell(GpFormat.shortValue1dp(value), Color.WHITE, GpFormat.grouped(value));
	}

	/** @return the item's GE-tax cell, or a placeholder when the item has no price. */
	private Cell taxCell(TrackedItem item)
	{
		return gpCell(MarketMath.geTax(item.getAvgPrice()), StockpileColors.MUTED);
	}

	/** @return the item's 24h volume cell, or a placeholder when unknown. */
	private Cell volumeCell(TrackedItem item)
	{
		long vol = vol24(item);
		if (vol <= 0)
			return Cell.placeholder();

		return new Cell(GpFormat.shortValue1dp(vol), StockpileColors.MUTED, GpFormat.grouped(vol) + " traded (24h)");
	}

	/** @return the signed percent change of the current price against the 24h average, coloured up/down. */
	private Cell changeCell(TrackedItem item)
	{
		PriceStats stats = item.getWindowStats().get(TimeWindow.H24);
		long baseline = stats == null ? 0 : stats.getAvg();
		double pct = MarketMath.changePct(item.getAvgPrice(), baseline);
		if (Double.isNaN(pct))
			return Cell.placeholder();

		String text = pct == 0.0 ? "0%" : String.format(Locale.US, "%+.1f%%", pct);
		Color color = pct > 0 ? StockpileColors.HIGH : (pct < 0 ? StockpileColors.LOW : StockpileColors.MUTED);
		return new Cell(text, color, text + " vs. 24 hour avg.");
	}

	/** @return a Low/Medium/High rating cell (volatility, liquidity), or a placeholder when unknown. */
	private Cell ratingCell(String rating)
	{
		if (rating == null)
			return Cell.placeholder();

		return new Cell(rating, Color.WHITE, null);
	}

	/** @return the 30-day range-position cell, or a placeholder when the range is unknown. */
	private Cell rangeCell(TrackedItem item)
	{
		long[] range = MarketClassifier.thirtyDayRange(item.getSeriesFor(TimeWindow.MONTH));
		String pos = MarketClassifier.rangePosition(range[0], range[1], item.getAvgPrice());
		if (pos == null)
			return Cell.placeholder();

		String tip = GpFormat.fullGp(range[0]) + " – " + GpFormat.fullGp(range[1]);
		return new Cell(pos, Color.WHITE, tip);
	}

	/** @return the high- or low-alch profit cell, signed and coloured, or a placeholder when unknown. */
	private Cell alchProfitCell(TrackedItem item, boolean high)
	{
		long alch = high ? item.getHighAlch() : item.getLowAlch();
		long avg = item.getAvgPrice();
		if (alch <= 0 || avg <= 0)
			return Cell.placeholder();

		long profit = high
				? MarketMath.highAlchProfit(alch, avg, host.natureRunePrice(), host.fireRunePrice())
				: MarketMath.lowAlchProfit(alch, avg, host.natureRunePrice(), host.fireRunePrice());
		Color color = profit > 0 ? StockpileColors.HIGH : (profit < 0 ? StockpileColors.LOW : Color.WHITE);
		return new Cell(GpFormat.signedShort(profit), color, GpFormat.signedGrouped(profit) + " gp per cast");
	}

	/** @return the tracked quantity cell, blank for an untracked item. */
	private Cell qtyCell(Entry entry)
	{
		if (!entry.tracked)
			return Cell.placeholder();

		int qty = entry.item.getQuantity();
		return new Cell(GpFormat.shortValue1dp(qty), Color.WHITE, GpFormat.grouped(qty) + " held");
	}

	/** @return the tracked holding-value cell (qty times price), blank for an untracked item. */
	private Cell holdingValueCell(Entry entry)
	{
		if (!entry.tracked)
			return Cell.placeholder();

		long value = (long) entry.item.getQuantity() * entry.item.getAvgPrice();
		return gpCell(value, StockpileColors.AVG);
	}

	/** @return the realized-profit cell, signed and coloured, blank for an untracked item. */
	private Cell holdingProfitCell(Entry entry)
	{
		if (!entry.tracked)
			return Cell.placeholder();

		long profit = entry.item.getRealizedProfit();
		Color color = profit > 0 ? StockpileColors.HIGH : (profit < 0 ? StockpileColors.LOW : Color.WHITE);
		return new Cell(GpFormat.signedShort(profit), color, GpFormat.signedGrouped(profit) + " gp realized");
	}

	/** @return the item's traded volume over the past 24 hours from its window stats, or 0 when unknown. */
	private static long vol24(TrackedItem item)
	{
		PriceStats stats = item.getWindowStats().get(TimeWindow.H24);
		return stats == null ? 0 : stats.getVolume();
	}

	/** @return a small muted, left-or-centre-aligned label in the panel's small font. */
	private static JLabel mutedLabel(String text, int alignment)
	{
		JLabel label = new JLabel(text, alignment);
		label.setFont(FontManager.getRunescapeSmallFont());
		label.setForeground(StockpileColors.MUTED);
		return label;
	}

	/** @return an empty, fixed-height spacer standing in for a column's header block in the label column. */
	private static JComponent headerSpacer(int width)
	{
		JPanel spacer = new JPanel();
		spacer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		return fixHeight(spacer, width, HEADER_H);
	}

	/** @return the panel's small font (the RuneScape small font). */
	private static Font smallFont()
	{
		return FontManager.getRunescapeSmallFont();
	}

	/** Pins a component to a fixed width, leaving its height free. */
	private static void fixWidth(JComponent component, int width)
	{
		component.setMinimumSize(new Dimension(width, 0));
		component.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
		component.setPreferredSize(new Dimension(width, component.getPreferredSize().height));
		component.setAlignmentY(Component.TOP_ALIGNMENT);
	}

	/** Pins a component to a fixed width and height so rows line up across every column. */
	private static JComponent fixHeight(JComponent component, int width, int height)
	{
		Dimension size = new Dimension(width, height);
		component.setMinimumSize(size);
		component.setPreferredSize(size);
		component.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
		component.setAlignmentX(Component.LEFT_ALIGNMENT);
		return component;
	}

	/** One item to compare: its backing {@link TrackedItem} and whether it is actually tracked. */
	static final class Entry
	{
		private final TrackedItem item;

		private final boolean tracked;

		/**
		 * @param item the tracked item (live) or a read-only preview instance for an untracked item
		 * @param tracked whether {@code item} is actually tracked (untracked items blank the holdings rows)
		 */
		Entry(TrackedItem item, boolean tracked)
		{
			this.item = item;
			this.tracked = tracked;
		}
	}

	/** A computed cell value: its display text, colour, and optional tooltip. */
	private static final class Cell
	{
		private final String text;

		private final Color color;

		private final String tooltip;

		/**
		 * @param text the display text
		 * @param color the text colour
		 * @param tooltip the tooltip, or {@code null} for none
		 */
		Cell(String text, Color color, String tooltip)
		{
			this.text = text;
			this.color = color;
			this.tooltip = tooltip;
		}

		/** @return the shared placeholder cell for an unknown/absent value (a muted dash). */
		static Cell placeholder()
		{
			return new Cell("—", StockpileColors.MUTED, null);
		}
	}

	/** Maps one {@link Entry} to its {@link Cell} value for a single row. */
	private interface CellFn
	{
		/**
		 * @param entry the item being rendered
		 * @return the computed cell for this row
		 */
		Cell apply(Entry entry);
	}

	/** One shared row in the grid: a section title (when {@code value} is {@code null}) or a value row. */
	private static final class RowDef
	{
		private final String label;

		private final CellFn value;

		/**
		 * @param label the row's label text
		 * @param value the value provider, or {@code null} for a section-title row
		 */
		private RowDef(String label, CellFn value)
		{
			this.label = label;
			this.value = value;
		}

		/**
		 * @param label the section title
		 * @return a section-title row
		 */
		static RowDef section(String label)
		{
			return new RowDef(label, null);
		}

		/**
		 * @param label the row label
		 * @param value the value provider
		 * @return a value row
		 */
		static RowDef value(String label, CellFn value)
		{
			return new RowDef(label, value);
		}

		/** @return whether this row is a section title rather than a value row. */
		boolean isSection()
		{
			return value == null;
		}
	}

	/**
	 * A small custom-painted price sparkline for one item: the 24h series' midpoints scaled to the
	 * row, giving an at-a-glance trend beneath each column.
	 */
	private static final class Sparkline extends JComponent
	{
		private final List<WikiRealtimePriceClient.PricePoint> series;

		/**
		 * @param series the 24h price points to plot (an empty list paints nothing)
		 */
		Sparkline(List<WikiRealtimePriceClient.PricePoint> series)
		{
			this.series = series;
			setBackground(ColorScheme.DARK_GRAY_COLOR);
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			List<Long> points = midpoints();
			if (points.size() < 2)
				return;

			long min = points.get(0);
			long max = points.get(0);
			for (long p : points)
			{
				min = Math.min(min, p);
				max = Math.max(max, p);
			}

			int w = getWidth();
			int h = getHeight();
			int padX = 6;
			int padY = 6;
			double span = Math.max(1, max - min);
			double stepX = (double) (w - 2 * padX) / (points.size() - 1);

			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(StockpileColors.AVG);
			g2.setStroke(new BasicStroke(1.2f));

			int prevX = 0;
			int prevY = 0;
			for (int i = 0; i < points.size(); i++)
			{
				int x = padX + (int) Math.round(i * stepX);
				int y = padY + (int) Math.round((1.0 - (points.get(i) - min) / span) * (h - 2 * padY));
				if (i > 0)
					g2.drawLine(prevX, prevY, x, y);

				prevX = x;
				prevY = y;
			}

			g2.dispose();
		}

		/** @return the in-order series midpoints (high/low average), skipping points with no price. */
		private List<Long> midpoints()
		{
			List<Long> out = new ArrayList<>();
			for (WikiRealtimePriceClient.PricePoint p : series)
			{
				long high = p.getAvgHighPrice();
				long low = p.getAvgLowPrice();
				if (high > 0 && low > 0)
					out.add((high + low) / 2);
				else if (high > 0)
					out.add(high);
				else if (low > 0)
					out.add(low);
			}

			return out;
		}
	}
}
