package dev.fabled.nowted.domain.use_cases.home

import dev.fabled.nowted.domain.repository.NotesRepository

/**
 * Collecting recent notes
 *
 * @property notesRepository repository manipulating notes data
 */
class CollectRecents(private val notesRepository: NotesRepository) {

    operator fun invoke() = notesRepository.getRecents(limit = 3)

}