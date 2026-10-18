package app.moviedb.designsystem.theme.palette

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import app.moviedb.designsystem.theme.Palette

/** Purple. The default palette. */
internal val VioletPalette = Palette(
    id = "violet",
    name = "Violet",
    swatch = Color(0xFF926FE3),
    light = lightColorScheme(
        primary = Color(0xFF6D47B8),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFE5DEFF),
        onPrimaryContainer = Color(0xFF280056),
        inversePrimary = Color(0xFFCDBDFF),

        secondary = Color(0xFF62597E),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFE5DEFF),
        onSecondaryContainer = Color(0xFF1F1636),

        tertiary = Color(0xFF8D4865),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFFD8E6),
        onTertiaryContainer = Color(0xFF3E0020),

        background = Color(0xFFFAF8FF),
        onBackground = Color(0xFF1C1B21),
        surface = Color(0xFFFAF8FF),
        onSurface = Color(0xFF1C1B21),
        surfaceVariant = Color(0xFFE4E0F1),
        onSurfaceVariant = Color(0xFF484552),

        surfaceDim = Color(0xFFDBD9E2),
        surfaceBright = Color(0xFFFAF8FF),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF4F2FB),
        surfaceContainer = Color(0xFFEEEDF6),
        surfaceContainerHigh = Color(0xFFE9E7F0),
        surfaceContainerHighest = Color(0xFFE3E1EA),

        outline = Color(0xFF787584),
        outlineVariant = Color(0xFFC8C4D4),
        inverseSurface = Color(0xFF312F36),
        inverseOnSurface = Color(0xFFF1F0F8),

        error = Color(0xFFB3261E),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFF9DEDC),
        onErrorContainer = Color(0xFF410E0B),
    ),
    dark = darkColorScheme(
        primary = Color(0xFFCDBDFF),
        onPrimary = Color(0xFF410C81),
        primaryContainer = Color(0xFF562D9C),
        onPrimaryContainer = Color(0xFFE5DEFF),
        inversePrimary = Color(0xFF6D47B8),

        secondary = Color(0xFFCAC1EC),
        onSecondary = Color(0xFF342B4D),
        secondaryContainer = Color(0xFF4A4165),
        onSecondaryContainer = Color(0xFFE5DEFF),

        tertiary = Color(0xFFFFB0CE),
        onTertiary = Color(0xFF581836),
        tertiaryContainer = Color(0xFF72304D),
        onTertiaryContainer = Color(0xFFFFD8E6),

        background = Color(0xFF141318),
        onBackground = Color(0xFFE3E1EA),
        surface = Color(0xFF141318),
        onSurface = Color(0xFFE3E1EA),
        surfaceVariant = Color(0xFF484552),
        onSurfaceVariant = Color(0xFFC8C4D4),

        surfaceDim = Color(0xFF141318),
        surfaceBright = Color(0xFF3A383F),
        surfaceContainerLowest = Color(0xFF0F0D13),
        surfaceContainerLow = Color(0xFF1C1B21),
        surfaceContainer = Color(0xFF201F25),
        surfaceContainerHigh = Color(0xFF2A2930),
        surfaceContainerHighest = Color(0xFF35343B),

        outline = Color(0xFF928F9E),
        outlineVariant = Color(0xFF484552),
        inverseSurface = Color(0xFFE3E1EA),
        inverseOnSurface = Color(0xFF312F36),

        error = Color(0xFFF2B8B5),
        onError = Color(0xFF601410),
        errorContainer = Color(0xFF8C1D18),
        onErrorContainer = Color(0xFFF9DEDC),
    ),
)
