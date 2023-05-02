package dev.fabled.nowted.presentation.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Class to define current window size. Provides [width] and [height] members to let user decide,
 * which one of them use to build different UI.
 */
data class WindowSize(
    val width: WindowType,
    val height: WindowType
)

/**
 * Defines possible window types.
 */
enum class WindowType { Compact, Medium, Expanded }

/**
 * An Effect to calculate [WindowSize] when [LocalConfiguration] changes. This can be used to create
 * screen UI, that changes automatically on different screen sizes.
 */
@Composable
fun rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current

    rememberCompositionContext()

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

/**
 * Calculates [WindowType] based on provided screen width.
 * @param screenWidthDp current screen width, calculated from [LocalConfiguration]
 */
fun getScreenWidth(screenWidthDp: Int): WindowType = when {
    screenWidthDp < 600 -> WindowType.Compact
    screenWidthDp < 800 -> WindowType.Medium
    else -> WindowType.Expanded
}

/**
 * Calculates [WindowType] based on provided screen height.
 * @param screenHeightDp current screen height, calculated from [LocalConfiguration]
 */
fun getScreenHeight(screenHeightDp: Int): WindowType = when {
    screenHeightDp < 480 -> WindowType.Compact
    screenHeightDp < 900 -> WindowType.Medium
    else -> WindowType.Expanded
}
