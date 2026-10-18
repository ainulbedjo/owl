package app.moviedb.designsystem.theme.palette

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import app.moviedb.designsystem.theme.Palette

/** Orange. */
internal val SunsetPalette = Palette(
    id = "sunset",
    name = "Sunset",
    swatch = Color(0xFFCC6900),
    light = lightColorScheme(
        primary = Color(0xFF944A00),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFDCC5),
        onPrimaryContainer = Color(0xFF311300),
        inversePrimary = Color(0xFFFFB785),

        secondary = Color(0xFF7A573E),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFFFDCC5),
        onSecondaryContainer = Color(0xFF311300),

        tertiary = Color(0xFF5C6312),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFDEE99B),
        onTertiaryContainer = Color(0xFF1A1D00),

        background = Color(0xFFFFF8F4),
        onBackground = Color(0xFF201A16),
        surface = Color(0xFFFFF8F4),
        onSurface = Color(0xFF201A16),
        surfaceVariant = Color(0xFFEFE0D6),
        onSurfaceVariant = Color(0xFF51443C),

        surfaceDim = Color(0xFFE1D8D3),
        surfaceBright = Color(0xFFFFF8F4),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFFBF2EC),
        surfaceContainer = Color(0xFFF5ECE7),
        surfaceContainerHigh = Color(0xFFEFE7E1),
        surfaceContainerHighest = Color(0xFFE9E1DB),

        outline = Color(0xFF82746B),
        outlineVariant = Color(0xFFD3C4BA),
        inverseSurface = Color(0xFF362F2B),
        inverseOnSurface = Color(0xFFF8EFEA),

        error = Color(0xFFB3261E),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFF9DEDC),
        onErrorContainer = Color(0xFF410E0B),
    ),
    dark = darkColorScheme(
        primary = Color(0xFFFFB785),
        onPrimary = Color(0xFF502400),
        primaryContainer = Color(0xFF713700),
        onPrimaryContainer = Color(0xFFFFDCC5),
        inversePrimary = Color(0xFF944A00),

        secondary = Color(0xFFE8BEA3),
        onSecondary = Color(0xFF492811),
        secondaryContainer = Color(0xFF613F27),
        onSecondaryContainer = Color(0xFFFFDCC5),

        tertiary = Color(0xFFC2CD80),
        onTertiary = Color(0xFF2F3300),
        tertiaryContainer = Color(0xFF454B00),
        onTertiaryContainer = Color(0xFFDEE99B),

        background = Color(0xFF18120E),
        onBackground = Color(0xFFE9E1DB),
        surface = Color(0xFF18120E),
        onSurface = Color(0xFFE9E1DB),
        surfaceVariant = Color(0xFF51443C),
        onSurfaceVariant = Color(0xFFD3C4BA),

        surfaceDim = Color(0xFF18120E),
        surfaceBright = Color(0xFF3F3834),
        surfaceContainerLowest = Color(0xFF130D09),
        surfaceContainerLow = Color(0xFF201A16),
        surfaceContainer = Color(0xFF241E1A),
        surfaceContainerHigh = Color(0xFF2F2925),
        surfaceContainerHighest = Color(0xFF3A332F),

        outline = Color(0xFF9C8E85),
        outlineVariant = Color(0xFF51443C),
        inverseSurface = Color(0xFFE9E1DB),
        inverseOnSurface = Color(0xFF362F2B),

        error = Color(0xFFF2B8B5),
        onError = Color(0xFF601410),
        errorContainer = Color(0xFF8C1D18),
        onErrorContainer = Color(0xFFF9DEDC),
    ),
)
