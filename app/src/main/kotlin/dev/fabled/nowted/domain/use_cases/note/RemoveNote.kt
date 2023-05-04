package dev.fabled.nowted.domain.use_cases.note

import dev.fabled.nowted.domain.model.Resource
import dev.fabled.nowted.domain.repository.FoldersRepository
import dev.fabled.nowted.domain.repository.NotesRepository
import dev.fabled.nowted.domain.utils.errorMessage

class RemoveNote(
    private val notesRepository: NotesRepository,
    private val foldersRepository: FoldersRepository
) {

    suspend operator fun invoke(noteName: String, noteFolder: String): Resource<Boolean> {
        return try {
            if (noteFolder == "Trash") {
                notesRepository.deleteNote(noteName)
                Resource.Success(data = false)
            } else {
                foldersRepository.changeNoteFolder(noteName = noteName, noteFolder = "Trash")
                Resource.Success(data = true)
            }
        } catch (e: Exception) {
            Resource.Error(error = e.errorMessage)
        }
    }

}