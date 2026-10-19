package app.moviedb.core.common.state

import app.moviedb.core.common.AppResult

/** A failure becomes an error state and a success becomes content. */
fun <T> AppResult<T>.toSectionState(): SectionState<T> = when (this) {
    is AppResult.Success -> SectionState.Content(data)
    is AppResult.Failure -> SectionState.Error(error)
}

/** Like [toSectionState], and a list with nothing in it is [SectionState.Empty], not an error. */
fun <T> AppResult<List<T>>.toListSectionState(): SectionState<List<T>> = when (this) {
    is AppResult.Success -> data.toSectionState()
    is AppResult.Failure -> SectionState.Error(error)
}

fun <T> List<T>.toSectionState(): SectionState<List<T>> =
    if (isEmpty()) SectionState.Empty else SectionState.Content(this)
