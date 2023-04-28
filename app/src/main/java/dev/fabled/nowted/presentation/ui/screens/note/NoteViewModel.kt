package dev.fabled.nowted.presentation.ui.screens.note

import androidx.compose.ui.unit.TextUnit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.fabled.nowted.domain.model.Resource
import dev.fabled.nowted.domain.use_cases.note.ChangeNoteFavoriteState
import dev.fabled.nowted.domain.use_cases.note.GetCurrentNote
import dev.fabled.nowted.domain.use_cases.note.RemoveNote
import dev.fabled.nowted.domain.use_cases.note.RestoreNote
import dev.fabled.nowted.domain.use_cases.note.UpdateOrCreateNote
import dev.fabled.nowted.presentation.mapper.toModel
import dev.fabled.nowted.presentation.mapper.toUiModel
import dev.fabled.nowted.presentation.model.UiNote
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreenContract.State.NoteScreenContentState
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

class NoteViewModel(
    private val getCurrentNote: GetCurrentNote,
    private val updateOrCreateNote: UpdateOrCreateNote,
    private val removeNote: RemoveNote,
    private val restore: RestoreNote,
    private val changeNoteFavoriteState: ChangeNoteFavoriteState
) : ViewModel(), NoteScreenContract {

    private val mutableState = MutableStateFlow(NoteScreenContract.State())
    override val state: StateFlow<NoteScreenContract.State> = mutableState.asStateFlow()

    private val effectFlow = MutableSharedFlow<NoteScreenContract.Effect>()
    override val effect: SharedFlow<NoteScreenContract.Effect> = effectFlow.asSharedFlow()

    override fun onEvent(event: NoteScreenContract.Event) = when (event) {
        NoteScreenContract.Event.CollectCurrentNote -> collectCurrentNote()
        NoteScreenContract.Event.SaveNote -> saveNote()
        NoteScreenContract.Event.ToggleFavorite -> changeFavoriteState()
        NoteScreenContract.Event.ArchiveNote -> {}
        NoteScreenContract.Event.DeleteNote -> deleteNote()
        NoteScreenContract.Event.RestoreNote -> restoreNote()
        NoteScreenContract.Event.ToggleTextDecoration -> changeTextDecoration()
        NoteScreenContract.Event.ToggleTextStyle -> changeTextStyle()
        NoteScreenContract.Event.ToggleTextWeight -> changeTextWeight()
        is NoteScreenContract.Event.NoteTitleChanged -> changeNoteTitle(event.newTitle)
        is NoteScreenContract.Event.NoteTextChanged -> changeNoteText(event.newText)
        is NoteScreenContract.Event.NoteParagraphChanged -> changeNoteParagraph(event.newValue)
        is NoteScreenContract.Event.NoteTextSizeChanged -> changeNoteTextSize(event.textSize)
    }

    private fun collectCurrentNote() {
        getCurrentNote()
            .catch { exception ->
                Timber.e(exception)
            }
            .onEach { model ->
                val uiModel = model?.toUiModel()

                if (uiModel != null) {
                    mutableState.update { state ->
                        state.copy(note = uiModel)
                    }
                } else {
                    mutableState.update { state ->
                        state.copy(
                            contentState = NoteScreenContentState.NOTE_NOT_SELECTED,
                            note = UiNote()
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun changeNoteTitle(newTitle: String) = mutableState.update { state ->
        state.copy(note = state.note.copy(noteTitle = newTitle))
    }

    private fun changeNoteText(newText: String) = mutableState.update { state ->
        state.copy(note = state.note.copy(noteText = newText))
    }

    private fun changeNoteParagraph(paragraph: TextUnit) = mutableState.update { state ->
        state.copy(note = state.note.copy(paragraph = paragraph))
    }

    private fun changeNoteTextSize(textSize: TextUnit) = mutableState.update { state ->
        state.copy(note = state.note.copy(textSize = textSize))
    }

    private fun changeTextWeight() = mutableState.update { state ->
        state.copy(note = state.note.copy(fontWeight = state.note.fontWeight.next))
    }

    private fun changeTextStyle() = mutableState.update { state ->
        state.copy(note = state.note.copy(textStyle = state.note.textStyle.next))
    }

    private fun changeTextDecoration() = mutableState.update { state ->
        state.copy(note = state.note.copy(textDecoration = state.note.textDecoration.next))
    }

    private fun saveNote() {
        viewModelScope.launch {
            val note = state.value.note

            when (val result = updateOrCreateNote(note.toModel())) {
                Resource.Completed -> launch {
                    effectFlow.emit(NoteScreenContract.Effect.NoteSaved)
                }

                is Resource.Error -> {
                    Timber.e(result.error)

                    launch {
                        effectFlow.emit(NoteScreenContract.Effect.NoteSaveError)
                    }
                }

                Resource.Failure -> launch {
                    effectFlow.emit(NoteScreenContract.Effect.NoteSaveError)
                }

                else -> Unit
            }
        }
    }

    private fun changeFavoriteState() {
        viewModelScope.launch {
            val newState = changeNoteFavoriteState(
                noteName = state.value.note.noteTitle,
                isFavorite = state.value.note.isFavorite
            )

            when (newState) {
                Resource.Completed -> effectFlow.emit(NoteScreenContract.Effect.AddedToFavorite)

                Resource.Failure -> effectFlow.emit(NoteScreenContract.Effect.FavoriteFailure)

                else -> Unit
            }
        }
    }

    private fun deleteNote() {
        viewModelScope.launch {
            val note = state.value.note

            when (
                val result = removeNote(
                    noteName = note.noteTitle,
                    noteFolder = note.noteFolder
                )
            ) {
                is Resource.Success -> {
                    launch { effectFlow.emit(NoteScreenContract.Effect.NoteRemoved) }

                    val canBeRestored = result.data

                    if (canBeRestored) {
                        mutableState.update { state ->
                            state.copy(
                                contentState = NoteScreenContentState.NOTE_RESTORING,
                                deletedNoteName = note.noteTitle,
                                deletedNoteFolderName = note.noteFolder
                            )
                        }
                    } else {
                        effectFlow.emit(NoteScreenContract.Effect.NoteDeleted)
                    }
                }

                Resource.Failure -> effectFlow.emit(NoteScreenContract.Effect.NoteDeleteError)
                else -> Unit
            }
        }
    }

    private fun restoreNote() {
        viewModelScope.launch {
            val deletedNoteName = state.value.deletedNoteName
            val deletedNoteFolder = state.value.deletedNoteFolderName

            restore(deletedNoteName, deletedNoteFolder)

            mutableState.update { state ->
                state.copy(contentState = NoteScreenContentState.NOTE_OPENED)
            }
        }
    }
}