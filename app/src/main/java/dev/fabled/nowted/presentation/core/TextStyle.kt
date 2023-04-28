package dev.fabled.nowted.presentation.core

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.style.TextDecoration

@Stable
enum class NoteTextWeight(val weight: Int) {
    Normal(weight = 400) {
        override val next: NoteTextWeight
            get() = Bold
    },
    Bold(weight = 700) {
        override val next: NoteTextWeight
            get() = Normal
    };

    abstract val next: NoteTextWeight
}

fun Int.getTextWeight(): NoteTextWeight = when (this) {
    700 -> NoteTextWeight.Bold
    else -> NoteTextWeight.Normal
}

@Stable
enum class NoteTextStyle(val value: Int) {
    Normal(value = 0) {
        override val next: NoteTextStyle
            get() = Italic
    },
    Italic(value = 1) {
        override val next: NoteTextStyle
            get() = Normal
    };

    abstract val next: NoteTextStyle
}

fun Int.getTextStyle(): NoteTextStyle = when(this) {
    1 -> NoteTextStyle.Italic
    else -> NoteTextStyle.Normal
}

@Stable
enum class NoteTextDecoration(val decoration: TextDecoration) {
    None(decoration = TextDecoration.None) {
        override val next: NoteTextDecoration
            get() = Underline
    },
    Underline(decoration = TextDecoration.Underline) {
        override val next: NoteTextDecoration
            get() = None
    };

    abstract val next: NoteTextDecoration
}

fun Boolean.getDecoration(): NoteTextDecoration = when(this) {
    true -> NoteTextDecoration.Underline
    false -> NoteTextDecoration.None
}