package app.moviedb.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.moviedb.designsystem.R
import app.moviedb.designsystem.theme.MoviedbTheme

/**
 * Shown once when a section fails because the device is offline.
 * "Open favorites" works offline, so it is offered next to "Try again".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineSheet(
    onDismiss: () -> Unit,
    onTryAgain: () -> Unit,
    onOpenFavorites: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier,
    ) {
        OfflineSheetContent(
            onTryAgain = onTryAgain,
            onOpenFavorites = onOpenFavorites
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OfflineSheetContent(
    onTryAgain: () -> Unit,
    onOpenFavorites: () -> Unit,
) {
    val illustrationShape = MaterialShapes.Cookie12Sided.toShape()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.no_connection_clear),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(200.dp)
                .clip(illustrationShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        )
        Text(
            text = stringResource(R.string.offline_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 20.dp),
        )
        Text(
            text = stringResource(R.string.offline_hint),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 24.dp),
        ) {
            Button(
                onClick = onTryAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
                Text(
                    text = stringResource(R.string.offline_try_again),
                    modifier = Modifier.padding(start = ButtonDefaults.IconSpacing),
                )
            }
            FilledTonalButton(
                onClick = onOpenFavorites,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Text(stringResource(R.string.offline_open_favorites))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OfflineSheetContentPreview() {
    MoviedbTheme(paletteId = "violet") {
        OfflineSheetContent(
            onTryAgain = {},
            onOpenFavorites = {}
        )
    }
}
