package dev.fabled.nowted.presentation.ui.screens.note

import androidx.compose.runtime.Stable
import dev.fabled.nowted.presentation.model.UiNote

@Stable
data class NoteScreenState(
    val contentState: NoteScreenContentState = NoteScreenContentState.NOTE_NOT_SELECTED,
    val note: UiNote = UiNote(),
    val deletedNoteName: String = "",
    val deletedNoteFolderName: String = ""
)

@Stable
enum class NoteScreenContentState {
    NOTE_NOT_SELECTED, NOTE_OPENED, NOTE_RESTORING
}
