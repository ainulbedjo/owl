package app.moviedb.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/** Needs Android 12 (API 31) for dynamic wallpaper. */
val isWallpaperPaletteSupported: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/**
 * The app theme. It takes plain values so this module does not depend on `:core`:
 * the caller turns its stored settings into [darkTheme] and [paletteId].
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MoviedbTheme(
    paletteId: String,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val useWallpaper = paletteId == Palette.WALLPAPER_ID && isWallpaperPaletteSupported
    val colorScheme = if (useWallpaper) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        Palette.byId(paletteId).colorScheme(darkTheme)
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        typography = Typography,
        content = content,
    )
}
