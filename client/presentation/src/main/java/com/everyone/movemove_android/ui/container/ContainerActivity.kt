package com.everyone.movemove_android.ui.container

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import com.everyone.movemove_android.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContainerActivity : BaseActivity() {
    @Composable
    override fun InitComposeUi() {
        MainScreen(navigateToActivity = ::launchActivity)
    }

    private fun launchActivity(intent: Intent) = startActivity(intent)

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, ContainerActivity::class.java)
    }
}
