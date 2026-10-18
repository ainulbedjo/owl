package app.moviedb.core.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TmdbImageTest {
    @Test
    fun buildsUrlFromPathAndSize() {
        assertEquals("https://image.tmdb.org/t/p/w500/a.jpg", TmdbImage.url("/a.jpg", ImageSize.W500))
    }

    @Test
    fun nullOrBlankPathGivesNull() {
        assertNull(TmdbImage.url(null, ImageSize.W185))
        assertNull(TmdbImage.url("  ", ImageSize.W185))
    }
}
