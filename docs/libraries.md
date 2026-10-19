# Libraries

Each choice here has a reason and a rejected alternative. How the libraries fit together is in [architecture.md](architecture.md).

**Versions are not listed here.** They live in `buildSrc/src/main/kotlin/Deps.kt`, the single source of truth. Add each library there, not in a module's `build.gradle.kts`. Pick the newest **stable** release at the time you add it, and check the compatibility notes at the end of this page first.

## Summary

| Area | Library |
|---|---|
| UI | Jetpack Compose, Material 3 (Expressive), Compose BOM |
| Navigation | Navigation Compose (type-safe routes) |
| Architecture | Lifecycle ViewModel and runtime for Compose |
| DI | Hilt, with KSP |
| Network | Retrofit, OkHttp |
| Parsing | kotlinx.serialization |
| Images | Coil 3 |
| Local data | Room |
| Async | Kotlin coroutines and Flow |
| Startup | AndroidX SplashScreen |
| Tests | JUnit 4, kotlinx-coroutines-test, Turbine, MockK |

## Details

### UI

| Library | Role | Why | Not chosen |
|---|---|---|---|
| **Compose BOM** | Pins matching versions of all Compose artifacts. | One version to bump, no mismatched Compose libraries. Already in `Deps.kt`. The BOM was raised to a release whose Material 3 includes the Expressive APIs. | Pinning each Compose artifact separately. |
| **Material 3** (`androidx.compose.material3`) | Components, theming, `MaterialExpressiveTheme`, motion scheme, shape morphing. | The requirement is Material 3 Expressive. Most Expressive APIs are still experimental and need `@OptIn(ExperimentalMaterial3ExpressiveApi::class)`. We opt in at file level and list the used APIs in `design/design_system.md` (planned). | Material 2 (no Expressive), or a custom design system. |
| **Material icons (core set)** | A few icons: back, favorite, share, star, refresh. | Comes with Material 3 and is small. Missing icons (for example the palette icon) are added as vector drawables. | `material-icons-extended`: a very large set that adds build time and size before shrinking. |
| **Lifecycle runtime and ViewModel for Compose** | `collectAsStateWithLifecycle`, `viewModel()`. | Stops collecting when the app is in the background and keeps state across configuration changes. | Collecting with plain `collectAsState`, which keeps collecting in the background. |
| **AndroidX SplashScreen** | Holds the splash until the saved theme is read. | Prevents a flash of the wrong theme at startup. | A custom splash activity. |

### Navigation

| Library | Role | Why | Not chosen |
|---|---|---|---|
| **Navigation Compose** | Moves between Home, Detail, and Favorites. | The standard Jetpack choice. Type-safe routes (`@Serializable` classes) remove string route typos and give typed arguments. | Compose Destinations or other third-party routers (extra dependency for three screens), or manual state-based screen switching (no back stack, no process-death restore). |

### Dependency injection

| Library | Role | Why | Not chosen |
|---|---|---|---|
| **Hilt** (`hilt-android`, `hilt-compiler`, `hilt-navigation-compose`) | DI for the Application, ViewModels, and repositories. | Built for Android, and it removes manual wiring in a multi-module app. `@HiltViewModel` plus `hiltViewModel()` is the Jetpack standard. | Koin (runtime errors instead of compile-time checks, and not the Jetpack path), or manual DI (more wiring to write and check by hand). |
| **KSP** | Runs the Hilt and Room annotation processors. | Faster than kapt. **kapt is not an option**: it conflicts with AGP 9 built-in Kotlin. | kapt. |

### Network and data parsing

| Library | Role | Why | Not chosen |
|---|---|---|---|
| **Retrofit** | Typed HTTP client for the five TMDB endpoints. | The Android standard for REST. Suspend functions fit coroutines, and an interface is easy to fake in tests. | Ktor client (heavier for this need), or hand-written OkHttp calls (more boilerplate). |
| **OkHttp** | HTTP engine under Retrofit. Adds the auth header with an interceptor. | Pulled in by Retrofit and Coil, so the app has one HTTP stack. A logging interceptor is added for debug builds only. | A second HTTP client. |
| **kotlinx.serialization** (plus the Retrofit converter) | Decodes JSON into DTOs. | Kotlin-first, with no reflection and compile-time generated serializers. The same library also powers type-safe navigation routes, so one library does two jobs. Handles `null` and missing fields with default values. | Gson (reflection, ignores Kotlin null safety), Moshi (good, but a second serialization system next to the one Navigation already needs). |

### Images

| Library | Role | Why | Not chosen |
|---|---|---|---|
| **Coil 3** (`coil-compose`, plus its OkHttp network module) | Loads posters and backdrops into Compose with caching. | Compose-native `AsyncImage`, small, built on coroutines, and shares OkHttp with Retrofit. | Glide or Picasso (View-era first, more glue for Compose). |

### Local data

| Library | Role | Why | Not chosen |
|---|---|---|---|
| **Room** (with KSP) | Stores the favorites snapshot. | Observable queries return `Flow`, so the Favorites list and the heart toggle update instantly. Compile-time checked SQL. A Jetpack library. | Raw SQLite (boilerplate), or saving a JSON list in preferences (no queries, no ordering, not safe under concurrent writes). |

### Async

| Library | Role | Why | Not chosen |
|---|---|---|---|
| **Kotlin coroutines and Flow** | Async work and observable state. | Used by Retrofit, Room, and Compose. `StateFlow` carries UI state. | RxJava (legacy for new Android code). |

### Testing

| Library | Role | Why | Not chosen |
|---|---|---|---|
| **JUnit 4** | Test runner. | Already in `Deps.kt`. Broad tool support. | JUnit 5 (needs extra setup on Android for no gain here). |
| **kotlinx-coroutines-test** | `runTest`, `StandardTestDispatcher`, a main dispatcher swap. | Controls virtual time, so tests are fast and deterministic. | `runBlocking` with real delays. |
| **Turbine** | Asserts on `Flow` emissions. | Clear, short Flow tests with no `delay`. | Hand-collecting into lists. |
| **MockK** | Mocks where a hand-written fake is more code. | Kotlin-native: handles final classes, coroutines, and `suspend` functions. | Mockito (needs extra setup for final Kotlin classes). |

Not used in v1, on purpose: Paging 3 (page 1 only, see the README decisions), MockWebServer (a fake `MovieApi` plus JSON decode tests are enough), and Compose UI tests (previews cover the states).

## Build compatibility to verify

The project uses **AGP 9.x with built-in Kotlin** (see `Deps.Versions.AGP`). This changes how annotation processing plugins are set up, so verify these points when the first module that uses them is added. Nothing below has been built yet.

1. **No `org.jetbrains.kotlin.android` and no kapt.** Built-in Kotlin replaces the Kotlin Android plugin, and the kapt plugin is incompatible with it. Both statements are in Google's [migration guide to built-in Kotlin](https://developer.android.com/build/migrate-to-built-in-kotlin) and its [KSP, kapt, and legacy-kapt notes](https://developer.android.com/agents/skills/build-system/agp/agp-9-upgrade/references/ksp-kapt).
2. **KSP2 only.** KSP1 does not support AGP 9. Use a KSP2 release and confirm that it supports `Deps.Versions.KOTLIN`. A KSP release targets a range of Kotlin versions, so a KSP bump can force a Kotlin bump.
3. **Hilt must be new enough for AGP 9.** Dagger 2.59 is reported to add AGP 9 support to the Hilt Gradle plugin, with later patch releases fixing AGP 9 issues. This comes from a secondary source. Confirm it in the Dagger release notes before choosing a version.
4. **Room** works with KSP (its compiler has supported KSP for a long time). Use its Gradle plugin only if schema export needs it.
5. **Plugin placement in a multi-module build.** The Hilt Gradle plugin goes in `:app`. Modules with `@HiltViewModel` or `@Module` classes need `hilt-android` and the KSP `hilt-compiler`. Check whether any library module also needs the plugin (for example one with `@AndroidEntryPoint`).
6. **Serialization compiler plugin** (`org.jetbrains.kotlin.plugin.serialization`) in every module that declares `@Serializable` classes: `:core` for DTOs and each feature for its route.
7. **Retrofit version.** `Deps.kt` pins `2.11.0`, and a newer major release exists on Maven. Both work with OkHttp. Decide when adding the converter, and keep Retrofit, the converter, and OkHttp consistent.
8. **Avoid pre-release versions.** Some libraries (for example kotlinx.serialization) may show release candidates as the newest tag. Pin the newest stable one.
9. **Experimental Material 3 APIs.** Opt in only in the files that use Expressive APIs, so a future signature change touches few files.

## Adding a library

1. Add its version to `Deps.Versions` and its coordinates to `Deps`.
2. Add the plugin id to `Deps.Plugins` if it has one.
3. Reference it as `Deps.X` in the module's `build.gradle.kts`.
4. Add a row to this page with the reason and the rejected alternative.
