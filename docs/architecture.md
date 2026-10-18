# Architecture

MovieDB is a multi-module Android app. The UI is Jetpack Compose with Material 3 Expressive, the presentation pattern is **MVVM with unidirectional data flow**, and dependencies are injected with **Hilt**. This page fixes the structure before code starts. Library choices and their reasons are in [libraries.md](libraries.md). The data contract is in [api/tmdb_overview.md](api/tmdb_overview.md).

## Goals that shape the design

From [requirements/requirements.md](requirements/requirements.md): no bugs and no crashes, every required feature works, library choices are justified, and MVVM, Hilt, Jetpack, and unit tests earn bonus points. The design therefore favors:

- **Small and obvious over clever.** Three screens do not need a use-case layer or a module per layer.
- **Failure is a first-class state.** Every screen models loading, content, empty, and error explicitly.
- **Testable seams.** Mappers are pure functions, repositories sit behind interfaces, and ViewModels take their dependencies by constructor.

## Modules

```
:app
 ├── :feature:home       ──┐
 ├── :feature:detail     ──┼──> :core           (data and logic, no Compose)
 ├── :feature:favorites  ──┤
 │                         └──> :designsystem   (Compose UI, no data layer)
 ├── :core
 └── :designsystem
```

`:core` and `:designsystem` depend on nothing. Features use both.

| Module | Type | Owns |
|---|---|---|
| `:app` | Android application | `@HiltAndroidApp` `Application`, `MainActivity`, the `NavHost`, cross-feature navigation wiring, the theme host. |
| `:core` | Android library | Network, database, preferences, repositories, domain models, error model. **No Compose.** |
| `:designsystem` | Android library | Theme, color palettes, typography, shape and motion helpers, and generic components that two or more features share. **Compose only, and no knowledge of `Movie` or the data layer.** |
| `:feature:home` | Android library | Home screen, its ViewModel, and its navigation entry. |
| `:feature:detail` | Android library | Detail screen, its ViewModel, and its navigation entry. |
| `:feature:favorites` | Android library | Favorites screen, its ViewModel, and its navigation entry. |

Dependency rules:

1. `:app` depends on every module.
2. Feature modules depend on `:core` and `:designsystem` only. **A feature never depends on another feature.**
3. `:core` and `:designsystem` depend on no other project module, and not on each other.
4. A feature cannot navigate to another feature directly. See [Navigation](#navigation).
5. **A thing moves into a shared module only when a second feature needs it.** Until then it lives in the feature that uses it.

### Where does a file go?

Ask these three questions in order:

1. Does it import `androidx.compose`? Then it is not `:core`.
2. Does it know about `Movie`, `SectionState`, a repository, or any other data type? Then it is not `:designsystem`.
3. Is only one feature using it? Then it stays in that feature.

The module boundary makes the answer checkable: a wrong import fails the build. To keep `:designsystem` free of data types, its components take plain values (`movieId: Int`, `title: String`, `rating: Double`) and ready-made text, never a `Movie` or an `AppError`. For the same reason `MoviedbTheme` takes `paletteId` and `darkTheme`, not `ThemeSettings`. `:app` turns the stored settings into those two values.

Examples from the Home screen:

| Piece | Lives in | Why |
|---|---|---|
| `MoviedbTheme`, palettes, `pressedCornerShape` | `:designsystem` | Every screen uses the theme. |
| `RatingChip`, `RatingBadge`, `PosterArt`, `ErrorCard`, `EmptyCard`, `OfflineSheet` | `:designsystem` | Home, Detail, and Favorites all need them, and they need only plain values. |
| `PopularCard`, `PosterCard`, `PosterSection`, loading skeletons | `:feature:home` | Only Home shows these strips. They take a `Movie`. |
| `SectionStateContent`, the error and empty wording | `:feature:home` | It maps `SectionState` and `AppError` to text. |
| `AppearanceSheet`, `ic_palette` | `:feature:home` | The theme picker is opened only from Home. |
| `Movie`, `SectionState`, `AppError`, `ThemeSettings` | `:core` | Plain Kotlin that every layer shares. |

**Why `:designsystem` is separate from `:core`.** Data code and Compose UI change for different reasons and need different dependencies. With one module, UI components drift into `:core` next to DTOs and repositories, and every file feels like it could go anywhere. Two modules give a yes-or-no test (does it use Compose?). It also keeps `:core` fast to compile and test: it has no Compose plugin and no Compose dependencies.

**Why one `:core` instead of `:core:network`, `:core:database`, and so on.** Splitting data code into many modules pays off in large teams and for build parallelism. For three screens it adds Gradle files, `Deps` entries, and Hilt wiring without a benefit. Packages inside `:core` keep the boundaries visible. If `:core` grows, the packages below are the split lines.

**Why shared repositories live in `:core`.** `MovieRepository` is used by Home and Detail, and `FavoriteRepository` by Detail and Favorites. Putting them in a feature would force one feature to depend on another, which rule 2 forbids.

Build configuration follows the existing setup: `buildSrc/Deps.kt` is the single source for versions, SDK levels, and plugin ids. New modules use namespaces under `app.moviedb` (`app.moviedb.core`, `app.moviedb.designsystem`, `app.moviedb.home`, and so on) and the same SDK constants. Under AGP 9, library modules use the `com.android.library` plugin and do **not** apply `org.jetbrains.kotlin.android`, because Kotlin support is built in.

## Package layout

```
:core  (app.moviedb.core)            no Compose
  network/       Retrofit service, DTOs, auth interceptor, safeApiCall
  database/      Room database, FavoriteMovieEntity, FavoriteDao
  datastore/     Theme settings storage
  data/          MovieRepository, FavoriteRepository, ThemeRepository, mappers
  model/         Movie, MovieDetail, Review, ThemeSettings
  common/        AppError, SectionState, TmdbImage (image URL builder)
  di/            Hilt modules

:designsystem  (app.moviedb.designsystem)   Compose only
  theme/         MoviedbTheme, Palette, palette/ (one file per palette), Type, pressedCornerShape
  component/     RatingChip, RatingBadge, PosterArt, ErrorCard, EmptyCard, OfflineSheet, pulse
  format/        toRatingLabel, toCardDate
  res/           Strings and drawables used by the components above

:feature:home  (app.moviedb.home)
  ui/            HomeRoute, HomeScreen, HomeViewModel, HomeUiState, and the Home-only
                 components (PopularCard, PosterCard, sections, skeletons, AppearanceSheet)
  (same shape for detail and favorites)
```

Each feature holds only presentation code: a route, a screen composable, a ViewModel, a UI state class, and the components only that feature uses. The data layer is in `:core`. Shared UI is in `:designsystem`.

**No use-case layer in v1.** ViewModels call repositories directly. A use case is added only when logic is shared by two ViewModels or is complex enough to need its own tests. None of the three screens meets that bar today.

## Layers and data flow

```
Compose UI  ──events──>  ViewModel  ──calls──>  Repository  ──>  Retrofit API / Room / DataStore
    ^                       │                       │
    └──── UiState (StateFlow) ◄──── maps DTO to domain model, errors to AppError
```

- **UI** renders a `UiState` and sends events as plain function calls (`onRetry()`, `onFavoriteClick()`). It holds no business logic.
- **ViewModel** owns a `StateFlow<UiState>`. It starts work in `viewModelScope`, turns repository results into state, and sends one-shot effects (a snackbar message, a navigation request) through a `Channel` exposed as a `Flow`.
- **Repository** is the only code that knows where data comes from. It returns domain models, never DTOs or entities.
- **Mappers** are pure functions: DTO to domain model, entity to domain model. They hold all null and format handling from [api/tmdb_overview.md](api/tmdb_overview.md#nullability-and-defensive-parsing).

State is collected in composables with `collectAsStateWithLifecycle`, so collection stops when the screen is in the background.

## State and error model

### `AppError`

Repositories never throw to the UI. A shared `safeApiCall` wrapper catches exceptions and HTTP errors and returns a typed failure.

```kotlin
sealed interface AppError {
    data object Network : AppError        // any IOException
    data object Unauthorized : AppError   // HTTP 401, 403
    data object NotFound : AppError       // HTTP 404
    data object RateLimited : AppError    // HTTP 429
    data object Server : AppError         // HTTP 5xx
    data object Unexpected : AppError     // parse failure or anything else
}
```

The mapping from cause to `AppError` is in [api/tmdb_overview.md](api/tmdb_overview.md#errors). `CancellationException` is always rethrown and never turned into an error.

### `SectionState`

Every independently loading part of a screen uses the same state type:

```kotlin
sealed interface SectionState<out T> {
    data object Loading : SectionState<Nothing>
    data class Content<T>(val data: T) : SectionState<T>
    data object Empty : SectionState<Nothing>
    data class Error(val error: AppError) : SectionState<Nothing>
}
```

A list that comes back empty is `Empty`, not `Error`. Retry is available only on `Error`.

### Screen state

```kotlin
data class HomeUiState(
    val popular: SectionState<List<Movie>>,
    val topRated: SectionState<List<Movie>>,
    val nowPlaying: SectionState<List<Movie>>,
    val offlineSheetVisible: Boolean,
    val favoriteCount: Int,
    val theme: ThemeSettings,
)

data class DetailUiState(
    val movie: SectionState<MovieDetail>,
    val reviews: SectionState<List<Review>>,
    val isFavorite: Boolean,
    val shareSheetVisible: Boolean,
    val offlineSheetVisible: Boolean,
)

data class FavoritesUiState(
    val favorites: SectionState<List<Movie>>,
)
```

Home's three sections load in parallel and each owns its state, so one failed list never hides the other two.

### Where errors are shown

The rule is defined once, in [api/tmdb_overview.md](api/tmdb_overview.md#where-errors-are-shown): only `AppError.Network` opens the no-connection bottom sheet, once per screen. Every other error is an inline card with Retry in the section that failed. In state terms:

- `offlineSheetVisible` becomes `true` when the first call on the screen fails with `Network`. A second `Network` failure while it is `true` changes nothing.
- Dismissing the sheet sets it to `false`. The failed sections stay in `Error` and show their inline cards.
- **Try again** reloads every section in `Error` and, on success, leaves the sheet closed. If the failure repeats, the sheet reopens.

## Feature behavior in short

Full behavior is in the feature docs (`features/home.md`, `features/detail.md`, and `features/favorites.md`, planned). The architectural points:

| Screen | Data sources | Notes |
|---|---|---|
| Home | `MovieRepository` (three calls), `FavoriteRepository` (count for the app bar badge) | Calls run in parallel, one `SectionState` each. |
| Detail | `MovieRepository` (detail, reviews), `FavoriteRepository` (is favorite, toggle) | The movie id comes from `SavedStateHandle`, so it survives process death. Detail and reviews load and fail independently. The Favorite button is disabled until the movie has loaded, because the snapshot is built from the detail response. |
| Favorites | `FavoriteRepository` only | Reads a Room `Flow`. No network, so it works offline. |

## Local data

### Favorites (Room)

Favorites are stored as a **snapshot**, so the list works offline and needs no API call.

| Column | Type | Notes |
|---|---|---|
| `id` | `Int`, primary key | TMDB movie id. |
| `title` | `String` | |
| `posterPath` | `String?` | Stored as the TMDB path, not a full URL, so the image size can change later. |
| `releaseDate` | `String?` | Stored as `yyyy-MM-dd`. |
| `overview` | `String` | |
| `voteAverage` | `Double` | Not shown in the Favorites list today. Stored so the snapshot maps to the same `Movie` model as the API lists. |
| `addedAt` | `Long` | Epoch millis. Sorts newest first. |

`FavoriteDao` exposes `observeAll(): Flow<List<FavoriteMovieEntity>>` ordered by `addedAt` descending, `observeCount(): Flow<Int>`, `observeIsFavorite(id): Flow<Boolean>`, `insert` (replace on conflict), and `delete(id)`. `FavoriteRepository` wraps them as `observeAll()`, `observeCount()`, `observeIsFavorite(id)`, `remove(id)`, and `toggle(movie)`. The toggle checks and writes inside one database transaction. Because the list, the count, and the heart state are all Flows, Home, Detail, and Favorites update instantly without refresh calls.

Database changes after the first release need a migration. Until then the schema is exported (`exportSchema = true`) so migrations can be written and tested.

### Theme settings (DataStore)

The prototype includes a theme picker (mode and palette). It is not in the PDF and is an optional addition.

- `ThemeSettings(mode: ThemeMode, paletteId: String)`, with `ThemeMode` being `SYSTEM`, `LIGHT`, or `DARK`.
- `ThemeRepository` exposes `Flow<ThemeSettings>` and `suspend fun update(...)`, backed by DataStore Preferences. Room would be overkill for two values.
- `MainActivity` collects the settings, works out whether the theme is dark, and passes `paletteId` and `darkTheme` to `MoviedbTheme` in `:designsystem`. `ThemeSettings` itself stays in `:core`. Features read `MaterialTheme.colorScheme` and never know which palette is active.
- Palettes are written out by hand: one file per palette in `:designsystem/.../theme/palette/`, each with a light and a dark `ColorScheme`. A unit test checks text contrast for every palette.
- To avoid a flash of the wrong theme at startup, keep the splash screen visible until the first settings value arrives.
- The wallpaper-based palette needs Android 12 (API 31). `MIN_SDK` is 30, so that option is hidden when `Build.VERSION.SDK_INT < 31`.

## Navigation

The app uses Navigation Compose with **type-safe routes**: `@Serializable` classes instead of route strings.

| Route | Owner | Arguments |
|---|---|---|
| `HomeRoute` | `:feature:home` | none |
| `DetailRoute` | `:feature:detail` | `movieId: Int` |
| `FavoritesRoute` | `:feature:favorites` | none |

Each feature module exposes one extension function that registers its destination and takes **callbacks** instead of a `NavController`:

```kotlin
fun NavGraphBuilder.homeScreen(
    onMovieClick: (Int) -> Unit,
    onFavoritesClick: () -> Unit,
)
```

`:app` owns the `NavHost` and supplies the callbacks, for example `onMovieClick = { navController.navigate(DetailRoute(it)) }`. This is how Home reaches Detail without depending on it.

Flows:

- Home to Detail (movie card), Home to Favorites (app bar icon).
- Favorites to Detail (row tap), and Back from either returns to the caller.
- The no-connection sheet's **Open favorites** action calls the same `onFavoritesClick` callback.

## Dependency injection

Hilt provides every dependency. Modules live in `:core/di`:

| Module | Provides |
|---|---|
| `NetworkModule` | `OkHttpClient` (with the auth interceptor), `Json`, `Retrofit`, `MovieApi`. |
| `DatabaseModule` | `MovieDatabase`, `FavoriteDao`. |
| `DataStoreModule` | The DataStore instance. |
| `RepositoryModule` | `@Binds` for each repository interface to its implementation. |

ViewModels use `@HiltViewModel` with constructor injection and are obtained with `hiltViewModel()`.

Repositories are interfaces with one implementation each. This is deliberate, not speculative: tests inject fakes, and Hilt binds the implementation. ViewModels depend on the interface.

Hilt Gradle plugin and annotation processing under AGP 9 need care. The compatibility notes are in [libraries.md](libraries.md#build-compatibility-to-verify).

## Configuration and secrets

The TMDB read access token comes from `local.properties`, which is not committed, and is exposed as a `BuildConfig` field in `:core`. It never appears in code, docs, or logs. Details: [api/tmdb_overview.md](api/tmdb_overview.md#authentication).

## Image loading

`TmdbImage.url(path, size)` in `:core/common` builds `https://image.tmdb.org/t/p/{size}{path}`. A shared `MoviePoster` composable in `:core/ui` wraps Coil's `AsyncImage` with a placeholder and an error fallback, so a missing or broken image never leaves a gap. Sizes per use are in [api/tmdb_overview.md](api/tmdb_overview.md#images).

## Threading

- Retrofit suspend functions switch off the main thread themselves.
- Room and DataStore suspend and Flow APIs are main-safe.
- Repositories do not hard-code dispatchers. Anything that needs one takes it by constructor, so tests can replace it.
- `StateFlow`s exposed to the UI use `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial)` where they are derived from other flows.

## Testing strategy

Tests are a bonus item and also protect the "no crashes" goal. The plan is unit tests first, kept fast and JVM-only.

| Target | What is tested | Tools |
|---|---|---|
| Mappers | Normal data, `null` poster, empty date, empty overview, all avatar forms, blank author name. | JUnit |
| DTO parsing | Sample JSON from `api/*` decodes with `ignoreUnknownKeys`, including missing and `null` fields. | JUnit, kotlinx.serialization |
| `safeApiCall` | `IOException` becomes `Network`, 404 becomes `NotFound`, 429 becomes `RateLimited`, 5xx becomes `Server`, cancellation is rethrown. | JUnit, coroutines-test |
| Repositories | Success, each error type, and empty list, using a fake `MovieApi`. | JUnit, coroutines-test |
| ViewModels | Initial state, each section's loading and result, independent failure, retry, sheet rules, favorite toggle. | JUnit, MockK or fakes, Turbine, coroutines-test |
| `FavoriteDao` | Insert, delete, ordering, replace on conflict. | Instrumented test, optional |

Conventions:

- A `MainDispatcherRule` replaces `Dispatchers.Main` in ViewModel tests.
- Prefer hand-written fakes for repositories and the API. Use MockK where a fake would be more code than the test.
- Flow assertions use Turbine, not `delay`.
- UI tests are out of scope for v1. Previews cover visual states: loading, content, empty, error, and offline sheet for each screen.

## Decision log

| Decision | Choice | Alternative not taken |
|---|---|---|
| Module split | `:app`, one `:core`, three features | One module per layer, or a single app module. |
| Repositories location | `:core`, shared | Per feature, which forces feature-to-feature dependencies. |
| Domain layer | None. ViewModels call repositories. | Use cases for every call. |
| Feature navigation | Callbacks supplied by `:app` | Features sharing a navigation module or calling each other. |
| Detail data | Fresh `GET /movie/{id}` by id | Passing the list item through navigation. |
| Favorites storage | Snapshot in Room | Storing only ids and refetching. |
| Error display | Sheet for `Network`, inline card otherwise | Sheet or inline for everything. |
| Pagination | Page 1 only | Paging 3. |
