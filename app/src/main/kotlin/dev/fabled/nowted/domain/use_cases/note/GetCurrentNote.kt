package dev.fabled.nowted.domain.use_cases.note

import dev.fabled.nowted.domain.repository.NotesRepository

/**
 * Collecting current note name
 *
 * @property notesRepository repository manipulating notes data
 */
class GetCurrentNote(private val notesRepository: NotesRepository) {

    operator fun invoke(noteName: String) = notesRepository.getNoteByName(noteName)

}