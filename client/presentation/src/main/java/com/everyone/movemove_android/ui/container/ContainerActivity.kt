package com.everyone.movemove_android.ui.container

import androidx.compose.runtime.Composable
import com.everyone.movemove_android.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContainerActivity : BaseActivity() {
    @Composable
    override fun InitComposeUi() {
        MainScreen()
    }
}
