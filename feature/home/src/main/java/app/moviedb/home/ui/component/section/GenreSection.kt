package app.moviedb.home.ui.component.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import app.moviedb.designsystem.theme.MoviedbTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GenreSection() {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(colors.primaryContainer)
    ) {
        // Cookie decoration
        CookieDecoration(
            shape = MaterialShapes.Cookie12Sided,
            size = 200.dp,
            color = colors.primary.copy(alpha = 0.16f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 70.dp, y = (-84).dp),
        )
        CookieDecoration(
            shape = MaterialShapes.Cookie9Sided,
            size = 100.dp,
            color = colors.primary.copy(alpha = 0.25f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-30).dp, y = 30.dp)
        )

        // Content, drawn on top.
        Column(Modifier.padding(start = 24.dp, top = 28.dp, end = 24.dp, bottom = 24.dp)) {
            Text(
                text = "More Movies",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.onPrimaryContainer,
                modifier = Modifier.widthIn(max = 220.dp),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Check out movies by genre \nor explore for more",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onPrimaryContainer,
            )
            Spacer(Modifier.height(24.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = {},
                    shapes = ButtonDefaults.shapes(), // Expressive: corners morph when pressed
                    modifier = Modifier.height(ButtonDefaults.MediumContainerHeight),
                    contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight),
                ) {
                    Text("Explore", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.size(8.dp))
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Button(
                    onClick = {},
                    shapes = ButtonDefaults.shapes(),
                    modifier = Modifier.height(ButtonDefaults.MediumContainerHeight),
                    contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.secondary,
                        contentColor = colors.onSecondary,
                    ),
                ) {
                    Text("All Genres", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CookieDecoration(
    shape: RoundedPolygon,
    size: Dp,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .size(size)
            .clip(shape.toShape())
            .background(color),
    )
}

@Preview
@Composable
fun GenreSectionPreview() {
    MoviedbTheme(paletteId = "violet") {
        GenreSection()
    }
}