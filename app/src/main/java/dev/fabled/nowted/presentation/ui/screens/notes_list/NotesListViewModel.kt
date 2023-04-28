package dev.fabled.nowted.presentation.ui.screens.notes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.fabled.nowted.domain.use_cases.common.GetCurrentFolderName
import dev.fabled.nowted.domain.use_cases.common.OpenNote
import dev.fabled.nowted.domain.use_cases.notes_list.GetNotesFromCurrentFolder
import dev.fabled.nowted.presentation.core.mapAsync
import dev.fabled.nowted.presentation.mapper.toUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class NotesListViewModel(
    private val getCurrentFolderName: GetCurrentFolderName,
    private val collectNotes: GetNotesFromCurrentFolder,
    private val openNote: OpenNote
) : ViewModel(), NotesListScreenContract {

    private val mutableState = MutableStateFlow(NotesListScreenContract.State())
    override val state: StateFlow<NotesListScreenContract.State> = mutableState.asStateFlow()

    private val effectFlow = MutableSharedFlow<NotesListScreenContract.Effect>()
    override val effect: SharedFlow<NotesListScreenContract.Effect> = effectFlow.asSharedFlow()

    override fun onEvent(event: NotesListScreenContract.Event) = when (event) {
        NotesListScreenContract.Event.OnReadNotesFromFolder -> readNotesFromFolder()
        NotesListScreenContract.Event.OnCreateNote -> setNote()
        is NotesListScreenContract.Event.OnNoteClick -> setNote(noteName = event.noteName)
    }

    private fun readNotesFromFolder() {
        combine(
            flow = getCurrentFolderName(),
            flow2 = collectNotes()
        ) { currentFolder, notesList ->
            mutableState.update { state ->
                state.copy(
                    folderName = currentFolder,
                    notesList = notesList
                        .mapAsync { model -> model.toUiModel() }
                        .toImmutableList()
                )
            }
        }
            .catch { exception ->
                Timber.e(exception)
            }
            .launchIn(viewModelScope)
    }

    private fun setNote(noteName: String = "") {
        viewModelScope.launch {
            openNote(noteName)
        }
    }

}