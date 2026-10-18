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
    val (top, bottom) = gradients[movieId.mod(gradients.size)]
    val brush = remember(movieId) { Brush.linearGradient(listOf(top, bottom)) }
    val shapeA = shapes[movieId.mod(shapes.size)].toShape()
    val shapeB = shapes[(movieId + 1).mod(shapes.size)].toShape()

    Box(modifier = modifier
        .clipToBounds()
        .background(brush)) {
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private val shapes = listOf(
    MaterialShapes.Cookie6Sided,
    MaterialShapes.Cookie9Sided,
    MaterialShapes.Cookie12Sided,
)

/** Poster gradients, top to bottom. A movie always gets the same one. */
private val gradients = listOf(
    Color(0xFFE07F3C) to Color(0xFF5A3A0E), // amber
    Color(0xFFC274D6) to Color(0xFF6E2152), // plum
    Color(0xFF9AA11A) to Color(0xFF1F3F0E), // olive
    Color(0xFF4F8FE8) to Color(0xFF1D2F7A), // blue
    Color(0xFF2BB0A0) to Color(0xFF0D4A55), // teal
    Color(0xFFE8707F) to Color(0xFF7A2E1A), // coral
    Color(0xFF5FB36B) to Color(0xFF14452A), // green
    Color(0xFF8A7BE0) to Color(0xFF2B2370), // indigo
)

@Preview
@Composable
fun PosterArtPreview() {
    MoviedbTheme(paletteId = "violet") {
        PosterArt(movieId = 1, title = "Midnight Orchard")
    }
}
