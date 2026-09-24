/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.Menu;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GrandExchangeOfferChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Notification;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.AsyncBufferedImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Drives {@link StockpilePlugin}'s event handlers through a real {@code startUp()} against Guice-bound
 * Mockito mocks (#386): game events go in, and the tracked item's quantity and collection log are
 * checked on the way out. {@link ClientThread} runs queued work inline, {@link StockpileConfig}
 * answers with its real defaults, and profile config is an in-memory map, so tracked items are seeded
 * exactly the way a relog loads them.
 */
public class StockpilePluginHandlersTest
{
	private static final int ITEM = 560;

	private static final int SLOT = 0;

	@Mock
	@Bind
	private Client client;

	@Mock
	@Bind
	private ClientThread clientThread;

	@Mock
	@Bind
	private ItemManager itemManager;

	@Mock
	@Bind
	private ClientToolbar clientToolbar;

	@Bind
	private final StockpileConfig config = mock(StockpileConfig.class, Answers.CALLS_REAL_METHODS);

	@Mock
	@Bind
	private ConfigManager configManager;

	@Mock
	@Bind
	private ScheduledExecutorService executor;

	@Mock
	@Bind
	private WikiRealtimePriceClient wikiPriceClient;

	@Mock
	@Bind
	private Notifier notifier;

	@Bind
	private final Gson gson = new Gson();

	@Mock
	@Bind
	private KeyManager keyManager;

	@Mock
	@Bind
	private MouseManager mouseManager;

	@Mock
	@Bind
	private OverlayManager overlayManager;

	@Mock
	@Bind
	private StockpileHighlightOverlay highlightOverlay;

	@Mock
	@Bind
	private StockpileGroundOverlay groundOverlay;

	@Inject
	private StockpilePlugin plugin;

	private final Map<String, String> profileConfig = new HashMap<>();

	private int tick;

	private AutoCloseable mocks;

	@Before
	public void setUp()
	{
		mocks = MockitoAnnotations.openMocks(this);
		Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);

		doAnswer(invocation ->
		{
			invocation.<Runnable>getArgument(0).run();
			return null;
		})
				.when(clientThread)
				.invokeLater(any(Runnable.class));
		doAnswer(invocation ->
		{
			invocation.<BooleanSupplier>getArgument(0).getAsBoolean();
			return null;
		})
				.when(clientThread)
				.invokeLater(any(BooleanSupplier.class));
		doAnswer(invocation ->
		{
			invocation.<Runnable>getArgument(0).run();
			return null;
		})
				.when(clientThread)
				.invoke(any(Runnable.class));

		doAnswer(invocation -> profileConfig.get(invocation.getArgument(0) + "." + invocation.getArgument(1)))
				.when(configManager)
				.getRSProfileConfiguration(anyString(), anyString());
		doAnswer(invocation -> profileConfig.put(invocation.getArgument(0) + "." + invocation.getArgument(1),
				invocation.getArgument(2)))
				.when(configManager)
				.setRSProfileConfiguration(anyString(), anyString(), any());

		when(itemManager.getItemComposition(anyInt())).thenAnswer(invocation -> composition(invocation.getArgument(0)));
		when(itemManager.canonicalize(anyInt())).thenAnswer(invocation -> invocation.getArgument(0));
		when(itemManager.getImage(anyInt())).thenAnswer(invocation -> icon());
		when(itemManager.getImage(anyInt(), anyInt(), anyBoolean())).thenAnswer(invocation -> icon());

		Menu menu = mock(Menu.class);
		when(menu.getMenuEntries()).thenReturn(new MenuEntry[0]);
		when(client.getMenu()).thenReturn(menu);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getTickCount()).thenAnswer(invocation -> tick);
		when(wikiPriceClient.fetchMapping()).thenReturn(Collections.emptyMap());
	}

	@After
	public void tearDown() throws Exception
	{
		mocks.close();
	}

	/** A blank item icon; the panel only needs something to draw. */
	private AsyncBufferedImage icon()
	{
		return new AsyncBufferedImage(clientThread, 36, 32, BufferedImage.TYPE_INT_ARGB);
	}

	/** A plain, tradeable, unnoted item composition named after its id. */
	private static ItemComposition composition(int itemId)
	{
		ItemComposition composition = mock(ItemComposition.class);
		when(composition.getName()).thenReturn("Item " + itemId);
		when(composition.isTradeable()).thenReturn(true);
		when(composition.getNote()).thenReturn(-1);
		when(composition.getPlaceholderTemplateId()).thenReturn(-1);
		when(composition.getLinkedNoteId()).thenReturn(-1);
		return composition;
	}

	/** Persists one tracked item holding {@code lots}, as a previous session would have, then starts the plugin. */
	private TrackedItem startTracking(int quantity, AcquisitionRecord... lots) throws Exception
	{
		return startTracking(true, new ArrayList<>(), quantity, lots);
	}

	/** Persists one tracked item with its cost-basis flag and notification rules, then starts the plugin. */
	private TrackedItem startTracking(boolean costBasisInitialized, List<NotificationRule> rules, int quantity,
			AcquisitionRecord... lots) throws Exception
	{
		StockpilePersistence.PersistedItem persisted = new StockpilePersistence.PersistedItem();
		persisted.itemId = ITEM;
		persisted.quantity = quantity;
		persisted.costBasisInitialized = costBasisInitialized;
		persisted.notificationsInitialized = true;
		persisted.acquisitions = new ArrayList<>(List.of(lots));
		persisted.notifications = rules;
		new StockpilePersistence(configManager, gson).saveItems(List.of(persisted));

		plugin.startUp();
		tick = 100;

		TrackedItem tracked = plugin.trackedItem(ITEM);
		assertNotNull("the persisted item loads on startUp", tracked);
		return tracked;
	}

	private static ItemContainer container(int quantity)
	{
		ItemContainer container = mock(ItemContainer.class);
		Item[] items = quantity > 0 ? new Item[]{new Item(ITEM, quantity)} : new Item[0];
		when(container.getItems()).thenReturn(items);
		return container;
	}

	/** Fires an inventory change holding {@code quantity} of the item. */
	private void inventory(int quantity)
	{
		plugin.onItemContainerChanged(new ItemContainerChanged(InventoryID.INV, container(quantity)));
	}

	/** Advances one client tick, which flushes pending container deltas into the ledger. */
	private void clientTick()
	{
		tick++;
		plugin.onClientTick(new ClientTick());
	}

	private void geOffer(GrandExchangeOfferState state, int total, int sold, int spent)
	{
		GrandExchangeOffer offer = mock(GrandExchangeOffer.class);
		when(offer.getItemId()).thenReturn(ITEM);
		when(offer.getState()).thenReturn(state);
		when(offer.getTotalQuantity()).thenReturn(total);
		when(offer.getQuantitySold()).thenReturn(sold);
		when(offer.getSpent()).thenReturn(spent);
		GrandExchangeOfferChanged event = new GrandExchangeOfferChanged();
		event.setSlot(SLOT);
		event.setOffer(offer);
		plugin.onGrandExchangeOfferChanged(event);
	}

	/** Runs background work inline, as if the executor finished it immediately. */
	private void runExecutorInline()
	{
		doAnswer(invocation ->
		{
			invocation.<Runnable>getArgument(0).run();
			return null;
		})
				.when(executor)
				.execute(any(Runnable.class));
	}

	/** @return the fixed-rate price refresh {@code startUp} scheduled. */
	private Runnable scheduledPriceRefresh()
	{
		ArgumentCaptor<Runnable> task = ArgumentCaptor.forClass(Runnable.class);
		verify(executor).scheduleAtFixedRate(task.capture(), anyLong(), anyLong(), any(TimeUnit.class));
		return task.getValue();
	}

	private static NotificationRule quantityRule(NotificationOperation op, String value, boolean repeat)
	{
		NotificationRule rule = new NotificationRule();
		rule.setMetric(NotificationMetric.QUANTITY);
		rule.setOperation(op);
		rule.setValue(value);
		rule.setRepeat(repeat);
		return rule;
	}

	private static AcquisitionRecord onlyClosed(TrackedItem tracked)
	{
		AcquisitionRecord closed = null;
		for (AcquisitionRecord r : tracked.getAcquisitions())
		{
			if (r.getSoldAt() == null)
				continue;

			assertEquals("exactly one closed lot", null, closed);
			closed = r;
		}

		assertNotNull("a lot closed", closed);
		return closed;
	}

	private static AcquisitionRecord newestOpen(TrackedItem tracked)
	{
		AcquisitionRecord open = null;
		for (AcquisitionRecord r : tracked.getAcquisitions())
			if (r.getSoldAt() == null)
				open = r;

		assertNotNull("an open lot", open);
		return open;
	}

	@Test
	public void aGeSellRealizesAtTheAfterTaxPrice() throws Exception
	{
		TrackedItem tracked = startTracking(10, new AcquisitionRecord(10, 100, null, AcquisitionSource.GATHER));
		inventory(10);

		geOffer(GrandExchangeOfferState.SELLING, 5, 0, 0);
		inventory(5);
		clientTick();
		assertEquals("offered units suspend while the offer is open", 5,
				tracked.getSuspended(SuspensionSource.SELL));

		geOffer(GrandExchangeOfferState.SOLD, 5, 5, 5_000);
		clientTick();

		AcquisitionRecord sold = onlyClosed(tracked);
		assertEquals(5, sold.getQuantity());
		assertEquals("1,000 gp gross less the 2% GE tax (#380)", 980L, (long) sold.getSoldAt());
		assertEquals(AcquisitionSource.GE_TRADE, sold.sellSourceOrUnknown());
		assertEquals(5, tracked.getQuantity());
		assertEquals(0, tracked.getSuspended(SuspensionSource.SELL));
	}

	@Test
	public void aCollectedGeBuyOpensALotAtTheFillPrice() throws Exception
	{
		TrackedItem tracked = startTracking(0);
		inventory(0);

		geOffer(GrandExchangeOfferState.BUYING, 5, 0, 0);
		geOffer(GrandExchangeOfferState.BOUGHT, 5, 5, 600);
		inventory(5);
		clientTick();

		AcquisitionRecord lot = newestOpen(tracked);
		assertEquals(5, lot.getQuantity());
		assertEquals(120, lot.getBoughtAt());
		assertEquals(AcquisitionSource.GE_TRADE, lot.sourceOrUnknown());
		assertEquals(5, tracked.getQuantity());
	}

	@Test
	public void offerEventsInsideTheLoginWindowPrimeInsteadOfReplaying() throws Exception
	{
		GrandExchangeOffer existing = mock(GrandExchangeOffer.class);
		when(existing.getItemId()).thenReturn(ITEM);
		when(existing.getState()).thenReturn(GrandExchangeOfferState.SELLING);
		when(existing.getTotalQuantity()).thenReturn(5);
		when(client.getGrandExchangeOffers()).thenReturn(new GrandExchangeOffer[]{existing});
		TrackedItem tracked = startTracking(5, new AcquisitionRecord(10, 100, null, AcquisitionSource.GATHER));
		tick = 2;

		geOffer(GrandExchangeOfferState.SELLING, 5, 0, 0);
		tick = 100;
		inventory(5);
		inventory(3);
		clientTick();

		assertEquals("the pre-login sell is rebuilt as suspended", 5, tracked.getSuspended(SuspensionSource.SELL));
		assertEquals("a later unrelated removal is not mistaken for that sell's placement", 2,
				onlyClosed(tracked).getQuantity());
	}

	@Test
	public void theFirstSightOfAContainerOnlySeedsABaseline() throws Exception
	{
		TrackedItem tracked = startTracking(0);

		inventory(7);
		clientTick();

		assertEquals("no lots from the login baseline", 0, tracked.getAcquisitions().size());
		assertEquals(0, tracked.getQuantity());
	}

	@Test
	public void quantityFollowsContainersEvenWithAutoRecordOff() throws Exception
	{
		doReturn(false)
				.when(config)
				.autoAddItems();
		TrackedItem tracked = startTracking(4, new AcquisitionRecord(4, 100, null, AcquisitionSource.GATHER));
		inventory(4);

		inventory(9);
		clientTick();

		assertEquals("the displayed quantity moves (#316)", 9, tracked.getQuantity());
		assertEquals("but no lot is recorded", 1, tracked.getAcquisitions().size());
	}

	@Test
	public void theFirstBankSightReconcilesTheLogToTheTrueTotal() throws Exception
	{
		TrackedItem tracked = startTracking(10, new AcquisitionRecord(10, 100, null, AcquisitionSource.GATHER));
		ItemContainer bank = container(12);
		when(client.getItemContainer(InventoryID.BANK)).thenReturn(bank);

		plugin.onItemContainerChanged(new ItemContainerChanged(InventoryID.BANK, bank));

		assertEquals(12, tracked.getQuantity());
		assertEquals("the two unexplained units open an unknown lot", 2, newestOpen(tracked).getQuantity());
		assertEquals(AcquisitionSource.UNKNOWN, newestOpen(tracked).sourceOrUnknown());
	}

	@Test
	public void aCancelledSellReturnsItsUnitsWithoutAPhantomLot() throws Exception
	{
		TrackedItem tracked = startTracking(10, new AcquisitionRecord(10, 100, null, AcquisitionSource.GATHER));
		inventory(10);
		geOffer(GrandExchangeOfferState.SELLING, 5, 0, 0);
		inventory(5);
		clientTick();

		geOffer(GrandExchangeOfferState.CANCELLED_SELL, 5, 0, 0);
		inventory(10);
		clientTick();

		assertEquals(0, tracked.getSuspended(SuspensionSource.SELL));
		assertEquals("the original lot is all there is", 1, tracked.getAcquisitions().size());
		AcquisitionRecord lot = newestOpen(tracked);
		assertEquals(10, lot.getQuantity());
	}

	@Test
	public void aScheduledPriceRefreshAppliesPricesAndSeedsAnUnbasedLog() throws Exception
	{
		runExecutorInline();
		TrackedItem tracked = startTracking(false, new ArrayList<>(), 4);
		long now = System.currentTimeMillis() / 1000L;
		when(wikiPriceClient.fetchAll()).thenReturn(Map.of(ITEM,
				new WikiRealtimePriceClient.ItemPrices(160, 140, now, now)));

		scheduledPriceRefresh().run();

		assertEquals(160, tracked.getHighPrice());
		assertEquals(140, tracked.getLowPrice());
		assertTrue(tracked.isCostBasisInitialized());
		AcquisitionRecord lot = newestOpen(tracked);
		assertEquals("the held units open one lot at the fallback price", 4, lot.getQuantity());
		assertEquals(150, lot.getBoughtAt());
		assertEquals(AcquisitionSource.UNKNOWN, lot.sourceOrUnknown());
	}

	@Test
	public void aOneShotRuleFiresOnceAndIsRemoved() throws Exception
	{
		List<NotificationRule> rules = new ArrayList<>(List.of(quantityRule(NotificationOperation.GTE, "8", false)));
		TrackedItem tracked = startTracking(true, rules, 4, new AcquisitionRecord(4, 100, null,
				AcquisitionSource.GATHER));
		inventory(4);

		inventory(9);
		clientTick();
		inventory(12);
		clientTick();

		verify(notifier, times(1)).notify(any(Notification.class), contains("Item " + ITEM));
		assertTrue("the fired rule is gone", tracked.getNotifications()
				.stream()
				.noneMatch(r -> r.getMetric() == NotificationMetric.QUANTITY));
	}

	@Test
	public void aRepeatingRuleFiresOnlyWhenTheConditionTurnsTrue() throws Exception
	{
		List<NotificationRule> rules = new ArrayList<>(List.of(quantityRule(NotificationOperation.GTE, "8", true)));
		startTracking(true, rules, 4, new AcquisitionRecord(4, 100, null, AcquisitionSource.GATHER));
		inventory(4);

		inventory(5);
		clientTick();
		inventory(9);
		clientTick();
		inventory(10);
		clientTick();
		verify(notifier, times(1)).notify(any(Notification.class), anyString());

		inventory(3);
		clientTick();
		inventory(8);
		clientTick();
		verify(notifier, times(2)).notify(any(Notification.class), anyString());
	}

	/** Runs an import on the plugin and waits for the summary it hands back on the EDT. */
	private String importCode(String code) throws Exception
	{
		CompletableFuture<String> result = new CompletableFuture<>();
		plugin.importTrackedList(code, result::complete);
		return result.get(5, TimeUnit.SECONDS);
	}

	@Test
	public void anImportAddsOnlyNewItemsWithTheirModeAndCategory() throws Exception
	{
		TrackedItem existing = startTracking(4, new AcquisitionRecord(4, 100, null, AcquisitionSource.GATHER));
		when(client.getItemContainer(InventoryID.INV)).thenAnswer(invocation ->
		{
			ItemContainer inv = mock(ItemContainer.class);
			when(inv.getItems()).thenReturn(new Item[]{new Item(562, 3)});
			return inv;
		});
		PortfolioShareCodec.Snapshot snapshot = new PortfolioShareCodec.Snapshot();
		snapshot.setV(1);
		snapshot.setItems(List.of(
				new PortfolioShareCodec.Entry(ITEM, TrackItemMode.VIEW, "Runes", true),
				new PortfolioShareCodec.Entry(561, TrackItemMode.VIEW, "Runes", true),
				new PortfolioShareCodec.Entry(562, TrackItemMode.TRACK, null, false)));
		snapshot.setCategories(List.of(new CategoryState("Runes", false)));

		String message = importCode(new PortfolioShareCodec(gson).encode(snapshot));

		assertEquals("Imported 2 item(s), skipped 1 already tracked.", message);
		assertEquals("an already-tracked item is left untouched", TrackItemMode.TRACK, existing.getMode());
		assertEquals(false, existing.isFavorite());
		TrackedItem watched = plugin.trackedItem(561);
		assertEquals(TrackItemMode.VIEW, watched.getMode());
		assertEquals("Runes", watched.getCategory());
		assertTrue(watched.isFavorite());
		assertEquals("a tracked import counts what is already held", 3, plugin.trackedItem(562).getQuantity());
	}

	@Test
	public void anUnreadableImportCodeChangesNothing() throws Exception
	{
		startTracking(4);

		String message = importCode("not a share code");

		assertTrue(message.startsWith("Couldn't read that code"));
		assertEquals(1, plugin.trackedItems().size());
	}
}
