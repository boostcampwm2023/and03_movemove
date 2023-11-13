package com.everyone.movemove_android.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.everyone.movemove_android.ui.theme.MoveMoveAndroidTheme

abstract class BaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMoveAndroidTheme {
                InitComposeUi()
            }
        }
    }

    @Composable
    protected abstract fun InitComposeUi()
}