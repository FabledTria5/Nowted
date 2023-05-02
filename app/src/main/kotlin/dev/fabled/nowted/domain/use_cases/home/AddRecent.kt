package dev.fabled.nowted.domain.use_cases.home

import dev.fabled.nowted.domain.repository.NotesRepository

class AddRecent(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(noteName: String) {
        notesRepository.addRecent(noteName)
    }

}