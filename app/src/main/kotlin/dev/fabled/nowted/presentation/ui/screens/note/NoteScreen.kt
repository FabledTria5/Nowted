package dev.fabled.nowted.presentation.ui.screens.note

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.fabled.nowted.R
import dev.fabled.nowted.presentation.core.snackBar
import dev.fabled.nowted.presentation.core.viewmodel.collectInLaunchedEffect
import dev.fabled.nowted.presentation.core.viewmodel.use
import dev.fabled.nowted.presentation.ui.components.MyTextField
import dev.fabled.nowted.presentation.ui.components.OptionsButton
import dev.fabled.nowted.presentation.ui.screens.empty.EmptyScreen
import dev.fabled.nowted.presentation.ui.screens.restore.RestoreNoteScreen
import dev.fabled.nowted.presentation.ui.theme.LocalPaddings
import dev.fabled.nowted.presentation.ui.theme.SourceSans
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

class NoteScreen(private val screenKey: String = "") : Screen {

    override val key: ScreenKey
        get() = screenKey.ifEmpty { super.key }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val verticalPaddings = LocalPaddings.current
        val context = LocalContext.current

        val viewModel = koinViewModel<NoteViewModel>()
        val (state, event, effect) = use(viewModel = viewModel)

        val snackbarHostState = remember { SnackbarHostState() }
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(key1 = Unit) {
            event.invoke(NoteScreenContract.Event.CollectCurrentNote)
        }

        effect.collectInLaunchedEffect {
            when (it) {
                NoteScreenContract.Effect.AddedToFavorite -> snackBar(
                    snackbarHostState = snackbarHostState,
                    message = context.getString(R.string.added_to_favorites),
                    softwareKeyboardController = keyboardController
                )

                NoteScreenContract.Effect.NoteDeleted -> navigator.replace(EmptyScreen())

                NoteScreenContract.Effect.NoteSaveError -> snackBar(
                    snackbarHostState = snackbarHostState,
                    message = context.getString(R.string.note_save_error),
                    softwareKeyboardController = keyboardController
                )

                NoteScreenContract.Effect.NoteSaved -> snackBar(
                    snackbarHostState = snackbarHostState,
                    message = context.getString(R.string.note_save_success),
                    softwareKeyboardController = keyboardController
                )

                is NoteScreenContract.Effect.AddedToTrash -> {
                    navigator.replace(RestoreNoteScreen(it.noteName, it.noteFolder))
                }

                NoteScreenContract.Effect.Archived -> snackBar(
                    snackbarHostState = snackbarHostState,
                    message = context.getString(R.string.note_archived),
                    softwareKeyboardController = keyboardController
                )
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            NoteScreenContent(
                state = state,
                onEvent = event::invoke,
                modifier = Modifier
                    .padding(padding)
                    .padding(vertical = verticalPaddings.mediumPadding)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
fun NoteScreenContent(
    state: NoteScreenContract.State,
    onEvent: (NoteScreenContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    var noteText by remember(state.note.noteText) {
        mutableStateOf(state.note.noteText)
    }

    Column(
        modifier = Modifier
            .imePadding()
            .verticalScroll(scrollState)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        NoteTopBar(
            noteTitle = state.note.noteTitle,
            isFavorite = state.note.isFavorite,
            isNoteExists = state.note.isNoteExists,
            onEvent = onEvent,
            primaryTextFieldFocusRequester = focusRequester,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        )
        NoteDateFolder(
            noteDate = state.note.noteDate,
            noteFolder = state.note.noteFolder,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        )
        NoteSettings(
            contentPadding = 20.dp,
            textSize = state.note.textSize,
            onEvent = onEvent,
            modifier = Modifier.fillMaxWidth()
        )
        MyTextField(
            value = noteText,
            onValueChange = { newText ->
                noteText = newText
                onEvent(NoteScreenContract.Event.NoteTextChanged(newText))
            },
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxSize()
                .focusRequester(focusRequester),
            textStyle = TextStyle(
                color = Color.White,
                fontFamily = SourceSans,
                fontSize = state.note.textSize,
                lineHeight = state.note.paragraph,
                fontWeight = FontWeight(state.note.fontWeight.weight),
                fontStyle = FontStyle(state.note.textStyle.value),
                textDecoration = state.note.textDecoration.decoration
            )
        )
    }
}

@Composable
private fun NoteTopBar(
    noteTitle: String,
    isFavorite: Boolean,
    isNoteExists: Boolean,
    onEvent: (NoteScreenContract.Event) -> Unit,
    modifier: Modifier = Modifier,
    primaryTextFieldFocusRequester: FocusRequester = remember { FocusRequester() }
) {
    val focusRequester = remember { FocusRequester() }

    var titleText by remember(noteTitle) { mutableStateOf(noteTitle) }
    var popUpController by remember { mutableStateOf(value = false) }

    LaunchedEffect(key1 = noteTitle) {
        delay(500.milliseconds)
        if (noteTitle.isEmpty()) {
            focusRequester.requestFocus()
        }
    }

    Box(modifier = modifier) {
        MyTextField(
            value = titleText,
            onValueChange = { newText ->
                titleText = newText
                onEvent(NoteScreenContract.Event.NoteTitleChanged(newText))
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(fraction = .8f)
                .focusRequester(focusRequester),
            textStyle = TextStyle(
                color = Color.White,
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { primaryTextFieldFocusRequester.requestFocus() }
            )
        )
        OptionsButton(
            isClicked = popUpController,
            onClick = { popUpController = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(30.dp)
        )
        if (popUpController) {
            NoteOptionsPopup(
                onDismiss = { popUpController = false },
                isFavorite = isFavorite,
                isNoteExists = isNoteExists,
                onEvent = { event ->
                    popUpController = false

                    focusRequester.freeFocus()
                    primaryTextFieldFocusRequester.freeFocus()

                    onEvent(event)
                }
            )
        }
    }
}

@Composable
private fun NoteOptionsPopup(
    onDismiss: () -> Unit,
    isNoteExists: Boolean,
    isFavorite: Boolean,
    onEvent: (NoteScreenContract.Event) -> Unit
) {
    Popup(
        onDismissRequest = onDismiss,
        alignment = Alignment.TopEnd,
        offset = IntOffset(x = 0, y = 120)
    ) {
        Column(
            modifier = Modifier
                .width(250.dp)
                .wrapContentHeight()
                .clip(RoundedCornerShape(6.dp))
                .background(Color(color = 0xFF333333))
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEvent(NoteScreenContract.Event.SaveNote) },
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SaveAlt,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.save_note),
                    fontFamily = SourceSans,
                    fontSize = 16.sp
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isNoteExists) {
                        onEvent(NoteScreenContract.Event.ToggleFavorite)
                    },
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Outlined.StarBorder else Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (isFavorite)
                        stringResource(R.string.remove_from_favorites)
                    else
                        stringResource(R.string.add_to_favorites),
                    fontFamily = SourceSans,
                    fontSize = 16.sp
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isNoteExists) {
                        onEvent(NoteScreenContract.Event.ArchiveNote)
                    },
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_archive),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.archived),
                    fontFamily = SourceSans,
                    fontSize = 16.sp
                )
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = .05f))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isNoteExists) {
                        onEvent(NoteScreenContract.Event.DeleteNote)
                    },
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trash),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.delete),
                    fontFamily = SourceSans,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun NoteDateFolder(modifier: Modifier = Modifier, noteDate: String, noteFolder: String) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_calendar),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.White.copy(alpha = .6f)
            )
            Text(
                text = stringResource(R.string.date),
                modifier = Modifier
                    .padding(start = 20.dp)
                    .width(100.dp),
                color = Color.White.copy(alpha = .6f),
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = noteDate,
                color = Color.White,
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = .1f))
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_folder_closed),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.White.copy(alpha = .6f)
            )
            Text(
                text = stringResource(R.string.folder),
                modifier = Modifier
                    .padding(start = 20.dp)
                    .width(100.dp),
                color = Color.White.copy(alpha = .6f),
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
            Text(
                text = noteFolder,
                color = Color.White,
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
private fun NoteSettings(
    contentPadding: Dp,
    textSize: TextUnit,
    onEvent: (NoteScreenContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    var paragraphPopupController by remember { mutableStateOf(value = false) }
    var textSizePopupController by remember { mutableStateOf(value = false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Spacer(
            modifier = Modifier
                .padding(horizontal = contentPadding)
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = .1f))
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = contentPadding),
            horizontalArrangement = Arrangement.spacedBy(30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.clickable { paragraphPopupController = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.paragraph),
                    modifier = Modifier.width(105.dp),
                    color = Color.White,
                    fontFamily = SourceSans,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp),
                    tint = Color.White
                )
                if (paragraphPopupController) {
                    ParagraphPopUp(
                        onDismiss = { paragraphPopupController = false },
                        textSize = textSize,
                        onEvent = onEvent
                    )
                }
            }
            Row(
                modifier = Modifier.clickable { textSizePopupController = true },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = textSize.value
                        .toInt()
                        .toString(),
                    color = Color.White,
                    fontFamily = SourceSans,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp),
                    tint = Color.White
                )
                if (textSizePopupController) {
                    TextSizePopup(
                        onDismiss = { textSizePopupController = false },
                        onEvent = onEvent,
                        textSize = textSize.value
                            .toInt()
                            .toString()
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bold),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onEvent(NoteScreenContract.Event.ToggleTextWeight) }
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_italic),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onEvent(NoteScreenContract.Event.ToggleTextStyle) }
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_underline),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onEvent(NoteScreenContract.Event.ToggleTextDecoration) }
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_image),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { }
            )
        }
        Spacer(
            modifier = Modifier
                .padding(horizontal = contentPadding)
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = .1f))
        )
    }
}

@Composable
private fun ParagraphPopUp(
    onDismiss: () -> Unit,
    textSize: TextUnit,
    onEvent: (NoteScreenContract.Event) -> Unit
) {
    val paragraphs = remember(textSize) {
        hashMapOf(
            TextUnit.Unspecified to "1",
            (textSize * 1.5) to "1.5",
            (textSize * 2) to "2"
        )
    }

    Popup(
        onDismissRequest = onDismiss,
        alignment = Alignment.TopEnd,
        offset = IntOffset(x = 30, y = 70)
    ) {
        Column(
            modifier = Modifier
                .width(150.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(color = 0xFF333333)),
        ) {
            for (entity in paragraphs) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onEvent(NoteScreenContract.Event.NoteParagraphChanged(entity.key))
                        }
                        .padding(horizontal = 15.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = entity.value,
                        fontFamily = SourceSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
                if (entity.value != "2") {
                    Spacer(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = .1f))
                    )
                }
            }
        }
    }
}

@Composable
private fun TextSizePopup(
    onDismiss: () -> Unit,
    onEvent: (NoteScreenContract.Event) -> Unit,
    textSize: String
) {
    Popup(
        onDismissRequest = onDismiss,
        alignment = Alignment.TopEnd,
        offset = IntOffset(x = 75, y = 70)
    ) {
        Column(
            modifier = Modifier
                .width(100.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(color = 0xFF333333))
        ) {
            for (num in 16..32 step 4) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onEvent(NoteScreenContract.Event.NoteTextSizeChanged(num.sp))
                        }
                        .padding(horizontal = 15.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "$num",
                        fontFamily = SourceSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    if (textSize == "$num") {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (num != 32) {
                    Spacer(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = .1f))
                    )
                }
            }
        }
    }
}