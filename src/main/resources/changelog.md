# Stockpile changelog

Newest release first. Each entry is `## <version> — <YYYY-MM-DD>` followed by
user-facing highlights. The build fails if the top entry's version does not match
`runelite-plugin.properties` (see ChangelogGuardTest), so a version bump forces a
new entry before anything can ship.

## 1.4 — 2026-07-25
- Source-attributed pricing: buys and sells are priced by how items actually entered or left your collection — GE offers, shops, player trades, alchemy casts, ground pickups, processing, and deaths.
- Grand Exchange: buys and sells recorded at their real offer prices, with rolling 4-hour buy-limit tracking.
- Prices the plugin had to estimate (mobile/offline changes) are marked with `~` and a tooltip, so you always know observed from guessed.
- Dropping items or dying no longer wipes cost basis: drops and death losses suspend their lots and restore on re-pickup or gravestone recovery.
- Cooking, smithing, crafting, and other processing transfers the ingredients' cost onto the product, so margins read end to end.
- Collection log shows each lot's source (GE, Shop, Ground, …); CSV exports carry the buy/sell provenance too.
- Export the acquisitions log to CSV and share your tracked list with a compact code.
- Portfolio value history chart: watch your total collection value over time.
- Session stats: see value gained or lost since login, split into price movement vs. quantity change.
- Auto-categorize tracked items using the wiki's item groupings, alphabetized with Other last.
- Tracked list gains sort modes (Name, Value, Profit, 24h Change) and distinct header icons with hover highlights.
- Last-known prices are cached, so the panel shows values immediately at startup instead of dashes.
- Notification rules can now repeat, re-arming after the condition clears and returns.
- Smoother panel: fixed client freezes during heavy activity, detail-view scroll jumps, and bank placeholders counting as owned items.

## 1.3 — 2026-05-30
- Collection log overhaul: FIFO cost basis, realized vs. unrealized profit, and per-lot acquisitions.
- Latest-price staleness dimming with age tooltips.
- Detail-view market ratings: volatility, liquidity, and 30-day range.

## 1.2 — 2026-04-18
- Price alerts with configurable metric, timeframe, and threshold.
- Per-item price charts with selectable timeframes and pop-out windows.
- Grouping and favorites for the tracked list.

## 1.1 — 2026-03-12
- Live GE and wiki realtime prices with high/low/average and 24h change.
- Ground and inventory item highlights.
- In-plugin bug report and feature request forms.
