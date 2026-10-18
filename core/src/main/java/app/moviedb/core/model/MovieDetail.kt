package app.moviedb.core.model

import java.time.LocalDate

/**
 * Full movie data for the Detail screen, from `GET /movie/{id}`.
 *
 * [tagline] is `null` when blank. [runtimeMinutes] is `null` when TMDB reports `0` or nothing.
 * [backdropPath] falls back to [posterPath] in the UI, then to a color placeholder.
 */
data class MovieDetail(
    val id: Int,
    val title: String,
    val tagline: String?,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: LocalDate?,
    val runtimeMinutes: Int?,
    val genres: List<String>,
    val rating: Double,
)
