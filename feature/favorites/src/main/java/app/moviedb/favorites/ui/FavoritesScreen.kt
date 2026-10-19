package app.moviedb.favorites.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.model.Movie
import app.moviedb.designsystem.component.ErrorCard
import app.moviedb.favorites.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * The Favorites screen. It only draws [state] and reports what the viewer does.
 * [messages] carries one-shot snackbar texts from the ViewModel.
 */
@Composable
fun FavoritesScreen(
    state: FavoritesUiState,
    onBack: () -> Unit,
    onMovieClick: (movieId: Int) -> Unit,
    onRemoveClick: (movieId: Int) -> Unit,
    onRetry: () -> Unit,
    onBrowseClick: () -> Unit,
    modifier: Modifier = Modifier,
    messages: Flow<String> = emptyFlow(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(messages) {
        messages.collect { snackbarHostState.showSnackbar(it) }
    }

    // A Surface (not a plain Box) so text inside gets the theme's text color.
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Box(Modifier.fillMaxSize()) {
            val favorites = state.favorites
            LazyColumn(
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
            ) {
                item(key = "header") {
                    FavoritesHeader(
                        count = (favorites as? SectionState.Content)?.data?.size,
                        onBack = onBack,
                    )
                }
                when (favorites) {
                    SectionState.Loading -> item(key = "loading") { FavoritesSkeleton() }
                    SectionState.Empty -> item(key = "empty") {
                        FavoritesEmpty(
                            onBrowseClick = onBrowseClick,
                            modifier = Modifier.padding(top = 72.dp),
                        )
                    }
                    is SectionState.Error -> item(key = "error") {
                        ErrorCard(
                            title = stringResource(R.string.favorites_error_title),
                            message = stringResource(R.string.favorites_error_message),
                            onRetry = onRetry,
                        )
                    }
                    is SectionState.Content -> items(items = favorites.data, key = { it.id }) { movie ->
                        FavoriteRow(
                            movie = movie,
                            onClick = { onMovieClick(movie.id) },
                            onRemoveClick = { onRemoveClick(movie.id) },
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .animateItem(),
                        )
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            )
        }
    }
}

/** A round back button, the title, and the count. The count is announced when it changes. */
@Composable
private fun FavoritesHeader(
    count: Int?,
    onBack: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 12.dp),
    ) {
        FilledIconButton(
            onClick = onBack,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.favorites_back),
            )
        }
        Column {
            Text(
                text = stringResource(R.string.favorites_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.semantics { heading() },
            )
            if (count != null && count > 0) {
                Text(
                    text = stringResource(R.string.favorites_count, count),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
            }
        }
    }
}
