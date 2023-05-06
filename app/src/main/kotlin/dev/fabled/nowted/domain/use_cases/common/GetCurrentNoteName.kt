package dev.fabled.nowted.domain.use_cases.common

import dev.fabled.nowted.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow

/**
 * Returns [Flow] containing name of current note
 *
 * @property notesRepository repository manipulating notes data
 */
class GetCurrentNoteName(private val notesRepository: NotesRepository) {

    operator fun invoke() = notesRepository.currentNoteName

}