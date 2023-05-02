package dev.fabled.nowted.domain.use_cases.home

import dev.fabled.nowted.domain.repository.FoldersRepository

class OpenFolder(private val foldersRepository: FoldersRepository) {

    suspend operator fun invoke(folderName: String) {
        foldersRepository.openFolder(folderName)
    }

}