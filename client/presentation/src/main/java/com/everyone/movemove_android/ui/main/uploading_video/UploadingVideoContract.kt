package com.everyone.movemove_android.ui.main.uploading_video

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import com.everyone.movemove_android.base.BaseContract

interface UploadingVideoContract : BaseContract<UploadingVideoContract.State, UploadingVideoContract.Event, UploadingVideoContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val isPlaying: Boolean = false,
        val isPlayAndPauseShowing: Boolean = false,
        val isVideoReady: Boolean = false,
        val videoInfo: VideoInfo = VideoInfo(),
        val videoStartTime: Long = 0L,
        val videoEndTime: Long = 0L,
        val thumbnailList: List<ImageBitmap> = emptyList(),
        val title: String = "",
        val description: String = "",
        val isUploadEnabled: Boolean = false,
    )

    data class VideoInfo(
        val uri: Uri? = null,
        val duration: Long = 0L
    )

    sealed interface Event {
        data object OnClickSelectVideo : Event
        data class OnGetUri(val uri: Uri) : Event
        data object OnClickPlayAndPause : Event
        data object OnClickPlayer : Event
        data object OnPlayAndPauseTimeOut : Event
        data class OnVideoReady(val duration: Long) : Event
        data class SetVideoStartTime(val time: Long) : Event
        data class SetVideoEndTime(val time: Long) : Event
        data class OnTitleTyped(val title: String) : Event
        data class OnDescriptionTyped(val description: String) : Event
        data object OnClickUpload : Event
    }

    sealed interface Effect {
        data object LaunchVideoPicker : Effect
    }
}