package dev.fabled.nowted.domain.use_cases.note

import dev.fabled.nowted.domain.model.NoteModel
import dev.fabled.nowted.domain.model.Resource
import dev.fabled.nowted.domain.repository.NotesRepository
import dev.fabled.nowted.domain.utils.errorMessage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class UpdateOrCreateNote(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(noteModel: NoteModel): Resource<Nothing> = coroutineScope {
        return@coroutineScope try {
            noteModel.noteTitle.ifBlank {
                return@coroutineScope Resource.Error(error = "")
            }

            val noteJob = launch {
                notesRepository.createNote(noteModel = noteModel)
                notesRepository.selectNote(noteName = noteModel.noteTitle)
            }

            launch { notesRepository.addRecent(noteName = noteModel.noteTitle) }

            noteJob.join()

            Resource.Completed
        } catch (e: Exception) {
            Resource.Error(error = e.errorMessage)
        }
    }

}