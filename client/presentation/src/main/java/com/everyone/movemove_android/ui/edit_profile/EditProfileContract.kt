package com.everyone.movemove_android.ui.edit_profile

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import com.everyone.domain.model.Profile
import com.everyone.movemove_android.base.BaseContract
import com.everyone.movemove_android.ui.sign_up.SignUpContract

interface EditProfileContract : BaseContract<EditProfileContract.State, EditProfileContract.Event, EditProfileContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val isEditProfileEnabled: Boolean = false,
        val profileImage: ImageBitmap? = null,
        val nickname: String = "",
        val introduce: String = "",
        val profileImageUrl: String? = null,
    )

    sealed interface Event {
        data class OnNicknameTyped(val nickname: String) : Event

        data class OnIntroduceTyped(val introduce: String) : Event

        data object OnClickSelectImage : Event

        data class OnGetUri(val uri: Uri) : Event

        data class OnGetCroppedImage(val imageBitmap: ImageBitmap) : Event

        data object OnClickEditProfile : Event
    }

    sealed interface Effect {
        data object LaunchImagePicker : Effect

        data class LaunchImageCropper(val uri: Uri) : Effect

    }
}