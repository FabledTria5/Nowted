package dev.fabled.nowted.domain.use_cases.home

import dev.fabled.nowted.domain.repository.NotesRepository

/**
 * Creating recent note
 *
 * @property notesRepository repository manipulating notes data
 */
class AddRecent(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(noteName: String) = notesRepository.addRecent(noteName)

}