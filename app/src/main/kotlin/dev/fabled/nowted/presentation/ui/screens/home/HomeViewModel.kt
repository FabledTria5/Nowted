package dev.fabled.nowted.presentation.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.fabled.nowted.domain.use_cases.common.GetCurrentFolderName
import dev.fabled.nowted.domain.use_cases.common.GetCurrentNoteName
import dev.fabled.nowted.domain.use_cases.common.OpenNote
import dev.fabled.nowted.domain.use_cases.home.CollectFolders
import dev.fabled.nowted.domain.use_cases.home.CollectRecents
import dev.fabled.nowted.domain.use_cases.home.CreateFolder
import dev.fabled.nowted.domain.use_cases.home.OpenFolder
import dev.fabled.nowted.presentation.core.mapAsync
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val collectRecents: CollectRecents,
    private val collectFolders: CollectFolders,
    private val createNewFolder: CreateFolder,
    private val openFolder: OpenFolder,
    private val openNote: OpenNote,
    private val getCurrentFolderName: GetCurrentFolderName,
    private val getCurrentNoteName: GetCurrentNoteName
) : ViewModel(), HomeScreenContract {

    private val mutableState = MutableStateFlow(HomeScreenContract.State())
    override val state: StateFlow<HomeScreenContract.State> = mutableState.asStateFlow()

    private val effectFlow = MutableSharedFlow<HomeScreenContract.Effect>()
    override val effect: SharedFlow<HomeScreenContract.Effect> = effectFlow.asSharedFlow()

    override fun onEvent(event: HomeScreenContract.Event) = when (event) {
        HomeScreenContract.Event.GetSelections -> getSelectedItems()
        HomeScreenContract.Event.GetRecents -> getRecents()
        HomeScreenContract.Event.GetFolders -> getFolders()
        HomeScreenContract.Event.OnStartCreateNewFolder -> startFolderCreation()
        HomeScreenContract.Event.ToggleSearch -> toggleSearch()
        is HomeScreenContract.Event.UpdateSearchQuery -> updateSearchQuery(event.query)
        is HomeScreenContract.Event.CreateFolder -> createFolder(event.folderName)
        is HomeScreenContract.Event.FavoriteNotesSelected -> getFavoriteNotes()
        is HomeScreenContract.Event.OpenFolder -> selectFolder(event.folderName)
        is HomeScreenContract.Event.OpenNote -> setNote(event.noteName)
    }

    private fun getSelectedItems() {
        getCurrentFolderName()
            .catch { exception ->
                Timber.e(exception)
            }
            .onEach { folderName ->
                mutableState.update { state ->
                    state.copy(selectedFolderName = folderName)
                }
            }
            .launchIn(viewModelScope)

        getCurrentNoteName()
            .catch { exception ->
                Timber.e(exception)
            }
            .onEach { noteName ->
                mutableState.update { state ->
                    state.copy(selectedNoteName = noteName)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getRecents() {
        collectRecents()
            .catch { exception ->
                Timber.e(exception)
            }
            .onEach { recents ->
                mutableState.update { state ->
                    state.copy(recentNotes = recents.toImmutableList())
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getFolders() {
        collectFolders()
            .catch { exception ->
                Timber.e(exception)
            }
            .onEach { modelsList ->
                if (state.value.selectedFolderName.isEmpty())
                    openFolder(folderName = modelsList.first().folderName)

                mutableState.update { state ->
                    state.copy(
                        folders = modelsList
                            .mapAsync { it.folderName }
                            .toImmutableList(),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun updateSearchQuery(newQuery: String) = mutableState.update { state ->
        state.copy(searchQuery = newQuery)
    }

    private fun toggleSearch() = mutableState.update { state ->
        state.copy(isSearching = !state.isSearching)
    }

    private fun startFolderCreation() = mutableState.update { state ->
        state.copy(isCreatingFolder = true)
    }

    private fun createFolder(folderName: String) {
        viewModelScope.launch {
            launch {
                mutableState.update { state ->
                    state.copy(isCreatingFolder = false)
                }
            }

            launch {
                createNewFolder(folderName = folderName)
            }
        }
    }

    private fun selectFolder(folderName: String) {
        openFolder(folderName = folderName)
    }

    private fun setNote(noteName: String = "") {
        openNote(noteName = noteName)
    }

    private fun getFavoriteNotes() {

    }
}