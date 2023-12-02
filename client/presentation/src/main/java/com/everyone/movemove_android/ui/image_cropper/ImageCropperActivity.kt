package com.everyone.movemove_android.ui.image_cropper

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import com.everyone.movemove_android.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageCropperActivity : BaseActivity() {
    @Composable
    override fun InitComposeUi() {
        ImageCropperScreen()
    }

    companion object {
        const val KEY_IMAGE_URI = "key_image_uri"

        fun newIntent(
            context: Context,
            uri: Uri
        ): Intent = Intent(context, ImageCropperActivity::class.java).apply {
            putExtra(KEY_IMAGE_URI, uri)
        }
    }
}