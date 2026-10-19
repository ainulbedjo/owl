package app.moviedb.favorites.ui

import app.moviedb.core.common.AppError
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.data.repository.FavoriteRepository
import app.moviedb.core.model.Movie
import app.moviedb.core.model.MovieDetail
import app.moviedb.favorites.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private fun movie(id: Int) = Movie(id, "Movie $id", "Overview $id", null, null, 7.0)

/** Keeps favorites in memory. When [failing] is true, reading the list throws. */
private class FakeFavoriteRepository : FavoriteRepository {
    val saved = MutableStateFlow<List<Movie>>(emptyList())
    var failing = false
    val removedIds = mutableListOf<Int>()

    override fun observeAll(): Flow<List<Movie>> =
        if (failing) flow { throw IllegalStateException("database is gone") } else saved

    override fun observeCount(): Flow<Int> = saved.map { it.size }
    override fun observeIsFavorite(id: Int): Flow<Boolean> = saved.map { list -> list.any { it.id == id } }
    override suspend fun toggle(movie: MovieDetail): Boolean = error("not used by Favorites")

    override suspend fun remove(id: Int) {
        removedIds += id
        saved.value = saved.value.filterNot { it.id == id }
    }
}

class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    private val repository = FakeFavoriteRepository()

    private fun viewModel() = FavoritesViewModel(repository)

    /** The state is shared while something collects, so tests wait for the value they expect. */
    private suspend fun FavoritesViewModel.next(matches: (SectionState<List<Movie>>) -> Boolean) =
        state.first { matches(it.favorites) }.favorites

    @Test
    fun startsLoadingBeforeAnythingIsRead() {
        assertEquals(SectionState.Loading, viewModel().state.value.favorites)
    }

    @Test
    fun savedMoviesBecomeContentInTheOrderGiven() = runTest {
        repository.saved.value = listOf(movie(3), movie(2), movie(1))

        val content = viewModel().next { it is SectionState.Content } as SectionState.Content

        assertEquals(listOf(3, 2, 1), content.data.map { it.id })
    }

    @Test
    fun noSavedMoviesIsEmpty() = runTest {
        assertEquals(SectionState.Empty, viewModel().next { it !is SectionState.Loading })
    }

    @Test
    fun removingCallsTheRepositoryAndSendsAMessage() = runTest {
        repository.saved.value = listOf(movie(1), movie(2))
        val vm = viewModel()
        vm.next { it is SectionState.Content }

        vm.onRemoveClick(1)

        assertEquals(listOf(1), repository.removedIds)
        assertEquals(FavoritesMessage.Removed, vm.messages.first())
    }

    @Test
    fun removingTheLastMovieMovesToEmpty() = runTest {
        repository.saved.value = listOf(movie(1))
        val vm = viewModel()
        vm.next { it is SectionState.Content }

        vm.onRemoveClick(1)

        assertEquals(SectionState.Empty, vm.next { it is SectionState.Empty })
    }

    @Test
    fun aMovieSavedElsewhereAppearsWithoutRefreshing() = runTest {
        val vm = viewModel()
        vm.next { it is SectionState.Empty }

        // The viewer saves a movie on Detail.
        repository.saved.value = listOf(movie(5))

        val content = vm.next { it is SectionState.Content } as SectionState.Content
        assertEquals(listOf(5), content.data.map { it.id })
    }

    @Test
    fun aFailingReadBecomesAnErrorAndRetryRecovers() = runTest {
        repository.failing = true
        val vm = viewModel()
        assertEquals(SectionState.Error(AppError.Unexpected), vm.next { it is SectionState.Error })

        repository.failing = false
        repository.saved.value = listOf(movie(1))
        vm.onRetry()

        assertTrue(vm.next { it is SectionState.Content } is SectionState.Content)
    }
}
