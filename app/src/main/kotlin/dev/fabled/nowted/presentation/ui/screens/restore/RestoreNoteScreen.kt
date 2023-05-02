package dev.fabled.nowted.presentation.ui.screens.restore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.fabled.nowted.R
import dev.fabled.nowted.presentation.core.collectInLaunchedEffect
import dev.fabled.nowted.presentation.core.use
import dev.fabled.nowted.presentation.ui.screens.note.NoteScreen
import dev.fabled.nowted.presentation.ui.theme.Active
import dev.fabled.nowted.presentation.ui.theme.SourceSans
import org.koin.androidx.compose.koinViewModel

class RestoreNoteScreen(
    private val deletedNoteName: String,
    private val noteFolder: String
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<RestoreViewModel>()

        val (state, event, effect) = use(viewModel = viewModel)

        LaunchedEffect(key1 = Unit) {
            event.invoke(RestoreScreenContract.Event.SetData(deletedNoteName, noteFolder))
        }

        effect.collectInLaunchedEffect {
            when (it) {
                RestoreScreenContract.Effect.NoteRestored -> navigator.replace(NoteScreen())
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
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
                text = """Restore "${state.deletedNoteName}"""",
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
                onClick = { event.invoke(RestoreScreenContract.Event.RestoreNote) },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Active,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 30.dp)
            ) {
                Text(
                    text = stringResource(R.string.restore),
                    fontFamily = SourceSans,
                    fontSize = 16.sp
                )
            }
        }
    }

}