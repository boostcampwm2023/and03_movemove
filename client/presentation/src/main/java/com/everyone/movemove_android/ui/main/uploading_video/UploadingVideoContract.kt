package com.everyone.movemove_android.ui.main.uploading_video

import android.graphics.Bitmap
import android.net.Uri
import com.everyone.movemove_android.base.BaseContract

interface UploadingVideoContract : BaseContract<UploadingVideoContract.State, UploadingVideoContract.Event, UploadingVideoContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val videoUri: Uri? = null,
        val isPlaying: Boolean = false,
        val isPlayAndPauseShowing: Boolean = false,
        val isVideoReady: Boolean = false,
        val videoStartTime: Long = 0L,
        val videoEndTime: Long = 0L,
        val thumbnailList: List<Bitmap> = emptyList(),
    )

    sealed interface Event {
        data object OnClickSelectVideo : Event
        data class OnGetUri(val uri: Uri) : Event
        data object OnClickPlayAndPause : Event
        data object OnClickPlayer : Event
        data object OnPlayAndPauseTimeOut : Event
        data object OnVideoReady : Event
        data class SetVideoStartTime(val time: Long) : Event
        data class SetVideoEndTime(val time: Long) : Event
    }

    sealed interface Effect {
        data object LaunchVideoPicker : Effect
    }
}