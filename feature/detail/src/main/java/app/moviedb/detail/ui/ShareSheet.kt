package app.moviedb.detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.moviedb.core.model.MovieDetail
import app.moviedb.designsystem.component.PosterArt
import app.moviedb.designsystem.theme.MoviedbTheme
import app.moviedb.detail.R

/** What the viewer picked. The screen starts the intent, because it needs a `Context`. */
internal enum class ShareAction { Messages, CopyLink, Email, More }

/** A modal bottom sheet with a summary of the movie and four ways to send it. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShareSheet(
    movie: MovieDetail,
    onAction: (ShareAction) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier,
    ) {
        ShareSheetContent(movie = movie, onAction = onAction)
    }
}

@Composable
private fun ShareSheetContent(movie: MovieDetail, onAction: (ShareAction) -> Unit) {
    Column(Modifier.padding(start = 20.dp, end = 20.dp, bottom = 24.dp)) {
        Text(
            text = stringResource(R.string.share_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )

        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(12.dp),
            ) {
                // The w154 poster replaces PosterArt once we add Coil.
                PosterArt(
                    movieId = movie.id,
                    title = movie.title,
                    letterSize = null,
                    modifier = Modifier
                        .size(width = 56.dp, height = 80.dp)
                        .clip(RoundedCornerShape(16.dp)),
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = movie.shareLink(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
        ) {
            ShareButton(
                icon = Icons.AutoMirrored.Filled.Message,
                label = stringResource(R.string.share_messages),
                container = MaterialTheme.colorScheme.secondaryContainer,
                content = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = { onAction(ShareAction.Messages) },
            )
            ShareButton(
                icon = Icons.Filled.Link,
                label = stringResource(R.string.share_copy_link),
                container = MaterialTheme.colorScheme.primary,
                content = MaterialTheme.colorScheme.onPrimary,
                onClick = { onAction(ShareAction.CopyLink) },
            )
            ShareButton(
                icon = Icons.Filled.Email,
                label = stringResource(R.string.share_email),
                container = MaterialTheme.colorScheme.tertiaryContainer,
                content = MaterialTheme.colorScheme.onTertiaryContainer,
                onClick = { onAction(ShareAction.Email) },
            )
            ShareButton(
                icon = Icons.Filled.MoreHoriz,
                label = stringResource(R.string.share_more),
                container = MaterialTheme.colorScheme.surfaceContainerHighest,
                content = MaterialTheme.colorScheme.onSurface,
                onClick = { onAction(ShareAction.More) },
            )
        }
    }
}

@Composable
private fun ShareButton(
    icon: ImageVector,
    label: String,
    container: Color,
    content: Color,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(container),
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = content)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShareSheetContentPreview() {
    MoviedbTheme(paletteId = "violet") {
        ShareSheetContent(movie = sampleMovie, onAction = {})
    }
}
