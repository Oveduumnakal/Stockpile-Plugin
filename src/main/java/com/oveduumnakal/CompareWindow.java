/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

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

	/** The time windows offered by the top toggle, from the latest snapshot out to a month. */
	private static final TimeWindow[] WINDOWS =
	{
		TimeWindow.LIVE, TimeWindow.M5, TimeWindow.H1, TimeWindow.H6,
		TimeWindow.H24, TimeWindow.WEEK, TimeWindow.MONTH,
	};

	private final CompareView view;

	private final CompareHost host;

	private final JFrame frame;

	/** A shareable comparison code carries this prefix so an unrelated pasted string is rejected (#303). */
	private static final String CODE_PREFIX = "STOCKPILE-CMP-1:";

	/** The remove-glyph colour on a Load row, brightening to red on hover (#303). */
	private static final Color DELETE_REST = new Color(140, 140, 140);

	/** The remove-glyph hover colour on a Load row (#303). */
	private static final Color DELETE_HOVER = new Color(200, 60, 60);

	/** The Save/Export controls, disabled while the compare set is empty. */
	private JButton saveButton;

	private JButton exportButton;

	/** Names of the persisted comparisons, refreshed by the plugin; drives the Load menu (#303). */
	private List<String> savedNames = new ArrayList<>();

	/** The current compare-set item ids, refreshed by the plugin; backs Export (#303). */
	private List<Integer> currentIds = new ArrayList<>();

	/**
	 * Builds and shows the compare window for {@code entries}.
	 *
	 * @param host the seam supplying config, the item manager, rune prices, and the remove/clear actions
	 * @param entries the initial items to compare, in display order
	 * @param onClose fires when the window is disposed, letting the plugin drop its singleton reference
	 */
	CompareWindow(CompareHost host, List<CompareView.Entry> entries, Runnable onClose)
	{
		this.host = host;
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
		content.add(buildToolbar(), BorderLayout.NORTH);
		content.add(scroll, BorderLayout.CENTER);
		content.add(buildFooter(host), BorderLayout.SOUTH);

		JFrame f = new JFrame("Stockpile — Compare");
		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.setContentPane(content);
		f.setSize(DEFAULT_SIZE);
		f.setMinimumSize(MIN_SIZE);
		f.setLocationByPlatform(true);
		f.setIconImage(ImageUtil.loadImageResource(getClass(), "icon.png"));
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

	/** Builds the top toolbar holding the time-window toggle that drives every column's figures. */
	private JPanel buildToolbar()
	{
		JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
		bar.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JLabel label = new JLabel("Window:");
		label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		bar.add(label);

		JComboBox<TimeWindow> windows = new JComboBox<>(WINDOWS);
		windows.setSelectedItem(view.activeWindow());
		windows.setRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus)
			{
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof TimeWindow)
				{
					TimeWindow w = (TimeWindow) value;
					setText(windowLabel(w));
				}

				return this;
			}
		});
		windows.addActionListener(e -> view.setActiveWindow((TimeWindow) windows.getSelectedItem()));
		bar.add(windows);

		return bar;
	}

	/** @return the toggle label for a window: {@code "Latest"} for the live snapshot, {@code "5m"} for the
	 *          5-minute datapoint, else the window's spelled-out label. */
	private static String windowLabel(TimeWindow window)
	{
		if (window == TimeWindow.LIVE)
			return "Latest";

		if (window == TimeWindow.M5)
			return "5m";

		return window.getLongLabel();
	}

	/** Builds the footer strip: Save/Load saved-comparison controls on the left, Clear-all on the right (#303). */
	private JPanel buildFooter(CompareHost host)
	{
		JPanel footer = new JPanel(new BorderLayout());
		footer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
		left.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		saveButton = new JButton("Save");
		saveButton.setFocusPainted(false);
		saveButton.setToolTipText("Save the current comparison under a name");
		saveButton.addActionListener(e -> promptSave());
		left.add(saveButton);

		JButton load = new JButton("Load ▾");
		load.setFocusPainted(false);
		load.setToolTipText("Load or delete a saved comparison");
		load.addActionListener(e -> showLoadMenu(load));
		left.add(load);

		exportButton = new JButton("Export");
		exportButton.setFocusPainted(false);
		exportButton.setToolTipText("Copy a shareable code for the current comparison to the clipboard");
		exportButton.addActionListener(e -> doExport());
		left.add(exportButton);

		JButton importButton = new JButton("Import");
		importButton.setFocusPainted(false);
		importButton.setToolTipText("Load a comparison from a shared code");
		importButton.addActionListener(e -> promptImport());
		left.add(importButton);

		footer.add(left, BorderLayout.WEST);

		JButton clear = new JButton("Clear all");
		clear.setFocusPainted(false);
		clear.addActionListener(e -> host.clearCompare());
		footer.add(clear, BorderLayout.EAST);

		return footer;
	}

	/** Prompts for a name and saves the current comparison under it (blank/cancelled input is ignored). */
	private void promptSave()
	{
		String input = JOptionPane.showInputDialog(frame, "Name this comparison:", "Save comparison",
				JOptionPane.PLAIN_MESSAGE);
		if (input == null)
			return;

		String name = input.trim();
		if (!name.isEmpty())
			host.saveComparison(name);
	}

	/** Shows the Load menu below {@code anchor}: each row loads its comparison, with a trailing ✕ to delete it. */
	private void showLoadMenu(Component anchor)
	{
		JPopupMenu menu = new JPopupMenu();
		if (savedNames.isEmpty())
		{
			JMenuItem empty = new JMenuItem("(no saved comparisons)");
			empty.setEnabled(false);
			menu.add(empty);
			menu.show(anchor, 0, anchor.getHeight());
			return;
		}

		for (String name : savedNames)
			menu.add(loadMenuRow(menu, name));

		menu.show(anchor, 0, anchor.getHeight());
	}

	/** Builds one Load-menu row: a name (loads on click) with a trailing ✕ that deletes it; both close the menu. */
	private JPanel loadMenuRow(JPopupMenu menu, String name)
	{
		JPanel row = new JPanel(new BorderLayout(8, 0));
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setBorder(new EmptyBorder(3, 8, 3, 6));
		row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JLabel label = new JLabel(name);
		label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);

		JLabel remove = new JLabel("✕");
		remove.setForeground(DELETE_REST);
		remove.setToolTipText("Delete this comparison");
		remove.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		row.add(label, BorderLayout.CENTER);
		row.add(remove, BorderLayout.EAST);

		row.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				row.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
				label.setForeground(Color.WHITE);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
				label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				menu.setVisible(false);
				host.loadComparison(name);
			}
		});

		remove.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				remove.setForeground(DELETE_HOVER);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				remove.setForeground(DELETE_REST);
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				e.consume();
				menu.setVisible(false);
				host.deleteComparison(name);
			}
		});

		return row;
	}

	/** Copies a shareable code for the current comparison to the clipboard (#303). No-op when the set is empty. */
	private void doExport()
	{
		if (currentIds.isEmpty())
			return;

		String code = encodeIds(currentIds);
		Toolkit.getDefaultToolkit()
				.getSystemClipboard()
				.setContents(new StringSelection(code), null);
		JOptionPane.showMessageDialog(frame, "Comparison code copied to the clipboard.", "Export comparison",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/** Prompts for a shared code and loads its items into the compare set (#303); a bad code shows an error. */
	private void promptImport()
	{
		String input = JOptionPane.showInputDialog(frame, "Paste a comparison code:", "Import comparison",
				JOptionPane.PLAIN_MESSAGE);
		if (input == null)
			return;

		List<Integer> ids = decodeIds(input.trim());
		if (ids == null || ids.isEmpty())
		{
			JOptionPane.showMessageDialog(frame, "That is not a valid comparison code.", "Import comparison",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		host.importComparison(ids);
	}

	/** @return a shareable code for {@code ids}: {@link #CODE_PREFIX} plus the Base64 of the comma-joined ids. */
	private static String encodeIds(List<Integer> ids)
	{
		String csv = ids.stream()
				.map(String::valueOf)
				.collect(Collectors.joining(","));
		return CODE_PREFIX + Base64.getEncoder().encodeToString(csv.getBytes());
	}

	/** @return the item ids decoded from {@code code}, or {@code null} when it is not a valid comparison code. */
	private static List<Integer> decodeIds(String code)
	{
		if (code == null || !code.startsWith(CODE_PREFIX))
			return null;

		try
		{
			String csv = new String(Base64.getDecoder().decode(code.substring(CODE_PREFIX.length())));
			List<Integer> ids = new ArrayList<>();
			for (String part : csv.split(","))
			{
				String trimmed = part.trim();
				if (!trimmed.isEmpty())
					ids.add(Integer.parseInt(trimmed));
			}

			return ids;
		}
		catch (IllegalArgumentException e)
		{
			return null;
		}
	}

	/**
	 * Updates the saved-comparison names backing the Load menu (#303). Called by the plugin on the EDT
	 * whenever the persisted set changes.
	 *
	 * @param names the current saved-comparison names, in saved order
	 */
	void setSavedNames(List<String> names)
	{
		this.savedNames = new ArrayList<>(names);
	}

	/**
	 * Updates the current compare-set ids backing Export (#303). Called by the plugin on the EDT whenever the
	 * compare set changes.
	 *
	 * @param ids the current compare-set item ids, in display order
	 */
	void setCurrentIds(List<Integer> ids)
	{
		this.currentIds = new ArrayList<>(ids);
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
		if (saveButton != null)
			saveButton.setEnabled(!entries.isEmpty());

		if (exportButton != null)
			exportButton.setEnabled(!entries.isEmpty());
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
