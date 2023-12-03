package com.everyone.movemove_android.ui.starting

import com.everyone.movemove_android.base.BaseContract

interface StartingContract : BaseContract<StartingContract.State, StartingContract.Event, StartingContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val isSignUpNeeded: Boolean = false,
    )

    sealed interface Event {
        data object OnStarted : Event

        data object OnClickKakaoLogin : Event

        data object OnClickGoogleLogin : Event

        data class OnSocialLoginSuccess(
            val accessToken: String,
            val uuid: String,
            val platform: String,
        ) : Event
    }

    sealed interface Effect {
        data object LaunchKakaoLogin : Effect

        data object LaunchGoogleLogin : Effect

        data class GoToSignUpScreen(
            val accessToken: String,
            val uuid: String,
            val platform: String
        ) : Effect

        data object GoToHomeScreen : Effect
    }
}