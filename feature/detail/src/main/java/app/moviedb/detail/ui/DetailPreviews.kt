package app.moviedb.detail.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import app.moviedb.core.common.AppError
import app.moviedb.core.common.state.SectionState
import app.moviedb.designsystem.theme.MoviedbTheme

@Composable
private fun PreviewDetail(state: DetailUiState, dark: Boolean = false) {
    MoviedbTheme(paletteId = "violet", darkTheme = dark) {
        DetailScreen(
            state = state,
            onBack = {},
            onRetryMovie = {},
            onRetryReviews = {},
            onTryAgain = {},
            onFavoriteClick = {},
            onShareClick = {},
            onDismissShare = {},
            onDismissOfflineSheet = {},
            onOpenFavorites = {},
        )
    }
}

private val content = sampleDetailState(movieId = 1)

@PreviewLightDark
@Preview(name = "Content", showSystemUi = true)
@Composable
private fun DetailContentPreview() = PreviewDetail(content)

@Preview(name = "Loading", showSystemUi = true)
@Composable
private fun DetailLoadingPreview() = PreviewDetail(DetailUiState())

@Preview(name = "Movie error", showSystemUi = true)
@Composable
private fun DetailMovieErrorPreview() =
    PreviewDetail(content.copy(movie = SectionState.Error(AppError.Server)))

@Preview(name = "Not found", showSystemUi = true)
@Composable
private fun DetailNotFoundPreview() =
    PreviewDetail(content.copy(movie = SectionState.Error(AppError.NotFound)))

@Preview(name = "Reviews error", showSystemUi = true)
@Composable
private fun DetailReviewsErrorPreview() =
    PreviewDetail(content.copy(reviews = SectionState.Error(AppError.Server)))

@Preview(name = "No reviews", showSystemUi = true)
@Composable
private fun DetailNoReviewsPreview() = PreviewDetail(content.copy(reviews = SectionState.Empty))

@Preview(name = "Favorite", showSystemUi = true)
@Composable
private fun DetailFavoritePreview() = PreviewDetail(content.copy(isFavorite = true))

@Preview(name = "Share sheet", showSystemUi = true)
@Composable
private fun DetailShareSheetPreview() = PreviewDetail(content.copy(shareSheetVisible = true))

@Preview(name = "No connection", showSystemUi = true)
@Composable
private fun DetailOfflinePreview() = PreviewDetail(
    DetailUiState(
        movie = SectionState.Error(AppError.Network),
        reviews = SectionState.Error(AppError.Network),
        offlineSheetVisible = true,
    ),
)
