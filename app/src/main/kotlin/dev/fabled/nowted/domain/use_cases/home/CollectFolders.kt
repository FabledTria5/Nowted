package dev.fabled.nowted.domain.use_cases.home

import dev.fabled.nowted.domain.repository.FoldersRepository

class CollectFolders(private val foldersRepository: FoldersRepository) {

    operator fun invoke() = foldersRepository.getFolders()

}