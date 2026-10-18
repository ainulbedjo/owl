package app.moviedb.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Envelope shared by popular, top_rated and now_playing. `dates` of now_playing is ignored. */
@Serializable
data class MovieListDto(
    val page: Int = 1,
    val results: List<MovieDto> = emptyList(),
    @SerialName("total_pages") val totalPages: Int = 1,
    @SerialName("total_results") val totalResults: Int = 0,
)

@Serializable
data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
)
