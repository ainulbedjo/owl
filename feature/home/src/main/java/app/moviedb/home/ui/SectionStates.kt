package app.moviedb.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.moviedb.designsystem.component.pulse
import app.moviedb.designsystem.theme.MoviedbTheme
import app.moviedb.home.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun PopularSkeleton(sectionName: String, modifier: Modifier = Modifier) {
    val loadingDescription = stringResource(R.string.section_loading, sectionName)
    LazyRow(
        userScrollEnabled = false,
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
    ) {
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(width = 296.dp, height = 340.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .semantics { contentDescription = loadingDescription },
                ) {
                    LoadingIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        item {
            Box(
                Modifier
                    .size(width = 296.dp, height = 340.dp)
                    .pulse()
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            )
        }
    }
}

@Composable
internal fun PosterSkeleton(sectionName: String, modifier: Modifier = Modifier) {
    val loadingDescription = stringResource(R.string.section_loading, sectionName)
    LazyRow(
        userScrollEnabled = false,
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.semantics { contentDescription = loadingDescription },
    ) {
        items(count = 3) {
            Box(
                Modifier
                    .size(width = 132.dp, height = 198.dp)
                    .pulse()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SkeletonPreview() {
    MoviedbTheme(paletteId = "violet") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PopularSkeleton(sectionName = "Popular")
            PosterSkeleton(sectionName = "Top Rated")
        }
    }
}
