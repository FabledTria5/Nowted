package dev.fabled.nowted.domain.model

/**
 * Model of folder
 *
 * @property folderName name of folder
 * @property isSystemFolder indicates if folder is system and user can not directly create notes
 * inside of them
 */
data class FolderModel(
    val folderName: String = "",
    val isSystemFolder: Boolean = true,
)
