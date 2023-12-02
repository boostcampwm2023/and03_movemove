package com.everyone.movemove_android.ui.sign_up

import android.net.Uri
import com.everyone.movemove_android.base.BaseContract

interface SignUpContract : BaseContract<SignUpContract.State, SignUpContract.Event, SignUpContract.Effect> {

    data class State(
        val isPossibleSignUp: Boolean = false,
        val profileImageUri: Uri? = null
    )

    sealed interface Event {
        data object OnClickSignUp : Event
        data class OnNicknameTyped(val title: String) : Event
        data class OnIntroduceTyped(val title: String) : Event
    }

    sealed interface Effect {
        data object LaunchHomeScreen : Effect
    }
}