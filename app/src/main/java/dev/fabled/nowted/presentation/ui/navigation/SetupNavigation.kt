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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import dev.fabled.nowted.presentation.core.WindowType
import dev.fabled.nowted.presentation.core.collectInLaunchedEffect
import dev.fabled.nowted.presentation.core.rememberWindowSize
import dev.fabled.nowted.presentation.core.showSnackBar
import dev.fabled.nowted.presentation.core.use
import dev.fabled.nowted.presentation.ui.navigation.transitions.SlideTransition
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreen
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreenContent
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreenContract
import dev.fabled.nowted.presentation.ui.screens.home.HomeViewModel
import dev.fabled.nowted.presentation.ui.screens.note.EmptyNoteContent
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreenContent
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreenContract
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreenContract.State.NoteScreenContentState
import dev.fabled.nowted.presentation.ui.screens.note.NoteViewModel
import dev.fabled.nowted.presentation.ui.screens.note.RestoreNoteContent
import dev.fabled.nowted.presentation.ui.screens.notes_list.NotesListScreenContent
import dev.fabled.nowted.presentation.ui.screens.notes_list.NotesListScreenContract
import dev.fabled.nowted.presentation.ui.screens.notes_list.NotesListViewModel
import dev.fabled.nowted.presentation.ui.theme.SecondaryBackground
import org.koin.androidx.compose.koinViewModel

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
 * Launches navigation for expanded screens
 */
@Composable
private fun ExpandedNavigation() {
    Row(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        HomeScreenRoute(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = .25f)
                .padding(top = 30.dp)
        )
        NotesListScreenRoute(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = .3f)
                .background(SecondaryBackground)
                .padding(top = 30.dp)
        )
        NoteScreenRoute(
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun HomeScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val (state, event, effect) = use(viewModel = viewModel)

    val snackbarHostState = remember { SnackbarHostState() }

    effect.collectInLaunchedEffect {
        when (it) {
            HomeScreenContract.Effect.FolderCreated -> showSnackBar(
                snackbarHostState = snackbarHostState,
                message = "Created new folder!"
            )
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        HomeScreenContent(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            state = state,
            onFolderClick = { folderName ->
                event.invoke(HomeScreenContract.Event.SelectFolder(folderName))
            },
            onRecentClick = { noteName ->
                event.invoke(HomeScreenContract.Event.OpenNote(noteName))
            },
            onToggleSearch = {
                event.invoke(HomeScreenContract.Event.ToggleSearch)
            },
            onNewNoteClick = {
                event.invoke(HomeScreenContract.Event.OpenNote())
            },
            onSearchClick = { },
            onSearchQueryChange = { query ->
                event.invoke(HomeScreenContract.Event.UpdateSearchQuery(query))
            },
            onStartCreateNewFolderClick = {
                event.invoke(HomeScreenContract.Event.OnStartCreateNewFolder)
            },
            onCreateNewFolderClick = { folderName ->
                event.invoke(HomeScreenContract.Event.CreateFolder(folderName))
            }
        )
    }
}

@Composable
private fun NotesListScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: NotesListViewModel = koinViewModel()
) {
    val (state, event) = use(viewModel = viewModel)

    NotesListScreenContent(
        modifier = modifier,
        screenState = state,
        onNewItemClick = { event.invoke(NotesListScreenContract.Event.OnCreateNote) },
        onNoteClick = { noteName ->
            event.invoke(NotesListScreenContract.Event.OnNoteClick(noteName))
        }
    )
}

@Composable
fun NoteScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel = koinViewModel()
) {
    val (state, event, effect) = use(viewModel = viewModel)

    val snackbarHostState = remember { SnackbarHostState() }

    effect.collectInLaunchedEffect {
        when (it) {
            NoteScreenContract.Effect.AddedToFavorite -> showSnackBar(
                snackbarHostState = snackbarHostState,
                message = "Added to favorites!"
            )

            NoteScreenContract.Effect.FavoriteFailure -> showSnackBar(
                snackbarHostState = snackbarHostState,
                message = "Note does not exist!"
            )

            NoteScreenContract.Effect.NoteDeleteError -> showSnackBar(
                snackbarHostState = snackbarHostState,
                message = "Can not delete non existing note!"
            )

            NoteScreenContract.Effect.NoteDeleted -> showSnackBar(
                snackbarHostState = snackbarHostState,
                message = "Note has been deleted!"
            )

            NoteScreenContract.Effect.NoteRemoved -> showSnackBar(
                snackbarHostState = snackbarHostState,
                message = "Note has been saved!"
            )

            NoteScreenContract.Effect.NoteRestored -> showSnackBar(
                snackbarHostState = snackbarHostState,
                message = "Note has been restored!"
            )

            NoteScreenContract.Effect.NoteSaveError -> showSnackBar(
                snackbarHostState = snackbarHostState,
                message = "Error while saving note"
            )

            NoteScreenContract.Effect.NoteSaved -> showSnackBar(
                snackbarHostState = snackbarHostState,
                message = "Note has been saved!"
            )
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when (state.contentState) {
            NoteScreenContentState.NOTE_NOT_SELECTED ->
                EmptyNoteContent(modifier = Modifier.fillMaxSize())

            NoteScreenContentState.NOTE_OPENED -> NoteScreenContent(
                modifier = Modifier
                    .padding(padding)
                    .padding(vertical = 50.dp)
                    .fillMaxSize(),
                state = state,
                onEvent = { event.invoke(it) }
            )

            NoteScreenContentState.NOTE_RESTORING -> RestoreNoteContent(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxSize(),
                noteName = state.deletedNoteName,
                onRestoreNote = { event.invoke(NoteScreenContract.Event.RestoreNote) }
            )
        }
    }
}