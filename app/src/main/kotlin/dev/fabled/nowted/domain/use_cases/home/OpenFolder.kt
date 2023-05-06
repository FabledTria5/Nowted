package dev.fabled.nowted.domain.use_cases.home

import dev.fabled.nowted.domain.repository.FoldersRepository

/**
 * Selecting folder with given name as current
 *
 * @property foldersRepository repository manipulating folders data
 */
class OpenFolder(private val foldersRepository: FoldersRepository) {

    suspend operator fun invoke(folderName: String) = foldersRepository.openFolder(folderName)

}