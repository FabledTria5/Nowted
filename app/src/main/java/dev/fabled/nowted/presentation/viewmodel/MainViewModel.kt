package dev.fabled.nowted.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.fabled.nowted.domain.model.Resource
import dev.fabled.nowted.domain.use_cases.folders.CollectFolders
import dev.fabled.nowted.domain.use_cases.folders.CreateFolder
import dev.fabled.nowted.domain.use_cases.notes.CollectNotes
import dev.fabled.nowted.domain.use_cases.notes.DeleteNote
import dev.fabled.nowted.domain.use_cases.notes.GetNote
import dev.fabled.nowted.domain.use_cases.notes.RestoreNote
import dev.fabled.nowted.domain.use_cases.notes.SaveNote
import dev.fabled.nowted.presentation.mapper.toModel
import dev.fabled.nowted.presentation.mapper.toUiModel
import dev.fabled.nowted.presentation.mapper.toUiNotesList
import dev.fabled.nowted.presentation.model.UiNote
import dev.fabled.nowted.presentation.ui.navigation.manager.NavigationManager
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreenEvent
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreenState
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreenEvent
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreenState
import dev.fabled.nowted.presentation.ui.screens.notes_list.NotesListScreenEvent
import dev.fabled.nowted.presentation.ui.screens.notes_list.NotesListScreenState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainViewModel(
    private val collectFolders: CollectFolders,
    private val collectNotes: CollectNotes,
    private val createFolder: CreateFolder,
    private val getNote: GetNote,
    private val restoreNote: RestoreNote,
    private val deleteNote: DeleteNote,
    private val saveNote: SaveNote,
    private val navigationManager: NavigationManager
) : ViewModel(), NavigationManager by navigationManager {

    companion object {
        private val noteDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }

    private val _homeScreenState = MutableStateFlow(HomeScreenState())
    val homeScreenState = _homeScreenState.asStateFlow()

    private val _notesListScreenState = MutableStateFlow(NotesListScreenState())
    val notesListScreenState = _notesListScreenState.asStateFlow()

    private val _noteScreenState = MutableStateFlow(NoteScreenState())
    val noteScreenState = _noteScreenState.asStateFlow()

    private val _messagesFlow = MutableSharedFlow<String>()
    val messagesFlow = _messagesFlow.asSharedFlow()

    init {
        getFolders()
    }

    private fun getFolders() = collectFolders()
        .onEach { resultList ->
            val newPrimaryList = arrayListOf<String>()
            val newAdditionsList = arrayListOf<String>()

            for (folderModel in resultList) {
                if (folderModel.isPrimary)
                    newPrimaryList += folderModel.folderName
                else
                    newAdditionsList += folderModel.folderName
            }

            _homeScreenState.update { state ->
                state.copy(
                    primaryFolders = newPrimaryList.toImmutableList(),
                    additionalFolders = newAdditionsList.toImmutableList(),
                    selectedFolder = state.selectedFolder.ifEmpty {
                        val firstFolder = newPrimaryList.first()
                        openFolder(firstFolder)

                        firstFolder
                    }
                )
            }
        }
        .catch { exception -> Timber.e(exception) }
        .flowOn(Dispatchers.IO)
        .launchIn(viewModelScope)

    private fun openFolder(folderName: String) = collectNotes(folderName = folderName)
        .onEach { list ->
            _notesListScreenState.update { state ->
                state.copy(
                    folderName = folderName,
                    notesList = list.toUiNotesList()
                )
            }
        }
        .catch { exception -> Timber.e(exception) }
        .flowOn(Dispatchers.IO)
        .launchIn(viewModelScope)

    fun onHomeScreenEvent(event: HomeScreenEvent) {
        when (event) {
            HomeScreenEvent.NewNote -> {
                _homeScreenState.update { state -> state.copy(selectedNoteName = "") }
                _notesListScreenState.update { state -> state.copy(selectedNoteName = "") }

                _noteScreenState.update { state ->
                    state.copy(
                        isNoteOpened = true,
                        note = UiNote(
                            noteFolder = homeScreenState.value.selectedFolder,
                            noteDate = LocalDate.now().format(noteDateFormatter)
                        ),
                        deletedNoteName = "",
                        deletedNoteFolderName = ""
                    )
                }
            }

            HomeScreenEvent.OnStartCreateNewFolder -> {
                _homeScreenState.update { state -> state.copy(isCreatingFolder = true) }
            }

            is HomeScreenEvent.OpenFolder -> {
                _homeScreenState.update { state -> state.copy(selectedFolder = event.folderName) }
                openFolder(event.folderName)
            }

            is HomeScreenEvent.OnCreateFolder -> {
                viewModelScope.launch(Dispatchers.IO) { createFolder(event.folderName) }
                _homeScreenState.update { state -> state.copy(isCreatingFolder = false) }
            }
        }
    }

    fun onNotesListScreenEvent(event: NotesListScreenEvent) {
        when (event) {
            NotesListScreenEvent.OnCreateFirstNote -> {
                _notesListScreenState.update { state ->
                    state.copy(selectedNoteName = "")
                }

                _noteScreenState.update { state ->
                    state.copy(
                        isNoteOpened = true,
                        note = UiNote(
                            noteFolder = notesListScreenState.value.folderName,
                            noteDate = LocalDate.now().format(noteDateFormatter)
                        ),
                        deletedNoteName = "",
                        deletedNoteFolderName = ""
                    )
                }
            }

            is NotesListScreenEvent.OnNoteClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _notesListScreenState.update { state ->
                        state.copy(selectedNoteName = event.noteName)
                    }

                    _noteScreenState.update { state ->
                        state.copy(
                            isNoteOpened = true,
                            note = getNote(event.noteName).toUiModel(),
                            deletedNoteName = "",
                            deletedNoteFolderName = ""
                        )
                    }
                }
            }
        }
    }

    fun onNoteScreenEvent(event: NoteScreenEvent) {
        when (event) {
            is NoteScreenEvent.NoteParagraphChanged -> _noteScreenState.update { state ->
                state.copy(note = state.note.copy(paragraph = event.newValue))
            }

            is NoteScreenEvent.NoteTextSizeChanged -> _noteScreenState.update { state ->
                state.copy(note = state.note.copy(textSize = event.textSize))
            }

            is NoteScreenEvent.NoteTitleChanged -> _noteScreenState.update { state ->
                state.copy(note = state.note.copy(noteTitle = event.newTitle))
            }

            is NoteScreenEvent.NoteTextChanged -> _noteScreenState.update { state ->
                state.copy(note = state.note.copy(noteText = event.newText))
            }

            NoteScreenEvent.ToggleTextWeight -> _noteScreenState.update { state ->
                state.copy(note = state.note.copy(fontWeight = state.note.fontWeight.next))
            }

            NoteScreenEvent.ToggleTextStyle -> _noteScreenState.update { state ->
                state.copy(note = state.note.copy(textStyle = state.note.textStyle.next))
            }

            NoteScreenEvent.ToggleTextDecoration -> _noteScreenState.update { state ->
                state.copy(note = state.note.copy(textDecoration = state.note.textDecoration.next))
            }

            NoteScreenEvent.RestoreNote -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val deleteNoteName = _noteScreenState.value.deletedNoteName
                    val deletedNoteFolder = _noteScreenState.value.deletedNoteFolderName

                    restoreNote(noteName = deleteNoteName, noteFolder = deletedNoteFolder)

                    _noteScreenState.update { state ->
                        state.copy(deletedNoteName = "", deletedNoteFolderName = "")
                    }
                }
            }

            NoteScreenEvent.DeleteNote -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val currentNote = noteScreenState.value.note

                    val canBeRestored = deleteNote(
                        noteName = currentNote.noteTitle,
                        noteFolder = currentNote.noteFolder
                    )

                    if (canBeRestored) {
                        _noteScreenState.update { state ->
                            state.copy(
                                deletedNoteName = currentNote.noteTitle,
                                deletedNoteFolderName = currentNote.noteFolder
                            )
                        }
                    } else {
                        _noteScreenState.update { state ->
                            state.copy(isNoteOpened = false, note = UiNote())
                        }
                    }
                }
            }

            NoteScreenEvent.AddToFavorites -> {}
            NoteScreenEvent.ArchiveNote -> {}
            NoteScreenEvent.SaveNote -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val note = noteScreenState.value.note

                    when (val result = saveNote(note.toModel())) {
                        Resource.Completed -> {
                            launch { _messagesFlow.emit("Note has been created!") }

                            _notesListScreenState.update { state ->
                                state.copy(selectedNoteName = note.noteTitle)
                            }
                        }

                        is Resource.Error -> _messagesFlow.emit(result.error)
                        else -> Unit
                    }
                }
            }
        }
    }

}