/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

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
final class CompareView extends JPanel implements Scrollable
{
	/** Width of the pinned label column in pixels. */
	private static final int LABEL_COL_W = 84;

	/** Right-hand padding on the right-justified label column, in pixels. */
	private static final int LABEL_PAD_R = 8;

	/**
	 * Minimum width of each item column in pixels. Columns grow past this to share the viewport width
	 * when they all fit (so they fill the window), and hold at this width &mdash; scrolling horizontally
	 * &mdash; once their combined minimum overflows it.
	 */
	private static final int ITEM_COL_W = 132;

	/** Height of a column's top header block (icon, name, remove) in pixels. */
	private static final int HEADER_H = 52;

	/** Height of a single value row in pixels. */
	private static final int ROW_H = 18;

	/** Height of a section-title row in pixels. */
	private static final int SECTION_H = 22;

	/** Height of the trend sparkline row in pixels. */
	private static final int SPARK_H = 44;

	/** Height of the mini volume-bar row beneath the trend, in pixels. The trend flexes to fill what's left. */
	private static final int VOL_H = 60;

	/** Item-icon edge length in pixels. */
	private static final int ICON_SIZE = 32;

	/** The recent look-back the charts clip to for the {@code 5m} window, in seconds. */
	private static final long RECENT_CHART_SECONDS = 30 * 60;

	/** Minimum horizontal drag, in pixels, before a header drag is treated as a column reorder. */
	private static final int DRAG_THRESHOLD = 8;

	/** Translucent tint painted over the column being dragged, so the grabbed column stands out. */
	private static final Color DRAG_HIGHLIGHT = new Color(255, 255, 255, 45);

	/** The vertical insertion line marking where a dragged column will drop. */
	private static final Color DRAG_LINE = ColorScheme.BRAND_ORANGE;

	/** Cursor shown over a draggable column header, and while it is being dragged: the four-way move arrow. */
	private static final Cursor HOVER_CURSOR = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);

	/** Cursor shown while a column is being dragged: the four-way move arrow. */
	private static final Cursor DRAG_CURSOR = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);

	/** The horizontal section rule colour: the vertical {@link StockpileColors#DIVIDER} shifted 66% toward
	 * the column background so the row separators read fainter than the column separators. */
	private static final Color SECTION_RULE = blend(StockpileColors.DIVIDER, ColorScheme.DARK_GRAY_COLOR, 0.66f);

	/** Edge length of the header's remove control, also the balancing strut opposite it, in pixels. */
	private static final int REMOVE_W = 20;

	/** The red the remove control turns on hover, matching the tracked list's remove button. */
	private static final Color REMOVE_HOVER = new Color(200, 60, 60);

	private final CompareHost host;

	private final JPanel labelColumn = new JPanel();

	/** The time window every column currently reads its price/volume figures from. */
	private TimeWindow window = TimeWindow.LIVE;

	/** The most recent entries, retained so a window switch can re-render without the plugin re-supplying them. */
	private List<Entry> entries = Collections.emptyList();

	/** Whether a header drag-reorder is in progress (drives the drag highlight and insertion line). */
	private boolean dragging;

	/** The item id of the column being dragged, valid while {@link #dragging}. */
	private int dragItemId;

	/** The drag pointer's current x in this view's coordinates, valid while {@link #dragging}. */
	private int dragPointerX;

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
	 * Paints the columns, then — while a header drag is in progress — a translucent highlight over the
	 * grabbed column and a bright vertical line marking where it will drop.
	 *
	 * @param g the graphics context
	 */
	@Override
	protected void paintChildren(Graphics g)
	{
		super.paintChildren(g);
		if (!dragging)
			return;

		Component[] columns = getComponents();
		int source = entryIndex(dragItemId);
		if (source >= 0 && source < columns.length)
		{
			Component c = columns[source];
			g.setColor(DRAG_HIGHLIGHT);
			g.fillRect(c.getX(), 0, c.getWidth(), getHeight());
		}

		int boundary = insertionIndex(dragPointerX);
		int lineX;
		if (columns.length == 0)
			lineX = 0;
		else if (boundary >= columns.length)
			lineX = columns[columns.length - 1].getX() + columns[columns.length - 1].getWidth() - 2;
		else
			lineX = columns[boundary].getX();

		g.setColor(DRAG_LINE);
		g.fillRect(lineX, 0, 2, getHeight());
	}

	/**
	 * Retains {@code entries} and rebuilds the grid, honouring the current section-visibility config. The
	 * label column and every item column are rebuilt from one shared row list so the rows stay aligned.
	 *
	 * @param entries the items to compare, in display order
	 */
	void setEntries(List<Entry> entries)
	{
		this.entries = entries;
		render();
	}

	/** @return the time window the columns currently read their price/volume figures from. */
	TimeWindow activeWindow()
	{
		return window;
	}

	/**
	 * Switches the window the columns read from and re-renders the retained entries against it.
	 *
	 * @param window the window whose stats every column should show
	 */
	void setActiveWindow(TimeWindow window)
	{
		this.window = window;
		render();
	}

	/** Rebuilds the label column and every item column from one shared row list against the active window. */
	private void render()
	{
		removeAll();
		labelColumn.removeAll();

		List<RowDef> rows = buildRows();
		boolean spark = host.config().showPriceGraph() != SectionSlot.NONE;
		boolean volChart = host.config().showVolumeGraph() != SectionSlot.NONE;

		labelColumn.add(headerSpacer(LABEL_COL_W));
		for (RowDef row : rows)
			labelColumn.add(rowLabel(row));

		if (spark)
			labelColumn.add(growHeight(chartLabel("Trend"), LABEL_COL_W, SPARK_H, false));
		else
			labelColumn.add(Box.createVerticalGlue());

		if (volChart)
			labelColumn.add(fixHeight(chartLabel("Vol."), LABEL_COL_W, VOL_H));

		if (entries.isEmpty())
		{
			JLabel empty = mutedLabel("Add items to compare.", SwingConstants.CENTER);
			empty.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
			add(Box.createHorizontalGlue());
			add(empty);
			add(Box.createHorizontalGlue());
		}
		else
		{
			for (Entry entry : entries)
				add(buildColumn(entry, rows, spark, volChart));
		}

		revalidate();
		repaint();
	}

	/** Builds one item's column: the header block, every value cell, then the trend and volume charts. */
	private JPanel buildColumn(Entry entry, List<RowDef> rows, boolean spark, boolean volChart)
	{
		JPanel column = new JPanel();
		column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
		column.setBackground(ColorScheme.DARK_GRAY_COLOR);
		column.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, StockpileColors.DIVIDER));
		flexWidth(column, ITEM_COL_W);

		column.add(buildColumnHeader(entry));
		for (RowDef row : rows)
			column.add(buildCell(row, entry));

		List<WikiRealtimePriceClient.PricePoint> series = windowSeries(entry.item);
		if (spark)
			column.add(growHeight(new Sparkline(series), ITEM_COL_W, SPARK_H, true));
		else
			column.add(Box.createVerticalGlue());

		if (volChart)
			column.add(fixHeight(new VolumeChart(series), ITEM_COL_W, VOL_H));

		return column;
	}

	/** Builds a column's header: the item icon and name centred, with a remove control in the corner. */
	private JPanel buildColumnHeader(Entry entry)
	{
		JPanel header = new JPanel(new BorderLayout(0, 0));
		header.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		header.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
		fixHeight(header, ITEM_COL_W, HEADER_H);

		JPanel identity = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
		identity.setOpaque(false);

		JLabel icon = new JLabel();
		icon.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
		AsyncBufferedImage image = host.itemManager().getImage(entry.item.getItemId());
		image.addTo(icon);
		identity.add(icon);

		JLabel name = new JLabel(entry.item.getName());
		name.setFont(smallFont());
		name.setForeground(entry.tracked ? Color.WHITE : StockpileColors.MUTED);
		name.setToolTipText(entry.tracked ? entry.item.getName() : entry.item.getName() + " (not tracked)");
		identity.add(name);

		JPanel center = new JPanel(new GridBagLayout());
		center.setOpaque(false);
		center.add(identity);
		header.add(center, BorderLayout.CENTER);

		if (entries.size() > 1)
		{
			int itemId = entry.item.getItemId();
			installHeaderDrag(header, itemId);
			installHeaderDrag(center, itemId);
			installHeaderDrag(identity, itemId);
			installHeaderDrag(icon, itemId);
			installHeaderDrag(name, itemId);
		}

		JButton remove = new JButton("✕");
		Font removeFont = FontManager.getRunescapeSmallFont();
		remove.setFont(removeFont.deriveFont(removeFont.getSize() * 1.125f));
		remove.setForeground(StockpileColors.MUTED);
		remove.setBorderPainted(false);
		remove.setContentAreaFilled(false);
		remove.setFocusPainted(false);
		remove.setMargin(new Insets(0, 0, 0, 0));
		remove.setPreferredSize(new Dimension(REMOVE_W, REMOVE_W));
		remove.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		remove.setToolTipText("Remove from compare");
		remove.addActionListener(e -> host.removeFromCompare(entry.item.getItemId()));
		remove.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				remove.setForeground(REMOVE_HOVER);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				remove.setForeground(StockpileColors.MUTED);
			}
		});

		JPanel corner = new JPanel(new BorderLayout());
		corner.setOpaque(false);
		corner.add(remove, BorderLayout.CENTER);
		header.add(corner, BorderLayout.EAST);
		header.add(Box.createHorizontalStrut(REMOVE_W), BorderLayout.WEST);

		return header;
	}

	/**
	 * Makes a header handle drag its column to a new position: pressing shows a move cursor, and
	 * releasing far enough away drops the column over whichever column the cursor is above.
	 *
	 * @param handle the header component the drag is attached to (the identity panel, icon, or name)
	 * @param itemId the id of the column being dragged
	 */
	private void installHeaderDrag(JComponent handle, int itemId)
	{
		handle.setCursor(HOVER_CURSOR);
		MouseAdapter drag = new MouseAdapter()
		{
			private int pressX;

			@Override
			public void mousePressed(MouseEvent e)
			{
				pressX = viewX(handle, e);
			}

			@Override
			public void mouseDragged(MouseEvent e)
			{
				int x = viewX(handle, e);
				if (!dragging && Math.abs(x - pressX) < DRAG_THRESHOLD)
					return;

				dragging = true;
				dragItemId = itemId;
				dragPointerX = x;
				handle.setCursor(DRAG_CURSOR);
				repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				handle.setCursor(HOVER_CURSOR);
				if (!dragging)
					return;

				dragging = false;
				repaint();

				int boundary = insertionIndex(viewX(handle, e));
				int source = entryIndex(itemId);
				int target = boundary > source ? boundary - 1 : boundary;
				if (source >= 0 && target != source)
					host.moveCompare(itemId, target);
			}
		};
		handle.addMouseListener(drag);
		handle.addMouseMotionListener(drag);
	}

	/** @return {@code e}'s x, translated from {@code source}'s coordinates into this view's coordinates. */
	private int viewX(JComponent source, MouseEvent e)
	{
		return SwingUtilities.convertPoint(source, e.getPoint(), this).x;
	}

	/** @return the current column index of the compared item {@code itemId}, or -1 when it is not present. */
	private int entryIndex(int itemId)
	{
		for (int i = 0; i < entries.size(); i++)
		{
			if (entries.get(i).item.getItemId() == itemId)
				return i;
		}

		return -1;
	}

	/** @return the boundary (0..N) between columns nearest x, where a dropped column would be inserted. */
	private int insertionIndex(int x)
	{
		Component[] columns = getComponents();
		for (int i = 0; i < columns.length; i++)
		{
			if (x < columns[i].getX() + columns[i].getWidth() / 2)
				return i;
		}

		return columns.length;
	}

	/** Builds the label-column entry for one shared row: a section title or a muted row label. */
	private JComponent rowLabel(RowDef row)
	{
		if (row.isSection())
		{
			JLabel section = new JLabel(row.label, SwingConstants.RIGHT);
			section.setFont(smallFont());
			section.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
			section.setBackground(ColorScheme.DARK_GRAY_COLOR);
			section.setOpaque(true);
			section.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, LABEL_PAD_R));
			return fixHeight(section, LABEL_COL_W, SECTION_H);
		}

		JLabel label = mutedLabel(row.label, SwingConstants.RIGHT);
		label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, LABEL_PAD_R));
		return fixHeight(label, LABEL_COL_W, ROW_H);
	}

	/** Builds one item column's cell for a shared row: a faint section rule or the computed value label. */
	private JComponent buildCell(RowDef row, Entry entry)
	{
		if (row.isSection())
			return fixHeight(new Rule(), ITEM_COL_W, SECTION_H);

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
			rows.add(RowDef.value("Price", e -> gpCell(windowAvg(e.item), StockpileColors.AVG)));
			rows.add(RowDef.value("Buy", e -> gpCell(windowHigh(e.item), StockpileColors.HIGH)));
			rows.add(RowDef.value("Sell", e -> gpCell(windowLow(e.item), StockpileColors.LOW)));
			rows.add(RowDef.value("Change", e -> changeCell(e.item)));
		}

		if (cfg.showVolumeGraph() != SectionSlot.NONE)
			rows.add(RowDef.value("Volume", e -> volumeCell(e.item)));

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

		return new Cell(GpFormat.grouped(value), color, GpFormat.fullGp(value));
	}

	/** @return a full-number count cell (buy limit, etc.), or a placeholder when zero. */
	private Cell countCell(long value)
	{
		if (value <= 0)
			return Cell.placeholder();

		return new Cell(GpFormat.grouped(value), Color.WHITE, GpFormat.grouped(value));
	}

	/** @return the item's GE-tax cell, or a placeholder when the item has no price. */
	private Cell taxCell(TrackedItem item)
	{
		return gpCell(MarketMath.geTax(item.getAvgPrice()), StockpileColors.MUTED);
	}

	/** @return the item's traded-volume cell over the active window, or a placeholder when unknown. */
	private Cell volumeCell(TrackedItem item)
	{
		long vol = windowVol(item);
		if (vol <= 0)
			return Cell.placeholder();

		return new Cell(GpFormat.grouped(vol), StockpileColors.MUTED,
				GpFormat.grouped(vol) + " traded (" + window.getLongLabel() + ")");
	}

	/** @return the signed percent change of the current price against the active window's average. */
	private Cell changeCell(TrackedItem item)
	{
		double pct = MarketMath.changePct(item.getAvgPrice(), windowAvg(item));
		if (Double.isNaN(pct))
			return Cell.placeholder();

		String text = pct == 0.0 ? "0%" : String.format(Locale.US, "%+.1f%%", pct);
		Color color = pct > 0 ? StockpileColors.HIGH : (pct < 0 ? StockpileColors.LOW : StockpileColors.MUTED);
		return new Cell(text, color, text + " vs. " + window.getLongLabel() + " avg.");
	}

	/**
	 * @return the stats the active window reads its figures from: the most recent 5-minute datapoint for
	 *         the {@code 5m} window ({@link TimeWindow#M5}), else the item's precomputed window stats.
	 */
	private PriceStats windowStats(TrackedItem item)
	{
		if (window == TimeWindow.M5)
			return latestFivePoint(item);

		return item.getWindowStats().get(window);
	}

	/**
	 * @return a {@link PriceStats} built from the most recent 5-minute datapoint (the {@code /5m} series'
	 *         last point): its averaged high and low, their midpoint, and traded volume; {@code null}
	 *         when the series is empty.
	 */
	private static PriceStats latestFivePoint(TrackedItem item)
	{
		List<WikiRealtimePriceClient.PricePoint> series = item.getSeriesFor(TimeWindow.M5);
		if (series.isEmpty())
			return null;

		WikiRealtimePriceClient.PricePoint p = series.get(series.size() - 1);
		long high = p.getAvgHighPrice();
		long low = p.getAvgLowPrice();
		long avg = high > 0 && low > 0 ? (high + low) / 2 : Math.max(high, low);
		long vol = p.getHighPriceVolume() + p.getLowPriceVolume();
		return new PriceStats(high, low, avg, vol);
	}

	/**
	 * @return the item's price series for the active window, clipped to that window's look-back so the
	 *         trend and volume charts differ between windows drawn from the same underlying series. The
	 *         {@code Latest} window ({@link TimeWindow#LIVE}) shows the full 24h of 5-minute points; the
	 *         {@code 5m} window ({@link TimeWindow#M5}) shows just a zoomed recent span.
	 */
	private List<WikiRealtimePriceClient.PricePoint> windowSeries(TrackedItem item)
	{
		List<WikiRealtimePriceClient.PricePoint> series = item.getSeriesFor(window);
		long secs;
		if (window == TimeWindow.LIVE)
			secs = 0;
		else if (window == TimeWindow.M5)
			secs = RECENT_CHART_SECONDS;
		else
			secs = window.getDuration().getSeconds();

		if (secs <= 0)
			return series;

		long cutoff = System.currentTimeMillis() / 1000L - secs;
		List<WikiRealtimePriceClient.PricePoint> clipped = new ArrayList<>();
		for (WikiRealtimePriceClient.PricePoint p : series)
		{
			if (p.getTimestamp() >= cutoff)
				clipped.add(p);
		}

		return clipped;
	}

	/** @return the active window's average price, or 0 when unknown. */
	private long windowAvg(TrackedItem item)
	{
		PriceStats s = windowStats(item);
		return s == null ? 0 : s.getAvg();
	}

	/** @return the active window's high (buy) price, or 0 when unknown. */
	private long windowHigh(TrackedItem item)
	{
		PriceStats s = windowStats(item);
		return s == null ? 0 : s.getHigh();
	}

	/** @return the active window's low (sell) price, or 0 when unknown. */
	private long windowLow(TrackedItem item)
	{
		PriceStats s = windowStats(item);
		return s == null ? 0 : s.getLow();
	}

	/** @return the active window's traded volume, or 0 when unknown. */
	private long windowVol(TrackedItem item)
	{
		PriceStats s = windowStats(item);
		return s == null ? 0 : s.getVolume();
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
		return new Cell(GpFormat.signedGrouped(profit), color, GpFormat.signedGrouped(profit) + " gp per cast");
	}

	/** @return the tracked quantity cell, blank for an untracked item. */
	private Cell qtyCell(Entry entry)
	{
		if (!entry.tracked)
			return Cell.placeholder();

		int qty = entry.item.getQuantity();
		return new Cell(GpFormat.grouped(qty), Color.WHITE, GpFormat.grouped(qty) + " held");
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
		return new Cell(GpFormat.signedGrouped(profit), color, GpFormat.signedGrouped(profit) + " gp realized");
	}

	/** @return the item's traded volume over the past 24 hours from its window stats, or 0 when unknown. */
	private static long vol24(TrackedItem item)
	{
		PriceStats stats = item.getWindowStats().get(TimeWindow.H24);
		return stats == null ? 0 : stats.getVolume();
	}

	/**
	 * @param from the colour to start from
	 * @param to the colour to move toward
	 * @param t the fraction (0..1) of the way from {@code from} to {@code to}
	 * @return {@code from} blended {@code t} of the way toward {@code to}
	 */
	private static Color blend(Color from, Color to, float t)
	{
		int r = Math.round(from.getRed() + (to.getRed() - from.getRed()) * t);
		int g = Math.round(from.getGreen() + (to.getGreen() - from.getGreen()) * t);
		int b = Math.round(from.getBlue() + (to.getBlue() - from.getBlue()) * t);
		return new Color(r, g, b);
	}

	/** @return a right-aligned, right-padded muted label for the trend and volume rows in the label column. */
	private static JLabel chartLabel(String text)
	{
		JLabel label = mutedLabel(text, SwingConstants.RIGHT);
		label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, LABEL_PAD_R));
		return label;
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

	/**
	 * Fixes a component's width and minimum height while letting its height grow unbounded, so the trend
	 * row stretches to absorb the column's leftover vertical space. {@code growWidth} lets the sparkline
	 * also fill its column's width, while the label column's trend cell keeps its fixed width.
	 */
	private static JComponent growHeight(JComponent component, int width, int minHeight, boolean growWidth)
	{
		component.setMinimumSize(new Dimension(width, minHeight));
		component.setPreferredSize(new Dimension(width, minHeight));
		component.setMaximumSize(new Dimension(growWidth ? Integer.MAX_VALUE : width, Integer.MAX_VALUE));
		component.setAlignmentX(Component.LEFT_ALIGNMENT);
		return component;
	}

	/**
	 * Fixes a column's minimum width while letting it grow unbounded, so the layout stretches columns to
	 * share the viewport when they fit and holds them at {@code minWidth} once they overflow. The column's
	 * preferred height is left to compute from its rows (it is sized after they are added).
	 */
	private static void flexWidth(JComponent component, int minWidth)
	{
		component.setMinimumSize(new Dimension(minWidth, 0));
		component.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		component.setAlignmentY(Component.TOP_ALIGNMENT);
	}

	/** @return this view's own preferred size as the preferred viewport size. */
	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return getPreferredSize();
	}

	/** @return a fixed unit scroll increment matching the enclosing scroll pane's step. */
	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 16;
	}

	/** @return a block scroll increment of one visible page along the scroll axis. */
	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return orientation == SwingConstants.HORIZONTAL ? visibleRect.width : visibleRect.height;
	}

	/**
	 * @return {@code true} when the columns fit the viewport, so the layout stretches them to fill its
	 *         width; {@code false} once their combined minimum width overflows, so they hold their width
	 *         and the pane scrolls horizontally instead.
	 */
	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		Container parent = SwingUtilities.getUnwrappedParent(this);
		if (parent instanceof JViewport)
			return parent.getWidth() >= getPreferredSize().width;

		return false;
	}

	/**
	 * @return {@code true} when the grid fits the viewport, so the trend row stretches to fill its height;
	 *         {@code false} once the rows overflow, so the pane scrolls vertically instead of squashing.
	 */
	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		Container parent = SwingUtilities.getUnwrappedParent(this);
		if (parent instanceof JViewport)
			return parent.getHeight() >= getPreferredSize().height;

		return false;
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
	 * A faint horizontal rule filling a section row's cell in an item column, so each section boundary
	 * reads as a continuous line across the columns rather than a blank gap.
	 */
	private static final class Rule extends JComponent
	{
		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			int y = getHeight() / 2;
			g.setColor(SECTION_RULE);
			g.drawLine(6, y, getWidth() - 6, y);
		}
	}

	/**
	 * A small custom-painted price sparkline for one item: the active window series' midpoints scaled to
	 * the row, giving an at-a-glance trend beneath each column.
	 */
	private static final class Sparkline extends JComponent
	{
		private final List<WikiRealtimePriceClient.PricePoint> series;

		/**
		 * @param series the price points to plot (an empty list paints nothing)
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
			g2.setStroke(new BasicStroke(0.7f));

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

	/**
	 * A small custom-painted volume bar chart for one item: each active-window point's total traded
	 * volume (high plus low) drawn as a bar scaled to the row, sitting beneath the price trend.
	 */
	private static final class VolumeChart extends JComponent
	{
		private final List<WikiRealtimePriceClient.PricePoint> series;

		/**
		 * @param series the price points whose volumes to plot (an empty list paints nothing)
		 */
		VolumeChart(List<WikiRealtimePriceClient.PricePoint> series)
		{
			this.series = series;
			setBackground(ColorScheme.DARK_GRAY_COLOR);
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			List<Long> volumes = volumes();
			long max = 0;
			for (long v : volumes)
				max = Math.max(max, v);

			if (max <= 0)
				return;

			int w = getWidth();
			int h = getHeight();
			int padX = 6;
			int padY = 4;
			int plotH = h - 2 * padY;
			double slotW = (double) (w - 2 * padX) / volumes.size();

			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(StockpileColors.MUTED);
			for (int i = 0; i < volumes.size(); i++)
			{
				int barH = (int) Math.round((double) volumes.get(i) / max * plotH);
				if (barH <= 0)
					continue;

				int x = padX + (int) Math.round(i * slotW);
				int bw = Math.max(1, (int) Math.round(slotW) - 1);
				g2.fillRect(x, padY + plotH - barH, bw, barH);
			}

			g2.dispose();
		}

		/** @return each point's total traded volume (high plus low) in series order. */
		private List<Long> volumes()
		{
			List<Long> out = new ArrayList<>();
			for (WikiRealtimePriceClient.PricePoint p : series)
				out.add(p.getHighPriceVolume() + p.getLowPriceVolume());

			return out;
		}
	}
}
