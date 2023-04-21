package dev.fabled.nowted.presentation.ui.navigation

import cafe.adriel.voyager.core.screen.Screen

sealed class NavigationCommand {

    object NavigateBack : NavigationCommand()

    data class Navigate(val screen: Screen) : NavigationCommand()

    data class Replace(val screen: Screen): NavigationCommand()

}