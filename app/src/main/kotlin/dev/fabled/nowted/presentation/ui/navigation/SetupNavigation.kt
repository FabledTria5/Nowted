package dev.fabled.nowted.presentation.ui.navigation

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
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import cafe.adriel.voyager.core.platform.multiplatformName
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.fabled.nowted.R
import dev.fabled.nowted.presentation.core.LocalWindowSize
import dev.fabled.nowted.presentation.core.WindowType
import dev.fabled.nowted.presentation.core.distinctReplace
import dev.fabled.nowted.presentation.core.rememberWindowSize
import dev.fabled.nowted.presentation.core.snackBar
import dev.fabled.nowted.presentation.core.viewmodel.collectInLaunchedEffect
import dev.fabled.nowted.presentation.core.viewmodel.use
import dev.fabled.nowted.presentation.ui.navigation.transitions.FadeTransition
import dev.fabled.nowted.presentation.ui.navigation.transitions.SlideTransition
import dev.fabled.nowted.presentation.ui.screens.empty.EmptyScreen
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreen
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreenContent
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreenContract
import dev.fabled.nowted.presentation.ui.screens.home.HomeViewModel
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreen
import dev.fabled.nowted.presentation.ui.screens.folder.FolderScreenContent
import dev.fabled.nowted.presentation.ui.screens.folder.FolderScreenContract
import dev.fabled.nowted.presentation.ui.screens.folder.FolderViewModel
import dev.fabled.nowted.presentation.ui.screens.restore.RestoreNoteScreen
import dev.fabled.nowted.presentation.ui.theme.LocalPaddings
import dev.fabled.nowted.presentation.ui.theme.SecondaryBackground
import org.koin.androidx.compose.koinViewModel

/**
 * Resolves navigation method depending on window size provided by [rememberWindowSize].
 * If window size is compact launches navigation, where every screen is presented as Voyager screen
 * class. Else launches navigation, when all app screens are presented at one time.
 */
@Composable
fun SetupNavigation() {
    val windowSize = LocalWindowSize.current

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
    val verticalPaddings = LocalPaddings.current

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
                    .padding(top = verticalPaddings.normalPadding),
            )
            FolderScreenRoute(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = .3f)
                    .padding(top = verticalPaddings.normalPadding),
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
 * @param modifier a [Modifier] that applies for [HomeScreenContent]
 * @param viewModel [HomeViewModel] injected with Koin
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val navigator = LocalNavigator.currentOrThrow
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val (state, event, effect) = use(viewModel = viewModel)

    LaunchedEffect(key1 = Unit) {
        event.invoke(HomeScreenContract.Event.GetFolders)
        event.invoke(HomeScreenContract.Event.GetSelections)
        event.invoke(HomeScreenContract.Event.GetRecents)
    }

    effect.collectInLaunchedEffect {
        when (it) {
            is HomeScreenContract.Effect.OpenNote -> {
                navigator.distinctReplace(screen = NoteScreen()) {
                    lastItem.key == EmptyScreen::class.multiplatformName
                            || lastItem.key == RestoreNoteScreen::class.multiplatformName
                }
            }

            HomeScreenContract.Effect.FolderCreated -> snackBar(
                snackbarHostState = snackbarHostState,
                message = context.getString(R.string.folder_created),
                softwareKeyboardController = keyboardController
            )

            else -> Unit
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        HomeScreenContent(
            state = state,
            onFolderClick = { folderName ->
                event.invoke(HomeScreenContract.Event.OpenFolder(folderName))
            },
            onNoteClick = { noteName ->
                event.invoke(HomeScreenContract.Event.OpenNote(noteName))
            },
            onStartCreateNewFolderClick = {
                event.invoke(HomeScreenContract.Event.OnStartCreateNewFolder)
            },
            onCreateNewFolderClick = { folderName ->
                event.invoke(HomeScreenContract.Event.CreateFolder(folderName))
            },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}

/**
 * Represents route for folder screen.
 *
 * @param modifier a [Modifier] applied to [FolderScreenContent]
 * @param viewModel [FolderViewModel] injected with koin
 */
@Composable
private fun FolderScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: FolderViewModel = koinViewModel()
) {
    val navigator = LocalNavigator.currentOrThrow
    val (state, event, effect) = use(viewModel = viewModel)

    LaunchedEffect(key1 = Unit) {
        event.invoke(FolderScreenContract.Event.ReadScreenData)
    }

    effect.collectInLaunchedEffect {
        when (it) {
            is FolderScreenContract.Effect.OpenNote -> {
                navigator.distinctReplace(screen = NoteScreen()) {
                    lastItem.key == EmptyScreen::class.multiplatformName
                            || lastItem.key == RestoreNoteScreen::class.multiplatformName
                }
            }
        }
    }

    Surface(color = SecondaryBackground) {
        FolderScreenContent(
            state = state,
            onNewItemClick = { event.invoke(FolderScreenContract.Event.OnCreateNote) },
            onNoteClick = { noteName ->
                event.invoke(FolderScreenContract.Event.OnNoteClick(noteName))
            },
            modifier = modifier
        )
    }
}