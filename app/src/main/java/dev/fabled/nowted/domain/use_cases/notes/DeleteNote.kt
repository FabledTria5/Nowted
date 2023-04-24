package dev.fabled.nowted.domain.use_cases.notes

import dev.fabled.nowted.domain.model.Resource
import dev.fabled.nowted.domain.repository.NotesRepository

class DeleteNote(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(noteName: String): Resource<Boolean> {
        val noteFolder = notesRepository.getNote(noteName)?.noteFolder

        if (noteFolder == null)
            Resource.Failure

        return if (noteFolder == "Trash") {
            notesRepository.deleteNote(noteName)
            Resource.Success(data = false)
        } else {
            notesRepository.changeNoteFolder(noteName = noteName, noteFolder = "Trash")
            Resource.Success(data = true)
        }
    }

}