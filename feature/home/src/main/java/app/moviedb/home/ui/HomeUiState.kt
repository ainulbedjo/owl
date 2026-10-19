package app.moviedb.home.ui

import app.moviedb.core.common.state.SectionState
import app.moviedb.core.model.Movie
import app.moviedb.core.model.ThemeSettings

enum class HomeSection { Popular, TopRated, NowPlaying }

data class HomeUiState(
    val popular: SectionState<List<Movie>> = SectionState.Loading,
    val topRated: SectionState<List<Movie>> = SectionState.Loading,
    val nowPlaying: SectionState<List<Movie>> = SectionState.Loading,
    val offlineSheetVisible: Boolean = false,
    val favoriteCount: Int = 0,
    val theme: ThemeSettings = ThemeSettings(),
)
