package dev.fabled.nowted.presentation.mapper

import androidx.compose.ui.unit.sp
import dev.fabled.nowted.domain.model.NoteModel
import dev.fabled.nowted.presentation.model.UiNote
import dev.fabled.nowted.presentation.utils.getDecoration
import dev.fabled.nowted.presentation.utils.getTextStyle
import dev.fabled.nowted.presentation.utils.getTextWeight
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

fun List<NoteModel>.toUiNotesList(): ImmutableList<UiNote> =
    map { it.toUiModel() }.toImmutableList()

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
    isFavorite = isFavorite
)