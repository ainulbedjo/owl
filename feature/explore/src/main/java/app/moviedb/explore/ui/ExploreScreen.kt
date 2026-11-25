package app.moviedb.explore.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.moviedb.core.common.ImageSize
import app.moviedb.core.common.TmdbImage
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.model.Movie
import app.moviedb.designsystem.component.BackHeader
import app.moviedb.designsystem.component.EmptyCard
import app.moviedb.designsystem.component.ErrorCard
import app.moviedb.designsystem.component.LoadingBadge
import app.moviedb.designsystem.component.OfflineSheet
import app.moviedb.designsystem.component.PosterImage
import app.moviedb.designsystem.component.RatingBadge
import app.moviedb.designsystem.theme.MoviedbTheme

private const val PREFETCH_ITEMS = 6

@Composable
fun ExploreScreen(
    state: ExploreUiState,
    onBack: () -> Unit,
    onMovieClick: (movieId: Int) -> Unit,
    onFavoritesClick: () -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onTryAgain: () -> Unit,
    onDismissOfflineSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()
    val movies = state.movies

    val nearEnd by remember {
        derivedStateOf {
            val info = gridState.layoutInfo
            val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0
            last >= info.totalItemsCount - PREFETCH_ITEMS
        }
    }

    val loadedCount = (movies as? SectionState.Content)?.data?.size ?: 0
    LaunchedEffect(nearEnd, loadedCount, state.append) {
        if (nearEnd && loadedCount > 0) onLoadMore()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(Modifier.padding(horizontal = 20.dp)) {
            BackHeader(
                title = state.title ?: "Explore",
                subtitle = if (state.totalResults > 0) "%,d movies".format(state.totalResults) else null,
                onBack = onBack,
            )

            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
            ) {
                when (movies) {
                    SectionState.Loading -> fullWidth("loading") { LoadingBadge() }
                    SectionState.Empty -> fullWidth("empty") { EmptyCard("No movies found.") }
                    is SectionState.Error -> fullWidth("error") {
                        ErrorCard(
                            title = "Could not load movies",
                            message = "Check your connection and try again.",
                            onRetry = onRetry,
                        )
                    }
                    is SectionState.Content -> {
                        items(movies.data, key = { it.id }) { movie ->
                            MovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
                        }
                        footer(state, onRetry)
                    }
                }
            }
        }
    }

    if (state.offlineSheetVisible) {
        OfflineSheet(
            onDismiss = onDismissOfflineSheet,
            onTryAgain = onTryAgain,
            onOpenFavorites = {
                onDismissOfflineSheet()
                onFavoritesClick()
            },
        )
    }
}

// Spinner for last movie
private fun LazyGridScope.footer(state: ExploreUiState, onRetry: () -> Unit) {
    when {
        state.append == AppendState.Loading -> fullWidth("append") { LoadingBadge() }
        state.append == AppendState.Error -> fullWidth("append") {
            ErrorCard(
                title = "Could not load more movies",
                message = "The movies above are still here.",
                onRetry = onRetry,
            )
        }
        !state.hasMore -> fullWidth("end") {
            EmptyCard("That is everything TMDB serves for this list.")
        }
    }
}

private fun LazyGridScope.fullWidth(key: String, content: @Composable () -> Unit) {
    item(key = key, span = { GridItemSpan(maxLineSpan) }) { content() }
}

@Composable
private fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.clickable(onClick = onClick)) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(20.dp)),
        ) {
            PosterImage(
                url = TmdbImage.url(movie.posterPath, ImageSize.W342),
                movieId = movie.id,
                title = movie.title,
                modifier = Modifier.fillMaxSize(),
            )
            RatingBadge(
                rating = movie.rating,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
            )
        }
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 10.dp),
        )
        movie.releaseDate?.let {
            Text(
                text = it.year.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }
}

private val sampleMovies = (1..8).map { Movie(it, "Midnight Orchard $it", "", null, null, 6.0 + it / 4.0) }

@Preview
@Composable
private fun ExplorePreview() {
    MoviedbTheme(paletteId = "violet") {
        ExploreScreen(
            state = ExploreUiState(
                title = "Mystery",
                movies = SectionState.Content(sampleMovies),
                hasMore = true,
                totalResults = 4812,
            ),
            onBack = {}, onMovieClick = {}, onFavoritesClick = {}, onRetry = {},
            onLoadMore = {}, onTryAgain = {}, onDismissOfflineSheet = {},
        )
    }
}

@Preview
@Composable
private fun ExploreLoadingPreview() {
    MoviedbTheme(paletteId = "violet") {
        ExploreScreen(
            state = ExploreUiState(),
            onBack = {}, onMovieClick = {}, onFavoritesClick = {}, onRetry = {},
            onLoadMore = {}, onTryAgain = {}, onDismissOfflineSheet = {},
        )
    }
}
