package app.moviedb.detail.ui

import androidx.lifecycle.SavedStateHandle
import app.moviedb.core.common.AppError
import app.moviedb.core.common.AppResult
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.data.repository.FavoriteRepository
import app.moviedb.core.data.repository.MovieRepository
import app.moviedb.core.model.Movie
import app.moviedb.core.model.MovieDetail
import app.moviedb.core.model.Review
import app.moviedb.detail.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

class DetailViewModelTest {

    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    private val repository = FakeMovieRepository()
    private val favorites = FakeFavoriteRepository()

    private fun viewModel(movieId: Int = 7) = DetailViewModel(
        savedStateHandle = SavedStateHandle(mapOf(DetailRoute::movieId.name to movieId)),
        repository = repository,
        favorites = favorites,
    )

    private val offline = AppResult.Failure(AppError.Network)

    @Test
    fun readsTheMovieIdFromTheRoute() {
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
    fun favoriteSavesTheMovieAndSendsAMessage() = runTest {
        val vm = viewModel(movieId = 7)

        vm.onFavoriteClick()

        assertEquals(listOf(7), favorites.saved.value.map { it.id })
        assertTrue(vm.state.value.isFavorite)
        assertEquals(DetailMessage.FavoriteAdded, vm.messages.first())
    }

    @Test
    fun favoriteAgainRemovesItAndSendsAMessage() = runTest {
        val vm = viewModel(movieId = 7)
        vm.onFavoriteClick()
        vm.messages.first()

        vm.onFavoriteClick()

        assertEquals(emptyList<Movie>(), favorites.saved.value)
        assertFalse(vm.state.value.isFavorite)
        assertEquals(DetailMessage.FavoriteRemoved, vm.messages.first())
    }

    @Test
    fun heartStartsOnWhenTheMovieIsAlreadySaved() {
        favorites.saved.value = listOf(Movie(7, "Saved", "", null, null, 7.0))

        assertTrue(viewModel(movieId = 7).state.value.isFavorite)
    }

    @Test
    fun heartFollowsTheDatabaseWhenRemovedElsewhere() {
        favorites.saved.value = listOf(Movie(7, "Saved", "", null, null, 7.0))
        val vm = viewModel(movieId = 7)

        // The viewer removes it on the Favorites screen.
        favorites.saved.value = emptyList()

        assertFalse(vm.state.value.isFavorite)
    }

    @Test
    fun anotherSavedMovieDoesNotTurnTheHeartOn() {
        favorites.saved.value = listOf(Movie(99, "Other", "", null, null, 7.0))

        assertFalse(viewModel(movieId = 7).state.value.isFavorite)
    }

    @Test
    fun savingIsNotOfferedWhenTheMovieFailedToLoad() {
        repository.movie = { AppResult.Failure(AppError.Server) }
        val vm = viewModel()

        vm.onFavoriteClick()

        assertEquals(emptyList<Movie>(), favorites.saved.value)
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
