package app.moviedb.home.ui

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

/** A section with a strip of wide cards: Now Playing. */
@Composable
fun WideSection(
    title: String,
    subtitle: String,
    state: SectionState<List<Movie>>,
    onMovieClick: (Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(title = title, subtitle = subtitle)
        SectionStateContent(
            state = state,
            sectionName = title,
            onRetry = onRetry,
            loading = { WideSkeleton(sectionName = title) },
        ) { movies ->
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items = movies, key = { it.id }) { movie ->
                    PosterWideCard(movie = movie, onClick = { onMovieClick(movie.id) })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WideSectionPreview() {
    MoviedbTheme(paletteId = "violet") {
        WideSection(
            title = "Now Playing",
            subtitle = "In theaters right now",
            state = SectionState.Content(fakeMovies),
            onMovieClick = {},
            onRetry = {},
        )
    }
}
