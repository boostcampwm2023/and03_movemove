package com.everyone.movemove_android.ui.screens.signup

import androidx.lifecycle.ViewModel
import com.everyone.movemove_android.ui.screens.signup.SignUpContract.Effect
import com.everyone.movemove_android.ui.screens.signup.SignUpContract.Event.OnClickSignUp
import com.everyone.movemove_android.ui.screens.signup.SignUpContract.Event.OnIntroduceTyped
import com.everyone.movemove_android.ui.screens.signup.SignUpContract.Event.OnNicknameTyped
import com.everyone.movemove_android.ui.screens.signup.SignUpContract.State
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SignUpViewModel @Inject constructor() : ViewModel(), SignUpContract {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: SignUpContract.Event) = when (event) {
        is OnClickSignUp -> TODO()
        is OnIntroduceTyped -> TODO()
        is OnNicknameTyped -> TODO()
    }
}