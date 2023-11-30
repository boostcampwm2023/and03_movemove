package com.everyone.movemove_android.ui.image_cropper

import androidx.compose.runtime.Composable
import com.everyone.movemove_android.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageCropperActivity : BaseActivity() {
    @Composable
    override fun InitComposeUi() {
        ImageCropperScreen()
    }
}