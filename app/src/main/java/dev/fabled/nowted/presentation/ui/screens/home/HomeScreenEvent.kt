package dev.fabled.nowted.presentation.ui.screens.home

import androidx.compose.runtime.Stable

@Stable
sealed class HomeScreenEvent {

    data class CreateFolder(val folderName: String) : HomeScreenEvent()

    data class OpenFolder(val folderName: String) : HomeScreenEvent()

    data class OpenRecent(val name: String): HomeScreenEvent()

    object NewNote : HomeScreenEvent()

    object OnStartCreateNewFolder : HomeScreenEvent()

}