package dev.fabled.nowted.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.fabled.nowted.data.db.entities.FolderEntity
import dev.fabled.nowted.data.db.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Upsert
    fun addFolder(folderEntity: FolderEntity)

    @Upsert
    fun addOrUpdateNote(noteEntity: NoteEntity)

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
    suspend fun getNoteByName(noteName: String): NoteEntity

    @Query(value = "DELETE FROM notes_table WHERE note_name = :noteName")
    suspend fun deleteNote(noteName: String)

    @Query(value = "SELECT * FROM folders_table ORDER BY id DESC")
    fun getFolders(): Flow<List<FolderEntity>>

    @Query(value = "SELECT * FROM notes_table WHERE parent_folder = :folderName")
    fun getNotesInFolder(folderName: String): Flow<List<NoteEntity>>

}