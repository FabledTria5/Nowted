package dev.fabled.nowted.domain.use_cases.common

import dev.fabled.nowted.domain.repository.FoldersRepository
import kotlinx.coroutines.flow.Flow

/**
 * Returns [Flow] containing current selected folder model
 *
 * @property foldersRepository repository manipulating folders data
 */
class GetCurrentFolder(private val foldersRepository: FoldersRepository) {

    operator fun invoke() = foldersRepository.currentFolder

}