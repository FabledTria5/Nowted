package dev.fabled.nowted.presentation.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import dev.fabled.nowted.presentation.ui.theme.Active

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    cursorColor: Color = Active,
    colors: TextFieldColors,
    singleLine: Boolean = false,
    contentPaddingValues: PaddingValues,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    border: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        interactionSource = interactionSource,
        textStyle = textStyle,
        cursorBrush = SolidColor(cursorColor),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        decorationBox = { innerTextField ->
            TextFieldDefaults.OutlinedTextFieldDecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = singleLine,
                colors = colors,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                contentPadding = contentPaddingValues,
                border = border
            )
        }
    )
}