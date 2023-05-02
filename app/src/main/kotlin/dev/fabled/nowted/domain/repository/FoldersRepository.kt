package dev.fabled.nowted.domain.repository

import dev.fabled.nowted.domain.model.FolderModel
import kotlinx.coroutines.flow.Flow

interface FoldersRepository {

    /**
     * [Flow] that represents the name of current folder
     */
    val currentFolder: Flow<FolderModel>

    /**
     * Returns flow of [FolderModel]
     *
     * @return flow containing list of [FolderModel]
     */
    fun getFolders(): Flow<List<FolderModel>>

    /**
     * Creates folder with given [folderName]
     *
     * @param folderName name of new folder
     */
    suspend fun createFolder(folderName: String)

    /**
     * Open folder with passed name by emitting it to [currentFolder]
     *
     * @param folderName target folder name
     */
    suspend fun openFolder(folderName: String)

    /**
     * Changing folder for note
     *
     * @param noteName name of target note
     * @param noteFolder new folder for given note
     */
    suspend fun changeNoteFolder(noteName: String, noteFolder: String)

}