package com.everyone.movemove_android.ui.main.uploading_video

import android.net.Uri
import com.everyone.movemove_android.base.BaseContract

interface UploadingVideoContract : BaseContract<UploadingVideoContract.State, UploadingVideoContract.Event, UploadingVideoContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val videoUri: Uri? = null,
    )

    sealed interface Event {
        data object OnClickSelectVideo : Event

        data class OnGetUri(val uri: Uri) : Event
    }

    sealed interface Effect {
        data object LaunchVideoPicker : Effect
    }
}