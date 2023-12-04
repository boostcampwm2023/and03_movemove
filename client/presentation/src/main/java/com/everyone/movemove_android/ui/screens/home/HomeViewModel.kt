package com.everyone.movemove_android.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.domain.model.base.DataState
import com.everyone.domain.usecase.GetAdsUseCase
import com.everyone.domain.usecase.GetVideosTopRatedUseCase
import com.everyone.domain.usecase.GetVideosTrendUseCase
import com.everyone.movemove_android.ui.screens.home.HomeContract.*
import com.everyone.movemove_android.ui.watching_video.WatchingVideoContract.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAdsUseCase: GetAdsUseCase,
    private val getVideosTrendUseCase: GetVideosTrendUseCase,
    private val getVideosTopRatedUseCase: GetVideosTopRatedUseCase
) : ViewModel(), HomeContract {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: Event) {}

    init {
        // TODO 광고 API 문제가 있어 주석 처리했슴돠
        getAds()
        getVideosTrend()
        geVideosTopRated(category = Category.CHALLENGE)
        geVideosTopRated(category = Category.OLD_SCHOOL)
    }

    private fun getAds() {
        viewModelScope.launch {
            loading(isLoading = true)
            getAdsUseCase().onEach { result ->
                when (result) {
                    is DataState.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                advertisements = result.data
                            )
                        }
                    }

                    is DataState.Failure -> {
                        loading(isLoading = false)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getVideosTrend() {
        viewModelScope.launch {
            loading(isLoading = true)
            getVideosTrendUseCase(limit = LIMIT).onEach { result ->
                when (result) {
                    is DataState.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                videosTrend = result.data
                            )
                        }
                    }

                    is DataState.Failure -> {
                        loading(isLoading = false)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun geVideosTopRated(category: Category) {
        viewModelScope.launch {
            loading(isLoading = true)
            getVideosTopRatedUseCase(category = category.displayName).onEach { result ->
                when (result) {
                    is DataState.Success -> {
                        when (category) {
                            Category.CHALLENGE -> {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        videosTopRatedChallenge = result.data
                                    )
                                }
                            }

                            Category.OLD_SCHOOL -> {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        videosTopRatedOldSchool = result.data
                                    )
                                }
                            }

                            else -> {}
                        }
                    }

                    is DataState.Failure -> {
                        loading(isLoading = false)
                    }
                }
            }.launchIn(viewModelScope)
        }

    }

    private fun loading(isLoading: Boolean) {
        _state.update {
            it.copy(isLoading = isLoading)
        }
    }

    companion object {
        const val LIMIT = "10"
    }
}