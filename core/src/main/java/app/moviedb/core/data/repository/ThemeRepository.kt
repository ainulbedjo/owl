package app.moviedb.core.data.repository

import app.moviedb.core.model.ThemeSettings
import kotlinx.coroutines.flow.Flow

/** Where the app's theme choice is kept. It lives in the local database, so it works offline. */
interface ThemeRepository {

    /** The saved theme, or the default when nothing is saved. Emits again on every change. */
    val settings: Flow<ThemeSettings>

    suspend fun update(settings: ThemeSettings)
}
