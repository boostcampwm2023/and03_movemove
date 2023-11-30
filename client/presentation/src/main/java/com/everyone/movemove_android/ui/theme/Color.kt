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
val KakaoYellow = Color(0xFFFEE500)
val GoogleGray = Color(0xFFF1F4F6)
val VideoDescriptionInDark = Color(0xFFE0E0E0)
val FooterTopBackgroundInDark = Color(0xff282828)
val FooterMiddleBackgroundInDark = Color(0xff191919)
val FooterBottomBackgroundInDark = Color(0xff000000)
val CategoryBackgroundInDark = Color(0xff1F2128)
val EditorTimelineDim = Color(0x88000000)
val ProfileAddGray = Color(0xff616161)
val StartingDim = Color(0xAA000000)
val ClipOpDim = Color(0x88000000)

@Composable
fun textFieldSelectionColors() = TextSelectionColors(
    handleColor = BackgroundInDark,
    backgroundColor = BackgroundInDark
)