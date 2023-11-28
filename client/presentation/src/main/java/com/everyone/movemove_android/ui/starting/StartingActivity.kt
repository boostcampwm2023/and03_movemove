package com.everyone.movemove_android.ui.starting

import androidx.compose.runtime.Composable
import com.everyone.movemove_android.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    @Composable
    override fun InitComposeUi() {
        StartingScreen()
    }

    companion object {
        const val SIGN_IN_REQUEST_CODE = 1
    }
}