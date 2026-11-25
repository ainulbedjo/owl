package app.moviedb.explore.ui

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ExploreRoute(
    val genreId: Int? = null,
    val genreName: String? = null,
)

fun NavGraphBuilder.exploreScreen(
    onBack: () -> Unit,
    onMovieClick: (movieId: Int) -> Unit,
    onFavoritesClick: () -> Unit,
) {
    composable<ExploreRoute> {
        val viewModel: ExploreViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        ExploreScreen(
            state = state,
            onBack = onBack,
            onMovieClick = onMovieClick,
            onFavoritesClick = onFavoritesClick,
            onRetry = viewModel::onRetry,
            onLoadMore = viewModel::onLoadMore,
            onTryAgain = viewModel::onTryAgain,
            onDismissOfflineSheet = viewModel::onDismissOfflineSheet,
        )
    }
}
