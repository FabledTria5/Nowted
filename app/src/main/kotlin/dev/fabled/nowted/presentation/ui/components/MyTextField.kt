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
import androidx.compose.ui.unit.dp
import dev.fabled.nowted.presentation.ui.theme.Active

/**
 * An implementation of text field. The point of creating custom text field is to provide
 * ability of customizing text field paddings to fit app design guidelines.
 *
 * @param value the input [String] text to be shown in the text field
 * @param onValueChange the callback that is triggered when the input service updates the text. An
 * updated text comes as parameter of callback
 * @param modifier optional [Modifier] for this text field
 * @param textStyle Style configuration that applies at character level such as color, font etc.
 * @param cursorColor the color of cursor that will be applied as [SolidColor]
 * @param contentPaddingValues the spacing values to apply internally between the internals of text
 * field and the decoration box container
 * @param colors [TextFieldColors] that will be used to resolve color of the text and content
 * (including label, placeholder, leading and trailing icons) for this text field
 * @param singleLine indicates if this is a single line or multi line text field
 * @param leadingIcon the optional leading icon to be displayed at the beginning of the text field
 * container
 * @param placeHolder the optional placeholder to be displayed when the text field is in focus and
 * the input text is empty
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(),
    cursorColor: Color = Active,
    contentPaddingValues: PaddingValues = PaddingValues(0.dp),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    placeHolder: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = textStyle,
        cursorBrush = SolidColor(cursorColor),
        interactionSource = interactionSource,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        decorationBox = { innerTextField ->
            TextFieldDefaults.TextFieldDecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = singleLine,
                leadingIcon = leadingIcon,
                placeholder = placeHolder,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                contentPadding = contentPaddingValues,
                colors = colors
            )
        }
    )
}