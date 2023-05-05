package dev.fabled.nowted.domain.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Coroutine dispatchers
 */
interface AppDispatchers {

    /**
     * Default Dispatcher
     */
    val defaultDispatcher: CoroutineDispatcher

    /**
     * IO Dispatcher
     */
    val ioDispatcher: CoroutineDispatcher

    /**
     * Main Dispatcher
     */
    val mainDispatcher: CoroutineDispatcher

}