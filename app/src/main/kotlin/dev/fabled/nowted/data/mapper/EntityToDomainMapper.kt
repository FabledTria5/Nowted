package dev.fabled.nowted.data.mapper

import dev.fabled.nowted.data.db.entities.FolderEntity
import dev.fabled.nowted.data.db.entities.NoteEntity
import dev.fabled.nowted.domain.model.FolderModel
import dev.fabled.nowted.domain.model.NoteModel

/**
 * Mapping list of [NoteEntity] to list of [NoteModel]
 */
fun List<NoteEntity>.toNotesModels(): List<NoteModel> = map { it.toNoteModel() }

/**
 * Mapping list of [FolderEntity] to list of [FolderModel]
 */
fun List<FolderEntity>.toFoldersModels(): List<FolderModel> = map { it.toFolderModel() }

/**
 * Mapping single [FolderEntity] to [FolderModel]
 */
fun FolderEntity.toFolderModel(): FolderModel =
    FolderModel(folderName = folderName, isSystemFolder = !isPrimary)

/**
 * Mapping single [NoteEntity] to [NoteModel]
 */
fun NoteEntity.toNoteModel(): NoteModel = NoteModel(
    noteTitle = noteName,
    noteText = noteText,
    noteDate = createdAt,
    noteFolder = parentFolder,
    textSize = textSize,
    fontWeight = fontWeight,
    paragraph = paragraph,
    fontStyle = fontStyle,
    isUnderline = isUnderline,
    isFavorite = isFavorite
)