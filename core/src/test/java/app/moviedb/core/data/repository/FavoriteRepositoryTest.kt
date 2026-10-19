package app.moviedb.core.data.repository

import app.moviedb.core.data.local.FavoriteDao
import app.moviedb.core.data.local.FavoriteMovieEntity
import app.moviedb.core.model.MovieDetail
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * A [FavoriteDao] that keeps rows in memory. It extends the real class, so the real
 * `toggle` logic runs; only the queries are replaced. Sorting is done by the real SQL,
 * which the instrumented `FavoriteDaoTest` covers.
 */
private class FakeFavoriteDao : FavoriteDao() {
    private val rows = MutableStateFlow<List<FavoriteMovieEntity>>(emptyList())

    override fun observeAll(): Flow<List<FavoriteMovieEntity>> =
        rows.map { list -> list.sortedByDescending { it.addedAt } }

    override fun observeCount(): Flow<Int> = rows.map { it.size }

    override fun observeIsFavorite(id: Int): Flow<Boolean> = rows.map { list -> list.any { it.id == id } }

    override suspend fun isFavorite(id: Int): Boolean = rows.value.any { it.id == id }

    override suspend fun insert(entity: FavoriteMovieEntity) {
        rows.value = rows.value.filterNot { it.id == entity.id } + entity
    }

    override suspend fun delete(id: Int) {
        rows.value = rows.value.filterNot { it.id == id }
    }
}

private fun detail(
    id: Int,
    title: String = "Movie $id",
    posterPath: String? = "/p$id.jpg",
    releaseDate: LocalDate? = LocalDate.of(2026, 10, 10),
    overview: String = "Overview $id",
) = MovieDetail(
    id = id, title = title, tagline = null, overview = overview, posterPath = posterPath,
    backdropPath = null, releaseDate = releaseDate, runtimeMinutes = null,
    genres = emptyList(), rating = 8.4,
)

class FavoriteRepositoryTest {

    private var clock = 1_000L
    private val repository = FavoriteRepositoryImpl(FakeFavoriteDao()) { clock }

    @Test
    fun toggleSavesAMovieThatIsNotAFavorite() = runTest {
        val nowFavorite = repository.toggle(detail(1))

        assertTrue(nowFavorite)
        assertTrue(repository.observeIsFavorite(1).first())
        assertEquals(1, repository.observeCount().first())
    }

    @Test
    fun toggleRemovesAMovieThatIsAlreadyAFavorite() = runTest {
        repository.toggle(detail(1))

        val nowFavorite = repository.toggle(detail(1))

        assertFalse(nowFavorite)
        assertFalse(repository.observeIsFavorite(1).first())
        assertEquals(0, repository.observeCount().first())
    }

    @Test
    fun removeDeletesTheMovie() = runTest {
        repository.toggle(detail(1))

        repository.remove(1)

        assertEquals(emptyList<Any>(), repository.observeAll().first())
    }

    @Test
    fun removingAMovieThatIsNotSavedDoesNothing() = runTest {
        repository.toggle(detail(1))

        repository.remove(99)

        assertEquals(1, repository.observeCount().first())
    }

    @Test
    fun snapshotKeepsWhatWasSavedAsAMovie() = runTest {
        repository.toggle(detail(7, title = "Quiet Harbor", overview = "A fishing family."))

        val movie = repository.observeAll().first().single()

        assertEquals(7, movie.id)
        assertEquals("Quiet Harbor", movie.title)
        assertEquals("A fishing family.", movie.overview)
        assertEquals("/p7.jpg", movie.posterPath)
        assertEquals(LocalDate.of(2026, 10, 10), movie.releaseDate)
        assertEquals(8.4, movie.rating, 0.0)
    }

    @Test
    fun missingPosterAndDateStayNull() = runTest {
        repository.toggle(detail(2, posterPath = null, releaseDate = null))

        val movie = repository.observeAll().first().single()

        assertNull(movie.posterPath)
        assertNull(movie.releaseDate)
    }

    @Test
    fun newestSavedComesFirst() = runTest {
        clock = 1_000
        repository.toggle(detail(1))
        clock = 2_000
        repository.toggle(detail(2))
        clock = 3_000
        repository.toggle(detail(3))

        assertEquals(listOf(3, 2, 1), repository.observeAll().first().map { it.id })
    }
}
