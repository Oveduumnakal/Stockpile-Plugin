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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;
import net.runelite.http.api.item.ItemPrice;

/**
 * The plugin's side panel and its entire Swing UI.
 *
 * <p>Uses a {@link CardLayout} to switch between two views: the main list of
 * tracked items (with search, totals, and per-row prices/value/profit) and a
 * per-item detail card (current values, market info, price/volume charts, a
 * price overview grid, alch info, notification rules, and an editable
 * acquisitions log). It also manages detail-section ordering/visibility, the
 * price-change pulse animations, and the chart pop-out windows.
 *
 * <p>The panel is purely a view: it never touches game state directly. All
 * actions (add/remove item, edit acquisitions/notifications, request detail
 * data, clear) are delegated to the plugin through the callbacks supplied to the
 * constructor, and the plugin pushes data back via {@link #rebuild} and
 * {@link #refreshDetailData}. All methods run on the Swing EDT.
 */
@Slf4j
public class StockpilePanel extends PluginPanel implements DetailViewHost
{
	private static final Color COLOR_HIGH = StockpileColors.HIGH;
	private static final Color COLOR_LOW  = StockpileColors.LOW;
	private static final Color COLOR_AVG  = StockpileColors.AVG;

	private static final Color COLOR_HIGH_STALE = new Color(70, 110, 70);
	private static final Color COLOR_LOW_STALE  = new Color(110, 70, 70);

	private static final Color TINT_HIGH = StockpileColors.TINT_HIGH;
	private static final Color TINT_LOW  = StockpileColors.TINT_LOW;
	private static final Color TINT_AVG  = StockpileColors.TINT_AVG;
	private static final Color TINT_VOLUME = StockpileColors.TINT_VOLUME;

	private final ItemManager itemManager;
	private final StockpileConfig config;
	private final BiConsumer<Integer, TrackItemMode> onAddItem;
	private final Consumer<Integer> onRemoveItem;
	/** Untracks the shown item but keeps the detail view open as a preview (#138). */
	private final Consumer<Integer> onUntrackToPreview;
	/** Pops the shown item out into its own standalone detail window (#109). */
	private final Consumer<Integer> onPopOut;
	/** Adds the item to the compare set, opening or focusing the compare window (#280). */
	private final Consumer<Integer> onAddToCompare;
	/** Adds every variant of the item (dose line or cooking chain) to the compare set (#302). */
	private final Consumer<Integer> onCompareVariants;
	/** Opens the item-less Stockpile dashboard window (#109). */
	private final Runnable onOpenDashboard;

	private final Runnable onOpenCompare;
	private final Consumer<Integer> onAcquisitionsEdited;
	private final Consumer<Integer> onRequestDetailData;
	private final Consumer<Integer> onClearAcquisitions;
	private final Consumer<Integer> onNotificationsEdited;
	private final Runnable onClearAll;
	private final IntFunction<String> examineLookup;
	/**
	 * Reorder callback: {@code (itemId, targetIndex)} — moves the item with that id to a new position
	 * in the tracked list. The first argument is an item id, not a row index; see
	 * {@link PanelActions#reorder}.
	 */
	private final BiConsumer<Integer, Integer> onReorder;
	/** Drag-reorder callback: replaces the full tracked-item order with the given id sequence. */
	private final Consumer<List<Integer>> onSetGlobalOrder;
	/** Flips the persisted compact-view config flag; the resulting config change rebuilds the list. */
	private final Runnable onToggleCompactView;
	private final Consumer<SortMode> onSetSortMode;
	/** Flips the persisted sort-direction flag; the resulting config change rebuilds the list. */
	private final Runnable onToggleSortDirection;
	/** Favorite toggle callback: (itemId, favorite) — pins/unpins an item to the top Favorites group. */
	private final BiConsumer<Integer, Boolean> onSetFavorite;
	/** Overlay toggle callback: (itemId, onOverlay) — adds/removes an item from the on-screen overlay. */
	private final BiConsumer<Integer, Boolean> onSetOnOverlay;
	/** Per-item compact toggle callback: (itemId, compact) — flips one row's compact override (#210). */
	private final BiConsumer<Integer, Boolean> onSetItemCompact;
	/** Group collapse callback: (groupKey, collapsed) — persists a group's accordion state. */
	private final BiConsumer<String, Boolean> onSetGroupCollapsed;
	/** Category create/rename/delete/reorder and per-item assignment operations. */
	private final CategoryActions categoryActions;
	/** Builds the shareable tracked-list token on the client thread and delivers it back on the EDT. */
	private final Consumer<Consumer<String>> onExportList;
	/** Imports a tracked-list token (merge, non-destructive); delivers a user-facing result message on the EDT. */
	private final BiConsumer<String, Consumer<String>> onImportList;
	/** Builds the acquisitions CSV on the client thread and delivers it back on the EDT. */
	private final Consumer<Consumer<String>> onExportCsv;
	/** Supplies the portfolio value history points ({@code {epochSeconds, value, costBasis}}) for the chart. */
	private final Supplier<List<long[]>> onPortfolioHistory;
	/** Supplies the cheap aggregate point count for the chart button's per-rebuild visibility check (#184). */
	private final IntSupplier onPortfolioPointCount;
	/** Bundled release notes shown in the changelog window. */
	private final Changelog changelog;
	/** Callback to persist that the current release's "What's New" has been seen. */
	private final Runnable onWhatsNewSeen;
	/** Whether the footer indicator is currently in the highlighted "What's New" state. */
	private boolean whatsNew;
	/** The footer "What's New ✨" / "Change log" indicator button. */
	private JButton changelogButton;

	/** Latest category state from the plugin, used to render the grouped/accordion list. */
	private List<CategoryState> categories = new ArrayList<>();
	private boolean favoritesCollapsed;
	private boolean uncategorizedCollapsed;

	/**
	 * Whether the list is currently grouped (favorites or categories active); disables drag
	 * reorder, which is global-order only.
	 */
	private boolean groupingActive;

	/**
	 * Last-rendered items/mode, retained so toggling manage mode can re-render rows without a full
	 * plugin refresh, and so a session reset ({@link #resetSession()}) re-primes from the same list.
	 */
	private List<TrackedItem> lastRenderItems = new ArrayList<>();
	private PriceIndicatorMode lastRenderIndicatorMode = PriceIndicatorMode.OFF;

	/**
	 * Cached per-item row scaffolding (#275), keyed by item id in display order. When the structural
	 * signature is unchanged, rows are updated in place through {@link #populateRow} instead of rebuilt,
	 * avoiding a full {@code removeAll()} + reconstruction of every row on each price/inventory event.
	 */
	private final Map<Integer, RowView> rowViews = new LinkedHashMap<>();

	/** Cached group-header total labels (#275), keyed by group key, so header totals refresh in place too. */
	private final Map<String, JLabel> groupTotalLabels = new HashMap<>();

	/** The last full render's structural signature (#275); an equal signature enables the in-place path. */
	private String lastStructuralSig;

	/** Item ids in current display order, kept in sync on each {@link #rebuild}, used to compute reorder targets. */
	private final List<Integer> orderedItemIds = new ArrayList<>();

	/** Whether the list is in reorder mode, which reveals the per-row drag/arrow strip. */
	private boolean reorderMode = false;

	/** Header toggle that enters/exits reorder mode. */
	private JLabel reorderToggle;

	/**
	 * Header toggle that switches between the standard and compact row layouts. Its
	 * {@code ≣} glyph renders from a taller fallback font, so it uses a shrunken derived
	 * font to match the other header icons.
	 */
	private JLabel compactToggle;

	/** Header button (manage mode only) that opens the Manage Categories dialog. */
	private JLabel categoriesButton;

	/** Header toggle that shows/hides the tracked-list filter field. */
	private JLabel filterToggle;

	/** Header toggle that opens the sort-mode menu; highlighted when a non-manual sort is active. */
	private JLabel sortToggle;

	private final CardLayout cardLayout = new CardLayout();

	private final JPanel cardsHost = new JPanel(cardLayout)
	{
		/**
		 * Sizes the host to the visible card, letting the logged-out placeholder and
		 * loading spinner fill the viewport so they center vertically. The fill target
		 * subtracts the scroll view's vertical insets (StockpilePanel's border) so the
		 * card fills exactly the visible area; targeting the raw extent height would
		 * overflow by the border and show a spurious scroll bar.
		 */
		@Override
		public Dimension getPreferredSize()
		{
			Dimension base = super.getPreferredSize();

			for (Component c : getComponents())
			{
				if (c.isVisible())
				{
					base = c.getPreferredSize();
					break;
				}
			}

			if ((loggedOutCard != null && loggedOutCard.isVisible())
					|| detailView.isLoadingVisible())
			{
				JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, this);

				if (viewport != null)
				{
					Insets insets = StockpilePanel.this.getInsets();
					int target = viewport.getExtentSize().height - insets.top - insets.bottom;

					return new Dimension(base.width, Math.max(base.height, target));
				}
			}

			return base;
		}
	};
	private static final String CARD_MAIN = "main";
	private static final String CARD_DETAIL = "detail";
	private static final String CARD_LOGGED_OUT = "loggedOut";

	private final Map<Integer, TrackedItem> currentItems = new HashMap<>();
	/** The sidebar's detail view (extracted for #110); the host mounts it as the {@link #CARD_DETAIL} card. */
	private final DetailView detailView;
	/**
	 * 18px row icons keyed by {@link #iconCacheKey} (item id + rendered stack size), so
	 * quantity-aware sprites are cached per stack.
	 */
	private final Map<Long, ImageIcon> rowIconCache = new HashMap<>();

	/** The logged-out placeholder card; tracked so {@link #cardsHost} can fill the viewport while it shows. */
	private JPanel loggedOutCard;

	private long natureRunePrice;
	private long fireRunePrice;

	private final IconTextField searchField;
	private JPopupMenu searchResultsPopup;
	private JPanel searchResultsContent;
	private int searchFirstResultId = -1;
	private int searchPopupHeight = -1;

	/** Name filter over the tracked list, shown only when the list overflows into scrolling. */
	private IconTextField trackedFilterField;
	private String trackedFilter = "";

	private final JPanel trackedItemsPanel;
	private final JPanel bottomPanel;
	private final JLabel totalsTitle;
	/** Title row hosting {@link #totalsTitle} and the chart pop-out button; carries the toggle-able divider. */
	private JPanel totalsTitleRow;
	/** Opens the portfolio value chart; hidden until at least two history points exist to plot. */
	private JButton portfolioChartButton;
	/** WEST strut balancing {@link #portfolioChartButton} so the title stays centred; toggled with it. */
	private Component portfolioChartStrut;
	private final JPanel geEstimatesSlotTop = new JPanel(new BorderLayout());
	private final JPanel geEstimatesSlotBottom = new JPanel(new BorderLayout());
	private EstimatesPosition currentEstimatesPosition;

	private static final Color DIVIDER_COLOR = StockpileColors.DIVIDER;
	/**
	 * Fainter divider above the footer's Report/Request row: dimmer than
	 * {@link #DIVIDER_COLOR} but still visible over the (40,40,40) background.
	 */
	private static final Color FOOTER_DIVIDER_COLOR = StockpileColors.TABLE_GRID;
	private static final Border TITLE_BORDER_WITH_TOP_DIVIDER =
			BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(
							new EmptyBorder(10, 0, 0, 0),
							new MatteBorder(1, 0, 0, 0, DIVIDER_COLOR)),
					new EmptyBorder(10, 0, 12, 0));
	private static final Border TITLE_BORDER_NO_DIVIDER =
			new EmptyBorder(10, 0, 12, 0);

	private static final Border ESTIMATE_ROW_BORDER_DEFAULT =
			new EmptyBorder(3, 0, 3, 0);
	private static final Border ESTIMATE_ROW_BORDER_COMPACT =
			new EmptyBorder(1, 0, 1, 0);
	private static final Border PROFIT_SECTION_BORDER_DEFAULT =
			BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(
							new EmptyBorder(4, 0, 0, 0),
							new MatteBorder(1, 0, 0, 0, DIVIDER_COLOR)),
					new EmptyBorder(4, 0, 0, 0));
	private static final Border PROFIT_SECTION_BORDER_COMPACT =
			BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(
							new EmptyBorder(1, 0, 0, 0),
							new MatteBorder(1, 0, 0, 0, DIVIDER_COLOR)),
					new EmptyBorder(1, 0, 0, 0));

	private final JLabel totalHighLabel;
	private final JLabel totalLowLabel;
	private final JLabel totalAvgLabel;
	private final JPanel totalHighRow;
	private final JPanel totalLowRow;
	private final JPanel totalAvgRow;

	/** Static grey "Session:" prefix; never recoloured, mirroring the profit row's prefix. */
	private final JLabel sessionLabel = new JLabel("Session:");
	/** Value gained/lost since the session baseline (login or manual reset); the only part recoloured. */
	private final JLabel sessionValueLabel = new JLabel();
	/** The row wrapping {@link #sessionLabel}; toggled as a whole so no empty row lingers when hidden. */
	private JPanel sessionRow;
	/** In-memory session tracking; baseline captured on the first priced render after a reset. */
	private final SessionStats sessionStats = new SessionStats();
	/** The standard totals rows (high/low/avg + profit), toggled off in compact view. */
	private JPanel totalsRows;

	/** Compact-view totals: a two-line "Total / profit (avg)" panel shown instead of the high/low/avg rows. */
	private JPanel compactTotalsRows;
	private JLabel compactTotalsCountLabel;
	private JLabel compactTotalsValueLabel;

	private final JLabel lastRefreshLabel;

	private final JPanel footerPanel = new JPanel(new BorderLayout());

	private final JButton clearButton = new JButton("Clear");

	private volatile Instant lastPriceRefresh = null;
	private final Set<Integer> trackedItemIds = new HashSet<>();

	private int hoveredItemId = -1;
	private final Timer refreshAgeTimer;

	/** Drag-reorder state. {@code dragItemId} is the item being dragged, or -1 when not dragging. */
	private int dragItemId = -1;
	/** The dragged item's group (visual-order item ids), so a drag stays within its group. */
	private List<Integer> dragGroupIds = new ArrayList<>();
	/** The list index where the dragged item would be inserted on drop. */
	private int dragInsertIndex = -1;
	/** The y-coordinate (in {@link #trackedItemsPanel} space) at which to paint the drop indicator line. */
	private int dragLineY = -1;
	/** Edge-autoscroll timer active while a drag hovers near the viewport top/bottom. */
	private Timer dragScrollTimer;
	/** Autoscroll direction while dragging: -1 up, +1 down, 0 none. */
	private int dragScrollDir = 0;
	private static final Color DRAG_LINE_COLOR = StockpileColors.AVG;
	private static final int DRAG_SCROLL_MARGIN = 28;
	private static final int DRAG_SCROLL_STEP = 12;
	/** Client property on each row card holding its item id, used to map drag positions to list indices. */
	private static final String ROW_ITEM_ID = "stockpileItemId";
	/** Client property marking a group accordion header, used to find group boundaries during a drag. */
	private static final String GROUP_HEADER_KEY = "stockpileGroupHeader";

	/** GitHub new-issue endpoint and templates; the footer forms deep-link here with fields prefilled. */
	private static final String GITHUB_NEW_ISSUE = "https://github.com/Oveduumnakal/Stockpile-Plugin/issues/new";
	private static final String BUG_TEMPLATE = "bug_report.yml";
	private static final String FEATURE_TEMPLATE = "feature_request.yml";

	private static final Color LOADING_COLOR = StockpileColors.MUTED;
	private static final long LOADING_GLOW_PERIOD_MS = 2000;
	private static final float LOADING_GLOW_MIN_ALPHA = 0.2f;
	private final List<JLabel> loadingLabels = new ArrayList<>();
	private final Timer loadingGlowTimer;

	private static final long PULSE_DURATION_MS = 1000;
	/** Fixed height of a floating add-item search result row (#279), for sizing the popup. */
	private static final int SEARCH_ROW_HEIGHT = 36;

	/** How many search rows are visible before the floating popup scrolls (#279). */
	private static final int SEARCH_VISIBLE_ROWS = 5;

	/** How many search matches the floating popup lists at most (scrollable beyond {@link #SEARCH_VISIBLE_ROWS}). */
	private static final int SEARCH_MAX_RESULTS = 25;

	private static final int PRICES_LEFT_PAD = 10;
	private static final int PRICES_RIGHT_PAD = 0;
	private static final Color COLOR_VOLUME = new Color(200, 200, 200);
	private final List<PulseEntry> pulseEntries = new ArrayList<>();
	private final Timer pulseTimer;
	private final JLabel totalHighDeltaLabel;
	private final JLabel totalLowDeltaLabel;
	private final JLabel totalAvgDeltaLabel;
	private final JLabel coinsIcon;
	private long lastCoinsIconValue = -1;
	private final Map<Integer, ImageIcon> coinsIconCache = new HashMap<>();

	private final JLabel profitLabel;
	private final JPanel profitSection;

	/**
	 * Builds the panel and its two cards (main list and detail view). The header toggles
	 * sit on their own right-justified row above the Tracked Items label.
	 *
	 * @param itemManager     for item names, icons, and prices
	 * @param config          the plugin configuration
	 * @param categoryActions the category-management operations, implemented by the plugin
	 * @param actions         the plugin-facing callbacks the panel invokes (see {@link PanelActions})
	 * @param changelog       the bundled changelog shown in the What's New view
	 * @param whatsNew        whether this launch should surface the What's New badge
	 */
	public StockpilePanel(
			ItemManager itemManager,
			StockpileConfig config,
			CategoryActions categoryActions,
			PanelActions actions,
			Changelog changelog,
			boolean whatsNew)
	{
		this.itemManager = itemManager;
		this.config = config;
		this.onAddItem = actions::addItem;
		this.onRemoveItem = actions::removeItem;
		this.onUntrackToPreview = actions::untrackToPreview;
		this.onPopOut = actions::popOut;
		this.onAddToCompare = actions::addToCompare;
		this.onCompareVariants = actions::addVariantsToCompare;
		this.onOpenDashboard = actions::openDashboard;
		this.onOpenCompare = actions::openCompare;
		this.onAcquisitionsEdited = actions::acquisitionsEdited;
		this.onRequestDetailData = actions::requestDetailData;
		this.onClearAcquisitions = actions::clearAcquisitions;
		this.onNotificationsEdited = actions::notificationsEdited;
		this.onClearAll = actions::clearAll;
		this.examineLookup = actions::examineLookup;
		this.onReorder = actions::reorder;
		this.onSetGlobalOrder = actions::setGlobalOrder;
		this.onToggleCompactView = actions::toggleCompactView;
		this.onSetSortMode = actions::setSortMode;
		this.onToggleSortDirection = actions::toggleSortDirection;
		this.onSetFavorite = actions::setFavorite;
		this.onSetOnOverlay = actions::setOnOverlay;
		this.onSetItemCompact = actions::setItemCompact;
		this.onSetGroupCollapsed = actions::setGroupCollapsed;
		this.categoryActions = categoryActions;
		this.onExportList = actions::exportList;
		this.onImportList = actions::importList;
		this.onExportCsv = actions::exportCsv;
		this.onPortfolioHistory = actions::portfolioHistory;
		this.onPortfolioPointCount = actions::portfolioPointCount;
		this.changelog = changelog;
		this.whatsNew = whatsNew;
		this.onWhatsNewSeen = actions::whatsNewSeen;
		this.detailView = new DetailView(this, DetailView.Layout.STACK);

		setLayout(new BorderLayout(0, 8));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		JLabel title = new JLabel("Stockpile", SwingConstants.CENTER);
		title.setForeground(Color.WHITE);
		title.setFont(FontManager.getRunescapeBoldFont());
		title.setBorder(new EmptyBorder(0, 0, 4, 0));

		changelogButton = buildChangelogBadge();
		applyChangelogButtonStyle();

		JPanel titleWrapper = new JPanel(new BorderLayout());
		titleWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		titleWrapper.add(title, BorderLayout.CENTER);
		titleWrapper.add(changelogButton, BorderLayout.EAST);

		searchResultsPopup = new JPopupMenu();
		searchResultsPopup.setFocusable(false);
		searchResultsPopup.setBorder(BorderFactory.createLineBorder(ColorScheme.LIGHT_GRAY_COLOR));
		searchResultsPopup.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		searchResultsContent = new JPanel();
		searchResultsContent.setLayout(new BoxLayout(searchResultsContent, BoxLayout.Y_AXIS));
		searchResultsContent.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JScrollPane searchScroll = new JScrollPane(searchResultsContent,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		searchScroll.setBorder(null);
		searchScroll.getVerticalScrollBar().setUnitIncrement(16);
		searchResultsPopup.add(searchScroll);

		searchField = new IconTextField();
		searchField.setIcon(IconTextField.Icon.SEARCH);
		searchField.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
		searchField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchField.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		searchField.setMinimumSize(new Dimension(0, 30));
		searchField.addClearListener(this::hideSearchResults);
		searchField.addActionListener(e -> trackFirstSearchResult());
		searchField.getDocument().addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e)
			{
				onSearch(searchField.getText());
			}

			public void removeUpdate(DocumentEvent e)
			{
				onSearch(searchField.getText());
			}

			public void changedUpdate(DocumentEvent e)
			{
				onSearch(searchField.getText());
			}
		});

		trackedFilterField = new IconTextField();
		trackedFilterField.setIcon(IconTextField.Icon.SEARCH);
		trackedFilterField.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 28));
		trackedFilterField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		trackedFilterField.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		trackedFilterField.setMinimumSize(new Dimension(0, 28));
		trackedFilterField.setVisible(false);
		trackedFilterField.addClearListener(this::onTrackedFilterChanged);
		trackedFilterField.getDocument().addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e)
			{
				onTrackedFilterChanged();
			}

			public void removeUpdate(DocumentEvent e)
			{
				onTrackedFilterChanged();
			}

			public void changedUpdate(DocumentEvent e)
			{
				onTrackedFilterChanged();
			}
		});

		trackedItemsPanel = new JPanel()
		{
			@Override
			protected void paintChildren(Graphics g)
			{
				super.paintChildren(g);
				if (dragItemId != -1 && dragLineY >= 0)
				{
					g.setColor(DRAG_LINE_COLOR);
					g.fillRect(2, dragLineY - 1, getWidth() - 4, 2);
				}
			}
		};
		trackedItemsPanel.setLayout(new BoxLayout(trackedItemsPanel, BoxLayout.Y_AXIS));
		trackedItemsPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		JLabel trackedLabel = new JLabel("Tracked Items", SwingConstants.CENTER);
		trackedLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		trackedLabel.setFont(FontManager.getRunescapeBoldFont());
		trackedLabel.setBorder(new EmptyBorder(0, 0, 4, 0));

		reorderToggle = new JLabel("⚙", SwingConstants.CENTER);
		reorderToggle.setVerticalAlignment(SwingConstants.TOP);
		reorderToggle.setAlignmentY(Component.TOP_ALIGNMENT);
		reorderToggle.setFont(FontManager.getRunescapeBoldFont());
		reorderToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		reorderToggle.setBorder(new EmptyBorder(6, 0, 4, 6));
		reorderToggle.setToolTipText("Reorganize tracked items");
		reorderToggle.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				toggleReorderMode();
			}
		});
		updateReorderToggle();

		compactToggle = new JLabel("≣", SwingConstants.CENTER);
		compactToggle.setVerticalAlignment(SwingConstants.TOP);
		compactToggle.setAlignmentY(Component.TOP_ALIGNMENT);
		compactToggle.setFont(FontManager.getRunescapeBoldFont().deriveFont(14f));
		compactToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		compactToggle.setBorder(new EmptyBorder(6, 0, 4, 6));
		compactToggle.setToolTipText("Toggle compact view");
		compactToggle.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (onToggleCompactView != null)
					onToggleCompactView.run();
			}
		});
		updateCompactToggle();

		categoriesButton = new JLabel();
		categoriesButton.setIcon(categoriesIcon(ColorScheme.LIGHT_GRAY_COLOR));
		categoriesButton.setVerticalAlignment(SwingConstants.TOP);
		categoriesButton.setAlignmentY(Component.TOP_ALIGNMENT);
		categoriesButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		categoriesButton.setBorder(new EmptyBorder(6, 0, 4, 6));
		categoriesButton.setToolTipText("Manage categories");
		categoriesButton.setVisible(false);
		categoriesButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				openManageCategoriesDialog();
			}
		});

		sortToggle = new JLabel("⇅", SwingConstants.CENTER);
		sortToggle.setVerticalAlignment(SwingConstants.TOP);
		sortToggle.setAlignmentY(Component.TOP_ALIGNMENT);
		sortToggle.setFont(FontManager.getRunescapeBoldFont());
		sortToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		sortToggle.setBorder(new EmptyBorder(6, 0, 4, 6));
		sortToggle.setToolTipText("Sort tracked items");
		sortToggle.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				showSortMenu();
			}
		});
		updateSortToggle();

		filterToggle = new JLabel();
		filterToggle.setVerticalAlignment(SwingConstants.TOP);
		filterToggle.setAlignmentY(Component.TOP_ALIGNMENT);
		filterToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		filterToggle.setBorder(new EmptyBorder(6, 4, 4, 6));
		filterToggle.setToolTipText("Filter tracked items");
		filterToggle.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				toggleTrackedFilter();
			}
		});
		updateFilterToggle();

		installToggleHover(reorderToggle, () -> reorderMode,
				reorderToggle::setForeground, this::updateReorderToggle);
		installToggleHover(sortToggle, () -> config.sortMode() != SortMode.MANUAL,
				sortToggle::setForeground, this::updateSortToggle);
		installToggleHover(compactToggle, config::compactView,
				compactToggle::setForeground, this::updateCompactToggle);
		installToggleHover(filterToggle, () -> trackedFilterField != null && trackedFilterField.isVisible(),
				color -> filterToggle.setIcon(filterIcon(color)), this::updateFilterToggle);
		installToggleHover(categoriesButton, () -> false,
				color -> categoriesButton.setIcon(categoriesIcon(color)),
				() -> categoriesButton.setIcon(categoriesIcon(ColorScheme.LIGHT_GRAY_COLOR)));

		JPanel headerToggles = new JPanel();
		headerToggles.setLayout(new BoxLayout(headerToggles, BoxLayout.X_AXIS));
		headerToggles.setBackground(ColorScheme.DARK_GRAY_COLOR);
		headerToggles.add(sortToggle);
		headerToggles.add(filterToggle);
		headerToggles.add(compactToggle);
		headerToggles.add(categoriesButton);
		headerToggles.add(reorderToggle);

		JLabel dashboardButton = new JLabel(dashboardIcon(ColorScheme.LIGHT_GRAY_COLOR));
		dashboardButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		dashboardButton.setBorder(new EmptyBorder(0, 2, 0, 6));
		dashboardButton.setToolTipText("Dashboard View");
		dashboardButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (onOpenDashboard != null)
					onOpenDashboard.run();
			}
		});
		installToggleHover(dashboardButton, () -> false,
				color -> dashboardButton.setIcon(dashboardIcon(color)),
				() -> dashboardButton.setIcon(dashboardIcon(ColorScheme.LIGHT_GRAY_COLOR)));

		JLabel compareButton = new JLabel(compareIcon(ColorScheme.LIGHT_GRAY_COLOR));
		compareButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		compareButton.setBorder(new EmptyBorder(0, 2, 0, 6));
		compareButton.setToolTipText("Compare View");
		compareButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (onOpenCompare != null)
					onOpenCompare.run();
			}
		});
		installToggleHover(compareButton, () -> false,
				color -> compareButton.setIcon(compareIcon(color)),
				() -> compareButton.setIcon(compareIcon(ColorScheme.LIGHT_GRAY_COLOR)));

		JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		leftButtons.setBackground(ColorScheme.DARK_GRAY_COLOR);
		leftButtons.add(dashboardButton);
		leftButtons.add(compareButton);

		JPanel dashboardButtonWrap = new JPanel(new GridBagLayout());
		dashboardButtonWrap.setBackground(ColorScheme.DARK_GRAY_COLOR);
		dashboardButtonWrap.add(leftButtons);

		JPanel togglesRow = new JPanel(new BorderLayout());
		togglesRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
		togglesRow.add(dashboardButtonWrap, BorderLayout.WEST);
		togglesRow.add(headerToggles, BorderLayout.EAST);

		JPanel trackedLabelWrapper = new JPanel(new BorderLayout(0, 2));
		trackedLabelWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		trackedLabelWrapper.add(togglesRow, BorderLayout.NORTH);
		trackedLabelWrapper.add(trackedLabel, BorderLayout.CENTER);

		JPanel totalsPanel = new JPanel(new BorderLayout(6, 0));
		totalsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		totalsPanel.setBorder(new EmptyBorder(6, 8, 6, 8));

		coinsIcon = new JLabel();
		coinsIcon.setPreferredSize(new Dimension(32, 32));
		coinsIcon.setVerticalAlignment(SwingConstants.CENTER);
		updateCoinsIcon(0);
		totalsPanel.add(coinsIcon, BorderLayout.WEST);

		totalsTitle = new JLabel("Estimated GE Sell Value", SwingConstants.CENTER);
		totalsTitle.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		totalsTitle.setFont(FontManager.getRunescapeBoldFont());

		portfolioChartButton = buildIconButton(buildChartIcon(), "View total tracked value over time",
				this::openPortfolioChart);
		portfolioChartButton.setVisible(false);

		portfolioChartStrut = Box.createHorizontalStrut(portfolioChartButton.getPreferredSize().width);
		portfolioChartStrut.setVisible(false);

		totalsTitleRow = new JPanel(new BorderLayout());
		totalsTitleRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
		totalsTitleRow.setBorder(TITLE_BORDER_WITH_TOP_DIVIDER);
		totalsTitleRow.add(portfolioChartStrut, BorderLayout.WEST);
		totalsTitleRow.add(totalsTitle, BorderLayout.CENTER);
		totalsTitleRow.add(portfolioChartButton, BorderLayout.EAST);

		totalsRows = new JPanel();
		totalsRows.setLayout(new BoxLayout(totalsRows, BoxLayout.Y_AXIS));
		totalsRows.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		totalHighLabel = new JLabel("High:  —");
		totalHighLabel.setForeground(COLOR_HIGH);
		totalHighLabel.setFont(FontManager.getRunescapeSmallFont());

		totalLowLabel = new JLabel("Low:   —");
		totalLowLabel.setForeground(COLOR_LOW);
		totalLowLabel.setFont(FontManager.getRunescapeSmallFont());

		totalAvgLabel = new JLabel("Avg:   —");
		totalAvgLabel.setForeground(COLOR_AVG);
		totalAvgLabel.setFont(FontManager.getRunescapeSmallFont());

		totalHighDeltaLabel = createDeltaLabel();
		totalLowDeltaLabel = createDeltaLabel();
		totalAvgDeltaLabel = createDeltaLabel();

		totalHighRow = buildTotalsRow(totalHighLabel, totalHighDeltaLabel);
		totalLowRow = buildTotalsRow(totalLowLabel, totalLowDeltaLabel);
		totalAvgRow = buildTotalsRow(totalAvgLabel, totalAvgDeltaLabel);

		totalsRows.add(totalHighRow);
		totalsRows.add(totalLowRow);
		totalsRows.add(totalAvgRow);

		compactTotalsRows = new JPanel();
		compactTotalsRows.setLayout(new BoxLayout(compactTotalsRows, BoxLayout.Y_AXIS));
		compactTotalsRows.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		compactTotalsRows.setVisible(false);

		compactTotalsCountLabel = new JLabel("0 itm");
		compactTotalsCountLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		compactTotalsCountLabel.setFont(FontManager.getRunescapeSmallFont());

		JLabel compactTotalsTitle = new JLabel("Total");
		compactTotalsTitle.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		compactTotalsTitle.setFont(FontManager.getRunescapeSmallFont());

		JPanel compactCountRow = new JPanel(new BorderLayout(6, 0));
		compactCountRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		compactCountRow.add(compactTotalsTitle, BorderLayout.WEST);
		compactCountRow.add(compactTotalsCountLabel, BorderLayout.EAST);

		compactTotalsValueLabel = new JLabel("—");
		compactTotalsValueLabel.setForeground(COLOR_AVG);
		compactTotalsValueLabel.setFont(FontManager.getRunescapeSmallFont());

		JPanel compactValueRow = new JPanel(new BorderLayout(6, 0));
		compactValueRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		compactValueRow.add(compactTotalsValueLabel, BorderLayout.WEST);

		compactTotalsRows.add(compactCountRow);
		compactTotalsRows.add(compactValueRow);

		JPanel totalsRowsWrapper = new JPanel(new GridBagLayout());
		totalsRowsWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		GridBagConstraints wrapC = new GridBagConstraints();
		wrapC.fill = GridBagConstraints.HORIZONTAL;
		wrapC.weightx = 1;
		wrapC.gridy = 0;
		totalsRowsWrapper.add(totalsRows, wrapC);
		wrapC.gridy = 1;
		totalsRowsWrapper.add(compactTotalsRows, wrapC);
		totalsPanel.add(totalsRowsWrapper, BorderLayout.CENTER);

		lastRefreshLabel = new JLabel("Prices not yet loaded", SwingConstants.CENTER);
		lastRefreshLabel.setForeground(StockpileColors.MUTED);
		lastRefreshLabel.setFont(FontManager.getRunescapeSmallFont());

		JLabel profitPrefixLabel = new JLabel("Est. Profit:");
		profitPrefixLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		profitPrefixLabel.setFont(FontManager.getRunescapeSmallFont());
		profitPrefixLabel.setToolTipText("Realized profit from sold lots plus unrealized "
				+ "gain/loss on held lots (marked to the current average price)");

		profitLabel = new JLabel("—");
		profitLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		profitLabel.setFont(FontManager.getRunescapeSmallFont());

		JPanel profitRow = new JPanel(new BorderLayout(6, 0));
		profitRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		profitRow.add(profitPrefixLabel, BorderLayout.WEST);
		profitRow.add(profitLabel, BorderLayout.CENTER);

		profitSection = new JPanel(new BorderLayout());
		profitSection.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		profitSection.setBorder(PROFIT_SECTION_BORDER_DEFAULT);
		profitSection.add(profitRow, BorderLayout.CENTER);
		profitSection.setVisible(false);
		totalsRows.add(profitSection);

		sessionLabel.setFont(FontManager.getRunescapeSmallFont());
		sessionLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);

		sessionValueLabel.setFont(FontManager.getRunescapeSmallFont());
		sessionValueLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);

		sessionRow = new JPanel();
		sessionRow.setLayout(new BoxLayout(sessionRow, BoxLayout.X_AXIS));
		sessionRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		sessionRow.setBorder(ESTIMATE_ROW_BORDER_DEFAULT);
		sessionRow.add(sessionLabel);
		sessionRow.add(Box.createRigidArea(new Dimension(6, 0)));
		sessionRow.add(sessionValueLabel);
		sessionRow.add(Box.createHorizontalGlue());
		totalsRows.add(sessionRow);

		MouseAdapter sessionResetListener = new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				resetSession();
			}
		};

		Stream.of(sessionRow, sessionLabel, sessionValueLabel).forEach(component ->
		{
			component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			component.setToolTipText("Value change since login — click to reset the session baseline");
			component.addMouseListener(sessionResetListener);
		});

		bottomPanel = new JPanel(new BorderLayout(0, 0));
		bottomPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		bottomPanel.add(totalsTitleRow, BorderLayout.NORTH);
		bottomPanel.add(totalsPanel, BorderLayout.CENTER);

		geEstimatesSlotTop.setBackground(ColorScheme.DARK_GRAY_COLOR);
		geEstimatesSlotBottom.setBackground(ColorScheme.DARK_GRAY_COLOR);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		topPanel.add(titleWrapper);
		topPanel.add(searchField);
		topPanel.add(Box.createVerticalStrut(4));
		topPanel.add(geEstimatesSlotTop);
		topPanel.add(trackedLabelWrapper);
		topPanel.add(trackedFilterField);

		JPanel mainCard = new JPanel(new BorderLayout(0, 8));
		mainCard.setBackground(ColorScheme.DARK_GRAY_COLOR);
		mainCard.add(topPanel, BorderLayout.NORTH);

		JPanel itemsAndTotals = new JPanel(new BorderLayout(0, 8));
		itemsAndTotals.setBackground(ColorScheme.DARK_GRAY_COLOR);
		itemsAndTotals.add(trackedItemsPanel, BorderLayout.NORTH);
		itemsAndTotals.add(geEstimatesSlotBottom, BorderLayout.CENTER);
		mainCard.add(itemsAndTotals, BorderLayout.CENTER);

		applyEstimatesPosition(EstimatesPosition.BOTTOM);

		loggedOutCard = new JPanel(new GridBagLayout());
		loggedOutCard.setBackground(ColorScheme.DARK_GRAY_COLOR);

		JPanel loggedOutMessage = new JPanel();
		loggedOutMessage.setLayout(new BoxLayout(loggedOutMessage, BoxLayout.Y_AXIS));
		loggedOutMessage.setBackground(ColorScheme.DARK_GRAY_COLOR);
		loggedOutMessage.setBorder(new EmptyBorder(10, 10, 10, 10));

		for (String line : new String[]{"Log in to view", "your tracked items"})
		{
			JLabel lineLabel = new JLabel(line, SwingConstants.CENTER);
			lineLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
			lineLabel.setFont(FontManager.getRunescapeSmallFont());
			lineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			loggedOutMessage.add(lineLabel);
		}

		loggedOutCard.add(loggedOutMessage);

		cardsHost.setBackground(ColorScheme.DARK_GRAY_COLOR);
		cardsHost.add(mainCard, CARD_MAIN);
		cardsHost.add(detailView, CARD_DETAIL);
		cardsHost.add(loggedOutCard, CARD_LOGGED_OUT);
		add(cardsHost, BorderLayout.CENTER);

		cardLayout.show(cardsHost, CARD_LOGGED_OUT);

		footerPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		footerPanel.setBorder(BorderFactory.createCompoundBorder(
				new MatteBorder(1, 0, 0, 0, DIVIDER_COLOR),
				new EmptyBorder(6, 10, 6, 10)));

		JPanel refreshRow = new JPanel(new BorderLayout());
		refreshRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
		refreshRow.add(lastRefreshLabel, BorderLayout.CENTER);

		clearButton.setFont(FontManager.getRunescapeSmallFont());
		clearButton.setForeground(COLOR_LOW);
		clearButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		clearButton.setFocusPainted(false);
		clearButton.setToolTipText("Remove all tracked items, including their notifications and collection log");
		clearButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		clearButton.setEnabled(false);
		clearButton.addActionListener(e -> confirmAndClearAll());
		refreshRow.add(clearButton, BorderLayout.EAST);

		JPopupMenu shareMenu = new JPopupMenu();
		shareMenu.add(buildFooterMenuItem("Export list", this::exportTrackedList,
				"Copy a shareable code for your tracked list to the clipboard"));
		shareMenu.add(buildFooterMenuItem("Import list", this::importTrackedList,
				"Paste a tracked-list code to merge it into this profile"));
		shareMenu.add(buildFooterMenuItem("Export acquisitions (CSV)", this::exportAcquisitionsCsv,
				"Copy the acquisitions log as CSV to the clipboard"));

		JPopupMenu feedbackMenu = new JPopupMenu();
		feedbackMenu.add(buildFooterMenuItem("Report a bug", this::openReportIssueForm,
				"Report a bug — fill it in here, then submit on GitHub"));
		feedbackMenu.add(buildFooterMenuItem("Request a feature", this::openRequestFeatureForm,
				"Request a feature — fill it in here, then submit on GitHub"));

		JPanel linksRow = new JPanel(new GridLayout(1, 2, 6, 0));
		linksRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
		linksRow.setBorder(BorderFactory.createCompoundBorder(
				new EmptyBorder(6, 0, 0, 0),
				BorderFactory.createCompoundBorder(
						new MatteBorder(1, 0, 0, 0, FOOTER_DIVIDER_COLOR),
						new EmptyBorder(6, 0, 0, 0))));
		linksRow.add(buildFooterMenu("Share", shareMenu,
				"Export or import your tracked list, or export the acquisitions log"));
		linksRow.add(buildFooterMenu("Feedback", feedbackMenu,
				"Report a bug or request a feature"));

		footerPanel.add(refreshRow, BorderLayout.CENTER);
		footerPanel.add(linksRow, BorderLayout.SOUTH);

		footerPanel.setVisible(false);
		getWrappedPanel().add(footerPanel, BorderLayout.SOUTH);

		refreshAgeTimer = new Timer(1000, e ->
		{
			updateRefreshLabel();
			detailView.updateMarketInfoTimes();
		});
		refreshAgeTimer.start();

		loadingGlowTimer = new Timer(50, e -> updateLoadingGlow());
		loadingGlowTimer.start();

		pulseTimer = new Timer(25, e -> updatePulses());
		pulseTimer.start();
	}

	/** Moves the GE estimates block above or below the other sections per the configured position. */
	private void applyEstimatesPosition(EstimatesPosition position)
	{
		if (position == currentEstimatesPosition && bottomPanel.getParent() != null)
			return;

		currentEstimatesPosition = position;
		geEstimatesSlotTop.removeAll();
		geEstimatesSlotBottom.removeAll();
		if (position == EstimatesPosition.TOP)
		{
			totalsTitleRow.setBorder(TITLE_BORDER_NO_DIVIDER);
			geEstimatesSlotTop.add(bottomPanel, BorderLayout.CENTER);
			geEstimatesSlotTop.add(buildDividerStrip(), BorderLayout.SOUTH);
		}
		else
		{
			totalsTitleRow.setBorder(TITLE_BORDER_WITH_TOP_DIVIDER);
			geEstimatesSlotBottom.add(bottomPanel, BorderLayout.NORTH);
		}

		geEstimatesSlotTop.revalidate();
		geEstimatesSlotBottom.revalidate();
		geEstimatesSlotTop.repaint();
		geEstimatesSlotBottom.repaint();
	}

	/** Applies normal or compact row padding to the GE estimates block per the configured spacing. */
	private void applyEstimatesSpacing(EstimatesSpacing spacing)
	{
		boolean compact = spacing == EstimatesSpacing.COMPACT;
		Border rowBorder = compact
				? ESTIMATE_ROW_BORDER_COMPACT : ESTIMATE_ROW_BORDER_DEFAULT;
		totalHighRow.setBorder(rowBorder);
		totalLowRow.setBorder(rowBorder);
		totalAvgRow.setBorder(rowBorder);
		profitSection.setBorder(compact
				? PROFIT_SECTION_BORDER_COMPACT : PROFIT_SECTION_BORDER_DEFAULT);
		bottomPanel.revalidate();
		bottomPanel.repaint();
	}

	/** Builds the horizontal divider strip drawn between the totals block and the footer. */
	private JPanel buildDividerStrip()
	{
		JPanel strip = new JPanel(new BorderLayout());
		strip.setBackground(ColorScheme.DARK_GRAY_COLOR);
		strip.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
						new EmptyBorder(10, 0, 0, 0),
						new MatteBorder(1, 0, 0, 0, DIVIDER_COLOR)),
				new EmptyBorder(0, 0, 10, 0)));
		return strip;
	}

	/** Stops the animation timers so the panel can be disposed cleanly. */
	public void shutdown()
	{
		refreshAgeTimer.stop();
		loadingGlowTimer.stop();
		pulseTimer.stop();
		detailView.stopLoading();
	}

	private static final Dimension DELTA_LABEL_SIZE = new Dimension(12, 12);

	/**
	 * Updates the totals coin icon to the stack sprite for the given gp value, loading it
	 * asynchronously and caching per quantity. A stale async load is discarded if the value
	 * has moved on by the time the image arrives.
	 */
	private void updateCoinsIcon(long value)
	{
		int quantity = (int) Math.max(1, Math.min(value, Integer.MAX_VALUE));
		if (quantity == lastCoinsIconValue)
			return;

		lastCoinsIconValue = quantity;

		ImageIcon cached = coinsIconCache.get(quantity);
		if (cached != null)
		{
			coinsIcon.setIcon(cached);
			return;
		}

		AsyncBufferedImage img = itemManager.getImage(ItemID.COINS, quantity, false);
		img.onLoaded(() ->
		{
			ImageIcon icon = new ImageIcon(img);
			coinsIconCache.put(quantity, icon);
			if (quantity == lastCoinsIconValue)
				coinsIcon.setIcon(icon);
		});
	}

	/**
	 * Populates the compact totals: item count plus a {@code total avg value (profit)} line,
	 * where the total avg uses the configured value format and the profit is always short format.
	 * The profit parenthetical is coloured per-part — grey parentheses with a green/red profit —
	 * while the parenthetical is dropped entirely when there is no cost-basis profit to show.
	 */
	private void updateCompactTotals(int itemCount, long totalAvg, long profit,
			boolean hasPrices, boolean showProfit, ValueFormat fmt)
	{
		compactTotalsCountLabel.setText(itemCount + " itm");
		compactTotalsValueLabel.setForeground(COLOR_AVG);

		if (!hasPrices)
		{
			compactTotalsValueLabel.setText("—");
			compactTotalsValueLabel.setToolTipText(null);
			return;
		}

		String avgText = formatTotalGp(totalAvg, fmt);

		if (!showProfit)
		{
			compactTotalsValueLabel.setText(avgText);
			compactTotalsValueLabel.setToolTipText(GpFormat.grouped(totalAvg) + " gp");
			return;
		}

		Color profitColor = profit == 0 ? ColorScheme.LIGHT_GRAY_COLOR : (profit > 0 ? COLOR_HIGH : COLOR_LOW);
		String grey = StockpileColors.toHex(ColorScheme.LIGHT_GRAY_COLOR);
		String profitHex = StockpileColors.toHex(profitColor);

		compactTotalsValueLabel.setText("<html><span style='color:" + StockpileColors.toHex(COLOR_AVG) + "'>" + avgText
				+ "</span>  <span style='color:" + grey + "'>(</span><span style='color:" + profitHex
				+ "'>" + GpFormat.signedShort(profit) + "</span>"
				+ "<span style='color:" + grey + "'>)</span></html>");
		compactTotalsValueLabel.setToolTipText("<html>" + GpFormat.grouped(totalAvg) + " gp<br>Profit: "
				+ signedGp(profit) + "</html>");
	}

	/**
	 * Renders the "Session:" line: the value gained/lost since the baseline, coloured
	 * green/red, with a tooltip splitting the change into price movement vs. quantity
	 * change. Captures the baseline on the first priced render after a reset; hidden
	 * until prices are available.
	 */
	private void updateSessionLine(List<TrackedItem> items, boolean hasPrices)
	{
		if (!config.showSession() || !hasPrices)
		{
			sessionRow.setVisible(false);
			return;
		}

		Map<Integer, long[]> snapshot = liveSessionSnapshot(items);

		if (snapshot.isEmpty())
		{
			sessionRow.setVisible(false);
			return;
		}

		if (!sessionStats.hasBaseline())
			sessionStats.reset(snapshot);
		else
			sessionStats.absorbNewItems(snapshot);

		SessionStats.Delta delta = sessionStats.delta(snapshot);
		long total = delta.getTotal();
		Color color = total == 0 ? ColorScheme.LIGHT_GRAY_COLOR : (total > 0 ? COLOR_HIGH : COLOR_LOW);

		sessionValueLabel.setForeground(color);
		sessionValueLabel.setText(GpFormat.signedShort(total));

		String tooltip = "<html>Since login:<br>"
				+ "Price movement: " + signedGp(delta.getPrice()) + "<br>"
				+ "Quantity change: " + signedGp(delta.getQuantity()) + "<br>"
				+ "<i>click to reset</i></html>";
		Stream.of(sessionRow, sessionLabel, sessionValueLabel).forEach(component -> component.setToolTipText(tooltip));
		sessionRow.setVisible(true);
	}

	/**
	 * Builds the session baseline snapshot (item id → [quantity, avg price]) from only the
	 * items whose prices came from a live fetch. Cache-hydrated prices are excluded so
	 * overnight market movement, restored from the persisted cache on login, never seeds
	 * the baseline and reads as session profit. An empty result means the session row stays
	 * hidden until real live prices arrive.
	 */
	static Map<Integer, long[]> liveSessionSnapshot(List<TrackedItem> items)
	{
		Map<Integer, long[]> snapshot = new HashMap<>();
		for (TrackedItem item : items)
			if (item.hasLivePrices())
				snapshot.put(item.getItemId(), new long[]{item.getQuantity(), item.getAvgPrice()});

		return snapshot;
	}

	/** Re-baselines the session to the current holdings, so "Session:" restarts from zero. */
	public void resetSession()
	{
		sessionStats.clear();
		boolean hasPrices = lastRenderItems.stream().anyMatch(TrackedItem::hasPrices);
		updateSessionLine(lastRenderItems, hasPrices);
	}

	/**
	 * Drops the session baseline without re-priming (used on profile change): the next
	 * rebuild captures the new profile's holdings as the baseline.
	 */
	public void clearSessionBaseline()
	{
		sessionStats.clear();
	}

	/** Drops one item's session-baseline entry when it is untracked, so removal is session-neutral. */
	public void removeSessionBaseline(int itemId)
	{
		sessionStats.removeItem(itemId);
	}

	/** A footer button that drops {@code menu} below itself, grouping related actions so the footer stays one row. */
	private JButton buildFooterMenu(String text, JPopupMenu menu, String tooltip)
	{
		JButton button = styledFooterButton(text + "  ▾", tooltip);
		button.addActionListener(e -> menu.show(button, 0, button.getHeight()));

		return button;
	}

	/** One action inside a footer dropdown, styled to match the footer links. */
	private JMenuItem buildFooterMenuItem(String text, Runnable onClick, String tooltip)
	{
		JMenuItem item = new JMenuItem(text);
		item.setFont(FontManager.getRunescapeSmallFont());
		item.setToolTipText(tooltip);
		item.addActionListener(e -> onClick.run());

		return item;
	}

	/** Shared styling for the footer's link and dropdown buttons. */
	private JButton styledFooterButton(String text, String tooltip)
	{
		JButton button = new JButton(text);
		button.setFont(FontManager.getRunescapeSmallFont());
		button.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		button.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		button.setFocusPainted(false);
		button.setMargin(new Insets(2, 2, 2, 2));
		button.setToolTipText(tooltip);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		return button;
	}

	/**
	 * Builds the top-right header badge that opens the changelog window;
	 * {@link #applyChangelogButtonStyle} sets its label and colour.
	 */
	private JButton buildChangelogBadge()
	{
		JButton badge = new JButton();
		badge.setFont(FontManager.getRunescapeSmallFont());
		badge.setBackground(ColorScheme.DARK_GRAY_COLOR);
		badge.setFocusPainted(false);
		badge.setBorderPainted(false);
		badge.setContentAreaFilled(false);
		badge.setBorder(new EmptyBorder(0, 6, 4, 0));
		badge.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		badge.setToolTipText("See what's new in this release");
		badge.setHorizontalTextPosition(SwingConstants.LEFT);
		badge.setIconTextGap(3);
		badge.addActionListener(e -> openChangelogWindow());
		badge.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				badge.setForeground(Color.WHITE);
				badge.setIcon(whatsNew ? sparkleIcon(Color.WHITE) : null);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				applyChangelogButtonStyle();
			}
		});

		return badge;
	}

	/** @return the indicator label: highlighted "What's New" for a new release, else "Change log". */
	private String changelogButtonText()
	{
		return whatsNew ? "What's New" : "Change log";
	}

	/** Applies the indicator styling — gold while "What's New", muted once seen. */
	private void applyChangelogButtonStyle()
	{
		changelogButton.setText(changelogButtonText());
		changelogButton.setForeground(whatsNew ? COLOR_AVG : ColorScheme.LIGHT_GRAY_COLOR);
		changelogButton.setIcon(whatsNew ? sparkleIcon(COLOR_AVG) : null);
	}

	/** Opens the changelog window; the first open of a new release quiets the "What's New" indicator. */
	private void openChangelogWindow()
	{
		if (whatsNew)
		{
			whatsNew = false;
			onWhatsNewSeen.run();
			applyChangelogButtonStyle();
		}

		showPopout("What's New", buildChangelogContent(), item -> { }, null);
	}

	/**
	 * Builds the changelog window: a left navigation column listing each release, with the selected
	 * release's sections expanded beneath it as quick-links that jump to that section, and the
	 * selected release's notes rendered on the right.
	 */
	private JComponent buildChangelogContent()
	{
		List<Changelog.Release> releases = changelog.releases();

		JEditorPane body = new JEditorPane();
		body.setContentType("text/html");
		body.setEditable(false);
		body.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		body.addHyperlinkListener(e ->
		{
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED && e.getURL() != null)
				LinkBrowser.browse(e.getURL().toString());
		});

		JPanel nav = new JPanel();
		nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
		nav.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nav.setBorder(new EmptyBorder(4, 4, 4, 4));

		if (!releases.isEmpty())
		{
			body.setText(renderReleaseHtml(releases.get(0)));
			body.setCaretPosition(0);
		}

		rebuildChangelogNav(nav, releases, 0, body);

		JScrollPane navScroll = new JScrollPane(nav);
		navScroll.setPreferredSize(new Dimension(168, 560));
		navScroll.getVerticalScrollBar().setUnitIncrement(16);

		JScrollPane bodyScroll = new JScrollPane(body);
		bodyScroll.setPreferredSize(new Dimension(440, 560));

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, navScroll, bodyScroll);
		split.setDividerLocation(168);
		return split;
	}

	/**
	 * (Re)populates the changelog nav: one clickable row per release (selecting it loads its notes),
	 * and beneath the selected release its section quick-links, each of which scrolls the notes to
	 * that section.
	 */
	private void rebuildChangelogNav(JPanel nav, List<Changelog.Release> releases, int selectedIndex, JEditorPane body)
	{
		nav.removeAll();
		for (int i = 0; i < releases.size(); i++)
		{
			final int index = i;
			Changelog.Release release = releases.get(i);
			boolean selected = index == selectedIndex;

			JLabel versionRow = buildChangelogNavVersion(release.getVersion(), selected);
			versionRow.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					body.setText(renderReleaseHtml(releases.get(index)));
					body.setCaretPosition(0);
					rebuildChangelogNav(nav, releases, index, body);
				}
			});
			nav.add(versionRow);

			if (selected)
			{
				for (ChangelogSection section : extractSections(release.getBody()))
				{
					JLabel link = buildChangelogNavLink(section);
					link.addMouseListener(new MouseAdapter()
					{
						@Override
						public void mouseClicked(MouseEvent e)
						{
							body.scrollToReference(section.getAnchor());
						}
					});
					nav.add(link);
				}
			}
		}

		nav.revalidate();
		nav.repaint();
	}

	/** Builds one clickable release row for the changelog nav; the selected release is gold, the rest muted. */
	private JLabel buildChangelogNavVersion(String version, boolean selected)
	{
		JLabel row = new JLabel(version);
		row.setFont(FontManager.getRunescapeBoldFont());
		row.setOpaque(true);
		row.setBorder(new EmptyBorder(5, 10, 5, 8));
		row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height + 12));

		Color restFg = selected ? COLOR_AVG : ColorScheme.LIGHT_GRAY_COLOR;
		Color restBg = selected ? ColorScheme.DARK_GRAY_COLOR : ColorScheme.DARKER_GRAY_COLOR;
		row.setForeground(restFg);
		row.setBackground(restBg);
		installChangelogNavHover(row, restFg, restBg);
		return row;
	}

	/** Builds one indented, clickable section quick-link for the changelog nav. */
	private JLabel buildChangelogNavLink(ChangelogSection section)
	{
		JLabel link = new JLabel(section.getText());
		link.setFont(FontManager.getRunescapeSmallFont());
		link.setOpaque(true);
		link.setBorder(new EmptyBorder(2, 14 + section.getLevel() * 10, 2, 6));
		link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		link.setAlignmentX(Component.LEFT_ALIGNMENT);
		link.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		link.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		link.setMaximumSize(new Dimension(Integer.MAX_VALUE, link.getPreferredSize().height + 4));
		installChangelogNavHover(link, ColorScheme.LIGHT_GRAY_COLOR, ColorScheme.DARKER_GRAY_COLOR);
		return link;
	}

	/** Adds a hover highlight (brighten to white on a lighter row) that restores the given resting colours. */
	private void installChangelogNavHover(JLabel label, Color restFg, Color restBg)
	{
		label.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				label.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
				label.setForeground(Color.WHITE);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				label.setBackground(restBg);
				label.setForeground(restFg);
			}
		});
	}

	/** @return the {@code ##}/{@code ###} section headings of a release body, in order, with scroll anchors. */
	private static List<ChangelogSection> extractSections(String body)
	{
		List<ChangelogSection> sections = new ArrayList<>();
		int index = 0;
		for (String raw : body.split("\n", -1))
		{
			String line = raw.trim();
			if (line.startsWith("### "))
			{
				sections.add(new ChangelogSection(1, line.substring(4), "sec" + index));
				index++;
			}
			else if (line.startsWith("## "))
			{
				sections.add(new ChangelogSection(0, line.substring(3), "sec" + index));
				index++;
			}
		}

		return sections;
	}

	/** One navigable changelog section: heading depth (0 for {@code ##}, 1 for {@code ###}), text, and anchor. */
	@Value
	private static class ChangelogSection
	{
		int level;

		String text;

		String anchor;
	}

	/** Escapes the HTML-significant characters so text renders literally inside an HTML label. */
	private static String escapeHtml(String text)
	{
		return text
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;");
	}

	/** A markdown link {@code [label](url)} used for the changelog's issue references. */
	private static final Pattern MD_LINK = Pattern.compile("\\[([^\\]]+)\\]\\(([^)]+)\\)");

	/** @return an HTML rendering of one release: its version and date heading, then its markdown body. */
	private String renderReleaseHtml(Changelog.Release release)
	{
		StringBuilder sb = new StringBuilder("<html><body style='font-family:sans-serif; margin:4px 8px;'>");
		sb.append("<div style='font-size:15px; font-weight:bold;'>");
		sb.append(escapeHtml(release.getVersion()));
		if (release.getDate() != null)
		{
			sb.append(" <span style='color:gray; font-weight:normal; font-size:10px;'>");
			sb.append(escapeHtml(release.getDate()));
			sb.append("</span>");
		}

		sb.append("</div>");
		sb.append(renderChangelogBody(release.getBody()));
		sb.append("</body></html>");
		return sb.toString();
	}

	private static final String CL_SECTION_STYLE = "font-size:14px;font-weight:bold;color:"
			+ StockpileColors.toHex(Color.WHITE) + ";margin-top:14px;";
	private static final String CL_AREA_STYLE = "font-size:13px;font-weight:bold;color:"
			+ StockpileColors.toHex(StockpileColors.AVG) + ";margin-top:12px;";
	private static final String CL_FEATURE_STYLE = "font-weight:bold;color:"
			+ StockpileColors.toHex(ColorScheme.LIGHT_GRAY_COLOR) + ";margin-top:8px;";
	private static final String CL_TEXT_STYLE = "color:"
			+ StockpileColors.toHex(StockpileColors.MUTED) + ";margin-top:2px;";

	/** Pixels of left indent added per nesting level in the changelog body. */
	private static final int CL_INDENT_STEP = 12;

	/**
	 * Renders a release's markdown body to HTML for the changelog window: {@code ##}/{@code ###}/{@code ####}
	 * headings become sized/weighted/coloured headers that each indent one level deeper, their content indents
	 * one level further still, and {@code [#12](url)} issue links become clickable anchors. Deliberately minimal
	 * — it only covers the constructs the bundled changelog uses, since Swing's HTML renderer is HTML-3.2-era.
	 */
	private String renderChangelogBody(String body)
	{
		StringBuilder sb = new StringBuilder();
		int contentLevel = 0;
		int sectionIndex = 0;
		for (String raw : body.split("\n", -1))
		{
			String line = raw.trim();
			if (line.isEmpty())
				continue;

			if (line.startsWith("#### "))
			{
				appendChangelogDiv(sb, CL_FEATURE_STYLE, 2, inlineLinks(line.substring(5)));
				contentLevel = 3;
			}
			else if (line.startsWith("### "))
			{
				appendChangelogAnchor(sb, sectionIndex);
				sectionIndex++;
				appendChangelogDiv(sb, CL_AREA_STYLE, 1, inlineLinks(line.substring(4)));
				contentLevel = 2;
			}
			else if (line.startsWith("## "))
			{
				appendChangelogAnchor(sb, sectionIndex);
				sectionIndex++;
				appendChangelogDiv(sb, CL_SECTION_STYLE, 0, inlineLinks(line.substring(3)));
				contentLevel = 1;
			}
			else
			{
				appendChangelogDiv(sb, CL_TEXT_STYLE, contentLevel, inlineLinks(line));
			}
		}

		return sb.toString();
	}

	/** Appends a named scroll anchor ({@code sec<n>}) matching the ids {@link #extractSections} hands the nav. */
	private static void appendChangelogAnchor(StringBuilder sb, int sectionIndex)
	{
		sb.append("<a name='sec");
		sb.append(sectionIndex);
		sb.append("'></a>");
	}

	/** Appends a {@code <div>} with the given inline CSS {@code style} and left indent, wrapping {@code html}. */
	private static void appendChangelogDiv(StringBuilder sb, String style, int indentLevel, String html)
	{
		sb.append("<div style='");
		sb.append(style);
		if (indentLevel > 0)
		{
			sb.append("margin-left:");
			sb.append(indentLevel * CL_INDENT_STEP);
			sb.append("px;");
		}

		sb.append("'>");
		sb.append(html);
		sb.append("</div>");
	}

	/** Escapes {@code text}, then turns markdown {@code [label](url)} links into clickable HTML anchors. */
	private static String inlineLinks(String text)
	{
		Matcher matcher = MD_LINK.matcher(escapeHtml(text));
		StringBuffer sb = new StringBuffer();
		while (matcher.find())
		{
			String anchor = "<a href='" + matcher.group(2) + "'>" + matcher.group(1) + "</a>";
			matcher.appendReplacement(sb, Matcher.quoteReplacement(anchor));
		}

		matcher.appendTail(sb);
		return sb.toString();
	}

	/** Copies the shareable tracked-list code to the clipboard once the plugin has built it. */
	private void exportTrackedList()
	{
		onExportList.accept(token ->
		{
			if (token == null || token.isEmpty())
			{
				JOptionPane.showMessageDialog(this, "Nothing tracked to export.", "Export List",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			copyToClipboard(token);
			JOptionPane.showMessageDialog(this, "Tracked-list code copied to the clipboard.", "Export List",
					JOptionPane.INFORMATION_MESSAGE);
		});
	}

	/** Prompts for a tracked-list code, merges it into the current profile, and reports the outcome. */
	private void importTrackedList()
	{
		JTextArea input = new JTextArea(5, 24);
		input.setLineWrap(true);
		JScrollPane scroll = new JScrollPane(input);

		int choice = JOptionPane.showConfirmDialog(this, scroll, "Paste tracked-list code",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (choice != JOptionPane.OK_OPTION)
			return;

		onImportList.accept(input.getText(), result ->
				JOptionPane.showMessageDialog(this, result, "Import List", JOptionPane.INFORMATION_MESSAGE));
	}

	/** Copies the acquisitions log as CSV to the clipboard once the plugin has built it. */
	private void exportAcquisitionsCsv()
	{
		onExportCsv.accept(csv ->
		{
			copyToClipboard(csv);
			JOptionPane.showMessageDialog(this, "Acquisitions CSV copied to the clipboard.", "Export CSV",
					JOptionPane.INFORMATION_MESSAGE);
		});
	}

	private void copyToClipboard(String text)
	{
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(text), null);
	}

	/** Opens a pop-out with the portfolio value history chart, fed live from the plugin's stored points. */
	private void openPortfolioChart()
	{
		PortfolioChartPanel chart = new PortfolioChartPanel();
		Runnable refresh = () -> chart.setData(onPortfolioHistory.get());
		refresh.run();
		portfolioPopoutRefreshers.add(refresh);
		showPopout("Portfolio Value", chart, item -> refresh.run(),
				() -> portfolioPopoutRefreshers.remove(refresh));
	}

	/** Shows the chart pop-out button (and its balancing strut) only once at least two history points exist to plot. */
	private void updatePortfolioChartButton()
	{
		boolean enough = onPortfolioPointCount != null && onPortfolioPointCount.getAsInt() >= 2;
		if (portfolioChartButton.isVisible() == enough)
			return;

		portfolioChartButton.setVisible(enough);
		portfolioChartStrut.setVisible(enough);
		totalsTitleRow.revalidate();
		totalsTitleRow.repaint();
	}

	/** Feature-template "Related area" dropdown options, matched exactly for URL prefill. */
	private static final String[] FEATURE_AREAS = {
			"Item tracking (adding / auto-add / consolidation / collection log)",
			"Live pricing (GE / wiki realtime / time-window values)",
			"Profit tracking (cost basis / acquisitions / portfolio total)",
			"Detail view & charts (graphs, timeframes, pop-out windows)",
			"Notifications / price alerts",
			"Panel / overlays (ground or inventory highlights)",
			"Configuration / settings",
			"New / other"
	};

	/** Opens the in-plugin "Report a bug" form. */
	private void openReportIssueForm()
	{
		openIssueForm("Report a Bug", BUG_TEMPLATE, "[Bug]: ", Arrays.asList(
				new IssueField("description", "Describe the bug", 4),
				new IssueField("repro", "Steps to reproduce", 3),
				new IssueField("expected", "Expected behavior", 2),
				new IssueField("actual", "Actual behavior", 2)));
	}

	/** Opens the in-plugin "Request a feature" form. */
	private void openRequestFeatureForm()
	{
		openIssueForm("Request a Feature", FEATURE_TEMPLATE, "[Feature]: ", Arrays.asList(
				new IssueField("problem", "Problem or motivation", 3),
				new IssueField("solution", "Proposed solution", 4),
				new IssueField("area", "Related area", FEATURE_AREAS),
				new IssueField("alternatives", "Alternatives considered", 3),
				new IssueField("context", "Additional context", 3)));
	}

	/**
	 * Shows a modal form for an issue template, then opens the GitHub issue form in the browser
	 * with the entered title/fields pre-filled (via query params) so the user only has to review
	 * and click Submit on GitHub. No data leaves the machine until they submit on GitHub.
	 */
	private void openIssueForm(String dialogTitle, String template, String titlePrefix, List<IssueField> fields)
	{
		JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), dialogTitle);
		dialog.setModal(true);

		JPanel form = new JPanel();
		form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
		form.setBorder(new EmptyBorder(8, 8, 8, 8));

		JTextField titleField = new JTextField();
		addFormRow(form, "Title", titleField);

		Map<IssueField, JComponent> inputs = new LinkedHashMap<>();
		for (IssueField field : fields)
		{
			if (field.options != null)
			{
				JComboBox<String> combo = new JComboBox<>(field.options);
				combo.insertItemAt("", 0);
				combo.setSelectedIndex(0);
				combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, combo.getPreferredSize().height));
				inputs.put(field, combo);
				addFormRow(form, field.label, combo);
			}
			else
			{
				JTextArea area = new JTextArea(field.rows, 28);
				area.setLineWrap(true);
				area.setWrapStyleWord(true);
				inputs.put(field, area);
				addFormRow(form, field.label, new JScrollPane(area));
			}
		}

		JButton submit = new JButton("Open on GitHub");
		submit.addActionListener(e ->
		{
			LinkBrowser.browse(buildIssueUrl(template, titlePrefix, titleField.getText(), fields, inputs));
			dialog.dispose();
		});

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(e -> dialog.dispose());

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
		buttons.add(cancel);
		buttons.add(submit);

		JPanel content = new JPanel(new BorderLayout());
		content.add(new JScrollPane(form), BorderLayout.CENTER);
		content.add(buttons, BorderLayout.SOUTH);

		dialog.setContentPane(content);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	/** Adds a labelled row (label above the field) to a vertical form panel. */
	private void addFormRow(JPanel form, String label, JComponent field)
	{
		JLabel labelComponent = new JLabel(label);
		labelComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
		field.setAlignmentX(Component.LEFT_ALIGNMENT);
		form.add(labelComponent);
		form.add(field);
		form.add(Box.createVerticalStrut(6));
	}

	/** Builds the GitHub new-issue URL with the title and non-empty fields pre-filled as query params. */
	private static String buildIssueUrl(String template, String titlePrefix, String title,
			List<IssueField> fields, Map<IssueField, JComponent> inputs)
	{
		StringBuilder url = new StringBuilder(GITHUB_NEW_ISSUE).append("?template=").append(template);

		String trimmedTitle = title == null ? "" : title.trim();
		if (!trimmedTitle.isEmpty())
			url.append("&title=").append(encode(titlePrefix + trimmedTitle));

		for (IssueField field : fields)
		{
			String value = fieldValue(inputs.get(field)).trim();
			if (!value.isEmpty())
				url.append('&')
						.append(field.id)
						.append('=')
						.append(encode(value));
		}

		return url.toString();
	}

	/** @return the current text of an issue-form input (text area or dropdown selection). */
	private static String fieldValue(JComponent input)
	{
		if (input instanceof JTextArea)
			return ((JTextArea) input).getText();

		if (input instanceof JComboBox)
		{
			Object selected = ((JComboBox<?>) input).getSelectedItem();
			return selected == null ? "" : selected.toString();
		}

		return "";
	}

	/** URL-encodes a value for a query parameter (spaces as %20, not +). */
	private static String encode(String value)
	{
		return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
	}

	/** Fixes the three totals value labels to the widest one's width so the columns stay aligned. */
	private void equalizeTotalsLabelWidths()
	{
		JLabel[] labels = {totalHighLabel, totalLowLabel, totalAvgLabel};
		int maxW = 0;
		for (JLabel l : labels)
		{
			l.setPreferredSize(null);
			maxW = Math.max(maxW, l.getPreferredSize().width);
		}

		for (JLabel l : labels)
		{
			Dimension d = l.getPreferredSize();
			Dimension fixed = new Dimension(maxW, d.height);
			l.setPreferredSize(fixed);
			l.setMinimumSize(fixed);
			l.setMaximumSize(fixed);
		}
	}

	/** Builds one estimate row pairing a totals value label with its pulse-indicator label. */
	private JPanel buildTotalsRow(JLabel valueLabel, JLabel pulseLabel)
	{
		JPanel row = new JPanel();
		row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setBorder(ESTIMATE_ROW_BORDER_DEFAULT);
		row.add(valueLabel);
		row.add(Box.createHorizontalStrut(6));
		row.add(pulseLabel);
		row.add(Box.createHorizontalGlue());
		return row;
	}

	/** Creates a fixed-size label that hosts the ▲/▼ price-change pulse next to a value. */
	private JLabel createDeltaLabel()
	{
		JLabel label = new JLabel();
		label.setFont(FontManager.getRunescapeSmallFont());
		label.setPreferredSize(DELTA_LABEL_SIZE);
		label.setMinimumSize(DELTA_LABEL_SIZE);
		label.setMaximumSize(DELTA_LABEL_SIZE);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		return label;
	}

	/** Starts a price pulse on the label unless the configured indicator mode suppresses it. */
	private void pulseIfShown(JLabel label, int delta, PriceIndicatorMode mode)
	{
		if (mode == PriceIndicatorMode.OFF || (mode == PriceIndicatorMode.CHANGE && delta == 0))
			return;

		startPulse(label, delta);
	}

	/** Begins a color pulse on a label (green up / red down) reflecting the sign of a price change. */
	private void startPulse(JLabel label, int delta)
	{
		label.setText(delta > 0 ? "▲" : delta < 0 ? "▼" : "–");
		Color base = delta > 0 ? COLOR_HIGH : delta < 0 ? COLOR_LOW : LOADING_COLOR;
		label.setForeground(new Color(base.getRed(), base.getGreen(), base.getBlue(), 0));
		pulseEntries.add(new PulseEntry(label, base, System.currentTimeMillis()));
	}

	/** Timer tick that advances every active pulse's color toward its base, retiring finished ones. */
	private void updatePulses()
	{
		if (pulseEntries.isEmpty())
			return;

		long now = System.currentTimeMillis();
		Iterator<PulseEntry> it = pulseEntries.iterator();
		while (it.hasNext())
		{
			PulseEntry p = it.next();
			long elapsed = now - p.start;
			if (elapsed >= PULSE_DURATION_MS)
			{
				p.label.setText("");
				it.remove();
				continue;
			}

			float alpha = (float) Math.sin(Math.PI * elapsed / PULSE_DURATION_MS);
			p.label.setForeground(new Color(
					p.base.getRed(), p.base.getGreen(), p.base.getBlue(),
					Math.round(alpha * 255)));
		}
	}

	/** Timer tick that breathes the shared glow colour across every label still awaiting prices. */
	private void updateLoadingGlow()
	{
		if (loadingLabels.isEmpty())
			return;

		double phase = (System.currentTimeMillis() % LOADING_GLOW_PERIOD_MS) / (double) LOADING_GLOW_PERIOD_MS;
		double wave = (Math.sin(phase * 2 * Math.PI) + 1) / 2;
		float alpha = LOADING_GLOW_MIN_ALPHA + (1f - LOADING_GLOW_MIN_ALPHA) * (float) wave;
		Color glow = new Color(
				LOADING_COLOR.getRed(), LOADING_COLOR.getGreen(), LOADING_COLOR.getBlue(),
				Math.round(alpha * 255));

		for (JLabel label : loadingLabels)
			label.setForeground(glow);
	}

	/** Updates the footer's "updated N ago" text from the last price-refresh timestamp. */
	private void updateRefreshLabel()
	{
		if (lastPriceRefresh == null)
		{
			lastRefreshLabel.setText("Prices not yet loaded");
		}
		else
		{
			long secondsAgo = ChronoUnit.SECONDS.between(lastPriceRefresh, Instant.now());
			long rate = Math.max(30, config.priceRefreshSeconds());
			long secondsUntil = Math.max(0, rate - secondsAgo);
			lastRefreshLabel.setText("Price refresh in " + secondsUntil + " seconds");
		}
	}

	/**
	 * Filters the add-item search to items matching the typed query and lists the matches in a floating
	 * popup below the search field (#279), overlaying the panel rather than pushing the list down &mdash;
	 * matching the pop-out Dashboard search. Records the first hit so Enter can track it.
	 */
	private void onSearch(String query)
	{
		searchFirstResultId = -1;
		searchResultsContent.removeAll();

		if (query == null || query.trim().length() < 2)
		{
			hideSearchResults();
			return;
		}

		List<ItemPrice> results = itemManager.search(query);
		int shown = 0;
		for (ItemPrice item : results)
		{
			if (shown >= SEARCH_MAX_RESULTS)
				break;

			if (trackedItemIds.contains(item.getId()))
				continue;

			if (searchFirstResultId < 0)
				searchFirstResultId = item.getId();

			searchResultsContent.add(buildSearchResultRow(item.getId(), item.getName()));
			shown++;
		}

		if (shown == 0)
		{
			hideSearchResults();
			return;
		}

		int height = Math.min(shown, SEARCH_VISIBLE_ROWS) * SEARCH_ROW_HEIGHT + 2;

		if (searchResultsPopup.isVisible() && height == searchPopupHeight)
		{
			searchResultsContent.revalidate();
			searchResultsContent.repaint();
			return;
		}

		searchPopupHeight = height;
		searchResultsPopup.setPopupSize(searchField.getWidth(), height);
		searchResultsPopup.show(searchField, 0, searchField.getHeight());
		searchField.requestFocusInWindow();
	}

	/** Hides the floating search-results popup and clears its rows. */
	private void hideSearchResults()
	{
		if (searchResultsPopup == null)
			return;

		searchResultsPopup.setVisible(false);
		searchResultsContent.removeAll();
		searchPopupHeight = -1;
	}

	/** Tracks the first search hit (Enter in the search field), then clears the field and popup (#279). */
	private void trackFirstSearchResult()
	{
		if (searchFirstResultId <= 0)
			return;

		onAddItem.accept(searchFirstResultId, TrackItemMode.TRACK);
		searchField.setText("");
		hideSearchResults();
	}

	/** Builds one clickable row in the search-results dropdown that adds the item when clicked. */
	private JPanel buildSearchResultRow(int itemId, String itemName)
	{
		JPanel row = new JPanel(new BorderLayout());
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setBorder(new EmptyBorder(4, 6, 4, 6));
		row.setPreferredSize(new Dimension(10, SEARCH_ROW_HEIGHT));
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, SEARCH_ROW_HEIGHT));

		JLabel nameLabel = new JLabel();
		nameLabel.setForeground(Color.WHITE);
		nameLabel.setFont(FontManager.getRunescapeSmallFont());
		EllipsisText.set(nameLabel, itemName);

		JButton viewBtn = new JButton(buildEyeIcon(14));
		viewBtn.setPreferredSize(new Dimension(28, 22));
		viewBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		viewBtn.setForeground(Color.WHITE);
		viewBtn.setFocusPainted(false);
		viewBtn.setBorderPainted(true);
		viewBtn.setBorder(BorderFactory.createLineBorder(ColorScheme.LIGHT_GRAY_COLOR));
		viewBtn.setToolTipText("View prices only");
		viewBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		viewBtn.addActionListener(e ->
		{
			onAddItem.accept(itemId, TrackItemMode.VIEW);
			searchField.setText("");
			hideSearchResults();
		});

		Color addGreen = new Color(0, 153, 0);
		JButton addBtn = new JButton("+");
		addBtn.setPreferredSize(new Dimension(28, 22));
		addBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		addBtn.setForeground(addGreen);
		addBtn.setFocusPainted(false);
		addBtn.setBorderPainted(true);
		addBtn.setBorder(BorderFactory.createLineBorder(addGreen));
		addBtn.setToolTipText("Track item");
		addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addBtn.addActionListener(e ->
		{
			onAddItem.accept(itemId, TrackItemMode.TRACK);
			searchField.setText("");
			hideSearchResults();
		});

		JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
		buttonRow.setOpaque(false);
		buttonRow.add(viewBtn);
		buttonRow.add(addBtn);

		row.add(nameLabel, BorderLayout.CENTER);
		row.add(buttonRow, BorderLayout.EAST);

		MouseAdapter rowHover = new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				row.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				Point p = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), row);
				if (!row.contains(p))
					row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			}
		};
		addListenerRecursively(row, rowHover);
		row.setCursor(Cursor.getDefaultCursor());

		return row;
	}

	/**
	 * Rebuilds the main item list from the latest tracked items and totals.
	 *
	 * <p>This is the primary entry point the plugin calls after any data change:
	 * it repopulates the rows, updates the value/profit totals and the refresh
	 * timestamp, and (when {@code indicatorMode} permits) starts pulse animations
	 * for items whose price moved.
	 */
	public void rebuild(List<TrackedItem> rawItems, Instant newLastPriceRefresh,
			PriceIndicatorMode indicatorMode, boolean loggedIn,
			List<CategoryState> categories, boolean favoritesCollapsed, boolean uncategorizedCollapsed)
	{
		this.lastPriceRefresh = newLastPriceRefresh;
		this.categories = categories != null ? categories : new ArrayList<>();
		this.favoritesCollapsed = favoritesCollapsed;
		this.uncategorizedCollapsed = uncategorizedCollapsed;
		SortMode sortMode = config.sortMode();
		final List<TrackedItem> items;
		if (sortMode != SortMode.MANUAL)
		{
			items = new ArrayList<>(rawItems);
			sortMode.sort(items, config.sortReversed());
		}
		else
		{
			items = rawItems;
		}

		updateSortToggle();
		if (sortMode != SortMode.MANUAL && reorderMode)
			toggleReorderMode();

		reorderToggle.setVisible(sortMode == SortMode.MANUAL);

		trackedItemIds.clear();
		currentItems.clear();
		orderedItemIds.clear();
		for (TrackedItem item : items)
		{
			trackedItemIds.add(item.getItemId());
			currentItems.put(item.getItemId(), item);
			orderedItemIds.add(item.getItemId());
		}

		rowIconCache.keySet().removeIf(key -> !trackedItemIds.contains((int) (key >> 32)));

		if (!loggedIn)
		{
			SwingUtilities.invokeLater(() ->
			{
				detailView.onLeaveDetail();
				closePopouts();
				footerPanel.setVisible(false);
				cardLayout.show(cardsHost, CARD_LOGGED_OUT);
			});
			return;
		}

		if (detailView.getBoundItemId() > 0)
		{
			if (!detailView.onRebuild() && !currentItems.isEmpty())
				SwingUtilities.invokeLater(this::showMain);
		}
		else
		{
			SwingUtilities.invokeLater(() ->
			{
				footerPanel.setVisible(true);
				cardLayout.show(cardsHost, CARD_MAIN);
			});
		}

		SwingUtilities.invokeLater(() ->
		{
			loadingLabels.clear();
			pulseEntries.clear();
			clearButton.setEnabled(!trackedItemIds.isEmpty());
			updateCompactToggle();
			totalHighDeltaLabel.setText("");
			totalLowDeltaLabel.setText("");
			totalAvgDeltaLabel.setText("");

			long totalHigh = 0, totalLow = 0, totalAvg = 0;
			long totalCostBasis = 0;
			long totalRealized = 0;
			long totalSuspendedValue = 0;
			boolean anyProfitData = false;
			long prevPriceTotalHigh = 0, prevPriceTotalLow = 0, prevPriceTotalAvg = 0;
			boolean anyDeltas = false;
			ValueFormat totalFmt = config.geEstimatesFormat();
			boolean showEstHigh = config.showEstHigh();
			boolean showEstLow = config.showEstLow();
			boolean showEstAvg = config.showEstAvg();
			boolean showEstProfit = config.showEstProfit();

			for (TrackedItem item : items)
			{
				totalHigh += item.getHighValue();
				totalLow  += item.getLowValue();
				totalAvg  += item.getAvgValue();
				totalSuspendedValue += item.getSuspendedValue();
				long realized = item.getRealizedProfit();
				totalRealized += realized;
				if (item.isCostBasisInitialized())
				{
					totalCostBasis += item.getCostBasis();
					anyProfitData = true;
				}

				if (realized != 0)
					anyProfitData = true;

				if (item.isHasDeltas())
				{
					anyDeltas = true;
					prevPriceTotalHigh += (long) item.getQuantity() * item.getPrevHighPrice();
					prevPriceTotalLow  += (long) item.getQuantity() * item.getPrevLowPrice();
					prevPriceTotalAvg  += (long) item.getQuantity() * item.getPrevAvgPrice();
				}
				else
				{
					prevPriceTotalHigh += item.getHighValue();
					prevPriceTotalLow  += item.getLowValue();
					prevPriceTotalAvg  += item.getAvgValue();
				}
			}

			renderTrackedRows(items, indicatorMode);

			boolean hasPrices = items.stream().anyMatch(TrackedItem::hasPrices);

			updateSessionLine(items, hasPrices);

			totalHighRow.setVisible(showEstHigh);
			totalLowRow.setVisible(showEstLow);
			totalAvgRow.setVisible(showEstAvg);

			totalHighLabel.setText("High:  " + (hasPrices ? formatTotalGp(totalHigh, totalFmt) : "—"));
			totalLowLabel.setText( "Low:   " + (hasPrices ? formatTotalGp(totalLow,  totalFmt) : "—"));
			totalAvgLabel.setText( "Avg:   " + (hasPrices ? formatTotalGp(totalAvg, totalFmt) : "—"));
			if (hasPrices)
			{
				applyTotalTooltip(totalHighLabel, totalHigh, totalFmt);
				applyTotalTooltip(totalLowLabel,  totalLow,  totalFmt);
				applyTotalTooltip(totalAvgLabel,  totalAvg,  totalFmt);
			}
			else
			{
				totalHighLabel.setToolTipText(null);
				totalLowLabel.setToolTipText(null);
				totalAvgLabel.setToolTipText(null);
			}

			equalizeTotalsLabelWidths();
			bottomPanel.setVisible(config.showGeEstimates() && !reorderMode);
			updatePortfolioChartButton();
			portfolioPopoutRefreshers.forEach(Runnable::run);
			applyEstimatesPosition(config.geEstimatesPosition());
			applyEstimatesSpacing(config.geEstimatesSpacing());

			updateCoinsIcon(hasPrices ? totalAvg : 0);

			if (indicatorMode != PriceIndicatorMode.OFF && hasPrices && anyDeltas)
			{
				pulseIfShown(totalHighDeltaLabel, Long.compare(totalHigh, prevPriceTotalHigh), indicatorMode);
				pulseIfShown(totalLowDeltaLabel,  Long.compare(totalLow,  prevPriceTotalLow),  indicatorMode);
				pulseIfShown(totalAvgDeltaLabel,  Long.compare(totalAvg,  prevPriceTotalAvg),  indicatorMode);
			}

			long totalProfit = (totalAvg + totalSuspendedValue - totalCostBasis) + totalRealized;
			if (anyProfitData && hasPrices && showEstProfit)
			{
				String sign = totalProfit > 0 ? "+" : "";
				profitLabel.setText(sign + formatTotalGp(totalProfit, totalFmt));
				applyTotalTooltip(profitLabel, totalProfit, totalFmt);
				profitLabel.setForeground(totalProfit == 0
						? ColorScheme.LIGHT_GRAY_COLOR
						: (totalProfit > 0 ? COLOR_HIGH : COLOR_LOW));
				profitSection.setVisible(true);
			}
			else
			{
				profitSection.setVisible(false);
			}

			boolean compact = config.compactView();
			totalsRows.setVisible(!compact);
			compactTotalsRows.setVisible(compact);
			if (compact)
				updateCompactTotals(items.size(), totalAvg, totalProfit, hasPrices,
						anyProfitData && showEstProfit, totalFmt);

			trackedItemsPanel.revalidate();
			trackedItemsPanel.repaint();
		});
	}

	/**
	 * Clears and re-renders the tracked-item rows (empty placeholder, or the grouped rows),
	 * retaining the inputs so {@link #toggleReorderMode()} can re-render the manage layout
	 * without a full plugin refresh.
	 */
	private void renderTrackedRows(List<TrackedItem> items, PriceIndicatorMode indicatorMode)
	{
		lastRenderItems = items;
		lastRenderIndicatorMode = indicatorMode;
		loadingLabels.clear();

		if (items.isEmpty())
		{
			renderEmptyState();
			return;
		}

		List<RowSection> sections = computeSections(items);
		String sig = structuralSignature(sections);

		if (!reorderMode && sig.equals(lastStructuralSig) && cacheCovers(sections))
			updateRowsInPlace(sections, indicatorMode);
		else
			fullRebuild(sections, indicatorMode, sig);

		trackedItemsPanel.revalidate();
		trackedItemsPanel.repaint();
	}

	/** Clears the list to the "no items tracked" placeholder and resets the render cache (#275). */
	private void renderEmptyState()
	{
		trackedItemsPanel.removeAll();
		rowViews.clear();
		groupTotalLabels.clear();
		lastStructuralSig = "EMPTY";

		PluginErrorPanel errorPanel = new PluginErrorPanel();
		errorPanel.setContent("No items tracked", "Search above to add an item to track.");

		JPanel emptyWrapper = new JPanel(new BorderLayout());
		emptyWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		emptyWrapper.add(errorPanel, BorderLayout.CENTER);
		trackedItemsPanel.add(emptyWrapper);

		trackedItemsPanel.revalidate();
		trackedItemsPanel.repaint();
	}

	/**
	 * Computes the ordered, filtered display sections (#275): a single flat section when no grouping is
	 * active, otherwise the Favorites pseudo-group (pinned on top), each user category in order, then
	 * Uncategorized. Empty groups are skipped. Also refreshes {@link #groupingActive}.
	 */
	private List<RowSection> computeSections(List<TrackedItem> items)
	{
		boolean hasFavorites = items.stream().anyMatch(TrackedItem::isFavorite);
		groupingActive = hasFavorites || !categories.isEmpty();

		List<RowSection> sections = new ArrayList<>();

		if (!groupingActive)
		{
			List<TrackedItem> visible = new ArrayList<>();
			for (TrackedItem item : items)
				if (matchesFilter(item))
					visible.add(item);

			if (!visible.isEmpty())
				sections.add(new RowSection(null, null, false, visible));

			return sections;
		}

		Set<String> categoryNames = new HashSet<>();
		for (CategoryState cat : categories)
			categoryNames.add(cat.getName());

		List<TrackedItem> favorites = new ArrayList<>();
		for (TrackedItem item : items)
			if (item.isFavorite() && matchesFilter(item))
				favorites.add(item);

		if (!favorites.isEmpty())
			sections.add(new RowSection("★ Favorites", CategoryState.FAVORITES_KEY, favoritesCollapsed, favorites));

		for (CategoryState cat : categories)
		{
			List<TrackedItem> inCategory = new ArrayList<>();
			for (TrackedItem item : items)
				if (!item.isFavorite() && cat.getName().equals(item.getCategory()) && matchesFilter(item))
					inCategory.add(item);

			if (!inCategory.isEmpty())
				sections.add(new RowSection(cat.getName(), cat.getName(), cat.isCollapsed(), inCategory));
		}

		List<TrackedItem> uncategorized = new ArrayList<>();
		for (TrackedItem item : items)
		{
			String cat = item.getCategory();
			boolean uncat = cat == null || cat.isEmpty() || !categoryNames.contains(cat);
			if (!item.isFavorite() && uncat && matchesFilter(item))
				uncategorized.add(item);
		}

		if (!uncategorized.isEmpty())
			sections.add(new RowSection("Uncategorized", CategoryState.UNCATEGORIZED_KEY, uncategorizedCollapsed,
					uncategorized));

		return sections;
	}

	/**
	 * @return a signature of the render's structure (scaffolding globals, group order/collapse, and each
	 *         rendered row's id and compact shape) for the in-place gate (#275); value-only data such as
	 *         prices, quantities, deltas and group totals is excluded so it can be refreshed in place.
	 */
	private String structuralSignature(List<RowSection> sections)
	{
		StringBuilder sb = new StringBuilder();
		sb.append('G')
				.append(config.showQuantityValue() ? 1 : 0)
				.append(config.showScreenOverlay() ? 1 : 0)
				.append(config.compactView() ? 1 : 0)
				.append(groupingActive ? 1 : 0)
				.append(reorderMode ? 1 : 0)
				.append(config.quickActionDelivery().ordinal());

		for (RowSection s : sections)
		{
			sb.append(";H:")
					.append(s.title)
					.append(':')
					.append(s.key)
					.append(':')
					.append(s.collapsed);
			if (s.collapsed)
				continue;

			for (TrackedItem item : s.items)
				sb.append(";I:").append(item.getItemId())
						.append(':').append(config.compactView() || item.isCompact() ? 1 : 0);
		}

		return sb.toString();
	}

	/** @return whether every row the plan will render already has a cached {@link RowView} (#275). */
	private boolean cacheCovers(List<RowSection> sections)
	{
		for (RowSection s : sections)
		{
			if (s.collapsed)
				continue;

			for (TrackedItem item : s.items)
				if (!rowViews.containsKey(item.getItemId()))
					return false;
		}

		return true;
	}

	/** Rebuilds the whole list from scratch, repopulating the row/header caches and storing the signature (#275). */
	private void fullRebuild(List<RowSection> sections, PriceIndicatorMode indicatorMode, String sig)
	{
		trackedItemsPanel.removeAll();
		rowViews.clear();
		groupTotalLabels.clear();

		for (RowSection s : sections)
		{
			if (s.title != null)
			{
				trackedItemsPanel.add(buildGroupHeader(s.title, s.key, s.collapsed, sectionTotal(s)));
				trackedItemsPanel.add(Box.createVerticalStrut(4));
				if (s.collapsed)
					continue;
			}

			for (TrackedItem item : s.items)
			{
				if (reorderMode)
				{
					trackedItemsPanel.add(buildManageRow(item, s.items));
				}
				else
				{
					RowView rv = buildRowView(item, indicatorMode, s.items);
					rowViews.put(item.getItemId(), rv);
					trackedItemsPanel.add(rv.card);
				}

				trackedItemsPanel.add(Box.createVerticalStrut(4));
			}
		}

		lastStructuralSig = sig;
	}

	/** Refreshes group-header totals and each row's values in place against the cached scaffolding (#275). */
	private void updateRowsInPlace(List<RowSection> sections, PriceIndicatorMode indicatorMode)
	{
		for (RowSection s : sections)
		{
			if (s.title != null)
			{
				JLabel totalLabel = groupTotalLabels.get(s.key);
				if (totalLabel != null)
				{
					long total = sectionTotal(s);
					totalLabel.setText(GpFormat.shortValue(total));
					totalLabel.setToolTipText(GpFormat.grouped(total) + " gp");
				}

				if (s.collapsed)
					continue;
			}

			for (TrackedItem item : s.items)
			{
				RowView rv = rowViews.get(item.getItemId());
				if (rv != null)
					populateRow(rv, item, indicatorMode);
			}
		}
	}

	/** @return the sum of average values across a section's items, for its group-header total (#275). */
	private static long sectionTotal(RowSection s)
	{
		long total = 0;
		for (TrackedItem item : s.items)
			total += item.getAvgValue();

		return total;
	}

	/** @return whether the item matches the active tracked-list name filter (always true when the filter is empty). */
	private boolean matchesFilter(TrackedItem item)
	{
		return trackedFilter.isEmpty() || item.getName().toLowerCase().contains(trackedFilter);
	}

	/** Re-renders the rows against the updated tracked-list filter text. */
	private void onTrackedFilterChanged()
	{
		String text = trackedFilterField.getText();
		trackedFilter = text == null ? "" : text.trim().toLowerCase();
		renderTrackedRows(lastRenderItems, lastRenderIndicatorMode);
	}

	/** Toggles the tracked-list filter field via the header filter button, focusing it when shown. */
	private void toggleTrackedFilter()
	{
		boolean show = !trackedFilterField.isVisible();
		setTrackedFilterVisible(show);
		updateFilterToggle();

		if (show)
			trackedFilterField.requestFocusInWindow();
	}

	/** Sets the filter field's visibility, clearing any active filter when it is hidden. */
	private void setTrackedFilterVisible(boolean visible)
	{
		if (visible == trackedFilterField.isVisible())
			return;

		trackedFilterField.setVisible(visible);

		if (!visible && !trackedFilter.isEmpty())
		{
			trackedFilter = "";
			trackedFilterField.setText("");
			renderTrackedRows(lastRenderItems, lastRenderIndicatorMode);
		}

		trackedFilterField.revalidate();
		trackedFilterField.repaint();
	}

	/** Updates the header filter button's funnel icon, tinting it gold while the filter field is shown. */
	private void updateFilterToggle()
	{
		if (filterToggle != null)
			filterToggle.setIcon(filterIcon(trackedFilterField != null && trackedFilterField.isVisible()
					? COLOR_AVG : ColorScheme.LIGHT_GRAY_COLOR));
	}

	/**
	 * Paints a small monochrome funnel (filter) icon in the given colour: a wide top bar
	 * tapering to a narrow central stem.
	 */
	private static Icon filterIcon(Color color)
	{
		int size = 14;
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color);

		g.fillPolygon(
				new int[]{1, size - 1, size / 2 + 1, size / 2 + 1, size / 2 - 1, size / 2 - 1},
				new int[]{2, 2, size / 2, size - 1, size - 1, size / 2},
				6);

		g.dispose();
		return new ImageIcon(img);
	}

	/**
	 * Paints a small monochrome "dashboard" glyph in the given colour: a window outline with a title
	 * bar and three content columns, echoing the pop-out dashboard's panel layout (#109).
	 */
	static Icon dashboardIcon(Color color)
	{
		int size = 16;
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color);

		g.drawRect(1, 2, 13, 11);
		g.fillRect(2, 3, 12, 2);
		g.fillRect(3, 7, 2, 5);
		g.fillRect(7, 7, 2, 5);
		g.fillRect(11, 7, 2, 5);

		g.dispose();
		return new ImageIcon(img);
	}

	/** Draws two overlapping rings — the Venn-overlap Compare glyph (#280) — tinted {@code color}. */
	static Icon compareIcon(Color color)
	{
		int size = 16;
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color);

		g.drawOval(1, 3, 10, 10);
		g.drawOval(5, 3, 10, 10);

		g.dispose();
		return new ImageIcon(img);
	}

	/**
	 * Draws a "view detail" glyph tinted {@code color}: a document with two text lines and a magnifying
	 * glass overlapping its lower-right corner, echoing the detail view's document-and-lens motif (#299).
	 */
	private static Icon detailIcon(Color color)
	{
		int size = 16;
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color);

		g.drawRect(1, 1, 8, 12);
		g.fillRect(3, 4, 4, 1);
		g.fillRect(3, 7, 4, 1);

		g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawOval(8, 8, 5, 5);
		g.drawLine(13, 13, 15, 15);

		g.dispose();
		return new ImageIcon(img);
	}

	/** Draws a five-point star (favorite) tinted {@code color}; {@code filled} fills it, else outlines it. */
	private static Icon starMenuIcon(Color color, boolean filled)
	{
		int size = 14;
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color);

		double centre = size / 2.0;
		double outer = centre - 1.0;
		double inner = outer * 0.42;
		int[] xs = new int[10];
		int[] ys = new int[10];
		for (int i = 0; i < 10; i++)
		{
			double r = i % 2 == 0 ? outer : inner;
			double angle = -Math.PI / 2 + i * Math.PI / 5;
			xs[i] = (int) Math.round(centre + Math.cos(angle) * r);
			ys[i] = (int) Math.round(centre + Math.sin(angle) * r);
		}

		if (filled)
			g.fillPolygon(xs, ys, 10);
		else
			g.drawPolygon(xs, ys, 10);

		g.dispose();
		return new ImageIcon(img);
	}

	/** Draws two stacked bars (per-item compact) tinted {@code color}. */
	private static Icon compactMenuIcon(Color color)
	{
		int size = 14;
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color);

		g.fillRect(2, 4, size - 4, 2);
		g.fillRect(2, 8, size - 4, 2);

		g.dispose();
		return new ImageIcon(img);
	}

	/** Draws an "✕" cross (remove) tinted {@code color}. */
	private static Icon removeMenuIcon(Color color)
	{
		int size = 14;
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color);
		g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		g.drawLine(3, 3, size - 3, size - 3);
		g.drawLine(size - 3, 3, 3, size - 3);

		g.dispose();
		return new ImageIcon(img);
	}

	/** Draws a bulleted-list glyph — three dots, each followed by a line — tinted {@code color}. */
	private static Icon categoriesIcon(Color color)
	{
		int size = 14;
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color);

		for (int y : new int[]{1, 6, 11})
		{
			g.fillOval(1, y, 3, 3);
			g.fillRect(6, y, 7, 3);
		}

		g.dispose();
		return new ImageIcon(img);
	}

	/**
	 * Installs grey↔gold hover colouring on a header toggle: an unselected (grey) button
	 * turns gold while hovered, a selected (gold) button turns grey, and its resting
	 * state colour is repainted on exit.
	 *
	 * @param selected whether the button is currently in its selected/gold state
	 * @param apply    paints the button in a colour ({@code setForeground} for glyph
	 *                     buttons, {@code setIcon} for icon buttons)
	 * @param restore  repaints the button's resting state colour
	 */
	private static void installToggleHover(JLabel button, BooleanSupplier selected,
			Consumer<Color> apply, Runnable restore)
	{
		button.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				apply.accept(selected.getAsBoolean() ? ColorScheme.LIGHT_GRAY_COLOR : COLOR_AVG);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				restore.run();
			}
		});
	}

	/** Paints a small monochrome monitor (on-screen overlay) icon in the given colour. */
	private static Icon overlayIcon(Color color)
	{
		int size = 16;
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color);

		g.drawRect(2, 2, size - 5, size - 8);
		g.fillRect(size / 2 - 2, size - 5, 4, 2);
		g.fillRect(size / 2 - 4, size - 3, 8, 1);

		g.dispose();
		return new ImageIcon(img);
	}

	/** Paints a small firework burst (eight rays capped with sparks) for the "What's New" badge. */
	private static Icon sparkleIcon(Color color)
	{
		int size = 14;
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color);
		g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		double centre = size / 2.0;
		double inner = 1.6;
		double outer = centre - 1.0;
		for (int i = 0; i < 8; i++)
		{
			double angle = Math.PI * i / 4.0;
			double length = i % 2 == 0 ? outer : outer - 2.2;
			int x1 = (int) Math.round(centre + Math.cos(angle) * inner);
			int y1 = (int) Math.round(centre + Math.sin(angle) * inner);
			int x2 = (int) Math.round(centre + Math.cos(angle) * length);
			int y2 = (int) Math.round(centre + Math.sin(angle) * length);
			g.drawLine(x1, y1, x2, y2);
			g.fillOval(x2 - 1, y2 - 1, 2, 2);
		}

		g.dispose();
		return new ImageIcon(img);
	}

	/** @return the position of {@code itemId} within {@code list}, or -1 if absent. */
	private static int indexOfItem(List<TrackedItem> list, int itemId)
	{
		for (int i = 0; i < list.size(); i++)
			if (list.get(i).getItemId() == itemId)
				return i;

		return -1;
	}

	/**
	 * Builds a clickable accordion header (chevron + title + group total value) that
	 * toggles the group's collapsed state.
	 */
	private JPanel buildGroupHeader(String title, String groupKey, boolean collapsed, long groupTotal)
	{
		JPanel header = new JPanel(new BorderLayout(6, 0))
		{
			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
			}
		};
		header.setBackground(ColorScheme.DARK_GRAY_COLOR);
		header.setBorder(new EmptyBorder(4, 2, 2, 4));
		header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		header.putClientProperty(GROUP_HEADER_KEY, Boolean.TRUE);

		JLabel chevron = new JLabel(collapsed ? "▸" : "▾");
		chevron.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		chevron.setFont(FontManager.getRunescapeSmallFont());

		JLabel titleLabel = new JLabel(title);
		titleLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		titleLabel.setFont(FontManager.getRunescapeBoldFont());

		JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		left.setBackground(ColorScheme.DARK_GRAY_COLOR);
		left.add(chevron);
		left.add(titleLabel);

		JLabel totalLabel = new JLabel(GpFormat.shortValue(groupTotal));
		totalLabel.setForeground(StockpileColors.MUTED);
		totalLabel.setFont(FontManager.getRunescapeSmallFont());
		totalLabel.setToolTipText(GpFormat.grouped(groupTotal) + " gp");
		groupTotalLabels.put(groupKey, totalLabel);

		header.add(left, BorderLayout.WEST);
		header.add(totalLabel, BorderLayout.EAST);
		header.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (onSetGroupCollapsed != null)
					onSetGroupCollapsed.accept(groupKey, !collapsed);
			}
		});

		return header;
	}

	private static final Color STAR_HIDDEN = new Color(0, 0, 0, 0);
	private static final Color REMOVE_COLOR = new Color(200, 60, 60);
	private static final Color STAR_DIM = new Color(110, 110, 110);
	private static final Color STAR_PREVIEW = new Color(255, 235, 140);
	private static final String STAR_ROW_HOVERED = "stockpile.starRowHovered";
	private static final String STAR_HOVERED = "stockpile.starHovered";

	/**
	 * Builds the favorite-toggle star shown beneath each row's remove button. Like the
	 * remove button it is hidden until the row is hovered; hovering the star itself previews
	 * the toggle (fills light gold to add a favorite, or drops the gold to remove one).
	 */
	private JLabel buildFavoriteStar(TrackedItem item)
	{
		JLabel star = new JLabel("★", SwingConstants.CENTER);
		star.setPreferredSize(new Dimension(20, 20));
		star.setMaximumSize(new Dimension(20, 20));
		star.setFont(FontManager.getRunescapeSmallFont());
		star.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		star.setToolTipText(item.isFavorite() ? "Remove from favorites" : "Add to favorites");
		star.putClientProperty(STAR_ROW_HOVERED, false);
		star.putClientProperty(STAR_HOVERED, false);
		star.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (onSetFavorite != null)
					onSetFavorite.accept(item.getItemId(), !item.isFavorite());
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				star.putClientProperty(STAR_HOVERED, true);
				refreshFavoriteStar(star, item.isFavorite());
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				star.putClientProperty(STAR_HOVERED, false);
				refreshFavoriteStar(star, item.isFavorite());
			}
		});

		refreshFavoriteStar(star, item.isFavorite());

		return star;
	}

	/**
	 * Applies a favorite star's visual from its row-hover/star-hover client flags: hidden
	 * when its row isn't hovered, the resting gold/grey glyph when the row is hovered, and a
	 * preview (light-gold fill to add, or grey outline to remove) when the star itself is hovered.
	 */
	private void refreshFavoriteStar(JLabel star, boolean favorite)
	{
		boolean rowHovered = Boolean.TRUE.equals(star.getClientProperty(STAR_ROW_HOVERED));
		boolean starHovered = Boolean.TRUE.equals(star.getClientProperty(STAR_HOVERED));

		if (!rowHovered)
		{
			star.setText(favorite ? "★" : "☆");
			star.setForeground(STAR_HIDDEN);
			return;
		}

		if (starHovered)
		{
			star.setText(favorite ? "☆" : "★");
			star.setForeground(favorite ? STAR_DIM : STAR_PREVIEW);
		}
		else
		{
			star.setText(favorite ? "★" : "☆");
			star.setForeground(favorite ? COLOR_AVG : STAR_DIM);
		}
	}

	private static final String UNCATEGORIZED_LABEL = "Uncategorized";
	private static final String NEW_CATEGORY_LABEL = "+ New category…";

	/**
	 * Builds the per-row category picker used in the manage row: assigns the item to an existing
	 * category, clears it to Uncategorized, or prompts to create-and-assign a new one.
	 */
	private JComboBox<String> buildCategoryPicker(TrackedItem item)
	{
		JComboBox<String> picker = new JComboBox<>();
		picker.setFont(FontManager.getRunescapeSmallFont());
		picker.addItem(UNCATEGORIZED_LABEL);
		for (CategoryState cat : categories)
			picker.addItem(cat.getName());

		picker.addItem(NEW_CATEGORY_LABEL);

		final String current = item.getCategory();
		final String currentSelection = current == null || current.isEmpty() ? UNCATEGORIZED_LABEL : current;
		picker.setSelectedItem(currentSelection);

		picker.addActionListener(e ->
		{
			String selected = (String) picker.getSelectedItem();
			if (selected == null || selected.equals(currentSelection))
				return;

			if (NEW_CATEGORY_LABEL.equals(selected))
			{
				String name = JOptionPane.showInputDialog(this, "New category name:",
						"New Category", JOptionPane.PLAIN_MESSAGE);
				if (name != null && !name.trim().isEmpty())
				{
					categoryActions.create(name.trim());
					categoryActions.setItemCategory(item.getItemId(), name.trim());
				}
				else
				{
					picker.setSelectedItem(currentSelection);
				}

				return;
			}

			categoryActions.setItemCategory(item.getItemId(),
					UNCATEGORIZED_LABEL.equals(selected) ? null : selected);
		});

		return picker;
	}

	/**
	 * Category prompt shown at track time (#211): a modal dropdown of the existing categories plus
	 * Uncategorized and a create-new option. Choosing a category (or a freshly created one) assigns it
	 * to the just-tracked item; Uncategorized or cancel leaves it uncategorized. A no-op if the item
	 * is gone by the time this runs.
	 */
	public void promptCategoryForItem(int itemId)
	{
		TrackedItem item = currentItems.get(itemId);
		if (item == null)
			return;

		List<String> options = new ArrayList<>();
		options.add(UNCATEGORIZED_LABEL);
		for (CategoryState cat : categories)
			options.add(cat.getName());

		options.add(NEW_CATEGORY_LABEL);

		String choice = (String) JOptionPane.showInputDialog(this,
				"Category for " + item.getName() + ":",
				"Track in category",
				JOptionPane.QUESTION_MESSAGE,
				null,
				options.toArray(),
				UNCATEGORIZED_LABEL);
		if (choice == null || UNCATEGORIZED_LABEL.equals(choice))
			return;

		if (NEW_CATEGORY_LABEL.equals(choice))
		{
			String name = JOptionPane.showInputDialog(this, "New category name:",
					"New Category", JOptionPane.PLAIN_MESSAGE);
			if (name != null && !name.trim().isEmpty())
			{
				categoryActions.create(name.trim());
				categoryActions.setItemCategory(itemId, name.trim());
			}

			return;
		}

		categoryActions.setItemCategory(itemId, choice);
	}

	/**
	 * Opens the modal Manage Categories dialog: create, rename, delete, and reorder categories.
	 * Each action updates the dialog's list immediately and forwards to the plugin via
	 * {@link #categoryActions}, which persists and rebuilds the panel.
	 */
	private void openManageCategoriesDialog()
	{
		JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Manage Categories");
		dialog.setModal(true);

		DefaultListModel<String> model = new DefaultListModel<>();
		for (CategoryState cat : categories)
			model.addElement(cat.getName());

		JList<String> list = new JList<>(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		installCategoryDragReorder(list, model);

		JButton newBtn = new JButton("New");
		newBtn.addActionListener(e ->
		{
			String name = JOptionPane.showInputDialog(dialog, "Category name:",
					"New Category", JOptionPane.PLAIN_MESSAGE);
			if (name == null || name.trim().isEmpty())
				return;

			String trimmed = name.trim();
			if (containsIgnoreCase(model, trimmed))
				return;

			model.addElement(trimmed);
			categoryActions.create(trimmed);
		});

		JButton renameBtn = new JButton("Rename");
		renameBtn.addActionListener(e ->
		{
			int i = list.getSelectedIndex();
			if (i < 0)
				return;

			String old = model.get(i);
			Object input = JOptionPane.showInputDialog(dialog, "Rename category:",
					"Rename", JOptionPane.PLAIN_MESSAGE, null, null, old);
			if (input == null || input.toString().trim().isEmpty())
				return;

			String trimmed = input.toString().trim();
			if (trimmed.equals(old) || containsIgnoreCase(model, trimmed))
				return;

			model.set(i, trimmed);
			categoryActions.rename(old, trimmed);
		});

		JButton deleteBtn = new JButton("Delete");
		deleteBtn.addActionListener(e ->
		{
			int i = list.getSelectedIndex();
			if (i < 0)
				return;

			String name = model.get(i);
			int choice = JOptionPane.showConfirmDialog(dialog,
					"Delete category \"" + name + "\"? Its items move to Uncategorized.",
					"Delete Category", JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION)
			{
				model.remove(i);
				categoryActions.delete(name);
			}
		});

		JButton upBtn = new JButton("↑");
		upBtn.addActionListener(e -> moveCategoryInDialog(list, model, -1));

		JButton downBtn = new JButton("↓");
		downBtn.addActionListener(e -> moveCategoryInDialog(list, model, 1));

		JButton autoBtn = new JButton("Auto");
		autoBtn.setToolTipText("Auto-categorize tracked items by their characteristics");
		autoBtn.addActionListener(e -> autoCategorizeFromDialog(dialog));

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
		actions.add(newBtn);
		actions.add(renameBtn);
		actions.add(deleteBtn);
		actions.add(upBtn);
		actions.add(downBtn);
		actions.add(autoBtn);

		JScrollPane scroll = new JScrollPane(list);
		scroll.setPreferredSize(new Dimension(220, 200));

		JPanel content = new JPanel(new BorderLayout(0, 6));
		content.setBorder(new EmptyBorder(8, 8, 8, 8));
		content.add(scroll, BorderLayout.CENTER);
		content.add(actions, BorderLayout.SOUTH);

		dialog.setContentPane(content);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	/**
	 * Prompts for the auto-categorize scope (uncategorized only vs. everything), runs it via
	 * {@link #categoryActions}, reports the result, and closes the dialog so it reopens with the
	 * freshly generated categories.
	 */
	private void autoCategorizeFromDialog(JDialog dialog)
	{
		Object[] options = {"Only uncategorized", "Re-categorize all", "Cancel"};
		int choice = JOptionPane.showOptionDialog(dialog,
				"Assign tracked items to categories from the wiki's item groupings?\n"
						+ "\"Only uncategorized\" keeps your manual assignments.",
				"Auto-categorize", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0]);
		if (choice != 0 && choice != 1)
			return;

		String result = categoryActions.autoCategorize(choice == 1);
		JOptionPane.showMessageDialog(dialog, result, "Auto-categorize", JOptionPane.INFORMATION_MESSAGE);
		dialog.dispose();
	}

	/**
	 * Enables drag-and-drop reordering on the Manage Categories list (#212): dragging a category and
	 * dropping it between two others sets the order in one gesture, committing through the same
	 * {@link CategoryActions#reorder(String, int)} path as the ↑/↓ buttons. {@link DropMode#INSERT}
	 * draws the insertion line, so the drop target is shown while dragging.
	 */
	private void installCategoryDragReorder(JList<String> list, DefaultListModel<String> model)
	{
		list.setDragEnabled(true);
		list.setDropMode(DropMode.INSERT);
		list.setTransferHandler(new TransferHandler()
		{
			@Override
			public int getSourceActions(JComponent c)
			{
				return MOVE;
			}

			@Override
			protected Transferable createTransferable(JComponent c)
			{
				return new StringSelection(String.valueOf(list.getSelectedIndex()));
			}

			@Override
			public boolean canImport(TransferSupport support)
			{
				return support.isDrop() && support.isDataFlavorSupported(DataFlavor.stringFlavor);
			}

			@Override
			public boolean importData(TransferSupport support)
			{
				if (!canImport(support))
					return false;

				int from = sourceIndex(support);
				if (from < 0 || from >= model.size())
					return false;

				int drop = ((JList.DropLocation) support.getDropLocation()).getIndex();
				int target = drop > from ? drop - 1 : drop;
				if (target < 0)
					target = 0;

				if (target >= model.size())
					target = model.size() - 1;

				if (target == from)
					return false;

				String name = model.remove(from);
				model.add(target, name);
				list.setSelectedIndex(target);
				categoryActions.reorder(name, target);
				return true;
			}

			/** @return the dragged row index carried in the transfer, or -1 when unreadable. */
			private int sourceIndex(TransferSupport support)
			{
				try
				{
					Object data = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
					return Integer.parseInt(data.toString());
				}
				catch (Exception ex)
				{
					return -1;
				}
			}
		});
	}

	/** Moves the selected dialog category by {@code delta} and forwards the new index to the plugin. */
	private void moveCategoryInDialog(JList<String> list, DefaultListModel<String> model, int delta)
	{
		int i = list.getSelectedIndex();
		if (i < 0)
			return;

		int j = i + delta;
		if (j < 0 || j >= model.size())
			return;

		String name = model.remove(i);
		model.add(j, name);
		list.setSelectedIndex(j);
		categoryActions.reorder(name, j);
	}

	/** @return whether the list model already contains {@code value}, ignoring case. */
	private static boolean containsIgnoreCase(DefaultListModel<String> model, String value)
	{
		for (int i = 0; i < model.size(); i++)
			if (model.get(i).equalsIgnoreCase(value))
				return true;

		return false;
	}

	/** Builds an 18px item-icon label backed by {@link #rowIconCache}, loading asynchronously on a miss. */
	private JLabel buildRowIcon(TrackedItem item)
	{
		JLabel iconLabel = new JLabel();
		iconLabel.setVerticalAlignment(SwingConstants.CENTER);
		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		applyRowIcon(iconLabel, item);
		return iconLabel;
	}

	/** Sets a label's 18px quantity-aware item icon from {@link #rowIconCache}, loading asynchronously on a miss. */
	private void applyRowIcon(JLabel iconLabel, TrackedItem item)
	{
		long key = iconCacheKey(item);
		ImageIcon cached = rowIconCache.get(key);
		if (cached != null)
		{
			iconLabel.setIcon(cached);
			return;
		}

		AsyncBufferedImage icon = itemManager.getImage(item.getItemId(), item.iconStackSize(), item.isStackable());
		icon.onLoaded(() ->
		{
			ImageIcon scaled = new ImageIcon(icon.getScaledInstance(18, 18, Image.SCALE_SMOOTH));
			rowIconCache.put(key, scaled);
			iconLabel.setIcon(scaled);
		});
	}

	/** @return a {@link #rowIconCache} key combining an item's id with the stack size its icon is rendered at. */
	private static long iconCacheKey(TrackedItem item)
	{
		return ((long) item.getItemId() << 32) | (item.iconStackSize() & 0xffffffffL);
	}

	/**
	 * Builds the dedicated manage-mode row: a stripped-down layout showing only what's needed to
	 * organise items. A left column of reorder controls (up/down, plus drag when ungrouped), a
	 * middle column with the icon+name over a category picker, and a right column with the
	 * always-visible remove and favorite controls. All price/quantity/profit content is omitted.
	 */
	private JPanel buildManageRow(TrackedItem item, List<TrackedItem> groupItems)
	{
		JPanel card = new JPanel(new BorderLayout(6, 0))
		{
			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
			}
		};
		card.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		card.setBorder(new EmptyBorder(6, 8, 6, 8));
		card.putClientProperty(ROW_ITEM_ID, item.getItemId());

		card.add(buildReorderStrip(item, groupItems), BorderLayout.WEST);

		JLabel nameLabel = new JLabel();
		nameLabel.setForeground(Color.WHITE);
		nameLabel.setFont(FontManager.getRunescapeBoldFont());
		EllipsisText.set(nameLabel, item.getName());

		JPanel nameRow = new JPanel(new BorderLayout(6, 0));
		nameRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nameRow.add(buildRowIcon(item), BorderLayout.WEST);
		nameRow.add(nameLabel, BorderLayout.CENTER);

		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		center.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		JComboBox<String> picker = buildCategoryPicker(item);
		picker.setAlignmentX(Component.LEFT_ALIGNMENT);
		center.add(nameRow);
		center.add(Box.createVerticalStrut(4));
		center.add(picker);
		card.add(center, BorderLayout.CENTER);

		card.add(buildManageEastControls(item), BorderLayout.EAST);

		return card;
	}

	/** Builds the left reorder column (up/down, plus a drag handle when the list isn't grouped) for the manage row. */
	private JPanel buildReorderStrip(TrackedItem item, List<TrackedItem> groupItems)
	{
		final Color controlColor = StockpileColors.MUTED;
		final Color controlDim = DIVIDER_COLOR;

		final int groupPos = indexOfItem(groupItems, item.getItemId());
		final boolean canUp = groupPos > 0;
		final boolean canDown = groupPos >= 0 && groupPos < groupItems.size() - 1;
		final int upTarget = canUp ? orderedItemIds.indexOf(groupItems.get(groupPos - 1).getItemId()) : -1;
		final int downTarget = canDown ? orderedItemIds.indexOf(groupItems.get(groupPos + 1).getItemId()) : -1;

		JButton upBtn = makeRowControl("▲", "Move up");
		upBtn.setForeground(canUp ? controlColor : controlDim);
		upBtn.addActionListener(e ->
		{
			if (canUp && upTarget >= 0 && onReorder != null)
				onReorder.accept(item.getItemId(), upTarget);
		});

		JButton downBtn = makeRowControl("▼", "Move down");
		downBtn.setForeground(canDown ? controlColor : controlDim);
		downBtn.addActionListener(e ->
		{
			if (canDown && downTarget >= 0 && onReorder != null)
				onReorder.accept(item.getItemId(), downTarget);
		});

		JLabel dragHandle = new JLabel("≡", SwingConstants.CENTER);
		dragHandle.setPreferredSize(new Dimension(20, 20));
		dragHandle.setForeground(controlColor);
		dragHandle.setFont(FontManager.getRunescapeSmallFont());
		dragHandle.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		dragHandle.setToolTipText("Drag to reorder");
		installDragHandle(dragHandle, item.getItemId());

		JPanel strip = new JPanel();
		strip.setLayout(new BoxLayout(strip, BoxLayout.Y_AXIS));
		strip.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		strip.setBorder(new EmptyBorder(0, 0, 0, 6));
		upBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		downBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		dragHandle.setAlignmentX(Component.CENTER_ALIGNMENT);
		strip.add(Box.createVerticalGlue());
		strip.add(upBtn);
		strip.add(dragHandle);
		strip.add(downBtn);
		strip.add(Box.createVerticalGlue());

		return strip;
	}

	/** Builds the right column of the manage row: an always-visible remove button stacked over a favorite star. */
	private JPanel buildManageEastControls(TrackedItem item)
	{
		JButton removeBtn = new JButton("✕");
		removeBtn.setPreferredSize(new Dimension(20, 20));
		removeBtn.setMaximumSize(new Dimension(20, 20));
		removeBtn.setMargin(new Insets(0, 0, 0, 0));
		removeBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		removeBtn.setForeground(REMOVE_COLOR);
		removeBtn.setFont(FontManager.getRunescapeSmallFont());
		removeBtn.setFocusPainted(false);
		removeBtn.setBorderPainted(false);
		removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		removeBtn.setToolTipText("Remove from tracking");
		removeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		removeBtn.addActionListener(e -> onRemoveItem.accept(item.getItemId()));

		JLabel star = new JLabel(item.isFavorite() ? "★" : "☆", SwingConstants.CENTER);
		star.setPreferredSize(new Dimension(20, 20));
		star.setMaximumSize(new Dimension(20, 20));
		star.setAlignmentX(Component.CENTER_ALIGNMENT);
		star.setFont(FontManager.getRunescapeSmallFont());
		star.setForeground(item.isFavorite() ? COLOR_AVG : STAR_DIM);
		star.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		star.setToolTipText(item.isFavorite() ? "Remove from favorites" : "Add to favorites");
		star.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (onSetFavorite != null)
					onSetFavorite.accept(item.getItemId(), !item.isFavorite());
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				star.setText(item.isFavorite() ? "☆" : "★");
				star.setForeground(item.isFavorite() ? STAR_DIM : STAR_PREVIEW);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				star.setText(item.isFavorite() ? "★" : "☆");
				star.setForeground(item.isFavorite() ? COLOR_AVG : STAR_DIM);
			}
		});

		JPanel east = new JPanel();
		east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
		east.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		east.add(removeBtn);
		east.add(Box.createVerticalStrut(4));
		east.add(star);

		if (config.showScreenOverlay())
		{
			east.add(Box.createVerticalStrut(4));
			east.add(buildOverlayToggle(item));
		}

		return east;
	}

	/**
	 * Builds the overlay-select control beneath the favorite star: a painted monitor icon that
	 * toggles whether the item appears in the on-screen overlay. Gold when selected, and disabled
	 * (greyed) once {@link StockpilePlugin#OVERLAY_MAX} items are selected and this isn't one.
	 */
	private JLabel buildOverlayToggle(TrackedItem item)
	{
		boolean on = item.isOnOverlay();
		boolean atCap = !on && overlayCount() >= StockpilePlugin.OVERLAY_MAX;

		final Color restColor = on ? COLOR_AVG : (atCap ? DIVIDER_COLOR : STAR_DIM);
		final Color hoverColor = on ? STAR_DIM : COLOR_AVG;

		JLabel toggle = new JLabel(overlayIcon(restColor));
		toggle.setAlignmentX(Component.CENTER_ALIGNMENT);
		toggle.setToolTipText(on
				? "Remove from on-screen overlay"
				: atCap
						? "Overlay is full (" + StockpilePlugin.OVERLAY_MAX + " max)"
						: "Show on the on-screen overlay");

		if (!atCap)
		{
			toggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			toggle.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					if (onSetOnOverlay != null)
						onSetOnOverlay.accept(item.getItemId(), !item.isOnOverlay());
				}

				@Override
				public void mouseEntered(MouseEvent e)
				{
					toggle.setIcon(overlayIcon(hoverColor));
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
					toggle.setIcon(overlayIcon(restColor));
				}
			});
		}

		return toggle;
	}

	/**
	 * Builds the per-item compact toggle beneath the overlay button (#210): a painted "≣" glyph
	 * that flips this row between the standard and compact two-row layouts, independent of the
	 * global compact toggle. Gold when this row's compact override is on, grey otherwise.
	 */
	private JLabel buildRowCompactToggle(TrackedItem item)
	{
		final Color restColor = item.isCompact() ? COLOR_AVG : STAR_DIM;
		final Color hoverColor = item.isCompact() ? STAR_DIM : COLOR_AVG;

		JLabel toggle = new JLabel("≣", SwingConstants.CENTER);
		toggle.setPreferredSize(new Dimension(20, 20));
		toggle.setMaximumSize(new Dimension(20, 20));
		toggle.setFont(FontManager.getRunescapeSmallFont());
		toggle.setForeground(restColor);
		toggle.setAlignmentX(Component.CENTER_ALIGNMENT);
		toggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		toggle.setToolTipText(item.isCompact() ? "Expand to standard row" : "Compact this row");
		toggle.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (onSetItemCompact != null)
					onSetItemCompact.accept(item.getItemId(), !item.isCompact());
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				toggle.setForeground(hoverColor);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				toggle.setForeground(restColor);
			}
		});

		return toggle;
	}

	/**
	 * Builds a row hover button (dashboard icon) that opens this item in its own dashboard window (#109).
	 * Dim at rest, gold while hovered, mirroring the other row hover affordances.
	 */
	private JLabel buildRowDashboardButton(TrackedItem item)
	{
		final Color restColor = STAR_DIM;
		final Color hoverColor = COLOR_AVG;

		JLabel button = new JLabel(dashboardIcon(restColor));
		button.setPreferredSize(new Dimension(20, 20));
		button.setMaximumSize(new Dimension(20, 20));
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setToolTipText("Open in Dashboard View");
		button.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (onPopOut != null)
					onPopOut.accept(item.getItemId());
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				button.setIcon(dashboardIcon(hoverColor));
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				button.setIcon(dashboardIcon(restColor));
			}
		});

		return button;
	}

	/** @return how many currently tracked items are flagged for the on-screen overlay. */
	private int overlayCount()
	{
		int count = 0;
		for (TrackedItem item : currentItems.values())
			if (item.isOnOverlay())
				count++;

		return count;
	}

	/**
	 * One display section of the tracked list (#275): an optional group header ({@code title}/{@code key}/
	 * {@code collapsed}) and its filtered items. A flat, header-less list is a single section with a null title.
	 */
	private static final class RowSection
	{
		private final String title;
		private final String key;
		private final boolean collapsed;
		private final List<TrackedItem> items;

		private RowSection(String title, String key, boolean collapsed, List<TrackedItem> items)
		{
			this.title = title;
			this.key = key;
			this.collapsed = collapsed;
			this.items = items;
		}
	}

	/**
	 * Cached scaffolding for one tracked-item row (#275): the reusable card, identity labels and favourite
	 * star built once, plus the {@link #contentSlot} whose price/compact/loading content is refilled by
	 * {@link #populateRow} on a value change, and the {@link #hoverListener} re-attached to that new content.
	 */
	private static final class RowView
	{
		private final int itemId;
		private final JPanel card;
		private final JLabel iconLabel;
		private final JLabel nameLabel;
		private final JLabel qtyLabel;
		private final JLabel favStar;
		private final JPanel contentSlot;
		private final MouseAdapter hoverListener;

		private RowView(int itemId, JPanel card, JLabel iconLabel, JLabel nameLabel, JLabel qtyLabel,
				JLabel favStar, JPanel contentSlot, MouseAdapter hoverListener)
		{
			this.itemId = itemId;
			this.card = card;
			this.iconLabel = iconLabel;
			this.nameLabel = nameLabel;
			this.qtyLabel = qtyLabel;
			this.favStar = favStar;
			this.contentSlot = contentSlot;
			this.hoverListener = hoverListener;
		}
	}

	/**
	 * Builds the reusable scaffolding for one tracked-item row (#275): the card, identity (icon/name/qty)
	 * and the hover buttons, plus an empty content slot filled by {@link #populateRow}. A later value change
	 * refreshes the row in place against this scaffolding rather than reconstructing it. Returns the
	 * {@link RowView} the caller caches by item id.
	 */
	private RowView buildRowView(TrackedItem item, PriceIndicatorMode indicatorMode, List<TrackedItem> groupItems)
	{
		final boolean hovered = item.getItemId() == hoveredItemId;
		final boolean showQty = config.showQuantityValue();
		final boolean rowCompact = config.compactView() || item.isCompact();
		final boolean showHoverButtons = config.quickActionDelivery().showsHoverButtons();

		JPanel card = new JPanel(new BorderLayout(0, 0))
		{
			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
			}
		};
		card.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		card.setBorder(new EmptyBorder(6, 8, 6, 8));
		card.putClientProperty(ROW_ITEM_ID, item.getItemId());

		JLabel iconLabel = new JLabel();
		iconLabel.setVerticalAlignment(SwingConstants.CENTER);
		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JButton removeBtn = new JButton("✕");
		removeBtn.setPreferredSize(new Dimension(20, 20));
		removeBtn.setMargin(new Insets(0, 0, 0, 0));
		removeBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		removeBtn.setForeground(hovered ? REMOVE_COLOR : STAR_HIDDEN);
		removeBtn.setFont(FontManager.getRunescapeSmallFont());
		removeBtn.setFocusPainted(false);
		removeBtn.setBorderPainted(false);
		removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		removeBtn.setToolTipText("Remove from tracking");
		removeBtn.addActionListener(e -> onRemoveItem.accept(item.getItemId()));

		JLabel favStar = buildFavoriteStar(item);
		removeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		favStar.setAlignmentX(Component.CENTER_ALIGNMENT);

		final JLabel overlayBtn = config.showScreenOverlay() && showHoverButtons ? buildOverlayToggle(item) : null;
		final JLabel compactBtn = config.compactView() || !showHoverButtons ? null : buildRowCompactToggle(item);
		final JLabel dashboardBtn = showHoverButtons ? buildRowDashboardButton(item) : null;

		JPanel eastPanel = new JPanel()
		{
			/**
			 * Reserves the hidden hover buttons' extent so revealing them on hover changes only
			 * appearance, not layout size. Without this a short row (e.g. a non-tradeable item)
			 * grows on hover and shifts the rows beneath it. A compact row stacks its controls
			 * horizontally, so the reservation is along the width there and the height stays down
			 * to the compact content — otherwise the taller button column would pad the row.
			 */
			@Override
			public Dimension getPreferredSize()
			{
				Dimension d = super.getPreferredSize();
				if (overlayBtn != null && !overlayBtn.isVisible())
				{
					Dimension o = overlayBtn.getPreferredSize();
					if (rowCompact)
						d.width += o.width;
					else
						d.height += o.height;
				}

				if (compactBtn != null && !compactBtn.isVisible())
				{
					Dimension cb = compactBtn.getPreferredSize();
					if (rowCompact)
						d.width += cb.width;
					else
						d.height += cb.height;
				}

				if (dashboardBtn != null && !dashboardBtn.isVisible())
				{
					Dimension db = dashboardBtn.getPreferredSize();
					if (rowCompact)
						d.width += db.width;
					else
						d.height += db.height;
				}

				return d;
			}
		};
		eastPanel.setLayout(new BoxLayout(eastPanel, rowCompact ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS));
		eastPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		eastPanel.add(removeBtn);
		eastPanel.add(favStar);
		if (overlayBtn != null)
		{
			overlayBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
			overlayBtn.setVisible(false);
			eastPanel.add(overlayBtn);
		}

		if (compactBtn != null)
		{
			compactBtn.setVisible(false);
			eastPanel.add(compactBtn);
		}

		if (dashboardBtn != null)
		{
			dashboardBtn.setVisible(false);
			eastPanel.add(dashboardBtn);
		}

		if (rowCompact)
		{
			JPanel eastWrapper = new JPanel(new BorderLayout());
			eastWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			eastWrapper.add(eastPanel, BorderLayout.SOUTH);
			card.add(eastWrapper, BorderLayout.EAST);
		}
		else
		{
			card.add(eastPanel, BorderLayout.EAST);
		}

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JLabel nameLabel = new JLabel();
		nameLabel.setForeground(Color.WHITE);
		nameLabel.setFont(FontManager.getRunescapeBoldFont());

		JLabel qtyLabel = new JLabel();
		qtyLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		qtyLabel.setFont(FontManager.getRunescapeSmallFont());

		JPanel nameRow = new JPanel(new BorderLayout(6, 0));
		nameRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		nameRow.add(iconLabel, BorderLayout.WEST);
		nameRow.add(nameLabel, BorderLayout.CENTER);
		if (showQty)
			nameRow.add(qtyLabel, BorderLayout.EAST);

		centerPanel.add(nameRow);

		JPanel contentSlot = new JPanel();
		contentSlot.setLayout(new BoxLayout(contentSlot, BoxLayout.Y_AXIS));
		contentSlot.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		contentSlot.setAlignmentX(Component.LEFT_ALIGNMENT);
		centerPanel.add(contentSlot);

		card.add(centerPanel, BorderLayout.CENTER);

		MouseAdapter hoverListener = installRowHover(card, item, removeBtn, favStar, overlayBtn, compactBtn,
				dashboardBtn, REMOVE_COLOR, STAR_HIDDEN);

		RowView rv = new RowView(item.getItemId(), card, iconLabel, nameLabel, qtyLabel, favStar,
				contentSlot, hoverListener);
		populateRow(rv, item, indicatorMode);
		return rv;
	}

	/**
	 * Refreshes a cached row's mutable content in place (#275): icon, name, quantity, the favourite star's
	 * resting state, and the price/compact/loading content slot (rebuilt cheaply and re-wired to the row's
	 * hover listener). No scaffolding is reconstructed.
	 */
	private void populateRow(RowView rv, TrackedItem item, PriceIndicatorMode indicatorMode)
	{
		applyRowIcon(rv.iconLabel, item);
		EllipsisText.set(rv.nameLabel, item.getName());
		rv.qtyLabel.setText("Qty: " + GpFormat.shortValue(item.getQuantity()));
		rv.qtyLabel.setToolTipText(GpFormat.grouped(item.getQuantity()));
		refreshFavoriteStar(rv.favStar, item.isFavorite());

		rv.contentSlot.removeAll();
		buildRowContent(rv.contentSlot, item, indicatorMode);
		for (Component child : rv.contentSlot.getComponents())
			addListenerRecursively(child, rv.hoverListener);

		rv.contentSlot.revalidate();
		rv.contentSlot.repaint();
	}

	/**
	 * Fills a row's content slot (#275) with the price grid, compact value line, or loading placeholder for
	 * the item's current state, plus the optional per-item profit row. Rebuilt cheaply on each value change.
	 */
	private void buildRowContent(JPanel slot, TrackedItem item, PriceIndicatorMode indicatorMode)
	{
		if (config.compactView() || item.isCompact())
		{
			slot.add(buildCompactValueRow(item));
			return;
		}

		final PriceIndicatorMode itemIndicatorMode = item.isHasDeltas() ? indicatorMode : PriceIndicatorMode.OFF;

		if (!item.hasPrices())
		{
			final JLabel loading;
			if (!item.isTradeable())
			{
				loading = new JLabel("Item not tradeable");
				loading.setForeground(StockpileColors.MUTED);
			}
			else if (item.isPriceLoadFailed())
			{
				loading = new JLabel("Unable to load price");
				loading.setForeground(COLOR_LOW);
			}
			else
			{
				loading = new JLabel("Prices loading...");
				loading.setForeground(LOADING_COLOR);
				loadingLabels.add(loading);
			}

			loading.setFont(FontManager.getRunescapeSmallFont());

			JPanel loadingRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
			loadingRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			loadingRow.setAlignmentX(Component.LEFT_ALIGNMENT);
			loadingRow.add(loading);
			slot.add(loadingRow);
			return;
		}

		final List<TimeWindow> rowWindows = Arrays.asList(config.row1Data(), config.row2Data(), config.row3Data());
		final boolean showColHigh = config.showColHigh();
		final boolean showColLow = config.showColLow();
		final boolean showColAvg = config.showColAvg();
		final boolean showColVolume = config.showColVolume();

		JPanel pricesPanel = new JPanel(new GridBagLayout());
		pricesPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		pricesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		pricesPanel.setBorder(new EmptyBorder(0, PRICES_LEFT_PAD, 0, PRICES_RIGHT_PAD));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 0, 1, 4);

		int gridy = 0;
		for (TimeWindow window : rowWindows)
		{
			if (window == TimeWindow.NONE)
				continue;

			PriceStats stats = item.getWindowStats().get(window);
			long h, l, a, vol;
			if (window == TimeWindow.LIVE || stats == null)
			{
				h = item.getHighPrice();
				l = item.getLowPrice();
				a = item.getAvgPrice();
				vol = stats != null ? stats.getVolume() : 0;
			}
			else
			{
				h = stats.getHigh();
				l = stats.getLow();
				a = stats.getAvg();
				vol = stats.getVolume();
			}

			boolean isLive = window == TimeWindow.LIVE;

			JLabel windowLbl = new JLabel(window.toString());
			windowLbl.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
			windowLbl.setFont(FontManager.getRunescapeSmallFont());
			if (isLive)
				windowLbl.setToolTipText("Buy: " + formatAge(item.getLatestHighTime())
						+ ", Sell: " + formatAge(item.getLatestLowTime()));

			c.gridx = 0;
			c.gridy = gridy;
			c.weightx = 0;
			c.anchor = GridBagConstraints.WEST;
			pricesPanel.add(windowLbl, c);

			List<JLabel> visibleCells = new ArrayList<>(4);
			if (showColHigh)
			{
				JLabel cell = new JLabel("", SwingConstants.CENTER);
				cell.setFont(FontManager.getRunescapeSmallFont());
				cell.setForeground(COLOR_HIGH);
				installItemValue(cell, h, "", "High", TINT_HIGH);
				if (isLive)
					applyLiveStaleness(cell, h, "High", "Last Buy", item.getLatestHighTime(),
							COLOR_HIGH, COLOR_HIGH_STALE);

				visibleCells.add(cell);
			}

			if (showColLow)
			{
				JLabel cell = new JLabel("", SwingConstants.CENTER);
				cell.setFont(FontManager.getRunescapeSmallFont());
				cell.setForeground(COLOR_LOW);
				installItemValue(cell, l, "", "Low", TINT_LOW);
				if (isLive)
					applyLiveStaleness(cell, l, "Low", "Last Sell", item.getLatestLowTime(),
							COLOR_LOW, COLOR_LOW_STALE);

				visibleCells.add(cell);
			}

			if (showColAvg)
			{
				JLabel cell = new JLabel("", SwingConstants.CENTER);
				cell.setFont(FontManager.getRunescapeSmallFont());
				cell.setForeground(COLOR_AVG);
				installItemValue(cell, a, "", "Avg", TINT_AVG);
				visibleCells.add(cell);
			}

			if (showColVolume)
			{
				JLabel cell = new JLabel("", SwingConstants.CENTER);
				cell.setForeground(COLOR_VOLUME);
				cell.setFont(FontManager.getRunescapeSmallFont());
				String volText = window == TimeWindow.LIVE ? "-" : GpFormat.shortValue(vol);
				cell.setText(volText);
				if (window != TimeWindow.LIVE)
					cell.setToolTipText("Volume: " + GpFormat.grouped(vol));

				HoverTintListener volListener = new HoverTintListener(cell, volText, TINT_VOLUME);
				cell.addMouseListener(volListener);
				SwingUtilities.invokeLater(volListener::applyIfHovered);
				visibleCells.add(cell);
			}

			int col = 1;
			for (JLabel cell : visibleCells)
			{
				c.gridx = col++;
				c.weightx = 1;
				c.anchor = GridBagConstraints.CENTER;
				pricesPanel.add(cell, c);
			}

			JLabel pulse = createDeltaLabel();
			if (itemIndicatorMode != PriceIndicatorMode.OFF)
				pulseIfShown(pulse, item.getAvgDelta(), itemIndicatorMode);

			c.gridx = col++;
			c.weightx = 0;
			c.anchor = GridBagConstraints.WEST;
			pricesPanel.add(pulse, c);

			for (int i = visibleCells.size(); i < 4; i++)
			{
				c.gridx = col++;
				c.weightx = 1;
				c.anchor = GridBagConstraints.CENTER;
				pricesPanel.add(new JLabel(), c);
			}

			gridy++;
		}

		slot.add(pricesPanel);

		if (config.showItemProfitRow()
				&& item.isCostBasisInitialized() && item.hasPrices())
		{
			long itemProfit = item.getProfitAt(item.getAvgPrice());
			String sign = itemProfit > 0 ? "+" : "";
			ValueFormat fmt = config.geEstimatesFormat();

			JLabel itemProfitPrefix = new JLabel("Est. Profit:");
			itemProfitPrefix.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
			itemProfitPrefix.setFont(FontManager.getRunescapeSmallFont());
			itemProfitPrefix.setToolTipText("Realized profit from sold lots plus unrealized "
					+ "gain/loss on held lots (marked to the current average price)");

			JLabel itemProfitValue = new JLabel(sign + formatTotalGp(itemProfit, fmt));
			itemProfitValue.setFont(FontManager.getRunescapeSmallFont());
			itemProfitValue.setForeground(itemProfit == 0 ? ColorScheme.LIGHT_GRAY_COLOR
					: (itemProfit > 0 ? COLOR_HIGH : COLOR_LOW));
			applyTotalTooltip(itemProfitValue, itemProfit, fmt);

			JPanel itemProfitRow = new JPanel(new BorderLayout(6, 0));
			itemProfitRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			itemProfitRow.add(itemProfitPrefix, BorderLayout.WEST);
			itemProfitRow.add(itemProfitValue, BorderLayout.CENTER);

			JPanel itemProfitSection = new JPanel(new BorderLayout());
			itemProfitSection.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			itemProfitSection.setAlignmentX(Component.LEFT_ALIGNMENT);
			itemProfitSection.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(
							new EmptyBorder(4, 0, 0, 0),
							new MatteBorder(1, 0, 0, 0, DIVIDER_COLOR)
					),
					new EmptyBorder(4, 10, 0, 0)
			));
			itemProfitSection.add(itemProfitRow, BorderLayout.CENTER);
			slot.add(itemProfitSection);
		}
	}

	/**
	 * Shows the tracked-row right-click menu (#280) when {@code e} is a popup trigger. Always offers
	 * "View detail", "Open in Dashboard" and "Add to Compare"; when {@link StockpileConfig#quickActionDelivery()}
	 * routes quick actions through the menu (#299), it also carries the row's favorite, overlay, per-item
	 * compact and remove actions so they need not live on the hover strip.
	 */
	private void maybeShowRowMenu(MouseEvent e, int itemId)
	{
		if (!e.isPopupTrigger())
			return;

		JPopupMenu menu = new JPopupMenu();

		final Color grey = ColorScheme.LIGHT_GRAY_COLOR;
		final Color gold = COLOR_AVG;

		menu.add(rowMenuItem("View detail", detailIcon(grey), detailIcon(gold), null,
				true, a -> showDetail(itemId)));
		menu.add(rowMenuItem("Open in Dashboard", dashboardIcon(grey), dashboardIcon(gold), null,
				true, a -> onPopOut.accept(itemId)));
		menu.add(rowMenuItem("Add to Compare", compareIcon(grey), compareIcon(gold), null,
				true, a -> onAddToCompare.accept(itemId)));

		final TrackedItem item = currentItems.get(itemId);
		if (item != null && VariantFamily.hasFamily(item.getName()))
			menu.add(rowMenuItem("Compare all variants", compareIcon(grey), compareIcon(gold), null,
					true, a -> onCompareVariants.accept(itemId)));

		if (item != null && config.quickActionDelivery().showsRowMenuActions())
		{
			menu.addSeparator();

			final boolean fav = item.isFavorite();
			menu.add(rowMenuItem(fav ? "Remove from favorites" : "Add to favorites",
					starMenuIcon(fav ? gold : grey, fav), starMenuIcon(fav ? grey : gold, !fav), null,
					onSetFavorite != null, a -> onSetFavorite.accept(itemId, !fav)));

			menu.add(buildChangeCategoryMenu(itemId, item));

			if (!config.compactView())
			{
				final boolean comp = item.isCompact();
				menu.add(rowMenuItem(comp ? "Expand to standard row" : "Compact this row",
						compactMenuIcon(comp ? gold : grey), compactMenuIcon(comp ? grey : gold), null,
						onSetItemCompact != null, a -> onSetItemCompact.accept(itemId, !comp)));
			}

			if (config.showScreenOverlay())
			{
				final boolean on = item.isOnOverlay();
				final boolean atCap = !on && overlayCount() >= StockpilePlugin.OVERLAY_MAX;
				menu.add(rowMenuItem(on ? "Remove from overlay" : "Show on overlay",
						overlayIcon(on ? gold : grey), overlayIcon(on ? grey : gold), null,
						onSetOnOverlay != null && !atCap, a -> onSetOnOverlay.accept(itemId, !on)));
			}

			menu.addSeparator();
			menu.add(rowMenuItem("Remove from tracking", removeMenuIcon(grey), removeMenuIcon(REMOVE_COLOR),
					"selectionBackground: " + hex(removeHighlight()) + "; selectionForeground: #000000",
					true, a -> onRemoveItem.accept(itemId)));
		}

		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	/**
	 * Builds a row right-click menu item: {@code restIcon} shown at rest (2px before the label) in the RuneScape
	 * small font, swapped to {@code hoverIcon} while the item is highlighted (icon only — the label and default
	 * row highlight are untouched). Active toggles pass a gold rest icon that greys out on hover, mirroring the
	 * hover strip. A non-null {@code flatStyle} overrides the FlatLaf selection styling for this one item.
	 */
	private JMenuItem rowMenuItem(String label, Icon restIcon, Icon hoverIcon, String flatStyle,
			boolean enabled, ActionListener action)
	{
		JMenuItem item = new JMenuItem(label, restIcon);
		item.setFont(FontManager.getRunescapeSmallFont());
		item.setIconTextGap(2);
		item.setEnabled(enabled);
		if (flatStyle != null)
			item.putClientProperty("FlatLaf.style", flatStyle);

		item.getModel().addChangeListener(ev ->
				item.setIcon(item.getModel().isArmed() ? hoverIcon : restIcon));
		item.addActionListener(action);
		return item;
	}

	/**
	 * Builds the row menu's "Change category" submenu (#300): one child per existing category plus
	 * Uncategorized, each moving the item into that group, followed by a "New category…" child that
	 * prompts to create-and-assign. The item's current category is shown checked and disabled.
	 */
	private JMenu buildChangeCategoryMenu(int itemId, TrackedItem item)
	{
		final Color grey = ColorScheme.LIGHT_GRAY_COLOR;
		final Color gold = COLOR_AVG;

		JMenu submenu = new JMenu("Change category");
		submenu.setFont(FontManager.getRunescapeSmallFont());
		submenu.setIcon(categoriesIcon(grey));
		submenu.setIconTextGap(2);
		submenu.getModel().addChangeListener(ev ->
				submenu.setIcon(submenu.getModel().isArmed() || submenu.getModel().isSelected()
						? categoriesIcon(gold) : categoriesIcon(grey)));

		final String current = item.getCategory();
		final boolean uncategorized = current == null || current.isEmpty();

		submenu.add(categoryMenuItem(UNCATEGORIZED_LABEL, uncategorized,
				a -> categoryActions.setItemCategory(itemId, null)));

		for (CategoryState cat : categories)
		{
			final String name = cat.getName();
			submenu.add(categoryMenuItem(name, name.equals(current),
					a -> categoryActions.setItemCategory(itemId, name)));
		}

		submenu.addSeparator();
		submenu.add(categoryMenuItem(NEW_CATEGORY_LABEL, false, a -> promptNewCategory(itemId)));
		return submenu;
	}

	/**
	 * Builds a "Change category" child: the item's current category is marked with a check icon and
	 * disabled (selecting it would be a no-op); every other entry runs {@code action} on click.
	 */
	private JMenuItem categoryMenuItem(String label, boolean current, ActionListener action)
	{
		JMenuItem item = new JMenuItem(label);
		item.setFont(FontManager.getRunescapeSmallFont());
		item.setIconTextGap(4);
		if (current)
		{
			item.setIcon(checkIcon(COLOR_AVG));
			item.setEnabled(false);
		}
		else
		{
			item.addActionListener(action);
		}

		return item;
	}

	/**
	 * Prompts for a new category name (reusing the manage-row prompt), then creates it and assigns
	 * {@code itemId} to it. A blank or cancelled entry does nothing.
	 */
	private void promptNewCategory(int itemId)
	{
		String name = JOptionPane.showInputDialog(this, "New category name:",
				"New Category", JOptionPane.PLAIN_MESSAGE);
		if (name != null && !name.trim().isEmpty())
		{
			categoryActions.create(name.trim());
			categoryActions.setItemCategory(itemId, name.trim());
		}
	}

	/** Draws a check-mark glyph tinted {@code color}, marking the item's current category. */
	private static Icon checkIcon(Color color)
	{
		int size = 14;
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color);
		g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawPolyline(new int[]{2, 6, 12}, new int[]{7, 11, 3}, 3);
		g.dispose();
		return new ImageIcon(img);
	}

	/**
	 * @return a light red-tinted blend of the current menu-item highlight colour, for the Remove entry's hover.
	 *         Kept light so the darker red ✕ icon stands out against it rather than blending in.
	 */
	private static Color removeHighlight()
	{
		Color highlight = UIManager.getColor("MenuItem.selectionBackground");
		if (highlight == null)
			highlight = ColorScheme.DARK_GRAY_HOVER_COLOR;

		Color tint = blend(highlight, REMOVE_COLOR, 0.45f);
		return blend(tint, Color.WHITE, 0.4f);
	}

	/** @return the linear blend of {@code base} and {@code other}, weighting {@code other} by {@code t} (0..1). */
	private static Color blend(Color base, Color other, float t)
	{
		return new Color(
				Math.round(base.getRed() * (1 - t) + other.getRed() * t),
				Math.round(base.getGreen() * (1 - t) + other.getGreen() * t),
				Math.round(base.getBlue() * (1 - t) + other.getBlue() * t));
	}

	/** @return {@code color} as a {@code #RRGGBB} hex string for a FlatLaf style property. */
	private static String hex(Color color)
	{
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	/**
	 * Wires the shared row hover behaviour onto a tracked-item card: clicking the row
	 * (other than the remove button, favorite star, overlay button, or compact button) opens
	 * the detail view, and entering/leaving the card tracks {@link #hoveredItemId} and reveals/hides
	 * the remove button, favorite star, and the (optional) overlay-select and per-item compact buttons.
	 *
	 * @return the installed hover listener, so it can be re-attached to a row's rebuilt content slot (#275)
	 */
	private MouseAdapter installRowHover(JPanel card, TrackedItem item, JButton removeBtn, JLabel favStar,
			JLabel overlayBtn, JLabel compactBtn, JLabel dashboardBtn, Color removeColor, Color removeHidden)
	{
		card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		MouseAdapter hoverListener = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getSource() == removeBtn || e.getSource() == favStar
						|| e.getSource() == overlayBtn || e.getSource() == compactBtn
						|| e.getSource() == dashboardBtn)
					return;

				if (e.isPopupTrigger())
					return;

				showDetail(item.getItemId());
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				maybeShowRowMenu(e, item.getItemId());
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				maybeShowRowMenu(e, item.getItemId());
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				hoveredItemId = item.getItemId();
				removeBtn.setForeground(removeColor);
				favStar.putClientProperty(STAR_ROW_HOVERED, true);
				refreshFavoriteStar(favStar, item.isFavorite());
				if (overlayBtn != null)
					overlayBtn.setVisible(true);

				if (compactBtn != null)
					compactBtn.setVisible(true);

				if (dashboardBtn != null)
					dashboardBtn.setVisible(true);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				Point p = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), card);
				if (!card.contains(p))
				{
					if (hoveredItemId == item.getItemId())
						hoveredItemId = -1;

					removeBtn.setForeground(removeHidden);
					favStar.putClientProperty(STAR_ROW_HOVERED, false);
					favStar.putClientProperty(STAR_HOVERED, false);
					refreshFavoriteStar(favStar, item.isFavorite());
					if (overlayBtn != null)
						overlayBtn.setVisible(false);

					if (compactBtn != null)
						compactBtn.setVisible(false);

					if (dashboardBtn != null)
						dashboardBtn.setVisible(false);
				}
			}
		};
		addListenerRecursively(card, hoverListener);
		return hoverListener;
	}

	/**
	 * Builds the compact-view row-2 value line: {@code total value (single item value)}, both
	 * in short format and both derived from the latest avg-of-1 price (e.g. {@code 4.86m (1.62m)}).
	 * Falls back to a muted placeholder when the item has no prices.
	 */
	private JPanel buildCompactValueRow(TrackedItem item)
	{
		JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setBorder(new EmptyBorder(2, 4, 0, 0));

		if (!item.hasPrices())
		{
			JLabel placeholder = new JLabel(!item.isTradeable() ? "Item not tradeable" : "—");
			placeholder.setForeground(StockpileColors.MUTED);
			placeholder.setFont(FontManager.getRunescapeSmallFont());
			row.add(placeholder);
			return row;
		}

		final long totalValue = item.getAvgValue();
		final long singleValue = item.getAvgPrice();

		JLabel totalLabel = new JLabel(GpFormat.shortValue(totalValue));
		totalLabel.setFont(FontManager.getRunescapeSmallFont());
		totalLabel.setForeground(COLOR_AVG);
		totalLabel.setToolTipText(GpFormat.grouped(totalValue) + " gp");
		row.add(totalLabel);

		JLabel singleLabel = new JLabel("(" + GpFormat.shortValue(singleValue) + ")");
		singleLabel.setFont(FontManager.getRunescapeSmallFont());
		singleLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		singleLabel.setToolTipText(GpFormat.grouped(singleValue) + " gp");
		row.add(singleLabel);

		return row;
	}

	/**
	 * Opens the sort-mode menu on the header toggle, with the active mode checked and its current
	 * direction arrow shown. Clicking the active (non-manual) mode flips the sort direction; clicking
	 * any other mode selects it.
	 */
	private void showSortMenu()
	{
		JPopupMenu menu = new JPopupMenu();
		SortMode active = config.sortMode();
		boolean reversed = config.sortReversed();
		for (SortMode mode : SortMode.values())
		{
			boolean isActive = mode == active;
			String label = isActive && mode != SortMode.MANUAL
					? mode + (mode.descending(reversed) ? "  ↓" : "  ↑")
					: mode.toString();
			JCheckBoxMenuItem entry = new JCheckBoxMenuItem(label, isActive);
			entry.setFont(FontManager.getRunescapeSmallFont());
			entry.addActionListener(e ->
			{
				if (isActive && mode != SortMode.MANUAL)
				{
					if (onToggleSortDirection != null)
						onToggleSortDirection.run();
				}
				else if (onSetSortMode != null)
				{
					onSetSortMode.accept(mode);
				}
			});
			menu.add(entry);
		}

		menu.show(sortToggle, 0, sortToggle.getHeight());
	}

	/**
	 * Reflects the active sort on the header toggle: the effective direction arrow
	 * (highlighted) or the neutral glyph.
	 */
	private void updateSortToggle()
	{
		if (sortToggle == null)
			return;

		SortMode mode = config.sortMode();
		if (mode == SortMode.MANUAL)
		{
			sortToggle.setText("⇅");
			sortToggle.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		}
		else
		{
			sortToggle.setText(mode.descending(config.sortReversed()) ? "↓" : "↑");
			sortToggle.setForeground(COLOR_AVG);
		}
	}

	/** Toggles reorder mode, showing or hiding the per-row drag/arrow strips without a full rebuild. */
	private void toggleReorderMode()
	{
		reorderMode = !reorderMode;
		updateReorderToggle();
		renderTrackedRows(lastRenderItems, lastRenderIndicatorMode);
		bottomPanel.setVisible(config.showGeEstimates() && !reorderMode);
	}

	/** Highlights the header reorder toggle and reveals the manage-categories button when manage mode is active. */
	private void updateReorderToggle()
	{
		if (reorderToggle != null)
			reorderToggle.setForeground(reorderMode ? COLOR_AVG : ColorScheme.LIGHT_GRAY_COLOR);

		if (categoriesButton != null)
			categoriesButton.setVisible(reorderMode);
	}

	/** Highlights the header compact toggle when compact view is active. */
	private void updateCompactToggle()
	{
		if (compactToggle != null)
			compactToggle.setForeground(config.compactView() ? COLOR_AVG : ColorScheme.LIGHT_GRAY_COLOR);
	}

	/** Builds a compact, hover-revealed glyph button styled like the row's remove button. */
	private JButton makeRowControl(String glyph, String tooltip)
	{
		JButton btn = new JButton(glyph);
		btn.setPreferredSize(new Dimension(20, 20));
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		btn.setFont(FontManager.getRunescapeSmallFont());
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.setToolTipText(tooltip);

		return btn;
	}

	/**
	 * Wires drag-to-reorder onto a row's drag handle: pressing starts the drag, dragging
	 * updates the drop indicator and edge autoscroll, and releasing commits the move.
	 */
	private void installDragHandle(JLabel handle, int itemId)
	{
		MouseAdapter dragAdapter = new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				dragItemId = itemId;
				dragGroupIds = computeDragGroup(itemId);
				updateDrag(e);
			}

			@Override
			public void mouseDragged(MouseEvent e)
			{
				if (dragItemId != -1)
					updateDrag(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				commitDrag();
			}
		};

		handle.addMouseListener(dragAdapter);
		handle.addMouseMotionListener(dragAdapter);
	}

	/** Recomputes the drop target and autoscroll state for the current drag pointer, then repaints. */
	private void updateDrag(MouseEvent e)
	{
		Point inPanel = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), trackedItemsPanel);
		updateDropTarget(inPanel.y);
		updateDragAutoscroll(e);
		trackedItemsPanel.repaint();
	}

	/** Finds the list index where a drop at {@code yInPanel} would insert, and the indicator line position. */
	private void updateDropTarget(int yInPanel)
	{
		int idx = 0;
		int lastBottom = -1;
		for (Component c : trackedItemsPanel.getComponents())
		{
			if (!(c instanceof JComponent))
				continue;

			Object id = ((JComponent) c).getClientProperty(ROW_ITEM_ID);
			if (!(id instanceof Integer) || !dragGroupIds.contains(id))
				continue;

			Rectangle b = c.getBounds();
			if (yInPanel < b.y + b.height / 2)
			{
				dragInsertIndex = idx;
				dragLineY = b.y;
				return;
			}

			idx++;
			lastBottom = b.y + b.height;
		}

		dragInsertIndex = idx;
		dragLineY = lastBottom;
	}

	/**
	 * Commits the in-progress drag: places the dragged item at its new slot within its own
	 * group and rewrites the full tracked order accordingly (kept within-group, since groups
	 * render in global order). A no-op drop is ignored.
	 */
	private void commitDrag()
	{
		stopDragAutoscroll();

		if (dragItemId == -1)
			return;

		int draggedId = dragItemId;
		int gap = dragInsertIndex;
		List<Integer> group = dragGroupIds;

		dragItemId = -1;
		dragInsertIndex = -1;
		dragLineY = -1;
		dragGroupIds = new ArrayList<>();
		trackedItemsPanel.repaint();

		int fromGroupPos = group.indexOf(draggedId);
		if (gap < 0 || onSetGlobalOrder == null || fromGroupPos < 0)
			return;

		List<Integer> remaining = new ArrayList<>(group);
		remaining.remove(Integer.valueOf(draggedId));

		int k = gap > fromGroupPos ? gap - 1 : gap;
		k = Math.max(0, Math.min(k, remaining.size()));

		List<Integer> newOrder = new ArrayList<>(orderedItemIds);
		newOrder.remove(Integer.valueOf(draggedId));

		int insertAt;
		if (k < remaining.size())
			insertAt = newOrder.indexOf(remaining.get(k));
		else if (!remaining.isEmpty())
			insertAt = newOrder.indexOf(remaining.get(remaining.size() - 1)) + 1;
		else
			insertAt = newOrder.size();

		if (insertAt < 0)
			insertAt = newOrder.size();

		newOrder.add(insertAt, draggedId);

		if (newOrder.equals(orderedItemIds))
			return;

		onSetGlobalOrder.accept(newOrder);
	}

	/**
	 * Determines the dragged item's group as the contiguous run of item rows between accordion
	 * headers in the rendered list (the whole list when ungrouped), returning its item ids in
	 * visual order.
	 */
	private List<Integer> computeDragGroup(int itemId)
	{
		List<Integer> current = new ArrayList<>();
		List<Integer> found = null;

		for (Component c : trackedItemsPanel.getComponents())
		{
			if (!(c instanceof JComponent))
				continue;

			JComponent jc = (JComponent) c;
			if (Boolean.TRUE.equals(jc.getClientProperty(GROUP_HEADER_KEY)))
			{
				if (found != null)
					break;

				current = new ArrayList<>();
				continue;
			}

			Object id = jc.getClientProperty(ROW_ITEM_ID);
			if (id instanceof Integer)
			{
				current.add((Integer) id);
				if ((Integer) id == itemId)
					found = current;
			}
		}

		return found != null ? found : current;
	}

	/** Starts/stops edge autoscroll based on whether the drag pointer is near the viewport's top or bottom. */
	private void updateDragAutoscroll(MouseEvent e)
	{
		JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, trackedItemsPanel);
		if (viewport == null)
		{
			stopDragAutoscroll();
			return;
		}

		Point inViewport = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), viewport);
		int height = viewport.getExtentSize().height;

		int dir = 0;
		if (inViewport.y < DRAG_SCROLL_MARGIN)
			dir = -1;
		else if (inViewport.y > height - DRAG_SCROLL_MARGIN)
			dir = 1;

		if (dir == 0)
		{
			stopDragAutoscroll();
			return;
		}

		dragScrollDir = dir;
		if (dragScrollTimer == null)
		{
			dragScrollTimer = new Timer(16, ev -> dragAutoscrollTick());
			dragScrollTimer.start();
		}
	}

	/** One autoscroll step: nudges the viewport in {@link #dragScrollDir} and recomputes the drop target. */
	private void dragAutoscrollTick()
	{
		JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, trackedItemsPanel);
		if (viewport == null || dragItemId == -1)
		{
			stopDragAutoscroll();
			return;
		}

		Point pos = viewport.getViewPosition();
		int maxY = Math.max(0, trackedItemsPanel.getHeight() - viewport.getExtentSize().height);
		int newY = Math.max(0, Math.min(maxY, pos.y + dragScrollDir * DRAG_SCROLL_STEP));
		if (newY == pos.y)
			return;

		viewport.setViewPosition(new Point(pos.x, newY));

		Point mouse = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mouse, trackedItemsPanel);
		updateDropTarget(mouse.y);
		trackedItemsPanel.repaint();
	}

	/** Stops the edge-autoscroll timer, if running. */
	private void stopDragAutoscroll()
	{
		if (dragScrollTimer != null)
		{
			dragScrollTimer.stop();
			dragScrollTimer = null;
		}

		dragScrollDir = 0;
	}

	/** Attaches a mouse listener to a component and all its descendants, so a whole row reacts as one. */
	private void addListenerRecursively(Component c, MouseListener listener)
	{
		c.addMouseListener(listener);
		if (c instanceof Container)
		{
			for (Component child : ((Container) c).getComponents())
				addListenerRecursively(child, listener);
		}
	}

	/** Installs a compact gp value on a label with no tooltip caption. */
	private void installItemValue(JLabel label, long value, String prefix, Color tint)
	{
		installItemValue(label, value, prefix, null, tint);
	}

	/** Installs a prefixed compact gp value on a label via {@link #installShortValue}. */
	private void installItemValue(JLabel label, long value, String prefix, String tooltipLabel, Color tint)
	{
		installShortValue(label, value, prefix + GpFormat.shortValue(value), tooltipLabel, tint);
	}

	/** Installs a pre-formatted compact value on a label with a full-number tooltip and a hover tint. */
	static void installShortValue(JLabel label, long value, String shortText, String tooltipLabel, Color tint)
	{
		label.setText(shortText);
		String tooltipPrefix = tooltipLabel == null ? "" : tooltipLabel + ": ";
		label.setToolTipText(tooltipPrefix + GpFormat.grouped(value) + " gp");
		removeHoverTint(label);
		HoverTintListener listener = new HoverTintListener(label, shortText, tint);
		label.addMouseListener(listener);
		SwingUtilities.invokeLater(listener::applyIfHovered);
	}

	/**
	 * Reflects the staleness of a Ltst high/low value on its cell: appends the last
	 * trade time as a second tooltip line and dims the value's color when that trade
	 * is older than the configured threshold.
	 *
	 * @param sideLabel    the value side, e.g. {@code "High"} or {@code "Low"}
	 * @param timeLabel    the trade-time caption, e.g. {@code "Last Buy"}
	 * @param tradeTime    the trade's epoch-second timestamp (0 when unknown)
	 * @param freshColor   the normal value color
	 * @param staleColor   the dimmed color used once the value is stale
	 */
	private void applyLiveStaleness(JLabel cell, long value, String sideLabel, String timeLabel,
			long tradeTime, Color freshColor, Color staleColor)
	{
		cell.setToolTipText("<html>" + sideLabel + ": " + GpFormat.grouped(value) + " gp<br>"
				+ timeLabel + ": " + formatAge(tradeTime) + "</html>");

		if (isStale(tradeTime))
			cell.setForeground(staleColor);
		else
			cell.setForeground(freshColor);
	}

	/** @return whether {@code epochSeconds} is older than the configured stale-price threshold. */
	private boolean isStale(long epochSeconds)
	{
		if (epochSeconds <= 0)
			return false;

		long ageSec = System.currentTimeMillis() / 1000L - epochSeconds;
		return ageSec > (long) config.stalePriceThresholdMinutes() * 60L;
	}

	/**
	 * Formats an epoch-second timestamp's age as a compact relative string,
	 * e.g. {@code "5s"}, {@code "5m"}, {@code "3hr"}, {@code "2d ago"}.
	 */
	static String formatAge(long epochSeconds)
	{
		if (epochSeconds <= 0)
			return "unknown";

		long ageSec = Math.max(0, System.currentTimeMillis() / 1000L - epochSeconds);
		if (ageSec < 60)
			return ageSec + "s ago";

		long mins = ageSec / 60;
		if (mins < 60)
			return mins + "m ago";

		long hours = mins / 60;
		if (hours < 24)
			return hours + "hr ago";

		return (hours / 24) + "d ago";
	}

	/** Gives a totals label a full-number tooltip when its text is abbreviated, none otherwise. */
	private void applyTotalTooltip(JLabel label, long value, ValueFormat fmt)
	{
		if (fmt == ValueFormat.ABBREVIATED)
			label.setToolTipText(GpFormat.grouped(value) + " gp");
		else
			label.setToolTipText(null);
	}

	/** Formats a totals value as either full or abbreviated gp per the configured {@link ValueFormat}. */
	static String formatTotalGp(long value, ValueFormat fmt)
	{
		return fmt == ValueFormat.FULL ? GpFormat.fullGp(value) : GpFormat.shortGp(value);
	}

	/** @return the item id whose detail view is open, or a non-positive value when on the main list. */
	public int getDetailItemId()
	{
		return detailView.getBoundItemId();
	}

	/**
	 * Opens the tracked detail card for {@code itemId}. A no-op when that item's tracked
	 * detail is already showing, so re-opening it (e.g. from the GE integration) leaves the
	 * card's scroll position and state untouched instead of rebuilding it back to the top.
	 */
	public void openTrackedDetail(int itemId)
	{
		if (detailView.getBoundItemId() == itemId && !detailView.isPreview())
			return;

		showDetail(itemId);
	}

	/** @return whether the user is mid-edit in the notifications table, so the plugin should defer firing rules. */
	public boolean isEditingNotifications()
	{
		return detailView.isEditingNotifications();
	}

	/** Supplies the latest nature/fire rune prices used to compute high-alch profit in the detail view. */
	public void setAlchRunePrices(long naturePrice, long firePrice)
	{
		this.natureRunePrice = naturePrice;
		this.fireRunePrice = firePrice;
	}

	/** Re-populates the open detail view with fresh data for {@code itemId} (no-op if a different item is shown). */
	public void refreshDetailData(int itemId)
	{
		detailView.refreshDetailData(itemId);
	}

	/**
	 * Reveals the tracked detail card for {@code itemId} in the card stack and binds the detail view to
	 * it. A no-op when the item is not tracked.
	 */
	private void showDetail(int itemId)
	{
		if (currentItems.get(itemId) == null)
			return;

		footerPanel.setVisible(false);
		cardLayout.show(cardsHost, CARD_DETAIL);
		detailView.show(itemId);
	}

	private final List<PopoutHandle> openPopouts = new ArrayList<>();
	/** Re-fetch actions for open portfolio-chart pop-outs; run on every rebuild so they update live. */
	private final List<Runnable> portfolioPopoutRefreshers = new ArrayList<>();

	/** Paints the small line-chart icon used to open the portfolio value chart. */
	private Icon buildChartIcon()
	{
		int s = 11;
		BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(ColorScheme.LIGHT_GRAY_COLOR);
		g.setStroke(new BasicStroke(1f));

		g.drawLine(1, 0, 1, s - 1);
		g.drawLine(1, s - 1, s - 1, s - 1);
		g.drawLine(2, 8, 4, 5);
		g.drawLine(4, 5, 6, 7);
		g.drawLine(6, 7, 9, 2);
		g.dispose();
		return new ImageIcon(img);
	}

	/** Builds a borderless icon button with the given icon, tooltip, click action, and a hover highlight. */
	static JButton buildIconButton(Icon icon, String tooltip, Runnable onClick)
	{
		JButton btn = new JButton(icon);
		btn.setToolTipText(tooltip);
		btn.setBackground(ColorScheme.DARK_GRAY_COLOR);
		btn.setFocusPainted(false);
		btn.setBorder(new EmptyBorder(2, 4, 2, 4));
		btn.setContentAreaFilled(false);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.addActionListener(e -> onClick.run());
		btn.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				btn.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
				btn.setContentAreaFilled(true);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				btn.setContentAreaFilled(false);
				btn.setBackground(ColorScheme.DARK_GRAY_COLOR);
			}
		});
		return btn;
	}

	/**
	 * Opens a read-only preview of an untracked item in the detail view. Unlike
	 * {@link #showDetail}, the item is not in the tracked list; the plugin supplies
	 * its price/history data directly and the tracked-only sections stay hidden.
	 */
	public void showPreview(TrackedItem item)
	{
		footerPanel.setVisible(false);
		cardLayout.show(cardsHost, CARD_DETAIL);
		detailView.showPreview(item);
	}

	/** Returns to the main item list, closing any open pop-outs. */
	private void showMain()
	{
		detailView.onLeaveDetail();
		closePopouts();
		footerPanel.setVisible(true);
		cardLayout.show(cardsHost, CARD_MAIN);
	}

	/** Disposes all open pop-out windows owned by the panel (portfolio, What's New). */
	private void closePopouts()
	{
		for (PopoutHandle h : new ArrayList<>(openPopouts))
			h.frame.dispose();

		openPopouts.clear();
	}

	/**
	 * Opens a non-modal pop-out window hosting {@code content}, registering its
	 * refresher so live updates reach it and running {@code onClose} when dismissed.
	 */
	private void showPopout(String title, JComponent content, Consumer<TrackedItem> refresher, Runnable onClose)
	{
		JPanel holder = new JPanel(new BorderLayout());
		holder.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		holder.setBorder(new EmptyBorder(8, 8, 8, 8));
		holder.add(content, BorderLayout.CENTER);

		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setContentPane(holder);

		TrackedItem current = detailView.shownItem();
		if (current != null)
			refresher.accept(current);

		frame.pack();
		frame.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));

		PopoutHandle handle = new PopoutHandle(frame, refresher, onClose);
		openPopouts.add(handle);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				openPopouts.remove(handle);
				if (onClose != null)
					onClose.run();
			}
		});

		frame.setVisible(true);
	}

	/** Loads the bundled {@code eye.png} scaled to a square icon for the view-only button. */
	private Icon buildEyeIcon(int size)
	{
		BufferedImage img = ImageUtil.loadImageResource(getClass(), "eye.png");
		if (img == null)
			return new ImageIcon(new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB));

		return new ImageIcon(img.getScaledInstance(size, size, Image.SCALE_SMOOTH));
	}

	/** {@inheritDoc} Supplies the panel's live plugin config to the detail view. */
	@Override
	public StockpileConfig config()
	{
		return config;
	}

	/** {@inheritDoc} Supplies the panel's shared item manager to the detail view. */
	@Override
	public ItemManager itemManager()
	{
		return itemManager;
	}

	/** {@inheritDoc} Resolves the examine text through the panel's examine lookup. */
	@Override
	public String examine(int itemId)
	{
		return examineLookup == null ? null : examineLookup.apply(itemId);
	}

	/** {@inheritDoc} Reads from the panel's current tracked-item map. */
	@Override
	public TrackedItem trackedItem(int itemId)
	{
		return currentItems.get(itemId);
	}

	/** {@inheritDoc} Returns the nature-rune price the panel currently holds. */
	@Override
	public long natureRunePrice()
	{
		return natureRunePrice;
	}

	/** {@inheritDoc} Returns the fire-rune price the panel currently holds. */
	@Override
	public long fireRunePrice()
	{
		return fireRunePrice;
	}

	/** {@inheritDoc} Delegates to the panel's detail-data request callback when present. */
	@Override
	public void requestDetailData(int itemId)
	{
		if (onRequestDetailData != null)
			onRequestDetailData.accept(itemId);
	}

	/** {@inheritDoc} Delegates to the panel's acquisitions-edited callback when present. */
	@Override
	public void acquisitionsEdited(int itemId)
	{
		if (onAcquisitionsEdited != null)
			onAcquisitionsEdited.accept(itemId);
	}

	/** {@inheritDoc} Delegates to the panel's clear-acquisitions callback when present. */
	@Override
	public void clearAcquisitions(int itemId)
	{
		if (onClearAcquisitions != null)
			onClearAcquisitions.accept(itemId);
	}

	/** {@inheritDoc} Delegates to the panel's notifications-edited callback when present. */
	@Override
	public void notificationsEdited(int itemId)
	{
		if (onNotificationsEdited != null)
			onNotificationsEdited.accept(itemId);
	}

	/** {@inheritDoc} Delegates to the panel's add-item callback. */
	@Override
	public void addItem(int itemId, TrackItemMode mode)
	{
		onAddItem.accept(itemId, mode);
	}

	/** {@inheritDoc} Delegates to the panel's untrack-to-preview callback. */
	@Override
	public void untrackToPreview(int itemId)
	{
		onUntrackToPreview.accept(itemId);
	}

	/** {@inheritDoc} Delegates to the panel's pop-out callback. */
	@Override
	public void popOut(int itemId)
	{
		if (itemId > 0)
			onPopOut.accept(itemId);
	}

	/** {@inheritDoc} Delegates to the panel's add-to-compare callback. */
	@Override
	public void addToCompare(int itemId)
	{
		if (itemId > 0)
			onAddToCompare.accept(itemId);
	}

	/**
	 * {@inheritDoc} The sidebar has no dashboard search bar, so this fires only via the shared host
	 * contract; it shows the requested item's detail in place.
	 */
	@Override
	public void switchDetailItem(int itemId)
	{
		if (itemId > 0)
			showDetail(itemId);
	}

	/** {@inheritDoc} Returns the sidebar panel to the main tracked-item list. */
	@Override
	public void onBack()
	{
		showMain();
	}

	/** Prompts for confirmation, then clears all tracked items via the plugin callback. */
	private void confirmAndClearAll()
	{
		int count = currentItems.size();
		if (count == 0)
			return;

		int choice = JOptionPane.showConfirmDialog(
				this,
				"Remove all " + count + " tracked item" + (count == 1 ? "" : "s")
						+ ", including their notifications and collection log?\nThis cannot be undone.",
				"Clear tracked items",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
		if (choice == JOptionPane.YES_OPTION && onClearAll != null)
			onClearAll.run();
	}

	/** Detaches any hover-tint listener from a label before its value is replaced. */
	static void removeHoverTint(JLabel label)
	{
		for (MouseListener ml : label.getMouseListeners())
		{
			if (ml instanceof HoverTintListener)
				label.removeMouseListener(ml);
		}
	}

	/** @return the value as a comma-grouped gp string with an explicit {@code +} when positive. */
	static String signedGp(long v)
	{
		return GpFormat.signedGrouped(v) + " gp";
	}
}
