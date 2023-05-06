package dev.fabled.nowted.presentation.ui.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.fabled.nowted.R
import dev.fabled.nowted.presentation.core.snackBar
import dev.fabled.nowted.presentation.core.viewmodel.collectInLaunchedEffect
import dev.fabled.nowted.presentation.core.viewmodel.use
import dev.fabled.nowted.presentation.model.MoreItem
import dev.fabled.nowted.presentation.ui.components.MyOutlinedTextField
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreen
import dev.fabled.nowted.presentation.ui.screens.folder.FolderScreen
import dev.fabled.nowted.presentation.ui.theme.Active
import dev.fabled.nowted.presentation.ui.theme.Kaushan
import dev.fabled.nowted.presentation.ui.theme.SourceSans
import kotlinx.collections.immutable.ImmutableList
import org.koin.androidx.compose.koinViewModel

/**
 * Voyager route for home screen
 */
class HomeScreen : Screen {

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val mainViewModel = koinViewModel<HomeViewModel>()
        val context = LocalContext.current

        val snackbarHostState = remember { SnackbarHostState() }
        val keyboardController = LocalSoftwareKeyboardController.current

        val (state, event, effect) = use(viewModel = mainViewModel)

        LaunchedEffect(key1 = Unit) {
            event.invoke(HomeScreenContract.Event.GetFolders)
            event.invoke(HomeScreenContract.Event.GetSelections)
            event.invoke(HomeScreenContract.Event.GetRecents)
        }

        effect.collectInLaunchedEffect {
            when (it) {
                is HomeScreenContract.Effect.OpenNote -> navigator.push(NoteScreen(it.noteName))

                HomeScreenContract.Effect.FolderCreated -> snackBar(
                    snackbarHostState = snackbarHostState,
                    message = context.getString(R.string.folder_created),
                    softwareKeyboardController = keyboardController
                )

                HomeScreenContract.Effect.OpenFolder -> navigator.push(FolderScreen())
            }
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
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
}

/**
 * Represents content of [HomeScreen]
 *
 * @param state current state of screen
 * @param onFolderClick callback for event, when user intends to open folder
 * @param onNoteClick callback for event, when user intends to create new note or open existing
 * @param onStartCreateNewFolderClick callback for event, when user starts creating new folder
 * @param onCreateNewFolderClick callback for event, when user finished creating new folder
 * @param modifier [Modifier] applied to content
 *
 * @see [HomeScreenContract.State]
 */
@Composable
fun HomeScreenContent(
    state: HomeScreenContract.State,
    onFolderClick: (String) -> Unit,
    onNoteClick: (String) -> Unit,
    onStartCreateNewFolderClick: () -> Unit,
    onCreateNewFolderClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.imePadding(),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        HomeScreenTopContent(
            onNewNoteClick = { onNoteClick("") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            if (state.recentNotes.isNotEmpty()) {
                recents(
                    recents = state.recentNotes,
                    onRecentClick = onNoteClick,
                    selectedNoteName = state.selectedNoteName
                )
            }
            folders(
                folders = state.folders,
                selectedFolder = state.selectedFolderName,
                onFolderClick = onFolderClick,
                isCreatingNewFolder = state.isCreatingFolder,
                onCreateNewFolderClick = onStartCreateNewFolderClick,
                onNewFolderDoneClick = onCreateNewFolderClick
            )
            more(
                selectedFolder = state.selectedFolderName,
                onFolderClick = onFolderClick
            )
        }
    }
}

/**
 * Represents content of top bar for [HomeScreen]. This content includes app name and new note
 * button
 *
 * @param onNewNoteClick callback for event, when user intends to create new note
 * @param modifier [Modifier] applied to content
 */
@Composable
private fun HomeScreenTopContent(
    onNewNoteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(30.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontFamily = Kaushan,
                    fontSize = 26.sp,
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
        Button(
            onClick = onNewNoteClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(3.dp),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 5.dp,
                pressedElevation = 2.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.icon_add),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(R.string.new_note),
                modifier = Modifier.padding(start = 8.dp),
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}

/**
 * Extends [LazyListScope] to show recent notes. Shows "Recents" text and list. Not showing if
 * there is no recent notes
 *
 * @param recents list of recent notes. Size of list never surpasses 3
 * @param onRecentClick callback for event, when user intends to open recent note
 * @param selectedNoteName name of current selected note to highlight it in the list
 */
@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.recents(
    recents: ImmutableList<String>,
    onRecentClick: (String) -> Unit,
    selectedNoteName: String
) {
    item {
        Text(
            text = stringResource(id = R.string.recents),
            modifier = Modifier.padding(start = 20.dp, bottom = 8.dp),
            color = Color.White.copy(alpha = .6f),
            fontFamily = SourceSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
    items(items = recents) { noteName ->
        val backgroundColor by animateColorAsState(
            targetValue = if (noteName == selectedNoteName) Active else Color.Transparent,
            animationSpec = tween(durationMillis = 250),
            label = "background_color_animation"
        )

        val contentColor by animateColorAsState(
            targetValue = if (noteName == selectedNoteName)
                Color.White
            else
                Color.White.copy(alpha = .6f),
            animationSpec = tween(durationMillis = 250),
            label = "content_color_animation"
        )

        Row(
            modifier = Modifier
                .padding(bottom = 5.dp)
                .fillMaxWidth()
                .background(backgroundColor)
                .clickable { onRecentClick(noteName) }
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .animateItemPlacement(),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_note),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = contentColor
            )
            Text(
                text = noteName,
                color = contentColor,
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Extends [LazyListScope] to show folders. Shows "Folders" text, new folder form and folders
 * list. Shows only user created folders and non-system default folders.
 *
 * @param folders list of folders
 * @param selectedFolder name of selected folder to highlight it in the list
 * @param onFolderClick callback for event, when user intends to open folder
 * @param onCreateNewFolderClick callback for event, when user starts creating folder
 * @param isCreatingNewFolder indicates, if user is creating new folder
 * @param onNewFolderDoneClick callback for event, when user finishes creating folder
 */
@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.folders(
    folders: ImmutableList<String>,
    selectedFolder: String,
    onFolderClick: (String) -> Unit,
    onCreateNewFolderClick: () -> Unit,
    isCreatingNewFolder: Boolean,
    onNewFolderDoneClick: (String) -> Unit
) {
    item {
        Row(
            modifier = Modifier
                .padding(top = 25.dp, bottom = 8.dp)
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.folders),
                color = Color.White.copy(alpha = .6f),
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            IconButton(
                onClick = onCreateNewFolderClick,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_folder),
                    contentDescription = stringResource(id = R.string.add_folder_icon),
                    tint = Color.White.copy(alpha = .6f)
                )
            }
        }
    }
    if (isCreatingNewFolder) {
        createFolderItem(onCreateFolder = onNewFolderDoneClick)
    }
    items(items = folders) { name ->
        val backgroundColor by animateColorAsState(
            targetValue = if (name == selectedFolder)
                Color(0xFF1F1F1F)
            else
                Color.Transparent,
            animationSpec = tween(durationMillis = 250),
            label = "background_color_animation"
        )

        val contentColor by animateColorAsState(
            targetValue = if (name == selectedFolder)
                Color.White
            else
                Color.White.copy(alpha = .6f),
            animationSpec = tween(durationMillis = 250),
            label = "content_color_animation"
        )

        Row(
            modifier = Modifier
                .padding(bottom = 5.dp)
                .fillMaxWidth()
                .background(backgroundColor)
                .clickable { onFolderClick(name) }
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .animateItemPlacement(),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = if (name == selectedFolder)
                    painterResource(id = R.drawable.ic_folder_opened)
                else
                    painterResource(id = R.drawable.ic_folder_closed),
                contentDescription = null,
                tint = contentColor
            )
            Text(
                text = name,
                color = contentColor,
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

}

/**
 * Extends [LazyListScope] and shows single text field, when user can type the name of new folder
 *
 * @param onCreateFolder callback for event, when user finishes creating new folder
 */
private fun LazyListScope.createFolderItem(
    onCreateFolder: (String) -> Unit
) = item {
    var newFolderName by remember { mutableStateOf(value = "") }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_folder_opened),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.White
        )
        MyOutlinedTextField(
            value = newFolderName,
            onValueChange = { newFolderName = it },
            modifier = Modifier.focusRequester(focusRequester),
            textStyle = TextStyle(
                color = Color.White,
                fontFamily = SourceSans,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.Transparent,
            ),
            singleLine = true,
            contentPaddingValues = PaddingValues(horizontal = 2.dp, vertical = 1.dp),
            border = {
                Box(
                    modifier = Modifier.border(
                        width = .5.dp,
                        color = Color.White.copy(alpha = .4f)
                    )
                )
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onCreateFolder(newFolderName) }
            )
        )
    }
}

/**
 * Extends [LazyListScope] to show more items. This items is system and user can not directly
 * create note inside them.
 *
 * @param selectedFolder name of selected folder to be highlighted in list
 * @param onFolderClick callback for event, when user intends to open folder
 */
private fun LazyListScope.more(
    selectedFolder: String,
    onFolderClick: (String) -> Unit
) {
    val moreItems = MoreItem.getItems()

    item {
        Text(
            modifier = Modifier.padding(start = 20.dp, top = 25.dp),
            text = stringResource(id = R.string.more),
            color = Color.White.copy(alpha = .6f),
            fontFamily = SourceSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
    items(items = moreItems) { item ->
        val backgroundColor by animateColorAsState(
            targetValue = if (item.folderName == selectedFolder)
                Color(color = 0xFF1F1F1F)
            else
                Color.Transparent,
            animationSpec = tween(durationMillis = 250),
            label = "background_color_animation"
        )

        val contentColor by animateColorAsState(
            targetValue = if (item.folderName == selectedFolder)
                Color.White
            else
                Color.White.copy(alpha = .6f),
            animationSpec = tween(durationMillis = 250),
            label = "content_color_animation"
        )

        Row(
            modifier = Modifier
                .padding(bottom = 5.dp)
                .fillMaxWidth()
                .background(backgroundColor)
                .clickable { onFolderClick(item.folderName) }
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = item.icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = contentColor
            )
            Text(
                text = stringResource(id = item.name),
                color = contentColor,
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}