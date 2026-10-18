package app.moviedb.designsystem.theme.palette

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import app.moviedb.designsystem.theme.Palette

/** Blue. */
internal val OceanPalette = Palette(
    id = "ocean",
    name = "Ocean",
    swatch = Color(0xFF2F84E7),
    light = lightColorScheme(
        primary = Color(0xFF005DB7),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFD0E4FF),
        onPrimaryContainer = Color(0xFF001B3F),
        inversePrimary = Color(0xFFA0C9FF),

        secondary = Color(0xFF476080),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFD0E4FF),
        onSecondaryContainer = Color(0xFF061C37),

        tertiary = Color(0xFF774F88),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFF3DAFF),
        onTertiaryContainer = Color(0xFF2F083E),

        background = Color(0xFFF6FAFF),
        onBackground = Color(0xFF181C21),
        surface = Color(0xFFF6FAFF),
        onSurface = Color(0xFF181C21),
        surfaceVariant = Color(0xFFD9E3F1),
        onSurfaceVariant = Color(0xFF3F4753),

        surfaceDim = Color(0xFFD5DBE2),
        surfaceBright = Color(0xFFF6FAFF),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFEEF4FC),
        surfaceContainer = Color(0xFFE9EEF6),
        surfaceContainerHigh = Color(0xFFE3E9F0),
        surfaceContainerHighest = Color(0xFFDDE3EB),

        outline = Color(0xFF6E7884),
        outlineVariant = Color(0xFFBDC7D5),
        inverseSurface = Color(0xFF2C3136),
        inverseOnSurface = Color(0xFFEBF1F9),

        error = Color(0xFFB3261E),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFF9DEDC),
        onErrorContainer = Color(0xFF410E0B),
    ),
    dark = darkColorScheme(
        primary = Color(0xFFA0C9FF),
        onPrimary = Color(0xFF002F64),
        primaryContainer = Color(0xFF00468C),
        onPrimaryContainer = Color(0xFFD0E4FF),
        inversePrimary = Color(0xFF005DB7),

        secondary = Color(0xFFADC9ED),
        onSecondary = Color(0xFF1A314E),
        secondaryContainer = Color(0xFF304867),
        onSecondaryContainer = Color(0xFFD0E4FF),

        tertiary = Color(0xFFE3B6F7),
        onTertiary = Color(0xFF462056),
        tertiaryContainer = Color(0xFF5E376F),
        onTertiaryContainer = Color(0xFFF3DAFF),

        background = Color(0xFF101419),
        onBackground = Color(0xFFDDE3EB),
        surface = Color(0xFF101419),
        onSurface = Color(0xFFDDE3EB),
        surfaceVariant = Color(0xFF3F4753),
        onSurfaceVariant = Color(0xFFBDC7D5),

        surfaceDim = Color(0xFF101419),
        surfaceBright = Color(0xFF353A3F),
        surfaceContainerLowest = Color(0xFF0B0E13),
        surfaceContainerLow = Color(0xFF181C21),
        surfaceContainer = Color(0xFF1C2025),
        surfaceContainerHigh = Color(0xFF262A30),
        surfaceContainerHighest = Color(0xFF31353B),

        outline = Color(0xFF88929E),
        outlineVariant = Color(0xFF3F4753),
        inverseSurface = Color(0xFFDDE3EB),
        inverseOnSurface = Color(0xFF2C3136),

        error = Color(0xFFF2B8B5),
        onError = Color(0xFF601410),
        errorContainer = Color(0xFF8C1D18),
        onErrorContainer = Color(0xFFF9DEDC),
    ),
)
