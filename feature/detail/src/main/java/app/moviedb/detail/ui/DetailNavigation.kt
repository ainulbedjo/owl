package app.moviedb.detail.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.moviedb.detail.R
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

@Serializable
data class DetailRoute(val movieId: Int)

fun NavGraphBuilder.detailScreen(
    onBack: () -> Unit,
    onFavoritesClick: () -> Unit,
) {
    composable<DetailRoute> {
        DetailEntry(onBack = onBack, onFavoritesClick = onFavoritesClick)
    }
}

@Composable
private fun DetailEntry(
    onBack: () -> Unit,
    onFavoritesClick: () -> Unit,
) {
    val viewModel: DetailViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    // The ViewModel sends message kinds, and the screen turns them into text.
    val savedText = stringResource(R.string.detail_saved_message)
    val removedText = stringResource(R.string.detail_removed_message)
    val messages = remember(viewModel, savedText, removedText) {
        viewModel.messages.map { message ->
            when (message) {
                DetailMessage.FavoriteAdded -> savedText
                DetailMessage.FavoriteRemoved -> removedText
            }
        }
    }

    DetailScreen(
        state = state,
        messages = messages,
        onBack = onBack,
        onRetryMovie = viewModel::onRetryMovie,
        onRetryReviews = viewModel::onRetryReviews,
        onTryAgain = viewModel::onTryAgain,
        onFavoriteClick = viewModel::onFavoriteClick,
        onShareClick = viewModel::onShareClick,
        onDismissShare = viewModel::onDismissShare,
        onDismissOfflineSheet = viewModel::onDismissOfflineSheet,
        onOpenFavorites = onFavoritesClick,
    )
}
