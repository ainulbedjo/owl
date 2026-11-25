package app.moviedb.genre.data.repository

import app.moviedb.core.common.AppResult
import app.moviedb.genre.model.Genre

interface GenreRepository {
    suspend fun genres(): AppResult<List<Genre>>
}
