package dev.fabled.nowted.presentation.ui.screens.note

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.TextUnit
import dev.fabled.nowted.presentation.core.UnidirectionalViewModel
import dev.fabled.nowted.presentation.model.UiNote

interface NoteScreenContract :
    UnidirectionalViewModel<NoteScreenContract.State, NoteScreenContract.Event, NoteScreenContract.Effect> {

    @Stable
    data class State(
        val contentState: NoteScreenContentState = NoteScreenContentState.NOTE_NOT_SELECTED,
        val note: UiNote = UiNote(),
        val deletedNoteName: String = "",
        val deletedNoteFolderName: String = ""
    ) {
        @Stable
        enum class NoteScreenContentState {
            NOTE_NOT_SELECTED, NOTE_OPENED, NOTE_RESTORING
        }
    }

    sealed class Event {
        data class NoteTitleChanged(val newTitle: String) : Event()

        data class NoteTextChanged(val newText: String) : Event()

        data class NoteParagraphChanged(val newValue: TextUnit) : Event()

        data class NoteTextSizeChanged(val textSize: TextUnit) : Event()

        object CollectCurrentNote : Event()

        object RestoreNote : Event()

        object ToggleTextWeight : Event()

        object ToggleTextStyle : Event()

        object ToggleTextDecoration : Event()

        object DeleteNote : Event()

        object ToggleFavorite : Event()

        object SaveNote : Event()

        object ArchiveNote : Event()
    }

    sealed class Effect {
        object NoteSaved : Effect()

        object NoteSaveError : Effect()

        object AddedToFavorite : Effect()

        object FavoriteFailure : Effect()

        object NoteRemoved : Effect()

        object NoteRestored : Effect()

        object NoteDeleted : Effect()

        object NoteDeleteError : Effect()
    }


}