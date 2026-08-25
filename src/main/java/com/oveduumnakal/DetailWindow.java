/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import net.runelite.client.ui.ColorScheme;

/**
 * A standalone, resizable window hosting the full item detail view for one item (#109). Wraps a
 * dedicated {@link DetailView} built in {@link DetailView.Layout#DASHBOARD} (two-column) mode inside
 * its own {@link JFrame}, so an item's stats, charts and collection log can stay open and
 * live-updating independently of the sidebar &mdash; and several items can be popped out at once.
 * The plugin owns the window registry, keeps the bound item's prices fresh, and disposes every
 * window on shutdown. All methods run on the Swing EDT.
 */
final class DetailWindow
{
	private static final Dimension DEFAULT_SIZE = new Dimension(760, 720);
	private static final Dimension MIN_SIZE = new Dimension(480, 400);

	private int itemId;
	private final DetailView view;
	private final JFrame frame;
	private TrackedItem boundItem;
	private boolean preview;

	/**
	 * Builds and shows the window for {@code item}. The host is supplied by {@code hostFactory} so it
	 * can capture this window &mdash; its Back/close disposes the window and its tracked-item lookup
	 * returns this window's own bound instance rather than reading the plugin's live map off the EDT.
	 * {@code onClose} fires with the item id when the window is disposed, letting the plugin drop it
	 * from the registry.
	 */
	DetailWindow(Function<DetailWindow, DetailViewHost> hostFactory, TrackedItem item, boolean preview,
			Consumer<Integer> onClose)
	{
		this.itemId = item.getItemId();
		this.boundItem = item;
		this.preview = preview;
		this.view = new DetailView(hostFactory.apply(this), DetailView.Layout.DASHBOARD);
		this.frame = buildFrame("Stockpile — " + item.getName(), onClose);

		if (preview)
			view.showPreview(item);
		else
			view.show(itemId);

		frame.setVisible(true);
	}

	/**
	 * Builds and shows the item-less "dashboard home" window (#109): a full dashboard whose header stands in
	 * for an item (Stockpile icon and name) and whose body prompts the user to search for an item. Picking an
	 * item from the search bar rebinds this window to that item's detail. The registry keys it under id 0.
	 */
	DetailWindow(Function<DetailWindow, DetailViewHost> hostFactory, Consumer<Integer> onClose)
	{
		this.itemId = 0;
		this.boundItem = null;
		this.preview = false;
		this.view = new DetailView(hostFactory.apply(this), DetailView.Layout.DASHBOARD);
		this.frame = buildFrame("Stockpile — Dashboard", onClose);

		view.showDashboardHome();
		frame.setVisible(true);
	}

	/** Wraps the view in a scroll pane and a disposable {@link JFrame} titled {@code title}. */
	private JFrame buildFrame(String title, Consumer<Integer> onClose)
	{
		JScrollPane scroll = new JScrollPane(view,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBorder(null);
		scroll.getViewport().setBackground(ColorScheme.DARK_GRAY_COLOR);
		scroll.getVerticalScrollBar().setUnitIncrement(16);

		JFrame f = new JFrame(title);
		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.setContentPane(scroll);
		f.setSize(DEFAULT_SIZE);
		f.setMinimumSize(MIN_SIZE);
		f.setLocationByPlatform(true);
		f.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				view.onLeaveDetail();
				onClose.accept(itemId);
			}
		});

		return f;
	}

	/** @return the item id this window is bound to. */
	int itemId()
	{
		return itemId;
	}

	/** @return whether this window currently shows a read-only preview rather than a tracked item. */
	boolean isPreview()
	{
		return preview;
	}

	/** @return the item instance backing this window (tracked or preview). */
	TrackedItem boundItem()
	{
		return boundItem;
	}

	/** Re-populates the window with fresh data for its item, keeping the scroll position. */
	void refreshData()
	{
		view.refreshDetailData(itemId);
	}

	/** Live-updates the Market Info last-bought / last-sold relative times. */
	void updateMarketInfoTimes()
	{
		view.updateMarketInfoTimes();
	}

	/**
	 * Rebinds the window to a different item picked from the dashboard search bar (#109): re-titles the
	 * frame and re-shows the view as the tracked item, or as a read-only preview when {@code preview}.
	 * The plugin re-keys its window registry to {@code newItemId} around this call.
	 */
	void rebind(int newItemId, TrackedItem item, boolean preview)
	{
		this.itemId = newItemId;
		this.boundItem = item;
		this.preview = preview;
		frame.setTitle("Stockpile — " + item.getName());
		if (preview)
			view.showPreview(item);
		else
			view.show(newItemId);
	}

	/** Transitions the window to show {@code tracked} after its preview was tracked from the header (#138). */
	void syncTracked(TrackedItem tracked)
	{
		this.boundItem = tracked;
		this.preview = false;
		view.onRebuild();
	}

	/** Transitions the window to a read-only preview after its item was untracked from the header (#138). */
	void showAsPreview(TrackedItem previewItem)
	{
		this.boundItem = previewItem;
		this.preview = true;
		view.showPreview(previewItem);
	}

	/** Brings the window to the front, restoring it if minimised, so re-popping focuses it. */
	void focus()
	{
		if (frame.getState() == Frame.ICONIFIED)
			frame.setState(Frame.NORMAL);

		frame.toFront();
		frame.requestFocus();
	}

	/** Disposes the window (its close listener drops it from the plugin registry). */
	void dispose()
	{
		frame.dispose();
	}
}
