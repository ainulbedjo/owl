package app.moviedb.explore.data

import app.moviedb.core.common.AppResult
import app.moviedb.core.data.network.dto.MovieDto
import app.moviedb.core.data.network.dto.MovieListDto
import app.moviedb.explore.data.mapper.toMoviePage
import app.moviedb.explore.data.network.ExploreApi
import app.moviedb.explore.data.repository.ExploreRepositoryImpl
import app.moviedb.explore.model.MovieSort
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExploreRepositoryTest {
    private class RecordingApi : ExploreApi {
        var args: List<Any?> = emptyList()
        override suspend fun discoverMovies(
            page: Int, genreId: Int?, sortBy: String?, minVoteCount: Int?, releasedBefore: String?,
        ): MovieListDto {
            args = listOf(page, genreId, sortBy, minVoteCount, releasedBefore)
            return MovieListDto(page = page, results = listOf(MovieDto(1, "T")), totalPages = 3, totalResults = 60)
        }
    }

    private val api = RecordingApi()
    private val repo = ExploreRepositoryImpl(api, today = { LocalDate.of(2026, 11, 25) })

    @Test
    fun popularSendsOnlyThePopularitySort() = runTest {
        repo.movies(9648, MovieSort.Popular, page = 2)

        assertEquals(listOf(2, 9648, "popularity.desc", null, null), api.args)
    }

    @Test
    fun topRatedAddsTheVoteFloor() = runTest {
        repo.movies(9648, MovieSort.TopRated, page = 1)

        assertEquals(listOf(1, 9648, "vote_average.desc", 200, null), api.args)
    }

    @Test
    fun newestStopsAtToday() = runTest {
        repo.movies(9648, MovieSort.Newest, page = 1)

        assertEquals(listOf(1, 9648, "primary_release_date.desc", null, "2026-11-25"), api.args)
    }

    @Test
    fun nullGenreSendsNoGenreFilter() = runTest {
        repo.movies(null, MovieSort.Popular, page = 1)

        assertEquals(listOf(1, null, "popularity.desc", null, null), api.args)
    }

    @Test
    fun mapsThePage() = runTest {
        val page = (repo.movies(9648, MovieSort.Popular, page = 1) as AppResult.Success).data

        assertEquals(1, page.items.single().id)
        assertEquals(60, page.totalResults)
        assertTrue(page.hasMore)
    }

    @Test
    fun lastPageHasNoMore() {
        assertFalse(MovieListDto(page = 3, totalPages = 3).toMoviePage().hasMore)
    }

    @Test
    fun capsAtFiveHundredPages() {
        assertTrue(MovieListDto(page = 499, totalPages = 38020).toMoviePage().hasMore)
        assertFalse(MovieListDto(page = 500, totalPages = 38020).toMoviePage().hasMore)
    }

    @Test
    fun emptyResultsGiveEmptyItems() {
        assertTrue(MovieListDto().toMoviePage().items.isEmpty())
    }
}
