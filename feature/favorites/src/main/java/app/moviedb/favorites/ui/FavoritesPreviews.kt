package app.moviedb.favorites.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import app.moviedb.core.common.AppError
import app.moviedb.core.common.state.SectionState
import app.moviedb.designsystem.theme.MoviedbTheme
import app.moviedb.favorites.data.sampleFavorites
import app.moviedb.favorites.data.sampleFavoritesState

@Composable
private fun PreviewFavorites(state: FavoritesUiState) {
    MoviedbTheme(paletteId = "violet") {
        FavoritesScreen(
            state = state,
            onBack = {},
            onMovieClick = {},
            onRemoveClick = {},
            onRetry = {},
            onBrowseClick = {},
            skeletonDelayMillis = 0,
        )
    }
}

@PreviewLightDark
@Preview(name = "Content", showSystemUi = true)
@Composable
private fun FavoritesContentPreview() = PreviewFavorites(sampleFavoritesState)

@Preview(name = "Empty", showSystemUi = true)
@Composable
private fun FavoritesEmptyScreenPreview() =
    PreviewFavorites(FavoritesUiState(SectionState.Empty))

@Preview(name = "Loading", showSystemUi = true)
@Composable
private fun FavoritesLoadingPreview() = PreviewFavorites(FavoritesUiState())

@Preview(name = "Error", showSystemUi = true)
@Composable
private fun FavoritesErrorPreview() =
    PreviewFavorites(FavoritesUiState(SectionState.Error(AppError.Unexpected)))

@Preview(name = "One item", showSystemUi = true)
@Composable
private fun FavoritesOneItemPreview() =
    PreviewFavorites(FavoritesUiState(SectionState.Content(sampleFavorites.take(1))))
