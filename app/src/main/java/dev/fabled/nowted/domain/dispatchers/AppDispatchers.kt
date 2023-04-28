package dev.fabled.nowted.domain.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

interface AppDispatchers {

    val dispatcherDefault: CoroutineDispatcher

    val dispatcherIO: CoroutineDispatcher

    val dispatcherMain: CoroutineDispatcher

}