package dev.fabled.nowted.domain.use_cases.folders

import dev.fabled.nowted.domain.repository.NotesRepository

class CreateFolder(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(folderName: String) = notesRepository.createFolder(folderName)

}