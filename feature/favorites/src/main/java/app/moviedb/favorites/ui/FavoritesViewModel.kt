package app.moviedb.favorites.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.moviedb.core.common.AppError
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.common.state.toSectionState
import app.moviedb.core.data.repository.FavoriteRepository
import app.moviedb.core.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** One-shot messages for a snackbar. The screen turns each into text, so no strings live here. */
enum class FavoritesMessage { Removed }

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favorites: FavoriteRepository,
) : ViewModel() {

    /** Changing this value starts reading the list again. That is what Retry does. */
    private val reload = MutableStateFlow(0)

    /**
     * The list comes straight from Room as a `Flow`, so anything that changes favorites elsewhere
     * (the Save button on Detail) shows up here with no refresh code. Collection stops five
     * seconds after the screen goes away, and restarts from the database when it returns.
     */
    val state: StateFlow<FavoritesUiState> = reload
        .flatMapLatest { attempt -> readFavorites(showLoading = attempt > 0) }
        .map { FavoritesUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), FavoritesUiState())

    private val messageChannel = Channel<FavoritesMessage>(Channel.BUFFERED)
    val messages: Flow<FavoritesMessage> = messageChannel.receiveAsFlow()

    fun onRemoveClick(movieId: Int) {
        viewModelScope.launch {
            // The list updates through the Flow. Removing a movie twice deletes nothing the second time.
            favorites.remove(movieId)
            messageChannel.trySend(FavoritesMessage.Removed)
        }
    }

    fun onRetry() {
        reload.update { it + 1 }
    }

    /**
     * [showLoading] is false for the first read: the state already starts as `Loading`. It is true
     * for Retry. It is never set when the screen comes back, so returning to Favorites keeps the
     * list on screen instead of flashing a skeleton while Room answers.
     */
    private fun readFavorites(showLoading: Boolean): Flow<SectionState<List<Movie>>> = favorites.observeAll()
        .map { it.toSectionState() }
        .onStart { if (showLoading) emit(SectionState.Loading) }
        // A failing read becomes an error card, never a crash.
        .catch { emit(SectionState.Error(AppError.Unexpected)) }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
