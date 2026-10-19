package app.moviedb.detail.ui.sheet

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import app.moviedb.core.model.MovieDetail

/** The public TMDB page for a movie. This web address is not part of the API reference. */
internal fun MovieDetail.shareLink(): String = "https://www.themoviedb.org/movie/$id"

internal fun MovieDetail.shareText(): String = "$title\n${shareLink()}"

/** Starts [intent]. Returns false when no app can handle it, so the caller can show a message. */
private fun Context.launch(intent: Intent): Boolean = try {
    startActivity(intent)
    true
} catch (_: ActivityNotFoundException) {
    false
}

internal fun Context.shareByMessages(movie: MovieDetail): Boolean = launch(
    Intent(Intent.ACTION_SENDTO, "smsto:".toUri()).putExtra("sms_body", movie.shareText()),
)

internal fun Context.shareByEmail(movie: MovieDetail): Boolean = launch(
    Intent(Intent.ACTION_SENDTO, "mailto:".toUri())
        .putExtra(Intent.EXTRA_SUBJECT, movie.title)
        .putExtra(Intent.EXTRA_TEXT, movie.shareText()),
)

internal fun Context.shareWithChooser(movie: MovieDetail): Boolean = launch(
    Intent.createChooser(
        Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, movie.shareText()),
        null,
    ),
)

internal fun Context.copyLink(movie: MovieDetail) {
    val clipboard = getSystemService(ClipboardManager::class.java)
    clipboard.setPrimaryClip(ClipData.newPlainText(movie.title, movie.shareLink()))
}
