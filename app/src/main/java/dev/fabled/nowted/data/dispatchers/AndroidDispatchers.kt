package dev.fabled.nowted.data.dispatchers

import dev.fabled.nowted.domain.dispatchers.AppDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AndroidDispatchers : AppDispatchers {

    override val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default

    override val dispatcherIO: CoroutineDispatcher = Dispatchers.IO

    override val dispatcherMain: CoroutineDispatcher = Dispatchers.Main
}