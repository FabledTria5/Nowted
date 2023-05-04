package dev.fabled.nowted.presentation.core

import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.SoftwareKeyboardController
import timber.log.Timber

/**
 * Shows snackbar
 *
 * @param snackbarHostState [SnackbarHostState] used to attach snackbar to Scaffold
 * @param message message to be displayed
 * @param actionLabel action label for custom action
 * @param softwareKeyboardController [SoftwareKeyboardController] used to close keyboard before
 * show snackbar
 *
 * @return [SnackbarResult]
 */
@ExperimentalComposeUiApi
suspend fun snackBar(
    snackbarHostState: SnackbarHostState,
    message: String,
    actionLabel: String? = null,
    softwareKeyboardController: SoftwareKeyboardController? = null
): SnackbarResult {
    Timber.d(message = message)

    softwareKeyboardController?.hide()
    return snackbarHostState.showSnackbar(message = message, actionLabel = actionLabel)
}