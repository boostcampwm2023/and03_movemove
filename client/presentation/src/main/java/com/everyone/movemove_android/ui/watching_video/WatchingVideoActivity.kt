package com.everyone.movemove_android.ui.watching_video

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.everyone.movemove_android.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchingVideoActivity : BaseActivity() {

    private val viewModel: WatchingVideoViewModel by viewModels()

    @Composable
    override fun InitComposeUi() {
        WatchingVideoScreen(
            viewModel = viewModel,
            onBack = ::navigateUp
        )
    }

    private fun navigateUp() = finish()

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, WatchingVideoActivity::class.java)
    }
}
