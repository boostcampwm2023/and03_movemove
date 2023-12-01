package com.everyone.movemove_android.ui.my

import com.everyone.domain.model.Profile
import com.everyone.movemove_android.base.BaseContract

interface MyContract : BaseContract<MyContract.State, MyContract.Event, MyContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val profile: Profile = Profile(
            uuid = null,
            nickname = null,
            statusMessage = null,
            profileImageUrl = null
        )
    )

    sealed interface Event {}

    sealed interface Effect {}
}