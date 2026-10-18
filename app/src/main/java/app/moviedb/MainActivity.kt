package app.moviedb

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import app.moviedb.core.model.ThemeMode
import app.moviedb.core.model.ThemeSettings
import app.moviedb.designsystem.theme.MoviedbTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Held here until ThemeRepository (DataStore) exists; then this collects its Flow.
            var mode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
            var paletteId by rememberSaveable { mutableStateOf(ThemeSettings.DEFAULT_PALETTE_ID) }
            val theme = ThemeSettings(mode, paletteId)

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
                        mode = it.mode
                        paletteId = it.paletteId
                    },
                )
            }
        }
    }
}

@Composable
private fun ThemeSettings.useDarkTheme(): Boolean = when (mode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}