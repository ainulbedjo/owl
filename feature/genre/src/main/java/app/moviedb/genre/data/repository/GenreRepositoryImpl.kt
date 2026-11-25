package app.moviedb.genre.data.repository

import app.moviedb.core.common.AppResult
import app.moviedb.core.common.map
import app.moviedb.core.data.network.safeApiCall
import app.moviedb.genre.data.mapper.toGenres
import app.moviedb.genre.data.network.GenreApi
import app.moviedb.genre.model.Genre
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GenreRepositoryImpl(
    private val api: GenreApi,
) : GenreRepository {

    private val lock = Mutex()
    private var cached: List<Genre>? = null

    /** List is small, no need to call it everytime, so better to cache it. */
    override suspend fun genres(): AppResult<List<Genre>> = lock.withLock {
        cached?.let { return AppResult.Success(it) }
        safeApiCall { api.getMovieGenres() }
            .map { it.toGenres() }
            .also { if (it is AppResult.Success) cached = it.data }
    }
}
