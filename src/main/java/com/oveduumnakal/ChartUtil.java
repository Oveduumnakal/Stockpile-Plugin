/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.function.IntToLongFunction;

/**
 * Shared plotting maths and hover-overlay drawing for the plugin's line charts, hoisted so
 * {@link PriceGraphPanel} and {@link PortfolioChartPanel} share one copy instead of near-identical
 * duplicates (#177) — a tick-selection, tooltip-clamping, or DPI fix now lives in one place rather
 * than drifting between the two. Colours come from {@link StockpileColors}.
 */
final class ChartUtil
{
	private ChartUtil()
	{
	}

	/**
	 * Picks a human-friendly value axis covering {@code [dataMin, dataMax]} using a 1/2/2.5/5 step
	 * progression so labels land on round numbers.
	 *
	 * @param minTicks minimum number of gridlines to aim for
	 * @param maxTicks maximum number of gridlines to allow
	 * @return {@code [axisMin, axisMax, ticks]}
	 */
	static double[] niceAxis(long dataMin, long dataMax, int minTicks, int maxTicks)
	{
		if (dataMax <= dataMin)
			dataMax = dataMin + 1;

		double range = dataMax - dataMin;
		double[] niceMults = {1, 2, 2.5, 5};
		double bestStep = range / minTicks;
		double chosenStep = -1;
		for (int k = -2; k <= 12 && chosenStep < 0; k++)
		{
			double pow = Math.pow(10, k);
			for (double m : niceMults)
			{
				double step = m * pow;
				if (step <= 0)
					continue;

				double aMin = Math.floor(dataMin / step) * step;
				double aMax = Math.ceil(dataMax / step) * step;
				int count = (int) Math.round((aMax - aMin) / step);
				if (count >= minTicks && count <= maxTicks)
				{
					chosenStep = step;
					break;
				}
			}
		}

		if (chosenStep < 0)
			chosenStep = bestStep;

		double axisMin = Math.floor(dataMin / chosenStep) * chosenStep;
		double axisMax = Math.ceil(dataMax / chosenStep) * chosenStep;
		int ticks = Math.max(1, (int) Math.round((axisMax - axisMin) / chosenStep));
		return new double[]{axisMin, axisMax, ticks};
	}

	/** Maps {@code value} to its y pixel within the plot, clamped to {@code [top, bottom]}. */
	static int clampedY(long value, double axisMin, double axisRange, int top, int bottom, int plotH)
	{
		int y = bottom - (int) ((value - axisMin) / axisRange * plotH);
		if (y < top)
			return top;

		if (y > bottom)
			return bottom;

		return y;
	}

	/** Draws a string rotated 90° (reading bottom-to-top) hanging below the axis at {@code cx}. */
	static void drawVerticalLabel(Graphics2D g2, String s, int cx, int topY, FontMetrics fm)
	{
		Graphics2D gg = (Graphics2D) g2.create();
		try
		{
			gg.translate(cx, topY);
			gg.rotate(-Math.PI / 2);
			gg.drawString(s, -fm.stringWidth(s), fm.getAscent() / 2);
		}
		finally
		{
			gg.dispose();
		}
	}

	/**
	 * @return the index in {@code [0, count)} whose plotted x pixel is nearest {@code hoverX}, or -1
	 *         when {@code count} is 0. {@code timeAt} yields the epoch-second timestamp of each index;
	 *         points map to x via {@code plotLeft + (t - startSec) / span * plotW}.
	 */
	static int closestIndex(int hoverX, int count, IntToLongFunction timeAt, int plotLeft, int plotW,
			long startSec, double span)
	{
		int best = -1;
		int bestDx = Integer.MAX_VALUE;
		for (int i = 0; i < count; i++)
		{
			int x = plotLeft + (int) ((timeAt.applyAsLong(i) - startSec) / span * plotW);
			int dx = Math.abs(x - hoverX);
			if (dx < bestDx)
			{
				bestDx = dx;
				best = i;
			}
		}

		return best;
	}

	/**
	 * Positions and paints the hover-tooltip background box: sized to {@code contentWidth} (the widest
	 * line) and {@code lineCount}, anchored right of {@code hoverX} but flipped left when it would
	 * overflow {@code plotRight} and clamped to {@code plotLeft}. The caller draws its own lines.
	 *
	 * @return {@code [textX, firstBaselineY]} — the left text origin and the baseline of the first line;
	 *         successive lines advance by {@code fm.getHeight() + 1}
	 */
	static int[] drawTooltipBox(Graphics2D g2, int hoverX, int plotLeft, int plotTop, int plotRight,
			int contentWidth, int lineCount, FontMetrics fm)
	{
		int boxW = contentWidth + 8;
		int boxH = lineCount * (fm.getHeight() + 1) + 4;
		int bx = hoverX + 8;
		if (bx + boxW > plotRight)
			bx = hoverX - 8 - boxW;

		if (bx < plotLeft)
			bx = plotLeft;

		int by = plotTop + 4;
		g2.setColor(StockpileColors.CHART_TOOLTIP_BG);
		g2.fillRoundRect(bx, by, boxW, boxH, 6, 6);
		return new int[]{bx + 4, by + fm.getAscent() + 2};
	}
}
