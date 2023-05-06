package dev.fabled.nowted.presentation.ui.screens.home

import androidx.compose.runtime.Stable
import dev.fabled.nowted.presentation.core.viewmodel.UnidirectionalViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Realization of [UnidirectionalViewModel], that creates contract for home screen.
 */
interface HomeScreenContract :
    UnidirectionalViewModel<HomeScreenContract.State, HomeScreenContract.Event, HomeScreenContract.Effect> {

    /**
     * Represents state of home screen.
     *
     * @property recentNotes list of recent notes. Never can contain more than 3 elements.
     * Represented by [ImmutableList] to guarantee compose, that list is stable
     *
     * @property folders list of primary folders. Represented by [ImmutableList] to
     * guarantee compose, that list is stable
     *
     * @property selectedFolderName used to highlight folder, that matches this name
     * @property selectedFolderName used to highlight note, that has the same name
     * @property isCreatingFolder used to show additional item in folders list. This item is
     * used only when user creating new folder and should not be visible other time
     *
     * @property isSearching used to determine, what element will be shown in top of the screen.
     * If true, than text field should be shown, else button to create new note.
     *
     * @property searchQuery used to store search query
     */
    @Stable
    data class State(
        val recentNotes: ImmutableList<String> = persistentListOf(),
        val folders: ImmutableList<String> = persistentListOf(),
        val selectedFolderName: String = "",
        val selectedNoteName: String = "",
        val isCreatingFolder: Boolean = false,
        val isSearching: Boolean = false,
        val searchQuery: String = ""
    )

    sealed class Event {
        /**
         * Called, when user presses the button to create new folder.
         *
         * @property folderName name of target folder
         */
        data class CreateFolder(val folderName: String) : Event()

        /**
         * Called, when user intends to open folder.
         *
         * @property folderName name of folder, that should be opened
         */
        data class OpenFolder(val folderName: String) : Event()

        /**
         * Called when user intends to open note.
         *
         * @property noteName name of target note. Empty note name indicates, that user wants
         * to create a new note. In this case the note will be created inside selected folder.
         */
        data class OpenNote(val noteName: String = "") : Event()

        /**
         * Called initially to observe selected folder name and selected note names
         *
         * @see State.selectedNoteName
         * @see State.selectedFolderName
         */
        object GetSelections : Event()

        /**
         * Called initially to observe recent notes.
         *
         * @see State.recentNotes
         */
        object GetRecents : Event()

        /**
         * Called initially to observe primary folders.
         *
         * @see State.folders
         */
        object GetFolders : Event()

        /**
         * Called when user presses new folder button. Not creating a new folder, but shows item
         * with text field to enter folder name.
         *
         * @see CreateFolder
         */
        object OnStartCreateNewFolder : Event()
    }

    sealed class Effect {
        /**
         * Should be called, when user creates new folder
         */
        object FolderCreated : Effect()

        /**
         * Should be called, when user wants to navigate to note screen
         *
         * @property noteName used as key for NoteScreen
         */
        data class OpenNote(val noteName: String) : Effect()

        /**
         * Should be called when user select folder
         */
        object OpenFolder : Effect()
    }

}