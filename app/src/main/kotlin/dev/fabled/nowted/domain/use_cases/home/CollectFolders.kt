package dev.fabled.nowted.domain.use_cases.home

import dev.fabled.nowted.domain.repository.FoldersRepository

/**
 * Collecting non-system folders
 *
 * @property foldersRepository repository manipulating folders data
 */
class CollectFolders(private val foldersRepository: FoldersRepository) {

    operator fun invoke() = foldersRepository.getFolders()

}