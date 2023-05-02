package dev.fabled.nowted.presentation.ui.screens.empty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import dev.fabled.nowted.R
import dev.fabled.nowted.presentation.ui.theme.SourceSans

class EmptyScreen : Screen {

    @Composable
    override fun Content() {
        Column(
            modifier = Modifier.fillMaxSize(),
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

}