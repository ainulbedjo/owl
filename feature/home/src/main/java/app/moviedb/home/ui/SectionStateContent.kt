package app.moviedb.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.moviedb.core.common.AppError
import app.moviedb.core.common.state.SectionState
import app.moviedb.designsystem.component.EmptyCard
import app.moviedb.designsystem.component.ErrorCard
import app.moviedb.home.R

/**
 * Shows the right piece for a section's state. Loading and Content differ per section,
 * so they are passed in; Empty and Error look the same for every Home section.
 */
@Composable
fun <T> SectionStateContent(
    state: SectionState<T>,
    sectionName: String,
    onRetry: () -> Unit,
    loading: @Composable () -> Unit,
    content: @Composable (T) -> Unit,
) {
    when (state) {
        SectionState.Loading -> loading()
        is SectionState.Content -> content(state.data)
        SectionState.Empty -> EmptyCard(message = stringResource(R.string.section_empty))
        is SectionState.Error -> ErrorCard(
            title = stringResource(R.string.section_error_title, sectionName),
            message = stringResource(state.error.messageRes()),
            onRetry = onRetry,
        )
    }
}

private fun AppError.messageRes(): Int = when (this) {
    AppError.RateLimited -> R.string.section_error_rate_limited
    else -> R.string.section_error_generic
}
