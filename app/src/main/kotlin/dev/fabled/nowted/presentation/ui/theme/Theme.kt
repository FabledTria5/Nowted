package dev.fabled.nowted.presentation.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val appColorScheme = darkColors(
    background = PrimaryBackground,
    primary = Primary,
    onPrimary = Color.White,
    surface = PrimaryBackground,
)

@Composable
fun NowtedTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = appColorScheme,
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                content()
            }
        }
    )
}