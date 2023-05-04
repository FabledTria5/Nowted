package dev.fabled.nowted.presentation.core

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

/**
 * Performs replace operation, if [condition] returns true
 *
 * @param screen target screen
 * @param condition condition to perform operation
 */
fun Navigator.distinctReplace(screen: Screen, condition: Navigator.() -> Boolean) {
    if (condition()) replace(screen)
}

/**
 * Draws stroke border for the component
 *
 * @param color border color
 * @param width width of the border
 * @param cornerRadius corner radius for components with custom shape
 */
fun Modifier.strokeBorder(
    color: Color,
    width: Float,
    cornerRadius: Dp? = null
) = this then drawBehind {
    val stroke = Stroke(
        width = width,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(10f, 10f),
            phase = 0f
        )
    )

    drawRoundRect(
        color = color,
        style = stroke,
        cornerRadius = cornerRadius?.let { CornerRadius(cornerRadius.toPx()) } ?: CornerRadius.Zero
    )
}