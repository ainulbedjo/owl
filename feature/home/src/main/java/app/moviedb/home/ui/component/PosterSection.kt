package app.moviedb.home.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.model.Movie
import app.moviedb.designsystem.theme.MoviedbTheme
import app.moviedb.home.data.fakeMovies
import app.moviedb.home.ui.PosterSkeleton
import app.moviedb.home.ui.SectionStateContent

@Composable
fun PosterSection(
    title: String,
    subtitle: String,
    state: SectionState<List<Movie>>,
    onMovieClick: (Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    showRating: Boolean = false,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(title = title, subtitle = subtitle)
        SectionStateContent(
            state = state,
            sectionName = title,
            onRetry = onRetry,
            loading = { PosterSkeleton(sectionName = title) },
        ) { movies ->
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items = movies, key = { it.id }) { movie ->
                    PosterCard(
                        movie = movie,
                        onClick = { onMovieClick(movie.id) },
                        showRating = showRating,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PosterSectionPreview() {
    MoviedbTheme(paletteId = "violet") {
        PosterSection(
            title = "Popular",
            subtitle = "Popular movie today",
            state = SectionState.Content(fakeMovies),
            onMovieClick = {},
            onRetry = {},
            showRating = true,
        )
    }
}
