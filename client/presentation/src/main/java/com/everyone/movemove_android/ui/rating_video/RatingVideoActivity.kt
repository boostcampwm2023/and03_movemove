package com.everyone.movemove_android.ui.rating_video

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.everyone.domain.model.Videos
import com.everyone.movemove_android.base.BaseActivity
import com.everyone.movemove_android.ui.my.MyActivity
import com.everyone.movemove_android.ui.profile.ProfileActivity
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
        const val EXTRA_KEY_UUID = "EXTRA_KEY_UUID"

        fun newIntent(
            context: Context,
            uuid: String
        ): Intent = Intent(context, RatingVideoActivity::class.java).apply {
            putExtra(EXTRA_KEY_UUID, uuid)
        }
    }
}
