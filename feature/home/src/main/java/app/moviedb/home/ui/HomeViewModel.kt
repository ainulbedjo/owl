package app.moviedb.home.ui

import androidx.lifecycle.viewModelScope
import app.moviedb.core.common.AppResult
import app.moviedb.core.common.OfflineAwareViewModel
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.common.state.toListSectionState
import app.moviedb.core.data.repository.FavoriteRepository
import app.moviedb.core.data.repository.MovieRepository
import app.moviedb.core.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val favorites: FavoriteRepository,
) : OfflineAwareViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        // The three lists start together and run in parallel, each in its own coroutine.
        HomeSection.entries.forEach(::load)

        // The badge on the heart button follows the database, so saving on Detail updates it live.
        viewModelScope.launch {
            favorites.observeCount()
                .catch { /* A read failure leaves the badge hidden; it must not crash the screen. */ }
                .collect { count -> _state.update { it.copy(favoriteCount = count) } }
        }
    }

    fun onRetry(section: HomeSection) = load(section)

    override fun reloadFailedParts() {
        HomeSection.entries
            .filter { _state.value.sectionOf(it) is SectionState.Error }
            .forEach(::load)
    }

    override fun setOfflineSheetVisible(visible: Boolean) {
        _state.update { it.copy(offlineSheetVisible = visible) }
    }

    private fun load(section: HomeSection) = loadOnce(section) {
        _state.update { it.withSection(section, SectionState.Loading) }

        val result = fetch(section)

        _state.update { it.withSection(section, result.toListSectionState()) }
        handleOfflineSheet(result)
    }

    private suspend fun fetch(section: HomeSection): AppResult<List<Movie>> = when (section) {
        HomeSection.Popular -> repository.popular()
        HomeSection.TopRated -> repository.topRated()
        HomeSection.NowPlaying -> repository.nowPlaying()
    }
}

private fun HomeUiState.sectionOf(section: HomeSection): SectionState<List<Movie>> = when (section) {
    HomeSection.Popular -> popular
    HomeSection.TopRated -> topRated
    HomeSection.NowPlaying -> nowPlaying
}

private fun HomeUiState.withSection(
    section: HomeSection,
    value: SectionState<List<Movie>>,
): HomeUiState = when (section) {
    HomeSection.Popular -> copy(popular = value)
    HomeSection.TopRated -> copy(topRated = value)
    HomeSection.NowPlaying -> copy(nowPlaying = value)
}
