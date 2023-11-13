package com.everyone.movemove_android.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Thin
import com.everyone.movemove_android.R

val pretendard = FontFamily(
    listOf(
        Font(
            resId = R.font.pretendard_thin,
            weight = Thin,
            style = FontStyle.Normal
        ), Font(
            resId = R.font.pretendard_regular,
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        ), Font(
            resId = R.font.pretendard_medium,
            weight = FontWeight.Medium,
            style = FontStyle.Normal
        ), Font(
            resId = R.font.pretendard_semi_bold,
            weight = FontWeight.SemiBold,
            style = FontStyle.Normal
        ), Font(
            resId = R.font.pretendard_bold,
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        )
    )
)