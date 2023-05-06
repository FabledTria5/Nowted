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

/**
 * ViewModel for home screen
 *
 * @property collectRecents collects recent notes
 * @property collectFolders collects folders
 * @property createNewFolder creates new folder
 * @property openNote sets new current note
 * @property collectCurrentFolder collects current folder
 * @property collectCurrentNoteName collects current note name
 */
class HomeViewModel(
    private val collectRecents: CollectRecents,
    private val collectFolders: CollectFolders,
    private val createNewFolder: CreateFolder,
    private val openFolder: OpenFolder,
    private val openNote: OpenNote,
    private val collectCurrentFolder: GetCurrentFolder,
    private val collectCurrentNoteName: GetCurrentNoteName,
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

    /**
     * Collects current folder and current note name
     */
    private fun getSelectedItems() {
        collectCurrentFolder()
            .catch { exception ->
                Timber.e(exception)
            }
            .onEach { folderModel ->
                mutableState.update { state ->
                    state.copy(selectedFolderName = folderModel.folderName)
                }
            }
            .launchIn(viewModelScope)

        collectCurrentNoteName()
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

    /**
     * Collects recent notes
     */
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

    /**
     * Collects folders
     */
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

    /**
     * Initialize folder creating process
     */
    private fun startFolderCreation() = mutableState.update { state ->
        state.copy(isCreatingFolder = true)
    }

    /**
     * Creates new folder and finishes folder creating process. After process is finished, emits
     * [HomeScreenContract.Effect.FolderCreated]
     *
     * @param folderName creates new folder with given name
     *
     * @see createNewFolder
     */
    private fun createFolder(folderName: String) {
        if (folderName.isBlank()) return

        viewModelScope.launch {
            mutableState.update { state ->
                state.copy(isCreatingFolder = false)
            }

            createNewFolder(folderName = folderName)
            effectFlow.emit(HomeScreenContract.Effect.FolderCreated)
        }
    }

    /**
     * Selects new folder and emits [HomeScreenContract.Effect.OpenFolder]
     *
     * @param folderName name of folder to be selected
     *
     * @see openFolder
     */
    private fun selectFolder(folderName: String) {
        viewModelScope.launch {
            openFolder(folderName = folderName)

            effectFlow.emit(HomeScreenContract.Effect.OpenFolder)
        }
    }

    /**
     * Updates current note and sends effect [HomeScreenContract.Effect.OpenNote] to perform
     * navigation to note screen
     *
     * @param noteName next note name
     *
     * @see openNote
     */
    private fun setNote(noteName: String = "") {
        viewModelScope.launch {
            openNote(noteName = noteName)

            effectFlow.emit(HomeScreenContract.Effect.OpenNote(noteName))
        }
    }
}