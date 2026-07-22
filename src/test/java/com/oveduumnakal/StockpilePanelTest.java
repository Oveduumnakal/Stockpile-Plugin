/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.junit.Test;

import sun.misc.Unsafe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Verifies session-row behavior that depends on live, not cache-hydrated, prices. */
public class StockpilePanelTest
{
	@Test
	public void cacheHydratedItemsHideSessionRowAndDoNotCaptureBaseline() throws Exception
	{
		StockpilePanel panel = panel();

		updateSessionLine(panel, Collections.singletonList(item(560, 100, 125, false)), true);

		assertFalse(sessionRow(panel).isVisible());
		assertFalse(sessionStats(panel).hasBaseline());
	}

	@Test
	public void livePricesAfterCacheHydrationSeedBaselineWithoutOvernightDelta() throws Exception
	{
		StockpilePanel panel = panel();
		TrackedItem item = item(560, 100, 125, false);

		updateSessionLine(panel, Collections.singletonList(item), true);
		item.setAvgPrice(130);
		item.setPriceCacheHydrated(false);
		updateSessionLine(panel, Collections.singletonList(item), true);

		assertTrue(sessionRow(panel).isVisible());
		assertTrue(sessionStats(panel).hasBaseline());
		assertEquals("0", sessionValue(panel).getText());
	}

	@Test
	public void laterHydratedItemGoingLiveIsAbsorbedNeutrally() throws Exception
	{
		StockpilePanel panel = panel();
		TrackedItem earlyLive = item(560, 10, 100, true);
		TrackedItem laterHydrated = item(4151, 1, 1_000, false);

		updateSessionLine(panel, Collections.singletonList(earlyLive), true);
		earlyLive.setAvgPrice(110);
		updateSessionLine(panel, Arrays.asList(earlyLive, laterHydrated), true);
		assertEquals("+100", sessionValue(panel).getText());

		laterHydrated.setPriceCacheHydrated(false);
		updateSessionLine(panel, Arrays.asList(earlyLive, laterHydrated), true);

		assertTrue(sessionRow(panel).isVisible());
		assertEquals("+100", sessionValue(panel).getText());
	}

	private StockpilePanel panel()
			throws Exception
	{
		StockpilePanel panel = (StockpilePanel) unsafe().allocateInstance(StockpilePanel.class);
		setField(panel, "config", new StockpileConfig() {});
		setField(panel, "sessionRow", new JPanel());
		setField(panel, "sessionLabel", new JLabel("Session:"));
		setField(panel, "sessionValueLabel", new JLabel());
		setField(panel, "sessionStats", new SessionStats());
		return panel;
	}

	private TrackedItem item(int id, int quantity, long avgPrice, boolean live)
	{
		TrackedItem item = new TrackedItem(id, "Item " + id);
		item.setQuantity(quantity);
		item.setAvgPrice(avgPrice);
		item.setLowPrice(avgPrice);
		item.setHighPrice(avgPrice);
		item.setPriceCacheHydrated(!live);
		return item;
	}

	private void updateSessionLine(StockpilePanel panel, List<TrackedItem> items, boolean hasPrices) throws Exception
	{
		Method method = StockpilePanel.class.getDeclaredMethod("updateSessionLine", List.class, boolean.class);
		method.setAccessible(true);
		method.invoke(panel, items, hasPrices);
	}

	private JPanel sessionRow(StockpilePanel panel) throws Exception
	{
		return field(panel, "sessionRow", JPanel.class);
	}

	private JLabel sessionValue(StockpilePanel panel) throws Exception
	{
		return field(panel, "sessionValueLabel", JLabel.class);
	}

	private SessionStats sessionStats(StockpilePanel panel) throws Exception
	{
		return field(panel, "sessionStats", SessionStats.class);
	}

	private <T> T field(StockpilePanel panel, String name, Class<T> type) throws Exception
	{
		Field field = StockpilePanel.class.getDeclaredField(name);
		field.setAccessible(true);
		return type.cast(field.get(panel));
	}

	private void setField(StockpilePanel panel, String name, Object value) throws Exception
	{
		Field field = StockpilePanel.class.getDeclaredField(name);
		field.setAccessible(true);
		field.set(panel, value);
	}

	private Unsafe unsafe() throws Exception
	{
		Field field = Unsafe.class.getDeclaredField("theUnsafe");
		field.setAccessible(true);
		return (Unsafe) field.get(null);
	}
}
