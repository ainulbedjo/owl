package app.moviedb.favorites.data

import app.moviedb.core.common.state.SectionState
import app.moviedb.core.model.Movie
import app.moviedb.favorites.ui.FavoritesUiState
import java.time.LocalDate

// Sample data for previews and the UI-only build. Delete when FavoriteRepository exists.
val sampleFavorites = listOf(
    Movie(
        id = 2,
        title = "The Last Lighthouse",
        overview = "A retired keeper and a stranded cartographer race a coming storm to relight " +
            "the last beacon on a coast the maps forgot, and learn why it went dark.",
        posterPath = null,
        releaseDate = LocalDate.of(2026, 9, 26),
        rating = 7.9,
    ),
    Movie(
        id = 5,
        title = "Velvet Run",
        overview = "A getaway driver with one last job learns the vault is empty and the crew " +
            "was hired to be caught.",
        posterPath = null,
        releaseDate = LocalDate.of(2026, 10, 2),
        rating = 7.6,
    ),
    Movie(
        id = 8,
        title = "A Movie With a Very Long Title That Needs Two Lines and Then Some More",
        overview = "",
        posterPath = null,
        releaseDate = null,
        rating = 8.3,
    ),
)

val sampleFavoritesState = FavoritesUiState(
    SectionState.Content(sampleFavorites)
)
