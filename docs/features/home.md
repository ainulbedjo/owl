# Home

Home is the start screen. It shows three movie lists from TMDB and gives access to Favorites. Module: `:feature:home`. Architecture context: [architecture.md](../architecture.md). Data contract: [api/movie_lists.md](../api/movie_lists.md).

## Requirements covered

From [requirements.md](../requirements/requirements.md):

- [ ] Popular movies list.
- [ ] Top rated movies list.
- [ ] Now playing movies list.
- [ ] Favorite icon button that navigates to the Favorite List page.

Also on this screen: a theme picker entry point (see [Theme picker](#theme-picker-optional)).

## User stories

- As a viewer, I see what is popular, top rated, and now playing as soon as the app opens.
- As a viewer, I tap a movie to see its details.
- As a viewer, I tap the heart icon to see my favorites, and I can see how many I have saved.
- As a viewer with a bad connection, I still see the sections that loaded, and I can retry the ones that did not.
- As a viewer, I can change the color theme without leaving Home.

## Layout

Reference: the "Home" boards in the UI prototype canvas. The styling follows Material 3 Expressive (see `design/design_system.md`, planned).

| Part | Content |
|---|---|
| App bar | Title "MovieDB", subtitle "What is on tonight". Two round icon buttons: theme picker and favorites. The favorites button shows a count badge. |
| Popular | Section title and subtitle. A horizontal strip of wide cards, snapping one card at a time. |
| Top Rated | Section title and subtitle. A horizontal strip of posters, each with a rating badge, then title and release date below. |
| Now Playing | Section title and subtitle. A horizontal strip of wide cards: a 16:9 backdrop with the release date on it, then the title and rating below. |

The whole screen scrolls vertically. Each strip scrolls horizontally.

### Cards

| Card | Size | Shows |
|---|---|---|
| Popular card | About 296 x 340 dp, corner radius 28 dp | Poster (`w500`), a rating chip (star and value), title, release date. Title sits on a dark gradient for contrast. |
| Poster card | 132 x 198 dp, corner radius 20 dp | Poster (`w342`) and a rating badge. Used by Top Rated. Title (one line, ellipsis) and release date below the image. |
| Wide card | 252 dp wide, 16:9 image, corner radius 16 dp | Backdrop (`w780`, or the poster when there is no backdrop) with the release date in a dark pill at the bottom left. Title (one line, ellipsis) and the rating (star and value) below. Used by Now Playing. |

Image sizes come from [api/tmdb_overview.md](../api/tmdb_overview.md#images). Cards are single tap targets that open Detail. Pressing a card morphs its corners (Expressive shape feedback).

Section copy:

| Section | Subtitle |
|---|---|
| Popular | What everyone is watching |
| Top Rated | Favorites of critics and viewers |
| Now Playing | In theaters right now |

## States

Each section has its own state, so one failing list never affects the others. The state type is `SectionState` from [architecture.md](../architecture.md#state-and-error-model).

| State | Popular | Top Rated and Now Playing |
|---|---|---|
| Loading | One large skeleton card with the morphing loading indicator, and a second skeleton card peeking in | Three skeleton posters pulsing |
| Content | The card strip | The poster strip |
| Empty | A small card: "No movies to show right now." | Same |
| Error | Inline error card with message and **Retry** | Same |

Notes:

- **Empty** is not in the prototype yet. It is defined here and is to be added to the design docs.
- **Error** shows the inline card for every error except `Network`. The card text depends on the error: "Could not load {section}. Check your connection and try again." is the default, and the rate-limit case says "Too many requests. Try again in a moment."
- **Network** opens the no-connection bottom sheet once for the screen. After the sheet is dismissed, the failed sections show their inline cards. The rule is in [api/tmdb_overview.md](../api/tmdb_overview.md#where-errors-are-shown).

### No-connection sheet

Owl illustration, title "No connection", hint text, **Try again** and **Open favorites**.

- **Try again** reloads every section that is in `Error`. If one loads, the sheet closes. If the device is still offline, the sheet stays open.
- **Open favorites** closes the sheet and goes to Favorites, which works offline.

## Data

| Source | Call | Used for |
|---|---|---|
| `MovieRepository` | `popular()`, `topRated()`, `nowPlaying()` | The three sections. Page 1 only. |
| `FavoriteRepository` | `observeCount()` | The badge on the favorites button. Updates live. |
| `ThemeRepository` | `settings: Flow<ThemeSettings>`, `update(...)` | Current theme and the picker. |

The three calls start together when the ViewModel is created and run in parallel.

## ViewModel

`HomeViewModel` (`@HiltViewModel`). It injects `MovieRepository`, `FavoriteRepository`, and `ThemeRepository`.

State:

```kotlin
data class HomeUiState(
    val popular: SectionState<List<Movie>>,
    val topRated: SectionState<List<Movie>>,
    val nowPlaying: SectionState<List<Movie>>,
    val offlineSheetVisible: Boolean,
    val favoriteCount: Int,
    val theme: ThemeSettings,
)
```

Actions (plain functions called by the screen):

| Action | Effect |
|---|---|
| `onRetry(section)` | Sets that section to `Loading` and reloads only it. |
| `onTryAgain()` | Reloads every section currently in `Error`. |
| `onDismissOfflineSheet()` | Sets `offlineSheetVisible` to `false`. |
| `onThemeChange(mode, paletteId)` | Saves the new settings. The theme updates for the whole app. |

Rules:

- `offlineSheetVisible` becomes `true` when a section fails with `Network` and it is not already `true`.
- A section result is applied only for the current load, so a late response from an earlier try cannot overwrite a newer one.
- `CancellationException` is never converted to an error.

Navigation is not handled by the ViewModel. The screen calls the callbacks it receives (see below).

## Theme picker (optional)

It is a small addition that shows Material 3 Expressive theming.

- The palette icon in the app bar opens a modal bottom sheet titled "Appearance".
- Mode: a connected button group with System, Light, and Dark. The selected button has a check mark and fully rounded corners. Unselected buttons have small inner corners.
- Palette: a grid of swatches (Wallpaper, Violet, Ocean, Lagoon, Forest, Sunset, Rose). The selected swatch has a ring and a check mark.
- Changes apply at once, with no confirm button.
- **Wallpaper** needs Android 12 (API 31). On Android 11 (`MIN_SDK` is 30) the swatch is hidden.
- Visible-only UI state (is the sheet open) lives in the screen with `rememberSaveable`. The saved settings live in the local database (Room), and `MainViewModel` in `:app` owns them.

Details of the color system will be in `design/design_system.md` (planned).

## Navigation

`HomeRoute` is the start destination. The screen receives these callbacks from `:app`:

```kotlin
fun NavGraphBuilder.homeScreen(
    onMovieClick: (movieId: Int) -> Unit,
    onFavoritesClick: () -> Unit,
)
```

| From | To | Trigger |
|---|---|---|
| Home | Detail (`movieId`) | Tap any card. |
| Home | Favorites | Tap the heart button, or **Open favorites** in the offline sheet. |

Back on Home leaves the app.

## Edge cases

| Case | Behavior |
|---|---|
| `poster_path` is `null` | A colored placeholder with the title's first letter. The card still works. |
| `release_date` empty | The date line is hidden. |
| Very long title | One line with ellipsis on posters. Wrapped on the Popular card, up to two lines. |
| `vote_average` is `0.0` | The rating chip or badge is hidden. |
| The same movie is in two sections | Allowed. Each card opens the same Detail. |
| Rotation or other configuration change | State survives in the ViewModel. No reload. |
| App process was killed | The screen reloads from the start. There is no response cache in v1. |
| Retry tapped twice quickly | One request. The section is already `Loading`. |
| Offline at launch | Sheet opens once. All three sections end in `Error`. After dismiss, each shows its card. |
| Favorites count is `0` | The badge is hidden. |
| A section returns zero movies | `Empty`, not `Error`, and no Retry. |

## Accessibility

- Every card has a content description such as "Midnight Orchard, rated 8.4".
- The favorites button says "Favorites, 3 saved". The theme button says "Change theme".
- All tap targets are at least 48 dp. Cards are far larger.
- Section titles are marked as headings, so screen readers can jump between them.
- The loading indicator has a "Loading" description. Error cards use a polite live region so the failure is announced.
- Text and badge colors come from paired theme roles, so contrast holds in every palette and in dark mode.

## Out of scope

Pagination, search, filters, pull to refresh, and a response cache. Page 1 only, as decided in the [README](../README.md#decisions-so-far).

## Tests

| Target | Cases |
|---|---|
| `HomeViewModel` | Initial state is three `Loading` sections. Each section reaches `Content` or `Empty` on success. One failing section leaves the others unchanged. `onRetry` reloads only that section. `Network` failure sets `offlineSheetVisible` once even when all three fail. Dismiss, then retry success keeps the sheet closed. Favorite count follows the repository `Flow`. Late response from an older load is ignored. |
| Mappers | Covered in [api/movie_lists.md](../api/movie_lists.md#mapping-to-the-domain-model). |
| Previews | Content, loading, empty, one section in error, all sections in error, offline sheet, dark theme. |
