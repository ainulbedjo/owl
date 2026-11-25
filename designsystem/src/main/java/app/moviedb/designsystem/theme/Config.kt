package app.moviedb.designsystem.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.ui.graphics.Color
import app.moviedb.designsystem.component.GenreArt
import app.moviedb.designsystem.component.PosterArt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val artShapes = listOf(
    MaterialShapes.Cookie6Sided,
    MaterialShapes.Cookie9Sided,
    MaterialShapes.Cookie12Sided,
)

/** Art gradients, top to bottom, shared by [PosterArt] and [GenreArt]. An id always gets the same one. */
val artGradients = listOf(
    Color(0xFFE07F3C) to Color(0xFF5A3A0E), // amber
    Color(0xFFC274D6) to Color(0xFF6E2152), // plum
    Color(0xFF9AA11A) to Color(0xFF1F3F0E), // olive
    Color(0xFF4F8FE8) to Color(0xFF1D2F7A), // blue
    Color(0xFF2BB0A0) to Color(0xFF0D4A55), // teal
    Color(0xFFE8707F) to Color(0xFF7A2E1A), // coral
    Color(0xFF5FB36B) to Color(0xFF14452A), // green
    Color(0xFF8A7BE0) to Color(0xFF2B2370), // indigo
)