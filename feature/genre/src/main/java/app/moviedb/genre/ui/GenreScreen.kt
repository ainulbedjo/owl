package app.moviedb.genre.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.moviedb.core.common.state.SectionState
import app.moviedb.designsystem.component.BackHeader
import app.moviedb.designsystem.component.GenreArt
import app.moviedb.designsystem.component.LoadingBadge
import app.moviedb.designsystem.theme.MoviedbTheme
import app.moviedb.genre.model.Genre

@Composable
fun GenreScreen(
    onBack: () -> Unit,
    onGenreClick: (genreId: Int, genreName: String) -> Unit,
    state: GenreUiState,
) {
    val genres = (state.genres as? SectionState.Content)?.data.orEmpty()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(Modifier.padding(horizontal = 20.dp)) {
            BackHeader(
                title = "Genres",
                subtitle = "Pick a mood to start browsing",
                onBack = onBack,
            )

            if (state.genres is SectionState.Loading) LoadingBadge()

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
            ) {
                items(genres, key = { it.id }) { genre ->
                    GenreArt(
                        genreId = genre.id,
                        name = genre.name,
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onGenreClick(genre.id, genre.name) },
                    )
                }
            }
        }
    }
}

private val sampleGenres = listOf(
    Genre(28, "Action"), Genre(12, "Adventure"), Genre(16, "Animation"), Genre(35, "Comedy"),
    Genre(80, "Crime"), Genre(18, "Drama"), Genre(878, "Science Fiction"), Genre(53, "Thriller"),
)

@Preview
@Composable
fun GenreScreenPreview() {
    MoviedbTheme(paletteId = "violet") {
        GenreScreen(
            onBack = {},
            onGenreClick = { _, _ -> },
            state = GenreUiState(genres = SectionState.Content(sampleGenres)),
        )
    }
}

@Preview
@Composable
fun GenreScreenLoadingPreview() {
    MoviedbTheme(paletteId = "violet") {
        GenreScreen(onBack = {}, onGenreClick = { _, _ -> }, state = GenreUiState())
    }
}
