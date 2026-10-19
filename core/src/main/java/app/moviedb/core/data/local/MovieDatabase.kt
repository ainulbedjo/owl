package app.moviedb.core.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteMovieEntity::class, ThemeSettingsEntity::class],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)],
    exportSchema = true,
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun themeDao(): ThemeDao
}
