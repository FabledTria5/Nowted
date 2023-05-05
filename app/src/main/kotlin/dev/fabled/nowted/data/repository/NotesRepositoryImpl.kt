package dev.fabled.nowted.data.repository

import dev.fabled.nowted.data.db.dao.NotesDao
import dev.fabled.nowted.data.db.entities.RecentEntity
import dev.fabled.nowted.data.mapper.toEntity
import dev.fabled.nowted.data.mapper.toNoteModel
import dev.fabled.nowted.data.mapper.toNotesModels
import dev.fabled.nowted.domain.dispatchers.AppDispatchers
import dev.fabled.nowted.domain.model.NoteModel
import dev.fabled.nowted.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class NotesRepositoryImpl(
    private val notesDao: NotesDao,
    private val dispatchers: AppDispatchers
) : NotesRepository {

    private val mutableNoteName = MutableStateFlow(value = "")
    override val currentNoteName: Flow<String> = mutableNoteName.asStateFlow()

    override fun getRecents(limit: Int): Flow<List<String>> = notesDao.getRecents()
        .map { list ->
            list
                .map { entity -> entity.noteName }
                .takeLast(limit)
                .reversed()
        }
        .flowOn(dispatchers.ioDispatcher)

    override suspend fun addRecent(noteName: String) = withContext(dispatchers.ioDispatcher) {
        notesDao.addRecent(RecentEntity(noteName = noteName))
    }

    override fun getNotesFromFolder(folderName: String): Flow<List<NoteModel>> =
        notesDao.getNotesInFolder(folderName)
            .map { it.toNotesModels() }
            .flowOn(dispatchers.ioDispatcher)

    override fun getFavoriteNotes(): Flow<List<NoteModel>> = notesDao.getFavoriteNotes()
        .map { it.toNotesModels() }
        .flowOn(dispatchers.ioDispatcher)

    override fun selectNote(noteName: String) = mutableNoteName.update { noteName }

    override fun getNoteByName(noteName: String): Flow<NoteModel?> =
        notesDao.getNoteByName(noteName)
            .map { entities -> entities?.toNoteModel() }
            .flowOn(dispatchers.ioDispatcher)

    override suspend fun createNote(noteModel: NoteModel) = withContext(dispatchers.ioDispatcher) {
        notesDao.addNote(noteEntity = noteModel.toEntity())
    }

    override suspend fun deleteNote(noteName: String) = withContext(dispatchers.ioDispatcher) {
        notesDao.removeNote(noteName = noteName)
    }

    override suspend fun toggleFavoriteState(name: String, newState: Boolean) =
        withContext(dispatchers.ioDispatcher) {
            notesDao.setFavoriteState(name, newState)
        }


}