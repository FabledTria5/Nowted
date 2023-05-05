package dev.fabled.nowted

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import dev.fabled.nowted.presentation.ui.navigation.SetupNavigation
import dev.fabled.nowted.presentation.ui.theme.NowtedTheme

/**
 * Single activity
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        installSplashScreen()

        setContent {
            NowtedTheme {
                SetupNavigation()
            }
        }
    }

}