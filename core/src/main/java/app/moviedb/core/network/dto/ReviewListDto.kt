package app.moviedb.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewListDto(
    val id: Int = 0,
    val page: Int = 1,
    val results: List<ReviewDto> = emptyList(),
    @SerialName("total_pages") val totalPages: Int = 1,
    @SerialName("total_results") val totalResults: Int = 0,
)

@Serializable
data class ReviewDto(
    val id: String,
    val author: String,
    @SerialName("author_details") val authorDetails: AuthorDetailsDto? = null,
    val content: String,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class AuthorDetailsDto(
    val name: String? = null,
    val username: String? = null,
    @SerialName("avatar_path") val avatarPath: String? = null,
    val rating: Double? = null,
)
