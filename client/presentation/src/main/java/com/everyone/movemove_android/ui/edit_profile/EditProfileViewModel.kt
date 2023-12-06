package com.everyone.movemove_android.ui.edit_profile

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.domain.model.base.DataState
import com.everyone.domain.usecase.GetProfileUseCase
import com.everyone.domain.usecase.GetStoredUUIDUseCase
import com.everyone.domain.usecase.PatchUserProfileUseCase
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Effect
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Effect.LaunchImageCropper
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Effect.LaunchImagePicker
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnClickEditProfile
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnClickSelectImage
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnGetCroppedImage
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnGetUri
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnIntroduceTyped
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnNicknameTyped
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getProfileUseCase: GetProfileUseCase,
    private val patchUserProfileUseCase: PatchUserProfileUseCase,
    private val getStoredUUIDUseCase: GetStoredUUIDUseCase
) : ViewModel(), EditProfileContract {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>(replay = 1)
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    init {
        getProfile()
    }

    override fun event(event: Event) = when (event) {
        is OnGetUri -> onGetUri(event.uri)

        is OnGetCroppedImage -> onGetCroppedImage(event.imageBitmap)

        is OnClickEditProfile -> onClickEditProfile()

        is OnClickSelectImage -> onClickSelectImage()

        is OnIntroduceTyped -> onIntroduceTyped(event.introduce)

        is OnNicknameTyped -> onNicknameTyped(event.nickname)
    }

    private fun getProfile() {
        loading(isLoading = true)

        viewModelScope.launch(ioDispatcher) {
            getStoredUUIDUseCase().first()?.let { uuid ->
                getProfileUseCase(uuid).onEach { result ->
                    when (result) {
                        is DataState.Success -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    nickname = result.data.nickname.orEmpty(),
                                    introduce = result.data.statusMessage.orEmpty(),
                                    profileImageUrl = result.data.profileImageUrl
                                )
                            }
                        }

                        is DataState.Failure -> {
                            loading(isLoading = false)
                        }
                    }
                }.collect()
            }
        }
    }

    private fun onNicknameTyped(nickname: String) {
        _state.update {
            it.copy(nickname = nickname)
        }

        checkSignUpEnabled()
    }

    private fun onIntroduceTyped(introduce: String) {
        _state.update {
            it.copy(introduce = introduce)
        }

        checkSignUpEnabled()
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

    private fun onGetCroppedImage(imageBitmap: ImageBitmap) {
        _state.update {
            it.copy(profileImage = imageBitmap)
        }

        checkSignUpEnabled()
    }

    private fun checkSignUpEnabled() {
        with(state.value) {
            _state.update {
                it.copy(
                    isEditProfileEnabled = nickname.isNotEmpty() &&
                            introduce.isNotEmpty() &&
                            profileImage != null
                )
            }
        }
    }

    private fun onClickEditProfile() {
        viewModelScope.launch(ioDispatcher) {
            getStoredUUIDUseCase().first()?.let { uuid ->
                patchUserProfileUseCase(
                    nickname = state.value.nickname,
                    statusMessage = state.value.introduce,
                    profileImageExtension = WEBP
                ).onEach { result ->
                    when (result) {
                        is DataState.Success -> {

                        }

                        is DataState.Failure -> {

                        }
                    }
                }.launchIn(viewModelScope + ioDispatcher)
            }
        }
    }

    private fun loading(isLoading: Boolean) {
        _state.update {
            it.copy(isLoading = isLoading)
        }
    }

    companion object {
        private const val WEBP = "webp"
    }
}