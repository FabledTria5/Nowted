package dev.fabled.nowted.domain.use_cases.note

import dev.fabled.nowted.domain.repository.NotesRepository

class RestoreNote(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(noteName: String, noteFolder: String) {
        notesRepository.changeNoteFolder(noteName = noteName, noteFolder = noteFolder)
    }

}