package dev.fabled.nowted.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview

data class WindowSize(
    val width: WindowType,
    val height: WindowType
)

enum class WindowType { Compact, Medium, Expanded }

@Composable
fun rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current

    val screenWidthDp by remember(configuration) {
        mutableStateOf(configuration.screenWidthDp)
    }

    val screenHeightDp by remember(configuration) {
        mutableStateOf(configuration.screenHeightDp)
    }

    return WindowSize(
        width = getScreenWidth(screenWidthDp),
        height = getScreenHeight(screenHeightDp)
    )
}

fun getScreenWidth(screenWidthDp: Int): WindowType = when {
    screenWidthDp < 600 -> WindowType.Compact
    screenWidthDp < 800 -> WindowType.Medium
    else -> WindowType.Expanded
}

fun getScreenHeight(screenHeightDp: Int): WindowType = when {
    screenHeightDp < 480 -> WindowType.Compact
    screenHeightDp < 900 -> WindowType.Medium
    else -> WindowType.Expanded
}
