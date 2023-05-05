package dev.fabled.nowted.presentation.core.viewmodel

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for ViewModel contracts
 *
 * @property state represents screen state with generic type
 * @property effect represents [SharedFlow] of effects to send events from ViewModel to screen
 */
interface UnidirectionalViewModel<STATE, EVENT, EFFECT> {
    val state: StateFlow<STATE>
    val effect: SharedFlow<EFFECT>
    fun onEvent(event: EVENT)
}