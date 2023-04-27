package dev.fabled.nowted.domain.model

data class NoteModel(
    val noteTitle: String,
    val noteText: String,
    val noteDate: String,
    val noteFolder: String,
    val textSize: Float,
    val paragraph: Float,
    val fontWeight: Int,
    val fontStyle: Int,
    val isUnderline: Boolean,
    val isFavorite: Boolean
)