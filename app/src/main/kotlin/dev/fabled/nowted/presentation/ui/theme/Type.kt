package dev.fabled.nowted.presentation.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import dev.fabled.nowted.R

/**
 * Application logo font family
 */
val Kaushan = FontFamily(
    Font(resId = R.font.kaushan_script)
)

/**
 * Primary application font family
 */
val SourceSans = FontFamily(
    Font(resId = R.font.source_sans_pro_regular),
    Font(resId = R.font.source_sans_pro_italic, style = FontStyle.Italic),
    Font(resId = R.font.source_sans_pro_semibold, weight = FontWeight.SemiBold),
    Font(
        resId = R.font.source_sans_pro_semibold_italic,
        weight = FontWeight.SemiBold,
        style = FontStyle.Italic
    ),
)