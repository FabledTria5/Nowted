package dev.fabled.nowted.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class FolderWithNotes(
    @Embedded val folder: FolderEntity,
    @Relation(
        parentColumn = "folder_name",
        entityColumn = "parent_folder"
    )
    val notes: List<NoteEntity>
)