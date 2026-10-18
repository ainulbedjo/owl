package app.moviedb.home.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.moviedb.home.R

/** Large title that collapses on scroll, with the theme button and the favorites button. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeTopBar(
    favoriteCount: Int,
    onThemeClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    val favoritesDescription = stringResource(R.string.home_favorites_saved, favoriteCount)

    LargeFlexibleTopAppBar(
        title = { Text(stringResource(R.string.home_title), fontWeight = FontWeight.SemiBold) },
        subtitle = { Text(stringResource(R.string.home_subtitle)) },
        actions = {
            FilledIconButton(
                onClick = onThemeClick,
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ),
            ) {
                Icon(
                    painter = painterResource(app.moviedb.home.R.drawable.ic_palette),
                    contentDescription = stringResource(R.string.home_change_theme),
                )
            }
            Spacer(Modifier.width(8.dp))
            FilledIconButton(
                onClick = onFavoritesClick,
                modifier = Modifier
                    .size(48.dp)
                    .semantics { contentDescription = favoritesDescription },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
            ) {
                BadgedBox(
                    badge = {
                        if (favoriteCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ) { Text(favoriteCount.toString()) }
                        }
                    },
                ) {
                    Icon(imageVector = Icons.Filled.Favorite, contentDescription = null)
                }
            }
            // The bar gives its actions no end margin, so keep them off the screen edge.
            Spacer(Modifier.width(12.dp))
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}
