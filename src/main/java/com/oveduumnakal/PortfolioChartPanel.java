/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

/**
 * A compact line chart of total portfolio value over time (gold), with a second
 * grey line for cost basis where it is initialized. Mirrors the rendering approach
 * of {@link PriceGraphPanel} — antialiased lines over a padded plot with min/max
 * value labels and a start/end date axis — but plots the single portfolio series
 * from {@link PortfolioHistory} points ({@code {epochSeconds, value, costBasis}}).
 */
public final class PortfolioChartPanel extends JPanel
{
	private static final Color VALUE_LINE = StockpileColors.AVG;

	private static final Color COST_LINE = new Color(150, 150, 150);

	private static final Color GRID = new Color(60, 60, 60);

	private static final int PAD_LEFT = 6;

	private static final int PAD_RIGHT = 6;

	private static final int PAD_TOP = 10;

	private static final int PAD_BOTTOM = 18;

	private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d MMM").withZone(ZoneId.systemDefault());

	private List<long[]> points = Collections.emptyList();

	public PortfolioChartPanel()
	{
		setPreferredSize(new Dimension(360, 240));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setFont(FontManager.getRunescapeSmallFont());
	}

	/** Sets the points to plot ({@code {epochSeconds, value, costBasis}}) and repaints. */
	public void setData(List<long[]> data)
	{
		points = data != null ? data : Collections.emptyList();
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int width = getWidth();
		int height = getHeight();

		if (points.size() < 2)
		{
			drawCentered(g2, "Not enough history yet — check back later.", width, height);
			g2.dispose();
			return;
		}

		long minTime = points.get(0)[0];
		long maxTime = points.get(points.size() - 1)[0];
		long minValue = Long.MAX_VALUE;
		long maxValue = Long.MIN_VALUE;
		for (long[] p : points)
		{
			minValue = Math.min(minValue, Math.min(p[1], costOrValue(p)));
			maxValue = Math.max(maxValue, Math.max(p[1], costOrValue(p)));
		}

		if (maxValue == minValue)
			maxValue = minValue + 1;

		int plotLeft = PAD_LEFT;
		int plotRight = width - PAD_RIGHT;
		int plotTop = PAD_TOP;
		int plotBottom = height - PAD_BOTTOM;

		drawGuides(g2, plotLeft, plotRight, plotTop, plotBottom, minValue, maxValue, minTime, maxTime);
		drawSeries(g2, plotLeft, plotRight, plotTop, plotBottom, minTime, maxTime, minValue, maxValue, false);
		drawSeries(g2, plotLeft, plotRight, plotTop, plotBottom, minTime, maxTime, minValue, maxValue, true);

		g2.dispose();
	}

	/** @return the cost basis when initialized (&gt; 0), else the value, so a zero cost line doesn't skew the scale. */
	private long costOrValue(long[] point)
	{
		return point[2] > 0 ? point[2] : point[1];
	}

	private void drawGuides(Graphics2D g2, int left, int right, int top, int bottom,
			long minValue, long maxValue, long minTime, long maxTime)
	{
		g2.setColor(GRID);
		g2.drawLine(left, top, left, bottom);
		g2.drawLine(left, bottom, right, bottom);

		g2.setColor(ColorScheme.LIGHT_GRAY_COLOR);
		g2.drawString(GpFormat.shortValue(maxValue), left + 2, top + 9);
		g2.drawString(GpFormat.shortValue(minValue), left + 2, bottom - 2);

		String start = DATE.format(Instant.ofEpochSecond(minTime));
		String end = DATE.format(Instant.ofEpochSecond(maxTime));
		g2.drawString(start, left + 2, bottom + 14);

		int endWidth = g2.getFontMetrics().stringWidth(end);
		g2.drawString(end, right - endWidth, bottom + 14);
	}

	private void drawSeries(Graphics2D g2, int left, int right, int top, int bottom,
			long minTime, long maxTime, long minValue, long maxValue, boolean cost)
	{
		g2.setColor(cost ? COST_LINE : VALUE_LINE);
		g2.setStroke(new BasicStroke(cost ? 1f : 1.6f));

		double timeSpan = Math.max(1, maxTime - minTime);
		double valueSpan = Math.max(1, maxValue - minValue);

		int prevX = 0;
		int prevY = 0;
		boolean have = false;
		for (long[] p : points)
		{
			long value = cost ? p[2] : p[1];
			if (cost && value <= 0)
			{
				have = false;
				continue;
			}

			int x = left + (int) ((p[0] - minTime) / timeSpan * (right - left));
			int y = bottom - (int) ((value - minValue) / valueSpan * (bottom - top));
			if (have)
				g2.drawLine(prevX, prevY, x, y);

			prevX = x;
			prevY = y;
			have = true;
		}
	}

	private void drawCentered(Graphics2D g2, String text, int width, int height)
	{
		g2.setColor(ColorScheme.LIGHT_GRAY_COLOR);
		int textWidth = g2.getFontMetrics().stringWidth(text);
		g2.drawString(text, (width - textWidth) / 2, height / 2);
	}
}
