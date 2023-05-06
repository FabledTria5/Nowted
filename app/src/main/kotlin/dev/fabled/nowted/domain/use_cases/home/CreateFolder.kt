package dev.fabled.nowted.domain.use_cases.home

import dev.fabled.nowted.domain.repository.FoldersRepository

/**
 * Creating folder with given name
 *
 * @property foldersRepository repository manipulating folders data
 */
class CreateFolder(private val foldersRepository: FoldersRepository) {

    suspend operator fun invoke(folderName: String) = foldersRepository.createFolder(folderName)

}