package dev.fabled.nowted.domain.repository

import dev.fabled.nowted.domain.model.FolderModel
import dev.fabled.nowted.domain.model.NoteModel
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    fun getRecents(limit: Int): Flow<List<String>>

    suspend fun addRecent(noteName: String)

    fun getFolders(): Flow<List<FolderModel>>

    suspend fun createFolder(folderName: String)

    fun getNotesFromFolder(folderName: String): Flow<List<NoteModel>>

    suspend fun getNote(name: String): NoteModel

    suspend fun isNoteExists(name: String): Boolean

    suspend fun createNote(noteModel: NoteModel)

    suspend fun deleteNote(noteName: String)

    suspend fun changeNoteFolder(noteName: String, noteFolder: String)

    suspend fun toggleFavoriteState(name: String, newState: Boolean)

}