package dev.fabled.nowted.presentation.ui.screens.home

import dev.fabled.nowted.presentation.core.UnidirectionalViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

interface HomeScreenContract :
    UnidirectionalViewModel<HomeScreenContract.State, HomeScreenContract.Event, HomeScreenContract.Effect> {

    data class State(
        val recentNotes: ImmutableList<String> = persistentListOf(),
        val primaryFolders: ImmutableList<String> = persistentListOf(),
        val additionalFolders: ImmutableList<String> = persistentListOf(),
        val selectedFolderName: String = "",
        val selectedNoteName: String = "",
        val isCreatingFolder: Boolean = false,
        val isSearching: Boolean = false,
        val searchQuery: String = ""
    )

    sealed class Event {
        data class CreateFolder(val folderName: String) : Event()

        data class SelectFolder(val folderName: String) : Event()

        data class OpenNote(val noteName: String = "") : Event()

        data class UpdateSearchQuery(val query: String) : Event()

        data class FavoriteNotesSelected(val folderName: String) : Event()

        object GetSelections : Event()

        object GetRecents : Event()

        object GetFolders : Event()

        object ToggleSearch : Event()

        object NewNote : Event()

        object OnStartCreateNewFolder : Event()
    }

    sealed class Effect {
        object FolderCreated : Effect()
    }

}