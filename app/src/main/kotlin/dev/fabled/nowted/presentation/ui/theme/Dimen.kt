package dev.fabled.nowted.presentation.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Defines vertical paddings for app
 *
 * @property normalPadding defines paddings for home screen and notes list screen
 * @property mediumPadding defines paddings for note screen
 */
data class VerticalPaddings(
    val normalPadding: Dp = 0.dp,
    val mediumPadding: Dp = 0.dp
)

/**
 * Creates [compositionLocalOf] from [VerticalPaddings]
 */
val LocalPaddings = compositionLocalOf { VerticalPaddings() }