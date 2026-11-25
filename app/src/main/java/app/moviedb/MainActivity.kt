package app.moviedb

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import app.moviedb.core.BuildConfig
import app.moviedb.core.model.ThemeMode
import app.moviedb.core.model.ThemeSettings
import app.moviedb.designsystem.theme.MoviedbTheme
import app.moviedb.detail.ui.DetailRoute
import app.moviedb.detail.ui.detailScreen
import app.moviedb.favorites.ui.FavoritesRoute
import app.moviedb.favorites.ui.favoritesScreen
import app.moviedb.genre.ui.GenreRoute
import app.moviedb.genre.ui.genreScreen
import app.moviedb.home.ui.HomeRoute
import app.moviedb.home.ui.homeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // for chucker notification
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= 33) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val saved by viewModel.theme.collectAsState()
            val theme = saved ?: return@setContent

            // The app theme can differ from the device theme, so the system bar icons follow the app.
            val dark = theme.useDarkTheme()
            DisposableEffect(dark) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark },
                )
                onDispose {}
            }

            MoviedbTheme(paletteId = theme.paletteId, darkTheme = dark) {
                AppNavHost(
                    theme = theme,
                    onThemeChange = {
                        viewModel.onThemeChange(it)
                    },
                )
            }
        }
    }
}

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
            onGenreClick = { navController.navigate(GenreRoute) }
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
        genreScreen { navController.popBackStack() }
    }
}

@Composable
private fun ThemeSettings.useDarkTheme(): Boolean = when (mode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}
