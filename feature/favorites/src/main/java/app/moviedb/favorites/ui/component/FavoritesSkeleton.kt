package app.moviedb.favorites.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.moviedb.designsystem.component.pulse
import app.moviedb.designsystem.theme.MoviedbTheme
import app.moviedb.favorites.R

@Composable
fun FavoritesSkeleton(modifier: Modifier = Modifier) {
    val description = stringResource(R.string.favorites_loading)
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .padding(horizontal = 16.dp)
            .semantics { contentDescription = description },
    ) {
        repeat(3) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(156.dp)
                    .pulse()
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesSkeletonPreview() {
    MoviedbTheme(paletteId = "violet") {
        FavoritesSkeleton()
    }
}
