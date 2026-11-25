package app.moviedb.explore.ui

import androidx.lifecycle.SavedStateHandle
import app.moviedb.core.common.AppError
import app.moviedb.core.common.AppResult
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.model.Movie
import app.moviedb.explore.MainDispatcherRule
import app.moviedb.explore.data.repository.ExploreRepository
import app.moviedb.explore.model.MoviePage
import app.moviedb.explore.model.MovieSort
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private fun movie(id: Int) = Movie(id, "Movie $id", "", null, null, 7.0)

private fun page(n: Int, vararg ids: Int, totalPages: Int = 3) =
    AppResult.Success(MoviePage(ids.map(::movie), n, totalPages, 60))

/** Answers page 1, 2, ... from [answers], and remembers every request. */
private class FakeExploreRepository(val answers: MutableList<AppResult<MoviePage>>) : ExploreRepository {
    val requests = mutableListOf<Pair<Int?, Int>>()

    override suspend fun movies(genreId: Int?, sort: MovieSort, page: Int): AppResult<MoviePage> {
        requests += genreId to page
        return answers.removeFirst()
    }
}

class ExploreViewModelTest {
    @get:Rule
    val dispatcher = MainDispatcherRule()

    private fun viewModel(repo: ExploreRepository, genreId: Int? = 9648) = ExploreViewModel(
        SavedStateHandle(mapOf("genreId" to genreId, "genreName" to "Mystery")),
        repo,
    )

    private fun ExploreViewModel.ids() =
        (state.value.movies as SectionState.Content).data.map { it.id }

    @Test
    fun loadsThePageForTheGenreOnStart() {
        val repo = FakeExploreRepository(mutableListOf(page(1, 1, 2)))
        val vm = viewModel(repo)

        assertEquals(listOf(9648 to 1), repo.requests)
        assertEquals(listOf(1, 2), vm.ids())
        assertEquals("Mystery", vm.state.value.title)
        assertTrue(vm.state.value.hasMore)
    }

    @Test
    fun loadMoreAppendsAndDropsDuplicates() {
        val repo = FakeExploreRepository(mutableListOf(page(1, 1, 2), page(2, 2, 3)))
        val vm = viewModel(repo)

        vm.onLoadMore()

        assertEquals(listOf(1, 2, 3), vm.ids())
        assertEquals(listOf(9648 to 1, 9648 to 2), repo.requests)
    }

    @Test
    fun noRequestAfterTheLastPage() {
        val repo = FakeExploreRepository(mutableListOf(page(1, 1, totalPages = 1)))
        val vm = viewModel(repo)

        vm.onLoadMore()

        assertFalse(vm.state.value.hasMore)
        assertEquals(1, repo.requests.size)
    }

    @Test
    fun failedLaterPageKeepsTheListAndRetryLoadsThatPage() {
        val repo = FakeExploreRepository(
            mutableListOf(page(1, 1), AppResult.Failure(AppError.Server), page(2, 2)),
        )
        val vm = viewModel(repo)

        vm.onLoadMore()
        assertEquals(AppendState.Error, vm.state.value.append)
        assertEquals(listOf(1), vm.ids())

        vm.onRetry()
        assertEquals(listOf(1, 2), vm.ids())
        assertEquals(listOf(1, 2, 2), repo.requests.map { it.second })
    }

    @Test
    fun failedFirstPageIsAnErrorAndNoGenreMeansAllMovies() {
        val repo = FakeExploreRepository(mutableListOf(AppResult.Failure(AppError.Server)))
        val vm = viewModel(repo, genreId = null)

        assertEquals(SectionState.Error(AppError.Server), vm.state.value.movies)
        assertEquals(listOf<Int?>(null), repo.requests.map { it.first })
    }

    @Test
    fun noConnectionOpensTheOfflineSheet() {
        val repo = FakeExploreRepository(mutableListOf(AppResult.Failure(AppError.Network)))
        val vm = viewModel(repo)

        assertTrue(vm.state.value.offlineSheetVisible)
    }
}
