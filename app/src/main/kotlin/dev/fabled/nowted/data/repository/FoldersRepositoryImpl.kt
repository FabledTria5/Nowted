package dev.fabled.nowted.data.repository

import dev.fabled.nowted.data.db.dao.NotesDao
import dev.fabled.nowted.data.db.entities.FolderEntity
import dev.fabled.nowted.data.mapper.toFolderModel
import dev.fabled.nowted.data.mapper.toFoldersModels
import dev.fabled.nowted.domain.dispatchers.AppDispatchers
import dev.fabled.nowted.domain.model.FolderModel
import dev.fabled.nowted.domain.repository.FoldersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class FoldersRepositoryImpl(
    private val notesDao: NotesDao,
    private val dispatchers: AppDispatchers
) : FoldersRepository {

    private val mutableFolder = MutableStateFlow(FolderModel())
    override val currentFolder: Flow<FolderModel> = mutableFolder.asStateFlow()

    override fun getFolders(): Flow<List<FolderModel>> = notesDao
        .getFolders()
        .map { list -> list.toFoldersModels() }
        .flowOn(dispatchers.ioDispatcher)

    override suspend fun createFolder(folderName: String) = withContext(dispatchers.ioDispatcher) {
        notesDao.addFolder(FolderEntity(folderName = folderName))
    }

    override suspend fun openFolder(folderName: String) {
        val folderEntity = notesDao.getFolder(folderName)
        mutableFolder.update { folderEntity.toFolderModel() }
    }

    override suspend fun changeNoteFolder(noteName: String, noteFolder: String) =
        withContext(dispatchers.ioDispatcher) {
            notesDao.changeFolder(noteName = noteName, newFolder = noteFolder)
        }


}