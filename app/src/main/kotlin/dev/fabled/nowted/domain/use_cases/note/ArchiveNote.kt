package dev.fabled.nowted.domain.use_cases.note

import dev.fabled.nowted.domain.model.SystemFolders
import dev.fabled.nowted.domain.repository.FoldersRepository

/**
 * For note with given name sets it's folder to [SystemFolders.ARCHIVE]
 *
 * @property foldersRepository repository manipulating folders data
 */
class ArchiveNote(private val foldersRepository: FoldersRepository) {

    suspend operator fun invoke(noteName: String) {
        foldersRepository.changeNoteFolder(
            noteName = noteName,
            noteFolder = SystemFolders.ARCHIVE.folderName
        )
    }

}