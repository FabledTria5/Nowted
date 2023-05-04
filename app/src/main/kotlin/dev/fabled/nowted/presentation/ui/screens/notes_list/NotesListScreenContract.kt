package dev.fabled.nowted.presentation.ui.screens.notes_list

import androidx.compose.runtime.Stable
import dev.fabled.nowted.presentation.core.viewmodel.UnidirectionalViewModel
import dev.fabled.nowted.presentation.model.UiNote
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

interface NotesListScreenContract :
    UnidirectionalViewModel<NotesListScreenContract.State, NotesListScreenContract.Event, NotesListScreenContract.Effect> {

    @Stable
    data class State(
        val isLoading: Boolean = true,
        val folderName: String = "",
        val isSystemFolder: Boolean = true,
        val selectedNoteName: String = "",
        val notesList: ImmutableList<UiNote> = persistentListOf()
    )

    sealed class Event {

        @Stable
        object ReadScreenData : Event()

        @Stable
        data class OnNoteClick(val noteName: String) : Event()

        @Stable
        object OnCreateNote : Event()
    }

    sealed class Effect {
        @Stable
        data class OpenNote(val noteName: String) : Effect()
    }

}