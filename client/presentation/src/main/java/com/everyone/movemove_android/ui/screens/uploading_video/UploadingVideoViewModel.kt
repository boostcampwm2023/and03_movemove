package com.everyone.movemove_android.ui.screens.uploading_video

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.domain.model.UploadCategory
import com.everyone.domain.model.VideoUploadUrl
import com.everyone.domain.model.base.DataState
import com.everyone.domain.usecase.GetVideoUploadUrlUseCase
import com.everyone.domain.usecase.GetVideoWithIdUseCase
import com.everyone.domain.usecase.PostVideoInfoUseCase
import com.everyone.domain.usecase.PutFileUseCase
import com.everyone.movemove_android.R
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.di.MainImmediateDispatcher
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Effect
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Effect.GoToWatchingVideoScreen
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Effect.LaunchVideoPicker
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Effect.PauseVideo
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Effect.SeekToStart
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
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnErrorDialogDismissed
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnExit
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnGetUri
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnLowerBoundDrag
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnLowerBoundDraggingFinished
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnLowerBoundDraggingStarted
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnPlayAndPauseTimeOut
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnSelectThumbnailDialogDismissed
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnStopped
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnTimelineWidthMeasured
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnTitleTyped
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnUpperBoundDrag
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnUpperBoundDraggingFinished
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnUpperBoundDraggingStarted
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Event.OnVideoPositionUpdated
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
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainImmediateDispatcher private val mainImmediateDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val getVideoUploadUrlUseCase: GetVideoUploadUrlUseCase,
    private val putFileUseCase: PutFileUseCase,
    private val postVideoInfoUseCase: PostVideoInfoUseCase,
    private val getVideoWithIdUseCase: GetVideoWithIdUseCase
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

        is OnTimelineWidthMeasured -> onTimelineWidthMeasured(event.timelineWidth)

        is OnLowerBoundDraggingStarted -> onLowerBoundDraggingStarted()

        is OnLowerBoundDraggingFinished -> onLowerBoundDraggingFinished()

        is OnLowerBoundDrag -> onLowerBoundDrag(
            offset = event.offset,
            boundWidthPx = event.boundWidthPx
        )

        is OnUpperBoundDraggingStarted -> onUpperBoundDraggingStarted()

        is OnUpperBoundDraggingFinished -> onUpperBoundDraggingFinished()

        is OnUpperBoundDrag -> onUpperBoundDrag(
            offset = event.offset,
            boundWidthPx = event.boundWidthPx
        )

        is OnVideoPositionUpdated -> onVideoPositionUpdated(event.videoPosition)

        is SetVideoStartTime -> setVideoStartTime()

        is SetVideoEndTime -> setVideoEndTime()

        is OnTitleTyped -> onTitleTyped(event.title)

        is OnDescriptionTyped -> onDescriptionTyped(event.description)

        is OnClickSelectThumbnail -> onClickSelectThumbnail()

        is OnBottomSheetHide -> onBottomSheetHide()

        is OnClickSelectCategory -> onClickSelectCategory()

        is OnCategorySelected -> onCategorySelected(event.category)

        is OnClickThumbnail -> onClickThumbnail(event.thumbnail)

        is OnSelectThumbnailDialogDismissed -> onSelectThumbnailDialogDismissed()

        is OnClickUpload -> onClickUpload()

        is OnErrorDialogDismissed -> onErrorDialogDismissed()

        is OnExit -> onExit()

        is OnStopped -> onStopped()
    }

    private fun onClickAddVideo() {
        viewModelScope.launch {
            _effect.emit(LaunchVideoPicker)
        }
    }

    private fun onGetUri(uri: Uri) {
        if (state.value.videoUri != uri) {
            _state.update {
                State(
                    videoUri = uri,
                    title = it.title,
                    description = it.description,
                    category = it.category
                )
            }

            checkUploadEnable()
        }
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
        if (state.value.videoDuration != duration) {
            _state.update {
                it.copy(
                    isVideoReady = true,
                    isPlaying = true,
                    videoDuration = duration,
                    videoEndTime = duration,
                    videoLengthUnit = duration / 1000L
                )
            }

            getThumbnailList()
        }
    }

    private fun onTimelineWidthMeasured(width: Int) {
        _state.update {
            it.copy(
                timelineWidth = width,
                timelineUnitWidth = width / 1000L
            )
        }
    }

    private fun onLowerBoundDraggingStarted() {
        _state.update {
            it.copy(isLowerBoundDragging = true)
        }
    }

    private fun onLowerBoundDraggingFinished() {
        _state.update {
            it.copy(isLowerBoundDragging = false)
        }
    }

    private fun onLowerBoundDrag(
        offset: Float,
        boundWidthPx: Float
    ) {
        val sum = state.value.lowerBoundPosition + offset
        val timelineWidth = state.value.timelineWidth
        val upperBoundPosition = state.value.upperBoundPosition

        if (sum >= 0 && sum < timelineWidth + upperBoundPosition - (boundWidthPx * 3)) {
            _state.update {
                it.copy(lowerBoundPosition = sum)
            }
        }
    }

    private fun onUpperBoundDraggingStarted() {
        _state.update {
            it.copy(isUpperBoundDragging = true)
        }
    }

    private fun onUpperBoundDraggingFinished() {
        _state.update {
            it.copy(isUpperBoundDragging = false)
        }
    }

    private fun onUpperBoundDrag(
        offset: Float,
        boundWidthPx: Float
    ) {
        val sum = state.value.upperBoundPosition + offset
        val timelineWidth = state.value.timelineWidth
        val lowerBoundPosition = state.value.lowerBoundPosition

        if (sum <= 0 && sum > lowerBoundPosition - timelineWidth + (boundWidthPx * 3)) {
            _state.update {
                it.copy(upperBoundPosition = sum)
            }
        }
    }

    private fun onVideoPositionUpdated(videoPosition: Long) {
        if (state.value.videoLengthUnit != 0L) {
            _state.update {
                it.copy(indicatorPosition = (it.timelineUnitWidth * (videoPosition / it.videoLengthUnit)).toInt())
            }

            if (videoPosition < state.value.videoStartTime || videoPosition > state.value.videoEndTime) {
                viewModelScope.launch {
                    _effect.emit(SeekToStart(state.value.videoStartTime))
                }
            }
        }
    }

    private fun getThumbnailList() {
        state.value.videoUri?.let { videoUri ->
            viewModelScope.launch(ioDispatcher) {
                val tempList = mutableListOf<ImageBitmap>()
                getVideoFilePath(
                    context = context,
                    videoUri = videoUri,
                    onSuccess = { videoFilePath ->
                        this@UploadingVideoViewModel.videoFilePath = videoFilePath
                        FrameExtractor(videoFilePath).getThumbnails(
                            duration = state.value.videoDuration,
                            onGetBitmap = {
                                tempList.add(it.asImageBitmap())
                            }
                        )

                        if (tempList.isNotEmpty()) {
                            _state.update {
                                it.copy(thumbnailList = tempList)
                            }
                        } else {
                            showErrorDialog(R.string.error_getting_thumbnail)
                        }
                    },
                    onFailure = {
                        showErrorDialog(R.string.error_get_video_file_path)
                    }
                )
            }
        }
    }

    private fun setVideoStartTime() {
        _state.update {
            it.copy(videoStartTime = it.videoLengthUnit * (it.lowerBoundPosition / it.timelineUnitWidth).toLong())
        }
    }

    private fun setVideoEndTime() {
        _state.update {
            it.copy(videoEndTime = it.videoLengthUnit * ((it.timelineWidth + it.upperBoundPosition) / it.timelineUnitWidth).toLong())
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
                it.copy(isVideoTrimming = true)
            }

            viewModelScope.launch(ioDispatcher) {
                state.value.stagedVideoFile?.let {

                } ?: run {
                    trimVideo()
                }
            }.invokeOnCompletion {
                _state.update {
                    it.copy(
                        isVideoTrimming = false,
                        isSelectThumbnailDialogShowing = true
                    )
                }
            }
        } else {
            showErrorDialog(R.string.error_no_video)
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
                    showErrorDialog(R.string.error_video_trimming)
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

    private fun onCategorySelected(category: UploadCategory) {
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
                    isUploadEnabled = videoUri != null &&
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
        _state.update {
            it.copy(isLoading = true)
        }

        getVideoUploadUrlUseCase(
            videoExtension = MP4,
            thumbnailExtension = WEBP
        ).onEach { result ->
            when (result) {
                is DataState.Success -> {
                    uploadVideo(result.data)
                }

                is DataState.Failure -> {
                    // todo : 예외 처리
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
                            videoResult == SUCCESS && thumbnailResult == SUCCESS
                        }
                    ).onEach { isSuccess ->
                        when (isSuccess) {
                            true -> {
                                sendVideoInfo(videoId)
                                removeTrimmedVideo()
                            }

                            false -> {
                                showErrorDialog(R.string.error_uploading_video)
                            }
                        }
                    }.collect()
                }
            }
        }
    }

    private suspend fun sendVideoInfo(videoId: String) {
        postVideoInfoUseCase(
            videoId = videoId,
            title = state.value.title,
            content = state.value.description,
            category = state.value.category!!.uploadString,
            videoExtension = MP4,
            thumbnailExtension = WEBP
        ).onEach { result ->
            when (result) {
                is DataState.Success -> {
                    withContext(mainImmediateDispatcher) {
                        getUploadedVideo(videoId)
                    }
                }

                is DataState.Failure -> {
                    showErrorDialog(R.string.error_uploading_video)
                }
            }
        }.collect()
    }

    private suspend fun getUploadedVideo(videoId: String) {
        getVideoWithIdUseCase(videoId).onEach { result ->
            withContext(mainImmediateDispatcher) {
                when (result) {
                    is DataState.Success -> {
                        _effect.emit(GoToWatchingVideoScreen(result.data))
                        _state.update {
                            it.copy(isLoading = false)
                        }
                    }

                    is DataState.Failure -> {

                    }
                }
            }
        }.collect()
    }


    private fun removeTrimmedVideo() {
        val stagedVideoFile = state.value.stagedVideoFile
        if (stagedVideoFile != null) {
            viewModelScope.launch(ioDispatcher) {
                runCatching {
                    stagedVideoFile.delete()
                }
            }
        }
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
        _state.update {
            it.copy(isErrorDialogShowing = false)
        }
    }

    private fun onExit() {
        _state.update { State() }
        removeTrimmedVideo()
    }

    private fun onStopped() {
        _state.update {
            it.copy(isPlaying = false)
        }

        viewModelScope.launch {
            _effect.emit(PauseVideo)
        }
    }

    companion object {
        private const val MP4 = "mp4"
        private const val WEBP = "webp"
        private const val SUCCESS = 200
    }
}