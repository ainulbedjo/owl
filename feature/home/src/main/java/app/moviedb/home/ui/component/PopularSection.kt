package app.moviedb.home.ui.component

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.model.Movie
import app.moviedb.home.R
import app.moviedb.home.ui.PopularSkeleton
import app.moviedb.home.ui.SectionStateContent

@Composable
fun PopularSection(
    state: SectionState<List<Movie>>,
    onMovieClick: (Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val name = stringResource(R.string.section_popular)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(title = name, subtitle = stringResource(R.string.section_popular_subtitle))
        SectionStateContent(
            state = state,
            sectionName = name,
            onRetry = onRetry,
            loading = { PopularSkeleton(sectionName = name) },
        ) { movies ->
            val listState = rememberLazyListState()
            LazyRow(
                state = listState,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items = movies, key = { it.id }) { movie ->
                    PopularCard(movie = movie, onClick = { onMovieClick(movie.id) })
                }
            }
        }
    }
}
