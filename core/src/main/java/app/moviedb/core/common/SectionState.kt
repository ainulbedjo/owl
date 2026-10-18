package app.moviedb.core.common

/** State of one independently loading part of a screen. An empty list is [Empty], not [Error]. */
sealed interface SectionState<out T> {
    data object Loading : SectionState<Nothing>
    data class Content<T>(val data: T) : SectionState<T>
    data object Empty : SectionState<Nothing>
    data class Error(val error: AppError) : SectionState<Nothing>
}
