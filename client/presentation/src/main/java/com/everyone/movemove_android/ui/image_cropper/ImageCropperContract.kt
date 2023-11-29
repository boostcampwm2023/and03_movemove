package com.everyone.movemove_android.ui.image_cropper

import androidx.compose.ui.graphics.ImageBitmap
import com.everyone.movemove_android.base.BaseContract

interface ImageCropperContract : BaseContract<ImageCropperContract.State, ImageCropperContract.Event, ImageCropperContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val image: ImageBitmap? = null,
        val isSectionSelectorSelected: Boolean = false
    )

    sealed interface Event {
        data object OnClickImage : Event

        data object OnClickSectionSelector : Event

        data object OnClickCompleteButton : Event
    }

    sealed interface Effect {

    }
}