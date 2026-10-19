<div align="center">
  <a href="https://github.com/ainulbedjo/owl">
    <img src="https://raw.githubusercontent.com/ainulbedjo/owl/refs/heads/master/app/src/main/res/drawable/play_store_512.png" width="100" height="100">
  </a>
  <h1>MovieDB</h1>
  <p>An Android app for browsing movies from TMDB, built with Jetpack Compose and Material 3 Expressive.</p>
</div>

<table>
<tr>
<td>
<img src="https://raw.githubusercontent.com/ainulbedjo/owl/refs/heads/master/screenshot/home.png" alt="Home screenshot" width="100%" />
</td>
<td>
<img src="https://raw.githubusercontent.com/ainulbedjo/owl/refs/heads/master/screenshot/detail.png" alt="Detail screenshot" width="100%" />
</td>
<td>
<img src="https://raw.githubusercontent.com/ainulbedjo/owl/refs/heads/master/screenshot/favorites.png" alt="Favorites screenshot" width="100%" />
</td>
</tr>
<tr>
<td>
<img src="https://raw.githubusercontent.com/ainulbedjo/owl/refs/heads/master/screenshot/theme.png" alt="Theme screenshot" width="100%" />
</td>
<td>
<img src="https://raw.githubusercontent.com/ainulbedjo/owl/refs/heads/master/screenshot/offline.png" alt="Offline screenshot" width="100%" />
</td>
<td>
<img src="https://raw.githubusercontent.com/ainulbedjo/owl/refs/heads/master/screenshot/chucker.png" alt="Chucker screenshot" width="100%" />
</td>
</tr>
</table>

### Features:
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

## Resources
- [Material 3 Expressive](https://m3.material.io/blog/building-with-m3-expressive) | M3 Expressive design system
- [Moviebase.app](https://moviebase.app/) | Direct inspiration for research purposes
- [ip-as-logo-skill](https://github.com/s1dashu/ip-as-logo-skill) | Skill to create the logo used.
- [Icon kitchen](https://icon.kitchen/) | To create a ```icon/ic_launcher``` resources used in the codebase.
- [themoviedb.org](https://www.themoviedb.org/) | API and data seeding.
