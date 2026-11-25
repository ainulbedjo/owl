package app.moviedb.genre.data.network

import app.moviedb.core.data.network.dto.GenreDto
import kotlinx.serialization.Serializable

/** Body of `GET /3/genre/movie/list`. See docs/api/genre_list.md. */
@Serializable
data class GenreListDto(
    val genres: List<GenreDto> = emptyList(),
)
