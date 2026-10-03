# Brave parity notes (Car Browser)

Reference APK analyzed locally: **Brave 1.96.61** (`com.brave.browser`, Chromium-based).

Extracts and APK copies live under `reference/brave-apk-extract/` (gitignored). Do not commit Brave binaries or decompiled assets.

## What we mirror (UX, not code)

| Area | Brave pattern | Car Browser implementation |
|------|----------------|----------------------------|
| Bottom chrome | Home, Bookmarks, **Search**, Tabs, Menu | `BraveBottomNavigationBar` + `MainActivitySetup` |
| Search | Focus **top omnibox** / start-page search, not tab switcher search | `StartPageManager.requestFocusTopSearchBar()`, `BrowserTopSearchController` |
| Tab switcher | Full-height sheet, “Search your tabs”, 2-column grid, group folder cards | `TabManagerSheet`, `TabGridViews` |
| Tab groups | Drag to group/ungroup; folder card colors; **Edit group colour** cycles palette; open group → grid + overflow menu | `TabGridDragState`, `TabGroupDetailScreen`, `TabGroupColors`, `TabGroupPreferences` |
| Web page | Pull down to refresh when at scroll top | `SwipeRefreshLayout` per tab |
| Menu | New tab → New private → Bookmarks / History / Downloads → Settings | `BraveBrowserMenuSheet` |

## APK inspection (1.96.61)

- Confirmed strings include **“Search your tabs”**, tab group actions, **bottom_toolbar_tab_switcher**, **bottom_new_tab_button**.
- UI is mostly Chromium (`org.chromium.chrome.browser.*`); layouts are not copied—only behavior and layout **patterns** are matched in Compose.

## Open-source reference (behavior only)

Brave’s Android UI builds on Chromium tab management. Useful read-only pointers (do not copy proprietary Brave assets):

- [brave-core](https://github.com/brave/brave-core) — `chrome/android/features/tab_ui/.../tab_management/` (grid tab switcher, groups).
- [Chromium tab groups](https://source.chromium.org/chromium/chromium/src/+/main:components/tab_groups/) — group color semantics.

## When extending parity

1. Compare with Brave on device or screenshots.
2. Re-run `aapt dump` on a new APK into `reference/brave-apk-extract/` if needed.
3. Bump `versionCode` / `versionName` and run `assembleDebug` per project rules.
