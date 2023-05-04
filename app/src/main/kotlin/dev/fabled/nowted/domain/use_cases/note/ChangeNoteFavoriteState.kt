package dev.fabled.nowted.domain.use_cases.note

import dev.fabled.nowted.domain.model.Resource
import dev.fabled.nowted.domain.repository.NotesRepository

class ChangeNoteFavoriteState(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(noteName: String, isFavorite: Boolean): Resource<Boolean> {
        return try {
            notesRepository.toggleFavoriteState(noteName, !isFavorite)
            Resource.Success(!isFavorite)
        } catch (e: Exception) {
            Resource.Error(error = e.message ?: e.stackTraceToString())
        }
    }

}