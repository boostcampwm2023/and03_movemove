package com.everyone.movemove_android.ui.sign_up

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import com.everyone.movemove_android.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : BaseActivity() {
    @Composable
    override fun InitComposeUi() {
        SignUpScreen()
    }

    companion object {
        const val KEY_BUNDLE = "key_bundle"
        const val KEY_ID_TOKEN = "key_id_token"
        const val KEY_UUID = "key_uuid"
        const val KEY_PLATFORM = "key_platform"

        fun newIntent(
            context: Context,
            idToken: String,
            uuid: String,
            platform: String
        ): Intent = Intent(context, SignUpActivity::class.java).apply {
            putExtra(
                KEY_BUNDLE, bundleOf(
                    KEY_ID_TOKEN to idToken,
                    KEY_UUID to uuid,
                    KEY_PLATFORM to platform
                )
            )
        }
    }
}