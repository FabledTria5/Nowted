package dev.fabled.nowted.presentation.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.Navigator
import dev.fabled.nowted.presentation.ui.navigation.manager.NavigationManager
import dev.fabled.nowted.presentation.ui.navigation.transitions.SlideTransition
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreen
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreenContent
import dev.fabled.nowted.presentation.ui.screens.note.EmptyNoteContent
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreenContent
import dev.fabled.nowted.presentation.ui.screens.notes_list.NotesListScreenContent
import dev.fabled.nowted.presentation.ui.theme.SecondaryBackground
import dev.fabled.nowted.presentation.utils.WindowType
import dev.fabled.nowted.presentation.utils.rememberWindowSize
import dev.fabled.nowted.presentation.utils.replaceIf
import dev.fabled.nowted.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SetupNavigation() {
    val windowSize = rememberWindowSize()

    when (windowSize.width) {
        WindowType.Compact -> CompactNavigation()
        else -> ExpandedNavigation()
    }
}

@Composable
private fun CompactNavigation() {
    Navigator(HomeScreen()) { navigator ->
        navigator.ProcessNavigationCommand()

        SlideTransition(navigator = navigator, modifier = Modifier.systemBarsPadding())
    }
}

@Composable
private fun ExpandedNavigation(mainViewModel: MainViewModel = koinViewModel()) {
    val homeScreenState by mainViewModel.homeScreenState.collectAsStateWithLifecycle()
    val notesListScreenState by mainViewModel.notesListScreenState.collectAsStateWithLifecycle()
    val notesScreenState by mainViewModel.noteScreenState.collectAsStateWithLifecycle()

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
        if (notesScreenState.isNoteOpened) {
            NoteScreenContent(
                modifier = Modifier.padding(vertical = 50.dp),
                screenState = notesScreenState,
                onScreenEvent = mainViewModel::onNoteScreenEvent
            )
        } else {
            EmptyNoteContent()
        }
    }
}

@Composable
private fun Navigator.ProcessNavigationCommand(
    navigationManager: NavigationManager = koinInject()
) {
    LaunchedEffect(key1 = navigationManager) {
        navigationManager.navigationCommand.collectLatest { command ->
            when (command) {
                NavigationCommand.NavigateBack -> pop()
                is NavigationCommand.Navigate -> push(command.screen)
                is NavigationCommand.Replace -> replaceIf(
                    destination = command.screen,
                    block = { command.screen.key != lastItem.key }
                )
            }
        }
    }
}