package app.moviedb.designsystem.theme.palette

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import app.moviedb.designsystem.theme.Palette

/** Pink. */
internal val RosePalette = Palette(
    id = "rose",
    name = "Rose",
    swatch = Color(0xFFDC5572),
    light = lightColorScheme(
        primary = Color(0xFFAF294E),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFD9DE),
        onPrimaryContainer = Color(0xFF400013),
        inversePrimary = Color(0xFFFFB2BD),

        secondary = Color(0xFF7F5259),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFFFD9DE),
        onSecondaryContainer = Color(0xFF340F17),

        tertiary = Color(0xFF83540E),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFFDDB7),
        onTertiaryContainer = Color(0xFF2B1700),

        background = Color(0xFFFFF8F8),
        onBackground = Color(0xFF211A1B),
        surface = Color(0xFFFFF8F8),
        onSurface = Color(0xFF211A1B),
        surfaceVariant = Color(0xFFF2DEE0),
        onSurfaceVariant = Color(0xFF534345),

        surfaceDim = Color(0xFFE2D7D9),
        surfaceBright = Color(0xFFFFF8F8),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFFCF1F2),
        surfaceContainer = Color(0xFFF6EBEC),
        surfaceContainerHigh = Color(0xFFF0E6E7),
        surfaceContainerHighest = Color(0xFFEBE0E1),

        outline = Color(0xFF847375),
        outlineVariant = Color(0xFFD5C2C4),
        inverseSurface = Color(0xFF372E2F),
        inverseOnSurface = Color(0xFFF9EEEF),

        error = Color(0xFFB3261E),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFF9DEDC),
        onErrorContainer = Color(0xFF410E0B),
    ),
    dark = darkColorScheme(
        primary = Color(0xFFFFB2BD),
        onPrimary = Color(0xFF670025),
        primaryContainer = Color(0xFF900037),
        onPrimaryContainer = Color(0xFFFFD9DE),
        inversePrimary = Color(0xFFAF294E),

        secondary = Color(0xFFEDB9C0),
        onSecondary = Color(0xFF4C242B),
        secondaryContainer = Color(0xFF653B41),
        onSecondaryContainer = Color(0xFFFFD9DE),

        tertiary = Color(0xFFF2BD7F),
        onTertiary = Color(0xFF472A00),
        tertiaryContainer = Color(0xFF663E00),
        onTertiaryContainer = Color(0xFFFFDDB7),

        background = Color(0xFF191212),
        onBackground = Color(0xFFEBE0E1),
        surface = Color(0xFF191212),
        onSurface = Color(0xFFEBE0E1),
        surfaceVariant = Color(0xFF534345),
        onSurfaceVariant = Color(0xFFD5C2C4),

        surfaceDim = Color(0xFF191212),
        surfaceBright = Color(0xFF403738),
        surfaceContainerLowest = Color(0xFF130C0D),
        surfaceContainerLow = Color(0xFF211A1B),
        surfaceContainer = Color(0xFF251E1F),
        surfaceContainerHigh = Color(0xFF302829),
        surfaceContainerHighest = Color(0xFF3B3334),

        outline = Color(0xFF9E8C8E),
        outlineVariant = Color(0xFF534345),
        inverseSurface = Color(0xFFEBE0E1),
        inverseOnSurface = Color(0xFF372E2F),

        error = Color(0xFFF2B8B5),
        onError = Color(0xFF601410),
        errorContainer = Color(0xFF8C1D18),
        onErrorContainer = Color(0xFFF9DEDC),
    ),
)
