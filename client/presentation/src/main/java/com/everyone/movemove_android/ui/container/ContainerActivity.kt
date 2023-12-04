package com.everyone.movemove_android.ui.container

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import com.everyone.domain.model.Videos
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
        videosList: List<Videos>?,
        page: Int?
    ) {
        val intent = Intent(this, WatchingVideoActivity::class.java)
            .putExtra("EXTRA_KEY_VIDEOS_INFO", Pair(videosList, page))
        startActivity(intent)
    }

    private fun startMyActivity() = startActivity(Intent(this, MyActivity::class.java))

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, ContainerActivity::class.java)
    }
}
