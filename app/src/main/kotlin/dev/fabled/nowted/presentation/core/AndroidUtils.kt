package dev.fabled.nowted.presentation.core

import android.app.Activity
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import timber.log.Timber

/**
 * Shows snackbar
 */
@ExperimentalComposeUiApi
suspend fun snackBar(
    snackbarHostState: SnackbarHostState,
    softwareKeyboardController: SoftwareKeyboardController? = null,
    message: String,
    actionLabel: String? = null
): SnackbarResult {
    Timber.d(message = message)

    softwareKeyboardController?.hide()
    return snackbarHostState.showSnackbar(message = message, actionLabel = actionLabel)
}