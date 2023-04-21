package dev.fabled.nowted.domain.use_cases.notes

import dev.fabled.nowted.domain.repository.NotesRepository

class CollectNotes(private val notesRepository: NotesRepository) {

    operator fun invoke(folderName: String) =
        notesRepository.getNotesFromFolder(folderName = folderName)

}