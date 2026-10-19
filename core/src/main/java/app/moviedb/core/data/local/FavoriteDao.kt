package app.moviedb.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FavoriteDao {

    /** Newest first. Emits again whenever the table changes. */
    @Query("SELECT * FROM favorite_movies ORDER BY addedAt DESC")
    abstract fun observeAll(): Flow<List<FavoriteMovieEntity>>

    @Query("SELECT COUNT(*) FROM favorite_movies")
    abstract fun observeCount(): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :id)")
    abstract fun observeIsFavorite(id: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :id)")
    abstract suspend fun isFavorite(id: Int): Boolean

    /** Saving the same movie twice replaces its row, so it never duplicates. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: FavoriteMovieEntity)

    @Query("DELETE FROM favorite_movies WHERE id = :id")
    abstract suspend fun delete(id: Int)

    /**
     * Saves the movie if it is not a favorite, removes it if it is. The check and the write
     * run in one transaction, so two quick taps cannot insert twice or leave a half state.
     * Returns true when the movie is a favorite afterwards.
     */
    @Transaction
    open suspend fun toggle(entity: FavoriteMovieEntity): Boolean =
        if (isFavorite(entity.id)) {
            delete(entity.id)
            false
        } else {
            insert(entity)
            true
        }
}
