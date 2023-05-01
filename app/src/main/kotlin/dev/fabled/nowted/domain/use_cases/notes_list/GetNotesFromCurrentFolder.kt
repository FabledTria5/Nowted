package dev.fabled.nowted.domain.use_cases.notes_list

import dev.fabled.nowted.domain.model.NoteModel
import dev.fabled.nowted.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow

class GetNotesFromCurrentFolder(private val notesRepository: NotesRepository) {

    operator fun invoke(folderName: String): Flow<List<NoteModel>> =
        notesRepository.getNotesFromFolder(folderName)

}