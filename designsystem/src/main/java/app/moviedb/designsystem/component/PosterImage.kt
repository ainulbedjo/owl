package app.moviedb.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import app.moviedb.designsystem.theme.MoviedbTheme
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun PosterImage(
    url: String?,
    movieId: Int,
    title: String,
    modifier: Modifier = Modifier,
    letterSize: TextUnit? = 56.sp,
) {
    Box(modifier) {
        PosterArt(
            movieId = movieId,
            title = title,
            letterSize = letterSize,
            modifier = Modifier.fillMaxSize(),
        )
        RemoteImage(url = url, modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun RemoteImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    if (url == null) return

    val context = LocalPlatformContext.current
    // Built once per url, not on every recomposition.
    val request = remember(url) {
        ImageRequest.Builder(context).data(url).crossfade(true).build()
    }

    AsyncImage(
        model = request,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun PosterImagePreview() {
    // A preview has no network, so this shows the fallback art.
    MoviedbTheme(paletteId = "violet") {
        PosterImage(url = "https://image.tmdb.org/t/p/w500/none.jpg", movieId = 1, title = "Midnight Orchard")
    }
}
