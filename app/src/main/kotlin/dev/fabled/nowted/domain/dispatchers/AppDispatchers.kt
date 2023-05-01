package dev.fabled.nowted.domain.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

interface AppDispatchers {

    val defaultDispatcher: CoroutineDispatcher

    val ioDispatcher: CoroutineDispatcher

    val mainDispatcher: CoroutineDispatcher

}