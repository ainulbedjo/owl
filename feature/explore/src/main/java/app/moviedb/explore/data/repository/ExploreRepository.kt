package app.moviedb.explore.data.repository

import app.moviedb.core.common.AppResult
import app.moviedb.explore.model.MoviePage
import app.moviedb.explore.model.MovieSort

interface ExploreRepository {
    suspend fun movies(genreId: Int?, sort: MovieSort, page: Int): AppResult<MoviePage>
}
