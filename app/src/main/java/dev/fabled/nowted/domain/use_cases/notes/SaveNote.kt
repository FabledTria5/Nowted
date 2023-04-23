package dev.fabled.nowted.domain.use_cases.notes

import dev.fabled.nowted.domain.model.NoteModel
import dev.fabled.nowted.domain.model.Resource
import dev.fabled.nowted.domain.repository.NotesRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SaveNote(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(noteModel: NoteModel): Resource<Nothing> = coroutineScope {
        if (noteModel.noteTitle.isBlank())
            return@coroutineScope Resource.Error(error = "Note title can't be empty")

        launch { notesRepository.createNote(noteModel = noteModel) }

        launch { notesRepository.addRecent(noteName = noteModel.noteTitle) }

        return@coroutineScope Resource.Completed
    }

}