package dev.fabled.nowted.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import dev.fabled.nowted.data.db.entities.FolderEntity
import dev.fabled.nowted.data.db.entities.NoteEntity
import dev.fabled.nowted.data.db.entities.RecentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    /**
     * Creates recent entity
     *
     * @param recentEntity [RecentEntity]
     */
    @Insert
    suspend fun createRecent(recentEntity: RecentEntity)

    /**
     * Collects all recents
     *
     * @return [Flow] with list of [RecentEntity]
     */
    @Query(value = "SELECT * FROM recent_notes")
    fun getRecents(): Flow<List<RecentEntity>>

    /**
     * Deletes recent with given name
     *
     * @param name name of note
     */
    @Query(value = "DELETE FROM recent_notes WHERE note_name = :name")
    suspend fun deleteRecent(name: String)

    /**
     * Creates recent with given recent entity. If recent with the same name exists, deletes it
     * and then creates other one
     */
    @Transaction
    suspend fun addRecent(recentEntity: RecentEntity) {
        deleteRecent(recentEntity.noteName)
        createRecent(recentEntity)
    }

    /**
     * Creates or updates note
     *
     * @param noteEntity [NoteEntity]
     */
    @Upsert
    suspend fun addNote(noteEntity: NoteEntity)

    /**
     * Collects notes inside folder
     *
     * @param folderName name of folder
     *
     * @return [Flow] with list of [NoteEntity]
     */
    @Query(value = "SELECT * FROM notes_table WHERE parent_folder = :folderName")
    fun getNotesInFolder(folderName: String): Flow<List<NoteEntity>>

    /**
     * Collects all favorite notes
     *
     * @return [Flow] with list of [NoteEntity]
     */
    @Query(value = "SELECT * FROM notes_table WHERE is_favorite = 1")
    fun getFavoriteNotes(): Flow<List<NoteEntity>>

    /**
     * Changes favorite status of note with given name
     *
     * @param name target note name
     * @param state new favorite state
     */
    @Query(
        value = "UPDATE notes_table " +
                "SET is_favorite = :state " +
                "WHERE note_name = :name"
    )
    suspend fun setFavoriteState(name: String, state: Boolean)

    /**
     * Collects [Flow] with [NoteEntity] that matches given name. If there is no note with
     * such name, returns flow of null
     *
     * @param noteName name of target note
     *
     * @return [Flow] of [NoteEntity]
     */
    @Query(value = "SELECT * FROM notes_table WHERE note_name = :noteName")
    fun getNoteByName(noteName: String): Flow<NoteEntity?>

    /**
     * Deleting note with given name
     *
     * @param noteName name of target note
     */
    @Query(value = "DELETE FROM notes_table WHERE note_name = :noteName")
    fun deleteNote(noteName: String)

    /**
     * Deleting note from notes table and recents table
     *
     * @param noteName name of target note
     */
    @Transaction
    suspend fun removeNote(noteName: String) {
        deleteNote(noteName)
        deleteRecent(noteName)
    }

}