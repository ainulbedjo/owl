package app.moviedb.home.ui

import app.moviedb.core.common.AppError
import app.moviedb.core.common.AppResult
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.data.repository.MovieRepository
import app.moviedb.core.model.Movie
import app.moviedb.core.model.MovieDetail
import app.moviedb.core.model.Review
import app.moviedb.home.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private fun movie(id: Int) = Movie(id, "Movie $id", "", null, null, 7.0)

/** A repository whose answers each test sets. While [gate] is set, every call waits for it. */
private class FakeMovieRepository : MovieRepository {
    var popular: AppResult<List<Movie>> = AppResult.Success(listOf(movie(1)))
    var topRated: AppResult<List<Movie>> = AppResult.Success(listOf(movie(2)))
    var nowPlaying: AppResult<List<Movie>> = AppResult.Success(listOf(movie(3)))
    var gate: CompletableDeferred<Unit>? = null
    var popularCalls = 0
    var topRatedCalls = 0

    override suspend fun popular(): AppResult<List<Movie>> {
        popularCalls++
        gate?.await()
        return popular
    }

    override suspend fun topRated(): AppResult<List<Movie>> {
        topRatedCalls++
        gate?.await()
        return topRated
    }

    override suspend fun nowPlaying(): AppResult<List<Movie>> {
        gate?.await()
        return nowPlaying
    }

    override suspend fun movieDetail(id: Int): AppResult<MovieDetail> = error("not used by Home")
    override suspend fun reviews(id: Int): AppResult<List<Review>> = error("not used by Home")
}

class HomeViewModelTest {

    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    private val repository = FakeMovieRepository()

    private fun viewModel() = HomeViewModel(repository)

    private val offline = AppResult.Failure(AppError.Network)

    @Test
    fun startsWithThreeLoadingSections() {
        repository.gate = CompletableDeferred()

        val state = viewModel().state.value

        assertEquals(SectionState.Loading, state.popular)
        assertEquals(SectionState.Loading, state.topRated)
        assertEquals(SectionState.Loading, state.nowPlaying)
    }

    @Test
    fun sectionsReachContentOrEmpty() {
        repository.nowPlaying = AppResult.Success(emptyList())

        val state = viewModel().state.value

        assertEquals(SectionState.Content(listOf(movie(1))), state.popular)
        assertEquals(SectionState.Content(listOf(movie(2))), state.topRated)
        assertEquals(SectionState.Empty, state.nowPlaying)
    }

    @Test
    fun oneFailureLeavesTheOthersAlone() {
        repository.topRated = AppResult.Failure(AppError.Server)

        val state = viewModel().state.value

        assertEquals(SectionState.Error(AppError.Server), state.topRated)
        assertTrue(state.popular is SectionState.Content)
        assertTrue(state.nowPlaying is SectionState.Content)
        assertFalse(state.offlineSheetVisible)
    }

    @Test
    fun lostConnectionOpensTheSheetWhenAllThreeFail() {
        repository.popular = offline
        repository.topRated = offline
        repository.nowPlaying = offline

        val state = viewModel().state.value

        assertTrue(state.offlineSheetVisible)
        assertEquals(SectionState.Error(AppError.Network), state.popular)
    }

    @Test
    fun retryReloadsOnlyThatSection() {
        repository.topRated = AppResult.Failure(AppError.Server)
        val vm = viewModel()
        repository.topRated = AppResult.Success(listOf(movie(9)))

        vm.onRetry(HomeSection.TopRated)

        assertEquals(SectionState.Content(listOf(movie(9))), vm.state.value.topRated)
        assertEquals(1, repository.popularCalls)
        assertEquals(2, repository.topRatedCalls)
    }

    @Test
    fun retryWhileLoadingDoesNothing() {
        repository.gate = CompletableDeferred()
        val vm = viewModel()

        vm.onRetry(HomeSection.Popular)
        vm.onRetry(HomeSection.Popular)

        assertEquals(1, repository.popularCalls)
    }

    @Test
    fun dismissingThenRetryingSuccessfullyKeepsTheSheetClosed() {
        repository.topRated = offline
        val vm = viewModel()
        vm.onDismissOfflineSheet()
        repository.topRated = AppResult.Success(listOf(movie(2)))

        vm.onRetry(HomeSection.TopRated)

        assertFalse(vm.state.value.offlineSheetVisible)
    }

    @Test
    fun retryFailingOfflineAgainReopensTheSheet() {
        repository.topRated = offline
        val vm = viewModel()
        vm.onDismissOfflineSheet()

        vm.onRetry(HomeSection.TopRated)

        assertTrue(vm.state.value.offlineSheetVisible)
    }

    @Test
    fun tryAgainClosesTheSheetWhenSectionsLoad() {
        repository.popular = offline
        repository.topRated = offline
        repository.nowPlaying = offline
        val vm = viewModel()
        repository.popular = AppResult.Success(listOf(movie(1)))
        repository.topRated = AppResult.Success(listOf(movie(2)))
        repository.nowPlaying = AppResult.Success(listOf(movie(3)))

        vm.onTryAgain()

        assertFalse(vm.state.value.offlineSheetVisible)
        assertTrue(vm.state.value.popular is SectionState.Content)
    }

    @Test
    fun tryAgainKeepsTheSheetOpenWhileStillOffline() {
        repository.popular = offline
        repository.topRated = offline
        repository.nowPlaying = offline
        val vm = viewModel()

        vm.onTryAgain()

        assertTrue(vm.state.value.offlineSheetVisible)
    }
}
