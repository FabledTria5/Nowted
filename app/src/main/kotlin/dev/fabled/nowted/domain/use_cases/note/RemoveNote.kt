package dev.fabled.nowted.domain.use_cases.note

import dev.fabled.nowted.domain.model.Resource
import dev.fabled.nowted.domain.repository.NotesRepository

class RemoveNote(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(noteName: String, noteFolder: String): Resource<Boolean> {
        return if (noteFolder == "Trash") {
            notesRepository.deleteNote(noteName)
            Resource.Success(data = false)
        } else {
            notesRepository.changeNoteFolder(noteName = noteName, noteFolder = "Trash")
            Resource.Success(data = true)
        }
    }

}