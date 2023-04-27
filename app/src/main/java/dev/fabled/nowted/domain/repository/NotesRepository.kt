package dev.fabled.nowted.domain.repository

import dev.fabled.nowted.domain.model.NoteModel
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    /**
     * Returns flow of strings that represents recent notes names
     *
     * @param limit defines amount of recent items to be received in flow
     * @return flow of recents names, limited by [limit] param
     */
    fun getRecents(limit: Int): Flow<List<String>>

    /**
     * Created recent item
     */
    suspend fun addRecent(noteName: String)

    /**
     * Returns all notes from folder with given [folderName]
     *
     * @param folderName target folder name
     * @return flow of [NoteModel] from given folder
     */
    fun getNotesFromFolder(folderName: String): Flow<List<NoteModel>>

    /**
     * Returns flow, where all notes are marked as favorite
     *
     * @return [Flow] of [NoteModel]
     */
    fun getFavoriteNotes(): Flow<List<NoteModel>>

    /**
     * Returns single [NoteModel]
     *
     * @param name name of note
     * @return [NoteModel] that matches given [name]. Can be null if note does not exist
     */
    suspend fun getNote(name: String): NoteModel?

    /**
     * Checks if note with given name exists
     *
     * @param name note name to check if note exists
     * @return [Boolean] result of note existence
     */
    suspend fun isNoteExists(name: String): Boolean

    /**
     * Creates note from given [NoteModel] object
     *
     * @param noteModel model of note to create
     */
    suspend fun createNote(noteModel: NoteModel)

    /**
     * Deleting note with given note name
     *
     * @param noteName used to delete note with this name
     */
    suspend fun deleteNote(noteName: String)

    /**
     * Changing folder for note
     *
     * @param noteName name of target note
     * @param noteFolder new folder for given note
     */
    suspend fun changeNoteFolder(noteName: String, noteFolder: String)

    /**
     * Changing favorite state of note
     *
     * @param name name of target note
     * @param newState defines if note will be added to favorite or be removed from favorite
     */
    suspend fun toggleFavoriteState(name: String, newState: Boolean)

}