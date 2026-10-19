package app.moviedb.core.data.local

import app.moviedb.core.model.ThemeMode
import app.moviedb.core.model.ThemeSettings

fun ThemeSettingsEntity?.toSettings(): ThemeSettings {
    val default = ThemeSettings()
    if (this == null) return default
    return ThemeSettings(
        mode = ThemeMode.entries.firstOrNull { it.name == mode } ?: default.mode,
        paletteId = paletteId,
    )
}

fun ThemeSettings.toEntity(): ThemeSettingsEntity =
    ThemeSettingsEntity(mode = mode.name, paletteId = paletteId)
