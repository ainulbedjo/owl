package app.moviedb.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import app.moviedb.core.model.Movie
import app.moviedb.designsystem.component.PosterImage
import app.moviedb.designsystem.format.toCardDate
import app.moviedb.designsystem.format.toRatingLabel
import app.moviedb.designsystem.theme.MoviedbTheme
import app.moviedb.designsystem.theme.pressedCornerShape
import app.moviedb.home.R
import app.moviedb.home.data.fakeMovies

@Composable
fun PosterWideCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val imageShape = pressedCornerShape(interactionSource, restingCorner = 16.dp)
    val date = movie.releaseDate.toCardDate()
    val description = if (date.isNotEmpty()) {
        stringResource(R.string.movie_released, movie.title, date)
    } else {
        movie.title
    }

    Column(
        modifier = modifier
            .width(252.dp)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .semantics { contentDescription = description },
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(imageShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .indication(interactionSource, ripple())
                .clearAndSetSemantics { },
        ) {
            PosterImage(
                url = TmdbImage.url(movie.backdropPath ?: movie.posterPath, ImageSize.W780),
                movieId = movie.id,
                title = movie.title,
                letterSize = null,
                modifier = Modifier.fillMaxSize(),
            )
            if (date.isNotEmpty()) {
                DateBadge(
                    text = date,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                )
            }
        }
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp, top = 10.dp)
                .clearAndSetSemantics { },
        )
        if (movie.rating > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .clearAndSetSemantics { },
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = movie.rating.toRatingLabel(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun DateBadge(text: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.Black.copy(alpha = 0.7f),
        contentColor = Color.White,
        modifier = modifier,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PosterWideCardPreview() {
    MoviedbTheme(paletteId = "violet") {
        PosterWideCard(movie = fakeMovies[2], onClick = {}, modifier = Modifier.padding(16.dp))
    }
}
