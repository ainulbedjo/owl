package app.moviedb.core.data.repository

import app.moviedb.core.data.local.FavoriteDao
import app.moviedb.core.data.local.toFavoriteEntity
import app.moviedb.core.data.local.toMovie
import app.moviedb.core.model.Movie
import app.moviedb.core.model.MovieDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl internal constructor(
    private val dao: FavoriteDao,
    private val now: () -> Long,
) : FavoriteRepository {

    constructor(dao: FavoriteDao) : this(dao, System::currentTimeMillis)

    override fun observeAll(): Flow<List<Movie>> =
        dao.observeAll().map { list -> list.map { it.toMovie() } }

    override fun observeCount(): Flow<Int> = dao.observeCount()

    override fun observeIsFavorite(id: Int): Flow<Boolean> = dao.observeIsFavorite(id)

    override suspend fun toggle(movie: MovieDetail): Boolean =
        dao.toggle(movie.toFavoriteEntity(addedAt = now()))

    override suspend fun remove(id: Int) = dao.delete(id)
}
