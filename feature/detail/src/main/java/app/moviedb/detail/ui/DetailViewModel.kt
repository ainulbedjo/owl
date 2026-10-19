package app.moviedb.detail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import app.moviedb.core.common.AppResult
import app.moviedb.core.common.OfflineAwareViewModel
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.data.repository.FavoriteRepository
import app.moviedb.core.data.repository.MovieRepository
import app.moviedb.core.model.MovieDetail
import app.moviedb.core.model.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class DetailMessage { FavoriteAdded, FavoriteRemoved }

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MovieRepository,
    private val favorites: FavoriteRepository,
) : OfflineAwareViewModel() {

    /**
     * Navigation stores each route argument in the [SavedStateHandle] under its property name,
     * so the id is there when the screen opens, and again after the process is recreated.
     */
    private val movieId: Int = checkNotNull(savedStateHandle[DetailRoute::movieId.name]) {
        "DetailViewModel needs a movieId in its route"
    }

    private val _state = MutableStateFlow(DetailUiState())
    val state: StateFlow<DetailUiState> = _state.asStateFlow()

    private val messageChannel = Channel<DetailMessage>(Channel.BUFFERED)
    val messages: Flow<DetailMessage> = messageChannel.receiveAsFlow()

    private enum class Part { Movie, Reviews }

    init {
        // The movie and its reviews start together and run in parallel.
        Part.entries.forEach(::load)

        // The Save button reflects the database, not a local flag. If the movie is removed on
        // the Favorites screen, this screen is already right when the viewer comes back.
        viewModelScope.launch {
            favorites.observeIsFavorite(movieId)
                .catch { /* A read failure leaves the heart off; it must not crash the screen. */ }
                .collect { isFavorite -> _state.update { it.copy(isFavorite = isFavorite) } }
        }
    }

    fun onRetryMovie() = load(Part.Movie)

    fun onRetryReviews() = load(Part.Reviews)

    override fun reloadFailedParts() {
        val failed = _state.value
        if (failed.movie is SectionState.Error) load(Part.Movie)
        if (failed.reviews is SectionState.Error) load(Part.Reviews)
    }

    override fun setOfflineSheetVisible(visible: Boolean) {
        _state.update { it.copy(offlineSheetVisible = visible) }
    }

    fun onFavoriteClick() {
        // The saved snapshot is built from the loaded movie, so there is nothing to toggle before that.
        val movie = (_state.value.movie as? SectionState.Content)?.data ?: return

        viewModelScope.launch {
            // The check and the write are one transaction, so quick taps cannot double-insert.
            val nowFavorite = favorites.toggle(movie)
            messageChannel.trySend(
                if (nowFavorite) DetailMessage.FavoriteAdded else DetailMessage.FavoriteRemoved,
            )
        }
    }

    fun onShareClick() {
        if (_state.value.movie !is SectionState.Content) return
        _state.update { it.copy(shareSheetVisible = true) }
    }

    fun onDismissShare() {
        _state.update { it.copy(shareSheetVisible = false) }
    }

    private fun load(part: Part) = loadOnce(part) {
        when (part) {
            Part.Movie -> {
                _state.update { it.copy(movie = SectionState.Loading) }
                showMovie(repository.movieDetail(movieId))
            }
            Part.Reviews -> {
                _state.update { it.copy(reviews = SectionState.Loading) }
                showReviews(repository.reviews(movieId))
            }
        }
    }

    private fun showMovie(result: AppResult<MovieDetail>) {
        val movie = when (result) {
            is AppResult.Success -> SectionState.Content(result.data)
            // A 404 stays an Error(NotFound): the screen shows "not available" with no Retry.
            is AppResult.Failure -> SectionState.Error(result.error)
        }
        _state.update { it.copy(movie = movie) }
        handleOfflineSheet(result)
    }

    private fun showReviews(result: AppResult<List<Review>>) {
        val reviews = when (result) {
            is AppResult.Success ->
                if (result.data.isEmpty()) SectionState.Empty else SectionState.Content(result.data)
            is AppResult.Failure -> SectionState.Error(result.error)
        }
        _state.update { it.copy(reviews = reviews) }
        handleOfflineSheet(result)
    }
}
