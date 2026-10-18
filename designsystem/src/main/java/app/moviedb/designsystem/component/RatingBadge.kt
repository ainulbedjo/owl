package app.moviedb.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.moviedb.designsystem.format.toRatingLabel
import app.moviedb.designsystem.theme.MoviedbTheme

/** A scalloped badge with the rating, for Top Rated posters. Shows nothing when not rated (0.0). */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RatingBadge(
    rating: Double,
    modifier: Modifier = Modifier,
) {
    if (rating <= 0.0) return

    val shape = MaterialShapes.Cookie9Sided.toShape()
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(44.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.tertiaryContainer),
    ) {
        Text(
            text = rating.toRatingLabel(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
        )
    }
}

@Preview
@Composable
private fun RatingBadgePreview() {
    MoviedbTheme(paletteId = "violet") {
        RatingBadge(rating = 8.4)
    }
}
