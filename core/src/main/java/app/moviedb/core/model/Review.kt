package app.moviedb.core.model

import java.time.LocalDate

/**
 * A user review of a movie.
 *
 * [id] is a string such as `5b1c13b9c3a36848f2026384`, used as the list key.
 * [authorName] is the display name, or the username when the display name is blank.
 * [avatarUrl] is a full URL, or `null` when the author has no avatar.
 * [rating] is `null` when the author left none; the UI shows "No rating".
 */
data class Review(
    val id: String,
    val authorName: String,
    val avatarUrl: String?,
    val rating: Double?,
    val content: String,
    val createdAt: LocalDate?,
)
