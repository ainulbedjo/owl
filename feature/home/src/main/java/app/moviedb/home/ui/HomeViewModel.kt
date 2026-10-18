package app.moviedb.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.moviedb.core.common.AppError
import app.moviedb.core.common.AppResult
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.data.repository.MovieRepository
import app.moviedb.core.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    /** Sections with a request in flight. A section is never loaded twice at once. */
    private val inFlight = mutableSetOf<HomeSection>()

    /** True after "Try again" on the offline sheet, so the first success can close it. */
    private var retryingFromSheet = false

    init {
        // The three lists start together and run in parallel, each in its own coroutine.
        HomeSection.entries.forEach(::load)
    }

    fun onRetry(section: HomeSection) = load(section)

    fun onTryAgain() {
        retryingFromSheet = true
        HomeSection.entries
            .filter { _state.value.sectionOf(it) is SectionState.Error }
            .forEach(::load)
    }

    fun onDismissOfflineSheet() {
        retryingFromSheet = false
        _state.update { it.copy(offlineSheetVisible = false) }
    }

    private fun load(section: HomeSection) {
        // A second tap on Retry while the first request runs does nothing.
        if (!inFlight.add(section)) return

        _state.update { it.withSection(section, SectionState.Loading) }
        viewModelScope.launch {
            val result = fetch(section)
            inFlight.remove(section)
            apply(section, result)
        }
    }

    private suspend fun fetch(section: HomeSection): AppResult<List<Movie>> = when (section) {
        HomeSection.Popular -> repository.popular()
        HomeSection.TopRated -> repository.topRated()
        HomeSection.NowPlaying -> repository.nowPlaying()
    }

    private fun apply(section: HomeSection, result: AppResult<List<Movie>>) {
        val sectionState = when (result) {
            is AppResult.Success ->
                if (result.data.isEmpty()) SectionState.Empty else SectionState.Content(result.data)
            is AppResult.Failure -> SectionState.Error(result.error)
        }
        // Only a lost connection opens the sheet. Other errors stay in the section's own card.
        val opensSheet = result is AppResult.Failure && result.error == AppError.Network
        val closesSheet = result is AppResult.Success && retryingFromSheet
        if (closesSheet) retryingFromSheet = false

        _state.update { current ->
            val updated = current.withSection(section, sectionState)
            when {
                opensSheet -> updated.copy(offlineSheetVisible = true)
                closesSheet -> updated.copy(offlineSheetVisible = false)
                else -> updated
            }
        }
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
