package dev.fabled.nowted.domain.use_cases.note

import dev.fabled.nowted.domain.model.Resource
import dev.fabled.nowted.domain.repository.NotesRepository

class ChangeNoteFavoriteState(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(noteName: String, isFavorite: Boolean): Resource<Boolean> {
        if (!notesRepository.isNoteExists(noteName))
            return Resource.Error(error = "You should save note before mark it as favorite!")

        notesRepository.toggleFavoriteState(noteName, !isFavorite)

        return Resource.Success(!isFavorite)
    }

}