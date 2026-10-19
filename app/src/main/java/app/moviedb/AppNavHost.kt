package app.moviedb

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import app.moviedb.core.model.ThemeSettings
import app.moviedb.detail.ui.DetailRoute
import app.moviedb.detail.ui.detailScreen
import app.moviedb.favorites.ui.FavoritesRoute
import app.moviedb.favorites.ui.favoritesScreen
import app.moviedb.home.ui.HomeRoute
import app.moviedb.home.ui.homeScreen


@Composable
fun AppNavHost(
    theme: ThemeSettings,
    onThemeChange: (ThemeSettings) -> Unit,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HomeRoute) {
        homeScreen(
            theme = theme,
            onThemeChange = onThemeChange,
            onMovieClick = { movieId -> navController.navigate(DetailRoute(movieId)) },
            onFavoritesClick = { navController.navigate(FavoritesRoute) },
        )
        detailScreen(
            onBack = { navController.popBackStack() },
            onFavoritesClick = { navController.navigate(FavoritesRoute) },
        )
        favoritesScreen(
            onBack = { navController.popBackStack() },
            onMovieClick = { movieId -> navController.navigate(DetailRoute(movieId)) },
            // Back to Home, whatever screens are above it.
            onBrowseClick = { navController.popBackStack(HomeRoute, inclusive = false) },
        )
    }
}
