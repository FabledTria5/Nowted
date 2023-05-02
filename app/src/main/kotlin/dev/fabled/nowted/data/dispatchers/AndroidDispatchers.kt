package dev.fabled.nowted.data.dispatchers

import dev.fabled.nowted.domain.dispatchers.AppDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AndroidDispatchers : AppDispatchers {

    override val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    override val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
}