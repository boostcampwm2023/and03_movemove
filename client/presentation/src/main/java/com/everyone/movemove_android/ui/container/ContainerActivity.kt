package com.everyone.movemove_android.ui.container

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import com.everyone.domain.model.VideosTrend
import com.everyone.movemove_android.base.BaseActivity
import com.everyone.movemove_android.ui.watching_video.WatchingVideoActivity
import com.everyone.movemove_android.ui.my.MyActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContainerActivity : BaseActivity() {
    @Composable
    override fun InitComposeUi() {
        MainScreen(
            navigateToWatchingVideo = ::startWatchingVideoActivity,
            navigateToMy = ::startMyActivity
        )
    }

    private fun startWatchingVideoActivity(
        videosTrend: VideosTrend?,
        page: Int?
    ) {
        val intent = Intent(this, WatchingVideoActivity::class.java)
            .putExtra(EXTRA_KEY_VIDEOS_TREND, videosTrend)
            .putExtra(EXTRA_KEY_VIDEOS_PAGE, page)
        startActivity(intent)
    }

    private fun startMyActivity() = startActivity(Intent(this, MyActivity::class.java))

    companion object {
        const val EXTRA_KEY_VIDEOS_TREND = "EXTRA_KEY_VIDEOS_TREND"
        const val EXTRA_KEY_VIDEOS_PAGE = "EXTRA_KEY_VIDEOS_PAGE"

        fun newIntent(context: Context): Intent = Intent(context, ContainerActivity::class.java)
    }
}
