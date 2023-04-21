package dev.fabled.nowted.domain.use_cases.notes

import dev.fabled.nowted.domain.model.NoteModel
import dev.fabled.nowted.domain.repository.NotesRepository

class GetNote(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(noteName: String): NoteModel =
        notesRepository.getNote(name = noteName)

}