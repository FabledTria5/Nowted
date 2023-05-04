package dev.fabled.nowted.domain.use_cases.note

import dev.fabled.nowted.domain.repository.FoldersRepository

class ArchiveNote(private val foldersRepository: FoldersRepository) {

    suspend operator fun invoke(noteName: String) {
        foldersRepository.changeNoteFolder(noteName = noteName, noteFolder = "Archived Notes")
    }

}