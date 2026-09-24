/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;

import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link GeIntegration} (#393) against a mocked GE offer interface: which item the offer
 * screen shows, auto-opening it in Stockpile, the market price line that replaces the native
 * "Actively traded price" (fresh series first, then a background 5m/1h fetch, then the latest
 * prices), and the injected Track/Untrack button - created once per interface load and reused across
 * item changes (#324).
 */
public class GeIntegrationTest
{
	private static final int WHIP = 4151;

	private static final int SHARK = 385;

	private static final int PLACEHOLDER = 6512;

	private static final String NATIVE_DESC = "A whip.<br>Actively traded price: 1,500,000 coins";

	private final Client client = mock(Client.class);

	private final ItemManager itemManager = mock(ItemManager.class);

	private final StockpileConfig config = mock(StockpileConfig.class, Answers.CALLS_REAL_METHODS);

	private final GeIntegrationHost host = mock(GeIntegrationHost.class);

	private final GeIntegration ge = new GeIntegration(client, itemManager, config, host);

	/** Every widget the integration created, in creation order. */
	private final List<Widget> created = new ArrayList<>();

	/** Background fetches the host was asked to run, held so a test decides when they finish. */
	private final List<Runnable> background = new ArrayList<>();

	private Widget root;

	private Widget setup;

	private Widget desc;

	/** The GE description's current text; the game rewrites it to the native line every tick (#288). */
	private final String[] descText = new String[1];

	@Before
	public void setUp()
	{
		when(itemManager.canonicalize(anyInt())).thenAnswer(invocation -> invocation.getArgument(0));
		when(itemManager.getItemComposition(anyInt())).thenAnswer(invocation ->
		{
			int id = invocation.getArgument(0);
			ItemComposition composition = mock(ItemComposition.class);
			when(composition.getName()).thenReturn(id == PLACEHOLDER ? "null" : "Item " + id);
			return composition;
		});
		doAnswer(invocation ->
		{
			invocation.<Runnable>getArgument(0).run();
			return null;
		})
				.when(host)
				.runOnClientThread(any(Runnable.class));
		doAnswer(invocation -> background.add(invocation.getArgument(0)))
				.when(host)
				.runInBackground(any(Runnable.class));

		root = widget();
		Widget close = widget();
		when(close.getActions()).thenReturn(new String[]{null, "Close"});
		when(close.getParent()).thenReturn(root);
		when(close.getOriginalX()).thenReturn(5);
		when(close.getWidth()).thenReturn(26);
		setup = widget();
		when(setup.getParent()).thenReturn(root);
		when(root.getStaticChildren()).thenReturn(new Widget[]{setup, close});
		desc = widget();
		descText[0] = NATIVE_DESC;
		when(desc.getText()).thenAnswer(invocation -> descText[0]);
		doAnswer(invocation ->
		{
			descText[0] = invocation.getArgument(0);
			return desc;
		})
				.when(desc)
				.setText(anyString());
		when(client.getWidget(InterfaceID.GeOffers.SETUP)).thenReturn(setup);
		when(client.getWidget(InterfaceID.GeOffers.SETUP_DESC)).thenReturn(desc);
		showing(WHIP);
	}

	/** A widget that records every child created under it. */
	private Widget widget()
	{
		Widget widget = mock(Widget.class);
		when(widget.createChild(anyInt(), anyInt())).thenAnswer(invocation ->
		{
			Widget child = widget();
			created.add(child);
			return child;
		});
		return widget;
	}

	/** Puts {@code itemId} on the offer setup screen (or an empty slot for -1). */
	private void showing(int itemId)
	{
		Widget slot = widget();
		when(slot.getItemId()).thenReturn(itemId);
		when(setup.getDynamicChildren()).thenReturn(new Widget[]{slot});
	}

	private void mode(GeIntegrationMode mode, boolean prices, boolean track)
	{
		doReturn(mode)
				.when(config)
				.geIntegration();
		doReturn(prices)
				.when(config)
				.geShowMarketPrices();
		doReturn(track)
				.when(config)
				.geShowTrackButton();
	}

	private static List<WikiRealtimePriceClient.PricePoint> series(long high, long low)
	{
		return List.of(new WikiRealtimePriceClient.PricePoint(1, high, low, 0, 0));
	}

	/** @return the text written to the GE description, checking it was rewritten exactly once. */
	private String lastDesc()
	{
		ArgumentCaptor<String> text = ArgumentCaptor.forClass(String.class);
		verify(desc, times(1)).setText(text.capture());
		return text.getValue();
	}

	@Test
	public void theOfferScreenItemSkipsPlaceholderSlots()
	{
		Widget placeholder = widget();
		when(placeholder.getItemId()).thenReturn(PLACEHOLDER);
		Widget real = widget();
		when(real.getItemId()).thenReturn(SHARK);
		when(placeholder.getNestedChildren()).thenReturn(new Widget[]{null, real});
		when(setup.getDynamicChildren()).thenReturn(new Widget[]{placeholder});

		assertEquals(SHARK, ge.currentGeOfferItem());
	}

	@Test
	public void theDetailsScreenIsReadWhenSetupIsHidden()
	{
		when(setup.isHidden()).thenReturn(true);
		Widget details = widget();
		Widget slot = widget();
		when(slot.getItemId()).thenReturn(SHARK);
		when(details.getStaticChildren()).thenReturn(new Widget[]{slot});
		when(client.getWidget(InterfaceID.GeOffers.DETAILS)).thenReturn(details);

		assertEquals(SHARK, ge.currentGeOfferItem());

		when(details.isHidden()).thenReturn(true);
		assertEquals("no offer screen open", -1, ge.currentGeOfferItem());
	}

	@Test
	public void autoOpenPreviewsANewItemOnceAndFocusesThePanel()
	{
		mode(GeIntegrationMode.AUTO, false, false);

		ge.onGameTick();
		ge.onGameTick();

		verify(host, times(1)).previewItem(WHIP);
		verify(host, times(1)).focusPanel();
		verify(host, never()).openTrackedDetail(anyInt());
	}

	@Test
	public void autoOpenShowsATrackedItemsDetailInstead()
	{
		mode(GeIntegrationMode.BOTH, false, false);
		doReturn(false)
				.when(config)
				.geFocusPanel();
		when(host.isTracked(WHIP)).thenReturn(true);

		ge.onGameTick();

		verify(host).openTrackedDetail(WHIP);
		verify(host, never()).focusPanel();
	}

	@Test
	public void withEverythingOffTheInterfaceIsNotTouched()
	{
		mode(GeIntegrationMode.OFF, false, false);

		ge.onGameTick();

		verify(client, never()).getWidget(anyInt());
		assertTrue(created.isEmpty());
	}

	@Test
	public void theMarketLineUsesAFreshSeriesWithoutFetching()
	{
		mode(GeIntegrationMode.OFF, true, false);
		when(host.freshSeries(WHIP, SeriesTimestep.FIVE_MIN)).thenReturn(series(1_600_000, 1_550_000));

		ge.onGameTick();

		String text = lastDesc();
		assertTrue(text, text.startsWith("A whip.<br>5m High: <col=64dc64>1,600,000 gp</col>"));
		assertTrue(text, text.endsWith("Low: <col=dc6464>1,550,000 gp</col>"));
		assertFalse("the native line is replaced", text.contains("Actively traded price"));
		assertTrue(background.isEmpty());
	}

	@Test
	public void theMarketLineFallsBackToTheHourSeries()
	{
		mode(GeIntegrationMode.OFF, true, false);
		when(host.fetchSeries(WHIP, "1h")).thenReturn(series(1_700_000, 0));

		ge.onGameTick();
		background.get(0).run();

		String text = lastDesc();
		assertTrue(text, text.contains("1h High: <col=64dc64>1,700,000 gp</col>"));
		assertTrue("an unpriced side shows a dash", text.contains("Low: <col=969696>—</col>"));
	}

	@Test
	public void theMarketLineFallsBackToTheLatestPrices()
	{
		mode(GeIntegrationMode.OFF, true, false);
		when(host.latestPrices(WHIP)).thenReturn(new long[]{900, 800});

		ge.onGameTick();
		background.get(0).run();

		assertTrue(lastDesc().contains("Latest High: <col=64dc64>900 gp</col>"));
	}

	@Test
	public void withNoPricesAnywhereTheNativeLineStays()
	{
		mode(GeIntegrationMode.OFF, true, false);

		ge.onGameTick();
		background.get(0).run();

		verify(desc, never()).setText(anyString());
	}

	@Test
	public void aLateFetchForAnItemNoLongerShownIsDropped()
	{
		mode(GeIntegrationMode.OFF, true, false);
		when(host.fetchSeries(eq(WHIP), anyString())).thenReturn(series(1_600_000, 1_550_000));
		when(host.fetchSeries(eq(SHARK), anyString())).thenReturn(series(900, 850));
		ge.onGameTick();
		showing(SHARK);
		ge.onGameTick();

		background.get(1).run();
		background.get(0).run();
		descText[0] = NATIVE_DESC;
		ge.onGameTick();

		assertTrue("the shark's line survives the whip's late answer", descText[0].contains("900 gp"));
		assertFalse(descText[0].contains("1,600,000"));
	}

	@Test
	public void theTrackButtonIsInjectedOnceAndRelabelledPerItem()
	{
		mode(GeIntegrationMode.OFF, false, true);
		when(host.isTracked(SHARK)).thenReturn(true);

		ge.onGameTick();
		assertEquals("a border box and a label beside Close", 2, created.size());
		Widget label = created.get(1);
		verify(label).setText("Track");

		showing(SHARK);
		ge.onGameTick();

		assertEquals("reused, not re-injected (#324)", 2, created.size());
		verify(label).setText("Untrack");
	}

	@Test
	public void clickingTrackTracksTheItemOnScreen()
	{
		mode(GeIntegrationMode.OFF, false, true);
		ge.onGameTick();
		Widget label = created.get(1);
		ArgumentCaptor<Object> listener = ArgumentCaptor.forClass(Object.class);
		verify(label).setOnOpListener(listener.capture());

		((JavaScriptCallback) listener.getValue()).run(null);

		verify(host).trackItem(WHIP);
	}

	@Test
	public void theStockpileButtonHidesWhenNoItemIsShownAndGoesWhenSwitchedOff()
	{
		mode(GeIntegrationMode.BUTTON, false, false);
		ge.onGameTick();
		Widget button = created.get(0);
		verify(button).setAction(0, "View in Stockpile");

		showing(-1);
		when(button.isHidden()).thenReturn(false);
		ge.onGameTick();
		verify(button).setHidden(true);

		mode(GeIntegrationMode.OFF, false, false);
		ge.onGameTick();
		verify(button, times(2)).setHidden(true);
	}
}
