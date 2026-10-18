package app.moviedb.detail.ui

import app.moviedb.core.common.AppError
import app.moviedb.core.common.AppResult
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.data.repository.MovieRepository
import app.moviedb.core.model.Movie
import app.moviedb.core.model.MovieDetail
import app.moviedb.core.model.Review
import app.moviedb.detail.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private fun detail(id: Int) = MovieDetail(
    id = id, title = "Movie $id", tagline = null, overview = "", posterPath = null,
    backdropPath = null, releaseDate = null, runtimeMinutes = null, genres = emptyList(), rating = 7.0,
)

private fun review(id: String) = Review(id, "author", null, null, "text", null)

/** A repository whose answers each test sets. While [gate] is set, every call waits for it. */
private class FakeMovieRepository : MovieRepository {
    var movie: (Int) -> AppResult<MovieDetail> = { AppResult.Success(detail(it)) }
    var reviews: AppResult<List<Review>> = AppResult.Success(listOf(review("r1")))
    var gate: CompletableDeferred<Unit>? = null
    val requestedIds = mutableListOf<Int>()
    var movieCalls = 0
    var reviewCalls = 0

    override suspend fun movieDetail(id: Int): AppResult<MovieDetail> {
        movieCalls++
        requestedIds += id
        gate?.await()
        return movie(id)
    }

    override suspend fun reviews(id: Int): AppResult<List<Review>> {
        reviewCalls++
        gate?.await()
        return reviews
    }

    override suspend fun popular(): AppResult<List<Movie>> = error("not used by Detail")
    override suspend fun topRated(): AppResult<List<Movie>> = error("not used by Detail")
    override suspend fun nowPlaying(): AppResult<List<Movie>> = error("not used by Detail")
}

class DetailViewModelTest {

    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    private val repository = FakeMovieRepository()

    private fun viewModel(movieId: Int = 7) = DetailViewModel(movieId, repository)

    private val offline = AppResult.Failure(AppError.Network)

    @Test
    fun requestsTheMovieWithTheGivenId() {
        viewModel(movieId = 42)

        assertEquals(listOf(42), repository.requestedIds)
    }

    @Test
    fun startsLoadingBothPartsTogether() {
        repository.gate = CompletableDeferred()

        val vm = viewModel()

        assertEquals(SectionState.Loading, vm.state.value.movie)
        assertEquals(SectionState.Loading, vm.state.value.reviews)
        assertEquals(1, repository.movieCalls)
        assertEquals(1, repository.reviewCalls)
    }

    @Test
    fun bothPartsReachContent() {
        val state = viewModel().state.value

        assertEquals(SectionState.Content(detail(7)), state.movie)
        assertEquals(SectionState.Content(listOf(review("r1"))), state.reviews)
    }

    @Test
    fun movieErrorLeavesReviewsAlone() {
        repository.movie = { AppResult.Failure(AppError.Server) }

        val state = viewModel().state.value

        assertEquals(SectionState.Error(AppError.Server), state.movie)
        assertTrue(state.reviews is SectionState.Content)
        assertFalse(state.offlineSheetVisible)
    }

    @Test
    fun reviewsErrorLeavesTheMovieAlone() {
        repository.reviews = AppResult.Failure(AppError.Server)

        val state = viewModel().state.value

        assertTrue(state.movie is SectionState.Content)
        assertEquals(SectionState.Error(AppError.Server), state.reviews)
    }

    @Test
    fun noReviewsIsEmptyNotAnError() {
        repository.reviews = AppResult.Success(emptyList())

        assertEquals(SectionState.Empty, viewModel().state.value.reviews)
    }

    @Test
    fun unknownMovieIsNotFound() {
        repository.movie = { AppResult.Failure(AppError.NotFound) }

        assertEquals(SectionState.Error(AppError.NotFound), viewModel().state.value.movie)
    }

    @Test
    fun lostConnectionOnBothOpensOneSheet() {
        repository.movie = { offline }
        repository.reviews = offline

        assertTrue(viewModel().state.value.offlineSheetVisible)
    }

    @Test
    fun retryMovieReloadsOnlyTheMovie() {
        repository.movie = { AppResult.Failure(AppError.Server) }
        val vm = viewModel()
        repository.movie = { AppResult.Success(detail(it)) }

        vm.onRetryMovie()

        assertTrue(vm.state.value.movie is SectionState.Content)
        assertEquals(2, repository.movieCalls)
        assertEquals(1, repository.reviewCalls)
    }

    @Test
    fun retryReviewsReloadsOnlyTheReviews() {
        repository.reviews = AppResult.Failure(AppError.Server)
        val vm = viewModel()
        repository.reviews = AppResult.Success(listOf(review("r2")))

        vm.onRetryReviews()

        assertEquals(SectionState.Content(listOf(review("r2"))), vm.state.value.reviews)
        assertEquals(1, repository.movieCalls)
        assertEquals(2, repository.reviewCalls)
    }

    @Test
    fun retryWhileLoadingDoesNothing() {
        repository.gate = CompletableDeferred()
        val vm = viewModel()

        vm.onRetryMovie()
        vm.onRetryMovie()

        assertEquals(1, repository.movieCalls)
    }

    @Test
    fun tryAgainReloadsFailedPartsAndClosesTheSheet() {
        repository.movie = { offline }
        repository.reviews = offline
        val vm = viewModel()
        repository.movie = { AppResult.Success(detail(it)) }
        repository.reviews = AppResult.Success(listOf(review("r1")))

        vm.onTryAgain()

        assertFalse(vm.state.value.offlineSheetVisible)
        assertTrue(vm.state.value.movie is SectionState.Content)
        assertTrue(vm.state.value.reviews is SectionState.Content)
    }

    @Test
    fun tryAgainKeepsTheSheetOpenWhileStillOffline() {
        repository.movie = { offline }
        repository.reviews = offline
        val vm = viewModel()

        vm.onTryAgain()

        assertTrue(vm.state.value.offlineSheetVisible)
    }

    @Test
    fun favoriteBeforeTheMovieLoadsDoesNothing() {
        repository.gate = CompletableDeferred()
        val vm = viewModel()

        vm.onFavoriteClick()

        assertFalse(vm.state.value.isFavorite)
    }

    @Test
    fun favoriteTogglesAndSendsAMessage() = runTest {
        val vm = viewModel()

        vm.onFavoriteClick()
        assertTrue(vm.state.value.isFavorite)
        assertEquals(DetailMessage.FavoriteAdded, vm.messages.first())

        vm.onFavoriteClick()
        assertFalse(vm.state.value.isFavorite)
        assertEquals(DetailMessage.FavoriteRemoved, vm.messages.first())
    }

    @Test
    fun shareSheetShowsAndHides() {
        val vm = viewModel()

        vm.onShareClick()
        assertTrue(vm.state.value.shareSheetVisible)

        vm.onDismissShare()
        assertFalse(vm.state.value.shareSheetVisible)
    }

    @Test
    fun shareBeforeTheMovieLoadsDoesNothing() {
        repository.gate = CompletableDeferred()
        val vm = viewModel()

        vm.onShareClick()

        assertFalse(vm.state.value.shareSheetVisible)
    }
}
