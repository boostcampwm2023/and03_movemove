package com.everyone.movemove_android.ui.image_cropper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import com.everyone.movemove_android.base.BaseContract

interface ImageCropperContract : BaseContract<ImageCropperContract.State, ImageCropperContract.Event, ImageCropperContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val image: ImageBitmap? = null,
        val isSectionSelectorSelected: Boolean = false,
        val croppedImage: ImageBitmap? = null,
        val imageState: ImageState = ImageState(),
        val boardSize: Size = Size(0f, 0f),
        val sectionSelectorState: SectionSelectorState = SectionSelectorState(),
    )

    data class ImageState(
        val offset: Offset = Offset(0f, 0f),
        val scale: Float = 1f,
        val rotation: Float = 0f
    )

    data class SectionSelectorState(
        val offsetX: Float = DEFAULT_SECTION_SELECTOR_SIZE / 2f,
        val offsetY: Float = DEFAULT_SECTION_SELECTOR_SIZE / 2f,
        val size: Float = DEFAULT_SECTION_SELECTOR_SIZE
    )

    sealed interface Event {
        data object OnClickImage : Event

        data object OnClickSectionSelector : Event

        data object OnClickCrop : Event

        data object OnClickCompleteButton : Event

        data class SetImageOffset(val offset: Offset) : Event

        data class SetImageScale(val scale: Float) : Event

        data class SetImageRotation(val rotation: Float) : Event

        data class SetBoardSize(val size: Size) : Event

        data class SetSectionSelectorOffsetX(val offsetX: Float) : Event

        data class SetSectionSelectorOffsetY(val offsetY: Float) : Event

        data class SetSectionSelectorSize(val size: Float) : Event
    }

    sealed interface Effect {
        data object CropImage : Effect
    }

    companion object {
        private const val DEFAULT_SECTION_SELECTOR_SIZE = 500f
    }
}