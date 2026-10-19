package app.moviedb.favorites.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.moviedb.core.common.state.SectionState
import app.moviedb.favorites.R
import app.moviedb.favorites.data.sampleFavoritesState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.Serializable

@Serializable
data object FavoritesRoute

fun NavGraphBuilder.favoritesScreen(
    onBack: () -> Unit,
    onMovieClick: (movieId: Int) -> Unit,
    onBrowseClick: () -> Unit,
) {
    composable<FavoritesRoute> {
        FavoritesEntry(onBack = onBack, onMovieClick = onMovieClick, onBrowseClick = onBrowseClick)
    }
}

// TODO(data): replace this local sample state with FavoritesViewModel (see docs/todo/data-fetch.md).
// Until then removing a row only changes in-memory state, so the layout can be tried by hand.
@Composable
private fun FavoritesEntry(
    onBack: () -> Unit,
    onMovieClick: (movieId: Int) -> Unit,
    onBrowseClick: () -> Unit,
) {
    var state by remember { mutableStateOf(sampleFavoritesState) }
    val messageChannel = remember { Channel<String>(Channel.BUFFERED) }
    val messages = remember { messageChannel.receiveAsFlow() }
    val removedMessage = stringResource(R.string.favorites_removed_message)

    FavoritesScreen(
        state = state,
        messages = messages,
        onBack = onBack,
        onMovieClick = onMovieClick,
        onRemoveClick = { id ->
            val left = ((state.favorites as? SectionState.Content)?.data.orEmpty()).filterNot { it.id == id }
            state = state.copy(favorites = if (left.isEmpty()) SectionState.Empty else SectionState.Content(left))
            messageChannel.trySend(removedMessage)
        },
        onRetry = {},
        onBrowseClick = onBrowseClick,
    )
}
