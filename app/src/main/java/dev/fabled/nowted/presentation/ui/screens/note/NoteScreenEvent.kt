package dev.fabled.nowted.presentation.ui.screens.note

import androidx.compose.ui.unit.TextUnit

sealed class NoteScreenEvent {

    data class NoteTitleChanged(val newTitle: String) : NoteScreenEvent()

    data class NoteTextChanged(val newText: String) : NoteScreenEvent()

    data class NoteParagraphChanged(val newValue: TextUnit) : NoteScreenEvent()

    data class NoteTextSizeChanged(val textSize: TextUnit) : NoteScreenEvent()

    object RestoreNote : NoteScreenEvent()

    object ToggleTextWeight : NoteScreenEvent()

    object ToggleTextStyle : NoteScreenEvent()

    object ToggleTextDecoration : NoteScreenEvent()

    object DeleteNote : NoteScreenEvent()

    object ToggleFavorite : NoteScreenEvent()

    object SaveNote : NoteScreenEvent()

    object ArchiveNote : NoteScreenEvent()

}
