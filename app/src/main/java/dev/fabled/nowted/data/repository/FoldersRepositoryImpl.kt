package dev.fabled.nowted.data.repository

import dev.fabled.nowted.data.db.dao.NotesDao
import dev.fabled.nowted.data.db.entities.FolderEntity
import dev.fabled.nowted.data.mapper.toFoldersModels
import dev.fabled.nowted.domain.model.FolderModel
import dev.fabled.nowted.domain.repository.FoldersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FoldersRepositoryImpl(private val notesDao: NotesDao) : FoldersRepository {

    override fun getFolders(): Flow<List<FolderModel>> = notesDao
        .getFolders()
        .map { list -> list.toFoldersModels() }

    override suspend fun createFolder(folderName: String) {
        notesDao.addFolder(FolderEntity(folderName = folderName))
    }

}