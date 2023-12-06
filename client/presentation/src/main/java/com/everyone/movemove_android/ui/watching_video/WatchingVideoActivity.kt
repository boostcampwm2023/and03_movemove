package com.everyone.movemove_android.ui.watching_video

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.everyone.domain.model.VideosList
import com.everyone.movemove_android.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchingVideoActivity : BaseActivity() {

    private val viewModel: WatchingVideoViewModel by viewModels()

    @Composable
    override fun InitComposeUi() {
        WatchingVideoScreen(
            viewModel = viewModel,
            navigateToActivity = ::launchActivity
        )
    }

    private fun launchActivity(intent: Intent) = startActivity(intent)

    companion object {
        const val EXTRA_KEY_VIDEOS_LIST = "EXTRA_KEY_VIDEOS_LIST"
        const val EXTRA_KEY_VIDEOS_PAGE = "EXTRA_KEY_VIDEOS_PAGE"

        fun newIntent(
            context: Context,
            videosList: VideosList?,
            page: Int?
        ): Intent = Intent(context, WatchingVideoActivity::class.java).apply {
            putExtra(EXTRA_KEY_VIDEOS_LIST, videosList)
            putExtra(EXTRA_KEY_VIDEOS_PAGE, page)
        }
    }
}
