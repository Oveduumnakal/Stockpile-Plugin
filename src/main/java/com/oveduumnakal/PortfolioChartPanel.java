/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.swing.JPanel;

import net.runelite.client.ui.ColorScheme;

/**
 * A line chart of total portfolio value over time against a grey cost-basis line.
 * The value line is coloured by its position relative to cost basis — green where
 * it sits above (in profit), red below (in loss), grey when equal — splitting each
 * segment at the crossing point. Mirrors the look and feel of {@link PriceGraphPanel}:
 * a "nice" value axis with horizontal gridlines and right-side labels, rotated date
 * labels along the bottom, a legend, and a hover crosshair whose tooltip reports the
 * value, cost, and profit at the point nearest the cursor.
 *
 * <p>Plots the series from {@link PortfolioHistory} points
 * ({@code {epochSeconds, value, costBasis}}). Consecutive points are always joined,
 * so an offline gap reads as one connecting segment between the two known values.
 */
public final class PortfolioChartPanel extends JPanel
{
	private static final Color VALUE_UP = StockpileColors.HIGH;

	private static final Color VALUE_DOWN = StockpileColors.LOW;

	private static final Color VALUE_FLAT = new Color(180, 180, 180);

	private static final Color COST_LINE = StockpileColors.MUTED;

	private static final Color GRID = StockpileColors.CHART_GRID;

	private static final Color AXIS_TEXT = Color.GRAY;

	private static final Color CROSSHAIR = StockpileColors.CHART_CROSSHAIR;

	private static final Color TOOLTIP_LABEL = new Color(160, 160, 160);

	private static final Color TOOLTIP_VALUE = Color.WHITE;

	private static final DateTimeFormatter DAY_LABEL =
			DateTimeFormatter.ofPattern("d MMM", Locale.US).withZone(ZoneId.systemDefault());

	private static final DateTimeFormatter HOUR_LABEL =
			DateTimeFormatter.ofPattern("ha", Locale.US).withZone(ZoneId.systemDefault());

	private static final DateTimeFormatter TOOLTIP_TIME =
			DateTimeFormatter.ofPattern("MMMM d, hh:mm a", Locale.US).withZone(ZoneId.systemDefault());

	private static final long HOUR = 3600L;

	private static final long DAY = 86_400L;

	private static final int LEFT_PAD = 10;

	private static final int TOP_PAD = 8;

	private static final int X_LABEL_GAP = 12;

	private final Font baseFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);

	private List<long[]> points = Collections.emptyList();

	private int hoverX = -1;

	/** Rasterized static plot (grid, axes, legend, series); only the hover overlay is redrawn on mouse moves. */
	private transient BufferedImage plotCache;

	private boolean plotCacheDirty = true;

	/** Creates an empty portfolio chart panel. */
	public PortfolioChartPanel()
	{
		setPreferredSize(new Dimension(520, 320));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setFont(baseFont);

		addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				hoverX = e.getX();
				repaint();
			}
		});
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseExited(MouseEvent e)
			{
				hoverX = -1;
				repaint();
			}
		});
	}

	/** Sets the points to plot ({@code {epochSeconds, value, costBasis}}) and repaints. */
	public void setData(List<long[]> data)
	{
		List<long[]> next = data != null ? data : Collections.emptyList();
		if (samePoints(points, next))
			return;

		points = next;
		plotCacheDirty = true;
		repaint();
	}

	/**
	 * @return whether two point series are equal, so an unchanged rebuild does not re-rasterize the
	 *         full chart (a new ARGB image + both series) (#184). Cheaper than the raster it guards.
	 */
	private static boolean samePoints(List<long[]> a, List<long[]> b)
	{
		if (a == b)
			return true;

		if (a.size() != b.size())
			return false;

		for (int i = 0; i < a.size(); i++)
			if (!Arrays.equals(a.get(i), b.get(i)))
				return false;

		return true;
	}

	/**
	 * Paints the chart: the expensive static plot (grid, axes, legend, series) is
	 * rasterized once into {@link #plotCache} and reused, while only the lightweight
	 * hover crosshair is redrawn over it on mouse moves. The cheap layout is recomputed
	 * each paint so the hover overlay maps correctly onto the cached pixels.
	 */
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		int width = getWidth();
		int height = getHeight();
		if (width <= 0 || height <= 0)
			return;

		if (points.size() < 2)
		{
			Graphics2D g2 = (Graphics2D) g.create();
			try
			{
				g2.setFont(baseFont);
				drawCentered(g2, "Not enough history yet — check back later.", width, height);
			}
			finally
			{
				g2.dispose();
			}

			return;
		}

		FontMetrics fm = getFontMetrics(baseFont);
		int rightAxisWidth = fm.stringWidth("999.9M") + 8;
		int bottomAxisHeight = X_LABEL_GAP + fm.stringWidth("00 Mmm") + 4;
		int legendHeight = fm.getHeight() + 6;

		int plotLeft = LEFT_PAD;
		int plotTop = TOP_PAD + legendHeight;
		int plotRight = width - rightAxisWidth;
		int plotBottom = height - bottomAxisHeight;
		int plotW = Math.max(1, plotRight - plotLeft);
		int plotH = Math.max(1, plotBottom - plotTop);

		long minTime = points.get(0)[0];
		long maxTime = points.get(points.size() - 1)[0];

		long dataMin = Long.MAX_VALUE;
		long dataMax = Long.MIN_VALUE;
		for (long[] p : points)
		{
			dataMin = Math.min(dataMin, p[1]);
			dataMax = Math.max(dataMax, p[1]);
			if (p[2] > 0)
			{
				dataMin = Math.min(dataMin, p[2]);
				dataMax = Math.max(dataMax, p[2]);
			}
		}

		double[] axis = ChartUtil.niceAxis(dataMin, dataMax, 5, 9);
		double axisMin = axis[0];
		double axisMax = axis[1];
		int ticks = (int) axis[2];
		double axisRange = Math.max(1, axisMax - axisMin);

		if (plotCache == null || plotCache.getWidth() != width || plotCache.getHeight() != height || plotCacheDirty)
		{
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D cg = img.createGraphics();
			try
			{
				cg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				cg.setFont(baseFont);
				boolean anyCost = points.stream().anyMatch(p -> p[2] > 0);

				drawYAxis(cg, fm, plotLeft, plotRight, plotTop, plotBottom, plotH, axisMin, axisRange, ticks);
				drawXAxis(cg, fm, plotLeft, plotBottom, plotW, minTime, maxTime);
				drawLegend(cg, fm, plotLeft, anyCost);
				drawCostLine(cg, plotLeft, plotTop, plotBottom, plotW, plotH,
						minTime, maxTime, axisMin, axisRange);
				drawValueLine(cg, plotLeft, plotTop, plotBottom, plotW, plotH,
						minTime, maxTime, axisMin, axisRange);
			}
			finally
			{
				cg.dispose();
			}

			plotCache = img;
			plotCacheDirty = false;
		}

		g.drawImage(plotCache, 0, 0, null);

		if (hoverX >= plotLeft && hoverX <= plotRight)
		{
			Graphics2D g2 = (Graphics2D) g.create();
			try
			{
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setFont(baseFont);
				drawHover(g2, fm, plotLeft, plotTop, plotBottom, plotW, plotH,
						minTime, maxTime, axisMin, axisRange);
			}
			finally
			{
				g2.dispose();
			}
		}
	}

	/** Draws the horizontal gridlines and their right-side value labels for the "nice" value axis. */
	private void drawYAxis(Graphics2D g2, FontMetrics fm, int left, int right, int top, int bottom,
			int plotH, double axisMin, double axisRange, int ticks)
	{
		for (int i = 0; i <= ticks; i++)
		{
			int y = bottom - (int) ((double) plotH * i / ticks);
			g2.setColor(GRID);
			g2.drawLine(left, y, right, y);

			long val = (long) (axisMin + axisRange * i / ticks);
			g2.setColor(AXIS_TEXT);
			g2.drawString(GpFormat.shortValue(val), right + 4, y + fm.getAscent() / 2);
		}
	}

	/** Draws faint vertical gridlines and rotated date labels at "nice" time ticks along the bottom. */
	private void drawXAxis(Graphics2D g2, FontMetrics fm, int left, int bottom, int plotW,
			long minTime, long maxTime)
	{
		double span = Math.max(1, maxTime - minTime);
		boolean dayStep = maxTime - minTime >= 2 * DAY;
		DateTimeFormatter fmt = dayStep ? DAY_LABEL : HOUR_LABEL;

		for (long ts : buildTimeTicks(minTime, maxTime, Math.max(3, Math.min(8, plotW / 60))))
		{
			int x = left + (int) ((ts - minTime) / span * plotW);
			g2.setColor(GRID);
			g2.drawLine(x, bottom, x, bottom - 3);
			g2.setColor(AXIS_TEXT);
			ChartUtil.drawVerticalLabel(g2, fmt.format(Instant.ofEpochSecond(ts)), x, bottom + X_LABEL_GAP, fm);
		}
	}

	/**
	 * Draws the legend: with cost basis present, a grey "Cost basis" line and the green
	 * "Profit" / red "Loss" states of the value line; otherwise just a neutral "Value" swatch.
	 */
	private void drawLegend(Graphics2D g2, FontMetrics fm, int left, boolean anyCost)
	{
		int y = TOP_PAD + fm.getAscent();
		int x = left;
		if (anyCost)
		{
			x = drawLegendEntry(g2, fm, x, y, COST_LINE, "Cost basis");
			x = drawLegendEntry(g2, fm, x, y, VALUE_UP, "Profit");
			drawLegendEntry(g2, fm, x, y, VALUE_DOWN, "Loss");
		}
		else
		{
			drawLegendEntry(g2, fm, x, y, VALUE_FLAT, "Value");
		}
	}

	/** Draws one legend swatch + label starting at {@code x}; returns the x just past it. */
	private int drawLegendEntry(Graphics2D g2, FontMetrics fm, int x, int y, Color color, String label)
	{
		g2.setColor(color);
		g2.setStroke(new BasicStroke(2f));
		g2.drawLine(x, y - fm.getAscent() / 2, x + 16, y - fm.getAscent() / 2);

		g2.setColor(ColorScheme.LIGHT_GRAY_COLOR);
		g2.drawString(label, x + 20, y);
		return x + 20 + fm.stringWidth(label) + 16;
	}

	/** Draws the grey cost-basis line, joining consecutive points and breaking where cost basis is absent. */
	private void drawCostLine(Graphics2D g2, int left, int top, int bottom, int plotW, int plotH,
			long minTime, long maxTime, double axisMin, double axisRange)
	{
		g2.setColor(COST_LINE);
		g2.setStroke(new BasicStroke(1f));

		double timeSpan = Math.max(1, maxTime - minTime);

		int prevX = 0;
		int prevY = 0;
		boolean have = false;
		for (long[] p : points)
		{
			if (p[2] <= 0)
			{
				have = false;
				continue;
			}

			int x = left + (int) ((p[0] - minTime) / timeSpan * plotW);
			int y = ChartUtil.clampedY(p[2], axisMin, axisRange, top, bottom, plotH);
			if (have)
				g2.drawLine(prevX, prevY, x, y);

			prevX = x;
			prevY = y;
			have = true;
		}
	}

	/**
	 * Draws the value line, colouring each segment by the value's position relative to cost
	 * basis — green above (profit), red below (loss), grey when equal or when no cost basis
	 * exists — and splitting a segment at the point where the two lines cross.
	 */
	private void drawValueLine(Graphics2D g2, int left, int top, int bottom, int plotW, int plotH,
			long minTime, long maxTime, double axisMin, double axisRange)
	{
		g2.setStroke(new BasicStroke(1.6f));
		double timeSpan = Math.max(1, maxTime - minTime);

		for (int i = 0; i < points.size() - 1; i++)
		{
			long[] a = points.get(i);
			long[] b = points.get(i + 1);

			int ax = left + (int) ((a[0] - minTime) / timeSpan * plotW);
			int ay = ChartUtil.clampedY(a[1], axisMin, axisRange, top, bottom, plotH);
			int bx = left + (int) ((b[0] - minTime) / timeSpan * plotW);
			int by = ChartUtil.clampedY(b[1], axisMin, axisRange, top, bottom, plotH);

			boolean noBasis = a[2] <= 0 || b[2] <= 0;
			long da = a[1] - a[2];
			long db = b[1] - b[2];

			if (!noBasis && da > 0 != db > 0 && da != 0 && db != 0)
			{
				double t = (double) da / (da - db);
				int cx = ax + (int) Math.round((bx - ax) * t);
				int cy = ay + (int) Math.round((by - ay) * t);
				g2.setColor(diffColor(da));
				g2.drawLine(ax, ay, cx, cy);
				g2.setColor(diffColor(db));
				g2.drawLine(cx, cy, bx, by);
			}
			else
			{
				g2.setColor(noBasis ? VALUE_FLAT : diffColor(da != 0 ? da : db));
				g2.drawLine(ax, ay, bx, by);
			}
		}
	}

	/** @return green when {@code diff} (value − cost) is positive, red when negative, grey when zero. */
	private static Color diffColor(long diff)
	{
		return diff > 0 ? VALUE_UP : diff < 0 ? VALUE_DOWN : VALUE_FLAT;
	}

	/**
	 * Draws the hover overlay: a vertical crosshair at the cursor, dots on the value and
	 * cost lines at the nearest point, and a tooltip box with its date, value, cost, and
	 * unrealized profit.
	 */
	private void drawHover(Graphics2D g2, FontMetrics fm, int plotLeft, int plotTop, int plotBottom,
			int plotW, int plotH, long minTime, long maxTime, double axisMin, double axisRange)
	{
		double timeSpan = Math.max(1, maxTime - minTime);
		int idx = ChartUtil.closestIndex(hoverX, points.size(), i -> points.get(i)[0],
				plotLeft, plotW, minTime, timeSpan);
		if (idx < 0)
			return;

		long[] p = points.get(idx);
		int x = plotLeft + (int) ((p[0] - minTime) / timeSpan * plotW);

		g2.setStroke(new BasicStroke(1));
		g2.setColor(CROSSHAIR);
		g2.drawLine(x, plotTop, x, plotBottom);

		int valueY = ChartUtil.clampedY(p[1], axisMin, axisRange, plotTop, plotBottom, plotH);
		g2.setColor(p[2] > 0 ? diffColor(p[1] - p[2]) : VALUE_FLAT);
		g2.fillOval(x - 3, valueY - 3, 6, 6);

		List<TipLine> lines = new ArrayList<>();
		lines.add(new TipLine(TOOLTIP_TIME.format(Instant.ofEpochSecond(p[0])), "", null));
		lines.add(new TipLine("Value:  ", GpFormat.grouped(p[1]), TOOLTIP_VALUE));
		if (p[2] > 0)
		{
			int costY = ChartUtil.clampedY(p[2], axisMin, axisRange, plotTop, plotBottom, plotH);
			g2.setColor(COST_LINE);
			g2.fillOval(x - 3, costY - 3, 6, 6);

			lines.add(new TipLine("Cost:   ", GpFormat.grouped(p[2]), TOOLTIP_VALUE));
			long profit = p[1] - p[2];
			lines.add(new TipLine("Profit: ", GpFormat.signedGrouped(profit),
					profit >= 0 ? VALUE_UP : VALUE_DOWN));
		}

		drawTooltip(g2, fm, lines, plotLeft, plotTop, plotLeft + plotW);
	}

	/**
	 * Draws the hover tooltip box, flipping to the cursor's left near the right edge. Each
	 * line's label is drawn muted and its value in the line's own colour, so the numbers
	 * stand out from the labels (and profit reads green/red).
	 */
	private void drawTooltip(Graphics2D g2, FontMetrics fm, List<TipLine> lines,
			int plotLeft, int plotTop, int plotRight)
	{
		int boxW = 0;
		for (TipLine l : lines)
			boxW = Math.max(boxW, fm.stringWidth(l.label + l.value));

		int[] origin = ChartUtil.drawTooltipBox(g2, hoverX, plotLeft, plotTop, plotRight, boxW,
				lines.size(), fm);
		int tx = origin[0];
		int ty = origin[1];
		for (TipLine l : lines)
		{
			g2.setColor(l.valueColor == null ? Color.WHITE : TOOLTIP_LABEL);
			g2.drawString(l.label, tx, ty);
			if (l.valueColor != null)
			{
				g2.setColor(l.valueColor);
				g2.drawString(l.value, tx + fm.stringWidth(l.label), ty);
			}

			ty += fm.getHeight() + 1;
		}
	}

	/** One hover-tooltip line: a muted {@code label} and a {@code value} in {@code valueColor} (null = label only). */
	private static final class TipLine
	{
		private final String label;

		private final String value;

		private final Color valueColor;

		private TipLine(String label, String value, Color valueColor)
		{
			this.label = label;
			this.value = value;
			this.valueColor = valueColor;
		}
	}

	/**
	 * Builds evenly spaced time ticks snapped to natural boundaries: whole days for a
	 * span of two days or more (else whole hours), with the step widened so at most
	 * {@code target} ticks fall in the visible range.
	 *
	 * @return tick timestamps in epoch seconds within {@code [minTime, maxTime]}
	 */
	private List<Long> buildTimeTicks(long minTime, long maxTime, int target)
	{
		List<Long> ticks = new ArrayList<>();
		long span = Math.max(1, maxTime - minTime);
		ZoneId zone = ZoneId.systemDefault();

		long stepSec;
		ZonedDateTime cursor;
		if (span >= 2 * DAY)
		{
			long days = Math.max(1, span / DAY);
			long[] candidates = {1, 2, 3, 7, 14, 30, 60, 90};
			long chosen = candidates[candidates.length - 1];
			for (long d : candidates)
				if (days / d <= target)
				{
					chosen = d;
					break;
				}

			stepSec = chosen * DAY;
			cursor = Instant.ofEpochSecond(minTime)
					.atZone(zone)
					.toLocalDate()
					.atStartOfDay(zone);
		}
		else
		{
			long hours = Math.max(1, span / HOUR);
			long[] candidates = {1, 2, 3, 6, 12};
			long chosen = candidates[candidates.length - 1];
			for (long h : candidates)
				if (hours / h <= target)
				{
					chosen = h;
					break;
				}

			stepSec = chosen * HOUR;
			cursor = Instant.ofEpochSecond(minTime)
					.atZone(zone)
					.truncatedTo(ChronoUnit.HOURS);
		}

		long t = cursor.toEpochSecond();
		while (t < minTime)
			t += stepSec;

		while (t <= maxTime)
		{
			ticks.add(t);
			t += stepSec;
		}

		return ticks;
	}

	private void drawCentered(Graphics2D g2, String text, int width, int height)
	{
		g2.setColor(ColorScheme.LIGHT_GRAY_COLOR);
		int textWidth = g2.getFontMetrics().stringWidth(text);
		g2.drawString(text, (width - textWidth) / 2, height / 2);
	}
}
