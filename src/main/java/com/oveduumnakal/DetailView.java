/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DefaultCaret;

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
 * The item detail view, extracted from {@link StockpilePanel} so a second live instance can exist
 * (the #109 dashboard window) (#110). Owns every detail widget, the build/populate/apply methods, and
 * the section pop-outs; reaches its host for shared services, edit callbacks, and Back navigation
 * through {@link DetailViewHost}. The component itself is a {@link CardLayout} flipping between the
 * populated detail card and a loading placeholder.
 */
public class DetailView extends JPanel implements Scrollable
{
	/** Section arrangement: the sidebar's vertical stack, or the #109 dashboard's two-column layout. */
	enum Layout
	{
		STACK,
		DASHBOARD
	}

	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);
	private static final Color COLOR_VOLUME = new Color(200, 200, 200);
	private static final Color DESCRIPTION_COLOR = new Color(160, 160, 160);
	private static final Color SEARCH_PLACEHOLDER_COLOR = new Color(140, 140, 140);
	private static final Font DASHBOARD_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 10);

	/**
	 * Fixed dashboard column assignments (#109), indexing the section array built in
	 * {@link #applyDetailSectionLayout()}: left = item values, market info, alch, notifications;
	 * middle = collection values, price overview, collection log; right = price and volume graphs.
	 */
	private static final int[] DASHBOARD_LEFT = {0, 2, 6, 7};

	/** Middle dashboard column section indices; see {@link #DASHBOARD_LEFT}. */
	private static final int[] DASHBOARD_MIDDLE = {1, 3, 8};

	/** Right dashboard column section indices (the graphs); see {@link #DASHBOARD_LEFT}. */
	private static final int[] DASHBOARD_RIGHT = {4, 5};
	private static final Color OVERVIEW_ROW_DIVIDER = new Color(45, 45, 45);
	private static final int DEFAULT_NOTIFICATION_ROWS = 5;
	private static final int PRESSURE_BALANCED_LOW = 45;
	private static final int PRESSURE_BALANCED_HIGH = 55;
	private static final String WIKI_BASE = "https://oldschool.runescape.wiki/w/";
	private static final String PRICES_BASE = "https://prices.runescape.wiki/osrs/item/";
	private static final String WIKI_HOME = "https://oldschool.runescape.wiki/";
	private static final String PRICES_HOME = "https://prices.runescape.wiki/osrs/";
	private static final TimeWindow[] OVERVIEW_WINDOWS = {
			TimeWindow.LIVE, TimeWindow.M5, TimeWindow.H1, TimeWindow.H3, TimeWindow.H6, TimeWindow.H12,
			TimeWindow.H24, TimeWindow.WEEK, TimeWindow.MONTH, TimeWindow.MONTH3,
			TimeWindow.MONTH6, TimeWindow.YEAR
	};
	private static final String CARD_LOADING = "loading";
	private static final String CARD_CONTENT = "content";

	private final DetailViewHost host;
	private final Layout viewLayout;
	private final StockpileConfig config;
	private final ItemManager itemManager;
	private final Consumer<Integer> onAcquisitionsEdited;
	private final Consumer<Integer> onClearAcquisitions;
	private final Consumer<Integer> onNotificationsEdited;
	private final Consumer<Integer> onRequestDetailData;
	private final BiConsumer<Integer, TrackItemMode> onAddItem;
	private final Consumer<Integer> onUntrackToPreview;
	private final CardLayout cardLayout = new CardLayout();

	private int boundItemId = -1;
	private TrackedItem previewItem;
	private final JPanel detailLoadingCard = new JPanel(new GridBagLayout());
	private final Spinner detailSpinner = new Spinner();
	private Timer detailLoadTimeout;
	private boolean detailLoadTimedOut;

	private final JPanel detailCard = new JPanel(new BorderLayout(0, 8));
	private final JLabel detailIconLabel = new JLabel();
	private final JLabel detailNameLabel = new JLabel();
	private final JLabel detailQtyLabel = new JLabel();
	private final JButton detailTrackBtn = new JButton();
	private JButton dashboardWikiBtn;
	private JButton dashboardPricesBtn;
	private JPanel dashboardRightControls;
	private JButton detailPopOutBtn;
	private boolean detailItemTracked;
	private final JTextArea detailDescriptionArea = new JTextArea()
	{
		@Override
		public Dimension getMaximumSize()
		{
			return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
		}
	};

	private final JLabel icvHigh = new JLabel();
	private final JLabel icvLow = new JLabel();
	private final JLabel icvAvg = new JLabel();
	private final JLabel icvVolume = new JLabel();

	private JPanel ccvSection;
	private final JLabel ccvHigh = new JLabel();
	private final JLabel ccvLow = new JLabel();
	private final JLabel ccvAvg = new JLabel();
	private final JLabel ccvQuantity = new JLabel();
	private final JLabel ccvProfit = new JLabel();

	private final JLabel miBuyLimit = new JLabel();
	private final JLabel miGeTax = new JLabel();
	private final JLabel miLastBought = new JLabel();
	private final JLabel miLastSold = new JLabel();
	private final JLabel miVolatility = new JLabel();
	private final JLabel miLiquidity = new JLabel();
	private PriceRangeBar priceRangeBar;
	private final JLabel rangePositionLabel = new JLabel();

	private BuySellBar buySellBar;
	private final JLabel pressureMarketLabel = new JLabel();
	private final PressureVolumeLabel buyPressureLabel = new PressureVolumeLabel();
	private final PressureVolumeLabel sellPressureLabel = new PressureVolumeLabel();

	private final JLabel haValue = new JLabel();
	private final JLabel haProfit = new JLabel();
	private final JLabel laValue = new JLabel();
	private final JLabel laProfit = new JLabel();
	private final JLabel alchEstProfit = new JLabel();
	private JPanel alchEstProfitRow;

	private AcquisitionsTableModel acquisitionsModel;
	private JTable acquisitionsTable;
	private int acqHoverRow = -1;
	private int acqHoverCol = -1;
	private JScrollPane acquisitionsScroll;
	private JPanel acquisitionsSection;
	private NotificationsTableModel notificationsModel;
	private JTable notificationsTable;
	private volatile boolean editingNotifications;
	private JPanel notificationsSection;

	private JButton acqPopoutButton;
	private AcquisitionsTableModel acqPopoutModel;
	private JTable acqPopoutTable;
	private JScrollPane acqPopoutScroll;
	private JPanel overviewGrid;
	private PriceGraphPanel priceGraph;
	private PriceGraphPanel volumeGraph;

	private JPanel topStack;
	private String detailExamineText;
	private boolean dashboardHome;
	private JLabel dashboardEmptyMessage;
	private JPanel detailSectionsHost;
	private JPanel dashboardLeftColumn;
	private JPanel dashboardMiddleColumn;
	private JPanel dashboardRightColumn;
	private JPopupMenu dashboardSearchPopup;
	private JTextField dashboardSearchField;
	private int firstSearchResultId = -1;
	private JPanel itemValuesSection;
	private JPanel marketInfoSection;
	private JPanel priceOverviewSection;
	private JPanel priceGraphSection;
	private JPanel volumeGraphSection;
	private JPanel alchInfoSection;
	private JPanel linksSection;
	private String appliedSectionLayout;
	private Set<TimeWindow> appliedOverviewRows;

	private final Map<TimeWindow, JLabel[]> overviewLabels = new EnumMap<>(TimeWindow.class);
	private final List<JLabel> overviewWindowLabels = new ArrayList<>();

	private final List<PopoutHandle> openPopouts = new ArrayList<>();
	private boolean graphSmooth = true;
	private PriceGraphPanel.LineSet graphLineSet = PriceGraphPanel.LineSet.ALL;
	private PriceGraphPanel pricePopoutGraph;

	/**
	 * Builds a detail view bound to a host. Constructs the detail and loading cards and installs them
	 * in this component's own {@link CardLayout}; the host mounts this component where the detail view
	 * should appear.
	 */
	DetailView(DetailViewHost host, Layout viewLayout)
	{
		this.host = host;
		this.viewLayout = viewLayout;
		if (viewLayout == Layout.DASHBOARD)
			graphSmooth = false;

		this.config = host.config();
		this.itemManager = host.itemManager();
		this.onAcquisitionsEdited = host::acquisitionsEdited;
		this.onClearAcquisitions = host::clearAcquisitions;
		this.onNotificationsEdited = host::notificationsEdited;
		this.onRequestDetailData = host::requestDetailData;
		this.onAddItem = host::addItem;
		this.onUntrackToPreview = host::untrackToPreview;
		buildDetailCard();
		buildDetailLoadingCard();
		detailLoadTimeout = new Timer(12000, e ->
		{
			detailLoadTimedOut = true;
			applyDetailCard();
		});
		detailLoadTimeout.setRepeats(false);
		setLayout(cardLayout);
		add(detailCard, CARD_CONTENT);
		add(detailLoadingCard, CARD_LOADING);
		cardLayout.show(this, CARD_CONTENT);
	}

	/** @return the item backing the detail view (tracked or preview), or {@code null} when none is shown. */
	public TrackedItem shownItem()
	{
		return shownDetailItem();
	}

	/** Stops the loading spinner and its safety timeout (used when the host is disposed). */
	public void stopLoading()
	{
		stopDetailLoading();
	}

	/** @return whether the loading-spinner placeholder (rather than the populated card) is currently shown. */
	public boolean isLoadingVisible()
	{
		return detailLoadingCard.isVisible();
	}

	/**
	 * Refreshes the bound item from the host's current tracked state on a list rebuild: clears a stale
	 * preview once the item is tracked, repopulates in place, and reports whether an item is still shown
	 * so the host can fall back to the main list when it has gone.
	 *
	 * @return {@code true} if an item is still shown, {@code false} if the bound item has disappeared
	 */
	public boolean onRebuild()
	{
		if (boundItemId <= 0)
			return false;

		TrackedItem detail = host.trackedItem(boundItemId);
		if (detail != null)
			previewItem = null;
		else if (previewItem != null && previewItem.getItemId() == boundItemId)
			detail = previewItem;

		if (detail != null)
		{
			final TrackedItem shown = detail;
			SwingUtilities.invokeLater(() ->
					preserveDetailScroll(() ->
					{
						populateDetail(shown);
						applyDetailCard();
					}));
			return true;
		}

		return false;
	}

	/** @return the item id currently bound to this detail view, or -1 when none. */
	public int getBoundItemId()
	{
		return boundItemId;
	}

	/** @return whether the shown item is a read-only preview rather than a tracked item. */
	public boolean isPreview()
	{
		return previewItem != null;
	}

	/** @return whether the user is mid-edit in the notifications table. */
	public boolean isEditingNotifications()
	{
		return editingNotifications;
	}

	/**
	 * Opens a read-only preview of an untracked item. The item is not in the tracked list; the plugin
	 * supplies its price/history data directly and the tracked-only sections stay hidden.
	 */
	public void showPreview(TrackedItem item)
	{
		dashboardHome = false;
		previewItem = item;
		boundItemId = item.getItemId();
		detailLoadTimedOut = false;
		populateDetail(item);
		applyDetailCard();
	}

	/** Re-populates the open detail view with fresh data for {@code itemId} (no-op if a different item is shown). */
	public void refreshDetailData(int itemId)
	{
		if (boundItemId != itemId)
			return;

		TrackedItem item = host.trackedItem(itemId);
		if (item == null && previewItem != null && previewItem.getItemId() == itemId)
			item = previewItem;

		if (item != null)
		{
			final TrackedItem shown = item;
			preserveDetailScroll(() -> populateDetail(shown));
		}
	}

	/** Clears the bound item and stops any in-flight loading/pop-outs when the host leaves the detail view. */
	public void onLeaveDetail()
	{
		boundItemId = -1;
		previewItem = null;
		stopDetailLoading();
		closePopouts();
	}

	/** Live-updates the Market Info last-bought / last-sold relative times for the shown detail item. */
	public void updateMarketInfoTimes()
	{
		TrackedItem item = host.trackedItem(boundItemId);
		if (item == null && previewItem != null && previewItem.getItemId() == boundItemId)
			item = previewItem;

		if (item == null)
			return;

		applyTradeTime(miLastBought, item.getLatestHighTime());
		applyTradeTime(miLastSold, item.getLatestLowTime());
	}

	/** Sets a label to an epoch-second trade time's relative age, with the absolute time as a tooltip. */
	private void applyTradeTime(JLabel label, long epochSeconds)
	{
		label.setText(StockpilePanel.formatAge(epochSeconds));
		label.setToolTipText(epochSeconds > 0 ? new Date(epochSeconds * 1000L).toString() : null);
	}

	/**
	 * Shows the Buy Limit cell as {@code used / total} when purchases have been tracked in
	 * the current window (with a reset-countdown tooltip), the plain total when untouched,
	 * or {@code N/A} when the item has no GE limit.
	 */
	private void applyBuyLimit(TrackedItem item)
	{
		int limit = item.getBuyLimit();
		if (limit <= 0)
		{
			miBuyLimit.setText("N/A");
			miBuyLimit.setToolTipText(null);
			return;
		}

		if (item.getLimitResetEpoch() <= 0)
		{
			miBuyLimit.setText(NUMBER_FORMAT.format(limit));
			miBuyLimit.setToolTipText(null);
			return;
		}

		miBuyLimit.setText(NUMBER_FORMAT.format(item.getLimitBought()) + " / " + NUMBER_FORMAT.format(limit));
		long secondsLeft = item.getLimitResetEpoch() - System.currentTimeMillis() / 1000L;
		miBuyLimit.setToolTipText(secondsLeft > 0
				? "Resets in " + formatDuration(secondsLeft)
				: "Limit window reset");
	}

	/** Formats a positive second count as a compact {@code "2h 14m"} / {@code "43m"} / {@code "12s"} duration. */
	private static String formatDuration(long seconds)
	{
		long h = seconds / 3600;
		long m = (seconds % 3600) / 60;
		if (h > 0)
			return h + "h " + m + "m";

		if (m > 0)
			return m + "m";

		return seconds + "s";
	}

	/** Resets a value label to plain text, dropping its tooltip and any hover-tint listener. */
	private void clearItemValue(JLabel label, String text)
	{
		for (MouseListener ml : label.getMouseListeners())
		{
			if (ml instanceof HoverTintListener)
				label.removeMouseListener(ml);
		}

		label.setToolTipText(null);
		label.setText(text);
	}

	/**
	 * Constructs the detail-view card once: the header, the scrollable body, and
	 * every detail section (current values, market info, charts, overview grid,
	 * alch, notifications, acquisitions log). Sections are populated later per item.
	 */
	private void buildDetailCard()
	{
		detailCard.setBackground(ColorScheme.DARK_GRAY_COLOR);
		detailCard.setBorder(new EmptyBorder(10, 10, 10, 10));

		detailTrackBtn.setFocusPainted(false);
		detailTrackBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		detailTrackBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		detailTrackBtn.addActionListener(e -> toggleDetailTracking());

		detailIconLabel.setPreferredSize(new Dimension(32, 32));
		detailIconLabel.setVerticalAlignment(SwingConstants.CENTER);
		detailNameLabel.setForeground(Color.WHITE);
		detailNameLabel.setFont(boldFont());
		detailNameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				detailNameLabel.getFontMetrics(detailNameLabel.getFont()).getHeight()));
		detailQtyLabel.setForeground(Color.WHITE);
		detailQtyLabel.setFont(smallFont());

		detailDescriptionArea.setEditable(false);
		detailDescriptionArea.setFocusable(false);
		detailDescriptionArea.setOpaque(false);
		((DefaultCaret) detailDescriptionArea.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		detailDescriptionArea.setLineWrap(true);
		detailDescriptionArea.setWrapStyleWord(true);
		detailDescriptionArea.setMargin(new Insets(0, 0, 0, 0));
		detailDescriptionArea.setForeground(DESCRIPTION_COLOR);
		detailDescriptionArea.setFont(smallFont().deriveFont(Font.ITALIC));
		detailDescriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
		detailDescriptionArea.setBorder(new EmptyBorder(8, 0, 0, 0));

		JPanel titleTextStack = new JPanel();
		titleTextStack.setLayout(new BoxLayout(titleTextStack, BoxLayout.Y_AXIS));
		titleTextStack.setBackground(ColorScheme.DARK_GRAY_COLOR);
		detailNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		detailQtyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		titleTextStack.add(detailNameLabel);
		titleTextStack.add(Box.createVerticalStrut(2));
		titleTextStack.add(detailQtyLabel);

		topStack = new JPanel();
		topStack.setLayout(new BoxLayout(topStack, BoxLayout.Y_AXIS));
		topStack.setBackground(ColorScheme.DARK_GRAY_COLOR);
		if (viewLayout == Layout.DASHBOARD)
			buildDashboardToolbar(titleTextStack);
		else
			buildStackHeader(titleTextStack);

		detailSectionsHost = new JPanel();
		detailSectionsHost.setBackground(ColorScheme.DARK_GRAY_COLOR);
		detailSectionsHost.setAlignmentX(Component.LEFT_ALIGNMENT);
		if (viewLayout == Layout.DASHBOARD)
		{
			detailSectionsHost.setLayout(new WeightedColumnsLayout(new double[]{0.2, 0.2, 0.6}, 8));
			dashboardLeftColumn = buildDashboardColumn();
			dashboardMiddleColumn = buildDashboardColumn();
			dashboardRightColumn = buildDashboardColumn();
			detailSectionsHost.add(dashboardLeftColumn);
			detailSectionsHost.add(dashboardMiddleColumn);
			detailSectionsHost.add(dashboardRightColumn);
		}
		else
		{
			detailSectionsHost.setLayout(new BoxLayout(detailSectionsHost, BoxLayout.Y_AXIS));
		}

		topStack.add(detailSectionsHost);

		itemValuesSection = buildDetailSection("Item Current Values",
				buildCurrentValuesBlock(icvHigh, icvLow, icvAvg, icvVolume, null));

		ccvSection = buildDetailSection("Collection Current Values",
				buildCurrentValuesBlock(ccvHigh, ccvLow, ccvAvg, ccvQuantity, ccvProfit));

		priceOverviewSection = buildDetailSectionWithPopout("Price Overview",
				this::openOverviewPopout, buildOverviewGrid());

		boolean expandedGraphs = viewLayout == Layout.DASHBOARD;

		priceGraph = new PriceGraphPanel(PriceGraphPanel.Mode.PRICE, expandedGraphs, DASHBOARD_FONT.getSize());
		priceGraph.setAlignmentX(Component.LEFT_ALIGNMENT);
		if (expandedGraphs)
		{
			priceGraph.setActiveWindow(TimeWindow.WEEK);
			sizeDashboardGraph(priceGraph, 420);
		}

		priceGraph.setSmooth(graphSmooth);
		priceGraph.setSmoothListener(b ->
		{
			graphSmooth = b;
			if (pricePopoutGraph != null)
				pricePopoutGraph.setSmooth(b);
		});
		priceGraph.setLineSet(graphLineSet);
		priceGraph.setLineSetListener(set ->
		{
			graphLineSet = set;
			if (pricePopoutGraph != null)
				pricePopoutGraph.setLineSet(set);
		});
		priceGraphSection = buildDetailSectionWithPopout("Price Graph",
				() -> openGraphPopout("Price Graph", PriceGraphPanel.Mode.PRICE, priceGraph),
				priceGraph, Box.createVerticalStrut(4));

		volumeGraph = new PriceGraphPanel(PriceGraphPanel.Mode.VOLUME, expandedGraphs, DASHBOARD_FONT.getSize());
		volumeGraph.setAlignmentX(Component.LEFT_ALIGNMENT);
		if (expandedGraphs)
		{
			volumeGraph.setActiveWindow(TimeWindow.WEEK);
			sizeDashboardGraph(volumeGraph, 320);
		}

		volumeGraphSection = buildDetailSectionWithPopout("Volume Graph",
				() -> openGraphPopout("Volume Graph", PriceGraphPanel.Mode.VOLUME, volumeGraph),
				volumeGraph);

		marketInfoSection = buildDetailSection("Market Info", buildMarketInfoBlock());

		alchInfoSection = buildDetailSection("Alchemy Info", buildAlchBlock());

		linksSection = buildDetailSection("Links", buildLinksBlock());

		detailCard.add(topStack, BorderLayout.NORTH);

		if (viewLayout == Layout.DASHBOARD)
		{
			dashboardEmptyMessage = new JLabel("Search for an Item to see its detailed view...",
					SwingConstants.CENTER);
			dashboardEmptyMessage.setVerticalAlignment(SwingConstants.CENTER);
			dashboardEmptyMessage.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
			dashboardEmptyMessage.setFont(smallFont().deriveFont(Font.ITALIC, 13f));
			dashboardEmptyMessage.setVisible(false);
			detailCard.add(dashboardEmptyMessage, BorderLayout.CENTER);
		}

		acquisitionsModel = new AcquisitionsTableModel(config, onAcquisitionsEdited, () -> boundItemId, false);
		acquisitionsTable = new JTable(acquisitionsModel)
		{
			@Override
			public Dimension getPreferredScrollableViewportSize()
			{
				int visibleRows = Math.min(10, Math.max(5, getRowCount() + 1));
				Dimension prefBody = super.getPreferredScrollableViewportSize();
				return new Dimension(prefBody.width, visibleRows * getRowHeight());
			}

			@Override
			public String getToolTipText(MouseEvent e)
			{
				int row = rowAtPoint(e.getPoint());
				int col = columnAtPoint(e.getPoint());
				if (row < 0 || col < 0)
					return null;

				if (acquisitionsModel.isSymbolColumn(convertColumnIndexToModel(col)))
					return acquisitionsModel.sourceLabelAt(row);

				if (convertColumnIndexToModel(col) == 2 && acquisitionsModel.isSellEstimated(row))
					return AcqCellRenderer.ESTIMATED_TOOLTIP;

				Object val = getValueAt(row, col);
				if (val instanceof Number)
				{
					long v = ((Number) val).longValue();
					if (Math.abs(v) >= (col == 3 ? 1000 : 10000))
						return acqTooltipLabel(col) + ": " + NUMBER_FORMAT.format(v);
				}

				return null;
			}

			@Override
			protected JTableHeader createDefaultTableHeader()
			{
				return new JTableHeader(columnModel)
				{
					@Override
					public String getToolTipText(MouseEvent e)
					{
						int col = columnAtPoint(e.getPoint());
						return col < 0 ? null : acqTooltipLabel(convertColumnIndexToModel(col));
					}
				};
			}
		};
		acquisitionsTable.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				int r = acquisitionsTable.rowAtPoint(e.getPoint());
				int c = acquisitionsTable.columnAtPoint(e.getPoint());
				if (r != acqHoverRow || c != acqHoverCol)
				{
					acqHoverRow = r;
					acqHoverCol = c;
					acquisitionsTable.repaint();
				}
			}
		});
		acquisitionsTable.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		acquisitionsTable.setForeground(Color.WHITE);
		acquisitionsTable.setFont(smallFont());
		acquisitionsTable.setGridColor(StockpileColors.TABLE_GRID);
		acquisitionsTable.setRowHeight(22);
		acquisitionsTable.setFillsViewportHeight(true);
		acquisitionsTable.getTableHeader().setBackground(ColorScheme.DARKER_GRAY_COLOR);
		acquisitionsTable.getTableHeader().setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		acquisitionsTable.getTableHeader().setFont(smallFont());
		applyTableRenderers();
		TableCellRenderer headerRenderer = acquisitionsTable.getTableHeader().getDefaultRenderer();
		if (headerRenderer instanceof DefaultTableCellRenderer)
			((DefaultTableCellRenderer) headerRenderer).setHorizontalAlignment(SwingConstants.CENTER);

		acquisitionsTable.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseExited(MouseEvent e)
			{
				if (acqHoverRow != -1 || acqHoverCol != -1)
				{
					acqHoverRow = -1;
					acqHoverCol = -1;
					acquisitionsTable.repaint();
				}
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() != 2 || e.getButton() != MouseEvent.BUTTON1)
					return;

				int row = acquisitionsTable.rowAtPoint(e.getPoint());
				if (row >= 0)
					return;

				TrackedItem t = host.trackedItem(boundItemId);
				if (t == null)
					return;

				long price = t.getAvgPrice() > 0 ? t.getAvgPrice() : 0;
				t.getAcquisitions().add(new AcquisitionRecord(0, price, null, AcquisitionSource.MANUAL));
				acquisitionsModel.fireTableDataChanged();
				acquisitionsTable.revalidate();
				onAcquisitionsEdited.accept(boundItemId);
				int newRow = acquisitionsModel.getRowCount() - 1;
				scrollAcquisitionsToBottom();
				if (newRow >= 0 && acquisitionsTable.editCellAt(newRow, 0))
				{
					acquisitionsTable.changeSelection(newRow, 0, false, false);
					Component editor = acquisitionsTable.getEditorComponent();
					if (editor != null)
						editor.requestFocusInWindow();
				}
			}
		});

		JScrollPane tableScroll = new JScrollPane(acquisitionsTable);
		tableScroll.getViewport().setBackground(ColorScheme.DARKER_GRAY_COLOR);
		tableScroll.setBorder(BorderFactory.createLineBorder(StockpileColors.TABLE_GRID));
		tableScroll.setMinimumSize(new Dimension(0, acquisitionsTable.getRowHeight() * 5 + 26));
		acquisitionsScroll = tableScroll;

		JButton addRowBtn = new JButton("+ Add");
		addRowBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		addRowBtn.setForeground(Color.WHITE);
		addRowBtn.setFocusPainted(false);
		addRowBtn.setFont(smallFont());
		addRowBtn.setMargin(new Insets(2, 5, 2, 5));
		addRowBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addRowBtn.addActionListener(e ->
		{
			TrackedItem t = host.trackedItem(boundItemId);
			if (t == null)
				return;

			long price = t.getAvgPrice() > 0 ? t.getAvgPrice() : 0;
			t.getAcquisitions().add(new AcquisitionRecord(0, price, null, AcquisitionSource.MANUAL));
			acquisitionsModel.fireTableDataChanged();
			acquisitionsTable.revalidate();
			onAcquisitionsEdited.accept(boundItemId);
			scrollAcquisitionsToBottom();
		});

		JButton removeRowBtn = new JButton("− Remove");
		removeRowBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		removeRowBtn.setForeground(Color.WHITE);
		removeRowBtn.setFocusPainted(false);
		removeRowBtn.setFont(smallFont());
		removeRowBtn.setMargin(new Insets(2, 5, 2, 5));
		removeRowBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		removeRowBtn.setEnabled(false);

		Runnable removeSelectedRows = () ->
		{
			TrackedItem t = host.trackedItem(boundItemId);
			if (t == null)
				return;

			if (acquisitionsTable.isEditing())

				acquisitionsTable.getCellEditor().stopCellEditing();

			int[] selected = acquisitionsTable.getSelectedRows();
			if (selected.length == 0)
				return;

			List<AcquisitionRecord> records = t.getAcquisitions();
			Arrays.sort(selected);
			for (int i = selected.length - 1; i >= 0; i--)
			{
				int idx = selected[i];
				if (idx >= 0 && idx < records.size())
					records.remove(idx);
			}

			acquisitionsModel.fireTableDataChanged();
			acquisitionsTable.revalidate();
			onAcquisitionsEdited.accept(boundItemId);
		};

		removeRowBtn.addActionListener(e -> removeSelectedRows.run());
		acquisitionsTable.getSelectionModel().addListSelectionListener(e ->
				removeRowBtn.setEnabled(acquisitionsTable.getSelectedRowCount() > 0));

		acquisitionsTable.getInputMap(JComponent.WHEN_FOCUSED)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteSelectedRows");
		acquisitionsTable.getActionMap().put("deleteSelectedRows", new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				removeSelectedRows.run();
			}
		});

		JButton cleanBtn = new JButton(buildBrushIcon());
		cleanBtn.setToolTipText("Remove all rows with quantity 0");
		cleanBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		cleanBtn.setForeground(Color.WHITE);
		cleanBtn.setFocusPainted(false);
		cleanBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		cleanBtn.setMargin(new Insets(2, 4, 2, 4));
		cleanBtn.addActionListener(e ->
		{
			TrackedItem t = host.trackedItem(boundItemId);
			if (t == null)
				return;

			boolean removed = t.getAcquisitions().removeIf(r -> r.getQuantity() == 0);
			if (!removed)
				return;

			acquisitionsModel.fireTableDataChanged();
			acquisitionsTable.revalidate();
			onAcquisitionsEdited.accept(boundItemId);
		});

		JButton clearBtn = new JButton("Clear");
		clearBtn.setToolTipText("Remove every row from the collection log");
		clearBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		clearBtn.setForeground(StockpileColors.LOW);
		clearBtn.setFocusPainted(false);
		clearBtn.setFont(smallFont());
		clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		clearBtn.setMargin(new Insets(2, 5, 2, 5));
		clearBtn.addActionListener(e ->
		{
			TrackedItem t = host.trackedItem(boundItemId);
			if (t == null || t.getAcquisitions().isEmpty())
				return;

			int choice = JOptionPane.showConfirmDialog(
					DetailView.this,
					"Clear the entire collection log for this item?",
					"Clear Collection Log",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (choice != JOptionPane.YES_OPTION)
				return;

			if (acquisitionsTable.isEditing())

				acquisitionsTable.getCellEditor().cancelCellEditing();

			if (onClearAcquisitions != null)
				onClearAcquisitions.accept(boundItemId);
		});

		JButton[] logButtons = {addRowBtn, removeRowBtn, cleanBtn, clearBtn};
		int btnHeight = 0;
		for (JButton b : logButtons)
			btnHeight = Math.max(btnHeight, b.getPreferredSize().height);

		for (JButton b : logButtons)
			b.setPreferredSize(new Dimension(b.getPreferredSize().width, btnHeight));

		JPanel tableButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		tableButtons.setBackground(ColorScheme.DARK_GRAY_COLOR);
		tableButtons.setBorder(new EmptyBorder(4, 0, 4, 0));
		tableButtons.add(addRowBtn);
		tableButtons.add(removeRowBtn);
		tableButtons.add(cleanBtn);
		tableButtons.add(clearBtn);

		acqPopoutButton = buildPopoutButton(this::openCollectionLogPopout);
		acqPopoutButton.setVisible(false);
		JComponent logTitle = buildDetailSectionTitleRow("Item Collection Log", acqPopoutButton);
		tableScroll.getVerticalScrollBar().addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentShown(ComponentEvent e)
			{
				updateAcqPopoutButton();
			}

			@Override
			public void componentHidden(ComponentEvent e)
			{
				updateAcqPopoutButton();
			}
		});

		acquisitionsSection = new JPanel(new BorderLayout(0, 4));
		acquisitionsSection.setBackground(ColorScheme.DARK_GRAY_COLOR);
		acquisitionsSection.setAlignmentX(Component.LEFT_ALIGNMENT);
		acquisitionsSection.add(logTitle, BorderLayout.NORTH);
		acquisitionsSection.add(tableScroll, BorderLayout.CENTER);
		acquisitionsSection.add(tableButtons, BorderLayout.SOUTH);

		buildNotificationsSection();

		rebuildOverviewGrid();
		applyDetailSectionLayout();
	}

	/** Builds the notifications section: the rules table and its add/remove/edit controls. */
	private void buildNotificationsSection()
	{
		notificationsModel = new NotificationsTableModel(this::notifyNotificationsEdited);
		notificationsTable = new JTable(notificationsModel)
		{
			@Override
			public Dimension getPreferredScrollableViewportSize()
			{
				return new Dimension(getPreferredSize().width,
						Math.min(getPreferredSize().height, getRowHeight() * DEFAULT_NOTIFICATION_ROWS + 2));
			}
		};
		notificationsTable.setFillsViewportHeight(true);
		notificationsTable.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		notificationsTable.setForeground(Color.WHITE);
		notificationsTable.setGridColor(StockpileColors.TABLE_GRID);
		notificationsTable.setRowHeight(22);
		notificationsTable.setFont(smallFont());
		notificationsTable.getTableHeader().setFont(smallFont());
		notificationsTable.getTableHeader().setReorderingAllowed(false);
		notificationsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		notificationsTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		notificationsTable.addPropertyChangeListener("tableCellEditor",
				e -> editingNotifications = notificationsTable.isEditing());
		applyNotificationRenderers();

		JScrollPane tableScroll = new JScrollPane(notificationsTable);
		tableScroll.getViewport().setBackground(ColorScheme.DARKER_GRAY_COLOR);
		tableScroll.setBorder(BorderFactory.createLineBorder(StockpileColors.TABLE_GRID));
		int notifMinHeight = notificationsTable.getRowHeight() * DEFAULT_NOTIFICATION_ROWS + 26;
		tableScroll.setMinimumSize(new Dimension(0, notifMinHeight));

		JButton addRowBtn = new JButton("+ Add");
		styleNotifButton(addRowBtn, Color.WHITE);
		addRowBtn.addActionListener(e ->
		{
			TrackedItem t = host.trackedItem(boundItemId);
			if (t == null)
				return;

			t.getNotifications().add(new NotificationRule());
			notificationsModel.fireTableDataChanged();
			notificationsTable.revalidate();
			notifyNotificationsEdited();
		});

		JButton removeRowBtn = new JButton("− Remove");
		styleNotifButton(removeRowBtn, Color.WHITE);
		removeRowBtn.setEnabled(false);
		Runnable removeSelected = () ->
		{
			TrackedItem t = host.trackedItem(boundItemId);
			if (t == null)
				return;

			if (notificationsTable.isEditing())

				notificationsTable.getCellEditor().stopCellEditing();

			int[] selected = notificationsTable.getSelectedRows();
			if (selected.length == 0)
				return;

			List<NotificationRule> rules = t.getNotifications();
			Arrays.sort(selected);
			for (int i = selected.length - 1; i >= 0; i--)
			{
				int idx = selected[i];
				if (idx >= 0 && idx < rules.size())
					rules.remove(idx);
			}

			while (rules.size() < DEFAULT_NOTIFICATION_ROWS)
				rules.add(new NotificationRule());

			notificationsModel.fireTableDataChanged();
			notificationsTable.revalidate();
			notifyNotificationsEdited();
		};
		removeRowBtn.addActionListener(e -> removeSelected.run());
		notificationsTable.getSelectionModel().addListSelectionListener(e ->
				removeRowBtn.setEnabled(notificationsTable.getSelectedRowCount() > 0));
		notificationsTable.getInputMap(JComponent.WHEN_FOCUSED)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteSelectedRules");
		notificationsTable.getActionMap().put("deleteSelectedRules", new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				removeSelected.run();
			}
		});

		JButton clearBtn = new JButton("Clear");
		styleNotifButton(clearBtn, StockpileColors.LOW);
		clearBtn.setToolTipText("Remove every notification rule");
		clearBtn.addActionListener(e ->
		{
			TrackedItem t = host.trackedItem(boundItemId);
			if (t == null || t.getNotifications().isEmpty())
				return;

			int choice = JOptionPane.showConfirmDialog(this,
					"Remove all notification rules for this item?",
					"Clear Notifications", JOptionPane.YES_NO_OPTION);
			if (choice != JOptionPane.YES_OPTION)
				return;

			t.getNotifications().clear();

			for (int i = 0; i < DEFAULT_NOTIFICATION_ROWS; i++)
				t.getNotifications().add(new NotificationRule());

			notificationsModel.fireTableDataChanged();
			notificationsTable.revalidate();
			notifyNotificationsEdited();
		});

		JPanel tableButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		tableButtons.setBackground(ColorScheme.DARK_GRAY_COLOR);
		tableButtons.setBorder(new EmptyBorder(4, 0, 4, 0));
		tableButtons.add(addRowBtn);
		tableButtons.add(removeRowBtn);
		tableButtons.add(clearBtn);

		notificationsSection = new JPanel(new BorderLayout(0, 4));
		notificationsSection.setBackground(ColorScheme.DARK_GRAY_COLOR);
		notificationsSection.setAlignmentX(Component.LEFT_ALIGNMENT);
		notificationsSection.add(buildDetailSectionTitle("Notifications", true), BorderLayout.NORTH);
		notificationsSection.add(tableScroll, BorderLayout.CENTER);
		notificationsSection.add(tableButtons, BorderLayout.SOUTH);
	}

	/** Applies the shared small-button styling to a notifications-section button. */
	private void styleNotifButton(JButton btn, Color fg)
	{
		btn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		btn.setForeground(fg);
		btn.setFocusPainted(false);
		btn.setFont(smallFont());
		btn.setMargin(new Insets(2, 5, 2, 5));
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	/**
	 * Notifies the plugin (via callback) that the current item's notification rules
	 * changed, so it can persist them.
	 */
	private void notifyNotificationsEdited()
	{
		if (onNotificationsEdited != null && boundItemId > 0)
			onNotificationsEdited.accept(boundItemId);
	}

	/** Builds a titled detail-view section containing the given components. */
	private JPanel buildDetailSection(String title, Component... contents)
	{
		JPanel wrapper = newSectionWrapper();
		wrapper.add(buildDetailSectionTitle(title, true));
		for (Component c : contents)
			wrapper.add(c);

		return wrapper;
	}

	/** Builds a titled detail-view section whose title row carries a pop-out button. */
	private JPanel buildDetailSectionWithPopout(String title, Runnable onPopout, Component... contents)
	{
		JPanel wrapper = newSectionWrapper();
		wrapper.add(buildDetailSectionTitleRow(title, onPopout));
		for (Component c : contents)
			wrapper.add(c);

		return wrapper;
	}

	/** @return an empty vertical wrapper panel used to stack a detail section's rows. */
	private JPanel newSectionWrapper()
	{
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
		wrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
		return wrapper;
	}

	/** Builds a section title row with a pop-out button wired to the given action. */
	private JComponent buildDetailSectionTitleRow(String title, Runnable onPopout)
	{
		return buildDetailSectionTitleRow(title, buildPopoutButton(onPopout));
	}

	/**
	 * Builds a divider-topped section title row with the title centred between a strut
	 * matching the pop-out button's width (so the title stays optically centred) and the
	 * button itself.
	 */
	private JComponent buildDetailSectionTitleRow(String title, JButton popBtn)
	{
		JPanel row = new JPanel(new BorderLayout())
		{
			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
			}
		};
		row.setBackground(ColorScheme.DARK_GRAY_COLOR);
		row.setAlignmentX(Component.LEFT_ALIGNMENT);

		row.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
						new EmptyBorder(10, 0, 0, 0),
						new MatteBorder(1, 0, 0, 0, StockpileColors.DIVIDER)),
				new EmptyBorder(4, 0, 2, 0)));

		JLabel label = new JLabel(title, SwingConstants.CENTER);
		label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		label.setFont(boldFont());

		row.add(Box.createHorizontalStrut(popBtn.getPreferredSize().width), BorderLayout.WEST);
		row.add(label, BorderLayout.CENTER);
		row.add(popBtn, BorderLayout.EAST);
		return row;
	}

	/** Reorders and shows/hides the detail sections to match the configured slot assignments. */
	private void applyDetailSectionLayout()
	{
		if (detailSectionsHost == null)
			return;

		JPanel[] sections = {
				itemValuesSection, ccvSection, marketInfoSection, priceOverviewSection,
				priceGraphSection, volumeGraphSection, alchInfoSection, notificationsSection,
				acquisitionsSection, linksSection
		};
		SectionSlot[] slots = {
				config.showItemValues(), config.showCollectionValues(), config.showMarketInfo(),
				config.showPriceOverview(), config.showPriceGraph(), config.showVolumeGraph(),
				config.showAlchInfo(), config.showNotifications(), config.showItemLog(),
				config.showLinks()
		};

		StringBuilder sig = new StringBuilder();
		for (SectionSlot slot : slots)
			sig.append(slot.ordinal()).append(',');

		String signature = sig.toString();
		if (signature.equals(appliedSectionLayout))
			return;

		appliedSectionLayout = signature;

		if (viewLayout == Layout.DASHBOARD)
		{
			dashboardLeftColumn.removeAll();
			dashboardMiddleColumn.removeAll();
			dashboardRightColumn.removeAll();
			fillDashboardColumn(dashboardLeftColumn, DASHBOARD_LEFT, sections, slots);
			fillDashboardColumn(dashboardMiddleColumn, DASHBOARD_MIDDLE, sections, slots);
			fillDashboardColumn(dashboardRightColumn, DASHBOARD_RIGHT, sections, slots);
		}
		else
		{
			List<Integer> order = new ArrayList<>();
			for (int i = 0; i < sections.length; i++)
			{
				if (!slots[i].isNone())
					order.add(i);
			}

			order.sort(Comparator.comparingInt(i -> slots[i].ordinal()));

			detailSectionsHost.removeAll();
			for (int i : order)
			{
				sections[i].setVisible(true);
				detailSectionsHost.add(sections[i]);
			}
		}

		detailSectionsHost.revalidate();
		detailSectionsHost.repaint();
	}

	/**
	 * Adds the sections named by {@code indices} (in that fixed order) to a dashboard column, skipping any
	 * the config has hidden. The dashboard uses a fixed three-column arrangement (#109) rather than the
	 * config's sidebar ordering.
	 */
	private void fillDashboardColumn(JPanel column, int[] indices, JPanel[] sections, SectionSlot[] slots)
	{
		for (int i : indices)
		{
			if (slots[i].isNone())
				continue;

			sections[i].setVisible(true);
			column.add(sections[i]);
		}

		column.add(Box.createVerticalGlue());
	}

	/**
	 * A three-column horizontal layout for the dashboard body (#109). It splits the available width by
	 * fixed weights (20% / 20% / 60%) when the window is wide enough, but never shrinks a column below its
	 * content's minimum width &mdash; so a smaller window scales the columns to fit their contents rather
	 * than truncating them with "&hellip;". Each column is given the full body height; a trailing glue in
	 * the column keeps its sections at their natural heights.
	 */
	private static final class WeightedColumnsLayout implements LayoutManager
	{
		private final double[] weights;
		private final int gap;

		/**
		 * @param weights per-column width weights (need not sum to one)
		 * @param gap     horizontal pixels between adjacent columns
		 */
		WeightedColumnsLayout(double[] weights, int gap)
		{
			this.weights = weights;
			this.gap = gap;
		}

		@Override
		public void addLayoutComponent(String name, Component comp)
		{
		}

		@Override
		public void removeLayoutComponent(Component comp)
		{
		}

		@Override
		public Dimension preferredLayoutSize(Container parent)
		{
			return measure(parent, false);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent)
		{
			return measure(parent, true);
		}

		/** @return the summed column widths and tallest column height, using min or preferred sizes. */
		private Dimension measure(Container parent, boolean minimum)
		{
			Insets in = parent.getInsets();
			int n = parent.getComponentCount();
			int width = in.left + in.right + gap * Math.max(0, n - 1);
			int height = 0;
			for (int i = 0; i < n; i++)
			{
				Component c = parent.getComponent(i);
				Dimension d = minimum ? c.getMinimumSize() : c.getPreferredSize();
				width += d.width;
				height = Math.max(height, d.height);
			}

			return new Dimension(width, height + in.top + in.bottom);
		}

		@Override
		public void layoutContainer(Container parent)
		{
			Insets in = parent.getInsets();
			int n = parent.getComponentCount();
			if (n == 0)
				return;

			int avail = Math.max(0, parent.getWidth() - in.left - in.right - gap * (n - 1));
			int[] widths = new int[n];
			boolean[] locked = new boolean[n];
			int remaining = avail;

			boolean changed = true;
			while (changed)
			{
				changed = false;
				double weightSum = unlockedWeight(locked);
				if (weightSum <= 0)
					break;

				for (int i = 0; i < n; i++)
				{
					if (locked[i])
						continue;

					int share = (int) Math.round(remaining * weights[i] / weightSum);
					Component col = parent.getComponent(i);
					int min = col.getMinimumSize().width;
					if (share < min)
					{
						widths[i] = min;
						locked[i] = true;
						remaining -= min;
						changed = true;
						break;
					}
				}
			}

			double weightSum = unlockedWeight(locked);
			for (int i = 0; i < n; i++)
			{
				if (!locked[i])
					widths[i] = weightSum > 0 ? Math.max(0, (int) Math.round(remaining * weights[i] / weightSum)) : 0;
			}

			int x = in.left;
			int h = parent.getHeight() - in.top - in.bottom;
			for (int i = 0; i < n; i++)
			{
				parent.getComponent(i).setBounds(x, in.top, widths[i], h);
				x += widths[i] + gap;
			}
		}

		/** @return the total weight of the columns not yet locked to their minimum width. */
		private double unlockedWeight(boolean[] locked)
		{
			double sum = 0;
			for (int i = 0; i < locked.length; i++)
			{
				if (!locked[i])
					sum += weights[i];
			}

			return sum;
		}
	}

	/**
	 * Builds the sidebar (STACK) detail header: a Back button and Track/pop-out controls on one row,
	 * then the icon-and-title row, then the wrapping description &mdash; all stacked vertically.
	 */
	private void buildStackHeader(JPanel titleTextStack)
	{
		JButton backBtn = new JButton("Back", buildLeftArrowIcon());
		backBtn.setIconTextGap(6);
		backBtn.setVerticalAlignment(SwingConstants.CENTER);
		backBtn.setHorizontalAlignment(SwingConstants.CENTER);
		backBtn.setFocusPainted(false);
		backBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		backBtn.setForeground(Color.WHITE);
		backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		backBtn.addActionListener(e -> host.onBack());

		JPanel headerRow = new JPanel(new BorderLayout(6, 0))
		{
			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
			}
		};
		headerRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
		headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		headerRow.add(backBtn, BorderLayout.WEST);

		JPanel headerControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		headerControls.setBackground(ColorScheme.DARK_GRAY_COLOR);
		headerControls.add(detailTrackBtn);

		detailPopOutBtn = StockpilePanel.buildIconButton(
				StockpilePanel.dashboardIcon(ColorScheme.LIGHT_GRAY_COLOR),
				"Open in Dashboard View", () -> host.popOut(boundItemId));
		headerControls.add(detailPopOutBtn);

		headerRow.add(headerControls, BorderLayout.EAST);

		JPanel titleRow = new JPanel(new BorderLayout(8, 0))
		{
			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
			}
		};
		titleRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
		titleRow.setBorder(new EmptyBorder(16, 0, 0, 0));
		titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		titleRow.add(detailIconLabel, BorderLayout.WEST);
		titleRow.add(titleTextStack, BorderLayout.CENTER);

		topStack.add(headerRow);
		topStack.add(titleRow);
		topStack.add(detailDescriptionArea);
	}

	/**
	 * Builds the dashboard (pop-out window) header as a single toolbar row (#109): the item identity
	 * &mdash; icon, name, quantity and description &mdash; floats left, a width-capped search bar to
	 * switch the shown item sits centre, and equal-width Wiki, Live Prices and Track/Untrack controls
	 * float right. Everything is vertically centred; results are drawn in a floating popup that overlays
	 * the dashboard rather than displacing it.
	 */
	private void buildDashboardToolbar(JPanel titleTextStack)
	{
		detailDescriptionArea.setMaximumSize(new Dimension(340, Integer.MAX_VALUE));
		titleTextStack.add(detailDescriptionArea);

		JPanel identity = new JPanel(new BorderLayout(8, 0));
		identity.setBackground(ColorScheme.DARK_GRAY_COLOR);
		identity.add(detailIconLabel, BorderLayout.WEST);
		identity.add(titleTextStack, BorderLayout.CENTER);

		dashboardWikiBtn = buildLinkButton("Wiki", "Open the OSRS Wiki page for this item", this::openWikiLink);
		dashboardPricesBtn = buildLinkButton("Live Prices", "Open the live prices page for this item",
				this::openPricesLink);
		styleToolbarButton(dashboardWikiBtn);
		styleToolbarButton(dashboardPricesBtn);
		styleToolbarButton(detailTrackBtn);

		dashboardRightControls = new JPanel(new GridLayout(1, 3, 6, 0));
		dashboardRightControls.setBackground(ColorScheme.DARK_GRAY_COLOR);
		JPanel rightControls = dashboardRightControls;
		applyDashboardLinks(false);

		JPanel toolbar = new JPanel(new GridBagLayout())
		{
			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
			}
		};
		toolbar.setBackground(ColorScheme.DARK_GRAY_COLOR);
		toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
		toolbar.setBorder(new EmptyBorder(0, 0, 4, 0));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		toolbar.add(identity, gbc);
		gbc.gridx = 1;
		toolbar.add(buildDashboardPad(), gbc);
		gbc.gridx = 2;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;
		toolbar.add(buildDashboardSearch(), gbc);
		gbc.gridx = 3;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		toolbar.add(buildDashboardPad(), gbc);
		gbc.gridx = 4;
		gbc.anchor = GridBagConstraints.EAST;
		toolbar.add(rightControls, gbc);

		topStack.add(toolbar);
	}

	/**
	 * Lays out the dashboard toolbar's right-hand controls (#109). With an item shown, Wiki, Live Prices and
	 * Track share three equal cells; in dashboard-home mode the Track button is dropped and Wiki + Live Prices
	 * expand to fill the freed space, staying right-aligned.
	 */
	private void applyDashboardLinks(boolean home)
	{
		if (dashboardRightControls == null)
			return;

		dashboardRightControls.removeAll();
		dashboardRightControls.setLayout(new GridLayout(1, home ? 2 : 3, 6, 0));
		dashboardRightControls.add(dashboardWikiBtn);
		dashboardRightControls.add(dashboardPricesBtn);
		if (!home)
			dashboardRightControls.add(detailTrackBtn);

		dashboardRightControls.revalidate();
		dashboardRightControls.repaint();
	}

	/**
	 * @return a flexible horizontal spacer whose width tracks 10% of the view, used to pad the dashboard
	 *         toolbar's centre search bar away from the identity block and the right-hand controls.
	 */
	private Component buildDashboardPad()
	{
		JPanel pad = new JPanel()
		{
			@Override
			public Dimension getPreferredSize()
			{
				int viewWidth = DetailView.this.getWidth();
				return new Dimension(viewWidth > 0 ? (int) (viewWidth * 0.10) : 24, 0);
			}
		};
		pad.setOpaque(false);
		return pad;
	}

	/** @return the base body font: monospace across the dashboard (#109), RuneScape small in the sidebar. */
	private Font smallFont()
	{
		return viewLayout == Layout.DASHBOARD ? DASHBOARD_FONT : FontManager.getRunescapeSmallFont();
	}

	/** @return the emphasis font: bold monospace across the dashboard, RuneScape bold in the sidebar. */
	private Font boldFont()
	{
		return viewLayout == Layout.DASHBOARD
				? DASHBOARD_FONT.deriveFont(Font.BOLD)
				: FontManager.getRunescapeBoldFont();
	}

	/** Styles a dashboard toolbar button: small font, a subtle border, and padding matching the search bar. */
	private void styleToolbarButton(JButton button)
	{
		button.setFont(smallFont());
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(ColorScheme.MEDIUM_GRAY_COLOR),
				new EmptyBorder(3, 11, 3, 11)));
	}

	/**
	 * Builds the dashboard toolbar's centre search control: a text field that fills the middle of the
	 * toolbar, shows a faint "Search..." placeholder when empty, filters OSRS items as you type, and lists
	 * matches in a floating popup whose rows switch the window to the chosen item.
	 */
	private JTextField buildDashboardSearch()
	{
		dashboardSearchPopup = new JPopupMenu();
		dashboardSearchPopup.setFocusable(false);
		dashboardSearchPopup.setBorder(BorderFactory.createLineBorder(ColorScheme.LIGHT_GRAY_COLOR));
		dashboardSearchPopup.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		dashboardSearchField = new JTextField()
		{
			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				if (!getText().isEmpty())
					return;

				Graphics2D g2 = (Graphics2D) g.create();
				g2.setColor(SEARCH_PLACEHOLDER_COLOR);
				g2.setFont(getFont().deriveFont(Font.ITALIC));
				var fm = g2.getFontMetrics();
				int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
				g2.drawString("Search...", getInsets().left, ty);
				g2.dispose();
			}
		};
		dashboardSearchField.setToolTipText("Search for an item to show in this window");
		dashboardSearchField.setFont(smallFont());
		dashboardSearchField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		dashboardSearchField.setForeground(Color.WHITE);
		dashboardSearchField.setCaretColor(Color.WHITE);
		dashboardSearchField.setBorder(new EmptyBorder(4, 8, 4, 8));
		dashboardSearchField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				onDashboardSearch(dashboardSearchField.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				onDashboardSearch(dashboardSearchField.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				onDashboardSearch(dashboardSearchField.getText());
			}
		});
		dashboardSearchField.addActionListener(e ->
		{
			if (firstSearchResultId >= 0)
				selectDashboardSearch(firstSearchResultId);
		});

		return dashboardSearchField;
	}

	/** Filters the floating dashboard search popup to OSRS items matching {@code query} (min two characters). */
	private void onDashboardSearch(String query)
	{
		if (dashboardSearchPopup == null)
			return;

		firstSearchResultId = -1;
		dashboardSearchPopup.setVisible(false);
		dashboardSearchPopup.removeAll();

		if (query == null || query.trim().length() < 2)
			return;

		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		List<ItemPrice> results = host.itemManager().search(query);
		int shown = 0;
		for (ItemPrice item : results)
		{
			if (shown >= 25)
				break;

			if (item.getId() == boundItemId)
				continue;

			if (firstSearchResultId < 0)
				firstSearchResultId = item.getId();

			content.add(buildDashboardSearchRow(item.getId(), item.getName()));
			shown++;
		}

		if (shown == 0)
			return;

		JScrollPane scroll = new JScrollPane(content,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		dashboardSearchPopup.add(scroll);

		int visibleRows = Math.min(shown, 5);
		dashboardSearchPopup.setPopupSize(dashboardSearchField.getWidth(), visibleRows * 26 + 2);
		dashboardSearchPopup.show(dashboardSearchField, 0, dashboardSearchField.getHeight());
		dashboardSearchField.requestFocusInWindow();
	}

	/** Builds one clickable dashboard search-result row that switches the window to {@code itemId} when picked. */
	private JPanel buildDashboardSearchRow(int itemId, String itemName)
	{
		JPanel row = new JPanel(new BorderLayout());
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setBorder(new EmptyBorder(4, 6, 4, 6));
		row.setPreferredSize(new Dimension(10, 26));
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JLabel nameLabel = new JLabel();
		nameLabel.setForeground(Color.WHITE);
		nameLabel.setFont(smallFont());
		EllipsisText.set(nameLabel, itemName);
		row.add(nameLabel, BorderLayout.CENTER);

		MouseAdapter rowMouse = new MouseAdapter()
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

			@Override
			public void mouseClicked(MouseEvent e)
			{
				selectDashboardSearch(itemId);
			}
		};
		row.addMouseListener(rowMouse);
		nameLabel.addMouseListener(rowMouse);

		return row;
	}

	/** Clears the dashboard search field and asks the host to switch this window to {@code itemId}. */
	private void selectDashboardSearch(int itemId)
	{
		if (dashboardSearchField != null)
			dashboardSearchField.setText("");

		firstSearchResultId = -1;
		dashboardSearchPopup.setVisible(false);
		dashboardSearchPopup.removeAll();
		host.switchDetailItem(itemId);
	}

	/**
	 * Fixes a dashboard chart's height to {@code height} so it reads at a pop-out-like size rather than
	 * the shrunk sidebar height, while leaving its width free to stretch to the column (#109).
	 */
	private void sizeDashboardGraph(PriceGraphPanel graph, int height)
	{
		graph.setPreferredSize(new Dimension(10, height));
		graph.setMinimumSize(new Dimension(10, height));
		graph.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
	}

	/**
	 * Builds one top-anchored dashboard column. Its content minimum width is honoured by
	 * {@link WeightedColumnsLayout} so the column is never squeezed narrow enough to truncate its
	 * contents (#109).
	 */
	private JPanel buildDashboardColumn()
	{
		JPanel column = new JPanel();
		column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
		column.setBackground(ColorScheme.DARK_GRAY_COLOR);
		column.setAlignmentX(Component.LEFT_ALIGNMENT);
		return column;
	}

	/** Rebuilds the price overview grid to match the configured preset of time-window rows. */
	private void rebuildOverviewGrid()
	{
		if (overviewGrid == null)
			return;

		Set<TimeWindow> desired = config.priceOverviewRows().getWindows();
		if (desired.equals(appliedOverviewRows))
			return;

		appliedOverviewRows = desired;
		populateOverviewGrid(desired);
	}

	/**
	 * Runs a refresh-in-place of the open detail card, keeping the enclosing scroll
	 * pane's vertical position. The scroll yank this guards against was the
	 * description area's caret scrolling itself into view on {@code setText} — muzzled
	 * at the source with {@link DefaultCaret#NEVER_UPDATE} — so this is defensive:
	 * layout is forced synchronously and the position re-asserted in the same EDT
	 * event, with a queued re-assert for layout that settles late (async item images).
	 * Opening a different item still starts at the top, since {@link #show(int)}
	 * bypasses this.
	 */
	private void preserveDetailScroll(Runnable refresh)
	{
		JScrollPane scroll = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, this);
		if (scroll == null)
		{
			refresh.run();
			return;
		}

		final JScrollBar bar = scroll.getVerticalScrollBar();
		final int value = bar.getValue();
		refresh.run();
		scroll.validate();
		bar.setValue(value);
		SwingUtilities.invokeLater(() -> bar.setValue(value));
	}

	/** Scrolls the acquisitions log to its newest (bottom) entry once layout has settled. */
	private void scrollAcquisitionsToBottom()
	{
		if (acquisitionsScroll == null)
			return;

		SwingUtilities.invokeLater(() ->
		{
			JScrollBar bar = acquisitionsScroll.getVerticalScrollBar();
			bar.setValue(bar.getMaximum());
		});
	}

	/**
	 * Builds the stacked current-values block (high/low/avg plus a fourth metric row),
	 * colouring each label by metric and appending a divider-topped profit row when a
	 * profit label is supplied.
	 */
	private JPanel buildCurrentValuesBlock(JLabel high, JLabel low, JLabel avg, JLabel fourth, JLabel profit)
	{
		JPanel block = new JPanel()
		{
			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
			}
		};
		block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
		block.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		block.setBorder(new EmptyBorder(6, 8, 6, 8));
		block.setAlignmentX(Component.LEFT_ALIGNMENT);

		high.setForeground(StockpileColors.HIGH);
		low.setForeground(StockpileColors.LOW);
		avg.setForeground(StockpileColors.AVG);
		fourth.setForeground(COLOR_VOLUME);
		for (JLabel l : new JLabel[]{high, low, avg, fourth})
		{
			l.setFont(smallFont());
			JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
			row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			row.add(l);
			block.add(row);
		}

		if (profit != null)
		{
			profit.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
			profit.setFont(smallFont());
			JPanel profitRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
			profitRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			profitRow.add(profit);
			profitRow.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(
							new EmptyBorder(4, 0, 0, 0),
							new MatteBorder(1, 0, 0, 0, StockpileColors.DIVIDER)),
					new EmptyBorder(4, 0, 0, 0)));
			block.add(profitRow);
		}

		return block;
	}

	/** Builds (and remembers) the sidebar overview grid. */
	private JPanel buildOverviewGrid()
	{
		overviewGrid = createOverviewGrid(overviewLabels, overviewWindowLabels, 2);
		return overviewGrid;
	}

	/**
	 * Creates an overview grid panel that custom-paints its own dividers: a vertical rule
	 * after the window-label column and a horizontal rule between consecutive rows, both
	 * derived from the live label positions so they track layout changes.
	 */
	private JPanel createOverviewGrid(Map<TimeWindow, JLabel[]> labels, List<JLabel> windowLabels, int sepGap)
	{
		JPanel grid = new JPanel(new GridBagLayout())
		{
			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
			}

			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				JLabel[] firstRow = labels.get(OVERVIEW_WINDOWS[0]);
				if (firstRow == null || firstRow[0] == null)
					return;

				int vx = firstRow[0].getX() - 3;
				if (!windowLabels.isEmpty())
				{
					JLabel ref = windowLabels.get(0);
					vx = ref.getX() + ref.getWidth() + sepGap;
				}

				g.setColor(StockpileColors.DIVIDER);
				g.drawLine(vx, 4, vx, getHeight() - 4);

				g.setColor(OVERVIEW_ROW_DIVIDER);
				JLabel prev = null;
				for (TimeWindow w : OVERVIEW_WINDOWS)
				{
					JLabel[] cells = labels.get(w);
					if (cells == null || cells[0] == null)
						continue;

					JLabel cur = cells[0];
					if (prev != null)
					{
						int y = (prev.getY() + prev.getHeight() + cur.getY()) / 2;
						g.drawLine(2, y, getWidth() - 2, y);
					}

					prev = cur;
				}
			}
		};
		grid.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		grid.setBorder(new EmptyBorder(6, 3, 2, 3));
		grid.setAlignmentX(Component.LEFT_ALIGNMENT);
		return grid;
	}

	/** (Re)creates the overview grid panels (sidebar and pop-out) for the given set of time-window rows. */
	private void populateOverviewGrid(Set<TimeWindow> rows)
	{
		fillOverviewGrid(overviewGrid, overviewLabels, overviewWindowLabels, rows,
				smallFont(), false);
	}

	/** Lays out the overview grid's header and one row of price/volume labels per selected time window. */
	private void fillOverviewGrid(JPanel grid, Map<TimeWindow, JLabel[]> labels,
			List<JLabel> windowLabels, Set<TimeWindow> rows, Font font, boolean expanded)
	{
		grid.removeAll();
		labels.clear();
		windowLabels.clear();

		int vPad = expanded ? 7 : 2;
		int hPad = expanded ? 9 : 1;
		int hPadSep = expanded ? 16 : 3;
		grid.setBorder(new EmptyBorder(expanded ? 10 : 6, expanded ? 14 : 3,
				expanded ? 10 : 2, expanded ? 14 : 3));

		int colAlign = expanded ? SwingConstants.RIGHT : SwingConstants.CENTER;

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		String[] headers = {"", "High", "Low", "Avg", "Δ%", "Vol"};
		Color[] headerColors = {
				ColorScheme.LIGHT_GRAY_COLOR, StockpileColors.HIGH, StockpileColors.LOW, StockpileColors.AVG,
				ColorScheme.LIGHT_GRAY_COLOR, COLOR_VOLUME};
		for (int i = 0; i < headers.length; i++)
		{
			JLabel h = new JLabel(headers[i], i == 0 ? SwingConstants.CENTER : colAlign);
			h.setForeground(headerColors[i]);
			h.setFont(font);
			c.gridx = i;
			c.gridy = 0;
			c.weightx = i == 0 ? 0 : 1;

			c.insets = new Insets(vPad, i == 1 ? hPadSep : hPad, vPad, hPad);
			grid.add(h, c);
		}

		JPanel headerDivider = new JPanel();
		headerDivider.setBackground(StockpileColors.DIVIDER);
		headerDivider.setPreferredSize(new Dimension(0, 1));
		headerDivider.setMinimumSize(new Dimension(0, 1));
		GridBagConstraints dc = new GridBagConstraints();
		dc.gridx = 0;
		dc.gridy = 1;
		dc.gridwidth = headers.length;
		dc.weightx = 1;
		dc.fill = GridBagConstraints.HORIZONTAL;
		dc.insets = new Insets(1, hPad, 2, hPad);
		grid.add(headerDivider, dc);

		int y = 2;
		Color[] cellColors = {
				StockpileColors.HIGH, StockpileColors.LOW, StockpileColors.AVG, COLOR_VOLUME, COLOR_VOLUME
		};
		for (TimeWindow w : OVERVIEW_WINDOWS)
		{
			if (!rows.contains(w))
				continue;

			String wLabel = expanded ? fullWindowLabel(w) : (w == TimeWindow.LIVE ? "5m" : w.toString());
			JLabel windowLbl = new JLabel(wLabel, SwingConstants.RIGHT);
			windowLbl.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
			windowLbl.setFont(font);
			c.gridx = 0;
			c.gridy = y;
			c.weightx = 0;
			c.anchor = GridBagConstraints.EAST;
			c.insets = new Insets(vPad, hPad, vPad, hPad);
			grid.add(windowLbl, c);
			windowLabels.add(windowLbl);

			JLabel[] cells = new JLabel[5];
			for (int i = 0; i < 5; i++)
			{
				cells[i] = new JLabel("—", colAlign);
				cells[i].setForeground(cellColors[i]);
				cells[i].setFont(font);
				c.gridx = i + 1;
				c.gridy = y;
				c.weightx = 1;
				c.anchor = GridBagConstraints.CENTER;
				c.insets = new Insets(vPad, i == 0 ? hPadSep : hPad, vPad, hPad);
				grid.add(cells[i], c);
			}

			labels.put(w, cells);
			y++;
		}

		grid.revalidate();
		grid.repaint();
	}

	/** @return the long-form window name used by the pop-out overview grid. */
	private static String fullWindowLabel(TimeWindow w)
	{
		return w.getLongLabel();
	}

	/** Fills the overview grid's cells with an item's per-window high/low/avg/volume/Δ% values. */
	private void populateOverviewLabels(Map<TimeWindow, JLabel[]> labels, TrackedItem item, boolean full)
	{
		for (TimeWindow w : OVERVIEW_WINDOWS)
		{
			JLabel[] cells = labels.get(w);
			if (cells == null)
				continue;

			if (w == TimeWindow.LIVE)
			{
				List<WikiRealtimePriceClient.PricePoint> s5 = item.getSeries5m();
				WikiRealtimePriceClient.PricePoint last = s5.isEmpty() ? null : s5.get(s5.size() - 1);
				long high = last == null ? 0 : last.getAvgHighPrice();
				long low = last == null ? 0 : last.getAvgLowPrice();
				long avg = last == null ? 0 : overviewMidpoint(last);
				setPriceCell(cells[0], high, StockpileColors.HIGH, "High", StockpileColors.TINT_HIGH, full);
				setPriceCell(cells[1], low, StockpileColors.LOW, "Low", StockpileColors.TINT_LOW, full);
				setPriceCell(cells[2], avg, StockpileColors.AVG, "Avg", StockpileColors.TINT_AVG, full);
				setOverviewPlaceholder(cells[3]);
				if (last == null)
					setOverviewPlaceholder(cells[4]);
				else
					installVolumeValue(cells[4], last.getHighPriceVolume() + last.getLowPriceVolume(), full);

				continue;
			}

			PriceStats s = item.getWindowStats().get(w);
			setPriceCell(cells[0], s == null ? 0 : s.getHigh(), StockpileColors.HIGH, "High",
					StockpileColors.TINT_HIGH, full);
			setPriceCell(cells[1], s == null ? 0 : s.getLow(), StockpileColors.LOW, "Low",
					StockpileColors.TINT_LOW, full);
			setPriceCell(cells[2], s == null ? 0 : s.getAvg(), StockpileColors.AVG, "Avg",
					StockpileColors.TINT_AVG, full);
			applyDeltaPct(cells[3], item, w);

			if (s == null)
				setOverviewPlaceholder(cells[4]);
			else
				installVolumeValue(cells[4], s.getVolume(), full);
		}
	}

	/** Pushes fresh data for {@code item} into every open pop-out window. */
	private void refreshPopouts(TrackedItem item)
	{
		for (PopoutHandle h : new ArrayList<>(openPopouts))
			h.refresher.accept(item);
	}

	/** Disposes all open pop-out windows (e.g. when leaving the detail view). */
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

		TrackedItem current = host.trackedItem(boundItemId);
		if (current == null && previewItem != null && previewItem.getItemId() == boundItemId)
			current = previewItem;

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

	/** Opens an expanded chart pop-out mirroring (and kept in sync with) the in-panel graph. */
	private void openGraphPopout(String title, PriceGraphPanel.Mode mode, PriceGraphPanel source)
	{
		PriceGraphPanel graph = new PriceGraphPanel(mode, true);
		graph.setActiveWindow(source.getActiveWindow());
		graph.setPreferredSize(new Dimension(640, mode == PriceGraphPanel.Mode.PRICE ? 460 : 360));

		Runnable onClose = null;
		if (mode == PriceGraphPanel.Mode.PRICE)
		{
			graph.setSmooth(graphSmooth);
			graph.setSmoothListener(b ->
			{
				graphSmooth = b;
				source.setSmooth(b);
			});
			graph.setLineSet(graphLineSet);
			graph.setLineSetListener(set ->
			{
				graphLineSet = set;
				source.setLineSet(set);
			});
			pricePopoutGraph = graph;
			onClose = () ->
			{
				if (pricePopoutGraph == graph)
					pricePopoutGraph = null;
			};
		}

		Consumer<TrackedItem> refresher = it -> graph.setData(
				it.getSeries5m(), it.getSeries1h(), it.getSeries6h(), it.getSeries24h(), it.getAvgPrice());
		showPopout(title, graph, refresher, onClose);
	}

	/** Opens the price overview grid in a standalone pop-out window. */
	private void openOverviewPopout()
	{
		Map<TimeWindow, JLabel[]> labels = new EnumMap<>(TimeWindow.class);
		List<JLabel> windowLabels = new ArrayList<>();
		JPanel grid = createOverviewGrid(labels, windowLabels, 12);

		Font big = new Font(Font.MONOSPACED, Font.PLAIN, 18);

		fillOverviewGrid(grid, labels, windowLabels, OverviewPreset.DETAILED.getWindows(), big, true);

		JPanel top = new JPanel(new BorderLayout());
		top.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		top.add(grid, BorderLayout.NORTH);
		JScrollPane scroll = new JScrollPane(top);
		scroll.setBorder(null);
		scroll.getViewport().setBackground(ColorScheme.DARKER_GRAY_COLOR);

		Consumer<TrackedItem> refresher = it -> populateOverviewLabels(labels, it, true);
		showPopout("Price Overview", scroll, refresher, null);
	}

	/** Hides the acquisitions pop-out button while its pop-out window is already open. */
	private void updateAcqPopoutButton()
	{
		if (acqPopoutButton == null)
			return;

		acqPopoutButton.setVisible(acqPopoutModel == null);
	}

	/** Opens the editable acquisitions (collection log) table in a standalone pop-out window. */
	private void openCollectionLogPopout()
	{
		if (acqPopoutModel != null)
			return;

		acqPopoutModel = new AcquisitionsTableModel(config, onAcquisitionsEdited, () -> boundItemId, true);
		acqPopoutTable = new JTable(acqPopoutModel);
		final JTable table = acqPopoutTable;
		final AcquisitionsTableModel model = acqPopoutModel;
		table.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		table.setForeground(Color.WHITE);
		table.setGridColor(StockpileColors.TABLE_GRID);
		table.setFillsViewportHeight(true);
		table.getTableHeader().setBackground(ColorScheme.DARKER_GRAY_COLOR);
		table.getTableHeader().setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		table.getTableHeader().setReorderingAllowed(false);
		model.setItem(host.trackedItem(boundItemId));
		applyAcqRenderers(table, model, true);

		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1
						&& table.rowAtPoint(e.getPoint()) < 0)
					acqAddRow(table, model);
			}
		});

		JScrollPane scroll = new JScrollPane(table);
		scroll.getViewport().setBackground(ColorScheme.DARKER_GRAY_COLOR);
		scroll.setBorder(BorderFactory.createLineBorder(StockpileColors.TABLE_GRID));
		scroll.setPreferredSize(new Dimension(560, 380));
		acqPopoutScroll = scroll;

		JButton addBtn = acqTextButton("+ Add", Color.WHITE);
		addBtn.addActionListener(e -> acqAddRow(table, model));

		JButton removeBtn = acqTextButton("− Remove", Color.WHITE);
		removeBtn.setEnabled(false);
		removeBtn.addActionListener(e -> acqRemoveSelected(table, model));
		table.getSelectionModel().addListSelectionListener(e ->
				removeBtn.setEnabled(table.getSelectedRowCount() > 0));
		table.getInputMap(JComponent.WHEN_FOCUSED)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteSelectedRows");
		table.getActionMap().put("deleteSelectedRows", new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				acqRemoveSelected(table, model);
			}
		});

		JButton cleanBtn = new JButton(buildBrushIcon());
		cleanBtn.setToolTipText("Remove all rows with quantity 0");
		cleanBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		cleanBtn.setForeground(Color.WHITE);
		cleanBtn.setFocusPainted(false);
		cleanBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		cleanBtn.setMargin(new Insets(2, 4, 2, 4));
		cleanBtn.addActionListener(e -> acqClean(model));

		JButton clearBtn = acqTextButton("Clear", StockpileColors.LOW);
		clearBtn.setToolTipText("Remove every row from the collection log");
		clearBtn.addActionListener(e -> acqClear());

		JButton[] btns = {addBtn, removeBtn, cleanBtn, clearBtn};
		int btnHeight = 0;
		for (JButton b : btns)
			btnHeight = Math.max(btnHeight, b.getPreferredSize().height);

		for (JButton b : btns)
			b.setPreferredSize(new Dimension(b.getPreferredSize().width, btnHeight));

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		buttons.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		buttons.setBorder(new EmptyBorder(4, 0, 0, 0));
		for (JButton b : btns)
			buttons.add(b);

		JPanel content = new JPanel(new BorderLayout(0, 6));
		content.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		content.add(scroll, BorderLayout.CENTER);
		content.add(buttons, BorderLayout.SOUTH);

		updateAcqPopoutButton();

		showPopout("Item Collection Log", content, it -> { }, () ->
		{
			acqPopoutModel = null;
			acqPopoutTable = null;
			acqPopoutScroll = null;
			updateAcqPopoutButton();
		});
	}

	/** Builds a small flat text button used by the acquisitions pop-out toolbar. */
	private JButton acqTextButton(String text, Color fg)
	{
		JButton b = new JButton(text);
		b.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		b.setForeground(fg);
		b.setFocusPainted(false);
		b.setFont(smallFont());
		b.setMargin(new Insets(2, 5, 2, 5));
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return b;
	}

	/** Appends a new empty acquisition row to the table and scrolls it into view. */
	private void acqAddRow(JTable table, AcquisitionsTableModel model)
	{
		TrackedItem t = host.trackedItem(boundItemId);
		if (t == null)
			return;

		long price = t.getAvgPrice() > 0 ? t.getAvgPrice() : 0;
		t.getAcquisitions().add(new AcquisitionRecord(0, price, null, AcquisitionSource.MANUAL));
		model.fireTableDataChanged();
		table.revalidate();
		onAcquisitionsEdited.accept(boundItemId);
		JScrollPane sp = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, table);
		if (sp != null)
		{
			SwingUtilities.invokeLater(() ->
			{
				JScrollBar bar = sp.getVerticalScrollBar();
				bar.setValue(bar.getMaximum());
			});
		}

		int newRow = model.getRowCount() - 1;
		if (newRow >= 0 && table.editCellAt(newRow, 0))
		{
			table.changeSelection(newRow, 0, false, false);
			Component editor = table.getEditorComponent();
			if (editor != null)
				editor.requestFocusInWindow();
		}
	}

	/** Removes the selected acquisition rows and commits the change. */
	private void acqRemoveSelected(JTable table, AcquisitionsTableModel model)
	{
		TrackedItem t = host.trackedItem(boundItemId);
		if (t == null)
			return;

		if (table.isEditing())

			table.getCellEditor().stopCellEditing();

		int[] selected = table.getSelectedRows();
		if (selected.length == 0)
			return;

		List<AcquisitionRecord> records = t.getAcquisitions();
		Arrays.sort(selected);
		for (int i = selected.length - 1; i >= 0; i--)
		{
			if (selected[i] >= 0 && selected[i] < records.size())
				records.remove(selected[i]);
		}

		model.fireTableDataChanged();
		table.revalidate();
		onAcquisitionsEdited.accept(boundItemId);
	}

	/** Consolidates the acquisitions log, merging like rows and dropping empty ones. */
	private void acqClean(AcquisitionsTableModel model)
	{
		TrackedItem t = host.trackedItem(boundItemId);
		if (t == null)
			return;

		if (t.getAcquisitions().removeIf(r -> r.getQuantity() == 0))
		{
			model.fireTableDataChanged();
			onAcquisitionsEdited.accept(boundItemId);
		}
	}

	/** Clears all acquisitions for the current item after confirmation, via the plugin callback. */
	private void acqClear()
	{
		TrackedItem t = host.trackedItem(boundItemId);
		if (t == null || t.getAcquisitions().isEmpty())
			return;

		int choice = JOptionPane.showConfirmDialog(
				DetailView.this,
				"Clear the entire collection log for this item?",
				"Clear Collection Log",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
		if (choice != JOptionPane.YES_OPTION)
			return;

		if (onClearAcquisitions != null)

			onClearAcquisitions.accept(boundItemId);
	}

	/** Paints the small box-with-arrow "open in new window" icon used by pop-out buttons. */
	private Icon buildPopoutIcon()
	{
		int s = 11;
		BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(ColorScheme.LIGHT_GRAY_COLOR);
		g.setStroke(new BasicStroke(1f));

		g.drawRect(0, 4, 6, 6);
		g.drawLine(4, 6, s - 1, 0);
		g.drawLine(s - 5, 0, s - 1, 0);
		g.drawLine(s - 1, 0, s - 1, 4);
		g.dispose();
		return new ImageIcon(img);
	}

	/** Builds a borderless pop-out button that runs the given action when clicked. */
	private JButton buildPopoutButton(Runnable onClick)
	{
		return StockpilePanel.buildIconButton(buildPopoutIcon(), "Open in a larger window", onClick);
	}

	/** Wires the per-column cell editors/renderers on the notifications table to the current metric's input type. */
	private void applyNotificationRenderers()
	{
		Font f = smallFont();
		NotifCellRenderer renderer = new NotifCellRenderer();

		JComboBox<NotificationMetric> metricCombo = new JComboBox<>(NotificationMetric.values());
		metricCombo.setFont(f);
		metricCombo.setRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus)
			{
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof NotificationMetric)
					setText(((NotificationMetric) value).getDisplayName());

				setFont(f);
				return this;
			}
		});
		JComboBox<TimeWindow> timeCombo = new JComboBox<>(OVERVIEW_WINDOWS);
		timeCombo.setFont(f);
		JComboBox<NotificationOperation> opCombo = new JComboBox<>(NotificationOperation.values());
		opCombo.setFont(f);

		TableColumnModel columns = notificationsTable.getColumnModel();
		columns.getColumn(0).setCellEditor(new DefaultCellEditor(metricCombo));
		columns.getColumn(1).setCellEditor(new DefaultCellEditor(timeCombo));
		columns.getColumn(2).setCellEditor(new DefaultCellEditor(opCombo));
		columns.getColumn(3).setCellEditor(new NotificationValueEditor(host::trackedItem, () -> boundItemId));

		for (int i = 0; i < notificationsTable.getColumnCount(); i++)
		{
			if (notificationsTable.getColumnClass(i) != Boolean.class)
				columns.getColumn(i).setCellRenderer(renderer);
		}

		columns.getColumn(notificationsTable.getColumnCount() - 1).setMaxWidth(28);

		TableCellRenderer hr = notificationsTable.getTableHeader().getDefaultRenderer();
		if (hr instanceof DefaultTableCellRenderer)
			((DefaultTableCellRenderer) hr).setHorizontalAlignment(SwingConstants.CENTER);
	}

	/** Builds the market-info section (buy limit, GE value, volatility, liquidity, 30-day range, etc.). */
	private JPanel buildMarketInfoBlock()
	{
		JPanel block = new JPanel()
		{
			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
			}
		};
		block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
		block.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		block.setBorder(new EmptyBorder(6, 8, 6, 8));
		block.setAlignmentX(Component.LEFT_ALIGNMENT);

		miBuyLimit.setHorizontalAlignment(SwingConstants.CENTER);
		miGeTax.setHorizontalAlignment(SwingConstants.CENTER);
		miLastBought.setHorizontalAlignment(SwingConstants.CENTER);
		miLastSold.setHorizontalAlignment(SwingConstants.CENTER);
		miVolatility.setHorizontalAlignment(SwingConstants.CENTER);
		miLiquidity.setHorizontalAlignment(SwingConstants.CENTER);

		block.add(buildMarketInfoPair("Buy Limit", miBuyLimit, "GE Tax", miGeTax));
		block.add(Box.createVerticalStrut(6));
		block.add(buildMarketInfoPair("Last Bought", miLastBought, "Last Sold", miLastSold));
		block.add(Box.createVerticalStrut(6));
		block.add(buildMarketInfoPair("Volatility", miVolatility, "Liquidity", miLiquidity));

		block.add(Box.createVerticalStrut(8));
		JPanel rangeSep = new JPanel();
		rangeSep.setBackground(OVERVIEW_ROW_DIVIDER);
		rangeSep.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangeSep.setPreferredSize(new Dimension(0, 1));
		rangeSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		block.add(rangeSep);
		block.add(Box.createVerticalStrut(8));

		JLabel rangeTitle = new JLabel("30 Day Price Range", SwingConstants.CENTER);
		rangeTitle.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		rangeTitle.setFont(smallFont());
		rangeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangeTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, rangeTitle.getPreferredSize().height));
		block.add(rangeTitle);
		block.add(Box.createVerticalStrut(4));

		rangePositionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		rangePositionLabel.setFont(smallFont());
		rangePositionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		rangePositionLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				rangePositionLabel.getPreferredSize().height));
		block.add(rangePositionLabel);
		block.add(Box.createVerticalStrut(3));

		priceRangeBar = new PriceRangeBar();
		block.add(priceRangeBar);

		JPanel pressureSep = new JPanel();
		pressureSep.setBackground(OVERVIEW_ROW_DIVIDER);
		pressureSep.setAlignmentX(Component.LEFT_ALIGNMENT);
		pressureSep.setPreferredSize(new Dimension(0, 1));
		pressureSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		block.add(Box.createVerticalStrut(8));
		block.add(pressureSep);
		block.add(Box.createVerticalStrut(8));

		JLabel pressureTitle = new JLabel("Buy/Sell Pressure", SwingConstants.CENTER);
		pressureTitle.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		pressureTitle.setFont(smallFont());
		pressureTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		pressureTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, pressureTitle.getPreferredSize().height));
		block.add(pressureTitle);
		block.add(Box.createVerticalStrut(4));

		pressureMarketLabel.setHorizontalAlignment(SwingConstants.CENTER);
		pressureMarketLabel.setFont(smallFont());
		pressureMarketLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		pressureMarketLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				pressureMarketLabel.getPreferredSize().height));
		block.add(pressureMarketLabel);
		block.add(Box.createVerticalStrut(3));

		buySellBar = new BuySellBar();
		block.add(buySellBar);
		block.add(Box.createVerticalStrut(3));

		buyPressureLabel.setFont(smallFont());
		buyPressureLabel.setForeground(StockpileColors.HIGH);
		sellPressureLabel.setFont(smallFont());
		sellPressureLabel.setForeground(StockpileColors.LOW);
		sellPressureLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		JPanel pressureRow = new JPanel(new BorderLayout());
		pressureRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		pressureRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		pressureRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, buyPressureLabel.getPreferredSize().height));
		pressureRow.add(buyPressureLabel, BorderLayout.WEST);
		pressureRow.add(sellPressureLabel, BorderLayout.EAST);
		block.add(pressureRow);

		return block;
	}

	/** Builds a two-column grid pairing two captioned values side by side (Market Info / alch rows). */
	private JPanel buildMarketInfoPair(String leftLabel, JLabel leftValue, String rightLabel, JLabel rightValue)
	{
		JPanel grid = new JPanel(new GridBagLayout());
		grid.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		grid.setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.insets = new Insets(1, 4, 1, 4);

		JLabel lh = new JLabel(leftLabel, SwingConstants.CENTER);
		lh.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		lh.setFont(smallFont());
		JLabel rh = new JLabel(rightLabel, SwingConstants.CENTER);
		rh.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		rh.setFont(smallFont());

		leftValue.setFont(smallFont());
		leftValue.setForeground(Color.WHITE);
		rightValue.setFont(smallFont());
		rightValue.setForeground(Color.WHITE);

		c.gridx = 0;
		c.gridy = 0;
		grid.add(lh, c);
		c.gridx = 1;
		c.gridy = 0;
		grid.add(rh, c);
		c.gridx = 0;
		c.gridy = 1;
		grid.add(leftValue, c);
		c.gridx = 1;
		c.gridy = 1;
		grid.add(rightValue, c);
		return grid;
	}

	/** Builds the Links detail section's content: Wiki and Live Prices buttons for the current item. */
	private JPanel buildLinksBlock()
	{
		JPanel block = new JPanel(new GridLayout(1, 2, 6, 0))
		{
			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
			}
		};
		block.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		block.setBorder(new EmptyBorder(4, 8, 6, 8));
		block.setAlignmentX(Component.LEFT_ALIGNMENT);
		block.add(buildLinkButton("Wiki", "Open the OSRS Wiki page for this item", this::openWikiLink));
		block.add(buildLinkButton("Live Prices", "Open the live prices page for this item", this::openPricesLink));

		return block;
	}

	/** Builds a detail-view link button that runs the given action when clicked. */
	private JButton buildLinkButton(String text, String tooltip, Runnable onClick)
	{
		JButton button = new JButton(text);
		button.setFont(smallFont());
		button.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		button.setBackground(ColorScheme.DARK_GRAY_COLOR);
		button.setFocusPainted(false);
		button.setToolTipText(tooltip);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.addActionListener(e -> onClick.run());

		return button;
	}

	/** Opens the OSRS Wiki page for the item currently shown, or the wiki home page in dashboard-home mode. */
	private void openWikiLink()
	{
		if (dashboardHome)
		{
			LinkBrowser.browse(WIKI_HOME);
			return;
		}

		TrackedItem item = currentDetailItem();
		if (item == null)
			return;

		String name = URLEncoder.encode(item.getName(), StandardCharsets.UTF_8).replace("+", "_");
		LinkBrowser.browse(WIKI_BASE + name);
	}

	/** Opens the wiki realtime prices page for the item shown, or the prices home page in dashboard-home mode. */
	private void openPricesLink()
	{
		if (dashboardHome)
		{
			LinkBrowser.browse(PRICES_HOME);
			return;
		}

		TrackedItem item = currentDetailItem();
		if (item == null)
			return;

		LinkBrowser.browse(PRICES_BASE + item.getItemId());
	}

	/** @return the item currently shown in the detail view (a tracked item or the transient preview), or null. */
	private TrackedItem currentDetailItem()
	{
		TrackedItem item = host.trackedItem(boundItemId);
		if (item == null && previewItem != null && previewItem.getItemId() == boundItemId)
			item = previewItem;

		return item;
	}

	/** Builds the alch-info section (high/low alch values and the high-alch profit estimate). */
	private JPanel buildAlchBlock()
	{
		JPanel block = new JPanel()
		{
			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
			}
		};
		block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
		block.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		block.setBorder(new EmptyBorder(6, 8, 6, 8));
		block.setAlignmentX(Component.LEFT_ALIGNMENT);

		haValue.setHorizontalAlignment(SwingConstants.CENTER);
		haProfit.setHorizontalAlignment(SwingConstants.CENTER);
		laValue.setHorizontalAlignment(SwingConstants.CENTER);
		laProfit.setHorizontalAlignment(SwingConstants.CENTER);

		block.add(buildMarketInfoPair("High Alchemy Value", haValue, "Profit", haProfit));
		block.add(Box.createVerticalStrut(6));
		block.add(buildMarketInfoPair("Low Alchemy Value", laValue, "Profit", laProfit));

		JLabel estPrefix = new JLabel("Est. Profit:");
		estPrefix.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		estPrefix.setFont(smallFont());
		alchEstProfit.setFont(smallFont());
		alchEstProfit.setForeground(ColorScheme.LIGHT_GRAY_COLOR);

		alchEstProfitRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
		alchEstProfitRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		alchEstProfitRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		alchEstProfitRow.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
						new EmptyBorder(6, 0, 0, 0),
						new MatteBorder(1, 0, 0, 0, StockpileColors.DIVIDER)),
				new EmptyBorder(4, 0, 0, 0)));
		alchEstProfitRow.add(estPrefix);
		alchEstProfitRow.add(alchEstProfit);
		block.add(alchEstProfitRow);
		return block;
	}

	/** Paints the small left-pointing triangle used by the detail view's Back button. */
	private Icon buildLeftArrowIcon()
	{
		int w = 9;
		int h = 10;
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		int midY = h / 2;
		int[] xs = {0, w - 1, w - 1};
		int[] ys = {midY, 0, h - 1};
		g.fillPolygon(xs, ys, xs.length);
		g.dispose();
		return new ImageIcon(img);
	}

	/** Loads the bundled {@code eye.png} scaled to a square icon for the view-only button. */
	private Icon buildEyeIcon(int size)
	{
		try
		{
			BufferedImage img = ImageUtil.loadImageResource(getClass(), "eye.png");
			return new ImageIcon(img.getScaledInstance(size, size, Image.SCALE_SMOOTH));
		}
		catch (Exception ex)
		{
			return new ImageIcon(new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB));
		}
	}

	/** Loads the bundled {@code broom.png} scaled to a 12px icon for the clear-acquisitions button. */
	private Icon buildBrushIcon()
	{
		try
		{
			BufferedImage img = ImageUtil.loadImageResource(getClass(), "broom.png");
			return new ImageIcon(img.getScaledInstance(12, 12, Image.SCALE_SMOOTH));
		}
		catch (Exception ex)
		{
			return new ImageIcon(new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB));
		}
	}

	/** Builds a centred bold section title, optionally topped with a divider rule. */
	private JLabel buildDetailSectionTitle(String text, boolean withDivider)
	{
		JLabel title = new JLabel(text, SwingConstants.CENTER)
		{
			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
			}
		};
		title.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		title.setFont(boldFont());
		title.setAlignmentX(Component.LEFT_ALIGNMENT);
		if (withDivider)
			title.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(
							new EmptyBorder(10, 0, 0, 0),
							new MatteBorder(1, 0, 0, 0, StockpileColors.DIVIDER)),
					new EmptyBorder(10, 0, 6, 0)));
		else
			title.setBorder(new EmptyBorder(10, 0, 6, 0));

		return title;
	}

	/** Switches to the detail card for an item, requesting its full data and populating the view. */
	public void show(int itemId)
	{
		dashboardHome = false;
		TrackedItem item = host.trackedItem(itemId);
		if (item == null)
			return;

		previewItem = null;
		boundItemId = itemId;
		detailLoadTimedOut = false;
		populateDetail(item);
		applyDetailCard();
		if (onRequestDetailData != null)
			onRequestDetailData.accept(itemId);
	}

	/**
	 * Shows the item-less "dashboard home" state of the pop-out window (#109): the Stockpile icon and name
	 * stand in for an item, the body is replaced by a centred "search for an item" prompt, and the
	 * Track/Untrack control is hidden while the search bar stays live. Dashboard layout only.
	 */
	public void showDashboardHome()
	{
		dashboardHome = true;
		previewItem = null;
		boundItemId = -1;
		detailLoadTimedOut = false;
		stopDetailLoading();
		applyDashboardHome();
		cardLayout.show(this, CARD_CONTENT);
	}

	/**
	 * Applies the dashboard-home identity and layout: Stockpile icon and name, the dashboard examine
	 * caption, a hidden quantity line and Track button, the section columns hidden, and the centred
	 * search prompt shown in their place.
	 */
	private void applyDashboardHome()
	{
		applyDashboardLinks(true);
		detailQtyLabel.setVisible(false);
		detailSectionsHost.setVisible(false);
		if (dashboardEmptyMessage != null)
			dashboardEmptyMessage.setVisible(true);

		BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");
		if (icon != null)
			detailIconLabel.setIcon(new ImageIcon(ImageUtil.resizeImage(icon, 32, 32)));

		EllipsisText.set(detailNameLabel, "Stockpile");

		detailExamineText = "The Stockpile Dashboard View";
		applyExamineWrap();
		detailDescriptionArea.setVisible(true);
	}

	/**
	 * Toggles tracking of the item shown in the detail view (#138), driven by the header button.
	 * A read-only preview is added to the tracked list (the next rebuild swaps the preview for the
	 * real tracked detail); a tracked item is untracked but stays open as a preview so the detail
	 * view does not bounce back to the main list.
	 */
	private void toggleDetailTracking()
	{
		final int itemId = boundItemId;
		if (itemId <= 0)
			return;

		if (detailItemTracked)
			onUntrackToPreview.accept(itemId);
		else
			onAddItem.accept(itemId, TrackItemMode.TRACK);
	}

	/**
	 * Shows either the spinner placeholder or the populated detail view for the
	 * currently open item, depending on whether its prices are still loading.
	 * A view-only preview shows the spinner until its prices arrive, its load
	 * fails, or the safety timeout fires; everything else shows immediately.
	 */
	private void applyDetailCard()
	{
		TrackedItem shown = shownDetailItem();
		if (isDetailLoading(shown))
		{
			detailSpinner.start();
			if (!detailLoadTimeout.isRunning())
				detailLoadTimeout.restart();

			cardLayout.show(this, CARD_LOADING);
		}
		else
		{
			stopDetailLoading();
			cardLayout.show(this, CARD_CONTENT);
		}
	}

	/** Stops the spinner animation and cancels the pending load-timeout, if any. */
	private void stopDetailLoading()
	{
		detailSpinner.stop();
		detailLoadTimeout.stop();
	}

	/** @return the item currently backing the detail view (tracked or preview), or {@code null}. */
	private TrackedItem shownDetailItem()
	{
		if (previewItem != null && previewItem.getItemId() == boundItemId)
			return previewItem;

		return host.trackedItem(boundItemId);
	}

	/** @return whether {@code item} is a tradeable preview whose prices have not yet loaded (or failed). */
	private boolean isDetailLoading(TrackedItem item)
	{
		return item != null
				&& item.getMode() == TrackItemMode.VIEW
				&& item.isTradeable()
				&& !item.hasPrices()
				&& !item.isPriceLoadFailed()
				&& !detailLoadTimedOut;
	}

	/** Fills {@link #detailLoadingCard} with a centered spinner and caption. */
	private void buildDetailLoadingCard()
	{
		detailLoadingCard.setBackground(ColorScheme.DARK_GRAY_COLOR);
		detailLoadingCard.setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel inner = new JPanel();
		inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
		inner.setBackground(ColorScheme.DARK_GRAY_COLOR);

		detailSpinner.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel caption = new JLabel("Loading item data…");
		caption.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		caption.setFont(smallFont());
		caption.setAlignmentX(Component.CENTER_ALIGNMENT);

		inner.add(detailSpinner);
		inner.add(Box.createVerticalStrut(10));
		inner.add(caption);

		detailLoadingCard.add(inner);
	}

	/**
	 * Sets the current examine text on the description area. The {@link JTextArea} line-wraps to its
	 * own laid-out width, so no width measurement is needed and it re-wraps responsively on resize.
	 */
	private void applyExamineWrap()
	{
		if (detailExamineText == null)
			return;

		detailDescriptionArea.setText(detailExamineText);
	}

	/**
	 * Fills every detail section from an item's current state: header name/icon/quantity,
	 * item and collection values, overview grid, charts, market info (times, volatility,
	 * liquidity, range, pressure), alch figures, notifications, and the acquisitions log.
	 * Called whenever the shown item's data changes.
	 */
	private void populateDetail(TrackedItem item)
	{
		final boolean viewOnly = item.getMode() == TrackItemMode.VIEW;

		if (viewLayout == Layout.DASHBOARD)
		{
			applyDashboardLinks(false);
			detailSectionsHost.setVisible(true);
			if (dashboardEmptyMessage != null)
				dashboardEmptyMessage.setVisible(false);
		}

		detailItemTracked = !viewOnly;
		detailTrackBtn.setText(viewOnly ? "Track" : "Untrack");
		detailTrackBtn.setForeground(viewOnly ? config.trackItemColor() : config.stopTrackingColor());

		rebuildOverviewGrid();
		applyDetailSectionLayout();

		AsyncBufferedImage icon = itemManager.getImage(item.getItemId(), item.iconStackSize(), item.isStackable());
		icon.addTo(detailIconLabel);
		EllipsisText.set(detailNameLabel, item.getName());

		int detailQty = item.getQuantity();
		detailQtyLabel.setText("Qty: " + GpFormat.shortValue(detailQty));
		detailQtyLabel.setToolTipText(NUMBER_FORMAT.format(detailQty));
		detailQtyLabel.setVisible(!viewOnly);

		final String examine = host.examine(item.getItemId());
		final boolean hasExamine = examine != null && !examine.isEmpty();
		detailExamineText = hasExamine ? examine : null;
		applyExamineWrap();
		detailDescriptionArea.setVisible(hasExamine);

		final boolean hasPrices = item.hasPrices();
		final boolean showMarket = item.isTradeable();
		final ValueFormat full = ValueFormat.FULL;

		icvHigh.setText("High: " + (hasPrices ? StockpilePanel.formatTotalGp(item.getHighPrice(), full) : "—"));
		icvLow.setText("Low: " + (hasPrices ? StockpilePanel.formatTotalGp(item.getLowPrice(), full) : "—"));
		icvAvg.setText("Average: " + (hasPrices ? StockpilePanel.formatTotalGp(item.getAvgPrice(), full) : "—"));
		long vol24 = windowVolume(item, TimeWindow.H24);
		icvVolume.setText("Volume (24h): " + (vol24 > 0 ? NUMBER_FORMAT.format(vol24) : "—"));

		int colQty = item.getRecordQuantitySum();
		ccvSection.setVisible(showMarket && colQty > 0);
		if (colQty > 0)
		{
			long cHigh = (long) colQty * item.getHighPrice();
			long cLow = (long) colQty * item.getLowPrice();
			long cAvg = (long) colQty * item.getAvgPrice();
			ccvHigh.setText("High: " + (hasPrices ? StockpilePanel.formatTotalGp(cHigh, full) : "—"));
			ccvLow.setText("Low: " + (hasPrices ? StockpilePanel.formatTotalGp(cLow, full) : "—"));
			ccvAvg.setText("Average: " + (hasPrices ? StockpilePanel.formatTotalGp(cAvg, full) : "—"));
			ccvQuantity.setText("Quantity: " + NUMBER_FORMAT.format(colQty));
			long estProfit = cAvg - item.getCostBasis();
			String sign = estProfit > 0 ? "+" : "";
			ccvProfit.setText("Est. Profit: "
					+ (hasPrices ? sign + StockpilePanel.formatTotalGp(estProfit, full) : "—"));
			ccvProfit.setForeground(!hasPrices || estProfit == 0 ? ColorScheme.LIGHT_GRAY_COLOR
					: (estProfit > 0 ? StockpileColors.HIGH : StockpileColors.LOW));
		}

		populateOverviewLabels(overviewLabels, item, false);

		if (priceGraph != null)
			priceGraph.setData(item.getSeries5m(), item.getSeries1h(), item.getSeries6h(),
					item.getSeries24h(), item.getAvgPrice());

		if (volumeGraph != null)
			volumeGraph.setData(item.getSeries5m(), item.getSeries1h(), item.getSeries6h(),
					item.getSeries24h(), item.getAvgPrice());

		applyBuyLimit(item);
		long tax = geTax(item.getAvgPrice());
		miGeTax.setText(hasPrices ? "~" + StockpilePanel.formatTotalGp(tax, full) : "—");
		applyTradeTime(miLastBought, item.getLatestHighTime());
		applyTradeTime(miLastSold, item.getLatestLowTime());
		applyVolatility(item);
		applyLiquidity(vol24);
		if (priceRangeBar != null)
		{
			long[] range = thirtyDayRange(item);
			priceRangeBar.setRange(range[0], range[1], item.getAvgPrice());
			applyRangePosition(range[0], range[1], item.getAvgPrice());
		}

		applyBuySellPressure(item);

		long ha = item.getHighAlch();
		long la = item.getLowAlch();
		long avg = item.getAvgPrice();
		haValue.setText(ha > 0 ? StockpilePanel.formatTotalGp(ha, full) : "—");
		laValue.setText(la > 0 ? StockpilePanel.formatTotalGp(la, full) : "—");
		long haP = ha - avg - host.natureRunePrice() - 5 * host.fireRunePrice();
		long laP = la - avg - host.natureRunePrice() - 3 * host.fireRunePrice();
		boolean alchKnown = ha > 0 && hasPrices;
		boolean laKnown = la > 0 && hasPrices;
		applyProfitLabel(haProfit, haP, alchKnown);
		applyProfitLabel(laProfit, laP, laKnown);
		haProfit.setToolTipText(alchKnown ? alchProfitTooltip("High", ha, avg, 5) : null);
		laProfit.setToolTipText(laKnown ? alchProfitTooltip("Low", la, avg, 3) : null);
		boolean showAlchProfit = colQty > 0 && alchKnown;
		alchEstProfitRow.setVisible(showAlchProfit);
		if (showAlchProfit)
		{
			long estProfit = haP * colQty;
			String sign = estProfit > 0 ? "+" : "";
			alchEstProfit.setText(sign + StockpilePanel.formatTotalGp(estProfit, full));
			alchEstProfit.setForeground(estProfit == 0 ? ColorScheme.LIGHT_GRAY_COLOR
					: (estProfit > 0 ? StockpileColors.HIGH : StockpileColors.LOW));
			alchEstProfit.setToolTipText("<html>High alch profit (" + StockpilePanel.signedGp(haP)
					+ ") × " + NUMBER_FORMAT.format(colQty) + " in collection log"
					+ "<br>= " + StockpilePanel.signedGp(estProfit) + "</html>");
		}

		if (!viewOnly && item.getNotifications().isEmpty())
		{
			for (int i = 0; i < DEFAULT_NOTIFICATION_ROWS; i++)
				item.getNotifications().add(new NotificationRule());

			item.setNotificationsInitialized(true);
			notifyNotificationsEdited();
		}

		if (!viewOnly && !notificationsTable.isEditing())
		{
			notificationsModel.setItem(item);
			applyNotificationRenderers();
			notificationsTable.revalidate();
		}

		if (!viewOnly && !acquisitionsTable.isEditing())
		{
			acquisitionsModel.setItem(item);
			applyTableRenderers();
			acquisitionsTable.revalidate();
		}

		notificationsSection.setVisible(!viewOnly);
		acquisitionsSection.setVisible(!viewOnly);

		itemValuesSection.setVisible(showMarket);
		marketInfoSection.setVisible(showMarket);
		priceOverviewSection.setVisible(showMarket);
		priceGraphSection.setVisible(showMarket);
		volumeGraphSection.setVisible(showMarket);

		updateAcqPopoutButton();

		if (acqPopoutModel != null)
		{
			acqPopoutModel.setItem(item);
			applyAcqRenderers(acqPopoutTable, acqPopoutModel, true);
			acqPopoutTable.revalidate();
		}

		refreshPopouts(item);
	}

	/** Resets an overview cell to the {@code "-"} placeholder. */
	private void setOverviewPlaceholder(JLabel label)
	{
		clearItemValue(label, "-");
	}

	/** Sets a price cell's text (full or abbreviated), color, tooltip, and hover tint, or a placeholder if unset. */
	private void setPriceCell(JLabel label, long value, Color color, String tooltipLabel, Color tint, boolean full)
	{
		label.setForeground(color);
		if (value <= 0)
		{
			setOverviewPlaceholder(label);
		}
		else if (full)
		{
			StockpilePanel.removeHoverTint(label);
			label.setText(NUMBER_FORMAT.format(value));
			String tooltipPrefix = tooltipLabel == null ? "" : tooltipLabel + ": ";
			label.setToolTipText(tooltipPrefix + NUMBER_FORMAT.format(value) + " gp");
		}
		else
		{
			StockpilePanel.installShortValue(label, value, GpFormat.shortValue1dp(value), tooltipLabel, tint);
		}
	}

	/** @return the high/low midpoint of a price point, or whichever side is known when one is missing. */
	private static long overviewMidpoint(WikiRealtimePriceClient.PricePoint p)
	{
		long h = p.getAvgHighPrice();
		long l = p.getAvgLowPrice();
		if (h > 0 && l > 0)
			return (h + l) / 2;

		return Math.max(h, l);
	}

	/** @return the total traded volume for an item over the given window, or 0 if unknown. */
	private long windowVolume(TrackedItem item, TimeWindow window)
	{
		PriceStats s = item.getWindowStats().get(window);
		return s == null ? 0 : s.getVolume();
	}

	/** Sets a volume cell's compact/full text with a full-number tooltip and hover tint, or a placeholder. */
	private void installVolumeValue(JLabel label, long vol, boolean full)
	{
		label.setForeground(COLOR_VOLUME);
		label.setToolTipText("Volume: " + NUMBER_FORMAT.format(vol));
		if (full)
		{
			StockpilePanel.removeHoverTint(label);
			label.setText(NUMBER_FORMAT.format(vol));
			return;
		}

		String text = GpFormat.shortValue1dp(vol);
		label.setText(text);
		StockpilePanel.removeHoverTint(label);
		HoverTintListener listener = new HoverTintListener(label, text, StockpileColors.TINT_VOLUME);
		label.addMouseListener(listener);
		SwingUtilities.invokeLater(listener::applyIfHovered);
	}

	/** Sets a label to the signed percent change of the current price vs. the window average, colored up/down. */
	private void applyDeltaPct(JLabel label, TrackedItem item, TimeWindow window)
	{
		for (MouseListener ml : label.getMouseListeners())
		{
			if (ml instanceof HoverTintListener)
				label.removeMouseListener(ml);
		}

		label.setToolTipText(null);

		long current = item.getAvgPrice();
		PriceStats stats = item.getWindowStats().get(window);
		long baseline = stats == null ? 0 : stats.getAvg();
		if (current <= 0 || baseline <= 0)
		{
			label.setText("-");
			label.setForeground(COLOR_VOLUME);
			label.setToolTipText(null);
			return;
		}

		double pct = Math.round(((double) (current - baseline) / baseline) * 1000.0) / 10.0;
		String pctText;
		Color color;
		Color tint;
		if (pct == 0.0)
		{
			pctText = "0%";
			color = COLOR_VOLUME;
			tint = StockpileColors.TINT_VOLUME;
		}
		else
		{
			pctText = String.format(Locale.US, "%+.1f%%", pct);
			color = pct > 0 ? StockpileColors.HIGH : StockpileColors.LOW;
			tint = pct > 0 ? StockpileColors.TINT_HIGH : StockpileColors.TINT_LOW;
		}

		label.setText(pctText);
		label.setForeground(color);
		label.setToolTipText(pctText + " change compared to " + spelledInterval(window) + " avg.");

		HoverTintListener listener = new HoverTintListener(label, pctText, tint);
		label.addMouseListener(listener);
		SwingUtilities.invokeLater(listener::applyIfHovered);
	}

	/** @return the window's long label lower-cased for use mid-sentence in tooltips. */
	private static String spelledInterval(TimeWindow window)
	{
		return window.getLongLabel().toLowerCase(Locale.ROOT);
	}

	/** Sets a profit label to a signed, colored gp figure, or a placeholder when the profit is unknown. */
	private void applyProfitLabel(JLabel label, long profit, boolean known)
	{
		if (!known)
		{
			label.setText("—");
			label.setForeground(Color.WHITE);
			return;
		}

		String sign = profit > 0 ? "+" : "";
		label.setText(sign + StockpilePanel.formatTotalGp(profit, ValueFormat.FULL));
		label.setForeground(profit == 0 ? Color.WHITE : (profit > 0 ? StockpileColors.HIGH : StockpileColors.LOW));
	}

	/** Builds the tooltip breaking down an alch-profit figure: alch value minus item cost and rune cost. */
	private String alchProfitTooltip(String label, long alchValue, long itemAvg, int fireQty)
	{
		long natureCost = host.natureRunePrice();
		long fireCost = (long) fireQty * host.fireRunePrice();
		long profit = alchValue - itemAvg - natureCost - fireCost;
		return "<html>" + label + " alch profit:<br>"
				+ NUMBER_FORMAT.format(alchValue) + " (alch value)<br>"
				+ "− " + NUMBER_FORMAT.format(itemAvg) + " (item avg price)<br>"
				+ "− " + NUMBER_FORMAT.format(host.natureRunePrice()) + " (nature rune)<br>"
				+ "− " + fireQty + " × " + NUMBER_FORMAT.format(host.fireRunePrice()) + " (fire rune)<br>"
				+ "= " + StockpilePanel.signedGp(profit) + "</html>";
	}

	/** @return the Grand Exchange sell tax on a unit at {@code avgPrice} (per the live GE tax rules). */
	private long geTax(long avgPrice)
	{
		if (avgPrice < 50)
			return 0;

		long tax = (long) Math.floor(avgPrice * 0.02);
		return Math.min(tax, 5_000_000L);
	}

	/** Sets the market-info volatility rating from the item's week series via {@link MarketClassifier}. */
	private void applyVolatility(TrackedItem item)
	{
		String label = MarketClassifier.volatility(item.getSeriesFor(TimeWindow.WEEK));
		if (label == null)
		{
			miVolatility.setText("—");
			miVolatility.setForeground(Color.WHITE);
			miVolatility.setToolTipText(null);
			return;
		}

		Color color;
		String tooltip;
		switch (label)
		{
			case "Low":
				color = StockpileColors.HIGH; tooltip = "Stable Price";
				break;
			case "Medium":
				color = StockpileColors.AVG; tooltip = "Occasional/Moderate Price Swings";
				break;
			default:
				color = StockpileColors.LOW; tooltip = "Large/Frequent Price Swings";
				break;
		}

		miVolatility.setText(label);
		miVolatility.setForeground(color);
		miVolatility.setToolTipText(tooltip);
	}

	/** Sets the market-info liquidity rating from the last 24h volume via {@link MarketClassifier}. */
	private void applyLiquidity(long vol24)
	{
		String label = MarketClassifier.liquidity(vol24);
		if (label == null)
		{
			miLiquidity.setText("—");
			miLiquidity.setForeground(Color.WHITE);
			miLiquidity.setToolTipText(null);
			return;
		}

		Color color = "Low".equals(label) ? StockpileColors.LOW
				: "Medium".equals(label) ? StockpileColors.AVG : StockpileColors.HIGH;
		miLiquidity.setText(label);
		miLiquidity.setForeground(color);
		miLiquidity.setToolTipText("24h volume: " + NUMBER_FORMAT.format(vol24));
	}

	/** Sets the market-info "30-day range position" rating for where the live price sits within its month range. */
	private void applyRangePosition(long min, long max, long live)
	{
		String text = MarketClassifier.rangePosition(min, max, live);
		if (text == null)
		{
			rangePositionLabel.setText("-");
			rangePositionLabel.setForeground(COLOR_VOLUME);
			return;
		}

		Color color;
		switch (text)
		{
			case "Highest":
			case "High":
				color = StockpileColors.HIGH;
				break;
			case "Low":
			case "Lowest":
				color = StockpileColors.LOW;
				break;
			default:
				color = StockpileColors.AVG;
				break;
		}

		rangePositionLabel.setText(text);
		rangePositionLabel.setForeground(color);
	}

	/** Computes the buy/sell volume split over the configured window and updates the pressure bar + labels. */
	private void applyBuySellPressure(TrackedItem item)
	{
		if (buySellBar == null)
			return;

		PressureWindow win = config.buySellPressureWindow();
		long[] split = MarketClassifier.buySellVolume(item.getSeriesFor(win.window()), win.duration());
		long buy = split[0];
		long sell = split[1];
		long total = buy + sell;

		if (total <= 0)
		{
			buySellBar.setRatio(-1);
			pressureMarketLabel.setText("No data");
			pressureMarketLabel.setForeground(StockpileColors.MUTED);
			buyPressureLabel.setText("");
			buyPressureLabel.setVolume(-1);
			sellPressureLabel.setText("");
			sellPressureLabel.setVolume(-1);
			return;
		}

		double buyFraction = (double) buy / total;
		int buyPct = (int) Math.round(buyFraction * 100);
		int sellPct = 100 - buyPct;
		buySellBar.setRatio(buyFraction);

		if (buyPct >= PRESSURE_BALANCED_LOW && buyPct <= PRESSURE_BALANCED_HIGH)
		{
			pressureMarketLabel.setText("Balanced Market");
			pressureMarketLabel.setForeground(StockpileColors.AVG);
		}
		else if (buyPct > PRESSURE_BALANCED_HIGH)
		{
			pressureMarketLabel.setText("Sellers Market");
			pressureMarketLabel.setForeground(StockpileColors.LOW);
		}
		else
		{
			pressureMarketLabel.setText("Buyers Market");
			pressureMarketLabel.setForeground(StockpileColors.HIGH);
		}

		buyPressureLabel.setText(buyPct + "% Buy (" + GpFormat.shortValue(buy) + ")");
		buyPressureLabel.setVolume(buy);
		sellPressureLabel.setText(sellPct + "% Sell (" + GpFormat.shortValue(sell) + ")");
		sellPressureLabel.setVolume(sell);
	}

	/** @return the {@code [min, max]} price range over the item's last 30 days via {@link MarketClassifier}. */
	private long[] thirtyDayRange(TrackedItem item)
	{
		return MarketClassifier.thirtyDayRange(item.getSeriesFor(TimeWindow.MONTH));
	}

	/** Applies the acquisitions renderers to the sidebar (compact) table. */
	private void applyTableRenderers()
	{
		applyAcqRenderers(acquisitionsTable, acquisitionsModel, false);
	}

	/**
	 * Wires an acquisitions table's fonts, row height, per-column renderers/editors, and
	 * headers for either the compact sidebar table or the expanded pop-out table.
	 */
	private void applyAcqRenderers(JTable table, AcquisitionsTableModel model, boolean expanded)
	{
		Font f = expanded ? new Font(Font.MONOSPACED, Font.PLAIN, 18) : smallFont();
		table.setFont(f);
		table.setRowHeight(expanded ? 30 : 22);
		table.getTableHeader().setFont(f);

		JTextField centerEditorField = new JTextField();
		centerEditorField.setHorizontalAlignment(SwingConstants.CENTER);
		centerEditorField.setFont(f);
		DefaultCellEditor centerEditor = new DefaultCellEditor(centerEditorField);

		int cols = table.getColumnCount();
		for (int i = 0; i < cols; i++)
		{
			TableColumn col = table.getColumnModel().getColumn(i);
			String name = model.getColumnName(i);
			if (model.isSymbolColumn(i))
			{
				col.setCellRenderer(new SourceGlyphRenderer(() -> acqHoverRow, () -> acqHoverCol));
				col.setMinWidth(28);
				col.setMaxWidth(28);
				col.setPreferredWidth(28);
				col.setHeaderValue("");
				continue;
			}

			boolean isProfit = "Profit".equals(name);
			boolean isSold = "Sold".equals(name);
			col.setCellRenderer(new AcqCellRenderer(isProfit, expanded, () -> acqHoverRow, () -> acqHoverCol,
					isSold ? model::isSellEstimated : null));
			if (i < 3)
				col.setCellEditor(centerEditor);

			col.setHeaderValue(expanded ? expandedAcqHeader(name) : name);
		}

		TableCellRenderer hr = table.getTableHeader().getDefaultRenderer();
		if (hr instanceof DefaultTableCellRenderer)
			((DefaultTableCellRenderer) hr).setHorizontalAlignment(SwingConstants.CENTER);

		table.getTableHeader().repaint();
	}

	/** @return the roomy pop-out header for a compact acquisitions column name. */
	private static String expandedAcqHeader(String compact)
	{
		switch (compact)
		{
			case "Qty": return "Quantity";
			case "Bought": return "Bought Price";
			case "Sold": return "Sold Price";
			default: return compact;
		}
	}

	/** @return the tooltip caption for an acquisitions-table column. */
	private static String acqTooltipLabel(int col)
	{
		switch (col)
		{
			case 0: return "Quantity";
			case 1: return "Bought At";
			case 2: return "Sold At";
			case 3: return "Profit";
			default: return "";
		}
	}

	/** @return this view's own preferred size as the preferred viewport size. */
	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return getPreferredSize();
	}

	/** @return a fixed unit scroll increment matching the enclosing scroll pane's step. */
	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 16;
	}

	/** @return a block scroll increment of one visible page along the scroll axis. */
	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return orientation == SwingConstants.VERTICAL ? visibleRect.height : visibleRect.width;
	}

	/**
	 * @return {@code true} so the view always matches the viewport width and reflows its dashboard
	 *         columns when the hosting window is resized &mdash; including shrinking back after a
	 *         maximise/restore &mdash; rather than clipping at a stale wide preferred width (#109).
	 */
	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		return true;
	}

	/**
	 * @return {@code true} in dashboard-home mode so the short header + centred prompt fill the viewport
	 *         height (letting the prompt sit vertically centred); {@code false} otherwise so tall item
	 *         content scrolls vertically instead of squashing.
	 */
	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		return dashboardHome;
	}
}

