package com.everyone.movemove_android.ui.image_cropper

import androidx.lifecycle.ViewModel
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Effect
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnClickCompleteButton
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnClickImage
import com.everyone.movemove_android.ui.image_cropper.ImageCropperContract.Event.OnClickSectionSelector
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

        is OnClickCompleteButton -> {

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

    private fun onClickCompleteButton() {

    }
}