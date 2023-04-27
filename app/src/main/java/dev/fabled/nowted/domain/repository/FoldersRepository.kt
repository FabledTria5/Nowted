package dev.fabled.nowted.domain.repository

import dev.fabled.nowted.domain.model.FolderModel
import kotlinx.coroutines.flow.Flow

interface FoldersRepository {

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

}