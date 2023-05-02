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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.fabled.nowted.R
import dev.fabled.nowted.presentation.core.use
import dev.fabled.nowted.presentation.model.UiNote
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreen
import dev.fabled.nowted.presentation.ui.theme.SourceSans
import org.koin.androidx.compose.koinViewModel

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
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
            screenState = state,
            onNewItemClick = {
                event.invoke(NotesListScreenContract.Event.OnCreateNote)
                navigator.push(NoteScreen())
            },
            onNoteClick = { noteTitle ->
                event.invoke(NotesListScreenContract.Event.OnNoteClick(noteTitle))
                navigator.push(NoteScreen())
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun NotesListScreenContent(
    modifier: Modifier = Modifier,
    screenState: NotesListScreenContract.State,
    onNewItemClick: () -> Unit,
    onNoteClick: (String) -> Unit
) {
    Column(
        modifier = modifier.imePadding(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        AnimatedContent(
            modifier = Modifier.padding(start = 20.dp),
            targetState = screenState.folderName,
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
            if (screenState.notesList.isEmpty()
                && !screenState.isSystemFolder
                && !screenState.isLoading
            ) {
                item {
                    EmptyItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        onItemClick = onNewItemClick
                    )
                }
            }
            items(items = screenState.notesList) { note ->
                NoteListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 100.dp)
                        .animateItemPlacement(),
                    noteItem = note,
                    selectedNoteName = screenState.selectedNoteName,
                    onNoteClick = onNoteClick
                )
            }
        }
    }
}

@Composable
fun EmptyItem(modifier: Modifier = Modifier, onItemClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(Color(color = 0xFF323232))
            .drawBehind {
                val stroke = Stroke(
                    width = 2f,
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(10f, 10f), 0f
                    )
                )

                drawRoundRect(
                    color = Color.White,
                    style = stroke,
                    cornerRadius = CornerRadius(3.dp.toPx())
                )
            }
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
            modifier = Modifier.padding(top = 15.dp),
            text = stringResource(R.string.create_first_note),
            fontFamily = SourceSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun NoteListItem(
    modifier: Modifier = Modifier,
    noteItem: UiNote,
    onNoteClick: (String) -> Unit,
    selectedNoteName: String
) {
    // Using concrete colors instead of manipulate White color alpha, cause card doesn't show
    // correct colors when set them with alpha
    val backgroundColor by animateColorAsState(
        targetValue = if (noteItem.noteTitle == selectedNoteName)
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
                    modifier = Modifier.fillMaxWidth(fraction = .9f),
                    text = noteItem.noteTitle,
                    fontFamily = SourceSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = if (!noteItem.isFavorite)
                        Icons.Outlined.StarBorder
                    else
                        Icons.Default.Star,
                    contentDescription = null,
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