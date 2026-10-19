package app.moviedb.favorites.ui

import app.moviedb.core.common.state.SectionState
import app.moviedb.core.model.Movie

data class FavoritesUiState(
    val favorites: SectionState<List<Movie>> = SectionState.Loading,
)
