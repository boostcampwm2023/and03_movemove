package com.everyone.movemove_android.ui.screens.profile

import com.everyone.movemove_android.base.BaseContract
import com.everyone.movemove_android.ui.sign_up.SignUpContract

interface ProfileContract :
    BaseContract<ProfileContract.State, ProfileContract.Event, ProfileContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
    )

    sealed interface Event {
        data object LoadingStart : Event
        data object LoadingEnd : Event
        data object OnClickedMenu : Event

    }

    sealed interface Effect {
        data object NavigateToMy : Effect
    }
}
