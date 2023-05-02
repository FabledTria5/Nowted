package dev.fabled.nowted.presentation.ui.screens.home

import android.widget.Toast
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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
import dev.fabled.nowted.presentation.core.TestTags.BUTTON_NEW_NOTE
import dev.fabled.nowted.presentation.core.TestTags.ICONS_SEARCH
import dev.fabled.nowted.presentation.core.TestTags.TEXT_FIELD_SEARCH
import dev.fabled.nowted.presentation.core.collectInLaunchedEffect
import dev.fabled.nowted.presentation.core.use
import dev.fabled.nowted.presentation.model.MoreItem
import dev.fabled.nowted.presentation.ui.components.MyOutlinedTextField
import dev.fabled.nowted.presentation.ui.components.MyTextField
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreen
import dev.fabled.nowted.presentation.ui.screens.notes_list.NotesListScreen
import dev.fabled.nowted.presentation.ui.theme.Active
import dev.fabled.nowted.presentation.ui.theme.Kaushan
import dev.fabled.nowted.presentation.ui.theme.Primary
import dev.fabled.nowted.presentation.ui.theme.SourceSans
import kotlinx.collections.immutable.ImmutableList
import org.koin.androidx.compose.koinViewModel

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val mainViewModel = koinViewModel<HomeViewModel>()
        val context = LocalContext.current

        val (state, event, effect) = use(viewModel = mainViewModel)

        LaunchedEffect(key1 = Unit) {
            event.invoke(HomeScreenContract.Event.GetFolders)
            event.invoke(HomeScreenContract.Event.GetSelections)
        }

        effect.collectInLaunchedEffect {
            when (it) {
                HomeScreenContract.Effect.FolderCreated ->
                    Toast.makeText(context, "Folder created", Toast.LENGTH_SHORT).show()
                else -> Unit
            }
        }

        HomeScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onFolderClick = { folderName ->
                event.invoke(HomeScreenContract.Event.OpenFolder(folderName))
                navigator.push(NotesListScreen())
            },
            onRecentClick = { recentName ->
                event.invoke(HomeScreenContract.Event.OpenNote(recentName))
                navigator.push(NoteScreen())
            },
            onToggleSearch = {
                event.invoke(HomeScreenContract.Event.ToggleSearch)
            },
            onNewNoteClick = {
                event.invoke(HomeScreenContract.Event.OpenNote())
                navigator.push(NoteScreen())
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

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    state: HomeScreenContract.State,
    onFolderClick: (String) -> Unit,
    onRecentClick: (String) -> Unit,
    onToggleSearch: () -> Unit,
    onNewNoteClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onStartCreateNewFolderClick: () -> Unit,
    onCreateNewFolderClick: (String) -> Unit
) {
    Column(
        modifier = modifier.imePadding(),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        HomeScreenTopContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            isSearching = state.isSearching,
            searchQuery = state.searchQuery,
            onToggleSearch = onToggleSearch,
            onNewNoteClick = onNewNoteClick,
            onSearchQueryChange = onSearchQueryChange
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            if (state.recentNotes.isNotEmpty()) {
                recents(
                    recents = state.recentNotes,
                    onRecentClick = onRecentClick,
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

@Composable
private fun HomeScreenTopContent(
    modifier: Modifier = Modifier,
    searchQuery: String,
    isSearching: Boolean,
    onToggleSearch: () -> Unit,
    onNewNoteClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
) {
    var query by remember(searchQuery) { mutableStateOf(value = "") }

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
                    modifier = Modifier.size(15.dp),
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = null
                )
            }
            IconButton(
                modifier = Modifier
                    .size(20.dp)
                    .testTag(tag = ICONS_SEARCH),
                onClick = onToggleSearch
            ) {
                Icon(
                    imageVector = if (!isSearching)
                        Icons.Default.Search
                    else
                        Icons.Default.SearchOff,
                    contentDescription = stringResource(R.string.icon_search),
                    tint = Color.White.copy(alpha = .4f)
                )
            }
        }
        if (!isSearching) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag(BUTTON_NEW_NOTE),
                onClick = onNewNoteClick,
                shape = RoundedCornerShape(3.dp)
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(R.string.new_note),
                    fontFamily = SourceSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        } else {
            MyTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Primary, shape = RoundedCornerShape(3.dp))
                    .testTag(TEXT_FIELD_SEARCH),
                value = query,
                onValueChange = { newText ->
                    query = newText
                    onSearchQueryChange(newText)
                },
                textStyle = TextStyle(
                    color = Color.White,
                    fontFamily = SourceSans,
                    fontSize = 16.sp
                ),
                colors = TextFieldDefaults.textFieldColors(
                    leadingIconColor = Color.White.copy(alpha = .6f)
                ),
                singleLine = true,
                contentPaddingValues = PaddingValues(end = 15.dp),
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null
                    )
                },
                placeHolder = { Text(text = stringResource(R.string.search_note)) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.recents(
    recents: ImmutableList<String>,
    onRecentClick: (String) -> Unit,
    selectedNoteName: String
) {
    item {
        Text(
            modifier = Modifier.padding(start = 20.dp, bottom = 8.dp),
            text = stringResource(id = R.string.recents),
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
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.ic_note),
                contentDescription = null,
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

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.folders(
    folders: List<String>,
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
            IconButton(modifier = Modifier.size(20.dp), onClick = onCreateNewFolderClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_folder),
                    contentDescription = stringResource(id = R.string.add_folder_icon),
                    tint = Color.White.copy(alpha = .6f)
                )
            }
        }
    }
    if (isCreatingNewFolder) {
        item {
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
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.ic_folder_opened),
                    contentDescription = null,
                    tint = Color.White
                )
                MyOutlinedTextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
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
                        onDone = { onNewFolderDoneClick(newFolderName) }
                    )
                )
            }
        }
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
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = item.icon),
                contentDescription = null,
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