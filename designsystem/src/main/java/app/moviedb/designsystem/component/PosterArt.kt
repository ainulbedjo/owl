package app.moviedb.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.moviedb.designsystem.theme.MoviedbTheme
import app.moviedb.designsystem.theme.artGradients
import app.moviedb.designsystem.theme.artShapes

/**
 * The stand-in for a poster when `poster_path` is null, and while we have no image loader.
 * A gradient with two soft shapes, picked from the movie id, plus the title's first letter.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PosterArt(
    movieId: Int,
    title: String,
    modifier: Modifier = Modifier,
    letterSize: TextUnit? = 56.sp,
) {
    val (top, bottom) = artGradients[movieId.mod(artGradients.size)]
    val brush = remember(movieId) { Brush.linearGradient(listOf(top, bottom)) }
    val shapeA = artShapes[movieId.mod(artShapes.size)].toShape()
    val shapeB = artShapes[(movieId + 1).mod(artShapes.size)].toShape()

    Box(
        modifier = modifier
            .clipToBounds()
            .background(brush)
    ) {
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .graphicsLayer {
                    translationX = size.width * 0.28f
                    translationY = -size.height * 0.14f
                }
                .clip(shapeA)
                .background(Color.White.copy(alpha = 0.3f)),
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
                .graphicsLayer {
                    translationX = -size.width * 0.12f
                    translationY = size.height * 0.05f
                }
                .clip(shapeB)
                .background(Color.Black.copy(alpha = 0.3f)),
        )
        if (letterSize != null) {
            Text(
                text = title.firstOrNull()?.uppercase().orEmpty(),
                color = Color.White.copy(alpha = 0.92f),
                fontSize = letterSize,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 12.dp, bottom = 6.dp),
            )
        }
    }
}

@Preview
@Composable
fun PosterArtPreview() {
    MoviedbTheme(paletteId = "violet") {
        PosterArt(movieId = 1, title = "Midnight Orchard")
    }
}
