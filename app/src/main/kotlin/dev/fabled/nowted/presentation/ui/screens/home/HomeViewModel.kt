package dev.fabled.nowted.presentation.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.fabled.nowted.domain.use_cases.common.GetCurrentFolder
import dev.fabled.nowted.domain.use_cases.common.GetCurrentNoteName
import dev.fabled.nowted.domain.use_cases.common.OpenNote
import dev.fabled.nowted.domain.use_cases.home.CollectFolders
import dev.fabled.nowted.domain.use_cases.home.CollectRecents
import dev.fabled.nowted.domain.use_cases.home.CreateFolder
import dev.fabled.nowted.domain.use_cases.home.OpenFolder
import dev.fabled.nowted.domain.utils.mapAsync
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
    private val getCurrentFolder: GetCurrentFolder,
    private val getCurrentNoteName: GetCurrentNoteName,
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
        is HomeScreenContract.Event.CreateFolder -> createFolder(event.folderName)
        is HomeScreenContract.Event.OpenFolder -> selectFolder(event.folderName)
        is HomeScreenContract.Event.OpenNote -> setNote(event.noteName)
    }

    private fun getSelectedItems() {
        getCurrentFolder()
            .catch { exception ->
                Timber.e(exception)
            }
            .onEach { folderModel ->
                mutableState.update { state ->
                    state.copy(selectedFolderName = folderModel.folderName)
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

    private fun startFolderCreation() = mutableState.update { state ->
        state.copy(isCreatingFolder = true)
    }

    private fun createFolder(folderName: String) {
        viewModelScope.launch {
            mutableState.update { state ->
                state.copy(isCreatingFolder = false)
            }

            createNewFolder(folderName = folderName)
            effectFlow.emit(HomeScreenContract.Effect.FolderCreated)
        }
    }

    private fun selectFolder(folderName: String) {
        viewModelScope.launch {
            openFolder(folderName = folderName)

            effectFlow.emit(HomeScreenContract.Effect.OpenFolder)
        }
    }

    private fun setNote(noteName: String = "") {
        viewModelScope.launch {
            openNote(noteName = noteName)

            effectFlow.emit(HomeScreenContract.Effect.OpenNote(noteName))
        }
    }
}