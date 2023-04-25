package dev.fabled.nowted.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.fabled.nowted.domain.model.NoteModel
import dev.fabled.nowted.domain.model.Resource
import dev.fabled.nowted.domain.use_cases.folders.FoldersCases
import dev.fabled.nowted.domain.use_cases.notes.NotesCases
import dev.fabled.nowted.domain.use_cases.recents.RecentsCases
import dev.fabled.nowted.presentation.mapper.toModel
import dev.fabled.nowted.presentation.mapper.toUiModel
import dev.fabled.nowted.presentation.mapper.toUiNotesList
import dev.fabled.nowted.presentation.model.UiNote
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreenEvent
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreenState
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreenContentState
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreenEvent
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreenState
import dev.fabled.nowted.presentation.ui.screens.notes_list.NotesListScreenEvent
import dev.fabled.nowted.presentation.ui.screens.notes_list.NotesListScreenState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

/**
 * MainViewModel - primary application ViewModel, that processes all logics of applications and
 * hosts all screens states. Due to availability of all screens to be displayed at one time, this
 * ViewModel operates with states of all screens and may change states of multiple screens at one time.
 *
 * To achieve
 */
class MainViewModel(
    private val recentsCases: RecentsCases,
    private val foldersCases: FoldersCases,
    private val notesCases: NotesCases,
) : ViewModel() {

    /**
     * Defines state of Home Screen. Mutable state is private and can be modified only in this
     * ViewModel.
     */
    private val _homeScreenState = MutableStateFlow(HomeScreenState())
    val homeScreenState = _homeScreenState.asStateFlow()

    /**
     * Defines state of Notes List Screen. Mutable state is private and can be modified only in this
     * ViewModel.
     */
    private val _notesListScreenState = MutableStateFlow(NotesListScreenState())
    val notesListScreenState = _notesListScreenState.asStateFlow()

    /**
     * Defines state of Note Screen. Mutable state is private and can be modified only in this
     * ViewModel.
     */
    private val _noteScreenState = MutableStateFlow(NoteScreenState())
    val noteScreenState = _noteScreenState.asStateFlow()

    /**
     * Flow that receives errors from different sources to be delivered to user. Replay count is 0
     * to prevent showing the same error twice. This Flow should be used only for collecting
     * errors, that should notify user about self's.
     */
    private val _messagesFlow = MutableSharedFlow<String>()
    val messagesFlow = _messagesFlow.asSharedFlow()

    /**
     * Initialization of ViewModel includes collecting recent notes and folders. It also starts
     * observing searchQuery from homeScreenState to deliver search results as fast as possible.
     */
    init {
        getRecents()
        getFolders()

        collectSearchQuery()
    }

    /**
     * Observes homeScreenState and permits search in database for each query debounced for
     * 1 second. To prevent it from getting the same query, uses distinctUntilChanged() after
     * mapping state to searched query.
     */
    @OptIn(FlowPreview::class)
    private fun collectSearchQuery() = viewModelScope.launch(Dispatchers.IO) {
        _homeScreenState
            .debounce(1.seconds)
            .map { state -> state.searchQuery }
            .distinctUntilChanged()
            .onEach { }
            .catch { exception -> Timber.e(exception) }
            .flowOn(Dispatchers.IO)
            .collect()
    }

    /**
     * Collects recents list at all time of viewModel lifecycle
     */
    private fun getRecents() = recentsCases.collectRecents()
        .onEach { recentsList ->
            _homeScreenState.update { state ->
                state.copy(recentNotes = recentsList.toImmutableList())
            }
        }
        .catch { exception -> Timber.e(exception) }
        .flowOn(Dispatchers.IO)
        .launchIn(viewModelScope)

    /**
     * Collects folders list at all time of viewModel lifecycle. When getting folders, map it to
     * two lists for primary folders and utility folders ("Trash", "Favorites"...). Sets two lists
     * to homeScreenState, casted to ImmutableList.
     *
     * If no folder in state is selected, than selects first folder from primary lists and collecting
     * notes from it. This works at app startup to immediately show user list of notes.
     */
    private fun getFolders() = foldersCases.collectFolders()
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
                    selectedFolder = state.selectedFolder.ifBlank {
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

    /**
     * Collects notes from given folder name and set them to notesListState.
     *
     * @param folderName is using to identify target folder
     */
    private fun openFolder(folderName: String) = notesCases.collectNotes(folderName = folderName)
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

    /**
     * Sets note model to note screen state if model if non null.
     *
     * @param noteModel sets model to noteScreenState
     */
    private fun openNote(noteModel: NoteModel?) {
        noteModel?.let { model ->
            _noteScreenState.update { state ->
                state.copy(
                    contentState = NoteScreenContentState.NOTE_OPENED,
                    note = model.toUiModel(),
                    deletedNoteName = "",
                    deletedNoteFolderName = ""
                )
            }
        }
    }

    /**
     * Receives all events from Home Screen and performs some operations with each of them.
     *
     * This fun does not performs any navigation. The navigation should be performed in composable
     * function after event was sent to ViewModel. This is done to achieve different behavior for
     * different screen sizes.
     *
     * @param event event to be processed
     */
    fun onHomeScreenEvent(event: HomeScreenEvent) {
        when (event) {
            HomeScreenEvent.NewNote -> {
                _homeScreenState.update { state -> state.copy(selectedNoteName = "") }
                _notesListScreenState.update { state -> state.copy(selectedNoteName = "") }

                _noteScreenState.update { state ->
                    state.copy(
                        contentState = NoteScreenContentState.NOTE_OPENED,
                        note = UiNote(noteFolder = homeScreenState.value.selectedFolder),
                        deletedNoteName = "",
                        deletedNoteFolderName = ""
                    )
                }
            }

            HomeScreenEvent.OnStartCreateNewFolder -> {
                _homeScreenState.update { state -> state.copy(isCreatingFolder = true) }
            }

            HomeScreenEvent.ToggleSearch -> _homeScreenState.update { state ->
                state.copy(isSearching = !state.isSearching)
            }

            is HomeScreenEvent.OpenRecent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    launch {
                        _homeScreenState.update { state ->
                            state.copy(selectedNoteName = event.name)
                        }
                    }

                    launch { openNote(noteModel = notesCases.getNote(event.name)) }
                }
            }

            is HomeScreenEvent.OpenFolder -> {
                _homeScreenState.update { state -> state.copy(selectedFolder = event.folderName) }
                openFolder(event.folderName)
            }

            is HomeScreenEvent.CreateFolder -> {
                viewModelScope.launch(Dispatchers.IO) {
                    foldersCases.createFolder(event.folderName)
                }
                _homeScreenState.update { state -> state.copy(isCreatingFolder = false) }
            }

            is HomeScreenEvent.ChangeSearchQuery -> {

            }
        }
    }

    /**
     * Receives all events from Notes List Screen and performs some operations with each of them.
     *
     * This fun does not performs any navigation. The navigation should be performed in composable
     * function after event was sent to ViewModel. This is done to achieve different behavior for
     * different screen sizes.
     *
     * @param event event to be processed
     */
    fun onNotesListScreenEvent(event: NotesListScreenEvent) {
        when (event) {
            NotesListScreenEvent.OnCreateFirstNote -> {
                _notesListScreenState.update { state ->
                    state.copy(selectedNoteName = "")
                }

                _noteScreenState.update { state ->
                    state.copy(
                        contentState = NoteScreenContentState.NOTE_OPENED,
                        note = UiNote(noteFolder = notesListScreenState.value.folderName),
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

                    launch {
                        recentsCases.addRecent(noteName = event.noteName)

                        _homeScreenState.update { state ->
                            state.copy(selectedNoteName = event.noteName)
                        }
                    }

                    launch {
                        openNote(noteModel = notesCases.getNote(event.noteName))
                    }
                }
            }
        }
    }

    /**
     * Receives all events from Note Screen and performs some operations with each of them.
     *
     * This fun does not performs any navigation. The navigation should be performed in composable
     * function after event was sent to ViewModel. This is done to achieve different behavior for
     * different screen sizes.
     *
     * @param event event to be processed
     */
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

                    notesCases.restoreNote(
                        noteName = deleteNoteName,
                        noteFolder = deletedNoteFolder
                    )

                    _noteScreenState.update { state ->
                        state.copy(
                            contentState = NoteScreenContentState.NOTE_OPENED,
                            deletedNoteName = "",
                            deletedNoteFolderName = ""
                        )
                    }
                }
            }

            NoteScreenEvent.DeleteNote -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val currentNote = noteScreenState.value.note

                    val result = notesCases.deleteNote(
                        noteName = currentNote.noteTitle,
                    )

                    when (result) {
                        is Resource.Success -> {
                            if (result.data) {
                                _noteScreenState.update { state ->
                                    state.copy(
                                        contentState = NoteScreenContentState.NOTE_RESTORING,
                                        deletedNoteName = currentNote.noteTitle,
                                        deletedNoteFolderName = currentNote.noteFolder
                                    )
                                }
                            } else {
                                _noteScreenState.update { state ->
                                    state.copy(
                                        contentState = NoteScreenContentState.NOTE_NOT_SELECTED,
                                        note = UiNote()
                                    )
                                }
                            }
                        }

                        Resource.Failure -> _messagesFlow.emit(value = "Can not delete non-existing note")
                        else -> Unit
                    }
                }
            }

            NoteScreenEvent.AddToFavorites -> {}
            NoteScreenEvent.ArchiveNote -> {}
            NoteScreenEvent.SaveNote -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val note = noteScreenState.value.note

                    when (val result = notesCases.saveNote(note.toModel())) {
                        Resource.Completed -> {
                            launch { _messagesFlow.emit(value = "Note has been created!") }

                            launch {
                                _homeScreenState.update { state ->
                                    state.copy(selectedNoteName = note.noteTitle)
                                }
                            }

                            launch {
                                _notesListScreenState.update { state ->
                                    state.copy(selectedNoteName = note.noteTitle)
                                }
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