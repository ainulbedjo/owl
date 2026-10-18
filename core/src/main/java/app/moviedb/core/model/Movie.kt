package app.moviedb.core.model

import java.time.LocalDate

/**
 * A movie as shown in lists (Home sections and Favorites).
 *
 * Holds the TMDB image path, not a URL, so each screen picks its own image size.
 * A [rating] of `0.0` means "not rated"; the UI hides the rating chip.
 */
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val releaseDate: LocalDate?,
    val rating: Double,
)
