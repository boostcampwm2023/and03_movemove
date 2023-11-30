package com.everyone.movemove_android.ui.screens.uploading_video

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.domain.model.Category
import com.everyone.domain.model.VideoUploadUrl
import com.everyone.domain.model.base.DataState
import com.everyone.domain.usecase.PostExtensionInfoUseCase
import com.everyone.domain.usecase.PutFileUseCase
import com.everyone.movemove_android.di.DefaultDispatcher
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.di.MainImmediateDispatcher
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Effect
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Effect.LaunchVideoPicker
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnBottomSheetHide
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnCategorySelected
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnClickPlayAndPause
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnClickPlayer
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnClickSelectCategory
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnClickSelectThumbnail
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnClickSelectVideo
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnClickThumbnail
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnClickUpload
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnDescriptionTyped
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnGetUri
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnPlayAndPauseTimeOut
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnSelectThumbnailDialogDismissed
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnTitleTyped
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnVideoReady
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.SetVideoEndTime
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.SetVideoStartTime
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.State
import com.everyone.movemove_android.ui.util.getVideoFilePath
import com.everyone.movemove_android.ui.util.toWebpFile
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UploadingVideoViewModel @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainImmediateDispatcher private val mainImmediateDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val postExtensionInfoUseCase: PostExtensionInfoUseCase,
    private val putFileUseCase: PutFileUseCase,
) : ViewModel(), UploadingVideoContract {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    private lateinit var videoFilePath: String

    override fun event(event: Event) = when (event) {
        is OnClickSelectVideo -> onClickAddVideo()

        is OnGetUri -> onGetUri(event.uri)

        is OnClickPlayAndPause -> onClickPlayAndPause()

        is OnClickPlayer -> onClickPlayer()

        is OnPlayAndPauseTimeOut -> onPlayAndPauseTimeOut()

        is OnVideoReady -> onVideoReady(event.duration)

        is SetVideoStartTime -> setVideoStartTime(event.time)

        is SetVideoEndTime -> setVideoEndTime(event.time)

        is OnTitleTyped -> onTitleTyped(event.title)

        is OnDescriptionTyped -> onDescriptionTyped(event.description)

        is OnClickSelectThumbnail -> onClickSelectThumbnail()

        is OnBottomSheetHide -> onBottomSheetHide()

        is OnClickSelectCategory -> onClickSelectCategory()

        is OnCategorySelected -> onCategorySelected(event.category)

        is OnClickThumbnail -> onClickThumbnail(event.thumbnail)

        is OnSelectThumbnailDialogDismissed -> onSelectThumbnailDialogDismissed()

        is OnClickUpload -> onClickUpload()
    }

    private fun onClickAddVideo() {
        viewModelScope.launch {
            _effect.emit(LaunchVideoPicker)
        }
    }

    private fun onGetUri(uri: Uri) {
        _state.update {
            it.copy(videoInfo = it.videoInfo.copy(uri = uri))
        }

        checkUploadEnable()
    }

    private fun onClickPlayAndPause() {
        _state.update {
            it.copy(isPlaying = !it.isPlaying)
        }
    }

    private fun onClickPlayer() {
        _state.update {
            it.copy(isPlayAndPauseShowing = !it.isPlayAndPauseShowing)
        }
    }

    private fun onPlayAndPauseTimeOut() {
        _state.update {
            it.copy(isPlayAndPauseShowing = false)
        }
    }

    private fun onVideoReady(duration: Long) {
        _state.update {
            it.copy(
                isVideoReady = true,
                isPlaying = true, videoInfo = it.videoInfo.copy(duration = duration)
            )
        }

        getThumbnailList()
    }

    private fun getThumbnailList() {
        with(state.value.videoInfo) {
            viewModelScope.launch(ioDispatcher) {
                val tempList = mutableListOf<ImageBitmap>()
                val mediaMetadataRetriever = MediaMetadataRetriever().apply {
                    uri?.let {
                        getVideoFilePath(context, uri)?.let { path ->
                            videoFilePath = path
                            setDataSource(path)
                        }
                    }
                }

                withContext(defaultDispatcher) {
                    repeat(THUMBNAIL_COUNT) {
                        mediaMetadataRetriever.getFrameAtTime(
                            ((state.value.videoInfo.duration / THUMBNAIL_COUNT) * it + 1) * 1000L,
                            MediaMetadataRetriever.OPTION_CLOSEST
                        )?.let { bitmap ->
                            tempList.add(bitmap.asImageBitmap())
                        }
                    }
                }

                withContext(mainImmediateDispatcher) {
                    _state.update {
                        it.copy(thumbnailList = tempList)
                    }
                }

                mediaMetadataRetriever.release()
            }
        }
    }

    private fun setVideoStartTime(time: Long) {
        _state.update {
            it.copy(videoStartTime = time)
        }
    }

    private fun setVideoEndTime(time: Long) {
        _state.update {
            it.copy(videoEndTime = time)
        }
    }

    private fun onTitleTyped(title: String) {
        _state.update {
            it.copy(title = title)
        }

        checkUploadEnable()
    }

    private fun onDescriptionTyped(description: String) {
        _state.update {
            it.copy(description = description)
        }

        checkUploadEnable()
    }

    private fun onClickSelectThumbnail() {
        if (::videoFilePath.isInitialized) {
            _state.update {
                it.copy(isLoading = it.isLoading)
            }

            viewModelScope.launch(ioDispatcher) {
                trimVideo()
            }.invokeOnCompletion {
                _state.update {
                    it.copy(isSelectThumbnailDialogShowing = true)
                }
            }
        } else {
            // todo: Not Initialized
        }
    }

    private fun trimVideo() {
        with(state.value) {
            VideoTrimmer(
                originalPath = videoFilePath,
                newPath = context.filesDir.absolutePath,
                startMs = videoStartTime,
                endMs = videoEndTime
            ).trim(
                onSuccess = { videoFile ->
                    _state.update {
                        it.copy(stagedVideoFile = videoFile)
                    }
                },
                onFailure = {
                    // todo : 예외 처리
                }
            )
        }
    }

    private fun onBottomSheetHide() {
        _state.update {
            it.copy(isBottomSheetShowing = false)
        }
    }

    private fun onClickSelectCategory() {
        _state.update {
            it.copy(isBottomSheetShowing = true)
        }
    }

    private fun onCategorySelected(category: Category) {
        _state.update {
            it.copy(
                isBottomSheetShowing = false,
                category = category
            )
        }

        checkUploadEnable()
    }

    private fun checkUploadEnable() {
        with(state.value) {
            _state.update {
                it.copy(
                    isUploadEnabled = videoInfo.uri != null &&
                            title.isNotEmpty() &&
                            description.isNotEmpty() &&
                            category != null
                )
            }
        }
    }

    private fun onClickThumbnail(thumbnail: ImageBitmap) {
        _state.update {
            it.copy(selectedThumbnail = thumbnail)
        }
    }

    private fun onSelectThumbnailDialogDismissed() {
        _state.update {
            it.copy(isSelectThumbnailDialogShowing = false)
        }
    }

    private fun onClickUpload() {
        postExtensionInfoUseCase().onEach { result ->
            when (result) {
                is DataState.Success -> {
                    uploadVideo(result.data)
                }

                is DataState.Failure -> {
                    // todo :
                }
            }
        }.launchIn(viewModelScope + ioDispatcher)
    }

    private suspend fun uploadVideo(videoUploadUrl: VideoUploadUrl) {
        state.value.stagedVideoFile?.let { videoFile ->
            state.value.selectedThumbnail?.let { thumbnailBitmap ->
                val thumbnailFile = thumbnailBitmap.toWebpFile()
                val videoId = videoUploadUrl.videoId
                val videoUrl = videoUploadUrl.videoUrl
                val thumbnailUrl = videoUploadUrl.thumbnailUrl

                if (videoId != null && videoUrl != null && thumbnailUrl != null) {
                    putFileUseCase(
                        requestUrl = videoUrl,
                        file = videoFile
                    ).zip(
                        putFileUseCase(
                            requestUrl = thumbnailUrl,
                            file = thumbnailFile
                        ),
                        transform = { videoResult, thumbnailResult ->
                            videoResult == 200 && thumbnailResult == 200
                        }
                    ).onEach { isSuccess ->
                        when (isSuccess) {
                            true -> {

                            }

                            false -> {
                                // todo : 업로드 예외 처리
                            }
                        }
                    }.collect()
                }
            }
        }
    }

    companion object {
        const val THUMBNAIL_COUNT = 15
    }
}