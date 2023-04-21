package dev.fabled.nowted.presentation.utils

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

fun Navigator.replaceIf(destination: Screen, block: () -> Boolean) {
    if (block())
        replace(destination)
}