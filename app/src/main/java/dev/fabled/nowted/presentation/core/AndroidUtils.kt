package dev.fabled.nowted.presentation.core

import androidx.compose.material.SnackbarHostState
import timber.log.Timber

suspend fun showSnackBar(snackbarHostState: SnackbarHostState, message: String) {
    Timber.d(message = message)
    snackbarHostState.showSnackbar(message)
}