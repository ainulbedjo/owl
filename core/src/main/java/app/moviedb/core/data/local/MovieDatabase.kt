package app.moviedb.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteMovieEntity::class], version = 1, exportSchema = true)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
