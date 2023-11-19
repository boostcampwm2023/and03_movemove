package com.everyone.movemove_android.ui.main.uploading_video

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.movemove_android.di.DefaultDispatcher
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.di.MainImmediateDispatcher
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
import com.everyone.movemove_android.ui.util.getVideoFilePath
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UploadingVideoViewModel @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainImmediateDispatcher private val mainImmediateDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
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
        is OnVideoReady -> onVideoReady(event.duration)
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
            it.copy(videoInfo = it.videoInfo.copy(uri = uri))
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

    private fun onVideoReady(duration: Long) {
        _state.update {
            it.copy(
                isVideoReady = true,
                isPlaying = true, videoInfo = it.videoInfo.copy(duration = duration)
            )
        }

        getThumbnailList()
    }

    private fun getThumbnailList() {
        with(state.value.videoInfo) {
            viewModelScope.launch(ioDispatcher) {
                val tempList = mutableListOf<ImageBitmap>()
                val mediaMetadataRetriever = MediaMetadataRetriever().apply {
                    uri?.let {
                        setDataSource(getVideoFilePath(context, uri))
                    }
                }

                withContext(defaultDispatcher) {
                    repeat(THUMBNAIL_COUNT) {
                        mediaMetadataRetriever.getFrameAtTime(
                            ((state.value.videoInfo.duration / THUMBNAIL_COUNT) * it + 1) * 1000L,
                            MediaMetadataRetriever.OPTION_CLOSEST
                        )?.let { bitmap ->
                            tempList.add(bitmap.asImageBitmap())
                        }
                    }
                }

                withContext(mainImmediateDispatcher) {
                    _state.update {
                        it.copy(thumbnailList = tempList)
                    }
                }

                mediaMetadataRetriever.release()
            }
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

    companion object {
        const val THUMBNAIL_COUNT = 15
    }
}