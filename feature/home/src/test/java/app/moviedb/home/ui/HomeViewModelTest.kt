package app.moviedb.home.ui

import app.moviedb.core.common.AppError
import app.moviedb.core.common.AppResult
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.data.repository.FavoriteRepository
import app.moviedb.core.data.repository.MovieRepository
import app.moviedb.core.model.Movie
import app.moviedb.core.model.MovieDetail
import app.moviedb.core.model.Review
import app.moviedb.home.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
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


/** Keeps favorites in memory and tells observers about every change, like the Room version does. */
private class FakeFavoriteRepository : FavoriteRepository {
    val saved = MutableStateFlow<List<Movie>>(emptyList())

    override fun observeAll(): Flow<List<Movie>> = saved
    override fun observeCount(): Flow<Int> = saved.map { it.size }
    override fun observeIsFavorite(id: Int): Flow<Boolean> = saved.map { list -> list.any { it.id == id } }

    override suspend fun toggle(movie: MovieDetail): Boolean {
        val exists = saved.value.any { it.id == movie.id }
        saved.value = if (exists) {
            saved.value.filterNot { it.id == movie.id }
        } else {
            listOf(Movie(movie.id, movie.title, movie.overview, movie.posterPath, movie.releaseDate, movie.rating)) + saved.value
        }
        return !exists
    }

    override suspend fun remove(id: Int) {
        saved.value = saved.value.filterNot { it.id == id }
    }
}

class HomeViewModelTest {

    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    private val repository = FakeMovieRepository()
    private val favorites = FakeFavoriteRepository()

    private fun viewModel() = HomeViewModel(repository, favorites)

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

    @Test
    fun badgeIsHiddenWhenNothingIsSaved() {
        assertEquals(0, viewModel().state.value.favoriteCount)
    }

    @Test
    fun badgeFollowsTheNumberOfFavorites() {
        val vm = viewModel()

        favorites.saved.value = listOf(movie(1), movie(2), movie(3))
        assertEquals(3, vm.state.value.favoriteCount)

        favorites.saved.value = listOf(movie(1))
        assertEquals(1, vm.state.value.favoriteCount)
    }
}
