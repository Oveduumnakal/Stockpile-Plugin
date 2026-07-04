<p align="center">
  <img src="banner.png" alt="Stockpile banner">
</p>
<h1 align="center">Stockpile</h1>

A RuneLite plugin for tracking the quantity, value, and profitability of the
items you gather, produce, or trade. Stockpile pairs live Grand Exchange /
wiki pricing with a cost-basis profit model, in-panel charting, on-screen
overlays, and Grand Exchange integration, so you can see at a glance what your
tracked items are worth and how much you have actually made.

## Features

### Tracked list

- Add items from the search bar, from a right-click "Track Item" / "Stop
  Tracking" context-menu entry, or automatically as collection-log entries
  arrive (auto-add modes control the buy-in price).
- View-only mode: preview any item's prices and charts without adding it to
  your tracked list.
- Favorites pin frequent items to the top; user-defined collapsible categories
  group the rest.
- Compact mode, a filter field for long lists, drag reordering, and
  quantity-aware item icons that render your actual stack size.
- Quantities stay in sync with your inventory, bank, and rune pouch.

### Per-item detail view & charts

- A rich detail view with configurable section ordering and visibility.
- Price and volume graphs with selectable timeframes (1 day to 1 year), a
  hover crosshair, optional smoothing, selectable line sets, and pop-out
  windows for a larger standalone view.
- Price overview grid with high/low/average/volume/change per time window
  (live, 24h, 1 week, 1 month, 1 year), with preset row selections.
- Item examine text, plus link buttons to the item's OSRS Wiki page and its
  live-prices page.

### Market info

- Buy limit, GE tax, and the item's last-bought / last-sold times, with stale
  prices dimmed and their trade times shown in tooltips.
- Volatility and liquidity ratings, a 30-day price-range bar showing where the
  live price sits, and a buy/sell pressure bar from instant-buy vs
  instant-sell volume.
- High/low alchemy values with rune-cost-adjusted profit estimates.

### Profit & cost basis

- Cost-basis model: record acquisitions with their quantity and price, and the
  plugin tracks your average cost and estimated profit per item.
- Editable acquisitions table (with a pop-out window) to correct or backfill
  your history.
- GE Estimates section with portfolio-wide high/low/average totals and profit,
  with configurable position, price format, and row spacing.

### Overlays & in-game integration

- On-screen overlay boxes for chosen tracked items, in a compact or standard
  layout with configurable data rows.
- Highlight tracked items on the ground and in your inventory.
- Grand Exchange integration: opening a GE offer can auto-open the item in
  Stockpile and/or inject a "View in Stockpile" button on the offer screen.

### Notifications

- Per-item alert rules on price, percent change, volume, and market ratings,
  with configurable timeframes and operators.
- Delivered through RuneLite's notification system; each rule fires once when
  its condition is met.

### Live pricing

- Current Grand Exchange / live wiki value for every tracked item, refreshed
  on a configurable interval.
- Time-window price and volume breakdowns sourced from the wiki realtime and
  timeseries APIs.

## Links

- [Report a bug](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/new?template=bug_report.yml)
- [Request a feature](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/new?template=feature_request.yml)

## Building / running

Run the `run` Gradle task to launch a development client with the plugin loaded:

```
./gradlew run
```

To build the plugin jar:

```
./gradlew build
```
