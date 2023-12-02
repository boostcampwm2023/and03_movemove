package com.everyone.movemove_android.ui.screens.watching_video

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.domain.model.Videos
import com.everyone.domain.model.base.DataState
import com.everyone.domain.usecase.GetVideosRandomUseCase
import com.everyone.domain.usecase.PutVideosRatingUseCase
import com.everyone.domain.usecase.PutVideosViewsUseCase
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.Category
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.Event.OnClickedCategory
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.Event.OnCategorySelected
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.Effect
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.Event
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.Event.ChangedVideoTab
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.Event.GetRandomVideos
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.Event.OnClickedVideoRating
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.Event.PutVideosViews
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.Event.SetVideos
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.State
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.VideoTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class WatchingVideoViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getVideosRandomUseCase: GetVideosRandomUseCase,
    private val putVideosRatingUseCase: PutVideosRatingUseCase,
    private val putVideosViewsUseCase: PutVideosViewsUseCase
) : ViewModel(), WatchingVideoContract {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: Event) {
        when (event) {
            is OnClickedCategory -> onClickedCategory()
            is OnCategorySelected -> onCategorySelected(selectedCategory = event.selectedCategory)
            is SetVideos -> setVideos(videos = event.videos)
            is GetRandomVideos -> getRandomVideos()
            is OnClickedVideoRating -> onClickedVideoRating(
                id = event.id,
                rating = event.rating,
                reason = event.reason
            )

            is ChangedVideoTab -> changedVideoTab(videoTab = event.videoTab)
            is PutVideosViews -> putVideosViews(videoId = event.videoId)
        }
    }

    private fun putVideosViews(videoId: String) {
        putVideosViewsUseCase(
            videoId = videoId,
            seed = state.value.seed
        ).onEach { result ->
            when (result) {
                is DataState.Success -> {}
                is DataState.Failure -> {}
            }
        }.launchIn(viewModelScope + ioDispatcher)
    }

    private fun getRandomVideos() {
        viewModelScope.launch {
            loading(isLoading = true)
            getVideosRandomUseCase(
                limit = LIMIT,
                category = state.value.selectedCategory.displayName,
                seed = state.value.seed
            ).onEach { result ->
                when (result) {
                    is DataState.Success -> {
                        result.data.videos?.let { videos ->
                            _state.update {
                                it.copy(
                                    videos = result.data.videos,
                                    seed = result.data.seed.toString(),
                                    isLoading = false
                                )
                            }

                        }
                    }

                    is DataState.Failure -> {
                        loading(isLoading = false)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun setVideos(videos: List<Videos>) {
        _state.update {
            it.copy(videos = videos)
        }
    }

    private fun onClickedVideoRating(
        id: String,
        rating: String,
        reason: String
    ) {
        viewModelScope.launch {
            putVideosRatingUseCase(
                id = id,
                rating = rating,
                reason = reason
            ).onEach { result ->
                when (result) {
                    is DataState.Success -> {}
                    is DataState.Failure -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun onClickedCategory() {
        _state.update {
            it.copy(isClickedCategory = !it.isClickedCategory)
        }
    }

    private fun onCategorySelected(selectedCategory: Category) {
        _state.update {
            it.copy(
                isClickedCategory = !it.isClickedCategory,
                selectedCategory = selectedCategory
            )
        }
    }

    private fun changedVideoTab(videoTab: VideoTab) {
        _state.update {
            it.copy(
                videoTab = videoTab
            )
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