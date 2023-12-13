package com.everyone.movemove_android.ui.watching_video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.everyone.domain.model.Videos
import com.everyone.domain.model.VideosList
import com.everyone.domain.model.base.DataState
import com.everyone.domain.usecase.GetVideosRandomUseCase
import com.everyone.domain.usecase.PutVideosRatingUseCase
import com.everyone.domain.usecase.PutVideosViewsUseCase
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.di.MainImmediateDispatcher
import com.everyone.movemove_android.ui.watching_video.WatchingVideoContract.Category
import com.everyone.movemove_android.ui.watching_video.WatchingVideoContract.Effect
import com.everyone.movemove_android.ui.watching_video.WatchingVideoContract.Event
import com.everyone.movemove_android.ui.watching_video.WatchingVideoContract.Event.*
import com.everyone.movemove_android.ui.watching_video.WatchingVideoContract.State
import com.everyone.movemove_android.ui.watching_video.WatchingVideoContract.VideoTab
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
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WatchingVideoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainImmediateDispatcher private val mainImmediateDispatcher: CoroutineDispatcher,
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
            is GetRandomVideos -> getRandomVideos()
            is OnClickedVideoRating -> onClickedVideoRating(
                id = event.id,
                rating = event.rating,
                reason = event.reason
            )

            is PutVideosViews -> putVideosViews(videoId = event.videoId)
            is OnClickedProfile -> onClickedProfile(uuid = event.uuid)
            is Refresh -> getSavedState()
        }
    }

    init {
        getSavedState()
    }

    private fun getSavedState() {
        val videosList =
            savedStateHandle.get<VideosList>(WatchingVideoActivity.EXTRA_KEY_VIDEOS_LIST)
        val page = savedStateHandle.get<Int>(WatchingVideoActivity.EXTRA_KEY_VIDEOS_PAGE)

        videosList?.let {
            setVideos(videos = it.videos, page = page)
            changedVideoTab(videoTab = VideoTab.CATEGORY_TAB)
        } ?: run {
            getRandomVideos()
            changedVideoTab(videoTab = VideoTab.BOTTOM_TAB)
        }
    }

    private fun putVideosViews(videoId: String) {
        putVideosViewsUseCase(
            videoId = videoId,
            seed = state.value.seed.value
        ).onEach { result ->
            when (result) {
                is DataState.Success -> {}
                is DataState.Failure -> {}
            }
        }.launchIn(viewModelScope + ioDispatcher)
    }

    private fun getRandomVideos() {
        state.value.videosRandom =
            createVideoPagingSource(
                getVideosRandomUseCase = getVideosRandomUseCase,
                viewModel = this
            ).flow.cachedIn(
                viewModelScope + ioDispatcher
            )
    }

    private fun setVideos(
        videos: List<Videos>?,
        page: Int?
    ) {
        _state.update {
            it.copy(
                videos = videos,
                page = page ?: 0,
                seed = MutableStateFlow("")
            )
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
                    is DataState.Success -> {
                        _state.update {
                            it.copy(
                                videos = it.videos?.let { videosList ->
                                    videosList.map { videos ->
                                        if (videos.video?.id == id) {
                                            Videos(
                                                video = videos.video?.copy(userRating = rating.toInt()),
                                                uploader = videos.uploader
                                            )
                                        } else {
                                            videos
                                        }
                                    }
                                }
                            )
                        }
                    }

                    is DataState.Failure -> {}
                }
            }.launchIn(viewModelScope + ioDispatcher)
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

    private fun onClickedProfile(uuid: String) {
        viewModelScope.launch {
            _effect.emit(Effect.NavigateToProfile(uuid = uuid))
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