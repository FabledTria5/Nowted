package dev.fabled.nowted.presentation.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.Navigator
import dev.fabled.nowted.presentation.ui.navigation.transitions.SlideTransition
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreen
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreenContent
import dev.fabled.nowted.presentation.ui.screens.note.EmptyNoteContent
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreenContent
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreenContentState
import dev.fabled.nowted.presentation.ui.screens.note.RestoreNoteContent
import dev.fabled.nowted.presentation.ui.screens.notes_list.NotesListScreenContent
import dev.fabled.nowted.presentation.ui.theme.SecondaryBackground
import dev.fabled.nowted.presentation.utils.WindowType
import dev.fabled.nowted.presentation.utils.rememberWindowSize
import dev.fabled.nowted.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

/**
 * Resolves navigation method depending on window size provided by [rememberWindowSize].
 * If window size is compact launches navigation, where every screen is presented as Voyager screen
 * class. Else launches navigation, when all app screens are presented at one time. Second approach
 * does not use any Voyager navigation library
 */
@Composable
fun SetupNavigation() {
    val windowSize = rememberWindowSize()

    when (windowSize.width) {
        WindowType.Compact -> CompactNavigation()
        else -> ExpandedNavigation()
    }
}

/**
 * Launches navigation for compact screens using Voyager library
 */
@Composable
private fun CompactNavigation() {
    Navigator(HomeScreen()) { navigator ->
        SlideTransition(navigator = navigator, modifier = Modifier.systemBarsPadding())
    }
}

/**
 * Launches navigation for expanded screens. In this case navigation is handling only by changing
 * screens states in [MainViewModel]. Voyager library is not used in this case
 */
@Composable
private fun ExpandedNavigation(mainViewModel: MainViewModel = koinViewModel()) {
    val homeScreenState by mainViewModel.homeScreenState.collectAsStateWithLifecycle()
    val notesListScreenState by mainViewModel.notesListScreenState.collectAsStateWithLifecycle()
    val notesScreenState by mainViewModel.noteScreenState.collectAsStateWithLifecycle()

    val messagesFlow = mainViewModel.messagesFlow

    val snackbarHostState = remember { SnackbarHostState() }

    Row(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        HomeScreenContent(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = .25f)
                .padding(top = 30.dp),
            homeScreenState = homeScreenState,
            onScreenEvent = mainViewModel::onHomeScreenEvent
        )
        NotesListScreenContent(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = .30f)
                .background(SecondaryBackground)
                .padding(top = 30.dp),
            screenState = notesListScreenState,
            onScreenEvent = mainViewModel::onNotesListScreenEvent
        )
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            when (notesScreenState.contentState) {
                NoteScreenContentState.NOTE_NOT_SELECTED ->
                    EmptyNoteContent(modifier = Modifier.fillMaxSize())

                NoteScreenContentState.NOTE_OPENED -> NoteScreenContent(
                    modifier = Modifier
                        .padding(padding)
                        .padding(vertical = 50.dp)
                        .fillMaxSize(),
                    screenState = notesScreenState,
                    onScreenEvent = mainViewModel::onNoteScreenEvent
                )

                NoteScreenContentState.NOTE_RESTORING -> RestoreNoteContent(
                    noteName = notesScreenState.deletedNoteName,
                    onScreenEvent = mainViewModel::onNoteScreenEvent
                )
            }
        }
    }

    LaunchedEffect(key1 = mainViewModel) {
        messagesFlow.collectLatest { message ->
            Timber.d(message = "Message received: $message")
            snackbarHostState.showSnackbar(message = message)
        }
    }
}