package dev.fabled.nowted.presentation.mapper

import dev.fabled.nowted.domain.model.NoteModel
import dev.fabled.nowted.presentation.core.NoteTextDecoration
import dev.fabled.nowted.presentation.model.UiNote

fun UiNote.toModel(): NoteModel = NoteModel(
    noteTitle = noteTitle,
    noteText = noteText,
    noteDate = noteDate,
    noteFolder = noteFolder,
    textSize = textSize.value,
    paragraph = paragraph.value,
    fontWeight = fontWeight.weight,
    fontStyle = textStyle.value,
    isUnderline = textDecoration == NoteTextDecoration.Underline,
    isFavorite = isFavorite
)