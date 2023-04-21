package dev.fabled.nowted.presentation.ui.screens.notes_list

sealed class NotesListScreenEvent {

    data class OnNoteClick(val noteName: String) : NotesListScreenEvent()

    object OnCreateFirstNote : NotesListScreenEvent()

}
