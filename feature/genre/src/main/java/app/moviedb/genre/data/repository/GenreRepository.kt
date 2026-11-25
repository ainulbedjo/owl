package app.moviedb.genre.data.repository

import app.moviedb.core.common.AppResult
import app.moviedb.genre.model.Genre

interface GenreRepository {
    /** All official movie genres. Loaded once per process, then served from memory. */
    suspend fun genres(): AppResult<List<Genre>>
}
