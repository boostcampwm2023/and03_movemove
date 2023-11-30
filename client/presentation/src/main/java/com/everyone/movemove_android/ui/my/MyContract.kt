package com.everyone.movemove_android.ui.my

import com.everyone.movemove_android.base.BaseContract

interface MyContract : BaseContract<MyContract.State, MyContract.Event, MyContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
    )

    sealed interface Event {

    }

    sealed interface Effect {

    }
}