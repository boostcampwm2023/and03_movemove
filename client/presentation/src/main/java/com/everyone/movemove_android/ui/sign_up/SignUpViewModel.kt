package com.everyone.movemove_android.ui.sign_up

import android.net.Uri
import android.os.Bundle
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.domain.model.UserInfo
import com.everyone.domain.model.base.DataState
import com.everyone.domain.usecase.GetProfileImageUploadUrlUseCase
import com.everyone.domain.usecase.PutFileUseCase
import com.everyone.domain.usecase.SetAccessTokenUseCase
import com.everyone.domain.usecase.SignUpUseCase
import com.everyone.domain.usecase.StoreRefreshTokenUseCase
import com.everyone.domain.usecase.StoreSignedPlatformUseCase
import com.everyone.domain.usecase.StoreUUIDUseCase
import com.everyone.movemove_android.R
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.di.MainImmediateDispatcher
import com.everyone.movemove_android.ui.sign_up.SignUpActivity.Companion.KEY_BUNDLE
import com.everyone.movemove_android.ui.sign_up.SignUpActivity.Companion.KEY_ID_TOKEN
import com.everyone.movemove_android.ui.sign_up.SignUpActivity.Companion.KEY_PLATFORM
import com.everyone.movemove_android.ui.sign_up.SignUpActivity.Companion.KEY_UUID
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Effect
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Effect.GoToHomeScreen
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Effect.LaunchImageCropper
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Effect.LaunchImagePicker
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnClickSelectImage
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnClickSignUp
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnErrorDialogDismissed
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnGetCroppedImage
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnGetUri
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnIntroduceTyped
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnNicknameTyped
import com.everyone.movemove_android.ui.sign_up.SignUpContract.State
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainImmediateDispatcher private val mainImmediateDispatcher: CoroutineDispatcher,
    private val getProfileImageUploadUrlUseCase: GetProfileImageUploadUrlUseCase,
    private val putFileUseCase: PutFileUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val setAccessTokenUseCase: SetAccessTokenUseCase,
    private val storeRefreshTokenUseCase: StoreRefreshTokenUseCase,
    private val storeUUIDUseCase: StoreUUIDUseCase,
    private val storeSignedPlatformUseCase: StoreSignedPlatformUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), SignUpContract {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>(replay = 1)
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: SignUpContract.Event) = when (event) {
        is OnNicknameTyped -> onNicknameTyped(event.nickname)

        is OnIntroduceTyped -> onIntroduceTyped(event.introduce)

        is OnClickSelectImage -> onClickSelectImage()

        is OnGetUri -> onGetUri(event.uri)

        is OnGetCroppedImage -> onGetCroppedImage(event.imageBitmap)

        is OnClickSignUp -> onClickSignUp()

        is OnErrorDialogDismissed -> onErrorDialogDismissed()
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
                    isSignUpEnabled = nickname.isNotEmpty() &&
                            introduce.isNotEmpty() &&
                            profileImage != null
                )
            }
        }
    }

    private fun onClickSignUp() {
        val uuid = getBundle()?.getString(KEY_UUID)
        val idToken = getBundle()?.getString(KEY_ID_TOKEN)

        if (uuid != null && idToken != null) {
            _state.update {
                it.copy(isLoading = true)
            }

            getProfileImageUploadUrlUseCase(
                profileImageExtension = WEBP,
                uuid = uuid,
                idToken = idToken,
            ).onEach { result ->
                when (result) {
                    is DataState.Success -> {
                        result.data.presignedUrl?.let { profileImageUploadUrl ->
                            uploadProfileImage(profileImageUploadUrl)
                        } ?: run {
                            showErrorDialog(R.string.error_upload_image_failure)
                        }
                    }

                    is DataState.Failure -> {
                        showErrorDialog(R.string.error_upload_image_failure)
                    }
                }
            }.launchIn(viewModelScope + ioDispatcher)
        }
    }

    private fun getBundle(): Bundle? = savedStateHandle.get<Bundle>(KEY_BUNDLE)

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
                        signUp()
                    } else {
                        showErrorDialog(R.string.error_upload_image_failure)
                    }
                }.collect()
            }
        }
    }

    private suspend fun signUp() {
        val idToken = getBundle()?.getString(KEY_ID_TOKEN)
        val uuid = getBundle()?.getString(KEY_UUID)
        val platform = getBundle()?.getString(KEY_PLATFORM)

        if (idToken != null && uuid != null && platform != null) {
            signUpUseCase(
                platform = platform,
                idToken = idToken,
                uuid = uuid,
                profileImageExtension = WEBP,
                nickname = state.value.nickname,
                statusMessage = state.value.introduce
            ).onEach { result ->
                when (result) {
                    is DataState.Success -> {
                        val isDataStored = setUserData(
                            userInfo = result.data,
                            signedPlatform = platform
                        )

                        if (isDataStored) {
                            withContext(mainImmediateDispatcher) {
                                _effect.emit(GoToHomeScreen)
                            }
                        }
                    }

                    is DataState.Failure -> {
                        showErrorDialog(R.string.error_sign_up)
                    }
                }
            }.collect()
        }
    }

    private suspend fun setUserData(
        userInfo: UserInfo,
        signedPlatform: String
    ): Boolean {
        val accessToken = userInfo.jsonWebToken?.accessToken
        val refreshToken = userInfo.jsonWebToken?.refreshToken
        val uuid = userInfo.profile?.uuid

        if (accessToken != null && refreshToken != null && uuid != null) {
            setAccessTokenUseCase(accessToken)

            return combine(
                storeRefreshTokenUseCase(refreshToken),
                storeUUIDUseCase(uuid),
                storeSignedPlatformUseCase(signedPlatform)
            ) { isRefreshTokenStored, isUUIDStored, isSignedPlatformStored ->
                isRefreshTokenStored && isUUIDStored && isSignedPlatformStored
            }.first()
        }

        return false
    }

    private fun showErrorDialog(textResourceId: Int) {
        _state.update {
            it.copy(
                isErrorDialogShowing = true,
                errorDialogTextResourceId = textResourceId
            )
        }
    }

    private fun onErrorDialogDismissed() {

    }

    companion object {
        private const val WEBP = "webp"
        private const val PUT_FILE_SUCCESS = 200
    }
}