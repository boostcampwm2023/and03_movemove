package com.everyone.movemove_android.ui.sign_up

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Effect
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Effect.LaunchImageCropper
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Effect.LaunchImagePicker
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnClickSelectImage
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnClickSignUp
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnGetUri
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnIntroduceTyped
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnNicknameTyped
import com.everyone.movemove_android.ui.sign_up.SignUpContract.State
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpViewModel @Inject constructor() : ViewModel(), SignUpContract {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: SignUpContract.Event) = when (event) {
        is OnNicknameTyped -> onNicknameTyped(event.nickname)

        is OnIntroduceTyped -> onIntroduceTyped(event.introduce)

        is OnClickSignUp -> onClickSignUp()

        is OnClickSelectImage -> onClickSelectImage()

        is OnGetUri -> onGetUri(event.uri)
    }

    private fun onNicknameTyped(nickname: String) {
        _state.update {
            it.copy(nickname = nickname)
        }
    }

    private fun onIntroduceTyped(introduce: String) {
        _state.update {
            it.copy(introduce = introduce)
        }
    }

    private fun onClickSelectImage() {
        viewModelScope.launch {
            _effect.emit(LaunchImagePicker)
        }
    }

    private fun onGetUri(uri: Uri) {
        viewModelScope.launch {
            _effect.emit(LaunchImageCropper(uri))
        }
    }

    private fun onClickSignUp() {

    }
}