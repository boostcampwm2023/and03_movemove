package com.everyone.movemove_android.ui.edit_profile

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import com.everyone.movemove_android.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileActivity : BaseActivity() {
    @Composable
    override fun InitComposeUi() {
        EditProfileScreen()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, EditProfileActivity::class.java)
    }
}