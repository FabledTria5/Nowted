package dev.fabled.nowted.domain.use_cases.common

import dev.fabled.nowted.domain.repository.NotesRepository

/**
 * Updates current note name with given name
 *
 * @property notesRepository repository manipulating notes data
 */
class OpenNote(private val notesRepository: NotesRepository) {

    operator fun invoke(noteName: String) = notesRepository.selectNote(noteName)

}