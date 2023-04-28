package dev.fabled.nowted.data.repository

import dev.fabled.nowted.data.db.dao.NotesDao
import dev.fabled.nowted.data.db.entities.RecentEntity
import dev.fabled.nowted.data.mapper.toEntity
import dev.fabled.nowted.data.mapper.toNoteModel
import dev.fabled.nowted.domain.dispatchers.AppDispatchers
import dev.fabled.nowted.domain.model.NoteModel
import dev.fabled.nowted.domain.repository.NotesRepository
import dev.fabled.nowted.presentation.core.mapAsync
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class NotesRepositoryImpl(
    private val notesDao: NotesDao,
    private val dispatchers: AppDispatchers
) : NotesRepository {

    private val mutableFolder = MutableStateFlow(value = "")
    private val mutableNote = MutableStateFlow(value = "")

    override val currentFolder: Flow<String> = mutableFolder.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val notesInFolder: Flow<List<NoteModel>> = currentFolder
        .flatMapLatest { folderName ->
            notesDao.getNotesInFolder(folderName)
        }
        .map { entities ->
            entities.mapAsync { entity -> entity.toNoteModel() }
        }
        .flowOn(dispatchers.dispatcherIO)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentNote: Flow<NoteModel?> = mutableNote
        .flatMapLatest { noteName ->
            if (noteName.isNotBlank())
                notesDao.getNoteByName(noteName = noteName)
            else
                flowOf(value = null)
        }
        .map { entity ->
            entity?.toNoteModel()
        }
        .flowOn(dispatchers.dispatcherIO)

    override fun getRecents(limit: Int): Flow<List<String>> = notesDao.getRecents()
        .map { list ->
            list
                .map { entity -> entity.noteName }
                .takeLast(limit)
                .reversed()
        }
        .flowOn(dispatchers.dispatcherIO)

    override suspend fun addRecent(noteName: String) = withContext(dispatchers.dispatcherIO) {
        notesDao.addRecent(RecentEntity(noteName = noteName))
    }

    override fun openFolder(folderName: String) {
        mutableFolder.update { folderName }
    }

    override fun openNote(noteName: String) {
        mutableNote.update { noteName }
    }

    override suspend fun isNoteExists(name: String): Boolean =
        withContext(dispatchers.dispatcherIO) {
            notesDao.isNoteExist(name)
        }

    override suspend fun createNote(noteModel: NoteModel) = withContext(dispatchers.dispatcherIO) {
        notesDao.addNote(noteEntity = noteModel.toEntity())
    }

    override suspend fun deleteNote(noteName: String) = withContext(dispatchers.dispatcherIO) {
        notesDao.removeNote(noteName = noteName)
    }

    override suspend fun changeNoteFolder(noteName: String, noteFolder: String) =
        withContext(dispatchers.dispatcherIO) {
            notesDao.changeFolder(noteName = noteName, newFolder = noteFolder)
        }

    override suspend fun toggleFavoriteState(name: String, newState: Boolean) =
        withContext(dispatchers.dispatcherIO) {
            notesDao.setFavoriteState(name, newState)
        }


}