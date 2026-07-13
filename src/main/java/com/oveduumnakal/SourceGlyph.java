/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

/**
 * Draws a small monochrome vector symbol for each {@link AcquisitionSource}, tinted to the caller's
 * colour. Rendered directly with Java2D (rather than SVG assets) so the glyphs stay crisp at any
 * size and re-tint for the hover highlight with no image recolouring. All shapes are laid out in a
 * normalised box where {@code (0, 0)} is the glyph centre and coordinates run roughly {@code [-1, 1]}.
 */
final class SourceGlyph
{
	private SourceGlyph()
	{
	}

	/** Paints {@code source}'s glyph centred in a {@code w} x {@code h} area in {@code color}. */
	static void draw(Graphics2D g, AcquisitionSource source, int w, int h, Color color)
	{
		Object oldAa = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double cx = w / 2.0;
		double cy = h / 2.0;
		double u = Math.min(w, h) * 0.30;
		float sw = (float) Math.max(1.1, u * 0.22);
		g.setColor(color);
		g.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		switch (source)
		{
			case MANUAL: manual(g, cx, cy, u); break;
			case GE_TRADE: geTrade(g, cx, cy, u); break;
			case GROUND: ground(g, cx, cy, u); break;
			case PLAYER_TRADE: trade(g, cx, cy, u); break;
			case SHOP: shop(g, cx, cy, u); break;
			case ALCHEMY: alchemy(g, cx, cy, u); break;
			case PROCESSING: processing(g, cx, cy, u); break;
			case DEATH: death(g, cx, cy, u); break;
			case UNKNOWN:
			default: unknown(g, cx, cy, u, sw); break;
		}

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				oldAa == null ? RenderingHints.VALUE_ANTIALIAS_DEFAULT : oldAa);
	}

	/** Builds an open/closed polyline from normalised {@code (x, y)} pairs. */
	private static Path2D.Double poly(double cx, double cy, double u, boolean close, double... n)
	{
		Path2D.Double p = new Path2D.Double();
		for (int i = 0; i + 1 < n.length; i += 2)
		{
			double x = cx + n[i] * u;
			double y = cy + n[i + 1] * u;
			if (i == 0)
				p.moveTo(x, y);
			else
				p.lineTo(x, y);
		}

		if (close)
			p.closePath();

		return p;
	}

	/** A filled dot at a normalised centre with device radius {@code r}. */
	private static Ellipse2D.Double dot(double cx, double cy, double u, double nx, double ny, double r)
	{
		return new Ellipse2D.Double(cx + nx * u - r, cy + ny * u - r, 2 * r, 2 * r);
	}

	/** Question mark: a stroked hook over a dot. */
	private static void unknown(Graphics2D g, double cx, double cy, double u, float sw)
	{
		Path2D.Double hook = new Path2D.Double();
		hook.moveTo(cx - 0.42 * u, cy - 0.30 * u);
		hook.quadTo(cx - 0.48 * u, cy - 0.95 * u, cx + 0.02 * u, cy - 0.95 * u);
		hook.quadTo(cx + 0.55 * u, cy - 0.95 * u, cx + 0.46 * u, cy - 0.28 * u);
		hook.quadTo(cx + 0.40 * u, cy + 0.02 * u, cx + 0.02 * u, cy + 0.10 * u);
		hook.lineTo(cx + 0.02 * u, cy + 0.34 * u);
		g.draw(hook);
		g.fill(dot(cx, cy, u, 0.02, 0.74, sw * 0.72));
	}

	/** Person: a filled head over filled shoulders. */
	private static void manual(Graphics2D g, double cx, double cy, double u)
	{
		g.fill(new Ellipse2D.Double(cx - 0.28 * u, cy - 0.78 * u, 0.56 * u, 0.56 * u));
		g.fill(new Arc2D.Double(cx - 0.62 * u, cy - 0.15 * u, 1.24 * u, 1.24 * u, 0, 180, Arc2D.CHORD));
	}

	/** Market graph: an L axis with a rising line and an arrowhead. */
	private static void geTrade(Graphics2D g, double cx, double cy, double u)
	{
		g.draw(poly(cx, cy, u, false, -0.75, -0.7, -0.75, 0.68, 0.8, 0.68));
		g.draw(poly(cx, cy, u, false, -0.55, 0.35, -0.15, -0.05, 0.15, 0.15, 0.62, -0.45));
		g.draw(poly(cx, cy, u, false, 0.35, -0.4, 0.62, -0.45, 0.55, -0.13));
	}

	/** Terrain: a baseline with two hills. */
	private static void ground(Graphics2D g, double cx, double cy, double u)
	{
		g.draw(poly(cx, cy, u, false, -0.85, 0.5, 0.85, 0.5));
		g.draw(poly(cx, cy, u, false, -0.75, 0.5, -0.4, -0.2, -0.05, 0.5));
		g.draw(poly(cx, cy, u, false, 0.0, 0.5, 0.4, 0.02, 0.8, 0.5));
	}

	/** Horizontal double arrow. */
	private static void trade(Graphics2D g, double cx, double cy, double u)
	{
		g.draw(poly(cx, cy, u, false, -0.78, 0, 0.78, 0));
		g.draw(poly(cx, cy, u, false, -0.45, -0.28, -0.78, 0, -0.45, 0.28));
		g.draw(poly(cx, cy, u, false, 0.45, -0.28, 0.78, 0, 0.45, 0.28));
	}

	/** Shopping basket: a tapered body under a handle. */
	private static void shop(Graphics2D g, double cx, double cy, double u)
	{
		g.draw(poly(cx, cy, u, true, -0.55, -0.05, 0.55, -0.05, 0.4, 0.58, -0.4, 0.58));
		g.draw(new Arc2D.Double(cx - 0.34 * u, cy - 0.35 * u, 0.68 * u, 0.68 * u, 25, 130, Arc2D.OPEN));
	}

	/** Alchemy: a flask with a liquid line and a sparkle. */
	private static void alchemy(Graphics2D g, double cx, double cy, double u)
	{
		Path2D.Double flask = new Path2D.Double();
		flask.moveTo(cx - 0.2 * u, cy - 0.7 * u);
		flask.lineTo(cx - 0.2 * u, cy - 0.2 * u);
		flask.lineTo(cx - 0.5 * u, cy + 0.55 * u);
		flask.lineTo(cx + 0.5 * u, cy + 0.55 * u);
		flask.lineTo(cx + 0.2 * u, cy - 0.2 * u);
		flask.lineTo(cx + 0.2 * u, cy - 0.7 * u);
		g.draw(flask);
		g.draw(poly(cx, cy, u, false, -0.26, -0.7, 0.26, -0.7));
		g.draw(poly(cx, cy, u, false, -0.35, 0.2, 0.35, 0.2));
		g.draw(poly(cx, cy, u, false, 0.55, -0.62, 0.55, -0.3));
		g.draw(poly(cx, cy, u, false, 0.39, -0.46, 0.71, -0.46));
	}

	/** Hourglass wrapped by a circling arrow. */
	private static void processing(Graphics2D g, double cx, double cy, double u)
	{
		g.draw(poly(cx, cy, u, true, -0.3, -0.42, 0.3, -0.42, -0.3, 0.42, 0.3, 0.42));

		double r = 0.85 * u;
		g.draw(new Arc2D.Double(cx - r, cy - r, 2 * r, 2 * r, 70, 250, Arc2D.OPEN));

		double a1 = Math.toRadians(320);
		double ex = cx + r * Math.cos(a1);
		double ey = cy - r * Math.sin(a1);
		double dir = Math.atan2(-Math.cos(a1), -Math.sin(a1));
		double ah = 0.34 * u;
		g.draw(new Line2D.Double(ex, ey, ex + ah * Math.cos(dir + Math.toRadians(150)),
				ey + ah * Math.sin(dir + Math.toRadians(150))));
		g.draw(new Line2D.Double(ex, ey, ex + ah * Math.cos(dir - Math.toRadians(150)),
				ey + ah * Math.sin(dir - Math.toRadians(150))));
	}

	/** Gravestone: a rounded-top slab with a cross, on a ground line. */
	private static void death(Graphics2D g, double cx, double cy, double u)
	{
		Path2D.Double stone = new Path2D.Double();
		stone.moveTo(cx - 0.42 * u, cy + 0.55 * u);
		stone.lineTo(cx - 0.42 * u, cy - 0.15 * u);
		stone.quadTo(cx - 0.42 * u, cy - 0.62 * u, cx, cy - 0.62 * u);
		stone.quadTo(cx + 0.42 * u, cy - 0.62 * u, cx + 0.42 * u, cy - 0.15 * u);
		stone.lineTo(cx + 0.42 * u, cy + 0.55 * u);
		g.draw(stone);
		g.draw(poly(cx, cy, u, false, -0.7, 0.55, 0.7, 0.55));
		g.draw(poly(cx, cy, u, false, 0.0, -0.4, 0.0, 0.05));
		g.draw(poly(cx, cy, u, false, -0.2, -0.2, 0.2, -0.2));
	}
}
