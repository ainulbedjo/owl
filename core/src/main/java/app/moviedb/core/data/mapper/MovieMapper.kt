package app.moviedb.core.data.mapper

import app.moviedb.core.model.Movie
import app.moviedb.core.model.MovieDetail
import app.moviedb.core.data.network.dto.MovieDetailDto
import app.moviedb.core.data.network.dto.MovieDto
import java.time.LocalDate

fun MovieDto.toMovie(): Movie = Movie(
    id = id,
    title = title,
    overview = overview.orEmpty(),
    posterPath = posterPath,
    releaseDate = releaseDate.toLocalDateOrNull(),
    rating = voteAverage,
)

fun MovieDetailDto.toMovieDetail(): MovieDetail = MovieDetail(
    id = id,
    title = title,
    tagline = tagline?.takeIf { it.isNotBlank() },
    overview = overview.orEmpty(),
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate.toLocalDateOrNull(),
    runtimeMinutes = runtime?.takeIf { it > 0 },
    genres = genres.map { it.name },
    rating = voteAverage,
)

/** Parses `yyyy-MM-dd`. Empty, missing or invalid input gives `null`. */
internal fun String?.toLocalDateOrNull(): LocalDate? =
    if (isNullOrBlank()) null else runCatching { LocalDate.parse(this) }.getOrNull()
