package app.moviedb.detail.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.moviedb.designsystem.component.pulse
import app.moviedb.designsystem.theme.MoviedbTheme
import app.moviedb.detail.R

@Composable
private fun SkeletonBar(width: Dp?, height: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier
            .then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(height)
            .pulse()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
    )
}

/** Stands in for the title, chips, and overview while the movie loads. */
@Composable
fun MovieSkeleton(modifier: Modifier = Modifier) {
    val description = stringResource(R.string.detail_loading_movie)
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .padding(horizontal = 20.dp)
            .semantics { contentDescription = description },
    ) {
        SkeletonBar(width = 220.dp, height = 36.dp)
        SkeletonBar(width = 160.dp, height = 20.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) { SkeletonBar(width = 88.dp, height = 32.dp) }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 16.dp),
        ) {
            SkeletonBar(width = null, height = 16.dp)
            SkeletonBar(width = null, height = 16.dp)
            SkeletonBar(width = 240.dp, height = 16.dp)
        }
    }
}

/** Two cards while the reviews load. */
@Composable
internal fun ReviewsSkeleton(modifier: Modifier = Modifier) {
    val description = stringResource(R.string.detail_loading_reviews)
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .padding(horizontal = 20.dp)
            .semantics { contentDescription = description },
    ) {
        repeat(2) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .pulse()
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SkeletonsPreview() {
    MoviedbTheme(paletteId = "violet") {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            MovieSkeleton()
            ReviewsSkeleton()
        }
    }
}
