package dev.fabled.nowted.domain.repository

import dev.fabled.nowted.domain.model.FolderModel
import dev.fabled.nowted.domain.model.NoteModel
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    suspend fun createFolder(folderName: String)

    suspend fun createNote(noteModel: NoteModel)

    suspend fun changeNoteFolder(noteName: String, noteFolder: String)

    suspend fun deleteNote(noteName: String)

    suspend fun getNote(name: String): NoteModel

    suspend fun isNoteExists(name: String): Boolean

    suspend fun toggleFavoriteState(name: String, newState: Boolean)

    fun getFoldersList(): Flow<List<FolderModel>>

    fun getNotesFromFolder(folderName: String): Flow<List<NoteModel>>

}