package dev.fabled.nowted.presentation.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.fabled.nowted.presentation.core.WindowType
import dev.fabled.nowted.presentation.core.rememberWindowSize

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
            val systemUiController = rememberSystemUiController()
            val windowSize = rememberWindowSize()

            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = false
                )
            }

            val paddings = when (windowSize.width) {
                WindowType.Compact -> VerticalPaddings(normalPadding = 20.dp, mediumPadding = 30.dp)
                else -> VerticalPaddings(normalPadding = 30.dp, mediumPadding = 50.dp)
            }

            CompositionLocalProvider(
                LocalPaddings provides paddings,
                LocalWindowSize provides windowSize
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    content()
                }
            }
        }
    )
}