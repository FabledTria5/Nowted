package dev.fabled.nowted.presentation.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.fabled.nowted.presentation.core.WindowSize

data class VerticalPaddings(
    val normalPadding: Dp = 0.dp,
    val mediumPadding: Dp = 0.dp
)

val LocalPaddings = compositionLocalOf { VerticalPaddings() }

val LocalWindowSize = compositionLocalOf { WindowSize() }