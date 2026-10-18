package app.moviedb.core.data.mapper

import app.moviedb.core.data.network.dto.AuthorDetailsDto
import app.moviedb.core.data.network.dto.ReviewDto
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReviewMapperTest {
    private fun dto(
        details: AuthorDetailsDto? = AuthorDetailsDto(),
        createdAt: String? = "2026-10-12T17:51:53.359Z",
        content: String = "  text \n",
    ) = ReviewDto(id = "r1", author = "mara", authorDetails = details, content = content, createdAt = createdAt)

    @Test
    fun blankNameFallsBackToAuthor() {
        assertEquals("mara", dto(AuthorDetailsDto(name = "")).toReview().authorName)
        assertEquals("mara", dto(details = null).toReview().authorName)
    }

    @Test
    fun nameWinsWhenPresent() {
        assertEquals("Mara Quill", dto(AuthorDetailsDto(name = "Mara Quill")).toReview().authorName)
    }

    @Test
    fun ratingNullAndPresent() {
        assertNull(dto(AuthorDetailsDto(rating = null)).toReview().rating)
        assertEquals(9.0, dto(AuthorDetailsDto(rating = 9.0)).toReview().rating!!, 0.0)
    }

    @Test
    fun avatarForms() {
        assertNull(dto(AuthorDetailsDto(avatarPath = null)).toReview().avatarUrl)
        assertNull(dto(AuthorDetailsDto(avatarPath = " ")).toReview().avatarUrl)
        assertEquals(
            "https://image.tmdb.org/t/p/w154/abc123.jpg",
            dto(AuthorDetailsDto(avatarPath = "/abc123.jpg")).toReview().avatarUrl,
        )
        assertEquals(
            "https://www.gravatar.com/avatar/x",
            dto(AuthorDetailsDto(avatarPath = "/https://www.gravatar.com/avatar/x")).toReview().avatarUrl,
        )
    }

    @Test
    fun parsesDateAndTrimsContent() {
        val review = dto().toReview()

        assertEquals(LocalDate.of(2026, 10, 12), review.createdAt)
        assertEquals("text", review.content)
    }

    @Test
    fun invalidOrMissingDateGivesNull() {
        assertNull(dto(createdAt = "yesterday").toReview().createdAt)
        assertNull(dto(createdAt = null).toReview().createdAt)
    }
}
