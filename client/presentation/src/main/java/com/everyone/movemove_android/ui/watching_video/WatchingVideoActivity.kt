package com.everyone.movemove_android.ui.watching_video

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.everyone.domain.model.Videos
import com.everyone.domain.model.VideosTrend
import com.everyone.movemove_android.base.BaseActivity
import com.everyone.movemove_android.ui.image_cropper.ImageCropperActivity
import com.everyone.movemove_android.ui.my.MyActivity
import com.everyone.movemove_android.ui.rating_video.RatingVideoActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchingVideoActivity : BaseActivity() {

    private val viewModel: WatchingVideoViewModel by viewModels()

    @Composable
    override fun InitComposeUi() {
        WatchingVideoScreen(
            viewModel = viewModel,
        )
    }

    companion object {
        const val EXTRA_KEY_VIDEOS_TREND = "EXTRA_KEY_VIDEOS_TREND"
        const val EXTRA_KEY_VIDEOS_PAGE = "EXTRA_KEY_VIDEOS_PAGE"

        fun newIntent(
            context: Context,
            videosTrend: VideosTrend,
            page: Int
        ): Intent = Intent(context, WatchingVideoActivity::class.java).apply {
            putExtra(EXTRA_KEY_VIDEOS_TREND, videosTrend)
            putExtra(EXTRA_KEY_VIDEOS_PAGE, page)
        }
    }
}
