package dev.fabled.nowted.presentation.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import dev.fabled.nowted.presentation.core.NoteTextDecoration
import dev.fabled.nowted.presentation.core.NoteTextStyle
import dev.fabled.nowted.presentation.core.NoteTextWeight
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Ui representation of note
 */
@Stable
data class UiNote(
    val noteTitle: String = "",
    val noteText: String = "",
    val noteFolder: String = "",
    val isFavorite: Boolean = false,
    val paragraph: TextUnit = 28.sp,
    val textSize: TextUnit = 16.sp,
    val fontWeight: NoteTextWeight = NoteTextWeight.Normal,
    val textStyle: NoteTextStyle = NoteTextStyle.Normal,
    val textDecoration: NoteTextDecoration = NoteTextDecoration.None,
    val noteDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
    val isNoteExists: Boolean = false
)