package com.everyone.movemove_android.ui.my

import com.everyone.domain.model.Profile
import com.everyone.movemove_android.base.BaseContract

interface MyContract : BaseContract<MyContract.State, MyContract.Event, MyContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val profile: Profile = Profile()
    )

    sealed interface Event {
        data object OnNullProfileNickname : Event
    }

    sealed interface Effect {
        data object CloseMyScreen : Effect
    }
}