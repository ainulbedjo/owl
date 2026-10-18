package app.moviedb.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.moviedb.core.model.ThemeSettings

/**
 * The entry point `:app` calls. For now it feeds Home sample data;
 * it becomes the `HomeViewModel` collector when the data layer exists.
 */
@Composable
fun HomeRoute(
    theme: ThemeSettings,
    onThemeChange: (ThemeSettings) -> Unit,
    onMovieClick: (movieId: Int) -> Unit,
    onFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = remember(theme) { _root_ide_package_.app.moviedb.home.data.sampleHomeState.copy(theme = theme) }

    HomeScreen(
        state = state,
        onMovieClick = onMovieClick,
        onFavoritesClick = onFavoritesClick,
        onRetry = {},
        onTryAgain = {},
        onDismissOfflineSheet = {},
        onThemeChange = onThemeChange,
        modifier = modifier,
    )
}
