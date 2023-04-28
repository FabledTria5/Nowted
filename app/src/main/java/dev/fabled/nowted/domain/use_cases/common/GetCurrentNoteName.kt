package dev.fabled.nowted.domain.use_cases.common

import dev.fabled.nowted.domain.repository.NotesRepository
import kotlinx.coroutines.flow.map

class GetCurrentNoteName(private val notesRepository: NotesRepository) {

    operator fun invoke() = notesRepository.currentNote.map { model ->
        model?.noteTitle.orEmpty()
    }

}