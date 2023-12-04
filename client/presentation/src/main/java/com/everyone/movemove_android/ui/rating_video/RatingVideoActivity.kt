package com.everyone.movemove_android.ui.rating_video

import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.everyone.domain.model.Videos
import com.everyone.movemove_android.base.BaseActivity
import com.everyone.movemove_android.ui.my.MyActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RatingVideoActivity : BaseActivity() {

    private val viewModel: RatingVideoViewModel by viewModels()

    @Composable
    override fun InitComposeUi() {
        RatingVideoScreen(
            viewModel = viewModel,
            navigateToWatchingVideo = ::startWatchingVideoActivity,
            onBack = ::navigateUp
        )
    }

    private fun startWatchingVideoActivity(
        videosList: List<Videos>,
        page: Int
    ) {
        // TODO 수정 필요 : 아직 WatchingVideoActivity가 없어서 임의적으로 My로 작성했습니다.
        val intent = Intent(this, MyActivity::class.java)
            .putExtra(EXTRA_KEY_VIDEOS_INFO, Pair(videosList, page))
        startActivity(intent)
    }

    private fun navigateUp() = finish()

    companion object {
        const val EXTRA_KEY_VIDEOS_INFO = "EXTRA_KEY_VIDEOS_INFO"
    }
}
