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

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.FontID;
import net.runelite.api.GameState;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Skill;
import net.runelite.api.SpritePixels;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GrandExchangeOfferChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemQuantityChanged;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Notification;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.item.ItemPrice;

@Slf4j
/**
 * Plugin entry point: wires up the side panel and overlays and drives all
 * tracking logic.
 *
 * <p>Responsibilities: persisting and restoring the tracked-item set; counting
 * each item across the watched inventory/bank containers (and the rune pouch);
 * polling the wiki for live prices, metadata, and history; maintaining each
 * item's cost-basis lots (FIFO acquire/close) for profit; and evaluating
 * user-defined notification rules. It subscribes to the relevant game events and
 * marshals UI work onto the Swing thread and network work onto a background
 * executor.
 */
@PluginDescriptor(
		name = "Stockpile",
		description = "Track item quantities across your inventory and bank with live GE prices",
		tags = {"items", "bank", "inventory", "price", "ge", "tracker"}
)
public class StockpilePlugin extends Plugin implements LedgerHost
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private StockpileConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private WikiRealtimePriceClient wikiPriceClient;

	@Inject
	private Notifier notifier;

	@Inject
	private Gson gson;

	/** Client-free persistence layer (#111); built in {@link #startUp()} once gson/config are injected. */
	private StockpilePersistence persistence;

	@Inject
	private KeyManager keyManager;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private StockpileHighlightOverlay highlightOverlay;

	@Inject
	private StockpileGroundOverlay groundOverlay;

	/** Maximum number of items shown in the on-screen overlay (fixed for now). */
	static final int OVERLAY_MAX = 5;

	/** One independently-draggable overlay box per slot; they start grouped in the same snap corner. */
	private final List<StockpileScreenOverlay> screenOverlays = new ArrayList<>();

	private static final int[] RUNE_POUCH_TYPE_VARBITS = {
			VarbitID.RUNE_POUCH_TYPE_1, VarbitID.RUNE_POUCH_TYPE_2, VarbitID.RUNE_POUCH_TYPE_3,
			VarbitID.RUNE_POUCH_TYPE_4, VarbitID.RUNE_POUCH_TYPE_5, VarbitID.RUNE_POUCH_TYPE_6
	};
	private static final int[] RUNE_POUCH_QUANTITY_VARBITS = {
			VarbitID.RUNE_POUCH_QUANTITY_1, VarbitID.RUNE_POUCH_QUANTITY_2, VarbitID.RUNE_POUCH_QUANTITY_3,
			VarbitID.RUNE_POUCH_QUANTITY_4, VarbitID.RUNE_POUCH_QUANTITY_5, VarbitID.RUNE_POUCH_QUANTITY_6
	};
	private static final ImmutableSet<Integer> RUNE_POUCH_VARBITS;
	static
	{
		ImmutableSet.Builder<Integer> b = ImmutableSet.builder();
		for (int v : RUNE_POUCH_TYPE_VARBITS)
			b.add(v);

		for (int v : RUNE_POUCH_QUANTITY_VARBITS)
			b.add(v);

		RUNE_POUCH_VARBITS = b.build();
	}

	private static final ImmutableSet<Integer> TRACKED_CONTAINERS = ImmutableSet.of(
			InventoryID.INV,
			InventoryID.WORN,
			InventoryID.BANK,
			InventoryID.LOOTING_BAG,
			InventoryID.SEED_BOX,
			InventoryID.SEED_VAULT,
			InventoryID.TACKLE_BOX,
			InventoryID.FORESTRY_KIT,
			InventoryID.HUNTSMANS_KIT,
			InventoryID.BARBARIAN_KNAPSACK,
			InventoryID.SAILING_BOAT_1_CARGOHOLD,
			InventoryID.SAILING_BOAT_2_CARGOHOLD,
			InventoryID.SAILING_BOAT_3_CARGOHOLD,
			InventoryID.SAILING_BOAT_4_CARGOHOLD,
			InventoryID.SAILING_BOAT_5_CARGOHOLD
	);

	/**
	 * Reward/loot containers that hand out free loot into the inventory (#215). These are not
	 * tracked as holdings — they are transient interfaces — but an inventory gain while one is
	 * open marks that gain as a zero-cost {@link AcquisitionSource#REWARD} rather than Unknown.
	 * Point-spending reward shops are deliberately excluded (their withdrawals are purchases,
	 * not free loot). The object-search rewards that loot straight to the inventory with no
	 * reward container are handled elsewhere: the Huntsman's loot sack via a menu hook
	 * ({@link #LOOT_SACK_OPTION}) and the Tempoross reward pool via a chat hook
	 * ({@link #REWARD_LOOT_PREFIX}).
	 */
	private static final ImmutableSet<Integer> REWARD_CONTAINERS = ImmutableSet.of(
			InventoryID.TRAWLER_REWARDINV,
			InventoryID.TRAIL_REWARDINV,
			InventoryID.RAIDS_REWARDS,
			InventoryID.TOB_CHESTS,
			InventoryID.TOA_CHESTS,
			InventoryID.COLOSSEUM_REWARDS,
			InventoryID.PMOON_REWARDINV,
			InventoryID.DOM_LOOTPILE
	);

	/**
	 * Menu option and target substring for the Huntsman's loot sack, whose contents land in
	 * the inventory with no reward {@link ItemContainer} to observe. Live capture confirmed the
	 * loot arrives on the same tick as the "Open" click, so stamping {@link #rewardContainerTick}
	 * here lets {@link #correlateReward()} claim it within the existing window (#215). The Tempoross
	 * reward pool and GOTR reward guardian remain deferred pending their own live capture.
	 */
	private static final String LOOT_SACK_OPTION = "Open";
	private static final String LOOT_SACK_TARGET = "loot sack";

	/**
	 * Chat-line prefix for the generic "loot to inventory" reward message ("You found some loot:
	 * N x Item"). The Tempoross reward pool (Net/Big-search) drops loot straight into the inventory
	 * with no reward {@link ItemContainer} and its object-search click lands three ticks before the
	 * loot; live capture (#215) confirmed this SPAM line fires on the same tick as the inventory
	 * gains, so stamping {@link #rewardContainerTick} here lets {@link #correlateReward()} claim the
	 * whole multi-item drop within the existing window. Other activities that surface reward loot
	 * through the same message (e.g. the GOTR reward guardian) are covered by the same hook.
	 */
	private static final String REWARD_LOOT_PREFIX = "You found some loot:";

	private final Map<Integer, TrackedItem> trackedItems = new LinkedHashMap<>();

	/** Ordered user-defined categories (names + collapsed state); the source of truth for category order. */
	private final List<CategoryState> categories = new ArrayList<>();
	private boolean favoritesCollapsed;
	private boolean uncategorizedCollapsed;

	/**
	 * Transient, non-persisted item backing the read-only detail preview (view-only
	 * button); not in {@link #trackedItems}.
	 */
	private TrackedItem previewItem;

	/**
	 * Open pop-out detail windows keyed by item id (#109). EDT-only: created, focused, refreshed and
	 * disposed on the Swing thread. Its client-thread counterpart is {@link #windowItems}, which holds
	 * the bound instances so pricing/lookup can reach them without touching Swing state off the EDT.
	 */
	private final Map<Integer, DetailWindow> detailWindows = new HashMap<>();

	/**
	 * The bound item instance backing each open pop-out window, keyed by item id (#109). Client-thread
	 * state, mutated alongside the price maps so {@link #lookupItem}, {@link #applyGePrices} and the
	 * per-detail request loop keep every popped-out item (tracked or preview) live.
	 */
	private final Map<Integer, TrackedItem> windowItems = new HashMap<>();

	/** Latest nature/fire rune prices, cached for the pop-out windows' alch figures ({@link #requestDetailData}). */
	private volatile long lastNatureRunePrice;
	private volatile long lastFireRunePrice;

	/** The most items the compare view holds at once (#280); adding past this is a no-op with a chat notice. */
	private static final int COMPARE_CAP = 6;

	/**
	 * The shared compare set (#280): canonical item ids compared side by side, in insertion order.
	 * Client-thread state (mutated alongside the price maps), transient (never persisted).
	 */
	private final Set<Integer> compareIds = new LinkedHashSet<>();

	/**
	 * Read-only preview instances backing untracked items in the compare set (#280), keyed by item id.
	 * Client-thread state, the compare-set analogue of {@link #windowItems} so {@link #lookupItem} and the
	 * per-tick request loop keep untracked compared items live.
	 */
	private final Map<Integer, TrackedItem> compareItems = new HashMap<>();

	/** The singleton compare window (#280), or {@code null} when none is open. EDT-only, like the detail windows. */
	private CompareWindow compareWindow;

	/**
	 * The persisted named comparisons (#303), in saved order. Client-thread state, loaded at startup and
	 * written back through {@link #persistence} on every save/delete.
	 */
	private final List<StockpilePersistence.SavedComparison> savedComparisons = new ArrayList<>();

	/** The item shown on the currently-open GE offer screen, or -1 when no offer screen is up (GE integration). */
	private int currentGeItem = -1;
	/** The native-style button injected onto the GE offer screen in Button mode, or null. */
	private Widget geButton;
	/** The Track/Untrack button's beige chrome (a BUTTON_BROWN graphic) beside GE History, or null (#139). */
	private Widget geTrackButton;
	/** The dark text label riding on {@link #geTrackButton}; carries the Track/Untrack text (#139). */
	private Widget geTrackLabel;
	/** The raw GE item id the cached 5m high/low belong to, or -1 when unfetched or stale (#142). */
	private int geLineItem = -1;
	/** High and low market prices for {@link #geLineItem} from the resolved source; 0 when unavailable (#142). */
	private long geLineHigh;
	private long geLineLow;
	/** Which source {@link #geLineHigh}/{@link #geLineLow} came from, as a row-label prefix (5m/1h/Latest) (#142). */
	private String geLineSource;
	/** Height the GE info-block text widget is grown to so its fourth row is not self-clipped (#142). */
	private static final int GE_DESC_HEIGHT = 80;
	/** Muted GE-title orange for the Track button's outline box (#139). */
	private static final int GE_TRACK_BORDER = 0xcc7d1a;
	/** The GE offer title/heading orange used for the Track button text, same for both states (#139). */
	private static final int GE_TITLE_ORANGE = 0xff981f;
	/** Custom sprite-override id for the Stockpile icon shown on the GE "View in Stockpile" button (#140). */
	private static final int STOCKPILE_GE_SPRITE_ID = -21140;
	/** Rendered size, in pixels, of the Stockpile icon on the GE button (#140). */
	private static final int GE_ICON_SIZE = 25;
	/** The native "Actively traded price" segment of the GE info block, rewritten every tick (#288). */
	private static final Pattern GE_ACTIVE_PRICE_LINE =
		Pattern.compile("(?: / |<br>)?Actively traded price:[^<]*");

	private final Map<Integer, Map<Integer, Integer>> containerCounts = new HashMap<>();

	private final Map<Integer, Integer> runePouchCounts = new HashMap<>();

	private final Set<Integer> seenContainersSinceLogin = new HashSet<>();

	private boolean runePouchSeenSinceLogin = false;

	/**
	 * Set when a rune pouch varbit changes; the diff is deferred to {@link #onClientTick} so every
	 * type/quantity varbit for the change has settled before it is read (#237).
	 */
	private boolean runePouchDirty = false;

	/**
	 * Whether the current logged-in session has been initialised. Guards the one-time
	 * clear+reload so a respawn or region load re-firing {@code LOGGED_IN} mid-session
	 * doesn't wipe pending quantity changes (e.g. a death loss) or reset held state (#70).
	 * Set by whichever path initialises the session: the {@code LOGGED_IN} handler, or
	 * {@code startUp} when the plugin is enabled while already logged in — the two do the
	 * same load, so leaving the flag false there let the next region crossing re-clear.
	 */
	private boolean sessionInitialized = false;
	private int geLoginTick = -1;

	/** Whether an NPC shop interface is open, gating the coin-delta shop pricing (#67). */
	private boolean shopOpen = false;

	/** The partner-side trade container: the offer container id with the "other player" bit set. */
	private static final int TRADE_OTHER_CONTAINER = InventoryID.TRADEOFFER | 0x8000;

	/** Latest captured trade-offer sides (canonical id → qty), read when the trade completes (#66). */
	private final Map<Integer, Integer> myTradeOffer = new HashMap<>();
	private final Map<Integer, Integer> theirTradeOffer = new HashMap<>();

	/** Skills whose XP drops identify a processing action for the basis-transfer pairing (#69). */
	private static final Set<Skill> PROCESSING_SKILLS = ImmutableSet.of(
			Skill.COOKING, Skill.SMITHING, Skill.CRAFTING, Skill.FLETCHING, Skill.HERBLORE, Skill.MAGIC,
			Skill.RUNECRAFT);

	/** Skills whose XP drops mark an inventory gain as gathered from the world at 0 cost (#213). */
	private static final Set<Skill> GATHERING_SKILLS = ImmutableSet.of(
			Skill.HUNTER, Skill.MINING, Skill.FISHING, Skill.WOODCUTTING, Skill.FARMING);

	/**
	 * Item categories whose members are used up in a single action — food eaten, a last potion
	 * dose drunk — so an unclaimed removal of one closes at 0 under
	 * {@link AcquisitionSource#CONSUMED} (its cost realizes as a loss) rather than an avg-price
	 * Unknown "sale" (#218). Keyed by {@link ItemCategoryClassifier} category names.
	 * <p>
	 * Two categories are deliberately excluded because they need their own attribution rather than
	 * this generic branch. Ammo splits into destroyed-on-use (a genuine 0-gp loss) and recoverable
	 * ammo that lands on the target's tile and belongs on the ground-suspension path (#234). Runes
	 * are burned by a spellcast and book to a dedicated Cast source; they also never reach here
	 * today, since a Magic XP tick lets {@code correlateProcessing} claim them first (#235).
	 */
	private static final Set<String> CONSUMABLE_CATEGORIES = ImmutableSet.of("Food", "Potions");

	/** {@link ItemCategoryClassifier} category holding the runes a spellcast burns (#235). */
	private static final String RUNE_CATEGORY = "Runes";

	/** {@link ItemCategoryClassifier} category holding recoverable ranged ammo — arrows, bolts, darts, … (#234). */
	private static final String AMMO_CATEGORY = "Ammo";

	/**
	 * Name tokens marking ammo destroyed on use — a cannonball fired from a cannon, a chinchompa thrown —
	 * matched by name rather than category, since chinchompas classify as Hunter and a few cannonballs as
	 * Weapons. Such a removal closes at 0 under {@link AcquisitionSource#DESTROYED} (#234).
	 */
	private static final Set<String> DESTROYED_AMMO_TOKENS = ImmutableSet.of("cannonball", "chinchompa");

	/**
	 * Name tokens for thrown melee weapons that survive the throw and land recoverable like arrows —
	 * knives and throwing axes classify as Weapons rather than the {@link #AMMO_CATEGORY}, so they need a
	 * name match to reach the ground-suspension path (#234). A non-thrown "knife" tool never fires and only
	 * ever leaves the inventory by a drop (handled earlier) or a claimed trade, so the loose token is safe.
	 */
	private static final Set<String> RECOVERABLE_WEAPON_TOKENS = ImmutableSet.of("knife", "thrownaxe", "throwing axe");

	/**
	 * Empty vessels left behind when a potion or drink/dish is finished — a free byproduct of the
	 * consumption, booked at 0 rather than an avg-price Unknown purchase (#218). Claimed only on a
	 * terminal-consumption tick, bounded to the number of vessels emptied.
	 */
	private static final Set<Integer> EMPTY_CONTAINERS = ImmutableSet.of(
			ItemID.VIAL_EMPTY, ItemID.JUG_EMPTY, ItemID.BOWL_EMPTY, ItemID.BUCKET_EMPTY, ItemID.BEER_GLASS);

	/** Menu option that stores held furs/meats into an open hunting pouch (#214). */
	private static final String POUCH_FILL_OPTION = "Fill";

	/** Menu option that discards a potion's liquid, leaving an empty vial — booked as a 0-gp drop (#232). */
	private static final String POTION_EMPTY_OPTION = "Empty";

	/** Substrings identifying a fur/meat hunting pouch as the "Fill" menu target, across sizes (#214). */
	private static final Set<String> POUCH_TARGETS = ImmutableSet.of("fur pouch", "meat pouch");

	/**
	 * Chat lines emitted when a hunting pouch is emptied to the bank — the per-pouch
	 * "Empty" deposit (SPAM) and the bank's "Empty containers" button (GAMEMESSAGE).
	 * Neither the pouch container nor a varbit changes, so these are the only signal (#214).
	 */
	private static final String POUCH_DEPOSIT_PREFIX = "You deposit some ";
	private static final String POUCH_DEPOSIT_SUFFIX = " into your bank.";
	private static final String EMPTY_CONTAINERS_MESSAGE = "You empty all of your containers into the bank.";

	/** Per-skill XP as last seen, so a StatChanged can be classified as a real XP gain. */
	private final Map<Skill, Integer> lastSkillXp = new EnumMap<>(Skill.class);

	/** The tick of the most recent processing-skill XP gain, pairing recipe inputs to outputs. */
	private int processingXpTick = -1;

	/**
	 * The tick of the most recent Magic XP gain, marking removed runes as burned by a spellcast (#235).
	 * Tracked separately from {@link #processingXpTick} because Magic is also a processing skill: runes
	 * consumed on a Magic tick are the cast's fuel, but runes consumed on a Runecraft tick (earth runes
	 * into lava runes) are a genuine recipe input whose basis belongs on the product.
	 */
	private int magicXpTick = -1;

	/** The tick of the most recent gathering-skill XP gain, marking a gain as a free gather (#213). */
	private int gatherXpTick = -1;

	/** The tick a reward/loot container last changed, marking a matching inventory gain as a free reward (#215). */
	private int rewardContainerTick = -1;

	/** The tick of the most recent Thieving XP gain, marking a gain as free stolen loot (#217). */
	private int thievingXpTick = -1;

	/** Ids claimed by this tick's dose-swap pass, so the XP-less combine detector skips a decant/consume (#231). */
	private final Set<Integer> doseSwapClaimedIds = new HashSet<>();

	private boolean pendingQuantitySync = false;
	private final Map<Integer, Integer> pendingItemDeltas = new HashMap<>();

	private final Map<TileItem, Tile> groundItems = new HashMap<>();

	/** This tick's ground spawns/despawns/stack changes, correlated against the inventory deltas (#65). */
	private final List<ItemSpawned> tickGroundSpawns = new ArrayList<>();
	private final List<ItemDespawned> tickGroundDespawns = new ArrayList<>();
	private final List<ItemQuantityChanged> tickGroundQuantityChanges = new ArrayList<>();

	/** Ground items this player dropped: the {@code TileItem} → how many of its units are ours. */
	private final Map<TileItem, Integer> myDrops = new HashMap<>();

	/**
	 * True while the configured Context Menu Key is held, gating the right-click Stockpile section (#285). Driven by
	 * {@link #contextMenuKeyListener}, and &mdash; for a modifier keybind &mdash; re-synced from the actual mouse
	 * modifier state on every press by {@link #contextMenuMouseListener}, so a missed key release cannot latch
	 * it (#292).
	 */
	private volatile boolean contextKeyHeld;

	/** The key code that set {@link #contextKeyHeld}, so its release clears the flag even if the bind changes. */
	private int contextHeldKeyCode = KeyEvent.VK_UNDEFINED;

	/** Tracks the Context Menu Key so {@link #onMenuOpened} can offer the section; (un)registered in start/shutdown. */
	private final KeyListener contextMenuKeyListener = new KeyListener()
	{
		@Override
		public boolean isEnabledOnLoginScreen()
		{
			return false;
		}

		@Override
		public void keyTyped(KeyEvent e)
		{
		}

		@Override
		public void keyPressed(KeyEvent e)
		{
			if (config.contextMenuKey().matches(e))
			{
				contextKeyHeld = true;
				contextHeldKeyCode = e.getKeyCode();
			}
		}

		@Override
		public void keyReleased(KeyEvent e)
		{
			if (contextKeyHeld && e.getKeyCode() == contextHeldKeyCode)
				contextKeyHeld = false;
		}

		@Override
		public void focusLost()
		{
			contextKeyHeld = false;
		}
	};

	/**
	 * Re-syncs {@link #contextKeyHeld} from the real modifier state carried on each mouse press, so the right-click
	 * gate can never stay latched after a missed key release &mdash; e.g. with the bank open, where the canvas keeps
	 * focus and {@code focusLost()} never fires (#292). Only meaningful for a modifier-based Context Menu Key (the
	 * default, Shift); a non-modifier keybind is not reflected in mouse modifiers, so its state is left to the
	 * {@link #contextMenuKeyListener}. The event is always returned unchanged.
	 */
	private final MouseListener contextMenuMouseListener = new MouseListener()
	{
		@Override
		public MouseEvent mousePressed(MouseEvent e)
		{
			reconcileContextKeyFromMouse(e);
			return e;
		}

		@Override
		public MouseEvent mouseClicked(MouseEvent e)
		{
			return e;
		}

		@Override
		public MouseEvent mouseReleased(MouseEvent e)
		{
			return e;
		}

		@Override
		public MouseEvent mouseEntered(MouseEvent e)
		{
			return e;
		}

		@Override
		public MouseEvent mouseExited(MouseEvent e)
		{
			return e;
		}

		@Override
		public MouseEvent mouseDragged(MouseEvent e)
		{
			return e;
		}

		@Override
		public MouseEvent mouseMoved(MouseEvent e)
		{
			return e;
		}
	};

	/**
	 * Sets {@link #contextKeyHeld} from a mouse event's live modifier state when the Context Menu Key is a modifier
	 * (or modifier combo); leaves the flag untouched for a non-modifier keybind, which mouse events cannot report.
	 *
	 * @param e the mouse event whose modifier state to read
	 */
	private void reconcileContextKeyFromMouse(MouseEvent e)
	{
		final Keybind bind = config.contextMenuKey();
		final Integer keyMask = Keybind.getModifierForKeyCode(bind.getKeyCode());
		if (keyMask == null)
			return;

		final int required = keyMask | bind.getModifiers();
		contextKeyHeld = (e.getModifiersEx() & required) == required;
	}

	private StockpilePanel panel;
	private NavigationButton navButton;
	private ScheduledFuture<?> priceRefreshTask;
	private Instant lastPriceRefresh = null;

	/** The newest un-rendered panel snapshot; non-null means a rebuild drainer is already queued. */
	private final AtomicReference<Runnable> pendingRebuild = new AtomicReference<>();

	private static final int NATURE_RUNE_ID = 561;
	private static final int FIRE_RUNE_ID = 554;

	private volatile Map<Integer, WikiRealtimePriceClient.ItemMapping> itemMappings = Collections.emptyMap();

	private volatile boolean mappingsLoaded;

	/** The cost-basis / GE trade ledger (#255); this plugin is its {@link LedgerHost} seam. */
	private CostBasisLedger ledger;

	/**
	 * Ticks after login during which {@code GrandExchangeOfferChanged} events are treated as the
	 * login offer sync (pre-existing offers) rather than user actions. The client delivers the GE
	 * offers with the login packet within a tick or two, while the player cannot open the GE and
	 * abort an offer anywhere near this fast — so the window reliably separates the two.
	 */
	private static final int GE_LOGIN_SYNC_TICKS = 5;

	/**
	 * Grace window (in ticks) after login during which an empty→full rune pouch read is treated
	 * as baseline hydration rather than a real acquisition. Pouch type/quantity varbits can arrive
	 * across a couple of ticks as the login packet settles; a player cannot fill a pouch this fast,
	 * so suppressing the delta here avoids the phantom login acquisition (#237).
	 */
	private static final int RUNE_POUCH_LOGIN_GRACE_TICKS = 2;

	/** Per-item thinned time series of portfolio value/cost for the history chart. */
	private final PortfolioHistory portfolioHistory = new PortfolioHistory();

	/** How often at most the portfolio history is rewritten to config. */
	private static final Duration PORTFOLIO_SAVE_INTERVAL = Duration.ofMinutes(5);

	private Instant lastPortfolioSave;

	/**
	 * Records a portfolio snapshot into the history (persisting throttled): the running
	 * value — owned units (held plus suspended) marked to the current average plus sold
	 * lots at their actual sale price — against the invested cost basis of every logged
	 * lot, which stays fixed as lots sell. Their gap is thus the realized-plus-unrealized
	 * profit. Suspended units must count: their lots are still open on the cost side, so
	 * omitting their value would carve a false loss into the chart for the duration of
	 * every in-flight sell, trade, drop, or death.
	 */
	private void recordPortfolioSnapshot()
	{
		Map<Integer, long[]> perItem = new HashMap<>();
		for (TrackedItem item : trackedItems.values())
		{
			if (!item.hasPrices())
				continue;

			long value = item.getAvgValue() + item.getSuspendedValue() + item.getRealizedProceeds();
			long cost = item.isCostBasisInitialized() ? item.getInvestedCostBasis() : 0;
			perItem.put(item.getItemId(), new long[]{value, cost});
		}

		if (perItem.isEmpty())
			return;

		portfolioHistory.record(Instant.now().getEpochSecond(), perItem);

		if (lastPortfolioSave == null
				|| Duration.between(lastPortfolioSave, Instant.now()).compareTo(PORTFOLIO_SAVE_INTERVAL) >= 0)
			persistPortfolioHistory();
	}

	/** Monotonic submit counter that coalesces overlapping async portfolio saves so only the newest snapshot wins. */
	private final AtomicLong portfolioSaveSeq = new AtomicLong();

	/**
	 * Persists the per-item portfolio history to per-profile config (#184). The snapshot
	 * ({@link PortfolioHistory#seriesByItem()}, a deep copy) is taken here on the client thread, but the
	 * gson serialization (~48k numbers at 50 items) and the config write are handed to the shared
	 * executor so they don't stall the game thread each save interval or on every item removal. A submit
	 * sequence guard drops a snapshot already superseded by a newer submission, so out-of-order pool
	 * execution can't write stale history.
	 */
	private void persistPortfolioHistory()
	{
		lastPortfolioSave = Instant.now();
		Map<Integer, List<long[]>> snapshot = portfolioHistory.seriesByItem();
		long seq = portfolioSaveSeq.incrementAndGet();
		executor.execute(() ->
		{
			if (seq == portfolioSaveSeq.get())
				persistence.savePortfolioHistory(snapshot);
		});
	}

	/** Serializes the portfolio history synchronously (shutdown only), when the executor may not run queued tasks. */
	private void persistPortfolioHistorySync()
	{
		lastPortfolioSave = Instant.now();
		portfolioSaveSeq.incrementAndGet();
		persistence.savePortfolioHistory(portfolioHistory.seriesByItem());
	}

	/**
	 * Restores the per-item portfolio history from per-profile config, ignoring a corrupt
	 * value. The pre-#152 aggregate format (a JSON array rather than an object) can't be
	 * split per item, so it is discarded — history simply rebuilds from the next snapshot.
	 */
	private void loadPortfolioHistory()
	{
		Map<Integer, List<long[]>> stored = persistence.loadPortfolioHistory();
		if (stored != null)
			portfolioHistory.load(stored);
	}

	/** @return the aggregated portfolio history points ({@code {epochSeconds, value, costBasis}}) for the chart. */
	List<long[]> portfolioHistoryPoints()
	{
		return portfolioHistory.aggregate();
	}

	/** How long after first launching a new release the "What's New" indicator stays highlighted. */
	private static final Duration WHATS_NEW_WINDOW = Duration.ofDays(7);

	/** Bundled release notes, parsed once at startup; the newest entry is the current version. */
	private Changelog changelog;

	/**
	 * Detects a new plugin version by comparing the changelog's current version to the
	 * last-seen version in config. On a change, restamps the first-seen time and re-arms
	 * the "What's New" indicator so late updaters still get their week.
	 */
	private void detectVersionChange()
	{
		String current = changelog.currentVersion();
		if (current == null)
			return;

		String lastSeen = configManager.getConfiguration(StockpileConfig.GROUP, StockpileConfig.KEY_LAST_SEEN_VERSION);
		if (current.equals(lastSeen))
			return;

		configManager.setConfiguration(StockpileConfig.GROUP, StockpileConfig.KEY_LAST_SEEN_VERSION, current);
		configManager.setConfiguration(StockpileConfig.GROUP, StockpileConfig.KEY_VERSION_FIRST_SEEN,
				System.currentTimeMillis());
		configManager.setConfiguration(StockpileConfig.GROUP, StockpileConfig.KEY_WHATS_NEW_DISMISSED, false);
	}

	/**
	 * One-time migration for #219: the old combined {@code autoAddItems} enum
	 * (High/Low/Avg/Zero/Off) split into a boolean auto-add gate plus a separate
	 * {@link FallbackPricing}. Rewrites a legacy enum name still stored under
	 * {@link StockpileConfig#KEY_AUTO_ADD_ITEMS} as the boolean gate — Off becomes off,
	 * every pricing value becomes on — and seeds {@link StockpileConfig#KEY_FALLBACK_PRICING}
	 * from its pricing half (Off, which conflated the two and couldn't carry a pricing
	 * choice, defaults to Avg). Idempotent: a value already migrated to a boolean, or a
	 * fresh install with no value, is left untouched.
	 */
	private void migrateAutoAddSetting()
	{
		String legacy = configManager.getConfiguration(StockpileConfig.GROUP, StockpileConfig.KEY_AUTO_ADD_ITEMS);
		FallbackPricing pricing = FallbackPricing.fromLegacyMode(legacy);
		if (pricing == null)
			return;

		boolean autoAdd = !legacy.equals("OFF");
		configManager.setConfiguration(StockpileConfig.GROUP, StockpileConfig.KEY_AUTO_ADD_ITEMS, autoAdd);
		configManager.setConfiguration(StockpileConfig.GROUP, StockpileConfig.KEY_FALLBACK_PRICING, pricing);
	}

	/** @return whether the indicator should read "What's New" — within a week of first launch and not dismissed. */
	private boolean isWhatsNew()
	{
		if (changelog.currentVersion() == null)
			return false;

		Boolean dismissed = configManager.getConfiguration(StockpileConfig.GROUP,
				StockpileConfig.KEY_WHATS_NEW_DISMISSED, Boolean.class);
		if (Boolean.TRUE.equals(dismissed))
			return false;

		Long firstSeen = configManager.getConfiguration(StockpileConfig.GROUP,
				StockpileConfig.KEY_VERSION_FIRST_SEEN, Long.class);
		if (firstSeen == null)
			return true;

		return System.currentTimeMillis() - firstSeen < WHATS_NEW_WINDOW.toMillis();
	}

	/** Persists that the user has seen the current release's "What's New", quieting the indicator. */
	private void markWhatsNewSeen()
	{
		configManager.setConfiguration(StockpileConfig.GROUP, StockpileConfig.KEY_WHATS_NEW_DISMISSED, true);
	}

	/**
	 * Builds the side panel (wiring its callbacks back to this plugin), registers
	 * the nav button and overlays, restores persisted items, and kicks off the
	 * metadata fetch and recurring price refresh.
	 */
	@Override
	protected void startUp() throws Exception
	{
		persistence = new StockpilePersistence(configManager, gson);
		ledger = new CostBasisLedger(this, persistence);
		changelog = Changelog.load();
		detectVersionChange();
		migrateAutoAddSetting();

		panel = new StockpilePanel(
				itemManager,
				config,
				new CategoryActions()
				{
					@Override
					public void setItemCategory(int itemId, String category)
					{
						StockpilePlugin.this.setItemCategory(itemId, category);
					}

					@Override
					public void create(String name)
					{
						createCategory(name);
					}

					@Override
					public void rename(String oldName, String newName)
					{
						renameCategory(oldName, newName);
					}

					@Override
					public void delete(String name)
					{
						deleteCategory(name);
					}

					@Override
					public void reorder(String name, int targetIndex)
					{
						reorderCategory(name, targetIndex);
					}

					@Override
					public String autoCategorize(boolean includeCategorized)
					{
						return StockpilePlugin.this.autoCategorize(includeCategorized);
					}
				},
				new PanelActions()
				{
					@Override
					public void addItem(int itemId, TrackItemMode mode)
					{
						addTrackedItem(itemId, mode);
					}

					@Override
					public void removeItem(int itemId)
					{
						removeTrackedItem(itemId);
					}

					@Override
					public void untrackToPreview(int itemId)
					{
						StockpilePlugin.this.untrackToPreview(itemId);
					}

					@Override
					public void popOut(int itemId)
					{
						popOutDetail(itemId);
					}

					@Override
					public void addToCompare(int itemId)
					{
						clientThread.invokeLater(() -> StockpilePlugin.this.addToCompare(itemId));
					}

					@Override
					public void addVariantsToCompare(int itemId)
					{
						clientThread.invokeLater(() -> StockpilePlugin.this.addVariantsToCompare(itemId));
					}

					@Override
					public void openDashboard()
					{
						openDashboardWindow();
					}

					@Override
					public void openCompare()
					{
						clientThread.invokeLater(StockpilePlugin.this::openCompareWindow);
					}

					@Override
					public void acquisitionsEdited(int itemId)
					{
						onAcquisitionsEdited(itemId);
					}

					@Override
					public void requestDetailData(int itemId)
					{
						StockpilePlugin.this.requestDetailData(itemId);
					}

					@Override
					public void clearAcquisitions(int itemId)
					{
						StockpilePlugin.this.clearAcquisitions(itemId);
					}

					@Override
					public void notificationsEdited(int itemId)
					{
						onNotificationsEdited(itemId);
					}

					@Override
					public void clearAll()
					{
						clearAllTrackedItems();
					}

					@Override
					public String examineLookup(int itemId)
					{
						return examineFor(itemId);
					}

					@Override
					public void reorder(int from, int to)
					{
						reorderTrackedItem(from, to);
					}

					@Override
					public void setGlobalOrder(List<Integer> order)
					{
						StockpilePlugin.this.setGlobalOrder(order);
					}

					@Override
					public void toggleCompactView()
					{
						StockpilePlugin.this.toggleCompactView();
					}

					@Override
					public void setSortMode(SortMode mode)
					{
						StockpilePlugin.this.setSortMode(mode);
					}

					@Override
					public void toggleSortDirection()
					{
						toggleSortReversed();
					}

					@Override
					public void setFavorite(int itemId, boolean favorite)
					{
						StockpilePlugin.this.setFavorite(itemId, favorite);
					}

					@Override
					public void setOnOverlay(int itemId, boolean onOverlay)
					{
						StockpilePlugin.this.setOnOverlay(itemId, onOverlay);
					}

					@Override
					public void setItemCompact(int itemId, boolean compact)
					{
						StockpilePlugin.this.setItemCompact(itemId, compact);
					}

					@Override
					public void setGroupCollapsed(String group, boolean collapsed)
					{
						StockpilePlugin.this.setGroupCollapsed(group, collapsed);
					}

					@Override
					public void exportList(Consumer<String> callback)
					{
						buildShareToken(callback);
					}

					@Override
					public void importList(String data, Consumer<String> callback)
					{
						importTrackedList(data, callback);
					}

					@Override
					public void exportCsv(Consumer<String> callback)
					{
						buildAcquisitionsCsv(callback);
					}

					@Override
					public List<long[]> portfolioHistory()
					{
						return portfolioHistoryPoints();
					}

					@Override
					public int portfolioPointCount()
					{
						return portfolioHistory.pointCount();
					}

					@Override
					public void whatsNewSeen()
					{
						markWhatsNewSeen();
					}
				},
				changelog,
				isWhatsNew()
		);

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");

		navButton = NavigationButton.builder()
				.tooltip("Stockpile")
				.icon(icon)
				.priority(6)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);
		keyManager.registerKeyListener(contextMenuKeyListener);
		mouseManager.registerMouseListener(contextMenuMouseListener);
		clientThread.invokeLater(() -> registerGeButtonSprite(icon));
		overlayManager.add(highlightOverlay);
		overlayManager.add(groundOverlay);
		for (int slot = 0; slot < OVERLAY_MAX; slot++)
		{
			StockpileScreenOverlay overlay = new StockpileScreenOverlay(this, config, itemManager, slot);
			screenOverlays.add(overlay);
			overlayManager.add(overlay);
		}

		clientThread.invokeLater(() ->
		{
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				sessionInitialized = true;
				geLoginTick = client.getTickCount();
			}

			loadCategories();
			loadPersistedItems();
			loadSavedComparisons();
			ledger.load();
			loadPortfolioHistory();

			refreshPanel();
			clientThread.invokeLater(this::hydratePriceCache);
		});
		executor.execute(this::fetchItemMappings);
		scheduleRefresh();
	}

	/**
	 * @return the examine text for the given item id from the wiki mapping, or
	 *         {@code null} when the item isn't GE-tradeable or the mapping hasn't
	 *         loaded yet
	 */
	private String examineFor(int itemId)
	{
		WikiRealtimePriceClient.ItemMapping mapping = itemMappings.get(itemId);
		return mapping == null ? null : mapping.getExamine();
	}

	/** Fetches GE item metadata in the background, keeping the previous map on failure. */
	private void fetchItemMappings()
	{
		Map<Integer, WikiRealtimePriceClient.ItemMapping> mappings = wikiPriceClient.fetchMapping();
		if (mappings.isEmpty())
			return;

		itemMappings = mappings;
		mappingsLoaded = true;

		clientThread.invokeLater(this::resolveTradeabilityForAll);
	}

	/**
	 * Applies wiki metadata (tradeability, buy limit, GE value, high/low alch) to every
	 * tracked item and the preview item now that the wiki mapping is available, then
	 * refreshes the panel. Folding {@link #applyItemMetadata(TrackedItem)} into this sweep
	 * (rather than waiting for each item's price-series fetch) means alch values are cached
	 * for all items as soon as the mapping loads (#238). Items absent from the mapping are
	 * not on the Grand Exchange, so they are marked non-tradeable and any stale price-load
	 * failure is cleared.
	 */
	private void resolveTradeabilityForAll()
	{
		trackedItems.values().forEach(this::applyItemMetadata);

		if (previewItem != null)
			applyItemMetadata(previewItem);

		refreshPanel();
	}

	/**
	 * Narrows an item's tradeable flag using the wiki mapping: an item that the game
	 * composition reports as tradeable but which is absent from the Grand Exchange
	 * mapping (e.g. coins, burnt food) is reclassified as non-tradeable so it shows
	 * "Item not tradeable" rather than a price-load failure. No-op until the mapping
	 * has loaded, so a slow fetch never mislabels a genuinely tradeable item.
	 */
	private void resolveTradeable(TrackedItem item)
	{
		item.setStackable(itemManager.getItemComposition(item.getItemId()).isStackable());

		if (!mappingsLoaded)
			return;

		if (item.isTradeable() && !itemMappings.containsKey(item.getItemId()))
		{
			item.setTradeable(false);
			item.setPriceLoadFailed(false);
		}
	}

	/** Tears down the nav button, overlays, panel, and refresh task and clears all in-memory state. */
	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
		keyManager.unregisterKeyListener(contextMenuKeyListener);
		mouseManager.unregisterMouseListener(contextMenuMouseListener);
		overlayManager.remove(highlightOverlay);
		overlayManager.remove(groundOverlay);
		screenOverlays.forEach(overlayManager::remove);
		screenOverlays.clear();
		SwingUtilities.invokeLater(this::closeAllDetailWindows);
		SwingUtilities.invokeLater(this::closeCompareWindow);
		windowItems.clear();
		compareIds.clear();
		compareItems.clear();
		panel.shutdown();
		closeAllGroundSuspensions();
		groundItems.clear();
		tickGroundSpawns.clear();
		tickGroundDespawns.clear();
		tickGroundQuantityChanges.clear();
		clientThread.invokeLater(this::hideGeButton);
		clientThread.invokeLater(this::unregisterGeButtonSprite);
		currentGeItem = -1;
		persistPriceCache();
		if (priceRefreshTask != null)
		{
			priceRefreshTask.cancel(false);
			priceRefreshTask = null;
		}

		ledger.persist();
		persistPortfolioHistorySync();
		trackedItems.clear();
		containerCounts.clear();
		runePouchCounts.clear();
		ledger.resetForShutdown();
		lastPriceRefresh = null;
	}

	@Provides
	StockpileConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StockpileConfig.class);
	}

	/** (Re)schedules the recurring GE price refresh at the configured rate (min 30s), replacing any prior task. */
	private void scheduleRefresh()
	{
		if (priceRefreshTask != null)
			priceRefreshTask.cancel(false);

		int rate = Math.max(30, config.priceRefreshSeconds());
		priceRefreshTask = executor.scheduleAtFixedRate(
				this::refreshGePricesGuarded, 0, rate, TimeUnit.SECONDS
		);
	}

	/**
	 * Runs one scheduled price refresh, swallowing anything it throws.
	 *
	 * <p>An exception escaping a {@code scheduleAtFixedRate} task cancels that task permanently, so a
	 * single bad response would stop price refreshes for the rest of the session with nothing but a
	 * swallowed future to show for it. Logging and continuing means the next tick simply tries again.
	 */
	private void refreshGePricesGuarded()
	{
		try
		{
			refreshGePrices();
		}
		catch (RuntimeException e)
		{
			log.warn("Scheduled price refresh failed; retrying on the next tick", e);
		}
	}

	/** How often at most the price cache is rewritten to config during regular refreshes. */
	private static final Duration PRICE_CACHE_SAVE_INTERVAL = Duration.ofMinutes(5);

	/** When the price cache was last written, to throttle per-refresh saves. */
	private Instant lastPriceCacheSave;

	/** Restores tracked items from the per-profile JSON written by {@link #persistTrackedItems()}. */
	private void loadPersistedItems()
	{
		for (StockpilePersistence.PersistedItem p : persistence.loadItems())
		{
			addTrackedItem(p.itemId, p.quantity, p.acquisitions, p.notifications,
				p.notificationsInitialized, p.costBasisInitialized, false, false, TrackItemMode.TRACK);
			applyPersistedGrouping(p.itemId, p.favorite, p.category, p.onOverlay, p.compact);
			applyPersistedDeathSuspension(p.itemId, p.deathSuspendedQuantity, p.deathSuspendedAt);
			applyPersistedPouchSuspension(p.itemId, p.pouchSuspendedQuantity);
		}
	}

	/**
	 * Applies a persisted item's favorite/category/overlay/compact grouping after it has been added.
	 * Enqueued on the client thread so it runs after the matching {@link #addTrackedItem}
	 * body (which is itself client-thread-deferred), guaranteeing the item exists.
	 */
	private void applyPersistedGrouping(int itemId, boolean favorite, String category, boolean onOverlay,
			boolean compact)
	{
		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null)
				return;

			tracked.setFavorite(favorite);
			tracked.setCategory(category);
			tracked.setOnOverlay(onOverlay);
			tracked.setCompact(compact);
		});
	}

	/**
	 * Restores a persisted death suspension after its item has been added, so a
	 * recovery spanning a relog still un-suspends instead of opening new lots.
	 * Client-thread-deferred like {@link #applyPersistedGrouping}.
	 */
	private void applyPersistedDeathSuspension(int itemId, int quantity, Long suspendedAtEpoch)
	{
		if (quantity <= 0)
			return;

		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null)
				return;

			tracked.restoreSuspended(SuspensionSource.DEATH, quantity, suspendedAtEpoch == null
					? Instant.now()
					: Instant.ofEpochSecond(suspendedAtEpoch));
		});
	}

	/**
	 * Restores a persisted fur/meat-pouch suspension after its item has been added, so
	 * furs "Fill"ed in before a logout still un-suspend (keeping their original source
	 * and basis) when the pouch is emptied in a later session (#214).
	 * Client-thread-deferred like {@link #applyPersistedGrouping}.
	 */
	private void applyPersistedPouchSuspension(int itemId, int quantity)
	{
		if (quantity <= 0)
			return;

		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null)
				return;

			tracked.restoreSuspended(SuspensionSource.POUCH, quantity, null);
		});
	}

	/** Restores the category definitions and group collapsed state from per-profile JSON. */
	private void loadCategories()
	{
		categories.clear();
		favoritesCollapsed = false;
		uncategorizedCollapsed = false;

		StockpilePersistence.CategoryData data = persistence.loadCategories();
		if (data == null)
			return;

		if (data.categories != null)
			data.categories.stream()
					.filter(c -> c != null && c.getName() != null && !c.getName().trim().isEmpty())
					.forEach(categories::add);

		favoritesCollapsed = data.favoritesCollapsed;
		uncategorizedCollapsed = data.uncategorizedCollapsed;
	}

	/** Serializes the category definitions and group collapsed state to per-profile config. */
	private void persistCategories()
	{
		StockpilePersistence.CategoryData data = new StockpilePersistence.CategoryData();
		data.categories = new ArrayList<>(categories);
		data.favoritesCollapsed = favoritesCollapsed;
		data.uncategorizedCollapsed = uncategorizedCollapsed;
		persistence.saveCategories(data);
	}

	/** Serializes the current tracked items (quantity, cost basis, notifications, grouping) to per-profile config. */
	@Override
	public void persistTrackedItems()
	{
		List<StockpilePersistence.PersistedItem> list = new ArrayList<>();
		for (TrackedItem item : trackedItems.values())
		{
			StockpilePersistence.PersistedItem p = new StockpilePersistence.PersistedItem();
			p.itemId = item.getItemId();
			p.quantity = item.getQuantity();
			p.costBasisInitialized = item.isCostBasisInitialized();
			p.acquisitions = item.getAcquisitions();
			p.notifications = item.getNotifications();
			p.notificationsInitialized = item.isNotificationsInitialized();
			p.favorite = item.isFavorite();
			p.category = item.getCategory();
			p.onOverlay = item.isOnOverlay();
			p.compact = item.isCompact();
			p.deathSuspendedQuantity = item.getSuspended(SuspensionSource.DEATH);
			p.deathSuspendedAt = item.getSuspendedAt(SuspensionSource.DEATH) == null
					? null
					: item.getSuspendedAt(SuspensionSource.DEATH).getEpochSecond();
			p.pouchSuspendedQuantity = item.getSuspended(SuspensionSource.POUCH);
			list.add(p);
		}

		persistence.saveItems(list);
	}

	/** Tracks an item by id with defaults (full tracking mode, no preset cost basis). */
	private void addTrackedItem(int itemId)
	{
		addTrackedItem(itemId, TrackItemMode.TRACK);
	}

	/** Tracks an item by id in the given mode, routing {@link TrackItemMode#VIEW} to a read-only preview instead. */
	private void addTrackedItem(int itemId, TrackItemMode mode)
	{
		if (mode == TrackItemMode.VIEW)
		{
			previewItem(itemId);
			return;
		}

		boolean isNew = !trackedItems.containsKey(itemId);
		addTrackedItem(itemId, 0, null, null, false, false, true, true, mode);

		if (isNew && config.promptCategoryOnTrack())
			promptCategoryForTrackedItem(itemId);
	}

	/**
	 * After an item is explicitly tracked (#211), asks the panel to prompt for its category. Enqueued
	 * on the client thread so it runs after the deferred add body, then hops to the EDT once the item
	 * is confirmed present. Only reached from explicit tracking — never from load, import, or auto-add.
	 */
	private void promptCategoryForTrackedItem(int itemId)
	{
		clientThread.invokeLater(() ->
		{
			if (trackedItems.containsKey(itemId))
				SwingUtilities.invokeLater(() -> panel.promptCategoryForItem(itemId));
		});
	}

	/**
	 * Opens a read-only detail preview for an untracked item without adding it to
	 * the tracked list or persisting anything. Builds a transient {@link TrackedItem},
	 * shows it in the detail view, then fetches its prices and history in the
	 * background. Runs on the client thread.
	 */
	private void previewItem(int itemId)
	{
		clientThread.invokeLater(() ->
		{
			TrackedItem preview = previewItem;
			if (preview == null || preview.getItemId() != itemId)
			{
				var composition = itemManager.getItemComposition(itemId);
				preview = new TrackedItem(itemId, composition.getName());
				preview.setTradeable(composition.isTradeable());
				preview.setMode(TrackItemMode.VIEW);
				applyItemMetadata(preview);
				previewItem = preview;
			}

			final TrackedItem shown = preview;
			SwingUtilities.invokeLater(() -> panel.showPreview(shown));
			requestDetailData(itemId);
			refreshGePrices();
		});
	}

	/**
	 * @return the tracked item for {@code itemId}, or the transient preview item when it
	 *         matches; otherwise {@code null}
	 */
	private TrackedItem lookupItem(int itemId)
	{
		TrackedItem tracked = trackedItems.get(itemId);
		if (tracked != null)
			return tracked;

		if (previewItem != null && previewItem.getItemId() == itemId)
			return previewItem;

		TrackedItem window = windowItems.get(itemId);
		if (window != null)
			return window;

		return compareItems.get(itemId);
	}

	/** Tracks an item with a preset quantity and acquisition history (e.g. a restore), using default notifications. */
	private void addTrackedItem(int itemId, int initialQuantity, List<AcquisitionRecord> records,
			boolean costBasisInitialized)
	{
		addTrackedItem(itemId, initialQuantity, records, null, false, costBasisInitialized, true, true,
				TrackItemMode.TRACK);
	}

	/**
	 * Canonical add: creates a {@link TrackedItem} (resolving its name/tradeable
	 * flag from the item composition), seeds its quantity, acquisitions, and
	 * notifications, registers it, and persists/refreshes. No-op if already
	 * tracked. Runs on the client thread.
	 *
	 * @param initialQuantity        starting count
	 * @param records                preset acquisition lots, or {@code null}
	 * @param notifications          preset notification rules, or {@code null}
	 * @param notificationsInitialized whether default rules have already been seeded
	 * @param costBasisInitialized   whether a cost basis has already been established
	 * @param syncOnAdd              recount from containers immediately when in TRACK mode
	 * @param persistOnAdd           persist the tracked list after adding; the persisted-load
	 *                               replay passes {@code false}, both because the data came from
	 *                               config unchanged and because persisting mid-replay would write
	 *                               the item before its deferred grouping/death-suspension
	 *                               callbacks have applied, stripping those fields
	 * @param mode                   tracking vs. view-only
	 */
	private void addTrackedItem(int itemId, int initialQuantity, List<AcquisitionRecord> records,
			List<NotificationRule> notifications, boolean notificationsInitialized,
			boolean costBasisInitialized, boolean syncOnAdd, boolean persistOnAdd, TrackItemMode mode)
	{
		clientThread.invokeLater(() ->
		{
			if (trackedItems.containsKey(itemId))
				return;

			var composition = itemManager.getItemComposition(itemId);
			TrackedItem tracked = new TrackedItem(itemId, composition.getName());
			tracked.setTradeable(composition.isTradeable());
			resolveTradeable(tracked);
			tracked.setQuantity(initialQuantity);
			tracked.setMode(mode == null ? TrackItemMode.TRACK : mode);
			if (records != null)
				tracked.setAcquisitions(new ArrayList<>(records));

			if (notifications != null)
				tracked.setNotifications(new ArrayList<>(notifications));

			tracked.setNotificationsInitialized(notificationsInitialized);
			tracked.setCostBasisInitialized(costBasisInitialized);
			trackedItems.put(itemId, tracked);

			if (syncOnAdd && tracked.getMode() == TrackItemMode.TRACK)
				syncQuantitiesForItem(tracked);

			if (persistOnAdd)
				persistTrackedItems();

			refreshPanel();
			refreshGePrices();
		});
	}

	/**
	 * Stops tracking an item, then persists and refreshes. Also drops the item from the
	 * session baseline — before the panel's next rebuild computes the session delta — so
	 * untracking doesn't read as the item's whole value lost. Runs on the client thread.
	 */
	private void removeTrackedItem(int itemId)
	{
		clientThread.invokeLater(() ->
		{
			trackedItems.remove(itemId);
			portfolioHistory.removeItem(itemId);
			SwingUtilities.invokeLater(() ->
			{
				panel.removeSessionBaseline(itemId);
				closeDetailWindowFor(itemId);
			});
			persistTrackedItems();
			persistPortfolioHistory();
			refreshPanel();
		});
	}

	/**
	 * Stops tracking an item but leaves it open in the detail view as a read-only preview (#138),
	 * so untracking from the detail header does not bounce the user back to the main list. Removes
	 * and persists exactly as {@link #removeTrackedItem}, then builds a transient preview and shows
	 * it: the preview is opened (posting {@code showPreview} to the EDT) before the list rebuild is
	 * queued, so the rebuild finds the panel already backed by the preview and keeps the detail card
	 * up instead of returning to the list. Runs on the client thread.
	 */
	private void untrackToPreview(int itemId)
	{
		clientThread.invokeLater(() ->
		{
			trackedItems.remove(itemId);
			portfolioHistory.removeItem(itemId);
			SwingUtilities.invokeLater(() -> panel.removeSessionBaseline(itemId));
			persistTrackedItems();
			persistPortfolioHistory();

			var composition = itemManager.getItemComposition(itemId);
			TrackedItem preview = new TrackedItem(itemId, composition.getName());
			preview.setTradeable(composition.isTradeable());
			preview.setMode(TrackItemMode.VIEW);
			applyItemMetadata(preview);
			previewItem = preview;

			final TrackedItem shown = preview;
			SwingUtilities.invokeLater(() -> panel.showPreview(shown));
			refreshPanel();
			requestDetailData(itemId);
			refreshGePrices();
		});
	}

	/**
	 * Pops {@code itemId} out into its own standalone detail window (#109), or focuses the existing one.
	 * Invoked on the EDT from the detail header pop-out button. Resolves the bound instance on the
	 * client thread &mdash; the live tracked item, the current sidebar preview when it matches, or a
	 * freshly built preview &mdash; then opens the window on the EDT and kicks off a data fetch.
	 */
	private void popOutDetail(int itemId)
	{
		DetailWindow existing = detailWindows.get(itemId);
		if (existing != null)
		{
			existing.focus();
			return;
		}

		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			final TrackedItem item;
			final boolean preview;
			if (tracked != null)
			{
				item = tracked;
				preview = false;
			}
			else
			{
				item = previewItem != null && previewItem.getItemId() == itemId
						? previewItem
						: buildPreview(itemId);
				preview = true;
			}

			windowItems.put(itemId, item);
			SwingUtilities.invokeLater(() -> openDetailWindow(item, preview));
			requestDetailData(itemId);
			if (preview)
				refreshGePrices();
		});
	}

	/** Builds a transient read-only preview item (name, tradeability, GE metadata) for an untracked id. */
	private TrackedItem buildPreview(int itemId)
	{
		var composition = itemManager.getItemComposition(itemId);
		TrackedItem preview = new TrackedItem(itemId, composition.getName());
		preview.setTradeable(composition.isTradeable());
		preview.setMode(TrackItemMode.VIEW);
		applyItemMetadata(preview);
		return preview;
	}

	/** Creates and registers a pop-out window for {@code item}, or focuses an existing one. Runs on the EDT. */
	private void openDetailWindow(TrackedItem item, boolean preview)
	{
		DetailWindow existing = detailWindows.get(item.getItemId());
		if (existing != null)
		{
			existing.focus();
			return;
		}

		DetailWindow window = new DetailWindow(this::windowHost, item, preview, this::onDetailWindowClosed);
		detailWindows.put(item.getItemId(), window);
	}

	/**
	 * Opens the item-less Stockpile dashboard window (#109), or focuses the existing one. The window is
	 * registered under the reserved id 0 (real item ids are always positive); searching an item from it
	 * re-keys the registry to that item's id via {@link #switchWindowItem}. Runs on the EDT.
	 */
	private void openDashboardWindow()
	{
		DetailWindow existing = detailWindows.get(0);
		if (existing != null)
		{
			existing.focus();
			return;
		}

		DetailWindow window = new DetailWindow(this::windowHost, this::onDetailWindowClosed);
		detailWindows.put(0, window);
	}

	/** Drops a closed pop-out window from both the EDT registry and its client-thread instance map. */
	private void onDetailWindowClosed(int itemId)
	{
		detailWindows.remove(itemId);
		clientThread.invokeLater(() -> windowItems.remove(itemId));
	}

	/**
	 * Builds the {@link DetailViewHost} for a pop-out window. Shares the plugin's services and edit
	 * callbacks, but resolves the tracked item from the window's own bound instance (never the live map
	 * off the EDT), routes track/untrack through window-aware handlers so the transition stays in that
	 * window, and disposes the window on Back.
	 */
	private DetailViewHost windowHost(DetailWindow window)
	{
		return new DetailViewHost()
		{
			@Override
			public StockpileConfig config()
			{
				return config;
			}

			@Override
			public ItemManager itemManager()
			{
				return itemManager;
			}

			@Override
			public String examine(int id)
			{
				return examineFor(id);
			}

			@Override
			public TrackedItem trackedItem(int id)
			{
				return !window.isPreview() && window.itemId() == id ? window.boundItem() : null;
			}

			@Override
			public long natureRunePrice()
			{
				return lastNatureRunePrice;
			}

			@Override
			public long fireRunePrice()
			{
				return lastFireRunePrice;
			}

			@Override
			public void requestDetailData(int id)
			{
				StockpilePlugin.this.requestDetailData(id);
			}

			@Override
			public void acquisitionsEdited(int id)
			{
				onAcquisitionsEdited(id);
			}

			@Override
			public void clearAcquisitions(int id)
			{
				StockpilePlugin.this.clearAcquisitions(id);
			}

			@Override
			public void notificationsEdited(int id)
			{
				onNotificationsEdited(id);
			}

			@Override
			public void addItem(int id, TrackItemMode mode)
			{
				trackFromWindow(window, id, mode);
			}

			@Override
			public void untrackToPreview(int id)
			{
				untrackWindowToPreview(window, id);
			}

			@Override
			public void popOut(int id)
			{
				window.focus();
			}

			@Override
			public void addToCompare(int id)
			{
				clientThread.invokeLater(() -> StockpilePlugin.this.addToCompare(id));
			}

			@Override
			public void switchDetailItem(int id)
			{
				switchWindowItem(window, id);
			}

			@Override
			public void onBack()
			{
				window.dispose();
			}
		};
	}

	/**
	 * Rebinds a pop-out window to a different item chosen from its search bar (#109). Re-keys both the
	 * EDT window registry and its client-thread instance map from the old id to the new one, resolves the
	 * new item (the live tracked item, or a freshly built read-only preview when untracked), transitions
	 * the window in place, and kicks off a data fetch. When another window already shows the target item,
	 * that window is focused instead and this one is left unchanged.
	 */
	private void switchWindowItem(DetailWindow window, int newItemId)
	{
		int oldItemId = window.itemId();
		if (newItemId == oldItemId)
			return;

		DetailWindow existing = detailWindows.get(newItemId);
		if (existing != null && existing != window)
		{
			existing.focus();
			return;
		}

		detailWindows.remove(oldItemId);
		detailWindows.put(newItemId, window);

		clientThread.invokeLater(() ->
		{
			windowItems.remove(oldItemId);

			TrackedItem tracked = trackedItems.get(newItemId);
			final TrackedItem item;
			final boolean preview;
			if (tracked != null)
			{
				item = tracked;
				preview = false;
			}
			else
			{
				item = buildPreview(newItemId);
				preview = true;
			}

			windowItems.put(newItemId, item);
			SwingUtilities.invokeLater(() -> window.rebind(newItemId, item, preview));
			requestDetailData(newItemId);
			if (preview)
				refreshGePrices();
		});
	}

	/** Tracks {@code itemId} from a pop-out window's header (#138), then transitions that window to tracked. */
	private void trackFromWindow(DetailWindow window, int itemId, TrackItemMode mode)
	{
		addTrackedItem(itemId, mode);
		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null)
				return;

			windowItems.put(itemId, tracked);
			SwingUtilities.invokeLater(() -> window.syncTracked(tracked));
		});
	}

	/**
	 * Untracks {@code itemId} from a pop-out window's header (#138) but keeps that window open as a
	 * read-only preview. Removes and persists exactly as {@link #untrackToPreview}, then rebinds the
	 * window to a fresh preview instead of the sidebar.
	 */
	private void untrackWindowToPreview(DetailWindow window, int itemId)
	{
		clientThread.invokeLater(() ->
		{
			trackedItems.remove(itemId);
			portfolioHistory.removeItem(itemId);
			SwingUtilities.invokeLater(() -> panel.removeSessionBaseline(itemId));
			persistTrackedItems();
			persistPortfolioHistory();

			TrackedItem preview = buildPreview(itemId);
			windowItems.put(itemId, preview);

			final TrackedItem shown = preview;
			SwingUtilities.invokeLater(() -> window.showAsPreview(shown));
			refreshPanel();
			requestDetailData(itemId);
			refreshGePrices();
		});
	}

	/** Closes any open pop-out window for {@code itemId} (e.g. when the item is untracked). Runs on the EDT. */
	private void closeDetailWindowFor(int itemId)
	{
		DetailWindow window = detailWindows.get(itemId);
		if (window != null)
			window.dispose();
	}

	/** Disposes every open pop-out window (on the EDT), e.g. at shutdown or when the whole list is cleared. */
	private void closeAllDetailWindows()
	{
		for (DetailWindow window : new ArrayList<>(detailWindows.values()))
			window.dispose();
	}

	/** Re-populates every open pop-out window with fresh data. Runs on the EDT. */
	private void refreshDetailWindows()
	{
		for (DetailWindow window : new ArrayList<>(detailWindows.values()))
			window.refreshData();
	}

	/**
	 * Adds {@code itemId} to the shared compare set and opens or focuses the compare window (#280). A
	 * canonicalised id already present just focuses the window; a full set (see {@link #COMPARE_CAP})
	 * focuses without adding. Untracked ids get a read-only preview instance so their column stays live.
	 * A background data fetch fills in the new item's prices. Runs on the client thread.
	 */
	private void addToCompare(int itemId)
	{
		int canonicalId = itemManager.canonicalize(itemId);
		if (canonicalId <= 0)
			return;

		if (compareIds.contains(canonicalId))
		{
			SwingUtilities.invokeLater(this::focusCompareWindow);
			return;
		}

		if (compareIds.size() >= COMPARE_CAP)
		{
			SwingUtilities.invokeLater(this::focusCompareWindow);
			return;
		}

		compareIds.add(canonicalId);
		if (!trackedItems.containsKey(canonicalId))
			compareItems.put(canonicalId, buildPreview(canonicalId));

		requestDetailData(canonicalId);
		refreshGePrices();
		rebuildCompareWindow(true);
	}

	/**
	 * Adds every resolved variant of {@code itemId} — its potion dose line or cooking chain (#302) — to
	 * the compare set in natural order, up to {@link #COMPARE_CAP}, then opens/focuses the window. Siblings
	 * beyond the cap or already present are skipped; when nothing new fits, the window is just focused.
	 * Client thread.
	 */
	private void addVariantsToCompare(int itemId)
	{
		int canonicalId = itemManager.canonicalize(itemId);
		if (canonicalId <= 0)
			return;

		boolean changed = false;
		for (int id : resolveVariantIds(canonicalId))
		{
			if (compareIds.size() >= COMPARE_CAP)
				break;

			if (compareIds.contains(id))
				continue;

			compareIds.add(id);
			if (!trackedItems.containsKey(id))
				compareItems.put(id, buildPreview(id));

			requestDetailData(id);
			changed = true;
		}

		if (changed)
		{
			refreshGePrices();
			rebuildCompareWindow(true);
		}
		else
		{
			SwingUtilities.invokeLater(this::focusCompareWindow);
		}
	}

	/**
	 * Resolves the canonical ids of {@code itemId}'s variant family in natural order (dose {@code (1)}→
	 * {@code (4)}, or raw→cooked→burnt), mapping the family's sibling names to ids through the cached wiki
	 * mapping ({@link #variantNameIndex()}), falling back to {@link ItemManager#search} when it is empty.
	 * Siblings with no Grand Exchange data are dropped ({@link #hasMarketData}) so the Compare set never
	 * gains a column that can never fill. Always includes the clicked item, even when it has no family.
	 */
	private List<Integer> resolveVariantIds(int itemId)
	{
		List<String> siblings = VariantFamily.siblingNames(itemManager.getItemComposition(itemId).getName());
		List<Integer> ids = new ArrayList<>();
		Map<String, Integer> byName = variantNameIndex();
		for (String sibling : siblings)
		{
			Integer id = byName.isEmpty() ? searchItemIdByExactName(sibling) : byName.get(sibling);
			if (id == null)
				continue;

			int canonicalSibling = itemManager.canonicalize(id);
			if (canonicalSibling > 0 && !ids.contains(canonicalSibling) && hasMarketData(canonicalSibling))
				ids.add(canonicalSibling);
		}

		if (!ids.contains(itemId))
			ids.add(0, itemId);

		return ids;
	}

	/**
	 * @param itemId the candidate variant's canonical item id
	 * @return whether the item has Grand Exchange data behind it — membership of the wiki mapping once
	 *         that has loaded, else the client's own tradeable flag. Variants without it (the burnt
	 *         hunter meats, which exist in game but never reach the GE) would add a permanently empty
	 *         Compare column, so {@link #resolveVariantIds} drops them (#309).
	 */
	private boolean hasMarketData(int itemId)
	{
		Map<Integer, WikiRealtimePriceClient.ItemMapping> mappings = itemMappings;
		if (!mappings.isEmpty())
			return mappings.containsKey(itemId);

		return itemManager.getItemComposition(itemId).isTradeable();
	}

	/**
	 * Builds a lowercased-name → item-id index from the cached wiki {@link #itemMappings} — the
	 * authoritative tradeable-item corpus (#302). Empty until the mapping has been fetched, in which case
	 * the caller falls back to {@link #searchItemIdByExactName}.
	 */
	private Map<String, Integer> variantNameIndex()
	{
		Map<Integer, WikiRealtimePriceClient.ItemMapping> mappings = itemMappings;
		Map<String, Integer> byName = new HashMap<>(mappings.size());
		for (Map.Entry<Integer, WikiRealtimePriceClient.ItemMapping> entry : mappings.entrySet())
		{
			String name = entry.getValue().getName();
			if (name == null || name.isEmpty())
				continue;

			byName.putIfAbsent(name.toLowerCase(Locale.ROOT), entry.getKey());
		}

		return byName;
	}

	/**
	 * Offline fallback for {@link #resolveVariantIds}: searches the client's item index for a tradeable
	 * item whose name equals {@code lowerName} (case-insensitive).
	 *
	 * @return the matching item id, or {@code null} when none matches
	 */
	private Integer searchItemIdByExactName(String lowerName)
	{
		for (ItemPrice price : itemManager.search(lowerName))
		{
			if (price.getName() != null && price.getName().equalsIgnoreCase(lowerName))
				return price.getId();
		}

		return null;
	}

	/** Removes {@code itemId} from the compare set, closing the window when the set empties. Client thread. */
	private void removeFromCompareId(int itemId)
	{
		if (!compareIds.remove(itemId))
			return;

		compareItems.remove(itemId);
		rebuildCompareWindow(false);
	}

	/**
	 * Reorders the compare set so {@code itemId} sits at {@code toIndex}, then refreshes the window.
	 * Backs the drag-reorder of the compare columns. Client thread.
	 *
	 * @param itemId the compared item being moved
	 * @param toIndex the target position in the compare order
	 */
	private void moveCompareId(int itemId, int toIndex)
	{
		if (!compareIds.contains(itemId))
			return;

		List<Integer> order = new ArrayList<>(compareIds);
		order.remove((Integer) itemId);
		int index = Math.max(0, Math.min(toIndex, order.size()));
		order.add(index, itemId);
		compareIds.clear();
		compareIds.addAll(order);
		rebuildCompareWindow(false);
	}

	/** Clears the whole compare set and closes the window. Client thread. */
	private void clearCompareSet()
	{
		compareIds.clear();
		compareItems.clear();
		SwingUtilities.invokeLater(this::closeCompareWindow);
	}

	/** Loads the persisted saved comparisons into memory at startup (#303). Client thread. */
	private void loadSavedComparisons()
	{
		savedComparisons.clear();
		savedComparisons.addAll(persistence.loadComparisons());
	}

	/**
	 * Saves the current compare set under {@code name} (#303), overwriting any existing comparison of that
	 * name (case-insensitive), then persists and refreshes the window's Load menu. Client thread.
	 *
	 * @param name the user-given comparison name
	 */
	private void saveCurrentComparison(String name)
	{
		StockpilePersistence.SavedComparison saved = new StockpilePersistence.SavedComparison();
		saved.name = name;
		saved.itemIds = new ArrayList<>(compareIds);

		int existing = indexOfComparison(name);
		if (existing >= 0)
			savedComparisons.set(existing, saved);
		else
			savedComparisons.add(saved);

		persistence.saveComparisons(savedComparisons);
		pushSavedNames();
	}

	/**
	 * Replaces the current compare set with the saved comparison named {@code name} (#303): its canonical,
	 * de-duplicated ids up to {@link #COMPARE_CAP}, each given a read-only preview when untracked. Client thread.
	 *
	 * @param name the saved comparison to load
	 */
	private void loadSavedComparison(String name)
	{
		int index = indexOfComparison(name);
		if (index < 0)
			return;

		StockpilePersistence.SavedComparison saved = savedComparisons.get(index);
		applyCompareIds(saved.itemIds);
	}

	/**
	 * Replaces the current compare set with the items from an imported shared code (#303): its canonical,
	 * de-duplicated ids up to {@link #COMPARE_CAP}. Client thread.
	 *
	 * @param itemIds the imported item ids
	 */
	private void importComparison(List<Integer> itemIds)
	{
		applyCompareIds(itemIds);
	}

	/**
	 * Replaces the compare set with {@code itemIds} (#303): canonicalised, de-duplicated, capped at
	 * {@link #COMPARE_CAP}, each untracked id given a read-only preview; then refreshes prices and the window.
	 *
	 * @param itemIds the item ids to load (a {@code null} list clears the set)
	 */
	private void applyCompareIds(List<Integer> itemIds)
	{
		compareIds.clear();
		compareItems.clear();
		if (itemIds != null)
		{
			for (int id : itemIds)
			{
				int canonicalId = itemManager.canonicalize(id);
				if (canonicalId <= 0 || compareIds.contains(canonicalId))
					continue;

				if (compareIds.size() >= COMPARE_CAP)
					break;

				compareIds.add(canonicalId);
				if (!trackedItems.containsKey(canonicalId))
					compareItems.put(canonicalId, buildPreview(canonicalId));

				requestDetailData(canonicalId);
			}
		}

		refreshGePrices();
		rebuildCompareWindow(true);
	}

	/** Deletes the saved comparison named {@code name} (#303), persists, and refreshes the Load menu. Client thread. */
	private void deleteSavedComparison(String name)
	{
		int index = indexOfComparison(name);
		if (index < 0)
			return;

		savedComparisons.remove(index);
		persistence.saveComparisons(savedComparisons);
		pushSavedNames();
	}

	/** @return the index of the saved comparison named {@code name} (case-insensitive), or -1 if none. */
	private int indexOfComparison(String name)
	{
		for (int i = 0; i < savedComparisons.size(); i++)
		{
			String existing = savedComparisons.get(i).name;
			if (existing != null && existing.equalsIgnoreCase(name))
				return i;
		}

		return -1;
	}

	/** @return the saved-comparison names in saved order (#303). Client thread. */
	private List<String> savedComparisonNames()
	{
		List<String> names = new ArrayList<>();
		for (StockpilePersistence.SavedComparison saved : savedComparisons)
			if (saved.name != null)
				names.add(saved.name);

		return names;
	}

	/** Pushes the current saved-comparison names to the open window's Load menu (#303), if one is open. */
	private void pushSavedNames()
	{
		final List<String> names = savedComparisonNames();
		SwingUtilities.invokeLater(() ->
		{
			if (compareWindow != null)
				compareWindow.setSavedNames(names);
		});
	}

	/**
	 * Snapshots the compare set into an ordered {@link CompareView.Entry} list (resolving each id to its
	 * live tracked item or its preview) and hands it to the EDT to open or update the window. Client thread.
	 *
	 * @param focus whether to bring the window to the front after updating (true when an item was just added)
	 */
	private void rebuildCompareWindow(boolean focus)
	{
		final List<CompareView.Entry> entries = compareEntries();
		final List<String> names = savedComparisonNames();
		final List<Integer> ids = new ArrayList<>(compareIds);
		SwingUtilities.invokeLater(() -> showOrUpdateCompareWindow(entries, names, ids, focus));
	}

	/**
	 * Snapshots the compare set into an ordered {@link CompareView.Entry} list, resolving each id to its
	 * live tracked item or its read-only preview. Runs on the client thread.
	 *
	 * @return the compared items, in display order
	 */
	private List<CompareView.Entry> compareEntries()
	{
		List<CompareView.Entry> entries = new ArrayList<>();
		for (int id : compareIds)
		{
			TrackedItem tracked = trackedItems.get(id);
			if (tracked != null)
			{
				entries.add(new CompareView.Entry(tracked, true));
			}
			else
			{
				TrackedItem preview = compareItems.computeIfAbsent(id, this::buildPreview);
				entries.add(new CompareView.Entry(preview, false));
			}
		}

		return entries;
	}

	/**
	 * Opens the compare window from the main-view toolbar button (or focuses the open one), showing the
	 * empty "add items to compare" prompt when the set is empty rather than staying closed. Client thread.
	 */
	private void openCompareWindow()
	{
		final List<CompareView.Entry> entries = compareEntries();
		final List<String> names = savedComparisonNames();
		final List<Integer> ids = new ArrayList<>(compareIds);
		SwingUtilities.invokeLater(() -> openOrFocusCompareWindow(entries, names, ids));
	}

	/**
	 * Opens the compare window (or updates the open one) with {@code entries}, disposing it when the set is
	 * empty. Runs on the EDT.
	 *
	 * @param entries the items to compare, in display order
	 * @param names the current saved-comparison names for the Load menu
	 * @param ids the current compare-set item ids, backing Export
	 * @param focus whether to bring the window to the front after updating
	 */
	private void showOrUpdateCompareWindow(List<CompareView.Entry> entries, List<String> names,
			List<Integer> ids, boolean focus)
	{
		if (entries.isEmpty())
		{
			closeCompareWindow();
			return;
		}

		if (compareWindow == null)
			compareWindow = new CompareWindow(compareHost(), entries, this::onCompareWindowClosed);
		else
			compareWindow.setEntries(entries);

		compareWindow.setSavedNames(names);
		compareWindow.setCurrentIds(ids);
		if (focus)
			compareWindow.focus();
	}

	/**
	 * Opens the compare window with {@code entries} (an empty list is allowed, showing the prompt) or
	 * updates and focuses the already-open one. Runs on the EDT.
	 *
	 * @param entries the items to compare, in display order
	 * @param names the current saved-comparison names for the Load menu
	 * @param ids the current compare-set item ids, backing Export
	 */
	private void openOrFocusCompareWindow(List<CompareView.Entry> entries, List<String> names, List<Integer> ids)
	{
		if (compareWindow == null)
			compareWindow = new CompareWindow(compareHost(), entries, this::onCompareWindowClosed);
		else
			compareWindow.setEntries(entries);

		compareWindow.setSavedNames(names);
		compareWindow.setCurrentIds(ids);
		compareWindow.focus();
	}

	/** Brings the compare window to the front if one is open. Runs on the EDT. */
	private void focusCompareWindow()
	{
		if (compareWindow != null)
			compareWindow.focus();
	}

	/** Re-reads the compare columns from current prices, if a window is open. Client thread. */
	private void refreshCompareWindow()
	{
		if (!compareIds.isEmpty())
			rebuildCompareWindow(false);
	}

	/** Disposes the compare window (its close listener clears the set). Runs on the EDT. */
	private void closeCompareWindow()
	{
		if (compareWindow != null)
			compareWindow.dispose();
	}

	/** Drops the singleton reference and clears the set when the compare window is closed. */
	private void onCompareWindowClosed()
	{
		compareWindow = null;
		clientThread.invokeLater(() ->
		{
			compareIds.clear();
			compareItems.clear();
		});
	}

	/** Builds the {@link CompareHost} for the compare window, routing edits back onto the client thread. */
	private CompareHost compareHost()
	{
		return new CompareHost()
		{
			@Override
			public StockpileConfig config()
			{
				return config;
			}

			@Override
			public ItemManager itemManager()
			{
				return itemManager;
			}

			@Override
			public long natureRunePrice()
			{
				return lastNatureRunePrice;
			}

			@Override
			public long fireRunePrice()
			{
				return lastFireRunePrice;
			}

			@Override
			public void removeFromCompare(int itemId)
			{
				clientThread.invokeLater(() -> removeFromCompareId(itemId));
			}

			@Override
			public void addVariantsToCompare(int itemId)
			{
				clientThread.invokeLater(() -> StockpilePlugin.this.addVariantsToCompare(itemId));
			}

			@Override
			public void moveCompare(int itemId, int toIndex)
			{
				clientThread.invokeLater(() -> moveCompareId(itemId, toIndex));
			}

			@Override
			public void clearCompare()
			{
				clientThread.invokeLater(StockpilePlugin.this::clearCompareSet);
			}

			@Override
			public void saveComparison(String name)
			{
				clientThread.invokeLater(() -> saveCurrentComparison(name));
			}

			@Override
			public void loadComparison(String name)
			{
				clientThread.invokeLater(() -> loadSavedComparison(name));
			}

			@Override
			public void deleteComparison(String name)
			{
				clientThread.invokeLater(() -> deleteSavedComparison(name));
			}

			@Override
			public void importComparison(List<Integer> itemIds)
			{
				clientThread.invokeLater(() -> StockpilePlugin.this.importComparison(itemIds));
			}
		};
	}

	/** Wipes the portfolio value history (in memory and in config), e.g. when the whole tracked list is cleared. */
	private void clearPortfolioHistory()
	{
		portfolioHistory.clear();
		persistPortfolioHistory();
	}

	/**
	 * Builds a shareable code for the current tracked list (ids, modes, categories,
	 * favorites) — "" when empty — and hands it to {@code onResult} on the EDT.
	 * {@code trackedItems} is client-thread state, so the snapshot is taken there
	 * rather than on the EDT the panel's button handler runs on, where a concurrent
	 * mutation (login replay, auto-add, GE fill) could tear the iteration.
	 */
	void buildShareToken(Consumer<String> onResult)
	{
		clientThread.invokeLater(() ->
		{
			List<PortfolioShareCodec.Entry> entries = trackedItems.values().stream()
					.map(t -> new PortfolioShareCodec.Entry(t.getItemId(), t.getMode(), t.getCategory(),
							t.isFavorite()))
					.collect(Collectors.toList());
			String token = entries.isEmpty()
					? ""
					: new PortfolioShareCodec(gson)
							.encode(new PortfolioShareCodec.Snapshot(1, entries, new ArrayList<>(categories)));
			SwingUtilities.invokeLater(() -> onResult.accept(token));
		});
	}

	/**
	 * Merges a shared tracked-list code into the current profile: adds items that
	 * aren't already tracked (with their mode, category, and favorite flag) plus any
	 * missing categories they reference. Non-destructive — existing items are left
	 * untouched. Decode, count, and merge all run on the client thread (the counts
	 * read {@code trackedItems}); the outcome summary is handed to {@code onResult}
	 * on the EDT.
	 */
	void importTrackedList(String token, Consumer<String> onResult)
	{
		clientThread.invokeLater(() ->
		{
			String message = applyImportedList(token);
			SwingUtilities.invokeLater(() -> onResult.accept(message));
		});
	}

	/** Decodes and merges a share code on the client thread. @return the user-facing outcome summary */
	private String applyImportedList(String token)
	{
		PortfolioShareCodec.Snapshot snapshot = new PortfolioShareCodec(gson).decode(token);
		if (snapshot == null || snapshot.getItems() == null)
			return "Couldn't read that code — make sure you pasted all of it.";

		List<PortfolioShareCodec.Entry> incoming = new ArrayList<>(snapshot.getItems());
		List<CategoryState> incomingCategories = snapshot.getCategories() != null
				? new ArrayList<>(snapshot.getCategories())
				: new ArrayList<>();

		long fresh = incoming.stream().filter(e -> !trackedItems.containsKey(e.getId())).count();
		long skipped = incoming.size() - fresh;

		mergeImportedList(incoming, incomingCategories);

		if (fresh == 0)
			return "Nothing new — all " + skipped + " item(s) are already tracked.";

		return "Imported " + fresh + " item(s)" + (skipped > 0 ? ", skipped " + skipped + " already tracked." : ".");
	}

	/** Applies a decoded tracked-list import on the client thread: categories first, then new items. */
	private void mergeImportedList(List<PortfolioShareCodec.Entry> entries, List<CategoryState> importedCategories)
	{
		importedCategories.stream()
				.filter(c -> c != null && c.getName() != null && !c.getName().trim().isEmpty())
				.forEach(c -> ensureCategory(c.getName().trim(), c.isCollapsed()));

		boolean changed = false;
		for (PortfolioShareCodec.Entry entry : entries)
		{
			if (trackedItems.containsKey(entry.getId()))
				continue;

			var composition = itemManager.getItemComposition(entry.getId());
			if (composition == null)
				continue;

			TrackedItem tracked = new TrackedItem(entry.getId(), composition.getName());
			tracked.setTradeable(composition.isTradeable());
			resolveTradeable(tracked);
			tracked.setMode(entry.getMode() == null ? TrackItemMode.TRACK : entry.getMode());
			tracked.setFavorite(entry.isFavorite());
			if (entry.getCategory() != null && !entry.getCategory().trim().isEmpty())
			{
				String category = entry.getCategory().trim();
				ensureCategory(category, false);
				tracked.setCategory(category);
			}

			trackedItems.put(entry.getId(), tracked);
			if (tracked.getMode() == TrackItemMode.TRACK)
				syncQuantitiesForItem(tracked);

			changed = true;
		}

		if (changed)
		{
			persistCategories();
			persistTrackedItems();
			refreshPanel();
			refreshGePrices();
		}
	}

	/** Adds a category by name if one with that name doesn't already exist (case-insensitive). */
	private void ensureCategory(String name, boolean collapsed)
	{
		boolean exists = categories.stream()
				.anyMatch(c -> c.getName() != null && c.getName().equalsIgnoreCase(name));
		if (!exists)
			categories.add(new CategoryState(name, collapsed));
	}

	/**
	 * Builds the acquisitions log of all tracked items as CSV (see
	 * {@link AcquisitionCsvExporter}) on the client thread — the items and their
	 * acquisition lists are client-thread state — and hands it to {@code onResult}
	 * on the EDT.
	 */
	void buildAcquisitionsCsv(Consumer<String> onResult)
	{
		clientThread.invokeLater(() ->
		{
			String csv = AcquisitionCsvExporter.toCsv(new ArrayList<>(trackedItems.values()));
			SwingUtilities.invokeLater(() -> onResult.accept(csv));
		});
	}

	/**
	 * Moves a tracked item to a new position in the list, persisting the new order so it
	 * survives restarts. {@code targetIndex} is clamped to the list bounds; a no-op if the
	 * item is unknown or already at that position. Runs on the client thread.
	 */
	private void reorderTrackedItem(int itemId, int targetIndex)
	{
		clientThread.invokeLater(() ->
		{
			List<TrackedItem> ordered = new ArrayList<>(trackedItems.values());

			int from = -1;
			for (int i = 0; i < ordered.size(); i++)
			{
				if (ordered.get(i).getItemId() == itemId)
				{
					from = i;
					break;
				}
			}

			if (from < 0)
				return;

			int to = Math.max(0, Math.min(targetIndex, ordered.size() - 1));
			if (to == from)
				return;

			ordered.add(to, ordered.remove(from));

			trackedItems.clear();
			ordered.forEach(item -> trackedItems.put(item.getItemId(), item));

			persistTrackedItems();
			refreshPanel();
		});
	}

	/** Persists the chosen sort mode; the resulting {@link ConfigChanged} rebuilds the panel. */
	private void setSortMode(SortMode mode)
	{
		configManager.setConfiguration(StockpileConfig.GROUP, StockpileConfig.KEY_SORT_MODE, mode);
	}

	/** Flips the persisted sort direction; the resulting {@link ConfigChanged} rebuilds the panel. */
	private void toggleSortReversed()
	{
		configManager.setConfiguration(StockpileConfig.GROUP, StockpileConfig.KEY_SORT_REVERSED,
				!config.sortReversed());
	}

	/** Flips the persisted compact-view flag; the resulting {@link ConfigChanged} rebuilds the panel. */
	private void toggleCompactView()
	{
		configManager.setConfiguration(StockpileConfig.GROUP, StockpileConfig.KEY_COMPACT_VIEW,
				!config.compactView());
	}

	/** Sets an item's favorite flag (pinning it to the top "Favorites" group), then persists and refreshes. */
	private void setFavorite(int itemId, boolean favorite)
	{
		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null)
				return;

			tracked.setFavorite(favorite);
			persistTrackedItems();
			refreshPanel();
		});
	}

	/**
	 * Adds/removes an item from the on-screen overlay set, enforcing the {@link #OVERLAY_MAX}
	 * cap (an add beyond the cap is ignored), then persists and refreshes.
	 */
	private void setOnOverlay(int itemId, boolean on)
	{
		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null || tracked.isOnOverlay() == on)
				return;

			if (on && overlayItemCount() >= OVERLAY_MAX)
				return;

			tracked.setOnOverlay(on);
			persistTrackedItems();
			refreshPanel();
		});
	}

	/** Toggles an item's per-item compact-row override (#210), then persists and refreshes. */
	private void setItemCompact(int itemId, boolean on)
	{
		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null || tracked.isCompact() == on)
				return;

			tracked.setCompact(on);
			persistTrackedItems();
			refreshPanel();
		});
	}

	/** @return how many tracked items are currently flagged for the on-screen overlay. */
	private int overlayItemCount()
	{
		return (int) trackedItems.values()
				.stream()
				.filter(TrackedItem::isOnOverlay)
				.count();
	}

	/** @return the tracked items shown on the overlay (in tracked order), capped at {@link #OVERLAY_MAX}. */
	List<TrackedItem> getOverlayItems()
	{
		return trackedItems.values().stream()
				.filter(TrackedItem::isOnOverlay)
				.limit(OVERLAY_MAX)
				.collect(Collectors.toList());
	}

	/** Sets a list group's collapsed state (a category name, or a special-group key), then persists and refreshes. */
	private void setGroupCollapsed(String groupKey, boolean collapsed)
	{
		clientThread.invokeLater(() ->
		{
			if (CategoryState.FAVORITES_KEY.equals(groupKey))
				favoritesCollapsed = collapsed;
			else if (CategoryState.UNCATEGORIZED_KEY.equals(groupKey))
				uncategorizedCollapsed = collapsed;
			else
				categories.stream()
						.filter(c -> c.getName().equals(groupKey))
						.findFirst()
						.ifPresent(c -> c.setCollapsed(collapsed));

			persistCategories();
			refreshPanel();
		});
	}

	/** Assigns an item to a category (null/blank clears it to Uncategorized), then persists and refreshes. */
	private void setItemCategory(int itemId, String category)
	{
		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null)
				return;

			tracked.setCategory(category == null || category.trim().isEmpty() ? null : category.trim());
			persistTrackedItems();
			refreshPanel();
		});
	}

	/**
	 * Auto-assigns tracked items to wiki-derived categories (see {@link ItemCategoryClassifier}),
	 * creating any missing categories. Non-destructive unless {@code includeCategorized} is set:
	 * by default only uncategorized items are touched, so manual assignments are preserved. The
	 * mutation runs on the client thread; the returned message reports the outcome.
	 *
	 * @return a user-facing summary of how many items were categorized
	 */
	String autoCategorize(boolean includeCategorized)
	{
		long willChange = trackedItems.values().stream()
				.filter(t -> inAutoCategorizeScope(t, includeCategorized))
				.filter(t -> !ItemCategoryClassifier.classify(t.getName()).equals(t.getCategory()))
				.count();

		clientThread.invokeLater(() -> applyAutoCategorize(includeCategorized));

		if (willChange == 0)
			return "Nothing to categorize — everything already matches.";

		return "Auto-categorized " + willChange + " item(s).";
	}

	/** @return whether the item is in scope: always when re-categorizing, otherwise only when uncategorized. */
	private boolean inAutoCategorizeScope(TrackedItem item, boolean includeCategorized)
	{
		return includeCategorized || item.getCategory() == null || item.getCategory().trim().isEmpty();
	}

	/** Applies auto-categorization on the client thread: classify each in-scope item, create categories, assign. */
	private void applyAutoCategorize(boolean includeCategorized)
	{
		boolean changed = false;
		List<CategoryState> created = new ArrayList<>();
		for (TrackedItem tracked : trackedItems.values())
		{
			if (!inAutoCategorizeScope(tracked, includeCategorized))
				continue;

			String target = ItemCategoryClassifier.classify(tracked.getName());
			if (target.equals(tracked.getCategory()))
				continue;

			if (categories.stream().noneMatch(c -> c.getName().equalsIgnoreCase(target)))
			{
				CategoryState category = new CategoryState(target, false);
				categories.add(category);
				created.add(category);
			}

			tracked.setCategory(target);
			changed = true;
		}

		if (changed)
		{
			orderGeneratedCategories(created);
			persistCategories();
			persistTrackedItems();
			refreshPanel();
		}
	}

	/**
	 * Orders an auto-categorize run's generated categories alphabetically after any
	 * pre-existing (manually ordered) ones, then keeps "Other" at the very end.
	 */
	private void orderGeneratedCategories(List<CategoryState> created)
	{
		categories.removeAll(created);
		created.stream()
				.sorted(Comparator.comparing(CategoryState::getName, String.CASE_INSENSITIVE_ORDER))
				.forEach(categories::add);

		List<CategoryState> other = categories.stream()
				.filter(c -> ItemCategoryClassifier.OTHER.equalsIgnoreCase(c.getName()))
				.collect(Collectors.toList());
		categories.removeAll(other);
		categories.addAll(other);
	}

	/** Creates a new category (ignoring blanks and case-insensitive duplicates), then persists and refreshes. */
	private void createCategory(String name)
	{
		clientThread.invokeLater(() ->
		{
			String trimmed = name == null ? "" : name.trim();
			if (trimmed.isEmpty() || categories.stream().anyMatch(c -> c.getName().equalsIgnoreCase(trimmed)))
				return;

			categories.add(new CategoryState(trimmed, false));
			persistCategories();
			refreshPanel();
		});
	}

	/** Renames a category and re-points its items, ignoring blanks and clashes, then persists and refreshes. */
	private void renameCategory(String oldName, String newName)
	{
		clientThread.invokeLater(() ->
		{
			String trimmed = newName == null ? "" : newName.trim();
			if (trimmed.isEmpty())
				return;

			CategoryState target = null;
			for (CategoryState c : categories)
			{
				if (c.getName().equals(oldName))
					target = c;
				else if (c.getName().equalsIgnoreCase(trimmed))
					return;
			}

			if (target == null)
				return;

			target.setName(trimmed);
			trackedItems.values().stream()
					.filter(t -> oldName.equals(t.getCategory()))
					.forEach(t -> t.setCategory(trimmed));

			persistCategories();
			persistTrackedItems();
			refreshPanel();
		});
	}

	/** Deletes a category, moving its items to Uncategorized, then persists and refreshes. */
	private void deleteCategory(String name)
	{
		clientThread.invokeLater(() ->
		{
			if (!categories.removeIf(c -> c.getName().equals(name)))
				return;

			trackedItems.values().stream()
					.filter(t -> name.equals(t.getCategory()))
					.forEach(t -> t.setCategory(null));

			persistCategories();
			persistTrackedItems();
			refreshPanel();
		});
	}

	/** Moves a category to a new position in the ordered list, then persists and refreshes. */
	private void reorderCategory(String name, int targetIndex)
	{
		clientThread.invokeLater(() ->
		{
			int from = -1;
			for (int i = 0; i < categories.size(); i++)
			{
				if (categories.get(i).getName().equals(name))
				{
					from = i;
					break;
				}
			}

			if (from < 0)
				return;

			int to = Math.max(0, Math.min(targetIndex, categories.size() - 1));
			if (to == from)
				return;

			categories.add(to, categories.remove(from));
			persistCategories();
			refreshPanel();
		});
	}

	/**
	 * Reorders the tracked items to match the given id order (drag reorder), then persists and
	 * refreshes. Applies the new order only when it is a faithful permutation of the current
	 * set, so a stale or partial drag result cannot drop items.
	 */
	private void setGlobalOrder(List<Integer> orderedIds)
	{
		clientThread.invokeLater(() ->
		{
			Map<Integer, TrackedItem> reordered = new LinkedHashMap<>();
			for (Integer id : orderedIds)
			{
				TrackedItem tracked = trackedItems.get(id);
				if (tracked != null)
					reordered.put(id, tracked);
			}

			if (reordered.size() != trackedItems.size())
				return;

			trackedItems.clear();
			trackedItems.putAll(reordered);
			persistTrackedItems();
			refreshPanel();
		});
	}

	/** Removes every tracked item, then persists and refreshes. Runs on the client thread. */
	private void clearAllTrackedItems()
	{
		clientThread.invokeLater(() ->
		{
			trackedItems.clear();
			clearPortfolioHistory();
			compareIds.clear();
			compareItems.clear();
			SwingUtilities.invokeLater(() ->
			{
				panel.clearSessionBaseline();
				closeAllDetailWindows();
				closeCompareWindow();
			});
			persistTrackedItems();
			refreshPanel();
		});
	}

	/** Fetches just the 5m series for an item in the background and recomputes its window stats. */
	private void requestSeries(int itemId, boolean refreshAfter)
	{
		executor.execute(() ->
		{
			List<WikiRealtimePriceClient.PricePoint> points = wikiPriceClient.fetchTimeseries(itemId, "5m");
			clientThread.invokeLater(() ->
			{
				TrackedItem tracked = trackedItems.get(itemId);
				if (tracked == null)
					return;

				tracked.setSeries5m(points);
				recomputeWindowStats(tracked);
				if (refreshAfter)
					refreshPanel();
			});
		});
	}

	/**
	 * @param itemId the item whose live instances to collect
	 * @return every in-memory {@link TrackedItem} carrying {@code itemId}: the tracked entry, the panel's
	 *         view-only preview, a pop-out window's item, and the Compare set's preview. Unlike
	 *         {@link #lookupItem} this returns ALL of them — the same id is routinely held by two at once
	 *         (preview an untracked item, then add it to Compare), and a fetch that reaches only the first
	 *         leaves the other view empty forever (#309).
	 */
	private List<TrackedItem> itemsFor(int itemId)
	{
		List<TrackedItem> items = new ArrayList<>(4);
		TrackedItem tracked = trackedItems.get(itemId);
		if (tracked != null)
			items.add(tracked);

		if (previewItem != null && previewItem.getItemId() == itemId)
			items.add(previewItem);

		TrackedItem window = windowItems.get(itemId);
		if (window != null)
			items.add(window);

		TrackedItem compare = compareItems.get(itemId);
		if (compare != null)
			items.add(compare);

		return items;
	}

	/**
	 * Fetches all four history series (5m/1h/6h/24h) plus metadata for the
	 * detail view in the background, then updates stats, alch rune prices, and the
	 * detail panel on the appropriate threads.
	 *
	 * <p>The result is written to EVERY live instance of the item ({@link #itemsFor}), not just the first
	 * one found: an untracked item can be held as the panel's preview and as a Compare preview at the same
	 * time, and writing to only one leaves the other rendering a permanently empty trend, volume, and
	 * ratings block (#309).
	 */
	private void requestDetailData(int itemId)
	{
		executor.execute(() ->
		{
			List<WikiRealtimePriceClient.PricePoint> s5 = wikiPriceClient.fetchTimeseries(itemId, "5m");
			List<WikiRealtimePriceClient.PricePoint> s1h = wikiPriceClient.fetchTimeseries(itemId, "1h");
			List<WikiRealtimePriceClient.PricePoint> s6 = wikiPriceClient.fetchTimeseries(itemId, "6h");
			List<WikiRealtimePriceClient.PricePoint> s24 = wikiPriceClient.fetchTimeseries(itemId, "24h");
			clientThread.invokeLater(() ->
			{
				List<TrackedItem> targets = itemsFor(itemId);
				if (targets.isEmpty())
					return;

				for (TrackedItem tracked : targets)
				{
					tracked.setSeries5m(s5);
					tracked.setSeries1h(s1h);
					tracked.setSeries6h(s6);
					tracked.setSeries24h(s24);
					applyItemMetadata(tracked);
					recomputeWindowStats(tracked);
				}

				final long nature = runePrice(NATURE_RUNE_ID);
				final long fire = runePrice(FIRE_RUNE_ID);
				lastNatureRunePrice = nature;
				lastFireRunePrice = fire;
				SwingUtilities.invokeLater(() ->
				{
					panel.setAlchRunePrices(nature, fire);
					panel.refreshDetailData(itemId);
					DetailWindow window = detailWindows.get(itemId);
					if (window != null)
						window.refreshData();
				});

				if (compareIds.contains(itemId))
					refreshCompareWindow();

				refreshPanel();
			});
		});
	}

	/** Rebuilds an item's per-window {@link PriceStats} from its current prices (LIVE) and history series. */
	private void recomputeWindowStats(TrackedItem tracked)
	{
		Map<TimeWindow, PriceStats> stats = new EnumMap<>(TimeWindow.class);
		for (TimeWindow w : TimeWindow.values())
		{
			if (w == TimeWindow.NONE)
				continue;

			if (w == TimeWindow.LIVE)
			{
				stats.put(w, new PriceStats(tracked.getHighPrice(), tracked.getLowPrice(), tracked.getAvgPrice(), 0));
			}
			else
			{
				List<WikiRealtimePriceClient.PricePoint> series = tracked.getSeriesFor(w);
				if (series.isEmpty())
					series = tracked.getSeries5m();

				stats.put(w, WikiRealtimePriceClient.computeStats(series, w));
			}
		}

		tracked.setWindowStats(stats);
	}

	/** Copies cached GE metadata (buy limit, value, alch values) onto an item, if available. */
	private void applyItemMetadata(TrackedItem tracked)
	{
		resolveTradeable(tracked);

		WikiRealtimePriceClient.ItemMapping mapping = itemMappings.get(tracked.getItemId());
		if (mapping == null)
			return;

		tracked.setBuyLimit(mapping.getLimit());
		tracked.setGeValue(mapping.getValue());
		tracked.setHighAlch(mapping.getHighAlch());
		tracked.setLowAlch(mapping.getLowAlch());
		tracked.setMetadataLoaded(true);
	}

	/** @return a price for a rune (for alch calc): the tracked average if present, else the GE price. */
	private long runePrice(int itemId)
	{
		TrackedItem tracked = trackedItems.get(itemId);
		if (tracked != null && tracked.getAvgPrice() > 0)
			return tracked.getAvgPrice();

		return Math.max(0, itemManager.getItemPrice(itemId));
	}

	/** Clears an item's acquisition lots (resetting its cost basis) and persists/refreshes. */
	private void clearAcquisitions(int itemId)
	{
		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null)
				return;

			tracked.getAcquisitions().clear();
			persistTrackedItems();
			refreshPanel();
		});
	}

	/** Fetches the latest prices for all items in the background, then applies them on the client thread. */
	private void refreshGePrices()
	{
		executor.execute(() ->
		{
			Map<Integer, WikiRealtimePriceClient.ItemPrices> all = wikiPriceClient.fetchAll();

			clientThread.invokeLater(() -> applyGePrices(all));
		});
	}

	/**
	 * Applies freshly fetched prices to every tracked item: records per-side
	 * deltas against the previous values, updates the LIVE window stats, seeds a
	 * cost basis on first successful price if one wasn't set, then re-evaluates
	 * notifications and refreshes the panel (including the open detail view).
	 * A failed (empty) fetch only triggers a plain refresh.
	 */
	private void applyGePrices(Map<Integer, WikiRealtimePriceClient.ItemPrices> all)
	{
		boolean fetchFailed = all.isEmpty();

		for (TrackedItem item : trackedItems.values())
		{
			WikiRealtimePriceClient.ItemPrices prices = all.get(item.getItemId());
			if (prices != null)
			{
				applyLivePrices(item, prices);

				if (!item.isCostBasisInitialized())
				{
					if (item.getQuantity() > 0 && item.getAcquisitions().isEmpty())
						ledger.addOpenAcquisition(item, item.getQuantity(), ledger.fallbackPrice(item),
								AcquisitionSource.UNKNOWN);

					item.setCostBasisInitialized(true);
					persistTrackedItems();
				}
			}
			else if (!item.hasPrices() && item.isTradeable() && mappingsLoaded)
				item.setPriceLoadFailed(true);
		}

		if (previewItem != null)
		{
			WikiRealtimePriceClient.ItemPrices prices = all.get(previewItem.getItemId());
			if (prices != null)
				applyLivePrices(previewItem, prices);
			else if (!previewItem.hasPrices() && previewItem.isTradeable() && mappingsLoaded)
				previewItem.setPriceLoadFailed(true);
		}

		for (TrackedItem windowItem : windowItems.values())
		{
			if (trackedItems.containsKey(windowItem.getItemId()))
				continue;

			WikiRealtimePriceClient.ItemPrices prices = all.get(windowItem.getItemId());
			if (prices != null)
				applyLivePrices(windowItem, prices);
			else if (!windowItem.hasPrices() && windowItem.isTradeable() && mappingsLoaded)
				windowItem.setPriceLoadFailed(true);
		}

		for (TrackedItem compareItem : compareItems.values())
		{
			if (trackedItems.containsKey(compareItem.getItemId())
					|| windowItems.containsKey(compareItem.getItemId()))
				continue;

			WikiRealtimePriceClient.ItemPrices prices = all.get(compareItem.getItemId());
			if (prices != null)
				applyLivePrices(compareItem, prices);
			else if (!compareItem.hasPrices() && compareItem.isTradeable() && mappingsLoaded)
				compareItem.setPriceLoadFailed(true);
		}

		if (fetchFailed)
		{
			refreshPanel();
			return;
		}

		lastPriceRefresh = Instant.now();
		if (lastPriceCacheSave == null
				|| Duration.between(lastPriceCacheSave, Instant.now()).compareTo(PRICE_CACHE_SAVE_INTERVAL) >= 0)
			persistPriceCache();

		recordPortfolioSnapshot();

		evaluateNotifications();
		refreshPanel(true);
		SwingUtilities.invokeLater(this::refreshDetailWindows);

		final Set<Integer> detailIds = new HashSet<>(windowItems.keySet());
		detailIds.add(panel.getDetailItemId());
		detailIds.addAll(compareIds);
		for (TrackedItem item : trackedItems.values())
		{
			if (item.isTradeable() && item.hasPrices())
			{
				if (detailIds.contains(item.getItemId()))
					requestDetailData(item.getItemId());
				else
					requestSeries(item.getItemId(), false);
			}
		}

		if (previewItem != null && detailIds.contains(previewItem.getItemId())
				&& previewItem.isTradeable() && previewItem.hasPrices())
			requestDetailData(previewItem.getItemId());

		for (TrackedItem windowItem : windowItems.values())
		{
			if (!trackedItems.containsKey(windowItem.getItemId())
					&& windowItem.isTradeable() && windowItem.hasPrices())
				requestDetailData(windowItem.getItemId());
		}

		for (TrackedItem compareItem : compareItems.values())
		{
			if (!trackedItems.containsKey(compareItem.getItemId())
					&& !windowItems.containsKey(compareItem.getItemId())
					&& compareItem.isTradeable() && compareItem.hasPrices())
				requestDetailData(compareItem.getItemId());
		}
	}

	/**
	 * Writes every priced tracked item's current prices to the RS profile config.
	 * Called throttled from refreshes and unconditionally at shutdown.
	 */
	private void persistPriceCache()
	{
		Map<Integer, StockpilePersistence.CachedPrice> cache = new HashMap<>();
		for (TrackedItem item : trackedItems.values())
		{
			if (!item.hasPrices())
				continue;

			StockpilePersistence.CachedPrice p = new StockpilePersistence.CachedPrice();
			p.high = item.getHighPrice();
			p.low = item.getLowPrice();
			p.avg = item.getAvgPrice();
			p.highTime = item.getLatestHighTime();
			p.lowTime = item.getLatestLowTime();
			cache.put(item.getItemId(), p);
		}

		if (cache.isEmpty())
			return;

		lastPriceCacheSave = Instant.now();
		persistence.savePriceCache(cache);
	}

	/**
	 * Hydrates tracked items from the persisted price cache so the panel shows
	 * last-known values (dimmed by the existing staleness treatment once their trade
	 * times age past the threshold) instead of placeholders. Live fetches simply
	 * overwrite these; items that already have prices are never touched. Runs on the
	 * client thread after the persisted items have been restored — enqueued from both
	 * initialization paths, since at startUp on the login screen the RS-profile config
	 * isn't available yet and only the {@code LOGGED_IN} load can hydrate.
	 */
	private void hydratePriceCache()
	{
		Map<Integer, StockpilePersistence.CachedPrice> cache = persistence.loadPriceCache();
		if (cache.isEmpty())
			return;

		boolean hydrated = false;
		for (Map.Entry<Integer, StockpilePersistence.CachedPrice> entry : cache.entrySet())
		{
			TrackedItem item = trackedItems.get(entry.getKey());
			if (item == null || item.hasPrices())
				continue;

			StockpilePersistence.CachedPrice p = entry.getValue();
			item.setHighPrice(p.high);
			item.setLowPrice(p.low);
			item.setAvgPrice(p.avg);
			item.setLatestHighTime(p.highTime);
			item.setLatestLowTime(p.lowTime);
			item.setPriceCacheHydrated(true);
			hydrated = true;
		}

		if (hydrated)
			refreshPanel();
	}

	/**
	 * Applies a freshly fetched price set to an item: records per-side deltas, updates
	 * current prices, and refreshes its LIVE window stats.
	 */
	private void applyLivePrices(TrackedItem item, WikiRealtimePriceClient.ItemPrices prices)
	{
		if (item.hasPrices())
		{
			item.setHighDelta(Long.compare(prices.getHigh(), item.getHighPrice()));
			item.setLowDelta(Long.compare(prices.getLow(), item.getLowPrice()));
			item.setAvgDelta(Long.compare(prices.avg(), item.getAvgPrice()));
			item.setPrevHighPrice(item.getHighPrice());
			item.setPrevLowPrice(item.getLowPrice());
			item.setPrevAvgPrice(item.getAvgPrice());
			item.setHasDeltas(true);
		}

		item.setHighPrice(prices.getHigh());
		item.setLowPrice(prices.getLow());
		item.setAvgPrice(prices.avg());
		item.setLatestHighTime(prices.getHighTime());
		item.setLatestLowTime(prices.getLowTime());
		item.setPriceCacheHydrated(false);
		item.setPriceLoadFailed(false);
		item.getWindowStats().put(TimeWindow.LIVE,
				new PriceStats(prices.getHigh(), prices.getLow(), prices.avg(), 0));
	}

	/**
	 * Reacts to this plugin's config changes: resolves detail-section slot
	 * conflicts, reschedules the refresh when the interval changes, and otherwise
	 * just repaints the panel. Ignores other plugins' groups.
	 */
	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!StockpileConfig.GROUP.equals(event.getGroup()))
			return;

		if (SECTION_SLOT_KEYS.contains(event.getKey()))
		{
			swapConflictingSection(event);
			refreshPanel();
			return;
		}

		switch (event.getKey())
		{
			case StockpileConfig.KEY_TRACKED_ITEMS:
				return;
			case StockpileConfig.KEY_PRICE_REFRESH_SECONDS:
				scheduleRefresh();
				return;
			case StockpileConfig.KEY_SCREEN_OVERLAY_ON_TOP:
				rebucketScreenOverlays();
				return;
			default:
				refreshPanel();
		}
	}

	/** Removes and re-adds the screen overlays so the manager re-buckets them into their (config-driven) layer. */
	private void rebucketScreenOverlays()
	{
		screenOverlays.forEach(overlayManager::remove);
		screenOverlays.forEach(overlayManager::add);
	}

	private static final Set<String> SECTION_SLOT_KEYS = Set.of(
			StockpileConfig.KEY_SHOW_ITEM_VALUES,
			StockpileConfig.KEY_SHOW_COLLECTION_VALUES,
			StockpileConfig.KEY_SHOW_MARKET_INFO,
			StockpileConfig.KEY_SHOW_PRICE_OVERVIEW,
			StockpileConfig.KEY_SHOW_PRICE_GRAPH,
			StockpileConfig.KEY_SHOW_VOLUME_GRAPH,
			StockpileConfig.KEY_SHOW_ALCH_INFO,
			StockpileConfig.KEY_SHOW_NOTIFICATIONS,
			StockpileConfig.KEY_SHOW_ITEM_LOG,
			StockpileConfig.KEY_SHOW_LINKS);

	/**
	 * Keeps detail-section slots unique: when a section is moved to a slot already
	 * occupied by another, the other section is swapped into the vacated slot.
	 */
	private void swapConflictingSection(ConfigChanged event)
	{
		SectionSlot newSlot;
		SectionSlot oldSlot;
		try
		{
			newSlot = SectionSlot.valueOf(event.getNewValue());
			oldSlot = SectionSlot.valueOf(event.getOldValue());
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			return;
		}

		if (newSlot == SectionSlot.NONE || newSlot == oldSlot)
			return;

		for (String key : SECTION_SLOT_KEYS)
		{
			if (key.equals(event.getKey()))
				continue;

			SectionSlot other = configManager.getConfiguration(
					StockpileConfig.GROUP, key, SectionSlot.class);
			if (other == newSlot)
			{
				configManager.setConfiguration(StockpileConfig.GROUP, key, oldSlot);
				return;
			}
		}
	}

	/**
	 * Claims an upcoming High/Low Alchemy disposal (#68): casting either spell on a
	 * tracked item registers an {@link AcquisitionSource#ALCHEMY} claim for one unit
	 * at the coins the cast actually yields — the item's cached high/low alch value —
	 * so the lot closes at the real proceeds instead of the current average. Casts on
	 * items with no cached alch value stay unclaimed and take the unknown-source path.
	 */
	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		String target = event.getMenuTarget();
		if (target == null || event.getItemId() <= 0)
			return;

		if (POUCH_FILL_OPTION.equals(event.getMenuOption()) && isPouchTarget(target))
		{
			ledger.signalPouchFill();
			return;
		}

		if (POTION_EMPTY_OPTION.equals(event.getMenuOption()) && isDosePotion(event.getItemId()))
		{
			ledger.signalPotionDiscard();
			return;
		}

		if (LOOT_SACK_OPTION.equals(event.getMenuOption())
				&& target.toLowerCase().contains(LOOT_SACK_TARGET))
		{
			rewardContainerTick = client.getTickCount();
			return;
		}

		boolean high = target.contains("High Level Alchemy");
		if (!high && !target.contains("Low Level Alchemy"))
			return;

		int canonicalId = itemManager.canonicalize(event.getItemId());
		TrackedItem tracked = trackedItems.get(canonicalId);
		if (tracked == null)
			return;

		long alchValue = resolveAlchValue(tracked, canonicalId, high);
		if (alchValue <= 0)
			return;

		ledger.claim(AcquisitionSource.ALCHEMY, canonicalId, 1, alchValue, client.getTickCount());
	}

	/**
	 * Resolves an item's alch value with a client-cache fallback (#238): prefers the
	 * cached wiki value on the tracked item, and when that has not loaded yet reads the
	 * item composition — {@link net.runelite.api.ItemComposition#getHaPrice()} for high
	 * alch, and the store value's 40% for low alch — so the
	 * {@link AcquisitionSource#ALCHEMY} claim is always registered regardless of whether
	 * the wiki mapping or the item's price series has been fetched this session.
	 */
	private long resolveAlchValue(TrackedItem tracked, int canonicalId, boolean high)
	{
		long cached = high ? tracked.getHighAlch() : tracked.getLowAlch();
		if (cached > 0)
			return cached;

		var composition = itemManager.getItemComposition(canonicalId);
		return high ? composition.getHaPrice() : Math.round(composition.getPrice() * 0.4);
	}

	/**
	 * Adds Stockpile right-click options to item menu entries, when enabled (#285). While the Context Menu Key is
	 * held, a right-click shows a single "Stockpile" entry whose submenu holds Track/Untrack, View in Stockpile,
	 * and Open in Dashboard. Runs at a negative priority so it fires after default-priority plugins (e.g. the
	 * menu-entry swapper), keeping the Stockpile entry grouped near the bottom of the menu (#292).
	 */
	@Subscribe(priority = -1f)
	public void onMenuOpened(MenuOpened event)
	{
		if (!config.contextMenuEnabled() || !contextKeyHeld)
			return;

		final MenuEntry[] entries = event.getMenuEntries();
		for (int idx = entries.length - 1; idx >= 0; --idx)
		{
			final MenuEntry entry = entries[idx];
			int itemId = getItemIdFromMenuEntry(entry);
			if (itemId <= 0)
				continue;

			final int canonicalId = itemManager.canonicalize(itemId);
			final boolean tracked = trackedItems.containsKey(canonicalId);
			addStockpileMenuSection(canonicalId, tracked);
			return;
		}
	}

	/**
	 * Adds the Stockpile context-menu section (#285) when the Context Menu Key is held: a single "Stockpile"
	 * parent entry whose submenu holds the enabled options (Track/Untrack, View in Stockpile, Open in Dashboard).
	 * The children are added so they read top-to-bottom in that order; each option is individually toggleable in
	 * the config.
	 */
	private void addStockpileMenuSection(int canonicalId, boolean tracked)
	{
		if (!config.contextMenuTrack() && !config.contextMenuView() && !config.contextMenuDashboard()
				&& !config.contextMenuCompare())
			return;

		final Menu submenu = client.getMenu()
				.createMenuEntry(1)
				.setOption("Stockpile")
				.setType(MenuAction.RUNELITE)
				.createSubMenu();

		if (config.contextMenuTrack())
		{
			submenu.createMenuEntry(0)
					.setOption(tracked ? "Stop Tracking" : "Track")
					.setType(MenuAction.RUNELITE)
					.onClick(e -> toggleTracked(canonicalId, tracked));
		}

		if (config.contextMenuView())
		{
			submenu.createMenuEntry(0)
					.setOption("View in Stockpile")
					.setType(MenuAction.RUNELITE)
					.onClick(e -> viewInStockpile(canonicalId));
		}

		if (config.contextMenuDashboard())
		{
			submenu.createMenuEntry(0)
					.setOption("Open in Dashboard")
					.setType(MenuAction.RUNELITE)
					.onClick(e -> SwingUtilities.invokeLater(() -> popOutDetail(canonicalId)));
		}

		if (config.contextMenuCompare())
		{
			submenu.createMenuEntry(0)
					.setOption("Add to Compare")
					.setType(MenuAction.RUNELITE)
					.onClick(e -> addToCompare(canonicalId));
		}
	}

	/** Tracks or untracks {@code canonicalId} from a right-click menu action. */
	private void toggleTracked(int canonicalId, boolean tracked)
	{
		if (tracked)
			removeTrackedItem(canonicalId);
		else
			addTrackedItem(canonicalId);
	}

	/**
	 * Opens {@code itemId}'s detailed view in the sidebar panel (a preview when untracked) and focuses the
	 * Stockpile panel &mdash; the "View in Stockpile" menu action (#285).
	 */
	private void viewInStockpile(int itemId)
	{
		if (itemId <= 0)
			return;

		int canonicalId = itemManager.canonicalize(itemId);
		if (trackedItems.containsKey(canonicalId))
			SwingUtilities.invokeLater(() -> panel.openTrackedDetail(canonicalId));
		else
			previewItem(canonicalId);

		SwingUtilities.invokeLater(() -> clientToolbar.openPanel(navButton));
	}

	/** @return the item id behind a menu entry (ground item or inventory/bank widget), or -1 if none. */
	private int getItemIdFromMenuEntry(MenuEntry entry)
	{
		switch (entry.getType())
		{
			case GROUND_ITEM_FIRST_OPTION:
			case GROUND_ITEM_SECOND_OPTION:
			case GROUND_ITEM_THIRD_OPTION:
			case GROUND_ITEM_FOURTH_OPTION:
			case GROUND_ITEM_FIFTH_OPTION:
			case EXAMINE_ITEM_GROUND:
				return entry.getIdentifier();
			default:
				break;
		}

		Widget w = entry.getWidget();
		if (w == null)
			return -1;

		int interfaceId = WidgetUtil.componentToInterface(w.getId());
		if (interfaceId == InterfaceID.INVENTORY
				|| interfaceId == InterfaceID.BANKMAIN
				|| interfaceId == InterfaceID.BANKSIDE
				|| interfaceId == InterfaceID.SHOPMAIN
				|| interfaceId == InterfaceID.SHOPSIDE)
			return w.getItemId();

		return -1;
	}

	/**
	 * Tracks per-container item counts as inventory/bank/etc. change, accumulating
	 * the deltas to apply on the next client tick. The first sight of a container
	 * after login only seeds a baseline (no deltas); seeing the bank can trigger a
	 * full reconcile for auto-add.
	 */
	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		int containerId = event.getContainerId();
		if (containerId == InventoryID.TRADEOFFER || containerId == TRADE_OTHER_CONTAINER)
		{
			boolean mine = containerId == InventoryID.TRADEOFFER;
			captureTradeOffer(mine ? myTradeOffer : theirTradeOffer, event.getItemContainer(), mine);
			return;
		}

		if (REWARD_CONTAINERS.contains(containerId))
			rewardContainerTick = client.getTickCount();

		if (!TRACKED_CONTAINERS.contains(containerId))
			return;

		boolean firstSync = seenContainersSinceLogin.add(containerId);

		Map<Integer, Integer> oldCounts = containerCounts.getOrDefault(containerId, Collections.emptyMap());
		Map<Integer, Integer> newCounts = new HashMap<>();
		ItemContainer container = event.getItemContainer();
		if (container != null)
		{
			for (Item item : container.getItems())
			{
				int canonical = canonicalCountId(item.getId());
				if (canonical > 0)
					newCounts.merge(canonical, item.getQuantity(), Integer::sum);
			}
		}

		if (!firstSync)
		{
			ItemDeltas.forEachDelta(oldCounts, newCounts, (itemId, delta) ->
					pendingItemDeltas.merge(itemId, delta, Integer::sum));

			pendingQuantitySync = true;

			if (shopOpen && containerId == InventoryID.INV)
				registerShopClaims(oldCounts, newCounts);
		}

		containerCounts.put(containerId, newCounts);

		if (firstSync && containerId == InventoryID.BANK && config.autoAddItems())
			reconcileAllQuantities();

		refreshPanel();
	}

	/**
	 * Per-tick work: flushes any pending quantity sync, evaluates notifications,
	 * and (when ground highlighting is on) reorders tracked items' "Take" menu
	 * entries to the bottom so they don't get in the way of normal actions.
	 */
	@Subscribe
	public void onClientTick(ClientTick event)
	{
		ledger.expireClaims(client.getTickCount());
		correlateGroundActivity();
		if (runePouchDirty)
		{
			runePouchDirty = false;
			flushRunePouchDelta();
		}

		if (pendingQuantitySync)
		{
			pendingQuantitySync = false;
			correlateProcessing();
			correlateDecant();
			correlateCombine();
			correlateGathering();
			correlateReward();
			correlateThieving();
			syncQuantitiesFromContainers();
		}

		ledger.flushPendingRealize();

		evaluateNotifications();

		if (!config.highlightTrackedItems().ground() || client.isMenuOpen())
			return;

		final MenuEntry[] entries = client.getMenu().getMenuEntries();
		final List<MenuEntry> normal = new ArrayList<>(entries.length);
		final List<MenuEntry> trackedTakes = new ArrayList<>();

		for (MenuEntry entry : entries)
		{
			if (entry.getType() == MenuAction.GROUND_ITEM_THIRD_OPTION
					&& isTracked(itemManager.canonicalize(entry.getIdentifier())))
				trackedTakes.add(entry);
			else
				normal.add(entry);
		}

		if (trackedTakes.isEmpty())
			return;

		normal.addAll(trackedTakes);
		client.getMenu().setMenuEntries(normal.toArray(new MenuEntry[0]));
	}

	/**
	 * Grand Exchange integration: each tick, detects the item on the open offer setup/details
	 * screen and, per {@link StockpileConfig#geIntegration()}, either auto-opens it in Stockpile
	 * or injects a "View in Stockpile" button. Only acts when the shown item changes.
	 */
	@Subscribe
	public void onGameTick(GameTick event)
	{
		ledger.expireSuspensions();
		ledger.closeVanishedGraveLosses();

		GeIntegrationMode mode = config.geIntegration();
		boolean wantButton = mode == GeIntegrationMode.BUTTON || mode == GeIntegrationMode.BOTH;
		boolean wantPrices = config.geShowMarketPrices();
		boolean wantTrack = config.geShowTrackButton();
		if (!wantButton)
			hideGeButton();

		if (!wantTrack)
			hideGeTrackButton();

		if (mode == GeIntegrationMode.OFF && !wantPrices && !wantTrack)
		{
			currentGeItem = -1;
			return;
		}

		int item = currentGeOfferItem();
		if (item != currentGeItem)
		{
			currentGeItem = item;
			hideGeButton();
			hideGeTrackButton();
			geLineItem = -1;
			geLineHigh = 0;
			geLineLow = 0;
			geLineSource = null;

			if (item > 0 && wantPrices)
				requestGeLinePrices(item);

			if (item > 0 && (mode == GeIntegrationMode.AUTO || mode == GeIntegrationMode.BOTH))
				openGeItemInStockpile(item);
		}

		if (wantButton && item > 0 && geButton == null)
			injectGeButton();

		if (wantTrack && item > 0 && geTrackButton == null)
			injectGeTrackButton();

		if (wantPrices && item > 0)
			applyGeHighLowLine();
	}

	/** Hides and forgets the injected GE button, if one is currently on the offer interface. */
	private void hideGeButton()
	{
		if (geButton == null)
			return;

		geButton.setHidden(true);
		geButton = null;
	}

	/** Hides and forgets the injected Track/Untrack button, if one is currently on the offer interface (#139). */
	private void hideGeTrackButton()
	{
		if (geTrackButton == null)
			return;

		geTrackButton.setHidden(true);
		geTrackButton = null;
		if (geTrackLabel != null)
		{
			geTrackLabel.setHidden(true);
			geTrackLabel = null;
		}
	}

	/** Forces the GE buttons to be re-injected against a freshly (re)built offer interface. */
	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == InterfaceID.GE_OFFERS)
		{
			geButton = null;
			geTrackButton = null;
			geTrackLabel = null;
		}

		if (event.getGroupId() == InterfaceID.SHOPMAIN)
			shopOpen = true;
	}

	/** Clears GE-integration state when the offer interface closes, and shop state for #67. */
	@Subscribe
	public void onWidgetClosed(WidgetClosed event)
	{
		if (event.getGroupId() == InterfaceID.GE_OFFERS)
		{
			currentGeItem = -1;
			geButton = null;
			geTrackButton = null;
			geTrackLabel = null;
		}

		if (event.getGroupId() == InterfaceID.SHOPMAIN)
			shopOpen = false;
	}

	/** @return the item shown on the visible GE offer setup/details screen, or -1 when none is open. */
	private int currentGeOfferItem()
	{
		int item = itemInGeContainer(InterfaceID.GeOffers.SETUP);
		if (item > 0)
			return item;

		return itemInGeContainer(InterfaceID.GeOffers.DETAILS);
	}

	/** @return the first item id found in the given GE container's subtree, or -1 when hidden/absent. */
	private int itemInGeContainer(int componentId)
	{
		Widget container = client.getWidget(componentId);
		if (container == null || container.isHidden())
			return -1;

		return scanForItem(container);
	}

	/** Recursively searches a widget subtree for the first child holding a real item id. */
	private int scanForItem(Widget widget)
	{
		if (widget == null)
			return -1;

		if (widget.getItemId() > 0 && isRealItem(widget.getItemId()))
			return widget.getItemId();

		Widget[][] groups = {widget.getStaticChildren(), widget.getDynamicChildren(), widget.getNestedChildren()};
		for (Widget[] group : groups)
		{
			if (group == null)
				continue;

			for (Widget child : group)
			{
				int id = scanForItem(child);
				if (id > 0)
					return id;
			}
		}

		return -1;
	}

	/**
	 * @return whether {@code itemId} resolves to a real, defined item. Empty widget
	 * slots are backed by placeholder items (e.g. id 6512) whose composition name is
	 * the literal string "null"; those must not open a preview.
	 */
	private boolean isRealItem(int itemId)
	{
		String name = itemManager.getItemComposition(itemId).getName();
		return name != null && !name.isEmpty() && !"null".equalsIgnoreCase(name);
	}

	/** Opens the item in Stockpile's view-only preview, switching to/focusing the panel when configured. */
	private void openGeItemInStockpile(int itemId)
	{
		if (itemId <= 0)
			return;

		int canonicalId = itemManager.canonicalize(itemId);
		if (trackedItems.containsKey(canonicalId))
			SwingUtilities.invokeLater(() -> panel.openTrackedDetail(canonicalId));
		else
			previewItem(canonicalId);

		if (config.geFocusPanel())
			SwingUtilities.invokeLater(() -> clientToolbar.openPanel(navButton));
	}

	/**
	 * Registers the bundled Stockpile icon as a custom sprite override so it can be drawn on the
	 * injected GE button (#140). Scaled to {@link #GE_ICON_SIZE} for a crisp render at button size.
	 * A no-op when sprite overrides are unavailable (e.g. before the client is ready).
	 */
	private void registerGeButtonSprite(BufferedImage icon)
	{
		Map<Integer, SpritePixels> overrides = client.getSpriteOverrides();
		if (overrides == null || icon == null)
			return;

		BufferedImage scaled = ImageUtil.resizeImage(icon, GE_ICON_SIZE, GE_ICON_SIZE);
		overrides.put(STOCKPILE_GE_SPRITE_ID, ImageUtil.getImageSpritePixels(scaled, client));
	}

	/** Removes the Stockpile GE-button sprite override on shutdown (#140). */
	private void unregisterGeButtonSprite()
	{
		Map<Integer, SpritePixels> overrides = client.getSpriteOverrides();
		if (overrides != null)
			overrides.remove(STOCKPILE_GE_SPRITE_ID);
	}

	/**
	 * Injects the Stockpile icon as a "View in Stockpile" button onto the visible GE offer container
	 * (#140). The icon-only graphic sits where the old text link did; the "View in Stockpile" text now
	 * lives on the hover action/tooltip. Clicking opens the offer's item in Stockpile's detail view;
	 * hover brightens the icon to full opacity.
	 */
	private void injectGeButton()
	{
		Widget container = client.getWidget(InterfaceID.GeOffers.SETUP);
		if (container == null || container.isHidden())
			container = client.getWidget(InterfaceID.GeOffers.DETAILS);

		if (container == null || container.isHidden())
			return;

		Widget button = container.createChild(-1, WidgetType.GRAPHIC);
		button.setSpriteId(STOCKPILE_GE_SPRITE_ID);
		button.setOpacity(60);
		button.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		button.setOriginalX(10);
		button.setOriginalY(8);
		button.setOriginalWidth(GE_ICON_SIZE);
		button.setOriginalHeight(GE_ICON_SIZE);
		button.setHasListener(true);
		button.setAction(0, "View in Stockpile");
		button.setOnOpListener((JavaScriptCallback) e -> openGeItemInStockpile(currentGeItem));
		button.setOnMouseOverListener((JavaScriptCallback) e -> button.setOpacity(0));
		button.setOnMouseLeaveListener((JavaScriptCallback) e -> button.setOpacity(60));
		button.revalidate();

		geButton = button;
	}

	/**
	 * Injects a Track/Untrack button in the GE window's title bar, immediately left of the close (X)
	 * button (#139): a muted-orange outline box framing bold Track/Untrack text (the "Grand Exchange"
	 * header font/weight) whose colour reflects the tracked state. The close button is located at
	 * runtime so the button sits in the same section and row as the X, not in the offer content.
	 * The text box is inset 3px inside the border so the label clears the outline.
	 */
	private void injectGeTrackButton()
	{
		Widget close = findGeCloseButton();
		if (close == null)
			return;

		Widget parent = close.getParent();
		if (parent == null)
			return;

		int width = 58;
		int boxHeight = 18;
		int gap = 6;
		int closeWidth = close.getWidth() > 0 ? close.getWidth() : close.getOriginalWidth();
		int closeHeight = close.getHeight() > 0 ? close.getHeight() : 21;
		boolean fromRight = close.getXPositionMode() == WidgetPositionMode.ABSOLUTE_RIGHT;
		int borderX = fromRight
				? close.getOriginalX() + closeWidth + gap
				: Math.max(0, close.getOriginalX() - width - gap);
		int y = close.getOriginalY() + Math.max(0, (closeHeight - boxHeight) / 2);
		int xMode = close.getXPositionMode();
		int yMode = close.getYPositionMode();

		Widget border = parent.createChild(-1, WidgetType.RECTANGLE);
		border.setFilled(false);
		border.setTextColor(GE_TRACK_BORDER);
		border.setOpacity(0);
		border.setXPositionMode(xMode);
		border.setYPositionMode(yMode);
		border.setOriginalX(borderX);
		border.setOriginalY(y);
		border.setOriginalWidth(width);
		border.setOriginalHeight(boxHeight);
		border.setHasListener(true);
		border.setOnOpListener((JavaScriptCallback) e -> toggleGeTracking());
		border.revalidate();

		Widget label = parent.createChild(-1, WidgetType.TEXT);
		label.setFontId(FontID.BOLD_12);
		label.setTextShadowed(true);
		label.setXPositionMode(xMode);
		label.setYPositionMode(yMode);
		label.setOriginalX(borderX + 3);
		label.setOriginalY(y);
		label.setOriginalWidth(width - 6);
		label.setOriginalHeight(boxHeight);
		label.setXTextAlignment(WidgetTextAlignment.CENTER);
		label.setYTextAlignment(WidgetTextAlignment.CENTER);
		label.setHasListener(true);
		label.setOnOpListener((JavaScriptCallback) e -> toggleGeTracking());
		label.setOnMouseOverListener((JavaScriptCallback) e -> label.setTextColor(0xffffff));
		label.setOnMouseLeaveListener((JavaScriptCallback) e -> applyGeTrackLabel());
		label.revalidate();

		geTrackButton = border;
		geTrackLabel = label;
		applyGeTrackLabel();
	}

	/**
	 * Locates the GE window's close (X) button by walking to the top-level ancestor of the open offer
	 * container and searching its subtree for a visible widget with a "Close" action (#139). Confined
	 * to the GE window's toplevel so it doesn't match some other interface's close button.
	 */
	private Widget findGeCloseButton()
	{
		Widget container = client.getWidget(InterfaceID.GeOffers.SETUP);
		if (container == null || container.isHidden())
			container = client.getWidget(InterfaceID.GeOffers.DETAILS);

		if (container == null || container.isHidden())
			return null;

		Widget root = container;
		while (root.getParent() != null)
			root = root.getParent();

		return scanForCloseAction(root);
	}

	/** Recursively searches a widget subtree for the first visible widget carrying a "Close" action. */
	private Widget scanForCloseAction(Widget widget)
	{
		if (widget == null || widget.isHidden())
			return null;

		String[] actions = widget.getActions();
		if (actions != null)
		{
			for (String action : actions)
			{
				if ("Close".equalsIgnoreCase(action))
					return widget;
			}
		}

		Widget[][] groups = {widget.getStaticChildren(), widget.getDynamicChildren(), widget.getNestedChildren()};
		for (Widget[] group : groups)
		{
			if (group == null)
				continue;

			for (Widget child : group)
			{
				Widget found = scanForCloseAction(child);
				if (found != null)
					return found;
			}
		}

		return null;
	}

	/** Sets the Track/Untrack text, action, and resting colour (green/red) from the offer's tracked state. */
	private void applyGeTrackLabel()
	{
		if (geTrackLabel == null || currentGeItem <= 0)
			return;

		int canonicalId = itemManager.canonicalize(currentGeItem);
		boolean tracked = trackedItems.containsKey(canonicalId);
		String label = tracked ? "Untrack" : "Track";
		geTrackLabel.setText(label);
		geTrackLabel.setTextColor(GE_TITLE_ORANGE);
		geTrackLabel.setAction(0, label);
		if (geTrackButton != null)
			geTrackButton.setAction(0, label);
	}

	/**
	 * Toggles tracking of the open GE offer's item (#139). The add/remove is deferred to the client
	 * thread, so the label refresh is enqueued after it — otherwise it would read the pre-toggle state
	 * and only correct itself on the next mouse-leave.
	 */
	private void toggleGeTracking()
	{
		if (currentGeItem <= 0)
			return;

		int canonicalId = itemManager.canonicalize(currentGeItem);
		if (trackedItems.containsKey(canonicalId))
			removeTrackedItem(canonicalId);
		else
			addTrackedItem(canonicalId);

		clientThread.invokeLater(this::applyGeTrackLabel);
	}

	/**
	 * Swaps the "Actively traded price" text inside the open GE offer's info block (the single
	 * {@code SETUP_DESC}/{@code DETAILS_DESC} text widget) for one compact market line — High, Low
	 * and Avg together — in place, so the line count never changes and nothing else moves (#142).
	 * Re-applied each tick so the game's own redraw does not win; idempotent because once the native
	 * text is gone the rewrite is skipped. No-op until the shown item's data has been fetched and priced.
	 */
	private void applyGeHighLowLine()
	{
		if (geLineItem != currentGeItem || (geLineHigh <= 0 && geLineLow <= 0))
			return;

		Widget desc = client.getWidget(InterfaceID.GeOffers.SETUP_DESC);
		if (desc == null || desc.isHidden())
			desc = client.getWidget(InterfaceID.GeOffers.DETAILS_DESC);

		if (desc == null || desc.isHidden())
			return;

		String text = desc.getText();
		String rebuilt = injectPriceLines(text);
		if (rebuilt.equals(text))
			return;

		desc.setText(rebuilt);
		desc.setOriginalHeight(GE_DESC_HEIGHT);
		desc.revalidate();
	}

	/**
	 * Swaps the "Actively traded price: N" segment of the GE info-block for the two market lines,
	 * always on their own rows: a leading "Buy limit: N /" that RuneLite inlines on buy offers is
	 * split off onto its own line, and any trailing convenience-fee line is kept. Returns the text
	 * unchanged when there is no native segment to replace, leaving an already-rewritten block
	 * alone (#142).
	 *
	 * @param desc the current info-block text (may be null)
	 * @return the rewritten text, or the original when nothing was replaced
	 */
	private String injectPriceLines(String desc)
	{
		if (desc == null || !desc.contains("Actively traded price"))
			return desc;

		String replacement = Matcher.quoteReplacement("<br>" + priceLines());
		return GE_ACTIVE_PRICE_LINE.matcher(desc).replaceAll(replacement);
	}

	/**
	 * @return one market row — High and Low together — coloured per side and prefixed with the
	 *         resolved source ({@code 5m}/{@code 1h}/{@code Latest}) (#142).
	 */
	private String priceLines()
	{
		String prefix = geLineSource == null ? "" : geLineSource + " ";
		return prefix + "High: " + colourGp(geLineHigh, "64dc64")
				+ "  Low: " + colourGp(geLineLow, "dc6464");
	}

	/**
	 * Resolves the open GE offer item's market prices in the background and caches them for the
	 * info-block line, overwriting it in place once they arrive (#142). Falls back down a chain:
	 * the latest priced 5m sample, then the latest priced 1h sample, then the item's latest instant
	 * high/low; whichever lands first sets the row-label prefix (5m / 1h / Latest).
	 */
	private void requestGeLinePrices(int itemId)
	{
		int canonical = itemManager.canonicalize(itemId);
		executor.execute(() ->
		{
			String source = "5m";
			long[] highLow = latestSeriesHighLow(wikiPriceClient.fetchTimeseries(canonical, "5m"));
			if (highLow[0] <= 0 && highLow[1] <= 0)
			{
				source = "1h";
				highLow = latestSeriesHighLow(wikiPriceClient.fetchTimeseries(canonical, "1h"));
			}

			final String seriesSource = source;
			final long[] seriesHighLow = highLow;
			clientThread.invokeLater(() ->
			{
				if (itemId != currentGeItem)
					return;

				long high = seriesHighLow[0];
				long low = seriesHighLow[1];
				String label = seriesSource;
				if (high <= 0 && low <= 0)
				{
					TrackedItem item = lookupItem(canonical);
					if (item != null)
					{
						high = item.getHighPrice();
						low = item.getLowPrice();
						label = "Latest";
					}
				}

				geLineItem = itemId;
				geLineHigh = high;
				geLineLow = low;
				geLineSource = label;
				applyGeHighLowLine();
			});
		});
	}

	/**
	 * Scans a price series newest-first for the most recent priced average high and low,
	 * returned as {@code [high, low]} (each 0 when the series holds no priced sample) (#142).
	 *
	 * @param series the price points, oldest first (may be null or empty)
	 * @return a two-element array of the latest non-zero high and low
	 */
	static long[] latestSeriesHighLow(List<WikiRealtimePriceClient.PricePoint> series)
	{
		long high = 0;
		long low = 0;
		if (series != null)
		{
			for (int i = series.size() - 1; i >= 0 && (high == 0 || low == 0); i--)
			{
				WikiRealtimePriceClient.PricePoint point = series.get(i);
				if (high == 0 && point.getAvgHighPrice() > 0)
					high = point.getAvgHighPrice();

				if (low == 0 && point.getAvgLowPrice() > 0)
					low = point.getAvgLowPrice();
			}
		}

		return new long[]{high, low};
	}

	/** @return a full grouped {@code "1,234 gp"} in the given colour, or a muted dash when unpriced (#142). */
	private static String colourGp(long value, String colour)
	{
		if (value <= 0)
			return "<col=969696>—</col>";

		return "<col=" + colour + ">" + GpFormat.grouped(value) + " gp</col>";
	}

	/** Records a ground item and its tile so the ground overlay can outline it, buffering it for #65. */
	@Subscribe
	public void onItemSpawned(ItemSpawned event)
	{
		groundItems.put(event.getItem(), event.getTile());
		if (isTracked(itemManager.canonicalize(event.getItem().getId())))
			tickGroundSpawns.add(event);
	}

	/** Forgets a ground item once it despawns, buffering it for #65's pickup/lost-drop correlation. */
	@Subscribe
	public void onItemDespawned(ItemDespawned event)
	{
		groundItems.remove(event.getItem());
		if (myDrops.containsKey(event.getItem()) || isTracked(itemManager.canonicalize(event.getItem().getId())))
			tickGroundDespawns.add(event);
	}

	/** Buffers ground-stack quantity changes so drops onto an existing stack correlate like spawns (#65). */
	@Subscribe
	public void onItemQuantityChanged(ItemQuantityChanged event)
	{
		if (myDrops.containsKey(event.getItem()) || isTracked(itemManager.canonicalize(event.getItem().getId())))
			tickGroundQuantityChanges.add(event);
	}

	/**
	 * Correlates this tick's ground-item activity with the pending inventory deltas (#65):
	 * a spawn (or stack increase) on the player's tile matching a pending removal is our
	 * drop — its units queue for ground suspension and the {@code TileItem} is remembered;
	 * a despawn of a remembered drop with no matching pickup closes its units as lost at 0;
	 * a despawn matching a pending addition that isn't ours is a loot pickup, claimed as a
	 * {@link AcquisitionSource#GROUND} acquisition at 0. Runs before the quantity sync
	 * consumes the deltas.
	 */
	private void correlateGroundActivity()
	{
		if (tickGroundSpawns.isEmpty() && tickGroundDespawns.isEmpty() && tickGroundQuantityChanges.isEmpty())
			return;

		WorldPoint myLocation = client.getLocalPlayer() == null
				? null
				: client.getLocalPlayer().getWorldLocation();

		for (ItemSpawned spawn : tickGroundSpawns)
			correlateGroundGain(spawn.getItem(), spawn.getTile(), spawn.getItem().getQuantity(), myLocation);

		boolean groundLossClosed = false;
		for (ItemQuantityChanged change : tickGroundQuantityChanges)
		{
			int delta = change.getNewQuantity() - change.getOldQuantity();
			if (delta > 0)
				correlateGroundGain(change.getItem(), change.getTile(), delta, myLocation);
			else
				groundLossClosed |= correlateGroundTaken(change.getItem(), -delta);
		}

		for (ItemDespawned despawn : tickGroundDespawns)
			groundLossClosed |= correlateGroundTaken(despawn.getItem(), despawn.getItem().getQuantity());

		tickGroundSpawns.clear();
		tickGroundDespawns.clear();
		tickGroundQuantityChanges.clear();

		if (groundLossClosed)
		{
			persistTrackedItems();
			refreshPanel();
		}
	}

	/**
	 * Handles a ground pile gaining units: on our tile against a pending removal, it's our
	 * drop. Gated by the Source-Based Pricing toggle — when off, no new ground suspensions
	 * are taken, so a drop closes classically at the average price; drops suspended while
	 * the toggle was on still resolve through the un-suspend/lost paths.
	 */
	private void correlateGroundGain(TileItem item, Tile tile, int gained, WorldPoint myLocation)
	{
		if (!config.sourcePricing())
			return;

		if (myLocation == null || !myLocation.equals(tile.getWorldLocation()))
			return;

		int canonicalId = itemManager.canonicalize(item.getId());
		if (!isTracked(canonicalId))
			return;

		int queued = ledger.pendingGroundSuspend(canonicalId);
		int pendingRemoval = -pendingItemDeltas.getOrDefault(canonicalId, 0) - queued;
		if (pendingRemoval <= 0)
			return;

		int qty = Math.min(gained, pendingRemoval);
		ledger.queueGroundSuspend(canonicalId, qty);
		myDrops.merge(item, qty, Integer::sum);
	}

	/**
	 * Handles a ground pile losing units: a remembered drop with a matching pending
	 * addition is a re-pickup (the greedy un-suspend consumes it during the sync);
	 * with no matching addition its units close as lost at 0. An unfamiliar pile
	 * matching a pending addition is a loot pickup, claimed as {@code GROUND} at 0.
	 */
	private boolean correlateGroundTaken(TileItem item, int taken)
	{
		int canonicalId = itemManager.canonicalize(item.getId());
		Integer ours = myDrops.get(item);
		int pendingAddition = pendingItemDeltas.getOrDefault(canonicalId, 0);

		if (ours != null)
		{
			int resolved = Math.min(ours, taken);
			boolean lossClosed = false;
			if (pendingAddition > 0)
				ledger.queueGroundUnsuspend(canonicalId, Math.min(resolved, pendingAddition));
			else
				lossClosed = ledger.closeGroundLost(canonicalId, resolved);

			if (resolved >= ours)
				myDrops.remove(item);
			else
				myDrops.put(item, ours - resolved);

			return lossClosed;
		}

		if (pendingAddition > 0 && isTracked(canonicalId))
			ledger.claim(AcquisitionSource.GROUND, canonicalId, Math.min(taken, pendingAddition), 0,
					client.getTickCount());

		return false;
	}

	/** Marks the local player's death, opening the death-loss suspension window (#70). */
	@Subscribe
	public void onActorDeath(ActorDeath event)
	{
		if (event.getActor() == client.getLocalPlayer())
		{
			ledger.signalDeath();
		}
	}

	/**
	 * Marks the tick of processing-skill XP gains (recipe actions, #69), gathering-skill XP
	 * gains (#213), and Thieving XP gains (#217).
	 */
	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		Integer previous = lastSkillXp.put(event.getSkill(), event.getXp());
		if (previous == null || event.getXp() <= previous)
			return;

		if (PROCESSING_SKILLS.contains(event.getSkill()))
			processingXpTick = client.getTickCount();

		if (event.getSkill() == Skill.MAGIC)
			magicXpTick = client.getTickCount();

		if (GATHERING_SKILLS.contains(event.getSkill()))
			gatherXpTick = client.getTickCount();

		if (event.getSkill() == Skill.THIEVING)
			thievingXpTick = client.getTickCount();
	}

	/**
	 * Pairs this tick's consumed inputs with the produced output when a processing-skill
	 * XP gain identifies a recipe action (#69), transferring the summed input cost: tracked
	 * inputs contribute (and close at) their FIFO open-lot cost, untracked inputs their
	 * fallback market value, and the total is carried onto the output's new lot(s) by
	 * {@link CostBasisLedger} so their basis sums exactly to it. Multi-output ticks
	 * are unattributable and left to the fallback; tracked inputs with no tracked output
	 * close at 0. A worthless, non-tradeable output is a destroyed product and is handled
	 * without an XP signal — a burn or crush gives none — closing each tracked input as a
	 * realized loss at 0 (#144): a crushed gem tags the input {@link AcquisitionSource#CRUSHED},
	 * any other destroyed product {@link AcquisitionSource#BURNED}. When the player tracks the
	 * destroyed byproduct itself, its gain is booked at 0-cost under that same source (#172).
	 * Coins never participate.
	 *
	 * <p>Runes removed on a Magic XP tick are the cast's fuel rather than one of its ingredients,
	 * so they are claimed as {@link AcquisitionSource#CAST} at 0 and never enter the input set (#235):
	 * a superheated bar carries the ore's basis alone, and a combat spell leaves no inputs to pair.
	 * A cast that yields no item at all is not a recipe either, so its remaining inputs are left
	 * unclaimed rather than booked as processing — an alched item is sold for coins, not processed,
	 * and belongs to the {@link AcquisitionSource#ALCHEMY} claim or the fallback path.
	 */
	private void correlateProcessing()
	{
		ledger.clearProcessingOutput();
		if (!config.sourcePricing() || pendingItemDeltas.isEmpty())
			return;

		List<int[]> inputs = new ArrayList<>();
		int outputId = 0;
		int outputQty = 0;
		int outputKinds = 0;
		for (Map.Entry<Integer, Integer> entry : pendingItemDeltas.entrySet())
		{
			int itemId = entry.getKey();
			int delta = entry.getValue();
			if (itemId == ItemID.COINS || delta == 0)
				continue;

			if (delta < 0)
			{
				if (isSpellcastRune(itemId))
				{
					if (isTracked(itemId))
						ledger.claim(AcquisitionSource.CAST, itemId, -delta, 0, client.getTickCount());

					continue;
				}

				inputs.add(new int[]{itemId, -delta});
			}
			else
			{
				outputKinds++;
				outputId = itemId;
				outputQty = delta;
			}
		}

		if (inputs.isEmpty() || outputKinds > 1)
			return;

		if (outputKinds == 0 && client.getTickCount() - magicXpTick <= 1)
			return;

		if (outputKinds == 1 && isDestroyedProduct(outputId))
		{
			AcquisitionSource loss = DestroyedOutputSources.sourceFor(outputId);
			for (int[] input : inputs)
			{
				if (isAmmo(input[0]))
					continue;

				if (isTracked(input[0]))
					ledger.claim(loss, input[0], input[1], 0, client.getTickCount());
			}

			if (isTracked(outputId))
				ledger.claim(loss, outputId, outputQty, 0, client.getTickCount());

			return;
		}

		if (client.getTickCount() - processingXpTick > 1)
			return;

		pairProcessingRecipe(inputs, outputId, outputQty, outputKinds == 1 && isTracked(outputId));
	}

	/**
	 * Closes a recipe's consumed inputs under {@link AcquisitionSource#PROCESSING} at their FIFO
	 * open-lot cost and queues the summed basis in {@code pendingProcessingOutput} so the matching
	 * gain opens the produced lot carrying it. Untracked inputs contribute their fallback value.
	 * When the output is untracked there is nothing to carry the basis onto, so the inputs close at
	 * 0 and no output is queued. Shared by the XP-gated {@link #correlateProcessing} path and the
	 * XP-less combine detector {@link #correlateCombine} (#231).
	 */
	private void pairProcessingRecipe(List<int[]> inputs, int outputId, int outputQty, boolean trackedOutput)
	{
		long totalCost = 0;
		for (int[] input : inputs)
		{
			TrackedItem tracked = trackedItems.get(input[0]);
			if (tracked == null)
			{
				totalCost += untrackedInputValue(input[0]) * input[1];
				continue;
			}

			long basis = ProcessingBasis.openLotCost(tracked.getAcquisitions(), input[1]);
			totalCost += basis;
			ledger.claim(AcquisitionSource.PROCESSING, input[0], input[1],
					trackedOutput ? basis / input[1] : 0, client.getTickCount());
		}

		if (trackedOutput && outputQty > 0)
			ledger.queueProcessingOutput(outputId, totalCost);
	}

	/**
	 * Pairs a dose family's consumed lots with the doses it produces on a single XP-less tick, so
	 * cost basis follows the liquid across the item-id change rather than being realized as a sale.
	 * Groups the tick's non-coin deltas into dose families ({@link DoseFamily}) and hands each family
	 * to {@link #correlateDoseSwapFamily}, which distinguishes two cases:
	 * <ul>
	 *   <li><b>Decant</b> (#220) — consumed doses equal produced doses: a pure swap, so the combined
	 *       input basis is distributed dose-weighted ({@link DecantBasis}) onto the produced ids under
	 *       {@link AcquisitionSource#DECANT}. Up, down, and mixed-basis inputs all merge.</li>
	 *   <li><b>Consume-down</b> (#218) — a dose is drunk (consumed doses exceed produced doses, but
	 *       some remain): the <em>full</em> input basis follows onto the lower-dose id under
	 *       {@link AcquisitionSource#CONSUMED}, since using a dose realizes no profit or loss.</li>
	 * </ul>
	 * Both queue the carried basis in {@code pendingDecantOutput} / {@code pendingConsumedOutput} so the
	 * matching gain opens the output lot carrying it; both close the consumed lots at their FIFO cost
	 * (no P/L). Untracked inputs contribute their fallback value; untracked outputs drop their share.
	 * Runs after {@link #correlateProcessing} — which a processing-XP tick handles instead — and before
	 * the source detectors, whose gains skip any id already queued in {@code pendingProcessingOutput},
	 * {@code pendingDecantOutput}, or {@code pendingConsumedOutput}. Gated by the Source-Based Pricing toggle.
	 */
	private void correlateDecant()
	{
		ledger.clearDecantAndConsumedOutput();
		doseSwapClaimedIds.clear();
		ledger.resetPotionEmptied();
		if (!config.sourcePricing() || pendingItemDeltas.isEmpty()
				|| client.getTickCount() - processingXpTick <= 1)
			return;

		Map<String, List<int[]>> families = new HashMap<>();
		for (Map.Entry<Integer, Integer> entry : pendingItemDeltas.entrySet())
		{
			int itemId = entry.getKey();
			int delta = entry.getValue();
			if (itemId == ItemID.COINS || delta == 0)
				continue;

			DoseFamily.Parsed parsed = DoseFamily.parse(itemManager.getItemComposition(itemId).getName());
			if (parsed == null)
				continue;

			families.computeIfAbsent(parsed.base, k -> new ArrayList<>()).add(new int[]{itemId, delta, parsed.doses});
		}

		for (List<int[]> members : families.values())
			correlateDoseSwapFamily(members);
	}

	/**
	 * Applies the dose-family basis transfer to one family's members ({@code {id, delta, doses}}):
	 * a dose-conserving swap (consumed doses equal produced doses) is a <b>decant</b> under
	 * {@link AcquisitionSource#DECANT}; a swap that loses doses while leaving some (a dose drunk) is
	 * a <b>consume-down</b> under {@link AcquisitionSource#CONSUMED}. Anything else — no consumption,
	 * or every dose gone (a last dose drunk, left to the {@link CostBasisLedger#applyDelta} loss path) — is skipped.
	 * The consumed lots close at their FIFO cost and the summed basis is distributed onto the produced
	 * ids: dose-weighted for a decant, but in full for a consume-down since a used dose is not a loss.
	 */
	private void correlateDoseSwapFamily(List<int[]> members)
	{
		long consumedDoses = 0;
		long producedDoses = 0;
		for (int[] member : members)
			if (member[1] < 0)
				consumedDoses += (long) -member[1] * member[2];
			else
				producedDoses += (long) member[1] * member[2];

		if (consumedDoses == 0 || producedDoses == 0 || producedDoses > consumedDoses)
		{
			if (consumedDoses > 0 && producedDoses == 0)
			{
				int emptied = members.stream().filter(m -> m[1] < 0).mapToInt(m -> -m[1]).sum();
				ledger.addPotionEmptied(emptied);
			}

			return;
		}

		boolean decant = consumedDoses == producedDoses;
		AcquisitionSource source = decant ? AcquisitionSource.DECANT : AcquisitionSource.CONSUMED;
		for (int[] member : members)
			doseSwapClaimedIds.add(member[0]);

		long totalBasis = 0;
		for (int[] member : members)
		{
			if (member[1] >= 0)
				continue;

			int itemId = member[0];
			int qty = -member[1];
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null)
			{
				totalBasis += untrackedInputValue(itemId) * qty;
				continue;
			}

			long basis = ProcessingBasis.openLotCost(tracked.getAcquisitions(), qty);
			totalBasis += basis;
			ledger.claim(source, itemId, qty, basis / qty, client.getTickCount());
		}

		List<int[]> outputs = new ArrayList<>();
		for (int[] member : members)
			if (member[1] > 0)
				outputs.add(new int[]{member[0], member[1] * member[2]});

		Map<Integer, Long> shares = DecantBasis.distribute(totalBasis, outputs);
		for (Map.Entry<Integer, Long> share : shares.entrySet())
		{
			if (!isTracked(share.getKey()))
				continue;

			if (decant)
				ledger.queueDecantOutput(share.getKey(), share.getValue());
			else
				ledger.queueConsumedOutput(share.getKey(), share.getValue());
		}
	}

	/**
	 * Pairs an XP-less combine — a tick that consumes one or more ingredients and produces a single
	 * tradeable output with no skill XP and no coin movement — as {@link AcquisitionSource#PROCESSING},
	 * so the ingredients' cost basis carries onto the product instead of both sides falling to
	 * Unknown at market value (#231). Handles the class of "mix"/combine recipes that grant no XP,
	 * such as combining a Sunlight moth with Raw pyre fox meat into a Sunlight moth mix.
	 *
	 * <p>Runs after {@link #correlateDecant} and reuses its {@link #pairProcessingRecipe} basis
	 * transfer. The XP-gated {@link #correlateProcessing} already claims recipes that emit XP, and
	 * a destroyed output ({@link #isDestroyedProduct}) is claimed there before the XP gate, so both
	 * are excluded here. A dose swap (decant/consume-down) is also a no-XP single-output tick, so any
	 * id claimed by the dose-swap pass ({@code doseSwapClaimedIds}) is skipped, and a finished-potion
	 * tick — where the freed vessel is the only gain — is left to the empty-container byproduct path.
	 * The output must be tracked; an untracked product gives nothing to carry basis onto and would only
	 * risk mislabelling an unrelated inventory shuffle. Gated by the Source-Based Pricing toggle.
	 */
	private void correlateCombine()
	{
		if (!config.sourcePricing() || pendingItemDeltas.isEmpty()
				|| pendingItemDeltas.getOrDefault(ItemID.COINS, 0) != 0)
			return;

		int tick = client.getTickCount();
		if (tick - processingXpTick <= 1 || tick - magicXpTick <= 1 || tick - gatherXpTick <= 1
				|| tick - thievingXpTick <= 1 || tick - rewardContainerTick <= 1
				|| tick - ledger.potionEmptiedTick() <= 1)
			return;

		List<int[]> inputs = new ArrayList<>();
		int outputId = 0;
		int outputQty = 0;
		int outputKinds = 0;
		for (Map.Entry<Integer, Integer> entry : pendingItemDeltas.entrySet())
		{
			int itemId = entry.getKey();
			int delta = entry.getValue();
			if (itemId == ItemID.COINS || delta == 0 || doseSwapClaimedIds.contains(itemId))
				continue;

			if (delta < 0)
			{
				inputs.add(new int[]{itemId, -delta});
			}
			else
			{
				outputKinds++;
				outputId = itemId;
				outputQty = delta;
			}
		}

		if (inputs.isEmpty() || outputKinds != 1 || isDestroyedProduct(outputId) || !isTracked(outputId))
			return;

		pairProcessingRecipe(inputs, outputId, outputQty, true);
	}

	/**
	 * Attributes this tick's unclaimed inventory gains to {@link AcquisitionSource#GATHER} at
	 * 0 when a gathering-skill XP drop (Hunter, Mining, Fishing, Woodcutting, Farming) marks
	 * them as gathered from the world at no cost (#213) — Sunfire splinters, antlers, ores,
	 * fish, logs, harvested herbs. Runs after {@link #correlateProcessing} (so a paired recipe
	 * output, already queued in {@code pendingProcessingOutput}, is skipped and keeps its
	 * transferred basis) and before the quantity sync consumes the deltas. A gain with no
	 * gathering XP this tick stays unclaimed and takes the unknown-source path. Coins never
	 * participate. Gated by the Source-Based Pricing toggle.
	 *
	 * <p>Yields to {@link #correlateReward}: when a reward-loot signal ({@link #rewardContainerTick})
	 * fired this tick, the gains are reward loot, not gathered — some reward interactions (e.g. the
	 * Tempoross reward pool) also grant gathering XP on the same tick, which would otherwise let a
	 * GATHER claim win the FIFO over the correct REWARD one (#215).
	 */
	private void correlateGathering()
	{
		if (!config.sourcePricing() || client.getTickCount() - gatherXpTick > 1 || pendingItemDeltas.isEmpty())
			return;

		if (client.getTickCount() - rewardContainerTick <= 1)
			return;

		for (Map.Entry<Integer, Integer> entry : pendingItemDeltas.entrySet())
		{
			int itemId = entry.getKey();
			int delta = entry.getValue();
			if (delta <= 0 || itemId == ItemID.COINS || ledger.hasProcessingOutput(itemId)
					|| ledger.hasDecantOrConsumedOutput(itemId))
				continue;

			if (isTracked(itemId))
				ledger.claim(AcquisitionSource.GATHER, itemId, delta, 0, client.getTickCount());
		}
	}

	/**
	 * Claims this tick's tracked inventory gains as a free {@link AcquisitionSource#REWARD} at 0
	 * when a reward-loot signal fired on the same tick ({@link #rewardContainerTick}) — a reward/loot
	 * container change ({@link #REWARD_CONTAINERS}), a Huntsman's loot-sack open, or a "you found some
	 * loot" chat line — i.e. loot taken from a raids chest, clue casket, reward pool or similar (#215).
	 * Takes precedence over {@link #correlateGathering}, which yields when this signal is present so a
	 * coincident gathering-XP tick can't mislabel the loot. Runs before the quantity sync consumes the
	 * deltas; a paired recipe output already queued in {@code pendingProcessingOutput} is skipped and
	 * keeps its transferred basis. A gain with no reward signal this tick stays unclaimed and takes the
	 * unknown-source path. Coins never participate. Gated by the Source-Based Pricing toggle.
	 */
	private void correlateReward()
	{
		if (!config.sourcePricing() || client.getTickCount() - rewardContainerTick > 1 || pendingItemDeltas.isEmpty())
			return;

		for (Map.Entry<Integer, Integer> entry : pendingItemDeltas.entrySet())
		{
			int itemId = entry.getKey();
			int delta = entry.getValue();
			if (delta <= 0 || itemId == ItemID.COINS || ledger.hasProcessingOutput(itemId)
					|| ledger.hasDecantOrConsumedOutput(itemId))
				continue;

			if (isTracked(itemId))
				ledger.claim(AcquisitionSource.REWARD, itemId, delta, 0, client.getTickCount());
		}
	}

	/**
	 * Attributes this tick's unclaimed inventory gains to {@link AcquisitionSource#THIEVING} at
	 * 0 when a Thieving XP drop marks them as stolen at no cost (#217) — pickpocket loot, stall
	 * produce, chest hauls. An exact mirror of {@link #correlateGathering}: it runs after
	 * {@link #correlateReward} (so reward loot keeps its REWARD claim) and before the quantity sync
	 * consumes the deltas; a paired recipe output already queued in {@code pendingProcessingOutput}
	 * is skipped and keeps its transferred basis. A gain with no Thieving XP this tick stays
	 * unclaimed and takes the unknown-source path. Coins never participate. Gated by the
	 * Source-Based Pricing toggle.
	 *
	 * <p>Yields to {@link #correlateReward}: when a reward-loot signal ({@link #rewardContainerTick})
	 * fired this tick, the gains are reward loot, not stolen, so a coincident Thieving-XP tick can't
	 * mislabel them (precedence: Processing &gt; Reward &gt; Gather/Thieving &gt; Unknown).
	 */
	private void correlateThieving()
	{
		if (!config.sourcePricing() || client.getTickCount() - thievingXpTick > 1 || pendingItemDeltas.isEmpty())
			return;

		if (client.getTickCount() - rewardContainerTick <= 1)
			return;

		for (Map.Entry<Integer, Integer> entry : pendingItemDeltas.entrySet())
		{
			int itemId = entry.getKey();
			int delta = entry.getValue();
			if (delta <= 0 || itemId == ItemID.COINS || ledger.hasProcessingOutput(itemId)
					|| ledger.hasDecantOrConsumedOutput(itemId))
				continue;

			if (isTracked(itemId))
				ledger.claim(AcquisitionSource.THIEVING, itemId, delta, 0, client.getTickCount());
		}
	}

	/**
	 * @return whether {@code itemId} is a worthless destroyed processing product — a
	 * non-tradeable item (absent from the GE mapping) with no market value, such as burnt
	 * food or a crushed gem. Requires the mapping to have loaded so a genuine tradeable
	 * item is never mistaken for one before its price is known.
	 */
	private boolean isDestroyedProduct(int itemId)
	{
		return mappingsLoaded
				&& !itemMappings.containsKey(itemId)
				&& itemManager.getItemPrice(itemId) <= 0;
	}

	/** @return an untracked processing input's per-unit value under the configured fallback pricing. */
	private long untrackedInputValue(int itemId)
	{
		long guide = itemManager.getItemPrice(itemId);
		return config.fallbackPricing().select(guide, guide, guide);
	}

	/**
	 * Snapshots one side of the trade window (canonical id → quantity) as its container
	 * changes. For our own side, diffs the new offer against the previous snapshot and
	 * queues the change so the matching inventory removal suspends (rather than closes) the
	 * offered lots, and a later withdrawal un-suspends them (#66).
	 */
	private void captureTradeOffer(Map<Integer, Integer> side, ItemContainer container, boolean mine)
	{
		Map<Integer, Integer> previous = mine ? new HashMap<>(side) : null;
		side.clear();
		if (container != null)
		{
			for (Item item : container.getItems())
			{
				if (item.getId() > 0)
					side.merge(itemManager.canonicalize(item.getId()), item.getQuantity(), Integer::sum);
			}
		}

		if (mine)
			queueTradeSuspension(previous, side);
	}

	/**
	 * Turns the change in our own offer into pending suspend/un-suspend intents: items added to
	 * the offer left our inventory and should suspend, items withdrawn returned and should
	 * un-suspend. Only tracked, non-currency items queue — coins and platinum tokens are the
	 * trade's numerator, not a lot, and untracked items never flow through {@link CostBasisLedger#applyDelta}
	 * to consume the intent.
	 */
	private void queueTradeSuspension(Map<Integer, Integer> before, Map<Integer, Integer> after)
	{
		if (!config.sourcePricing())
			return;

		ItemDeltas.forEachDelta(before, after, (id, delta) ->
		{
			if (isTradeCurrency(id) || !isTracked(id))
				return;

			if (delta > 0)
				ledger.queueTradeSuspend(id, delta);
			else
				ledger.queueTradeUnsuspend(id, -delta);
		});
	}

	/** Registers the completed trade's claims when the game confirms the exchange (#66). */
	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() == ChatMessageType.TRADE && "Accepted trade.".equals(event.getMessage()))
			registerTradeClaims();

		if (isPouchDepositMessage(event.getMessage()))
			ledger.signalPouchDeposit();

		if (event.getMessage() != null && event.getMessage().startsWith(REWARD_LOOT_PREFIX))
			rewardContainerTick = client.getTickCount();
	}

	/**
	 * @return whether a chat line signals a hunting pouch emptying into the bank — either the
	 *         per-pouch "Empty" deposit ("You deposit some &lt;fur/meat&gt; into your bank.") or
	 *         the bank's "Empty containers" button. Only pouch emptying produces these lines; a
	 *         normal manual bank deposit is silent, so there is no false positive (#214).
	 */
	private static boolean isPouchDepositMessage(String message)
	{
		if (message == null)
			return false;

		return EMPTY_CONTAINERS_MESSAGE.equals(message)
				|| (message.startsWith(POUCH_DEPOSIT_PREFIX) && message.endsWith(POUCH_DEPOSIT_SUFFIX));
	}

	/** @return whether a "Fill" menu target names a fur/meat hunting pouch (any size) (#214). */
	private static boolean isPouchTarget(String target)
	{
		String lower = target.toLowerCase();
		return POUCH_TARGETS.stream().anyMatch(lower::contains);
	}

	/**
	 * Books a completed trade's item movements as {@link AcquisitionSource#PLAYER_TRADE} (#66):
	 * items received buy in at the gp we gave apportioned across them by market value, and
	 * items given close at the gp we received apportioned the same way. Pure item-for-item
	 * legs price at 0; coins and platinum tokens (valued at 1,000 gp each) are the
	 * numerator, never an apportionment target.
	 *
	 * <p>The two sides settle differently. Received items only enter our inventory now, so they
	 * are registered as claims for the imminent additions to match. Given items already left our
	 * inventory when they were offered (suspended, not closed), so there is no delta to match —
	 * they are closed here directly against their trade suspension.
	 */
	private void registerTradeClaims()
	{
		long gpPaid = tradeGp(myTradeOffer);
		long gpReceived = tradeGp(theirTradeOffer);

		claimReceivedItems(theirTradeOffer, gpPaid);
		closeGivenItems(myTradeOffer, gpReceived);

		myTradeOffer.clear();
		theirTradeOffer.clear();
	}

	/** Gp value of one platinum token, the coin-equivalent currency for trades above max cash. */
	private static final long PLATINUM_TOKEN_GP = 1_000L;

	/**
	 * @return whether the item is trade currency — coins or platinum tokens — which
	 *         forms the trade's gp numerator rather than a lot-bearing item leg
	 */
	private static boolean isTradeCurrency(int itemId)
	{
		return itemId == ItemID.COINS || itemId == ItemID.PLATINUM;
	}

	/** @return one trade side's money in gp: coins plus platinum tokens at 1,000 gp each. */
	private static long tradeGp(Map<Integer, Integer> side)
	{
		return side.getOrDefault(ItemID.COINS, 0)
				+ PLATINUM_TOKEN_GP * side.getOrDefault(ItemID.PLATINUM, 0);
	}

	/** Builds one trade side's non-currency apportionment legs, each weighted by its unit market value. */
	private List<TradeApportioner.Leg> tradeLegs(Map<Integer, Integer> side)
	{
		List<TradeApportioner.Leg> legs = new ArrayList<>();
		for (Map.Entry<Integer, Integer> entry : side.entrySet())
		{
			if (!isTradeCurrency(entry.getKey()) && entry.getValue() > 0)
				legs.add(new TradeApportioner.Leg(entry.getKey(), entry.getValue(),
						marketUnitValue(entry.getKey())));
		}

		return legs;
	}

	/** Claims received items as buys at the apportioned per-unit price, matched by their inventory additions. */
	private void claimReceivedItems(Map<Integer, Integer> side, long gp)
	{
		List<TradeApportioner.Leg> legs = tradeLegs(side);
		Map<Integer, Long> prices = TradeApportioner.apportion(legs, gp);
		for (TradeApportioner.Leg leg : legs)
		{
			if (isTracked(leg.itemId))
				ledger.claim(AcquisitionSource.PLAYER_TRADE, leg.itemId, leg.quantity,
						prices.get(leg.itemId), client.getTickCount());
		}
	}

	/**
	 * Closes given items as sells at the apportioned per-unit price, realizing them against the trade
	 * suspension taken when they were offered. Any leg whose suspension has not landed yet — a same-tick
	 * offer+accept where "Accepted trade." outran the offer's inventory decrease — is parked and retried
	 * after the container sync, exactly as the GE sell path does, so the sale is never dropped (#175).
	 */
	private void closeGivenItems(Map<Integer, Integer> side, long gp)
	{
		List<TradeApportioner.Leg> legs = tradeLegs(side);
		Map<Integer, Long> prices = TradeApportioner.apportion(legs, gp);
		for (TradeApportioner.Leg leg : legs)
		{
			if (isTracked(leg.itemId))
				ledger.realizeTradeSale(leg.itemId, leg.quantity, prices.get(leg.itemId));
		}
	}

	/** @return an item's unit market value for apportionment weights: the tracked avg, or the wiki price. */
	private long marketUnitValue(int itemId)
	{
		TrackedItem tracked = trackedItems.get(itemId);
		if (tracked != null && tracked.getAvgPrice() > 0)
			return tracked.getAvgPrice();

		return itemManager.getItemPrice(itemId);
	}

	/**
	 * Claims an inventory change as a shop transaction (#67) when exactly one tracked
	 * non-coin item moved: the coins paid or received, divided across the quantity,
	 * price the item's {@link AcquisitionSource#SHOP} claim. A buy must pay coins; a
	 * sell must not spend them, and a worthless sell the shop pays nothing for is still
	 * a shop sale at 0. Anything murkier — multi-item changes, specialty-currency shops
	 * (tokkul, marks) that move a second item rather than coins — stays unclaimed and
	 * takes the unknown-source path.
	 */
	private void registerShopClaims(Map<Integer, Integer> oldCounts, Map<Integer, Integer> newCounts)
	{
		long coinDelta = 0;
		int changedItem = 0;
		int itemDelta = 0;
		int changedCount = 0;

		for (int itemId : ItemDeltas.keyUnion(oldCounts, newCounts))
		{
			int delta = newCounts.getOrDefault(itemId, 0) - oldCounts.getOrDefault(itemId, 0);
			if (delta == 0)
				continue;

			if (itemId == ItemID.COINS)
			{
				coinDelta = delta;
			}
			else
			{
				changedCount++;
				changedItem = itemId;
				itemDelta = delta;
			}
		}

		if (changedCount != 1 || itemDelta == 0 || !isTracked(changedItem))
			return;

		boolean sell = itemDelta < 0;
		if (sell ? coinDelta < 0 : coinDelta >= 0)
			return;

		long unitPrice = Math.abs(coinDelta) / Math.abs(itemDelta);
		ledger.claim(AcquisitionSource.SHOP, changedItem, Math.abs(itemDelta), unitPrice,
				client.getTickCount());
	}

	/** Closes every remaining ground suspension as lost (delegating to the ledger) and clears our own drop tracking. */
	private void closeAllGroundSuspensions()
	{
		ledger.closeAllGroundSuspensions();
		myDrops.clear();
	}

	/**
	 * Resets transient and per-login state on game-state transitions: clears
	 * ground items on each load, and on login wipes the count caches and reloads
	 * the persisted tracked items.
	 */
	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOADING:
				groundItems.clear();
				myDrops.clear();
				tickGroundSpawns.clear();
				tickGroundDespawns.clear();
				tickGroundQuantityChanges.clear();
				break;
			case LOGGED_IN:
				if (!sessionInitialized)
				{
					sessionInitialized = true;
					trackedItems.clear();
					previewItem = null;
					containerCounts.clear();
					runePouchCounts.clear();
					seenContainersSinceLogin.clear();
					runePouchSeenSinceLogin = false;
					runePouchDirty = false;
					geLoginTick = client.getTickCount();
					pendingQuantitySync = false;
					pendingItemDeltas.clear();
					loadCategories();
					loadPersistedItems();
					loadSavedComparisons();
					ledger.load();
					loadPortfolioHistory();
					ledger.resetForLogin();
					myDrops.clear();
					shopOpen = false;
					myTradeOffer.clear();
					theirTradeOffer.clear();
					lastSkillXp.clear();
					processingXpTick = -1;
					magicXpTick = -1;
					gatherXpTick = -1;
					rewardContainerTick = -1;
					thievingXpTick = -1;
					clientThread.invokeLater(this::hydratePriceCache);
				}

				refreshPanel();
				break;
			case LOGIN_SCREEN:
				sessionInitialized = false;
				closeAllGroundSuspensions();
				refreshPanel();
				break;
			default:
				break;
		}
	}

	/** Resets the session baseline when the RS profile (account) changes, so stats restart per account. */
	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged event)
	{
		SwingUtilities.invokeLater(panel::clearSessionBaseline);
	}

	/**
	 * Mirrors rune pouch contents (held in varbits, not a normal container) into
	 * the quantity counts, accumulating deltas like a container change.
	 */
	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarbitId() == VarbitID.GRAVESTONE_VISIBLE)
		{
			boolean durationExpired = client.getVarbitValue(VarbitID.GRAVESTONE_DURATION) <= 0;
			ledger.onGravestoneVisibility(event.getValue() != 0, durationExpired);
			return;
		}

		if (RUNE_POUCH_VARBITS.contains(event.getVarbitId()))
			runePouchDirty = true;
	}

	/**
	 * Diffs settled rune pouch contents once per tick. Debouncing to the tick (rather than diffing
	 * per varbit event) means every type/quantity varbit for a change has landed before the read, so
	 * a half-populated snapshot can no longer book a phantom acquisition (#237). The first settled
	 * read after login — and any empty→full read inside {@link #RUNE_POUCH_LOGIN_GRACE_TICKS} — only
	 * establishes the baseline, since a login must produce no pouch delta.
	 */
	private void flushRunePouchDelta()
	{
		Map<Integer, Integer> oldPouch = new HashMap<>(runePouchCounts);
		syncRunePouch();
		boolean firstSync = !runePouchSeenSinceLogin;
		runePouchSeenSinceLogin = true;
		boolean loginGrace = geLoginTick >= 0
				&& client.getTickCount() - geLoginTick <= RUNE_POUCH_LOGIN_GRACE_TICKS;
		boolean emptyToFullAtLogin = oldPouch.isEmpty() && !runePouchCounts.isEmpty() && loginGrace;
		if (!firstSync && !emptyToFullAtLogin)
		{
			Set<Integer> allIds = new HashSet<>(oldPouch.keySet());
			allIds.addAll(runePouchCounts.keySet());
			for (int itemId : allIds)
			{
				int delta = runePouchCounts.getOrDefault(itemId, 0) - oldPouch.getOrDefault(itemId, 0);
				if (delta != 0)
					pendingItemDeltas.merge(itemId, delta, Integer::sum);
			}

			pendingQuantitySync = true;
		}

		refreshPanel();
	}

	/** Rebuilds {@link #runePouchCounts} by reading the rune pouch type/quantity varbits. */
	private void syncRunePouch()
	{
		runePouchCounts.clear();
		EnumComposition runeEnum = client.getEnum(EnumID.RUNEPOUCH_RUNE);
		for (int i = 0; i < RUNE_POUCH_TYPE_VARBITS.length; i++)
		{
			int typeId = client.getVarbitValue(RUNE_POUCH_TYPE_VARBITS[i]);
			int qty    = client.getVarbitValue(RUNE_POUCH_QUANTITY_VARBITS[i]);
			if (typeId == 0 || qty <= 0)
				continue;

			int itemId = runeEnum.getIntValue(typeId);
			runePouchCounts.merge(itemId, qty, Integer::sum);
		}
	}

	/**
	 * Consumes GE offer progress to price trades and track the buy limit. Buy fills are
	 * ledgered until the items are collected; a sell's placement suspends the offered units
	 * and its fills realize them at the true price; a cancellation restores the remainder.
	 *
	 * <p>Just after login the offer sync replays pre-existing offers here rather than at
	 * container sync (whose offers array isn't populated yet). Within that window the state
	 * is rebuilt via {@link CostBasisLedger#primeGeStateFromLogin()} and the events are swallowed so they
	 * aren't replayed as fresh placements or fills.
	 */
	@Subscribe
	public void onGrandExchangeOfferChanged(GrandExchangeOfferChanged event)
	{
		GrandExchangeOffer offer = event.getOffer();
		if (offer == null)
			return;

		if (geLoginTick >= 0 && client.getTickCount() - geLoginTick <= GE_LOGIN_SYNC_TICKS)
		{
			ledger.primeGeStateFromLogin();
			return;
		}

		GrandExchangeOfferState state = offer.getState();
		boolean buying = state == GrandExchangeOfferState.BUYING
				|| state == GrandExchangeOfferState.BOUGHT
				|| state == GrandExchangeOfferState.CANCELLED_BUY;
		boolean cancelled = state == GrandExchangeOfferState.CANCELLED_BUY
				|| state == GrandExchangeOfferState.CANCELLED_SELL;
		boolean empty = state == GrandExchangeOfferState.EMPTY;

		ledger.onGeOffer(event.getSlot(), offer.getItemId(), buying, cancelled, empty,
				offer.getTotalQuantity(), offer.getQuantitySold(), offer.getSpent());
	}

	/**
	 * Applies the accumulated per-item container deltas to tracked items: positive
	 * deltas open new lots (auto-add), negative deltas close lots FIFO, and each
	 * item's quantity is adjusted. No-op when auto-add is off. Persists/refreshes
	 * if anything changed.
	 */
	private void syncQuantitiesFromContainers()
	{
		if (pendingItemDeltas.isEmpty())
			return;

		Map<Integer, Integer> deltas = new HashMap<>(pendingItemDeltas);
		pendingItemDeltas.clear();
		if (!config.autoAddItems() || trackedItems.isEmpty())
			return;

		boolean changed = false;
		for (TrackedItem tracked : trackedItems.values())
		{
			if (tracked.getMode() != TrackItemMode.TRACK)
				continue;

			Integer delta = deltas.get(tracked.getItemId());
			if (delta == null || delta == 0)
				continue;

			ledger.applyDelta(tracked, delta);

			tracked.setQuantity(tracked.getQuantity() + delta);
			changed = true;
		}

		if (changed)
		{
			persistTrackedItems();
			refreshPanel();
		}
	}

	/**
	 * Recounts every tracked item from scratch across all containers plus the rune
	 * pouch, and reconciles each item's lots to match the true on-hand total
	 * (opening or closing lots as needed). Used to catch up after login when full
	 * container state first becomes available.
	 */
	private void reconcileAllQuantities()
	{
		pendingItemDeltas.clear();
		if (client.getGameState() != GameState.LOGGED_IN || trackedItems.isEmpty())
			return;

		for (int containerId : TRACKED_CONTAINERS)
		{
			ItemContainer container = client.getItemContainer(containerId);
			if (container == null)
				continue;

			Map<Integer, Integer> counts = containerCounts.computeIfAbsent(containerId, k -> new HashMap<>());
			counts.clear();
			for (Item item : container.getItems())
			{
				int canonical = canonicalCountId(item.getId());
				if (canonical > 0)
					counts.merge(canonical, item.getQuantity(), Integer::sum);
			}
		}

		syncRunePouch();
		ledger.reconcileSuspendedFromOffers();

		boolean changed = false;
		for (TrackedItem tracked : trackedItems.values())
		{
			if (tracked.getMode() != TrackItemMode.TRACK)
				continue;

			int total = runePouchCounts.getOrDefault(tracked.getItemId(), 0)
					+ containerCounts.values().stream()
					.mapToInt(c -> c.getOrDefault(tracked.getItemId(), 0))
					.sum();
			int owned = total + tracked.getTotalSuspendedQuantity();
			int logDelta = owned - tracked.getRecordQuantitySum();
			if (logDelta > 0)
			{
				ledger.addOpenAcquisition(tracked, logDelta, ledger.fallbackPrice(tracked), AcquisitionSource.UNKNOWN);
				changed = true;
			}
			else if (logDelta < 0)
			{
				ledger.closeFifo(tracked, -logDelta, tracked.getAvgPrice(), AcquisitionSource.UNKNOWN);
				changed = true;
			}

			if (tracked.getQuantity() != total)
			{
				tracked.setQuantity(total);
				changed = true;
			}
		}

		if (changed)
			persistTrackedItems();
	}

	/** Recounts a single item across all containers and the rune pouch and sets its quantity. */
	private void syncQuantitiesForItem(TrackedItem tracked)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
			return;

		for (int containerId : TRACKED_CONTAINERS)
		{
			ItemContainer container = client.getItemContainer(containerId);
			if (container == null)
				continue;

			Map<Integer, Integer> counts = containerCounts.computeIfAbsent(containerId, k -> new HashMap<>());
			counts.clear();
			for (Item item : container.getItems())
			{
				int canonical = canonicalCountId(item.getId());
				if (canonical > 0)
					counts.merge(canonical, item.getQuantity(), Integer::sum);
			}
		}

		syncRunePouch();

		int total = runePouchCounts.getOrDefault(tracked.getItemId(), 0)
				+ containerCounts.values().stream()
				.mapToInt(c -> c.getOrDefault(tracked.getItemId(), 0))
				.sum();
		tracked.setQuantity(total);
	}

	/** @return whether the given (canonical) item id is currently tracked. */
	boolean isTracked(int itemId)
	{
		return trackedItems.containsKey(itemId);
	}

	/**
	 * @return whether {@code itemId} is a rune being burned by a spellcast this tick (#235) — a rune
	 *         by {@link ItemCategoryClassifier} category, removed within a tick of a Magic XP gain.
	 *         Such runes are the cast's fuel, never a recipe input: they close at 0 under
	 *         {@link AcquisitionSource#CAST} and their cost never transfers onto the spell's product.
	 *         Runes removed on a Runecraft tick (earth runes crafted into lava runes) fail the Magic
	 *         test and stay ordinary processing inputs. Client thread only.
	 */
	private boolean isSpellcastRune(int itemId)
	{
		if (client.getTickCount() - magicXpTick > 1)
			return false;

		return RUNE_CATEGORY.equals(ItemCategoryClassifier.classify(
				itemManager.getItemComposition(itemId).getName()));
	}

	/**
	 * @return whether {@code itemId} is a single-use consumable (food, a potion dose) by
	 *         {@link ItemCategoryClassifier} category — an unclaimed removal of one is booked as
	 *         used up at 0 rather than an avg-price Unknown sale (#218). Ammo and runes are
	 *         excluded; see {@link #CONSUMABLE_CATEGORIES}. Client thread only.
	 */
	@Override
	public boolean isConsumable(int itemId)
	{
		String category = ItemCategoryClassifier.classify(itemManager.getItemComposition(itemId).getName());
		return CONSUMABLE_CATEGORIES.contains(category);
	}

	/**
	 * @return whether {@code itemId} is ammo destroyed on use — a cannonball or a thrown chinchompa —
	 *         matched by {@link #DESTROYED_AMMO_TOKENS} name token. An unclaimed removal of one closes at 0
	 *         under {@link AcquisitionSource#DESTROYED} rather than suspending on the ground path (#234).
	 *         Client thread only.
	 */
	@Override
	public boolean isDestroyedAmmo(int itemId)
	{
		String name = itemManager.getItemComposition(itemId).getName();
		String lower = name.toLowerCase(Locale.ROOT);
		return DESTROYED_AMMO_TOKENS.stream().anyMatch(lower::contains);
	}

	/**
	 * @return whether {@code itemId} is recoverable ranged or thrown ammo — an arrow, bolt, dart or javelin
	 *         in the {@link #AMMO_CATEGORY}, or a knife/throwing axe by {@link #RECOVERABLE_WEAPON_TOKENS} —
	 *         that lands on the target's tile when fired. A fired unit suspends on the ground path with its
	 *         basis intact instead of closing, so picking it back up nets to nothing (#234). Destroyed ammo
	 *         is excluded; see {@link #isDestroyedAmmo}. Client thread only.
	 */
	@Override
	public boolean isRecoverableAmmo(int itemId)
	{
		if (isDestroyedAmmo(itemId))
			return false;

		String name = itemManager.getItemComposition(itemId).getName();
		if (AMMO_CATEGORY.equals(ItemCategoryClassifier.classify(name)))
			return true;

		String lower = name.toLowerCase(Locale.ROOT);
		return RECOVERABLE_WEAPON_TOKENS.stream().anyMatch(lower::contains);
	}

	/**
	 * @return whether {@code itemId} is ammo of either kind — destroyed-on-use or recoverable (#234). Ammo
	 *         is fuel for a shot, never a recipe input, so it must never be booked as a processing loss;
	 *         {@link #correlateProcessing} uses this to keep darts loaded into a blowpipe (a charged variant
	 *         reads as a destroyed product) off the {@link AcquisitionSource#BURNED} path. Client thread only.
	 */
	private boolean isAmmo(int itemId)
	{
		return isDestroyedAmmo(itemId) || isRecoverableAmmo(itemId);
	}

	/**
	 * @return whether {@code itemId} is a dosed potion — its name carries a trailing dose count
	 *         ({@link DoseFamily}) — so an "Empty" click on it can be distinguished from the same
	 *         option on a jug, bird nest, or hunting pouch, which do not parse as a dose family (#232).
	 *         Client thread only.
	 */
	private boolean isDosePotion(int itemId)
	{
		int canonicalId = itemManager.canonicalize(itemId);
		return DoseFamily.parse(itemManager.getItemComposition(canonicalId).getName()) != null;
	}

	/**
	 * Resolves a container slot's item id to the canonical (unnoted, non-placeholder) id it should count
	 * as, using a single {@link ItemComposition} lookup instead of a separate placeholder-check +
	 * {@code canonicalize} pair (#185) — a bank event covers ~800 slots. Mirrors
	 * {@link net.runelite.client.game.ItemManager#canonicalize(int)}; a bank placeholder variant returns
	 * -1 because a placeholder must never count as held quantity.
	 *
	 * @return the canonical id to count, or -1 for an empty slot or a placeholder. Client thread only.
	 */
	private int canonicalCountId(int itemId)
	{
		if (itemId <= 0)
			return -1;

		ItemComposition composition = itemManager.getItemComposition(itemId);
		if (composition.getPlaceholderTemplateId() != -1)
			return -1;

		if (composition.getNote() != -1)
			return composition.getLinkedNoteId();

		return itemId;
	}

	/**
	 * Callback after the user edits an item's acquisitions: re-derives its held quantity
	 * from the lots and persists. Open lots also cover suspended units (in-flight GE
	 * sells, trades, drops, deaths), which {@code quantity} must exclude — otherwise an
	 * edit made mid-suspension would double-count the suspended units as held.
	 */
	void onAcquisitionsEdited(int itemId)
	{
		clientThread.invokeLater(() ->
		{
			TrackedItem tracked = trackedItems.get(itemId);
			if (tracked == null)
				return;

			tracked.setCostBasisInitialized(true);
			tracked.setQuantity(Math.max(0, tracked.getRecordQuantitySum() - tracked.getTotalSuspendedQuantity()));
			persistTrackedItems();
			refreshPanel();
		});
	}

	/** Callback after the user edits an item's notification rules: just persists the change. */
	private void onNotificationsEdited(int itemId)
	{
		clientThread.invokeLater(() ->
		{
			if (trackedItems.containsKey(itemId))
				persistTrackedItems();
		});
	}

	/**
	 * Maximum plausible Δ% for a notification: changes beyond this magnitude
	 * indicate a sparse/stale window average (a near-zero denominator) rather than
	 * a real move, and are ignored so a one-shot rule isn't fired on noise.
	 */
	private static final double MAX_DELTA_PCT = 1000.0;

	private static final long GLOW_PERIOD_SLOW_MS = 2000;
	private static final long GLOW_PERIOD_MEDIUM_MS = 1500;
	private static final long GLOW_PERIOD_FAST_MS = 1000;
	private static final float GLOW_MIN_ALPHA = 0.2f;
	private static final float GLOW_MAX_ALPHA = 1f;

	/** @return the current highlight alpha, a sine "breathing" pulse whose period depends on the glow speed config. */
	float breathingAlpha()
	{
		long period;
		switch (config.glowEffect())
		{
			case SLOW:
				period = GLOW_PERIOD_SLOW_MS;
				break;
			case MEDIUM:
				period = GLOW_PERIOD_MEDIUM_MS;
				break;
			case FAST:
				period = GLOW_PERIOD_FAST_MS;
				break;
			default:
				return GLOW_MAX_ALPHA;
		}

		double phase = (System.currentTimeMillis() % period) / (double) period;
		double wave = (Math.sin(phase * 2 * Math.PI) + 1) / 2;
		return GLOW_MIN_ALPHA + (GLOW_MAX_ALPHA - GLOW_MIN_ALPHA) * (float) wave;
	}

	/** @return the live map of on-screen ground items to their tiles (used by the ground overlay). */
	Map<TileItem, Tile> getGroundItems()
	{
		return groundItems;
	}

	/** Refreshes the panel without flagging a price update (no change indicators). */
	@Override
	public void refreshPanel()
	{
		refreshPanel(false);
	}

	/**
	 * Returns the current client game tick.
	 *
	 * @return the tick count
	 */
	@Override
	public int currentTick()
	{
		return client.getTickCount();
	}

	/**
	 * Returns whether source-aware pricing is enabled in config.
	 *
	 * @return {@code true} if quantity changes are priced by their source
	 */
	@Override
	public boolean sourcePricing()
	{
		return config.sourcePricing();
	}

	/**
	 * Returns the configured fallback-pricing policy for unknown-source changes.
	 *
	 * @return the fallback-pricing mode
	 */
	@Override
	public FallbackPricing fallbackPricing()
	{
		return config.fallbackPricing();
	}

	/**
	 * Returns the tracked item with the given id, if tracked.
	 *
	 * @param itemId the item id
	 * @return the tracked item, or {@code null} when the id is not tracked
	 */
	@Override
	public TrackedItem trackedItem(int itemId)
	{
		return trackedItems.get(itemId);
	}

	/**
	 * Returns all currently tracked items.
	 *
	 * @return the tracked items
	 */
	@Override
	public Collection<TrackedItem> trackedItems()
	{
		return trackedItems.values();
	}

	/**
	 * Returns whether the given item id is a known empty-container placeholder.
	 *
	 * @param itemId the item id
	 * @return {@code true} if the id is an empty container (e.g. an empty vial)
	 */
	@Override
	public boolean isEmptyContainer(int itemId)
	{
		return EMPTY_CONTAINERS.contains(itemId);
	}

	/**
	 * Returns the player's current Grand Exchange offers.
	 *
	 * @return the open GE offer slots
	 */
	@Override
	public GrandExchangeOffer[] openGeOffers()
	{
		return client.getGrandExchangeOffers();
	}

	/**
	 * Pushes the current tracked items and totals to the panel on the Swing thread.
	 *
	 * <p>Rebuilds are coalesced: the snapshot is published to {@link #pendingRebuild}
	 * (last writer wins) and a drainer is enqueued only when none is pending, so no
	 * matter how fast game events arrive, at most one rebuild sits in the EDT queue
	 * and only the newest snapshot is rendered. Without this, per-tick events queue
	 * full rebuilds faster than one completes and the panel's {@code removeAll} —
	 * which scans the pending queue per removed child — live-locks the EDT (#120).
	 *
	 * @param pricesUpdated whether this refresh follows a price change, enabling
	 *                      the per-row change indicators
	 */
	private void refreshPanel(boolean pricesUpdated)
	{
		final Instant refresh = lastPriceRefresh;
		final PriceIndicatorMode indicatorMode = pricesUpdated
				? config.priceChangeIndicator()
				: PriceIndicatorMode.OFF;
		for (TrackedItem item : trackedItems.values())
			ledger.applyBuyLimitFields(item);

		final List<TrackedItem> items = new ArrayList<>(trackedItems.values());

		final GameState gs = client.getGameState();
		final boolean loggedIn = gs == GameState.LOGGED_IN || gs == GameState.LOADING;
		final List<CategoryState> categorySnapshot = new ArrayList<>(categories);
		final boolean favCollapsed = favoritesCollapsed;
		final boolean uncatCollapsed = uncategorizedCollapsed;
		Runnable rebuild = () -> panel.rebuild(items, refresh, indicatorMode, loggedIn,
				categorySnapshot, favCollapsed, uncatCollapsed);
		if (pendingRebuild.getAndSet(rebuild) != null)
			return;

		SwingUtilities.invokeLater(() ->
		{
			Runnable newest = pendingRebuild.getAndSet(null);
			if (newest != null)
				newest.run();
		});
	}

	/**
	 * Evaluates every item's notification rules and fires the configured notifier
	 * for any that are met. A once rule is removed after firing; a repeat rule stays
	 * and re-arms edge-triggered — it fires again only after its condition has gone
	 * false and come back true, and the first evaluation after a (re)load primes it
	 * without firing. Skipped when notifications are disabled or being edited.
	 */
	private void evaluateNotifications()
	{
		Notification style = config.notificationStyle();
		if (!style.isEnabled())
			return;

		if (panel.isEditingNotifications())
			return;

		boolean changed = false;
		for (TrackedItem item : trackedItems.values())
		{
			Iterator<NotificationRule> it = item.getNotifications().iterator();
			while (it.hasNext())
			{
				NotificationRule rule = it.next();
				Boolean condition = evaluateRule(item, rule);
				if (condition == null)
					continue;

				if (rule.isRepeat())
				{
					boolean fire = condition && Boolean.FALSE.equals(rule.getLastCondition());
					rule.setLastCondition(condition);
					if (fire)
						notifier.notify(style, notificationText(item, rule));

					continue;
				}

				if (condition)
				{
					notifier.notify(style, notificationText(item, rule));
					it.remove();
					changed = true;
				}
			}
		}

		if (changed)
		{
			persistTrackedItems();
			refreshPanel();
		}
	}

	/**
	 * Evaluates a single rule against an item.
	 *
	 * @return {@code TRUE}/{@code FALSE} for the condition, or {@code null} when it
	 *         can't be evaluated yet (incomplete rule or missing/unparseable data)
	 */
	private Boolean evaluateRule(TrackedItem item, NotificationRule rule)
	{
		NotificationMetric metric = rule.getMetric();
		if (metric == null || rule.getOperation() == null)
			return null;

		if (metric.isCategorical())
		{
			String current = categoryValue(item, metric);
			if (current == null || rule.getValue() == null)
				return null;

			return current.equalsIgnoreCase(rule.getValue().trim());
		}

		TimeWindow window = metric.locksTimeframeToMonth() ? TimeWindow.MONTH : rule.getTimeWindow();
		OptionalDouble current = numericValue(item, metric, window);
		if (!current.isPresent())
			return null;

		OptionalDouble target = metric.getKind() == NotificationMetric.Kind.PERCENT
				? NotificationRule.parsePercent(rule.getValue())
				: NotificationRule.parseNumeric(rule.getValue());
		if (!target.isPresent())
			return null;

		return rule.getOperation().test(current.getAsDouble(), target.getAsDouble());
	}

	/**
	 * Resolves the current numeric reading of a metric for an item over a window
	 * (price, volume, profit, HA profit, Δ% vs. the window average, or quantity).
	 *
	 * @return the value, or empty when the underlying data is missing or unreliable
	 */
	private OptionalDouble numericValue(TrackedItem item, NotificationMetric metric, TimeWindow window)
	{
		if (metric == NotificationMetric.QUANTITY)
			return OptionalDouble.of(item.getQuantity());

		PriceStats s = item.getWindowStats().get(window);
		long avg = s == null ? 0 : s.getAvg();
		switch (metric)
		{
			case HIGH:
				return s == null ? OptionalDouble.empty() : OptionalDouble.of(s.getHigh());
			case LOW:
				return s == null ? OptionalDouble.empty() : OptionalDouble.of(s.getLow());
			case AVERAGE:
				return s == null ? OptionalDouble.empty() : OptionalDouble.of(s.getAvg());
			case VOLUME:
				return s == null ? OptionalDouble.empty() : OptionalDouble.of(s.getVolume());
			case ITM_PROFIT:
				return avg <= 0 ? OptionalDouble.empty() : OptionalDouble.of(item.getProfitAt(avg));
			case HA_PROFIT:
				if (avg <= 0 || item.getHighAlch() <= 0)
					return OptionalDouble.empty();

				return OptionalDouble.of(MarketMath.highAlchProfit(item.getHighAlch(), avg,
						runePrice(NATURE_RUNE_ID), runePrice(FIRE_RUNE_ID)));
			case DELTA_PCT:
			{
				double pct = MarketMath.changePct(item.getAvgPrice(), avg);
				if (Double.isNaN(pct))
					return OptionalDouble.empty();

				return Math.abs(pct) > MAX_DELTA_PCT ? OptionalDouble.empty() : OptionalDouble.of(pct);
			}
			default:
				return OptionalDouble.empty();
		}
	}

	/**
	 * Resolves the current categorical rating of a metric for an item
	 * (volatility, liquidity, or 30-day range position) via {@link MarketClassifier}.
	 *
	 * @return the rating label, or {@code null} when it can't be classified
	 */
	private String categoryValue(TrackedItem item, NotificationMetric metric)
	{
		switch (metric)
		{
			case VOLATILITY:
				return MarketClassifier.volatility(item.getSeriesFor(TimeWindow.WEEK));
			case LIQUIDITY:
			{
				PriceStats s = item.getWindowStats().get(TimeWindow.H24);
				return MarketClassifier.liquidity(s == null ? 0 : s.getVolume());
			}
			case RANGE_30D:
			{
				long[] range = MarketClassifier.thirtyDayRange(item.getSeriesFor(TimeWindow.MONTH));
				return MarketClassifier.rangePosition(range[0], range[1], item.getAvgPrice());
			}
			default:
				return null;
		}
	}

	/** Builds the user-facing notification message, e.g. {@code "Stockpile: Coal - High >= 200"}. */
	private String notificationText(TrackedItem item, NotificationRule rule)
	{
		NotificationMetric metric = rule.getMetric();
		String valueDisplay;
		if (metric.isCategorical())
		{
			valueDisplay = rule.getValue();
		}
		else if (metric.getKind() == NotificationMetric.Kind.PERCENT)
		{
			OptionalDouble v = NotificationRule.parsePercent(rule.getValue());
			valueDisplay = v.isPresent() ? NotificationRule.formatPercent(v.getAsDouble()) : rule.getValue();
		}
		else
		{
			OptionalDouble v = NotificationRule.parseNumeric(rule.getValue());
			valueDisplay = v.isPresent()
					? String.format(Locale.US, "%,d", Math.round(v.getAsDouble()))
					: rule.getValue();
		}

		return "Stockpile: " + item.getName() + " - " + metric.getDisplayName()
				+ " " + rule.getOperation().getSymbol() + " " + valueDisplay;
	}
}
