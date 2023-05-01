package dev.fabled.nowted.data.mapper

import dev.fabled.nowted.data.db.entities.NoteEntity
import dev.fabled.nowted.domain.model.NoteModel

fun NoteModel.toEntity(): NoteEntity = NoteEntity(
    noteName = noteTitle,
    noteText = noteText,
    paragraph = paragraph,
    textSize = textSize,
    fontWeight = fontWeight,
    fontStyle = fontStyle,
    isUnderline = isUnderline,
    createdAt = noteDate,
    parentFolder = noteFolder,
    isFavorite = isFavorite
)