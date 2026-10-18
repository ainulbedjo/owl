package app.moviedb.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.moviedb.core.model.ThemeSettings
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

@Composable
private fun HomeEntry(
    theme: ThemeSettings,
    onThemeChange: (ThemeSettings) -> Unit,
    onMovieClick: (movieId: Int) -> Unit,
    onFavoritesClick: () -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        // The theme is owned by the app, so it is added here, not in the ViewModel.
        state = state.copy(theme = theme),
        onMovieClick = onMovieClick,
        onFavoritesClick = onFavoritesClick,
        onRetry = viewModel::onRetry,
        onTryAgain = viewModel::onTryAgain,
        onDismissOfflineSheet = viewModel::onDismissOfflineSheet,
        onThemeChange = onThemeChange,
    )
}
