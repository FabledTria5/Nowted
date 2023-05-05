package dev.fabled.nowted.data.dispatchers

import dev.fabled.nowted.domain.dispatchers.AppDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Implementation of [AppDispatchers] for android
 */
class AndroidDispatchers : AppDispatchers {

    /**
     * Implementation of [defaultDispatcher]
     */
    override val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    /**
     * Implementation of [ioDispatcher]
     */
    override val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    /**
     * Implementation of [mainDispatcher]. This dispatcher is available only in Android coroutines
     * library
     */
    override val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
}