package dev.fabled.nowted.domain.use_cases.folders

import dev.fabled.nowted.domain.repository.FoldersRepository

class CreateFolder(private val foldersRepository: FoldersRepository) {

    suspend operator fun invoke(folderName: String) = foldersRepository.createFolder(folderName)

}