package dev.fabled.nowted.presentation.ui.navigation.transitions

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.navigator.Navigator

/**
 * Custom slide transition for Voyager library
 * @param navigator [Navigator] to trigger the transition
 * @param modifier [Modifier] to be applied [ScreenTransition]
 * @param orientation defines [SlideOrientation] for transition
 * @param animationSpec defines [FiniteAnimationSpec] for AnimatedContentScope inside
 * [ScreenTransition]
 * @param content current [ScreenTransitionContent] that provides screen content
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SlideTransition(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    orientation: SlideOrientation = SlideOrientation.Horizontal,
    animationSpec: FiniteAnimationSpec<IntOffset> = spring(
        stiffness = Spring.StiffnessMediumLow,
        visibilityThreshold = IntOffset.VisibilityThreshold
    ),
    content: ScreenTransitionContent = { it.Content() }
) {
    ScreenTransition(
        navigator = navigator,
        modifier = modifier,
        content = content,
        transition = {
            val (initialOffset, targetOffset) = when (navigator.lastEvent) {
                StackEvent.Pop -> ({ size: Int -> -size }) to ({ size: Int -> size })
                else -> ({ size: Int -> size }) to ({ size: Int -> -size })
            }

            when (orientation) {
                SlideOrientation.Horizontal -> slideInHorizontally(
                    animationSpec,
                    initialOffset
                ) with slideOutHorizontally(animationSpec, targetOffset)

                SlideOrientation.Vertical -> slideInVertically(
                    animationSpec,
                    initialOffset
                ) with slideOutVertically(animationSpec, targetOffset)
            }
        }
    )
}

/**
 * Enum class, that defines types of [SlideTransition]
 */
enum class SlideOrientation {
    Horizontal,
    Vertical
}