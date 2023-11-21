package com.everyone.movemove_android.ui.login

import androidx.compose.runtime.Composable
import com.everyone.movemove_android.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    @Composable
    override fun InitComposeUi() {
        LoginScreen()
    }

    companion object{
        const val SIGN_IN_REQUEST_CODE = 1
    }
}