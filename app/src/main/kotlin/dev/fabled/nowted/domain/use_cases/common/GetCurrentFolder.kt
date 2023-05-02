package dev.fabled.nowted.domain.use_cases.common

import dev.fabled.nowted.domain.repository.FoldersRepository

class GetCurrentFolder(private val foldersRepository: FoldersRepository) {

    operator fun invoke() = foldersRepository.currentFolder

}