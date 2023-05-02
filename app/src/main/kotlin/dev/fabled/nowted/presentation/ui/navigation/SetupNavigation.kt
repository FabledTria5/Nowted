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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.platform.multiplatformName
import cafe.adriel.voyager.navigator.Navigator
import dev.fabled.nowted.presentation.core.WindowType
import dev.fabled.nowted.presentation.core.collectInLaunchedEffect
import dev.fabled.nowted.presentation.core.distinctReplace
import dev.fabled.nowted.presentation.core.rememberWindowSize
import dev.fabled.nowted.presentation.core.snackBar
import dev.fabled.nowted.presentation.core.use
import dev.fabled.nowted.presentation.ui.navigation.transitions.FadeTransition
import dev.fabled.nowted.presentation.ui.navigation.transitions.SlideTransition
import dev.fabled.nowted.presentation.ui.screens.empty.EmptyScreen
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreen
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreenContent
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreenContract
import dev.fabled.nowted.presentation.ui.screens.home.HomeViewModel
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreen
import dev.fabled.nowted.presentation.ui.screens.notes_list.NotesListScreenContent
import dev.fabled.nowted.presentation.ui.screens.notes_list.NotesListScreenContract
import dev.fabled.nowted.presentation.ui.screens.notes_list.NotesListViewModel
import dev.fabled.nowted.presentation.ui.screens.restore.RestoreNoteScreen
import dev.fabled.nowted.presentation.ui.theme.SecondaryBackground
import org.koin.androidx.compose.koinViewModel

/**
 * Resolves navigation method depending on window size provided by [rememberWindowSize].
 * If window size is compact launches navigation, where every screen is presented as Voyager screen
 * class. Else launches navigation, when all app screens are presented at one time.
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
 * Launches navigation for expanded screens. Navigation in this case only used in note section.
 */
@Composable
private fun ExpandedNavigation() {
    Navigator(EmptyScreen()) { navigator ->
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxSize()
        ) {
            HomeScreenRoute(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = .25f)
                    .padding(top = 30.dp),
                openNote = {
                    navigator.distinctReplace(screen = NoteScreen()) {
                        lastItem.key == EmptyScreen::class.multiplatformName
                                || lastItem.key == RestoreNoteScreen::class.multiplatformName
                    }
                },
            )
            NotesListScreenRoute(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = .3f)
                    .background(SecondaryBackground)
                    .padding(top = 30.dp),
                openNote = { noteName ->
                    navigator.distinctReplace(screen = NoteScreen(noteName)) {
                        lastItem.key == EmptyScreen::class.multiplatformName
                                || lastItem.key == RestoreNoteScreen::class.multiplatformName
                    }
                }
            )
            FadeTransition(
                navigator = navigator,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


/**
 * Represents Home screen route for expanded navigation.
 *
 * @param openNote callback for expanded navigation to open note edit screen in note section
 * @param modifier a [Modifier] that applies for [HomeScreenContent]
 * @param viewModel [HomeViewModel] injected with Koin
 */
@Composable
fun HomeScreenRoute(
    openNote: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val (state, event, effect) = use(viewModel = viewModel)

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        event.invoke(HomeScreenContract.Event.GetFolders)
        event.invoke(HomeScreenContract.Event.GetSelections)
    }

    effect.collectInLaunchedEffect {
        when (it) {
            HomeScreenContract.Effect.FolderCreated -> snackBar(
                snackbarHostState = snackbarHostState,
                message = "Created new folder!"
            )

            HomeScreenContract.Effect.OpenNote -> {
                openNote()
            }
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
                event.invoke(HomeScreenContract.Event.OpenFolder(folderName))
            },
            onRecentClick = { noteName ->
                event.invoke(HomeScreenContract.Event.OpenNote(noteName))
            },
            onToggleSearch = {
                event.invoke(HomeScreenContract.Event.ToggleSearch)
            },
            onNewNoteClick = {
                event.invoke(HomeScreenContract.Event.OpenNote())
                openNote()
            },
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

/**
 * Represents route for screen with list of notes in some folder.
 *
 * @param modifier a [Modifier] applied to [NotesListScreenContent]
 * @param viewModel [NotesListViewModel] injected with koin
 */
@Composable
private fun NotesListScreenRoute(
    openNote: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotesListViewModel = koinViewModel()
) {
    val (state, event) = use(viewModel = viewModel)

    LaunchedEffect(key1 = Unit) {
        event.invoke(NotesListScreenContract.Event.ReadScreenData)
    }

    NotesListScreenContent(
        modifier = modifier,
        screenState = state,
        onNewItemClick = {
            event.invoke(NotesListScreenContract.Event.OnCreateNote)
            openNote("")
        },
        onNoteClick = { noteName ->
            event.invoke(NotesListScreenContract.Event.OnNoteClick(noteName))
            openNote(noteName)
        }
    )
}