package com.everyone.movemove_android.ui.rating_video

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.everyone.movemove_android.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RatingVideoActivity : BaseActivity() {

    private val viewModel: RatingVideoViewModel by viewModels()

    @Composable
    override fun InitComposeUi() {
        RatingVideoScreen(
            viewModel = viewModel,
            navigateToWatchingVideo = ::launchActivity,
            onBack = ::navigateUp
        )
    }

    private fun launchActivity(intent: Intent) = startActivity(intent)

    private fun navigateUp() = finish()

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, RatingVideoActivity::class.java)
    }
}
