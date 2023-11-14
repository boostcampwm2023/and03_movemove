package com.everyone.movemove_android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.everyone.movemove_android.ui.theme.DisabledFontInDark
import com.everyone.movemove_android.ui.theme.DisabledInDark
import com.everyone.movemove_android.ui.theme.Point
import com.everyone.movemove_android.ui.theme.Typography
import com.everyone.movemove_android.ui.theme.textFieldSelectionColors
import com.everyone.movemove_android.ui.util.clickableWithoutRipple

@Composable
fun StyledText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Text(
        modifier = modifier,
        text = text,
        style = style,
        color = color
    )
}

@Composable
inline fun RoundedCornerButton(
    modifier: Modifier = Modifier,
    buttonText: String,
    isEnabled: Boolean = true,
    crossinline onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .height(48.dp)
            .background(color = if (isEnabled) Point else DisabledInDark)
            .clickableWithoutRipple { onClick() }
    ) {
        StyledText(
            modifier = Modifier.align(Alignment.Center),
            text = buttonText,
            style = Typography.labelLarge,
            color = if (isEnabled) Color.White else DisabledFontInDark
        )
    }
}

@Composable
fun MoveMoveTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(4.dp))
            .background(color = backgroundColor)
    ) {
        CompositionLocalProvider(LocalTextSelectionColors.provides(textFieldSelectionColors())) {
            BasicTextField(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                value = value,
                onValueChange = onValueChange,
                textStyle = Typography.labelMedium.copy(color = textColor),
                singleLine = singleLine,
                cursorBrush = SolidColor(Color.White),
                keyboardActions = if (keyboardActions == KeyboardActions.Default) {
                    KeyboardActions(onDone = { focusManager.clearFocus() })
                } else {
                    keyboardActions
                },
                keyboardOptions = keyboardOptions
            )
        }
    }
}