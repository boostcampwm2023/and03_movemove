package com.everyone.movemove_android.ui.login

import com.everyone.movemove_android.base.BaseContract

interface LoginContract : BaseContract<LoginContract.State,LoginContract.Event,LoginContract.Effect> {
    data class State(
        val isLoading: Boolean = false
    )

    sealed interface Event {
        data object OnClickKakaoLogin : Event
        data object OnClickGoogleLogin : Event

    }

    sealed interface Effect {
        data object LaunchKakaoLogin : Effect
        data object LaunchGoogleLogin : Effect
    }
}