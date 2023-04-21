package dev.fabled.nowted.presentation.ui.navigation.manager

import dev.fabled.nowted.presentation.ui.navigation.NavigationCommand
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NavigationManagerImpl : NavigationManager {

    private val _navigationCommand = MutableSharedFlow<NavigationCommand>(replay = 1)
    override val navigationCommand = _navigationCommand.asSharedFlow()

    override fun navigate(navigationCommand: NavigationCommand) {
        _navigationCommand.tryEmit(navigationCommand)
    }

}