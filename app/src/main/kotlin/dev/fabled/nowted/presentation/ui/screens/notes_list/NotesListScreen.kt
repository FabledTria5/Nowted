package dev.fabled.nowted.presentation.ui.screens.notes_list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.fabled.nowted.R
import dev.fabled.nowted.presentation.core.strokeBorder
import dev.fabled.nowted.presentation.core.viewmodel.use
import dev.fabled.nowted.presentation.model.UiNote
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreen
import dev.fabled.nowted.presentation.ui.theme.SourceSans
import org.koin.androidx.compose.koinViewModel

/**
 * Voyager route for notes list screen
 */
class NotesListScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: NotesListViewModel = koinViewModel()

        val (state, event) = use(viewModel = viewModel)

        LaunchedEffect(key1 = Unit) {
            event.invoke(NotesListScreenContract.Event.ReadScreenData)
        }

        NotesListScreenContent(
            state = state,
            onNewItemClick = {
                event.invoke(NotesListScreenContract.Event.OnCreateNote)
                navigator.push(NoteScreen())
            },
            onNoteClick = { noteTitle ->
                event.invoke(NotesListScreenContract.Event.OnNoteClick(noteTitle))
                navigator.push(NoteScreen())
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
        )
    }
}

/**
 * Represents content of [NoteScreen]
 *
 * @param state [NotesListScreenContract.State] state of screen
 * @param onNewItemClick callback for event, when used intends to create first note in folder
 * @param onNoteClick callback for event, when user selects note from folder
 * @param modifier [Modifier] that applies to content
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun NotesListScreenContent(
    state: NotesListScreenContract.State,
    onNewItemClick: () -> Unit,
    onNoteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.imePadding(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        AnimatedContent(
            targetState = state.folderName,
            modifier = Modifier.padding(start = 20.dp),
            transitionSpec = {
                slideInVertically() + fadeIn() with
                        slideOutVertically(targetOffsetY = { it * 2 }) + fadeOut()
            },
            label = "folder_name_animation"
        ) { text ->
            Text(
                text = text,
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(start = 20.dp, top = 15.dp, end = 20.dp, bottom = 30.dp)
        ) {
            if (state.notesList.isEmpty()
                && !state.isSystemFolder
                && !state.isLoading
            ) {
                item {
                    EmptyItem(
                        onItemClick = onNewItemClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }
            }
            items(items = state.notesList) { note ->
                NoteListItem(
                    noteItem = note,
                    isSelected = note.noteTitle == state.selectedNoteName,
                    onNoteClick = onNoteClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 100.dp)
                        .animateItemPlacement()
                )
            }
        }
    }
}

/**
 * Item, that is presented in notes list, if there is no notes in current folder
 *
 * @param onItemClick callback for event, when used intends to create first note in folder
 * @param modifier [Modifier] that is applied to element
 */
@Composable
fun EmptyItem(
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(Color(color = 0xFF323232))
            .strokeBorder(color = Color.White, width = 2f, cornerRadius = 3.dp)
            .clickable { onItemClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .border(width = 1.dp, color = Color.White, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
        Text(
            text = stringResource(R.string.create_first_note),
            modifier = Modifier.padding(top = 15.dp),
            fontFamily = SourceSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}

/**
 * Represents simple note in notes list
 *
 * @param noteItem [UiNote] that is representing in this item
 * @param isSelected used to highlight selected item
 * @param onNoteClick callback for event, when user selects note from folder
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteListItem(
    noteItem: UiNote,
    isSelected: Boolean,
    onNoteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Using concrete colors instead of manipulate White color alpha, cause card doesn't show
    // correct colors when set them with alpha
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            Color(color = 0xFF323232)
        else
            Color(color = 0xFF232323),
        animationSpec = tween(durationMillis = 250),
        label = "card_background_color"
    )

    Card(
        modifier = modifier,
        onClick = { onNoteClick(noteItem.noteTitle) },
        shape = RoundedCornerShape(3.dp),
        elevation = 5.dp,
        backgroundColor = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = noteItem.noteTitle,
                    modifier = Modifier.fillMaxWidth(fraction = .85f),
                    fontFamily = SourceSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = if (!noteItem.isFavorite)
                        Icons.Outlined.StarBorder
                    else
                        Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = noteItem.noteDate,
                    color = Color.White.copy(alpha = .4f),
                    fontFamily = SourceSans,
                    fontSize = 16.sp
                )
                Text(
                    text = noteItem.noteText,
                    color = Color.White.copy(alpha = .6f),
                    fontFamily = SourceSans,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}