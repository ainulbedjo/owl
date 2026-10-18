package app.moviedb.core.ui

enum class ThemeMode { SYSTEM, LIGHT, DARK }

/**
 * The viewer's theme choice, stored in DataStore.
 *
 * [paletteId] names a palette in the design system. The wallpaper palette needs API 31+.
 */
data class ThemeSettings(
    val mode: ThemeMode = ThemeMode.SYSTEM,
    val paletteId: String = DEFAULT_PALETTE_ID,
) {
    companion object {
        const val DEFAULT_PALETTE_ID = "violet"
    }
}
