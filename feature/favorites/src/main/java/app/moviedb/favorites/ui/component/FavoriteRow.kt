package app.moviedb.favorites.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import app.moviedb.core.model.Movie
import app.moviedb.core.common.ImageSize
import app.moviedb.core.common.TmdbImage
import app.moviedb.designsystem.component.PosterImage
import app.moviedb.designsystem.format.toCardDate
import app.moviedb.designsystem.theme.MoviedbTheme
import app.moviedb.designsystem.theme.pressedCornerShape
import app.moviedb.favorites.R
import app.moviedb.favorites.data.sampleFavorites

@Composable
fun FavoriteRow(
    movie: Movie,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val openDescription = stringResource(R.string.favorites_open, movie.title)
    val removeDescription = stringResource(R.string.favorites_remove, movie.title)
    val date = movie.releaseDate.toCardDate()

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = pressedCornerShape(interactionSource, restingCorner = 28.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = openDescription },
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(12.dp),
        ) {
            PosterImage(
                url = TmdbImage.url(movie.posterPath, ImageSize.W185),
                movieId = movie.id,
                title = movie.title,
                letterSize = 40.sp,
                modifier = Modifier
                    .size(width = 92.dp, height = 132.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clearAndSetSemantics { },
            )
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 2.dp)
                            .clearAndSetSemantics { },
                    )
                    // A 48 dp touch area around a 24 dp icon. The offset keeps the icon aligned with the title and the row's corner.
                    IconButton(
                        onClick = onRemoveClick,
                        modifier = Modifier.offset(x = 8.dp, y = (-8).dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = removeDescription,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                if (date.isNotEmpty()) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.clearAndSetSemantics { },
                    )
                }
                if (movie.overview.isNotBlank()) {
                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .clearAndSetSemantics { },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteRowPreview() {
    MoviedbTheme(paletteId = "violet") {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            FavoriteRow(movie = sampleFavorites[0], onClick = {}, onRemoveClick = {})
            FavoriteRow(movie = sampleFavorites[2], onClick = {}, onRemoveClick = {})
        }
    }
}
