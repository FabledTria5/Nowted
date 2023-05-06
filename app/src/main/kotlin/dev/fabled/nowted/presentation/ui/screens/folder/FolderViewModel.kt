package dev.fabled.nowted.presentation.ui.screens.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.fabled.nowted.domain.model.SystemFolders
import dev.fabled.nowted.domain.use_cases.common.GetCurrentFolder
import dev.fabled.nowted.domain.use_cases.common.OpenNote
import dev.fabled.nowted.domain.use_cases.notes_list.GetFavoriteNotes
import dev.fabled.nowted.domain.use_cases.notes_list.GetNotesFromCurrentFolder
import dev.fabled.nowted.domain.utils.mapAsync
import dev.fabled.nowted.presentation.mapper.toUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for folder screen
 *
 * @property getCurrentFolder use case, collects current folder
 * @property getFavoriteNotes use case, collects favorite notes
 * @property collectNotes use case, collects notes in folder
 * @property openNote use case, sets new current note
 */
class FolderViewModel(
    private val getCurrentFolder: GetCurrentFolder,
    private val getFavoriteNotes: GetFavoriteNotes,
    private val collectNotes: GetNotesFromCurrentFolder,
    private val openNote: OpenNote
) : ViewModel(), FolderScreenContract {

    private val mutableState = MutableStateFlow(FolderScreenContract.State())
    override val state: StateFlow<FolderScreenContract.State> = mutableState.asStateFlow()

    private val effectFlow = MutableSharedFlow<FolderScreenContract.Effect>()
    override val effect: SharedFlow<FolderScreenContract.Effect> = effectFlow.asSharedFlow()

    override fun onEvent(event: FolderScreenContract.Event) = when (event) {
        FolderScreenContract.Event.ReadScreenData -> readData()
        is FolderScreenContract.Event.OnNoteClick -> setNote(noteName = event.noteName)
    }

    /**
     * Collects current folder name and then performing [flatMapLatest] operation on it to collect
     * all notes inside this folder. If folder is "Favorites", then collecting favorite notes
     *
     * @see getCurrentFolder
     * @see getFavoriteNotes
     * @see collectNotes
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun readData() {
        getCurrentFolder()
            .filter { it.folderName.isNotEmpty() }
            .onEach { folderModel ->
                mutableState.update { state ->
                    state.copy(
                        isLoading = true,
                        folderName = folderModel.folderName,
                        isSystemFolder = folderModel.isSystemFolder
                    )
                }
            }
            .flatMapLatest { folderModel ->
                if (folderModel.folderName == SystemFolders.Favorites.folderName)
                    getFavoriteNotes()
                else
                    collectNotes(folderName = folderModel.folderName)
            }
            .onEach { models ->
                mutableState.update { state ->
                    state.copy(
                        isLoading = false,
                        notesList = models
                            .mapAsync { it.toUiModel() }
                            .toImmutableList()
                    )
                }
            }
            .catch { exception ->
                Timber.e(exception)
            }
            .launchIn(viewModelScope)
    }

    /**
     * Updates current note and sends effect [FolderScreenContract.Effect.OpenNote] to perform
     * navigation to note screen
     *
     * @param noteName next note name
     *
     * @see openNote
     */
    private fun setNote(noteName: String = "") {
        viewModelScope.launch {
            openNote(noteName)

            effectFlow.emit(FolderScreenContract.Effect.OpenNote(noteName))
        }
    }
}