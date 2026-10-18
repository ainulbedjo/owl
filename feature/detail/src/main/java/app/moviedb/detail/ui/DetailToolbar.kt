package app.moviedb.detail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.moviedb.designsystem.theme.MoviedbTheme
import app.moviedb.detail.R

/**
 * The pinned Save and Share buttons. Both are off until the movie has loaded,
 * because the saved snapshot and the share text are built from it.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun DetailToolbar(
    isFavorite: Boolean,
    enabled: Boolean,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stateText = stringResource(if (isFavorite) R.string.detail_state_on else R.string.detail_state_off)

    Surface(
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shadowElevation = 6.dp,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(8.dp),
        ) {
            ToggleButton(
                checked = isFavorite,
                onCheckedChange = { onFavoriteClick() },
                enabled = enabled,
                colors = ToggleButtonDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    checkedContainerColor = MaterialTheme.colorScheme.primary,
                    checkedContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.semantics { stateDescription = stateText },
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(if (isFavorite) R.string.detail_saved else R.string.detail_save),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            IconButton(onClick = onShareClick, enabled = enabled) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = stringResource(R.string.detail_share),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailToolbarPreview() {
    MoviedbTheme(paletteId = "violet") {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)) {
            DetailToolbar(isFavorite = false, enabled = true, onFavoriteClick = {}, onShareClick = {})
            DetailToolbar(isFavorite = true, enabled = true, onFavoriteClick = {}, onShareClick = {})
        }
    }
}
