package dev.fabled.nowted.domain.use_cases.notes

import dev.fabled.nowted.domain.repository.NotesRepository

class DeleteNote(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(noteName: String, noteFolder: String): Boolean {
        return if (noteFolder == "Trash") {
            notesRepository.deleteNote(noteName)
            false
        } else {
            notesRepository.changeNoteFolder(noteName = noteName, noteFolder = "Trash")
            true
        }
    }

}