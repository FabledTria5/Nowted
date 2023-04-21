package dev.fabled.nowted.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import dev.fabled.nowted.data.db.dao.NotesDao
import dev.fabled.nowted.data.db.entities.FolderEntity
import dev.fabled.nowted.data.db.entities.NoteEntity
import java.time.LocalDate

@Database(entities = [FolderEntity::class, NoteEntity::class], version = 2, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

}