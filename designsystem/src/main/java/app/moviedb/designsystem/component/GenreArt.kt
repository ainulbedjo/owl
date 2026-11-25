package app.moviedb.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.moviedb.designsystem.theme.MoviedbTheme
import app.moviedb.designsystem.theme.artGradients
import app.moviedb.designsystem.theme.artShapes

/**
 * A genre tile: a gradient with two soft shapes, picked from the TMDB genre id, and the full
 * genre name at the bottom. Compact on purpose, so it fits a [androidx.compose.foundation.lazy.grid.LazyVerticalGrid].
 * Height comes from the text (at least 72 dp); pass a height in [modifier] to override it.
 * Clip and click handling belong to the caller.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GenreArt(
    genreId: Int,
    name: String,
    modifier: Modifier = Modifier,
) {
    val (top, bottom) = artGradients[genreId.mod(artGradients.size)]
    val brush = remember(genreId) { Brush.linearGradient(listOf(top, bottom)) }
    val shapeA = artShapes[genreId.mod(artShapes.size)].toShape()
    val shapeB = artShapes[(genreId + 1).mod(artShapes.size)].toShape()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 72.dp)
            .clipToBounds()
            .background(brush),
    ) {
        // Fixed dp sizes, not a share of the tile: the tile is short and wide, so a share of the
        // width would give a shape taller than the tile. requiredSize lets it run past the edge.
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(x = 28.dp, y = (-30).dp)
                .requiredSize(104.dp)
                .clip(shapeA)
                .background(Color.White.copy(alpha = 0.3f)),
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-16).dp, y = 22.dp)
                .requiredSize(56.dp)
                .clip(shapeB)
                .background(Color.Black.copy(alpha = 0.3f)),
        )
        // Keeps the white name readable on the light end of every gradient.
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.5f),
                    ),
                ),
        )
        Text(
            text = name,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, end = 12.dp, top = 24.dp, bottom = 10.dp),
        )
    }
}

@Preview
@Composable
fun GenreArtPreview() {
    MoviedbTheme(paletteId = "violet") {
        GenreArt(genreId = 878, name = "Science Fiction")
    }
}
