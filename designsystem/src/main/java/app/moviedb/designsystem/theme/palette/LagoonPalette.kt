package app.moviedb.designsystem.theme.palette

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import app.moviedb.designsystem.theme.Palette

/** Oceanic blue-green. */
internal val LagoonPalette = Palette(
    id = "lagoon",
    name = "Lagoon",
    swatch = Color(0xFF009393),
    light = lightColorScheme(
        primary = Color(0xFF006A6A),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF00FBFC),
        onPrimaryContainer = Color(0xFF002020),
        inversePrimary = Color(0xFF00DDDD),

        secondary = Color(0xFF2D6767),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFB2EDEC),
        onSecondaryContainer = Color(0xFF002020),

        tertiary = Color(0xFF356096),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFD0E4FF),
        onTertiaryContainer = Color(0xFF001B3F),

        background = Color(0xFFF0FCFB),
        onBackground = Color(0xFF151D1D),
        surface = Color(0xFFF0FCFB),
        onSurface = Color(0xFF151D1D),
        surfaceVariant = Color(0xFFD2E7E6),
        onSurfaceVariant = Color(0xFF394A4A),

        surfaceDim = Color(0xFFD1DCDC),
        surfaceBright = Color(0xFFF0FCFB),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFEAF6F6),
        surfaceContainer = Color(0xFFE5F0F0),
        surfaceContainerHigh = Color(0xFFDFEAEA),
        surfaceContainerHighest = Color(0xFFD9E5E4),

        outline = Color(0xFF687B7A),
        outlineVariant = Color(0xFFB6CACA),
        inverseSurface = Color(0xFF293232),
        inverseOnSurface = Color(0xFFE8F3F3),

        error = Color(0xFFB3261E),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFF9DEDC),
        onErrorContainer = Color(0xFF410E0B),
    ),
    dark = darkColorScheme(
        primary = Color(0xFF00DDDD),
        onPrimary = Color(0xFF003737),
        primaryContainer = Color(0xFF004F50),
        onPrimaryContainer = Color(0xFF00FBFC),
        inversePrimary = Color(0xFF006A6A),

        secondary = Color(0xFF96D1D0),
        onSecondary = Color(0xFF003737),
        secondaryContainer = Color(0xFF114F4F),
        onSecondaryContainer = Color(0xFFB2EDEC),

        tertiary = Color(0xFFA0C9FF),
        onTertiary = Color(0xFF023062),
        tertiaryContainer = Color(0xFF1C477B),
        onTertiaryContainer = Color(0xFFD0E4FF),

        background = Color(0xFF0D1515),
        onBackground = Color(0xFFD9E5E4),
        surface = Color(0xFF0D1515),
        onSurface = Color(0xFFD9E5E4),
        surfaceVariant = Color(0xFF394A4A),
        onSurfaceVariant = Color(0xFFB6CACA),

        surfaceDim = Color(0xFF0D1515),
        surfaceBright = Color(0xFF323B3B),
        surfaceContainerLowest = Color(0xFF08100F),
        surfaceContainerLow = Color(0xFF151D1D),
        surfaceContainer = Color(0xFF192121),
        surfaceContainerHigh = Color(0xFF232C2B),
        surfaceContainerHighest = Color(0xFF2E3636),

        outline = Color(0xFF819494),
        outlineVariant = Color(0xFF394A4A),
        inverseSurface = Color(0xFFD9E5E4),
        inverseOnSurface = Color(0xFF293232),

        error = Color(0xFFF2B8B5),
        onError = Color(0xFF601410),
        errorContainer = Color(0xFF8C1D18),
        onErrorContainer = Color(0xFFF9DEDC),
    ),
)
