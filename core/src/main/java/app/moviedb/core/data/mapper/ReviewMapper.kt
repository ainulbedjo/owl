package app.moviedb.core.data.mapper

import app.moviedb.core.common.ImageSize
import app.moviedb.core.common.TmdbImage
import app.moviedb.core.model.Review
import app.moviedb.core.data.network.dto.ReviewDto
import java.time.LocalDate
import java.time.OffsetDateTime

fun ReviewDto.toReview(): Review = Review(
    id = id,
    authorName = authorDetails?.name?.takeIf { it.isNotBlank() } ?: author,
    avatarUrl = authorDetails?.avatarPath.toAvatarUrl(),
    rating = authorDetails?.rating,
    content = content.trim(),
    createdAt = createdAt.toDateOrNull(),
)

/** `avatar_path` is a TMDB path, a Gravatar URL with a stray leading slash, or blank. */
internal fun String?.toAvatarUrl(): String? = when {
    isNullOrBlank() -> null
    startsWith("/http") -> substring(1)
    else -> TmdbImage.url(this, ImageSize.W154)
}

/** Parses an ISO-8601 timestamp such as `2018-06-09T17:51:53.359Z` and keeps the date. */
private fun String?.toDateOrNull(): LocalDate? =
    if (isNullOrBlank()) null else runCatching { OffsetDateTime.parse(this).toLocalDate() }.getOrNull()
