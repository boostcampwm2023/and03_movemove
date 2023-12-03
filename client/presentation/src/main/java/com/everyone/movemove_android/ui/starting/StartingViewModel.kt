package com.everyone.movemove_android.ui.starting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.domain.model.base.DataState
import com.everyone.domain.usecase.GetStoredSignedPlatformUseCase
import com.everyone.domain.usecase.GetStoredUUIDUseCase
import com.everyone.domain.usecase.LoginUseCase
import com.everyone.domain.usecase.SetAccessTokenUseCase
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.di.MainImmediateDispatcher
import com.everyone.movemove_android.ui.UiConstants.KAKAO
import com.everyone.movemove_android.ui.starting.StartingContract.Effect
import com.everyone.movemove_android.ui.starting.StartingContract.Effect.GoToHomeScreen
import com.everyone.movemove_android.ui.starting.StartingContract.Effect.GoToSignUpScreen
import com.everyone.movemove_android.ui.starting.StartingContract.Effect.LaunchGoogleLogin
import com.everyone.movemove_android.ui.starting.StartingContract.Effect.LaunchKakaoLogin
import com.everyone.movemove_android.ui.starting.StartingContract.Event
import com.everyone.movemove_android.ui.starting.StartingContract.Event.OnClickGoogleLogin
import com.everyone.movemove_android.ui.starting.StartingContract.Event.OnClickKakaoLogin
import com.everyone.movemove_android.ui.starting.StartingContract.Event.OnSocialLoginSuccess
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StartingViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainImmediateDispatcher private val mainImmediateDispatcher: CoroutineDispatcher,
    private val getStoredUUIDUseCase: GetStoredUUIDUseCase,
    private val getStoredSignedPlatformUseCase: GetStoredSignedPlatformUseCase,
    private val loginUseCase: LoginUseCase,
    private val setAccessTokenUseCase: SetAccessTokenUseCase
) : ViewModel(), StartingContract {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: Event) = when (event) {
        is OnStarted -> onStarted()

        is OnClickKakaoLogin -> onClickKakaoLogin()

        is OnClickGoogleLogin -> onClickGoogleLogin()

        is OnSocialLoginSuccess -> onSocialLoginSuccess(
            accessToken = event.accessToken,
            uuid = event.uuid,
            platform = event.platform
        )
    }

    private fun onStarted() {
        viewModelScope.launch(ioDispatcher) {
            getUserInfo()?.let { userInfo ->
                val signedPlatform = userInfo.second
                withContext(mainImmediateDispatcher) {
                    _effect.emit(
                        if (signedPlatform == KAKAO) {
                            LaunchKakaoLogin
                        } else {
                            LaunchGoogleLogin
                        }
                    )
                }
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

    private fun onSocialLoginSuccess(
        accessToken: String,
        uuid: String,
        platform: String
    ) {
        loginUseCase(
            accessToken = accessToken,
            uuid = uuid
        ).onEach { result ->
            withContext(mainImmediateDispatcher) {
                when (result) {
                    is DataState.Success -> {
                        result.data.jsonWebToken?.accessToken?.let { accessToken ->
                            setAccessTokenUseCase(accessToken)
                            _effect.emit(GoToHomeScreen)
                        } ?: run {
                            // todo : 액세스 토큰 없는 경우 예외 처리
                        }
                    }

                    is DataState.Failure -> {
                        GoToSignUpScreen(
                            accessToken = accessToken,
                            uuid = uuid,
                            platform = platform,
                        )
                    }
                }
            }
        }.launchIn(viewModelScope + ioDispatcher)
    }

    private suspend fun getUserInfo(): Pair<String, String>? {
        return getStoredUUIDUseCase().zip(getStoredSignedPlatformUseCase()) { uuid, signedPlatform ->
            if (uuid != null && signedPlatform != null) Pair(uuid, signedPlatform) else null
        }.first()
    }

    private fun showSocialLoginButtons() {
        _state.update {
            it.copy(isSignUpNeeded = true)
        }
    }
}