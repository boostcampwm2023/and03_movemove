package com.everyone.movemove_android.ui.my

import androidx.lifecycle.ViewModel
import com.everyone.movemove_android.ui.my.MyContract.Effect
import com.everyone.movemove_android.ui.my.MyContract.Event
import com.everyone.movemove_android.ui.my.MyContract.State
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class MyViewModel @Inject constructor() : ViewModel(), MyContract {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()


    override fun event(event: Event) = when (event) {

        else -> {}
    }


}