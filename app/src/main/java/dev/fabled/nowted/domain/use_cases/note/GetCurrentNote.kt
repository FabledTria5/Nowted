package dev.fabled.nowted.domain.use_cases.note

import dev.fabled.nowted.domain.repository.NotesRepository

class GetCurrentNote(private val notesRepository: NotesRepository) {

    operator fun invoke() = notesRepository.currentNote

}