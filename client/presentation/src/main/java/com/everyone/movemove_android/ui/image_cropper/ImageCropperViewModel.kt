package com.everyone.movemove_android.ui.image_cropper

import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Effect
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Effect.ConvertUriToImageBitmap
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Effect.CropImage
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Effect.GoToPreviousScreen
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnClickCompleteButton
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnClickCrop
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnClickImage
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnClickSectionSelector
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnCropped
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnImageUriConverted
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnStarted
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetBoardSize
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetImageOffset
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetImageRotation
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetImageScale
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetSectionSelectorOffsetX
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetSectionSelectorOffsetY
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.SetSectionSelectorSize
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
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
class ImageCropperViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) : ImageCropperContract, ViewModel() {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>(replay = 1)
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: Event) = when (event) {
        is OnStarted -> onStarted()

        is OnImageUriConverted -> onImageUriConverted(event.imageBitmap)

        is OnClickImage -> onClickImage()

        is OnClickSectionSelector -> onClickSectionSelector()

        is OnClickCrop -> onClickCrop()

        is OnClickCompleteButton -> onClickCompleteButton()

        is SetImageOffset -> setImageOffset(event.offset)

        is SetImageScale -> setImageScale(event.scale)

        is SetImageRotation -> setImageRotation(event.rotation)

        is SetBoardSize -> setBoardSize(event.size)

        is SetSectionSelectorOffsetX -> setSectionSelectorOffsetX(event.offsetX)

        is SetSectionSelectorOffsetY -> setSectionSelectorOffsetY(event.offsetY)

        is SetSectionSelectorSize -> setSectionSelectorSize(event.size)

        is OnCropped -> onCropped(event.imageBitmap)
    }

    private fun onStarted() {
        savedStateHandle.get<Uri>(ImageCropperActivity.KEY_IMAGE_URI)?.let {
            convertUriToImageBitmap(it)
        } ?: run {
            // todo : 예외 처리
        }
    }

    private fun onImageUriConverted(imageBitmap: ImageBitmap) {
        _state.update {
            it.copy(
                isLoading = false,
                imageBitmap = imageBitmap
            )
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
            _effect.emit(
                CropImage(
                    offset = state.value.sectionSelectorState.offset,
                    size = state.value.sectionSelectorState.size
                )
            )
        }
    }

    private fun onClickCompleteButton() {
        state.value.croppedImage?.let { imageBitmap ->
            viewModelScope.launch {
                _effect.emit(GoToPreviousScreen(imageBitmap))
            }
        }
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
            val originalOffset = it.sectionSelectorState.offset
            val newOffset = Offset(
                x = originalOffset.x + offsetX,
                y = originalOffset.y
            )
            val size = it.sectionSelectorState.size
            val boardWidth = it.boardSize.width

            it.copy(
                sectionSelectorState = it.sectionSelectorState.copy(
                    offset = if (newOffset.x - size / 2f in 0f..boardWidth - size) {
                        newOffset
                    } else {
                        originalOffset
                    }
                )
            )
        }
    }

    private fun setSectionSelectorOffsetY(offsetY: Float) {
        _state.update {
            val originalOffset = it.sectionSelectorState.offset
            val newOffset = Offset(
                x = originalOffset.x,
                y = originalOffset.y + offsetY
            )
            val size = it.sectionSelectorState.size
            val boardHeight = it.boardSize.height

            it.copy(
                sectionSelectorState = it.sectionSelectorState.copy(
                    offset = if (newOffset.y - size / 2f in 0f..boardHeight - size) {
                        newOffset
                    } else {
                        originalOffset
                    }
                )
            )
        }
    }

    private fun setSectionSelectorSize(size: Float) {
        _state.update {
            val offsetX = it.sectionSelectorState.offset.x
            val offsetY = it.sectionSelectorState.offset.y
            val originalSize = it.sectionSelectorState.size
            val newSize = originalSize * size
            val boardWidth = it.boardSize.width
            val boardHeight = it.boardSize.height

            it.copy(
                sectionSelectorState = it.sectionSelectorState.copy(
                    size = if (offsetX - newSize / 2f in 0f..boardWidth - newSize &&
                        offsetY - newSize / 2f in 0f..boardHeight - newSize
                    ) {
                        newSize
                    } else {
                        originalSize
                    }
                )
            )
        }
    }

    private fun onCropped(imageBitmap: ImageBitmap) {
        _state.update {
            it.copy(croppedImage = imageBitmap)
        }
    }

    private fun convertUriToImageBitmap(uri: Uri) {
        viewModelScope.launch {
            _effect.emit(ConvertUriToImageBitmap(uri))
        }
    }
}