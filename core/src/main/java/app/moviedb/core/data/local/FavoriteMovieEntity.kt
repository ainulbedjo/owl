package app.moviedb.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A favorite, stored as a snapshot so the list works offline and needs no API call.
 * It holds the title and overview as they were when the movie was saved.
 *
 * [posterPath] is the TMDB path, not a URL, so the image size can change later.
 * [releaseDate] is `yyyy-MM-dd`. [addedAt] is epoch milliseconds and sorts newest first.
 */
@Entity(tableName = "favorite_movies")
data class FavoriteMovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val overview: String,
    val voteAverage: Double,
    val addedAt: Long,
)
