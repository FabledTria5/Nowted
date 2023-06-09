package dev.fabled.nowted.presentation.ui.screens.note

import androidx.compose.ui.unit.TextUnit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.fabled.nowted.domain.model.Resource
import dev.fabled.nowted.domain.use_cases.common.GetCurrentFolder
import dev.fabled.nowted.domain.use_cases.common.GetCurrentNoteName
import dev.fabled.nowted.domain.use_cases.note.ArchiveNote
import dev.fabled.nowted.domain.use_cases.note.ChangeNoteFavoriteState
import dev.fabled.nowted.domain.use_cases.note.GetCurrentNote
import dev.fabled.nowted.domain.use_cases.note.RemoveNote
import dev.fabled.nowted.domain.use_cases.note.UpdateOrCreateNote
import dev.fabled.nowted.presentation.mapper.toModel
import dev.fabled.nowted.presentation.mapper.toUiModel
import dev.fabled.nowted.presentation.model.UiNote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for note screen
 *
 * @property getCurrentNoteName collects current note name
 * @property getCurrentNote collects current note model based on note name
 * @property getCurrentFolder collects current folder
 * @property updateOrCreateNote creates new note or updates existing one
 * @property removeNote deletes current note or moving it to trash
 * @property changeNoteFavoriteState changes favorite state of current note
 * @property archiveNote adding current note to archive
 */
class NoteViewModel(
    private val getCurrentNoteName: GetCurrentNoteName,
    private val getCurrentNote: GetCurrentNote,
    private val getCurrentFolder: GetCurrentFolder,
    private val updateOrCreateNote: UpdateOrCreateNote,
    private val removeNote: RemoveNote,
    private val changeNoteFavoriteState: ChangeNoteFavoriteState,
    private val archiveNote: ArchiveNote
) : ViewModel(), NoteScreenContract {

    private val mutableState = MutableStateFlow(NoteScreenContract.State())
    override val state: StateFlow<NoteScreenContract.State> = mutableState.asStateFlow()

    private val effectFlow = MutableSharedFlow<NoteScreenContract.Effect>()
    override val effect: SharedFlow<NoteScreenContract.Effect> = effectFlow.asSharedFlow()

    override fun onEvent(event: NoteScreenContract.Event) = when (event) {
        NoteScreenContract.Event.CollectCurrentNote -> collectCurrentNote()
        NoteScreenContract.Event.SaveNote -> saveNote()
        NoteScreenContract.Event.ToggleFavorite -> changeFavoriteState()
        NoteScreenContract.Event.ArchiveNote -> putToArchive()
        NoteScreenContract.Event.DeleteNote -> deleteNote()
        NoteScreenContract.Event.ToggleTextDecoration -> changeTextDecoration()
        NoteScreenContract.Event.ToggleTextStyle -> changeTextStyle()
        NoteScreenContract.Event.ToggleTextWeight -> changeTextWeight()
        is NoteScreenContract.Event.NoteTitleChanged -> changeNoteTitle(event.newTitle)
        is NoteScreenContract.Event.NoteTextChanged -> changeNoteText(event.newText)
        is NoteScreenContract.Event.NoteParagraphChanged -> changeNoteParagraph(event.newValue)
        is NoteScreenContract.Event.NoteTextSizeChanged -> changeNoteTextSize(event.textSize)
    }

    /**
     * Collecting current note name and performing [flatMapLatest] operation to get current note
     * model. Collecting note model is combined with collecting current folder name
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun collectCurrentNote() {
        getCurrentNoteName()
            .flatMapLatest { noteName ->
                if (noteName.isEmpty())
                    flowOf(value = null)
                else
                    getCurrentNote(noteName = noteName)
            }
            .combine(getCurrentFolder()) { model, folderModel ->
                model to folderModel.folderName
            }
            .catch { exception ->
                Timber.e(exception)
            }
            .onEach { modelWithFolder ->
                modelWithFolder.first?.toUiModel()?.let { uiNote ->
                    mutableState.update { state ->
                        state.copy(note = uiNote)
                    }
                } ?: mutableState.update { state ->
                    state.copy(note = UiNote(noteFolder = modelWithFolder.second))
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Updates note title
     *
     * @param newTitle new note title
     */
    private fun changeNoteTitle(newTitle: String) = mutableState.update { state ->
        state.copy(note = state.note.copy(noteTitle = newTitle))
    }

    /**
     * Updates note text
     *
     * @param newText new note text
     */
    private fun changeNoteText(newText: String) = mutableState.update { state ->
        state.copy(note = state.note.copy(noteText = newText))
    }

    /**
     * Updates note paragraph
     *
     * @param paragraph new paragraph value
     */
    private fun changeNoteParagraph(paragraph: TextUnit) = mutableState.update { state ->
        state.copy(note = state.note.copy(paragraph = paragraph))
    }

    /**
     * Updates note text size
     *
     * @param textSize new text size
     */
    private fun changeNoteTextSize(textSize: TextUnit) = mutableState.update { state ->
        state.copy(note = state.note.copy(textSize = textSize))
    }

    /**
     * Toggles font weight
     */
    private fun changeTextWeight() = mutableState.update { state ->
        state.copy(note = state.note.copy(fontWeight = state.note.fontWeight.next))
    }

    /**
     * Toggles text style
     */
    private fun changeTextStyle() = mutableState.update { state ->
        state.copy(note = state.note.copy(textStyle = state.note.textStyle.next))
    }

    /**
     * Toggles text decoration
     */
    private fun changeTextDecoration() = mutableState.update { state ->
        state.copy(note = state.note.copy(textDecoration = state.note.textDecoration.next))
    }

    /**
     * Saving note and emitting result of it as [NoteScreenContract.Effect]
     */
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

    /**
     * Changes note favorite state and emitting result of it as [NoteScreenContract.Effect]
     */
    private fun changeFavoriteState() {
        viewModelScope.launch {
            val newState = changeNoteFavoriteState(
                noteName = state.value.note.noteTitle,
                isFavorite = state.value.note.isFavorite
            )

            when (newState) {
                Resource.Completed -> effectFlow.emit(NoteScreenContract.Effect.AddedToFavorite)

                is Resource.Error -> Timber.e(newState.error)

                else -> Unit
            }
        }
    }

    /**
     * Adding note to archive and emitting result of it as [NoteScreenContract.Effect]
     */
    private fun putToArchive() {
        viewModelScope.launch {
            val noteName = state.value.note.noteTitle

            archiveNote(noteName = noteName)
            effectFlow.emit(NoteScreenContract.Effect.Archived)
        }
    }

    /**
     * Perform delete operation, that can add note to trash or delete it. If note can be restored,
     * emits [NoteScreenContract.Effect.AddedToTrash] to navigate to restore screen. If note can not
     * be restored, emits [NoteScreenContract.Effect.NoteDeleted] to navigate to empty screen
     */
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
                    val canBeRestored = result.data

                    if (canBeRestored) {
                        effectFlow.emit(
                            NoteScreenContract.Effect.AddedToTrash(
                                noteName = note.noteTitle,
                                noteFolder = note.noteFolder
                            )
                        )
                    } else {
                        effectFlow.emit(NoteScreenContract.Effect.NoteDeleted)
                    }
                }

                is Resource.Error -> Timber.e(result.error)
                else -> Unit
            }
        }
    }
}