package com.everyone.movemove_android.ui.sign_up

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import com.everyone.movemove_android.base.BaseContract

interface SignUpContract : BaseContract<SignUpContract.State, SignUpContract.Event, SignUpContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val isSignUpEnabled: Boolean = false,
        val profileImage: ImageBitmap? = null,
        val nickname: String = "",
        val introduce: String = "",
        val isErrorDialogShowing: Boolean = false,
        val errorDialogTextResourceId: Int = 0
    )

    sealed interface Event {
        data class OnNicknameTyped(val nickname: String) : Event

        data class OnIntroduceTyped(val introduce: String) : Event

        data object OnClickSelectImage : Event

        data class OnGetUri(val uri: Uri) : Event

        data class OnGetCroppedImage(val imageBitmap: ImageBitmap) : Event

        data object OnClickSignUp : Event

        data object OnErrorDialogDismissed : Event
    }

    sealed interface Effect {
        data object LaunchImagePicker : Effect

        data class LaunchImageCropper(val uri: Uri) : Effect

        data object GoToHomeScreen : Effect
    }
}