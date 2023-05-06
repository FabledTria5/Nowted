package dev.fabled.nowted.presentation.ui.screens.folder

import androidx.compose.runtime.Stable
import dev.fabled.nowted.presentation.core.viewmodel.UnidirectionalViewModel
import dev.fabled.nowted.presentation.model.UiNote
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Realization of [UnidirectionalViewModel], that creates contract for folder screen.
 */
interface FolderScreenContract :
    UnidirectionalViewModel<FolderScreenContract.State, FolderScreenContract.Event, FolderScreenContract.Effect> {

    /**
     * Represents state of folder screen
     *
     * @property isLoading indicates, if notes list is loading
     * @property folderName current selected folder name
     * @property isSystemFolder indicates, can user create notes inside this folder
     * @property selectedNoteName current selected note name
     * @property notesList list of notes in folder
     */
    @Stable
    data class State(
        val isLoading: Boolean = true,
        val folderName: String = "",
        val isSystemFolder: Boolean = true,
        val selectedNoteName: String = "",
        val notesList: ImmutableList<UiNote> = persistentListOf()
    )

    sealed class Event {

        /**
         * Collecting data for UI
         */
        object ReadScreenData : Event()

        /**
         * Called when user open note or create a new one
         */
        data class OnNoteClick(val noteName: String = "") : Event()

    }

    sealed class Effect {

        /**
         * Called for navigation to note screen
         */
        data class OpenNote(val noteName: String) : Effect()
    }

}