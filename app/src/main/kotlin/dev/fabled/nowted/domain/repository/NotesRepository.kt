package dev.fabled.nowted.domain.repository

import dev.fabled.nowted.domain.model.NoteModel
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    /**
     * [Flow] that represents name of selected note
     */
    val currentNoteName: Flow<String>

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
     * Collects notes from given folder and returns it as [Flow]
     *
     * @param folderName name of folder to collect notes
     * @return [Flow] containing list if notes in folder
     */
    fun getNotesFromFolder(folderName: String): Flow<List<NoteModel>>

    /**
     * Collects all favorite notes
     */
    fun getFavoriteNotes(): Flow<List<NoteModel>>

    /**
     * Selects note with specified name.
     *
     * @param noteName updates [currentNoteName]
     */
    fun selectNote(noteName: String)

    /**
     * Collects note with given name.
     *
     * @param noteName name of target note
     * @return [Flow] containing [NoteModel]. [NoteModel] can be null, if no note with specified
     * name is presented
     */
    fun getNoteByName(noteName: String): Flow<NoteModel?>

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
     * Changing favorite state of note
     *
     * @param name name of target note
     * @param newState defines if note will be added to favorite or be removed from favorite
     */
    suspend fun toggleFavoriteState(name: String, newState: Boolean)

}