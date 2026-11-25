package app.moviedb.genre.data

import app.moviedb.core.common.AppError
import app.moviedb.core.common.AppResult
import app.moviedb.core.data.network.dto.GenreDto
import app.moviedb.genre.data.network.GenreApi
import app.moviedb.genre.data.network.GenreListDto
import app.moviedb.genre.data.repository.GenreRepositoryImpl
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GenreRepositoryTest {
    private class FakeApi(private val results: ArrayDeque<() -> GenreListDto>) : GenreApi {
        var calls = 0
        override suspend fun getMovieGenres(): GenreListDto {
            calls++
            return results.removeFirst()()
        }
    }

    private val ok = { GenreListDto(listOf(GenreDto(28, "Action"))) }
    private val offline = { throw IOException("offline") }

    @Test
    fun loadsOnceThenServesFromMemory() = runTest {
        val api = FakeApi(ArrayDeque(listOf(ok)))
        val repo = GenreRepositoryImpl(api)

        repo.genres()
        val second = repo.genres()

        assertEquals(1, api.calls)
        assertEquals(1, (second as AppResult.Success).data.size)
    }

    @Test
    fun failureIsNotCachedAndTheNextCallRetries() = runTest {
        val api = FakeApi(ArrayDeque(listOf(offline, ok)))
        val repo = GenreRepositoryImpl(api)

        val first = repo.genres()
        val second = repo.genres()

        assertEquals(AppResult.Failure(AppError.Network), first)
        assertEquals(2, api.calls)
        assertEquals(28, (second as AppResult.Success).data.single().id)
    }
}
