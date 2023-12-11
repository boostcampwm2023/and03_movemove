package com.everyone.movemove_android.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.domain.model.VideosList
import com.everyone.domain.model.base.DataState
import com.everyone.domain.usecase.GetProfileUseCase
import com.everyone.domain.usecase.GetStoredUUIDUseCase
import com.everyone.domain.usecase.GetUsersVideosUploadedUseCase
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.di.MainImmediateDispatcher
import com.everyone.movemove_android.ui.profile.ProfileContract.*
import com.everyone.movemove_android.ui.profile.ProfileContract.Effect.*
import com.everyone.movemove_android.ui.profile.ProfileContract.Event.OnClickedMenu
import com.everyone.movemove_android.ui.profile.ProfileContract.Event.OnClickedVideo
import com.everyone.movemove_android.ui.profile.ProfileContract.Event.Refresh
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainImmediateDispatcher private val mainImmediateDispatcher: CoroutineDispatcher,
    private val getStoredUUIDUseCase: GetStoredUUIDUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getUsersVideosUploadedUseCase: GetUsersVideosUploadedUseCase
) : ViewModel(), ProfileContract {
    private val _state = MutableStateFlow(State())
    override val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: Event) = when (event) {
        is OnClickedMenu -> onClickedMenu()
        is OnClickedVideo -> onClickedVideo(event.videosList, event.page)
        is Refresh -> getSavedState()
        is Event.OnClickedBack -> onClickedBack()
    }

    init {
        getSavedState()
    }

    private fun getSavedState() {
        val uuid = savedStateHandle.get<String?>(ProfileActivity.EXTRA_KEY_UUID)
        uuid?.let {
            getProfile(uuid = it)
            getUsersVideosUploaded(uuid = it)
        } ?: run {
            getStoredUUID()
            _state.update {
                it.copy(
                    isUser = true
                )
            }
        }
    }

    private fun getStoredUUID() {
        viewModelScope.launch {
            loading(isLoading = true)
            getStoredUUIDUseCase().onEach { result ->
                result?.let { uuid ->
                    getProfile(uuid = uuid)
                    getUsersVideosUploaded(uuid = uuid)
                } ?: run {
                    _state.update {
                        it.copy(isError = true)
                    }
                }
            }.launchIn(viewModelScope + ioDispatcher)
            loading(isLoading = false)
        }
    }

    private fun getProfile(uuid: String) {
        viewModelScope.launch {
            loading(isLoading = true)
            getProfileUseCase(uuid = uuid).onEach { result ->
                when (result) {
                    is DataState.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                profile = result.data,
                                isError = false
                            )
                        }
                    }

                    is DataState.Failure -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isError = true
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope + ioDispatcher)
        }
    }

    private fun getUsersVideosUploaded(uuid: String) {
        viewModelScope.launch {
            loading(isLoading = true)
            getUsersVideosUploadedUseCase(
                limit = LIMIT,
                userId = uuid,
                lastId = ""
            ).onEach { result ->
                when (result) {
                    is DataState.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                videosUploaded = result.data,
                                isError = false
                            )
                        }
                    }

                    is DataState.Failure -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isError = true
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope + ioDispatcher)
        }
    }


    private fun onClickedMenu() {
        viewModelScope.launch {
            _effect.emit(NavigateToMy)
        }
    }

    private fun onClickedVideo(videosList: VideosList, page: Int) {
        viewModelScope.launch {
            _effect.emit(NavigateToWatchingVideo(videosList, page))
        }
    }

    private fun onClickedBack() {
        viewModelScope.launch {
            _effect.emit(NavigateUp)
        }
    }

    private suspend fun loading(isLoading: Boolean) {
        withContext(mainImmediateDispatcher) {
            _state.update {
                it.copy(isLoading = isLoading)
            }
        }
    }

    companion object {
        const val LIMIT = "50"
    }
}