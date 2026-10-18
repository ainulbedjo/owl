package app.moviedb.core.data.network

import app.moviedb.core.common.AppError
import app.moviedb.core.common.AppResult
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> safeApiCall(block: suspend () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (_: IOException) {
    AppResult.Failure(AppError.Network)
} catch (_: Exception) {
    AppResult.Failure(AppError.Unexpected)
}

fun Int.toAppError(): AppError = when (this) {
    401, 403 -> AppError.Unauthorized
    404 -> AppError.NotFound
    429 -> AppError.RateLimited
    in 500..599 -> AppError.Server
    else -> AppError.Unexpected
}