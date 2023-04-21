package dev.fabled.nowted.domain.use_cases.folders

import dev.fabled.nowted.domain.repository.NotesRepository

class CollectFolders(private val notesRepository: NotesRepository) {

    operator fun invoke() = notesRepository.getFoldersList()

}