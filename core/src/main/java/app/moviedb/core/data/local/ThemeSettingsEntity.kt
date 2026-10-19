package app.moviedb.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "theme_settings")
data class ThemeSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val mode: String,
    val paletteId: String,
)
