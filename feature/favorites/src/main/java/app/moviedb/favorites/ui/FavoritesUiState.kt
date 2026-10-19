package app.moviedb.favorites.ui

import app.moviedb.core.common.state.SectionState
import app.moviedb.core.model.Movie

/** Favorites has one list. It is read from local storage, so there is no network error here. */
data class FavoritesUiState(
    val favorites: SectionState<List<Movie>> = SectionState.Loading,
)
