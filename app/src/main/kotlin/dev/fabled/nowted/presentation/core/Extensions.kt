package dev.fabled.nowted.presentation.core

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

suspend fun <T, R> List<T>.mapAsync(transformation: suspend (T) -> R): List<R> = coroutineScope {
    map { async { transformation(it) } }.awaitAll()
}

@Composable
inline fun <reified STATE, EVENT, EFFECT> use(
    viewModel: UnidirectionalViewModel<STATE, EVENT, EFFECT>
): StateDispatchEffect<STATE, EVENT, EFFECT> {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val dispatch: (EVENT) -> Unit = remember {
        { event ->
            viewModel.onEvent(event)
        }
    }

    return StateDispatchEffect(state = state, effectFlow = viewModel.effect, dispatch = dispatch)
}

@SuppressLint("ComposableNaming")
@Composable
fun <T> SharedFlow<T>.collectInLaunchedEffect(block: suspend (value: T) -> Unit) {
    LaunchedEffect(key1 = this) {
        collectLatest(block)
    }
}

data class StateDispatchEffect<STATE, EVENT, EFFECT>(
    val state: STATE,
    val dispatch: (EVENT) -> Unit,
    val effectFlow: SharedFlow<EFFECT>
)