package app.moviedb.core.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailDto(
    val id: Int,
    val title: String,
    val tagline: String? = null,
    val overview: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    val runtime: Int? = null,
    val genres: List<GenreDto> = emptyList(),
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    val videos: VideoListDto? = null,
)

@Serializable
data class VideoListDto(val results: List<VideoDto> = emptyList())

@Serializable
data class VideoDto(
    val key: String,
    val site: String = "",
    val type: String = "",
    val official: Boolean = false,
)

@Serializable
data class GenreDto(
    val id: Int,
    val name: String,
)
