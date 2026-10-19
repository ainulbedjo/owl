package app.moviedb.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.moviedb.designsystem.format.toCardDate
import app.moviedb.designsystem.format.toRatingLabel
import app.moviedb.core.model.Movie
import app.moviedb.core.common.ImageSize
import app.moviedb.core.common.TmdbImage
import app.moviedb.designsystem.component.PosterImage
import app.moviedb.designsystem.component.RatingChip
import app.moviedb.designsystem.theme.MoviedbTheme
import app.moviedb.designsystem.theme.pressedCornerShape
import app.moviedb.home.R

/** The wide card of the Popular strip. Corners tighten while pressed. */
@Composable
fun PopularCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val description = if (movie.rating > 0) {
        stringResource(R.string.movie_rated, movie.title, movie.rating.toRatingLabel())
    } else {
        movie.title
    }

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = pressedCornerShape(interactionSource, restingCorner = 28.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier
            .size(width = 296.dp, height = 340.dp)
            .semantics { contentDescription = description },
    ) {
        // The card reads as one element, so its parts must not be announced again.
        Box(Modifier.fillMaxSize().clearAndSetSemantics { }) {
            PosterImage(
                url = TmdbImage.url(movie.posterPath, ImageSize.W500),
                movieId = movie.id,
                title = movie.title,
                letterSize = null,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.62f)
                    .background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.74f))),
                    ),
            )
            RatingChip(
                rating = movie.rating,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
            )
            // White on purpose: the text always sits on the dark scrim, in every theme.
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp),
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = movie.releaseDate.toCardDate(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PopularCardPreview() {
    MoviedbTheme(paletteId = "violet") {
        PopularCard(movie = _root_ide_package_.app.moviedb.home.data.fakeMovies.first(), onClick = {})
    }
}
