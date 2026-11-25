package app.moviedb.genre.data

import app.moviedb.core.data.network.dto.GenreDto
import app.moviedb.genre.data.mapper.toGenres
import app.moviedb.genre.data.network.GenreListDto
import app.moviedb.genre.model.Genre
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GenreMapperTest {
    @Test
    fun mapsNormalItem() {
        val genres = GenreListDto(listOf(GenreDto(28, "Action"))).toGenres()

        assertEquals(listOf(Genre(28, "Action")), genres)
    }

    @Test
    fun trimsTheName() {
        assertEquals("Drama", GenreListDto(listOf(GenreDto(18, "  Drama "))).toGenres().single().name)
    }

    @Test
    fun dropsBlankNameAndKeepsTheRest() {
        val genres = GenreListDto(listOf(GenreDto(1, "  "), GenreDto(2, "War"))).toGenres()

        assertEquals(listOf(Genre(2, "War")), genres)
    }

    @Test
    fun emptyGenresGivesEmptyList() {
        assertTrue(GenreListDto().toGenres().isEmpty())
    }

    @Test
    fun parsesTheReferenceBody() {
        val body = """{"genres":[{"id":28,"name":"Action"},{"id":878,"name":"Science Fiction"}],"extra":1}"""
        val dto = Json { ignoreUnknownKeys = true }.decodeFromString<GenreListDto>(body)

        assertEquals(listOf(28, 878), dto.toGenres().map { it.id })
    }
}
