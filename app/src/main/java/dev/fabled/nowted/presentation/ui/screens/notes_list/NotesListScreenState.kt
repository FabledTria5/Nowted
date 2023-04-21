package dev.fabled.nowted.presentation.ui.screens.notes_list

import dev.fabled.nowted.presentation.model.UiNote
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class NotesListScreenState(
    val folderName: String = "",
    val selectedNoteName: String = "",
    val notesList: ImmutableList<UiNote> = persistentListOf()
)
