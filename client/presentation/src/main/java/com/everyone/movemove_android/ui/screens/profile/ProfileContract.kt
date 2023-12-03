package com.everyone.movemove_android.ui.screens.profile

import com.everyone.domain.model.Profile
import com.everyone.movemove_android.base.BaseContract

interface ProfileContract :
    BaseContract<ProfileContract.State, ProfileContract.Event, ProfileContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val profile: Profile = Profile()
    )

    sealed interface Event {
        data object OnClickedMenu : Event

    }

    sealed interface Effect {
        data object NavigateToMy : Effect
    }
}
