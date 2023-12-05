package com.everyone.movemove_android.ui.screens.uploading_video

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.VideoOnly
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.everyone.domain.model.UploadCategory
import com.everyone.domain.model.UploadCategory.CHALLENGE
import com.everyone.domain.model.UploadCategory.K_POP
import com.everyone.domain.model.UploadCategory.NEW_SCHOOL
import com.everyone.domain.model.UploadCategory.OLD_SCHOOL
import com.everyone.movemove_android.R
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.ErrorDialog
import com.everyone.movemove_android.ui.LoadingDialog
import com.everyone.movemove_android.ui.LoadingDialogWithText
import com.everyone.movemove_android.ui.MoveMoveTextField
import com.everyone.movemove_android.ui.RoundedCornerButton
import com.everyone.movemove_android.ui.SelectThumbnailDialog
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract.Effect.Finish
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
import com.everyone.movemove_android.ui.theme.BorderInDark
import com.everyone.movemove_android.ui.theme.EditorTimelineDim
import com.everyone.movemove_android.ui.theme.Point
import com.everyone.movemove_android.ui.theme.Typography
import com.everyone.movemove_android.ui.util.LifecycleCallbackEvent
import com.everyone.movemove_android.ui.util.addFocusCleaner
import com.everyone.movemove_android.ui.util.clickableWithoutRipple
import com.everyone.movemove_android.ui.util.pxToDp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs

@OptIn(ExperimentalMaterialApi::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun UploadingVideoScreen(viewModel: UploadingVideoViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val (state, event, effect) = use(viewModel)

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            videoScalingMode = C.VIDEO_SCALING_MODE_DEFAULT
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
            prepare()

            addListener(object : Player.Listener {
                override fun onTracksChanged(tracks: Tracks) {
                    super.onTracksChanged(tracks)
                    event(OnVideoReady(duration))
                    play()
                }
            })
        }
    }

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                event(OnGetUri(it))
            } ?: run {

            }
        }
    )

    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when (effect) {
                is LaunchVideoPicker -> {
                    videoLauncher.launch(PickVisualMediaRequest(VideoOnly))
                }

                is SeekToStart -> {
                    exoPlayer.seekTo(effect.position)
                }

                is Finish -> {

                }

                is PauseVideo -> {
                    exoPlayer.pause()
                }
            }
        }
    }

    LifecycleCallbackEvent(
        lifecycleEvent = Lifecycle.Event.ON_STOP,
        onEvent = { event(OnStopped) }
    )

    LaunchedEffect(state.videoUri) {
        state.videoUri?.let { videoUri ->
            val dataSourceFactory = DefaultDataSource.Factory(context, DefaultDataSource.Factory(context))
            val dataSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri))
            exoPlayer.setMediaSource(dataSource)
        }
    }

    val sheetState = rememberModalBottomSheetState(
        initialValue = Hidden,
        skipHalfExpanded = true
    )

    LaunchedEffect(state.isBottomSheetShowing) {
        if (state.isBottomSheetShowing) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible) {
            event(OnBottomSheetHide)
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = { CategoryBottomSheet(event) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .addFocusCleaner()
        ) {
            VideoFrame(
                exoPlayer = exoPlayer,
                state = state,
                event = event
            )

            VideoInfoSection(
                state = state,
                event = event
            )
        }

        if (state.isLoading) {
            LoadingDialog()
        }

        if (state.isVideoTrimming) {
            LoadingDialogWithText(text = stringResource(id = R.string.loading_video_trimming))
        }

        if (state.isErrorDialogShowing) {
            ErrorDialog(
                text = stringResource(id = state.errorDialogTextResourceId),
                onDismissRequest = { event(OnErrorDialogDismissed) }
            )
        }

        if (state.isSelectThumbnailDialogShowing) {
            SelectThumbnailDialog(
                thumbnailList = state.thumbnailList,
                selectedThumbnail = state.selectedThumbnail,
                onClickThumbnail = { event(OnClickThumbnail(it)) },
                onClickComplete = { event(OnClickUpload) },
                onDismissRequest = { event(OnSelectThumbnailDialogDismissed) }
            )
        }

        BackHandler(enabled = state.videoUri != null) {

        }

        BackHandler(enabled = state.isBottomSheetShowing) {
            event(OnBottomSheetHide)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            event(OnExit)
        }
    }
}

@Composable
private fun CategoryBottomSheet(event: (Event) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .align(TopCenter)
                .clip(shape = RoundedCornerShape(24.dp))
                .width(40.dp)
                .height(4.dp)
                .background(color = MaterialTheme.colorScheme.onBackground)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UploadCategory.values().forEach { category ->
                StyledText(
                    modifier = Modifier.clickableWithoutRipple { event(OnCategorySelected(category)) },
                    text = category.getString(),
                    style = Typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        StyledText(
            modifier = Modifier
                .padding(top = 36.dp)
                .align(TopCenter),
            text = stringResource(id = R.string.select_category),
            style = Typography.titleMedium
        )
    }
}

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
private fun ColumnScope.VideoFrame(
    exoPlayer: ExoPlayer,
    state: State,
    event: (Event) -> Unit
) {
    state.videoUri?.let {
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickableWithoutRipple { event(OnClickPlayer) }
            ) {
                if (state.isVideoReady) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = {
                            PlayerView(context).apply {
                                useController = false
                                player = exoPlayer
                            }
                        }
                    )
                }

                StyledText(
                    modifier = Modifier
                        .padding(24.dp)
                        .align(TopEnd)
                        .clip(shape = RoundedCornerShape(24.dp))
                        .alpha(0.5f)
                        .border(
                            width = 1.dp,
                            shape = RoundedCornerShape(24.dp),
                            color = Color.White
                        )
                        .padding(
                            vertical = 4.dp,
                            horizontal = 12.dp
                        )
                        .clickableWithoutRipple { event(OnClickSelectVideo) },
                    text = stringResource(id = R.string.video_re_select),
                    style = Typography.labelMedium,
                    color = Color.White
                )

                if (state.isPlayAndPauseShowing) {
                    Icon(
                        modifier = Modifier
                            .align(Center)
                            .size(48.dp)
                            .clickableWithoutRipple {
                                if (state.isPlaying) exoPlayer.pause() else exoPlayer.play()
                                event(OnClickPlayAndPause)
                            },
                        painter = painterResource(id = if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                        tint = Color.White,
                        contentDescription = null
                    )
                }

                LaunchedEffect(state.isPlayAndPauseShowing) {
                    if (state.isPlayAndPauseShowing) {
                        delay(5000L)
                        event(OnPlayAndPauseTimeOut)
                    }
                }
            }

            Divider(color = if (isSystemInDarkTheme()) BorderInDark else Color.White) // todo : 라이트 모드의 Border 색상이 정해지지 않음

            EditorTimeline(
                exoPlayer = exoPlayer,
                state = state,
                event = event
            )
        }

        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }
    } ?: run {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickableWithoutRipple { event(OnClickSelectVideo) }
        ) {

            Column(
                modifier = Modifier.align(Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(160.dp),
                    painter = painterResource(id = R.drawable.ic_add_video),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(18.dp))

                StyledText(
                    text = stringResource(id = R.string.add_video_description),
                    style = Typography.labelMedium
                )
            }
        }
    }
}

private val boundWidthDp = 4.dp

@Composable
private fun EditorTimeline(
    exoPlayer: ExoPlayer,
    state: State,
    event: (Event) -> Unit
) {
    if (state.videoDuration > 0) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = boundWidthDp)
                    .onGloballyPositioned {
                        if (state.timelineWidth == 0) event(OnTimelineWidthMeasured(it.size.width))
                    }
            ) {
                if (state.thumbnailList.isNotEmpty()) {
                    state.thumbnailList.forEach { thumbnail ->
                        Image(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            bitmap = thumbnail,
                            contentDescription = null
                        )
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Center)
                                .size(24.dp),
                            color = Point
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .absoluteOffset(x = state.lowerBoundPosition.pxToDp())
                    .width(boundWidthDp)
                    .fillMaxHeight()
                    .background(color = if (state.isLowerBoundDragging) Point else Color.White)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = { event(OnLowerBoundDraggingStarted) },
                            onDragEnd = {
                                event(OnLowerBoundDraggingFinished)
                                event(SetVideoStartTime)
                            },
                            onDragCancel = { event(OnLowerBoundDraggingFinished) },
                            onHorizontalDrag = { _, dragAmount ->
                                event(
                                    OnLowerBoundDrag(
                                        offset = dragAmount,
                                        boundWidthPx = boundWidthDp.toPx()
                                    )
                                )
                            }
                        )
                    }
            )

            Box(
                modifier = Modifier
                    .width(state.lowerBoundPosition.pxToDp())
                    .fillMaxHeight()
                    .background(EditorTimelineDim)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .absoluteOffset(x = state.upperBoundPosition.pxToDp())
                    .width(boundWidthDp)
                    .fillMaxHeight()
                    .background(color = if (state.isUpperBoundDragging) Point else Color.White)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = { event(OnUpperBoundDraggingStarted) },
                            onDragEnd = {
                                event(OnUpperBoundDraggingFinished)
                                event(SetVideoEndTime)
                            },
                            onDragCancel = { event(OnUpperBoundDraggingFinished) },
                            onHorizontalDrag = { _, dragAmount ->
                                event(
                                    OnUpperBoundDrag(
                                        offset = dragAmount,
                                        boundWidthPx = boundWidthDp.toPx()
                                    )
                                )
                            }
                        )
                    }
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .background(EditorTimelineDim)
                    .padding(end = abs(state.upperBoundPosition).pxToDp())
            )

            Box(
                modifier = Modifier
                    .absoluteOffset(x = state.indicatorPosition.pxToDp() + boundWidthDp)
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(color = Point)
            )

            LaunchedEffect(state.isPlaying) {
                if (state.isPlaying) {
                    while (true) {
                        event(OnVideoPositionUpdated(exoPlayer.currentPosition))
                        delay(100)
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoInfoSection(
    state: State,
    event: (Event) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            StyledText(
                modifier = Modifier.padding(top = 30.dp),
                text = stringResource(id = R.string.title),
                style = Typography.labelLarge
            )

            MoveMoveTextField(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                value = state.title,
                onValueChange = { event(OnTitleTyped(it)) }
            )

            StyledText(
                modifier = Modifier.padding(top = 12.dp),
                text = stringResource(id = R.string.video_description),
                style = Typography.labelLarge
            )

            MoveMoveTextField(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                value = state.description,
                onValueChange = { event(OnDescriptionTyped(it)) }
            )

            Row(modifier = Modifier.padding(top = 12.dp)) {
                StyledText(
                    text = stringResource(id = R.string.category),
                    style = Typography.labelLarge
                )

                Spacer(modifier = Modifier.width(12.dp))

                StyledText(
                    modifier = Modifier.clickableWithoutRipple { event(OnClickSelectCategory) },
                    text = state.category?.getString() ?: stringResource(id = R.string.select),
                    style = Typography.labelLarge,
                    color = Point
                )
            }
        }

        RoundedCornerButton(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .padding(horizontal = 24.dp)
                .align(Alignment.BottomCenter),
            buttonText = stringResource(id = R.string.select_thumbnail),
            isEnabled = state.isUploadEnabled,
            onClick = { event(OnClickSelectThumbnail) }
        )
    }
}

@Composable
private fun UploadCategory.getString(): String {
    return stringResource(
        id = when (this) {
            CHALLENGE -> R.string.challenge
            OLD_SCHOOL -> R.string.old_school
            NEW_SCHOOL -> R.string.new_school
            K_POP -> R.string.k_pop
        }
    )
}