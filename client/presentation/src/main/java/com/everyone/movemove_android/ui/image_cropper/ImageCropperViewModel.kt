package com.everyone.movemove_android.ui.image_cropper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Effect
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Effect.CropImage
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnClickCompleteButton
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnClickCrop
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnClickImage
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnClickSectionSelector
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetBoardSize
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetImageOffset
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetImageRotation
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetImageScale
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetSectionSelectorOffsetX
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetSectionSelectorOffsetY
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetSectionSelectorSize
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageCropperViewModel @Inject constructor(
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : ImageCropperContract, ViewModel() {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: Event) = when (event) {
        is OnClickImage -> {
            onClickImage()
        }

        is OnClickSectionSelector -> {
            onClickSectionSelector()
        }

        is OnClickCrop -> {
            onClickCrop()
        }

        is OnClickCompleteButton -> {
            onClickCompleteButton()
        }

        is SetImageOffset -> {
            setImageOffset(event.offset)
        }

        is SetImageScale -> {
            setImageScale(event.scale)
        }

        is SetImageRotation -> {
            setImageRotation(event.rotation)
        }

        is SetBoardSize -> {
            setBoardSize(event.size)
        }

        is SetSectionSelectorOffsetX -> {
            setSectionSelectorOffsetX(event.offsetX)
        }

        is SetSectionSelectorOffsetY -> {
            setSectionSelectorOffsetY(event.offsetY)
        }

        is SetSectionSelectorSize -> {
            setSectionSelectorSize(event.size)
        }
    }

    private fun onClickImage() {
        _state.update {
            it.copy(isSectionSelectorSelected = true)
        }
    }

    private fun onClickSectionSelector() {
        _state.update {
            it.copy(isSectionSelectorSelected = false)
        }
    }

    private fun onClickCrop() {
        viewModelScope.launch {
            _effect.emit(CropImage)
        }
    }

    private fun onClickCompleteButton() {

    }

    private fun setImageOffset(offset: Offset) {
        _state.update {
            it.copy(imageState = it.imageState.copy(offset = it.imageState.offset + offset))
        }
    }

    private fun setImageScale(scale: Float) {
        _state.update {
            it.copy(imageState = it.imageState.copy(scale = it.imageState.scale * scale))
        }
    }

    private fun setImageRotation(rotation: Float) {
        _state.update {
            it.copy(imageState = it.imageState.copy(rotation = it.imageState.rotation + rotation))
        }
    }

    private fun setBoardSize(size: Size) {
        _state.update {
            it.copy(boardSize = size)
        }
    }

    private fun setSectionSelectorOffsetX(offsetX: Float) {
        _state.update {
            val originalOffsetX = it.sectionSelectorState.offsetX
            val newOffsetX = it.sectionSelectorState.offsetX + offsetX
            it.copy(
                sectionSelectorState = it.sectionSelectorState.copy(
                    offsetX = if (newOffsetX - it.sectionSelectorState.size / 2f in 0f..it.boardSize.width - it.sectionSelectorState.size) {
                        newOffsetX
                    } else {
                        originalOffsetX
                    }
                )
            )
        }
    }

    private fun setSectionSelectorOffsetY(offsetY: Float) {
        _state.update {
            val originalOffsetY = it.sectionSelectorState.offsetY
            val newOffsetY = it.sectionSelectorState.offsetY + offsetY
            it.copy(
                sectionSelectorState = it.sectionSelectorState.copy(
                    offsetY = if (newOffsetY - it.sectionSelectorState.size / 2f in 0f..it.boardSize.height - it.sectionSelectorState.size) {
                        newOffsetY
                    } else {
                        originalOffsetY
                    }
                )
            )
        }
    }

    private fun setSectionSelectorSize(size: Float) {
        _state.update {
            val offsetX = it.sectionSelectorState.offsetX
            val offsetY = it.sectionSelectorState.offsetY
            val originalSize = it.sectionSelectorState.size
            val newSize = it.sectionSelectorState.size * size
            it.copy(
                sectionSelectorState = it.sectionSelectorState.copy(
                    size = if (offsetX - newSize / 2f in 0f..it.boardSize.width - newSize &&
                        offsetY - newSize / 2f in 0f..it.boardSize.height - newSize
                    ) {
                        newSize
                    } else {
                        originalSize
                    }
                )
            )
        }
    }
}