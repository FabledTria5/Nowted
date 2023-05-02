package dev.fabled.nowted.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.fabled.nowted.data.db.dao.NotesDao
import dev.fabled.nowted.data.db.entities.FolderEntity
import dev.fabled.nowted.data.db.entities.NoteEntity
import dev.fabled.nowted.data.db.entities.RecentEntity

@Database(
    entities = [FolderEntity::class, NoteEntity::class, RecentEntity::class],
    version = 4,
    exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

}