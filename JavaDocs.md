# Stockpile — JavaDoc Reference

<!-- GENERATED FILE — DO NOT EDIT BY HAND.
     Run `./gradlew generateJavaDocs` and commit the result. -->

## Contents

- [com.oveduumnakal.AcqCellRenderer](#comoveduumnakalacqcellrenderer)
- [com.oveduumnakal.AcquisitionCsvExporter](#comoveduumnakalacquisitioncsvexporter)
- [com.oveduumnakal.AcquisitionRecord](#comoveduumnakalacquisitionrecord)
- [com.oveduumnakal.AcquisitionSource](#comoveduumnakalacquisitionsource)
- [com.oveduumnakal.AcquisitionsTableModel](#comoveduumnakalacquisitionstablemodel)
- [com.oveduumnakal.BuySellBar](#comoveduumnakalbuysellbar)
- [com.oveduumnakal.CategoryActions](#comoveduumnakalcategoryactions)
- [com.oveduumnakal.CategoryState](#comoveduumnakalcategorystate)
- [com.oveduumnakal.Changelog](#comoveduumnakalchangelog)
- [com.oveduumnakal.Changelog.Release](#comoveduumnakalchangelogrelease)
- [com.oveduumnakal.CostBasisLedger](#comoveduumnakalcostbasisledger)
- [com.oveduumnakal.DecantBasis](#comoveduumnakaldecantbasis)
- [com.oveduumnakal.DestroyedOutputSources](#comoveduumnakaldestroyedoutputsources)
- [com.oveduumnakal.DetailView](#comoveduumnakaldetailview)
- [com.oveduumnakal.DetailView.Layout](#comoveduumnakaldetailviewlayout)
- [com.oveduumnakal.DetailViewHost](#comoveduumnakaldetailviewhost)
- [com.oveduumnakal.DoseFamily](#comoveduumnakaldosefamily)
- [com.oveduumnakal.DoseFamily.Parsed](#comoveduumnakaldosefamilyparsed)
- [com.oveduumnakal.EllipsisText](#comoveduumnakalellipsistext)
- [com.oveduumnakal.EllipsisText.EllipsisResizeListener](#comoveduumnakalellipsistextellipsisresizelistener)
- [com.oveduumnakal.EstimatesPosition](#comoveduumnakalestimatesposition)
- [com.oveduumnakal.EstimatesSpacing](#comoveduumnakalestimatesspacing)
- [com.oveduumnakal.FallbackPricing](#comoveduumnakalfallbackpricing)
- [com.oveduumnakal.GeIntegrationMode](#comoveduumnakalgeintegrationmode)
- [com.oveduumnakal.GeOfferTracker](#comoveduumnakalgeoffertracker)
- [com.oveduumnakal.GeOfferTracker.Event](#comoveduumnakalgeoffertrackerevent)
- [com.oveduumnakal.GeOfferTracker.Kind](#comoveduumnakalgeoffertrackerkind)
- [com.oveduumnakal.GeOfferTracker.SlotState](#comoveduumnakalgeoffertrackerslotstate)
- [com.oveduumnakal.GeOfferTracker.Type](#comoveduumnakalgeoffertrackertype)
- [com.oveduumnakal.GlowSpeed](#comoveduumnakalglowspeed)
- [com.oveduumnakal.GpFormat](#comoveduumnakalgpformat)
- [com.oveduumnakal.HighlightMode](#comoveduumnakalhighlightmode)
- [com.oveduumnakal.HoverTintListener](#comoveduumnakalhovertintlistener)
- [com.oveduumnakal.IssueField](#comoveduumnakalissuefield)
- [com.oveduumnakal.ItemCategoryClassifier](#comoveduumnakalitemcategoryclassifier)
- [com.oveduumnakal.ItemDeltas](#comoveduumnakalitemdeltas)
- [com.oveduumnakal.ItemDeltas.DeltaAction](#comoveduumnakalitemdeltasdeltaaction)
- [com.oveduumnakal.LedgerHost](#comoveduumnakalledgerhost)
- [com.oveduumnakal.MarketClassifier](#comoveduumnakalmarketclassifier)
- [com.oveduumnakal.NotifCellRenderer](#comoveduumnakalnotifcellrenderer)
- [com.oveduumnakal.NotificationMetric](#comoveduumnakalnotificationmetric)
- [com.oveduumnakal.NotificationMetric.Kind](#comoveduumnakalnotificationmetrickind)
- [com.oveduumnakal.NotificationOperation](#comoveduumnakalnotificationoperation)
- [com.oveduumnakal.NotificationRule](#comoveduumnakalnotificationrule)
- [com.oveduumnakal.NotificationValueEditor](#comoveduumnakalnotificationvalueeditor)
- [com.oveduumnakal.NotificationsTableModel](#comoveduumnakalnotificationstablemodel)
- [com.oveduumnakal.OverlayLayout](#comoveduumnakaloverlaylayout)
- [com.oveduumnakal.OverviewPreset](#comoveduumnakaloverviewpreset)
- [com.oveduumnakal.PanelActions](#comoveduumnakalpanelactions)
- [com.oveduumnakal.PopoutHandle](#comoveduumnakalpopouthandle)
- [com.oveduumnakal.PortfolioChartPanel](#comoveduumnakalportfoliochartpanel)
- [com.oveduumnakal.PortfolioChartPanel.TipLine](#comoveduumnakalportfoliochartpaneltipline)
- [com.oveduumnakal.PortfolioHistory](#comoveduumnakalportfoliohistory)
- [com.oveduumnakal.PortfolioShareCodec](#comoveduumnakalportfoliosharecodec)
- [com.oveduumnakal.PortfolioShareCodec.Entry](#comoveduumnakalportfoliosharecodecentry)
- [com.oveduumnakal.PortfolioShareCodec.Snapshot](#comoveduumnakalportfoliosharecodecsnapshot)
- [com.oveduumnakal.PressureVolumeLabel](#comoveduumnakalpressurevolumelabel)
- [com.oveduumnakal.PressureWindow](#comoveduumnakalpressurewindow)
- [com.oveduumnakal.PriceGraphPanel](#comoveduumnakalpricegraphpanel)
- [com.oveduumnakal.PriceGraphPanel.LineSet](#comoveduumnakalpricegraphpanellineset)
- [com.oveduumnakal.PriceGraphPanel.Mode](#comoveduumnakalpricegraphpanelmode)
- [com.oveduumnakal.PriceIndicatorMode](#comoveduumnakalpriceindicatormode)
- [com.oveduumnakal.PriceRangeBar](#comoveduumnakalpricerangebar)
- [com.oveduumnakal.PriceStats](#comoveduumnakalpricestats)
- [com.oveduumnakal.ProcessingBasis](#comoveduumnakalprocessingbasis)
- [com.oveduumnakal.PulseEntry](#comoveduumnakalpulseentry)
- [com.oveduumnakal.SectionSlot](#comoveduumnakalsectionslot)
- [com.oveduumnakal.SessionStats](#comoveduumnakalsessionstats)
- [com.oveduumnakal.SessionStats.Delta](#comoveduumnakalsessionstatsdelta)
- [com.oveduumnakal.SortMode](#comoveduumnakalsortmode)
- [com.oveduumnakal.SourceAttributionCore](#comoveduumnakalsourceattributioncore)
- [com.oveduumnakal.SourceAttributionCore.Attribution](#comoveduumnakalsourceattributioncoreattribution)
- [com.oveduumnakal.SourceAttributionCore.Claim](#comoveduumnakalsourceattributioncoreclaim)
- [com.oveduumnakal.SourceGlyphRenderer](#comoveduumnakalsourceglyphrenderer)
- [com.oveduumnakal.Spinner](#comoveduumnakalspinner)
- [com.oveduumnakal.StockpileColors](#comoveduumnakalstockpilecolors)
- [com.oveduumnakal.StockpileConfig](#comoveduumnakalstockpileconfig)
- [com.oveduumnakal.StockpileGroundOverlay](#comoveduumnakalstockpilegroundoverlay)
- [com.oveduumnakal.StockpileHighlightOverlay](#comoveduumnakalstockpilehighlightoverlay)
- [com.oveduumnakal.StockpilePanel](#comoveduumnakalstockpilepanel)
- [com.oveduumnakal.StockpilePanel.ChangelogSection](#comoveduumnakalstockpilepanelchangelogsection)
- [com.oveduumnakal.StockpilePersistence](#comoveduumnakalstockpilepersistence)
- [com.oveduumnakal.StockpilePersistence.CachedPrice](#comoveduumnakalstockpilepersistencecachedprice)
- [com.oveduumnakal.StockpilePersistence.CategoryData](#comoveduumnakalstockpilepersistencecategorydata)
- [com.oveduumnakal.StockpilePersistence.PersistedItem](#comoveduumnakalstockpilepersistencepersisteditem)
- [com.oveduumnakal.StockpilePlugin](#comoveduumnakalstockpileplugin)
- [com.oveduumnakal.StockpileScreenOverlay](#comoveduumnakalstockpilescreenoverlay)
- [com.oveduumnakal.StockpileScreenOverlay.Seg](#comoveduumnakalstockpilescreenoverlayseg)
- [com.oveduumnakal.SuspensionSource](#comoveduumnakalsuspensionsource)
- [com.oveduumnakal.SuspensionSource.StampMode](#comoveduumnakalsuspensionsourcestampmode)
- [com.oveduumnakal.TimeWindow](#comoveduumnakaltimewindow)
- [com.oveduumnakal.TrackItemMode](#comoveduumnakaltrackitemmode)
- [com.oveduumnakal.TrackedItem](#comoveduumnakaltrackeditem)
- [com.oveduumnakal.TrackedItem.SuspensionState](#comoveduumnakaltrackeditemsuspensionstate)
- [com.oveduumnakal.TradeApportioner](#comoveduumnakaltradeapportioner)
- [com.oveduumnakal.TradeApportioner.Leg](#comoveduumnakaltradeapportionerleg)
- [com.oveduumnakal.ValueFormat](#comoveduumnakalvalueformat)
- [com.oveduumnakal.WikiRealtimePriceClient](#comoveduumnakalwikirealtimepriceclient)
- [com.oveduumnakal.WikiRealtimePriceClient.ItemMapping](#comoveduumnakalwikirealtimepriceclientitemmapping)
- [com.oveduumnakal.WikiRealtimePriceClient.ItemPrices](#comoveduumnakalwikirealtimepriceclientitemprices)
- [com.oveduumnakal.WikiRealtimePriceClient.PricePoint](#comoveduumnakalwikirealtimepriceclientpricepoint)

---

## com.oveduumnakal.AcqCellRenderer

_class_

`class AcqCellRenderer`

Cell renderer for the acquisitions table, coloring the profit column, formatting
gp values, and marking estimated sell prices with a `~` prefix and tooltip.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `static final String` | `ESTIMATED_TOOLTIP` | Tooltip shown on sell prices that were estimated rather than observed. |
| `private final boolean` | `expanded` |  |
| `private final IntSupplier` | `hoverCol` |  |
| `private final IntSupplier` | `hoverRow` |  |
| `private final boolean` | `profit` |  |
| `private final IntPredicate` | `sellEstimated` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `AcqCellRenderer(boolean profit, boolean expanded, IntSupplier hoverRow, IntSupplier hoverCol)` |  |
| `AcqCellRenderer(boolean profit, boolean expanded, IntSupplier hoverRow, IntSupplier hoverCol, IntPredicate sellEstimated)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public Component` | `getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)` |  |

### Field Detail

#### ESTIMATED_TOOLTIP

`static final String ESTIMATED_TOOLTIP`

Tooltip shown on sell prices that were estimated rather than observed.

#### expanded

`private final boolean expanded`

#### hoverCol

`private final IntSupplier hoverCol`

#### hoverRow

`private final IntSupplier hoverRow`

#### profit

`private final boolean profit`

#### sellEstimated

`private final IntPredicate sellEstimated`

### Constructor Detail

#### AcqCellRenderer

`AcqCellRenderer(boolean profit, boolean expanded, IntSupplier hoverRow, IntSupplier hoverCol)`

#### AcqCellRenderer

`AcqCellRenderer(boolean profit, boolean expanded, IntSupplier hoverRow, IntSupplier hoverCol, IntPredicate sellEstimated)`

### Method Detail

#### getTableCellRendererComponent

`public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)`

---

## com.oveduumnakal.AcquisitionCsvExporter

_class_

`public final class AcquisitionCsvExporter`

Renders the acquisition (lot) log of the tracked items to CSV: one row per
acquisition, with the realized profit filled in for closed lots and the buy/sell
provenance from source-attributed pricing (#64) — `Sold Estimated` flags
closes priced at the average rather than an observed sale (#71). The output is
RFC-4180-style (quotes doubled, fields with commas/quotes/newlines quoted) so it
opens cleanly in any spreadsheet.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final String` | `HEADER` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `AcquisitionCsvExporter()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `private static void` | `appendRow(StringBuilder sb, TrackedItem item, AcquisitionRecord record)` |  |
| `private static String` | `escape(String value)` | Quotes a field when it contains a comma, quote, or newline, doubling embedded quotes. |
| `public static String` | `toCsv(Collection<TrackedItem> items)` | Builds the CSV for `items` in the given order. |

### Field Detail

#### HEADER

`private static final String HEADER`

### Constructor Detail

#### AcquisitionCsvExporter

`private AcquisitionCsvExporter()`

### Method Detail

#### appendRow

`private static void appendRow(StringBuilder sb, TrackedItem item, AcquisitionRecord record)`

#### escape

`private static String escape(String value)`

Quotes a field when it contains a comma, quote, or newline, doubling embedded quotes.

#### toCsv

`public static String toCsv(Collection<TrackedItem> items)`

Builds the CSV for `items` in the given order.

- **Returns:** the CSV text including the header row; header-only when nothing has lots.

---

## com.oveduumnakal.AcquisitionRecord

_class_

`public class AcquisitionRecord`

A single buy (and optional matching sell) of a tracked item, forming one lot
of its cost basis.

<p>`quantity` units were bought at `boughtAt` gp each. While
`soldAt` is `null` the lot is still held and contributes to the
item's cost basis and unrealized profit; once set, the lot is realized at that
sale price. `source` records how the lot entered the collection and
`sellSource` how it left it; records persisted before sources existed
have `null`, which `#sourceOrUnknown()` and
`#sellSourceOrUnknown()` map to `AcquisitionSource#UNKNOWN` — the
safe legacy default the schema fixtures and snapshot guard.

<p>A sold lot whose sell source is `AcquisitionSource#UNKNOWN` was closed
at an estimated price (the current average) rather than an observed sale, and
is marked as such in the acquisitions table.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private long` | `boughtAt` |  |
| `private int` | `quantity` |  |
| `private AcquisitionSource` | `sellSource` |  |
| `private Long` | `soldAt` |  |
| `private AcquisitionSource` | `source` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `AcquisitionRecord(int quantity, long boughtAt, Long soldAt)` | Creates a lot with an unknown acquisition source (the legacy default). |
| `AcquisitionRecord(int quantity, long boughtAt, Long soldAt, AcquisitionSource source)` | Creates a lot with an explicit acquisition source. |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public boolean` | `isSellEstimated()` |  |
| `public AcquisitionSource` | `sellSourceOrUnknown()` |  |
| `public AcquisitionSource` | `sourceOrUnknown()` |  |

### Field Detail

#### boughtAt

`private long boughtAt`

#### quantity

`private int quantity`

#### sellSource

`private AcquisitionSource sellSource`

#### soldAt

`private Long soldAt`

#### source

`private AcquisitionSource source`

### Constructor Detail

#### AcquisitionRecord

`public AcquisitionRecord(int quantity, long boughtAt, Long soldAt)`

Creates a lot with an unknown acquisition source (the legacy default).

- **Parameter** `quantity` — the number of units bought
- **Parameter** `boughtAt` — the buy price per unit
- **Parameter** `soldAt` — the sell price per unit, or `null` while the lot is still held

#### AcquisitionRecord

`public AcquisitionRecord(int quantity, long boughtAt, Long soldAt, AcquisitionSource source)`

Creates a lot with an explicit acquisition source.

- **Parameter** `quantity` — the number of units bought
- **Parameter** `boughtAt` — the buy price per unit
- **Parameter** `soldAt` — the sell price per unit, or `null` while the lot is still held
- **Parameter** `source` — how the lot entered the collection

### Method Detail

#### isSellEstimated

`public boolean isSellEstimated()`

- **Returns:** whether this lot was closed at an estimated price rather than an observed sale.

#### sellSourceOrUnknown

`public AcquisitionSource sellSourceOrUnknown()`

- **Returns:** the lot's sell source, mapping the legacy `null` to `AcquisitionSource#UNKNOWN`.

#### sourceOrUnknown

`public AcquisitionSource sourceOrUnknown()`

- **Returns:** the lot's source, mapping the legacy `null` to `AcquisitionSource#UNKNOWN`.

---

## com.oveduumnakal.AcquisitionSource

_enum_

`enum AcquisitionSource`

How a quantity change entered or left the collection, determining how its
acquisition lots are priced. `#UNKNOWN` is the fallback for changes no
detector attributed (mobile/offline resync, unmatched deltas) and the safe
default for records persisted before sources existed.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `ALCHEMY` | Consumed by a High/Low Alchemy cast, priced at the coins received. |
| `BURNED` | Destroyed by processing (burnt food); the input closes as a loss at 0. |
| `CAST` | Burned to cast a spell. |
| `CONSUMED` | Used up — food eaten, ammo fired, a potion dose drunk. |
| `CRUSHED` | A gem destroyed by a failed cut into gem dust; the uncut gem closes as a loss at 0. |
| `DEATH` | Lost to or recovered after a death; lots suspend rather than close. |
| `DECANT` | Swapped between dose ids by decanting a potion; basis follows the liquid, so no profit is realized. |
| `DESTROYED` | Destroyed on use, gone for good — a cannonball fired, a chinchompa thrown. |
| `GATHER` | Gathered from the world via a skill (Hunter, Mining, Fishing, …), priced at 0. |
| `GE_TRADE` | A Grand Exchange offer fill, priced at the actual offer price. |
| `GROUND` | Picked up from or dropped on the ground, priced at 0. |
| `MANUAL` | Entered by hand in the acquisitions table. |
| `PLAYER_TRADE` | Exchanged in a player trade, priced by apportioning the trade's gp. |
| `PROCESSING` | Consumed or produced by processing, priced by transferred cost basis. |
| `REWARD` | Claimed from a reward/loot container (raids chest, clue casket, reward pool, …), priced at 0. |
| `SHOP` | Bought from or sold to an NPC shop, priced from the coins moved. |
| `THIEVING` | Stolen via Thieving (pickpockets, stalls, chests), priced at 0. |
| `UNKNOWN` | No detector claimed the change; priced by the fallback (Auto Add) policy. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `label` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `AcquisitionSource(String label)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public String` | `toString()` |  |

### Enum Constant Detail

#### ALCHEMY

`ALCHEMY`

Consumed by a High/Low Alchemy cast, priced at the coins received.

#### BURNED

`BURNED`

Destroyed by processing (burnt food); the input closes as a loss at 0.

#### CAST

`CAST`

Burned to cast a spell. Runes are the fuel of a cast rather than an ingredient of it, so they
close at 0 (their cost realizes as a loss) even when the spell produces an item — a superheated
bar carries the ore's basis, not the fire runes'.

#### CONSUMED

`CONSUMED`

Used up — food eaten, ammo fired, a potion dose drunk. A single-use consumable closes at 0
(its cost realizes as a loss); a dose change carries the basis onto the lower-dose id so no
profit is realized just for using a dose.

#### CRUSHED

`CRUSHED`

A gem destroyed by a failed cut into gem dust; the uncut gem closes as a loss at 0.

#### DEATH

`DEATH`

Lost to or recovered after a death; lots suspend rather than close.

#### DECANT

`DECANT`

Swapped between dose ids by decanting a potion; basis follows the liquid, so no profit is realized.

#### DESTROYED

`DESTROYED`

Destroyed on use, gone for good — a cannonball fired, a chinchompa thrown. The lot closes at 0
(its cost realizes as a loss). Recoverable ammo that lands on the target's tile does not book
here; it suspends on the ground path and un-suspends when picked back up (#234).

#### GATHER

`GATHER`

Gathered from the world via a skill (Hunter, Mining, Fishing, …), priced at 0.

#### GE_TRADE

`GE_TRADE`

A Grand Exchange offer fill, priced at the actual offer price.

#### GROUND

`GROUND`

Picked up from or dropped on the ground, priced at 0.

#### MANUAL

`MANUAL`

Entered by hand in the acquisitions table.

#### PLAYER_TRADE

`PLAYER_TRADE`

Exchanged in a player trade, priced by apportioning the trade's gp.

#### PROCESSING

`PROCESSING`

Consumed or produced by processing, priced by transferred cost basis.

#### REWARD

`REWARD`

Claimed from a reward/loot container (raids chest, clue casket, reward pool, …), priced at 0.

#### SHOP

`SHOP`

Bought from or sold to an NPC shop, priced from the coins moved.

#### THIEVING

`THIEVING`

Stolen via Thieving (pickpockets, stalls, chests), priced at 0.

#### UNKNOWN

`UNKNOWN`

No detector claimed the change; priced by the fallback (Auto Add) policy.

### Field Detail

#### label

`private final String label`

### Constructor Detail

#### AcquisitionSource

`AcquisitionSource(String label)`

### Method Detail

#### toString

`public String toString()`

---

## com.oveduumnakal.AcquisitionsTableModel

_class_

`class AcquisitionsTableModel`

Swing table model backing the editable acquisitions log: one row per
`AcquisitionRecord` with quantity, buy price, sell price, and derived
profit columns. Edits are written straight back to the item's records.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final String[]` | `COLS_FULL` |  |
| `private static final String[]` | `COLS_NO_PROFIT` |  |
| `static final String` | `SOURCE_COL` | Read-only text column showing each lot's acquisition source; only in the expanded view. |
| `static final String` | `SYMBOL_COL` | Read-only symbol column showing each lot's acquisition source; the compact view's trailing column. |
| `private final StockpileConfig` | `config` |  |
| `private final IntSupplier` | `detailItemId` |  |
| `private final boolean` | `expanded` |  |
| `private TrackedItem` | `item` |  |
| `private String[]` | `lastFiredCols` | The column set last announced via `fireTableStructureChanged`, to skip redundant resets. |
| `private final Consumer<Integer>` | `onAcquisitionsEdited` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `AcquisitionsTableModel(StockpileConfig config, Consumer<Integer> onAcquisitionsEdited, IntSupplier detailItemId, boolean expanded)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `private String[]` | `cols()` |  |
| `private AcquisitionSource` | `displaySource(AcquisitionRecord rec)` |  |
| `public int` | `getColumnCount()` |  |
| `public String` | `getColumnName(int c)` |  |
| `public int` | `getRowCount()` |  |
| `public Object` | `getValueAt(int r, int c)` |  |
| `public boolean` | `isCellEditable(int r, int c)` |  |
| `boolean` | `isSellEstimated(int row)` |  |
| `private boolean` | `isSourceColumn(int c)` |  |
| `boolean` | `isSymbolColumn(int c)` |  |
| `long` | `rowProfit(AcquisitionRecord rec)` |  |
| `void` | `setItem(TrackedItem item)` | Swaps the backing item, announcing a full structure reset only when the column set actually changed — a plain data change keeps the table's layout, column widths, and renderers, so refresh-in-place doesn't collapse the detail card. |
| `public void` | `setValueAt(Object value, int r, int c)` |  |
| `String` | `sourceLabelAt(int row)` |  |

### Field Detail

#### COLS_FULL

`private static final String[] COLS_FULL`

#### COLS_NO_PROFIT

`private static final String[] COLS_NO_PROFIT`

#### SOURCE_COL

`static final String SOURCE_COL`

Read-only text column showing each lot's acquisition source; only in the expanded view.

#### SYMBOL_COL

`static final String SYMBOL_COL`

Read-only symbol column showing each lot's acquisition source; the compact view's trailing column.

#### config

`private final StockpileConfig config`

#### detailItemId

`private final IntSupplier detailItemId`

#### expanded

`private final boolean expanded`

#### item

`private TrackedItem item`

#### lastFiredCols

`private String[] lastFiredCols`

The column set last announced via `fireTableStructureChanged`, to skip redundant resets.

#### onAcquisitionsEdited

`private final Consumer<Integer> onAcquisitionsEdited`

### Constructor Detail

#### AcquisitionsTableModel

`AcquisitionsTableModel(StockpileConfig config, Consumer<Integer> onAcquisitionsEdited, IntSupplier detailItemId, boolean expanded)`

### Method Detail

#### cols

`private String[] cols()`

- **Returns:** the active column set: the profit column only when configured, plus a trailing source
        column — a text label in the expanded view, a symbol in the compact view.

#### displaySource

`private AcquisitionSource displaySource(AcquisitionRecord rec)`

- **Returns:** the source shown for a lot: for a sold lot how it left the collection
        (the sell source — GE, Alchemy, Shop, …), otherwise how it entered

#### getColumnCount

`public int getColumnCount()`

#### getColumnName

`public String getColumnName(int c)`

#### getRowCount

`public int getRowCount()`

#### getValueAt

`public Object getValueAt(int r, int c)`

#### isCellEditable

`public boolean isCellEditable(int r, int c)`

#### isSellEstimated

`boolean isSellEstimated(int row)`

- **Returns:** whether the lot in `row` was closed at an estimated price rather than an observed sale.

#### isSourceColumn

`private boolean isSourceColumn(int c)`

- **Returns:** whether column `c` is the expanded view's read-only text source column.

#### isSymbolColumn

`boolean isSymbolColumn(int c)`

- **Returns:** whether column `c` is the compact view's read-only source-symbol column.

#### rowProfit

`long rowProfit(AcquisitionRecord rec)`

- **Returns:** a lot's realised profit, or its unrealised profit at the current low price while unsold.

#### setItem

`void setItem(TrackedItem item)`

Swaps the backing item, announcing a full structure reset only when the column
set actually changed — a plain data change keeps the table's layout, column
widths, and renderers, so refresh-in-place doesn't collapse the detail card.

#### setValueAt

`public void setValueAt(Object value, int r, int c)`

#### sourceLabelAt

`String sourceLabelAt(int row)`

- **Returns:** the source label for the lot in `row`, for the compact table's tooltip.

---

## com.oveduumnakal.BuySellBar

_class_

`final class BuySellBar`

Custom-painted horizontal bar split green (buy fraction, left) and red (sell fraction, right).

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final int` | `BAR_ARC` |  |
| `private static final Color` | `BAR_GREEN` |  |
| `private static final int` | `BAR_H` |  |
| `private static final Color` | `BAR_RED` |  |
| `private double` | `buyFraction` | Buy fraction 0..1, or negative for the "no data" state. |

### Constructor Summary

| Constructor | Description |
|---|---|
| `BuySellBar()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public Dimension` | `getMaximumSize()` |  |
| `protected void` | `paintComponent(Graphics g)` | Paints the bar, grey when no ratio is known. |
| `void` | `setRatio(double buyFraction)` |  |

### Field Detail

#### BAR_ARC

`private static final int BAR_ARC`

#### BAR_GREEN

`private static final Color BAR_GREEN`

#### BAR_H

`private static final int BAR_H`

#### BAR_RED

`private static final Color BAR_RED`

#### buyFraction

`private double buyFraction`

Buy fraction 0..1, or negative for the "no data" state.

### Constructor Detail

#### BuySellBar

`BuySellBar()`

### Method Detail

#### getMaximumSize

`public Dimension getMaximumSize()`

#### paintComponent

`protected void paintComponent(Graphics g)`

Paints the bar, grey when no ratio is known. The green/red split is clipped to
the rounded bar outline so both ends stay cleanly rounded.

#### setRatio

`void setRatio(double buyFraction)`

---

## com.oveduumnakal.CategoryActions

_interface_

`public interface CategoryActions`

The category management operations the panel invokes; implemented by the plugin.

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `String` | `autoCategorize(boolean includeCategorized)` | Auto-assigns tracked items to generated categories from the bundled wiki category snapshot. |
| `void` | `create(String name)` | Creates a new empty category named `name`. |
| `void` | `delete(String name)` | Deletes the category `name`, leaving its items uncategorized. |
| `void` | `rename(String oldName, String newName)` | Renames the category `oldName` to `newName`. |
| `void` | `reorder(String name, int targetIndex)` | Moves the category `name` to `targetIndex` in the display order. |
| `void` | `setItemCategory(int itemId, String category)` | Assigns `itemId` to `category` (or clears it when `category` is null). |

### Method Detail

#### autoCategorize

`String autoCategorize(boolean includeCategorized)`

Auto-assigns tracked items to generated categories from the bundled wiki
category snapshot.

- **Parameter** `includeCategorized` — when `true` also re-categorizes items already in a
                          category; otherwise only uncategorized items are touched
- **Returns:** a user-facing summary of how many items were categorized

#### create

`void create(String name)`

Creates a new empty category named `name`.

#### delete

`void delete(String name)`

Deletes the category `name`, leaving its items uncategorized.

#### rename

`void rename(String oldName, String newName)`

Renames the category `oldName` to `newName`.

#### reorder

`void reorder(String name, int targetIndex)`

Moves the category `name` to `targetIndex` in the display order.

#### setItemCategory

`void setItemCategory(int itemId, String category)`

Assigns `itemId` to `category` (or clears it when `category` is null).

---

## com.oveduumnakal.CategoryState

_class_

`public class CategoryState`

One user-defined tracked-item category: its display `name` and whether
its accordion group is currently `collapsed` in the panel. The ordered
list of these is the source of truth for category order, naming, and
collapsed state, persisted separately from the tracked items themselves.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `public static final String` | `FAVORITES_KEY` | Group key for the special "Favorites" pseudo-group (pinned above all categories). |
| `public static final String` | `UNCATEGORIZED_KEY` | Group key for the catch-all "Uncategorized" group (items with no category). |
| `private boolean` | `collapsed` |  |
| `private String` | `name` |  |

### Field Detail

#### FAVORITES_KEY

`public static final String FAVORITES_KEY`

Group key for the special "Favorites" pseudo-group (pinned above all categories).

#### UNCATEGORIZED_KEY

`public static final String UNCATEGORIZED_KEY`

Group key for the catch-all "Uncategorized" group (items with no category).

#### collapsed

`private boolean collapsed`

#### name

`private String name`

---

## com.oveduumnakal.Changelog

_class_

`public final class Changelog`

Parses the bundled `changelog.md` resource into an ordered list of
releases (newest first). Each release starts with a top-level `# <version> - <date>`
heading; everything up to the next such heading is that release's markdown body (a
Quick Overview, a Detailed Breakdown of features grouped by area, and Bug Fixes).
The parser is offline and deterministic; the newest entry's version is treated as the
plugin's current version at runtime, and `ChangelogGuardTest` enforces that it
matches `runelite-plugin.properties`.

### Nested Type Summary

| Type | Description |
|---|---|
| _class_ [`Release`](#comoveduumnakalchangelogrelease) | One release: its version, written-out date, and the raw markdown body beneath its heading. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final Pattern` | `HEADING` | A release heading: `# 1.4 - July 25 2026`. |
| `static final String` | `RESOURCE` | Resource path of the bundled changelog, relative to the classpath root. |
| `private final List<Release>` | `releases` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `Changelog(List<Release> releases)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public String` | `currentVersion()` |  |
| `public boolean` | `hasVersion(String version)` |  |
| `public static Changelog` | `load()` | Loads and parses the bundled changelog resource. |
| `public static Changelog` | `parse(String markdown)` | Parses changelog markdown into releases in document order (expected newest first). |
| `private static String` | `read(InputStream in) throws IOException` |  |
| `public List<Release>` | `releases()` |  |

### Field Detail

#### HEADING

`private static final Pattern HEADING`

A release heading: `# 1.4 - July 25 2026`. The `(?!#)` keeps it to a single
`#` so the body's `##`/`###`/`####` headings aren't release boundaries.

#### RESOURCE

`static final String RESOURCE`

Resource path of the bundled changelog, relative to the classpath root.

#### releases

`private final List<Release> releases`

### Constructor Detail

#### Changelog

`private Changelog(List<Release> releases)`

### Method Detail

#### currentVersion

`public String currentVersion()`

- **Returns:** the newest release's version, or `null` when the changelog is empty.

#### hasVersion

`public boolean hasVersion(String version)`

- **Returns:** whether a release with exactly `version` exists.

#### load

`public static Changelog load()`

Loads and parses the bundled changelog resource.

#### parse

`public static Changelog parse(String markdown)`

Parses changelog markdown into releases in document order (expected newest first).

#### read

`private static String read(InputStream in) throws IOException`

#### releases

`public List<Release> releases()`

- **Returns:** the releases, newest first (document order).

---

## com.oveduumnakal.Changelog.Release

_class_

`public static class Release`

One release: its version, written-out date, and the raw markdown body beneath its heading.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `String` | `body` |  |
| `String` | `date` |  |
| `String` | `version` |  |

### Field Detail

#### body

`String body`

#### date

`String date`

#### version

`String version`

---

## com.oveduumnakal.CostBasisLedger

_class_

`class CostBasisLedger`

The cost-basis / GE trade ledger (#255), extracted from `StockpilePlugin`. Owns the FIFO lot
engine that turns container quantity deltas into open and closed `AcquisitionRecord`s, plus
the GE-offer pricing, source-attributed suspensions, and buy-limit windows built on top of it.

<p>Mostly client-free: the logic operates on plain values and domain `TrackedItem`s, reaching
the live client/config/panel only through a `LedgerHost` seam. Detectors that still live in
the plugin feed it observed events through the `queue*`/`signal*`/`#claim` mutators
and drive its per-tick sweeps. That keeps the attribution and FIFO behaviour — which CI cannot
smoke-test — unit-testable in isolation.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final Duration` | `BUY_LIMIT_WINDOW` | The rolling GE buy-limit window length. |
| `private static final int` | `DEATH_LOSS_BATCH_GRACE_TICKS` | How many ticks past the first consumed death loss the same death may keep consuming. |
| `private static final int` | `DEATH_LOSS_WINDOW_TICKS` | How many ticks after a death removals still count as death losses (respawn wipe + lag). |
| `private static final Duration` | `GE_STATE_SAVE_INTERVAL` | How often at most the GE ledger/window are rewritten to config during activity. |
| `private static final int` | `GRAVE_RECOVERY_GRACE_TICKS` | Once an expired gravestone has been gone this many ticks, its remaining suspensions close as lost. |
| `private int` | `deathLossTick` | The tick the current death first consumed a loss, bounding it to its own batch; -1 when none. |
| `private int` | `deathTick` | The tick the local player died, opening the death-loss window; -1 when none (#70). |
| `private final Map<Integer,long[]>` | `geBuyLimits` | Per-item rolling buy-limit window: `{windowStartEpochSeconds, quantityBought`}. |
| `private final GeOfferTracker` | `geOfferTracker` | Derives discrete increments from the raw GE offer stream; see `GeOfferTracker`. |
| `private int` | `graveGoneTick` | The tick the observed gravestone vanished (collected or expired), pending the grace check; -1 when none. |
| `private boolean` | `graveSeen` | True once the player's gravestone has been observed active, so its later loss is a real transition. |
| `private final LedgerHost` | `host` |  |
| `private Instant` | `lastGeStateSave` |  |
| `private final Map<Integer,Long>` | `pendingConsumedOutput` | Per-output transferred basis of a drunk dose awaiting the lower-dose potion (#218). |
| `private final Map<Integer,Long>` | `pendingDecantOutput` | Per-output transferred basis of a decant awaiting its produced potions (#220). |
| `private final Map<Integer,Integer>` | `pendingGroundSuspend` | Units dropped on the floor this tick awaiting the container decrease that suspends them. |
| `private final Map<Integer,Integer>` | `pendingGroundUnsuspend` | Units re-picked-up from our own drops awaiting the container increase that un-suspends them. |
| `private final Map<Integer,Long>` | `pendingProcessingOutput` | Per-output transferred basis of a processing action awaiting its produced units (#69). |
| `private final Map<SuspensionSource,Map<Integer,Deque<long[]>>>` | `pendingRealize` | Realize-at-price settlements (GE sell fills, accepted trades) that outran their suspension, parked per `SuspensionSource` until the offer/trade removal lands and suspends the units, each chunk `{quantity, unitPrice`}. |
| `private final Map<Integer,Integer>` | `pendingSellSuspend` | Units of a just-placed GE sell awaiting the container decrease that suspends them. |
| `private final Map<Integer,Integer>` | `pendingSellUnsuspend` | Units of a cancelled GE sell awaiting the container increase that un-suspends them. |
| `private final Map<Integer,Integer>` | `pendingTradeSuspend` | Units offered into a player trade awaiting the container decrease that suspends them. |
| `private final Map<Integer,Integer>` | `pendingTradeUnsuspend` | Units withdrawn from a player trade awaiting the container increase that un-suspends them. |
| `private final StockpilePersistence` | `persistence` |  |
| `private int` | `potionDiscardTick` | The tick a potion was "Empty"-clicked (discarded) on; -1 when none (#232). |
| `private int` | `potionEmptiedCount` | How many empty vessels were freed on `#potionEmptiedTick`. |
| `private int` | `potionEmptiedTick` | The tick empty vessels were freed by finishing a potion/drink; -1 when none (#218). |
| `private int` | `pouchDepositTick` | The tick a fur/meat pouch was emptied to the bank on; -1 when none (#214). |
| `private int` | `pouchFillTick` | The tick a fur/meat pouch was "Fill"ed on; -1 when none (#214). |
| `private final SourceAttributionCore` | `sourceAttribution` | Matches detector claims to observed quantity deltas, and holds GE buy fills awaiting collection as durable claims (#180); see `SourceAttributionCore`. |

### Constructor Summary

| Constructor | Description |
|---|---|
| `CostBasisLedger(LedgerHost host, StockpilePersistence persistence)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `void` | `addOpenAcquisition(TrackedItem tracked, int qty, long boughtAt, AcquisitionSource source)` | Adds `qty` units to an item's held lots at `boughtAt` gp. |
| `void` | `addPotionEmptied(int count)` | Records `count` empty vessels freed by finishing a potion/drink this tick (#218). |
| `void` | `applyBuyLimitFields(TrackedItem item)` | Sets the item's transient buy-limit fields from its window, clearing them when the window has expired. |
| `void` | `applyDelta(TrackedItem tracked, int delta)` | Prices one item's net container delta. |
| `private SourceAttributionCore.Attribution` | `attributeDelta(int itemId, int quantity)` | Attributes a quantity change against the open detector claims, honouring the Source-Based Pricing kill switch: when disabled, everything is `AcquisitionSource#UNKNOWN` and priced by the classic fallbacks. |
| `void` | `claim(AcquisitionSource source, int itemId, int quantity, long unitPrice, int currentTick)` | Registers a detector's expectation that `quantity` units of `itemId` are about to change hands at `unitPrice` gp each; see `SourceAttributionCore#claim`. |
| `void` | `clearDecantAndConsumedOutput()` | Drops the queued decant and drunk-dose outputs before a detection pass recomputes them (#218, #220). |
| `void` | `clearProcessingOutput()` | Drops the queued processing outputs before a detection pass recomputes them (#69). |
| `void` | `closeAllGroundSuspensions()` | Closes every remaining ground suspension as lost — floor items rarely survive a logout — and drops the pending ground routing maps. |
| `void` | `closeFifo(TrackedItem tracked, int amount, long soldAtPrice, AcquisitionSource sellSource)` | Closes `amount` units of held inventory at `soldAtPrice`, oldest lot first (FIFO), recording `sellSource` as the sale's provenance — `AcquisitionSource#UNKNOWN` marks the price as an estimate rather than an observed sale. |
| `boolean` | `closeGroundLost(int itemId, int qty)` | Closes a ground pile's units as lost at 0 (#234): the floor items were never recovered, so their suspended lots close under `AcquisitionSource#GROUND`. |
| `void` | `closeVanishedGraveLosses()` | Once an expired gravestone has been gone for `#GRAVE_RECOVERY_GRACE_TICKS`, closes any death suspension it left standing as lost at 0 (#70). |
| `private int` | `consumeBuyLedger(TrackedItem tracked, int qty)` | Consumes up to `qty` from the item's GE buy ledger into priced lots, returning the unconsumed remainder. |
| `int` | `consumeCarriedOutput(TrackedItem tracked, int qty, Map<Integer,Long> carried, AcquisitionSource source)` | Opens `qty` newly-produced units carrying the basis queued in `carried` for this item, tagged with `source`. |
| `private int` | `consumeConsumedOutput(TrackedItem tracked, int qty)` | Opens the lower-dose potion left after a dose is drunk (#218), carrying the full basis of the higher-dose lot so using a dose realizes no profit or loss — the cost simply follows the liquid. |
| `private int` | `consumeDeathLoss(TrackedItem tracked, int qty)` | Suspends removals in the post-death window (#70): the units were lost to the death, so quantities drop but the lots stay open pending gravestone/Death's Office recovery. |
| `private int` | `consumeDeathUnsuspend(TrackedItem tracked, int qty)` | Greedily restores an addition from death suspension — a recovery reactivates the suspended lots with their basis intact, opening nothing new. |
| `private int` | `consumeDecantOutput(TrackedItem tracked, int qty)` | Opens the output lot(s) of a decant (#220), carrying the combined dose-weighted input basis so the swapped potion keeps its cost — no profit is realized on the swap. |
| `private int` | `consumeEmptyContainerByproduct(TrackedItem tracked, int qty)` | Opens the empty vessel(s) freed by finishing a potion or drink this tick (#218) at 0 — a leftover byproduct, not a purchase. |
| `private int` | `consumeFiredAmmoRecovery(TrackedItem tracked, int qty)` | Restores picked-up ammo from ground suspension (#234): a fired-ammo lot lands on the target's tile with no `TileItem` of ours to key off, so its recovery can't route through `#consumeGroundUnsuspend`. |
| `private int` | `consumeGroundSuspend(TrackedItem tracked, int qty)` | Moves up to this tick's correlated drop quantity of a removal into ground suspension — the units left the containers but sit on the floor, still owned, lots untouched. |
| `private int` | `consumeGroundUnsuspend(TrackedItem tracked, int qty)` | Restores an addition from ground suspension, but only up to what an actual re-pickup of one of our dropped `TileItem`s queued — so a same-item pickup from an unrelated source (a monster drop while our own is on the floor) can't cancel the suspension and instead gets its own 0-cost ground lot. |
| `private int` | `consumePouchSuspend(TrackedItem tracked, int qty)` | Moves a removal into fur/meat-pouch suspension when it lands on the tick the pouch was "Fill"ed — the units left the inventory into the pouch but stay owned, lots (and their original source/basis) intact, until the pouch is emptied. |
| `private int` | `consumePouchUnsuspend(TrackedItem tracked, int qty)` | Restores an addition from fur/meat-pouch suspension on an empty-to-bank tick, up to what was filled in — those units re-enter tracked containers as the net no-op that reopens no lot, keeping their original source and basis. |
| `private int` | `consumeProcessingOutput(TrackedItem tracked, int qty)` | Opens the output lot(s) of a processing action (#69), carrying the transferred input basis so their cost sums exactly to it. |
| `private int` | `consumeSellSuspend(TrackedItem tracked, int qty)` | Suspends up to `qty` units for a just-placed GE sell (no close), returning the unconsumed remainder. |
| `private int` | `consumeSellUnsuspend(TrackedItem tracked, int qty)` | Restores up to `qty` cancelled-sell units to held (un-suspends), returning the unconsumed remainder. |
| `private int` | `consumeTradeSuspend(TrackedItem tracked, int qty)` | Moves up to `qty` of a removal into trade suspension — the units were placed into a player-trade offer, so they left the containers but stay owned with their lots intact until the trade finalizes or is withdrawn. |
| `private int` | `consumeTradeUnsuspend(TrackedItem tracked, int qty)` | Restores an addition from trade suspension — an offered item withdrawn from the trade returns to the inventory, a net no-op that opens no new lot. |
| `void` | `expireClaims(int currentTick)` | Discards expired detector claims; call once per tick. |
| `void` | `expireSuspensions()` | Closes every suspension that outlived its source's `SuspensionSource#expiry()` as an unrecovered loss at 0 gp, booked under that source's `SuspensionSource#closeSource()`. |
| `long` | `fallbackPrice(TrackedItem tracked)` |  |
| `void` | `flushPendingRealize()` | Closes any settled sale that outran its suspension, now that the offer/trade removal has moved the units into their `SuspensionSource` suspension. |
| `private boolean` | `flushRealizeSource(SuspensionSource source, Map<Integer,Deque<long[]>> queues)` | Drains one source's parked settlements against its now-landed suspensions, dropping emptied queues. |
| `private void` | `handleGeEvent(GeOfferTracker.Event e)` | Applies one derived GE event: ledger a buy, suspend/realize/restore a sell, and record the buy limit. |
| `boolean` | `hasDecantOrConsumedOutput(int itemId)` |  |
| `boolean` | `hasProcessingOutput(int itemId)` |  |
| `private boolean` | `isPotionDiscardTick()` |  |
| `private boolean` | `isPouchDepositTick()` |  |
| `void` | `load()` | Restores the GE buy ledger (as durable claims) and buy-limit windows from the RS profile config, defaulting to empty. |
| `private boolean` | `mergeClosed(List<AcquisitionRecord> records, int qty, long boughtAt, long soldAtPrice, AcquisitionSource sellSource)` | Merges `qty` into an existing closed (sold) lot with the same bought/sold prices and sell provenance, to avoid fragmenting the log. |
| `void` | `onGeOffer(int slot, int itemId, boolean buying, boolean cancelled, boolean empty, int totalQuantity, int quantitySold, long spent)` | Drives the GE offer tracker with one raw offer event and applies each discrete increment it derives (placement, fill, or cancellation); see `GeOfferTracker#onOffer`. |
| `void` | `onGravestoneVisibility(boolean present, boolean durationExpired)` | Records the local player's gravestone visibility (#70). |
| `int` | `pendingGroundSuspend(int itemId)` |  |
| `void` | `persist()` | Persists the GE buy ledger (the durable claims) and buy-limit windows to the RS profile config. |
| `int` | `potionEmptiedTick()` |  |
| `void` | `primeGeStateFromLogin()` | Post-login GE reconciliation, run for each offer event inside the login window (when the offers array is finally populated, unlike at container sync). |
| `void` | `queueConsumedOutput(int itemId, long totalCost)` | Queues `totalCost` transferred basis for a drunk-dose output `itemId` (#218). |
| `void` | `queueDecantOutput(int itemId, long totalCost)` | Queues `totalCost` transferred basis for a decant output `itemId` (#220). |
| `void` | `queueGroundSuspend(int itemId, int qty)` | Queues `qty` units of `itemId` for ground suspension (a correlated own-drop). |
| `void` | `queueGroundUnsuspend(int itemId, int qty)` | Queues `qty` units of `itemId` for ground un-suspension (a correlated re-pickup). |
| `void` | `queueProcessingOutput(int itemId, long totalCost)` | Queues `totalCost` transferred basis for a processing output `itemId` (#69). |
| `void` | `queueTradeSuspend(int itemId, int qty)` | Queues `qty` units of `itemId` for trade suspension (offered into a player trade). |
| `void` | `queueTradeUnsuspend(int itemId, int qty)` | Queues `qty` units of `itemId` for trade un-suspension (withdrawn from a trade). |
| `private void` | `realize(SuspensionSource source, int itemId, int qty, long unitPrice)` | Closes `qty` suspended units of a settled sale at its realized `unitPrice`, booking the source's `SuspensionSource#realizeSource()`. |
| `private int` | `realizeOpenLots(List<AcquisitionRecord> records, int remaining, long soldAtPrice, AcquisitionSource sellSource, AcquisitionSource onlySource)` | Realizes up to `remaining` units across the open lots oldest-first, closing (or splitting) each at `soldAtPrice` with `sellSource` and merging into a matching closed lot where possible. |
| `private void` | `realizeSell(int itemId, int qty, long unitPrice)` | Realizes a completed GE sell fill against its SELL suspension, then debounces a GE-state save. |
| `void` | `realizeTradeSale(int itemId, int qty, long unitPrice)` | Realizes a completed player trade against its `SuspensionSource#TRADE` suspension — the same shortfall-parking race fix the GE sell path carries (#175), so a same-tick offer+accept that outruns the offer's inventory decrease no longer drops the sale. |
| `void` | `reconcileSuspendedFromOffers()` | Rewrites `suspendedQuantity` from the live open sell offers plus the pending cancelled-sell returns (units cancelled but not yet collected, which are still the player's), so offline fills or cancels self-heal at login; released units are then re-priced by the caller's reconcile. |
| `private void` | `recordBuyLimit(int itemId, int qty)` | Accumulates a GE purchase into the item's rolling buy-limit window, rolling the window over when it expires. |
| `void` | `resetForLogin()` | Clears the transient session ledger state carried at login (matches the pre-#255 login reset). |
| `void` | `resetForShutdown()` | Clears the transient session ledger state at shutdown (matches the pre-#255 shutdown reset). |
| `void` | `resetPotionEmptied()` | Resets the running count of empty vessels freed this tick (a fresh detection pass). |
| `private void` | `scheduleGeStateSave()` | Persists the GE state at most once per `#GE_STATE_SAVE_INTERVAL`. |
| `private void` | `seedCancelledSellReturns(GrandExchangeOffer[] offers)` | Queues the uncollected remainder of every cancelled sell offer as a pending un-suspend, so those units stay suspended (they are still the player's, sitting in the collection box) and collecting them restores the original lots instead of opening fresh ones. |
| `void` | `signalDeath()` | Marks the local player's death, opening the death-loss suspension window (#70). |
| `void` | `signalPotionDiscard()` | Marks that a potion was "Empty"-clicked (discarded) this tick (#232). |
| `void` | `signalPouchDeposit()` | Marks that a fur/meat pouch was emptied to the bank this tick (#214). |
| `void` | `signalPouchFill()` | Marks that a fur/meat pouch was "Fill"ed this tick (#214). |
| `private void` | `suspendFiredAmmo(TrackedItem tracked, int qty)` | Suspends fired recoverable ammo on the ground path (#234): the units left the ammo slot but landed on the target's tile, still owned with their basis intact. |

### Field Detail

#### BUY_LIMIT_WINDOW

`private static final Duration BUY_LIMIT_WINDOW`

The rolling GE buy-limit window length.

#### DEATH_LOSS_BATCH_GRACE_TICKS

`private static final int DEATH_LOSS_BATCH_GRACE_TICKS`

How many ticks past the first consumed death loss the same death may keep consuming.

#### DEATH_LOSS_WINDOW_TICKS

`private static final int DEATH_LOSS_WINDOW_TICKS`

How many ticks after a death removals still count as death losses (respawn wipe + lag).

#### GE_STATE_SAVE_INTERVAL

`private static final Duration GE_STATE_SAVE_INTERVAL`

How often at most the GE ledger/window are rewritten to config during activity.

#### GRAVE_RECOVERY_GRACE_TICKS

`private static final int GRAVE_RECOVERY_GRACE_TICKS`

Once an expired gravestone has been gone this many ticks, its remaining suspensions close as lost.

#### deathLossTick

`private int deathLossTick`

The tick the current death first consumed a loss, bounding it to its own batch; -1 when none.

#### deathTick

`private int deathTick`

The tick the local player died, opening the death-loss window; -1 when none (#70).

#### geBuyLimits

`private final Map<Integer,long[]> geBuyLimits`

Per-item rolling buy-limit window: `{windowStartEpochSeconds, quantityBought`}. Persisted.

#### geOfferTracker

`private final GeOfferTracker geOfferTracker`

Derives discrete increments from the raw GE offer stream; see `GeOfferTracker`.

#### graveGoneTick

`private int graveGoneTick`

The tick the observed gravestone vanished (collected or expired), pending the grace check; -1 when none.

#### graveSeen

`private boolean graveSeen`

True once the player's gravestone has been observed active, so its later loss is a real transition.

#### host

`private final LedgerHost host`

#### lastGeStateSave

`private Instant lastGeStateSave`

#### pendingConsumedOutput

`private final Map<Integer,Long> pendingConsumedOutput`

Per-output transferred basis of a drunk dose awaiting the lower-dose potion (#218).

#### pendingDecantOutput

`private final Map<Integer,Long> pendingDecantOutput`

Per-output transferred basis of a decant awaiting its produced potions (#220).

#### pendingGroundSuspend

`private final Map<Integer,Integer> pendingGroundSuspend`

Units dropped on the floor this tick awaiting the container decrease that suspends them.

#### pendingGroundUnsuspend

`private final Map<Integer,Integer> pendingGroundUnsuspend`

Units re-picked-up from our own drops awaiting the container increase that un-suspends them.

#### pendingProcessingOutput

`private final Map<Integer,Long> pendingProcessingOutput`

Per-output transferred basis of a processing action awaiting its produced units (#69).

#### pendingRealize

`private final Map<SuspensionSource,Map<Integer,Deque<long[]>>> pendingRealize`

Realize-at-price settlements (GE sell fills, accepted trades) that outran their suspension,
parked per `SuspensionSource` until the offer/trade removal lands and suspends the units,
each chunk `{quantity, unitPrice`}. Drained by `#flushPendingRealize()` every tick.

#### pendingSellSuspend

`private final Map<Integer,Integer> pendingSellSuspend`

Units of a just-placed GE sell awaiting the container decrease that suspends them.

#### pendingSellUnsuspend

`private final Map<Integer,Integer> pendingSellUnsuspend`

Units of a cancelled GE sell awaiting the container increase that un-suspends them.

#### pendingTradeSuspend

`private final Map<Integer,Integer> pendingTradeSuspend`

Units offered into a player trade awaiting the container decrease that suspends them.

#### pendingTradeUnsuspend

`private final Map<Integer,Integer> pendingTradeUnsuspend`

Units withdrawn from a player trade awaiting the container increase that un-suspends them.

#### persistence

`private final StockpilePersistence persistence`

#### potionDiscardTick

`private int potionDiscardTick`

The tick a potion was "Empty"-clicked (discarded) on; -1 when none (#232).

#### potionEmptiedCount

`private int potionEmptiedCount`

How many empty vessels were freed on `#potionEmptiedTick`.

#### potionEmptiedTick

`private int potionEmptiedTick`

The tick empty vessels were freed by finishing a potion/drink; -1 when none (#218).

#### pouchDepositTick

`private int pouchDepositTick`

The tick a fur/meat pouch was emptied to the bank on; -1 when none (#214).

#### pouchFillTick

`private int pouchFillTick`

The tick a fur/meat pouch was "Fill"ed on; -1 when none (#214).

#### sourceAttribution

`private final SourceAttributionCore sourceAttribution`

Matches detector claims to observed quantity deltas, and holds GE buy fills awaiting
collection as durable claims (#180); see `SourceAttributionCore`.

### Constructor Detail

#### CostBasisLedger

`CostBasisLedger(LedgerHost host, StockpilePersistence persistence)`

### Method Detail

#### addOpenAcquisition

`void addOpenAcquisition(TrackedItem tracked, int qty, long boughtAt, AcquisitionSource source)`

Adds `qty` units to an item's held lots at `boughtAt` gp.

<p>First it reverses any equal-and-opposite "wash" closes (a prior sell at
the same price, which a re-acquire should cancel), then merges into an
existing open lot at the same price, or appends a new lot.

#### addPotionEmptied

`void addPotionEmptied(int count)`

Records `count` empty vessels freed by finishing a potion/drink this tick (#218).

#### applyBuyLimitFields

`void applyBuyLimitFields(TrackedItem item)`

Sets the item's transient buy-limit fields from its window, clearing them when the window has expired.

#### applyDelta

`void applyDelta(TrackedItem tracked, int delta)`

Prices one item's net container delta. On a gain: restore trade/sell suspensions, then open
positively-detected same-tick production — processing and decant outputs, which carry transferred
cost basis — <em>before</em> draining the GE buy ledger, so a lingering buy for that same id can't
steal a decant/processing output (#220); then the remaining GE and suspension routing, the
source-attribution claim, and finally the classic fallback.

#### attributeDelta

`private SourceAttributionCore.Attribution attributeDelta(int itemId, int quantity)`

Attributes a quantity change against the open detector claims, honouring the
Source-Based Pricing kill switch: when disabled, everything is
`AcquisitionSource#UNKNOWN` and priced by the classic fallbacks.

#### claim

`void claim(AcquisitionSource source, int itemId, int quantity, long unitPrice, int currentTick)`

Registers a detector's expectation that `quantity` units of `itemId` are about to
change hands at `unitPrice` gp each; see `SourceAttributionCore#claim`.

#### clearDecantAndConsumedOutput

`void clearDecantAndConsumedOutput()`

Drops the queued decant and drunk-dose outputs before a detection pass recomputes them (#218, #220).

#### clearProcessingOutput

`void clearProcessingOutput()`

Drops the queued processing outputs before a detection pass recomputes them (#69).

#### closeAllGroundSuspensions

`void closeAllGroundSuspensions()`

Closes every remaining ground suspension as lost — floor items rarely survive a logout — and
drops the pending ground routing maps. Persists and refreshes once after the sweep (#185). The
caller clears its own drop tracking.

#### closeFifo

`void closeFifo(TrackedItem tracked, int amount, long soldAtPrice, AcquisitionSource sellSource)`

Closes `amount` units of held inventory at `soldAtPrice`,
oldest lot first (FIFO), recording `sellSource` as the sale's
provenance — `AcquisitionSource#UNKNOWN` marks the price as an
estimate rather than an observed sale.

<p>It first cancels any just-added open lots bought at the same price (a
buy immediately followed by a sell nets out), then realizes the remaining
amount across the oldest open lots, splitting a lot when only part of it is
sold and merging into matching closed lots where possible.

#### closeGroundLost

`boolean closeGroundLost(int itemId, int qty)`

Closes a ground pile's units as lost at 0 (#234): the floor items were never recovered, so their
suspended lots close under `AcquisitionSource#GROUND`. Bounded to what is actually suspended.
Mutates only &mdash; the caller persists and refreshes once (#185), so several piles expiring in one
tick don't each re-serialize the whole item list.

- **Returns:** whether any units were closed

#### closeVanishedGraveLosses

`void closeVanishedGraveLosses()`

Once an expired gravestone has been gone for `#GRAVE_RECOVERY_GRACE_TICKS`,
closes any death suspension it left standing as lost at 0 (#70). The grace absorbs a
last-tick collection whose items are still landing; anything still suspended after it
is a genuine loss, so the collection log reflects it the moment the grave expires
rather than after the blunt `SuspensionSource#DEATH death` expiry fallback.

#### consumeBuyLedger

`private int consumeBuyLedger(TrackedItem tracked, int qty)`

Consumes up to `qty` from the item's GE buy ledger into priced lots, returning
the unconsumed remainder.

#### consumeCarriedOutput

`int consumeCarriedOutput(TrackedItem tracked, int qty, Map<Integer,Long> carried, AcquisitionSource source)`

Opens `qty` newly-produced units carrying the basis queued in `carried` for this
item, tagged with `source`. An uneven split gives the remainder units one extra gp each
— 13 gp across 60 units becomes 13 units at 1 gp plus 47 at 0 gp — since a single integer
per-unit price can't hold a sub-gp basis. Consumes the whole addition (returns 0) so it
bypasses the fallback auto-add.

#### consumeConsumedOutput

`private int consumeConsumedOutput(TrackedItem tracked, int qty)`

Opens the lower-dose potion left after a dose is drunk (#218), carrying the full basis of the
higher-dose lot so using a dose realizes no profit or loss — the cost simply follows the liquid.

#### consumeDeathLoss

`private int consumeDeathLoss(TrackedItem tracked, int qty)`

Suspends removals in the post-death window (#70): the units were lost to the
death, so quantities drop but the lots stay open pending gravestone/Death's
Office recovery. Consumption is bounded to the death's own container batch —
the first tick that consumes, plus a one-tick grace for a split
inventory/equipment sync — so ordinary removals later in the window (eating
after respawning, dropping an item) close normally instead of being misbooked
as 0-gp death losses. The suspension timestamp is only set when none exists,
so a second death can't reset the first's recovery-expiry clock. Returns the
unconsumed remainder (0 when consumed).

#### consumeDeathUnsuspend

`private int consumeDeathUnsuspend(TrackedItem tracked, int qty)`

Greedily restores an addition from death suspension — a recovery reactivates
the suspended lots with their basis intact, opening nothing new. Returns the
unconsumed remainder.

#### consumeDecantOutput

`private int consumeDecantOutput(TrackedItem tracked, int qty)`

Opens the output lot(s) of a decant (#220), carrying the combined dose-weighted input
basis so the swapped potion keeps its cost — no profit is realized on the swap.

#### consumeEmptyContainerByproduct

`private int consumeEmptyContainerByproduct(TrackedItem tracked, int qty)`

Opens the empty vessel(s) freed by finishing a potion or drink this tick (#218) at 0 — a
leftover byproduct, not a purchase. Bounded to the number of vessels emptied, so any vials bought
separately still price normally. The source matches the event: `AcquisitionSource#GROUND`
when the potion was discarded via "Empty" (#232), so the whole drop sits under one glyph, else
`AcquisitionSource#CONSUMED` for a drunk-dry potion. Returns the remainder.

#### consumeFiredAmmoRecovery

`private int consumeFiredAmmoRecovery(TrackedItem tracked, int qty)`

Restores picked-up ammo from ground suspension (#234): a fired-ammo lot lands on the target's tile
with no `TileItem` of ours to key off, so its recovery can't route through
`#consumeGroundUnsuspend`. When a gain of recoverable ammo finds units still suspended on the
ground, it un-suspends them at their original basis — the net no-op that opens no new lot — instead of
the phantom 0-gp `AcquisitionSource#GROUND` re-buy that would otherwise collapse the stack's cost
basis. Runs after `#consumeGroundUnsuspend` so a hand-dropped stack resolves through its own
`TileItem` first. Returns the unconsumed remainder.

#### consumeGroundSuspend

`private int consumeGroundSuspend(TrackedItem tracked, int qty)`

Moves up to this tick's correlated drop quantity of a removal into ground
suspension — the units left the containers but sit on the floor, still owned,
lots untouched. Returns the unconsumed remainder.

#### consumeGroundUnsuspend

`private int consumeGroundUnsuspend(TrackedItem tracked, int qty)`

Restores an addition from ground suspension, but only up to what an actual
re-pickup of one of our dropped `TileItem`s queued — so a same-item pickup
from an unrelated source (a monster drop while our own is on the floor) can't
cancel the suspension and instead gets its own 0-cost ground lot. A re-pickup of
our drop is the net no-op that opens no new lot. Returns the unconsumed remainder.

#### consumePouchSuspend

`private int consumePouchSuspend(TrackedItem tracked, int qty)`

Moves a removal into fur/meat-pouch suspension when it lands on the tick the pouch was
"Fill"ed — the units left the inventory into the pouch but stay owned, lots (and their
original source/basis) intact, until the pouch is emptied. Consumes the whole removal,
since a Fill click's only container effect is the furs/meats leaving the inventory.
Returns the unconsumed remainder (0 while the fill tick is live) (#214).

#### consumePouchUnsuspend

`private int consumePouchUnsuspend(TrackedItem tracked, int qty)`

Restores an addition from fur/meat-pouch suspension on an empty-to-bank tick, up to what
was filled in — those units re-enter tracked containers as the net no-op that reopens no
lot, keeping their original source and basis. Any surplus beyond the suspended amount is
left for the caller to book as freshly-gathered `GATHER`. Returns the unconsumed
remainder (#214).

#### consumeProcessingOutput

`private int consumeProcessingOutput(TrackedItem tracked, int qty)`

Opens the output lot(s) of a processing action (#69), carrying the transferred
input basis so their cost sums <em>exactly</em> to it.

#### consumeSellSuspend

`private int consumeSellSuspend(TrackedItem tracked, int qty)`

Suspends up to `qty` units for a just-placed GE sell (no close), returning the unconsumed remainder.

#### consumeSellUnsuspend

`private int consumeSellUnsuspend(TrackedItem tracked, int qty)`

Restores up to `qty` cancelled-sell units to held (un-suspends), returning the unconsumed remainder.

#### consumeTradeSuspend

`private int consumeTradeSuspend(TrackedItem tracked, int qty)`

Moves up to `qty` of a removal into trade suspension — the units were placed into a
player-trade offer, so they left the containers but stay owned with their lots intact until
the trade finalizes or is withdrawn. Returns the unconsumed remainder.

#### consumeTradeUnsuspend

`private int consumeTradeUnsuspend(TrackedItem tracked, int qty)`

Restores an addition from trade suspension — an offered item withdrawn from the trade
returns to the inventory, a net no-op that opens no new lot. Bounded by both the queued
withdrawal and the units actually suspended. Returns the unconsumed remainder.

#### expireClaims

`void expireClaims(int currentTick)`

Discards expired detector claims; call once per tick.

#### expireSuspensions

`void expireSuspensions()`

Closes every suspension that outlived its source's `SuspensionSource#expiry()` as an
unrecovered loss at 0 gp, booked under that source's `SuspensionSource#closeSource()`.
One sweep now covers ground drops and death losses alike (#179); the gravestone-grace fast
path that closes a death sooner stays in `#closeVanishedGraveLosses()`.

#### fallbackPrice

`long fallbackPrice(TrackedItem tracked)`

- **Returns:** the cost-basis price to seed an unknown-source change with (an auto-add or any
delta no detector observed), per the configured `FallbackPricing`.

#### flushPendingRealize

`void flushPendingRealize()`

Closes any settled sale that outran its suspension, now that the offer/trade removal has moved the
units into their `SuspensionSource` suspension. Runs each tick after the container sync;
unmatched settlements stay parked and retry on a later tick.

#### flushRealizeSource

`private boolean flushRealizeSource(SuspensionSource source, Map<Integer,Deque<long[]>> queues)`

Drains one source's parked settlements against its now-landed suspensions, dropping emptied queues.

- **Returns:** whether any parked units were realized (a lot closed)

#### handleGeEvent

`private void handleGeEvent(GeOfferTracker.Event e)`

Applies one derived GE event: ledger a buy, suspend/realize/restore a sell, and record
the buy limit. With Source-Based Pricing off, no new pricing state is created — buys
aren't ledgered (their additions price classically) and placements don't suspend (their
removals close classically at the average price) — while fills and cancels still drain
suspensions taken while the toggle was on, so nothing is stranded. Buy-limit tracking
is informational, not pricing, and stays on either way.

#### hasDecantOrConsumedOutput

`boolean hasDecantOrConsumedOutput(int itemId)`

- **Returns:** whether a decant or drunk-dose output is already queued for `itemId`.

#### hasProcessingOutput

`boolean hasProcessingOutput(int itemId)`

- **Returns:** whether a processing output is already queued for `itemId`.

#### isPotionDiscardTick

`private boolean isPotionDiscardTick()`

- **Returns:** whether a potion was "Empty"-clicked on (or one tick before) this tick, discarding it (#232).

#### isPouchDepositTick

`private boolean isPouchDepositTick()`

- **Returns:** whether a fur/meat pouch was emptied to the bank on (or one tick before) this tick (#214).

#### load

`void load()`

Restores the GE buy ledger (as durable claims) and buy-limit windows from the RS profile
config, defaulting to empty.

#### mergeClosed

`private boolean mergeClosed(List<AcquisitionRecord> records, int qty, long boughtAt, long soldAtPrice, AcquisitionSource sellSource)`

Merges `qty` into an existing closed (sold) lot with the same
bought/sold prices and sell provenance, to avoid fragmenting the log.

- **Returns:** `true` if a matching lot absorbed the quantity

#### onGeOffer

`void onGeOffer(int slot, int itemId, boolean buying, boolean cancelled, boolean empty, int totalQuantity, int quantitySold, long spent)`

Drives the GE offer tracker with one raw offer event and applies each discrete increment it
derives (placement, fill, or cancellation); see `GeOfferTracker#onOffer`.

#### onGravestoneVisibility

`void onGravestoneVisibility(boolean present, boolean durationExpired)`

Records the local player's gravestone visibility (#70). A grave that vanishes after its
duration ran out (`durationExpired`) has expired and its items are lost, so this arms the
grace check in `#closeVanishedGraveLosses()`. A grave that vanishes with time still on the
clock was collected — its returning items un-suspend themselves, so no loss is armed.

#### pendingGroundSuspend

`int pendingGroundSuspend(int itemId)`

- **Returns:** the units currently queued for ground suspension for `itemId`.

#### persist

`void persist()`

Persists the GE buy ledger (the durable claims) and buy-limit windows to the RS profile config.

#### potionEmptiedTick

`int potionEmptiedTick()`

- **Returns:** the tick empty vessels were last freed on, or -1 when none — for the combine detector's guard.

#### primeGeStateFromLogin

`void primeGeStateFromLogin()`

Post-login GE reconciliation, run for each offer event inside the login window (when the offers
array is finally populated, unlike at container sync). Seeds the offer tracker's baselines from
the live offers so an offer that already existed at login is not replayed as a fresh placement or
fill, drops the stale session sell-routing maps, and rebuilds `suspendedQuantity` from those
offers so a later cancel un-suspends correctly instead of logging a phantom acquisition.
Idempotent, so repeating it as the array fills in is safe.

#### queueConsumedOutput

`void queueConsumedOutput(int itemId, long totalCost)`

Queues `totalCost` transferred basis for a drunk-dose output `itemId` (#218).

#### queueDecantOutput

`void queueDecantOutput(int itemId, long totalCost)`

Queues `totalCost` transferred basis for a decant output `itemId` (#220).

#### queueGroundSuspend

`void queueGroundSuspend(int itemId, int qty)`

Queues `qty` units of `itemId` for ground suspension (a correlated own-drop).

#### queueGroundUnsuspend

`void queueGroundUnsuspend(int itemId, int qty)`

Queues `qty` units of `itemId` for ground un-suspension (a correlated re-pickup).

#### queueProcessingOutput

`void queueProcessingOutput(int itemId, long totalCost)`

Queues `totalCost` transferred basis for a processing output `itemId` (#69).

#### queueTradeSuspend

`void queueTradeSuspend(int itemId, int qty)`

Queues `qty` units of `itemId` for trade suspension (offered into a player trade).

#### queueTradeUnsuspend

`void queueTradeUnsuspend(int itemId, int qty)`

Queues `qty` units of `itemId` for trade un-suspension (withdrawn from a trade).

#### realize

`private void realize(SuspensionSource source, int itemId, int qty, long unitPrice)`

Closes `qty` suspended units of a settled sale at its realized `unitPrice`, booking the
source's `SuspensionSource#realizeSource()`. Any part whose suspension has not yet landed (the
settlement event outran the container removal) is parked and retried by `#flushPendingRealize()`.

#### realizeOpenLots

`private int realizeOpenLots(List<AcquisitionRecord> records, int remaining, long soldAtPrice, AcquisitionSource sellSource, AcquisitionSource onlySource)`

Realizes up to `remaining` units across the open lots oldest-first,
closing (or splitting) each at `soldAtPrice` with `sellSource` and
merging into a matching closed lot where possible. When `onlySource` is
non-null, only lots that entered from that source are eligible — so a sell
closes its own source's buys before any others (#137), with the caller running
a matched pass followed by an unrestricted one.

- **Returns:** the units still unrealized after this pass

#### realizeSell

`private void realizeSell(int itemId, int qty, long unitPrice)`

Realizes a completed GE sell fill against its SELL suspension, then debounces a GE-state save.

#### realizeTradeSale

`void realizeTradeSale(int itemId, int qty, long unitPrice)`

Realizes a completed player trade against its `SuspensionSource#TRADE` suspension —
the same shortfall-parking race fix the GE sell path carries (#175), so a same-tick offer+accept
that outruns the offer's inventory decrease no longer drops the sale.

#### reconcileSuspendedFromOffers

`void reconcileSuspendedFromOffers()`

Rewrites `suspendedQuantity` from the live open sell offers plus the pending
cancelled-sell returns (units cancelled but not yet collected, which are still the
player's), so offline fills or cancels self-heal at login; released units are then
re-priced by the caller's reconcile.

<p>With Source-Based Pricing off no offer suspends: placements made while off were
already closed classically (re-suspending them would double-count), and any leftover
suspension from while the toggle was on zeroes here, letting the reconcile close those
lots at the average price — the classic removal semantics the toggle promises.

#### recordBuyLimit

`private void recordBuyLimit(int itemId, int qty)`

Accumulates a GE purchase into the item's rolling buy-limit window, rolling the window over when it expires.

#### resetForLogin

`void resetForLogin()`

Clears the transient session ledger state carried at login (matches the pre-#255 login reset).

#### resetForShutdown

`void resetForShutdown()`

Clears the transient session ledger state at shutdown (matches the pre-#255 shutdown reset).

#### resetPotionEmptied

`void resetPotionEmptied()`

Resets the running count of empty vessels freed this tick (a fresh detection pass).

#### scheduleGeStateSave

`private void scheduleGeStateSave()`

Persists the GE state at most once per `#GE_STATE_SAVE_INTERVAL`.

#### seedCancelledSellReturns

`private void seedCancelledSellReturns(GrandExchangeOffer[] offers)`

Queues the uncollected remainder of every cancelled sell offer as a pending un-suspend,
so those units stay suspended (they are still the player's, sitting in the collection
box) and collecting them restores the original lots instead of opening fresh ones.
Runs after the login prime clears the pending maps, so re-priming stays idempotent.

#### signalDeath

`void signalDeath()`

Marks the local player's death, opening the death-loss suspension window (#70).

#### signalPotionDiscard

`void signalPotionDiscard()`

Marks that a potion was "Empty"-clicked (discarded) this tick (#232).

#### signalPouchDeposit

`void signalPouchDeposit()`

Marks that a fur/meat pouch was emptied to the bank this tick (#214).

#### signalPouchFill

`void signalPouchFill()`

Marks that a fur/meat pouch was "Fill"ed this tick (#214).

#### suspendFiredAmmo

`private void suspendFiredAmmo(TrackedItem tracked, int qty)`

Suspends fired recoverable ammo on the ground path (#234): the units left the ammo slot but landed
on the target's tile, still owned with their basis intact. They un-suspend when picked back up
(`#consumeFiredAmmoRecovery`), or close as a 0-gp `AcquisitionSource#GROUND` loss once the
suspension outlives the `SuspensionSource#GROUND ground` expiry (`#expireSuspensions`) — which also
covers the shots that broke on impact and were never really recoverable. Reuses the drop machinery's
suspension counter rather than a menu/animation hook, so an Ava's-device catch (no delta) is a no-op.

---

## com.oveduumnakal.DecantBasis

_class_

`final class DecantBasis`

Splits a decant's combined input cost across its produced dose lots (#220),
dose-weighted so basis follows the liquid: decanting a Potion(3)@100 and a
Potion(1)@50 into one Potion(4) carries the full 150gp onto the four-dose lot,
and an up-decant leaving a remainder (five doses → a Potion(4) + a Potion(1))
splits it 4:1. The allotments sum <em>exactly</em> to the input cost — a flooring
remainder is handed to the highest-dose output — so a decant realizes no profit
or loss. Client-free and unit-testable.

### Constructor Summary

| Constructor | Description |
|---|---|
| `DecantBasis()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `static Map<Integer,Long>` | `distribute(long totalBasis, List<int[]> outputs)` |  |

### Constructor Detail

#### DecantBasis

`private DecantBasis()`

### Method Detail

#### distribute

`static Map<Integer,Long> distribute(long totalBasis, List<int[]> outputs)`

- **Parameter** `totalBasis` — the summed cost of the consumed input lots
- **Parameter** `outputs` — each produced dose id paired with its total doses (`{id, doses`}), doses &gt; 0
- **Returns:** each output id mapped to its share of `totalBasis`, summing to it exactly

---

## com.oveduumnakal.DestroyedOutputSources

_class_

`final class DestroyedOutputSources`

The destroyed-output-id &rarr; `AcquisitionSource` mapping as data rather than control flow
(#182). When a processing recipe emits a single destroyed product (see
`isDestroyedProduct`), the flavour of loss its inputs are tagged with depends on which
product came out: a crushed gemstone is a `AcquisitionSource#CRUSHED`, anything else is a
generic `AcquisitionSource#BURNED`. Adding a future distinct-loss flavour (a new failed-craft
dust, an exploded vial, a broken tool) is a new table entry here, not another inline conditional in
`correlateProcessing`, and a second crushed-style output id can no longer silently mis-tag.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final Map<Integer,AcquisitionSource>` | `BY_OUTPUT_ID` | Output ids with a distinct loss flavour; anything absent falls back to `AcquisitionSource#BURNED`. |

### Constructor Summary

| Constructor | Description |
|---|---|
| `DestroyedOutputSources()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `static AcquisitionSource` | `sourceFor(int outputId)` |  |

### Field Detail

#### BY_OUTPUT_ID

`private static final Map<Integer,AcquisitionSource> BY_OUTPUT_ID`

Output ids with a distinct loss flavour; anything absent falls back to `AcquisitionSource#BURNED`.

### Constructor Detail

#### DestroyedOutputSources

`private DestroyedOutputSources()`

### Method Detail

#### sourceFor

`static AcquisitionSource sourceFor(int outputId)`

- **Parameter** `outputId` — the item id of the single destroyed product a recipe emitted
- **Returns:** the loss source that product's inputs should be tagged with, defaulting to
        `AcquisitionSource#BURNED` for any output not in the table

---

## com.oveduumnakal.DetailView

_class_

`public class DetailView`

The item detail view, extracted from `StockpilePanel` so a second live instance can exist
(the #109 dashboard window) (#110). Owns every detail widget, the build/populate/apply methods, and
the section pop-outs; reaches its host for shared services, edit callbacks, and Back navigation
through `DetailViewHost`. The component itself is a `CardLayout` flipping between the
populated detail card and a loading placeholder.

### Nested Type Summary

| Type | Description |
|---|---|
| _enum_ [`Layout`](#comoveduumnakaldetailviewlayout) | Section arrangement: the sidebar's vertical stack, or the #109 dashboard's two-column layout. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final String` | `CARD_CONTENT` |  |
| `private static final String` | `CARD_LOADING` |  |
| `private static final Color` | `COLOR_VOLUME` |  |
| `private static final int` | `DEFAULT_NOTIFICATION_ROWS` |  |
| `private static final Color` | `DESCRIPTION_COLOR` |  |
| `private static final NumberFormat` | `NUMBER_FORMAT` |  |
| `private static final Color` | `OVERVIEW_ROW_DIVIDER` |  |
| `private static final TimeWindow[]` | `OVERVIEW_WINDOWS` |  |
| `private static final int` | `PRESSURE_BALANCED_HIGH` |  |
| `private static final int` | `PRESSURE_BALANCED_LOW` |  |
| `private static final String` | `PRICES_BASE` |  |
| `private static final String` | `WIKI_BASE` |  |
| `private int` | `acqHoverCol` |  |
| `private int` | `acqHoverRow` |  |
| `private JButton` | `acqPopoutButton` |  |
| `private AcquisitionsTableModel` | `acqPopoutModel` |  |
| `private JScrollPane` | `acqPopoutScroll` |  |
| `private JTable` | `acqPopoutTable` |  |
| `private AcquisitionsTableModel` | `acquisitionsModel` |  |
| `private JScrollPane` | `acquisitionsScroll` |  |
| `private JPanel` | `acquisitionsSection` |  |
| `private JTable` | `acquisitionsTable` |  |
| `private final JLabel` | `alchEstProfit` |  |
| `private JPanel` | `alchEstProfitRow` |  |
| `private JPanel` | `alchInfoSection` |  |
| `private Set<TimeWindow>` | `appliedOverviewRows` |  |
| `private String` | `appliedSectionLayout` |  |
| `private int` | `boundItemId` |  |
| `private final PressureVolumeLabel` | `buyPressureLabel` |  |
| `private BuySellBar` | `buySellBar` |  |
| `private final CardLayout` | `cardLayout` |  |
| `private final JLabel` | `ccvAvg` |  |
| `private final JLabel` | `ccvHigh` |  |
| `private final JLabel` | `ccvLow` |  |
| `private final JLabel` | `ccvProfit` |  |
| `private final JLabel` | `ccvQuantity` |  |
| `private JPanel` | `ccvSection` |  |
| `private final StockpileConfig` | `config` |  |
| `private final JPanel` | `detailCard` |  |
| `private final JTextArea` | `detailDescriptionArea` |  |
| `private String` | `detailExamineText` |  |
| `private final JLabel` | `detailIconLabel` |  |
| `private boolean` | `detailItemTracked` |  |
| `private boolean` | `detailLoadTimedOut` |  |
| `private Timer` | `detailLoadTimeout` |  |
| `private final JPanel` | `detailLoadingCard` |  |
| `private final JLabel` | `detailNameLabel` |  |
| `private final JLabel` | `detailQtyLabel` |  |
| `private JPanel` | `detailSectionsHost` |  |
| `private final Spinner` | `detailSpinner` |  |
| `private final JButton` | `detailTrackBtn` |  |
| `private volatile boolean` | `editingNotifications` |  |
| `private PriceGraphPanel.LineSet` | `graphLineSet` |  |
| `private boolean` | `graphSmooth` |  |
| `private final JLabel` | `haProfit` |  |
| `private final JLabel` | `haValue` |  |
| `private final DetailViewHost` | `host` |  |
| `private final JLabel` | `icvAvg` |  |
| `private final JLabel` | `icvHigh` |  |
| `private final JLabel` | `icvLow` |  |
| `private final JLabel` | `icvVolume` |  |
| `private final ItemManager` | `itemManager` |  |
| `private JPanel` | `itemValuesSection` |  |
| `private final JLabel` | `laProfit` |  |
| `private final JLabel` | `laValue` |  |
| `private JPanel` | `linksSection` |  |
| `private JPanel` | `marketInfoSection` |  |
| `private final JLabel` | `miBuyLimit` |  |
| `private final JLabel` | `miGeTax` |  |
| `private final JLabel` | `miLastBought` |  |
| `private final JLabel` | `miLastSold` |  |
| `private final JLabel` | `miLiquidity` |  |
| `private final JLabel` | `miVolatility` |  |
| `private NotificationsTableModel` | `notificationsModel` |  |
| `private JPanel` | `notificationsSection` |  |
| `private JTable` | `notificationsTable` |  |
| `private final Consumer<Integer>` | `onAcquisitionsEdited` |  |
| `private final BiConsumer<Integer,TrackItemMode>` | `onAddItem` |  |
| `private final Consumer<Integer>` | `onClearAcquisitions` |  |
| `private final Consumer<Integer>` | `onNotificationsEdited` |  |
| `private final Consumer<Integer>` | `onRequestDetailData` |  |
| `private final Consumer<Integer>` | `onUntrackToPreview` |  |
| `private final List<PopoutHandle>` | `openPopouts` |  |
| `private JPanel` | `overviewGrid` |  |
| `private final Map<TimeWindow,JLabel[]>` | `overviewLabels` |  |
| `private final List<JLabel>` | `overviewWindowLabels` |  |
| `private final JLabel` | `pressureMarketLabel` |  |
| `private TrackedItem` | `previewItem` |  |
| `private PriceGraphPanel` | `priceGraph` |  |
| `private JPanel` | `priceGraphSection` |  |
| `private JPanel` | `priceOverviewSection` |  |
| `private PriceGraphPanel` | `pricePopoutGraph` |  |
| `private PriceRangeBar` | `priceRangeBar` |  |
| `private final JLabel` | `rangePositionLabel` |  |
| `private final PressureVolumeLabel` | `sellPressureLabel` |  |
| `private JPanel` | `topStack` |  |
| `private final Layout` | `viewLayout` |  |
| `private PriceGraphPanel` | `volumeGraph` |  |
| `private JPanel` | `volumeGraphSection` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `DetailView(DetailViewHost host, Layout viewLayout)` | Builds a detail view bound to a host. |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `private void` | `acqAddRow(JTable table, AcquisitionsTableModel model)` | Appends a new empty acquisition row to the table and scrolls it into view. |
| `private void` | `acqClean(AcquisitionsTableModel model)` | Consolidates the acquisitions log, merging like rows and dropping empty ones. |
| `private void` | `acqClear()` | Clears all acquisitions for the current item after confirmation, via the plugin callback. |
| `private void` | `acqRemoveSelected(JTable table, AcquisitionsTableModel model)` | Removes the selected acquisition rows and commits the change. |
| `private JButton` | `acqTextButton(String text, Color fg)` | Builds a small flat text button used by the acquisitions pop-out toolbar. |
| `private static String` | `acqTooltipLabel(int col)` |  |
| `private String` | `alchProfitTooltip(String label, long alchValue, long itemAvg, int fireQty)` | Builds the tooltip breaking down an alch-profit figure: alch value minus item cost and rune cost. |
| `private void` | `applyAcqRenderers(JTable table, AcquisitionsTableModel model, boolean expanded)` | Wires an acquisitions table's fonts, row height, per-column renderers/editors, and headers for either the compact sidebar table or the expanded pop-out table. |
| `private void` | `applyBuyLimit(TrackedItem item)` | Shows the Buy Limit cell as `used / total` when purchases have been tracked in the current window (with a reset-countdown tooltip), the plain total when untouched, or `N/A` when the item has no GE limit. |
| `private void` | `applyBuySellPressure(TrackedItem item)` | Computes the buy/sell volume split over the configured window and updates the pressure bar + labels. |
| `private void` | `applyDeltaPct(JLabel label, TrackedItem item, TimeWindow window)` | Sets a label to the signed percent change of the current price vs. |
| `private void` | `applyDetailCard()` | Shows either the spinner placeholder or the populated detail view for the currently open item, depending on whether its prices are still loading. |
| `private void` | `applyDetailSectionLayout()` | Reorders and shows/hides the detail sections to match the configured slot assignments. |
| `private void` | `applyExamineWrap()` | Sets the current examine text on the description area. |
| `private void` | `applyLiquidity(long vol24)` | Sets the market-info liquidity rating from the last 24h volume via `MarketClassifier`. |
| `private void` | `applyNotificationRenderers()` | Wires the per-column cell editors/renderers on the notifications table to the current metric's input type. |
| `private void` | `applyProfitLabel(JLabel label, long profit, boolean known)` | Sets a profit label to a signed, colored gp figure, or a placeholder when the profit is unknown. |
| `private void` | `applyRangePosition(long min, long max, long live)` | Sets the market-info "30-day range position" rating for where the live price sits within its month range. |
| `private void` | `applyTableRenderers()` | Applies the acquisitions renderers to the sidebar (compact) table. |
| `private void` | `applyTradeTime(JLabel label, long epochSeconds)` | Sets a label to an epoch-second trade time's relative age, with the absolute time as a tooltip. |
| `private void` | `applyVolatility(TrackedItem item)` | Sets the market-info volatility rating from the item's week series via `MarketClassifier`. |
| `private JPanel` | `buildAlchBlock()` | Builds the alch-info section (high/low alch values and the high-alch profit estimate). |
| `private Icon` | `buildBrushIcon()` | Loads the bundled `broom.png` scaled to a 12px icon for the clear-acquisitions button. |
| `private JPanel` | `buildCurrentValuesBlock(JLabel high, JLabel low, JLabel avg, JLabel fourth, JLabel profit)` | Builds the stacked current-values block (high/low/avg plus a fourth metric row), colouring each label by metric and appending a divider-topped profit row when a profit label is supplied. |
| `private void` | `buildDetailCard()` | Constructs the detail-view card once: the header, the scrollable body, and every detail section (current values, market info, charts, overview grid, alch, notifications, acquisitions log). |
| `private void` | `buildDetailLoadingCard()` | Fills `#detailLoadingCard` with a centered spinner and caption. |
| `private JPanel` | `buildDetailSection(String title, Component... contents)` | Builds a titled detail-view section containing the given components. |
| `private JLabel` | `buildDetailSectionTitle(String text, boolean withDivider)` | Builds a centred bold section title, optionally topped with a divider rule. |
| `private JComponent` | `buildDetailSectionTitleRow(String title, JButton popBtn)` | Builds a divider-topped section title row with the title centred between a strut matching the pop-out button's width (so the title stays optically centred) and the button itself. |
| `private JComponent` | `buildDetailSectionTitleRow(String title, Runnable onPopout)` | Builds a section title row with a pop-out button wired to the given action. |
| `private JPanel` | `buildDetailSectionWithPopout(String title, Runnable onPopout, Component... contents)` | Builds a titled detail-view section whose title row carries a pop-out button. |
| `private Icon` | `buildEyeIcon(int size)` | Loads the bundled `eye.png` scaled to a square icon for the view-only button. |
| `private Icon` | `buildLeftArrowIcon()` | Paints the small left-pointing triangle used by the detail view's Back button. |
| `private JButton` | `buildLinkButton(String text, String tooltip, Runnable onClick)` | Builds a detail-view link button that runs the given action when clicked. |
| `private JPanel` | `buildLinksBlock()` | Builds the Links detail section's content: Wiki and Live Prices buttons for the current item. |
| `private JPanel` | `buildMarketInfoBlock()` | Builds the market-info section (buy limit, GE value, volatility, liquidity, 30-day range, etc.). |
| `private JPanel` | `buildMarketInfoPair(String leftLabel, JLabel leftValue, String rightLabel, JLabel rightValue)` | Builds a two-column grid pairing two captioned values side by side (Market Info / alch rows). |
| `private void` | `buildNotificationsSection()` | Builds the notifications section: the rules table and its add/remove/edit controls. |
| `private JPanel` | `buildOverviewGrid()` | Builds (and remembers) the sidebar overview grid. |
| `private JButton` | `buildPopoutButton(Runnable onClick)` | Builds a borderless pop-out button that runs the given action when clicked. |
| `private Icon` | `buildPopoutIcon()` | Paints the small box-with-arrow "open in new window" icon used by pop-out buttons. |
| `private void` | `clearItemValue(JLabel label, String text)` | Resets a value label to plain text, dropping its tooltip and any hover-tint listener. |
| `private void` | `closePopouts()` | Disposes all open pop-out windows (e.g. |
| `private JPanel` | `createOverviewGrid(Map<TimeWindow,JLabel[]> labels, List<JLabel> windowLabels, int sepGap)` | Creates an overview grid panel that custom-paints its own dividers: a vertical rule after the window-label column and a horizontal rule between consecutive rows, both derived from the live label positions so they track layout changes. |
| `private TrackedItem` | `currentDetailItem()` |  |
| `private static String` | `expandedAcqHeader(String compact)` |  |
| `private void` | `fillOverviewGrid(JPanel grid, Map<TimeWindow,JLabel[]> labels, List<JLabel> windowLabels, Set<TimeWindow> rows, Font font, boolean expanded)` | Lays out the overview grid's header and one row of price/volume labels per selected time window. |
| `private static String` | `formatDuration(long seconds)` | Formats a positive second count as a compact `"2h 14m"` / `"43m"` / `"12s"` duration. |
| `private static String` | `fullWindowLabel(TimeWindow w)` |  |
| `private long` | `geTax(long avgPrice)` |  |
| `public int` | `getBoundItemId()` |  |
| `private void` | `installVolumeValue(JLabel label, long vol, boolean full)` | Sets a volume cell's compact/full text with a full-number tooltip and hover tint, or a placeholder. |
| `private boolean` | `isDetailLoading(TrackedItem item)` |  |
| `public boolean` | `isEditingNotifications()` |  |
| `public boolean` | `isLoadingVisible()` |  |
| `public boolean` | `isPreview()` |  |
| `private JPanel` | `newSectionWrapper()` |  |
| `private void` | `notifyNotificationsEdited()` | Notifies the plugin (via callback) that the current item's notification rules changed, so it can persist them. |
| `public void` | `onLeaveDetail()` | Clears the bound item and stops any in-flight loading/pop-outs when the host leaves the detail view. |
| `public boolean` | `onRebuild()` | Refreshes the bound item from the host's current tracked state on a list rebuild: clears a stale preview once the item is tracked, repopulates in place, and reports whether an item is still shown so the host can fall back to the main list when it has gone. |
| `private void` | `openCollectionLogPopout()` | Opens the editable acquisitions (collection log) table in a standalone pop-out window. |
| `private void` | `openGraphPopout(String title, PriceGraphPanel.Mode mode, PriceGraphPanel source)` | Opens an expanded chart pop-out mirroring (and kept in sync with) the in-panel graph. |
| `private void` | `openOverviewPopout()` | Opens the price overview grid in a standalone pop-out window. |
| `private void` | `openPricesLink()` | Opens the wiki realtime prices page for the item currently shown in the detail view. |
| `private void` | `openWikiLink()` | Opens the OSRS Wiki page for the item currently shown in the detail view. |
| `private static long` | `overviewMidpoint(WikiRealtimePriceClient.PricePoint p)` |  |
| `private void` | `populateDetail(TrackedItem item)` | Fills every detail section from an item's current state: header name/icon/quantity, item and collection values, overview grid, charts, market info (times, volatility, liquidity, range, pressure), alch figures, notifications, and the acquisitions log. |
| `private void` | `populateOverviewGrid(Set<TimeWindow> rows)` | (Re)creates the overview grid panels (sidebar and pop-out) for the given set of time-window rows. |
| `private void` | `populateOverviewLabels(Map<TimeWindow,JLabel[]> labels, TrackedItem item, boolean full)` | Fills the overview grid's cells with an item's per-window high/low/avg/volume/Δ% values. |
| `private void` | `preserveDetailScroll(Runnable refresh)` | Runs a refresh-in-place of the open detail card, keeping the enclosing scroll pane's vertical position. |
| `private void` | `rebuildOverviewGrid()` | Rebuilds the price overview grid to match the configured preset of time-window rows. |
| `public void` | `refreshDetailData(int itemId)` | Re-populates the open detail view with fresh data for `itemId` (no-op if a different item is shown). |
| `private void` | `refreshPopouts(TrackedItem item)` | Pushes fresh data for `item` into every open pop-out window. |
| `private void` | `scrollAcquisitionsToBottom()` | Scrolls the acquisitions log to its newest (bottom) entry once layout has settled. |
| `private void` | `setOverviewPlaceholder(JLabel label)` | Resets an overview cell to the `"-"` placeholder. |
| `private void` | `setPriceCell(JLabel label, long value, Color color, String tooltipLabel, Color tint, boolean full)` | Sets a price cell's text (full or abbreviated), color, tooltip, and hover tint, or a placeholder if unset. |
| `public void` | `show(int itemId)` | Switches to the detail card for an item, requesting its full data and populating the view. |
| `private void` | `showPopout(String title, JComponent content, Consumer<TrackedItem> refresher, Runnable onClose)` | Opens a non-modal pop-out window hosting `content`, registering its refresher so live updates reach it and running `onClose` when dismissed. |
| `public void` | `showPreview(TrackedItem item)` | Opens a read-only preview of an untracked item. |
| `private TrackedItem` | `shownDetailItem()` |  |
| `public TrackedItem` | `shownItem()` |  |
| `private static String` | `spelledInterval(TimeWindow window)` |  |
| `private void` | `stopDetailLoading()` | Stops the spinner animation and cancels the pending load-timeout, if any. |
| `public void` | `stopLoading()` | Stops the loading spinner and its safety timeout (used when the host is disposed). |
| `private void` | `styleNotifButton(JButton btn, Color fg)` | Applies the shared small-button styling to a notifications-section button. |
| `private long[]` | `thirtyDayRange(TrackedItem item)` |  |
| `private void` | `toggleDetailTracking()` | Toggles tracking of the item shown in the detail view (#138), driven by the header button. |
| `private void` | `updateAcqPopoutButton()` | Hides the acquisitions pop-out button while its pop-out window is already open. |
| `public void` | `updateMarketInfoTimes()` | Live-updates the Market Info last-bought / last-sold relative times for the shown detail item. |
| `private long` | `windowVolume(TrackedItem item, TimeWindow window)` |  |

### Field Detail

#### CARD_CONTENT

`private static final String CARD_CONTENT`

#### CARD_LOADING

`private static final String CARD_LOADING`

#### COLOR_VOLUME

`private static final Color COLOR_VOLUME`

#### DEFAULT_NOTIFICATION_ROWS

`private static final int DEFAULT_NOTIFICATION_ROWS`

#### DESCRIPTION_COLOR

`private static final Color DESCRIPTION_COLOR`

#### NUMBER_FORMAT

`private static final NumberFormat NUMBER_FORMAT`

#### OVERVIEW_ROW_DIVIDER

`private static final Color OVERVIEW_ROW_DIVIDER`

#### OVERVIEW_WINDOWS

`private static final TimeWindow[] OVERVIEW_WINDOWS`

#### PRESSURE_BALANCED_HIGH

`private static final int PRESSURE_BALANCED_HIGH`

#### PRESSURE_BALANCED_LOW

`private static final int PRESSURE_BALANCED_LOW`

#### PRICES_BASE

`private static final String PRICES_BASE`

#### WIKI_BASE

`private static final String WIKI_BASE`

#### acqHoverCol

`private int acqHoverCol`

#### acqHoverRow

`private int acqHoverRow`

#### acqPopoutButton

`private JButton acqPopoutButton`

#### acqPopoutModel

`private AcquisitionsTableModel acqPopoutModel`

#### acqPopoutScroll

`private JScrollPane acqPopoutScroll`

#### acqPopoutTable

`private JTable acqPopoutTable`

#### acquisitionsModel

`private AcquisitionsTableModel acquisitionsModel`

#### acquisitionsScroll

`private JScrollPane acquisitionsScroll`

#### acquisitionsSection

`private JPanel acquisitionsSection`

#### acquisitionsTable

`private JTable acquisitionsTable`

#### alchEstProfit

`private final JLabel alchEstProfit`

#### alchEstProfitRow

`private JPanel alchEstProfitRow`

#### alchInfoSection

`private JPanel alchInfoSection`

#### appliedOverviewRows

`private Set<TimeWindow> appliedOverviewRows`

#### appliedSectionLayout

`private String appliedSectionLayout`

#### boundItemId

`private int boundItemId`

#### buyPressureLabel

`private final PressureVolumeLabel buyPressureLabel`

#### buySellBar

`private BuySellBar buySellBar`

#### cardLayout

`private final CardLayout cardLayout`

#### ccvAvg

`private final JLabel ccvAvg`

#### ccvHigh

`private final JLabel ccvHigh`

#### ccvLow

`private final JLabel ccvLow`

#### ccvProfit

`private final JLabel ccvProfit`

#### ccvQuantity

`private final JLabel ccvQuantity`

#### ccvSection

`private JPanel ccvSection`

#### config

`private final StockpileConfig config`

#### detailCard

`private final JPanel detailCard`

#### detailDescriptionArea

`private final JTextArea detailDescriptionArea`

#### detailExamineText

`private String detailExamineText`

#### detailIconLabel

`private final JLabel detailIconLabel`

#### detailItemTracked

`private boolean detailItemTracked`

#### detailLoadTimedOut

`private boolean detailLoadTimedOut`

#### detailLoadTimeout

`private Timer detailLoadTimeout`

#### detailLoadingCard

`private final JPanel detailLoadingCard`

#### detailNameLabel

`private final JLabel detailNameLabel`

#### detailQtyLabel

`private final JLabel detailQtyLabel`

#### detailSectionsHost

`private JPanel detailSectionsHost`

#### detailSpinner

`private final Spinner detailSpinner`

#### detailTrackBtn

`private final JButton detailTrackBtn`

#### editingNotifications

`private volatile boolean editingNotifications`

#### graphLineSet

`private PriceGraphPanel.LineSet graphLineSet`

#### graphSmooth

`private boolean graphSmooth`

#### haProfit

`private final JLabel haProfit`

#### haValue

`private final JLabel haValue`

#### host

`private final DetailViewHost host`

#### icvAvg

`private final JLabel icvAvg`

#### icvHigh

`private final JLabel icvHigh`

#### icvLow

`private final JLabel icvLow`

#### icvVolume

`private final JLabel icvVolume`

#### itemManager

`private final ItemManager itemManager`

#### itemValuesSection

`private JPanel itemValuesSection`

#### laProfit

`private final JLabel laProfit`

#### laValue

`private final JLabel laValue`

#### linksSection

`private JPanel linksSection`

#### marketInfoSection

`private JPanel marketInfoSection`

#### miBuyLimit

`private final JLabel miBuyLimit`

#### miGeTax

`private final JLabel miGeTax`

#### miLastBought

`private final JLabel miLastBought`

#### miLastSold

`private final JLabel miLastSold`

#### miLiquidity

`private final JLabel miLiquidity`

#### miVolatility

`private final JLabel miVolatility`

#### notificationsModel

`private NotificationsTableModel notificationsModel`

#### notificationsSection

`private JPanel notificationsSection`

#### notificationsTable

`private JTable notificationsTable`

#### onAcquisitionsEdited

`private final Consumer<Integer> onAcquisitionsEdited`

#### onAddItem

`private final BiConsumer<Integer,TrackItemMode> onAddItem`

#### onClearAcquisitions

`private final Consumer<Integer> onClearAcquisitions`

#### onNotificationsEdited

`private final Consumer<Integer> onNotificationsEdited`

#### onRequestDetailData

`private final Consumer<Integer> onRequestDetailData`

#### onUntrackToPreview

`private final Consumer<Integer> onUntrackToPreview`

#### openPopouts

`private final List<PopoutHandle> openPopouts`

#### overviewGrid

`private JPanel overviewGrid`

#### overviewLabels

`private final Map<TimeWindow,JLabel[]> overviewLabels`

#### overviewWindowLabels

`private final List<JLabel> overviewWindowLabels`

#### pressureMarketLabel

`private final JLabel pressureMarketLabel`

#### previewItem

`private TrackedItem previewItem`

#### priceGraph

`private PriceGraphPanel priceGraph`

#### priceGraphSection

`private JPanel priceGraphSection`

#### priceOverviewSection

`private JPanel priceOverviewSection`

#### pricePopoutGraph

`private PriceGraphPanel pricePopoutGraph`

#### priceRangeBar

`private PriceRangeBar priceRangeBar`

#### rangePositionLabel

`private final JLabel rangePositionLabel`

#### sellPressureLabel

`private final PressureVolumeLabel sellPressureLabel`

#### topStack

`private JPanel topStack`

#### viewLayout

`private final Layout viewLayout`

#### volumeGraph

`private PriceGraphPanel volumeGraph`

#### volumeGraphSection

`private JPanel volumeGraphSection`

### Constructor Detail

#### DetailView

`DetailView(DetailViewHost host, Layout viewLayout)`

Builds a detail view bound to a host. Constructs the detail and loading cards and installs them
in this component's own `CardLayout`; the host mounts this component where the detail view
should appear.

### Method Detail

#### acqAddRow

`private void acqAddRow(JTable table, AcquisitionsTableModel model)`

Appends a new empty acquisition row to the table and scrolls it into view.

#### acqClean

`private void acqClean(AcquisitionsTableModel model)`

Consolidates the acquisitions log, merging like rows and dropping empty ones.

#### acqClear

`private void acqClear()`

Clears all acquisitions for the current item after confirmation, via the plugin callback.

#### acqRemoveSelected

`private void acqRemoveSelected(JTable table, AcquisitionsTableModel model)`

Removes the selected acquisition rows and commits the change.

#### acqTextButton

`private JButton acqTextButton(String text, Color fg)`

Builds a small flat text button used by the acquisitions pop-out toolbar.

#### acqTooltipLabel

`private static String acqTooltipLabel(int col)`

- **Returns:** the tooltip caption for an acquisitions-table column.

#### alchProfitTooltip

`private String alchProfitTooltip(String label, long alchValue, long itemAvg, int fireQty)`

Builds the tooltip breaking down an alch-profit figure: alch value minus item cost and rune cost.

#### applyAcqRenderers

`private void applyAcqRenderers(JTable table, AcquisitionsTableModel model, boolean expanded)`

Wires an acquisitions table's fonts, row height, per-column renderers/editors, and
headers for either the compact sidebar table or the expanded pop-out table.

#### applyBuyLimit

`private void applyBuyLimit(TrackedItem item)`

Shows the Buy Limit cell as `used / total` when purchases have been tracked in
the current window (with a reset-countdown tooltip), the plain total when untouched,
or `N/A` when the item has no GE limit.

#### applyBuySellPressure

`private void applyBuySellPressure(TrackedItem item)`

Computes the buy/sell volume split over the configured window and updates the pressure bar + labels.

#### applyDeltaPct

`private void applyDeltaPct(JLabel label, TrackedItem item, TimeWindow window)`

Sets a label to the signed percent change of the current price vs. the window average, colored up/down.

#### applyDetailCard

`private void applyDetailCard()`

Shows either the spinner placeholder or the populated detail view for the
currently open item, depending on whether its prices are still loading.
A view-only preview shows the spinner until its prices arrive, its load
fails, or the safety timeout fires; everything else shows immediately.

#### applyDetailSectionLayout

`private void applyDetailSectionLayout()`

Reorders and shows/hides the detail sections to match the configured slot assignments.

#### applyExamineWrap

`private void applyExamineWrap()`

Sets the current examine text on the description area. The `JTextArea` line-wraps to its
own laid-out width, so no width measurement is needed and it re-wraps responsively on resize.

#### applyLiquidity

`private void applyLiquidity(long vol24)`

Sets the market-info liquidity rating from the last 24h volume via `MarketClassifier`.

#### applyNotificationRenderers

`private void applyNotificationRenderers()`

Wires the per-column cell editors/renderers on the notifications table to the current metric's input type.

#### applyProfitLabel

`private void applyProfitLabel(JLabel label, long profit, boolean known)`

Sets a profit label to a signed, colored gp figure, or a placeholder when the profit is unknown.

#### applyRangePosition

`private void applyRangePosition(long min, long max, long live)`

Sets the market-info "30-day range position" rating for where the live price sits within its month range.

#### applyTableRenderers

`private void applyTableRenderers()`

Applies the acquisitions renderers to the sidebar (compact) table.

#### applyTradeTime

`private void applyTradeTime(JLabel label, long epochSeconds)`

Sets a label to an epoch-second trade time's relative age, with the absolute time as a tooltip.

#### applyVolatility

`private void applyVolatility(TrackedItem item)`

Sets the market-info volatility rating from the item's week series via `MarketClassifier`.

#### buildAlchBlock

`private JPanel buildAlchBlock()`

Builds the alch-info section (high/low alch values and the high-alch profit estimate).

#### buildBrushIcon

`private Icon buildBrushIcon()`

Loads the bundled `broom.png` scaled to a 12px icon for the clear-acquisitions button.

#### buildCurrentValuesBlock

`private JPanel buildCurrentValuesBlock(JLabel high, JLabel low, JLabel avg, JLabel fourth, JLabel profit)`

Builds the stacked current-values block (high/low/avg plus a fourth metric row),
colouring each label by metric and appending a divider-topped profit row when a
profit label is supplied.

#### buildDetailCard

`private void buildDetailCard()`

Constructs the detail-view card once: the header, the scrollable body, and
every detail section (current values, market info, charts, overview grid,
alch, notifications, acquisitions log). Sections are populated later per item.

#### buildDetailLoadingCard

`private void buildDetailLoadingCard()`

Fills `#detailLoadingCard` with a centered spinner and caption.

#### buildDetailSection

`private JPanel buildDetailSection(String title, Component... contents)`

Builds a titled detail-view section containing the given components.

#### buildDetailSectionTitle

`private JLabel buildDetailSectionTitle(String text, boolean withDivider)`

Builds a centred bold section title, optionally topped with a divider rule.

#### buildDetailSectionTitleRow

`private JComponent buildDetailSectionTitleRow(String title, JButton popBtn)`

Builds a divider-topped section title row with the title centred between a strut
matching the pop-out button's width (so the title stays optically centred) and the
button itself.

#### buildDetailSectionTitleRow

`private JComponent buildDetailSectionTitleRow(String title, Runnable onPopout)`

Builds a section title row with a pop-out button wired to the given action.

#### buildDetailSectionWithPopout

`private JPanel buildDetailSectionWithPopout(String title, Runnable onPopout, Component... contents)`

Builds a titled detail-view section whose title row carries a pop-out button.

#### buildEyeIcon

`private Icon buildEyeIcon(int size)`

Loads the bundled `eye.png` scaled to a square icon for the view-only button.

#### buildLeftArrowIcon

`private Icon buildLeftArrowIcon()`

Paints the small left-pointing triangle used by the detail view's Back button.

#### buildLinkButton

`private JButton buildLinkButton(String text, String tooltip, Runnable onClick)`

Builds a detail-view link button that runs the given action when clicked.

#### buildLinksBlock

`private JPanel buildLinksBlock()`

Builds the Links detail section's content: Wiki and Live Prices buttons for the current item.

#### buildMarketInfoBlock

`private JPanel buildMarketInfoBlock()`

Builds the market-info section (buy limit, GE value, volatility, liquidity, 30-day range, etc.).

#### buildMarketInfoPair

`private JPanel buildMarketInfoPair(String leftLabel, JLabel leftValue, String rightLabel, JLabel rightValue)`

Builds a two-column grid pairing two captioned values side by side (Market Info / alch rows).

#### buildNotificationsSection

`private void buildNotificationsSection()`

Builds the notifications section: the rules table and its add/remove/edit controls.

#### buildOverviewGrid

`private JPanel buildOverviewGrid()`

Builds (and remembers) the sidebar overview grid.

#### buildPopoutButton

`private JButton buildPopoutButton(Runnable onClick)`

Builds a borderless pop-out button that runs the given action when clicked.

#### buildPopoutIcon

`private Icon buildPopoutIcon()`

Paints the small box-with-arrow "open in new window" icon used by pop-out buttons.

#### clearItemValue

`private void clearItemValue(JLabel label, String text)`

Resets a value label to plain text, dropping its tooltip and any hover-tint listener.

#### closePopouts

`private void closePopouts()`

Disposes all open pop-out windows (e.g. when leaving the detail view).

#### createOverviewGrid

`private JPanel createOverviewGrid(Map<TimeWindow,JLabel[]> labels, List<JLabel> windowLabels, int sepGap)`

Creates an overview grid panel that custom-paints its own dividers: a vertical rule
after the window-label column and a horizontal rule between consecutive rows, both
derived from the live label positions so they track layout changes.

#### currentDetailItem

`private TrackedItem currentDetailItem()`

- **Returns:** the item currently shown in the detail view (a tracked item or the transient preview), or null.

#### expandedAcqHeader

`private static String expandedAcqHeader(String compact)`

- **Returns:** the roomy pop-out header for a compact acquisitions column name.

#### fillOverviewGrid

`private void fillOverviewGrid(JPanel grid, Map<TimeWindow,JLabel[]> labels, List<JLabel> windowLabels, Set<TimeWindow> rows, Font font, boolean expanded)`

Lays out the overview grid's header and one row of price/volume labels per selected time window.

#### formatDuration

`private static String formatDuration(long seconds)`

Formats a positive second count as a compact `"2h 14m"` / `"43m"` / `"12s"` duration.

#### fullWindowLabel

`private static String fullWindowLabel(TimeWindow w)`

- **Returns:** the long-form window name used by the pop-out overview grid.

#### geTax

`private long geTax(long avgPrice)`

- **Returns:** the Grand Exchange sell tax on a unit at `avgPrice` (per the live GE tax rules).

#### getBoundItemId

`public int getBoundItemId()`

- **Returns:** the item id currently bound to this detail view, or -1 when none.

#### installVolumeValue

`private void installVolumeValue(JLabel label, long vol, boolean full)`

Sets a volume cell's compact/full text with a full-number tooltip and hover tint, or a placeholder.

#### isDetailLoading

`private boolean isDetailLoading(TrackedItem item)`

- **Returns:** whether `item` is a tradeable preview whose prices have not yet loaded (or failed).

#### isEditingNotifications

`public boolean isEditingNotifications()`

- **Returns:** whether the user is mid-edit in the notifications table.

#### isLoadingVisible

`public boolean isLoadingVisible()`

- **Returns:** whether the loading-spinner placeholder (rather than the populated card) is currently shown.

#### isPreview

`public boolean isPreview()`

- **Returns:** whether the shown item is a read-only preview rather than a tracked item.

#### newSectionWrapper

`private JPanel newSectionWrapper()`

- **Returns:** an empty vertical wrapper panel used to stack a detail section's rows.

#### notifyNotificationsEdited

`private void notifyNotificationsEdited()`

Notifies the plugin (via callback) that the current item's notification rules
changed, so it can persist them.

#### onLeaveDetail

`public void onLeaveDetail()`

Clears the bound item and stops any in-flight loading/pop-outs when the host leaves the detail view.

#### onRebuild

`public boolean onRebuild()`

Refreshes the bound item from the host's current tracked state on a list rebuild: clears a stale
preview once the item is tracked, repopulates in place, and reports whether an item is still shown
so the host can fall back to the main list when it has gone.

- **Returns:** `true` if an item is still shown, `false` if the bound item has disappeared

#### openCollectionLogPopout

`private void openCollectionLogPopout()`

Opens the editable acquisitions (collection log) table in a standalone pop-out window.

#### openGraphPopout

`private void openGraphPopout(String title, PriceGraphPanel.Mode mode, PriceGraphPanel source)`

Opens an expanded chart pop-out mirroring (and kept in sync with) the in-panel graph.

#### openOverviewPopout

`private void openOverviewPopout()`

Opens the price overview grid in a standalone pop-out window.

#### openPricesLink

`private void openPricesLink()`

Opens the wiki realtime prices page for the item currently shown in the detail view.

#### openWikiLink

`private void openWikiLink()`

Opens the OSRS Wiki page for the item currently shown in the detail view.

#### overviewMidpoint

`private static long overviewMidpoint(WikiRealtimePriceClient.PricePoint p)`

- **Returns:** the high/low midpoint of a price point, or whichever side is known when one is missing.

#### populateDetail

`private void populateDetail(TrackedItem item)`

Fills every detail section from an item's current state: header name/icon/quantity,
item and collection values, overview grid, charts, market info (times, volatility,
liquidity, range, pressure), alch figures, notifications, and the acquisitions log.
Called whenever the shown item's data changes.

#### populateOverviewGrid

`private void populateOverviewGrid(Set<TimeWindow> rows)`

(Re)creates the overview grid panels (sidebar and pop-out) for the given set of time-window rows.

#### populateOverviewLabels

`private void populateOverviewLabels(Map<TimeWindow,JLabel[]> labels, TrackedItem item, boolean full)`

Fills the overview grid's cells with an item's per-window high/low/avg/volume/Δ% values.

#### preserveDetailScroll

`private void preserveDetailScroll(Runnable refresh)`

Runs a refresh-in-place of the open detail card, keeping the enclosing scroll
pane's vertical position. The scroll yank this guards against was the
description area's caret scrolling itself into view on `setText` — muzzled
at the source with `DefaultCaret#NEVER_UPDATE` — so this is defensive:
layout is forced synchronously and the position re-asserted in the same EDT
event, with a queued re-assert for layout that settles late (async item images).
Opening a different item still starts at the top, since `#show(int)`
bypasses this.

#### rebuildOverviewGrid

`private void rebuildOverviewGrid()`

Rebuilds the price overview grid to match the configured preset of time-window rows.

#### refreshDetailData

`public void refreshDetailData(int itemId)`

Re-populates the open detail view with fresh data for `itemId` (no-op if a different item is shown).

#### refreshPopouts

`private void refreshPopouts(TrackedItem item)`

Pushes fresh data for `item` into every open pop-out window.

#### scrollAcquisitionsToBottom

`private void scrollAcquisitionsToBottom()`

Scrolls the acquisitions log to its newest (bottom) entry once layout has settled.

#### setOverviewPlaceholder

`private void setOverviewPlaceholder(JLabel label)`

Resets an overview cell to the `"-"` placeholder.

#### setPriceCell

`private void setPriceCell(JLabel label, long value, Color color, String tooltipLabel, Color tint, boolean full)`

Sets a price cell's text (full or abbreviated), color, tooltip, and hover tint, or a placeholder if unset.

#### show

`public void show(int itemId)`

Switches to the detail card for an item, requesting its full data and populating the view.

#### showPopout

`private void showPopout(String title, JComponent content, Consumer<TrackedItem> refresher, Runnable onClose)`

Opens a non-modal pop-out window hosting `content`, registering its
refresher so live updates reach it and running `onClose` when dismissed.

#### showPreview

`public void showPreview(TrackedItem item)`

Opens a read-only preview of an untracked item. The item is not in the tracked list; the plugin
supplies its price/history data directly and the tracked-only sections stay hidden.

#### shownDetailItem

`private TrackedItem shownDetailItem()`

- **Returns:** the item currently backing the detail view (tracked or preview), or `null`.

#### shownItem

`public TrackedItem shownItem()`

- **Returns:** the item backing the detail view (tracked or preview), or `null` when none is shown.

#### spelledInterval

`private static String spelledInterval(TimeWindow window)`

- **Returns:** the window's long label lower-cased for use mid-sentence in tooltips.

#### stopDetailLoading

`private void stopDetailLoading()`

Stops the spinner animation and cancels the pending load-timeout, if any.

#### stopLoading

`public void stopLoading()`

Stops the loading spinner and its safety timeout (used when the host is disposed).

#### styleNotifButton

`private void styleNotifButton(JButton btn, Color fg)`

Applies the shared small-button styling to a notifications-section button.

#### thirtyDayRange

`private long[] thirtyDayRange(TrackedItem item)`

- **Returns:** the `[min, max]` price range over the item's last 30 days via `MarketClassifier`.

#### toggleDetailTracking

`private void toggleDetailTracking()`

Toggles tracking of the item shown in the detail view (#138), driven by the header button.
A read-only preview is added to the tracked list (the next rebuild swaps the preview for the
real tracked detail); a tracked item is untracked but stays open as a preview so the detail
view does not bounce back to the main list.

#### updateAcqPopoutButton

`private void updateAcqPopoutButton()`

Hides the acquisitions pop-out button while its pop-out window is already open.

#### updateMarketInfoTimes

`public void updateMarketInfoTimes()`

Live-updates the Market Info last-bought / last-sold relative times for the shown detail item.

#### windowVolume

`private long windowVolume(TrackedItem item, TimeWindow window)`

- **Returns:** the total traded volume for an item over the given window, or 0 if unknown.

---

## com.oveduumnakal.DetailView.Layout

_enum_

`enum Layout`

Section arrangement: the sidebar's vertical stack, or the #109 dashboard's two-column layout.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `DASHBOARD` |  |
| `STACK` |  |

### Enum Constant Detail

#### DASHBOARD

`DASHBOARD`

#### STACK

`STACK`

---

## com.oveduumnakal.DetailViewHost

_interface_

`public interface DetailViewHost`

The seam a `DetailView` uses to reach the state and callbacks it does not own itself (#110).
The detail view was extracted from `StockpilePanel` so a second live instance (the #109
dashboard window) can exist; everything the extracted component still needs from its host &mdash;
shared services, the plugin edit callbacks, and Back navigation &mdash; is supplied through this
interface rather than direct field access. `StockpilePanel` implements it by delegating to
the fields and callbacks it already holds.

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `void` | `acquisitionsEdited(int itemId)` | Signals that the acquisitions log for `itemId` was edited in-view. |
| `void` | `addItem(int itemId, TrackItemMode mode)` | Tracks `itemId` from the detail header Track button (#138), honouring the add mode. |
| `void` | `clearAcquisitions(int itemId)` | Clears the acquisitions log for `itemId`. |
| `StockpileConfig` | `config()` |  |
| `String` | `examine(int itemId)` |  |
| `long` | `fireRunePrice()` |  |
| `ItemManager` | `itemManager()` |  |
| `long` | `natureRunePrice()` |  |
| `void` | `notificationsEdited(int itemId)` | Signals that the notifications for `itemId` were edited in-view. |
| `void` | `onBack()` | Invoked by the detail view's Back control. |
| `void` | `requestDetailData(int itemId)` | Asks the plugin to (re)fetch the detailed price/history data for `itemId`. |
| `TrackedItem` | `trackedItem(int itemId)` |  |
| `void` | `untrackToPreview(int itemId)` | Untracks `itemId` but keeps it open as a read-only preview (#138). |

### Method Detail

#### acquisitionsEdited

`void acquisitionsEdited(int itemId)`

Signals that the acquisitions log for `itemId` was edited in-view.

#### addItem

`void addItem(int itemId, TrackItemMode mode)`

Tracks `itemId` from the detail header Track button (#138), honouring the add mode.

#### clearAcquisitions

`void clearAcquisitions(int itemId)`

Clears the acquisitions log for `itemId`.

#### config

`StockpileConfig config()`

- **Returns:** the live plugin config (colours, toggles, section visibility) the detail view reads.

#### examine

`String examine(int itemId)`

- **Returns:** the examine text for `itemId`, or `null`/empty when none is cached.

#### fireRunePrice

`long fireRunePrice()`

- **Returns:** the current fire-rune price used for high-alch profit figures.

#### itemManager

`ItemManager itemManager()`

- **Returns:** the shared item manager, for icon images and item lookups.

#### natureRunePrice

`long natureRunePrice()`

- **Returns:** the current nature-rune price used for high-alch profit figures.

#### notificationsEdited

`void notificationsEdited(int itemId)`

Signals that the notifications for `itemId` were edited in-view.

#### onBack

`void onBack()`

Invoked by the detail view's Back control. The sidebar returns to the main list; the dashboard
window disposes itself.

#### requestDetailData

`void requestDetailData(int itemId)`

Asks the plugin to (re)fetch the detailed price/history data for `itemId`.

#### trackedItem

`TrackedItem trackedItem(int itemId)`

- **Returns:** the tracked item backing `itemId`, or `null` if it is not tracked.

#### untrackToPreview

`void untrackToPreview(int itemId)`

Untracks `itemId` but keeps it open as a read-only preview (#138).

---

## com.oveduumnakal.DoseFamily

_class_

`final class DoseFamily`

Parses a potion's dose from its display name (#220): `Prayer potion(4)`
resolves to base `"prayer potion"` at 4 doses, so the four dose item ids
of one potion share a family and their cost basis can follow the liquid across a
decant. Only a trailing `(1)`–`(4)` counts — the standard potion dose
range — which keeps charge variants such as `Ring of dueling(8)` out of the
family. Client-free and unit-testable.

### Nested Type Summary

| Type | Description |
|---|---|
| _class_ [`Parsed`](#comoveduumnakaldosefamilyparsed) | One item's place in a dose family: the shared base name and this id's dose count. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final Pattern` | `DOSE_SUFFIX` |  |
| `private static final int` | `MAX_DOSES` | Highest dose a standard tradeable potion holds; larger parentheticals are charges, not doses. |

### Constructor Summary

| Constructor | Description |
|---|---|
| `DoseFamily()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `static Parsed` | `parse(String name)` |  |

### Field Detail

#### DOSE_SUFFIX

`private static final Pattern DOSE_SUFFIX`

#### MAX_DOSES

`private static final int MAX_DOSES`

Highest dose a standard tradeable potion holds; larger parentheticals are charges, not doses.

### Constructor Detail

#### DoseFamily

`private DoseFamily()`

### Method Detail

#### parse

`static Parsed parse(String name)`

- **Returns:** the dose family of `name`, or `null` when it carries no
        trailing `(1)`–`(4)` dose suffix

---

## com.oveduumnakal.DoseFamily.Parsed

_class_

`static final class Parsed`

One item's place in a dose family: the shared base name and this id's dose count.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `final String` | `base` |  |
| `final int` | `doses` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `Parsed(String base, int doses)` |  |

### Field Detail

#### base

`final String base`

#### doses

`final int doses`

### Constructor Detail

#### Parsed

`Parsed(String base, int doses)`

---

## com.oveduumnakal.EllipsisText

_class_

`final class EllipsisText`

Ellipsis-truncation for labels: assigns text that is shortened with a trailing
ellipsis to fit the label's width, keeps the full text in a tooltip, and
re-truncates automatically on resize. Stateless utility.

### Nested Type Summary

| Type | Description |
|---|---|
| _class_ [`EllipsisResizeListener`](#comoveduumnakalellipsistextellipsisresizelistener) | Re-applies ellipsis truncation to its label whenever the label's width changes. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final String` | `ELLIPSIS` |  |
| `private static final String` | `FULL_TEXT_KEY` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `EllipsisText()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `static void` | `apply(JLabel label)` | Re-truncates a label's stored full text with an ellipsis to fit its current width. |
| `static void` | `set(JLabel label, String fullText)` | Assigns text to a label, truncating it with a trailing ellipsis so it fits the label's available width and exposing the untruncated text as a tooltip. |

### Field Detail

#### ELLIPSIS

`private static final String ELLIPSIS`

#### FULL_TEXT_KEY

`private static final String FULL_TEXT_KEY`

### Constructor Detail

#### EllipsisText

`private EllipsisText()`

### Method Detail

#### apply

`static void apply(JLabel label)`

Re-truncates a label's stored full text with an ellipsis to fit its current width.

#### set

`static void set(JLabel label, String fullText)`

Assigns text to a label, truncating it with a trailing ellipsis so it fits the
label's available width and exposing the untruncated text as a tooltip. The text
is re-truncated automatically whenever the label is resized.

---

## com.oveduumnakal.EllipsisText.EllipsisResizeListener

_class_

`private static final class EllipsisResizeListener`

Re-applies ellipsis truncation to its label whenever the label's width changes.

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public void` | `componentResized(ComponentEvent e)` |  |

### Method Detail

#### componentResized

`public void componentResized(ComponentEvent e)`

---

## com.oveduumnakal.EstimatesPosition

_enum_

`public enum EstimatesPosition`

Where the GE value estimate block sits within an item's detail card &ndash;
above (`#TOP`) or below (`#BOTTOM`) the other sections. The
`label` is the name shown in the config dropdown.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `BOTTOM` | The `"Bottom"` option. |
| `TOP` | The `"Top"` option. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `label` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `EstimatesPosition(String label)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public String` | `toString()` | Returns the display label shown in the UI. |

### Enum Constant Detail

#### BOTTOM

`BOTTOM`

The `"Bottom"` option.

#### TOP

`TOP`

The `"Top"` option.

### Field Detail

#### label

`private final String label`

### Constructor Detail

#### EstimatesPosition

`EstimatesPosition(String label)`

### Method Detail

#### toString

`public String toString()`

Returns the display label shown in the UI.

- **Returns:** the display label

---

## com.oveduumnakal.EstimatesSpacing

_enum_

`public enum EstimatesSpacing`

Vertical density of the GE value estimate rows &ndash; normal
(`#DEFAULT`) or tightened (`#COMPACT`) padding. The `label`
is the name shown in the config dropdown.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `COMPACT` | The `"Compact"` option. |
| `DEFAULT` | The `"Default"` option. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `label` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `EstimatesSpacing(String label)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public String` | `toString()` | Returns the display label shown in the UI. |

### Enum Constant Detail

#### COMPACT

`COMPACT`

The `"Compact"` option.

#### DEFAULT

`DEFAULT`

The `"Default"` option.

### Field Detail

#### label

`private final String label`

### Constructor Detail

#### EstimatesSpacing

`EstimatesSpacing(String label)`

### Method Detail

#### toString

`public String toString()`

Returns the display label shown in the UI.

- **Returns:** the display label

---

## com.oveduumnakal.FallbackPricing

_enum_

`public enum FallbackPricing`

Which price seeds the cost basis of an unknown-source quantity change — an
auto-added item, a mobile/offline resync, or anything else no detector observed
(#219). Split out from the old `AutoAddMode` so the fallback price is chosen
independently of the on/off `StockpileConfig#autoAddItems() auto-add gate`.

<ul>
  <li>`#HIGH`/`#LOW`/`#AVG` &ndash; seed at the item's current
      high, low, or average price.</li>
  <li>`#ZERO` &ndash; seed at a zero cost basis (pure gain).</li>
</ul>

<p>The `label` is the human-readable name shown in the config dropdown.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `AVG` | The `"Avg"` option. |
| `HIGH` | The `"High"` option. |
| `LOW` | The `"Low"` option. |
| `ZERO` | The `"Zero"` option. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `label` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `FallbackPricing(String label)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `static FallbackPricing` | `fromLegacyMode(String legacy)` | Maps a legacy combined `AutoAddMode` name (HIGH/LOW/AVG/ZERO/OFF) to the fallback price it migrates to (#219). |
| `long` | `select(long high, long low, long avg)` | The single fallback-pricing policy, shared by every call site so a new value or a change to HIGH/LOW/AVG meaning is decided once (#181). |
| `public String` | `toString()` | Returns the display label shown in the UI. |

### Enum Constant Detail

#### AVG

`AVG`

The `"Avg"` option.

#### HIGH

`HIGH`

The `"High"` option.

#### LOW

`LOW`

The `"Low"` option.

#### ZERO

`ZERO`

The `"Zero"` option.

### Field Detail

#### label

`private final String label`

### Constructor Detail

#### FallbackPricing

`FallbackPricing(String label)`

### Method Detail

#### fromLegacyMode

`static FallbackPricing fromLegacyMode(String legacy)`

Maps a legacy combined `AutoAddMode` name (HIGH/LOW/AVG/ZERO/OFF) to the
fallback price it migrates to (#219). OFF carried no pricing choice, so it lands on
`#AVG` (today's default). Returns `null` for a value already migrated to a
boolean, a fresh install's `null`, or any unrecognised string — the caller then
leaves the setting untouched, keeping the migration idempotent.

#### select

`long select(long high, long low, long avg)`

The single fallback-pricing policy, shared by every call site so a new value or a change to
HIGH/LOW/AVG meaning is decided once (#181). Selects among the candidate prices supplied for
one item: `#HIGH` → `high`, `#LOW` → `low`, `#ZERO` → `0`,
`#AVG` → `avg`. Callers that only have a single guide price (an item with no
`TrackedItem`) pass it for all three, so the choice still resolves consistently.

- **Parameter** `high` — the item's current high price
- **Parameter** `low` — the item's current low price
- **Parameter** `avg` — the item's current average price (also the default)
- **Returns:** the price this mode seeds the cost basis with

#### toString

`public String toString()`

Returns the display label shown in the UI.

- **Returns:** the display label

---

## com.oveduumnakal.GeIntegrationMode

_enum_

`public enum GeIntegrationMode`

How the plugin ties the Stockpile view to the open Grand Exchange offer &ndash;
disabled (`#OFF`), a `#BUTTON` injected on the GE offer screen,
`#AUTO` opening the item in Stockpile as soon as an offer screen appears,
or `#BOTH` doing both at once. The `label` is the name shown in the
config dropdown.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `AUTO` | The `"Auto-open"` option. |
| `BOTH` | The `"Both"` option. |
| `BUTTON` | The `"Button on GE"` option. |
| `OFF` | The `"Off"` option. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `label` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `GeIntegrationMode(String label)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public String` | `toString()` | Returns the display label shown in the UI. |

### Enum Constant Detail

#### AUTO

`AUTO`

The `"Auto-open"` option.

#### BOTH

`BOTH`

The `"Both"` option.

#### BUTTON

`BUTTON`

The `"Button on GE"` option.

#### OFF

`OFF`

The `"Off"` option.

### Field Detail

#### label

`private final String label`

### Constructor Detail

#### GeIntegrationMode

`GeIntegrationMode(String label)`

### Method Detail

#### toString

`public String toString()`

Returns the display label shown in the UI.

- **Returns:** the display label

---

## com.oveduumnakal.GeOfferTracker

_class_

`class GeOfferTracker`

Turns the raw, cumulative `GrandExchangeOfferChanged` stream into discrete
per-slot increments — a placement, an incremental fill, or a cancellation — so the
plugin can price GE activity. Pure and client-free (callers pass the offer's
primitive fields), so it is unit-testable like `SourceAttributionCore`.

<p>Each slot's `quantitySold`/`spent` are cumulative, so a fill's real
increment is the difference from the last event for that slot. The first event seen
per slot after a (re)login seeds a baseline and emits nothing, so an offer's existing
progress isn't replayed as fresh fills.

### Nested Type Summary

| Type | Description |
|---|---|
| _class_ [`Event`](#comoveduumnakalgeoffertrackerevent) | One discrete change derived from an offer event. |
| _enum_ [`Kind`](#comoveduumnakalgeoffertrackerkind) | Which side an offer is: a buy adds items on collection, a sell removes them on placement. |
| _class_ [`SlotState`](#comoveduumnakalgeoffertrackerslotstate) | Cumulative progress last seen for one GE slot, used to compute each event's increment. |
| _enum_ [`Type`](#comoveduumnakalgeoffertrackertype) | What a single offer event means for pricing. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final Map<Integer,SlotState>` | `slots` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `void` | `clear()` | Drops all slot state (logout, plugin shutdown). |
| `List<Event>` | `onOffer(int slot, int itemId, boolean buying, boolean cancelled, boolean empty, int totalQuantity, int quantitySold, long spent)` | Records an offer event and returns the discrete changes it means, oldest first; empty when it carries no actionable change (baseline seed, empty slot, or a no-progress update). |
| `void` | `seed(int slot, int itemId, int quantitySold, long spent)` | Records a slot's current cumulative progress as the baseline without emitting an event, priming an offer that already existed at (re)login so its pre-existing state is not replayed as a fresh placement or fill by the next `#onOffer` for that slot. |

### Field Detail

#### slots

`private final Map<Integer,SlotState> slots`

### Method Detail

#### clear

`void clear()`

Drops all slot state (logout, plugin shutdown).

#### onOffer

`List<Event> onOffer(int slot, int itemId, boolean buying, boolean cancelled, boolean empty, int totalQuantity, int quantitySold, long spent)`

Records an offer event and returns the discrete changes it means, oldest first;
empty when it carries no actionable change (baseline seed, empty slot, or a
no-progress update). A cancellation whose event also advanced the fill counters
emits that final `FILL` before the `CANCELLED` remainder, so the
filled units realize at their true price instead of being discarded.

- **Parameter** `buying` — whether the state is a buy side (`BUYING`/`BOUGHT`/`CANCELLED_BUY`)
- **Parameter** `cancelled` — whether the state is a cancellation (`CANCELLED_BUY`/`CANCELLED_SELL`)
- **Parameter** `empty` — whether the slot is now empty (offer collected/removed)

#### seed

`void seed(int slot, int itemId, int quantitySold, long spent)`

Records a slot's current cumulative progress as the baseline <em>without</em> emitting an
event, priming an offer that already existed at (re)login so its pre-existing state is not
replayed as a fresh placement or fill by the next `#onOffer` for that slot.

---

## com.oveduumnakal.GeOfferTracker.Event

_class_

`static final class Event`

One discrete change derived from an offer event.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `final int` | `itemId` |  |
| `final Kind` | `kind` |  |
| `final int` | `quantity` |  |
| `final Type` | `type` |  |
| `final long` | `unitPrice` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `Event(Type type, Kind kind, int itemId, int quantity, long unitPrice)` |  |

### Field Detail

#### itemId

`final int itemId`

#### kind

`final Kind kind`

#### quantity

`final int quantity`

#### type

`final Type type`

#### unitPrice

`final long unitPrice`

### Constructor Detail

#### Event

`Event(Type type, Kind kind, int itemId, int quantity, long unitPrice)`

---

## com.oveduumnakal.GeOfferTracker.Kind

_enum_

`enum Kind`

Which side an offer is: a buy adds items on collection, a sell removes them on placement.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `BUY` |  |
| `SELL` |  |

### Enum Constant Detail

#### BUY

`BUY`

#### SELL

`SELL`

---

## com.oveduumnakal.GeOfferTracker.SlotState

_class_

`private static final class SlotState`

Cumulative progress last seen for one GE slot, used to compute each event's increment.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `int` | `itemId` |  |
| `int` | `lastQuantitySold` |  |
| `long` | `lastSpent` |  |

### Field Detail

#### itemId

`int itemId`

#### lastQuantitySold

`int lastQuantitySold`

#### lastSpent

`long lastSpent`

---

## com.oveduumnakal.GeOfferTracker.Type

_enum_

`enum Type`

What a single offer event means for pricing.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `CANCELLED` |  |
| `FILL` |  |
| `PLACED` |  |

### Enum Constant Detail

#### CANCELLED

`CANCELLED`

#### FILL

`FILL`

#### PLACED

`PLACED`

---

## com.oveduumnakal.GlowSpeed

_enum_

`public enum GlowSpeed`

Pulse rate of the highlight glow effect, from `#SLOW` to `#FAST`,
or `#OFF` for a steady (non-pulsing) highlight. The `displayName`
is the label shown in the config dropdown.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `FAST` | The `"Fast"` option. |
| `MEDIUM` | The `"Medium"` option. |
| `OFF` | The `"Off"` option. |
| `SLOW` | The `"Slow"` option. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `displayName` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `GlowSpeed(String displayName)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public String` | `toString()` | Returns the display label shown in the UI. |

### Enum Constant Detail

#### FAST

`FAST`

The `"Fast"` option.

#### MEDIUM

`MEDIUM`

The `"Medium"` option.

#### OFF

`OFF`

The `"Off"` option.

#### SLOW

`SLOW`

The `"Slow"` option.

### Field Detail

#### displayName

`private final String displayName`

### Constructor Detail

#### GlowSpeed

`GlowSpeed(String displayName)`

### Method Detail

#### toString

`public String toString()`

Returns the display label shown in the UI.

- **Returns:** the display label

---

## com.oveduumnakal.GpFormat

_class_

`public final class GpFormat`

Single source of truth for formatting gp values across the panel, graph, and
notifications.

<p>The compact form abbreviates with uppercase suffixes and drops trailing
zeros: `234K`, `23.4K`, `1.5M`, `2.1B`. Negatives keep
a leading `-`; values under 1,000 are shown as grouped digits. This is a
stateless utility and cannot be instantiated.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final NumberFormat` | `GROUPED` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `GpFormat()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `private static String` | `abbreviate(long value, boolean singleDecimal)` | Core abbreviation: scales by the largest fitting magnitude (K/M/B) and formats the mantissa, or returns grouped digits below 1,000. |
| `public static String` | `fullGp(long value)` | Full comma-grouped digits with a trailing `" gp"`: `"1,234,567 gp"`. |
| `public static String` | `grouped(long value)` | Full comma-grouped digits with no suffix: `"1,234,567"`. |
| `private static String` | `mantissa(double d, boolean singleDecimal)` | Formats a scaled mantissa in `[1, 1000)` to 3 significant figures (or one decimal place when `singleDecimal`), dropping any trailing zeros and a dangling decimal point. |
| `public static String` | `shortGp(long value)` | `#shortValue` with a trailing `" gp"`. |
| `public static String` | `shortValue(long value)` | Compact form to at most 3 significant figures: `234K`, `2.34K`, `1.5M`. |
| `public static String` | `shortValue1dp(long value)` | Compact form capped to a single decimal place (`2.3K` rather than `2.34K`). |
| `private static String` | `sign(long value)` |  |
| `public static String` | `signedGrouped(long value)` | `#grouped` with an explicit leading `+` on positive values (`+1,234`, `-350`, `0`), using the same zero-unsigned convention as `#signedShort`. |
| `public static String` | `signedShort(long value)` | `#shortValue` with an explicit leading `+` on positive values (`+1.2M`, `-350K`, `0`). |

### Field Detail

#### GROUPED

`private static final NumberFormat GROUPED`

### Constructor Detail

#### GpFormat

`private GpFormat()`

### Method Detail

#### abbreviate

`private static String abbreviate(long value, boolean singleDecimal)`

Core abbreviation: scales by the largest fitting magnitude (K/M/B) and
formats the mantissa, or returns grouped digits below 1,000.

- **Parameter** `singleDecimal` — cap the mantissa to one decimal place rather than 3 sig-figs

#### fullGp

`public static String fullGp(long value)`

Full comma-grouped digits with a trailing `" gp"`: `"1,234,567 gp"`.

#### grouped

`public static String grouped(long value)`

Full comma-grouped digits with no suffix: `"1,234,567"`.

#### mantissa

`private static String mantissa(double d, boolean singleDecimal)`

Formats a scaled mantissa in `[1, 1000)` to 3 significant figures (or
one decimal place when `singleDecimal`), dropping any trailing zeros
and a dangling decimal point.

#### shortGp

`public static String shortGp(long value)`

`#shortValue` with a trailing `" gp"`.

#### shortValue

`public static String shortValue(long value)`

Compact form to at most 3 significant figures: `234K`, `2.34K`, `1.5M`.

#### shortValue1dp

`public static String shortValue1dp(long value)`

Compact form capped to a single decimal place (`2.3K` rather than
`2.34K`). Narrower than `#shortValue` for sub-10K values, for
use where column width is tight (e.g. the price overview grid).

#### sign

`private static String sign(long value)`

- **Returns:** `"+"` for a positive value, empty otherwise (negatives get their sign from the number).

#### signedGrouped

`public static String signedGrouped(long value)`

`#grouped` with an explicit leading `+` on positive values
(`+1,234`, `-350`, `0`), using the same zero-unsigned
convention as `#signedShort`.

#### signedShort

`public static String signedShort(long value)`

`#shortValue` with an explicit leading `+` on positive values
(`+1.2M`, `-350K`, `0`). Negatives already carry their sign
from `#shortValue`; zero is unsigned. This is the single sign convention
shared by the session-stats and est-profit labels.

---

## com.oveduumnakal.HighlightMode

_enum_

`public enum HighlightMode`

Where tracked items are highlighted: on the `#GROUND`, in the
`#INV_BANK` (inventory/bank), `#BOTH`, or `#OFF`. Query the
surfaces with `#ground()` and `#invBank()` rather than comparing
constants. The `displayName` is the label shown in the config dropdown.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `BOTH` | The `"Both"` option. |
| `GROUND` | The `"Ground"` option. |
| `INV_BANK` | The `"Inv/Bank"` option. |
| `OFF` | The `"Off"` option. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `displayName` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `HighlightMode(String displayName)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public boolean` | `ground()` |  |
| `public boolean` | `invBank()` |  |
| `public String` | `toString()` | Returns the display label shown in the UI. |

### Enum Constant Detail

#### BOTH

`BOTH`

The `"Both"` option.

#### GROUND

`GROUND`

The `"Ground"` option.

#### INV_BANK

`INV_BANK`

The `"Inv/Bank"` option.

#### OFF

`OFF`

The `"Off"` option.

### Field Detail

#### displayName

`private final String displayName`

### Constructor Detail

#### HighlightMode

`HighlightMode(String displayName)`

### Method Detail

#### ground

`public boolean ground()`

- **Returns:** whether ground items should be highlighted in this mode.

#### invBank

`public boolean invBank()`

- **Returns:** whether inventory/bank items should be highlighted in this mode.

#### toString

`public String toString()`

Returns the display label shown in the UI.

- **Returns:** the display label

---

## com.oveduumnakal.HoverTintListener

_class_

`final class HoverTintListener`

Mouse listener that swaps a value label to a tinted background (and the full
comma-grouped number) while hovered, restoring the compact text on exit.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `highlightedText` |  |
| `private final JLabel` | `label` |  |
| `private final String` | `shortText` |  |
| `private final Color` | `tint` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `HoverTintListener(JLabel label, String shortText, Color tint)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `void` | `applyIfHovered()` | Applies the highlighted text immediately if the pointer is already over the label. |
| `public void` | `mouseEntered(MouseEvent e)` |  |
| `public void` | `mouseExited(MouseEvent e)` |  |

### Field Detail

#### highlightedText

`private final String highlightedText`

#### label

`private final JLabel label`

#### shortText

`private final String shortText`

#### tint

`private final Color tint`

### Constructor Detail

#### HoverTintListener

`HoverTintListener(JLabel label, String shortText, Color tint)`

### Method Detail

#### applyIfHovered

`void applyIfHovered()`

Applies the highlighted text immediately if the pointer is already over the label.

#### mouseEntered

`public void mouseEntered(MouseEvent e)`

#### mouseExited

`public void mouseExited(MouseEvent e)`

---

## com.oveduumnakal.IssueField

_class_

`final class IssueField`

A single field in an issue form mapped to a GitHub form `id`. A `null`
`#options` makes it a free-text area `#rows` tall; a non-null one makes it
a dropdown whose entries must match the template's option labels exactly.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `final String` | `id` |  |
| `final String` | `label` |  |
| `final String[]` | `options` |  |
| `final int` | `rows` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `IssueField(String id, String label, String[] options)` |  |
| `IssueField(String id, String label, int rows)` |  |
| `IssueField(String id, String label, int rows, String[] options)` |  |

### Field Detail

#### id

`final String id`

#### label

`final String label`

#### options

`final String[] options`

#### rows

`final int rows`

### Constructor Detail

#### IssueField

`IssueField(String id, String label, String[] options)`

#### IssueField

`IssueField(String id, String label, int rows)`

#### IssueField

`private IssueField(String id, String label, int rows, String[] options)`

---

## com.oveduumnakal.ItemCategoryClassifier

_class_

`public final class ItemCategoryClassifier`

Assigns an item to a category using the bundled `item-categories.txt`
snapshot of OSRS Wiki category membership (regenerated by
`scripts/gen-item-categories.py`), keyed by lower-cased item name.
Charge and dose variants such as `Ring of dueling(4)` resolve by
stripping the trailing parenthetical back to the base page name; anything
the snapshot doesn't know falls into `#OTHER`.

<p>Lookups stay offline, instant, and deterministic — the wiki is only
consulted when a maintainer regenerates the snapshot.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final Map<String,String>` | `CATEGORIES` | Lower-cased item name → category, loaded once from the bundled snapshot. |
| `public static final String` | `OTHER` | Catch-all bucket for items the snapshot doesn't cover. |

### Constructor Summary

| Constructor | Description |
|---|---|
| `ItemCategoryClassifier()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public static String` | `classify(String itemName)` |  |
| `private static Map<String,String>` | `loadCategories()` | Loads the bundled `name category` snapshot; a load failure degrades to an empty map. |

### Field Detail

#### CATEGORIES

`private static final Map<String,String> CATEGORIES`

Lower-cased item name → category, loaded once from the bundled snapshot.

#### OTHER

`public static final String OTHER`

Catch-all bucket for items the snapshot doesn't cover.

### Constructor Detail

#### ItemCategoryClassifier

`private ItemCategoryClassifier()`

### Method Detail

#### classify

`public static String classify(String itemName)`

- **Parameter** `itemName` — the item's display name
- **Returns:** the category name for `itemName`, or `#OTHER` when the snapshot has no entry

#### loadCategories

`private static Map<String,String> loadCategories()`

Loads the bundled `name<TAB>category` snapshot; a load failure degrades to an empty map.

---

## com.oveduumnakal.ItemDeltas

_class_

`final class ItemDeltas`

Small helpers for comparing two item-count snapshots keyed by item id (#179). The
"walk every id in either map and act on the signed change" idiom was hand-rolled at
four call sites (container sync, trade-offer suspension, shop claims, session stats);
this collapses it to one place so a fix or an off-by-one can't drift between them.

### Nested Type Summary

| Type | Description |
|---|---|
| _interface_ [`DeltaAction`](#comoveduumnakalitemdeltasdeltaaction) | Receives one item id and its signed `after − before` change. |

### Constructor Summary

| Constructor | Description |
|---|---|
| `ItemDeltas()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `static void` | `forEachDelta(Map<Integer,Integer> before, Map<Integer,Integer> after, DeltaAction action)` | Invokes `action` for every id present in either `before` or `after` with the signed `after − before` count change, skipping ids whose count is unchanged. |
| `static Set<Integer>` | `keyUnion(Map<Integer,?> before, Map<Integer,?> after)` |  |

### Constructor Detail

#### ItemDeltas

`private ItemDeltas()`

### Method Detail

#### forEachDelta

`static void forEachDelta(Map<Integer,Integer> before, Map<Integer,Integer> after, DeltaAction action)`

Invokes `action` for every id present in either `before` or `after` with the
signed `after − before` count change, skipping ids whose count is unchanged.

#### keyUnion

`static Set<Integer> keyUnion(Map<Integer,?> before, Map<Integer,?> after)`

- **Returns:** the union of both maps' keys — every id present in `before` or `after`.

---

## com.oveduumnakal.ItemDeltas.DeltaAction

_interface_

`interface DeltaAction`

Receives one item id and its signed `after − before` change.

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `void` | `accept(int itemId, int delta)` |  |

### Method Detail

#### accept

`void accept(int itemId, int delta)`

---

## com.oveduumnakal.LedgerHost

_interface_

`interface LedgerHost`

The thin seam between `CostBasisLedger` and the RuneLite client (#255). The ledger owns the
cost-basis lot engine, GE pricing, suspension bookkeeping, and buy-limit windows as client-free
logic; everything it still needs from the live client, config, or panel comes through this
interface, so the core takes plain values and stays unit-testable with a hand-rolled host.

<p>Implemented by `StockpilePlugin`, whose methods forward to `client`/`config`/
the panel. The only client type that crosses the seam is `GrandExchangeOffer`, read by the
login reconciliation; all other callbacks trade in primitives and domain `TrackedItem`s.

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `int` | `currentTick()` |  |
| `FallbackPricing` | `fallbackPricing()` |  |
| `boolean` | `isConsumable(int itemId)` |  |
| `boolean` | `isDestroyedAmmo(int itemId)` |  |
| `boolean` | `isEmptyContainer(int itemId)` |  |
| `boolean` | `isRecoverableAmmo(int itemId)` |  |
| `GrandExchangeOffer[]` | `openGeOffers()` |  |
| `void` | `persistTrackedItems()` | Persists the tracked items after the ledger has mutated their lots. |
| `void` | `refreshPanel()` | Refreshes the side panel after a ledger change the user should see. |
| `boolean` | `sourcePricing()` |  |
| `TrackedItem` | `trackedItem(int itemId)` |  |
| `Collection<TrackedItem>` | `trackedItems()` |  |

### Method Detail

#### currentTick

`int currentTick()`

- **Returns:** the current game tick (`client.getTickCount()`).

#### fallbackPricing

`FallbackPricing fallbackPricing()`

- **Returns:** the configured cost-basis fallback policy for unknown-source changes.

#### isConsumable

`boolean isConsumable(int itemId)`

- **Returns:** whether `itemId` is a consumable whose unclaimed removal closes at 0.

#### isDestroyedAmmo

`boolean isDestroyedAmmo(int itemId)`

- **Returns:** whether `itemId` is ammo destroyed on use (a cannonball, a thrown chinchompa).

#### isEmptyContainer

`boolean isEmptyContainer(int itemId)`

- **Returns:** whether `itemId` is an empty vessel freed by finishing a potion or drink.

#### isRecoverableAmmo

`boolean isRecoverableAmmo(int itemId)`

- **Returns:** whether `itemId` is recoverable ranged/thrown ammo that lands on the target's tile.

#### openGeOffers

`GrandExchangeOffer[] openGeOffers()`

- **Returns:** the live GE offers (`client.getGrandExchangeOffers()`), or `null` before login.

#### persistTrackedItems

`void persistTrackedItems()`

Persists the tracked items after the ledger has mutated their lots.

#### refreshPanel

`void refreshPanel()`

Refreshes the side panel after a ledger change the user should see.

#### sourcePricing

`boolean sourcePricing()`

- **Returns:** whether Source-Based Pricing is enabled (`config.sourcePricing()`).

#### trackedItem

`TrackedItem trackedItem(int itemId)`

- **Returns:** the tracked item for `itemId`, or `null` when it is not tracked.

#### trackedItems

`Collection<TrackedItem> trackedItems()`

- **Returns:** the live tracked items, for the expiry sweeps and login reconciliation.

---

## com.oveduumnakal.MarketClassifier

_class_

`final class MarketClassifier`

Derives the human-readable market ratings shown for an item &ndash;
volatility, liquidity, and where today's price sits within its 30-day range
&ndash; from raw wiki price history.

<p>Each method maps continuous figures onto coarse `"Low"`/`"Medium"`/`"High"`-style buckets, returning `null` when there is
not enough data to classify. This is a stateless package-private utility.

### Constructor Summary

| Constructor | Description |
|---|---|
| `MarketClassifier()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `static long[]` | `buySellVolume(List<WikiRealtimePriceClient.PricePoint> series, Duration window)` | Sums instant-buy vs instant-sell volume over the given look-back window. |
| `static String` | `liquidity(long vol24)` | Rates trading liquidity from the last 24h of volume. |
| `static String` | `rangePosition(long min, long max, long live)` | Labels where the live price falls within a `[min, max]` range as a fraction of the way up, from `"Lowest"` to `"Highest"`. |
| `static long[]` | `thirtyDayRange(List<WikiRealtimePriceClient.PricePoint> monthSeries)` | Finds the lowest low and highest high over the past 30 days. |
| `static String` | `volatility(List<WikiRealtimePriceClient.PricePoint> weekSeries)` | Rates price volatility over the past week as the coefficient of variation (standard deviation / mean) of the high/low samples. |

### Constructor Detail

#### MarketClassifier

`private MarketClassifier()`

### Method Detail

#### buySellVolume

`static long[] buySellVolume(List<WikiRealtimePriceClient.PricePoint> series, Duration window)`

Sums instant-buy vs instant-sell volume over the given look-back window.

- **Parameter** `series` — price points at any granularity (only in-window points count)
- **Parameter** `window` — how far back to aggregate
- **Returns:** `{buyVolume, sellVolume`} (high/low price volumes), each 0 when absent

#### liquidity

`static String liquidity(long vol24)`

Rates trading liquidity from the last 24h of volume.

- **Parameter** `vol24` — units traded in the past day
- **Returns:** `"Low"` (&lt;500), `"Medium"` (&le;5000), `"High"`,
        or `null` when volume is unknown

#### rangePosition

`static String rangePosition(long min, long max, long live)`

Labels where the live price falls within a `[min, max]` range as a
fraction of the way up, from `"Lowest"` to `"Highest"`.

- **Parameter** `min` — range low (e.g. from `#thirtyDayRange`)
- **Parameter** `max` — range high
- **Parameter** `live` — the current price
- **Returns:** a position label, or `null` if the range or live price is invalid

#### thirtyDayRange

`static long[] thirtyDayRange(List<WikiRealtimePriceClient.PricePoint> monthSeries)`

Finds the lowest low and highest high over the past 30 days.

- **Parameter** `monthSeries` — price points covering (at least) the last month
- **Returns:** a two-element `[min, max]` array, or `[0, 0]` if no
        in-window samples were found

#### volatility

`static String volatility(List<WikiRealtimePriceClient.PricePoint> weekSeries)`

Rates price volatility over the past week as the coefficient of variation
(standard deviation / mean) of the high/low samples.

- **Parameter** `weekSeries` — 1h price points covering (at least) the last week
- **Returns:** `"Low"` (&lt;1.5%), `"Medium"` (&le;5%), `"High"`,
        or `null` if fewer than two samples are available

---

## com.oveduumnakal.NotifCellRenderer

_class_

`class NotifCellRenderer`

Cell renderer for the notifications table, applying the panel's fonts/colors and centered alignment.

### Constructor Summary

| Constructor | Description |
|---|---|
| `NotifCellRenderer()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public Component` | `getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)` | Renders a notifications-table cell. |

### Constructor Detail

#### NotifCellRenderer

`NotifCellRenderer()`

### Method Detail

#### getTableCellRendererComponent

`public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)`

Renders a notifications-table cell. Metrics show a terse abbreviation in the narrow row cell with
the full name on hover; other values render as-is but still carry a tooltip so anything the column
truncates stays readable.

---

## com.oveduumnakal.NotificationMetric

_enum_

`public enum NotificationMetric`

The item attribute a `NotificationRule` watches &ndash; a price, profit,
volume, percent change, quantity, or a categorical rating.

<p>Each constant pairs a short `label` (used in the rule chip) with a
longer `displayName` (dropdown + tooltip) and an even shorter
`abbreviation` (the notifications-table row cell, which is too narrow for
the full name), plus a `Kind` that drives how the rule's value is entered
and compared. Categorical metrics additionally carry their allowed
`options`. The `locks*`/`is*` predicates capture per-metric
UI constraints (e.g. `#RANGE_30D` only makes sense over a month).

### Nested Type Summary

| Type | Description |
|---|---|
| _enum_ [`Kind`](#comoveduumnakalnotificationmetrickind) | The value domain of a metric, controlling input and comparison semantics. |

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `AVERAGE` | Current average price. |
| `DELTA_PCT` | Percent price change over the chosen window. |
| `HA_PROFIT` | Estimated high-alchemy profit. |
| `HIGH` | Current high (instant-buy) price. |
| `ITM_PROFIT` | Estimated item (buy/sell) profit. |
| `LIQUIDITY` | Market liquidity rating (Low/Medium/High). |
| `LOW` | Current low (instant-sell) price. |
| `QUANTITY` | Held quantity (live inventory count). |
| `RANGE_30D` | Position of the current price within its 30-day range. |
| `VOLATILITY` | Price volatility rating (Low/Medium/High). |
| `VOLUME` | Daily trade volume. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `abbreviation` |  |
| `private final String` | `displayName` |  |
| `private final Kind` | `kind` |  |
| `private final String` | `label` |  |
| `private final List<String>` | `options` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `NotificationMetric(String label, String displayName, String abbreviation, Kind kind, String... options)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public String` | `getAbbreviation()` |  |
| `public String` | `getDisplayName()` | Returns the long name shown in the dropdown and tooltip. |
| `public Kind` | `getKind()` | Returns the value domain that drives input and comparison semantics. |
| `public String` | `getLabel()` | Returns the short label shown in the rule chip. |
| `public List<String>` | `getOptions()` | Returns the allowed categorical options, empty for non-categorical metrics. |
| `public boolean` | `isCategorical()` | Returns whether this metric is compared against fixed categorical options. |
| `public boolean` | `isTimeframeDisabled()` | Quantity is a live inventory count with no timeframe to choose. |
| `public boolean` | `locksOperationToEquals()` | Categorical metrics compare by exact match, so the operator is forced to "=". |
| `public boolean` | `locksTimeframeToMonth()` | `#RANGE_30D` is inherently a 30-day metric, so its timeframe is pinned to a month. |
| `public String` | `toString()` | Returns the short label. |

### Enum Constant Detail

#### AVERAGE

`AVERAGE`

Current average price.

#### DELTA_PCT

`DELTA_PCT`

Percent price change over the chosen window.

#### HA_PROFIT

`HA_PROFIT`

Estimated high-alchemy profit.

#### HIGH

`HIGH`

Current high (instant-buy) price.

#### ITM_PROFIT

`ITM_PROFIT`

Estimated item (buy/sell) profit.

#### LIQUIDITY

`LIQUIDITY`

Market liquidity rating (Low/Medium/High).

#### LOW

`LOW`

Current low (instant-sell) price.

#### QUANTITY

`QUANTITY`

Held quantity (live inventory count).

#### RANGE_30D

`RANGE_30D`

Position of the current price within its 30-day range.

#### VOLATILITY

`VOLATILITY`

Price volatility rating (Low/Medium/High).

#### VOLUME

`VOLUME`

Daily trade volume.

### Field Detail

#### abbreviation

`private final String abbreviation`

#### displayName

`private final String displayName`

#### kind

`private final Kind kind`

#### label

`private final String label`

#### options

`private final List<String> options`

### Constructor Detail

#### NotificationMetric

`NotificationMetric(String label, String displayName, String abbreviation, Kind kind, String... options)`

### Method Detail

#### getAbbreviation

`public String getAbbreviation()`

- **Returns:** the terse form shown in the narrow notifications-table row (full name is in the tooltip/dropdown).

#### getDisplayName

`public String getDisplayName()`

Returns the long name shown in the dropdown and tooltip.

- **Returns:** the display name

#### getKind

`public Kind getKind()`

Returns the value domain that drives input and comparison semantics.

- **Returns:** the metric kind

#### getLabel

`public String getLabel()`

Returns the short label shown in the rule chip.

- **Returns:** the short label

#### getOptions

`public List<String> getOptions()`

Returns the allowed categorical options, empty for non-categorical metrics.

- **Returns:** the unmodifiable option list

#### isCategorical

`public boolean isCategorical()`

Returns whether this metric is compared against fixed categorical options.

- **Returns:** `true` if the metric's kind is `Kind#CATEGORY`

#### isTimeframeDisabled

`public boolean isTimeframeDisabled()`

Quantity is a live inventory count with no timeframe to choose.

#### locksOperationToEquals

`public boolean locksOperationToEquals()`

Categorical metrics compare by exact match, so the operator is forced to "=".

#### locksTimeframeToMonth

`public boolean locksTimeframeToMonth()`

`#RANGE_30D` is inherently a 30-day metric, so its timeframe is pinned to a month.

#### toString

`public String toString()`

Returns the short label.

- **Returns:** the short chip label

---

## com.oveduumnakal.NotificationMetric.Kind

_enum_

`public enum Kind`

The value domain of a metric, controlling input and comparison semantics.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `CATEGORY` | A categorical rating chosen from fixed options. |
| `NUMERIC` | A plain numeric value (gp). |
| `PERCENT` | A percentage value. |
| `QUANTITY` | An item quantity. |

### Enum Constant Detail

#### CATEGORY

`CATEGORY`

A categorical rating chosen from fixed options.

#### NUMERIC

`NUMERIC`

A plain numeric value (gp).

#### PERCENT

`PERCENT`

A percentage value.

#### QUANTITY

`QUANTITY`

An item quantity.

---

## com.oveduumnakal.NotificationOperation

_enum_

`public enum NotificationOperation`

The comparison operator of a `NotificationRule`, used to decide whether a
metric's current value has met the rule's target. Each constant carries its
display `symbol` (e.g. `">="`) and evaluates via `#test`.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `EQ` | Equal: fires when the reading equals the threshold. |
| `GT` | Greater than: fires when the reading exceeds the threshold. |
| `GTE` | Greater than or equal: fires when the reading meets or exceeds the threshold. |
| `LT` | Less than: fires when the reading is below the threshold. |
| `LTE` | Less than or equal: fires when the reading is at or below the threshold. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `symbol` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `NotificationOperation(String symbol)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public String` | `getSymbol()` | Returns the comparison symbol shown in the UI. |
| `public boolean` | `test(double current, double target)` | Applies this operator to a metric reading. |
| `public String` | `toString()` | Returns the comparison symbol. |

### Enum Constant Detail

#### EQ

`EQ`

Equal: fires when the reading equals the threshold.

#### GT

`GT`

Greater than: fires when the reading exceeds the threshold.

#### GTE

`GTE`

Greater than or equal: fires when the reading meets or exceeds the threshold.

#### LT

`LT`

Less than: fires when the reading is below the threshold.

#### LTE

`LTE`

Less than or equal: fires when the reading is at or below the threshold.

### Field Detail

#### symbol

`private final String symbol`

### Constructor Detail

#### NotificationOperation

`NotificationOperation(String symbol)`

### Method Detail

#### getSymbol

`public String getSymbol()`

Returns the comparison symbol shown in the UI.

- **Returns:** the operator symbol (e.g. `">="`)

#### test

`public boolean test(double current, double target)`

Applies this operator to a metric reading.

- **Parameter** `current` — the metric's current value
- **Parameter** `target` — the rule's threshold
- **Returns:** `true` if `current` satisfies the operator against `target`

#### toString

`public String toString()`

Returns the comparison symbol.

- **Returns:** the operator symbol

---

## com.oveduumnakal.NotificationRule

_class_

`public class NotificationRule`

One user-defined alert condition on a tracked item: when `metric` over
`timeWindow`, compared with `operation` against `value`,
holds true.

<p>`value` is stored as the raw text the user typed (e.g. `"5m"`,
`"10%"`, or a category like `"High"`); the static helpers here
parse that text into comparable numbers for evaluation and format numbers back
for display.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private transient Boolean` | `lastCondition` | The condition's result at the previous evaluation, used for edge-triggered re-arming of repeat rules; `null` until first evaluated after a (re)load, so a standing-true condition doesn't re-fire on every login. |
| `private NotificationMetric` | `metric` |  |
| `private NotificationOperation` | `operation` |  |
| `private boolean` | `repeat` | Whether the rule re-arms after firing (repeat) instead of being removed (once). |
| `private TimeWindow` | `timeWindow` |  |
| `private String` | `value` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public static String` | `formatPercent(double value)` | Formats a percent as `NN%` when whole, otherwise `NN.N%`. |
| `public static OptionalDouble` | `parseNumeric(String text)` | Parses a numeric threshold, accepting commas and a k/m/b suffix (e.g. |
| `public static OptionalDouble` | `parsePercent(String text)` | Parses a percent threshold. |

### Field Detail

#### lastCondition

`private transient Boolean lastCondition`

The condition's result at the previous evaluation, used for edge-triggered
re-arming of repeat rules; `null` until first evaluated after a (re)load,
so a standing-true condition doesn't re-fire on every login. Transient: never
persisted.

#### metric

`private NotificationMetric metric`

#### operation

`private NotificationOperation operation`

#### repeat

`private boolean repeat`

Whether the rule re-arms after firing (repeat) instead of being removed (once).

#### timeWindow

`private TimeWindow timeWindow`

#### value

`private String value`

### Method Detail

#### formatPercent

`public static String formatPercent(double value)`

Formats a percent as `NN%` when whole, otherwise `NN.N%`.

#### parseNumeric

`public static OptionalDouble parseNumeric(String text)`

Parses a numeric threshold, accepting commas and a k/m/b suffix
(e.g. `"1,500"`, `"5m"` &rarr; 5,000,000).

- **Parameter** `text` — the raw user input
- **Returns:** the parsed value, or empty if blank or unparseable

#### parsePercent

`public static OptionalDouble parsePercent(String text)`

Parses a percent threshold. A trailing `%` is optional; when omitted,
a bare fraction below 1 (e.g. `0.05`) is treated as 5%.

- **Parameter** `text` — the raw user input
- **Returns:** the percent value, or empty if blank or unparseable

---

## com.oveduumnakal.NotificationValueEditor

_class_

`class NotificationValueEditor`

Cell editor for a notification rule's value column that adapts to the row's
metric: a dropdown of allowed options for categorical metrics, or a free-text
field for numeric/percent ones.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private JComponent` | `active` |  |
| `private final JComboBox<String>` | `combo` |  |
| `private final IntSupplier` | `detailItemId` |  |
| `private final JTextField` | `field` |  |
| `private final IntFunction<TrackedItem>` | `itemLookup` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `NotificationValueEditor(IntFunction<TrackedItem> itemLookup, IntSupplier detailItemId)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public Object` | `getCellEditorValue()` |  |
| `public Component` | `getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)` |  |

### Field Detail

#### active

`private JComponent active`

#### combo

`private final JComboBox<String> combo`

#### detailItemId

`private final IntSupplier detailItemId`

#### field

`private final JTextField field`

#### itemLookup

`private final IntFunction<TrackedItem> itemLookup`

### Constructor Detail

#### NotificationValueEditor

`NotificationValueEditor(IntFunction<TrackedItem> itemLookup, IntSupplier detailItemId)`

### Method Detail

#### getCellEditorValue

`public Object getCellEditorValue()`

#### getTableCellEditorComponent

`public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)`

---

## com.oveduumnakal.NotificationsTableModel

_class_

`class NotificationsTableModel`

Swing table model backing the notification rules: one row per
`NotificationRule` with metric, timeframe, operator, and value columns.
Editing a cell mutates the rule and notifies the plugin to persist it.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final String[]` | `COLS` |  |
| `private TrackedItem` | `item` |  |
| `private final Runnable` | `notifyEdited` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `NotificationsTableModel(Runnable notifyEdited)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `private void` | `applyValueEdit(NotificationRule rule, String raw)` | Normalises an edited value into the rule: categorical values are stored as typed, while percent and numeric inputs are parsed and reformatted (e.g. |
| `public Class<?>` | `getColumnClass(int c)` |  |
| `public int` | `getColumnCount()` |  |
| `public String` | `getColumnName(int c)` |  |
| `public int` | `getRowCount()` |  |
| `public Object` | `getValueAt(int r, int c)` |  |
| `public boolean` | `isCellEditable(int r, int c)` |  |
| `void` | `setItem(TrackedItem item)` |  |
| `public void` | `setValueAt(Object value, int r, int c)` |  |

### Field Detail

#### COLS

`private static final String[] COLS`

#### item

`private TrackedItem item`

#### notifyEdited

`private final Runnable notifyEdited`

### Constructor Detail

#### NotificationsTableModel

`NotificationsTableModel(Runnable notifyEdited)`

### Method Detail

#### applyValueEdit

`private void applyValueEdit(NotificationRule rule, String raw)`

Normalises an edited value into the rule: categorical values are stored as
typed, while percent and numeric inputs are parsed and reformatted
(e.g. `"5000000"` &rarr; `"5m"`), ignored when unparseable.

#### getColumnClass

`public Class<?> getColumnClass(int c)`

- **Returns:** `Boolean` for the repeat column so the table renders/edits it as a checkbox.

#### getColumnCount

`public int getColumnCount()`

#### getColumnName

`public String getColumnName(int c)`

#### getRowCount

`public int getRowCount()`

#### getValueAt

`public Object getValueAt(int r, int c)`

#### isCellEditable

`public boolean isCellEditable(int r, int c)`

#### setItem

`void setItem(TrackedItem item)`

#### setValueAt

`public void setValueAt(Object value, int r, int c)`

---

## com.oveduumnakal.OverlayLayout

_enum_

`public enum OverlayLayout`

How items are drawn in the on-screen tracked-items overlay &ndash; the dense
two-row `#COMPACT` layout, or a `#STANDARD` replica of the panel's
standard row. The `label` is the name shown in the config dropdown.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `COMPACT` | The `"Compact"` option. |
| `STANDARD` | The `"Standard"` option. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `label` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `OverlayLayout(String label)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public String` | `toString()` | Returns the display label shown in the UI. |

### Enum Constant Detail

#### COMPACT

`COMPACT`

The `"Compact"` option.

#### STANDARD

`STANDARD`

The `"Standard"` option.

### Field Detail

#### label

`private final String label`

### Constructor Detail

#### OverlayLayout

`OverlayLayout(String label)`

### Method Detail

#### toString

`public String toString()`

Returns the display label shown in the UI.

- **Returns:** the display label

---

## com.oveduumnakal.OverviewPreset

_enum_

`public enum OverviewPreset`

A named selection of `TimeWindow`s shown as columns in the price
overview grid, ranging from a short `#RECENT` set to the full
`#DETAILED` set. Each preset carries a display `label` and its
immutable set of windows (see `#getWindows()`).

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `DETAILED` | The `"Detailed"` option. |
| `RECENT` | The `"Recent"` option. |
| `STANDARD` | The `"Standard"` option. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `label` |  |
| `private final Set<TimeWindow>` | `windows` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `OverviewPreset(String label, Set<TimeWindow> windows)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public Set<TimeWindow>` | `getWindows()` | Returns the time windows shown by this preset. |
| `public String` | `toString()` | Returns the display label shown in the UI. |

### Enum Constant Detail

#### DETAILED

`DETAILED`

The `"Detailed"` option.

#### RECENT

`RECENT`

The `"Recent"` option.

#### STANDARD

`STANDARD`

The `"Standard"` option.

### Field Detail

#### label

`private final String label`

#### windows

`private final Set<TimeWindow> windows`

### Constructor Detail

#### OverviewPreset

`OverviewPreset(String label, Set<TimeWindow> windows)`

### Method Detail

#### getWindows

`public Set<TimeWindow> getWindows()`

Returns the time windows shown by this preset.

- **Returns:** the unmodifiable set of windows

#### toString

`public String toString()`

Returns the display label shown in the UI.

- **Returns:** the display label

---

## com.oveduumnakal.PanelActions

_interface_

`public interface PanelActions`

The plugin-facing callbacks the panel invokes, gathered into one named-method interface
(implemented by the plugin) instead of a long positional constructor parameter list (#183).
Several callbacks previously shared identical functional types &mdash; three `Runnable`s,
two `Consumer<Consumer<String>>` exporters &mdash; so transposing them at the call site
still compiled but silently wired the wrong action; a named method makes that a compile error and
lets a new feature add a method rather than another positional lambda.

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `void` | `acquisitionsEdited(int itemId)` | Notifies the plugin that `itemId`'s acquisition lots were edited and must be persisted. |
| `void` | `addItem(int itemId, TrackItemMode mode)` | Tracks `itemId`, honouring how the user asked for it to be added. |
| `void` | `clearAcquisitions(int itemId)` | Clears all acquisition lots recorded for `itemId`. |
| `void` | `clearAll()` | Stops tracking every item and clears all tracked state. |
| `String` | `examineLookup(int itemId)` |  |
| `void` | `exportCsv(Consumer<String> callback)` | Builds the acquisitions CSV and hands it back through `callback`. |
| `void` | `exportList(Consumer<String> callback)` | Builds the share token for the tracked list and hands it back through `callback`. |
| `void` | `importList(String data, Consumer<String> callback)` | Imports the tracked list encoded in `data`, reporting the outcome through `callback`. |
| `void` | `notificationsEdited(int itemId)` | Notifies the plugin that `itemId`'s notification rules were edited and must be persisted. |
| `List<long[]>` | `portfolioHistory()` |  |
| `void` | `removeItem(int itemId)` | Stops tracking `itemId` and removes it from the list entirely. |
| `void` | `reorder(int from, int to)` | Moves the item at index `from` to index `to` in the manual order. |
| `void` | `requestDetailData(int itemId)` | Requests a fresh market/detail data load for `itemId`. |
| `void` | `setFavorite(int itemId, boolean favorite)` | Sets whether `itemId` is marked as a favourite. |
| `void` | `setGlobalOrder(List<Integer> order)` | Replaces the manual ordering with the given item-id order. |
| `void` | `setGroupCollapsed(String group, boolean collapsed)` | Sets whether the category `group` is collapsed in the list. |
| `void` | `setItemCompact(int itemId, boolean compact)` | Sets whether `itemId` is displayed as a compact row. |
| `void` | `setOnOverlay(int itemId, boolean onOverlay)` | Sets whether `itemId` is shown on the in-game screen overlay. |
| `void` | `setSortMode(SortMode mode)` | Sets the active sort mode for the tracked list. |
| `void` | `toggleCompactView()` | Toggles the compact (two-row) view for the whole tracked list. |
| `void` | `toggleSortDirection()` | Flips the current sort between ascending and descending. |
| `void` | `untrackToPreview(int itemId)` | Stops tracking `itemId` but keeps it open in the detail view as a read-only preview (#138), so untracking from the detail header does not bounce back to the main list. |
| `void` | `whatsNewSeen()` | Marks the current release's "What's New" notice as seen so it stops showing. |

### Method Detail

#### acquisitionsEdited

`void acquisitionsEdited(int itemId)`

Notifies the plugin that `itemId`'s acquisition lots were edited and must be persisted.

#### addItem

`void addItem(int itemId, TrackItemMode mode)`

Tracks `itemId`, honouring how the user asked for it to be added.

#### clearAcquisitions

`void clearAcquisitions(int itemId)`

Clears all acquisition lots recorded for `itemId`.

#### clearAll

`void clearAll()`

Stops tracking every item and clears all tracked state.

#### examineLookup

`String examineLookup(int itemId)`

- **Returns:** the examine text for `itemId`, or a placeholder when none is cached.

#### exportCsv

`void exportCsv(Consumer<String> callback)`

Builds the acquisitions CSV and hands it back through `callback`.

#### exportList

`void exportList(Consumer<String> callback)`

Builds the share token for the tracked list and hands it back through `callback`.

#### importList

`void importList(String data, Consumer<String> callback)`

Imports the tracked list encoded in `data`, reporting the outcome through `callback`.

#### notificationsEdited

`void notificationsEdited(int itemId)`

Notifies the plugin that `itemId`'s notification rules were edited and must be persisted.

#### portfolioHistory

`List<long[]> portfolioHistory()`

- **Returns:** the portfolio value-history points the chart plots.

#### removeItem

`void removeItem(int itemId)`

Stops tracking `itemId` and removes it from the list entirely.

#### reorder

`void reorder(int from, int to)`

Moves the item at index `from` to index `to` in the manual order.

#### requestDetailData

`void requestDetailData(int itemId)`

Requests a fresh market/detail data load for `itemId`.

#### setFavorite

`void setFavorite(int itemId, boolean favorite)`

Sets whether `itemId` is marked as a favourite.

#### setGlobalOrder

`void setGlobalOrder(List<Integer> order)`

Replaces the manual ordering with the given item-id order.

#### setGroupCollapsed

`void setGroupCollapsed(String group, boolean collapsed)`

Sets whether the category `group` is collapsed in the list.

#### setItemCompact

`void setItemCompact(int itemId, boolean compact)`

Sets whether `itemId` is displayed as a compact row.

#### setOnOverlay

`void setOnOverlay(int itemId, boolean onOverlay)`

Sets whether `itemId` is shown on the in-game screen overlay.

#### setSortMode

`void setSortMode(SortMode mode)`

Sets the active sort mode for the tracked list.

#### toggleCompactView

`void toggleCompactView()`

Toggles the compact (two-row) view for the whole tracked list.

#### toggleSortDirection

`void toggleSortDirection()`

Flips the current sort between ascending and descending.

#### untrackToPreview

`void untrackToPreview(int itemId)`

Stops tracking `itemId` but keeps it open in the detail view as a read-only preview
(#138), so untracking from the detail header does not bounce back to the main list.

#### whatsNewSeen

`void whatsNewSeen()`

Marks the current release's "What's New" notice as seen so it stops showing.

---

## com.oveduumnakal.PopoutHandle

_class_

`final class PopoutHandle`

Tracks one open pop-out window and the refresher used to push fresh item data into it.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `final JFrame` | `frame` |  |
| `final Runnable` | `onClose` |  |
| `final Consumer<TrackedItem>` | `refresher` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `PopoutHandle(JFrame frame, Consumer<TrackedItem> refresher, Runnable onClose)` |  |

### Field Detail

#### frame

`final JFrame frame`

#### onClose

`final Runnable onClose`

#### refresher

`final Consumer<TrackedItem> refresher`

### Constructor Detail

#### PopoutHandle

`PopoutHandle(JFrame frame, Consumer<TrackedItem> refresher, Runnable onClose)`

---

## com.oveduumnakal.PortfolioChartPanel

_class_

`public final class PortfolioChartPanel`

A line chart of total portfolio value over time against a grey cost-basis line.
The value line is coloured by its position relative to cost basis — green where
it sits above (in profit), red below (in loss), grey when equal — splitting each
segment at the crossing point. Mirrors the look and feel of `PriceGraphPanel`:
a "nice" value axis with horizontal gridlines and right-side labels, rotated date
labels along the bottom, a legend, and a hover crosshair whose tooltip reports the
value, cost, and profit at the point nearest the cursor.

<p>Plots the series from `PortfolioHistory` points
(`{epochSeconds, value, costBasis`}). Consecutive points are always joined,
so an offline gap reads as one connecting segment between the two known values.

### Nested Type Summary

| Type | Description |
|---|---|
| _class_ [`TipLine`](#comoveduumnakalportfoliochartpaneltipline) | One hover-tooltip line: a muted `label` and a `value` in `valueColor` (null = label only). |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final Color` | `AXIS_TEXT` |  |
| `private static final Color` | `COST_LINE` |  |
| `private static final Color` | `CROSSHAIR` |  |
| `private static final long` | `DAY` |  |
| `private static final DateTimeFormatter` | `DAY_LABEL` |  |
| `private static final Color` | `GRID` |  |
| `private static final long` | `HOUR` |  |
| `private static final DateTimeFormatter` | `HOUR_LABEL` |  |
| `private static final int` | `LEFT_PAD` |  |
| `private static final Color` | `PROFIT_DOWN` |  |
| `private static final Color` | `PROFIT_UP` |  |
| `private static final Color` | `TOOLTIP_LABEL` |  |
| `private static final DateTimeFormatter` | `TOOLTIP_TIME` |  |
| `private static final Color` | `TOOLTIP_VALUE` |  |
| `private static final int` | `TOP_PAD` |  |
| `private static final Color` | `VALUE_DOWN` |  |
| `private static final Color` | `VALUE_FLAT` |  |
| `private static final Color` | `VALUE_UP` |  |
| `private static final int` | `X_LABEL_GAP` |  |
| `private final Font` | `baseFont` |  |
| `private int` | `hoverX` |  |
| `private transient BufferedImage` | `plotCache` | Rasterized static plot (grid, axes, legend, series); only the hover overlay is redrawn on mouse moves. |
| `private boolean` | `plotCacheDirty` |  |
| `private List<long[]>` | `points` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `PortfolioChartPanel()` | Creates an empty portfolio chart panel. |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `private List<Long>` | `buildTimeTicks(long minTime, long maxTime, int target)` | Builds evenly spaced time ticks snapped to natural boundaries: whole days for a span of two days or more (else whole hours), with the step widened so at most `target` ticks fall in the visible range. |
| `private int` | `closestIndex(int plotLeft, int plotW, long minTime, long maxTime)` |  |
| `private static Color` | `diffColor(long diff)` |  |
| `private void` | `drawCentered(Graphics2D g2, String text, int width, int height)` |  |
| `private void` | `drawCostLine(Graphics2D g2, int left, int top, int bottom, int plotW, int plotH, long minTime, long maxTime, double axisMin, double axisRange)` | Draws the grey cost-basis line, joining consecutive points and breaking where cost basis is absent. |
| `private void` | `drawHover(Graphics2D g2, FontMetrics fm, int plotLeft, int plotTop, int plotBottom, int plotW, int plotH, long minTime, long maxTime, double axisMin, double axisRange)` | Draws the hover overlay: a vertical crosshair at the cursor, dots on the value and cost lines at the nearest point, and a tooltip box with its date, value, cost, and unrealized profit. |
| `private void` | `drawLegend(Graphics2D g2, FontMetrics fm, int left, boolean anyCost)` | Draws the legend: with cost basis present, a grey "Cost basis" line and the green "Profit" / red "Loss" states of the value line; otherwise just a neutral "Value" swatch. |
| `private int` | `drawLegendEntry(Graphics2D g2, FontMetrics fm, int x, int y, Color color, String label)` | Draws one legend swatch + label starting at `x`; returns the x just past it. |
| `private void` | `drawTooltip(Graphics2D g2, FontMetrics fm, List<TipLine> lines, int plotLeft, int plotTop, int plotRight)` | Draws the hover tooltip box, flipping to the cursor's left near the right edge. |
| `private void` | `drawValueLine(Graphics2D g2, int left, int top, int bottom, int plotW, int plotH, long minTime, long maxTime, double axisMin, double axisRange)` | Draws the value line, colouring each segment by the value's position relative to cost basis — green above (profit), red below (loss), grey when equal or when no cost basis exists — and splitting a segment at the point where the two lines cross. |
| `private void` | `drawVerticalLabel(Graphics2D g2, String s, int cx, int topY, FontMetrics fm)` | Draws a string rotated 90° (reading bottom-to-top) hanging below the axis at `cx`. |
| `private void` | `drawXAxis(Graphics2D g2, FontMetrics fm, int left, int bottom, int plotW, long minTime, long maxTime)` | Draws faint vertical gridlines and rotated date labels at "nice" time ticks along the bottom. |
| `private void` | `drawYAxis(Graphics2D g2, FontMetrics fm, int left, int right, int top, int bottom, int plotH, double axisMin, double axisRange, int ticks)` | Draws the horizontal gridlines and their right-side value labels for the "nice" value axis. |
| `private static double[]` | `niceAxis(long dataMin, long dataMax, int minTicks, int maxTicks)` | Picks a human-friendly value axis covering `[dataMin, dataMax]` using a 1/2/2.5/5 step progression so labels land on round numbers. |
| `protected void` | `paintComponent(Graphics g)` | Paints the chart: the expensive static plot (grid, axes, legend, series) is rasterized once into `#plotCache` and reused, while only the lightweight hover crosshair is redrawn over it on mouse moves. |
| `public void` | `setData(List<long[]> data)` | Sets the points to plot (`{epochSeconds, value, costBasis`}) and repaints. |
| `private static int` | `valueY(long value, double axisMin, double axisRange, int top, int bottom, int plotH)` | Maps a value to its y pixel within the plot, clamped to the plot bounds. |

### Field Detail

#### AXIS_TEXT

`private static final Color AXIS_TEXT`

#### COST_LINE

`private static final Color COST_LINE`

#### CROSSHAIR

`private static final Color CROSSHAIR`

#### DAY

`private static final long DAY`

#### DAY_LABEL

`private static final DateTimeFormatter DAY_LABEL`

#### GRID

`private static final Color GRID`

#### HOUR

`private static final long HOUR`

#### HOUR_LABEL

`private static final DateTimeFormatter HOUR_LABEL`

#### LEFT_PAD

`private static final int LEFT_PAD`

#### PROFIT_DOWN

`private static final Color PROFIT_DOWN`

#### PROFIT_UP

`private static final Color PROFIT_UP`

#### TOOLTIP_LABEL

`private static final Color TOOLTIP_LABEL`

#### TOOLTIP_TIME

`private static final DateTimeFormatter TOOLTIP_TIME`

#### TOOLTIP_VALUE

`private static final Color TOOLTIP_VALUE`

#### TOP_PAD

`private static final int TOP_PAD`

#### VALUE_DOWN

`private static final Color VALUE_DOWN`

#### VALUE_FLAT

`private static final Color VALUE_FLAT`

#### VALUE_UP

`private static final Color VALUE_UP`

#### X_LABEL_GAP

`private static final int X_LABEL_GAP`

#### baseFont

`private final Font baseFont`

#### hoverX

`private int hoverX`

#### plotCache

`private transient BufferedImage plotCache`

Rasterized static plot (grid, axes, legend, series); only the hover overlay is redrawn on mouse moves.

#### plotCacheDirty

`private boolean plotCacheDirty`

#### points

`private List<long[]> points`

### Constructor Detail

#### PortfolioChartPanel

`public PortfolioChartPanel()`

Creates an empty portfolio chart panel.

### Method Detail

#### buildTimeTicks

`private List<Long> buildTimeTicks(long minTime, long maxTime, int target)`

Builds evenly spaced time ticks snapped to natural boundaries: whole days for a
span of two days or more (else whole hours), with the step widened so at most
`target` ticks fall in the visible range.

- **Returns:** tick timestamps in epoch seconds within `[minTime, maxTime]`

#### closestIndex

`private int closestIndex(int plotLeft, int plotW, long minTime, long maxTime)`

- **Returns:** the index of the point whose x pixel is nearest `#hoverX`, or -1 if none.

#### diffColor

`private static Color diffColor(long diff)`

- **Returns:** green when `diff` (value − cost) is positive, red when negative, grey when zero.

#### drawCentered

`private void drawCentered(Graphics2D g2, String text, int width, int height)`

#### drawCostLine

`private void drawCostLine(Graphics2D g2, int left, int top, int bottom, int plotW, int plotH, long minTime, long maxTime, double axisMin, double axisRange)`

Draws the grey cost-basis line, joining consecutive points and breaking where cost basis is absent.

#### drawHover

`private void drawHover(Graphics2D g2, FontMetrics fm, int plotLeft, int plotTop, int plotBottom, int plotW, int plotH, long minTime, long maxTime, double axisMin, double axisRange)`

Draws the hover overlay: a vertical crosshair at the cursor, dots on the value and
cost lines at the nearest point, and a tooltip box with its date, value, cost, and
unrealized profit.

#### drawLegend

`private void drawLegend(Graphics2D g2, FontMetrics fm, int left, boolean anyCost)`

Draws the legend: with cost basis present, a grey "Cost basis" line and the green
"Profit" / red "Loss" states of the value line; otherwise just a neutral "Value" swatch.

#### drawLegendEntry

`private int drawLegendEntry(Graphics2D g2, FontMetrics fm, int x, int y, Color color, String label)`

Draws one legend swatch + label starting at `x`; returns the x just past it.

#### drawTooltip

`private void drawTooltip(Graphics2D g2, FontMetrics fm, List<TipLine> lines, int plotLeft, int plotTop, int plotRight)`

Draws the hover tooltip box, flipping to the cursor's left near the right edge. Each
line's label is drawn muted and its value in the line's own colour, so the numbers
stand out from the labels (and profit reads green/red).

#### drawValueLine

`private void drawValueLine(Graphics2D g2, int left, int top, int bottom, int plotW, int plotH, long minTime, long maxTime, double axisMin, double axisRange)`

Draws the value line, colouring each segment by the value's position relative to cost
basis — green above (profit), red below (loss), grey when equal or when no cost basis
exists — and splitting a segment at the point where the two lines cross.

#### drawVerticalLabel

`private void drawVerticalLabel(Graphics2D g2, String s, int cx, int topY, FontMetrics fm)`

Draws a string rotated 90° (reading bottom-to-top) hanging below the axis at `cx`.

#### drawXAxis

`private void drawXAxis(Graphics2D g2, FontMetrics fm, int left, int bottom, int plotW, long minTime, long maxTime)`

Draws faint vertical gridlines and rotated date labels at "nice" time ticks along the bottom.

#### drawYAxis

`private void drawYAxis(Graphics2D g2, FontMetrics fm, int left, int right, int top, int bottom, int plotH, double axisMin, double axisRange, int ticks)`

Draws the horizontal gridlines and their right-side value labels for the "nice" value axis.

#### niceAxis

`private static double[] niceAxis(long dataMin, long dataMax, int minTicks, int maxTicks)`

Picks a human-friendly value axis covering `[dataMin, dataMax]` using a
1/2/2.5/5 step progression so labels land on round numbers.

- **Returns:** `[axisMin, axisMax, ticks]`

#### paintComponent

`protected void paintComponent(Graphics g)`

Paints the chart: the expensive static plot (grid, axes, legend, series) is
rasterized once into `#plotCache` and reused, while only the lightweight
hover crosshair is redrawn over it on mouse moves. The cheap layout is recomputed
each paint so the hover overlay maps correctly onto the cached pixels.

#### setData

`public void setData(List<long[]> data)`

Sets the points to plot (`{epochSeconds, value, costBasis`}) and repaints.

#### valueY

`private static int valueY(long value, double axisMin, double axisRange, int top, int bottom, int plotH)`

Maps a value to its y pixel within the plot, clamped to the plot bounds.

---

## com.oveduumnakal.PortfolioChartPanel.TipLine

_class_

`private static final class TipLine`

One hover-tooltip line: a muted `label` and a `value` in `valueColor` (null = label only).

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `label` |  |
| `private final String` | `value` |  |
| `private final Color` | `valueColor` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `TipLine(String label, String value, Color valueColor)` |  |

### Field Detail

#### label

`private final String label`

#### value

`private final String value`

#### valueColor

`private final Color valueColor`

### Constructor Detail

#### TipLine

`private TipLine(String label, String value, Color valueColor)`

---

## com.oveduumnakal.PortfolioHistory

_class_

`public final class PortfolioHistory`

Per-item time series of portfolio value and cost basis for the "portfolio value
over time" chart. Each tracked item keeps its own thinned series of
`{epochSeconds, value, costBasis`} points; the chart line is the sum across
the stored items at each timestamp (`#aggregate()`).

<p>Keeping the data per item (rather than one aggregate series) means removing an
item drops exactly its contribution from every past point, and an item added
mid-history only affects points from when it was first recorded — the aggregate is
always consistent with the set of items currently stored.

<p>Config size is bounded per item: recent history is kept at hourly resolution for
the last {@value #HOURLY_HOURS} hours (≈ 7 days) and older history is collapsed to
one point per {@value #BUCKET_HOURS}-hour bucket, up to {@value #RETENTION_DAYS}
days; anything older is dropped. Points are `long[]` so persistence is a plain
map of primitive lists with no schema shape to guard.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `static final int` | `BUCKET_HOURS` | Bucket width, in hours, that history older than the hourly window is thinned to. |
| `private static final long` | `DAY` |  |
| `private static final long` | `HOUR` |  |
| `static final int` | `HOURLY_HOURS` | Hours of recent history kept at one-point-per-hour resolution (7 days). |
| `static final int` | `RETENTION_DAYS` | Days of history retained before points are dropped. |
| `private final Map<Integer,List<long[]>>` | `series` | itemId → that item's thinned series of `{epochSeconds, value, costBasis`}. |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public List<long[]>` | `aggregate()` |  |
| `public void` | `clear()` | Drops all stored series, e.g. |
| `public boolean` | `isEmpty()` |  |
| `public void` | `load(Map<Integer,List<long[]>> stored)` | Replaces all series with `stored` (as loaded from config); ignores malformed entries. |
| `public void` | `record(long epochSeconds, Map<Integer,long[]> perItem)` | Records a snapshot at `epochSeconds` for each item in `perItem` (id → `{value, costBasis`}). |
| `public void` | `removeItem(int itemId)` | Drops the series for `itemId` (e.g. |
| `public Map<Integer,List<long[]>>` | `seriesByItem()` |  |
| `private void` | `thin(List<long[]> points, long nowSeconds)` | Collapses points older than the hourly window to one per {@value #BUCKET_HOURS}-hour bucket (keeping the latest in each bucket) and drops points beyond the retention window. |

### Field Detail

#### BUCKET_HOURS

`static final int BUCKET_HOURS`

Bucket width, in hours, that history older than the hourly window is thinned to.

#### DAY

`private static final long DAY`

#### HOUR

`private static final long HOUR`

#### HOURLY_HOURS

`static final int HOURLY_HOURS`

Hours of recent history kept at one-point-per-hour resolution (7 days).

#### RETENTION_DAYS

`static final int RETENTION_DAYS`

Days of history retained before points are dropped.

#### series

`private final Map<Integer,List<long[]>> series`

itemId → that item's thinned series of `{epochSeconds, value, costBasis`}.

### Method Detail

#### aggregate

`public List<long[]> aggregate()`

- **Returns:** the aggregate chart series `{epochSeconds, totalValue, totalCostBasis`}:
        at each timestamp any stored item was recorded, the summed value and cost of
        the items recorded there. Chronological order.

#### clear

`public void clear()`

Drops all stored series, e.g. when the tracked list is emptied.

#### isEmpty

`public boolean isEmpty()`

- **Returns:** whether any item has stored points.

#### load

`public void load(Map<Integer,List<long[]>> stored)`

Replaces all series with `stored` (as loaded from config); ignores malformed entries.

#### record

`public void record(long epochSeconds, Map<Integer,long[]> perItem)`

Records a snapshot at `epochSeconds` for each item in `perItem`
(id → `{value, costBasis`}). Within the same hour as an item's latest point
the point is updated in place (so a burst of refreshes yields one hourly point);
otherwise a new point is appended. Each touched series is then thinned.

#### removeItem

`public void removeItem(int itemId)`

Drops the series for `itemId` (e.g. when it is untracked) so it leaves the aggregate.

#### seriesByItem

`public Map<Integer,List<long[]>> seriesByItem()`

- **Returns:** the per-item series for persistence (defensive copy).

#### thin

`private void thin(List<long[]> points, long nowSeconds)`

Collapses points older than the hourly window to one per {@value #BUCKET_HOURS}-hour
bucket (keeping the latest in each bucket) and drops points beyond the retention window.

---

## com.oveduumnakal.PortfolioShareCodec

_class_

`public final class PortfolioShareCodec`

Serializes a tracked list (item ids, modes, categories, favorites and the
category definitions) to a single compact, shareable token and back.

<p>The token is `#PREFIX` followed by a URL-safe Base64 encoding of the
gzipped JSON, so a whole watchlist pastes as one line into chat. `#decode`
also accepts the raw JSON directly (for hand-editing/debugging), detected by a
leading brace. Decoding is defensive: any malformed input yields `null`
rather than throwing, so an import dialog can report a friendly error.

### Nested Type Summary

| Type | Description |
|---|---|
| _class_ [`Entry`](#comoveduumnakalportfoliosharecodecentry) | One shared tracked item: its id, tracking mode, category (nullable) and favorite flag. |
| _class_ [`Snapshot`](#comoveduumnakalportfoliosharecodecsnapshot) | The exported watchlist: the tracked entries plus the category definitions to recreate. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `static final String` | `PREFIX` | Token marker + format version; a future breaking change bumps the digit. |
| `private final Gson` | `gson` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `PortfolioShareCodec(Gson gson)` | Creates a codec that (de)serializes share tokens with the given Gson instance. |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public Snapshot` | `decode(String input)` | Parses a token (or raw JSON) back into a snapshot. |
| `public String` | `encode(Snapshot snapshot)` |  |
| `private String` | `inflate(String token)` | Base64-decodes and gunzips a token body; `null` on any corruption. |

### Field Detail

#### PREFIX

`static final String PREFIX`

Token marker + format version; a future breaking change bumps the digit.

#### gson

`private final Gson gson`

### Constructor Detail

#### PortfolioShareCodec

`public PortfolioShareCodec(Gson gson)`

Creates a codec that (de)serializes share tokens with the given Gson instance.

- **Parameter** `gson` — the Gson instance used for JSON encoding

### Method Detail

#### decode

`public Snapshot decode(String input)`

Parses a token (or raw JSON) back into a snapshot.

- **Returns:** the decoded snapshot, or `null` if the input is blank, not a
        recognizable token, or fails to parse.

#### encode

`public String encode(Snapshot snapshot)`

- **Returns:** the shareable token for `snapshot`, never `null`.

#### inflate

`private String inflate(String token)`

Base64-decodes and gunzips a token body; `null` on any corruption.

---

## com.oveduumnakal.PortfolioShareCodec.Entry

_class_

`public static class Entry`

One shared tracked item: its id, tracking mode, category (nullable) and favorite flag.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `String` | `category` |  |
| `boolean` | `favorite` |  |
| `int` | `id` |  |
| `TrackItemMode` | `mode` |  |

### Field Detail

#### category

`String category`

#### favorite

`boolean favorite`

#### id

`int id`

#### mode

`TrackItemMode mode`

---

## com.oveduumnakal.PortfolioShareCodec.Snapshot

_class_

`public static class Snapshot`

The exported watchlist: the tracked entries plus the category definitions to recreate.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `List<CategoryState>` | `categories` |  |
| `List<Entry>` | `items` |  |
| `int` | `v` | Format version, for forward-compatible decoding. |

### Field Detail

#### categories

`List<CategoryState> categories`

#### items

`List<Entry> items`

#### v

`int v`

Format version, for forward-compatible decoding.

---

## com.oveduumnakal.PressureVolumeLabel

_class_

`final class PressureVolumeLabel`

Buy/Sell pressure label of the form `"55% Buy (550)"` whose short-format volume
parenthetical reveals the full number in a tooltip when hovered.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private long` | `volume` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `PressureVolumeLabel()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public String` | `getToolTipText(MouseEvent event)` | Shows the full-volume tooltip only while the pointer is over the parenthetical, measuring the rendered text with font metrics to find its on-screen extent. |
| `void` | `setVolume(long volume)` |  |

### Field Detail

#### volume

`private long volume`

### Constructor Detail

#### PressureVolumeLabel

`PressureVolumeLabel()`

### Method Detail

#### getToolTipText

`public String getToolTipText(MouseEvent event)`

Shows the full-volume tooltip only while the pointer is over the parenthetical,
measuring the rendered text with font metrics to find its on-screen extent.

#### setVolume

`void setVolume(long volume)`

---

## com.oveduumnakal.PressureWindow

_enum_

`public enum PressureWindow`

The look-back period for the Buy/Sell Pressure block, backed by a
`TimeWindow` that supplies both the aggregation `duration` and the
price-history granularity used to sum instant-buy vs instant-sell volume. The
`label` is the name shown in the config dropdown.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `DAY` | The `"24 Hours"` option. |
| `MONTH` | The `"1 Month"` option. |
| `WEEK` | The `"1 Week"` option. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `label` |  |
| `private final TimeWindow` | `window` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `PressureWindow(String label, TimeWindow window)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public Duration` | `duration()` | Returns the look-back duration of this pressure period. |
| `public String` | `toString()` | Returns the display label shown in the UI. |
| `public TimeWindow` | `window()` | Returns the time window this pressure period maps to. |

### Enum Constant Detail

#### DAY

`DAY`

The `"24 Hours"` option.

#### MONTH

`MONTH`

The `"1 Month"` option.

#### WEEK

`WEEK`

The `"1 Week"` option.

### Field Detail

#### label

`private final String label`

#### window

`private final TimeWindow window`

### Constructor Detail

#### PressureWindow

`PressureWindow(String label, TimeWindow window)`

### Method Detail

#### duration

`public Duration duration()`

Returns the look-back duration of this pressure period.

- **Returns:** the duration of the backing time window

#### toString

`public String toString()`

Returns the display label shown in the UI.

- **Returns:** the display label

#### window

`public TimeWindow window()`

Returns the time window this pressure period maps to.

- **Returns:** the backing `TimeWindow`

---

## com.oveduumnakal.PriceGraphPanel

_class_

`public class PriceGraphPanel`

Swing component that draws an item's price or volume history as a line/area
chart with a timeframe tab bar, optional smoothing, and a hover crosshair.

<p>The same class serves both the compact in-panel chart and a larger
`expanded` pop-out (which uses a bigger font, denser axes, and spelled-out
tab labels). It holds four pre-bucketed series (5m/1h/6h/24h) and picks the one
matching the active `TimeWindow`.

<p>Rendering is split in two for performance: the expensive static plot (grid,
axes, data paths, smoothing) is rasterized once into `#plotCache` and
reused, while only the lightweight hover crosshair is redrawn on mouse moves.
All drawing happens on the Swing EDT via `#paintComponent`.

### Nested Type Summary

| Type | Description |
|---|---|
| _enum_ [`LineSet`](#comoveduumnakalpricegraphpanellineset) | Which price lines to draw: all three, just high/low, or just the average. |
| _enum_ [`Mode`](#comoveduumnakalpricegraphpanelmode) | Whether this panel charts prices or trade volume. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final Color` | `BG_COLOR` |  |
| `private static final Color` | `COLOR_AVG` |  |
| `private static final Color` | `COLOR_HIGH` |  |
| `private static final Color` | `COLOR_LOW` |  |
| `private static final Color` | `COLOR_NEUTRAL` |  |
| `private static final Color` | `CURRENT_LINE_COLOR` |  |
| `private static final Color` | `GRID_COLOR` |  |
| `private static final int` | `LEFT_PAD` |  |
| `private static final Color` | `MA_COLOR` |  |
| `private static final NumberFormat` | `NUMBER_FORMAT` |  |
| `private static final Color` | `SEPARATOR_COLOR` |  |
| `private static final int` | `TAB_BAR_HEIGHT` |  |
| `private static final TimeWindow[]` | `TIMEFRAMES` |  |
| `private static final String[]` | `TIMEFRAME_LABELS` |  |
| `private static final String[]` | `TIMEFRAME_LABELS_FULL` |  |
| `private static final int` | `TOP_PAD` |  |
| `private static final Color` | `VOLUME_COLOR` |  |
| `private static final Color` | `VOLUME_OVER_COLOR` |  |
| `private static final int` | `X_AXIS_LABEL_GAP` |  |
| `private TimeWindow` | `activeWindow` |  |
| `private final Font` | `baseFont` |  |
| `private final int` | `bottomAxisHeight` |  |
| `private long` | `currentPrice` |  |
| `private final boolean` | `expanded` |  |
| `private int` | `hoverX` |  |
| `private LineSet` | `lineSet` |  |
| `private Consumer<LineSet>` | `lineSetListener` |  |
| `private JPanel` | `linesToggle` |  |
| `private MouseAdapter` | `linesToggleClick` |  |
| `private final Mode` | `mode` |  |
| `private transient BufferedImage` | `plotCache` |  |
| `private boolean` | `plotCacheDirty` |  |
| `private final int` | `rightAxisWidth` |  |
| `private List<WikiRealtimePriceClient.PricePoint>` | `series1h` |  |
| `private List<WikiRealtimePriceClient.PricePoint>` | `series24h` |  |
| `private List<WikiRealtimePriceClient.PricePoint>` | `series5m` |  |
| `private List<WikiRealtimePriceClient.PricePoint>` | `series6h` |  |
| `private boolean` | `smooth` |  |
| `private Consumer<Boolean>` | `smoothListener` |  |
| `private JLabel` | `smoothToggle` |  |
| `private final List<JLabel>` | `tabLabels` |  |
| `private final JPanel` | `tabsBar` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `PriceGraphPanel()` | Builds a sidebar-sized price chart. |
| `PriceGraphPanel(Mode mode)` | Builds a sidebar-sized chart in the given mode. |
| `PriceGraphPanel(Mode mode, boolean expanded)` | Builds the chart and its timeframe tab bar. |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `private Path2D` | `buildSeriesPath(int[] xs, int[] ys, int n)` | Builds the connecting path for a series of `n` screen points: a smooth monotone cubic when `#smooth` is set, otherwise straight segments. |
| `private List<long[]>` | `buildXTicks(long startSec, long endSec)` | Builds the x-axis tick timestamps for the active window, snapped to natural boundaries (months for a year, days/weeks for a month, days for a week, rounded hours for a day) and denser in the expanded pop-out. |
| `private static void` | `clipMarker(Graphics2D g2, int cx, long value, double axisMin, double axisMax, int plotTop, int plotBottom, Color color)` | Draws a small triangle at the top or bottom plot edge marking a data point that falls outside the visible value axis (so off-scale spikes are still visible). |
| `private int` | `closestIndex(List<WikiRealtimePriceClient.PricePoint> points, int plotLeft, int plotW, long startSec, long span)` |  |
| `private List<WikiRealtimePriceClient.PricePoint>` | `collectVisible(long startSec, long endSec)` |  |
| `private void` | `cycleLineSet()` | Advances the line set to the next option in the cycle and notifies the listener. |
| `private void` | `drawHover(Graphics2D g2, FontMetrics fm, List<WikiRealtimePriceClient.PricePoint> visible, int plotLeft, int plotTop, int plotRight, int plotBottom, int plotW, long startSec, long span)` | Draws the hover overlay: a vertical crosshair at the cursor and a tooltip describing the data point nearest the cursor's x position. |
| `private void` | `drawTooltip(Graphics2D g2, FontMetrics fm, String[] lines, int plotLeft, int plotTop, int plotRight)` | Draws the hover tooltip box of text lines, flipping to the cursor's left near the right edge. |
| `private void` | `drawVerticalLabel(Graphics2D g2, String s, int cx, int topY, FontMetrics fm)` | Draws a string rotated 90° (reading bottom-to-top) hanging below the axis at `cx`. |
| `private void` | `drawXAxis(Graphics2D g2, FontMetrics fm, int plotLeft, int plotBottom, int plotW, long startSec, long endSec)` | Draws the x-axis date/time labels at the tick positions for the active window. |
| `private static double[]` | `ema(double[] values, int period)` | Computes an exponential moving average over `values`. |
| `public TimeWindow` | `getActiveWindow()` | Returns the time window currently charted. |
| `private String` | `labelForTick(long tsSec)` | Formats a tick timestamp as an axis label appropriate to the active window (time of day vs. |
| `private static long` | `midpoint(WikiRealtimePriceClient.PricePoint p)` |  |
| `private static Path2D` | `monotoneCubic(int[] xsIn, int[] ysIn)` | Builds a Fritsch–Carlson monotone cubic Hermite spline through the points. |
| `private static double[]` | `movingAverage(int[] ys, int n, int window)` | Computes a centered simple moving average of the first `n` y-values. |
| `private static double[]` | `niceAxis(long dataMin, long dataMax, int minTicks, int maxTicks)` | Picks a human-friendly value axis covering `[dataMin, dataMax]` using a 1/2/2.5/5/10 step progression so labels land on round numbers. |
| `private static long` | `niceVolumeStep(long target, int intervals)` |  |
| `protected void` | `paintComponent(Graphics g)` | Paints the chart: computes the plot rectangle and visible window, lazily (re)rasterizes the static plot into `#plotCache` when size or data changed, blits the cache, then draws the hover crosshair overlay on top. |
| `private void` | `paintPrice(Graphics2D g2, FontMetrics fm, List<WikiRealtimePriceClient.PricePoint> visible, int plotLeft, int plotTop, int plotRight, int plotBottom, int plotW, int plotH, long startSec, long span)` | Draws the price chart: computes a "nice" value axis from the visible range, paints the horizontal grid and y-axis labels, the current-price reference line, and the selected high/low/average series (raw, smoothed, or with a moving average per `#smooth` and `#lineSet`). |
| `private void` | `paintVolume(Graphics2D g2, FontMetrics fm, List<WikiRealtimePriceClient.PricePoint> visible, int plotLeft, int plotTop, int plotRight, int plotBottom, int plotW, int plotH, long startSec, long span)` | Draws the volume chart: combined high+low traded volume as filled bars/area with a "nice" volume axis and labels. |
| `private static long` | `percentile(List<Long> values, double p)` |  |
| `private static int` | `priceY(long value, double axisMin, double axisRange, int plotTop, int plotBottom, int plotH)` | Maps a price to its y pixel within the plot, clamped to the plot bounds. |
| `private void` | `rebuildLinesToggle()` | Rebuilds the line-set toggle as a row of per-letter labels coloured to match the lines they represent (High green, Low red, Avg gold), so the active set is obvious at a glance: ALL = green/gold/red, H/L = green/red, AVG = gold. |
| `private void` | `renderStatic(Graphics2D g2, FontMetrics fm, int w, List<WikiRealtimePriceClient.PricePoint> visible, int plotLeft, int plotTop, int plotRight, int plotBottom, int plotW, int plotH, long startSec, long endSec, long span)` | Renders the full static plot into the cache image: the tab separator, then either a "No data" message or the price/volume series with its axes. |
| `private List<WikiRealtimePriceClient.PricePoint>` | `seriesForActiveWindow()` |  |
| `public void` | `setActiveWindow(TimeWindow w)` | Selects the displayed timeframe (defaulting to 24h), refreshes the tab highlight, and repaints. |
| `public void` | `setData(List<WikiRealtimePriceClient.PricePoint> series5m, List<WikiRealtimePriceClient.PricePoint> series1h, List<WikiRealtimePriceClient.PricePoint> series6h, List<WikiRealtimePriceClient.PricePoint> series24h, long currentPrice)` | Replaces the chart's data with fresh per-bucket series and the latest price, then invalidates the plot cache and repaints. |
| `public void` | `setLineSet(LineSet set)` | Sets which price lines are drawn (all / high-low / average) and invalidates the plot cache. |
| `public void` | `setLineSetListener(Consumer<LineSet> listener)` | Registers a callback fired when the user changes the line set, so a sibling chart can stay in sync. |
| `public void` | `setSmooth(boolean s)` | Toggles spline smoothing of the data lines and invalidates the plot cache. |
| `public void` | `setSmoothListener(Consumer<Boolean> listener)` | Registers a callback fired when the user toggles smoothing, so a sibling chart can stay in sync. |
| `private void` | `updateSmoothToggle()` | Restyles the smoothing toggle to reflect whether smoothing is active. |
| `private void` | `updateTabHighlight()` | Restyles the timeframe tabs so only the active window is bold, coloured, and underlined. |
| `private static Color` | `withAlpha(Color c, int alpha)` |  |

### Field Detail

#### BG_COLOR

`private static final Color BG_COLOR`

#### COLOR_AVG

`private static final Color COLOR_AVG`

#### COLOR_HIGH

`private static final Color COLOR_HIGH`

#### COLOR_LOW

`private static final Color COLOR_LOW`

#### COLOR_NEUTRAL

`private static final Color COLOR_NEUTRAL`

#### CURRENT_LINE_COLOR

`private static final Color CURRENT_LINE_COLOR`

#### GRID_COLOR

`private static final Color GRID_COLOR`

#### LEFT_PAD

`private static final int LEFT_PAD`

#### MA_COLOR

`private static final Color MA_COLOR`

#### NUMBER_FORMAT

`private static final NumberFormat NUMBER_FORMAT`

#### SEPARATOR_COLOR

`private static final Color SEPARATOR_COLOR`

#### TAB_BAR_HEIGHT

`private static final int TAB_BAR_HEIGHT`

#### TIMEFRAMES

`private static final TimeWindow[] TIMEFRAMES`

#### TIMEFRAME_LABELS

`private static final String[] TIMEFRAME_LABELS`

#### TIMEFRAME_LABELS_FULL

`private static final String[] TIMEFRAME_LABELS_FULL`

#### TOP_PAD

`private static final int TOP_PAD`

#### VOLUME_COLOR

`private static final Color VOLUME_COLOR`

#### VOLUME_OVER_COLOR

`private static final Color VOLUME_OVER_COLOR`

#### X_AXIS_LABEL_GAP

`private static final int X_AXIS_LABEL_GAP`

#### activeWindow

`private TimeWindow activeWindow`

#### baseFont

`private final Font baseFont`

#### bottomAxisHeight

`private final int bottomAxisHeight`

#### currentPrice

`private long currentPrice`

#### expanded

`private final boolean expanded`

#### hoverX

`private int hoverX`

#### lineSet

`private LineSet lineSet`

#### lineSetListener

`private Consumer<LineSet> lineSetListener`

#### linesToggle

`private JPanel linesToggle`

#### linesToggleClick

`private MouseAdapter linesToggleClick`

#### mode

`private final Mode mode`

#### plotCache

`private transient BufferedImage plotCache`

#### plotCacheDirty

`private boolean plotCacheDirty`

#### rightAxisWidth

`private final int rightAxisWidth`

#### series1h

`private List<WikiRealtimePriceClient.PricePoint> series1h`

#### series24h

`private List<WikiRealtimePriceClient.PricePoint> series24h`

#### series5m

`private List<WikiRealtimePriceClient.PricePoint> series5m`

#### series6h

`private List<WikiRealtimePriceClient.PricePoint> series6h`

#### smooth

`private boolean smooth`

#### smoothListener

`private Consumer<Boolean> smoothListener`

#### smoothToggle

`private JLabel smoothToggle`

#### tabLabels

`private final List<JLabel> tabLabels`

#### tabsBar

`private final JPanel tabsBar`

### Constructor Detail

#### PriceGraphPanel

`public PriceGraphPanel()`

Builds a sidebar-sized price chart.

#### PriceGraphPanel

`public PriceGraphPanel(Mode mode)`

Builds a sidebar-sized chart in the given mode.

#### PriceGraphPanel

`public PriceGraphPanel(Mode mode, boolean expanded)`

Builds the chart and its timeframe tab bar.

- **Parameter** `mode` — whether to chart price or volume
- **Parameter** `expanded` — `true` for the larger pop-out variant (bigger font,
                denser axes, full tab labels); `false` for the sidebar

### Method Detail

#### buildSeriesPath

`private Path2D buildSeriesPath(int[] xs, int[] ys, int n)`

Builds the connecting path for a series of `n` screen points: a smooth
monotone cubic when `#smooth` is set, otherwise straight segments.

#### buildXTicks

`private List<long[]> buildXTicks(long startSec, long endSec)`

Builds the x-axis tick timestamps for the active window, snapped to natural
boundaries (months for a year, days/weeks for a month, days for a week,
rounded hours for a day) and denser in the expanded pop-out.

- **Returns:** single-element `long[]` tick timestamps in epoch seconds

#### clipMarker

`private static void clipMarker(Graphics2D g2, int cx, long value, double axisMin, double axisMax, int plotTop, int plotBottom, Color color)`

Draws a small triangle at the top or bottom plot edge marking a data point
that falls outside the visible value axis (so off-scale spikes are still
visible). No-op for in-range or non-positive values.

#### closestIndex

`private int closestIndex(List<WikiRealtimePriceClient.PricePoint> points, int plotLeft, int plotW, long startSec, long span)`

- **Returns:** the index of the point whose x pixel is nearest `#hoverX`, or -1 if none.

#### collectVisible

`private List<WikiRealtimePriceClient.PricePoint> collectVisible(long startSec, long endSec)`

- **Returns:** the active-window points falling within `[startSec, endSec]` (recomputed each paint; cheap).

#### cycleLineSet

`private void cycleLineSet()`

Advances the line set to the next option in the cycle and notifies the listener.

#### drawHover

`private void drawHover(Graphics2D g2, FontMetrics fm, List<WikiRealtimePriceClient.PricePoint> visible, int plotLeft, int plotTop, int plotRight, int plotBottom, int plotW, long startSec, long span)`

Draws the hover overlay: a vertical crosshair at the cursor and a tooltip
describing the data point nearest the cursor's x position. Drawn fresh on
every paint (not cached) so it stays cheap during mouse movement.

#### drawTooltip

`private void drawTooltip(Graphics2D g2, FontMetrics fm, String[] lines, int plotLeft, int plotTop, int plotRight)`

Draws the hover tooltip box of text lines, flipping to the cursor's left near the right edge.

#### drawVerticalLabel

`private void drawVerticalLabel(Graphics2D g2, String s, int cx, int topY, FontMetrics fm)`

Draws a string rotated 90° (reading bottom-to-top) hanging below the axis at `cx`.

#### drawXAxis

`private void drawXAxis(Graphics2D g2, FontMetrics fm, int plotLeft, int plotBottom, int plotW, long startSec, long endSec)`

Draws the x-axis date/time labels at the tick positions for the active window.

#### ema

`private static double[] ema(double[] values, int period)`

Computes an exponential moving average over `values`.

- **Parameter** `period` — the EMA period; the smoothing factor is `2/(period+1)`
- **Returns:** a same-length array of smoothed values

#### getActiveWindow

`public TimeWindow getActiveWindow()`

Returns the time window currently charted.

- **Returns:** the active time window

#### labelForTick

`private String labelForTick(long tsSec)`

Formats a tick timestamp as an axis label appropriate to the active window (time of day vs. date).

#### midpoint

`private static long midpoint(WikiRealtimePriceClient.PricePoint p)`

- **Returns:** the high/low midpoint of a point, or whichever side is present if only one is.

#### monotoneCubic

`private static Path2D monotoneCubic(int[] xsIn, int[] ysIn)`

Builds a Fritsch–Carlson monotone cubic Hermite spline through the points.
Monotonicity prevents the overshoot a naive cubic would introduce, so the
smoothed line never invents peaks or troughs the data doesn't have.

#### movingAverage

`private static double[] movingAverage(int[] ys, int n, int window)`

Computes a centered simple moving average of the first `n` y-values.

- **Parameter** `window` — the averaging width in samples
- **Returns:** a length-`n` array of averaged values

#### niceAxis

`private static double[] niceAxis(long dataMin, long dataMax, int minTicks, int maxTicks)`

Picks a human-friendly value axis covering `[dataMin, dataMax]` using
a 1/2/2.5/5/10 step progression so labels land on round numbers.

- **Parameter** `minTicks` — minimum number of gridlines to aim for
- **Parameter** `maxTicks` — maximum number of gridlines to allow
- **Returns:** `[axisMin, axisMax, step]`

#### niceVolumeStep

`private static long niceVolumeStep(long target, int intervals)`

- **Returns:** a rounded gridline step near `target/intervals` (1/2/5 × power of ten) for the volume axis.

#### paintComponent

`protected void paintComponent(Graphics g)`

Paints the chart: computes the plot rectangle and visible window, lazily
(re)rasterizes the static plot into `#plotCache` when size or data
changed, blits the cache, then draws the hover crosshair overlay on top.

#### paintPrice

`private void paintPrice(Graphics2D g2, FontMetrics fm, List<WikiRealtimePriceClient.PricePoint> visible, int plotLeft, int plotTop, int plotRight, int plotBottom, int plotW, int plotH, long startSec, long span)`

Draws the price chart: computes a "nice" value axis from the visible range,
paints the horizontal grid and y-axis labels, the current-price reference
line, and the selected high/low/average series (raw, smoothed, or with a
moving average per `#smooth` and `#lineSet`).

#### paintVolume

`private void paintVolume(Graphics2D g2, FontMetrics fm, List<WikiRealtimePriceClient.PricePoint> visible, int plotLeft, int plotTop, int plotRight, int plotBottom, int plotW, int plotH, long startSec, long span)`

Draws the volume chart: combined high+low traded volume as filled bars/area
with a "nice" volume axis and labels. The axis is capped at the 90th
percentile in the sidebar (so a single spike doesn't flatten everything) but
uses the full range in the expanded pop-out; over-cap bars are tinted.

#### percentile

`private static long percentile(List<Long> values, double p)`

- **Parameter** `p` — the percentile in `[0, 1]`
- **Returns:** the `p`-th percentile of `values` (the list is sorted in place), or 0 if empty

#### priceY

`private static int priceY(long value, double axisMin, double axisRange, int plotTop, int plotBottom, int plotH)`

Maps a price to its y pixel within the plot, clamped to the plot bounds.

#### rebuildLinesToggle

`private void rebuildLinesToggle()`

Rebuilds the line-set toggle as a row of per-letter labels coloured to match the
lines they represent (High green, Low red, Avg gold), so the active set is obvious
at a glance: ALL = green/gold/red, H/L = green/red, AVG = gold.

#### renderStatic

`private void renderStatic(Graphics2D g2, FontMetrics fm, int w, List<WikiRealtimePriceClient.PricePoint> visible, int plotLeft, int plotTop, int plotRight, int plotBottom, int plotW, int plotH, long startSec, long endSec, long span)`

Renders the full static plot into the cache image: the tab separator, then
either a "No data" message or the price/volume series with its axes.
Dispatches to `#paintPrice` or `#paintVolume` by `#mode`.

#### seriesForActiveWindow

`private List<WikiRealtimePriceClient.PricePoint> seriesForActiveWindow()`

- **Returns:** the pre-bucketed series whose granularity matches the active window.

#### setActiveWindow

`public void setActiveWindow(TimeWindow w)`

Selects the displayed timeframe (defaulting to 24h), refreshes the tab highlight, and repaints.

#### setData

`public void setData(List<WikiRealtimePriceClient.PricePoint> series5m, List<WikiRealtimePriceClient.PricePoint> series1h, List<WikiRealtimePriceClient.PricePoint> series6h, List<WikiRealtimePriceClient.PricePoint> series24h, long currentPrice)`

Replaces the chart's data with fresh per-bucket series and the latest price,
then invalidates the plot cache and repaints. Null series are treated as empty.

- **Parameter** `currentPrice` — the live price, drawn as the reference line

#### setLineSet

`public void setLineSet(LineSet set)`

Sets which price lines are drawn (all / high-low / average) and invalidates the plot cache.

#### setLineSetListener

`public void setLineSetListener(Consumer<LineSet> listener)`

Registers a callback fired when the user changes the line set, so a sibling chart can stay in sync.

#### setSmooth

`public void setSmooth(boolean s)`

Toggles spline smoothing of the data lines and invalidates the plot cache.

#### setSmoothListener

`public void setSmoothListener(Consumer<Boolean> listener)`

Registers a callback fired when the user toggles smoothing, so a sibling chart can stay in sync.

#### updateSmoothToggle

`private void updateSmoothToggle()`

Restyles the smoothing toggle to reflect whether smoothing is active.

#### updateTabHighlight

`private void updateTabHighlight()`

Restyles the timeframe tabs so only the active window is bold, coloured, and underlined.

#### withAlpha

`private static Color withAlpha(Color c, int alpha)`

- **Returns:** a copy of `c` with the given alpha (0–255).

---

## com.oveduumnakal.PriceGraphPanel.LineSet

_enum_

`public enum LineSet`

Which price lines to draw: all three, just high/low, or just the average.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `ALL` | Draw high, low, and average lines. |
| `AVG` | Draw only the average line. |
| `HIGH_LOW` | Draw only the high and low lines. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `final String` | `label` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `LineSet(String label)` |  |

### Enum Constant Detail

#### ALL

`ALL`

Draw high, low, and average lines.

#### AVG

`AVG`

Draw only the average line.

#### HIGH_LOW

`HIGH_LOW`

Draw only the high and low lines.

### Field Detail

#### label

`final String label`

### Constructor Detail

#### LineSet

`LineSet(String label)`

---

## com.oveduumnakal.PriceGraphPanel.Mode

_enum_

`public enum Mode`

Whether this panel charts prices or trade volume.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `PRICE` | Chart item prices. |
| `VOLUME` | Chart trade volume. |

### Enum Constant Detail

#### PRICE

`PRICE`

Chart item prices.

#### VOLUME

`VOLUME`

Chart trade volume.

---

## com.oveduumnakal.PriceIndicatorMode

_enum_

`public enum PriceIndicatorMode`

Which price-movement indicators are shown on item rows: `#ALL` prices,
only those that `#CHANGE`d, or `#OFF`. The `displayName` is
the label shown in the config dropdown.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `ALL` | The `"All"` option. |
| `CHANGE` | The `"Change"` option. |
| `OFF` | The `"Off"` option. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `displayName` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `PriceIndicatorMode(String displayName)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public String` | `toString()` | Returns the display label shown in the UI. |

### Enum Constant Detail

#### ALL

`ALL`

The `"All"` option.

#### CHANGE

`CHANGE`

The `"Change"` option.

#### OFF

`OFF`

The `"Off"` option.

### Field Detail

#### displayName

`private final String displayName`

### Constructor Detail

#### PriceIndicatorMode

`PriceIndicatorMode(String displayName)`

### Method Detail

#### toString

`public String toString()`

Returns the display label shown in the UI.

- **Returns:** the display label

---

## com.oveduumnakal.PriceRangeBar

_class_

`final class PriceRangeBar`

Small custom-painted bar showing where the live price sits within its 30-day low/high range.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final int` | `BAR_ARC` |  |
| `private static final int` | `BAR_H` |  |
| `private static final Color` | `RANGE_GOLD` |  |
| `private static final Color` | `RANGE_GREEN` |  |
| `private static final Color` | `RANGE_RED` |  |
| `private static final int` | `TRIANGLE_H` |  |
| `private long` | `live` |  |
| `private long` | `max` |  |
| `private long` | `min` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `PriceRangeBar()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `private static Color` | `colorAt(double f)` |  |
| `public Dimension` | `getMaximumSize()` |  |
| `private static Color` | `lerp(Color a, Color b, double t)` |  |
| `protected void` | `paintComponent(Graphics g)` | Paints the red-to-green gradient range bar with min/max labels and a triangle marker at the live price's position, or a grey "No data" bar when the range is unknown. |
| `void` | `setRange(long min, long max, long live)` |  |

### Field Detail

#### BAR_ARC

`private static final int BAR_ARC`

#### BAR_H

`private static final int BAR_H`

#### RANGE_GOLD

`private static final Color RANGE_GOLD`

#### RANGE_GREEN

`private static final Color RANGE_GREEN`

#### RANGE_RED

`private static final Color RANGE_RED`

#### TRIANGLE_H

`private static final int TRIANGLE_H`

#### live

`private long live`

#### max

`private long max`

#### min

`private long min`

### Constructor Detail

#### PriceRangeBar

`PriceRangeBar()`

### Method Detail

#### colorAt

`private static Color colorAt(double f)`

- **Returns:** the gradient colour at fraction `f`: red through gold (0.5) to green.

#### getMaximumSize

`public Dimension getMaximumSize()`

#### lerp

`private static Color lerp(Color a, Color b, double t)`

- **Returns:** the linear interpolation between two colours at `t` in 0..1.

#### paintComponent

`protected void paintComponent(Graphics g)`

Paints the red-to-green gradient range bar with min/max labels and a triangle
marker at the live price's position, or a grey "No data" bar when the range is
unknown. The gradient is clipped to the rounded bar outline.

#### setRange

`void setRange(long min, long max, long live)`

---

## com.oveduumnakal.PriceStats

_class_

`public class PriceStats`

Aggregated price and volume figures for one item over a single
`TimeWindow`: the window's high, low, and average prices (gp) and the
total traded `volume`.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private long` | `avg` |  |
| `private long` | `high` |  |
| `private long` | `low` |  |
| `private long` | `volume` |  |

### Field Detail

#### avg

`private long avg`

#### high

`private long high`

#### low

`private long low`

#### volume

`private long volume`

---

## com.oveduumnakal.ProcessingBasis

_class_

`final class ProcessingBasis`

Pure lot math for processing basis transfer (#69): computes what `quantity`
units about to be consumed are carried at, walking the item's open lots oldest
first — the same FIFO order the closure itself will use — so the consumed cost can
be transferred onto the produced item's new lots. Units beyond the open lots have
no known basis and contribute 0. Client-free and unit-testable.

### Constructor Summary

| Constructor | Description |
|---|---|
| `ProcessingBasis()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `static long` | `openLotCost(List<AcquisitionRecord> records, int quantity)` |  |

### Constructor Detail

#### ProcessingBasis

`private ProcessingBasis()`

### Method Detail

#### openLotCost

`static long openLotCost(List<AcquisitionRecord> records, int quantity)`

- **Returns:** the total gp the first `quantity` open-lot units are carried at,
        FIFO; units past the open lots contribute 0

---

## com.oveduumnakal.PulseEntry

_class_

`final class PulseEntry`

One in-flight price-change pulse: the label being animated, its base color, and the animation start time.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `final Color` | `base` |  |
| `final JLabel` | `label` |  |
| `final long` | `start` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `PulseEntry(JLabel label, Color base, long start)` |  |

### Field Detail

#### base

`final Color base`

#### label

`final JLabel label`

#### start

`final long start`

### Constructor Detail

#### PulseEntry

`PulseEntry(JLabel label, Color base, long start)`

---

## com.oveduumnakal.SectionSlot

_enum_

`public enum SectionSlot`

An ordinal position (`#FIRST`..`#NINTH`) assigned to a detail-view
section to control its order, or `#NONE` to hide it. Used by the config
so each section can be placed independently. The `label` is the name
shown in the config dropdown.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `EIGHTH` | The `"8th"` option. |
| `FIFTH` | The `"5th"` option. |
| `FIRST` | The `"1st"` option. |
| `FOURTH` | The `"4th"` option. |
| `NINTH` | The `"9th"` option. |
| `NONE` | The `"None"` option. |
| `SECOND` | The `"2nd"` option. |
| `SEVENTH` | The `"7th"` option. |
| `SIXTH` | The `"6th"` option. |
| `TENTH` | The `"10th"` option. |
| `THIRD` | The `"3rd"` option. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `label` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `SectionSlot(String label)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public boolean` | `isNone()` | Returns whether this slot is the not-shown placeholder. |
| `public String` | `toString()` | Returns the display label shown in the UI. |

### Enum Constant Detail

#### EIGHTH

`EIGHTH`

The `"8th"` option.

#### FIFTH

`FIFTH`

The `"5th"` option.

#### FIRST

`FIRST`

The `"1st"` option.

#### FOURTH

`FOURTH`

The `"4th"` option.

#### NINTH

`NINTH`

The `"9th"` option.

#### NONE

`NONE`

The `"None"` option.

#### SECOND

`SECOND`

The `"2nd"` option.

#### SEVENTH

`SEVENTH`

The `"7th"` option.

#### SIXTH

`SIXTH`

The `"6th"` option.

#### TENTH

`TENTH`

The `"10th"` option.

#### THIRD

`THIRD`

The `"3rd"` option.

### Field Detail

#### label

`private final String label`

### Constructor Detail

#### SectionSlot

`SectionSlot(String label)`

### Method Detail

#### isNone

`public boolean isNone()`

Returns whether this slot is the not-shown placeholder.

- **Returns:** `true` if this is `#NONE`

#### toString

`public String toString()`

Returns the display label shown in the UI.

- **Returns:** the display label

---

## com.oveduumnakal.SessionStats

_class_

`public final class SessionStats`

Tracks how the tracked portfolio's value has moved since a baseline (captured at
login or a manual reset), and splits the change into a <em>price</em> component
(the baseline holdings revalued at current prices) and a <em>quantity</em>
component (units gained or lost, valued at current prices). The two components
sum exactly to the total change, so the tooltip breakdown always reconciles.

<p>Session state is deliberately in-memory only — it resets each login and is
never persisted.

### Nested Type Summary

| Type | Description |
|---|---|
| _class_ [`Delta`](#comoveduumnakalsessionstatsdelta) | The session change decomposed into total, price movement, and quantity movement (all gp). |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final Map<Integer,long[]>` | `baseline` | Baseline holdings: item id → `{quantity, unitPrice`} at baseline time. |
| `private boolean` | `hasBaseline` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public void` | `absorbNewItems(Map<Integer,long[]> current)` | Folds any ids present in `current` but absent from the baseline into the baseline at their current `{quantity, unitPrice`}, so an item newly tracked mid-session enters at its current value and contributes zero to `#delta` instead of counting its whole holding as a quantity gain. |
| `public void` | `clear()` | Drops the baseline (e.g. |
| `public Delta` | `delta(Map<Integer,long[]> current)` | Computes the session change for `current` (id → `{quantity, unitPrice`}) against the baseline. |
| `public boolean` | `hasBaseline()` |  |
| `public void` | `removeItem(int itemId)` | Drops one item's baseline entry when it is untracked mid-session, so untracking is session-neutral instead of reading as the item's whole value lost — the removal-side mirror of `#absorbNewItems`. |
| `public void` | `reset(Map<Integer,long[]> snapshot)` | Captures `snapshot` (id → `{quantity, unitPrice`}) as the new session baseline. |

### Field Detail

#### baseline

`private final Map<Integer,long[]> baseline`

Baseline holdings: item id → `{quantity, unitPrice`} at baseline time.

#### hasBaseline

`private boolean hasBaseline`

### Method Detail

#### absorbNewItems

`public void absorbNewItems(Map<Integer,long[]> current)`

Folds any ids present in `current` but absent from the baseline into the baseline at
their current `{quantity, unitPrice`}, so an item newly tracked mid-session enters at
its current value and contributes zero to `#delta` instead of counting its whole
holding as a quantity gain. Its later price and quantity moves still register normally. This
is the mirror of the "dropped item keeps its baseline price" handling in `#delta`.

<p>No-op until a baseline exists (the first snapshot is captured wholesale by `#reset`).

#### clear

`public void clear()`

Drops the baseline (e.g. on profile change); the next snapshot re-primes it.

#### delta

`public Delta delta(Map<Integer,long[]> current)`

Computes the session change for `current` (id → `{quantity, unitPrice`})
against the baseline. A dropped item keeps its baseline price, so its loss lands
entirely on the quantity side.

- **Returns:** the total delta and its price/quantity split; zeros when no baseline is set

#### hasBaseline

`public boolean hasBaseline()`

- **Returns:** whether a baseline has been captured this session.

#### removeItem

`public void removeItem(int itemId)`

Drops one item's baseline entry when it is untracked mid-session, so untracking is
session-neutral instead of reading as the item's whole value lost — the removal-side
mirror of `#absorbNewItems`. Genuine quantity losses on items that stay tracked
are unaffected: their ids remain in the baseline.

#### reset

`public void reset(Map<Integer,long[]> snapshot)`

Captures `snapshot` (id → `{quantity, unitPrice`}) as the new session baseline.

---

## com.oveduumnakal.SessionStats.Delta

_class_

`public static class Delta`

The session change decomposed into total, price movement, and quantity movement (all gp).

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `long` | `price` |  |
| `long` | `quantity` |  |
| `long` | `total` |  |

### Field Detail

#### price

`long price`

#### quantity

`long quantity`

#### total

`long total`

---

## com.oveduumnakal.SortMode

_enum_

`public enum SortMode`

How the tracked items list is ordered. `#MANUAL` keeps the user's drag
order; every other mode sorts for display only (within each group when
grouping is active) and disables drag reordering. Each mode has a natural
direction (Name ascending, value-like modes descending) that the reverse flag
flips; items missing the sort key always sort last, regardless of direction.

<p>Public because it is the return type of a `@ConfigItem` accessor: the
RuneLite config proxy lives in another module and must be able to access it, or
the plugin fails to start with an `IllegalAccessError`.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `CHANGE_24H` | The `"24h Change"` option. |
| `MANUAL` | The `"Manual"` option. |
| `NAME` | The `"Name"` option. |
| `PROFIT` | The `"Profit"` option. |
| `VALUE` | The `"Value"` option. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `label` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `SortMode(String label)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `private static double` | `changeKey(TrackedItem item)` |  |
| `Comparator<TrackedItem>` | `comparator(boolean reversed)` |  |
| `boolean` | `descending(boolean reversed)` |  |
| `private static Comparator<TrackedItem>` | `directed(Comparator<TrackedItem> key, Predicate<TrackedItem> hasKey, boolean descending)` | Applies the sort direction to an ascending `key` comparator while always sorting items that lack the key (`hasKey` false) last, whichever direction is active. |
| `private static boolean` | `hasChange(TrackedItem item)` |  |
| `private static long` | `profitKey(TrackedItem item)` |  |
| `public String` | `toString()` | Returns the display label shown in the UI. |

### Enum Constant Detail

#### CHANGE_24H

`CHANGE_24H`

The `"24h Change"` option.

#### MANUAL

`MANUAL`

The `"Manual"` option.

#### NAME

`NAME`

The `"Name"` option.

#### PROFIT

`PROFIT`

The `"Profit"` option.

#### VALUE

`VALUE`

The `"Value"` option.

### Field Detail

#### label

`private final String label`

### Constructor Detail

#### SortMode

`SortMode(String label)`

### Method Detail

#### changeKey

`private static double changeKey(TrackedItem item)`

- **Returns:** the percent change of the current price vs the 24h average (0 when either side is unknown).

#### comparator

`Comparator<TrackedItem> comparator(boolean reversed)`

- **Parameter** `reversed` — whether to flip this mode's natural direction
- **Returns:** the display comparator, or `null` for `#MANUAL`

#### descending

`boolean descending(boolean reversed)`

- **Returns:** whether this mode's effective direction is descending once `reversed` is applied.

#### directed

`private static Comparator<TrackedItem> directed(Comparator<TrackedItem> key, Predicate<TrackedItem> hasKey, boolean descending)`

Applies the sort direction to an ascending `key` comparator while always sorting items
that lack the key (`hasKey` false) last, whichever direction is active.

#### hasChange

`private static boolean hasChange(TrackedItem item)`

- **Returns:** whether the item has both a current price and a 24h baseline to compute a change from.

#### profitKey

`private static long profitKey(TrackedItem item)`

- **Returns:** the same estimated profit the item's rows, totals, and notification metric display
        (`TrackedItem#getProfitAt(long)` at the average price): realized profit plus the
        unrealized mark-to-market on held lots. Only meaningful once the cost basis is
        initialized. The old `getAvgValue() - getCostBasis()` omitted realized profit and
        mixed container quantity with all-open-lot cost, so the sort disagreed with every
        displayed figure and swung negative for the duration of an in-flight sell (#173).

#### toString

`public String toString()`

Returns the display label shown in the UI.

- **Returns:** the display label

---

## com.oveduumnakal.SourceAttributionCore

_class_

`class SourceAttributionCore`

Pure, client-free core of source-attributed pricing (#64). Detectors observe
game events and register `#claim claims` — "I expect a quantity
change of this item, from this source, at this unit price" — and the quantity
sync `#attribute attributes` each detected delta against them.
Deltas no claim matches fall back to `AcquisitionSource#UNKNOWN`, whose
pricing is the caller's legacy policy.

<p>Ordinary claims expire after a few ticks so a stale expectation can never
mis-price an unrelated later change. GE buys instead register `#claimDurable durable` claims (#180): a GE fill and its collected inventory
gain can be many ticks apart and must survive a logout, so durable claims
carry no TTL, are matched by their own `#attributeDurable` path, and
`#exportDurable serialize` as the persisted GE buy ledger. All
operations are O(open claims) with no allocation when idle, keeping the
per-tick cost negligible; the class touches no client types, so it is
unit-testable in isolation.

### Nested Type Summary

| Type | Description |
|---|---|
| _class_ [`Attribution`](#comoveduumnakalsourceattributioncoreattribution) | The outcome of attributing one delta: its source and, when known, a unit price. |
| _class_ [`Claim`](#comoveduumnakalsourceattributioncoreclaim) | One registered expectation of a quantity change. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `static final int` | `CLAIM_TTL_TICKS` | How many ticks a claim stays valid before `#expire` discards it. |
| `private final Deque<Claim>` | `claims` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `Attribution` | `attribute(int itemId, int quantity, int currentTick)` | Attributes a detected quantity change of `quantity` units (magnitude, direction-agnostic) of `itemId`, consuming the oldest live matching claim — partially when the claim is larger than the delta. |
| `List<long[]>` | `attributeDurable(int itemId, int quantity)` | Attributes a detected gain of `quantity` units of `itemId` against the durable (GE buy) claims only, consuming them oldest-first and partially draining a claim larger than the delta. |
| `void` | `claim(AcquisitionSource source, int itemId, int quantity, long unitPrice, int currentTick)` | Registers a detector's expectation that `quantity` units of `itemId` are about to change hands at `unitPrice` gp each. |
| `void` | `claimDurable(AcquisitionSource source, int itemId, int quantity, long unitPrice)` | Registers a durable expectation — a GE buy fill (#180) — that `quantity` units of `itemId` were acquired at `unitPrice` gp each. |
| `void` | `clear()` | Drops every open claim (logout, plugin shutdown), durable ones included. |
| `void` | `clearDurable()` | Drops only the durable (GE buy) claims, so a reload can replace them without disturbing live claims. |
| `void` | `expire(int currentTick)` | Discards expired ordinary claims; call once per tick. |
| `Map<Integer,List<long[]>>` | `exportDurable()` | Serializes the open durable (GE buy) claims as the persisted GE buy ledger: item id to a FIFO list of `{quantity, unitPrice`} chunks. |
| `void` | `importDurable(Map<Integer,List<long[]>> ledger)` | Rebuilds the durable (GE buy) claims from a persisted ledger; the reloaded chunks are GE trades. |

### Field Detail

#### CLAIM_TTL_TICKS

`static final int CLAIM_TTL_TICKS`

How many ticks a claim stays valid before `#expire` discards it.

#### claims

`private final Deque<Claim> claims`

### Method Detail

#### attribute

`Attribution attribute(int itemId, int quantity, int currentTick)`

Attributes a detected quantity change of `quantity` units (magnitude,
direction-agnostic) of `itemId`, consuming the oldest live matching
claim — partially when the claim is larger than the delta.

- **Returns:** the claim's attribution, or `Attribution#UNKNOWN` when nothing matches

#### attributeDurable

`List<long[]> attributeDurable(int itemId, int quantity)`

Attributes a detected gain of `quantity` units of `itemId` against the durable
(GE buy) claims only, consuming them oldest-first and partially draining a claim larger than
the delta. Because durable claims may carry differing unit prices, the consumed portions are
returned as `{quantity, unitPrice`} chunks in FIFO order for the caller to price into
lots; an empty list means nothing matched.

#### claim

`void claim(AcquisitionSource source, int itemId, int quantity, long unitPrice, int currentTick)`

Registers a detector's expectation that `quantity` units of
`itemId` are about to change hands at `unitPrice` gp each.

#### claimDurable

`void claimDurable(AcquisitionSource source, int itemId, int quantity, long unitPrice)`

Registers a durable expectation — a GE buy fill (#180) — that `quantity` units of
`itemId` were acquired at `unitPrice` gp each. Unlike `#claim`, a durable
claim never expires and is matched only by `#attributeDurable`, so the arbitrary gap
between a fill and the collected inventory gain can never strand it.

#### clear

`void clear()`

Drops every open claim (logout, plugin shutdown), durable ones included.

#### clearDurable

`void clearDurable()`

Drops only the durable (GE buy) claims, so a reload can replace them without disturbing live claims.

#### expire

`void expire(int currentTick)`

Discards expired ordinary claims; call once per tick. Durable (GE buy) claims have no TTL and
are never touched here. No-op (and allocation-free) when idle.

#### exportDurable

`Map<Integer,List<long[]>> exportDurable()`

Serializes the open durable (GE buy) claims as the persisted GE buy ledger: item id to a
FIFO list of `{quantity, unitPrice`} chunks. This is the on-disk shape the plugin has
always written, so no schema change results from folding the ledger into the core (#180).

#### importDurable

`void importDurable(Map<Integer,List<long[]>> ledger)`

Rebuilds the durable (GE buy) claims from a persisted ledger; the reloaded chunks are GE
trades. Call after `#clearDurable` so a login replaces rather than duplicates them.

---

## com.oveduumnakal.SourceAttributionCore.Attribution

_class_

`static final class Attribution`

The outcome of attributing one delta: its source and, when known, a unit price.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `static final Attribution` | `UNKNOWN` |  |
| `private final AcquisitionSource` | `source` |  |
| `private final Long` | `unitPrice` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `Attribution(AcquisitionSource source, Long unitPrice)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `AcquisitionSource` | `source()` |  |
| `long` | `unitPriceOr(long fallback)` |  |

### Field Detail

#### UNKNOWN

`static final Attribution UNKNOWN`

#### source

`private final AcquisitionSource source`

#### unitPrice

`private final Long unitPrice`

### Constructor Detail

#### Attribution

`Attribution(AcquisitionSource source, Long unitPrice)`

### Method Detail

#### source

`AcquisitionSource source()`

#### unitPriceOr

`long unitPriceOr(long fallback)`

- **Returns:** the observed unit price, or `fallback` when the source didn't carry one.

---

## com.oveduumnakal.SourceAttributionCore.Claim

_class_

`private static final class Claim`

One registered expectation of a quantity change.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `final boolean` | `durable` |  |
| `final int` | `expiryTick` |  |
| `final int` | `itemId` |  |
| `int` | `quantity` |  |
| `final AcquisitionSource` | `source` |  |
| `final long` | `unitPrice` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `Claim(AcquisitionSource source, int itemId, int quantity, long unitPrice, int expiryTick, boolean durable)` |  |

### Field Detail

#### durable

`final boolean durable`

#### expiryTick

`final int expiryTick`

#### itemId

`final int itemId`

#### quantity

`int quantity`

#### source

`final AcquisitionSource source`

#### unitPrice

`final long unitPrice`

### Constructor Detail

#### Claim

`Claim(AcquisitionSource source, int itemId, int quantity, long unitPrice, int expiryTick, boolean durable)`

---

## com.oveduumnakal.SourceGlyphRenderer

_class_

`class SourceGlyphRenderer`

Renders the collection log's read-only source column as a small PNG glyph — one per
`AcquisitionSource`, converted from the hand-authored SVGs in `icons/source/` — with a
faint background tint while the cell is hovered.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final Map<AcquisitionSource,ImageIcon>` | `ICONS` |  |
| `private static final int` | `SIZE` | On-screen glyph size; the source PNGs are authored larger and scaled down for crispness. |
| `private final IntSupplier` | `hoverCol` |  |
| `private final IntSupplier` | `hoverRow` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `SourceGlyphRenderer(IntSupplier hoverRow, IntSupplier hoverCol)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public Component` | `getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)` |  |
| `private static ImageIcon` | `icon(String resource)` |  |
| `private static Map<AcquisitionSource,ImageIcon>` | `loadIcons()` | Loads and scales each source's PNG once, keyed by source. |

### Field Detail

#### ICONS

`private static final Map<AcquisitionSource,ImageIcon> ICONS`

#### SIZE

`private static final int SIZE`

On-screen glyph size; the source PNGs are authored larger and scaled down for crispness.

#### hoverCol

`private final IntSupplier hoverCol`

#### hoverRow

`private final IntSupplier hoverRow`

### Constructor Detail

#### SourceGlyphRenderer

`SourceGlyphRenderer(IntSupplier hoverRow, IntSupplier hoverCol)`

### Method Detail

#### getTableCellRendererComponent

`public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)`

#### icon

`private static ImageIcon icon(String resource)`

#### loadIcons

`private static Map<AcquisitionSource,ImageIcon> loadIcons()`

Loads and scales each source's PNG once, keyed by source.

---

## com.oveduumnakal.Spinner

_class_

`final class Spinner`

A small indeterminate spinner: an orange arc that rotates while its Swing timer runs.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final int` | `DIAMETER` |  |
| `private int` | `angle` |  |
| `private final Timer` | `timer` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `Spinner()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `protected void` | `paintComponent(Graphics g)` | Paints a grey ring with a rotating orange arc segment. |
| `void` | `start()` |  |
| `void` | `stop()` |  |

### Field Detail

#### DIAMETER

`private static final int DIAMETER`

#### angle

`private int angle`

#### timer

`private final Timer timer`

### Constructor Detail

#### Spinner

`Spinner()`

### Method Detail

#### paintComponent

`protected void paintComponent(Graphics g)`

Paints a grey ring with a rotating orange arc segment.

#### start

`void start()`

#### stop

`void stop()`

---

## com.oveduumnakal.StockpileColors

_class_

`final class StockpileColors`

The plugin's shared colour palette, defined once so the panel, overlays, and
charts stay visually consistent and theme tweaks are one-line changes.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `static final Color` | `AVG` | Gold used for average prices and active/selected accents. |
| `static final Color` | `DIVIDER` | Grey rule used for section dividers and separators. |
| `static final Color` | `HIGH` | Green used for high / instant-buy prices and positive profit. |
| `static final Color` | `LOW` | Red used for low / instant-sell prices and negative profit. |
| `static final Color` | `MUTED` | Muted grey used for placeholder/secondary text and loading states. |
| `static final Color` | `TABLE_GRID` | Darker grey used for table grid lines and faint borders. |
| `static final Color` | `TINT_AVG` | Hover-tint background behind average-price values. |
| `static final Color` | `TINT_HIGH` | Hover-tint background behind high-price values. |
| `static final Color` | `TINT_LOW` | Hover-tint background behind low-price values. |
| `static final Color` | `TINT_VOLUME` | Hover-tint background behind volume values. |

### Constructor Summary

| Constructor | Description |
|---|---|
| `StockpileColors()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `static String` | `toHex(Color c)` |  |

### Field Detail

#### AVG

`static final Color AVG`

Gold used for average prices and active/selected accents.

#### DIVIDER

`static final Color DIVIDER`

Grey rule used for section dividers and separators.

#### HIGH

`static final Color HIGH`

Green used for high / instant-buy prices and positive profit.

#### LOW

`static final Color LOW`

Red used for low / instant-sell prices and negative profit.

#### MUTED

`static final Color MUTED`

Muted grey used for placeholder/secondary text and loading states.

#### TABLE_GRID

`static final Color TABLE_GRID`

Darker grey used for table grid lines and faint borders.

#### TINT_AVG

`static final Color TINT_AVG`

Hover-tint background behind average-price values.

#### TINT_HIGH

`static final Color TINT_HIGH`

Hover-tint background behind high-price values.

#### TINT_LOW

`static final Color TINT_LOW`

Hover-tint background behind low-price values.

#### TINT_VOLUME

`static final Color TINT_VOLUME`

Hover-tint background behind volume values.

### Constructor Detail

#### StockpileColors

`private StockpileColors()`

### Method Detail

#### toHex

`static String toHex(Color c)`

- **Returns:** the colour as a `#rrggbb` hex string for inline HTML styling.

---

## com.oveduumnakal.StockpileConfig

_interface_

`public interface StockpileConfig`

RuneLite configuration for the Stockpile plugin.

<p>Defines every user-facing setting as a defaulted `@ConfigItem`
accessor, grouped into five `@ConfigSection`s: main view, tracked-item
row display, GE estimates, tracking/highlighting, and the detail view. The
`KEY_*` constants are the persisted setting keys (also used directly by
the plugin when reading/writing config), and `#GROUP` names the config
group. Each accessor's behavior is described by its annotation; the per-item
`name`/`description` are the source of truth shown in the UI.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `String` | `GROUP` | RuneLite config group name (`"stockpile"`). |
| `String` | `KEY_ADD_CONTEXT_MENU_OPTION` | Persisted config key `"addContextMenuOption"`. |
| `String` | `KEY_AUTO_ADD_ITEMS` | Persisted config key `"autoAddItems"`. |
| `String` | `KEY_CATEGORIES` | Persisted config key `"trackedCategories"`. |
| `String` | `KEY_COMPACT_VIEW` | Persisted config key `"compactView"`. |
| `String` | `KEY_FALLBACK_PRICING` | Persisted config key `"fallbackPricing"`. |
| `String` | `KEY_GE_BUY_LEDGER` | Persisted config key `"geBuyLedger"`. |
| `String` | `KEY_GE_BUY_LIMITS` | Persisted config key `"geBuyLimits"`. |
| `String` | `KEY_GE_ESTIMATES_FORMAT` | Persisted config key `"geEstimatesFormat"`. |
| `String` | `KEY_GE_ESTIMATES_POSITION` | Persisted config key `"geEstimatesPosition"`. |
| `String` | `KEY_GE_ESTIMATES_SPACING` | Persisted config key `"geEstimatesSpacing"`. |
| `String` | `KEY_GE_FOCUS_PANEL` | Persisted config key `"geFocusPanel"`. |
| `String` | `KEY_GE_INTEGRATION` | Persisted config key `"geIntegration"`. |
| `String` | `KEY_GE_SHOW_MARKET_PRICES` | Persisted config key `"geShowMarketPrices"`. |
| `String` | `KEY_GE_SHOW_TRACK_BUTTON` | Persisted config key `"geShowTrackButton"`. |
| `String` | `KEY_GLOW_EFFECT` | Persisted config key `"glowEffect"`. |
| `String` | `KEY_HIGHLIGHT_COLOR` | Persisted config key `"highlightColor"`. |
| `String` | `KEY_HIGHLIGHT_TRACKED_ITEMS` | Persisted config key `"highlightTrackedItems"`. |
| `String` | `KEY_LAST_SEEN_VERSION` | Persisted config key `"lastSeenVersion"`. |
| `String` | `KEY_NOTIFICATION_STYLE` | Persisted config key `"notificationStyle"`. |
| `String` | `KEY_PORTFOLIO_HISTORY` | Persisted config key `"portfolioHistory"`. |
| `String` | `KEY_PRESSURE_WINDOW` | Persisted config key `"buySellPressureWindow"`. |
| `String` | `KEY_PRICE_CACHE` | Persisted config key `"priceCache"`. |
| `String` | `KEY_PRICE_CHANGE_INDICATOR` | Persisted config key `"priceChangeIndicator"`. |
| `String` | `KEY_PRICE_OVERVIEW_ROWS` | Persisted config key `"priceOverviewPreset"`. |
| `String` | `KEY_PRICE_REFRESH_SECONDS` | Persisted config key `"priceRefreshSeconds"`. |
| `String` | `KEY_PROMPT_CATEGORY_ON_TRACK` | Persisted config key `"promptCategoryOnTrack"`. |
| `String` | `KEY_ROW_1_DATA` | Persisted config key `"row1Data"`. |
| `String` | `KEY_ROW_2_DATA` | Persisted config key `"row2Data"`. |
| `String` | `KEY_ROW_3_DATA` | Persisted config key `"row3Data"`. |
| `String` | `KEY_SCREEN_OVERLAY_LAYOUT` | Persisted config key `"screenOverlayLayout"`. |
| `String` | `KEY_SCREEN_OVERLAY_ON_TOP` | Persisted config key `"screenOverlayOnTop"`. |
| `String` | `KEY_SHOW_ALCH_INFO` | Persisted config key `"showAlchInfo"`. |
| `String` | `KEY_SHOW_COLLECTION_VALUES` | Persisted config key `"showCollectionValues"`. |
| `String` | `KEY_SHOW_COL_AVG` | Persisted config key `"showColAvg"`. |
| `String` | `KEY_SHOW_COL_HIGH` | Persisted config key `"showColHigh"`. |
| `String` | `KEY_SHOW_COL_LOW` | Persisted config key `"showColLow"`. |
| `String` | `KEY_SHOW_COL_VOLUME` | Persisted config key `"showColVolume"`. |
| `String` | `KEY_SHOW_EST_AVG` | Persisted config key `"showEstAvg"`. |
| `String` | `KEY_SHOW_EST_HIGH` | Persisted config key `"showEstHigh"`. |
| `String` | `KEY_SHOW_EST_LOW` | Persisted config key `"showEstLow"`. |
| `String` | `KEY_SHOW_EST_PROFIT` | Persisted config key `"showEstProfit"`. |
| `String` | `KEY_SHOW_GE_ESTIMATES` | Persisted config key `"showGeEstimates"`. |
| `String` | `KEY_SHOW_ITEM_LOG` | Persisted config key `"showItemLog"`. |
| `String` | `KEY_SHOW_ITEM_PROFIT_ROW` | Persisted config key `"showItemProfitRow"`. |
| `String` | `KEY_SHOW_ITEM_VALUES` | Persisted config key `"showItemValues"`. |
| `String` | `KEY_SHOW_LINKS` | Persisted config key `"showLinks"`. |
| `String` | `KEY_SHOW_MARKET_INFO` | Persisted config key `"showMarketInfo"`. |
| `String` | `KEY_SHOW_NOTIFICATIONS` | Persisted config key `"showNotifications"`. |
| `String` | `KEY_SHOW_PRICE_GRAPH` | Persisted config key `"showPriceGraph"`. |
| `String` | `KEY_SHOW_PRICE_OVERVIEW` | Persisted config key `"showPriceOverview"`. |
| `String` | `KEY_SHOW_QUANTITY_VALUE` | Persisted config key `"showQuantityValue"`. |
| `String` | `KEY_SHOW_SCREEN_OVERLAY` | Persisted config key `"showScreenOverlay"`. |
| `String` | `KEY_SHOW_SESSION` | Persisted config key `"showSession"`. |
| `String` | `KEY_SHOW_VOLUME_GRAPH` | Persisted config key `"showVolumeGraph"`. |
| `String` | `KEY_SORT_MODE` | Persisted config key `"sortMode"`. |
| `String` | `KEY_SORT_REVERSED` | Persisted config key `"sortReversed"`. |
| `String` | `KEY_SOURCE_PRICING` | Persisted config key `"sourcePricing"`. |
| `String` | `KEY_STALE_PRICE_THRESHOLD` | Persisted config key `"stalePriceThresholdMinutes"`. |
| `String` | `KEY_STOP_TRACKING_COLOR` | Persisted config key `"stopTrackingColor"`. |
| `String` | `KEY_TRACKED_ITEMS` | Persisted config key `"trackedItemIds"`. |
| `String` | `KEY_TRACK_ITEM_COLOR` | Persisted config key `"trackItemColor"`. |
| `String` | `KEY_VERSION_FIRST_SEEN` | Persisted config key `"versionFirstSeen"`. |
| `String` | `KEY_WHATS_NEW_DISMISSED` | Persisted config key `"whatsNewDismissed"`. |
| `String` | `detailViewSection` | Order, visibility, and contents of the per-item detail view sections. |
| `String` | `geEstimatesSection` | Placement, format, spacing, and rows of the estimated GE sell-value block. |
| `String` | `geIntegrationSection` | How the open Grand Exchange offer ties into the Stockpile view. |
| `String` | `mainViewSection` | Top-level panel behavior: price refresh, change indicator, and global toggles. |
| `String` | `overlaySection` | The in-game on-screen overlay of selected tracked items. |
| `String` | `trackedItemSection` | Which columns and rows each tracked-item entry shows in the list. |
| `String` | `trackingSection` | Context-menu integration, highlight colors/mode, and the glow effect. |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `default boolean` | `addContextMenuOption()` | Add a "Track Item" / "Stop Tracking" entry to right-click menus on the ground, in the bank, or in the inventory. |
| `default boolean` | `autoAddItems()` | Automatically add collection-log entries from inventory/bank changes. |
| `default PressureWindow` | `buySellPressureWindow()` | Look-back period for the Buy/Sell Pressure bar in the Market Info section. |
| `default boolean` | `compactView()` | Show tracked items as compact two-row entries. |
| `default FallbackPricing` | `fallbackPricing()` | The price an unknown-source change buys in at — mobile/offline sessions and anything no detector observed (observed sources like GE offers price themselves). |
| `default ValueFormat` | `geEstimatesFormat()` | How GE Estimate prices are formatted. |
| `default EstimatesPosition` | `geEstimatesPosition()` | Top: under the search bar above the tracked items list. |
| `default EstimatesSpacing` | `geEstimatesSpacing()` | Vertical spacing of the estimate rows. |
| `default boolean` | `geFocusPanel()` | When a GE offer opens the item in Stockpile, switch to and focus the Stockpile panel. |
| `default GeIntegrationMode` | `geIntegration()` | Open the current Grand Exchange offer item in Stockpile's view-only mode: via an injected button, automatically, both, or off. |
| `default boolean` | `geShowMarketPrices()` | Show the item's latest 5-minute High/Low market prices as a line on the open Grand Exchange offer window. |
| `default boolean` | `geShowTrackButton()` | Show a Track/Untrack button beside the History button on the open Grand Exchange offer window, toggling tracking of the offer's item. |
| `default GlowSpeed` | `glowEffect()` | Speed of the highlight's breathing/glow effect. |
| `default Color` | `highlightColor()` | Color used to outline the highlighted tracked item. |
| `default HighlightMode` | `highlightTrackedItems()` | Where to outline tracked items. |
| `default Notification` | `notificationStyle()` | Master switch and delivery style for per-item notifications. |
| `default PriceIndicatorMode` | `priceChangeIndicator()` | How to display the pulse indicator for price changes. |
| `default OverviewPreset` | `priceOverviewRows()` | How many time-window rows the Price Overview shows. |
| `default int` | `priceRefreshSeconds()` | How often to refresh GE prices from the API. |
| `default boolean` | `promptCategoryOnTrack()` | When you track an item, ask which category to put it in (choose an existing one, create a new one, or skip to Uncategorized). |
| `default TimeWindow` | `row1Data()` | Price data shown on the first row. |
| `default TimeWindow` | `row2Data()` | Price data shown on the second row. |
| `default TimeWindow` | `row3Data()` | Price data shown on the third row. |
| `default OverlayLayout` | `screenOverlayLayout()` | Compact two-row entries, or a replica of the standard tracked-item row. |
| `default boolean` | `screenOverlayOnTop()` | Keep the overlay above open interfaces. |
| `default SectionSlot` | `showAlchInfo()` | Position of the Alchemy Info section, or None to hide it. |
| `default boolean` | `showColAvg()` | Show the Avg column in the tracked items list. |
| `default boolean` | `showColHigh()` | Show the High column in the tracked items list. |
| `default boolean` | `showColLow()` | Show the Low column in the tracked items list. |
| `default boolean` | `showColVolume()` | Show the Volume column in the tracked items list. |
| `default SectionSlot` | `showCollectionValues()` | Position of the Collection Current Values section, or None to hide it. |
| `default boolean` | `showEstAvg()` | Show the row containing the estimated average value. |
| `default boolean` | `showEstHigh()` | Show the row containing the estimated high value. |
| `default boolean` | `showEstLow()` | Show the row containing the estimated low value. |
| `default boolean` | `showEstProfit()` | Show the row containing the estimated profit. |
| `default boolean` | `showGeEstimates()` | Show the Estimated GE Sell Value section. |
| `default SectionSlot` | `showItemLog()` | Position of the Item Collection Log section, or None to hide it. |
| `default boolean` | `showItemProfitRow()` | Show the Est. |
| `default SectionSlot` | `showItemValues()` | Position of the Item Current Values section, or None to hide it. |
| `default SectionSlot` | `showLinks()` | Position of the Links section (Wiki / Live Prices), or None to hide it. |
| `default SectionSlot` | `showMarketInfo()` | Position of the Market Info section, or None to hide it. |
| `default SectionSlot` | `showNotifications()` | Position of the per-item notification rule editor, or None to hide it. |
| `default SectionSlot` | `showPriceGraph()` | Position of the Price Graph section, or None to hide it. |
| `default SectionSlot` | `showPriceOverview()` | Position of the Price Overview section, or None to hide it. |
| `default boolean` | `showQuantityValue()` | Show the quantity value next to the item name. |
| `default boolean` | `showScreenOverlay()` | Show the items selected (via the manage view) as a draggable in-game overlay. |
| `default boolean` | `showSession()` | Show the row containing the value gained/lost since login. |
| `default SectionSlot` | `showVolumeGraph()` | Position of the Volume Graph section, or None to hide it. |
| `default SortMode` | `sortMode()` | Order of the tracked items list. |
| `default boolean` | `sortReversed()` | Reverses the sort direction of the tracked items list (flips each mode's default ascending/descending order). |
| `default boolean` | `sourcePricing()` | Price quantity changes by how they occurred (GE offers, pickups, shops, alchemy...) as those detectors arrive. |
| `default int` | `stalePriceThresholdMinutes()` | Dim the Ltst high or low when its last trade is older than this many minutes. |
| `default Color` | `stopTrackingColor()` | Color of the "Stop Tracking" context menu entry. |
| `default Color` | `trackItemColor()` | Color of the "Track Item" context menu entry. |

### Field Detail

#### GROUP

`String GROUP`

RuneLite config group name (`"stockpile"`).

#### KEY_ADD_CONTEXT_MENU_OPTION

`String KEY_ADD_CONTEXT_MENU_OPTION`

Persisted config key `"addContextMenuOption"`.

#### KEY_AUTO_ADD_ITEMS

`String KEY_AUTO_ADD_ITEMS`

Persisted config key `"autoAddItems"`.

#### KEY_CATEGORIES

`String KEY_CATEGORIES`

Persisted config key `"trackedCategories"`.

#### KEY_COMPACT_VIEW

`String KEY_COMPACT_VIEW`

Persisted config key `"compactView"`.

#### KEY_FALLBACK_PRICING

`String KEY_FALLBACK_PRICING`

Persisted config key `"fallbackPricing"`.

#### KEY_GE_BUY_LEDGER

`String KEY_GE_BUY_LEDGER`

Persisted config key `"geBuyLedger"`.

#### KEY_GE_BUY_LIMITS

`String KEY_GE_BUY_LIMITS`

Persisted config key `"geBuyLimits"`.

#### KEY_GE_ESTIMATES_FORMAT

`String KEY_GE_ESTIMATES_FORMAT`

Persisted config key `"geEstimatesFormat"`.

#### KEY_GE_ESTIMATES_POSITION

`String KEY_GE_ESTIMATES_POSITION`

Persisted config key `"geEstimatesPosition"`.

#### KEY_GE_ESTIMATES_SPACING

`String KEY_GE_ESTIMATES_SPACING`

Persisted config key `"geEstimatesSpacing"`.

#### KEY_GE_FOCUS_PANEL

`String KEY_GE_FOCUS_PANEL`

Persisted config key `"geFocusPanel"`.

#### KEY_GE_INTEGRATION

`String KEY_GE_INTEGRATION`

Persisted config key `"geIntegration"`.

#### KEY_GE_SHOW_MARKET_PRICES

`String KEY_GE_SHOW_MARKET_PRICES`

Persisted config key `"geShowMarketPrices"`.

#### KEY_GE_SHOW_TRACK_BUTTON

`String KEY_GE_SHOW_TRACK_BUTTON`

Persisted config key `"geShowTrackButton"`.

#### KEY_GLOW_EFFECT

`String KEY_GLOW_EFFECT`

Persisted config key `"glowEffect"`.

#### KEY_HIGHLIGHT_COLOR

`String KEY_HIGHLIGHT_COLOR`

Persisted config key `"highlightColor"`.

#### KEY_HIGHLIGHT_TRACKED_ITEMS

`String KEY_HIGHLIGHT_TRACKED_ITEMS`

Persisted config key `"highlightTrackedItems"`.

#### KEY_LAST_SEEN_VERSION

`String KEY_LAST_SEEN_VERSION`

Persisted config key `"lastSeenVersion"`.

#### KEY_NOTIFICATION_STYLE

`String KEY_NOTIFICATION_STYLE`

Persisted config key `"notificationStyle"`.

#### KEY_PORTFOLIO_HISTORY

`String KEY_PORTFOLIO_HISTORY`

Persisted config key `"portfolioHistory"`.

#### KEY_PRESSURE_WINDOW

`String KEY_PRESSURE_WINDOW`

Persisted config key `"buySellPressureWindow"`.

#### KEY_PRICE_CACHE

`String KEY_PRICE_CACHE`

Persisted config key `"priceCache"`.

#### KEY_PRICE_CHANGE_INDICATOR

`String KEY_PRICE_CHANGE_INDICATOR`

Persisted config key `"priceChangeIndicator"`.

#### KEY_PRICE_OVERVIEW_ROWS

`String KEY_PRICE_OVERVIEW_ROWS`

Persisted config key `"priceOverviewPreset"`.

#### KEY_PRICE_REFRESH_SECONDS

`String KEY_PRICE_REFRESH_SECONDS`

Persisted config key `"priceRefreshSeconds"`.

#### KEY_PROMPT_CATEGORY_ON_TRACK

`String KEY_PROMPT_CATEGORY_ON_TRACK`

Persisted config key `"promptCategoryOnTrack"`.

#### KEY_ROW_1_DATA

`String KEY_ROW_1_DATA`

Persisted config key `"row1Data"`.

#### KEY_ROW_2_DATA

`String KEY_ROW_2_DATA`

Persisted config key `"row2Data"`.

#### KEY_ROW_3_DATA

`String KEY_ROW_3_DATA`

Persisted config key `"row3Data"`.

#### KEY_SCREEN_OVERLAY_LAYOUT

`String KEY_SCREEN_OVERLAY_LAYOUT`

Persisted config key `"screenOverlayLayout"`.

#### KEY_SCREEN_OVERLAY_ON_TOP

`String KEY_SCREEN_OVERLAY_ON_TOP`

Persisted config key `"screenOverlayOnTop"`.

#### KEY_SHOW_ALCH_INFO

`String KEY_SHOW_ALCH_INFO`

Persisted config key `"showAlchInfo"`.

#### KEY_SHOW_COLLECTION_VALUES

`String KEY_SHOW_COLLECTION_VALUES`

Persisted config key `"showCollectionValues"`.

#### KEY_SHOW_COL_AVG

`String KEY_SHOW_COL_AVG`

Persisted config key `"showColAvg"`.

#### KEY_SHOW_COL_HIGH

`String KEY_SHOW_COL_HIGH`

Persisted config key `"showColHigh"`.

#### KEY_SHOW_COL_LOW

`String KEY_SHOW_COL_LOW`

Persisted config key `"showColLow"`.

#### KEY_SHOW_COL_VOLUME

`String KEY_SHOW_COL_VOLUME`

Persisted config key `"showColVolume"`.

#### KEY_SHOW_EST_AVG

`String KEY_SHOW_EST_AVG`

Persisted config key `"showEstAvg"`.

#### KEY_SHOW_EST_HIGH

`String KEY_SHOW_EST_HIGH`

Persisted config key `"showEstHigh"`.

#### KEY_SHOW_EST_LOW

`String KEY_SHOW_EST_LOW`

Persisted config key `"showEstLow"`.

#### KEY_SHOW_EST_PROFIT

`String KEY_SHOW_EST_PROFIT`

Persisted config key `"showEstProfit"`.

#### KEY_SHOW_GE_ESTIMATES

`String KEY_SHOW_GE_ESTIMATES`

Persisted config key `"showGeEstimates"`.

#### KEY_SHOW_ITEM_LOG

`String KEY_SHOW_ITEM_LOG`

Persisted config key `"showItemLog"`.

#### KEY_SHOW_ITEM_PROFIT_ROW

`String KEY_SHOW_ITEM_PROFIT_ROW`

Persisted config key `"showItemProfitRow"`.

#### KEY_SHOW_ITEM_VALUES

`String KEY_SHOW_ITEM_VALUES`

Persisted config key `"showItemValues"`.

#### KEY_SHOW_LINKS

`String KEY_SHOW_LINKS`

Persisted config key `"showLinks"`.

#### KEY_SHOW_MARKET_INFO

`String KEY_SHOW_MARKET_INFO`

Persisted config key `"showMarketInfo"`.

#### KEY_SHOW_NOTIFICATIONS

`String KEY_SHOW_NOTIFICATIONS`

Persisted config key `"showNotifications"`.

#### KEY_SHOW_PRICE_GRAPH

`String KEY_SHOW_PRICE_GRAPH`

Persisted config key `"showPriceGraph"`.

#### KEY_SHOW_PRICE_OVERVIEW

`String KEY_SHOW_PRICE_OVERVIEW`

Persisted config key `"showPriceOverview"`.

#### KEY_SHOW_QUANTITY_VALUE

`String KEY_SHOW_QUANTITY_VALUE`

Persisted config key `"showQuantityValue"`.

#### KEY_SHOW_SCREEN_OVERLAY

`String KEY_SHOW_SCREEN_OVERLAY`

Persisted config key `"showScreenOverlay"`.

#### KEY_SHOW_SESSION

`String KEY_SHOW_SESSION`

Persisted config key `"showSession"`.

#### KEY_SHOW_VOLUME_GRAPH

`String KEY_SHOW_VOLUME_GRAPH`

Persisted config key `"showVolumeGraph"`.

#### KEY_SORT_MODE

`String KEY_SORT_MODE`

Persisted config key `"sortMode"`.

#### KEY_SORT_REVERSED

`String KEY_SORT_REVERSED`

Persisted config key `"sortReversed"`.

#### KEY_SOURCE_PRICING

`String KEY_SOURCE_PRICING`

Persisted config key `"sourcePricing"`.

#### KEY_STALE_PRICE_THRESHOLD

`String KEY_STALE_PRICE_THRESHOLD`

Persisted config key `"stalePriceThresholdMinutes"`.

#### KEY_STOP_TRACKING_COLOR

`String KEY_STOP_TRACKING_COLOR`

Persisted config key `"stopTrackingColor"`.

#### KEY_TRACKED_ITEMS

`String KEY_TRACKED_ITEMS`

Persisted config key `"trackedItemIds"`.

#### KEY_TRACK_ITEM_COLOR

`String KEY_TRACK_ITEM_COLOR`

Persisted config key `"trackItemColor"`.

#### KEY_VERSION_FIRST_SEEN

`String KEY_VERSION_FIRST_SEEN`

Persisted config key `"versionFirstSeen"`.

#### KEY_WHATS_NEW_DISMISSED

`String KEY_WHATS_NEW_DISMISSED`

Persisted config key `"whatsNewDismissed"`.

#### detailViewSection

`String detailViewSection`

Order, visibility, and contents of the per-item detail view sections.

#### geEstimatesSection

`String geEstimatesSection`

Placement, format, spacing, and rows of the estimated GE sell-value block.

#### geIntegrationSection

`String geIntegrationSection`

How the open Grand Exchange offer ties into the Stockpile view.

#### mainViewSection

`String mainViewSection`

Top-level panel behavior: price refresh, change indicator, and global toggles.

#### overlaySection

`String overlaySection`

The in-game on-screen overlay of selected tracked items.

#### trackedItemSection

`String trackedItemSection`

Which columns and rows each tracked-item entry shows in the list.

#### trackingSection

`String trackingSection`

Context-menu integration, highlight colors/mode, and the glow effect.

### Method Detail

#### addContextMenuOption

`default boolean addContextMenuOption()`

Add a "Track Item" / "Stop Tracking" entry to right-click menus on the ground, in the bank, or in the inventory.

#### autoAddItems

`default boolean autoAddItems()`

Automatically add collection-log entries from inventory/bank changes. When off, items are only tracked once you
add them yourself (manual edits still work). The price a change with no observed source buys in at is set
separately by "Fallback Pricing".

#### buySellPressureWindow

`default PressureWindow buySellPressureWindow()`

Look-back period for the Buy/Sell Pressure bar in the Market Info section.

#### compactView

`default boolean compactView()`

Show tracked items as compact two-row entries. Toggleable from the tracked list header.

#### fallbackPricing

`default FallbackPricing fallbackPricing()`

The price an unknown-source change buys in at — mobile/offline sessions and anything no detector observed
(observed sources like GE offers price themselves). High/Low/Avg use the latest matching price, Zero buys in at
0.

#### geEstimatesFormat

`default ValueFormat geEstimatesFormat()`

How GE Estimate prices are formatted. Short abbreviates with k/m/b and shows a full-value tooltip on hover.

#### geEstimatesPosition

`default EstimatesPosition geEstimatesPosition()`

Top: under the search bar above the tracked items list. Bottom: below the tracked items list.

#### geEstimatesSpacing

`default EstimatesSpacing geEstimatesSpacing()`

Vertical spacing of the estimate rows. Default keeps the roomier layout; Compact tightens the rows to match the
tracked items list.

#### geFocusPanel

`default boolean geFocusPanel()`

When a GE offer opens the item in Stockpile, switch to and focus the Stockpile panel. When off, the item is
loaded silently (shown next time you open Stockpile).

#### geIntegration

`default GeIntegrationMode geIntegration()`

Open the current Grand Exchange offer item in Stockpile's view-only mode: via an injected button, automatically,
both, or off.

#### geShowMarketPrices

`default boolean geShowMarketPrices()`

Show the item's latest 5-minute High/Low market prices as a line on the open Grand Exchange offer window.
Independent of the Interaction mode above.

#### geShowTrackButton

`default boolean geShowTrackButton()`

Show a Track/Untrack button beside the History button on the open Grand Exchange offer window, toggling tracking
of the offer's item. Independent of the Interaction mode above.

#### glowEffect

`default GlowSpeed glowEffect()`

Speed of the highlight's breathing/glow effect. Off results in a solid color.

#### highlightColor

`default Color highlightColor()`

Color used to outline the highlighted tracked item.

#### highlightTrackedItems

`default HighlightMode highlightTrackedItems()`

Where to outline tracked items.

#### notificationStyle

`default Notification notificationStyle()`

Master switch and delivery style for per-item notifications. Set to Off to disable all item notifications;
otherwise use the gear to choose how they are delivered. Independent of "Show Notifications", which only
controls where the rule editor appears.

#### priceChangeIndicator

`default PriceIndicatorMode priceChangeIndicator()`

How to display the pulse indicator for price changes.

#### priceOverviewRows

`default OverviewPreset priceOverviewRows()`

How many time-window rows the Price Overview shows. Recent: 5m, 1h, 12h, 24hr. Standard: 5m, 1h, 24hr, 1wk, 1mo.
Detailed: all windows.

#### priceRefreshSeconds

`default int priceRefreshSeconds()`

How often to refresh GE prices from the API. Minimum 30 seconds.

#### promptCategoryOnTrack

`default boolean promptCategoryOnTrack()`

When you track an item, ask which category to put it in (choose an existing one, create a new one, or skip to
Uncategorized). Applies only to explicit tracking, not auto-added or view-only items.

#### row1Data

`default TimeWindow row1Data()`

Price data shown on the first row. None hides the row.

#### row2Data

`default TimeWindow row2Data()`

Price data shown on the second row. None hides the row.

#### row3Data

`default TimeWindow row3Data()`

Price data shown on the third row. None hides the row.

#### screenOverlayLayout

`default OverlayLayout screenOverlayLayout()`

Compact two-row entries, or a replica of the standard tracked-item row.

#### screenOverlayOnTop

`default boolean screenOverlayOnTop()`

Keep the overlay above open interfaces. When off, it renders behind windows like the bank or Grand Exchange.

#### showAlchInfo

`default SectionSlot showAlchInfo()`

Position of the Alchemy Info section, or None to hide it.

#### showColAvg

`default boolean showColAvg()`

Show the Avg column in the tracked items list.

#### showColHigh

`default boolean showColHigh()`

Show the High column in the tracked items list.

#### showColLow

`default boolean showColLow()`

Show the Low column in the tracked items list.

#### showColVolume

`default boolean showColVolume()`

Show the Volume column in the tracked items list.

#### showCollectionValues

`default SectionSlot showCollectionValues()`

Position of the Collection Current Values section, or None to hide it.

#### showEstAvg

`default boolean showEstAvg()`

Show the row containing the estimated average value.

#### showEstHigh

`default boolean showEstHigh()`

Show the row containing the estimated high value.

#### showEstLow

`default boolean showEstLow()`

Show the row containing the estimated low value.

#### showEstProfit

`default boolean showEstProfit()`

Show the row containing the estimated profit.

#### showGeEstimates

`default boolean showGeEstimates()`

Show the Estimated GE Sell Value section.

#### showItemLog

`default SectionSlot showItemLog()`

Position of the Item Collection Log section, or None to hide it.

#### showItemProfitRow

`default boolean showItemProfitRow()`

Show the Est. Profit row below each tracked item using only that item's cost basis.

#### showItemValues

`default SectionSlot showItemValues()`

Position of the Item Current Values section, or None to hide it.

#### showLinks

`default SectionSlot showLinks()`

Position of the Links section (Wiki / Live Prices), or None to hide it.

#### showMarketInfo

`default SectionSlot showMarketInfo()`

Position of the Market Info section, or None to hide it.

#### showNotifications

`default SectionSlot showNotifications()`

Position of the per-item notification rule editor, or None to hide it. Does not enable or disable notifications
— use the "Notifications" setting for that.

#### showPriceGraph

`default SectionSlot showPriceGraph()`

Position of the Price Graph section, or None to hide it.

#### showPriceOverview

`default SectionSlot showPriceOverview()`

Position of the Price Overview section, or None to hide it.

#### showQuantityValue

`default boolean showQuantityValue()`

Show the quantity value next to the item name.

#### showScreenOverlay

`default boolean showScreenOverlay()`

Show the items selected (via the manage view) as a draggable in-game overlay.

#### showSession

`default boolean showSession()`

Show the row containing the value gained/lost since login.

#### showVolumeGraph

`default SectionSlot showVolumeGraph()`

Position of the Volume Graph section, or None to hide it.

#### sortMode

`default SortMode sortMode()`

Order of the tracked items list. Any mode except Manual sorts for display only and disables drag reordering.
Also toggleable from the tracked list header.

#### sortReversed

`default boolean sortReversed()`

Reverses the sort direction of the tracked items list (flips each mode's default ascending/descending order).
Also toggleable from the tracked list header.

#### sourcePricing

`default boolean sourcePricing()`

Price quantity changes by how they occurred (GE offers, pickups, shops, alchemy...) as those detectors arrive.
Off restores classic pricing: the Auto Add price for additions and the average price for removals. Activity
already in flight when switched off (an open GE offer, an unrecovered death) still settles as detected.

#### stalePriceThresholdMinutes

`default int stalePriceThresholdMinutes()`

Dim the Ltst high or low when its last trade is older than this many minutes.

#### stopTrackingColor

`default Color stopTrackingColor()`

Color of the "Stop Tracking" context menu entry.

#### trackItemColor

`default Color trackItemColor()`

Color of the "Track Item" context menu entry.

---

## com.oveduumnakal.StockpileGroundOverlay

_class_

`public class StockpileGroundOverlay`

Scene overlay that outlines tracked items lying on the ground.

<p>On each frame it walks the plugin's known ground items, keeps those whose
canonical id is tracked, and draws their tile polygon in the configured
highlight color &ndash; pulsing via the plugin's breathing alpha. Does nothing
when ground highlighting is disabled in config.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final Client` | `client` |  |
| `private final StockpileConfig` | `config` |  |
| `private final ItemManager` | `itemManager` |  |
| `private final StockpilePlugin` | `plugin` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `StockpileGroundOverlay(Client client, StockpilePlugin plugin, StockpileConfig config, ItemManager itemManager)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public Dimension` | `render(Graphics2D graphics)` | Draws the ground-item highlights for tracked items on the game scene. |

### Field Detail

#### client

`private final Client client`

#### config

`private final StockpileConfig config`

#### itemManager

`private final ItemManager itemManager`

#### plugin

`private final StockpilePlugin plugin`

### Constructor Detail

#### StockpileGroundOverlay

`StockpileGroundOverlay(Client client, StockpilePlugin plugin, StockpileConfig config, ItemManager itemManager)`

### Method Detail

#### render

`public Dimension render(Graphics2D graphics)`

Draws the ground-item highlights for tracked items on the game scene.

- **Parameter** `graphics` — the overlay graphics context
- **Returns:** `null` (this overlay has no fixed bounds)

---

## com.oveduumnakal.StockpileHighlightOverlay

_class_

`public class StockpileHighlightOverlay`

Widget overlay that draws a colored outline around tracked items in the
inventory and bank.

<p>For each rendered item widget whose canonical id is tracked, it fetches the
item's outline image and blits it at the configured highlight color, modulated
by the plugin's pulsing breathing alpha. Skips rendering when inventory/bank
highlighting is disabled in config.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final StockpileConfig` | `config` |  |
| `private final ItemManager` | `itemManager` |  |
| `private final StockpilePlugin` | `plugin` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `StockpileHighlightOverlay(StockpilePlugin plugin, StockpileConfig config, ItemManager itemManager)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public void` | `renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)` | Highlights a tracked item's widget in the inventory or bank. |

### Field Detail

#### config

`private final StockpileConfig config`

#### itemManager

`private final ItemManager itemManager`

#### plugin

`private final StockpilePlugin plugin`

### Constructor Detail

#### StockpileHighlightOverlay

`StockpileHighlightOverlay(StockpilePlugin plugin, StockpileConfig config, ItemManager itemManager)`

### Method Detail

#### renderItemOverlay

`public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)`

Highlights a tracked item's widget in the inventory or bank.

- **Parameter** `graphics` — the overlay graphics context
- **Parameter** `itemId` — the item id being rendered
- **Parameter** `widgetItem` — the widget item to highlight

---

## com.oveduumnakal.StockpilePanel

_class_

`public class StockpilePanel`

The plugin's side panel and its entire Swing UI.

<p>Uses a `CardLayout` to switch between two views: the main list of
tracked items (with search, totals, and per-row prices/value/profit) and a
per-item detail card (current values, market info, price/volume charts, a
price overview grid, alch info, notification rules, and an editable
acquisitions log). It also manages detail-section ordering/visibility, the
price-change pulse animations, and the chart pop-out windows.

<p>The panel is purely a view: it never touches game state directly. All
actions (add/remove item, edit acquisitions/notifications, request detail
data, clear) are delegated to the plugin through the callbacks supplied to the
constructor, and the plugin pushes data back via `#rebuild` and
`#refreshDetailData`. All methods run on the Swing EDT.

### Nested Type Summary

| Type | Description |
|---|---|
| _class_ [`ChangelogSection`](#comoveduumnakalstockpilepanelchangelogsection) | One navigable changelog section: heading depth (0 for `##`, 1 for `###`), text, and anchor. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final String` | `BUG_TEMPLATE` |  |
| `private static final String` | `CARD_DETAIL` |  |
| `private static final String` | `CARD_LOGGED_OUT` |  |
| `private static final String` | `CARD_MAIN` |  |
| `private static final String` | `CL_AREA_STYLE` |  |
| `private static final String` | `CL_FEATURE_STYLE` |  |
| `private static final int` | `CL_INDENT_STEP` | Pixels of left indent added per nesting level in the changelog body. |
| `private static final String` | `CL_SECTION_STYLE` |  |
| `private static final String` | `CL_TEXT_STYLE` |  |
| `private static final Color` | `COLOR_AVG` |  |
| `private static final Color` | `COLOR_HIGH` |  |
| `private static final Color` | `COLOR_HIGH_STALE` |  |
| `private static final Color` | `COLOR_LOW` |  |
| `private static final Color` | `COLOR_LOW_STALE` |  |
| `private static final Color` | `COLOR_VOLUME` |  |
| `private static final Dimension` | `DELTA_LABEL_SIZE` |  |
| `private static final Color` | `DIVIDER_COLOR` |  |
| `private static final Color` | `DRAG_LINE_COLOR` |  |
| `private static final int` | `DRAG_SCROLL_MARGIN` |  |
| `private static final int` | `DRAG_SCROLL_STEP` |  |
| `private static final Border` | `ESTIMATE_ROW_BORDER_COMPACT` |  |
| `private static final Border` | `ESTIMATE_ROW_BORDER_DEFAULT` |  |
| `private static final String[]` | `FEATURE_AREAS` | Feature-template "Related area" dropdown options, matched exactly for URL prefill. |
| `private static final String` | `FEATURE_TEMPLATE` |  |
| `private static final Color` | `FOOTER_DIVIDER_COLOR` | Fainter divider above the footer's Report/Request row: dimmer than `#DIVIDER_COLOR` but still visible over the (40,40,40) background. |
| `private static final String` | `GITHUB_NEW_ISSUE` | GitHub new-issue endpoint and templates; the footer forms deep-link here with fields prefilled. |
| `private static final String` | `GROUP_HEADER_KEY` | Client property marking a group accordion header, used to find group boundaries during a drag. |
| `private static final Color` | `LOADING_COLOR` |  |
| `private static final float` | `LOADING_GLOW_MIN_ALPHA` |  |
| `private static final long` | `LOADING_GLOW_PERIOD_MS` |  |
| `private static final Pattern` | `MD_LINK` | A markdown link `[label](url)` used for the changelog's issue references. |
| `private static final String` | `NEW_CATEGORY_LABEL` |  |
| `private static final NumberFormat` | `NUMBER_FORMAT` |  |
| `private static final int` | `PRICES_LEFT_PAD` |  |
| `private static final int` | `PRICES_RIGHT_PAD` |  |
| `private static final Border` | `PROFIT_SECTION_BORDER_COMPACT` |  |
| `private static final Border` | `PROFIT_SECTION_BORDER_DEFAULT` |  |
| `private static final long` | `PULSE_DURATION_MS` |  |
| `private static final Color` | `REMOVE_COLOR` |  |
| `private static final String` | `ROW_ITEM_ID` | Client property on each row card holding its item id, used to map drag positions to list indices. |
| `private static final Color` | `STAR_DIM` |  |
| `private static final Color` | `STAR_HIDDEN` |  |
| `private static final String` | `STAR_HOVERED` |  |
| `private static final Color` | `STAR_PREVIEW` |  |
| `private static final String` | `STAR_ROW_HOVERED` |  |
| `private static final Color` | `TINT_AVG` |  |
| `private static final Color` | `TINT_HIGH` |  |
| `private static final Color` | `TINT_LOW` |  |
| `private static final Color` | `TINT_VOLUME` |  |
| `private static final Border` | `TITLE_BORDER_NO_DIVIDER` |  |
| `private static final Border` | `TITLE_BORDER_WITH_TOP_DIVIDER` |  |
| `private static final String` | `UNCATEGORIZED_LABEL` |  |
| `private final JPanel` | `bottomPanel` |  |
| `private final CardLayout` | `cardLayout` |  |
| `private final JPanel` | `cardsHost` |  |
| `private List<CategoryState>` | `categories` | Latest category state from the plugin, used to render the grouped/accordion list. |
| `private JLabel` | `categoriesButton` | Header button (manage mode only) that opens the Manage Categories dialog. |
| `private final CategoryActions` | `categoryActions` | Category create/rename/delete/reorder and per-item assignment operations. |
| `private final Changelog` | `changelog` | Bundled release notes shown in the changelog window. |
| `private JButton` | `changelogButton` | The footer "What's New ✨" / "Change log" indicator button. |
| `private final JButton` | `clearButton` |  |
| `private final JLabel` | `coinsIcon` |  |
| `private final Map<Integer,ImageIcon>` | `coinsIconCache` |  |
| `private JLabel` | `compactToggle` | Header toggle that switches between the standard and compact row layouts. |
| `private JLabel` | `compactTotalsCountLabel` |  |
| `private JPanel` | `compactTotalsRows` | Compact-view totals: a two-line "Total / profit (avg)" panel shown instead of the high/low/avg rows. |
| `private JLabel` | `compactTotalsValueLabel` |  |
| `private final StockpileConfig` | `config` |  |
| `private EstimatesPosition` | `currentEstimatesPosition` |  |
| `private final Map<Integer,TrackedItem>` | `currentItems` |  |
| `private final DetailView` | `detailView` | The sidebar's detail view (extracted for #110); the host mounts it as the `#CARD_DETAIL` card. |
| `private List<Integer>` | `dragGroupIds` | The dragged item's group (visual-order item ids), so a drag stays within its group. |
| `private int` | `dragInsertIndex` | The list index where the dragged item would be inserted on drop. |
| `private int` | `dragItemId` | Drag-reorder state. |
| `private int` | `dragLineY` | The y-coordinate (in `#trackedItemsPanel` space) at which to paint the drop indicator line. |
| `private int` | `dragScrollDir` | Autoscroll direction while dragging: -1 up, +1 down, 0 none. |
| `private Timer` | `dragScrollTimer` | Edge-autoscroll timer active while a drag hovers near the viewport top/bottom. |
| `private final IntFunction<String>` | `examineLookup` |  |
| `private boolean` | `favoritesCollapsed` |  |
| `private JLabel` | `filterToggle` | Header toggle that shows/hides the tracked-list filter field. |
| `private long` | `fireRunePrice` |  |
| `private final JPanel` | `footerPanel` |  |
| `private final JPanel` | `geEstimatesSlotBottom` |  |
| `private final JPanel` | `geEstimatesSlotTop` |  |
| `private boolean` | `groupingActive` | Whether the list is currently grouped (favorites or categories active); disables drag reorder, which is global-order only. |
| `private int` | `hoveredItemId` |  |
| `private final ItemManager` | `itemManager` |  |
| `private long` | `lastCoinsIconValue` |  |
| `private volatile Instant` | `lastPriceRefresh` |  |
| `private final JLabel` | `lastRefreshLabel` |  |
| `private PriceIndicatorMode` | `lastRenderIndicatorMode` |  |
| `private List<TrackedItem>` | `lastRenderItems` | Last-rendered items/mode, retained so toggling manage mode can re-render rows without a full plugin refresh, and so a session reset (`#resetSession()`) re-primes from the same list. |
| `private final Timer` | `loadingGlowTimer` |  |
| `private final List<JLabel>` | `loadingLabels` |  |
| `private JPanel` | `loggedOutCard` | The logged-out placeholder card; tracked so `#cardsHost` can fill the viewport while it shows. |
| `private long` | `natureRunePrice` |  |
| `private final Consumer<Integer>` | `onAcquisitionsEdited` |  |
| `private final BiConsumer<Integer,TrackItemMode>` | `onAddItem` |  |
| `private final Consumer<Integer>` | `onClearAcquisitions` |  |
| `private final Runnable` | `onClearAll` |  |
| `private final Consumer<Consumer<String>>` | `onExportCsv` | Builds the acquisitions CSV on the client thread and delivers it back on the EDT. |
| `private final Consumer<Consumer<String>>` | `onExportList` | Builds the shareable tracked-list token on the client thread and delivers it back on the EDT. |
| `private final BiConsumer<String,Consumer<String>>` | `onImportList` | Imports a tracked-list token (merge, non-destructive); delivers a user-facing result message on the EDT. |
| `private final Consumer<Integer>` | `onNotificationsEdited` |  |
| `private final Supplier<List<long[]>>` | `onPortfolioHistory` | Supplies the portfolio value history points (`{epochSeconds, value, costBasis`}) for the chart. |
| `private final Consumer<Integer>` | `onRemoveItem` |  |
| `private final BiConsumer<Integer,Integer>` | `onReorder` | Reorder callback: (itemId, targetIndex) — moves the item to a new position in the tracked list. |
| `private final Consumer<Integer>` | `onRequestDetailData` |  |
| `private final BiConsumer<Integer,Boolean>` | `onSetFavorite` | Favorite toggle callback: (itemId, favorite) — pins/unpins an item to the top Favorites group. |
| `private final Consumer<List<Integer>>` | `onSetGlobalOrder` | Drag-reorder callback: replaces the full tracked-item order with the given id sequence. |
| `private final BiConsumer<String,Boolean>` | `onSetGroupCollapsed` | Group collapse callback: (groupKey, collapsed) — persists a group's accordion state. |
| `private final BiConsumer<Integer,Boolean>` | `onSetItemCompact` | Per-item compact toggle callback: (itemId, compact) — flips one row's compact override (#210). |
| `private final BiConsumer<Integer,Boolean>` | `onSetOnOverlay` | Overlay toggle callback: (itemId, onOverlay) — adds/removes an item from the on-screen overlay. |
| `private final Consumer<SortMode>` | `onSetSortMode` |  |
| `private final Runnable` | `onToggleCompactView` | Flips the persisted compact-view config flag; the resulting config change rebuilds the list. |
| `private final Runnable` | `onToggleSortDirection` | Flips the persisted sort-direction flag; the resulting config change rebuilds the list. |
| `private final Consumer<Integer>` | `onUntrackToPreview` | Untracks the shown item but keeps the detail view open as a preview (#138). |
| `private final Runnable` | `onWhatsNewSeen` | Callback to persist that the current release's "What's New" has been seen. |
| `private final List<PopoutHandle>` | `openPopouts` |  |
| `private final List<Integer>` | `orderedItemIds` | Item ids in current display order, kept in sync on each `#rebuild`, used to compute reorder targets. |
| `private JButton` | `portfolioChartButton` | Opens the portfolio value chart; hidden until at least two history points exist to plot. |
| `private Component` | `portfolioChartStrut` | WEST strut balancing `#portfolioChartButton` so the title stays centred; toggled with it. |
| `private final List<Runnable>` | `portfolioPopoutRefreshers` | Re-fetch actions for open portfolio-chart pop-outs; run on every rebuild so they update live. |
| `private final JLabel` | `profitLabel` |  |
| `private final JPanel` | `profitSection` |  |
| `private final List<PulseEntry>` | `pulseEntries` |  |
| `private final Timer` | `pulseTimer` |  |
| `private final Timer` | `refreshAgeTimer` |  |
| `private boolean` | `reorderMode` | Whether the list is in reorder mode, which reveals the per-row drag/arrow strip. |
| `private JLabel` | `reorderToggle` | Header toggle that enters/exits reorder mode. |
| `private final Map<Long,ImageIcon>` | `rowIconCache` | 18px row icons keyed by `#iconCacheKey` (item id + rendered stack size), so quantity-aware sprites are cached per stack. |
| `private final IconTextField` | `searchField` |  |
| `private final JPanel` | `searchResultsPanel` |  |
| `private final JLabel` | `sessionLabel` | Static grey "Session:" prefix; never recoloured, mirroring the profit row's prefix. |
| `private JPanel` | `sessionRow` | The row wrapping `#sessionLabel`; toggled as a whole so no empty row lingers when hidden. |
| `private final SessionStats` | `sessionStats` | In-memory session tracking; baseline captured on the first priced render after a reset. |
| `private final JLabel` | `sessionValueLabel` | Value gained/lost since the session baseline (login or manual reset); the only part recoloured. |
| `private JLabel` | `sortToggle` | Header toggle that opens the sort-mode menu; highlighted when a non-manual sort is active. |
| `private final JLabel` | `totalAvgDeltaLabel` |  |
| `private final JLabel` | `totalAvgLabel` |  |
| `private final JPanel` | `totalAvgRow` |  |
| `private final JLabel` | `totalHighDeltaLabel` |  |
| `private final JLabel` | `totalHighLabel` |  |
| `private final JPanel` | `totalHighRow` |  |
| `private final JLabel` | `totalLowDeltaLabel` |  |
| `private final JLabel` | `totalLowLabel` |  |
| `private final JPanel` | `totalLowRow` |  |
| `private JPanel` | `totalsRows` | The standard totals rows (high/low/avg + profit), toggled off in compact view. |
| `private final JLabel` | `totalsTitle` |  |
| `private JPanel` | `totalsTitleRow` | Title row hosting `#totalsTitle` and the chart pop-out button; carries the toggle-able divider. |
| `private String` | `trackedFilter` |  |
| `private IconTextField` | `trackedFilterField` | Name filter over the tracked list, shown only when the list overflows into scrolling. |
| `private final Set<Integer>` | `trackedItemIds` |  |
| `private final JPanel` | `trackedItemsPanel` |  |
| `private boolean` | `uncategorizedCollapsed` |  |
| `private boolean` | `whatsNew` | Whether the footer indicator is currently in the highlighted "What's New" state. |

### Constructor Summary

| Constructor | Description |
|---|---|
| `StockpilePanel(ItemManager itemManager, StockpileConfig config, CategoryActions categoryActions, PanelActions actions, Changelog changelog, boolean whatsNew)` | Builds the panel and its two cards (main list and detail view). |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public void` | `acquisitionsEdited(int itemId)` | {@inheritDoc} Delegates to the panel's acquisitions-edited callback when present. |
| `private void` | `addFormRow(JPanel form, String label, JComponent field)` | Adds a labelled row (label above the field) to a vertical form panel. |
| `public void` | `addItem(int itemId, TrackItemMode mode)` | {@inheritDoc} Delegates to the panel's add-item callback. |
| `private void` | `addItemRow(TrackedItem item, PriceIndicatorMode indicatorMode, List<TrackedItem> groupItems)` | Adds a single tracked-item row plus its trailing spacer; `groupItems` scopes reorder within the group. |
| `private void` | `addListenerRecursively(Component c, MouseListener listener)` | Attaches a mouse listener to a component and all its descendants, so a whole row reacts as one. |
| `private static void` | `appendChangelogAnchor(StringBuilder sb, int sectionIndex)` | Appends a named scroll anchor (`sec `) matching the ids `#extractSections` hands the nav. |
| `private static void` | `appendChangelogDiv(StringBuilder sb, String style, int indentLevel, String html)` | Appends a ` ` with the given inline CSS `style` and left indent, wrapping `html`. |
| `private void` | `applyChangelogButtonStyle()` | Applies the indicator styling — gold while "What's New", muted once seen. |
| `private void` | `applyEstimatesPosition(EstimatesPosition position)` | Moves the GE estimates block above or below the other sections per the configured position. |
| `private void` | `applyEstimatesSpacing(EstimatesSpacing spacing)` | Applies normal or compact row padding to the GE estimates block per the configured spacing. |
| `private void` | `applyLiveStaleness(JLabel cell, long value, String sideLabel, String timeLabel, long tradeTime, Color freshColor, Color staleColor)` | Reflects the staleness of a Ltst high/low value on its cell: appends the last trade time as a second tooltip line and dims the value's color when that trade is older than the configured threshold. |
| `private void` | `applyRowIcon(JLabel iconLabel, TrackedItem item)` | Sets a label's 18px quantity-aware item icon from `#rowIconCache`, loading asynchronously on a miss. |
| `private void` | `applyTotalTooltip(JLabel label, long value, ValueFormat fmt)` | Gives a totals label a full-number tooltip when its text is abbreviated, none otherwise. |
| `private void` | `autoCategorizeFromDialog(JDialog dialog)` | Prompts for the auto-categorize scope (uncategorized only vs. |
| `private JComboBox<String>` | `buildCategoryPicker(TrackedItem item)` | Builds the per-row category picker used in the manage row: assigns the item to an existing category, clears it to Uncategorized, or prompts to create-and-assign a new one. |
| `private JButton` | `buildChangelogBadge()` | Builds the top-right header badge that opens the changelog window; `#applyChangelogButtonStyle` sets its label and colour. |
| `private JComponent` | `buildChangelogContent()` | Builds the changelog window: a left navigation column listing each release, with the selected release's sections expanded beneath it as quick-links that jump to that section, and the selected release's notes rendered on the right. |
| `private JLabel` | `buildChangelogNavLink(ChangelogSection section)` | Builds one indented, clickable section quick-link for the changelog nav. |
| `private JLabel` | `buildChangelogNavVersion(String version, boolean selected)` | Builds one clickable release row for the changelog nav; the selected release is gold, the rest muted. |
| `private Icon` | `buildChartIcon()` | Paints the small line-chart icon used to open the portfolio value chart. |
| `private JPanel` | `buildCompactValueRow(TrackedItem item)` | Builds the compact-view row-2 value line: `total value (single item value)`, both in short format and both derived from the latest avg-of-1 price (e.g. |
| `private JPanel` | `buildDividerStrip()` | Builds the horizontal divider strip drawn between the totals block and the footer. |
| `private Icon` | `buildEyeIcon(int size)` | Loads the bundled `eye.png` scaled to a square icon for the view-only button. |
| `private JLabel` | `buildFavoriteStar(TrackedItem item)` | Builds the favorite-toggle star shown beneath each row's remove button. |
| `private JButton` | `buildFooterLink(String text, Runnable onClick, String tooltip)` | Builds a small footer button that runs the given action when clicked. |
| `private JButton` | `buildFooterMenu(String text, JPopupMenu menu, String tooltip)` | A footer button that drops `menu` below itself, grouping related actions so the footer stays one row. |
| `private JMenuItem` | `buildFooterMenuItem(String text, Runnable onClick, String tooltip)` | One action inside a footer dropdown, styled to match the footer links. |
| `private JPanel` | `buildGroupHeader(String title, String groupKey, boolean collapsed, long groupTotal)` | Builds a clickable accordion header (chevron + title + group total value) that toggles the group's collapsed state. |
| `static JButton` | `buildIconButton(Icon icon, String tooltip, Runnable onClick)` | Builds a borderless icon button with the given icon, tooltip, click action, and a hover highlight. |
| `private static String` | `buildIssueUrl(String template, String titlePrefix, String title, List<IssueField> fields, Map<IssueField,JComponent> inputs)` | Builds the GitHub new-issue URL with the title and non-empty fields pre-filled as query params. |
| `private JPanel` | `buildManageEastControls(TrackedItem item)` | Builds the right column of the manage row: an always-visible remove button stacked over a favorite star. |
| `private JPanel` | `buildManageRow(TrackedItem item, List<TrackedItem> groupItems)` | Builds the dedicated manage-mode row: a stripped-down layout showing only what's needed to organise items. |
| `private JLabel` | `buildOverlayToggle(TrackedItem item)` | Builds the overlay-select control beneath the favorite star: a painted monitor icon that toggles whether the item appears in the on-screen overlay. |
| `private JPanel` | `buildReorderStrip(TrackedItem item, List<TrackedItem> groupItems)` | Builds the left reorder column (up/down, plus a drag handle when the list isn't grouped) for the manage row. |
| `private JLabel` | `buildRowCompactToggle(TrackedItem item)` | Builds the per-item compact toggle beneath the overlay button (#210): a painted "≣" glyph that flips this row between the standard and compact two-row layouts, independent of the global compact toggle. |
| `private JLabel` | `buildRowIcon(TrackedItem item)` | Builds an 18px item-icon label backed by `#rowIconCache`, loading asynchronously on a miss. |
| `private JPanel` | `buildSearchResultRow(int itemId, String itemName)` | Builds one clickable row in the search-results dropdown that adds the item when clicked. |
| `private JPanel` | `buildTotalsRow(JLabel valueLabel, JLabel pulseLabel)` | Builds one estimate row pairing a totals value label with its pulse-indicator label. |
| `private JPanel` | `buildTrackedItemRow(TrackedItem item, PriceIndicatorMode indicatorMode, List<TrackedItem> groupItems)` | Builds one row of the main list for a tracked item: icon, name, quantity, the configured data rows (prices/value/volume/profit), hover affordances, and a click handler that opens the item's detail view. |
| `private static Icon` | `categoriesIcon(Color color)` | Draws a bulleted-list glyph — three dots, each followed by a line — tinted `color`. |
| `private String` | `changelogButtonText()` |  |
| `public void` | `clearAcquisitions(int itemId)` | {@inheritDoc} Delegates to the panel's clear-acquisitions callback when present. |
| `public void` | `clearSessionBaseline()` | Drops the session baseline without re-priming (used on profile change): the next rebuild captures the new profile's holdings as the baseline. |
| `private void` | `closePopouts()` | Disposes all open pop-out windows owned by the panel (portfolio, What's New). |
| `private void` | `commitDrag()` | Commits the in-progress drag: places the dragged item at its new slot within its own group and rewrites the full tracked order accordingly (kept within-group, since groups render in global order). |
| `private List<Integer>` | `computeDragGroup(int itemId)` | Determines the dragged item's group as the contiguous run of item rows between accordion headers in the rendered list (the whole list when ungrouped), returning its item ids in visual order. |
| `public StockpileConfig` | `config()` | {@inheritDoc} Supplies the panel's live plugin config to the detail view. |
| `private void` | `confirmAndClearAll()` | Prompts for confirmation, then clears all tracked items via the plugin callback. |
| `private static boolean` | `containsIgnoreCase(DefaultListModel<String> model, String value)` |  |
| `private void` | `copyToClipboard(String text)` |  |
| `private JLabel` | `createDeltaLabel()` | Creates a fixed-size label that hosts the ▲/▼ price-change pulse next to a value. |
| `private void` | `dragAutoscrollTick()` | One autoscroll step: nudges the viewport in `#dragScrollDir` and recomputes the drop target. |
| `private static String` | `encode(String value)` | URL-encodes a value for a query parameter (spaces as %20, not +). |
| `private void` | `equalizeTotalsLabelWidths()` | Fixes the three totals value labels to the widest one's width so the columns stay aligned. |
| `private static String` | `escapeHtml(String text)` | Escapes the HTML-significant characters so text renders literally inside an HTML label. |
| `public String` | `examine(int itemId)` | {@inheritDoc} Resolves the examine text through the panel's examine lookup. |
| `private void` | `exportAcquisitionsCsv()` | Copies the acquisitions log as CSV to the clipboard once the plugin has built it. |
| `private void` | `exportTrackedList()` | Copies the shareable tracked-list code to the clipboard once the plugin has built it. |
| `private static List<ChangelogSection>` | `extractSections(String body)` |  |
| `private static String` | `fieldValue(JComponent input)` |  |
| `private static Icon` | `filterIcon(Color color)` | Paints a small monochrome funnel (filter) icon in the given colour: a wide top bar tapering to a narrow central stem. |
| `public long` | `fireRunePrice()` | {@inheritDoc} Returns the fire-rune price the panel currently holds. |
| `static String` | `formatAge(long epochSeconds)` | Formats an epoch-second timestamp's age as a compact relative string, e.g. |
| `static String` | `formatTotalGp(long value, ValueFormat fmt)` | Formats a totals value as either full or abbreviated gp per the configured `ValueFormat`. |
| `public int` | `getDetailItemId()` |  |
| `private static long` | `iconCacheKey(TrackedItem item)` |  |
| `private void` | `importTrackedList()` | Prompts for a tracked-list code, merges it into the current profile, and reports the outcome. |
| `private static int` | `indexOfItem(List<TrackedItem> list, int itemId)` |  |
| `private static String` | `inlineLinks(String text)` | Escapes `text`, then turns markdown `[label](url)` links into clickable HTML anchors. |
| `private void` | `installCategoryDragReorder(JList<String> list, DefaultListModel<String> model)` | Enables drag-and-drop reordering on the Manage Categories list (#212): dragging a category and dropping it between two others sets the order in one gesture, committing through the same `CategoryActions#reorder(String, int)` path as the ↑/↓ buttons. |
| `private void` | `installChangelogNavHover(JLabel label, Color restFg, Color restBg)` | Adds a hover highlight (brighten to white on a lighter row) that restores the given resting colours. |
| `private void` | `installDragHandle(JLabel handle, int itemId)` | Wires drag-to-reorder onto a row's drag handle: pressing starts the drag, dragging updates the drop indicator and edge autoscroll, and releasing commits the move. |
| `private void` | `installItemValue(JLabel label, long value, String prefix, Color tint)` | Installs a compact gp value on a label with no tooltip caption. |
| `private void` | `installItemValue(JLabel label, long value, String prefix, String tooltipLabel, Color tint)` | Installs a prefixed compact gp value on a label via `#installShortValue`. |
| `private void` | `installRowHover(JPanel card, TrackedItem item, JButton removeBtn, JLabel favStar, JLabel overlayBtn, JLabel compactBtn, Color removeColor, Color removeHidden)` | Wires the shared row hover behaviour onto a tracked-item card: clicking the row (other than the remove button, favorite star, overlay button, or compact button) opens the detail view, and entering/leaving the card tracks `#hoveredItemId` and reveals/hides the remove button, favorite star, and the (optional) overlay-select and per-item compact buttons. |
| `static void` | `installShortValue(JLabel label, long value, String shortText, String tooltipLabel, Color tint)` | Installs a pre-formatted compact value on a label with a full-number tooltip and a hover tint. |
| `private static void` | `installToggleHover(JLabel button, BooleanSupplier selected, Consumer<Color> apply, Runnable restore)` | Installs grey↔gold hover colouring on a header toggle: an unselected (grey) button turns gold while hovered, a selected (gold) button turns grey, and its resting state colour is repainted on exit. |
| `public boolean` | `isEditingNotifications()` |  |
| `private boolean` | `isStale(long epochSeconds)` |  |
| `public ItemManager` | `itemManager()` | {@inheritDoc} Supplies the panel's shared item manager to the detail view. |
| `static Map<Integer,long[]>` | `liveSessionSnapshot(List<TrackedItem> items)` | Builds the session baseline snapshot (item id → [quantity, avg price]) from only the items whose prices came from a live fetch. |
| `private JButton` | `makeRowControl(String glyph, String tooltip)` | Builds a compact, hover-revealed glyph button styled like the row's remove button. |
| `private boolean` | `matchesFilter(TrackedItem item)` |  |
| `private void` | `moveCategoryInDialog(JList<String> list, DefaultListModel<String> model, int delta)` | Moves the selected dialog category by `delta` and forwards the new index to the plugin. |
| `public long` | `natureRunePrice()` | {@inheritDoc} Returns the nature-rune price the panel currently holds. |
| `public void` | `notificationsEdited(int itemId)` | {@inheritDoc} Delegates to the panel's notifications-edited callback when present. |
| `public void` | `onBack()` | {@inheritDoc} Returns the sidebar panel to the main tracked-item list. |
| `private void` | `onSearch(String query)` | Filters the add-item search dropdown to items matching the typed query. |
| `private void` | `onTrackedFilterChanged()` | Re-renders the rows against the updated tracked-list filter text. |
| `private void` | `openChangelogWindow()` | Opens the changelog window; the first open of a new release quiets the "What's New" indicator. |
| `private void` | `openIssueForm(String dialogTitle, String template, String titlePrefix, List<IssueField> fields)` | Shows a modal form for an issue template, then opens the GitHub issue form in the browser with the entered title/fields pre-filled (via query params) so the user only has to review and click Submit on GitHub. |
| `private void` | `openManageCategoriesDialog()` | Opens the modal Manage Categories dialog: create, rename, delete, and reorder categories. |
| `private void` | `openPortfolioChart()` | Opens a pop-out with the portfolio value history chart, fed live from the plugin's stored points. |
| `private void` | `openReportIssueForm()` | Opens the in-plugin "Report a bug" form. |
| `private void` | `openRequestFeatureForm()` | Opens the in-plugin "Request a feature" form. |
| `public void` | `openTrackedDetail(int itemId)` | Opens the tracked detail card for `itemId`. |
| `private int` | `overlayCount()` |  |
| `private static Icon` | `overlayIcon(Color color)` | Paints a small monochrome monitor (on-screen overlay) icon in the given colour. |
| `public void` | `promptCategoryForItem(int itemId)` | Category prompt shown at track time (#211): a modal dropdown of the existing categories plus Uncategorized and a create-new option. |
| `private void` | `pulseIfShown(JLabel label, int delta, PriceIndicatorMode mode)` | Starts a price pulse on the label unless the configured indicator mode suppresses it. |
| `public void` | `rebuild(List<TrackedItem> rawItems, Instant newLastPriceRefresh, PriceIndicatorMode indicatorMode, boolean loggedIn, List<CategoryState> categories, boolean favoritesCollapsed, boolean uncategorizedCollapsed)` | Rebuilds the main item list from the latest tracked items and totals. |
| `private void` | `rebuildChangelogNav(JPanel nav, List<Changelog.Release> releases, int selectedIndex, JEditorPane body)` | (Re)populates the changelog nav: one clickable row per release (selecting it loads its notes), and beneath the selected release its section quick-links, each of which scrolls the notes to that section. |
| `public void` | `refreshDetailData(int itemId)` | Re-populates the open detail view with fresh data for `itemId` (no-op if a different item is shown). |
| `private void` | `refreshFavoriteStar(JLabel star, boolean favorite)` | Applies a favorite star's visual from its row-hover/star-hover client flags: hidden when its row isn't hovered, the resting gold/grey glyph when the row is hovered, and a preview (light-gold fill to add, or grey outline to remove) when the star itself is hovered. |
| `static void` | `removeHoverTint(JLabel label)` | Detaches any hover-tint listener from a label before its value is replaced. |
| `public void` | `removeSessionBaseline(int itemId)` | Drops one item's session-baseline entry when it is untracked, so removal is session-neutral. |
| `private String` | `renderChangelogBody(String body)` | Renders a release's markdown body to HTML for the changelog window: `##`/`###`/`####` headings become sized/weighted/coloured headers that each indent one level deeper, their content indents one level further still, and `[#12](url)` issue links become clickable anchors. |
| `private void` | `renderGroup(String title, String groupKey, boolean collapsed, List<TrackedItem> groupItems, PriceIndicatorMode indicatorMode)` | Renders one collapsible group: a clickable header plus its rows, unless empty (skipped) or collapsed (header only). |
| `private void` | `renderGroupedRows(List<TrackedItem> items, PriceIndicatorMode indicatorMode)` | Renders the tracked rows into `#trackedItemsPanel`, grouped into the Favorites pseudo-group (pinned on top), then each user category in order, then Uncategorized. |
| `private String` | `renderReleaseHtml(Changelog.Release release)` |  |
| `private void` | `renderTrackedRows(List<TrackedItem> items, PriceIndicatorMode indicatorMode)` | Clears and re-renders the tracked-item rows (empty placeholder, or the grouped rows), retaining the inputs so `#toggleReorderMode()` can re-render the manage layout without a full plugin refresh. |
| `public void` | `requestDetailData(int itemId)` | {@inheritDoc} Delegates to the panel's detail-data request callback when present. |
| `public void` | `resetSession()` | Re-baselines the session to the current holdings, so "Session:" restarts from zero. |
| `public void` | `setAlchRunePrices(long naturePrice, long firePrice)` | Supplies the latest nature/fire rune prices used to compute high-alch profit in the detail view. |
| `private void` | `setTrackedFilterVisible(boolean visible)` | Sets the filter field's visibility, clearing any active filter when it is hidden. |
| `private void` | `showDetail(int itemId)` | Reveals the tracked detail card for `itemId` in the card stack and binds the detail view to it. |
| `private void` | `showMain()` | Returns to the main item list, closing any open pop-outs. |
| `private void` | `showPopout(String title, JComponent content, Consumer<TrackedItem> refresher, Runnable onClose)` | Opens a non-modal pop-out window hosting `content`, registering its refresher so live updates reach it and running `onClose` when dismissed. |
| `public void` | `showPreview(TrackedItem item)` | Opens a read-only preview of an untracked item in the detail view. |
| `private void` | `showSortMenu()` | Opens the sort-mode menu on the header toggle, with the active mode checked and its current direction arrow shown. |
| `public void` | `shutdown()` | Stops the animation timers so the panel can be disposed cleanly. |
| `static String` | `signedGp(long v)` |  |
| `private static Icon` | `sparkleIcon(Color color)` | Paints a small firework burst (eight rays capped with sparks) for the "What's New" badge. |
| `private void` | `startPulse(JLabel label, int delta)` | Begins a color pulse on a label (green up / red down) reflecting the sign of a price change. |
| `private void` | `stopDragAutoscroll()` | Stops the edge-autoscroll timer, if running. |
| `private JButton` | `styledFooterButton(String text, String tooltip)` | Shared styling for the footer's link and dropdown buttons. |
| `private void` | `toggleReorderMode()` | Toggles reorder mode, showing or hiding the per-row drag/arrow strips without a full rebuild. |
| `private void` | `toggleTrackedFilter()` | Toggles the tracked-list filter field via the header filter button, focusing it when shown. |
| `public TrackedItem` | `trackedItem(int itemId)` | {@inheritDoc} Reads from the panel's current tracked-item map. |
| `public void` | `untrackToPreview(int itemId)` | {@inheritDoc} Delegates to the panel's untrack-to-preview callback. |
| `private void` | `updateCoinsIcon(long value)` | Updates the totals coin icon to the stack sprite for the given gp value, loading it asynchronously and caching per quantity. |
| `private void` | `updateCompactToggle()` | Highlights the header compact toggle when compact view is active. |
| `private void` | `updateCompactTotals(int itemCount, long totalAvg, long profit, boolean hasPrices, boolean showProfit, ValueFormat fmt)` | Populates the compact totals: item count plus a `total avg value (profit)` line, where the total avg uses the configured value format and the profit is always short format. |
| `private void` | `updateDrag(MouseEvent e)` | Recomputes the drop target and autoscroll state for the current drag pointer, then repaints. |
| `private void` | `updateDragAutoscroll(MouseEvent e)` | Starts/stops edge autoscroll based on whether the drag pointer is near the viewport's top or bottom. |
| `private void` | `updateDropTarget(int yInPanel)` | Finds the list index where a drop at `yInPanel` would insert, and the indicator line position. |
| `private void` | `updateFilterToggle()` | Updates the header filter button's funnel icon, tinting it gold while the filter field is shown. |
| `private void` | `updateLoadingGlow()` | Timer tick that breathes the shared glow colour across every label still awaiting prices. |
| `private void` | `updatePortfolioChartButton()` | Shows the chart pop-out button (and its balancing strut) only once at least two history points exist to plot. |
| `private void` | `updatePulses()` | Timer tick that advances every active pulse's color toward its base, retiring finished ones. |
| `private void` | `updateRefreshLabel()` | Updates the footer's "updated N ago" text from the last price-refresh timestamp. |
| `private void` | `updateReorderToggle()` | Highlights the header reorder toggle and reveals the manage-categories button when manage mode is active. |
| `private void` | `updateSessionLine(List<TrackedItem> items, boolean hasPrices)` | Renders the "Session:" line: the value gained/lost since the baseline, coloured green/red, with a tooltip splitting the change into price movement vs. |
| `private void` | `updateSortToggle()` | Reflects the active sort on the header toggle: the effective direction arrow (highlighted) or the neutral glyph. |

### Field Detail

#### BUG_TEMPLATE

`private static final String BUG_TEMPLATE`

#### CARD_DETAIL

`private static final String CARD_DETAIL`

#### CARD_LOGGED_OUT

`private static final String CARD_LOGGED_OUT`

#### CARD_MAIN

`private static final String CARD_MAIN`

#### CL_AREA_STYLE

`private static final String CL_AREA_STYLE`

#### CL_FEATURE_STYLE

`private static final String CL_FEATURE_STYLE`

#### CL_INDENT_STEP

`private static final int CL_INDENT_STEP`

Pixels of left indent added per nesting level in the changelog body.

#### CL_SECTION_STYLE

`private static final String CL_SECTION_STYLE`

#### CL_TEXT_STYLE

`private static final String CL_TEXT_STYLE`

#### COLOR_AVG

`private static final Color COLOR_AVG`

#### COLOR_HIGH

`private static final Color COLOR_HIGH`

#### COLOR_HIGH_STALE

`private static final Color COLOR_HIGH_STALE`

#### COLOR_LOW

`private static final Color COLOR_LOW`

#### COLOR_LOW_STALE

`private static final Color COLOR_LOW_STALE`

#### COLOR_VOLUME

`private static final Color COLOR_VOLUME`

#### DELTA_LABEL_SIZE

`private static final Dimension DELTA_LABEL_SIZE`

#### DIVIDER_COLOR

`private static final Color DIVIDER_COLOR`

#### DRAG_LINE_COLOR

`private static final Color DRAG_LINE_COLOR`

#### DRAG_SCROLL_MARGIN

`private static final int DRAG_SCROLL_MARGIN`

#### DRAG_SCROLL_STEP

`private static final int DRAG_SCROLL_STEP`

#### ESTIMATE_ROW_BORDER_COMPACT

`private static final Border ESTIMATE_ROW_BORDER_COMPACT`

#### ESTIMATE_ROW_BORDER_DEFAULT

`private static final Border ESTIMATE_ROW_BORDER_DEFAULT`

#### FEATURE_AREAS

`private static final String[] FEATURE_AREAS`

Feature-template "Related area" dropdown options, matched exactly for URL prefill.

#### FEATURE_TEMPLATE

`private static final String FEATURE_TEMPLATE`

#### FOOTER_DIVIDER_COLOR

`private static final Color FOOTER_DIVIDER_COLOR`

Fainter divider above the footer's Report/Request row: dimmer than
`#DIVIDER_COLOR` but still visible over the (40,40,40) background.

#### GITHUB_NEW_ISSUE

`private static final String GITHUB_NEW_ISSUE`

GitHub new-issue endpoint and templates; the footer forms deep-link here with fields prefilled.

#### GROUP_HEADER_KEY

`private static final String GROUP_HEADER_KEY`

Client property marking a group accordion header, used to find group boundaries during a drag.

#### LOADING_COLOR

`private static final Color LOADING_COLOR`

#### LOADING_GLOW_MIN_ALPHA

`private static final float LOADING_GLOW_MIN_ALPHA`

#### LOADING_GLOW_PERIOD_MS

`private static final long LOADING_GLOW_PERIOD_MS`

#### MD_LINK

`private static final Pattern MD_LINK`

A markdown link `[label](url)` used for the changelog's issue references.

#### NEW_CATEGORY_LABEL

`private static final String NEW_CATEGORY_LABEL`

#### NUMBER_FORMAT

`private static final NumberFormat NUMBER_FORMAT`

#### PRICES_LEFT_PAD

`private static final int PRICES_LEFT_PAD`

#### PRICES_RIGHT_PAD

`private static final int PRICES_RIGHT_PAD`

#### PROFIT_SECTION_BORDER_COMPACT

`private static final Border PROFIT_SECTION_BORDER_COMPACT`

#### PROFIT_SECTION_BORDER_DEFAULT

`private static final Border PROFIT_SECTION_BORDER_DEFAULT`

#### PULSE_DURATION_MS

`private static final long PULSE_DURATION_MS`

#### REMOVE_COLOR

`private static final Color REMOVE_COLOR`

#### ROW_ITEM_ID

`private static final String ROW_ITEM_ID`

Client property on each row card holding its item id, used to map drag positions to list indices.

#### STAR_DIM

`private static final Color STAR_DIM`

#### STAR_HIDDEN

`private static final Color STAR_HIDDEN`

#### STAR_HOVERED

`private static final String STAR_HOVERED`

#### STAR_PREVIEW

`private static final Color STAR_PREVIEW`

#### STAR_ROW_HOVERED

`private static final String STAR_ROW_HOVERED`

#### TINT_AVG

`private static final Color TINT_AVG`

#### TINT_HIGH

`private static final Color TINT_HIGH`

#### TINT_LOW

`private static final Color TINT_LOW`

#### TINT_VOLUME

`private static final Color TINT_VOLUME`

#### TITLE_BORDER_NO_DIVIDER

`private static final Border TITLE_BORDER_NO_DIVIDER`

#### TITLE_BORDER_WITH_TOP_DIVIDER

`private static final Border TITLE_BORDER_WITH_TOP_DIVIDER`

#### UNCATEGORIZED_LABEL

`private static final String UNCATEGORIZED_LABEL`

#### bottomPanel

`private final JPanel bottomPanel`

#### cardLayout

`private final CardLayout cardLayout`

#### cardsHost

`private final JPanel cardsHost`

#### categories

`private List<CategoryState> categories`

Latest category state from the plugin, used to render the grouped/accordion list.

#### categoriesButton

`private JLabel categoriesButton`

Header button (manage mode only) that opens the Manage Categories dialog.

#### categoryActions

`private final CategoryActions categoryActions`

Category create/rename/delete/reorder and per-item assignment operations.

#### changelog

`private final Changelog changelog`

Bundled release notes shown in the changelog window.

#### changelogButton

`private JButton changelogButton`

The footer "What's New ✨" / "Change log" indicator button.

#### clearButton

`private final JButton clearButton`

#### coinsIcon

`private final JLabel coinsIcon`

#### coinsIconCache

`private final Map<Integer,ImageIcon> coinsIconCache`

#### compactToggle

`private JLabel compactToggle`

Header toggle that switches between the standard and compact row layouts. Its
`≣` glyph renders from a taller fallback font, so it uses a shrunken derived
font to match the other header icons.

#### compactTotalsCountLabel

`private JLabel compactTotalsCountLabel`

#### compactTotalsRows

`private JPanel compactTotalsRows`

Compact-view totals: a two-line "Total / profit (avg)" panel shown instead of the high/low/avg rows.

#### compactTotalsValueLabel

`private JLabel compactTotalsValueLabel`

#### config

`private final StockpileConfig config`

#### currentEstimatesPosition

`private EstimatesPosition currentEstimatesPosition`

#### currentItems

`private final Map<Integer,TrackedItem> currentItems`

#### detailView

`private final DetailView detailView`

The sidebar's detail view (extracted for #110); the host mounts it as the `#CARD_DETAIL` card.

#### dragGroupIds

`private List<Integer> dragGroupIds`

The dragged item's group (visual-order item ids), so a drag stays within its group.

#### dragInsertIndex

`private int dragInsertIndex`

The list index where the dragged item would be inserted on drop.

#### dragItemId

`private int dragItemId`

Drag-reorder state. `dragItemId` is the item being dragged, or -1 when not dragging.

#### dragLineY

`private int dragLineY`

The y-coordinate (in `#trackedItemsPanel` space) at which to paint the drop indicator line.

#### dragScrollDir

`private int dragScrollDir`

Autoscroll direction while dragging: -1 up, +1 down, 0 none.

#### dragScrollTimer

`private Timer dragScrollTimer`

Edge-autoscroll timer active while a drag hovers near the viewport top/bottom.

#### examineLookup

`private final IntFunction<String> examineLookup`

#### favoritesCollapsed

`private boolean favoritesCollapsed`

#### filterToggle

`private JLabel filterToggle`

Header toggle that shows/hides the tracked-list filter field.

#### fireRunePrice

`private long fireRunePrice`

#### footerPanel

`private final JPanel footerPanel`

#### geEstimatesSlotBottom

`private final JPanel geEstimatesSlotBottom`

#### geEstimatesSlotTop

`private final JPanel geEstimatesSlotTop`

#### groupingActive

`private boolean groupingActive`

Whether the list is currently grouped (favorites or categories active); disables drag
reorder, which is global-order only.

#### hoveredItemId

`private int hoveredItemId`

#### itemManager

`private final ItemManager itemManager`

#### lastCoinsIconValue

`private long lastCoinsIconValue`

#### lastPriceRefresh

`private volatile Instant lastPriceRefresh`

#### lastRefreshLabel

`private final JLabel lastRefreshLabel`

#### lastRenderIndicatorMode

`private PriceIndicatorMode lastRenderIndicatorMode`

#### lastRenderItems

`private List<TrackedItem> lastRenderItems`

Last-rendered items/mode, retained so toggling manage mode can re-render rows without a full
plugin refresh, and so a session reset (`#resetSession()`) re-primes from the same list.

#### loadingGlowTimer

`private final Timer loadingGlowTimer`

#### loadingLabels

`private final List<JLabel> loadingLabels`

#### loggedOutCard

`private JPanel loggedOutCard`

The logged-out placeholder card; tracked so `#cardsHost` can fill the viewport while it shows.

#### natureRunePrice

`private long natureRunePrice`

#### onAcquisitionsEdited

`private final Consumer<Integer> onAcquisitionsEdited`

#### onAddItem

`private final BiConsumer<Integer,TrackItemMode> onAddItem`

#### onClearAcquisitions

`private final Consumer<Integer> onClearAcquisitions`

#### onClearAll

`private final Runnable onClearAll`

#### onExportCsv

`private final Consumer<Consumer<String>> onExportCsv`

Builds the acquisitions CSV on the client thread and delivers it back on the EDT.

#### onExportList

`private final Consumer<Consumer<String>> onExportList`

Builds the shareable tracked-list token on the client thread and delivers it back on the EDT.

#### onImportList

`private final BiConsumer<String,Consumer<String>> onImportList`

Imports a tracked-list token (merge, non-destructive); delivers a user-facing result message on the EDT.

#### onNotificationsEdited

`private final Consumer<Integer> onNotificationsEdited`

#### onPortfolioHistory

`private final Supplier<List<long[]>> onPortfolioHistory`

Supplies the portfolio value history points (`{epochSeconds, value, costBasis`}) for the chart.

#### onRemoveItem

`private final Consumer<Integer> onRemoveItem`

#### onReorder

`private final BiConsumer<Integer,Integer> onReorder`

Reorder callback: (itemId, targetIndex) — moves the item to a new position in the tracked list.

#### onRequestDetailData

`private final Consumer<Integer> onRequestDetailData`

#### onSetFavorite

`private final BiConsumer<Integer,Boolean> onSetFavorite`

Favorite toggle callback: (itemId, favorite) — pins/unpins an item to the top Favorites group.

#### onSetGlobalOrder

`private final Consumer<List<Integer>> onSetGlobalOrder`

Drag-reorder callback: replaces the full tracked-item order with the given id sequence.

#### onSetGroupCollapsed

`private final BiConsumer<String,Boolean> onSetGroupCollapsed`

Group collapse callback: (groupKey, collapsed) — persists a group's accordion state.

#### onSetItemCompact

`private final BiConsumer<Integer,Boolean> onSetItemCompact`

Per-item compact toggle callback: (itemId, compact) — flips one row's compact override (#210).

#### onSetOnOverlay

`private final BiConsumer<Integer,Boolean> onSetOnOverlay`

Overlay toggle callback: (itemId, onOverlay) — adds/removes an item from the on-screen overlay.

#### onSetSortMode

`private final Consumer<SortMode> onSetSortMode`

#### onToggleCompactView

`private final Runnable onToggleCompactView`

Flips the persisted compact-view config flag; the resulting config change rebuilds the list.

#### onToggleSortDirection

`private final Runnable onToggleSortDirection`

Flips the persisted sort-direction flag; the resulting config change rebuilds the list.

#### onUntrackToPreview

`private final Consumer<Integer> onUntrackToPreview`

Untracks the shown item but keeps the detail view open as a preview (#138).

#### onWhatsNewSeen

`private final Runnable onWhatsNewSeen`

Callback to persist that the current release's "What's New" has been seen.

#### openPopouts

`private final List<PopoutHandle> openPopouts`

#### orderedItemIds

`private final List<Integer> orderedItemIds`

Item ids in current display order, kept in sync on each `#rebuild`, used to compute reorder targets.

#### portfolioChartButton

`private JButton portfolioChartButton`

Opens the portfolio value chart; hidden until at least two history points exist to plot.

#### portfolioChartStrut

`private Component portfolioChartStrut`

WEST strut balancing `#portfolioChartButton` so the title stays centred; toggled with it.

#### portfolioPopoutRefreshers

`private final List<Runnable> portfolioPopoutRefreshers`

Re-fetch actions for open portfolio-chart pop-outs; run on every rebuild so they update live.

#### profitLabel

`private final JLabel profitLabel`

#### profitSection

`private final JPanel profitSection`

#### pulseEntries

`private final List<PulseEntry> pulseEntries`

#### pulseTimer

`private final Timer pulseTimer`

#### refreshAgeTimer

`private final Timer refreshAgeTimer`

#### reorderMode

`private boolean reorderMode`

Whether the list is in reorder mode, which reveals the per-row drag/arrow strip.

#### reorderToggle

`private JLabel reorderToggle`

Header toggle that enters/exits reorder mode.

#### rowIconCache

`private final Map<Long,ImageIcon> rowIconCache`

18px row icons keyed by `#iconCacheKey` (item id + rendered stack size), so
quantity-aware sprites are cached per stack.

#### searchField

`private final IconTextField searchField`

#### searchResultsPanel

`private final JPanel searchResultsPanel`

#### sessionLabel

`private final JLabel sessionLabel`

Static grey "Session:" prefix; never recoloured, mirroring the profit row's prefix.

#### sessionRow

`private JPanel sessionRow`

The row wrapping `#sessionLabel`; toggled as a whole so no empty row lingers when hidden.

#### sessionStats

`private final SessionStats sessionStats`

In-memory session tracking; baseline captured on the first priced render after a reset.

#### sessionValueLabel

`private final JLabel sessionValueLabel`

Value gained/lost since the session baseline (login or manual reset); the only part recoloured.

#### sortToggle

`private JLabel sortToggle`

Header toggle that opens the sort-mode menu; highlighted when a non-manual sort is active.

#### totalAvgDeltaLabel

`private final JLabel totalAvgDeltaLabel`

#### totalAvgLabel

`private final JLabel totalAvgLabel`

#### totalAvgRow

`private final JPanel totalAvgRow`

#### totalHighDeltaLabel

`private final JLabel totalHighDeltaLabel`

#### totalHighLabel

`private final JLabel totalHighLabel`

#### totalHighRow

`private final JPanel totalHighRow`

#### totalLowDeltaLabel

`private final JLabel totalLowDeltaLabel`

#### totalLowLabel

`private final JLabel totalLowLabel`

#### totalLowRow

`private final JPanel totalLowRow`

#### totalsRows

`private JPanel totalsRows`

The standard totals rows (high/low/avg + profit), toggled off in compact view.

#### totalsTitle

`private final JLabel totalsTitle`

#### totalsTitleRow

`private JPanel totalsTitleRow`

Title row hosting `#totalsTitle` and the chart pop-out button; carries the toggle-able divider.

#### trackedFilter

`private String trackedFilter`

#### trackedFilterField

`private IconTextField trackedFilterField`

Name filter over the tracked list, shown only when the list overflows into scrolling.

#### trackedItemIds

`private final Set<Integer> trackedItemIds`

#### trackedItemsPanel

`private final JPanel trackedItemsPanel`

#### uncategorizedCollapsed

`private boolean uncategorizedCollapsed`

#### whatsNew

`private boolean whatsNew`

Whether the footer indicator is currently in the highlighted "What's New" state.

### Constructor Detail

#### StockpilePanel

`public StockpilePanel(ItemManager itemManager, StockpileConfig config, CategoryActions categoryActions, PanelActions actions, Changelog changelog, boolean whatsNew)`

Builds the panel and its two cards (main list and detail view). The header toggles
sit on their own right-justified row above the Tracked Items label.

- **Parameter** `itemManager` — for item names, icons, and prices
- **Parameter** `config` — the plugin configuration
- **Parameter** `categoryActions` — the category-management operations, implemented by the plugin
- **Parameter** `actions` — the plugin-facing callbacks the panel invokes (see `PanelActions`)
- **Parameter** `changelog` — the bundled changelog shown in the What's New view
- **Parameter** `whatsNew` — whether this launch should surface the What's New badge

### Method Detail

#### acquisitionsEdited

`public void acquisitionsEdited(int itemId)`

{@inheritDoc} Delegates to the panel's acquisitions-edited callback when present.

#### addFormRow

`private void addFormRow(JPanel form, String label, JComponent field)`

Adds a labelled row (label above the field) to a vertical form panel.

#### addItem

`public void addItem(int itemId, TrackItemMode mode)`

{@inheritDoc} Delegates to the panel's add-item callback.

#### addItemRow

`private void addItemRow(TrackedItem item, PriceIndicatorMode indicatorMode, List<TrackedItem> groupItems)`

Adds a single tracked-item row plus its trailing spacer; `groupItems` scopes reorder within the group.

#### addListenerRecursively

`private void addListenerRecursively(Component c, MouseListener listener)`

Attaches a mouse listener to a component and all its descendants, so a whole row reacts as one.

#### appendChangelogAnchor

`private static void appendChangelogAnchor(StringBuilder sb, int sectionIndex)`

Appends a named scroll anchor (`sec<n>`) matching the ids `#extractSections` hands the nav.

#### appendChangelogDiv

`private static void appendChangelogDiv(StringBuilder sb, String style, int indentLevel, String html)`

Appends a `<div>` with the given inline CSS `style` and left indent, wrapping `html`.

#### applyChangelogButtonStyle

`private void applyChangelogButtonStyle()`

Applies the indicator styling — gold while "What's New", muted once seen.

#### applyEstimatesPosition

`private void applyEstimatesPosition(EstimatesPosition position)`

Moves the GE estimates block above or below the other sections per the configured position.

#### applyEstimatesSpacing

`private void applyEstimatesSpacing(EstimatesSpacing spacing)`

Applies normal or compact row padding to the GE estimates block per the configured spacing.

#### applyLiveStaleness

`private void applyLiveStaleness(JLabel cell, long value, String sideLabel, String timeLabel, long tradeTime, Color freshColor, Color staleColor)`

Reflects the staleness of a Ltst high/low value on its cell: appends the last
trade time as a second tooltip line and dims the value's color when that trade
is older than the configured threshold.

- **Parameter** `sideLabel` — the value side, e.g. `"High"` or `"Low"`
- **Parameter** `timeLabel` — the trade-time caption, e.g. `"Last Buy"`
- **Parameter** `tradeTime` — the trade's epoch-second timestamp (0 when unknown)
- **Parameter** `freshColor` — the normal value color
- **Parameter** `staleColor` — the dimmed color used once the value is stale

#### applyRowIcon

`private void applyRowIcon(JLabel iconLabel, TrackedItem item)`

Sets a label's 18px quantity-aware item icon from `#rowIconCache`, loading asynchronously on a miss.

#### applyTotalTooltip

`private void applyTotalTooltip(JLabel label, long value, ValueFormat fmt)`

Gives a totals label a full-number tooltip when its text is abbreviated, none otherwise.

#### autoCategorizeFromDialog

`private void autoCategorizeFromDialog(JDialog dialog)`

Prompts for the auto-categorize scope (uncategorized only vs. everything), runs it via
`#categoryActions`, reports the result, and closes the dialog so it reopens with the
freshly generated categories.

#### buildCategoryPicker

`private JComboBox<String> buildCategoryPicker(TrackedItem item)`

Builds the per-row category picker used in the manage row: assigns the item to an existing
category, clears it to Uncategorized, or prompts to create-and-assign a new one.

#### buildChangelogBadge

`private JButton buildChangelogBadge()`

Builds the top-right header badge that opens the changelog window;
`#applyChangelogButtonStyle` sets its label and colour.

#### buildChangelogContent

`private JComponent buildChangelogContent()`

Builds the changelog window: a left navigation column listing each release, with the selected
release's sections expanded beneath it as quick-links that jump to that section, and the
selected release's notes rendered on the right.

#### buildChangelogNavLink

`private JLabel buildChangelogNavLink(ChangelogSection section)`

Builds one indented, clickable section quick-link for the changelog nav.

#### buildChangelogNavVersion

`private JLabel buildChangelogNavVersion(String version, boolean selected)`

Builds one clickable release row for the changelog nav; the selected release is gold, the rest muted.

#### buildChartIcon

`private Icon buildChartIcon()`

Paints the small line-chart icon used to open the portfolio value chart.

#### buildCompactValueRow

`private JPanel buildCompactValueRow(TrackedItem item)`

Builds the compact-view row-2 value line: `total value (single item value)`, both
in short format and both derived from the latest avg-of-1 price (e.g. `4.86m (1.62m)`).
Falls back to a muted placeholder when the item has no prices.

#### buildDividerStrip

`private JPanel buildDividerStrip()`

Builds the horizontal divider strip drawn between the totals block and the footer.

#### buildEyeIcon

`private Icon buildEyeIcon(int size)`

Loads the bundled `eye.png` scaled to a square icon for the view-only button.

#### buildFavoriteStar

`private JLabel buildFavoriteStar(TrackedItem item)`

Builds the favorite-toggle star shown beneath each row's remove button. Like the
remove button it is hidden until the row is hovered; hovering the star itself previews
the toggle (fills light gold to add a favorite, or drops the gold to remove one).

#### buildFooterLink

`private JButton buildFooterLink(String text, Runnable onClick, String tooltip)`

Builds a small footer button that runs the given action when clicked.

#### buildFooterMenu

`private JButton buildFooterMenu(String text, JPopupMenu menu, String tooltip)`

A footer button that drops `menu` below itself, grouping related actions so the footer stays one row.

#### buildFooterMenuItem

`private JMenuItem buildFooterMenuItem(String text, Runnable onClick, String tooltip)`

One action inside a footer dropdown, styled to match the footer links.

#### buildGroupHeader

`private JPanel buildGroupHeader(String title, String groupKey, boolean collapsed, long groupTotal)`

Builds a clickable accordion header (chevron + title + group total value) that
toggles the group's collapsed state.

#### buildIconButton

`static JButton buildIconButton(Icon icon, String tooltip, Runnable onClick)`

Builds a borderless icon button with the given icon, tooltip, click action, and a hover highlight.

#### buildIssueUrl

`private static String buildIssueUrl(String template, String titlePrefix, String title, List<IssueField> fields, Map<IssueField,JComponent> inputs)`

Builds the GitHub new-issue URL with the title and non-empty fields pre-filled as query params.

#### buildManageEastControls

`private JPanel buildManageEastControls(TrackedItem item)`

Builds the right column of the manage row: an always-visible remove button stacked over a favorite star.

#### buildManageRow

`private JPanel buildManageRow(TrackedItem item, List<TrackedItem> groupItems)`

Builds the dedicated manage-mode row: a stripped-down layout showing only what's needed to
organise items. A left column of reorder controls (up/down, plus drag when ungrouped), a
middle column with the icon+name over a category picker, and a right column with the
always-visible remove and favorite controls. All price/quantity/profit content is omitted.

#### buildOverlayToggle

`private JLabel buildOverlayToggle(TrackedItem item)`

Builds the overlay-select control beneath the favorite star: a painted monitor icon that
toggles whether the item appears in the on-screen overlay. Gold when selected, and disabled
(greyed) once `StockpilePlugin#OVERLAY_MAX` items are selected and this isn't one.

#### buildReorderStrip

`private JPanel buildReorderStrip(TrackedItem item, List<TrackedItem> groupItems)`

Builds the left reorder column (up/down, plus a drag handle when the list isn't grouped) for the manage row.

#### buildRowCompactToggle

`private JLabel buildRowCompactToggle(TrackedItem item)`

Builds the per-item compact toggle beneath the overlay button (#210): a painted "≣" glyph
that flips this row between the standard and compact two-row layouts, independent of the
global compact toggle. Gold when this row's compact override is on, grey otherwise.

#### buildRowIcon

`private JLabel buildRowIcon(TrackedItem item)`

Builds an 18px item-icon label backed by `#rowIconCache`, loading asynchronously on a miss.

#### buildSearchResultRow

`private JPanel buildSearchResultRow(int itemId, String itemName)`

Builds one clickable row in the search-results dropdown that adds the item when clicked.

#### buildTotalsRow

`private JPanel buildTotalsRow(JLabel valueLabel, JLabel pulseLabel)`

Builds one estimate row pairing a totals value label with its pulse-indicator label.

#### buildTrackedItemRow

`private JPanel buildTrackedItemRow(TrackedItem item, PriceIndicatorMode indicatorMode, List<TrackedItem> groupItems)`

Builds one row of the main list for a tracked item: icon, name, quantity,
the configured data rows (prices/value/volume/profit), hover affordances,
and a click handler that opens the item's detail view.

#### categoriesIcon

`private static Icon categoriesIcon(Color color)`

Draws a bulleted-list glyph — three dots, each followed by a line — tinted `color`.

#### changelogButtonText

`private String changelogButtonText()`

- **Returns:** the indicator label: highlighted "What's New" for a new release, else "Change log".

#### clearAcquisitions

`public void clearAcquisitions(int itemId)`

{@inheritDoc} Delegates to the panel's clear-acquisitions callback when present.

#### clearSessionBaseline

`public void clearSessionBaseline()`

Drops the session baseline without re-priming (used on profile change): the next
rebuild captures the new profile's holdings as the baseline.

#### closePopouts

`private void closePopouts()`

Disposes all open pop-out windows owned by the panel (portfolio, What's New).

#### commitDrag

`private void commitDrag()`

Commits the in-progress drag: places the dragged item at its new slot within its own
group and rewrites the full tracked order accordingly (kept within-group, since groups
render in global order). A no-op drop is ignored.

#### computeDragGroup

`private List<Integer> computeDragGroup(int itemId)`

Determines the dragged item's group as the contiguous run of item rows between accordion
headers in the rendered list (the whole list when ungrouped), returning its item ids in
visual order.

#### config

`public StockpileConfig config()`

{@inheritDoc} Supplies the panel's live plugin config to the detail view.

#### confirmAndClearAll

`private void confirmAndClearAll()`

Prompts for confirmation, then clears all tracked items via the plugin callback.

#### containsIgnoreCase

`private static boolean containsIgnoreCase(DefaultListModel<String> model, String value)`

- **Returns:** whether the list model already contains `value`, ignoring case.

#### copyToClipboard

`private void copyToClipboard(String text)`

#### createDeltaLabel

`private JLabel createDeltaLabel()`

Creates a fixed-size label that hosts the ▲/▼ price-change pulse next to a value.

#### dragAutoscrollTick

`private void dragAutoscrollTick()`

One autoscroll step: nudges the viewport in `#dragScrollDir` and recomputes the drop target.

#### encode

`private static String encode(String value)`

URL-encodes a value for a query parameter (spaces as %20, not +).

#### equalizeTotalsLabelWidths

`private void equalizeTotalsLabelWidths()`

Fixes the three totals value labels to the widest one's width so the columns stay aligned.

#### escapeHtml

`private static String escapeHtml(String text)`

Escapes the HTML-significant characters so text renders literally inside an HTML label.

#### examine

`public String examine(int itemId)`

{@inheritDoc} Resolves the examine text through the panel's examine lookup.

#### exportAcquisitionsCsv

`private void exportAcquisitionsCsv()`

Copies the acquisitions log as CSV to the clipboard once the plugin has built it.

#### exportTrackedList

`private void exportTrackedList()`

Copies the shareable tracked-list code to the clipboard once the plugin has built it.

#### extractSections

`private static List<ChangelogSection> extractSections(String body)`

- **Returns:** the `##`/`###` section headings of a release body, in order, with scroll anchors.

#### fieldValue

`private static String fieldValue(JComponent input)`

- **Returns:** the current text of an issue-form input (text area or dropdown selection).

#### filterIcon

`private static Icon filterIcon(Color color)`

Paints a small monochrome funnel (filter) icon in the given colour: a wide top bar
tapering to a narrow central stem.

#### fireRunePrice

`public long fireRunePrice()`

{@inheritDoc} Returns the fire-rune price the panel currently holds.

#### formatAge

`static String formatAge(long epochSeconds)`

Formats an epoch-second timestamp's age as a compact relative string,
e.g. `"5s"`, `"5m"`, `"3hr"`, `"2d ago"`.

#### formatTotalGp

`static String formatTotalGp(long value, ValueFormat fmt)`

Formats a totals value as either full or abbreviated gp per the configured `ValueFormat`.

#### getDetailItemId

`public int getDetailItemId()`

- **Returns:** the item id whose detail view is open, or a non-positive value when on the main list.

#### iconCacheKey

`private static long iconCacheKey(TrackedItem item)`

- **Returns:** a `#rowIconCache` key combining an item's id with the stack size its icon is rendered at.

#### importTrackedList

`private void importTrackedList()`

Prompts for a tracked-list code, merges it into the current profile, and reports the outcome.

#### indexOfItem

`private static int indexOfItem(List<TrackedItem> list, int itemId)`

- **Returns:** the position of `itemId` within `list`, or -1 if absent.

#### inlineLinks

`private static String inlineLinks(String text)`

Escapes `text`, then turns markdown `[label](url)` links into clickable HTML anchors.

#### installCategoryDragReorder

`private void installCategoryDragReorder(JList<String> list, DefaultListModel<String> model)`

Enables drag-and-drop reordering on the Manage Categories list (#212): dragging a category and
dropping it between two others sets the order in one gesture, committing through the same
`CategoryActions#reorder(String, int)` path as the ↑/↓ buttons. `DropMode#INSERT`
draws the insertion line, so the drop target is shown while dragging.

#### installChangelogNavHover

`private void installChangelogNavHover(JLabel label, Color restFg, Color restBg)`

Adds a hover highlight (brighten to white on a lighter row) that restores the given resting colours.

#### installDragHandle

`private void installDragHandle(JLabel handle, int itemId)`

Wires drag-to-reorder onto a row's drag handle: pressing starts the drag, dragging
updates the drop indicator and edge autoscroll, and releasing commits the move.

#### installItemValue

`private void installItemValue(JLabel label, long value, String prefix, Color tint)`

Installs a compact gp value on a label with no tooltip caption.

#### installItemValue

`private void installItemValue(JLabel label, long value, String prefix, String tooltipLabel, Color tint)`

Installs a prefixed compact gp value on a label via `#installShortValue`.

#### installRowHover

`private void installRowHover(JPanel card, TrackedItem item, JButton removeBtn, JLabel favStar, JLabel overlayBtn, JLabel compactBtn, Color removeColor, Color removeHidden)`

Wires the shared row hover behaviour onto a tracked-item card: clicking the row
(other than the remove button, favorite star, overlay button, or compact button) opens
the detail view, and entering/leaving the card tracks `#hoveredItemId` and reveals/hides
the remove button, favorite star, and the (optional) overlay-select and per-item compact buttons.

#### installShortValue

`static void installShortValue(JLabel label, long value, String shortText, String tooltipLabel, Color tint)`

Installs a pre-formatted compact value on a label with a full-number tooltip and a hover tint.

#### installToggleHover

`private static void installToggleHover(JLabel button, BooleanSupplier selected, Consumer<Color> apply, Runnable restore)`

Installs grey↔gold hover colouring on a header toggle: an unselected (grey) button
turns gold while hovered, a selected (gold) button turns grey, and its resting
state colour is repainted on exit.

- **Parameter** `selected` — whether the button is currently in its selected/gold state
- **Parameter** `apply` — paints the button in a colour (`setForeground` for glyph
                    buttons, `setIcon` for icon buttons)
- **Parameter** `restore` — repaints the button's resting state colour

#### isEditingNotifications

`public boolean isEditingNotifications()`

- **Returns:** whether the user is mid-edit in the notifications table, so the plugin should defer firing rules.

#### isStale

`private boolean isStale(long epochSeconds)`

- **Returns:** whether `epochSeconds` is older than the configured stale-price threshold.

#### itemManager

`public ItemManager itemManager()`

{@inheritDoc} Supplies the panel's shared item manager to the detail view.

#### liveSessionSnapshot

`static Map<Integer,long[]> liveSessionSnapshot(List<TrackedItem> items)`

Builds the session baseline snapshot (item id → [quantity, avg price]) from only the
items whose prices came from a live fetch. Cache-hydrated prices are excluded so
overnight market movement, restored from the persisted cache on login, never seeds
the baseline and reads as session profit. An empty result means the session row stays
hidden until real live prices arrive.

#### makeRowControl

`private JButton makeRowControl(String glyph, String tooltip)`

Builds a compact, hover-revealed glyph button styled like the row's remove button.

#### matchesFilter

`private boolean matchesFilter(TrackedItem item)`

- **Returns:** whether the item matches the active tracked-list name filter (always true when the filter is empty).

#### moveCategoryInDialog

`private void moveCategoryInDialog(JList<String> list, DefaultListModel<String> model, int delta)`

Moves the selected dialog category by `delta` and forwards the new index to the plugin.

#### natureRunePrice

`public long natureRunePrice()`

{@inheritDoc} Returns the nature-rune price the panel currently holds.

#### notificationsEdited

`public void notificationsEdited(int itemId)`

{@inheritDoc} Delegates to the panel's notifications-edited callback when present.

#### onBack

`public void onBack()`

{@inheritDoc} Returns the sidebar panel to the main tracked-item list.

#### onSearch

`private void onSearch(String query)`

Filters the add-item search dropdown to items matching the typed query.

#### onTrackedFilterChanged

`private void onTrackedFilterChanged()`

Re-renders the rows against the updated tracked-list filter text.

#### openChangelogWindow

`private void openChangelogWindow()`

Opens the changelog window; the first open of a new release quiets the "What's New" indicator.

#### openIssueForm

`private void openIssueForm(String dialogTitle, String template, String titlePrefix, List<IssueField> fields)`

Shows a modal form for an issue template, then opens the GitHub issue form in the browser
with the entered title/fields pre-filled (via query params) so the user only has to review
and click Submit on GitHub. No data leaves the machine until they submit on GitHub.

#### openManageCategoriesDialog

`private void openManageCategoriesDialog()`

Opens the modal Manage Categories dialog: create, rename, delete, and reorder categories.
Each action updates the dialog's list immediately and forwards to the plugin via
`#categoryActions`, which persists and rebuilds the panel.

#### openPortfolioChart

`private void openPortfolioChart()`

Opens a pop-out with the portfolio value history chart, fed live from the plugin's stored points.

#### openReportIssueForm

`private void openReportIssueForm()`

Opens the in-plugin "Report a bug" form.

#### openRequestFeatureForm

`private void openRequestFeatureForm()`

Opens the in-plugin "Request a feature" form.

#### openTrackedDetail

`public void openTrackedDetail(int itemId)`

Opens the tracked detail card for `itemId`. A no-op when that item's tracked
detail is already showing, so re-opening it (e.g. from the GE integration) leaves the
card's scroll position and state untouched instead of rebuilding it back to the top.

#### overlayCount

`private int overlayCount()`

- **Returns:** how many currently tracked items are flagged for the on-screen overlay.

#### overlayIcon

`private static Icon overlayIcon(Color color)`

Paints a small monochrome monitor (on-screen overlay) icon in the given colour.

#### promptCategoryForItem

`public void promptCategoryForItem(int itemId)`

Category prompt shown at track time (#211): a modal dropdown of the existing categories plus
Uncategorized and a create-new option. Choosing a category (or a freshly created one) assigns it
to the just-tracked item; Uncategorized or cancel leaves it uncategorized. A no-op if the item
is gone by the time this runs.

#### pulseIfShown

`private void pulseIfShown(JLabel label, int delta, PriceIndicatorMode mode)`

Starts a price pulse on the label unless the configured indicator mode suppresses it.

#### rebuild

`public void rebuild(List<TrackedItem> rawItems, Instant newLastPriceRefresh, PriceIndicatorMode indicatorMode, boolean loggedIn, List<CategoryState> categories, boolean favoritesCollapsed, boolean uncategorizedCollapsed)`

Rebuilds the main item list from the latest tracked items and totals.

<p>This is the primary entry point the plugin calls after any data change:
it repopulates the rows, updates the value/profit totals and the refresh
timestamp, and (when `indicatorMode` permits) starts pulse animations
for items whose price moved.

#### rebuildChangelogNav

`private void rebuildChangelogNav(JPanel nav, List<Changelog.Release> releases, int selectedIndex, JEditorPane body)`

(Re)populates the changelog nav: one clickable row per release (selecting it loads its notes),
and beneath the selected release its section quick-links, each of which scrolls the notes to
that section.

#### refreshDetailData

`public void refreshDetailData(int itemId)`

Re-populates the open detail view with fresh data for `itemId` (no-op if a different item is shown).

#### refreshFavoriteStar

`private void refreshFavoriteStar(JLabel star, boolean favorite)`

Applies a favorite star's visual from its row-hover/star-hover client flags: hidden
when its row isn't hovered, the resting gold/grey glyph when the row is hovered, and a
preview (light-gold fill to add, or grey outline to remove) when the star itself is hovered.

#### removeHoverTint

`static void removeHoverTint(JLabel label)`

Detaches any hover-tint listener from a label before its value is replaced.

#### removeSessionBaseline

`public void removeSessionBaseline(int itemId)`

Drops one item's session-baseline entry when it is untracked, so removal is session-neutral.

#### renderChangelogBody

`private String renderChangelogBody(String body)`

Renders a release's markdown body to HTML for the changelog window: `##`/`###`/`####`
headings become sized/weighted/coloured headers that each indent one level deeper, their content indents
one level further still, and `[#12](url)` issue links become clickable anchors. Deliberately minimal
— it only covers the constructs the bundled changelog uses, since Swing's HTML renderer is HTML-3.2-era.

#### renderGroup

`private void renderGroup(String title, String groupKey, boolean collapsed, List<TrackedItem> groupItems, PriceIndicatorMode indicatorMode)`

Renders one collapsible group: a clickable header plus its rows, unless empty
(skipped) or collapsed (header only).

#### renderGroupedRows

`private void renderGroupedRows(List<TrackedItem> items, PriceIndicatorMode indicatorMode)`

Renders the tracked rows into `#trackedItemsPanel`, grouped into the Favorites
pseudo-group (pinned on top), then each user category in order, then Uncategorized.
Falls back to a flat, header-less list when no favorites and no categories exist, so
users who don't use grouping see the list exactly as before. Empty groups are skipped.

#### renderReleaseHtml

`private String renderReleaseHtml(Changelog.Release release)`

- **Returns:** an HTML rendering of one release: its version and date heading, then its markdown body.

#### renderTrackedRows

`private void renderTrackedRows(List<TrackedItem> items, PriceIndicatorMode indicatorMode)`

Clears and re-renders the tracked-item rows (empty placeholder, or the grouped rows),
retaining the inputs so `#toggleReorderMode()` can re-render the manage layout
without a full plugin refresh.

#### requestDetailData

`public void requestDetailData(int itemId)`

{@inheritDoc} Delegates to the panel's detail-data request callback when present.

#### resetSession

`public void resetSession()`

Re-baselines the session to the current holdings, so "Session:" restarts from zero.

#### setAlchRunePrices

`public void setAlchRunePrices(long naturePrice, long firePrice)`

Supplies the latest nature/fire rune prices used to compute high-alch profit in the detail view.

#### setTrackedFilterVisible

`private void setTrackedFilterVisible(boolean visible)`

Sets the filter field's visibility, clearing any active filter when it is hidden.

#### showDetail

`private void showDetail(int itemId)`

Reveals the tracked detail card for `itemId` in the card stack and binds the detail view to
it. A no-op when the item is not tracked.

#### showMain

`private void showMain()`

Returns to the main item list, closing any open pop-outs.

#### showPopout

`private void showPopout(String title, JComponent content, Consumer<TrackedItem> refresher, Runnable onClose)`

Opens a non-modal pop-out window hosting `content`, registering its
refresher so live updates reach it and running `onClose` when dismissed.

#### showPreview

`public void showPreview(TrackedItem item)`

Opens a read-only preview of an untracked item in the detail view. Unlike
`#showDetail`, the item is not in the tracked list; the plugin supplies
its price/history data directly and the tracked-only sections stay hidden.

#### showSortMenu

`private void showSortMenu()`

Opens the sort-mode menu on the header toggle, with the active mode checked and its current
direction arrow shown. Clicking the active (non-manual) mode flips the sort direction; clicking
any other mode selects it.

#### shutdown

`public void shutdown()`

Stops the animation timers so the panel can be disposed cleanly.

#### signedGp

`static String signedGp(long v)`

- **Returns:** the value as a comma-grouped gp string with an explicit `+` when positive.

#### sparkleIcon

`private static Icon sparkleIcon(Color color)`

Paints a small firework burst (eight rays capped with sparks) for the "What's New" badge.

#### startPulse

`private void startPulse(JLabel label, int delta)`

Begins a color pulse on a label (green up / red down) reflecting the sign of a price change.

#### stopDragAutoscroll

`private void stopDragAutoscroll()`

Stops the edge-autoscroll timer, if running.

#### styledFooterButton

`private JButton styledFooterButton(String text, String tooltip)`

Shared styling for the footer's link and dropdown buttons.

#### toggleReorderMode

`private void toggleReorderMode()`

Toggles reorder mode, showing or hiding the per-row drag/arrow strips without a full rebuild.

#### toggleTrackedFilter

`private void toggleTrackedFilter()`

Toggles the tracked-list filter field via the header filter button, focusing it when shown.

#### trackedItem

`public TrackedItem trackedItem(int itemId)`

{@inheritDoc} Reads from the panel's current tracked-item map.

#### untrackToPreview

`public void untrackToPreview(int itemId)`

{@inheritDoc} Delegates to the panel's untrack-to-preview callback.

#### updateCoinsIcon

`private void updateCoinsIcon(long value)`

Updates the totals coin icon to the stack sprite for the given gp value, loading it
asynchronously and caching per quantity. A stale async load is discarded if the value
has moved on by the time the image arrives.

#### updateCompactToggle

`private void updateCompactToggle()`

Highlights the header compact toggle when compact view is active.

#### updateCompactTotals

`private void updateCompactTotals(int itemCount, long totalAvg, long profit, boolean hasPrices, boolean showProfit, ValueFormat fmt)`

Populates the compact totals: item count plus a `total avg value (profit)` line,
where the total avg uses the configured value format and the profit is always short format.
The profit parenthetical is coloured per-part — grey parentheses with a green/red profit —
while the parenthetical is dropped entirely when there is no cost-basis profit to show.

#### updateDrag

`private void updateDrag(MouseEvent e)`

Recomputes the drop target and autoscroll state for the current drag pointer, then repaints.

#### updateDragAutoscroll

`private void updateDragAutoscroll(MouseEvent e)`

Starts/stops edge autoscroll based on whether the drag pointer is near the viewport's top or bottom.

#### updateDropTarget

`private void updateDropTarget(int yInPanel)`

Finds the list index where a drop at `yInPanel` would insert, and the indicator line position.

#### updateFilterToggle

`private void updateFilterToggle()`

Updates the header filter button's funnel icon, tinting it gold while the filter field is shown.

#### updateLoadingGlow

`private void updateLoadingGlow()`

Timer tick that breathes the shared glow colour across every label still awaiting prices.

#### updatePortfolioChartButton

`private void updatePortfolioChartButton()`

Shows the chart pop-out button (and its balancing strut) only once at least two history points exist to plot.

#### updatePulses

`private void updatePulses()`

Timer tick that advances every active pulse's color toward its base, retiring finished ones.

#### updateRefreshLabel

`private void updateRefreshLabel()`

Updates the footer's "updated N ago" text from the last price-refresh timestamp.

#### updateReorderToggle

`private void updateReorderToggle()`

Highlights the header reorder toggle and reveals the manage-categories button when manage mode is active.

#### updateSessionLine

`private void updateSessionLine(List<TrackedItem> items, boolean hasPrices)`

Renders the "Session:" line: the value gained/lost since the baseline, coloured
green/red, with a tooltip splitting the change into price movement vs. quantity
change. Captures the baseline on the first priced render after a reset; hidden
until prices are available.

#### updateSortToggle

`private void updateSortToggle()`

Reflects the active sort on the header toggle: the effective direction arrow
(highlighted) or the neutral glyph.

---

## com.oveduumnakal.StockpilePanel.ChangelogSection

_class_

`private static class ChangelogSection`

One navigable changelog section: heading depth (0 for `##`, 1 for `###`), text, and anchor.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `String` | `anchor` |  |
| `int` | `level` |  |
| `String` | `text` |  |

### Field Detail

#### anchor

`String anchor`

#### level

`int level`

#### text

`String text`

---

## com.oveduumnakal.StockpilePersistence

_class_

`class StockpilePersistence`

The client-free persistence layer (#111): the Gson-serializable snapshots and every read/write of
Stockpile state to the RS-profile config. Extracted verbatim from `StockpilePlugin` so the
plugin keeps only the orchestration (building snapshots from live `TrackedItem`s and applying
loaded ones on the client thread) while the JSON shape, config keys, and corrupt-value handling
live in one testable place. Loaders default to empty/`null` on a missing or unparseable value
exactly as the originals did, so history/state simply rebuilds rather than throwing.

### Nested Type Summary

| Type | Description |
|---|---|
| _class_ [`CachedPrice`](#comoveduumnakalstockpilepersistencecachedprice) | Last-known prices for one tracked item, stored as JSON in the RS profile config so the panel can show (staleness-dimmed) values immediately at startup instead of placeholders until the first wiki fetch lands. |
| _class_ [`CategoryData`](#comoveduumnakalstockpilepersistencecategorydata) | Serializable snapshot of the category definitions and special-group collapsed state. |
| _class_ [`PersistedItem`](#comoveduumnakalstockpilepersistencepersisteditem) | Serializable snapshot of a tracked item, stored as JSON in the RS profile config. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final Type` | `CATEGORIES_TYPE` |  |
| `private static final Type` | `GE_LEDGER_TYPE` |  |
| `private static final Type` | `GE_LIMITS_TYPE` |  |
| `private static final Type` | `PERSIST_TYPE` |  |
| `private static final Type` | `PORTFOLIO_HISTORY_TYPE` |  |
| `private static final Type` | `PRICE_CACHE_TYPE` |  |
| `private final ConfigManager` | `configManager` |  |
| `private final Gson` | `gson` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `StockpilePersistence(ConfigManager configManager, Gson gson)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `CategoryData` | `loadCategories()` |  |
| `Map<Integer,long[]>` | `loadGeBuyLimits()` |  |
| `Map<Integer,List<long[]>>` | `loadGeLedger()` |  |
| `List<PersistedItem>` | `loadItems()` |  |
| `Map<Integer,List<long[]>>` | `loadPortfolioHistory()` |  |
| `Map<Integer,CachedPrice>` | `loadPriceCache()` |  |
| `void` | `saveCategories(CategoryData data)` | Serializes the category definitions and group collapsed state to per-profile config. |
| `void` | `saveGeState(Map<Integer,List<long[]>> ledger, Map<Integer,long[]> limits)` | Persists the GE buy ledger and buy-limit windows to the RS profile config. |
| `void` | `saveItems(List<PersistedItem> items)` | Serializes the tracked-item snapshots to per-profile config. |
| `void` | `savePortfolioHistory(Map<Integer,List<long[]>> seriesByItem)` | Serializes the per-item portfolio history to per-profile config. |
| `void` | `savePriceCache(Map<Integer,CachedPrice> cache)` | Serializes the per-item price cache to per-profile config. |

### Field Detail

#### CATEGORIES_TYPE

`private static final Type CATEGORIES_TYPE`

#### GE_LEDGER_TYPE

`private static final Type GE_LEDGER_TYPE`

#### GE_LIMITS_TYPE

`private static final Type GE_LIMITS_TYPE`

#### PERSIST_TYPE

`private static final Type PERSIST_TYPE`

#### PORTFOLIO_HISTORY_TYPE

`private static final Type PORTFOLIO_HISTORY_TYPE`

#### PRICE_CACHE_TYPE

`private static final Type PRICE_CACHE_TYPE`

#### configManager

`private final ConfigManager configManager`

#### gson

`private final Gson gson`

### Constructor Detail

#### StockpilePersistence

`StockpilePersistence(ConfigManager configManager, Gson gson)`

### Method Detail

#### loadCategories

`CategoryData loadCategories()`

- **Returns:** the persisted category data, or `null` when missing or corrupt.

#### loadGeBuyLimits

`Map<Integer,long[]> loadGeBuyLimits()`

- **Returns:** the persisted GE buy-limit windows, or an empty map when missing or corrupt.

#### loadGeLedger

`Map<Integer,List<long[]>> loadGeLedger()`

- **Returns:** the persisted GE buy ledger (item id → FIFO buy chunks), or an empty map when missing or corrupt.

#### loadItems

`List<PersistedItem> loadItems()`

- **Returns:** the persisted tracked-item snapshots, or an empty list when missing, non-array, or corrupt.

#### loadPortfolioHistory

`Map<Integer,List<long[]>> loadPortfolioHistory()`

- **Returns:** the persisted per-item portfolio history, or `null` when missing, corrupt, or in
        the pre-#152 aggregate array format (which can't be split per item and is discarded).

#### loadPriceCache

`Map<Integer,CachedPrice> loadPriceCache()`

- **Returns:** the persisted price cache, or an empty map when missing or corrupt.

#### saveCategories

`void saveCategories(CategoryData data)`

Serializes the category definitions and group collapsed state to per-profile config.

#### saveGeState

`void saveGeState(Map<Integer,List<long[]>> ledger, Map<Integer,long[]> limits)`

Persists the GE buy ledger and buy-limit windows to the RS profile config.

#### saveItems

`void saveItems(List<PersistedItem> items)`

Serializes the tracked-item snapshots to per-profile config.

#### savePortfolioHistory

`void savePortfolioHistory(Map<Integer,List<long[]>> seriesByItem)`

Serializes the per-item portfolio history to per-profile config.

#### savePriceCache

`void savePriceCache(Map<Integer,CachedPrice> cache)`

Serializes the per-item price cache to per-profile config.

---

## com.oveduumnakal.StockpilePersistence.CachedPrice

_class_

`static class CachedPrice`

Last-known prices for one tracked item, stored as JSON in the RS profile config
so the panel can show (staleness-dimmed) values immediately at startup instead
of placeholders until the first wiki fetch lands. Package-private so
`PersistedSchemaSnapshotTest` can guard its shape; any field change fails
the schema snapshot until it is regenerated and explained in the PR.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `long` | `avg` |  |
| `long` | `high` |  |
| `long` | `highTime` |  |
| `long` | `low` |  |
| `long` | `lowTime` |  |

### Field Detail

#### avg

`long avg`

#### high

`long high`

#### highTime

`long highTime`

#### low

`long low`

#### lowTime

`long lowTime`

---

## com.oveduumnakal.StockpilePersistence.CategoryData

_class_

`static class CategoryData`

Serializable snapshot of the category definitions and special-group collapsed state.
Package-private so `PersistenceCompatTest` can freeze its legacy shape and
`PersistedSchemaSnapshotTest` can guard it; any field change fails the
schema snapshot until it is regenerated and explained in the PR.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `List<CategoryState>` | `categories` |  |
| `boolean` | `favoritesCollapsed` |  |
| `boolean` | `uncategorizedCollapsed` |  |

### Field Detail

#### categories

`List<CategoryState> categories`

#### favoritesCollapsed

`boolean favoritesCollapsed`

#### uncategorizedCollapsed

`boolean uncategorizedCollapsed`

---

## com.oveduumnakal.StockpilePersistence.PersistedItem

_class_

`static class PersistedItem`

Serializable snapshot of a tracked item, stored as JSON in the RS profile config.
Package-private so `PersistenceCompatTest` can freeze its legacy shape and
`PersistedSchemaSnapshotTest` can guard it; any field change fails the
schema snapshot until it is regenerated and explained in the PR.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `List<AcquisitionRecord>` | `acquisitions` |  |
| `String` | `category` |  |
| `boolean` | `compact` |  |
| `boolean` | `costBasisInitialized` |  |
| `Long` | `deathSuspendedAt` |  |
| `int` | `deathSuspendedQuantity` |  |
| `boolean` | `favorite` |  |
| `int` | `itemId` |  |
| `List<NotificationRule>` | `notifications` |  |
| `boolean` | `notificationsInitialized` |  |
| `boolean` | `onOverlay` |  |
| `int` | `pouchSuspendedQuantity` |  |
| `int` | `quantity` |  |

### Field Detail

#### acquisitions

`List<AcquisitionRecord> acquisitions`

#### category

`String category`

#### compact

`boolean compact`

#### costBasisInitialized

`boolean costBasisInitialized`

#### deathSuspendedAt

`Long deathSuspendedAt`

#### deathSuspendedQuantity

`int deathSuspendedQuantity`

#### favorite

`boolean favorite`

#### itemId

`int itemId`

#### notifications

`List<NotificationRule> notifications`

#### notificationsInitialized

`boolean notificationsInitialized`

#### onOverlay

`boolean onOverlay`

#### pouchSuspendedQuantity

`int pouchSuspendedQuantity`

#### quantity

`int quantity`

---

## com.oveduumnakal.StockpilePlugin

_class_

`public class StockpilePlugin`

Plugin entry point: wires up the side panel and overlays and drives all
tracking logic.

<p>Responsibilities: persisting and restoring the tracked-item set; counting
each item across the watched inventory/bank containers (and the rune pouch);
polling the wiki for live prices, metadata, and history; maintaining each
item's cost-basis lots (FIFO acquire/close) for profit; and evaluating
user-defined notification rules. It subscribes to the relevant game events and
marshals UI work onto the Swing thread and network work onto a background
executor.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final String` | `AMMO_CATEGORY` | `ItemCategoryClassifier` category holding recoverable ranged ammo — arrows, bolts, darts, … (#234). |
| `private static final Set<String>` | `CONSUMABLE_CATEGORIES` | Item categories whose members are used up in a single action — food eaten, a last potion dose drunk — so an unclaimed removal of one closes at 0 under `AcquisitionSource#CONSUMED` (its cost realizes as a loss) rather than an avg-price Unknown "sale" (#218). |
| `private static final Set<String>` | `DESTROYED_AMMO_TOKENS` | Name tokens marking ammo destroyed on use — a cannonball fired from a cannon, a chinchompa thrown — matched by name rather than category, since chinchompas classify as Hunter and a few cannonballs as Weapons. |
| `private static final Set<Integer>` | `EMPTY_CONTAINERS` | Empty vessels left behind when a potion or drink/dish is finished — a free byproduct of the consumption, booked at 0 rather than an avg-price Unknown purchase (#218). |
| `private static final String` | `EMPTY_CONTAINERS_MESSAGE` |  |
| `private static final int` | `FIRE_RUNE_ID` |  |
| `private static final Set<Skill>` | `GATHERING_SKILLS` | Skills whose XP drops mark an inventory gain as gathered from the world at 0 cost (#213). |
| `private static final int` | `GE_DESC_HEIGHT` | Height the GE info-block text widget is grown to so its fourth row is not self-clipped (#142). |
| `private static final int` | `GE_ICON_SIZE` | Rendered size, in pixels, of the Stockpile icon on the GE button (#140). |
| `private static final int` | `GE_LOGIN_SYNC_TICKS` | Ticks after login during which `GrandExchangeOfferChanged` events are treated as the login offer sync (pre-existing offers) rather than user actions. |
| `private static final int` | `GE_TITLE_ORANGE` | The GE offer title/heading orange used for the Track button text, same for both states (#139). |
| `private static final int` | `GE_TRACK_BORDER` | Muted GE-title orange for the Track button's outline box (#139). |
| `private static final float` | `GLOW_MAX_ALPHA` |  |
| `private static final float` | `GLOW_MIN_ALPHA` |  |
| `private static final long` | `GLOW_PERIOD_FAST_MS` |  |
| `private static final long` | `GLOW_PERIOD_MEDIUM_MS` |  |
| `private static final long` | `GLOW_PERIOD_SLOW_MS` |  |
| `private static final String` | `LOOT_SACK_OPTION` | Menu option and target substring for the Huntsman's loot sack, whose contents land in the inventory with no reward `ItemContainer` to observe. |
| `private static final String` | `LOOT_SACK_TARGET` |  |
| `private static final double` | `MAX_DELTA_PCT` | Maximum plausible Δ% for a notification: changes beyond this magnitude indicate a sparse/stale window average (a near-zero denominator) rather than a real move, and are ignored so a one-shot rule isn't fired on noise. |
| `private static final int` | `NATURE_RUNE_ID` |  |
| `static final int` | `OVERLAY_MAX` | Maximum number of items shown in the on-screen overlay (fixed for now). |
| `private static final long` | `PLATINUM_TOKEN_GP` | Gp value of one platinum token, the coin-equivalent currency for trades above max cash. |
| `private static final Duration` | `PORTFOLIO_SAVE_INTERVAL` | How often at most the portfolio history is rewritten to config. |
| `private static final String` | `POTION_EMPTY_OPTION` | Menu option that discards a potion's liquid, leaving an empty vial — booked as a 0-gp drop (#232). |
| `private static final String` | `POUCH_DEPOSIT_PREFIX` | Chat lines emitted when a hunting pouch is emptied to the bank — the per-pouch "Empty" deposit (SPAM) and the bank's "Empty containers" button (GAMEMESSAGE). |
| `private static final String` | `POUCH_DEPOSIT_SUFFIX` |  |
| `private static final String` | `POUCH_FILL_OPTION` | Menu option that stores held furs/meats into an open hunting pouch (#214). |
| `private static final Set<String>` | `POUCH_TARGETS` | Substrings identifying a fur/meat hunting pouch as the "Fill" menu target, across sizes (#214). |
| `private static final Duration` | `PRICE_CACHE_SAVE_INTERVAL` | How often at most the price cache is rewritten to config during regular refreshes. |
| `private static final Set<Skill>` | `PROCESSING_SKILLS` | Skills whose XP drops identify a processing action for the basis-transfer pairing (#69). |
| `private static final Set<String>` | `RECOVERABLE_WEAPON_TOKENS` | Name tokens for thrown melee weapons that survive the throw and land recoverable like arrows — knives and throwing axes classify as Weapons rather than the `#AMMO_CATEGORY`, so they need a name match to reach the ground-suspension path (#234). |
| `private static final ImmutableSet<Integer>` | `REWARD_CONTAINERS` | Reward/loot containers that hand out free loot into the inventory (#215). |
| `private static final String` | `REWARD_LOOT_PREFIX` | Chat-line prefix for the generic "loot to inventory" reward message ("You found some loot: N x Item"). |
| `private static final String` | `RUNE_CATEGORY` | `ItemCategoryClassifier` category holding the runes a spellcast burns (#235). |
| `private static final int` | `RUNE_POUCH_LOGIN_GRACE_TICKS` | Grace window (in ticks) after login during which an empty→full rune pouch read is treated as baseline hydration rather than a real acquisition. |
| `private static final int[]` | `RUNE_POUCH_QUANTITY_VARBITS` |  |
| `private static final int[]` | `RUNE_POUCH_TYPE_VARBITS` |  |
| `private static final ImmutableSet<Integer>` | `RUNE_POUCH_VARBITS` |  |
| `private static final Set<String>` | `SECTION_SLOT_KEYS` |  |
| `private static final int` | `STOCKPILE_GE_SPRITE_ID` | Custom sprite-override id for the Stockpile icon shown on the GE "View in Stockpile" button (#140). |
| `private static final ImmutableSet<Integer>` | `TRACKED_CONTAINERS` |  |
| `private static final int` | `TRADE_OTHER_CONTAINER` | The partner-side trade container: the offer container id with the "other player" bit set. |
| `private static final Duration` | `WHATS_NEW_WINDOW` | How long after first launching a new release the "What's New" indicator stays highlighted. |
| `private final List<CategoryState>` | `categories` | Ordered user-defined categories (names + collapsed state); the source of truth for category order. |
| `private Changelog` | `changelog` | Bundled release notes, parsed once at startup; the newest entry is the current version. |
| `private Client` | `client` |  |
| `private ClientThread` | `clientThread` |  |
| `private ClientToolbar` | `clientToolbar` |  |
| `private StockpileConfig` | `config` |  |
| `private ConfigManager` | `configManager` |  |
| `private final Map<Integer,Map<Integer,Integer>>` | `containerCounts` |  |
| `private int` | `currentGeItem` | The item shown on the currently-open GE offer screen, or -1 when no offer screen is up (GE integration). |
| `private final Set<Integer>` | `doseSwapClaimedIds` | Ids claimed by this tick's dose-swap pass, so the XP-less combine detector skips a decant/consume (#231). |
| `private ScheduledExecutorService` | `executor` |  |
| `private boolean` | `favoritesCollapsed` |  |
| `private int` | `gatherXpTick` | The tick of the most recent gathering-skill XP gain, marking a gain as a free gather (#213). |
| `private Widget` | `geButton` | The native-style button injected onto the GE offer screen in Button mode, or null. |
| `private long` | `geLineHigh` | High and low market prices for `#geLineItem` from the resolved source; 0 when unavailable (#142). |
| `private int` | `geLineItem` | The raw GE item id the cached 5m high/low belong to, or -1 when unfetched or stale (#142). |
| `private long` | `geLineLow` |  |
| `private String` | `geLineSource` | Which source `#geLineHigh`/`#geLineLow` came from, as a row-label prefix (5m/1h/Latest) (#142). |
| `private int` | `geLoginTick` |  |
| `private Widget` | `geTrackButton` | The Track/Untrack button's beige chrome (a BUTTON_BROWN graphic) beside GE History, or null (#139). |
| `private Widget` | `geTrackLabel` | The dark text label riding on `#geTrackButton`; carries the Track/Untrack text (#139). |
| `private final Map<TileItem,Tile>` | `groundItems` |  |
| `private StockpileGroundOverlay` | `groundOverlay` |  |
| `private Gson` | `gson` |  |
| `private StockpileHighlightOverlay` | `highlightOverlay` |  |
| `private ItemManager` | `itemManager` |  |
| `private volatile Map<Integer,WikiRealtimePriceClient.ItemMapping>` | `itemMappings` |  |
| `private Instant` | `lastPortfolioSave` |  |
| `private Instant` | `lastPriceCacheSave` | When the price cache was last written, to throttle per-refresh saves. |
| `private Instant` | `lastPriceRefresh` |  |
| `private final Map<Skill,Integer>` | `lastSkillXp` | Per-skill XP as last seen, so a StatChanged can be classified as a real XP gain. |
| `private CostBasisLedger` | `ledger` | The cost-basis / GE trade ledger (#255); this plugin is its `LedgerHost` seam. |
| `private int` | `magicXpTick` | The tick of the most recent Magic XP gain, marking removed runes as burned by a spellcast (#235). |
| `private volatile boolean` | `mappingsLoaded` |  |
| `private final Map<TileItem,Integer>` | `myDrops` | Ground items this player dropped: the `TileItem` → how many of its units are ours. |
| `private final Map<Integer,Integer>` | `myTradeOffer` | Latest captured trade-offer sides (canonical id → qty), read when the trade completes (#66). |
| `private NavigationButton` | `navButton` |  |
| `private Notifier` | `notifier` |  |
| `private OverlayManager` | `overlayManager` |  |
| `private StockpilePanel` | `panel` |  |
| `private final Map<Integer,Integer>` | `pendingItemDeltas` |  |
| `private boolean` | `pendingQuantitySync` |  |
| `private final AtomicReference<Runnable>` | `pendingRebuild` | The newest un-rendered panel snapshot; non-null means a rebuild drainer is already queued. |
| `private StockpilePersistence` | `persistence` | Client-free persistence layer (#111); built in `#startUp()` once gson/config are injected. |
| `private final PortfolioHistory` | `portfolioHistory` | Per-item thinned time series of portfolio value/cost for the history chart. |
| `private TrackedItem` | `previewItem` | Transient, non-persisted item backing the read-only detail preview (view-only button); not in `#trackedItems`. |
| `private ScheduledFuture<?>` | `priceRefreshTask` |  |
| `private int` | `processingXpTick` | The tick of the most recent processing-skill XP gain, pairing recipe inputs to outputs. |
| `private int` | `rewardContainerTick` | The tick a reward/loot container last changed, marking a matching inventory gain as a free reward (#215). |
| `private final Map<Integer,Integer>` | `runePouchCounts` |  |
| `private boolean` | `runePouchDirty` | Set when a rune pouch varbit changes; the diff is deferred to `#onClientTick` so every type/quantity varbit for the change has settled before it is read (#237). |
| `private boolean` | `runePouchSeenSinceLogin` |  |
| `private final List<StockpileScreenOverlay>` | `screenOverlays` | One independently-draggable overlay box per slot; they start grouped in the same snap corner. |
| `private final Set<Integer>` | `seenContainersSinceLogin` |  |
| `private boolean` | `sessionInitialized` | Whether the current logged-in session has been initialised. |
| `private boolean` | `shopOpen` | Whether an NPC shop interface is open, gating the coin-delta shop pricing (#67). |
| `private final Map<Integer,Integer>` | `theirTradeOffer` |  |
| `private int` | `thievingXpTick` | The tick of the most recent Thieving XP gain, marking a gain as free stolen loot (#217). |
| `private final List<ItemDespawned>` | `tickGroundDespawns` |  |
| `private final List<ItemQuantityChanged>` | `tickGroundQuantityChanges` |  |
| `private final List<ItemSpawned>` | `tickGroundSpawns` | This tick's ground spawns/despawns/stack changes, correlated against the inventory deltas (#65). |
| `private final Map<Integer,TrackedItem>` | `trackedItems` |  |
| `private boolean` | `uncategorizedCollapsed` |  |
| `private WikiRealtimePriceClient` | `wikiPriceClient` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `private void` | `addTrackedItem(int itemId)` | Tracks an item by id with defaults (full tracking mode, no preset cost basis). |
| `private void` | `addTrackedItem(int itemId, TrackItemMode mode)` | Tracks an item by id in the given mode, routing `TrackItemMode#VIEW` to a read-only preview instead. |
| `private void` | `addTrackedItem(int itemId, int initialQuantity, List<AcquisitionRecord> records, List<NotificationRule> notifications, boolean notificationsInitialized, boolean costBasisInitialized, boolean syncOnAdd, boolean persistOnAdd, TrackItemMode mode)` | Canonical add: creates a `TrackedItem` (resolving its name/tradeable flag from the item composition), seeds its quantity, acquisitions, and notifications, registers it, and persists/refreshes. |
| `private void` | `addTrackedItem(int itemId, int initialQuantity, List<AcquisitionRecord> records, boolean costBasisInitialized)` | Tracks an item with a preset quantity and acquisition history (e.g. |
| `private void` | `applyAutoCategorize(boolean includeCategorized)` | Applies auto-categorization on the client thread: classify each in-scope item, create categories, assign. |
| `private void` | `applyGeHighLowLine()` | Swaps the "Actively traded price" text inside the open GE offer's info block (the single `SETUP_DESC`/`DETAILS_DESC` text widget) for one compact market line — High, Low and Avg together — in place, so the line count never changes and nothing else moves (#142). |
| `private void` | `applyGePrices(Map<Integer,WikiRealtimePriceClient.ItemPrices> all)` | Applies freshly fetched prices to every tracked item: records per-side deltas against the previous values, updates the LIVE window stats, seeds a cost basis on first successful price if one wasn't set, then re-evaluates notifications and refreshes the panel (including the open detail view). |
| `private void` | `applyGeTrackLabel()` | Sets the Track/Untrack text, action, and resting colour (green/red) from the offer's tracked state. |
| `private String` | `applyImportedList(String token)` | Decodes and merges a share code on the client thread. |
| `private void` | `applyItemMetadata(TrackedItem tracked)` | Copies cached GE metadata (buy limit, value, alch values) onto an item, if available. |
| `private void` | `applyLivePrices(TrackedItem item, WikiRealtimePriceClient.ItemPrices prices)` | Applies a freshly fetched price set to an item: records per-side deltas, updates current prices, and refreshes its LIVE window stats. |
| `private void` | `applyPersistedDeathSuspension(int itemId, int quantity, Long suspendedAtEpoch)` | Restores a persisted death suspension after its item has been added, so a recovery spanning a relog still un-suspends instead of opening new lots. |
| `private void` | `applyPersistedGrouping(int itemId, boolean favorite, String category, boolean onOverlay, boolean compact)` | Applies a persisted item's favorite/category/overlay/compact grouping after it has been added. |
| `private void` | `applyPersistedPouchSuspension(int itemId, int quantity)` | Restores a persisted fur/meat-pouch suspension after its item has been added, so furs "Fill"ed in before a logout still un-suspend (keeping their original source and basis) when the pouch is emptied in a later session (#214). |
| `String` | `autoCategorize(boolean includeCategorized)` | Auto-assigns tracked items to wiki-derived categories (see `ItemCategoryClassifier`), creating any missing categories. |
| `float` | `breathingAlpha()` |  |
| `void` | `buildAcquisitionsCsv(Consumer<String> onResult)` | Builds the acquisitions log of all tracked items as CSV (see `AcquisitionCsvExporter`) on the client thread — the items and their acquisition lists are client-thread state — and hands it to `onResult` on the EDT. |
| `void` | `buildShareToken(Consumer<String> onResult)` | Builds a shareable code for the current tracked list (ids, modes, categories, favorites) — "" when empty — and hands it to `onResult` on the EDT. |
| `private int` | `canonicalCountId(int itemId)` | Resolves a container slot's item id to the canonical (unnoted, non-placeholder) id it should count as, using a single `ItemComposition` lookup instead of a separate placeholder-check + `canonicalize` pair (#185) — a bank event covers ~800 slots. |
| `private void` | `captureTradeOffer(Map<Integer,Integer> side, ItemContainer container, boolean mine)` | Snapshots one side of the trade window (canonical id → quantity) as its container changes. |
| `private String` | `categoryValue(TrackedItem item, NotificationMetric metric)` | Resolves the current categorical rating of a metric for an item (volatility, liquidity, or 30-day range position) via `MarketClassifier`. |
| `private void` | `claimReceivedItems(Map<Integer,Integer> side, long gp)` | Claims received items as buys at the apportioned per-unit price, matched by their inventory additions. |
| `private void` | `clearAcquisitions(int itemId)` | Clears an item's acquisition lots (resetting its cost basis) and persists/refreshes. |
| `private void` | `clearAllTrackedItems()` | Removes every tracked item, then persists and refreshes. |
| `private void` | `clearPortfolioHistory()` | Wipes the portfolio value history (in memory and in config), e.g. |
| `private void` | `closeAllGroundSuspensions()` | Closes every remaining ground suspension as lost (delegating to the ledger) and clears our own drop tracking. |
| `private void` | `closeGivenItems(Map<Integer,Integer> side, long gp)` | Closes given items as sells at the apportioned per-unit price, realizing them against the trade suspension taken when they were offered. |
| `private static String` | `colourGp(long value, String colour)` |  |
| `private void` | `correlateCombine()` | Pairs an XP-less combine — a tick that consumes one or more ingredients and produces a single tradeable output with no skill XP and no coin movement — as `AcquisitionSource#PROCESSING`, so the ingredients' cost basis carries onto the product instead of both sides falling to Unknown at market value (#231). |
| `private void` | `correlateDecant()` | Pairs a dose family's consumed lots with the doses it produces on a single XP-less tick, so cost basis follows the liquid across the item-id change rather than being realized as a sale. |
| `private void` | `correlateDoseSwapFamily(List<int[]> members)` | Applies the dose-family basis transfer to one family's members (`{id, delta, doses`}): a dose-conserving swap (consumed doses equal produced doses) is a decant under `AcquisitionSource#DECANT`; a swap that loses doses while leaving some (a dose drunk) is a consume-down under `AcquisitionSource#CONSUMED`. |
| `private void` | `correlateGathering()` | Attributes this tick's unclaimed inventory gains to `AcquisitionSource#GATHER` at 0 when a gathering-skill XP drop (Hunter, Mining, Fishing, Woodcutting, Farming) marks them as gathered from the world at no cost (#213) — Sunfire splinters, antlers, ores, fish, logs, harvested herbs. |
| `private void` | `correlateGroundActivity()` | Correlates this tick's ground-item activity with the pending inventory deltas (#65): a spawn (or stack increase) on the player's tile matching a pending removal is our drop — its units queue for ground suspension and the `TileItem` is remembered; a despawn of a remembered drop with no matching pickup closes its units as lost at 0; a despawn matching a pending addition that isn't ours is a loot pickup, claimed as a `AcquisitionSource#GROUND` acquisition at 0. |
| `private void` | `correlateGroundGain(TileItem item, Tile tile, int gained, WorldPoint myLocation)` | Handles a ground pile gaining units: on our tile against a pending removal, it's our drop. |
| `private boolean` | `correlateGroundTaken(TileItem item, int taken)` | Handles a ground pile losing units: a remembered drop with a matching pending addition is a re-pickup (the greedy un-suspend consumes it during the sync); with no matching addition its units close as lost at 0. |
| `private void` | `correlateProcessing()` | Pairs this tick's consumed inputs with the produced output when a processing-skill XP gain identifies a recipe action (#69), transferring the summed input cost: tracked inputs contribute (and close at) their FIFO open-lot cost, untracked inputs their fallback market value, and the total is carried onto the output's new lot(s) by `CostBasisLedger` so their basis sums exactly to it. |
| `private void` | `correlateReward()` | Claims this tick's tracked inventory gains as a free `AcquisitionSource#REWARD` at 0 when a reward-loot signal fired on the same tick (`#rewardContainerTick`) — a reward/loot container change (`#REWARD_CONTAINERS`), a Huntsman's loot-sack open, or a "you found some loot" chat line — i.e. |
| `private void` | `correlateThieving()` | Attributes this tick's unclaimed inventory gains to `AcquisitionSource#THIEVING` at 0 when a Thieving XP drop marks them as stolen at no cost (#217) — pickpocket loot, stall produce, chest hauls. |
| `private void` | `createCategory(String name)` | Creates a new category (ignoring blanks and case-insensitive duplicates), then persists and refreshes. |
| `private int` | `currentGeOfferItem()` |  |
| `public int` | `currentTick()` | Returns the current client game tick. |
| `private void` | `deleteCategory(String name)` | Deletes a category, moving its items to Uncategorized, then persists and refreshes. |
| `private void` | `detectVersionChange()` | Detects a new plugin version by comparing the changelog's current version to the last-seen version in config. |
| `private void` | `ensureCategory(String name, boolean collapsed)` | Adds a category by name if one with that name doesn't already exist (case-insensitive). |
| `private void` | `evaluateNotifications()` | Evaluates every item's notification rules and fires the configured notifier for any that are met. |
| `private Boolean` | `evaluateRule(TrackedItem item, NotificationRule rule)` | Evaluates a single rule against an item. |
| `private String` | `examineFor(int itemId)` |  |
| `public FallbackPricing` | `fallbackPricing()` | Returns the configured fallback-pricing policy for unknown-source changes. |
| `private void` | `fetchItemMappings()` | Fetches GE item metadata in the background, keeping the previous map on failure. |
| `private Widget` | `findGeCloseButton()` | Locates the GE window's close (X) button by walking to the top-level ancestor of the open offer container and searching its subtree for a visible widget with a "Close" action (#139). |
| `private void` | `flushRunePouchDelta()` | Diffs settled rune pouch contents once per tick. |
| `Map<TileItem,Tile>` | `getGroundItems()` |  |
| `private int` | `getItemIdFromMenuEntry(MenuEntry entry)` |  |
| `List<TrackedItem>` | `getOverlayItems()` |  |
| `private void` | `hideGeButton()` | Hides and forgets the injected GE button, if one is currently on the offer interface. |
| `private void` | `hideGeTrackButton()` | Hides and forgets the injected Track/Untrack button, if one is currently on the offer interface (#139). |
| `private void` | `hydratePriceCache()` | Hydrates tracked items from the persisted price cache so the panel shows last-known values (dimmed by the existing staleness treatment once their trade times age past the threshold) instead of placeholders. |
| `void` | `importTrackedList(String token, Consumer<String> onResult)` | Merges a shared tracked-list code into the current profile: adds items that aren't already tracked (with their mode, category, and favorite flag) plus any missing categories they reference. |
| `private boolean` | `inAutoCategorizeScope(TrackedItem item, boolean includeCategorized)` |  |
| `private void` | `injectGeButton()` | Injects the Stockpile icon as a "View in Stockpile" button onto the visible GE offer container (#140). |
| `private void` | `injectGeTrackButton()` | Injects a Track/Untrack button in the GE window's title bar, immediately left of the close (X) button (#139): a muted-orange outline box framing bold Track/Untrack text (the "Grand Exchange" header font/weight) whose colour reflects the tracked state. |
| `private String` | `injectPriceLines(String desc)` | Swaps the "Actively traded price: N" segment of the GE info-block for the two market lines, always on their own rows: a leading "Buy limit: N /" that RuneLite inlines on buy offers is split off onto its own line, and any trailing convenience-fee line is kept. |
| `private boolean` | `isAmmo(int itemId)` |  |
| `public boolean` | `isConsumable(int itemId)` |  |
| `public boolean` | `isDestroyedAmmo(int itemId)` |  |
| `private boolean` | `isDestroyedProduct(int itemId)` |  |
| `private boolean` | `isDosePotion(int itemId)` |  |
| `public boolean` | `isEmptyContainer(int itemId)` | Returns whether the given item id is a known empty-container placeholder. |
| `private static boolean` | `isPouchDepositMessage(String message)` |  |
| `private static boolean` | `isPouchTarget(String target)` |  |
| `private boolean` | `isRealItem(int itemId)` |  |
| `public boolean` | `isRecoverableAmmo(int itemId)` |  |
| `private boolean` | `isSpellcastRune(int itemId)` |  |
| `boolean` | `isTracked(int itemId)` |  |
| `private static boolean` | `isTradeCurrency(int itemId)` |  |
| `private boolean` | `isWhatsNew()` |  |
| `private int` | `itemInGeContainer(int componentId)` |  |
| `static long[]` | `latestSeriesHighLow(List<WikiRealtimePriceClient.PricePoint> series)` | Scans a price series newest-first for the most recent priced average high and low, returned as `[high, low]` (each 0 when the series holds no priced sample) (#142). |
| `private void` | `loadCategories()` | Restores the category definitions and group collapsed state from per-profile JSON. |
| `private void` | `loadPersistedItems()` | Restores tracked items from the per-profile JSON written by `#persistTrackedItems()`. |
| `private void` | `loadPortfolioHistory()` | Restores the per-item portfolio history from per-profile config, ignoring a corrupt value. |
| `private TrackedItem` | `lookupItem(int itemId)` |  |
| `private void` | `markWhatsNewSeen()` | Persists that the user has seen the current release's "What's New", quieting the indicator. |
| `private long` | `marketUnitValue(int itemId)` |  |
| `private void` | `mergeImportedList(List<PortfolioShareCodec.Entry> entries, List<CategoryState> importedCategories)` | Applies a decoded tracked-list import on the client thread: categories first, then new items. |
| `private void` | `migrateAutoAddSetting()` | One-time migration for #219: the old combined `autoAddItems` enum (High/Low/Avg/Zero/Off) split into a boolean auto-add gate plus a separate `FallbackPricing`. |
| `private String` | `notificationText(TrackedItem item, NotificationRule rule)` | Builds the user-facing notification message, e.g. |
| `private OptionalDouble` | `numericValue(TrackedItem item, NotificationMetric metric, TimeWindow window)` | Resolves the current numeric reading of a metric for an item over a window (price, volume, profit, HA profit, Δ% vs. |
| `void` | `onAcquisitionsEdited(int itemId)` | Callback after the user edits an item's acquisitions: re-derives its held quantity from the lots and persists. |
| `public void` | `onActorDeath(ActorDeath event)` | Marks the local player's death, opening the death-loss suspension window (#70). |
| `public void` | `onChatMessage(ChatMessage event)` | Registers the completed trade's claims when the game confirms the exchange (#66). |
| `public void` | `onClientTick(ClientTick event)` | Per-tick work: flushes any pending quantity sync, evaluates notifications, and (when ground highlighting is on) reorders tracked items' "Take" menu entries to the bottom so they don't get in the way of normal actions. |
| `public void` | `onConfigChanged(ConfigChanged event)` | Reacts to this plugin's config changes: resolves detail-section slot conflicts, reschedules the refresh when the interval changes, and otherwise just repaints the panel. |
| `public void` | `onGameStateChanged(GameStateChanged event)` | Resets transient and per-login state on game-state transitions: clears ground items on each load, and on login wipes the count caches and reloads the persisted tracked items. |
| `public void` | `onGameTick(GameTick event)` | Grand Exchange integration: each tick, detects the item on the open offer setup/details screen and, per `StockpileConfig#geIntegration()`, either auto-opens it in Stockpile or injects a "View in Stockpile" button. |
| `public void` | `onGrandExchangeOfferChanged(GrandExchangeOfferChanged event)` | Consumes GE offer progress to price trades and track the buy limit. |
| `public void` | `onItemContainerChanged(ItemContainerChanged event)` | Tracks per-container item counts as inventory/bank/etc. |
| `public void` | `onItemDespawned(ItemDespawned event)` | Forgets a ground item once it despawns, buffering it for #65's pickup/lost-drop correlation. |
| `public void` | `onItemQuantityChanged(ItemQuantityChanged event)` | Buffers ground-stack quantity changes so drops onto an existing stack correlate like spawns (#65). |
| `public void` | `onItemSpawned(ItemSpawned event)` | Records a ground item and its tile so the ground overlay can outline it, buffering it for #65. |
| `public void` | `onMenuOpened(MenuOpened event)` | Adds a "Track Item" / "Stop Tracking" right-click option to item menu entries, when enabled. |
| `public void` | `onMenuOptionClicked(MenuOptionClicked event)` | Claims an upcoming High/Low Alchemy disposal (#68): casting either spell on a tracked item registers an `AcquisitionSource#ALCHEMY` claim for one unit at the coins the cast actually yields — the item's cached high/low alch value — so the lot closes at the real proceeds instead of the current average. |
| `private void` | `onNotificationsEdited(int itemId)` | Callback after the user edits an item's notification rules: just persists the change. |
| `public void` | `onRuneScapeProfileChanged(RuneScapeProfileChanged event)` | Resets the session baseline when the RS profile (account) changes, so stats restart per account. |
| `public void` | `onStatChanged(StatChanged event)` | Marks the tick of processing-skill XP gains (recipe actions, #69), gathering-skill XP gains (#213), and Thieving XP gains (#217). |
| `public void` | `onVarbitChanged(VarbitChanged event)` | Mirrors rune pouch contents (held in varbits, not a normal container) into the quantity counts, accumulating deltas like a container change. |
| `public void` | `onWidgetClosed(WidgetClosed event)` | Clears GE-integration state when the offer interface closes, and shop state for #67. |
| `public void` | `onWidgetLoaded(WidgetLoaded event)` | Forces the GE buttons to be re-injected against a freshly (re)built offer interface. |
| `private void` | `openGeItemInStockpile(int itemId)` | Opens the item in Stockpile's view-only preview, switching to/focusing the panel when configured. |
| `public GrandExchangeOffer[]` | `openGeOffers()` | Returns the player's current Grand Exchange offers. |
| `private void` | `orderGeneratedCategories(List<CategoryState> created)` | Orders an auto-categorize run's generated categories alphabetically after any pre-existing (manually ordered) ones, then keeps "Other" at the very end. |
| `private int` | `overlayItemCount()` |  |
| `private void` | `pairProcessingRecipe(List<int[]> inputs, int outputId, int outputQty, boolean trackedOutput)` | Closes a recipe's consumed inputs under `AcquisitionSource#PROCESSING` at their FIFO open-lot cost and queues the summed basis in `pendingProcessingOutput` so the matching gain opens the produced lot carrying it. |
| `private void` | `persistCategories()` | Serializes the category definitions and group collapsed state to per-profile config. |
| `private void` | `persistPortfolioHistory()` | Serializes the per-item portfolio history to per-profile config. |
| `private void` | `persistPriceCache()` | Writes every priced tracked item's current prices to the RS profile config. |
| `public void` | `persistTrackedItems()` | Serializes the current tracked items (quantity, cost basis, notifications, grouping) to per-profile config. |
| `List<long[]>` | `portfolioHistoryPoints()` |  |
| `private void` | `previewItem(int itemId)` | Opens a read-only detail preview for an untracked item without adding it to the tracked list or persisting anything. |
| `private String` | `priceLines()` |  |
| `private void` | `promptCategoryForTrackedItem(int itemId)` | After an item is explicitly tracked (#211), asks the panel to prompt for its category. |
| `StockpileConfig` | `provideConfig(ConfigManager configManager)` |  |
| `private void` | `queueTradeSuspension(Map<Integer,Integer> before, Map<Integer,Integer> after)` | Turns the change in our own offer into pending suspend/un-suspend intents: items added to the offer left our inventory and should suspend, items withdrawn returned and should un-suspend. |
| `private void` | `rebucketScreenOverlays()` | Removes and re-adds the screen overlays so the manager re-buckets them into their (config-driven) layer. |
| `private void` | `recomputeWindowStats(TrackedItem tracked)` | Rebuilds an item's per-window `PriceStats` from its current prices (LIVE) and history series. |
| `private void` | `reconcileAllQuantities()` | Recounts every tracked item from scratch across all containers plus the rune pouch, and reconciles each item's lots to match the true on-hand total (opening or closing lots as needed). |
| `private void` | `recordPortfolioSnapshot()` | Records a portfolio snapshot into the history (persisting throttled): the running value — owned units (held plus suspended) marked to the current average plus sold lots at their actual sale price — against the invested cost basis of every logged lot, which stays fixed as lots sell. |
| `private void` | `refreshGePrices()` | Fetches the latest prices for all items in the background, then applies them on the client thread. |
| `public void` | `refreshPanel()` | Refreshes the panel without flagging a price update (no change indicators). |
| `private void` | `refreshPanel(boolean pricesUpdated)` | Pushes the current tracked items and totals to the panel on the Swing thread. |
| `private void` | `registerGeButtonSprite(BufferedImage icon)` | Registers the bundled Stockpile icon as a custom sprite override so it can be drawn on the injected GE button (#140). |
| `private void` | `registerShopClaims(Map<Integer,Integer> oldCounts, Map<Integer,Integer> newCounts)` | Claims an inventory change as a shop transaction (#67) when exactly one tracked non-coin item moved: the coins paid or received, divided across the quantity, price the item's `AcquisitionSource#SHOP` claim. |
| `private void` | `registerTradeClaims()` | Books a completed trade's item movements as `AcquisitionSource#PLAYER_TRADE` (#66): items received buy in at the gp we gave apportioned across them by market value, and items given close at the gp we received apportioned the same way. |
| `private void` | `removeTrackedItem(int itemId)` | Stops tracking an item, then persists and refreshes. |
| `private void` | `renameCategory(String oldName, String newName)` | Renames a category and re-points its items, ignoring blanks and clashes, then persists and refreshes. |
| `private void` | `reorderCategory(String name, int targetIndex)` | Moves a category to a new position in the ordered list, then persists and refreshes. |
| `private void` | `reorderTrackedItem(int itemId, int targetIndex)` | Moves a tracked item to a new position in the list, persisting the new order so it survives restarts. |
| `private void` | `requestDetailData(int itemId)` | Fetches all four history series (5m/1h/6h/24h) plus metadata for the detail view in the background, then updates stats, alch rune prices, and the detail panel on the appropriate threads. |
| `private void` | `requestGeLinePrices(int itemId)` | Resolves the open GE offer item's market prices in the background and caches them for the info-block line, overwriting it in place once they arrive (#142). |
| `private void` | `requestSeries(int itemId, boolean refreshAfter)` | Fetches just the 5m series for an item in the background and recomputes its window stats. |
| `private long` | `resolveAlchValue(TrackedItem tracked, int canonicalId, boolean high)` | Resolves an item's alch value with a client-cache fallback (#238): prefers the cached wiki value on the tracked item, and when that has not loaded yet reads the item composition — `net.runelite.api.ItemComposition#getHaPrice()` for high alch, and the store value's 40% for low alch — so the `AcquisitionSource#ALCHEMY` claim is always registered regardless of whether the wiki mapping or the item's price series has been fetched this session. |
| `private void` | `resolveTradeabilityForAll()` | Applies wiki metadata (tradeability, buy limit, GE value, high/low alch) to every tracked item and the preview item now that the wiki mapping is available, then refreshes the panel. |
| `private void` | `resolveTradeable(TrackedItem item)` | Narrows an item's tradeable flag using the wiki mapping: an item that the game composition reports as tradeable but which is absent from the Grand Exchange mapping (e.g. |
| `private long` | `runePrice(int itemId)` |  |
| `private Widget` | `scanForCloseAction(Widget widget)` | Recursively searches a widget subtree for the first visible widget carrying a "Close" action. |
| `private int` | `scanForItem(Widget widget)` | Recursively searches a widget subtree for the first child holding a real item id. |
| `private void` | `scheduleRefresh()` | (Re)schedules the recurring GE price refresh at the configured rate (min 30s), replacing any prior task. |
| `private void` | `setFavorite(int itemId, boolean favorite)` | Sets an item's favorite flag (pinning it to the top "Favorites" group), then persists and refreshes. |
| `private void` | `setGlobalOrder(List<Integer> orderedIds)` | Reorders the tracked items to match the given id order (drag reorder), then persists and refreshes. |
| `private void` | `setGroupCollapsed(String groupKey, boolean collapsed)` | Sets a list group's collapsed state (a category name, or a special-group key), then persists and refreshes. |
| `private void` | `setItemCategory(int itemId, String category)` | Assigns an item to a category (null/blank clears it to Uncategorized), then persists and refreshes. |
| `private void` | `setItemCompact(int itemId, boolean on)` | Toggles an item's per-item compact-row override (#210), then persists and refreshes. |
| `private void` | `setOnOverlay(int itemId, boolean on)` | Adds/removes an item from the on-screen overlay set, enforcing the `#OVERLAY_MAX` cap (an add beyond the cap is ignored), then persists and refreshes. |
| `private void` | `setSortMode(SortMode mode)` | Persists the chosen sort mode; the resulting `ConfigChanged` rebuilds the panel. |
| `protected void` | `shutDown() throws Exception` | Tears down the nav button, overlays, panel, and refresh task and clears all in-memory state. |
| `public boolean` | `sourcePricing()` | Returns whether source-aware pricing is enabled in config. |
| `protected void` | `startUp() throws Exception` | Builds the side panel (wiring its callbacks back to this plugin), registers the nav button and overlays, restores persisted items, and kicks off the metadata fetch and recurring price refresh. |
| `private void` | `swapConflictingSection(ConfigChanged event)` | Keeps detail-section slots unique: when a section is moved to a slot already occupied by another, the other section is swapped into the vacated slot. |
| `private void` | `syncQuantitiesForItem(TrackedItem tracked)` | Recounts a single item across all containers and the rune pouch and sets its quantity. |
| `private void` | `syncQuantitiesFromContainers()` | Applies the accumulated per-item container deltas to tracked items: positive deltas open new lots (auto-add), negative deltas close lots FIFO, and each item's quantity is adjusted. |
| `private void` | `syncRunePouch()` | Rebuilds `#runePouchCounts` by reading the rune pouch type/quantity varbits. |
| `private void` | `toggleCompactView()` | Flips the persisted compact-view flag; the resulting `ConfigChanged` rebuilds the panel. |
| `private void` | `toggleGeTracking()` | Toggles tracking of the open GE offer's item (#139). |
| `private void` | `toggleSortReversed()` | Flips the persisted sort direction; the resulting `ConfigChanged` rebuilds the panel. |
| `public TrackedItem` | `trackedItem(int itemId)` | Returns the tracked item with the given id, if tracked. |
| `public Collection<TrackedItem>` | `trackedItems()` | Returns all currently tracked items. |
| `private static long` | `tradeGp(Map<Integer,Integer> side)` |  |
| `private List<TradeApportioner.Leg>` | `tradeLegs(Map<Integer,Integer> side)` | Builds one trade side's non-currency apportionment legs, each weighted by its unit market value. |
| `private void` | `unregisterGeButtonSprite()` | Removes the Stockpile GE-button sprite override on shutdown (#140). |
| `private void` | `untrackToPreview(int itemId)` | Stops tracking an item but leaves it open in the detail view as a read-only preview (#138), so untracking from the detail header does not bounce the user back to the main list. |
| `private long` | `untrackedInputValue(int itemId)` |  |

### Field Detail

#### AMMO_CATEGORY

`private static final String AMMO_CATEGORY`

`ItemCategoryClassifier` category holding recoverable ranged ammo — arrows, bolts, darts, … (#234).

#### CONSUMABLE_CATEGORIES

`private static final Set<String> CONSUMABLE_CATEGORIES`

Item categories whose members are used up in a single action — food eaten, a last potion
dose drunk — so an unclaimed removal of one closes at 0 under
`AcquisitionSource#CONSUMED` (its cost realizes as a loss) rather than an avg-price
Unknown "sale" (#218). Keyed by `ItemCategoryClassifier` category names.
<p>
Two categories are deliberately excluded because they need their own attribution rather than
this generic branch. Ammo splits into destroyed-on-use (a genuine 0-gp loss) and recoverable
ammo that lands on the target's tile and belongs on the ground-suspension path (#234). Runes
are burned by a spellcast and book to a dedicated Cast source; they also never reach here
today, since a Magic XP tick lets `correlateProcessing` claim them first (#235).

#### DESTROYED_AMMO_TOKENS

`private static final Set<String> DESTROYED_AMMO_TOKENS`

Name tokens marking ammo destroyed on use — a cannonball fired from a cannon, a chinchompa thrown —
matched by name rather than category, since chinchompas classify as Hunter and a few cannonballs as
Weapons. Such a removal closes at 0 under `AcquisitionSource#DESTROYED` (#234).

#### EMPTY_CONTAINERS

`private static final Set<Integer> EMPTY_CONTAINERS`

Empty vessels left behind when a potion or drink/dish is finished — a free byproduct of the
consumption, booked at 0 rather than an avg-price Unknown purchase (#218). Claimed only on a
terminal-consumption tick, bounded to the number of vessels emptied.

#### EMPTY_CONTAINERS_MESSAGE

`private static final String EMPTY_CONTAINERS_MESSAGE`

#### FIRE_RUNE_ID

`private static final int FIRE_RUNE_ID`

#### GATHERING_SKILLS

`private static final Set<Skill> GATHERING_SKILLS`

Skills whose XP drops mark an inventory gain as gathered from the world at 0 cost (#213).

#### GE_DESC_HEIGHT

`private static final int GE_DESC_HEIGHT`

Height the GE info-block text widget is grown to so its fourth row is not self-clipped (#142).

#### GE_ICON_SIZE

`private static final int GE_ICON_SIZE`

Rendered size, in pixels, of the Stockpile icon on the GE button (#140).

#### GE_LOGIN_SYNC_TICKS

`private static final int GE_LOGIN_SYNC_TICKS`

Ticks after login during which `GrandExchangeOfferChanged` events are treated as the
login offer sync (pre-existing offers) rather than user actions. The client delivers the GE
offers with the login packet within a tick or two, while the player cannot open the GE and
abort an offer anywhere near this fast — so the window reliably separates the two.

#### GE_TITLE_ORANGE

`private static final int GE_TITLE_ORANGE`

The GE offer title/heading orange used for the Track button text, same for both states (#139).

#### GE_TRACK_BORDER

`private static final int GE_TRACK_BORDER`

Muted GE-title orange for the Track button's outline box (#139).

#### GLOW_MAX_ALPHA

`private static final float GLOW_MAX_ALPHA`

#### GLOW_MIN_ALPHA

`private static final float GLOW_MIN_ALPHA`

#### GLOW_PERIOD_FAST_MS

`private static final long GLOW_PERIOD_FAST_MS`

#### GLOW_PERIOD_MEDIUM_MS

`private static final long GLOW_PERIOD_MEDIUM_MS`

#### GLOW_PERIOD_SLOW_MS

`private static final long GLOW_PERIOD_SLOW_MS`

#### LOOT_SACK_OPTION

`private static final String LOOT_SACK_OPTION`

Menu option and target substring for the Huntsman's loot sack, whose contents land in
the inventory with no reward `ItemContainer` to observe. Live capture confirmed the
loot arrives on the same tick as the "Open" click, so stamping `#rewardContainerTick`
here lets `#correlateReward()` claim it within the existing window (#215). The Tempoross
reward pool and GOTR reward guardian remain deferred pending their own live capture.

#### LOOT_SACK_TARGET

`private static final String LOOT_SACK_TARGET`

#### MAX_DELTA_PCT

`private static final double MAX_DELTA_PCT`

Maximum plausible Δ% for a notification: changes beyond this magnitude
indicate a sparse/stale window average (a near-zero denominator) rather than
a real move, and are ignored so a one-shot rule isn't fired on noise.

#### NATURE_RUNE_ID

`private static final int NATURE_RUNE_ID`

#### OVERLAY_MAX

`static final int OVERLAY_MAX`

Maximum number of items shown in the on-screen overlay (fixed for now).

#### PLATINUM_TOKEN_GP

`private static final long PLATINUM_TOKEN_GP`

Gp value of one platinum token, the coin-equivalent currency for trades above max cash.

#### PORTFOLIO_SAVE_INTERVAL

`private static final Duration PORTFOLIO_SAVE_INTERVAL`

How often at most the portfolio history is rewritten to config.

#### POTION_EMPTY_OPTION

`private static final String POTION_EMPTY_OPTION`

Menu option that discards a potion's liquid, leaving an empty vial — booked as a 0-gp drop (#232).

#### POUCH_DEPOSIT_PREFIX

`private static final String POUCH_DEPOSIT_PREFIX`

Chat lines emitted when a hunting pouch is emptied to the bank — the per-pouch
"Empty" deposit (SPAM) and the bank's "Empty containers" button (GAMEMESSAGE).
Neither the pouch container nor a varbit changes, so these are the only signal (#214).

#### POUCH_DEPOSIT_SUFFIX

`private static final String POUCH_DEPOSIT_SUFFIX`

#### POUCH_FILL_OPTION

`private static final String POUCH_FILL_OPTION`

Menu option that stores held furs/meats into an open hunting pouch (#214).

#### POUCH_TARGETS

`private static final Set<String> POUCH_TARGETS`

Substrings identifying a fur/meat hunting pouch as the "Fill" menu target, across sizes (#214).

#### PRICE_CACHE_SAVE_INTERVAL

`private static final Duration PRICE_CACHE_SAVE_INTERVAL`

How often at most the price cache is rewritten to config during regular refreshes.

#### PROCESSING_SKILLS

`private static final Set<Skill> PROCESSING_SKILLS`

Skills whose XP drops identify a processing action for the basis-transfer pairing (#69).

#### RECOVERABLE_WEAPON_TOKENS

`private static final Set<String> RECOVERABLE_WEAPON_TOKENS`

Name tokens for thrown melee weapons that survive the throw and land recoverable like arrows —
knives and throwing axes classify as Weapons rather than the `#AMMO_CATEGORY`, so they need a
name match to reach the ground-suspension path (#234). A non-thrown "knife" tool never fires and only
ever leaves the inventory by a drop (handled earlier) or a claimed trade, so the loose token is safe.

#### REWARD_CONTAINERS

`private static final ImmutableSet<Integer> REWARD_CONTAINERS`

Reward/loot containers that hand out free loot into the inventory (#215). These are not
tracked as holdings — they are transient interfaces — but an inventory gain while one is
open marks that gain as a zero-cost `AcquisitionSource#REWARD` rather than Unknown.
Point-spending reward shops are deliberately excluded (their withdrawals are purchases,
not free loot). The object-search rewards that loot straight to the inventory with no
reward container are handled elsewhere: the Huntsman's loot sack via a menu hook
(`#LOOT_SACK_OPTION`) and the Tempoross reward pool via a chat hook
(`#REWARD_LOOT_PREFIX`).

#### REWARD_LOOT_PREFIX

`private static final String REWARD_LOOT_PREFIX`

Chat-line prefix for the generic "loot to inventory" reward message ("You found some loot:
N x Item"). The Tempoross reward pool (Net/Big-search) drops loot straight into the inventory
with no reward `ItemContainer` and its object-search click lands three ticks before the
loot; live capture (#215) confirmed this SPAM line fires on the same tick as the inventory
gains, so stamping `#rewardContainerTick` here lets `#correlateReward()` claim the
whole multi-item drop within the existing window. Other activities that surface reward loot
through the same message (e.g. the GOTR reward guardian) are covered by the same hook.

#### RUNE_CATEGORY

`private static final String RUNE_CATEGORY`

`ItemCategoryClassifier` category holding the runes a spellcast burns (#235).

#### RUNE_POUCH_LOGIN_GRACE_TICKS

`private static final int RUNE_POUCH_LOGIN_GRACE_TICKS`

Grace window (in ticks) after login during which an empty→full rune pouch read is treated
as baseline hydration rather than a real acquisition. Pouch type/quantity varbits can arrive
across a couple of ticks as the login packet settles; a player cannot fill a pouch this fast,
so suppressing the delta here avoids the phantom login acquisition (#237).

#### RUNE_POUCH_QUANTITY_VARBITS

`private static final int[] RUNE_POUCH_QUANTITY_VARBITS`

#### RUNE_POUCH_TYPE_VARBITS

`private static final int[] RUNE_POUCH_TYPE_VARBITS`

#### RUNE_POUCH_VARBITS

`private static final ImmutableSet<Integer> RUNE_POUCH_VARBITS`

#### SECTION_SLOT_KEYS

`private static final Set<String> SECTION_SLOT_KEYS`

#### STOCKPILE_GE_SPRITE_ID

`private static final int STOCKPILE_GE_SPRITE_ID`

Custom sprite-override id for the Stockpile icon shown on the GE "View in Stockpile" button (#140).

#### TRACKED_CONTAINERS

`private static final ImmutableSet<Integer> TRACKED_CONTAINERS`

#### TRADE_OTHER_CONTAINER

`private static final int TRADE_OTHER_CONTAINER`

The partner-side trade container: the offer container id with the "other player" bit set.

#### WHATS_NEW_WINDOW

`private static final Duration WHATS_NEW_WINDOW`

How long after first launching a new release the "What's New" indicator stays highlighted.

#### categories

`private final List<CategoryState> categories`

Ordered user-defined categories (names + collapsed state); the source of truth for category order.

#### changelog

`private Changelog changelog`

Bundled release notes, parsed once at startup; the newest entry is the current version.

#### client

`private Client client`

#### clientThread

`private ClientThread clientThread`

#### clientToolbar

`private ClientToolbar clientToolbar`

#### config

`private StockpileConfig config`

#### configManager

`private ConfigManager configManager`

#### containerCounts

`private final Map<Integer,Map<Integer,Integer>> containerCounts`

#### currentGeItem

`private int currentGeItem`

The item shown on the currently-open GE offer screen, or -1 when no offer screen is up (GE integration).

#### doseSwapClaimedIds

`private final Set<Integer> doseSwapClaimedIds`

Ids claimed by this tick's dose-swap pass, so the XP-less combine detector skips a decant/consume (#231).

#### executor

`private ScheduledExecutorService executor`

#### favoritesCollapsed

`private boolean favoritesCollapsed`

#### gatherXpTick

`private int gatherXpTick`

The tick of the most recent gathering-skill XP gain, marking a gain as a free gather (#213).

#### geButton

`private Widget geButton`

The native-style button injected onto the GE offer screen in Button mode, or null.

#### geLineHigh

`private long geLineHigh`

High and low market prices for `#geLineItem` from the resolved source; 0 when unavailable (#142).

#### geLineItem

`private int geLineItem`

The raw GE item id the cached 5m high/low belong to, or -1 when unfetched or stale (#142).

#### geLineLow

`private long geLineLow`

#### geLineSource

`private String geLineSource`

Which source `#geLineHigh`/`#geLineLow` came from, as a row-label prefix (5m/1h/Latest) (#142).

#### geLoginTick

`private int geLoginTick`

#### geTrackButton

`private Widget geTrackButton`

The Track/Untrack button's beige chrome (a BUTTON_BROWN graphic) beside GE History, or null (#139).

#### geTrackLabel

`private Widget geTrackLabel`

The dark text label riding on `#geTrackButton`; carries the Track/Untrack text (#139).

#### groundItems

`private final Map<TileItem,Tile> groundItems`

#### groundOverlay

`private StockpileGroundOverlay groundOverlay`

#### gson

`private Gson gson`

#### highlightOverlay

`private StockpileHighlightOverlay highlightOverlay`

#### itemManager

`private ItemManager itemManager`

#### itemMappings

`private volatile Map<Integer,WikiRealtimePriceClient.ItemMapping> itemMappings`

#### lastPortfolioSave

`private Instant lastPortfolioSave`

#### lastPriceCacheSave

`private Instant lastPriceCacheSave`

When the price cache was last written, to throttle per-refresh saves.

#### lastPriceRefresh

`private Instant lastPriceRefresh`

#### lastSkillXp

`private final Map<Skill,Integer> lastSkillXp`

Per-skill XP as last seen, so a StatChanged can be classified as a real XP gain.

#### ledger

`private CostBasisLedger ledger`

The cost-basis / GE trade ledger (#255); this plugin is its `LedgerHost` seam.

#### magicXpTick

`private int magicXpTick`

The tick of the most recent Magic XP gain, marking removed runes as burned by a spellcast (#235).
Tracked separately from `#processingXpTick` because Magic is also a processing skill: runes
consumed on a Magic tick are the cast's fuel, but runes consumed on a Runecraft tick (earth runes
into lava runes) are a genuine recipe input whose basis belongs on the product.

#### mappingsLoaded

`private volatile boolean mappingsLoaded`

#### myDrops

`private final Map<TileItem,Integer> myDrops`

Ground items this player dropped: the `TileItem` → how many of its units are ours.

#### myTradeOffer

`private final Map<Integer,Integer> myTradeOffer`

Latest captured trade-offer sides (canonical id → qty), read when the trade completes (#66).

#### navButton

`private NavigationButton navButton`

#### notifier

`private Notifier notifier`

#### overlayManager

`private OverlayManager overlayManager`

#### panel

`private StockpilePanel panel`

#### pendingItemDeltas

`private final Map<Integer,Integer> pendingItemDeltas`

#### pendingQuantitySync

`private boolean pendingQuantitySync`

#### pendingRebuild

`private final AtomicReference<Runnable> pendingRebuild`

The newest un-rendered panel snapshot; non-null means a rebuild drainer is already queued.

#### persistence

`private StockpilePersistence persistence`

Client-free persistence layer (#111); built in `#startUp()` once gson/config are injected.

#### portfolioHistory

`private final PortfolioHistory portfolioHistory`

Per-item thinned time series of portfolio value/cost for the history chart.

#### previewItem

`private TrackedItem previewItem`

Transient, non-persisted item backing the read-only detail preview (view-only
button); not in `#trackedItems`.

#### priceRefreshTask

`private ScheduledFuture<?> priceRefreshTask`

#### processingXpTick

`private int processingXpTick`

The tick of the most recent processing-skill XP gain, pairing recipe inputs to outputs.

#### rewardContainerTick

`private int rewardContainerTick`

The tick a reward/loot container last changed, marking a matching inventory gain as a free reward (#215).

#### runePouchCounts

`private final Map<Integer,Integer> runePouchCounts`

#### runePouchDirty

`private boolean runePouchDirty`

Set when a rune pouch varbit changes; the diff is deferred to `#onClientTick` so every
type/quantity varbit for the change has settled before it is read (#237).

#### runePouchSeenSinceLogin

`private boolean runePouchSeenSinceLogin`

#### screenOverlays

`private final List<StockpileScreenOverlay> screenOverlays`

One independently-draggable overlay box per slot; they start grouped in the same snap corner.

#### seenContainersSinceLogin

`private final Set<Integer> seenContainersSinceLogin`

#### sessionInitialized

`private boolean sessionInitialized`

Whether the current logged-in session has been initialised. Guards the one-time
clear+reload so a respawn or region load re-firing `LOGGED_IN` mid-session
doesn't wipe pending quantity changes (e.g. a death loss) or reset held state (#70).
Set by whichever path initialises the session: the `LOGGED_IN` handler, or
`startUp` when the plugin is enabled while already logged in — the two do the
same load, so leaving the flag false there let the next region crossing re-clear.

#### shopOpen

`private boolean shopOpen`

Whether an NPC shop interface is open, gating the coin-delta shop pricing (#67).

#### theirTradeOffer

`private final Map<Integer,Integer> theirTradeOffer`

#### thievingXpTick

`private int thievingXpTick`

The tick of the most recent Thieving XP gain, marking a gain as free stolen loot (#217).

#### tickGroundDespawns

`private final List<ItemDespawned> tickGroundDespawns`

#### tickGroundQuantityChanges

`private final List<ItemQuantityChanged> tickGroundQuantityChanges`

#### tickGroundSpawns

`private final List<ItemSpawned> tickGroundSpawns`

This tick's ground spawns/despawns/stack changes, correlated against the inventory deltas (#65).

#### trackedItems

`private final Map<Integer,TrackedItem> trackedItems`

#### uncategorizedCollapsed

`private boolean uncategorizedCollapsed`

#### wikiPriceClient

`private WikiRealtimePriceClient wikiPriceClient`

### Method Detail

#### addTrackedItem

`private void addTrackedItem(int itemId)`

Tracks an item by id with defaults (full tracking mode, no preset cost basis).

#### addTrackedItem

`private void addTrackedItem(int itemId, TrackItemMode mode)`

Tracks an item by id in the given mode, routing `TrackItemMode#VIEW` to a read-only preview instead.

#### addTrackedItem

`private void addTrackedItem(int itemId, int initialQuantity, List<AcquisitionRecord> records, List<NotificationRule> notifications, boolean notificationsInitialized, boolean costBasisInitialized, boolean syncOnAdd, boolean persistOnAdd, TrackItemMode mode)`

Canonical add: creates a `TrackedItem` (resolving its name/tradeable
flag from the item composition), seeds its quantity, acquisitions, and
notifications, registers it, and persists/refreshes. No-op if already
tracked. Runs on the client thread.

- **Parameter** `initialQuantity` — starting count
- **Parameter** `records` — preset acquisition lots, or `null`
- **Parameter** `notifications` — preset notification rules, or `null`
- **Parameter** `notificationsInitialized` — whether default rules have already been seeded
- **Parameter** `costBasisInitialized` — whether a cost basis has already been established
- **Parameter** `syncOnAdd` — recount from containers immediately when in TRACK mode
- **Parameter** `persistOnAdd` — persist the tracked list after adding; the persisted-load
                              replay passes `false`, both because the data came from
                              config unchanged and because persisting mid-replay would write
                              the item before its deferred grouping/death-suspension
                              callbacks have applied, stripping those fields
- **Parameter** `mode` — tracking vs. view-only

#### addTrackedItem

`private void addTrackedItem(int itemId, int initialQuantity, List<AcquisitionRecord> records, boolean costBasisInitialized)`

Tracks an item with a preset quantity and acquisition history (e.g. a restore), using default notifications.

#### applyAutoCategorize

`private void applyAutoCategorize(boolean includeCategorized)`

Applies auto-categorization on the client thread: classify each in-scope item, create categories, assign.

#### applyGeHighLowLine

`private void applyGeHighLowLine()`

Swaps the "Actively traded price" text inside the open GE offer's info block (the single
`SETUP_DESC`/`DETAILS_DESC` text widget) for one compact market line — High, Low
and Avg together — in place, so the line count never changes and nothing else moves (#142).
Re-applied each tick so the game's own redraw does not win; idempotent because once the native
text is gone the rewrite is skipped. No-op until the shown item's data has been fetched and priced.

#### applyGePrices

`private void applyGePrices(Map<Integer,WikiRealtimePriceClient.ItemPrices> all)`

Applies freshly fetched prices to every tracked item: records per-side
deltas against the previous values, updates the LIVE window stats, seeds a
cost basis on first successful price if one wasn't set, then re-evaluates
notifications and refreshes the panel (including the open detail view).
A failed (empty) fetch only triggers a plain refresh.

#### applyGeTrackLabel

`private void applyGeTrackLabel()`

Sets the Track/Untrack text, action, and resting colour (green/red) from the offer's tracked state.

#### applyImportedList

`private String applyImportedList(String token)`

Decodes and merges a share code on the client thread. @return the user-facing outcome summary

#### applyItemMetadata

`private void applyItemMetadata(TrackedItem tracked)`

Copies cached GE metadata (buy limit, value, alch values) onto an item, if available.

#### applyLivePrices

`private void applyLivePrices(TrackedItem item, WikiRealtimePriceClient.ItemPrices prices)`

Applies a freshly fetched price set to an item: records per-side deltas, updates
current prices, and refreshes its LIVE window stats.

#### applyPersistedDeathSuspension

`private void applyPersistedDeathSuspension(int itemId, int quantity, Long suspendedAtEpoch)`

Restores a persisted death suspension after its item has been added, so a
recovery spanning a relog still un-suspends instead of opening new lots.
Client-thread-deferred like `#applyPersistedGrouping`.

#### applyPersistedGrouping

`private void applyPersistedGrouping(int itemId, boolean favorite, String category, boolean onOverlay, boolean compact)`

Applies a persisted item's favorite/category/overlay/compact grouping after it has been added.
Enqueued on the client thread so it runs after the matching `#addTrackedItem`
body (which is itself client-thread-deferred), guaranteeing the item exists.

#### applyPersistedPouchSuspension

`private void applyPersistedPouchSuspension(int itemId, int quantity)`

Restores a persisted fur/meat-pouch suspension after its item has been added, so
furs "Fill"ed in before a logout still un-suspend (keeping their original source
and basis) when the pouch is emptied in a later session (#214).
Client-thread-deferred like `#applyPersistedGrouping`.

#### autoCategorize

`String autoCategorize(boolean includeCategorized)`

Auto-assigns tracked items to wiki-derived categories (see `ItemCategoryClassifier`),
creating any missing categories. Non-destructive unless `includeCategorized` is set:
by default only uncategorized items are touched, so manual assignments are preserved. The
mutation runs on the client thread; the returned message reports the outcome.

- **Returns:** a user-facing summary of how many items were categorized

#### breathingAlpha

`float breathingAlpha()`

- **Returns:** the current highlight alpha, a sine "breathing" pulse whose period depends on the glow speed config.

#### buildAcquisitionsCsv

`void buildAcquisitionsCsv(Consumer<String> onResult)`

Builds the acquisitions log of all tracked items as CSV (see
`AcquisitionCsvExporter`) on the client thread — the items and their
acquisition lists are client-thread state — and hands it to `onResult`
on the EDT.

#### buildShareToken

`void buildShareToken(Consumer<String> onResult)`

Builds a shareable code for the current tracked list (ids, modes, categories,
favorites) — "" when empty — and hands it to `onResult` on the EDT.
`trackedItems` is client-thread state, so the snapshot is taken there
rather than on the EDT the panel's button handler runs on, where a concurrent
mutation (login replay, auto-add, GE fill) could tear the iteration.

#### canonicalCountId

`private int canonicalCountId(int itemId)`

Resolves a container slot's item id to the canonical (unnoted, non-placeholder) id it should count
as, using a single `ItemComposition` lookup instead of a separate placeholder-check +
`canonicalize` pair (#185) — a bank event covers ~800 slots. Mirrors
`net.runelite.client.game.ItemManager#canonicalize(int)`; a bank placeholder variant returns
-1 because a placeholder must never count as held quantity.

- **Returns:** the canonical id to count, or -1 for an empty slot or a placeholder. Client thread only.

#### captureTradeOffer

`private void captureTradeOffer(Map<Integer,Integer> side, ItemContainer container, boolean mine)`

Snapshots one side of the trade window (canonical id → quantity) as its container
changes. For our own side, diffs the new offer against the previous snapshot and
queues the change so the matching inventory removal suspends (rather than closes) the
offered lots, and a later withdrawal un-suspends them (#66).

#### categoryValue

`private String categoryValue(TrackedItem item, NotificationMetric metric)`

Resolves the current categorical rating of a metric for an item
(volatility, liquidity, or 30-day range position) via `MarketClassifier`.

- **Returns:** the rating label, or `null` when it can't be classified

#### claimReceivedItems

`private void claimReceivedItems(Map<Integer,Integer> side, long gp)`

Claims received items as buys at the apportioned per-unit price, matched by their inventory additions.

#### clearAcquisitions

`private void clearAcquisitions(int itemId)`

Clears an item's acquisition lots (resetting its cost basis) and persists/refreshes.

#### clearAllTrackedItems

`private void clearAllTrackedItems()`

Removes every tracked item, then persists and refreshes. Runs on the client thread.

#### clearPortfolioHistory

`private void clearPortfolioHistory()`

Wipes the portfolio value history (in memory and in config), e.g. when the whole tracked list is cleared.

#### closeAllGroundSuspensions

`private void closeAllGroundSuspensions()`

Closes every remaining ground suspension as lost (delegating to the ledger) and clears our own drop tracking.

#### closeGivenItems

`private void closeGivenItems(Map<Integer,Integer> side, long gp)`

Closes given items as sells at the apportioned per-unit price, realizing them against the trade
suspension taken when they were offered. Any leg whose suspension has not landed yet — a same-tick
offer+accept where "Accepted trade." outran the offer's inventory decrease — is parked and retried
after the container sync, exactly as the GE sell path does, so the sale is never dropped (#175).

#### colourGp

`private static String colourGp(long value, String colour)`

- **Returns:** a full grouped `"1,234 gp"` in the given colour, or a muted dash when unpriced (#142).

#### correlateCombine

`private void correlateCombine()`

Pairs an XP-less combine — a tick that consumes one or more ingredients and produces a single
tradeable output with no skill XP and no coin movement — as `AcquisitionSource#PROCESSING`,
so the ingredients' cost basis carries onto the product instead of both sides falling to
Unknown at market value (#231). Handles the class of "mix"/combine recipes that grant no XP,
such as combining a Sunlight moth with Raw pyre fox meat into a Sunlight moth mix.

<p>Runs after `#correlateDecant` and reuses its `#pairProcessingRecipe` basis
transfer. The XP-gated `#correlateProcessing` already claims recipes that emit XP, and
a destroyed output (`#isDestroyedProduct`) is claimed there before the XP gate, so both
are excluded here. A dose swap (decant/consume-down) is also a no-XP single-output tick, so any
id claimed by the dose-swap pass (`doseSwapClaimedIds`) is skipped, and a finished-potion
tick — where the freed vessel is the only gain — is left to the empty-container byproduct path.
The output must be tracked; an untracked product gives nothing to carry basis onto and would only
risk mislabelling an unrelated inventory shuffle. Gated by the Source-Based Pricing toggle.

#### correlateDecant

`private void correlateDecant()`

Pairs a dose family's consumed lots with the doses it produces on a single XP-less tick, so
cost basis follows the liquid across the item-id change rather than being realized as a sale.
Groups the tick's non-coin deltas into dose families (`DoseFamily`) and hands each family
to `#correlateDoseSwapFamily`, which distinguishes two cases:
<ul>
  <li><b>Decant</b> (#220) — consumed doses equal produced doses: a pure swap, so the combined
      input basis is distributed dose-weighted (`DecantBasis`) onto the produced ids under
      `AcquisitionSource#DECANT`. Up, down, and mixed-basis inputs all merge.</li>
  <li><b>Consume-down</b> (#218) — a dose is drunk (consumed doses exceed produced doses, but
      some remain): the <em>full</em> input basis follows onto the lower-dose id under
      `AcquisitionSource#CONSUMED`, since using a dose realizes no profit or loss.</li>
</ul>
Both queue the carried basis in `pendingDecantOutput` / `pendingConsumedOutput` so the
matching gain opens the output lot carrying it; both close the consumed lots at their FIFO cost
(no P/L). Untracked inputs contribute their fallback value; untracked outputs drop their share.
Runs after `#correlateProcessing` — which a processing-XP tick handles instead — and before
the source detectors, whose gains skip any id already queued in `pendingProcessingOutput`,
`pendingDecantOutput`, or `pendingConsumedOutput`. Gated by the Source-Based Pricing toggle.

#### correlateDoseSwapFamily

`private void correlateDoseSwapFamily(List<int[]> members)`

Applies the dose-family basis transfer to one family's members (`{id, delta, doses`}):
a dose-conserving swap (consumed doses equal produced doses) is a <b>decant</b> under
`AcquisitionSource#DECANT`; a swap that loses doses while leaving some (a dose drunk) is
a <b>consume-down</b> under `AcquisitionSource#CONSUMED`. Anything else — no consumption,
or every dose gone (a last dose drunk, left to the `CostBasisLedger#applyDelta` loss path) — is skipped.
The consumed lots close at their FIFO cost and the summed basis is distributed onto the produced
ids: dose-weighted for a decant, but in full for a consume-down since a used dose is not a loss.

#### correlateGathering

`private void correlateGathering()`

Attributes this tick's unclaimed inventory gains to `AcquisitionSource#GATHER` at
0 when a gathering-skill XP drop (Hunter, Mining, Fishing, Woodcutting, Farming) marks
them as gathered from the world at no cost (#213) — Sunfire splinters, antlers, ores,
fish, logs, harvested herbs. Runs after `#correlateProcessing` (so a paired recipe
output, already queued in `pendingProcessingOutput`, is skipped and keeps its
transferred basis) and before the quantity sync consumes the deltas. A gain with no
gathering XP this tick stays unclaimed and takes the unknown-source path. Coins never
participate. Gated by the Source-Based Pricing toggle.

<p>Yields to `#correlateReward`: when a reward-loot signal (`#rewardContainerTick`)
fired this tick, the gains are reward loot, not gathered — some reward interactions (e.g. the
Tempoross reward pool) also grant gathering XP on the same tick, which would otherwise let a
GATHER claim win the FIFO over the correct REWARD one (#215).

#### correlateGroundActivity

`private void correlateGroundActivity()`

Correlates this tick's ground-item activity with the pending inventory deltas (#65):
a spawn (or stack increase) on the player's tile matching a pending removal is our
drop — its units queue for ground suspension and the `TileItem` is remembered;
a despawn of a remembered drop with no matching pickup closes its units as lost at 0;
a despawn matching a pending addition that isn't ours is a loot pickup, claimed as a
`AcquisitionSource#GROUND` acquisition at 0. Runs before the quantity sync
consumes the deltas.

#### correlateGroundGain

`private void correlateGroundGain(TileItem item, Tile tile, int gained, WorldPoint myLocation)`

Handles a ground pile gaining units: on our tile against a pending removal, it's our
drop. Gated by the Source-Based Pricing toggle — when off, no new ground suspensions
are taken, so a drop closes classically at the average price; drops suspended while
the toggle was on still resolve through the un-suspend/lost paths.

#### correlateGroundTaken

`private boolean correlateGroundTaken(TileItem item, int taken)`

Handles a ground pile losing units: a remembered drop with a matching pending
addition is a re-pickup (the greedy un-suspend consumes it during the sync);
with no matching addition its units close as lost at 0. An unfamiliar pile
matching a pending addition is a loot pickup, claimed as `GROUND` at 0.

#### correlateProcessing

`private void correlateProcessing()`

Pairs this tick's consumed inputs with the produced output when a processing-skill
XP gain identifies a recipe action (#69), transferring the summed input cost: tracked
inputs contribute (and close at) their FIFO open-lot cost, untracked inputs their
fallback market value, and the total is carried onto the output's new lot(s) by
`CostBasisLedger` so their basis sums exactly to it. Multi-output ticks
are unattributable and left to the fallback; tracked inputs with no tracked output
close at 0. A worthless, non-tradeable output is a destroyed product and is handled
without an XP signal — a burn or crush gives none — closing each tracked input as a
realized loss at 0 (#144): a crushed gem tags the input `AcquisitionSource#CRUSHED`,
any other destroyed product `AcquisitionSource#BURNED`. When the player tracks the
destroyed byproduct itself, its gain is booked at 0-cost under that same source (#172).
Coins never participate.

<p>Runes removed on a Magic XP tick are the cast's fuel rather than one of its ingredients,
so they are claimed as `AcquisitionSource#CAST` at 0 and never enter the input set (#235):
a superheated bar carries the ore's basis alone, and a combat spell leaves no inputs to pair.
A cast that yields no item at all is not a recipe either, so its remaining inputs are left
unclaimed rather than booked as processing — an alched item is sold for coins, not processed,
and belongs to the `AcquisitionSource#ALCHEMY` claim or the fallback path.

#### correlateReward

`private void correlateReward()`

Claims this tick's tracked inventory gains as a free `AcquisitionSource#REWARD` at 0
when a reward-loot signal fired on the same tick (`#rewardContainerTick`) — a reward/loot
container change (`#REWARD_CONTAINERS`), a Huntsman's loot-sack open, or a "you found some
loot" chat line — i.e. loot taken from a raids chest, clue casket, reward pool or similar (#215).
Takes precedence over `#correlateGathering`, which yields when this signal is present so a
coincident gathering-XP tick can't mislabel the loot. Runs before the quantity sync consumes the
deltas; a paired recipe output already queued in `pendingProcessingOutput` is skipped and
keeps its transferred basis. A gain with no reward signal this tick stays unclaimed and takes the
unknown-source path. Coins never participate. Gated by the Source-Based Pricing toggle.

#### correlateThieving

`private void correlateThieving()`

Attributes this tick's unclaimed inventory gains to `AcquisitionSource#THIEVING` at
0 when a Thieving XP drop marks them as stolen at no cost (#217) — pickpocket loot, stall
produce, chest hauls. An exact mirror of `#correlateGathering`: it runs after
`#correlateReward` (so reward loot keeps its REWARD claim) and before the quantity sync
consumes the deltas; a paired recipe output already queued in `pendingProcessingOutput`
is skipped and keeps its transferred basis. A gain with no Thieving XP this tick stays
unclaimed and takes the unknown-source path. Coins never participate. Gated by the
Source-Based Pricing toggle.

<p>Yields to `#correlateReward`: when a reward-loot signal (`#rewardContainerTick`)
fired this tick, the gains are reward loot, not stolen, so a coincident Thieving-XP tick can't
mislabel them (precedence: Processing &gt; Reward &gt; Gather/Thieving &gt; Unknown).

#### createCategory

`private void createCategory(String name)`

Creates a new category (ignoring blanks and case-insensitive duplicates), then persists and refreshes.

#### currentGeOfferItem

`private int currentGeOfferItem()`

- **Returns:** the item shown on the visible GE offer setup/details screen, or -1 when none is open.

#### currentTick

`public int currentTick()`

Returns the current client game tick.

- **Returns:** the tick count

#### deleteCategory

`private void deleteCategory(String name)`

Deletes a category, moving its items to Uncategorized, then persists and refreshes.

#### detectVersionChange

`private void detectVersionChange()`

Detects a new plugin version by comparing the changelog's current version to the
last-seen version in config. On a change, restamps the first-seen time and re-arms
the "What's New" indicator so late updaters still get their week.

#### ensureCategory

`private void ensureCategory(String name, boolean collapsed)`

Adds a category by name if one with that name doesn't already exist (case-insensitive).

#### evaluateNotifications

`private void evaluateNotifications()`

Evaluates every item's notification rules and fires the configured notifier
for any that are met. A once rule is removed after firing; a repeat rule stays
and re-arms edge-triggered — it fires again only after its condition has gone
false and come back true, and the first evaluation after a (re)load primes it
without firing. Skipped when notifications are disabled or being edited.

#### evaluateRule

`private Boolean evaluateRule(TrackedItem item, NotificationRule rule)`

Evaluates a single rule against an item.

- **Returns:** `TRUE`/`FALSE` for the condition, or `null` when it
        can't be evaluated yet (incomplete rule or missing/unparseable data)

#### examineFor

`private String examineFor(int itemId)`

- **Returns:** the examine text for the given item id from the wiki mapping, or
        `null` when the item isn't GE-tradeable or the mapping hasn't
        loaded yet

#### fallbackPricing

`public FallbackPricing fallbackPricing()`

Returns the configured fallback-pricing policy for unknown-source changes.

- **Returns:** the fallback-pricing mode

#### fetchItemMappings

`private void fetchItemMappings()`

Fetches GE item metadata in the background, keeping the previous map on failure.

#### findGeCloseButton

`private Widget findGeCloseButton()`

Locates the GE window's close (X) button by walking to the top-level ancestor of the open offer
container and searching its subtree for a visible widget with a "Close" action (#139). Confined
to the GE window's toplevel so it doesn't match some other interface's close button.

#### flushRunePouchDelta

`private void flushRunePouchDelta()`

Diffs settled rune pouch contents once per tick. Debouncing to the tick (rather than diffing
per varbit event) means every type/quantity varbit for a change has landed before the read, so
a half-populated snapshot can no longer book a phantom acquisition (#237). The first settled
read after login — and any empty→full read inside `#RUNE_POUCH_LOGIN_GRACE_TICKS` — only
establishes the baseline, since a login must produce no pouch delta.

#### getGroundItems

`Map<TileItem,Tile> getGroundItems()`

- **Returns:** the live map of on-screen ground items to their tiles (used by the ground overlay).

#### getItemIdFromMenuEntry

`private int getItemIdFromMenuEntry(MenuEntry entry)`

- **Returns:** the item id behind a menu entry (ground item or inventory/bank widget), or -1 if none.

#### getOverlayItems

`List<TrackedItem> getOverlayItems()`

- **Returns:** the tracked items shown on the overlay (in tracked order), capped at `#OVERLAY_MAX`.

#### hideGeButton

`private void hideGeButton()`

Hides and forgets the injected GE button, if one is currently on the offer interface.

#### hideGeTrackButton

`private void hideGeTrackButton()`

Hides and forgets the injected Track/Untrack button, if one is currently on the offer interface (#139).

#### hydratePriceCache

`private void hydratePriceCache()`

Hydrates tracked items from the persisted price cache so the panel shows
last-known values (dimmed by the existing staleness treatment once their trade
times age past the threshold) instead of placeholders. Live fetches simply
overwrite these; items that already have prices are never touched. Runs on the
client thread after the persisted items have been restored — enqueued from both
initialization paths, since at startUp on the login screen the RS-profile config
isn't available yet and only the `LOGGED_IN` load can hydrate.

#### importTrackedList

`void importTrackedList(String token, Consumer<String> onResult)`

Merges a shared tracked-list code into the current profile: adds items that
aren't already tracked (with their mode, category, and favorite flag) plus any
missing categories they reference. Non-destructive — existing items are left
untouched. Decode, count, and merge all run on the client thread (the counts
read `trackedItems`); the outcome summary is handed to `onResult`
on the EDT.

#### inAutoCategorizeScope

`private boolean inAutoCategorizeScope(TrackedItem item, boolean includeCategorized)`

- **Returns:** whether the item is in scope: always when re-categorizing, otherwise only when uncategorized.

#### injectGeButton

`private void injectGeButton()`

Injects the Stockpile icon as a "View in Stockpile" button onto the visible GE offer container
(#140). The icon-only graphic sits where the old text link did; the "View in Stockpile" text now
lives on the hover action/tooltip. Clicking opens the offer's item in Stockpile's detail view;
hover brightens the icon to full opacity.

#### injectGeTrackButton

`private void injectGeTrackButton()`

Injects a Track/Untrack button in the GE window's title bar, immediately left of the close (X)
button (#139): a muted-orange outline box framing bold Track/Untrack text (the "Grand Exchange"
header font/weight) whose colour reflects the tracked state. The close button is located at
runtime so the button sits in the same section and row as the X, not in the offer content.
The text box is inset 3px inside the border so the label clears the outline.

#### injectPriceLines

`private String injectPriceLines(String desc)`

Swaps the "Actively traded price: N" segment of the GE info-block for the two market lines,
always on their own rows: a leading "Buy limit: N /" that RuneLite inlines on buy offers is
split off onto its own line, and any trailing convenience-fee line is kept. Returns the text
unchanged when there is no native segment to replace, leaving an already-rewritten block
alone (#142).

- **Parameter** `desc` — the current info-block text (may be null)
- **Returns:** the rewritten text, or the original when nothing was replaced

#### isAmmo

`private boolean isAmmo(int itemId)`

- **Returns:** whether `itemId` is ammo of either kind — destroyed-on-use or recoverable (#234). Ammo
        is fuel for a shot, never a recipe input, so it must never be booked as a processing loss;
        `#correlateProcessing` uses this to keep darts loaded into a blowpipe (a charged variant
        reads as a destroyed product) off the `AcquisitionSource#BURNED` path. Client thread only.

#### isConsumable

`public boolean isConsumable(int itemId)`

- **Returns:** whether `itemId` is a single-use consumable (food, a potion dose) by
        `ItemCategoryClassifier` category — an unclaimed removal of one is booked as
        used up at 0 rather than an avg-price Unknown sale (#218). Ammo and runes are
        excluded; see `#CONSUMABLE_CATEGORIES`. Client thread only.

#### isDestroyedAmmo

`public boolean isDestroyedAmmo(int itemId)`

- **Returns:** whether `itemId` is ammo destroyed on use — a cannonball or a thrown chinchompa —
        matched by `#DESTROYED_AMMO_TOKENS` name token. An unclaimed removal of one closes at 0
        under `AcquisitionSource#DESTROYED` rather than suspending on the ground path (#234).
        Client thread only.

#### isDestroyedProduct

`private boolean isDestroyedProduct(int itemId)`

- **Returns:** whether `itemId` is a worthless destroyed processing product — a
non-tradeable item (absent from the GE mapping) with no market value, such as burnt
food or a crushed gem. Requires the mapping to have loaded so a genuine tradeable
item is never mistaken for one before its price is known.

#### isDosePotion

`private boolean isDosePotion(int itemId)`

- **Returns:** whether `itemId` is a dosed potion — its name carries a trailing dose count
        (`DoseFamily`) — so an "Empty" click on it can be distinguished from the same
        option on a jug, bird nest, or hunting pouch, which do not parse as a dose family (#232).
        Client thread only.

#### isEmptyContainer

`public boolean isEmptyContainer(int itemId)`

Returns whether the given item id is a known empty-container placeholder.

- **Parameter** `itemId` — the item id
- **Returns:** `true` if the id is an empty container (e.g. an empty vial)

#### isPouchDepositMessage

`private static boolean isPouchDepositMessage(String message)`

- **Returns:** whether a chat line signals a hunting pouch emptying into the bank — either the
        per-pouch "Empty" deposit ("You deposit some &lt;fur/meat&gt; into your bank.") or
        the bank's "Empty containers" button. Only pouch emptying produces these lines; a
        normal manual bank deposit is silent, so there is no false positive (#214).

#### isPouchTarget

`private static boolean isPouchTarget(String target)`

- **Returns:** whether a "Fill" menu target names a fur/meat hunting pouch (any size) (#214).

#### isRealItem

`private boolean isRealItem(int itemId)`

- **Returns:** whether `itemId` resolves to a real, defined item. Empty widget
slots are backed by placeholder items (e.g. id 6512) whose composition name is
the literal string "null"; those must not open a preview.

#### isRecoverableAmmo

`public boolean isRecoverableAmmo(int itemId)`

- **Returns:** whether `itemId` is recoverable ranged or thrown ammo — an arrow, bolt, dart or javelin
        in the `#AMMO_CATEGORY`, or a knife/throwing axe by `#RECOVERABLE_WEAPON_TOKENS` —
        that lands on the target's tile when fired. A fired unit suspends on the ground path with its
        basis intact instead of closing, so picking it back up nets to nothing (#234). Destroyed ammo
        is excluded; see `#isDestroyedAmmo`. Client thread only.

#### isSpellcastRune

`private boolean isSpellcastRune(int itemId)`

- **Returns:** whether `itemId` is a rune being burned by a spellcast this tick (#235) — a rune
        by `ItemCategoryClassifier` category, removed within a tick of a Magic XP gain.
        Such runes are the cast's fuel, never a recipe input: they close at 0 under
        `AcquisitionSource#CAST` and their cost never transfers onto the spell's product.
        Runes removed on a Runecraft tick (earth runes crafted into lava runes) fail the Magic
        test and stay ordinary processing inputs. Client thread only.

#### isTracked

`boolean isTracked(int itemId)`

- **Returns:** whether the given (canonical) item id is currently tracked.

#### isTradeCurrency

`private static boolean isTradeCurrency(int itemId)`

- **Returns:** whether the item is trade currency — coins or platinum tokens — which
        forms the trade's gp numerator rather than a lot-bearing item leg

#### isWhatsNew

`private boolean isWhatsNew()`

- **Returns:** whether the indicator should read "What's New" — within a week of first launch and not dismissed.

#### itemInGeContainer

`private int itemInGeContainer(int componentId)`

- **Returns:** the first item id found in the given GE container's subtree, or -1 when hidden/absent.

#### latestSeriesHighLow

`static long[] latestSeriesHighLow(List<WikiRealtimePriceClient.PricePoint> series)`

Scans a price series newest-first for the most recent priced average high and low,
returned as `[high, low]` (each 0 when the series holds no priced sample) (#142).

- **Parameter** `series` — the price points, oldest first (may be null or empty)
- **Returns:** a two-element array of the latest non-zero high and low

#### loadCategories

`private void loadCategories()`

Restores the category definitions and group collapsed state from per-profile JSON.

#### loadPersistedItems

`private void loadPersistedItems()`

Restores tracked items from the per-profile JSON written by `#persistTrackedItems()`.

#### loadPortfolioHistory

`private void loadPortfolioHistory()`

Restores the per-item portfolio history from per-profile config, ignoring a corrupt
value. The pre-#152 aggregate format (a JSON array rather than an object) can't be
split per item, so it is discarded — history simply rebuilds from the next snapshot.

#### lookupItem

`private TrackedItem lookupItem(int itemId)`

- **Returns:** the tracked item for `itemId`, or the transient preview item when it
        matches; otherwise `null`

#### markWhatsNewSeen

`private void markWhatsNewSeen()`

Persists that the user has seen the current release's "What's New", quieting the indicator.

#### marketUnitValue

`private long marketUnitValue(int itemId)`

- **Returns:** an item's unit market value for apportionment weights: the tracked avg, or the wiki price.

#### mergeImportedList

`private void mergeImportedList(List<PortfolioShareCodec.Entry> entries, List<CategoryState> importedCategories)`

Applies a decoded tracked-list import on the client thread: categories first, then new items.

#### migrateAutoAddSetting

`private void migrateAutoAddSetting()`

One-time migration for #219: the old combined `autoAddItems` enum
(High/Low/Avg/Zero/Off) split into a boolean auto-add gate plus a separate
`FallbackPricing`. Rewrites a legacy enum name still stored under
`StockpileConfig#KEY_AUTO_ADD_ITEMS` as the boolean gate — Off becomes off,
every pricing value becomes on — and seeds `StockpileConfig#KEY_FALLBACK_PRICING`
from its pricing half (Off, which conflated the two and couldn't carry a pricing
choice, defaults to Avg). Idempotent: a value already migrated to a boolean, or a
fresh install with no value, is left untouched.

#### notificationText

`private String notificationText(TrackedItem item, NotificationRule rule)`

Builds the user-facing notification message, e.g. `"Stockpile: Coal - High >= 200"`.

#### numericValue

`private OptionalDouble numericValue(TrackedItem item, NotificationMetric metric, TimeWindow window)`

Resolves the current numeric reading of a metric for an item over a window
(price, volume, profit, HA profit, Δ% vs. the window average, or quantity).

- **Returns:** the value, or empty when the underlying data is missing or unreliable

#### onAcquisitionsEdited

`void onAcquisitionsEdited(int itemId)`

Callback after the user edits an item's acquisitions: re-derives its held quantity
from the lots and persists. Open lots also cover suspended units (in-flight GE
sells, trades, drops, deaths), which `quantity` must exclude — otherwise an
edit made mid-suspension would double-count the suspended units as held.

#### onActorDeath

`public void onActorDeath(ActorDeath event)`

Marks the local player's death, opening the death-loss suspension window (#70).

#### onChatMessage

`public void onChatMessage(ChatMessage event)`

Registers the completed trade's claims when the game confirms the exchange (#66).

#### onClientTick

`public void onClientTick(ClientTick event)`

Per-tick work: flushes any pending quantity sync, evaluates notifications,
and (when ground highlighting is on) reorders tracked items' "Take" menu
entries to the bottom so they don't get in the way of normal actions.

#### onConfigChanged

`public void onConfigChanged(ConfigChanged event)`

Reacts to this plugin's config changes: resolves detail-section slot
conflicts, reschedules the refresh when the interval changes, and otherwise
just repaints the panel. Ignores other plugins' groups.

#### onGameStateChanged

`public void onGameStateChanged(GameStateChanged event)`

Resets transient and per-login state on game-state transitions: clears
ground items on each load, and on login wipes the count caches and reloads
the persisted tracked items.

#### onGameTick

`public void onGameTick(GameTick event)`

Grand Exchange integration: each tick, detects the item on the open offer setup/details
screen and, per `StockpileConfig#geIntegration()`, either auto-opens it in Stockpile
or injects a "View in Stockpile" button. Only acts when the shown item changes.

#### onGrandExchangeOfferChanged

`public void onGrandExchangeOfferChanged(GrandExchangeOfferChanged event)`

Consumes GE offer progress to price trades and track the buy limit. Buy fills are
ledgered until the items are collected; a sell's placement suspends the offered units
and its fills realize them at the true price; a cancellation restores the remainder.

<p>Just after login the offer sync replays pre-existing offers here rather than at
container sync (whose offers array isn't populated yet). Within that window the state
is rebuilt via `CostBasisLedger#primeGeStateFromLogin()` and the events are swallowed so they
aren't replayed as fresh placements or fills.

#### onItemContainerChanged

`public void onItemContainerChanged(ItemContainerChanged event)`

Tracks per-container item counts as inventory/bank/etc. change, accumulating
the deltas to apply on the next client tick. The first sight of a container
after login only seeds a baseline (no deltas); seeing the bank can trigger a
full reconcile for auto-add.

#### onItemDespawned

`public void onItemDespawned(ItemDespawned event)`

Forgets a ground item once it despawns, buffering it for #65's pickup/lost-drop correlation.

#### onItemQuantityChanged

`public void onItemQuantityChanged(ItemQuantityChanged event)`

Buffers ground-stack quantity changes so drops onto an existing stack correlate like spawns (#65).

#### onItemSpawned

`public void onItemSpawned(ItemSpawned event)`

Records a ground item and its tile so the ground overlay can outline it, buffering it for #65.

#### onMenuOpened

`public void onMenuOpened(MenuOpened event)`

Adds a "Track Item" / "Stop Tracking" right-click option to item menu entries, when enabled.

#### onMenuOptionClicked

`public void onMenuOptionClicked(MenuOptionClicked event)`

Claims an upcoming High/Low Alchemy disposal (#68): casting either spell on a
tracked item registers an `AcquisitionSource#ALCHEMY` claim for one unit
at the coins the cast actually yields — the item's cached high/low alch value —
so the lot closes at the real proceeds instead of the current average. Casts on
items with no cached alch value stay unclaimed and take the unknown-source path.

#### onNotificationsEdited

`private void onNotificationsEdited(int itemId)`

Callback after the user edits an item's notification rules: just persists the change.

#### onRuneScapeProfileChanged

`public void onRuneScapeProfileChanged(RuneScapeProfileChanged event)`

Resets the session baseline when the RS profile (account) changes, so stats restart per account.

#### onStatChanged

`public void onStatChanged(StatChanged event)`

Marks the tick of processing-skill XP gains (recipe actions, #69), gathering-skill XP
gains (#213), and Thieving XP gains (#217).

#### onVarbitChanged

`public void onVarbitChanged(VarbitChanged event)`

Mirrors rune pouch contents (held in varbits, not a normal container) into
the quantity counts, accumulating deltas like a container change.

#### onWidgetClosed

`public void onWidgetClosed(WidgetClosed event)`

Clears GE-integration state when the offer interface closes, and shop state for #67.

#### onWidgetLoaded

`public void onWidgetLoaded(WidgetLoaded event)`

Forces the GE buttons to be re-injected against a freshly (re)built offer interface.

#### openGeItemInStockpile

`private void openGeItemInStockpile(int itemId)`

Opens the item in Stockpile's view-only preview, switching to/focusing the panel when configured.

#### openGeOffers

`public GrandExchangeOffer[] openGeOffers()`

Returns the player's current Grand Exchange offers.

- **Returns:** the open GE offer slots

#### orderGeneratedCategories

`private void orderGeneratedCategories(List<CategoryState> created)`

Orders an auto-categorize run's generated categories alphabetically after any
pre-existing (manually ordered) ones, then keeps "Other" at the very end.

#### overlayItemCount

`private int overlayItemCount()`

- **Returns:** how many tracked items are currently flagged for the on-screen overlay.

#### pairProcessingRecipe

`private void pairProcessingRecipe(List<int[]> inputs, int outputId, int outputQty, boolean trackedOutput)`

Closes a recipe's consumed inputs under `AcquisitionSource#PROCESSING` at their FIFO
open-lot cost and queues the summed basis in `pendingProcessingOutput` so the matching
gain opens the produced lot carrying it. Untracked inputs contribute their fallback value.
When the output is untracked there is nothing to carry the basis onto, so the inputs close at
0 and no output is queued. Shared by the XP-gated `#correlateProcessing` path and the
XP-less combine detector `#correlateCombine` (#231).

#### persistCategories

`private void persistCategories()`

Serializes the category definitions and group collapsed state to per-profile config.

#### persistPortfolioHistory

`private void persistPortfolioHistory()`

Serializes the per-item portfolio history to per-profile config.

#### persistPriceCache

`private void persistPriceCache()`

Writes every priced tracked item's current prices to the RS profile config.
Called throttled from refreshes and unconditionally at shutdown.

#### persistTrackedItems

`public void persistTrackedItems()`

Serializes the current tracked items (quantity, cost basis, notifications, grouping) to per-profile config.

#### portfolioHistoryPoints

`List<long[]> portfolioHistoryPoints()`

- **Returns:** the aggregated portfolio history points (`{epochSeconds, value, costBasis`}) for the chart.

#### previewItem

`private void previewItem(int itemId)`

Opens a read-only detail preview for an untracked item without adding it to
the tracked list or persisting anything. Builds a transient `TrackedItem`,
shows it in the detail view, then fetches its prices and history in the
background. Runs on the client thread.

#### priceLines

`private String priceLines()`

- **Returns:** one market row — High and Low together — coloured per side and prefixed with the
        resolved source (`5m`/`1h`/`Latest`) (#142).

#### promptCategoryForTrackedItem

`private void promptCategoryForTrackedItem(int itemId)`

After an item is explicitly tracked (#211), asks the panel to prompt for its category. Enqueued
on the client thread so it runs after the deferred add body, then hops to the EDT once the item
is confirmed present. Only reached from explicit tracking — never from load, import, or auto-add.

#### provideConfig

`StockpileConfig provideConfig(ConfigManager configManager)`

#### queueTradeSuspension

`private void queueTradeSuspension(Map<Integer,Integer> before, Map<Integer,Integer> after)`

Turns the change in our own offer into pending suspend/un-suspend intents: items added to
the offer left our inventory and should suspend, items withdrawn returned and should
un-suspend. Only tracked, non-currency items queue — coins and platinum tokens are the
trade's numerator, not a lot, and untracked items never flow through `CostBasisLedger#applyDelta`
to consume the intent.

#### rebucketScreenOverlays

`private void rebucketScreenOverlays()`

Removes and re-adds the screen overlays so the manager re-buckets them into their (config-driven) layer.

#### recomputeWindowStats

`private void recomputeWindowStats(TrackedItem tracked)`

Rebuilds an item's per-window `PriceStats` from its current prices (LIVE) and history series.

#### reconcileAllQuantities

`private void reconcileAllQuantities()`

Recounts every tracked item from scratch across all containers plus the rune
pouch, and reconciles each item's lots to match the true on-hand total
(opening or closing lots as needed). Used to catch up after login when full
container state first becomes available.

#### recordPortfolioSnapshot

`private void recordPortfolioSnapshot()`

Records a portfolio snapshot into the history (persisting throttled): the running
value — owned units (held plus suspended) marked to the current average plus sold
lots at their actual sale price — against the invested cost basis of every logged
lot, which stays fixed as lots sell. Their gap is thus the realized-plus-unrealized
profit. Suspended units must count: their lots are still open on the cost side, so
omitting their value would carve a false loss into the chart for the duration of
every in-flight sell, trade, drop, or death.

#### refreshGePrices

`private void refreshGePrices()`

Fetches the latest prices for all items in the background, then applies them on the client thread.

#### refreshPanel

`public void refreshPanel()`

Refreshes the panel without flagging a price update (no change indicators).

#### refreshPanel

`private void refreshPanel(boolean pricesUpdated)`

Pushes the current tracked items and totals to the panel on the Swing thread.

<p>Rebuilds are coalesced: the snapshot is published to `#pendingRebuild`
(last writer wins) and a drainer is enqueued only when none is pending, so no
matter how fast game events arrive, at most one rebuild sits in the EDT queue
and only the newest snapshot is rendered. Without this, per-tick events queue
full rebuilds faster than one completes and the panel's `removeAll` —
which scans the pending queue per removed child — live-locks the EDT (#120).

- **Parameter** `pricesUpdated` — whether this refresh follows a price change, enabling
                     the per-row change indicators

#### registerGeButtonSprite

`private void registerGeButtonSprite(BufferedImage icon)`

Registers the bundled Stockpile icon as a custom sprite override so it can be drawn on the
injected GE button (#140). Scaled to `#GE_ICON_SIZE` for a crisp render at button size.
A no-op when sprite overrides are unavailable (e.g. before the client is ready).

#### registerShopClaims

`private void registerShopClaims(Map<Integer,Integer> oldCounts, Map<Integer,Integer> newCounts)`

Claims an inventory change as a shop transaction (#67) when exactly one tracked
non-coin item moved: the coins paid or received, divided across the quantity,
price the item's `AcquisitionSource#SHOP` claim. A buy must pay coins; a
sell must not spend them, and a worthless sell the shop pays nothing for is still
a shop sale at 0. Anything murkier — multi-item changes, specialty-currency shops
(tokkul, marks) that move a second item rather than coins — stays unclaimed and
takes the unknown-source path.

#### registerTradeClaims

`private void registerTradeClaims()`

Books a completed trade's item movements as `AcquisitionSource#PLAYER_TRADE` (#66):
items received buy in at the gp we gave apportioned across them by market value, and
items given close at the gp we received apportioned the same way. Pure item-for-item
legs price at 0; coins and platinum tokens (valued at 1,000 gp each) are the
numerator, never an apportionment target.

<p>The two sides settle differently. Received items only enter our inventory now, so they
are registered as claims for the imminent additions to match. Given items already left our
inventory when they were offered (suspended, not closed), so there is no delta to match —
they are closed here directly against their trade suspension.

#### removeTrackedItem

`private void removeTrackedItem(int itemId)`

Stops tracking an item, then persists and refreshes. Also drops the item from the
session baseline — before the panel's next rebuild computes the session delta — so
untracking doesn't read as the item's whole value lost. Runs on the client thread.

#### renameCategory

`private void renameCategory(String oldName, String newName)`

Renames a category and re-points its items, ignoring blanks and clashes, then persists and refreshes.

#### reorderCategory

`private void reorderCategory(String name, int targetIndex)`

Moves a category to a new position in the ordered list, then persists and refreshes.

#### reorderTrackedItem

`private void reorderTrackedItem(int itemId, int targetIndex)`

Moves a tracked item to a new position in the list, persisting the new order so it
survives restarts. `targetIndex` is clamped to the list bounds; a no-op if the
item is unknown or already at that position. Runs on the client thread.

#### requestDetailData

`private void requestDetailData(int itemId)`

Fetches all four history series (5m/1h/6h/24h) plus metadata for the
detail view in the background, then updates stats, alch rune prices, and the
detail panel on the appropriate threads.

#### requestGeLinePrices

`private void requestGeLinePrices(int itemId)`

Resolves the open GE offer item's market prices in the background and caches them for the
info-block line, overwriting it in place once they arrive (#142). Falls back down a chain:
the latest priced 5m sample, then the latest priced 1h sample, then the item's latest instant
high/low; whichever lands first sets the row-label prefix (5m / 1h / Latest).

#### requestSeries

`private void requestSeries(int itemId, boolean refreshAfter)`

Fetches just the 5m series for an item in the background and recomputes its window stats.

#### resolveAlchValue

`private long resolveAlchValue(TrackedItem tracked, int canonicalId, boolean high)`

Resolves an item's alch value with a client-cache fallback (#238): prefers the
cached wiki value on the tracked item, and when that has not loaded yet reads the
item composition — `net.runelite.api.ItemComposition#getHaPrice()` for high
alch, and the store value's 40% for low alch — so the
`AcquisitionSource#ALCHEMY` claim is always registered regardless of whether
the wiki mapping or the item's price series has been fetched this session.

#### resolveTradeabilityForAll

`private void resolveTradeabilityForAll()`

Applies wiki metadata (tradeability, buy limit, GE value, high/low alch) to every
tracked item and the preview item now that the wiki mapping is available, then
refreshes the panel. Folding `#applyItemMetadata(TrackedItem)` into this sweep
(rather than waiting for each item's price-series fetch) means alch values are cached
for all items as soon as the mapping loads (#238). Items absent from the mapping are
not on the Grand Exchange, so they are marked non-tradeable and any stale price-load
failure is cleared.

#### resolveTradeable

`private void resolveTradeable(TrackedItem item)`

Narrows an item's tradeable flag using the wiki mapping: an item that the game
composition reports as tradeable but which is absent from the Grand Exchange
mapping (e.g. coins, burnt food) is reclassified as non-tradeable so it shows
"Item not tradeable" rather than a price-load failure. No-op until the mapping
has loaded, so a slow fetch never mislabels a genuinely tradeable item.

#### runePrice

`private long runePrice(int itemId)`

- **Returns:** a price for a rune (for alch calc): the tracked average if present, else the GE price.

#### scanForCloseAction

`private Widget scanForCloseAction(Widget widget)`

Recursively searches a widget subtree for the first visible widget carrying a "Close" action.

#### scanForItem

`private int scanForItem(Widget widget)`

Recursively searches a widget subtree for the first child holding a real item id.

#### scheduleRefresh

`private void scheduleRefresh()`

(Re)schedules the recurring GE price refresh at the configured rate (min 30s), replacing any prior task.

#### setFavorite

`private void setFavorite(int itemId, boolean favorite)`

Sets an item's favorite flag (pinning it to the top "Favorites" group), then persists and refreshes.

#### setGlobalOrder

`private void setGlobalOrder(List<Integer> orderedIds)`

Reorders the tracked items to match the given id order (drag reorder), then persists and
refreshes. Applies the new order only when it is a faithful permutation of the current
set, so a stale or partial drag result cannot drop items.

#### setGroupCollapsed

`private void setGroupCollapsed(String groupKey, boolean collapsed)`

Sets a list group's collapsed state (a category name, or a special-group key), then persists and refreshes.

#### setItemCategory

`private void setItemCategory(int itemId, String category)`

Assigns an item to a category (null/blank clears it to Uncategorized), then persists and refreshes.

#### setItemCompact

`private void setItemCompact(int itemId, boolean on)`

Toggles an item's per-item compact-row override (#210), then persists and refreshes.

#### setOnOverlay

`private void setOnOverlay(int itemId, boolean on)`

Adds/removes an item from the on-screen overlay set, enforcing the `#OVERLAY_MAX`
cap (an add beyond the cap is ignored), then persists and refreshes.

#### setSortMode

`private void setSortMode(SortMode mode)`

Persists the chosen sort mode; the resulting `ConfigChanged` rebuilds the panel.

#### shutDown

`protected void shutDown() throws Exception`

Tears down the nav button, overlays, panel, and refresh task and clears all in-memory state.

#### sourcePricing

`public boolean sourcePricing()`

Returns whether source-aware pricing is enabled in config.

- **Returns:** `true` if quantity changes are priced by their source

#### startUp

`protected void startUp() throws Exception`

Builds the side panel (wiring its callbacks back to this plugin), registers
the nav button and overlays, restores persisted items, and kicks off the
metadata fetch and recurring price refresh.

#### swapConflictingSection

`private void swapConflictingSection(ConfigChanged event)`

Keeps detail-section slots unique: when a section is moved to a slot already
occupied by another, the other section is swapped into the vacated slot.

#### syncQuantitiesForItem

`private void syncQuantitiesForItem(TrackedItem tracked)`

Recounts a single item across all containers and the rune pouch and sets its quantity.

#### syncQuantitiesFromContainers

`private void syncQuantitiesFromContainers()`

Applies the accumulated per-item container deltas to tracked items: positive
deltas open new lots (auto-add), negative deltas close lots FIFO, and each
item's quantity is adjusted. No-op when auto-add is off. Persists/refreshes
if anything changed.

#### syncRunePouch

`private void syncRunePouch()`

Rebuilds `#runePouchCounts` by reading the rune pouch type/quantity varbits.

#### toggleCompactView

`private void toggleCompactView()`

Flips the persisted compact-view flag; the resulting `ConfigChanged` rebuilds the panel.

#### toggleGeTracking

`private void toggleGeTracking()`

Toggles tracking of the open GE offer's item (#139). The add/remove is deferred to the client
thread, so the label refresh is enqueued after it — otherwise it would read the pre-toggle state
and only correct itself on the next mouse-leave.

#### toggleSortReversed

`private void toggleSortReversed()`

Flips the persisted sort direction; the resulting `ConfigChanged` rebuilds the panel.

#### trackedItem

`public TrackedItem trackedItem(int itemId)`

Returns the tracked item with the given id, if tracked.

- **Parameter** `itemId` — the item id
- **Returns:** the tracked item, or `null` when the id is not tracked

#### trackedItems

`public Collection<TrackedItem> trackedItems()`

Returns all currently tracked items.

- **Returns:** the tracked items

#### tradeGp

`private static long tradeGp(Map<Integer,Integer> side)`

- **Returns:** one trade side's money in gp: coins plus platinum tokens at 1,000 gp each.

#### tradeLegs

`private List<TradeApportioner.Leg> tradeLegs(Map<Integer,Integer> side)`

Builds one trade side's non-currency apportionment legs, each weighted by its unit market value.

#### unregisterGeButtonSprite

`private void unregisterGeButtonSprite()`

Removes the Stockpile GE-button sprite override on shutdown (#140).

#### untrackToPreview

`private void untrackToPreview(int itemId)`

Stops tracking an item but leaves it open in the detail view as a read-only preview (#138),
so untracking from the detail header does not bounce the user back to the main list. Removes
and persists exactly as `#removeTrackedItem`, then builds a transient preview and shows
it: the preview is opened (posting `showPreview` to the EDT) before the list rebuild is
queued, so the rebuild finds the panel already backed by the preview and keeps the detail card
up instead of returning to the list. Runs on the client thread.

#### untrackedInputValue

`private long untrackedInputValue(int itemId)`

- **Returns:** an untracked processing input's per-unit value under the configured fallback pricing.

---

## com.oveduumnakal.StockpileScreenOverlay

_class_

`public class StockpileScreenOverlay`

Draggable in-game overlay that renders the user's selected tracked items (up to
`StockpilePlugin#OVERLAY_MAX`). Each item is drawn either in the dense compact
two-row layout or as a replica of the panel's standard row (icon/name/qty, the configured
time-window price rows, and the est. profit line), per the configured `OverlayLayout`.
Hidden entirely when the overlay is disabled or no items are selected.

### Nested Type Summary

| Type | Description |
|---|---|
| _class_ [`Seg`](#comoveduumnakalstockpilescreenoverlayseg) | One coloured text segment within a rendered line. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final Color` | `AVG_COLOR` |  |
| `private static final Color` | `BACKGROUND` |  |
| `private static final Color` | `BORDER` | Dark brown border matching RuneLite's tan overlay background (rather than a stark black). |
| `private static final int` | `GAP` |  |
| `private static final Color` | `HIGH_COLOR` |  |
| `private static final int` | `ICON` |  |
| `private static final Color` | `LABEL_COLOR` |  |
| `private static final Color` | `LOW_COLOR` |  |
| `private static final Color` | `MUTED_COLOR` |  |
| `private static final Color` | `NAME_COLOR` |  |
| `private static final int` | `PAD` |  |
| `private static final Color` | `QTY_COLOR` |  |
| `private static final int` | `SEG_GAP` |  |
| `private static final Color` | `VOLUME_COLOR` |  |
| `private final StockpileConfig` | `config` |  |
| `private final Map<Long,BufferedImage>` | `iconCache` | Cached 18px scaled icons keyed by item id + rendered stack size, populated asynchronously on first use. |
| `private final ItemManager` | `itemManager` |  |
| `private final StockpilePlugin` | `plugin` |  |
| `private final int` | `slot` | Which overlay slot (0-based) this box renders — the item at that index in the overlay set. |

### Constructor Summary

| Constructor | Description |
|---|---|
| `StockpileScreenOverlay(StockpilePlugin plugin, StockpileConfig config, ItemManager itemManager, int slot)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `private List<List<Seg>>` | `blockLines(TrackedItem item, boolean compact)` | First line (name + qty) plus value lines for an item, used for both measuring and drawing. |
| `private int` | `drawLine(Graphics2D graphics, FontMetrics fm, int x, int baseline, List<Seg> segments)` | Draws a line of coloured segments left-to-right, returning the total width drawn. |
| `public OverlayLayer` | `getLayer()` |  |
| `public String` | `getName()` |  |
| `private BufferedImage` | `iconFor(TrackedItem item)` |  |
| `private int` | `maxLineWidth(FontMetrics fm, List<List<Seg>> lines)` |  |
| `public Dimension` | `render(Graphics2D graphics)` | Draws the on-screen overlay of selected tracked items. |
| `private static BufferedImage` | `toBuffered(Image image)` | Converts a scaled `Image` to a drawable `BufferedImage`. |
| `private List<Seg>` | `windowLine(TrackedItem item, TimeWindow window)` | Builds one standard-layout price line for a window, honouring the configured visible columns. |

### Field Detail

#### AVG_COLOR

`private static final Color AVG_COLOR`

#### BACKGROUND

`private static final Color BACKGROUND`

#### BORDER

`private static final Color BORDER`

Dark brown border matching RuneLite's tan overlay background (rather than a stark black).

#### GAP

`private static final int GAP`

#### HIGH_COLOR

`private static final Color HIGH_COLOR`

#### ICON

`private static final int ICON`

#### LABEL_COLOR

`private static final Color LABEL_COLOR`

#### LOW_COLOR

`private static final Color LOW_COLOR`

#### MUTED_COLOR

`private static final Color MUTED_COLOR`

#### NAME_COLOR

`private static final Color NAME_COLOR`

#### PAD

`private static final int PAD`

#### QTY_COLOR

`private static final Color QTY_COLOR`

#### SEG_GAP

`private static final int SEG_GAP`

#### VOLUME_COLOR

`private static final Color VOLUME_COLOR`

#### config

`private final StockpileConfig config`

#### iconCache

`private final Map<Long,BufferedImage> iconCache`

Cached 18px scaled icons keyed by item id + rendered stack size, populated asynchronously on first use.

#### itemManager

`private final ItemManager itemManager`

#### plugin

`private final StockpilePlugin plugin`

#### slot

`private final int slot`

Which overlay slot (0-based) this box renders — the item at that index in the overlay set.

### Constructor Detail

#### StockpileScreenOverlay

`StockpileScreenOverlay(StockpilePlugin plugin, StockpileConfig config, ItemManager itemManager, int slot)`

### Method Detail

#### blockLines

`private List<List<Seg>> blockLines(TrackedItem item, boolean compact)`

First line (name + qty) plus value lines for an item, used for both measuring and drawing.

#### drawLine

`private int drawLine(Graphics2D graphics, FontMetrics fm, int x, int baseline, List<Seg> segments)`

Draws a line of coloured segments left-to-right, returning the total width drawn.

#### getLayer

`public OverlayLayer getLayer()`

- **Returns:** the overlay layer: above interfaces when configured on top, otherwise behind windows (bank, GE, ...).

#### getName

`public String getName()`

- **Returns:** a per-slot unique name so each box persists (and is dragged) independently.

#### iconFor

`private BufferedImage iconFor(TrackedItem item)`

- **Returns:** an 18px cached quantity-aware icon for the item, requesting an async load on the first miss.

#### maxLineWidth

`private int maxLineWidth(FontMetrics fm, List<List<Seg>> lines)`

- **Returns:** the widest of the given lines in pixels.

#### render

`public Dimension render(Graphics2D graphics)`

Draws the on-screen overlay of selected tracked items.

- **Parameter** `graphics` — the overlay graphics context
- **Returns:** the rendered overlay's dimensions

#### toBuffered

`private static BufferedImage toBuffered(Image image)`

Converts a scaled `Image` to a drawable `BufferedImage`.

#### windowLine

`private List<Seg> windowLine(TrackedItem item, TimeWindow window)`

Builds one standard-layout price line for a window, honouring the configured visible columns.

---

## com.oveduumnakal.StockpileScreenOverlay.Seg

_class_

`private static final class Seg`

One coloured text segment within a rendered line.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `final Color` | `color` |  |
| `final String` | `text` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `Seg(String text, Color color)` |  |

### Field Detail

#### color

`final Color color`

#### text

`final String text`

### Constructor Detail

#### Seg

`Seg(String text, Color color)`

---

## com.oveduumnakal.SuspensionSource

_enum_

`enum SuspensionSource`

The kinds of suspension a tracked item's units can be held in (#179): a unit that has left the
held containers but is still owned, its lot kept open at its original basis until it resolves.
Each source carries its own lifecycle policy so one generic engine can suspend, restore, expire,
and persist all of them instead of the five hand-cloned pipelines this replaces.

<p>The policy captures the (previously drifted) per-source behaviour explicitly:
<ul>
  <li>`#SELL` and `#TRADE` — a GE sell / player-trade offer; they never time out and
      close at the realized transaction price, driven by the offer/trade events, so they carry no
      expiry and no fixed close source.</li>
  <li>`#GROUND` — a dropped or fired-ammo unit; refreshes its timestamp on each drop and, if
      not re-picked-up within `#expiry`, closes at 0 as a `AcquisitionSource#GROUND` loss.</li>
  <li>`#DEATH` — a unit lost to death; timestamps once (so a second death can't reset the
      first's clock, #168) and closes at 0 as `AcquisitionSource#DEATH` on gravestone loss or
      `#expiry`. Persisted, so it survives a relog into the recovery window.</li>
  <li>`#POUCH` — a unit filled into a fur/meat pouch; never times out and only ever
      un-suspends when the pouch is emptied. Persisted, since a pouch keeps its contents across a
      logout.</li>
</ul>

### Nested Type Summary

| Type | Description |
|---|---|
| _enum_ [`StampMode`](#comoveduumnakalsuspensionsourcestampmode) | How a source updates its suspension timestamp when more units are suspended. |

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `DEATH` | Units lost to a death; stamped once, expire to a 0-gp loss, persisted across a relog. |
| `GROUND` | Units dropped on the ground (or fired as recoverable ammo); refresh-stamped, expire to a 0-gp loss. |
| `POUCH` | Units filled into a fur/meat hunting pouch; never expire, only un-suspend on empty, persisted. |
| `SELL` | Units placed into a GE sell offer; realize at the fill price as a `AcquisitionSource#GE_TRADE` sale. |
| `TRADE` | Units placed into a player-trade offer; realize at the apportioned trade price as a player-trade sale. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final AcquisitionSource` | `closeSource` |  |
| `private final Duration` | `expiry` |  |
| `private final boolean` | `persisted` |  |
| `private final AcquisitionSource` | `realizeSource` |  |
| `private final StampMode` | `stampMode` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `SuspensionSource(StampMode stampMode, Duration expiry, AcquisitionSource closeSource, AcquisitionSource realizeSource, boolean persisted)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `AcquisitionSource` | `closeSource()` |  |
| `Duration` | `expiry()` |  |
| `boolean` | `persisted()` |  |
| `AcquisitionSource` | `realizeSource()` |  |
| `StampMode` | `stampMode()` |  |

### Enum Constant Detail

#### DEATH

`DEATH`

Units lost to a death; stamped once, expire to a 0-gp loss, persisted across a relog.

#### GROUND

`GROUND`

Units dropped on the ground (or fired as recoverable ammo); refresh-stamped, expire to a 0-gp loss.

#### POUCH

`POUCH`

Units filled into a fur/meat hunting pouch; never expire, only un-suspend on empty, persisted.

#### SELL

`SELL`

Units placed into a GE sell offer; realize at the fill price as a `AcquisitionSource#GE_TRADE` sale.

#### TRADE

`TRADE`

Units placed into a player-trade offer; realize at the apportioned trade price as a player-trade sale.

### Field Detail

#### closeSource

`private final AcquisitionSource closeSource`

#### expiry

`private final Duration expiry`

#### persisted

`private final boolean persisted`

#### realizeSource

`private final AcquisitionSource realizeSource`

#### stampMode

`private final StampMode stampMode`

### Constructor Detail

#### SuspensionSource

`SuspensionSource(StampMode stampMode, Duration expiry, AcquisitionSource closeSource, AcquisitionSource realizeSource, boolean persisted)`

### Method Detail

#### closeSource

`AcquisitionSource closeSource()`

- **Returns:** the acquisition source a timed-out suspension closes as (at 0 gp), or `null` for
        realize-at-price sources

#### expiry

`Duration expiry()`

- **Returns:** how long an unrecovered suspension survives before the expiry sweep closes it, or
        `null` when it never expires

#### persisted

`boolean persisted()`

- **Returns:** whether this source's suspension survives a relog and is written through `PersistedItem`.

#### realizeSource

`AcquisitionSource realizeSource()`

- **Returns:** the acquisition source a settled suspension realizes as (at its transaction price)
        when the sale/trade completes, or `null` for sources that never realize at price

#### stampMode

`StampMode stampMode()`

---

## com.oveduumnakal.SuspensionSource.StampMode

_enum_

`enum StampMode`

How a source updates its suspension timestamp when more units are suspended.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `NONE` | No timestamp is kept (the source has no expiry sweep). |
| `REFRESH` | Re-stamp on every addition (the newest suspension bounds the expiry window). |
| `STAMP_IF_EMPTY` | Stamp only when the entry was empty, so later additions can't reset the recovery clock (#168). |

### Enum Constant Detail

#### NONE

`NONE`

No timestamp is kept (the source has no expiry sweep).

#### REFRESH

`REFRESH`

Re-stamp on every addition (the newest suspension bounds the expiry window).

#### STAMP_IF_EMPTY

`STAMP_IF_EMPTY`

Stamp only when the entry was empty, so later additions can't reset the recovery clock (#168).

---

## com.oveduumnakal.TimeWindow

_enum_

`public enum TimeWindow`

A look-back period over which prices and volumes are summarized, from the
latest 5-minute snapshot (`#LIVE`) up to a `#YEAR`.

<p>Each constant carries three forms: a compact `label` for tight chips
(e.g. `"1mo"`), a spelled-out `longLabel` for headers
(e.g. `"1 Month"`), and a `duration` used to bound queries.
`#NONE` and `#LIVE` have a zero duration; `#NONE` is a
not-applicable placeholder.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `H1` | The trailing 1 hour. |
| `H12` | The trailing 12 hours. |
| `H24` | The trailing 24 hours. |
| `H3` | The trailing 3 hours. |
| `H6` | The trailing 6 hours. |
| `LIVE` | The latest 5-minute snapshot (no averaging window). |
| `M5` | The trailing 5-minute average. |
| `MONTH` | The trailing 30 days. |
| `MONTH3` | The trailing 90 days. |
| `MONTH6` | The trailing 180 days. |
| `NONE` | Not-applicable placeholder with a zero duration. |
| `WEEK` | The trailing week. |
| `YEAR` | The trailing 365 days. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final Duration` | `duration` |  |
| `private final String` | `label` |  |
| `private final String` | `longLabel` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `TimeWindow(String label, Duration duration, String longLabel)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public Duration` | `getDuration()` | Returns the look-back duration used to bound queries. |
| `public String` | `getLabel()` | Returns the compact chip label (e.g. |
| `public String` | `getLongLabel()` | Returns the spelled-out header label (e.g. |
| `public String` | `toString()` | Returns the compact label. |

### Enum Constant Detail

#### H1

`H1`

The trailing 1 hour.

#### H12

`H12`

The trailing 12 hours.

#### H24

`H24`

The trailing 24 hours.

#### H3

`H3`

The trailing 3 hours.

#### H6

`H6`

The trailing 6 hours.

#### LIVE

`LIVE`

The latest 5-minute snapshot (no averaging window).

#### M5

`M5`

The trailing 5-minute average.

#### MONTH

`MONTH`

The trailing 30 days.

#### MONTH3

`MONTH3`

The trailing 90 days.

#### MONTH6

`MONTH6`

The trailing 180 days.

#### NONE

`NONE`

Not-applicable placeholder with a zero duration.

#### WEEK

`WEEK`

The trailing week.

#### YEAR

`YEAR`

The trailing 365 days.

### Field Detail

#### duration

`private final Duration duration`

#### label

`private final String label`

#### longLabel

`private final String longLabel`

### Constructor Detail

#### TimeWindow

`TimeWindow(String label, Duration duration, String longLabel)`

### Method Detail

#### getDuration

`public Duration getDuration()`

Returns the look-back duration used to bound queries.

- **Returns:** the duration (`Duration#ZERO` for `#NONE` and `#LIVE`)

#### getLabel

`public String getLabel()`

Returns the compact chip label (e.g. `"1mo"`).

- **Returns:** the compact label

#### getLongLabel

`public String getLongLabel()`

Returns the spelled-out header label (e.g. `"1 Month"`).

- **Returns:** the long label

#### toString

`public String toString()`

Returns the compact label.

- **Returns:** the compact chip label

---

## com.oveduumnakal.TrackItemMode

_enum_

`public enum TrackItemMode`

Whether an entry is a full tracked item or a watch-only one.

<ul>
  <li>`#TRACK` &ndash; counts toward quantities, value, and profit totals.</li>
  <li>`#VIEW` &ndash; shown for its prices/charts only, excluded from totals.</li>
</ul>

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `TRACK` | Fully tracked: counts toward quantities, value, and profit totals. |
| `VIEW` | Watch-only: shown for prices/charts, excluded from totals. |

### Enum Constant Detail

#### TRACK

`TRACK`

Fully tracked: counts toward quantities, value, and profit totals.

#### VIEW

`VIEW`

Watch-only: shown for prices/charts, excluded from totals.

---

## com.oveduumnakal.TrackedItem

_class_

`public class TrackedItem`

The full state of one item being tracked: its identity and quantity, the
latest wiki prices (high/low/average) and their deltas, per-window summary
stats and price history, GE metadata (buy limit, alch values), and the
acquisition lots that back its cost-basis profit calculations.

<p>Price-history `series*` lists are `transient`: they are fetched
at runtime and not persisted with the rest of the item. The value/profit
accessors derive figures from `quantity`, the current prices, and the
`AcquisitionRecord` lots.

### Nested Type Summary

| Type | Description |
|---|---|
| _class_ [`SuspensionState`](#comoveduumnakaltrackeditemsuspensionstate) | Mutable per-source suspension counter and its (optional) recovery-expiry timestamp. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private List<AcquisitionRecord>` | `acquisitions` |  |
| `private int` | `avgDelta` |  |
| `private long` | `avgPrice` |  |
| `private int` | `buyLimit` |  |
| `private String` | `category` |  |
| `private boolean` | `compact` |  |
| `private boolean` | `costBasisInitialized` |  |
| `private boolean` | `favorite` |  |
| `private long` | `geValue` |  |
| `private boolean` | `hasDeltas` |  |
| `private long` | `highAlch` |  |
| `private int` | `highDelta` |  |
| `private long` | `highPrice` |  |
| `private final int` | `itemId` |  |
| `private long` | `latestHighTime` |  |
| `private long` | `latestLowTime` |  |
| `private transient int` | `limitBought` | Units bought toward the GE buy limit in the current 4-hour window (transient; set from the plugin). |
| `private transient long` | `limitResetEpoch` | Epoch-second when the current GE buy-limit window resets, or 0 when none (transient). |
| `private long` | `lowAlch` |  |
| `private int` | `lowDelta` |  |
| `private long` | `lowPrice` |  |
| `private boolean` | `metadataLoaded` |  |
| `private TrackItemMode` | `mode` |  |
| `private final String` | `name` |  |
| `private List<NotificationRule>` | `notifications` |  |
| `private boolean` | `notificationsInitialized` |  |
| `private boolean` | `onOverlay` |  |
| `private long` | `prevAvgPrice` |  |
| `private long` | `prevHighPrice` |  |
| `private long` | `prevLowPrice` |  |
| `private transient boolean` | `priceCacheHydrated` |  |
| `private boolean` | `priceLoadFailed` |  |
| `private int` | `quantity` |  |
| `private transient List<WikiRealtimePriceClient.PricePoint>` | `series1h` |  |
| `private transient List<WikiRealtimePriceClient.PricePoint>` | `series24h` |  |
| `private transient List<WikiRealtimePriceClient.PricePoint>` | `series5m` |  |
| `private transient List<WikiRealtimePriceClient.PricePoint>` | `series6h` |  |
| `private boolean` | `stackable` |  |
| `private transient Map<SuspensionSource,SuspensionState>` | `suspensions` | Per-source suspension state (#179): for each `SuspensionSource`, this item's units currently held in that suspension and — for sources that expire — when the newest was taken. |
| `private boolean` | `tradeable` |  |
| `private Map<TimeWindow,PriceStats>` | `windowStats` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public void` | `addSuspended(SuspensionSource source, int qty)` | Suspends `qty` more units under `source`, updating the timestamp per the source's `SuspensionSource#stampMode()` so death can't reset its recovery clock while ground can. |
| `public void` | `clearSuspended(SuspensionSource source)` | Drops `source`'s entire suspension — count and timestamp. |
| `public long` | `getAvgValue()` |  |
| `public long` | `getCostBasis()` |  |
| `public long` | `getHighValue()` |  |
| `public long` | `getInvestedCostBasis()` |  |
| `public long` | `getLowValue()` |  |
| `public long` | `getProfitAt(long markPrice)` | Total profit if the held lots were valued at `markPrice`: realized profit from sold lots plus the unrealized gain/loss on still-held lots. |
| `public long` | `getRealizedProceeds()` |  |
| `public long` | `getRealizedProfit()` |  |
| `public int` | `getRecordQuantitySum()` |  |
| `public List<WikiRealtimePriceClient.PricePoint>` | `getSeriesFor(TimeWindow window)` | Selects the price-history series whose sampling granularity best fits the given window: 1h points for a week, 6h for a month, 24h for quarter/half/year, and 5m points for anything shorter. |
| `public int` | `getSuspended(SuspensionSource source)` |  |
| `public Instant` | `getSuspendedAt(SuspensionSource source)` |  |
| `public long` | `getSuspendedValue()` |  |
| `public int` | `getTotalSuspendedQuantity()` |  |
| `public boolean` | `hasLivePrices()` |  |
| `public boolean` | `hasPrices()` |  |
| `public int` | `iconStackSize()` |  |
| `public int` | `reduceSuspended(SuspensionSource source, int qty)` | Restores up to `qty` units from `source`'s suspension, clearing the entry (and its timestamp) once it empties. |
| `public void` | `restoreSuspended(SuspensionSource source, int qty, Instant at)` | Seeds `source`'s suspension to `qty` at timestamp `at` when restoring persisted (death/pouch) state on login, so the recovery-expiry clock resumes from where it was saved rather than restarting now. |
| `public void` | `setSuspended(SuspensionSource source, int qty)` | Sets `source`'s suspended count outright, stamping per policy; drops the entry when 0. |
| `private Map<SuspensionSource,SuspensionState>` | `suspensions()` |  |

### Field Detail

#### acquisitions

`private List<AcquisitionRecord> acquisitions`

#### avgDelta

`private int avgDelta`

#### avgPrice

`private long avgPrice`

#### buyLimit

`private int buyLimit`

#### category

`private String category`

#### compact

`private boolean compact`

#### costBasisInitialized

`private boolean costBasisInitialized`

#### favorite

`private boolean favorite`

#### geValue

`private long geValue`

#### hasDeltas

`private boolean hasDeltas`

#### highAlch

`private long highAlch`

#### highDelta

`private int highDelta`

#### highPrice

`private long highPrice`

#### itemId

`private final int itemId`

#### latestHighTime

`private long latestHighTime`

#### latestLowTime

`private long latestLowTime`

#### limitBought

`private transient int limitBought`

Units bought toward the GE buy limit in the current 4-hour window (transient; set from the plugin).

#### limitResetEpoch

`private transient long limitResetEpoch`

Epoch-second when the current GE buy-limit window resets, or 0 when none (transient).

#### lowAlch

`private long lowAlch`

#### lowDelta

`private int lowDelta`

#### lowPrice

`private long lowPrice`

#### metadataLoaded

`private boolean metadataLoaded`

#### mode

`private TrackItemMode mode`

#### name

`private final String name`

#### notifications

`private List<NotificationRule> notifications`

#### notificationsInitialized

`private boolean notificationsInitialized`

#### onOverlay

`private boolean onOverlay`

#### prevAvgPrice

`private long prevAvgPrice`

#### prevHighPrice

`private long prevHighPrice`

#### prevLowPrice

`private long prevLowPrice`

#### priceCacheHydrated

`private transient boolean priceCacheHydrated`

#### priceLoadFailed

`private boolean priceLoadFailed`

#### quantity

`private int quantity`

#### series1h

`private transient List<WikiRealtimePriceClient.PricePoint> series1h`

#### series24h

`private transient List<WikiRealtimePriceClient.PricePoint> series24h`

#### series5m

`private transient List<WikiRealtimePriceClient.PricePoint> series5m`

#### series6h

`private transient List<WikiRealtimePriceClient.PricePoint> series6h`

#### stackable

`private boolean stackable`

#### suspensions

`private transient Map<SuspensionSource,SuspensionState> suspensions`

Per-source suspension state (#179): for each `SuspensionSource`, this item's units
currently held in that suspension and — for sources that expire — when the newest was taken.
A unit here has left the held containers but is still owned, its lot kept open at basis until
it resolves. All transient: sell/trade/ground suspensions are session-only, while death and
pouch are re-seeded on login from `PersistedItem` (see `SuspensionSource#persisted()`),
so Gson never touches this map. Legacy records default to empty — the safe additive default.

#### tradeable

`private boolean tradeable`

#### windowStats

`private Map<TimeWindow,PriceStats> windowStats`

### Method Detail

#### addSuspended

`public void addSuspended(SuspensionSource source, int qty)`

Suspends `qty` more units under `source`, updating the timestamp per the source's
`SuspensionSource#stampMode()` so death can't reset its recovery clock while ground can.

#### clearSuspended

`public void clearSuspended(SuspensionSource source)`

Drops `source`'s entire suspension — count and timestamp.

#### getAvgValue

`public long getAvgValue()`

- **Returns:** the tracked quantity valued at the average price.

#### getCostBasis

`public long getCostBasis()`

- **Returns:** total gp paid for the lots still held (unsold acquisitions).

#### getHighValue

`public long getHighValue()`

- **Returns:** the tracked quantity valued at the high (instant-buy) price.

#### getInvestedCostBasis

`public long getInvestedCostBasis()`

- **Returns:** total gp paid across every logged lot, held and sold. Unlike `#getCostBasis()`
        (held lots only), this stays fixed as lots are sold, so it is the running invested
        baseline for the portfolio value chart.

#### getLowValue

`public long getLowValue()`

- **Returns:** the tracked quantity valued at the low (instant-sell) price.

#### getProfitAt

`public long getProfitAt(long markPrice)`

Total profit if the held lots were valued at `markPrice`: realized
profit from sold lots plus the unrealized gain/loss on still-held lots.

- **Parameter** `markPrice` — the per-unit price used to mark held lots to market
- **Returns:** realized plus unrealized profit in gp

#### getRealizedProceeds

`public long getRealizedProceeds()`

- **Returns:** realized sale proceeds: sum of qty * sold price across sold lots (0 while nothing is sold).

#### getRealizedProfit

`public long getRealizedProfit()`

- **Returns:** profit already locked in from sold lots: sum of qty * (sold - bought).

#### getRecordQuantitySum

`public int getRecordQuantitySum()`

- **Returns:** total units across the lots still held (unsold acquisitions).

#### getSeriesFor

`public List<WikiRealtimePriceClient.PricePoint> getSeriesFor(TimeWindow window)`

Selects the price-history series whose sampling granularity best fits the
given window: 1h points for a week, 6h for a month, 24h for quarter/half/year,
and 5m points for anything shorter.

- **Parameter** `window` — the time window being displayed
- **Returns:** the backing point list (live, not a copy)

#### getSuspended

`public int getSuspended(SuspensionSource source)`

- **Returns:** this item's units currently suspended by `source`.

#### getSuspendedAt

`public Instant getSuspendedAt(SuspensionSource source)`

- **Returns:** when `source`'s newest suspension was taken, or `null` when none is held.

#### getSuspendedValue

`public long getSuspendedValue()`

- **Returns:** the suspended units valued at the average price. Suspended units are still
        owned and their lots still open, so value/profit figures that subtract an
        open-lot cost basis must add this back — otherwise every in-flight sell,
        trade, drop, or death reads as a loss for its duration

#### getTotalSuspendedQuantity

`public int getTotalSuspendedQuantity()`

- **Returns:** units suspended across every source (GE sell, trade, ground, death,
        hunting pouch): owned and still covered by open lots, but held outside
        the containers that `quantity` counts

#### hasLivePrices

`public boolean hasLivePrices()`

- **Returns:** whether this item has prices from a live fetch rather than persisted cache hydration.

#### hasPrices

`public boolean hasPrices()`

- **Returns:** whether any live price is known for this item.

#### iconStackSize

`public int iconStackSize()`

- **Returns:** the stack size to render this item's icon at: the tracked quantity for
        stackable items, else 1 (plain single sprite)

#### reduceSuspended

`public int reduceSuspended(SuspensionSource source, int qty)`

Restores up to `qty` units from `source`'s suspension, clearing the entry (and its
timestamp) once it empties. Returns the number actually restored.

#### restoreSuspended

`public void restoreSuspended(SuspensionSource source, int qty, Instant at)`

Seeds `source`'s suspension to `qty` at timestamp `at` when restoring persisted
(death/pouch) state on login, so the recovery-expiry clock resumes from where it was saved rather
than restarting now. A non-positive `qty` clears the entry.

#### setSuspended

`public void setSuspended(SuspensionSource source, int qty)`

Sets `source`'s suspended count outright, stamping per policy; drops the entry when 0.

#### suspensions

`private Map<SuspensionSource,SuspensionState> suspensions()`

- **Returns:** the suspension map, lazily created. Lazy because Gson deserializes a legacy record
        through `Unsafe` without running field initializers, leaving the field null.

---

## com.oveduumnakal.TrackedItem.SuspensionState

_class_

`private static final class SuspensionState`

Mutable per-source suspension counter and its (optional) recovery-expiry timestamp.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private Instant` | `at` |  |
| `private int` | `quantity` |  |

### Field Detail

#### at

`private Instant at`

#### quantity

`private int quantity`

---

## com.oveduumnakal.TradeApportioner

_class_

`final class TradeApportioner`

Pure math for player-trade pricing (#66): splits one side's gp across the other
side's items proportionally to their market values, yielding a per-unit price for
each item leg. Receiving 10M-for-item values that item's lots at 10M; two items
for gp split the gp by their (unit value × quantity) weights. Legs with no market
value anywhere split the gp evenly per unit, and pure item-for-item trades (no gp)
price at 0. Client-free so the split rules are unit-testable in isolation.

### Nested Type Summary

| Type | Description |
|---|---|
| _class_ [`Leg`](#comoveduumnakaltradeapportionerleg) | One non-coin item leg of a trade side: the item, how many, and its unit market value. |

### Constructor Summary

| Constructor | Description |
|---|---|
| `TradeApportioner()` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `static Map<Integer,Long>` | `apportion(List<Leg> legs, long gp)` | Splits `gp` across `legs` proportionally to their total market values, returning each leg's per-unit price. |

### Constructor Detail

#### TradeApportioner

`private TradeApportioner()`

### Method Detail

#### apportion

`static Map<Integer,Long> apportion(List<Leg> legs, long gp)`

Splits `gp` across `legs` proportionally to their total market
values, returning each leg's per-unit price. A zero or negative `gp`
prices every leg at 0; legs whose combined market value is 0 split the gp
evenly per unit instead. Integer division truncates — the dust is ignored.

<p>The proportional multiply is done in `double` so a max-cash `gp`
against a multi-billion leg value can't overflow `long` and wrap to a
negative per-unit price; a double's 53-bit mantissa covers gp magnitudes exactly.

- **Returns:** item id → per-unit price in gp

---

## com.oveduumnakal.TradeApportioner.Leg

_class_

`static final class Leg`

One non-coin item leg of a trade side: the item, how many, and its unit market value.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `final int` | `itemId` |  |
| `final int` | `quantity` |  |
| `final long` | `unitValue` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `Leg(int itemId, int quantity, long unitValue)` |  |

### Field Detail

#### itemId

`final int itemId`

#### quantity

`final int quantity`

#### unitValue

`final long unitValue`

### Constructor Detail

#### Leg

`Leg(int itemId, int quantity, long unitValue)`

---

## com.oveduumnakal.ValueFormat

_enum_

`public enum ValueFormat`

How gp values are rendered: `#ABBREVIATED` compact suffixes
(e.g. `1.5M`) or `#FULL` comma-grouped digits
(e.g. `1,500,000`). See `GpFormat` for the formatting itself. The
`displayName` is the label shown in the config dropdown.

### Enum Constant Summary

| Enum Constant | Description |
|---|---|
| `ABBREVIATED` | The `"Short (k,m,b)"` option. |
| `FULL` | The `"Full (x,xxx)"` option. |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private final String` | `displayName` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `ValueFormat(String displayName)` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public String` | `toString()` | Returns the display label shown in the UI. |

### Enum Constant Detail

#### ABBREVIATED

`ABBREVIATED`

The `"Short (k,m,b)"` option.

#### FULL

`FULL`

The `"Full (x,xxx)"` option.

### Field Detail

#### displayName

`private final String displayName`

### Constructor Detail

#### ValueFormat

`ValueFormat(String displayName)`

### Method Detail

#### toString

`public String toString()`

Returns the display label shown in the UI.

- **Returns:** the display label

---

## com.oveduumnakal.WikiRealtimePriceClient

_class_

`public class WikiRealtimePriceClient`

Thin client for the OSRS Wiki real-time prices API
(<a href="https://prices.runescape.wiki">prices.runescape.wiki</a>).

<p>Fetches live high/low prices, item metadata (buy limit, GE value, alch
values), and historical time series, and aggregates a series into
`PriceStats`. Every call is defensive: network, HTTP, and malformed-JSON
failures are logged and return an empty result rather than throwing, and
individual bad entries are skipped.

### Nested Type Summary

| Type | Description |
|---|---|
| _class_ [`ItemMapping`](#comoveduumnakalwikirealtimepriceclientitemmapping) | Static GE metadata for an item: buy `limit`, store `value`, high/low alch values, and the in-game `examine` text (`null` when absent). |
| _class_ [`ItemPrices`](#comoveduumnakalwikirealtimepriceclientitemprices) | The latest instant-buy (`high`) and instant-sell (`low`) prices for one item, each with the epoch-second timestamp of the trade that set it (`highTime`, `lowTime`). |
| _class_ [`PricePoint`](#comoveduumnakalwikirealtimepriceclientpricepoint) | One sample from a time series: the average high/low prices and traded volumes over the bucket ending at `timestamp` (epoch seconds). |

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private static final HttpUrl` | `LATEST_URL` |  |
| `private static final HttpUrl` | `MAPPING_URL` |  |
| `private static final String` | `TIMESERIES_URL` |  |
| `private static final String` | `USER_AGENT` |  |
| `private final Gson` | `gson` |  |
| `private final OkHttpClient` | `httpClient` |  |

### Constructor Summary

| Constructor | Description |
|---|---|
| `WikiRealtimePriceClient(OkHttpClient httpClient, Gson gson)` | Creates a client for the OSRS Wiki real-time prices API. |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public static PriceStats` | `computeStats(List<PricePoint> points, TimeWindow window)` | Aggregates a time series into summary `PriceStats` over a window. |
| `public Map<Integer,ItemPrices>` | `fetchAll()` | Fetches the latest high/low prices for every item from the `/latest` endpoint. |
| `public Map<Integer,ItemMapping>` | `fetchMapping()` | Fetches per-item GE metadata from the `/mapping` endpoint. |
| `public List<PricePoint>` | `fetchTimeseries(int itemId, String timestep)` | Fetches the price/volume history for one item at a given sampling step. |
| `private static long` | `readLong(JsonObject obj, String key)` | Reads a numeric field as a long, returning 0 when absent, null, or non-numeric. |

### Field Detail

#### LATEST_URL

`private static final HttpUrl LATEST_URL`

#### MAPPING_URL

`private static final HttpUrl MAPPING_URL`

#### TIMESERIES_URL

`private static final String TIMESERIES_URL`

#### USER_AGENT

`private static final String USER_AGENT`

#### gson

`private final Gson gson`

#### httpClient

`private final OkHttpClient httpClient`

### Constructor Detail

#### WikiRealtimePriceClient

`public WikiRealtimePriceClient(OkHttpClient httpClient, Gson gson)`

Creates a client for the OSRS Wiki real-time prices API.

- **Parameter** `httpClient` — the shared HTTP client
- **Parameter** `gson` — the Gson instance used to parse API responses

### Method Detail

#### computeStats

`public static PriceStats computeStats(List<PricePoint> points, TimeWindow window)`

Aggregates a time series into summary `PriceStats` over a window.

<p>Points older than the window are ignored. High and low are simple
averages of samples that have both a price and volume on that side; the
average is volume-weighted across both sides (falling back to the high/low
midpoint when no volume is present). Volume is the total units traded.

- **Parameter** `points` — the item's history (may be `null` or empty)
- **Parameter** `window` — the look-back window; a zero duration means "all points"
- **Returns:** the computed stats, or all-zero stats when there is no data

#### fetchAll

`public Map<Integer,ItemPrices> fetchAll()`

Fetches the latest high/low prices for every item from the `/latest`
endpoint.

- **Returns:** a map of item id to prices, or an empty map on any failure

#### fetchMapping

`public Map<Integer,ItemMapping> fetchMapping()`

Fetches per-item GE metadata from the `/mapping` endpoint.

- **Returns:** a map of item id to `ItemMapping`, or an empty map on any failure

#### fetchTimeseries

`public List<PricePoint> fetchTimeseries(int itemId, String timestep)`

Fetches the price/volume history for one item at a given sampling step.

- **Parameter** `itemId` — the item to query
- **Parameter** `timestep` — the bucket size, e.g. `"5m"`, `"1h"`, `"6h"`, `"24h"`
- **Returns:** the points oldest-first, or an empty list on any failure

#### readLong

`private static long readLong(JsonObject obj, String key)`

Reads a numeric field as a long, returning 0 when absent, null, or non-numeric.

---

## com.oveduumnakal.WikiRealtimePriceClient.ItemMapping

_class_

`public static class ItemMapping`

Static GE metadata for an item: buy `limit`, store `value`, high/low
alch values, and the in-game `examine` text (`null` when absent).

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `String` | `examine` |  |
| `long` | `highAlch` |  |
| `int` | `limit` |  |
| `long` | `lowAlch` |  |
| `long` | `value` |  |

### Field Detail

#### examine

`String examine`

#### highAlch

`long highAlch`

#### limit

`int limit`

#### lowAlch

`long lowAlch`

#### value

`long value`

---

## com.oveduumnakal.WikiRealtimePriceClient.ItemPrices

_class_

`public static class ItemPrices`

The latest instant-buy (`high`) and instant-sell (`low`) prices
for one item, each with the epoch-second timestamp of the trade that set it
(`highTime`, `lowTime`). The two sides are independent, so one can
be much staler than the other.

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `long` | `high` |  |
| `long` | `highTime` |  |
| `long` | `low` |  |
| `long` | `lowTime` |  |

### Method Summary

| Modifier and Type | Method | Description |
|---|---|---|
| `public long` | `avg()` |  |

### Field Detail

#### high

`long high`

#### highTime

`long highTime`

#### low

`long low`

#### lowTime

`long lowTime`

### Method Detail

#### avg

`public long avg()`

- **Returns:** the midpoint of high and low, or whichever side is present if only one is.

---

## com.oveduumnakal.WikiRealtimePriceClient.PricePoint

_class_

`public static class PricePoint`

One sample from a time series: the average high/low prices and traded
volumes over the bucket ending at `timestamp` (epoch seconds).

### Field Summary

| Modifier and Type | Field | Description |
|---|---|---|
| `private long` | `avgHighPrice` |  |
| `private long` | `avgLowPrice` |  |
| `private long` | `highPriceVolume` |  |
| `private long` | `lowPriceVolume` |  |
| `private long` | `timestamp` |  |

### Field Detail

#### avgHighPrice

`private long avgHighPrice`

#### avgLowPrice

`private long avgLowPrice`

#### highPriceVolume

`private long highPriceVolume`

#### lowPriceVolume

`private long lowPriceVolume`

#### timestamp

`private long timestamp`
