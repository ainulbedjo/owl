package app.moviedb.explore.data.repository

import app.moviedb.core.common.AppResult
import app.moviedb.core.common.map
import app.moviedb.core.data.network.safeApiCall
import app.moviedb.explore.data.mapper.toMoviePage
import app.moviedb.explore.data.network.ExploreApi
import app.moviedb.explore.model.MoviePage
import app.moviedb.explore.model.MovieSort
import java.time.LocalDate

class ExploreRepositoryImpl(
    private val api: ExploreApi,
    private val today: () -> LocalDate = { LocalDate.now() },
) : ExploreRepository {

    override suspend fun movies(genreId: Int?, sort: MovieSort, page: Int): AppResult<MoviePage> =
        safeApiCall {
            when (sort) {
                MovieSort.Popular -> api.discoverMovies(page, genreId, "popularity.desc")
                MovieSort.TopRated -> api.discoverMovies(
                    page, genreId, "vote_average.desc", minVoteCount = MIN_VOTES_FOR_TOP_RATED,
                )
                MovieSort.Newest -> api.discoverMovies(
                    page, genreId, "primary_release_date.desc", releasedBefore = today().toString(),
                )
            }
        }.map { it.toMoviePage() }

    private companion object {
        const val MIN_VOTES_FOR_TOP_RATED = 200
    }
}
