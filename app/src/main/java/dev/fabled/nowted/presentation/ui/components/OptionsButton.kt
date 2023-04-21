package dev.fabled.nowted.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun OptionsButton(
    modifier: Modifier,
    onClick: () -> Unit,
    isClicked: Boolean,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.White.copy(alpha = .5f)
) {
    val buttonColor by animateColorAsState(
        targetValue = if (isClicked) activeColor else inactiveColor,
        animationSpec = tween(durationMillis = 250),
        label = "button_color_animation"
    )

    Canvas(
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick() }
    ) {
        drawCircle(color = buttonColor, style = Stroke(width = 5f))
        drawCircle(
            color = buttonColor,
            radius = 5f,
            center = Offset(x = center.x - 15f, y = center.y)
        )
        drawCircle(
            color = buttonColor,
            radius = 5f,
        )
        drawCircle(
            color = buttonColor,
            radius = 5f,
            center = Offset(x = center.x + 15f, y = center.y)
        )
    }
}