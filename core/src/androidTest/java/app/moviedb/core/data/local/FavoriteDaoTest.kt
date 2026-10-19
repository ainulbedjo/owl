package app.moviedb.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Runs against a real SQLite database in memory, so it checks the actual SQL. */
@RunWith(AndroidJUnit4::class)
class FavoriteDaoTest {

    private lateinit var database: MovieDatabase
    private lateinit var dao: FavoriteDao

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MovieDatabase::class.java).build()
        dao = database.favoriteDao()
    }

    @After
    fun closeDatabase() = database.close()

    private fun entity(id: Int, addedAt: Long, title: String = "Movie $id") = FavoriteMovieEntity(
        id = id, title = title, posterPath = null, releaseDate = null,
        overview = "", voteAverage = 7.0, addedAt = addedAt,
    )

    @Test
    fun listIsOrderedNewestFirst() = runTest {
        dao.insert(entity(1, addedAt = 100))
        dao.insert(entity(2, addedAt = 300))
        dao.insert(entity(3, addedAt = 200))

        assertEquals(listOf(2, 3, 1), dao.observeAll().first().map { it.id })
    }

    @Test
    fun insertingTheSameMovieTwiceLeavesOneRow() = runTest {
        dao.insert(entity(1, addedAt = 100, title = "Old"))
        dao.insert(entity(1, addedAt = 200, title = "New"))

        val all = dao.observeAll().first()

        assertEquals(1, all.size)
        assertEquals("New", all.single().title)
    }

    @Test
    fun countAndIsFavoriteFollowTheTable() = runTest {
        assertEquals(0, dao.observeCount().first())
        assertFalse(dao.observeIsFavorite(1).first())

        dao.insert(entity(1, addedAt = 100))

        assertEquals(1, dao.observeCount().first())
        assertTrue(dao.observeIsFavorite(1).first())
    }

    @Test
    fun deleteRemovesOnlyThatRow() = runTest {
        dao.insert(entity(1, addedAt = 100))
        dao.insert(entity(2, addedAt = 200))

        dao.delete(1)

        assertEquals(listOf(2), dao.observeAll().first().map { it.id })
    }

    @Test
    fun toggleInsertsThenDeletes() = runTest {
        assertTrue(dao.toggle(entity(1, addedAt = 100)))
        assertTrue(dao.observeIsFavorite(1).first())

        assertFalse(dao.toggle(entity(1, addedAt = 200)))
        assertFalse(dao.observeIsFavorite(1).first())
    }
}
