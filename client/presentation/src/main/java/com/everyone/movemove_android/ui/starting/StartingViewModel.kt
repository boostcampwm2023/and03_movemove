package com.everyone.movemove_android.ui.starting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.domain.usecase.GetStoredSignedPlatformUseCase
import com.everyone.domain.usecase.GetStoredUserIdUseCase
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.di.MainImmediateDispatcher
import com.everyone.movemove_android.ui.starting.StartingContract.Effect
import com.everyone.movemove_android.ui.starting.StartingContract.Effect.LaunchGoogleLogin
import com.everyone.movemove_android.ui.starting.StartingContract.Effect.LaunchKakaoLogin
import com.everyone.movemove_android.ui.starting.StartingContract.Event
import com.everyone.movemove_android.ui.starting.StartingContract.Event.OnClickGoogleLogin
import com.everyone.movemove_android.ui.starting.StartingContract.Event.OnClickKakaoLogin
import com.everyone.movemove_android.ui.starting.StartingContract.Event.OnStarted
import com.everyone.movemove_android.ui.starting.StartingContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StartingViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainImmediateDispatcher private val mainImmediateDispatcher: CoroutineDispatcher,
    private val getStoredUserIdUseCase: GetStoredUserIdUseCase,
    private val getStoredSignedPlatformUseCase: GetStoredSignedPlatformUseCase
) : ViewModel(), StartingContract {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: Event) = when (event) {
        is OnStarted -> onStarted()

        is OnClickKakaoLogin -> onClickKakaoLogin()

        is OnClickGoogleLogin -> onClickGoogleLogin()
    }

    private fun onStarted() {
        viewModelScope.launch(ioDispatcher) {
            getUserInfo()?.let {
                // todo : 로그인
            } ?: run {
                withContext(mainImmediateDispatcher) {
                    showSocialLoginButtons()
                }
            }
        }
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

    private suspend fun getUserInfo(): Pair<String, String>? {
        return getStoredUserIdUseCase().zip(getStoredSignedPlatformUseCase()) { userId, signedPlatform ->
            if (userId != null && signedPlatform != null) Pair(userId, signedPlatform) else null
        }.first()
    }

    private fun showSocialLoginButtons() {
        _state.update {
            it.copy(isSignUpNeeded = true)
        }
    }
}