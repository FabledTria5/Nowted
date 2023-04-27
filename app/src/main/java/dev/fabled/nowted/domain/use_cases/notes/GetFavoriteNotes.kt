package dev.fabled.nowted.domain.use_cases.notes

import dev.fabled.nowted.domain.repository.NotesRepository

class GetFavoriteNotes(private val notesRepository: NotesRepository) {

    operator fun invoke() = notesRepository.getFavoriteNotes()

}