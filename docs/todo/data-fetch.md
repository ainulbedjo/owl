# TODO: data fetch

Home and Detail are built and run on sample data. This list is everything left to make them load real data. Work top to bottom: each step needs the one before it, and each ends with a check you can run.

Find the places to change with:

```bash
grep -rn "TODO(data)\|once we add Coil" --include="*.kt" .
```

## Already done

- [x] Domain models: `Movie`, `MovieDetail`, `Review`, `ThemeSettings` in `:core/model`.
- [x] DTOs for the three list calls, the detail call, and the reviews call in `:core/network/dto`.
- [x] Mappers DTO to model, with unit tests, in `:core/data/mapper`.
- [x] `AppError`, `SectionState`, and `TmdbImage.url(path, size)` in `:core/common`.
- [x] `BuildConfig.ACCESS_TOKEN` from `local.properties` (the `API_KEY` field is unused).
- [x] Home and Detail UI, with every state and previews. Both take a `*UiState` and callbacks, so the ViewModels only have to produce state.
- [x] Navigation: `HomeRoute`, `DetailRoute`, and `AppNavHost` in `:app`.

## 1. Dependencies

Add to `buildSrc/.../Deps.kt` (check each version is current, and that Hilt and KSP work with AGP 9.1 and built-in Kotlin before relying on them):

- [x] OkHttp, and the logging interceptor for debug builds.
- [x] `Deps.RETROFIT` already exists. Add the Retrofit kotlinx.serialization converter (`com.squareup.retrofit2:converter-kotlinx-serialization`).
- [x] Hilt (`hilt-android`, compiler through KSP, `hilt-navigation-compose`) and the KSP plugin.
- [x] `lifecycle-viewmodel-compose` and `lifecycle-runtime-compose` (for `collectAsStateWithLifecycle`).
- [x] Coil 3 (`coil-compose`, `coil-network-okhttp`). Pinned to 3.3.0: newer Coil releases need Kotlin 2.3 or later, and this project is on 2.2.10.
- [ ] Test libraries: `kotlinx-coroutines-test`, Turbine, MockK.

Done when: the project syncs and `./gradlew :app:assembleDebug` passes.

## 2. Network layer, in `:core/network`

Contract: [api/tmdb_overview.md](../api/tmdb_overview.md), [api/movie_lists.md](../api/movie_lists.md), [api/movie_detail.md](../api/movie_detail.md), [api/movie_reviews.md](../api/movie_reviews.md).

- [x] `MovieApi`: a Retrofit interface with `popular()`, `topRated()`, `nowPlaying()`, `movieDetail(id)`, `reviews(id)`. Base URL `https://api.themoviedb.org/3/`. Page 1 only.
- [x] An OkHttp interceptor that adds `Authorization: Bearer ${BuildConfig.ACCESS_TOKEN}` and `accept: application/json`.
- [x] A `Json` instance with `ignoreUnknownKeys = true`, wired to the Retrofit converter.
- [x] `safeApiCall { }` that turns failures into `AppError`:
  - `IOException` to `Network`
  - HTTP 401 to `Unauthorized`, 404 to `NotFound`, 429 to `RateLimited`, 5xx to `Server`
  - anything else to `Unexpected`
  - **rethrow `CancellationException`**, never turn it into an error (see [home.md](../features/home.md#viewmodel)).
- [x] Decide the result type the repository returns (for example a small `AppResult<T>` with `Success` and `Failure(AppError)`), and put it in `:core/common`.

Done when: a unit test with MockWebServer returns a mapped `Movie` list for a sample JSON, and maps a 404 and a thrown `IOException` to the right `AppError`.

## 3. `MovieRepository`, in `:core/data`

- [x] An interface plus an implementation: `popular()`, `topRated()`, `nowPlaying()`, `movieDetail(id)`, `reviews(id)`. It returns domain models, never DTOs.
- [x] Reviews: a 404 means "no reviews", return an empty list, not an error ([detail.md](../features/detail.md#reviews)).
- [ ] A fake implementation for ViewModel tests.

Done when: repository tests pass against the fake API from step 2.

## 4. Hilt

- [x] An `@HiltAndroidApp` `Application` class in `:app`, and `android:name` in the manifest.
- [x] `@AndroidEntryPoint` on `MainActivity`.
- [x] Modules in `:core/di`: one provides OkHttp, `Json`, Retrofit, and `MovieApi`. One binds `MovieRepository`.

Done when: the app starts with no Hilt errors.

## 5. `HomeViewModel`

Spec: [features/home.md](../features/home.md#viewmodel).

- [x] `@HiltViewModel` with `StateFlow<HomeUiState>`. The three loads start together and run in parallel.
- [x] Map each result to `SectionState`: success with items to `Content`, success with none to `Empty`, failure to `Error`.
- [x] `onRetry(section)` reloads one section. `onTryAgain()` reloads every section in `Error`.
- [x] Open the offline sheet once when a section fails with `Network`, even when all three fail.
- [x] Ignore a late response from an earlier load (keep a job or a request id per section).
- [x] In `HomeNavigation.kt`, replace `HomeEntry`'s sample state with `hiltViewModel()` and `collectAsStateWithLifecycle()`. Then delete `home/data/FakeMovies.kt`, or keep it only for previews.

Done when: Home shows real movies. Turn on airplane mode and relaunch: the offline sheet appears once, then each section shows its Retry card.

## 6. `DetailViewModel`

Spec: [features/detail.md](../features/detail.md#viewmodel).

- [x] `@HiltViewModel` that reads the id with `savedStateHandle.toRoute<DetailRoute>()`.
- [x] Load the movie and the reviews in parallel, each into its own `SectionState`.
- [x] A `NotFound` on the movie stays `Error(AppError.NotFound)` (the screen already shows the "not available" state). A 404 on reviews is `Empty`.
- [x] Share sheet visibility, the offline sheet rule (one sheet when both calls fail), `onRetryMovie`, `onRetryReviews`, `onTryAgain`.
- [x] A `Channel` of message kinds (`DetailMessage`) exposed as a `Flow`. The screen turns each into text and passes it to `DetailScreen(messages = ...)`.
- [x] In `DetailNavigation.kt`, replace `DetailEntry`'s sample state with the ViewModel.

Done when: tapping a Home card shows the real movie and its reviews, and a bad id shows the not-found state.

## 7. Real images (Coil)

`TmdbImage.url(path, size)` already builds the URLs. Sizes: Popular card `W500`, poster cards `W342`, Detail backdrop `W780`, Favorites row `W185`, share thumbnail and review avatar `W154`.

- [x] Add a `PosterImage` to `:designsystem` that loads a URL with Coil and falls back to `PosterArt` when the path is null or loading fails.
- [x] Use it where the code says "once we add Coil": `PopularCard`, `PosterCard`, the Detail backdrop, the share sheet, and the review avatar in `ReviewsSection`.
- [x] Keep the Coil dependency in `:designsystem`. The features pass a URL, they do not use Coil.

Done when: posters appear, and a movie with no poster still shows the colored placeholder.

## 8. Favorites (Room)

Needed by Detail's Save button and Home's badge count. Spec: [architecture.md](../architecture.md#favorites-room) and [features/favorites.md](../features/favorites.md).

- [ ] `FavoriteMovieEntity`, `FavoriteDao`, and the database in `:core/database`.
- [ ] `FavoriteRepository`: `observeIsFavorite(id)`, `observeCount()`, `observeAll()`, `toggle(movie)` (atomic), `remove(id)`.
- [ ] Wire `isFavorite` in `DetailViewModel` and `favoriteCount` in `HomeViewModel` from these Flows.

Done when: Save on Detail survives an app restart, and Home's badge follows it.

## 8b. Favorites screen

The layout is built on sample data: `FavoritesScreen`, `FavoriteRow`, the empty, loading and error states, previews, and navigation (`FavoritesRoute`, wired in `AppNavHost`). What is left is the data:

- [ ] `FavoritesViewModel` (`@HiltViewModel`): `observeAll()` mapped to `Content` or `Empty`, a `catch` mapped to `Error`, `stateIn(viewModelScope, WhileSubscribed(5_000), Loading)`. Spec: [features/favorites.md](../features/favorites.md#viewmodel).
- [ ] `onRemoveClick(id)` calls `FavoriteRepository.remove(id)` and sends the "Removed from favorites" message.
- [ ] In `FavoritesNavigation.kt`, replace the sample state in `FavoritesEntry` with the ViewModel.
- [ ] Tests for the ViewModel (cases in [favorites.md](../features/favorites.md#tests)).

## 9. Tests to add

| Target | Cases |
|---|---|
| `safeApiCall` | Each HTTP code and `IOException` map to the right `AppError`. `CancellationException` is rethrown. |
| `MovieRepository` | Success maps to models. 404 on reviews is an empty list. |
| `HomeViewModel` | Three sections reach `Content` or `Empty`. One failure leaves the others alone. Offline sheet opens once. A late response is ignored. |
| `DetailViewModel` | Id comes from `SavedStateHandle`. Movie error leaves reviews alone, and the reverse. 404 gives not found on the movie and `Empty` on reviews. |

The full case lists are in the Tests section of [home.md](../features/home.md#tests) and [detail.md](../features/detail.md#tests).
