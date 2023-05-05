package dev.fabled.nowted.presentation.ui.screens.restore

import androidx.compose.runtime.Stable
import dev.fabled.nowted.presentation.core.viewmodel.UnidirectionalViewModel

interface RestoreScreenContract :
    UnidirectionalViewModel<RestoreScreenContract.State, RestoreScreenContract.Event, RestoreScreenContract.Effect> {

    @Stable
    data class State(
        val deletedNoteName: String = "",
        val noteFolderName: String = ""
    )

    sealed class Event {
        data class SetData(
            val deletedNoteName: String = "",
            val noteFolderName: String = ""
        ) : Event()

        object RestoreNote : Event()
    }

    sealed class Effect {
        object NoteRestored : Effect()
    }

}