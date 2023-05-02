package dev.fabled.nowted.domain.use_cases.note

import dev.fabled.nowted.domain.repository.FoldersRepository

class RestoreNote(private val foldersRepository: FoldersRepository) {

    suspend operator fun invoke(noteName: String, noteFolder: String) {
        foldersRepository.changeNoteFolder(noteName = noteName, noteFolder = noteFolder)
    }

}