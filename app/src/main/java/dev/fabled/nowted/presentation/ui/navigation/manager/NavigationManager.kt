package dev.fabled.nowted.presentation.ui.navigation.manager

import dev.fabled.nowted.presentation.ui.navigation.NavigationCommand
import kotlinx.coroutines.flow.SharedFlow

interface NavigationManager {

    val navigationCommand: SharedFlow<NavigationCommand>

    fun navigate(navigationCommand: NavigationCommand)

}