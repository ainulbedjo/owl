package app.moviedb.explore.ui

import androidx.lifecycle.SavedStateHandle
import app.moviedb.core.common.AppResult
import app.moviedb.core.common.OfflineAwareViewModel
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.common.state.toSectionState
import app.moviedb.core.model.Movie
import app.moviedb.explore.data.repository.ExploreRepository
import app.moviedb.explore.model.MovieSort
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AppendState { Idle, Loading, Error }

data class ExploreUiState(
    val title: String? = null,
    val movies: SectionState<List<Movie>> = SectionState.Loading,
    val append: AppendState = AppendState.Idle,
    val hasMore: Boolean = false,
    val totalResults: Int = 0,
    val offlineSheetVisible: Boolean = false,
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ExploreRepository,
) : OfflineAwareViewModel() {

    private val genreId: Int? = savedStateHandle[ExploreRoute::genreId.name]
    private val genreName: String? = savedStateHandle[ExploreRoute::genreName.name]

    private val _state = MutableStateFlow(
        ExploreUiState(title = genreName),
    )
    val state: StateFlow<ExploreUiState> = _state.asStateFlow()

    private var nextPage = 1

    init {
        loadNextPage()
    }

    fun onRetry() = loadNextPage()

    fun onLoadMore() {
        val current = _state.value
        if (current.hasMore && current.append == AppendState.Idle) loadNextPage()
    }

    override fun reloadFailedParts() = loadNextPage()

    override fun setOfflineSheetVisible(visible: Boolean) {
        _state.update { it.copy(offlineSheetVisible = visible) }
    }

    private fun loadNextPage() = loadOnce("page") {
        val page = nextPage
        _state.update {
            if (page == 1) it.copy(movies = SectionState.Loading) else it.copy(append = AppendState.Loading)
        }

        val result = repository.movies(genreId, MovieSort.Popular, page)
        handleOfflineSheet(result)

        _state.update { current ->
            when (result) {
                is AppResult.Success -> {
                    nextPage = page + 1
                    val all = ((current.movies as? SectionState.Content)?.data.orEmpty() + result.data.items)
                        .distinctBy { it.id }
                    current.copy(
                        movies = all.toSectionState(),
                        append = AppendState.Idle,
                        hasMore = result.data.hasMore,
                        totalResults = result.data.totalResults,
                    )
                }
                is AppResult.Failure ->
                    if (page == 1) current.copy(movies = SectionState.Error(result.error))
                    else current.copy(append = AppendState.Error)
            }
        }
    }
}
