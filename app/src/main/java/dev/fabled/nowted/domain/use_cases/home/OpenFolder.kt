package dev.fabled.nowted.domain.use_cases.home

import dev.fabled.nowted.domain.repository.NotesRepository

class OpenFolder(private val notesRepository: NotesRepository) {

    operator fun invoke(folderName: String) {
        notesRepository.openFolder(folderName)
    }

}