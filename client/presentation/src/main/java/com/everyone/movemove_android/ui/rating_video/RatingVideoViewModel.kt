package com.everyone.movemove_android.ui.rating_video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.domain.model.VideosList
import com.everyone.domain.model.base.DataState
import com.everyone.domain.usecase.GetStoredUUIDUseCase
import com.everyone.domain.usecase.GetUsersVideosRatedUseCase
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.di.MainImmediateDispatcher
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
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RatingVideoViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainImmediateDispatcher private val mainImmediateDispatcher: CoroutineDispatcher,
    private val getStoredUUIDUseCase: GetStoredUUIDUseCase,
    private val getUsersVideosRatedUseCase: GetUsersVideosRatedUseCase
) : ViewModel(), RatingVideoContract {
    private val _state = MutableStateFlow(State())
    override val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: Event) = when (event) {
        is Event.OnClickedBack -> onClickedBack()
        is Event.OnClickedVideo -> onClickedVideo(event.videosLit, event.page)
        is Event.Refresh -> getStoredUUID()
    }

    init {
        getStoredUUID()
    }

    private fun getStoredUUID() {
        viewModelScope.launch {
            loading(isLoading = true)
            getStoredUUIDUseCase().onEach { result ->
                result?.let { uuid ->
                    getUsersVideosUploaded(uuid = uuid)
                } ?: run {
                    _state.update {
                        it.copy(isError = true)
                    }
                }
            }.launchIn(viewModelScope + ioDispatcher)
            loading(isLoading = false)
        }
    }

    private fun getUsersVideosUploaded(uuid: String) {
        viewModelScope.launch {

            loading(isLoading = true)
            getUsersVideosRatedUseCase(
                limit = LIMIT,
                userId = uuid,
                lastRatedAt = ""
            ).onEach { result ->
                when (result) {
                    is DataState.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isError = false,
                                videosRated = result.data
                            )
                        }
                    }

                    is DataState.Failure -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope + ioDispatcher)
        }
    }

    private fun onClickedBack() {
        viewModelScope.launch {
            _effect.emit(OnClickedBack)
        }
    }

    private fun onClickedVideo(videosList: VideosList, page: Int) {
        viewModelScope.launch {
            _effect.emit(
                OnClickedVideo(
                    videosList = videosList,
                    page = page
                )
            )
        }
    }

    private suspend fun loading(isLoading: Boolean) {
        withContext(mainImmediateDispatcher) {
            _state.update {
                it.copy(isLoading = isLoading)
            }
        }
    }

    companion object {
        const val LIMIT = "50"
    }
}