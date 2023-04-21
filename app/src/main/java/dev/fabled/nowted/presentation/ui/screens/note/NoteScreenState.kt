package dev.fabled.nowted.presentation.ui.screens.note

import androidx.compose.runtime.Stable
import dev.fabled.nowted.presentation.model.UiNote

@Stable
data class NoteScreenState(
    val isNoteOpened: Boolean = false,
    val note: UiNote = UiNote(),
    val deletedNoteName: String = "",
    val deletedNoteFolderName: String = ""
)
