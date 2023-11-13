package com.everyone.movemove_android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.everyone.movemove_android.ui.theme.DisabledFontInDark
import com.everyone.movemove_android.ui.theme.DisabledInDark
import com.everyone.movemove_android.ui.theme.Point
import com.everyone.movemove_android.ui.theme.Typography
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