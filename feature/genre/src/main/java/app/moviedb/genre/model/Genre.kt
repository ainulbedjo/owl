package app.moviedb.genre.model

/** An official TMDB movie genre. [id] is the `with_genres` value for Discover. */
data class Genre(
    val id: Int,
    val name: String,
)
