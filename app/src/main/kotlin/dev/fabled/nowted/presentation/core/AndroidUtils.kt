package dev.fabled.nowted.presentation.core

import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import timber.log.Timber

suspend fun snackBar(
    snackbarHostState: SnackbarHostState,
    message: String,
    actionLabel: String? = null
): SnackbarResult {
    Timber.d(message = message)

    return snackbarHostState.showSnackbar(message = message, actionLabel = actionLabel)
}