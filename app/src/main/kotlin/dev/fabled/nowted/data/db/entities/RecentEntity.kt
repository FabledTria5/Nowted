package dev.fabled.nowted.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for recent note
 *
 * @property noteName name of note
 */
@Entity(tableName = "recent_notes")
data class RecentEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "note_name")
    val noteName: String
)