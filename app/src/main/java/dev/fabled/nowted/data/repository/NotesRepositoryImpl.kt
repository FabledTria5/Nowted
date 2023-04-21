package dev.fabled.nowted.data.repository

import dev.fabled.nowted.data.db.dao.NotesDao
import dev.fabled.nowted.data.db.entities.FolderEntity
import dev.fabled.nowted.data.mapper.toEntity
import dev.fabled.nowted.data.mapper.toFoldersModels
import dev.fabled.nowted.data.mapper.toNoteModel
import dev.fabled.nowted.data.mapper.toNotesModels
import dev.fabled.nowted.domain.model.FolderModel
import dev.fabled.nowted.domain.model.NoteModel
import dev.fabled.nowted.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotesRepositoryImpl(
    private val notesDao: NotesDao
) : NotesRepository {

    override suspend fun createFolder(folderName: String) {
        notesDao.addFolder(FolderEntity(folderName = folderName))
    }

    override suspend fun createNote(noteModel: NoteModel) {
        notesDao.addOrUpdateNote(noteEntity = noteModel.toEntity())
    }

    override suspend fun changeNoteFolder(noteName: String, noteFolder: String) {
        notesDao.changeFolder(noteName = noteName, newFolder = noteFolder)
    }

    override suspend fun deleteNote(noteName: String) {
        notesDao.deleteNote(noteName = noteName)
    }

    override suspend fun getNote(name: String): NoteModel =
        notesDao.getNoteByName(noteName = name).toNoteModel()

    override suspend fun isNoteExists(name: String): Boolean = notesDao.isNoteExist(name)

    override suspend fun toggleFavoriteState(name: String, newState: Boolean) =
        notesDao.setFavoriteState(name, newState)

    override fun getFoldersList(): Flow<List<FolderModel>> = notesDao
        .getFolders()
        .map { list -> list.toFoldersModels() }

    override fun getNotesFromFolder(folderName: String): Flow<List<NoteModel>> = notesDao
        .getNotesInFolder(folderName = folderName)
        .map { list -> list.toNotesModels() }

}