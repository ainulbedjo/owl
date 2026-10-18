package app.moviedb.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PaletteTest {

    private fun contrast(a: Color, b: Color): Double {
        val l1 = a.luminance().toDouble()
        val l2 = b.luminance().toDouble()
        return (maxOf(l1, l2) + 0.05) / (minOf(l1, l2) + 0.05)
    }

    private fun pairs(s: ColorScheme) = mapOf(
        "onPrimary/primary" to (s.onPrimary to s.primary),
        "onPrimaryContainer/primaryContainer" to (s.onPrimaryContainer to s.primaryContainer),
        "onSecondaryContainer/secondaryContainer" to (s.onSecondaryContainer to s.secondaryContainer),
        "onTertiaryContainer/tertiaryContainer" to (s.onTertiaryContainer to s.tertiaryContainer),
        "onSurface/surface" to (s.onSurface to s.surface),
        "onSurface/surfaceContainerHighest" to (s.onSurface to s.surfaceContainerHighest),
        "onSurfaceVariant/surface" to (s.onSurfaceVariant to s.surface),
        "onSurfaceVariant/surfaceContainer" to (s.onSurfaceVariant to s.surfaceContainer),
        "onErrorContainer/errorContainer" to (s.onErrorContainer to s.errorContainer),
        "onError/error" to (s.onError to s.error),
        "inverseOnSurface/inverseSurface" to (s.inverseOnSurface to s.inverseSurface),
    )

    @Test
    fun everyPaletteKeepsTextReadableInLightAndDark() {
        for (palette in Palette.presets) {
            for (dark in listOf(false, true)) {
                for ((name, pair) in pairs(palette.colorScheme(dark))) {
                    val ratio = contrast(pair.first, pair.second)
                    assertTrue("${palette.id} dark=$dark $name contrast $ratio", ratio >= 4.5)
                }
            }
        }
    }

    @Test
    fun palettesHaveUniqueIds() {
        val ids = Palette.presets.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
        assertTrue(Palette.WALLPAPER_ID !in ids)
    }

    @Test
    fun unknownPaletteIdFallsBackToViolet() {
        assertEquals("violet", Palette.byId("nope").id)
    }
}
