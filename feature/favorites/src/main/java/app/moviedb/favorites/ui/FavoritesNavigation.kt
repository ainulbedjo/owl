package app.moviedb.favorites.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.moviedb.favorites.R
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

/** The Favorites destination. It has no arguments. */
@Serializable
data object FavoritesRoute

/** Registers Favorites in the app's `NavHost`. Navigation goes out through the callbacks. */
fun NavGraphBuilder.favoritesScreen(
    onBack: () -> Unit,
    onMovieClick: (movieId: Int) -> Unit,
    onBrowseClick: () -> Unit,
) {
    composable<FavoritesRoute> {
        FavoritesEntry(onBack = onBack, onMovieClick = onMovieClick, onBrowseClick = onBrowseClick)
    }
}

@Composable
private fun FavoritesEntry(
    onBack: () -> Unit,
    onMovieClick: (movieId: Int) -> Unit,
    onBrowseClick: () -> Unit,
) {
    val viewModel: FavoritesViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    // The ViewModel sends message kinds, and the screen turns them into text.
    val removedText = stringResource(R.string.favorites_removed_message)
    val messages = remember(viewModel, removedText) {
        viewModel.messages.map { message ->
            when (message) {
                FavoritesMessage.Removed -> removedText
            }
        }
    }

    FavoritesScreen(
        state = state,
        messages = messages,
        onBack = onBack,
        onMovieClick = onMovieClick,
        onRemoveClick = viewModel::onRemoveClick,
        onRetry = viewModel::onRetry,
        onBrowseClick = onBrowseClick,
    )
}
