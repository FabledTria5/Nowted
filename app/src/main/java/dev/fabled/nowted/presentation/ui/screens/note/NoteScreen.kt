package dev.fabled.nowted.presentation.ui.screens.note

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import cafe.adriel.voyager.core.screen.Screen
import dev.fabled.nowted.R
import dev.fabled.nowted.presentation.core.collectInLaunchedEffect
import dev.fabled.nowted.presentation.core.showSnackBar
import dev.fabled.nowted.presentation.core.use
import dev.fabled.nowted.presentation.ui.components.MyTextField
import dev.fabled.nowted.presentation.ui.components.OptionsButton
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreenContract.State.NoteScreenContentState
import dev.fabled.nowted.presentation.ui.theme.Active
import dev.fabled.nowted.presentation.ui.theme.SourceSans
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

class NoteScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinViewModel<NoteViewModel>()

        val (state, event, effect) = use(viewModel = viewModel)

        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(key1 = Unit) {
            event.invoke(NoteScreenContract.Event.CollectCurrentNote)
        }

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
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            when (state.contentState) {
                NoteScreenContentState.NOTE_NOT_SELECTED ->
                    EmptyNoteContent(modifier = Modifier.fillMaxSize())

                NoteScreenContentState.NOTE_OPENED -> NoteScreenContent(
                    modifier = Modifier
                        .padding(padding)
                        .padding(vertical = 20.dp)
                        .fillMaxSize(),
                    state = state,
                    onEvent = { event.invoke(it) }
                )

                NoteScreenContentState.NOTE_RESTORING -> RestoreNoteContent(
                    noteName = state.deletedNoteName,
                    onRestoreNote = {
                        event.invoke(NoteScreenContract.Event.RestoreNote)
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyNoteContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(80.dp),
            painter = painterResource(id = R.drawable.ic_blank_note),
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(vertical = 10.dp),
            text = stringResource(R.string.select_a_note_to_view),
            fontFamily = SourceSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp
        )
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(R.string.choose_note_rationale),
            color = Color.White.copy(alpha = .6f),
            fontFamily = SourceSans,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RestoreNoteContent(modifier: Modifier = Modifier, noteName: String, onRestoreNote: () -> Unit) {
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(80.dp),
            painter = painterResource(id = R.drawable.ic_restore),
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = """Restore "$noteName"""",
            fontFamily = SourceSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )
        Text(
            modifier = Modifier
                .padding(top = 10.dp)
                .widthIn(min = 0.dp, max = 500.dp),
            text = stringResource(R.string.note_restore_rationale),
            color = Color.White.copy(alpha = .6f),
            fontFamily = SourceSans,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Button(
            modifier = Modifier.padding(top = 10.dp),
            onClick = onRestoreNote,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Active,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 30.dp)
        ) {
            Text(text = stringResource(R.string.restore), fontFamily = SourceSans, fontSize = 16.sp)
        }
    }
}

@Composable
fun NoteScreenContent(
    modifier: Modifier = Modifier,
    state: NoteScreenContract.State,
    onEvent: (NoteScreenContract.Event) -> Unit
) {
    val scrollState = rememberScrollState()

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
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            noteTitle = state.note.noteTitle,
            isFavorite = state.note.isFavorite,
            onEvent = onEvent
        )
        NoteDateFolder(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            noteDate = state.note.noteDate,
            noteFolder = state.note.noteFolder
        )
        NoteSettings(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = 20.dp,
            textSize = state.note.textSize,
            onEvent = onEvent
        )
        MyTextField(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            value = noteText,
            onValueChange = { newText ->
                noteText = newText
                onEvent(NoteScreenContract.Event.NoteTextChanged(newText))
            },
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
    modifier: Modifier = Modifier,
    noteTitle: String,
    isFavorite: Boolean,
    onEvent: (NoteScreenContract.Event) -> Unit
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

    Box(
        modifier = modifier
    ) {
        MyTextField(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(fraction = .8f)
                .focusRequester(focusRequester),
            value = titleText,
            onValueChange = { newText ->
                titleText = newText
                onEvent(NoteScreenContract.Event.NoteTitleChanged(newText))
            },
            textStyle = TextStyle(
                color = Color.White,
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp
            )
        )
        OptionsButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(30.dp),
            isClicked = popUpController,
            onClick = {
                popUpController = true
            }
        )
        if (popUpController)
            NoteOptionsPopup(
                onDismiss = { popUpController = false },
                isFavorite = isFavorite,
                onEvent = { event ->
                    popUpController = false
                    onEvent(event)
                }
            )
    }
}

@Composable
private fun NoteOptionsPopup(
    onDismiss: () -> Unit,
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
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.SaveAlt,
                    contentDescription = null
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
                    .clickable { onEvent(NoteScreenContract.Event.ToggleFavorite) },
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = if (isFavorite) Icons.Outlined.StarBorder else Icons.Default.Star,
                    contentDescription = null
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
                    .clickable { onEvent(NoteScreenContract.Event.ArchiveNote) },
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.ic_archive),
                    contentDescription = null
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
                    .clickable { onEvent(NoteScreenContract.Event.DeleteNote) },
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.ic_trash),
                    contentDescription = null
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
                modifier = Modifier.size(18.dp),
                painter = painterResource(id = R.drawable.ic_calendar),
                contentDescription = null,
                tint = Color.White.copy(alpha = .6f)
            )
            Text(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .width(100.dp),
                text = stringResource(R.string.date),
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
                modifier = Modifier.size(18.dp),
                painter = painterResource(id = R.drawable.ic_folder_closed),
                contentDescription = null,
                tint = Color.White.copy(alpha = .6f)
            )
            Text(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .width(100.dp),
                text = "Folder",
                color = Color.White.copy(alpha = .6f),
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
            Text(
                modifier = Modifier.clickable { },
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
    modifier: Modifier = Modifier,
    contentPadding: Dp,
    textSize: TextUnit,
    onEvent: (NoteScreenContract.Event) -> Unit
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
                    modifier = Modifier.width(105.dp),
                    text = stringResource(R.string.paragraph),
                    color = Color.White,
                    fontFamily = SourceSans,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp),
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White
                )
                if (paragraphPopupController)
                    ParagraphPopUp(
                        onDismiss = { paragraphPopupController = false },
                        textSize = textSize,
                        onEvent = onEvent
                    )
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
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp),
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White
                )
                if (textSizePopupController)
                    TextSizePopup(
                        onDismiss = { textSizePopupController = false },
                        onEvent = onEvent,
                        textSize = textSize.value
                            .toInt()
                            .toString()
                    )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onEvent(NoteScreenContract.Event.ToggleTextWeight) },
                    painter = painterResource(id = R.drawable.ic_bold),
                    contentDescription = null
                )
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onEvent(NoteScreenContract.Event.ToggleTextStyle) },
                    painter = painterResource(id = R.drawable.ic_italic),
                    contentDescription = null
                )
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onEvent(NoteScreenContract.Event.ToggleTextDecoration) },
                    painter = painterResource(id = R.drawable.ic_underline),
                    contentDescription = null
                )
            }
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .clickable { },
                painter = painterResource(id = R.drawable.ic_image),
                contentDescription = null
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
                if (entity.value != "2")
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

                    if (textSize == "$num")
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                }

                if (num != 32)
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