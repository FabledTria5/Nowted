package dev.fabled.nowted.presentation.ui.screens.notes_list

import dev.fabled.nowted.presentation.core.UnidirectionalViewModel
import dev.fabled.nowted.presentation.model.UiNote
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

interface NotesListScreenContract :
    UnidirectionalViewModel<NotesListScreenContract.State, NotesListScreenContract.Event, NotesListScreenContract.Effect> {

    data class State(
        val folderName: String = "",
        val selectedNoteName: String = "",
        val notesList: ImmutableList<UiNote> = persistentListOf()
    )

    sealed class Event {
        object ReadScreenData : Event()

        data class OnNoteClick(val noteName: String) : Event()

        object OnCreateNote : Event()
    }

    object Effect

}