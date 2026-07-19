<!--
Stockpile changelog. Newest release first. Each release is a top-level heading
"# <version> - <written-out date>" followed by a Quick Overview, a Detailed
Breakdown (features grouped by area, each with the issues that make it up), and
Bug Fixes. The build fails if the top entry's version does not match
runelite-plugin.properties (see ChangelogGuardTest), so a version bump forces a
new entry before anything can ship. Order features within a section by user impact.
Bug Fixes lists only bugs that shipped in a previous release; bugs introduced and
fixed within the same release cycle are omitted, since users never saw them.
-->

# 1.4 - July 18 2026

## Quick Overview

Stockpile 1.4 is all about making your profit numbers something you can actually trust. Instead of guessing, the plugin now watches how items really come and go — buying and selling on the Grand Exchange, trading with other players, buying from shops, alching, cooking, and more — and works out your profit from what actually happened. Your whole collection now has a value-history chart so you can watch it grow over time, plus a "Session" line that shows how much you've made or lost since you logged in. You can sort and auto-organize your tracked list, and share or back it up with a click. On top of that, there are lots of fixes to make the panel smoother and the numbers more accurate.

## Detailed Breakdown

### Tracked Item View

#### Session stats
A new "Session" line shows how much value you've gained or lost since login, split into price movement versus what you bought and sold.
[#62](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/62)

#### Portfolio value history chart
A chart of your entire collection's value over time, opened from the totals area. It tracks value per item, so removing something corrects the chart's history too.
[#61](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/61), [#152](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/152)

#### Auto-categorize your items
One click sorts your tracked items into sensible groups based on what they are.
[#63](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/63)

#### Sort your list your way
Sort your tracked items by name, total value, profit, or 24-hour price change.
[#57](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/57)

#### Export and share
Export your acquisitions to a spreadsheet (CSV), and share or back up your tracked list with a short code.
[#58](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/58)

#### Values the moment you log in
The plugin remembers the last prices it saw, so the panel shows real numbers straight away instead of blank dashes while it loads.
[#59](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/59)

#### "What's New" changelog
This window — a quick way to see what each update added, right inside the plugin.
[#79](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/79)

### Detailed Item View

#### See where each item came from
The collection log now shows the source of every batch of items — Grand Exchange, shop, ground, trade, and so on.
[#100](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/100)

### Grand Exchange

#### Real buy and sell prices
Buys and sells are recorded at the actual price your offer went through at, not an estimate.
[#72](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/72), [#12](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/12)

#### Buy-limit tracking
See how much of an item's 4-hour Grand Exchange buy limit you have left.
[#60](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/60)

### Accurate Pricing & Profit

#### Prices based on what actually happened
Every change in your item counts is priced by how it happened, so profit reflects reality. When the plugin can't tell how something changed (for example, edits made on mobile), it falls back sensibly and marks the price as an estimate.
[#64](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/64), [#71](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/71)

#### Every source priced correctly
Ground pickups and drops, player trades, shop buys and sells, and High/Low Alchemy are each priced the right way.
[#65](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/65), [#66](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/66), [#67](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/67), [#68](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/68)

#### Cost that survives processing and death
Cooking, crafting, smithing, and fletching carry an item's cost onto whatever you make from it. Dying or dropping items no longer wipes your cost history — it's restored when you get them back.
[#69](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/69), [#70](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/70)

#### Smarter loss handling
Failed gem cuts and burnt or destroyed outputs are now valued correctly instead of counting as full-price items.
[#146](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/146)

### Notifications

#### Repeating alerts
Price alerts can now re-arm: once the condition clears and happens again, the alert will fire a second time.
[#56](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/56)

### Bug Fixes

[#136](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/136) - You can now track items from the shop or your inventory while a shop is open
[#123](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/123) - Bank placeholders no longer count as real items and create phantom quantities
[#125](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/125) - Fixed the detail view scroll jumping back to the top on every price refresh
[#120](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/120) - Fixed the client freezing during heavy activity from too many panel rebuilds
[#112](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/112) - Fixed tracked rows changing height and shifting the list when you hover them
[#141](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/141) - Fixed the graph pop-out showing no data for view-only items
[#99](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/99) - Fixed examine/flavour text not wrapping properly

# 1.3 - July 3 2026

## Quick Overview

Stockpile 1.3 is a big quality-of-life update for organizing and reading your list. You can now favourite items to pin them to the top, group them into your own collapsible categories, and switch to a compact view or quickly filter a long list. The detailed view gained buy/sell pressure and clearer market timing, and you can jump straight into Stockpile from the Grand Exchange window. There's also a new on-screen overlay so you can keep an eye on key items without opening the panel.

## Detailed Breakdown

### Tracked Item View

#### Favourites
Star your most-used items to pin them to the top of the list.
[#20](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/20)

#### Categories
Group your tracked items into your own collapsible categories.
[#22](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/22)

#### Compact mode
A tighter layout that fits more items on screen at once.
[#33](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/33)

#### Filter and search your list
Quickly narrow a long tracked list down to what you're looking for.
[#21](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/21)

#### Quantity-aware item icons
Item icons now reflect the stack size you're holding.
[#41](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/41)

#### Report a bug or request a feature
New footer buttons to send feedback without leaving the client.
[#34](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/34)

### Detailed Item View

#### Buy/sell pressure
See at a glance whether an item is under more buying or selling pressure.
[#6](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/6)

#### Last bought and sold times
Market info now shows when an item was most recently bought and sold.
[#18](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/18)

#### Wiki and live-price links
Jump straight to an item's wiki page or live-price page.
[#16](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/16)

### Grand Exchange

#### Open an item in Stockpile from the GE window
A button on the Grand Exchange buy/sell window opens that item in Stockpile.
[#11](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/11)

### On-screen Overlay

#### Track items on your screen
Show chosen items as an on-screen overlay so you can watch them without the panel open.
[#35](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/35)

### Bug Fixes

[#36](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/36) - Fixed high/low prices that could invert; added staleness tooltips, dimming, and a 5-minute window
[#27](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/27) - Market sections are now hidden in the detailed view for non-tradeable items
[#40](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/40) - Removed the stray scroll bar on the logged-out message

# 1.2 - June 26 2026

## Quick Overview

Stockpile 1.2 lets you make the list your own — drag your tracked items into whatever order you like. The detailed view now shows an item's description and a cleaner, colour-coded price graph. This release also fixes a batch of display problems so long names, the header, and untradeable items all look right.

## Detailed Breakdown

### Tracked Item View

#### Reorder your list
Drag tracked items into any order you want.
[#19](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/19)

### Detailed Item View

#### Item description under the quantity
The detailed view now shows the item's in-game description.
[#17](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/17)

#### Colour-coded price graph
The price graph line is now coloured to make trends easier to read.
[#5](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/5)

### Bug Fixes

[#13](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/13) - Fixed long item names being clipped in search results and blank in the tracked list
[#14](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/14) - Centered the "Stockpile" header in the main view
[#26](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/26) - Untradeable items now read "Item not tradeable" instead of "Unable to Load Price"
[#15](https://github.com/Oveduumnakal/Stockpile-Plugin/issues/15) - Centered and wrapped the logged-out placeholder message

# 1.1 - June 22 2026

## Quick Overview

Stockpile 1.1 brought live prices to your tracked items — high, low, and average values from the Grand Exchange and the wiki, along with each item's 24-hour change. It also started highlighting your tracked items on the ground and in your inventory so they're easy to spot.

## Detailed Breakdown

### Tracked Item View

#### Live prices for everything you track
High, low, and average prices from the Grand Exchange and the wiki, plus each item's 24-hour change.

#### On-screen item highlights
Tracked items are highlighted on the ground and in your inventory so they stand out.

### Note

Version 1.1 was released before issue tracking began, so it has no linked issues.
