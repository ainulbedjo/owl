package app.moviedb.detail.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import app.moviedb.detail.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.Serializable

/** Only the id travels. Detail loads everything else itself, so it also works after process death. */
@Serializable
data class DetailRoute(val movieId: Int)

/** Registers Detail in the app's `NavHost`. Navigation goes out through the callbacks. */
fun NavGraphBuilder.detailScreen(
    onBack: () -> Unit,
    onFavoritesClick: () -> Unit,
) {
    composable<DetailRoute> { entry ->
        DetailEntry(
            movieId = entry.toRoute<DetailRoute>().movieId,
            onBack = onBack,
            onFavoritesClick = onFavoritesClick,
        )
    }
}

// TODO(data): replace this local sample state with DetailViewModel (see docs/todo/data-fetch.md).
// Until then the buttons change in-memory state only, so the UI can be tried by hand.
@Composable
private fun DetailEntry(
    movieId: Int,
    onBack: () -> Unit,
    onFavoritesClick: () -> Unit,
) {
    var state by remember(movieId) { mutableStateOf(sampleDetailState(movieId)) }
    val messageChannel = remember { Channel<String>(Channel.BUFFERED) }
    val messages = remember { messageChannel.receiveAsFlow() }
    val savedMessage = stringResource(R.string.detail_saved_message)
    val removedMessage = stringResource(R.string.detail_removed_message)

    DetailScreen(
        state = state,
        messages = messages,
        onBack = onBack,
        onRetryMovie = {},
        onRetryReviews = {},
        onTryAgain = {},
        onFavoriteClick = {
            val nowFavorite = !state.isFavorite
            state = state.copy(isFavorite = nowFavorite)
            messageChannel.trySend(if (nowFavorite) savedMessage else removedMessage)
        },
        onShareClick = { state = state.copy(shareSheetVisible = true) },
        onDismissShare = { state = state.copy(shareSheetVisible = false) },
        onDismissOfflineSheet = { state = state.copy(offlineSheetVisible = false) },
        onOpenFavorites = onFavoritesClick,
    )
}
