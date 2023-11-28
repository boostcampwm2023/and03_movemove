package com.everyone.movemove_android.ui.my

import androidx.compose.runtime.Composable
import com.everyone.movemove_android.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyActivity : BaseActivity() {
    @Composable
    override fun InitComposeUi() {
        MyScreen()
    }
}