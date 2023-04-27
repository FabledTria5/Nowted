package dev.fabled.nowted.presentation.ui.navigation.transitions

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

typealias ScreenTransitionContent = @Composable AnimatedVisibilityScope.(Screen) -> Unit

/**
 * Defines transition for given [Screen]
 * @param navigator voyager navigator is used to trigger animations
 * @param transition defines screens transition. Now has only slide transition
 * @param modifier a [Modifier] to be applied [AnimatedContent]
 * @param content current [Screen] content
 *
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ScreenTransition(
    navigator: Navigator,
    transition: AnimatedContentScope<Screen>.() -> ContentTransform,
    modifier: Modifier = Modifier,
    content: ScreenTransitionContent = { it.Content() }
) {
    AnimatedContent(
        targetState = navigator.lastItem,
        transitionSpec = transition,
        modifier = modifier,
        label = "screen_transition"
    ) { screen ->
        navigator.saveableState(key = "transition", screen = screen) {
            content(screen)
        }
    }
}