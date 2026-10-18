# Favorites

Favorites lists the movies the viewer saved. It reads only local storage, so it works with no connection. Module: `:feature:favorites`. Architecture context: [architecture.md](../architecture.md). Storage schema: [architecture.md](../architecture.md#favorites-room).

## Requirements covered

From [requirements.md](../requirements/requirements.md):

- [ ] Show the list of the user's favorite movies.

Removing a favorite from this list is a small addition. The PDF requires removal on Detail, and the wireframe has no remove control here. It is cheap, and it fixes the case where a viewer wants to clean up without opening each movie.

## User stories

- As a viewer, I see the movies I saved, newest first, with poster, title, release date, and a short description.
- As a viewer, I tap a movie to open its details.
- As a viewer, I remove a movie from the list with one tap.
- As a viewer with no connection, the list still works.
- As a new viewer with nothing saved, I see what this screen is for and a way back to browsing.

## Layout

Reference: the "Favorites" and "Favorites, empty" boards in the UI prototype canvas.

| Part | Content |
|---|---|
| App bar | A round back button, the title "Favorite Movie" (the wording in the PDF wireframe), and the subtitle with the count, for example "3 saved". |
| List | A vertical list of rows, 12 dp apart. |

### Row

A rounded container (28 dp corner radius, 12 dp padding) holding:

| Part | Content |
|---|---|
| Poster | 92 x 132 dp, `w185`. Corner radius 16 dp, which is the container radius minus its padding (28 - 12), so nested corners look balanced. |
| Title | Up to two lines, ellipsis after that. |
| Release date | One line, in the accent color. |
| Description | The stored overview, truncated to three lines with ellipsis. This matches the "truncated description" in the wireframe. |
| Remove | A heart icon button at the top right of the row. The heart is filled because every item here is a favorite. |

Tapping the poster or the title opens Detail. Tapping the heart removes the movie.

## States

| State | Shown |
|---|---|
| Loading | Three skeleton rows. This lasts only until Room emits its first value, which is normally instant, so the skeleton is shown only if loading takes longer than about 150 ms. This avoids a flash. |
| Content | The list. |
| Empty | A large decorative shape with a heart, the title "No favorites yet", the text "Tap the heart on a movie to keep it here, even offline.", and a **Browse movies** button. |
| Error | A generic inline card with **Retry**. Reading from Room failing is rare, but a `catch` on the flow keeps the screen from crashing. |

The no-connection sheet never appears on this screen, because nothing here uses the network.

**Browse movies** goes back to Home.

## Data

| Source | Call | Used for |
|---|---|---|
| `FavoriteRepository` | `observeAll(): Flow<List<Movie>>` | The list, ordered by `addedAt` descending. |
| `FavoriteRepository` | `remove(id)` | The heart button. |

The repository maps `FavoriteMovieEntity` to the shared `Movie` model. The poster path is stored, not a full URL, so this screen picks the image size (`w185`). See [api/tmdb_overview.md](../api/tmdb_overview.md#images).

There is no network call on this screen. Because the list is a Room `Flow`, anything that changes favorites elsewhere (the Save button on Detail) updates this list with no refresh code.

## ViewModel

`FavoritesViewModel` (`@HiltViewModel`). It injects `FavoriteRepository`.

State:

```kotlin
data class FavoritesUiState(
    val favorites: SectionState<List<Movie>>,
)
```

The state comes from `repository.observeAll()`, mapped to `Content` or `Empty`, with `catch` mapped to `Error`, started with `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Loading)`.

Actions:

| Action | Effect |
|---|---|
| `onRemoveClick(id)` | Calls `repository.remove(id)`. The list updates through the `Flow`. Sends the effect `ShowMessage("Removed from favorites")`. |
| `onRetry()` | Re-collects the flow after an error. |

Navigation uses callbacks, not the ViewModel.

## Navigation

```kotlin
@Serializable data object FavoritesRoute

fun NavGraphBuilder.favoritesScreen(
    onBack: () -> Unit,
    onMovieClick: (movieId: Int) -> Unit,
    onBrowseClick: () -> Unit,
)
```

| From | To | Trigger |
|---|---|---|
| Home | Favorites | The heart button in the app bar, or **Open favorites** in the no-connection sheet. |
| Detail | Favorites | **Open favorites** in the no-connection sheet. |
| Favorites | Detail | Tap a row. |
| Favorites | previous screen | Back button or system back. |
| Favorites | Home | **Browse movies** in the empty state. |

Back returns to wherever the viewer came from. Opening Favorites from the offline sheet on Detail therefore returns to Detail.

## Edge cases

| Case | Behavior |
|---|---|
| Poster path is `null` | A colored placeholder with the title's first letter. |
| Release date missing | The date line is hidden. |
| Overview blank | The description line is hidden. |
| Very long title | Two lines, then ellipsis. |
| Last favorite removed | The list changes to the empty state through the same `Flow`. |
| A favorite is removed on Detail, then the viewer goes back | The list is already updated. |
| Removed by mistake | There is no undo in v1. The movie can be saved again from Detail. |
| Remove tapped twice | The second call deletes nothing. It is harmless. |
| Rotation | State survives in the ViewModel. List scroll position is kept. |
| App restarts | The list comes back from Room. |
| Snapshot is old | The stored title, overview, and poster are what was saved. They are not refreshed. |
| Many favorites | `LazyColumn` with a stable `key` of the movie id, so rows animate when one is removed. |
| Database read fails | The `Error` state with Retry. No crash. |

## Accessibility

- The remove button says "Remove {title} from favorites" and has a 48 dp touch area, though the icon is 24 dp.
- Poster and title together act as one button, "Open {title}".
- The list announces its length. The count subtitle is a live region, so removing an item is announced.
- The empty state heading is a heading and the **Browse movies** button is the first focus target.
- Rows keep the same corner nesting and color roles as in the prototype, so contrast holds in every palette and in dark mode.

## Out of scope

Sorting options, search within favorites, undo, multi-select, sync across devices, and refreshing the stored snapshot.

## Tests

| Target | Cases |
|---|---|
| `FavoritesViewModel` | First emission is `Loading`, then `Content` for a non-empty list and `Empty` for an empty one. The order is newest first. `onRemoveClick` calls the repository and sends the message effect. Removing the last item moves to `Empty`. A failing flow becomes `Error`, and `onRetry` recovers. |
| `FavoriteDao` (instrumented, optional) | Insert, delete, ordering by `addedAt`, replace on conflict, and the same movie inserted twice leaves one row. |
| Entity mapper | Entity to `Movie` with null poster, null date, empty overview. |
| Previews | Content, empty, loading, error, long title, missing poster, dark theme. |
