package dev.fabled.nowted.presentation.ui.screens.restore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.fabled.nowted.domain.use_cases.note.RestoreNote
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RestoreViewModel(private val restoreNoteCase: RestoreNote) : ViewModel(),
    RestoreScreenContract {

    private val mutableState = MutableStateFlow(RestoreScreenContract.State())
    override val state: StateFlow<RestoreScreenContract.State> = mutableState.asStateFlow()

    private val effectFlow = MutableSharedFlow<RestoreScreenContract.Effect>()
    override val effect: SharedFlow<RestoreScreenContract.Effect> = effectFlow.asSharedFlow()

    override fun onEvent(event: RestoreScreenContract.Event) = when (event) {
        RestoreScreenContract.Event.RestoreNote -> restoreNote()
        is RestoreScreenContract.Event.SetData -> mutableState.update { state ->
            state.copy(
                deletedNoteName = event.deletedNoteName,
                noteFolderName = event.noteFolderName
            )
        }
    }

    private fun restoreNote() {
        viewModelScope.launch {
            restoreNoteCase(
                noteName = state.value.deletedNoteName,
                noteFolder = state.value.noteFolderName
            )
            effectFlow.emit(RestoreScreenContract.Effect.NoteRestored)
        }
    }
}