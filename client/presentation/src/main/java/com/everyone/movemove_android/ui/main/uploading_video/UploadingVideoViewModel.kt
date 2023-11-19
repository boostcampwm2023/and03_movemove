package com.everyone.movemove_android.ui.main.uploading_video

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Effect
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Effect.LaunchVideoPicker
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnClickPlayAndPause
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnClickPlayer
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnClickSelectVideo
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnGetUri
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnPlayAndPauseTimeOut
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnVideoReady
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.SetVideoEndTime
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.SetVideoStartTime
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.State
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
class UploadingVideoViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel(), UploadingVideoContract {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: Event) = when (event) {
        is OnClickSelectVideo -> onClickAddVideo()
        is OnGetUri -> onGetUri(event.uri)
        is OnClickPlayAndPause -> onClickPlayAndPause()
        is OnClickPlayer -> onClickPlayer()
        is OnPlayAndPauseTimeOut -> onPlayAndPauseTimeOut()
        is OnVideoReady -> onVideoReady()
        is SetVideoStartTime -> setVideoStartTime(event.time)
        is SetVideoEndTime -> setVideoEndTime(event.time)
    }

    private fun onClickAddVideo() {
        viewModelScope.launch {
            _effect.emit(LaunchVideoPicker)
        }
    }

    private fun onGetUri(uri: Uri) {
        _state.update {
            it.copy(videoUri = uri)
        }
    }

    private fun onClickPlayAndPause() {
        _state.update {
            it.copy(isPlaying = !it.isPlaying)
        }
    }

    private fun onClickPlayer() {
        _state.update {
            it.copy(isPlayAndPauseShowing = !it.isPlayAndPauseShowing)
        }
    }

    private fun onPlayAndPauseTimeOut() {
        _state.update {
            it.copy(isPlayAndPauseShowing = false)
        }
    }

    private fun onVideoReady() {
        _state.update {
            it.copy(
                isVideoReady = true,
                isPlaying = true
            )
        }
    }

    private fun setVideoStartTime(time: Long) {
        _state.update {
            it.copy(videoStartTime = time)
        }
    }

    private fun setVideoEndTime(time: Long) {
        _state.update {
            it.copy(videoEndTime = time)
        }
    }
}