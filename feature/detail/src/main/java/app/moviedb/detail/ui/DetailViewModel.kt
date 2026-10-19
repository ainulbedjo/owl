package app.moviedb.detail.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.moviedb.core.common.AppError
import app.moviedb.core.common.AppResult
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.data.repository.MovieRepository
import app.moviedb.core.model.MovieDetail
import app.moviedb.core.model.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class DetailMessage { FavoriteAdded, FavoriteRemoved }

@HiltViewModel
class DetailViewModel internal constructor(
    private val movieId: Int,
    @Inject private val repository: MovieRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DetailUiState())
    val state: StateFlow<DetailUiState> = _state.asStateFlow()

    private val messageChannel = Channel<DetailMessage>(Channel.BUFFERED)
    val messages: Flow<DetailMessage> = messageChannel.receiveAsFlow()

    private enum class Part { Movie, Reviews }

    /** Parts with a request in flight. A part is never loaded twice at once. */
    private val inFlight = mutableSetOf<Part>()

    /** True after "Try again" on the offline sheet, so the first success can close it. */
    private var retryingFromSheet = false

    init {
        // The movie and its reviews start together and run in parallel.
        Part.entries.forEach(::load)
    }

    fun onRetryMovie() = load(Part.Movie)

    fun onRetryReviews() = load(Part.Reviews)

    fun onTryAgain() {
        retryingFromSheet = true
        val failed = _state.value
        if (failed.movie is SectionState.Error) load(Part.Movie)
        if (failed.reviews is SectionState.Error) load(Part.Reviews)
    }

    fun onFavoriteClick() {
        // The saved snapshot is built from the loaded movie, so there is nothing to toggle before that.
        if (_state.value.movie !is SectionState.Content) return

        // TODO(data): replace this in-memory flag with FavoriteRepository.toggle(movie)
        // and collect observeIsFavorite(id) into the state (see docs/todo/data-fetch.md).
        val nowFavorite = !_state.value.isFavorite
        _state.update { it.copy(isFavorite = nowFavorite) }
        messageChannel.trySend(if (nowFavorite) DetailMessage.FavoriteAdded else DetailMessage.FavoriteRemoved)
    }

    fun onShareClick() {
        if (_state.value.movie !is SectionState.Content) return
        _state.update { it.copy(shareSheetVisible = true) }
    }

    fun onDismissShare() {
        _state.update { it.copy(shareSheetVisible = false) }
    }

    fun onDismissOfflineSheet() {
        retryingFromSheet = false
        _state.update { it.copy(offlineSheetVisible = false) }
    }

    private fun load(part: Part) {
        // A second tap on Retry while the first request runs does nothing.
        if (!inFlight.add(part)) return

        _state.update {
            when (part) {
                Part.Movie -> it.copy(movie = SectionState.Loading)
                Part.Reviews -> it.copy(reviews = SectionState.Loading)
            }
        }
        viewModelScope.launch {
            when (part) {
                Part.Movie -> showMovie(repository.movieDetail(movieId))
                Part.Reviews -> showReviews(repository.reviews(movieId))
            }
            inFlight.remove(part)
        }
    }

    private fun showMovie(result: AppResult<MovieDetail>) {
        val movie = when (result) {
            is AppResult.Success -> SectionState.Content(result.data)
            // A 404 stays an Error(NotFound): the screen shows "not available" with no Retry.
            is AppResult.Failure -> SectionState.Error(result.error)
        }
        commit(result) { it.copy(movie = movie) }
    }

    private fun showReviews(result: AppResult<List<Review>>) {
        val reviews = when (result) {
            is AppResult.Success ->
                if (result.data.isEmpty()) SectionState.Empty else SectionState.Content(result.data)
            is AppResult.Failure -> SectionState.Error(result.error)
        }
        commit(result) { it.copy(reviews = reviews) }
    }

    /**
     * Applies [change], then decides about the offline sheet. Only a lost connection opens it,
     * and one sheet serves both parts, so a second failure while it is open changes nothing.
     */
    private fun commit(result: AppResult<*>, change: (DetailUiState) -> DetailUiState) {
        val opensSheet = result is AppResult.Failure && result.error == AppError.Network
        val closesSheet = result is AppResult.Success && retryingFromSheet
        if (closesSheet) retryingFromSheet = false

        _state.update { current ->
            val updated = change(current)
            when {
                opensSheet -> updated.copy(offlineSheetVisible = true)
                closesSheet -> updated.copy(offlineSheetVisible = false)
                else -> updated
            }
        }
    }
}
