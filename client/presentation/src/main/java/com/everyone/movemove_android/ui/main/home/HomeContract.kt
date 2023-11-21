package com.everyone.movemove_android.ui.main.home

import com.everyone.movemove_android.base.BaseContract

interface HomeContract :
    BaseContract<HomeContract.State, HomeContract.Event, HomeContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
    )

    sealed interface Event {}

    sealed interface Effect {}
}
