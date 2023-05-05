package dev.fabled.nowted.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for folder
 *
 * @property id primary key
 * @property folderName name of folder
 * @property isPrimary is this folder primary
 */
@Entity(tableName = "folders_table")
data class FolderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "folder_name")
    val folderName: String,
    @ColumnInfo(name = "is_primary")
    val isPrimary: Boolean = true
)
