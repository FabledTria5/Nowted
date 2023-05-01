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

    @Insert
    suspend fun createRecent(recentEntity: RecentEntity)

    @Query(value = "SELECT * FROM recent_notes")
    fun getRecents(): Flow<List<RecentEntity>>

    @Query(value = "DELETE FROM recent_notes WHERE note_name = :name")
    suspend fun deleteRecent(name: String)

    @Transaction
    suspend fun addRecent(recentEntity: RecentEntity) {
        deleteRecent(recentEntity.noteName)
        createRecent(recentEntity)
    }

    @Upsert
    fun addFolder(folderEntity: FolderEntity)

    @Query(value = "SELECT * FROM folders_table WHERE is_primary = 1 ORDER BY id DESC")
    fun getFolders(): Flow<List<FolderEntity>>

    @Upsert
    suspend fun addNote(noteEntity: NoteEntity)

    @Query(value = "SELECT * FROM notes_table WHERE parent_folder = :folderName")
    fun getNotesInFolder(folderName: String): Flow<List<NoteEntity>>

    @Query(value = "SELECT * FROM notes_table WHERE is_favorite = 1")
    fun getFavoriteNotes(): Flow<List<NoteEntity>>

    @Query(
        value = "UPDATE notes_table " +
                "SET parent_folder = :newFolder " +
                "WHERE note_name = :noteName"
    )
    suspend fun changeFolder(noteName: String, newFolder: String)

    @Query(value = "SELECT EXISTS (SELECT * FROM notes_table WHERE note_name = :name)")
    suspend fun isNoteExist(name: String): Boolean

    @Query(
        value = "UPDATE notes_table " +
                "SET is_favorite = :state " +
                "WHERE note_name = :name"
    )
    suspend fun setFavoriteState(name: String, state: Boolean)

    @Query(value = "SELECT * FROM notes_table WHERE note_name = :noteName")
    fun getNoteByName(noteName: String): Flow<NoteEntity?>

    @Query(value = "DELETE FROM notes_table WHERE note_name = :noteName")
    fun deleteNote(noteName: String)

    @Transaction
    suspend fun removeNote(noteName: String) {
        deleteNote(noteName)
        deleteRecent(noteName)
    }

}