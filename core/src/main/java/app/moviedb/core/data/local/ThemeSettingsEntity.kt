package app.moviedb.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "theme_settings")
data class ThemeSettingsEntity(
    @PrimaryKey val id: Int = SINGLE_ROW_ID,
    val mode: String,
    val paletteId: String,
) {
    companion object {
        /** The key of the one row. The DAO query uses the same constant. */
        const val SINGLE_ROW_ID = 1
    }
}
