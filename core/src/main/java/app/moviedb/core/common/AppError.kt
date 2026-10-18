package app.moviedb.core.common

/** Typed failure that repositories hand to ViewModels. Cause-to-error mapping: docs/api/tmdb_overview.md. */
sealed interface AppError {
    /** Any `IOException`. The only error that opens the no-connection sheet. */
    data object Network : AppError

    /** HTTP 401 or 403. */
    data object Unauthorized : AppError

    /** HTTP 404. */
    data object NotFound : AppError

    /** HTTP 429. */
    data object RateLimited : AppError

    /** HTTP 5xx. */
    data object Server : AppError

    /** Parse failure or anything else. */
    data object Unexpected : AppError
}
