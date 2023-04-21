package dev.fabled.nowted.presentation.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import dev.fabled.nowted.presentation.utils.NoteTextDecoration
import dev.fabled.nowted.presentation.utils.NoteTextStyle
import dev.fabled.nowted.presentation.utils.NoteTextWeight

@Stable
data class UiNote(
    val noteTitle: String = "",
    val noteText: String = "",
    val noteFolder: String = "",
    val paragraph: TextUnit = 28.sp,
    val textSize: TextUnit = 16.sp,
    val fontWeight: NoteTextWeight = NoteTextWeight.Normal,
    val textStyle: NoteTextStyle = NoteTextStyle.Normal,
    val textDecoration: NoteTextDecoration = NoteTextDecoration.None,
    val noteDate: String = ""
)