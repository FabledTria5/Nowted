package dev.fabled.nowted.domain.use_cases.note

import dev.fabled.nowted.domain.model.Resource
import dev.fabled.nowted.domain.model.SystemFolders
import dev.fabled.nowted.domain.repository.FoldersRepository
import dev.fabled.nowted.domain.repository.NotesRepository
import dev.fabled.nowted.domain.utils.errorMessage

/**
 * For note with given name checks if it is already in [SystemFolders.TRASH]. If it is, then fully
 * deletes note, else put it in [SystemFolders.TRASH]
 *
 * @property notesRepository repository to manipulate notes data
 * @property foldersRepository repository to manipulate folders data
 */
class RemoveNote(
    private val notesRepository: NotesRepository,
    private val foldersRepository: FoldersRepository
) {
    suspend operator fun invoke(noteName: String, noteFolder: String): Resource<Boolean> {
        return try {
            if (noteFolder == SystemFolders.TRASH.folderName) {
                notesRepository.deleteNote(noteName)
                Resource.Success(data = false)
            } else {
                foldersRepository.changeNoteFolder(
                    noteName = noteName,
                    noteFolder = SystemFolders.TRASH.folderName
                )
                Resource.Success(data = true)
            }
        } catch (e: Exception) {
            Resource.Error(error = e.errorMessage)
        }
    }

}