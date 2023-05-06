package dev.fabled.nowted.presentation.ui.screens.note

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.TextUnit
import dev.fabled.nowted.presentation.core.viewmodel.UnidirectionalViewModel
import dev.fabled.nowted.presentation.model.UiNote

/**
 * Realization of [UnidirectionalViewModel], that creates contract for note screen
 */
interface NoteScreenContract :
    UnidirectionalViewModel<NoteScreenContract.State, NoteScreenContract.Event, NoteScreenContract.Effect> {

    /**
     * Represents state of note screen
     *
     * @property note current note
     */
    @Stable
    data class State(
        val note: UiNote = UiNote()
    )

    sealed class Event {

        /**
         * Called when user updates note title
         *
         * @param newTitle new note title
         */
        data class NoteTitleChanged(val newTitle: String) : Event()

        /**
         * Called when user changes note text
         *
         * @param newText new note text
         */
        data class NoteTextChanged(val newText: String) : Event()

        /**
         * Called when user changes note paragraph
         *
         * @param newValue new paragraph size
         */
        data class NoteParagraphChanged(val newValue: TextUnit) : Event()

        /**
         * Called when user changes note text size
         *
         * @param textSize new text size
         */
        data class NoteTextSizeChanged(val textSize: TextUnit) : Event()

        /**
         * Collecting current note
         */
        object CollectCurrentNote : Event()

        /**
         * Toggles text weight between normal and bold
         */
        object ToggleTextWeight : Event()

        /**
         * Toggles text style between normal and italic
         */
        object ToggleTextStyle : Event()

        /**
         * Toggles text decoration between none and underline
         */
        object ToggleTextDecoration : Event()

        /**
         * Called when user intends to delete note
         */
        object DeleteNote : Event()

        /**
         * Called when user wants to change favorite state of note
         */
        object ToggleFavorite : Event()

        /**
         * Called when user intends to save note
         */
        object SaveNote : Event()

        /**
         * Called when user intends to put note to archive
         */
        object ArchiveNote : Event()
    }

    sealed class Effect {

        /**
         * Should be called after saving note success
         */
        object NoteSaved : Effect()

        /**
         * Should be called, when note is added to trash
         *
         * @property noteName name of deleted note
         * @property noteFolder name of previous note folder
         */
        data class AddedToTrash(val noteName: String, val noteFolder: String) : Effect()

        /**
         * Should be called after error while saving note
         */
        object NoteSaveError : Effect()

        /**
         * Should be called after adding note to favorites
         */
        object AddedToFavorite : Effect()

        /**
         * SHold be called after adding note to archive
         */
        object Archived : Effect()

        /**
         * Should be called after deleting note
         */
        object NoteDeleted : Effect()

    }


}