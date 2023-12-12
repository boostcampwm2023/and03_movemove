package com.everyone.movemove_android.ui.edit_profile

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.domain.model.base.DataState
import com.everyone.domain.usecase.GetPresignedUrlProfileUseCase
import com.everyone.domain.usecase.GetProfileUseCase
import com.everyone.domain.usecase.GetStoredUUIDUseCase
import com.everyone.domain.usecase.PatchUserProfileUseCase
import com.everyone.domain.usecase.PutFileUseCase
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Effect
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Effect.CloseEditProfileScreen
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Effect.LaunchImageCropper
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Effect.LaunchImagePicker
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnClickBackButton
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnClickEditProfile
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnClickSelectImage
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnGetCroppedImage
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnGetUri
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnIntroduceTyped
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnNicknameTyped
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.State
import com.everyone.movemove_android.ui.util.toWebpFile
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
    private val getStoredUUIDUseCase: GetStoredUUIDUseCase,
    private val getPresignedUrlProfileUseCase: GetPresignedUrlProfileUseCase,
    private val putFileUseCase: PutFileUseCase
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

        is OnClickBackButton -> onClickBackButton()
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

                            checkEditProfileEnabled()
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

        checkEditProfileEnabled()
    }

    private fun onIntroduceTyped(introduce: String) {
        _state.update {
            it.copy(introduce = introduce)
        }

        checkEditProfileEnabled()
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

        checkEditProfileEnabled()
    }

    private fun checkEditProfileEnabled() {
        with(state.value) {
            _state.update {
                it.copy(
                    isEditProfileEnabled = nickname.isNotEmpty() &&
                            introduce.isNotEmpty() &&
                            (profileImage != null || profileImageUrl != null)
                )
            }
        }
    }

    private fun onClickEditProfile() {
        if (state.value.profileImage == null) {
            postEditProfile()
        } else {
            getPresignedUrlProfileUseCase(profileExtension = WEBP).onEach { result ->
                when (result) {
                    is DataState.Success -> {
                        result.data.presignedUrl?.let {
                            uploadProfileImage(it)
                        } ?: run {
                            // todo 예외 처리
                        }
                    }

                    is DataState.Failure -> {
                        // todo 예외 처리
                    }
                }
            }.launchIn(viewModelScope + ioDispatcher)
        }
    }

    private suspend fun uploadProfileImage(profileImageUploadUrl: String) {
        state.value.profileImage?.let { profileImageBitmap ->
            if (state.value.nickname.isNotEmpty() &&
                state.value.introduce.isNotEmpty() &&
                state.value.profileImage != null
            ) {
                putFileUseCase(
                    requestUrl = profileImageUploadUrl,
                    file = profileImageBitmap.toWebpFile(isCompressNeeded = false)
                ).onEach { statusCode ->
                    if (statusCode == PUT_FILE_SUCCESS) {
                        postEditProfile()
                    } else {
                        // todo 예외 처리
                    }
                }.collect()
            }
        }
    }

    private fun postEditProfile() {
        patchUserProfileUseCase(
            nickname = state.value.nickname,
            statusMessage = state.value.introduce,
            profileImageExtension = WEBP
        ).onEach { result ->
            when (result) {
                is DataState.Success -> {
                    _effect.emit(CloseEditProfileScreen)
                }

                is DataState.Failure -> {
                    // todo 예외 처리
                }
            }
        }.launchIn(viewModelScope + ioDispatcher)
    }

    private fun loading(isLoading: Boolean) {
        _state.update {
            it.copy(isLoading = isLoading)
        }
    }

    private fun onClickBackButton() {
        viewModelScope.launch {
            _effect.emit(CloseEditProfileScreen)
        }
    }

    companion object {
        private const val WEBP = "webp"
        private const val PUT_FILE_SUCCESS = 200
    }
}