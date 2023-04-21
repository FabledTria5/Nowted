package dev.fabled.nowted.domain.use_cases.notes

import dev.fabled.nowted.domain.model.NoteModel
import dev.fabled.nowted.domain.model.Resource
import dev.fabled.nowted.domain.repository.NotesRepository

class SaveNote(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(noteModel: NoteModel): Resource<Nothing> {
        if (noteModel.noteTitle.isBlank()) return Resource.Error(error = "Note title can't be empty")

        notesRepository.createNote(noteModel)

        return Resource.Completed
    }

}