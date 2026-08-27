/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import net.runelite.client.ui.ColorScheme;

/**
 * The standalone, resizable compare window (#280): a singleton {@link JFrame} wrapping a
 * {@link CompareView} whose columns compare the items in the plugin's shared compare set side by
 * side. The view's label column is pinned as the scroll pane's row header so it stays put while the
 * item columns scroll horizontally. The plugin owns the single instance, drives its item list, keeps
 * its prices fresh each tick, and disposes it on shutdown. All methods run on the Swing EDT.
 */
final class CompareWindow
{
	private static final Dimension DEFAULT_SIZE = new Dimension(560, 640);

	private static final Dimension MIN_SIZE = new Dimension(320, 360);

	private final CompareView view;

	private final JFrame frame;

	/**
	 * Builds and shows the compare window for {@code entries}.
	 *
	 * @param host the seam supplying config, the item manager, rune prices, and the remove/clear actions
	 * @param entries the initial items to compare, in display order
	 * @param onClose fires when the window is disposed, letting the plugin drop its singleton reference
	 */
	CompareWindow(CompareHost host, List<CompareView.Entry> entries, Runnable onClose)
	{
		this.view = new CompareView(host);
		this.frame = buildFrame(host, onClose);
		setEntries(entries);
		frame.setVisible(true);
	}

	/** Wraps the compare view (with a pinned label column) and a Clear-all footer in a disposable frame. */
	private JFrame buildFrame(CompareHost host, Runnable onClose)
	{
		JScrollPane scroll = new JScrollPane(view,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBorder(null);
		scroll.setRowHeaderView(view.rowHeader());
		scroll.getViewport().setBackground(ColorScheme.DARK_GRAY_COLOR);
		scroll.getRowHeader().setBackground(ColorScheme.DARKER_GRAY_COLOR);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		scroll.getHorizontalScrollBar().setUnitIncrement(16);

		JPanel content = new JPanel(new BorderLayout());
		content.setBackground(ColorScheme.DARK_GRAY_COLOR);
		content.add(scroll, BorderLayout.CENTER);
		content.add(buildFooter(host), BorderLayout.SOUTH);

		JFrame f = new JFrame("Stockpile — Compare");
		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.setContentPane(content);
		f.setSize(DEFAULT_SIZE);
		f.setMinimumSize(MIN_SIZE);
		f.setLocationByPlatform(true);
		f.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				onClose.run();
			}
		});

		return f;
	}

	/** Builds the footer strip holding the Clear-all control. */
	private JPanel buildFooter(CompareHost host)
	{
		JPanel footer = new JPanel(new BorderLayout());
		footer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JButton clear = new JButton("Clear all");
		clear.setFocusPainted(false);
		clear.addActionListener(e -> host.clearCompare());
		footer.add(clear, BorderLayout.EAST);

		return footer;
	}

	/**
	 * Re-populates the window with {@code entries} and updates the title count.
	 *
	 * @param entries the items to compare, in display order
	 */
	void setEntries(List<CompareView.Entry> entries)
	{
		view.setEntries(entries);
		frame.setTitle("Stockpile — Compare (" + entries.size() + ")");
	}

	/** Brings the window to the front, restoring it if minimised, so re-adding focuses it. */
	void focus()
	{
		if (frame.getState() == Frame.ICONIFIED)
			frame.setState(Frame.NORMAL);

		frame.toFront();
		frame.requestFocus();
	}

	/** Disposes the window (its close listener drops the plugin's singleton reference). */
	void dispose()
	{
		frame.dispose();
	}
}
