package dev.fabled.nowted.domain.use_cases.recents

import dev.fabled.nowted.domain.repository.NotesRepository

class CollectRecents(private val notesRepository: NotesRepository) {

    operator fun invoke() = notesRepository.getRecents(limit = 3)

}