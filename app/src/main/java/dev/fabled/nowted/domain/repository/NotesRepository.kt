package dev.fabled.nowted.domain.repository

import dev.fabled.nowted.domain.model.NoteModel
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    /**
     * [Flow] that represents the name of current folder. Each emission triggers [notesInFolder]
     * to update itself depending in it's value
     */
    val currentFolder: Flow<String>

    /**
     * [Flow] that represents notes in current folder. On each emission of [currentFolder] will
     * trigger collecting notes from this folder.
     */
    val notesInFolder: Flow<List<NoteModel>>

    /**
     * Flow that represents current selected note. If creating new note, value is null
     */
    val currentNote: Flow<NoteModel?>

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
     * Updates current folder, that triggers update of notes list for this folder
     *
     * @param folderName name of folder
     */
    fun openFolder(folderName: String)

    /**
     * Selects note with specified name.
     *
     * @param noteName triggers [currentNote] to be updated by given value
     */
    fun openNote(noteName: String)

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