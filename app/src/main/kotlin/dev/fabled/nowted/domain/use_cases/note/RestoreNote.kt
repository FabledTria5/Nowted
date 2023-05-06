package dev.fabled.nowted.domain.use_cases.note

import dev.fabled.nowted.domain.repository.FoldersRepository

/**
 * Restoring note with given name to given folder name
 *
 * @property foldersRepository repository manipulating folders data
 */
class RestoreNote(private val foldersRepository: FoldersRepository) {

    suspend operator fun invoke(noteName: String, noteFolder: String) =
        foldersRepository.changeNoteFolder(noteName = noteName, noteFolder = noteFolder)

}