package com.everyone.movemove_android.ui.screens.profile

import com.everyone.movemove_android.base.BaseContract

interface ProfileContract :
    BaseContract<ProfileContract.State, ProfileContract.Event, ProfileContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
    )

    sealed interface Event {
        data object LoadingStart : Event
        data object LoadingEnd : Event

    }

    sealed interface Effect {}
}
