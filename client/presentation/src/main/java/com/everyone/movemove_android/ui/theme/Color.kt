package com.everyone.movemove_android.ui.theme

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Point = Color(0xFF5465FF)
val BackgroundInDark = Color(0xFF282828)
val FontInDark = Color(0xFFD7D7D7)
val InActiveInDark = Color(0xFFBABABA)
val BorderInDark = Color(0xFF434343)
val DisabledInDark = Color(0xFF565656)
val DisabledFontInDark = Color(0xFF888888)
val SurfaceInDark = Color(0xFF3C3C3C)
val SurfaceVariantInDark = Color(0xFF414141)
val VideoDescriptionInDark = Color(0xFFE0E0E0)

@Composable
fun textFieldSelectionColors() = TextSelectionColors(
    handleColor = BackgroundInDark,
    backgroundColor = BackgroundInDark
)