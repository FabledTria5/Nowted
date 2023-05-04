package dev.fabled.nowted.presentation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import dev.fabled.nowted.R

/**
 * Ui representation of system folders
 */
@Stable
sealed class MoreItem(
    @DrawableRes val icon: Int,
    @StringRes val name: Int,
    val folderName: String
) {

    object Favorites :
        MoreItem(icon = R.drawable.ic_favorite, name = R.string.favorites, folderName = "Favorites")

    object Trash : MoreItem(icon = R.drawable.ic_trash, name = R.string.trash, folderName = "Trash")

    object ArchivedNotes : MoreItem(
        icon = R.drawable.ic_trash,
        name = R.string.archived_notes,
        folderName = "Archived Notes"
    )

    companion object {
        fun getItems() = listOf(Favorites, Trash, ArchivedNotes)
    }

}