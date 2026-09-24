/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oveduumnakal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Map;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

/**
 * Scene overlay that outlines tracked items lying on the ground.
 *
 * <p>On each frame it walks the plugin's <em>tracked</em> ground items and draws their tile polygon
 * in the configured highlight color &ndash; pulsing via the plugin's breathing alpha. Does nothing
 * when ground highlighting is disabled in config.
 *
 * <p>The plugin maintains that subset, so the per-frame cost is proportional to what is drawn. This
 * used to walk every ground item in the scene and canonicalize each one, which at the Grand
 * Exchange, Wintertodt or a death pile is thousands of item-manager calls per frame to find
 * the two that matter (#325).
 */
public class StockpileGroundOverlay extends Overlay
{
	private final Client client;
	private final StockpilePlugin plugin;
	private final StockpileConfig config;

	/** The last colour handed to {@link #breathingColor()}, reused while the colour and alpha hold. */
	private Color cachedBreathing;

	/** The configured highlight colour {@link #cachedBreathing} was built from. */
	private Color cachedBase;

	/** The alpha {@link #cachedBreathing} was built at. */
	private int cachedAlpha = -1;

	@Inject
	StockpileGroundOverlay(Client client, StockpilePlugin plugin, StockpileConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	/**
	 * Draws the ground-item highlights for tracked items on the game scene.
	 *
	 * @param graphics the overlay graphics context
	 * @return {@code null} (this overlay has no fixed bounds)
	 */
	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.highlightTrackedItems().ground())
			return null;

		Map<TileItem, Tile> tracked = plugin.getTrackedGroundItems();
		if (tracked.isEmpty())
			return null;

		graphics.setColor(breathingColor());

		for (Map.Entry<TileItem, Tile> entry : tracked.entrySet())
		{
			Tile tile = entry.getValue();
			Shape poly = Perspective.getCanvasTilePoly(client, tile.getLocalLocation());
			if (poly != null)
				graphics.draw(poly);
		}

		return null;
	}

	/**
	 * @return the highlight colour at the current breathing alpha, reusing the last instance while
	 *         neither the configured colour nor the rounded alpha has moved. The alpha changes only
	 *         once per frame and repeats across frames, so this allocates on a step rather than on
	 *         every paint.
	 */
	private Color breathingColor()
	{
		Color base = config.highlightColor();
		int alpha = Math.round(plugin.breathingAlpha() * 255);
		if (cachedBreathing == null || cachedBase == null || !cachedBase.equals(base) || cachedAlpha != alpha)
		{
			cachedBase = base;
			cachedAlpha = alpha;
			cachedBreathing = new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);
		}

		return cachedBreathing;
	}
}
