package dev.fabled.nowted.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.fabled.nowted.data.db.entities.FolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoldersDao {

    /**
     * Creating folder entity
     *
     * @param folderEntity [FolderEntity]
     */
    @Upsert
    suspend fun addFolder(folderEntity: FolderEntity)

    /**
     * Returns [Flow] containing all primary folders
     */
    @Query(value = "SELECT * FROM folders_table WHERE is_primary = 1 ORDER BY id DESC")
    fun getFolders(): Flow<List<FolderEntity>>

    /**
     * Returns folder with given name
     *
     * @param folderName name of target folder
     *
     * @return [FolderEntity]
     */
    @Query(value = "SELECT * FROM folders_table WHERE folder_name = :folderName")
    suspend fun getFolder(folderName: String): FolderEntity

    /**
     * Changes folder for given note
     *
     * @param noteName name of target note
     * @param newFolder name of new folder
     */
    @Query(
        value = "UPDATE notes_table " +
                "SET parent_folder = :newFolder " +
                "WHERE note_name = :noteName"
    )
    suspend fun changeFolder(noteName: String, newFolder: String)

}