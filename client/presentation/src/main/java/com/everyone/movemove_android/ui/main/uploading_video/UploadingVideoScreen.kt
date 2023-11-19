package com.everyone.movemove_android.ui.main.uploading_video

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.VideoOnly
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
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
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.everyone.movemove_android.R
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.LoadingDialog
import com.everyone.movemove_android.ui.MoveMoveTextField
import com.everyone.movemove_android.ui.RoundedCornerButton
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Effect.LaunchVideoPicker
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnClickPlayAndPause
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnClickPlayer
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnClickSelectVideo
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnClickUpload
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnDescriptionTyped
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnGetUri
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnPlayAndPauseTimeOut
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnTitleTyped
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnVideoReady
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.SetVideoEndTime
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.SetVideoStartTime
import com.everyone.movemove_android.ui.theme.BorderInDark
import com.everyone.movemove_android.ui.theme.EditorTimelineDim
import com.everyone.movemove_android.ui.theme.Point
import com.everyone.movemove_android.ui.theme.Typography
import com.everyone.movemove_android.ui.util.addFocusCleaner
import com.everyone.movemove_android.ui.util.clickableWithoutRipple
import com.everyone.movemove_android.ui.util.pxToDp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun UploadingVideoScreen(viewModel: UploadingVideoViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val (state, event, effect) = use(viewModel)
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
            }
        }
    }

    with(state) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .addFocusCleaner()
        ) {
            videoInfo.uri?.let { uri ->
                val videoDataSource by remember {
                    val dataSourceFactory = DefaultDataSource.Factory(context, DefaultDataSource.Factory(context))
                    mutableStateOf(ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri)))
                }
                val exoPlayer = remember {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaSource(videoDataSource)
                        videoScalingMode = C.VIDEO_SCALING_MODE_DEFAULT
                        repeatMode = Player.REPEAT_MODE_ONE
                        playWhenReady = true
                        prepare()

                        addListener(object : Player.Listener {
                            override fun onPlaybackStateChanged(playbackState: Int) {
                                if (duration > 0) {
                                    event(
                                        OnVideoReady(duration = duration)
                                    )
                                }
                            }
                        })
                    }
                }

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
                        if (isVideoReady) {
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
                                .align(Alignment.TopEnd)
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

                        if (isPlayAndPauseShowing) {
                            Icon(
                                modifier = Modifier
                                    .align(Center)
                                    .size(48.dp)
                                    .clickableWithoutRipple {
                                        if (isPlaying) exoPlayer.pause() else exoPlayer.play()
                                        event(OnClickPlayAndPause)
                                    },
                                painter = painterResource(id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                                tint = Color.White,
                                contentDescription = null
                            )
                        }

                        LaunchedEffect(isPlayAndPauseShowing) {
                            if (isPlayAndPauseShowing) {
                                delay(5000L)
                                event(OnPlayAndPauseTimeOut)
                            }
                        }
                    }

                    Divider(color = if (isSystemInDarkTheme()) BorderInDark else Color.White) // todo : 라이트 모드의 Border 색상이 정해지지 않음

                    var timelineWidthState by remember { mutableIntStateOf(0) }
                    var timelineUnitWidthState by remember { mutableLongStateOf(0L) }
                    var videoLengthUnitState by remember { mutableLongStateOf(0L) }
                    val boundWidthDp = 4.dp

                    timelineUnitWidthState = timelineWidthState.toLong() / 1000L
                    videoLengthUnitState = videoInfo.duration / 1000L
                    if (videoEndTime == 0L && videoInfo.duration > 0L) {
                        event(SetVideoEndTime(videoInfo.duration))
                    }

                    if (exoPlayer.currentPosition < videoStartTime || exoPlayer.currentPosition > videoEndTime) {
                        exoPlayer.seekTo(videoStartTime)
                    }

                    if (videoEndTime > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            var indicatorXPositionState by remember { mutableIntStateOf(0) }
                            var lowerBoundDraggingState by remember { mutableStateOf(false) }
                            var lowerBoundOffsetState by remember { mutableFloatStateOf(0f) }
                            var upperBoundDraggingState by remember { mutableStateOf(false) }
                            var upperBoundOffsetState by remember { mutableFloatStateOf(0f) }

                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = boundWidthDp)
                                    .onGloballyPositioned { timelineWidthState = it.size.width }
                            ) {
                                if (thumbnailList.isNotEmpty()) {
                                    thumbnailList.forEach { thumbnail ->
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
                                    .absoluteOffset(x = lowerBoundOffsetState.pxToDp())
                                    .width(boundWidthDp)
                                    .fillMaxHeight()
                                    .background(color = if (lowerBoundDraggingState) Point else Color.White)
                                    .pointerInput(Unit) {
                                        detectHorizontalDragGestures(
                                            onDragStart = { lowerBoundDraggingState = true },
                                            onDragEnd = {
                                                lowerBoundDraggingState = false
                                                event(SetVideoStartTime((videoLengthUnitState * (lowerBoundOffsetState / timelineUnitWidthState)).toLong()))
                                            },
                                            onDragCancel = { lowerBoundDraggingState = false },
                                            onHorizontalDrag = { _, dragAmount ->
                                                val sum = lowerBoundOffsetState + dragAmount
                                                if (sum >= 0 && sum < timelineWidthState + upperBoundOffsetState - (boundWidthDp.toPx() * 3)) {
                                                    lowerBoundOffsetState = sum
                                                }
                                            }
                                        )
                                    }
                            )

                            Box(
                                modifier = Modifier
                                    .width(lowerBoundOffsetState.pxToDp())
                                    .fillMaxHeight()
                                    .background(EditorTimelineDim)
                            )

                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .absoluteOffset(x = upperBoundOffsetState.pxToDp())
                                    .width(boundWidthDp)
                                    .fillMaxHeight()
                                    .background(color = if (upperBoundDraggingState) Point else Color.White)
                                    .pointerInput(Unit) {
                                        detectHorizontalDragGestures(
                                            onDragStart = { upperBoundDraggingState = true },
                                            onDragEnd = {
                                                upperBoundDraggingState = false
                                                event(SetVideoEndTime(videoLengthUnitState * ((timelineWidthState + upperBoundOffsetState) / timelineUnitWidthState).toLong()))
                                            },
                                            onDragCancel = { upperBoundDraggingState = false },
                                            onHorizontalDrag = { _, dragAmount ->
                                                val sum = upperBoundOffsetState + dragAmount
                                                if (sum <= 0 && sum > lowerBoundOffsetState - timelineWidthState + (boundWidthDp.toPx() * 3)) {
                                                    upperBoundOffsetState = sum
                                                }
                                            }
                                        )
                                    }
                            )

                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .fillMaxHeight()
                                    .background(EditorTimelineDim)
                                    .padding(end = abs(upperBoundOffsetState).pxToDp())
                            )

                            Box(
                                modifier = Modifier
                                    .absoluteOffset(x = indicatorXPositionState.pxToDp() + boundWidthDp)
                                    .width(1.dp)
                                    .fillMaxHeight()
                                    .background(color = Point)
                            )

                            LaunchedEffect(Unit) {
                                while (true) {
                                    indicatorXPositionState = (timelineUnitWidthState * (exoPlayer.currentPosition / videoLengthUnitState)).toInt()
                                    delay(100)
                                }
                            }
                        }
                    }
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
                        value = title,
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
                        value = description,
                        onValueChange = { event(OnDescriptionTyped(it)) }
                    )

                    Row(modifier = Modifier.padding(top = 12.dp)) {
                        StyledText(
                            text = stringResource(id = R.string.category),
                            style = Typography.labelLarge
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        StyledText(
                            modifier = Modifier.clickableWithoutRipple { },
                            text = stringResource(id = R.string.select),
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
                    buttonText = stringResource(id = R.string.complete),
                    isEnabled = isUploadEnabled
                ) {
                    event(OnClickUpload)
                }
            }
        }

        if (isLoading) {
            LoadingDialog()
        }
    }
}