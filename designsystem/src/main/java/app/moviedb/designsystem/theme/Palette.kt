package app.moviedb.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import app.moviedb.designsystem.theme.palette.ForestPalette
import app.moviedb.designsystem.theme.palette.LagoonPalette
import app.moviedb.designsystem.theme.palette.OceanPalette
import app.moviedb.designsystem.theme.palette.RosePalette
import app.moviedb.designsystem.theme.palette.SunsetPalette
import app.moviedb.designsystem.theme.palette.VioletPalette

/**
 * A named set of colors: one [ColorScheme] for light and one for dark, plus a [swatch] for the picker.
 *
 * Every color is written out by hand in its own file under `palette/`.
 * To add a palette, copy one of those files, change the colors, and add it to [presets].
 *
 * [WALLPAPER_ID] has no colors here: it uses the system wallpaper colors on Android 12+.
 */
data class Palette(
    val id: String,
    val name: String,
    val swatch: Color,
    val light: ColorScheme,
    val dark: ColorScheme,
) {
    fun colorScheme(isDark: Boolean): ColorScheme = if (isDark) dark else light

    companion object {
        const val WALLPAPER_ID = "dynamic"

        /** Palettes with their own colors, in picker order. The wallpaper option is added by the picker. */
        val presets: List<Palette> by lazy {
            listOf(VioletPalette, OceanPalette, LagoonPalette, ForestPalette, SunsetPalette, RosePalette)
        }

        fun byId(id: String): Palette = presets.firstOrNull { it.id == id } ?: presets.first()
    }
}
