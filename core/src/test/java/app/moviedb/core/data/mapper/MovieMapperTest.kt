package app.moviedb.core.data.mapper

import app.moviedb.core.data.network.dto.GenreDto
import app.moviedb.core.data.network.dto.MovieDetailDto
import app.moviedb.core.data.network.dto.MovieDto
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MovieMapperTest {
    @Test
    fun keepsTheBackdropPathOfAListItem() {
        val withBackdrop = MovieDto(id = 1, title = "T", backdropPath = "/b.jpg").toMovie()
        val without = MovieDto(id = 2, title = "T").toMovie()

        assertEquals("/b.jpg", withBackdrop.backdropPath)
        assertNull(without.backdropPath)
    }

    @Test
    fun mapsNormalListItem() {
        val movie = MovieDto(
            id = 1, title = "Midnight Orchard", overview = "Story", posterPath = "/p.jpg",
            releaseDate = "2026-10-10", voteAverage = 8.4,
        ).toMovie()

        assertEquals(1, movie.id)
        assertEquals("Story", movie.overview)
        assertEquals("/p.jpg", movie.posterPath)
        assertEquals(LocalDate.of(2026, 10, 10), movie.releaseDate)
        assertEquals(8.4, movie.rating, 0.0)
    }

    @Test
    fun nullPosterEmptyDateAndNullOverview() {
        val movie = MovieDto(id = 2, title = "T", overview = null, posterPath = null, releaseDate = "").toMovie()

        assertNull(movie.posterPath)
        assertNull(movie.releaseDate)
        assertEquals("", movie.overview)
    }

    @Test
    fun invalidDateGivesNull() {
        assertNull(MovieDto(id = 3, title = "T", releaseDate = "soon").toMovie().releaseDate)
    }

    @Test
    fun detailBlankTaglineAndZeroRuntimeBecomeNull() {
        val detail = MovieDetailDto(id = 4, title = "T", tagline = "  ", runtime = 0).toMovieDetail()

        assertNull(detail.tagline)
        assertNull(detail.runtimeMinutes)
    }

    @Test
    fun detailKeepsGenreNamesAndRuntime() {
        val detail = MovieDetailDto(
            id = 5, title = "T", tagline = "Hi", runtime = 128, backdropPath = "/b.jpg",
            genres = listOf(GenreDto(9648, "Mystery"), GenreDto(18, "Drama")),
        ).toMovieDetail()

        assertEquals(listOf("Mystery", "Drama"), detail.genres)
        assertEquals(128, detail.runtimeMinutes)
        assertEquals("Hi", detail.tagline)
        assertEquals("/b.jpg", detail.backdropPath)
    }
}
