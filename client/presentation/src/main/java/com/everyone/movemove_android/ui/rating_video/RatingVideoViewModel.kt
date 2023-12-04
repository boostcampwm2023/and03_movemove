package com.everyone.movemove_android.ui.rating_video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.domain.model.Videos
import com.everyone.domain.model.base.DataState
import com.everyone.domain.usecase.GetProfileUseCase
import com.everyone.domain.usecase.GetUsersVideosUploadedUseCase
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.ui.rating_video.RatingVideoContract.Effect
import com.everyone.movemove_android.ui.rating_video.RatingVideoContract.Effect.*
import com.everyone.movemove_android.ui.rating_video.RatingVideoContract.Event
import com.everyone.movemove_android.ui.rating_video.RatingVideoContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class RatingVideoViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getProfileUseCase: GetProfileUseCase,
    private val getUsersVideosUploadedUseCase: GetUsersVideosUploadedUseCase
) : ViewModel(), RatingVideoContract {
    private val _state = MutableStateFlow(State())
    override val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()
    override fun event(event: Event) = when (event) {
        is Event.OnClickedBack -> onClickedBack()
        is Event.OnClickedVideo -> onClickedVideo(event.videosLit, event.page)
    }

    init {
        getProfile()
        getUsersVideosUploaded()
    }

    private fun getProfile() {
        loading(isLoading = true)
        getProfileUseCase("550e8400-e13b-45d5-a826-446655440011").onEach { result ->
            when (result) {
                is DataState.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            profile = result.data
                        )
                    }
                }

                is DataState.Failure -> {
                    loading(isLoading = false)
                }
            }
        }.launchIn(viewModelScope + ioDispatcher)
    }

    private fun getUsersVideosUploaded() {
        loading(isLoading = true)
        getUsersVideosUploadedUseCase(
            limit = "10",
            userId = "550e8400-e13b-45d5-a826-446655440011",
            lastId = ""
        ).onEach { result ->
            when (result) {
                is DataState.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            videosUploaded = result.data
                        )
                    }
                }

                is DataState.Failure -> {
                    loading(isLoading = false)
                }
            }
        }.launchIn(viewModelScope + ioDispatcher)
    }

    private fun onClickedBack() {
        viewModelScope.launch {
            _effect.emit(OnClickedBack)
        }
    }

    private fun onClickedVideo(videosList: List<Videos>, page: Int) {
        viewModelScope.launch {
            _effect.emit(
                OnClickedVideo(
                    videosList = videosList,
                    page = page
                )
            )
        }
    }

    private fun loading(isLoading: Boolean) {
        _state.update {
            it.copy(isLoading = isLoading)
        }
    }
}