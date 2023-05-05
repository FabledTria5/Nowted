package dev.fabled.nowted.domain.model

/**
 * Model of note
 *
 * @property noteTitle name of note
 * @property noteText note text
 * @property noteDate note creating date
 * @property noteFolder folder where note belongs
 * @property textSize size of text
 * @property paragraph paragraph size
 * @property fontWeight font weight
 * @property fontStyle style of text
 * @property isUnderline is text underlined
 * @property isFavorite is note favorite
 */
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