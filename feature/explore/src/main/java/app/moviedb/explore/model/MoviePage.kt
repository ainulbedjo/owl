package app.moviedb.explore.model

import app.moviedb.core.model.Movie

data class MoviePage(
    val items: List<Movie>,
    val page: Int,
    val totalPages: Int,
    val totalResults: Int,
) {
    val hasMore: Boolean get() = page < minOf(totalPages, MAX_PAGES)

    companion object {
        const val MAX_PAGES = 500
    }
}
