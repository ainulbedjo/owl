package app.moviedb.genre.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object GenreRoute

fun NavGraphBuilder.genreScreen(
    onBack: () -> Unit,
) {
    composable<GenreRoute> {
        GenreEntry(onBack = onBack)
    }
}

@Composable
fun GenreEntry(
    onBack: () -> Unit,
) {
    val viewModel: GenreViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    GenreScreen(
        onBack = onBack,
        state = state
    )
}