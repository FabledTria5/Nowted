package dev.fabled.nowted.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "notes_table")
data class NoteEntity(
    @PrimaryKey
    @ColumnInfo(name = "note_name")
    val noteName: String,
    @ColumnInfo(name = "note_text")
    val noteText: String,
    @ColumnInfo(name = "paragraph")
    val paragraph: Float,
    @ColumnInfo(name = "text_size")
    val textSize: Float,
    @ColumnInfo(name = "font_weight")
    val fontWeight: Int,
    @ColumnInfo(name = "font_style")
    val fontStyle: Int,
    @ColumnInfo(name = "is_underline")
    val isUnderline: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @ColumnInfo(name = "parent_folder")
    val parentFolder: String,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean
)
