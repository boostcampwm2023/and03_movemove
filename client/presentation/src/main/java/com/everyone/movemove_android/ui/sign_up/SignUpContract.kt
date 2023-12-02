package com.everyone.movemove_android.ui.sign_up

import android.net.Uri
import com.everyone.movemove_android.base.BaseContract

interface SignUpContract : BaseContract<SignUpContract.State, SignUpContract.Event, SignUpContract.Effect> {

    data class State(
        val isSignUpEnabled: Boolean = false,
        val profileImageUri: Uri? = null,
        val nickname: String = "",
        val introduce: String = "",
    )

    sealed interface Event {
        data object OnClickSignUp : Event

        data class OnNicknameTyped(val nickname: String) : Event

        data class OnIntroduceTyped(val introduce: String) : Event
    }

    sealed interface Effect {
        data object GoToHomeScreen : Effect
    }
}