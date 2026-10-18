package app.moviedb.designsystem.theme.palette

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import app.moviedb.designsystem.theme.Palette

/** Green. */
internal val ForestPalette = Palette(
    id = "forest",
    name = "Forest",
    swatch = Color(0xFF009845),
    light = lightColorScheme(
        primary = Color(0xFF006D2F),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF7FFB9F),
        onPrimaryContainer = Color(0xFF002108),
        inversePrimary = Color(0xFF61DE83),

        secondary = Color(0xFF43664A),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFC5ECCC),
        onSecondaryContainer = Color(0xFF00210A),

        tertiary = Color(0xFF006876),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF9AF0FF),
        onTertiaryContainer = Color(0xFF001F25),

        background = Color(0xFFF4FBF5),
        onBackground = Color(0xFF171D18),
        surface = Color(0xFFF4FBF5),
        onSurface = Color(0xFF171D18),
        surfaceVariant = Color(0xFFD8E6DA),
        onSurfaceVariant = Color(0xFF3E4A40),

        surfaceDim = Color(0xFFD4DCD5),
        surfaceBright = Color(0xFFF4FBF5),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFEEF6EF),
        surfaceContainer = Color(0xFFE8F0E9),
        surfaceContainerHigh = Color(0xFFE2EAE4),
        surfaceContainerHighest = Color(0xFFDDE4DE),

        outline = Color(0xFF6E7A70),
        outlineVariant = Color(0xFFBCCABE),
        inverseSurface = Color(0xFF2C322D),
        inverseOnSurface = Color(0xFFEBF3EC),

        error = Color(0xFFB3261E),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFF9DEDC),
        onErrorContainer = Color(0xFF410E0B),
    ),
    dark = darkColorScheme(
        primary = Color(0xFF61DE83),
        onPrimary = Color(0xFF003915),
        primaryContainer = Color(0xFF005322),
        onPrimaryContainer = Color(0xFF7FFB9F),
        inversePrimary = Color(0xFF006D2F),

        secondary = Color(0xFFA9D0B0),
        onSecondary = Color(0xFF15371E),
        secondaryContainer = Color(0xFF2C4E34),
        onSecondaryContainer = Color(0xFFC5ECCC),

        tertiary = Color(0xFF6CD5E7),
        onTertiary = Color(0xFF00363E),
        tertiaryContainer = Color(0xFF004E59),
        onTertiaryContainer = Color(0xFF9AF0FF),

        background = Color(0xFF0F1510),
        onBackground = Color(0xFFDDE4DE),
        surface = Color(0xFF0F1510),
        onSurface = Color(0xFFDDE4DE),
        surfaceVariant = Color(0xFF3E4A40),
        onSurfaceVariant = Color(0xFFBCCABE),

        surfaceDim = Color(0xFF0F1510),
        surfaceBright = Color(0xFF353B36),
        surfaceContainerLowest = Color(0xFF0A0F0B),
        surfaceContainerLow = Color(0xFF171D18),
        surfaceContainer = Color(0xFF1B211C),
        surfaceContainerHigh = Color(0xFF262B27),
        surfaceContainerHighest = Color(0xFF303631),

        outline = Color(0xFF879489),
        outlineVariant = Color(0xFF3E4A40),
        inverseSurface = Color(0xFFDDE4DE),
        inverseOnSurface = Color(0xFF2C322D),

        error = Color(0xFFF2B8B5),
        onError = Color(0xFF601410),
        errorContainer = Color(0xFF8C1D18),
        onErrorContainer = Color(0xFFF9DEDC),
    ),
)
