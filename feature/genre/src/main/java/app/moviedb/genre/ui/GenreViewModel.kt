package app.moviedb.genre.ui

import app.moviedb.core.common.OfflineAwareViewModel
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.common.state.toListSectionState
import app.moviedb.genre.data.repository.GenreRepository
import app.moviedb.genre.model.Genre
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class GenreViewModel @Inject constructor(
    private val repository: GenreRepository,
) : OfflineAwareViewModel() {

    private val _state = MutableStateFlow(GenreUiState())
    val state: StateFlow<GenreUiState> get() = _state

    init {
        refresh()
    }

    private fun refresh() = loadOnce("genres") {
        _state.update { it.copy(genres = SectionState.Loading) }
        val result = repository.genres()
        handleOfflineSheet(result)
        _state.update { it.copy(genres = result.toListSectionState()) }
    }

    override fun setOfflineSheetVisible(visible: Boolean) {
        _state.update { it.copy(offlineSheetVisible = visible) }
    }

    override fun reloadFailedParts() {
        refresh()
    }
}

data class GenreUiState(
    val genres: SectionState<List<Genre>> = SectionState.Loading,
    val offlineSheetVisible: Boolean = false,
)