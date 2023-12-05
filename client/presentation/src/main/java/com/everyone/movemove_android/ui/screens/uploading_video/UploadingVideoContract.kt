package com.everyone.movemove_android.ui.screens.uploading_video

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import com.everyone.domain.model.UploadCategory
import com.everyone.movemove_android.R
import com.everyone.movemove_android.base.BaseContract
import java.io.File

interface UploadingVideoContract : BaseContract<UploadingVideoContract.State, UploadingVideoContract.Event, UploadingVideoContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val isPlaying: Boolean = false,
        val isPlayAndPauseShowing: Boolean = false,
        val isVideoReady: Boolean = false,
        val videoUri: Uri? = null,
        val videoDuration: Long = 0L,
        val videoStartTime: Long = 0L,
        val videoEndTime: Long = 0L,
        val timelineWidth: Int = 0,
        val timelineUnitWidth: Long = 0L,
        val videoLengthUnit: Long = 0L,
        val indicatorPosition: Int = 0,
        val isLowerBoundDragging: Boolean = false,
        val lowerBoundPosition: Float = 0f,
        val isUpperBoundDragging: Boolean = false,
        val upperBoundPosition: Float = 0f,
        val thumbnailList: List<ImageBitmap> = emptyList(),
        val title: String = "",
        val description: String = "",
        val isUploadEnabled: Boolean = false,
        val isBottomSheetShowing: Boolean = false,
        val category: UploadCategory? = null,
        val isSelectThumbnailDialogShowing: Boolean = false,
        val selectedThumbnail: ImageBitmap? = null,
        val stagedVideoFile: File? = null,
        val isErrorDialogShowing: Boolean = false,
        val errorDialogTextResourceId: Int = 0
    )

    sealed interface Event {
        data object OnClickSelectVideo : Event

        data class OnGetUri(val uri: Uri) : Event

        data object OnClickPlayAndPause : Event

        data object OnClickPlayer : Event

        data object OnPlayAndPauseTimeOut : Event

        data class OnVideoReady(val duration: Long) : Event

        data class OnTimelineWidthMeasured(val timelineWidth: Int) : Event

        data object OnLowerBoundDraggingStarted : Event

        data object OnLowerBoundDraggingFinished : Event

        data class OnLowerBoundDrag(
            val offset: Float,
            val boundWidthPx: Float
        ) : Event

        data object OnUpperBoundDraggingStarted : Event

        data object OnUpperBoundDraggingFinished : Event

        data class OnUpperBoundDrag(
            val offset: Float,
            val boundWidthPx: Float
        ) : Event

        data class OnVideoPositionUpdated(val videoPosition: Long) : Event

        data object SetVideoStartTime : Event

        data object SetVideoEndTime : Event

        data class OnTitleTyped(val title: String) : Event

        data class OnDescriptionTyped(val description: String) : Event

        data object OnClickSelectThumbnail : Event

        data object OnClickSelectCategory : Event

        data class OnCategorySelected(val category: UploadCategory) : Event

        data object OnBottomSheetHide : Event

        data class OnClickThumbnail(val thumbnail: ImageBitmap) : Event

        data object OnSelectThumbnailDialogDismissed : Event

        data object OnClickUpload : Event

        data object OnErrorDialogDismissed : Event
    }

    sealed interface Effect {
        data object LaunchVideoPicker : Effect

        data class SeekToStart(val position: Long) : Effect

        data object Finish : Effect
    }
}