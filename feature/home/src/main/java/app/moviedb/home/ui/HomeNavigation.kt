package app.moviedb.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.moviedb.core.model.ThemeSettings
import app.moviedb.home.data.sampleHomeState
import kotlinx.serialization.Serializable

/** The start destination. It has no arguments. */
@Serializable
data object HomeRoute

/**
 * Registers Home in the app's `NavHost`. The callbacks are how Home asks `:app` to
 * navigate, so Home never depends on Detail or Favorites.
 */
fun NavGraphBuilder.homeScreen(
    theme: ThemeSettings,
    onThemeChange: (ThemeSettings) -> Unit,
    onMovieClick: (movieId: Int) -> Unit,
    onFavoritesClick: () -> Unit,
) {
    composable<HomeRoute> {
        HomeEntry(
            theme = theme,
            onThemeChange = onThemeChange,
            onMovieClick = onMovieClick,
            onFavoritesClick = onFavoritesClick,
        )
    }
}

// TODO(data): replace the sample state with HomeViewModel (see docs/todo/data-fetch.md).
@Composable
private fun HomeEntry(
    theme: ThemeSettings,
    onThemeChange: (ThemeSettings) -> Unit,
    onMovieClick: (movieId: Int) -> Unit,
    onFavoritesClick: () -> Unit,
) {
    val state = remember(theme) { sampleHomeState.copy(theme = theme) }

    HomeScreen(
        state = state,
        onMovieClick = onMovieClick,
        onFavoritesClick = onFavoritesClick,
        onRetry = {},
        onTryAgain = {},
        onDismissOfflineSheet = {},
        onThemeChange = onThemeChange,
    )
}
