package app.moviedb.home.ui.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.moviedb.core.model.ThemeMode
import app.moviedb.core.model.ThemeSettings
import app.moviedb.designsystem.theme.Palette
import app.moviedb.designsystem.theme.isWallpaperPaletteSupported
import app.moviedb.home.R

private const val PALETTE_COLUMNS = 4

/** Mode and palette picker. Every change applies at once, there is no confirm button. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppearanceSheet(
    settings: ThemeSettings,
    onSettingsChange: (ThemeSettings) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier,
    ) {
        Column(Modifier.padding(start = 20.dp, end = 20.dp, bottom = 24.dp)) {
            Text(
                text = stringResource(R.string.appearance_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.appearance_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            ModeButtons(
                selected = settings.mode,
                onSelect = { onSettingsChange(settings.copy(mode = it)) },
                modifier = Modifier.padding(top = 20.dp),
            )

            Text(
                text = stringResource(R.string.appearance_palette),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
            )
            PaletteGrid(
                selectedId = settings.paletteId,
                onSelect = { onSettingsChange(settings.copy(paletteId = it)) },
            )

            if (!isWallpaperPaletteSupported) {
                Text(
                    text = stringResource(R.string.appearance_wallpaper_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
        }
    }
}

/** System, Light and Dark as a connected group. The selected one is fully rounded with a check. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ModeButtons(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val modes = ThemeMode.entries
    Row(
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
    ) {
        modes.forEachIndexed { index, mode ->
            val shapes = when (index) {
                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                modes.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
            }
            val isSelected = mode == selected
            ToggleButton(
                checked = isSelected,
                onCheckedChange = { onSelect(mode) },
                shapes = shapes,
                icon = if (isSelected) {
                    { Icon(Icons.Filled.Check, contentDescription = null) }
                } else {
                    null
                },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
            ) {
                Text(stringResource(mode.labelRes()))
            }
        }
    }
}

private fun ThemeMode.labelRes(): Int = when (this) {
    ThemeMode.SYSTEM -> R.string.appearance_mode_system
    ThemeMode.LIGHT -> R.string.appearance_mode_light
    ThemeMode.DARK -> R.string.appearance_mode_dark
}

/** A swatch per palette. The wallpaper swatch is left out below Android 12. */
@Composable
private fun PaletteGrid(
    selectedId: String,
    onSelect: (String) -> Unit,
) {
    val options = buildList {
        if (isWallpaperPaletteSupported) add(PaletteOption(Palette.WALLPAPER_ID, stringResource(R.string.appearance_wallpaper)))
        Palette.presets.forEach { add(PaletteOption(it.id, it.name)) }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.selectableGroup(),
    ) {
        options.chunked(PALETTE_COLUMNS).forEach { rowOptions ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowOptions.forEach { option ->
                    PaletteSwatch(
                        option = option,
                        selected = option.id == selectedId,
                        onClick = { onSelect(option.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                // Keeps the last row's swatches the same width as the full rows.
                repeat(PALETTE_COLUMNS - rowOptions.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

private data class PaletteOption(val id: String, val name: String)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PaletteSwatch(
    option: PaletteOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cookie = MaterialShapes.Cookie9Sided.toShape()
    val fill = if (option.id == Palette.WALLPAPER_ID) {
        Brush.sweepGradient(Palette.presets.map { it.swatch } + Palette.presets.first().swatch)
    } else {
        Brush.linearGradient(listOf(Palette.byId(option.id).swatch, Palette.byId(option.id).swatch))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier.selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(68.dp)
                .border(
                    width = 3.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = CircleShape,
                ),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(cookie)
                    .background(fill),
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }
        }
        Text(
            text = option.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
