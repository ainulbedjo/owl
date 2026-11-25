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
    onGenreClick: (genreId: Int, genreName: String) -> Unit,
) {
    composable<GenreRoute> {
        GenreEntry(onBack = onBack, onGenreClick = onGenreClick)
    }
}

@Composable
fun GenreEntry(
    onBack: () -> Unit,
    onGenreClick: (genreId: Int, genreName: String) -> Unit,
) {
    val viewModel: GenreViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    GenreScreen(
        onBack = onBack,
        onGenreClick = onGenreClick,
        state = state
    )
}