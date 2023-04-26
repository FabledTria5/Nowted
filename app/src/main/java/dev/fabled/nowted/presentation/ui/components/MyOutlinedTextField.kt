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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import dev.fabled.nowted.presentation.ui.theme.Active

/**
 * An implementation of outlined text field. The point of creating custom text field is to provide
 * ability of customizing text field paddings to fit app design guidelines.
 *
 * @param value the input [String] text to be shown in the text field
 * @param onValueChange the callback that is triggered when the input service updates the text. An
 * updated text comes as parameter of callback
 * @param border the border to be drawn around the text field
 * @param modifier optional [Modifier] for this text field
 * @param textStyle Style configuration that applies at character level such as color, font etc.
 * @param cursorColor the color of cursor that will be applied as [SolidColor]
 * @param colors [TextFieldColors] that will be used to resolve color of the text and content
 * (including label, placeholder, leading and trailing icons) for this text field
 * @param singleLine indicates if this is a single line or multi line text field
 * @param contentPaddingValues the spacing values to apply internally between the internals of text
 * field and the decoration box container
 * @param keyboardOptions software keyboard options that contains configuration such as
 * [KeyboardType] and [ImeAction]
 * @param keyboardActions when the input service emits an IME action, the corresponding callback
 * is called
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    border: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(),
    cursorColor: Color = Active,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(),
    singleLine: Boolean = false,
    contentPaddingValues: PaddingValues = PaddingValues(),
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions()
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