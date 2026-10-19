package app.moviedb.detail.data

import app.moviedb.core.common.state.SectionState
import app.moviedb.core.model.MovieDetail
import app.moviedb.core.model.Review
import java.time.LocalDate

// Sample data for previews and the UI-only build. Delete when MovieRepository exists.
internal val sampleMovie = MovieDetail(
    id = 1,
    title = "Midnight Orchard",
    tagline = "Every harvest keeps a secret.",
    overview = "When a night shift at a failing apple farm uncovers a locked cellar, three siblings " +
        "must decide whether the family land is worth the truth buried beneath it.",
    posterPath = null,
    backdropPath = null,
    releaseDate = LocalDate.of(2026, 10, 10),
    runtimeMinutes = 128,
    genres = listOf("Mystery", "Drama"),
    rating = 8.4,
)

internal val sampleReviews = listOf(
    Review(
        id = "r1",
        authorName = "Mara Quill",
        avatarUrl = null,
        rating = 9.0,
        content = "A patient, beautifully shot film that trusts the audience. The second act drags a " +
            "little, but the last twenty minutes pay off everything it quietly sets up. I am still " +
            "thinking about that final shot a day later.",
        createdAt = LocalDate.of(2026, 10, 12),
    ),
    Review(
        id = "r2",
        authorName = "devon_reyes",
        avatarUrl = null,
        rating = null,
        content = "Strong cast and a score that carries more than the script does.",
        createdAt = LocalDate.of(2026, 10, 8),
    ),
    Review(
        id = "r3",
        authorName = "Ioana B.",
        avatarUrl = null,
        rating = 7.5,
        content = "Not perfect, but it has a point of view, which is rare right now. The production " +
            "design alone deserves an award. Go in without reading too much about it and let it " +
            "surprise you.",
        createdAt = LocalDate.of(2026, 9, 30),
    ),
)

internal fun sampleDetailState(movieId: Int = 1) =
    _root_ide_package_.app.moviedb.detail.ui.DetailUiState(
        movie = SectionState.Content(sampleMovie.copy(id = movieId)),
        reviews = SectionState.Content(sampleReviews),
    )
