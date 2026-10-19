package app.moviedb.core.data.local

import app.moviedb.core.data.mapper.toLocalDateOrNull
import app.moviedb.core.model.Movie
import app.moviedb.core.model.MovieDetail

fun MovieDetail.toFavoriteEntity(addedAt: Long): FavoriteMovieEntity = FavoriteMovieEntity(
    id = id,
    title = title,
    posterPath = posterPath,
    releaseDate = releaseDate?.toString(),
    overview = overview,
    voteAverage = rating,
    addedAt = addedAt,
)

fun FavoriteMovieEntity.toMovie(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    releaseDate = releaseDate.toLocalDateOrNull(),
    rating = voteAverage,
)
