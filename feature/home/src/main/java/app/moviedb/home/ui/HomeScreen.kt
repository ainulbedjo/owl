package app.moviedb.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.moviedb.core.model.ThemeSettings
import app.moviedb.designsystem.component.OfflineSheet
import app.moviedb.home.R
import app.moviedb.home.ui.component.HomeTopBar
import app.moviedb.home.ui.sheet.AppearanceSheet

/**
 * The Home screen. It only draws [state] and reports what the viewer does,
 * so previews and tests can drive it with any state. Navigation is up to the caller.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onMovieClick: (movieId: Int) -> Unit,
    onFavoritesClick: () -> Unit,
    onRetry: (HomeSection) -> Unit,
    onTryAgain: () -> Unit,
    onDismissOfflineSheet: () -> Unit,
    onThemeChange: (ThemeSettings) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var appearanceSheetVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopBar(
                favoriteCount = state.favoriteCount,
                onThemeClick = { appearanceSheetVisible = true },
                onFavoritesClick = onFavoritesClick,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        val direction = LocalLayoutDirection.current
        LazyColumn(
            contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(direction),
                top = innerPadding.calculateTopPadding() + 8.dp,
                end = innerPadding.calculateEndPadding(direction),
                bottom = innerPadding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                PopularSection(
                    state = state.popular,
                    onMovieClick = onMovieClick,
                    onRetry = { onRetry(HomeSection.Popular) },
                )
            }
            item {
                PosterSection(
                    title = stringResource(R.string.section_top_rated),
                    subtitle = stringResource(R.string.section_top_rated_subtitle),
                    state = state.topRated,
                    onMovieClick = onMovieClick,
                    onRetry = { onRetry(HomeSection.TopRated) },
                    showRating = true,
                )
            }
            item {
                PosterSection(
                    title = stringResource(R.string.section_now_playing),
                    subtitle = stringResource(R.string.section_now_playing_subtitle),
                    state = state.nowPlaying,
                    onMovieClick = onMovieClick,
                    onRetry = { onRetry(HomeSection.NowPlaying) },
                )
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

    if (appearanceSheetVisible) {
        AppearanceSheet(
            settings = state.theme,
            onSettingsChange = onThemeChange,
            onDismiss = { appearanceSheetVisible = false },
        )
    }
}
