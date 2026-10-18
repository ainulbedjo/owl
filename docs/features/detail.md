# Detail

Detail shows one movie: a backdrop, the main facts, the overview, and user reviews. The viewer can save the movie as a favorite or share it. Module: `:feature:detail`. Architecture context: [architecture.md](../architecture.md). Data contracts: [api/movie_detail.md](../api/movie_detail.md) and [api/movie_reviews.md](../api/movie_reviews.md).

## Requirements covered

From [requirements.md](../requirements/requirements.md):

- [ ] Show movie reviews.
- [ ] Favorite: save the movie to local storage and remove it from local storage.
- [ ] Share: show a bottom sheet.

The screen also shows a large backdrop, title, release date, and description. Those come from the movie detail call.

## User stories

- As a viewer, I see the movie's backdrop, title, release date, runtime, rating, genres, and description.
- As a viewer, I read what other people wrote about the movie.
- As a viewer, I tap Save to keep the movie in my favorites, and tap again to remove it. The button always shows the current state.
- As a viewer, I tap Share and get a bottom sheet with ways to send the movie to someone.
- As a viewer on a bad connection, I see what went wrong and can retry, without losing the part of the screen that did load.

## Layout

Reference: the "Detail" and "Share bottom sheet" boards in the UI prototype canvas.

| Part | Content |
|---|---|
| Header | Backdrop image, about 340 dp tall, with a round back button at the top left. |
| Panel | A surface that overlaps the header and has a large top corner radius (48 dp). It holds everything below. |
| Title block | Title, tagline (when present). |
| Facts | Chips: release date, runtime, rating. A second row of genre chips. |
| Overview | The heading "Overview" and the description. |
| Reviews | The heading "Reviews" with a count pill, then review cards. |
| Floating toolbar | Pinned at the bottom: a **Save / Saved** toggle button with a heart icon, and a **Share** icon button. |

The screen scrolls. The toolbar stays in place above the content.

### Save button

A toggle with a label. It uses the Expressive shape morph: pill-shaped when off, a smaller corner radius when on. The icon is an outlined heart when off and a filled heart when on. The label is "Save" when off and "Saved" when on.

### Review card

| Part | Content |
|---|---|
| Avatar | The author's image when there is one, otherwise the first letter in a decorative cookie shape. |
| Name and date | Display name and review date. |
| Rating pill | A star and one decimal. Reads "No rating" without a star when the review has no rating. |
| Text | Three lines, then **Read more**. Expanding shows the whole text and the action becomes **Show less**. |

The "Read more" action appears only when the text is actually cut off. This is measured from the text layout, not guessed from length.

## States

The movie and the reviews are two separate calls with two separate states.

### Movie

| State | Shown |
|---|---|
| Loading | Header placeholder, skeleton lines for title, chips, and overview. The toolbar buttons are visible but disabled. |
| Content | The full layout. |
| Error | An inline card in the panel with a message and **Retry**. The back button keeps working. |
| Not found (`404`) | "This movie is not available." with a **Back** action. No Retry, because it cannot succeed. |

### Reviews

| State | Shown |
|---|---|
| Loading | Two skeleton cards. |
| Content | The review cards, in API order. |
| Empty | A dashed card: "No reviews yet" and "Nobody has reviewed this movie so far." This is a normal result, not an error. |
| Error | An inline card with **Retry**. The movie part of the screen stays visible. |

A `404` on reviews is treated as empty.

### No-connection sheet

When either call fails with `Network`, the no-connection bottom sheet opens once for the screen. If both calls fail, only one sheet opens. After it is dismissed, the failed parts show their inline cards. **Try again** reloads every part in `Error`. **Open favorites** goes to Favorites. The rule is in [api/tmdb_overview.md](../api/tmdb_overview.md#where-errors-are-shown).

## Favorite behavior

Favorites are stored in Room as a snapshot, so Favorites works offline. The schema is in [architecture.md](../architecture.md#favorites-room).

| Action | What happens |
|---|---|
| Tap **Save** when not a favorite | The repository inserts a snapshot built from the loaded `MovieDetail`: `id`, `title`, `posterPath`, `releaseDate`, `overview`, `voteAverage`, and `addedAt` set to now. A snackbar says "Saved to favorites". |
| Tap **Saved** when a favorite | The repository deletes the row by `id`. A snackbar says "Removed from favorites". |

Rules:

- **The button reflects the database, not a local flag.** `isFavorite` comes from `FavoriteRepository.observeIsFavorite(id)`, a Room `Flow`. If the movie is removed on the Favorites screen, this screen is already correct when the user comes back.
- **The toggle is atomic.** The repository checks and writes inside one database transaction, so two quick taps cannot insert twice or leave an inconsistent state. The primary key and replace-on-conflict make an insert idempotent.
- **The button is disabled until the movie has loaded**, because the snapshot is built from the detail response. It is also disabled when the movie is in `Error`. Removing an existing favorite while the movie failed to load is not offered here. The user can remove it from the Favorites screen.
- The favorite action works offline, since it touches only local storage.

## Share behavior

Tapping **Share** opens a modal bottom sheet. It is disabled until the movie has loaded, because it needs the title.

Sheet content:

| Part | Content |
|---|---|
| Movie summary | Small poster (`w154`), title, and the link text. |
| Actions | **Messages**, **Copy link**, **Email**, **More**. |

The shared link is the TMDB movie page: `https://www.themoviedb.org/movie/{id}`. The text is `"{title}\nhttps://www.themoviedb.org/movie/{id}"`. This URL form is the public TMDB web address and is not part of the API reference.

| Action | Implementation |
|---|---|
| Messages | `ACTION_SENDTO` with an `smsto:` URI and the text as `sms_body`. |
| Copy link | Puts the link on the clipboard, closes the sheet, shows the snackbar "Link copied". |
| Email | `ACTION_SENDTO` with a `mailto:` URI, the title as subject, and the text as body. |
| More | `ACTION_SEND` with `text/plain` inside `Intent.createChooser`, the system share sheet. |

Every launch is wrapped so that `ActivityNotFoundException` shows the snackbar "No app available to share with" instead of crashing. The sheet closes after any action. Tapping the scrim or pressing back closes it without an action.

Intents are started from the screen composable, because they need a `Context`. The ViewModel only owns whether the sheet is visible.

## Data

| Source | Call | Used for |
|---|---|---|
| `MovieRepository` | `movieDetail(id)` | The movie part. |
| `MovieRepository` | `reviews(id)` | The reviews part. Page 1 only. |
| `FavoriteRepository` | `observeIsFavorite(id)` | The Save button state. |
| `FavoriteRepository` | `toggle(movie)` | Insert or delete. |

Both network calls start together when the ViewModel is created and run in parallel.

## ViewModel

`DetailViewModel` (`@HiltViewModel`). It injects `MovieRepository` and `FavoriteRepository`, plus `SavedStateHandle`. The id is read from the type-safe route with `savedStateHandle.toRoute<DetailRoute>()`, so it survives process death.

State:

```kotlin
data class DetailUiState(
    val movie: SectionState<MovieDetail>,
    val reviews: SectionState<List<Review>>,
    val isFavorite: Boolean,
    val shareSheetVisible: Boolean,
    val offlineSheetVisible: Boolean,
)
```

Actions:

| Action | Effect |
|---|---|
| `onRetryMovie()` | Reloads the movie part only. |
| `onRetryReviews()` | Reloads the reviews part only. |
| `onTryAgain()` | Reloads every part currently in `Error`. |
| `onFavoriteClick()` | Toggles the favorite. Does nothing unless the movie is `Content`. |
| `onShareClick()` | Shows the share sheet. Does nothing unless the movie is `Content`. |
| `onDismissShare()` | Hides the share sheet. |
| `onDismissOfflineSheet()` | Hides the no-connection sheet. |

One-shot effects, sent through a `Channel` and collected once by the screen: `ShowMessage(text)` for snackbars. Navigation uses callbacks, not effects.

Expanded state of review cards ("Read more") is purely visual, so it lives in the screen with `rememberSaveable`, keyed by review id.

## Navigation

```kotlin
@Serializable data class DetailRoute(val movieId: Int)

fun NavGraphBuilder.detailScreen(
    onBack: () -> Unit,
    onFavoritesClick: () -> Unit,
)
```

| From | To | Trigger |
|---|---|---|
| Home | Detail | Tap a card. |
| Favorites | Detail | Tap a row. |
| Detail | previous screen | Back button, system back, or **Back** on the not-found state. |
| Detail | Favorites | **Open favorites** in the no-connection sheet. |

Only the id is passed. The screen loads everything else itself, so it also works after process death.

## Edge cases

| Case | Behavior |
|---|---|
| No backdrop | Fall back to the poster, then to a colored placeholder. |
| No tagline | The line is hidden. |
| Runtime is `0` or missing | The runtime chip is hidden. |
| Release date empty | The date chip is hidden. |
| Overview blank | Shows "No description available." |
| No genres | The genre row is hidden. |
| Review without rating | Shows "No rating". |
| Review text is very long | Collapsed to three lines, expandable. |
| Review author has a blank name | Falls back to the username. |
| Review avatar missing or fails to load | Shows the initial in a decorative shape. |
| Movie loaded, reviews failed | Reviews show their own error card. The rest stays usable. |
| Movie failed, reviews loaded | The movie part shows its error card. Reviews still display. Save and Share stay disabled. |
| Save tapped twice quickly | One toggle at a time, applied atomically. The state ends consistent with the database. |
| Movie removed from favorites elsewhere | The button updates from the database `Flow`. |
| Rotation | State survives in the ViewModel. Sheets stay open. Expanded reviews are kept by `rememberSaveable`. |
| Process death | The id is restored from the route. Data reloads. |
| Share target app missing | A snackbar. No crash. |
| Back pressed with a sheet open | Closes the sheet first. |

## Accessibility

- The back button says "Back". The toolbar buttons announce their state: the Save button is a toggle that reads as "Save, off" or "Saved, on", and Share says "Share".
- The backdrop is decorative and hidden from screen readers.
- Chips are read in order: date, runtime, rating, genres.
- Review cards are one reading unit: author, date, rating, then text. **Read more** has the description "Read more, review by {author}".
- Tap targets are at least 48 dp. The "Read more" action has a 44 dp-tall touch area at minimum.
- Sheets are modal: focus moves into them and returns to the button that opened them when they close.
- Colors come from paired theme roles. Text on the backdrop gradient keeps at least 4.5:1 contrast.

## Out of scope

More than the first page of reviews, posting or rating reviews, trailers, cast, similar movies, and refreshing a stored favorite snapshot. A snapshot is taken when the movie is saved and is not updated later.

## Tests

| Target | Cases |
|---|---|
| `DetailViewModel` | Id is read from `SavedStateHandle`. Initial state loads movie and reviews in parallel. Movie error leaves reviews unaffected, and the reverse. Empty reviews give `Empty`, not `Error`. `404` on the movie gives not found, and `404` on reviews gives `Empty`. `Network` failure on both opens one offline sheet. `onFavoriteClick` before the movie loads does nothing. Toggle inserts a snapshot with the expected fields, then deletes it. `isFavorite` follows the repository `Flow`. Share sheet shows and hides. |
| `FavoriteRepository.toggle` | Insert when absent, delete when present, and two concurrent calls end consistent. |
| Mappers | Covered in [api/movie_detail.md](../api/movie_detail.md#mapping-to-the-domain-model) and [api/movie_reviews.md](../api/movie_reviews.md#mapping-to-the-domain-model). |
| Previews | Content, loading, movie error, reviews error, empty reviews, not found, favorite on, share sheet, offline sheet, dark theme. |
