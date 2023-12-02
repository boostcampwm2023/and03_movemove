package com.everyone.movemove_android.ui.sign_up

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import com.everyone.movemove_android.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : BaseActivity() {
    @Composable
    override fun InitComposeUi() {
        SignUpScreen()
    }

    companion object {
        const val KAKAO = "kakao"
        const val GOOGLE = "google"

        fun newIntent(
            context: Context,
            signedPlatform: String
        ): Intent = Intent(context, SignUpActivity::class.java)
    }
}