package app.moviedb.designsystem.theme

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A rounded shape whose corners tighten while pressed, with a springy motion.
 * This is the Expressive "shape feedback" on cards.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun pressedCornerShape(
    interactionSource: InteractionSource,
    restingCorner: Dp,
    pressedCorner: Dp = 12.dp,
): Shape {
    val pressed by interactionSource.collectIsPressedAsState()
    val corner by animateDpAsState(
        targetValue = if (pressed) pressedCorner else restingCorner,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        label = "pressedCorner",
    )
    // A spring can overshoot below zero, and a negative radius is invalid.
    return RoundedCornerShape(corner.coerceAtLeast(0.dp))
}
