package com.everyone.movemove_android.ui.profile

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.everyone.movemove_android.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : BaseActivity() {

    private val viewModel: ProfileViewModel by viewModels()

    @Composable
    override fun InitComposeUi() {
        ProfileScreen(
            viewModel = viewModel,
            navigateToActivity = ::launchActivity
        )
    }

    private fun launchActivity(intent: Intent) = startActivity(intent)

    companion object {
        const val EXTRA_KEY_UUID = "EXTRA_KEY_UUID"

        fun newIntent(
            context: Context,
            uuid: String?,
        ): Intent = Intent(context, ProfileActivity::class.java).apply {
            putExtra(EXTRA_KEY_UUID, uuid)
        }
    }
}
