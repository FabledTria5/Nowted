package dev.fabled.nowted.domain.use_cases.common

import dev.fabled.nowted.domain.repository.NotesRepository

class GetCurrentFolderName(private val notesRepository: NotesRepository) {

    operator fun invoke() = notesRepository.currentFolderName

}