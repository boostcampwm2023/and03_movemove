package com.everyone.movemove_android.ui.main

import androidx.compose.runtime.Composable
import com.everyone.movemove_android.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Composable
    override fun InitComposeUi() {
        MainScreen()
    }
}
