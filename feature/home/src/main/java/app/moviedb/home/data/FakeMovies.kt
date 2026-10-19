package app.moviedb.home.data

import app.moviedb.core.common.state.SectionState
import app.moviedb.core.model.Movie
import app.moviedb.home.ui.HomeUiState
import java.time.LocalDate

// Sample data for previews.
internal val fakeMovies = listOf(
    fake(1, "Midnight Orchard", LocalDate.of(2026, 10, 10), 8.4),
    fake(2, "The Last Lighthouse", LocalDate.of(2026, 9, 26), 7.9),
    fake(3, "A Movie With a Very Long Title That Must Be Cut Short", LocalDate.of(2026, 8, 14), 7.2),
    fake(4, "Salt & Static", LocalDate.of(2026, 7, 3), 8.1),
    fake(5, "Velvet Run", LocalDate.of(2026, 10, 2), 7.6),
    fake(6, "Quiet Harbor", LocalDate.of(2026, 6, 20), 8.8),
    fake(7, "Neon Lullaby", LocalDate.of(2026, 10, 15), 7.0),
    fake(8, "Atlas of Rain", LocalDate.of(2026, 5, 29), 8.3),
)

private fun fake(id: Int, title: String, date: LocalDate, rating: Double) = Movie(
    id = id,
    title = title,
    overview = "Overview of $title.",
    posterPath = null,
    releaseDate = date,
    rating = rating,
)

internal val sampleHomeState = HomeUiState(
    popular = SectionState.Content(fakeMovies),
    topRated = SectionState.Content(fakeMovies.sortedByDescending { it.rating }),
    nowPlaying = SectionState.Content(fakeMovies.reversed()),
    favoriteCount = 3,
)
