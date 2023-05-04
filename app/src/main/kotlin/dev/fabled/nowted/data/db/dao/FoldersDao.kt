package dev.fabled.nowted.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.fabled.nowted.data.db.entities.FolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoldersDao {

    @Upsert
    suspend fun addFolder(folderEntity: FolderEntity)

    @Query(value = "SELECT * FROM folders_table WHERE is_primary = 1 ORDER BY id DESC")
    fun getFolders(): Flow<List<FolderEntity>>

    @Query(value = "SELECT * FROM folders_table WHERE folder_name = :folderName")
    suspend fun getFolder(folderName: String): FolderEntity

    @Query(
        value = "UPDATE notes_table " +
                "SET parent_folder = :newFolder " +
                "WHERE note_name = :noteName"
    )
    suspend fun changeFolder(noteName: String, newFolder: String)

}