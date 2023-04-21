package dev.fabled.nowted.data.utils

import android.content.Context
import android.os.Environment
import androidx.datastore.preferences.preferencesDataStore
import java.io.File

val Context.dataStore by preferencesDataStore(name = "APP_PREFERENCES")

fun Context.getFoldersDirectory(): File =
    File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + Constants.FOLDERS_DIRECTORY)

fun Context.getAdditionsDirectory(): File =
    File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + Constants.ADDITIONS_DIRECTORY)