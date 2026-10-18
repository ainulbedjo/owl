package app.moviedb.core.data.repository

import app.moviedb.core.common.AppResult
import app.moviedb.core.model.Movie
import app.moviedb.core.model.MovieDetail
import app.moviedb.core.model.Review

interface MovieRepository {
    suspend fun popular(): AppResult<List<Movie>>
    suspend fun topRated(): AppResult<List<Movie>>
    suspend fun nowPlaying(): AppResult<List<Movie>>
    suspend fun movieDetail(id: Int): AppResult<MovieDetail>

    suspend fun reviews(id: Int): AppResult<List<Review>>
}