package com.everyone.movemove_android.ui.screens.uploading_video

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import com.everyone.domain.model.Category
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
        val isBottomSheetShowing: Boolean = false,
        val category: Category? = null,
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
        data object OnClickSelectCategory : Event
        data class OnCategorySelected(val category: Category) : Event
        data object OnBottomSheetHide : Event
    }

    sealed interface Effect {
        data object LaunchVideoPicker : Effect
    }
}