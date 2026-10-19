# MovieDB

<table>
<tr>
<td>
<img src="https://raw.githubusercontent.com/ainulbedjo/owl/refs/heads/master/screenshot/home.webp" alt="Home screenshot" width="100%" />
</td>
<td>
<img src="https://raw.githubusercontent.com/ainulbedjo/owl/refs/heads/master/screenshot/detail.webp" alt="Detail screenshot" width="100%" />
</td>
<td>
<img src="https://raw.githubusercontent.com/ainulbedjo/owl/refs/heads/master/screenshot/favorites.webp" alt="Favorites screenshot" width="100%" />
</td>
</tr>
<tr>
<td>
<img src="https://raw.githubusercontent.com/ainulbedjo/owl/refs/heads/master/screenshot/theme.webp" alt="Theme screenshot" width="100%" />
</td>
<td>
<img src="https://raw.githubusercontent.com/ainulbedjo/owl/refs/heads/master/screenshot/offline.webp" alt="Offline screenshot" width="100%" />
</td>
<td>
<img src="https://raw.githubusercontent.com/ainulbedjo/owl/refs/heads/master/screenshot/chucker.webp" alt="Chucker screenshot" width="100%" />
</td>
</tr>
</table>

An Android app for browsing movies from [TMDB](https://www.themoviedb.org/), built with Jetpack Compose and Material 3 Expressive. Your favorites are saved on the device, so they work offline.

- **Home**: Popular, Top Rated, and Now Playing. Each list loads, fails, and retries on its own.
- **Detail**: backdrop, facts, overview, and reviews. Save the movie to favorites or share it from a bottom sheet.
- **Favorites**: your saved movies, newest first. Open or remove one, and every screen updates live.
- **Appearance**: light, dark, or system mode and a choice of color palettes, saved between launches.
- **Offline**: only a lost connection opens a "No connection" sheet. Other errors stay in the section that failed.

## Libraries

| Area | Library |
|---|---|
| UI | Jetpack Compose, Material 3 Expressive |
| Navigation | Navigation Compose (type-safe routes) |
| Architecture | Lifecycle ViewModel, StateFlow, Kotlin coroutines and Flow |
| DI | Hilt, with KSP |
| Network | Retrofit, OkHttp, kotlinx.serialization |
| Images | Coil 3 |
| Local data | Room (favorites and theme) |
| Debugging | Chucker (debug builds only) |
| Tests | JUnit 4, kotlinx-coroutines-test |

Why each one was chosen is in [docs/libraries.md](docs/libraries.md). Versions are in [`Deps.kt`](buildSrc/src/main/kotlin/Deps.kt).

## Architecture

MVVM with one-way data flow: the UI draws a `UiState`, a ViewModel owns it as a `StateFlow`, and repositories return `AppResult` values instead of throwing.
