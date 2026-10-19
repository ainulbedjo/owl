package app.moviedb.detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.moviedb.core.common.AppError
import app.moviedb.core.common.ImageSize
import app.moviedb.core.common.TmdbImage
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.model.MovieDetail
import app.moviedb.designsystem.component.ErrorCard
import app.moviedb.designsystem.component.OfflineSheet
import app.moviedb.designsystem.component.PosterImage
import app.moviedb.designsystem.component.pulse
import app.moviedb.detail.R
import app.moviedb.detail.ui.component.DetailToolbar
import app.moviedb.detail.ui.component.MovieInfo
import app.moviedb.detail.ui.component.MovieNotFound
import app.moviedb.detail.ui.component.MovieSkeleton
import app.moviedb.detail.ui.component.ReviewsSection
import app.moviedb.detail.ui.sheet.ShareAction
import app.moviedb.detail.ui.sheet.ShareSheet
import app.moviedb.detail.ui.sheet.copyLink
import app.moviedb.detail.ui.sheet.shareByEmail
import app.moviedb.detail.ui.sheet.shareByMessages
import app.moviedb.detail.ui.sheet.shareWithChooser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

private val HeaderHeight = 340.dp
private val PanelOverlap = 48.dp

/**
 * The Detail screen. It only draws [state] and reports what the viewer does, so previews and
 * tests can drive it with any state. [messages] carries one-shot snackbar texts from the ViewModel.
 */
@Composable
fun DetailScreen(
    state: DetailUiState,
    onBack: () -> Unit,
    onRetryMovie: () -> Unit,
    onRetryReviews: () -> Unit,
    onTryAgain: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    onDismissShare: () -> Unit,
    onDismissOfflineSheet: () -> Unit,
    onOpenFavorites: () -> Unit,
    modifier: Modifier = Modifier,
    messages: Flow<String> = emptyFlow(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var expandedIds by rememberSaveable { mutableStateOf(emptySet<String>()) }
    val movie = (state.movie as? SectionState.Content)?.data

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
        DetailBody(
            state = state,
            movie = movie,
            expandedIds = expandedIds,
            onToggleExpanded = { id ->
                expandedIds = if (id in expandedIds) expandedIds - id else expandedIds + id
            },
            onBack = onBack,
            onRetryMovie = onRetryMovie,
            onRetryReviews = onRetryReviews,
        )

        FilledIconButton(
            onClick = onBack,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 16.dp, top = 8.dp)
                .size(48.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.detail_back),
            )
        }

          DetailToolbar(
              isFavorite = state.isFavorite,
              enabled = movie != null,
              onFavoriteClick = onFavoriteClick,
              onShareClick = onShareClick,
              modifier = Modifier
                  .align(Alignment.BottomCenter)
                  .navigationBarsPadding()
                  .padding(bottom = 20.dp),
          )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, bottom = 96.dp),
        )
      }
    }

    if (state.shareSheetVisible && movie != null) {
        val linkCopied = stringResource(R.string.share_link_copied)
        val noApp = stringResource(R.string.share_no_app)
        ShareSheet(
            movie = movie,
            onDismiss = onDismissShare,
            onAction = { action ->
                val launched = when (action) {
                    ShareAction.Messages -> context.shareByMessages(movie)
                    ShareAction.Email -> context.shareByEmail(movie)
                    ShareAction.More -> context.shareWithChooser(movie)
                    ShareAction.CopyLink -> true.also { context.copyLink(movie) }
                }
                onDismissShare()
                scope.launch {
                    when {
                        !launched -> snackbarHostState.showSnackbar(noApp)
                        action == ShareAction.CopyLink -> snackbarHostState.showSnackbar(linkCopied)
                    }
                }
            },
        )
    }

    if (state.offlineSheetVisible) {
        OfflineSheet(
            onDismiss = onDismissOfflineSheet,
            onTryAgain = onTryAgain,
            onOpenFavorites = {
                onDismissOfflineSheet()
                onOpenFavorites()
            },
        )
    }
}

/** The scrolling part: backdrop, then a rounded panel that overlaps it. */
@Composable
private fun DetailBody(
    state: DetailUiState,
    movie: MovieDetail?,
    expandedIds: Set<String>,
    onToggleExpanded: (String) -> Unit,
    onBack: () -> Unit,
    onRetryMovie: () -> Unit,
    onRetryReviews: () -> Unit,
) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Backdrop(movie)

        Column(
            Modifier
                .overlapTop(PanelOverlap)
                .clip(RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = 28.dp, bottom = 140.dp + bottomInset),
        ) {
            when (val m = state.movie) {
                SectionState.Loading -> MovieSkeleton()
                is SectionState.Content -> MovieInfo(m.data)
                SectionState.Empty -> Unit
                is SectionState.Error -> if (m.error == AppError.NotFound) {
                    MovieNotFound(onBack = onBack)
                } else {
                    ErrorCard(
                        title = stringResource(R.string.detail_error_movie_title),
                        message = stringResource(m.error.messageRes()),
                        onRetry = onRetryMovie,
                    )
                }
            }

            ReviewsSection(
                state = state.reviews,
                expandedIds = expandedIds,
                onToggleExpanded = onToggleExpanded,
                onRetry = onRetryReviews,
            )
        }
    }
}

/** Decorative, so screen readers skip it. With no movie it is a plain placeholder. */
@Composable
private fun Backdrop(movie: MovieDetail?) {
    val modifier = Modifier
        .fillMaxWidth()
        .height(HeaderHeight)
        .clearAndSetSemantics { }

    if (movie != null) {
        // The backdrop, or the poster when there is none. With neither, the art shows.
        PosterImage(
            url = TmdbImage.url(movie.backdropPath ?: movie.posterPath, ImageSize.W780),
            movieId = movie.id,
            title = movie.title,
            letterSize = null,
            modifier = modifier,
        )
    } else {
        Box(modifier
            .pulse()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh))
    }
}

/** Draws this element [overlap] higher, over the one above, without leaving a gap below. */
private fun Modifier.overlapTop(overlap: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val shift = overlap.roundToPx()
    layout(placeable.width, (placeable.height - shift).coerceAtLeast(0)) {
        placeable.place(0, -shift)
    }
}
