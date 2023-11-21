package com.everyone.movemove_android.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.movemove_android.ui.login.LoginContract.Effect
import com.everyone.movemove_android.ui.login.LoginContract.Effect.LaunchGoogleLogin
import com.everyone.movemove_android.ui.login.LoginContract.Effect.LaunchKakaoLogin
import com.everyone.movemove_android.ui.login.LoginContract.Event
import com.everyone.movemove_android.ui.login.LoginContract.Event.OnClickGoogleLogin
import com.everyone.movemove_android.ui.login.LoginContract.Event.OnClickKakaoLogin
import com.everyone.movemove_android.ui.login.LoginContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel(), LoginContract {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: Event) = when (event) {
        is OnClickKakaoLogin -> onClickKakaoLogin()
        is OnClickGoogleLogin -> onClickGoogleLogin()
    }

    private fun onClickKakaoLogin() {
        viewModelScope.launch {
            _effect.emit(LaunchKakaoLogin)
        }
    }

    private fun onClickGoogleLogin() {
        viewModelScope.launch {
            _effect.emit(LaunchGoogleLogin)
        }
    }

}