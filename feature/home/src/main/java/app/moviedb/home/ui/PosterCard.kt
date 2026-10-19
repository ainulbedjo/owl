package app.moviedb.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.moviedb.core.common.ImageSize
import app.moviedb.core.common.TmdbImage
import app.moviedb.designsystem.component.PosterImage
import app.moviedb.home.R
import app.moviedb.designsystem.format.toCardDate
import app.moviedb.designsystem.format.toRatingLabel
import app.moviedb.home.data.fakeMovies
import app.moviedb.core.model.Movie
import app.moviedb.designsystem.component.RatingBadge
import app.moviedb.designsystem.theme.MoviedbTheme
import app.moviedb.designsystem.theme.pressedCornerShape

/**
 * A poster with its title and date below. The whole column is one tap target;
 * the poster's corners tighten while pressed. [showRating] adds the badge (Top Rated).
 */
@Composable
fun PosterCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showRating: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val posterShape = pressedCornerShape(interactionSource, restingCorner = 20.dp)
    val description = when {
        showRating && movie.rating > 0 ->
            stringResource(R.string.movie_rated, movie.title, movie.rating.toRatingLabel())
        movie.releaseDate != null ->
            stringResource(R.string.movie_released, movie.title, movie.releaseDate.toCardDate())
        else -> movie.title
    }

    Column(
        modifier = modifier
            .width(132.dp)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .semantics { contentDescription = description },
    ) {
        Box(
            Modifier
                .size(width = 132.dp, height = 198.dp)
                .clip(posterShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .indication(interactionSource, ripple())
                .clearAndSetSemantics { },
        ) {
            PosterImage(
                url = TmdbImage.url(movie.posterPath, ImageSize.W342),
                movieId = movie.id,
                title = movie.title,
                modifier = Modifier.fillMaxSize(),
            )
            if (showRating) {
                RatingBadge(
                    rating = movie.rating,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                )
            }
        }
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp, top = 10.dp)
                .clearAndSetSemantics { },
        )
        if (movie.releaseDate != null) {
            Text(
                text = movie.releaseDate.toCardDate(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clearAndSetSemantics { },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PosterCardPreview() {
    MoviedbTheme(paletteId = "violet") {
        PosterCard(movie = fakeMovies[2], onClick = {}, showRating = true)
    }
}
