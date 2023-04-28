package dev.fabled.nowted.domain.use_cases.common

import dev.fabled.nowted.domain.repository.NotesRepository

class OpenNote(private val notesRepository: NotesRepository) {

    operator fun invoke(noteName: String) {
        notesRepository.openNote(noteName)
    }

}