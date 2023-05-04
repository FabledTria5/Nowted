package dev.fabled.nowted.presentation.mapper

import androidx.compose.ui.unit.sp
import dev.fabled.nowted.domain.model.NoteModel
import dev.fabled.nowted.presentation.core.getDecoration
import dev.fabled.nowted.presentation.core.getTextStyle
import dev.fabled.nowted.presentation.core.getTextWeight
import dev.fabled.nowted.presentation.model.UiNote

/**
 * Map [NoteModel] to [UiNote]
 */
fun NoteModel.toUiModel(): UiNote = UiNote(
    noteTitle = noteTitle,
    noteText = noteText,
    noteFolder = noteFolder,
    paragraph = 28.sp,
    textSize = textSize.sp,
    fontWeight = fontWeight.getTextWeight(),
    textStyle = fontStyle.getTextStyle(),
    textDecoration = isUnderline.getDecoration(),
    noteDate = noteDate,
    isFavorite = isFavorite,
    isNoteExists = true
)