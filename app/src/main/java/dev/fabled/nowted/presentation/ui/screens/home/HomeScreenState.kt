package dev.fabled.nowted.presentation.ui.screens.home

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class HomeScreenState(
    val primaryFolders: ImmutableList<String> = persistentListOf(),
    val additionalFolders: ImmutableList<String> = persistentListOf(),
    val selectedFolder: String = "",
    val selectedNoteName: String = "",
    val isCreatingFolder: Boolean = false
)
