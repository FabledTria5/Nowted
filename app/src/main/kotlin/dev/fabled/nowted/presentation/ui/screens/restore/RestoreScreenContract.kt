package dev.fabled.nowted.presentation.ui.screens.restore

import androidx.compose.runtime.Stable
import dev.fabled.nowted.presentation.core.viewmodel.UnidirectionalViewModel

/**
 * Realization of [UnidirectionalViewModel], that creates contract for home screen.
 */
interface RestoreScreenContract :
    UnidirectionalViewModel<RestoreScreenContract.State, RestoreScreenContract.Event, RestoreScreenContract.Effect> {

    /**
     * Represents state of restore screen
     *
     * @property deletedNoteName deleted note name
     * @property noteFolderName deleted note folder name
     */
    @Stable
    data class State(
        val deletedNoteName: String = "",
        val noteFolderName: String = ""
    )

    sealed class Event {

        /**
         * Set data to UI
         *
         * @property deletedNoteName deleted note name
         * @property noteFolderName deleted note folder name
         */
        data class SetData(
            val deletedNoteName: String = "",
            val noteFolderName: String = ""
        ) : Event()

        /**
         * Called, when user intends to restore note
         */
        object RestoreNote : Event()
    }

    sealed class Effect {
        /**
         * Should be called after restoring note to perform navigation to note screen
         */
        object NoteRestored : Effect()
    }

}