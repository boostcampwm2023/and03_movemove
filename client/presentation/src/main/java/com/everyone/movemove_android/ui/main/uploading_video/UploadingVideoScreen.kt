package com.everyone.movemove_android.ui.main.uploading_video

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.VideoOnly
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Effect.LoadVideo
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnClickSelectVideo
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoContract.Event.OnGetUri
import com.everyone.movemove_android.ui.theme.Point
import com.everyone.movemove_android.ui.theme.Typography
import com.everyone.movemove_android.ui.util.addFocusCleaner
import com.everyone.movemove_android.ui.util.clickableWithoutRipple
import kotlinx.coroutines.flow.collectLatest

@Composable
fun UploadingVideoScreen(viewModel: UploadingVideoViewModel = hiltViewModel()) {
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

                is LoadVideo -> {

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
            videoUri?.let {
                VideoEditor(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    uri = it,
                    onClickReSelectVideo = {
                        event(OnClickSelectVideo)
                    }
                )
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickableWithoutRipple { event(OnClickSelectVideo) }
                ) {

                    Column(
                        modifier = Modifier.align(Alignment.Center),
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
                    .height(400.dp)
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
                        value = "",
                        onValueChange = { }
                    )

                    StyledText(
                        modifier = Modifier.padding(top = 30.dp),
                        text = stringResource(id = R.string.video_description),
                        style = Typography.labelLarge
                    )

                    MoveMoveTextField(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                            .height(40.dp),
                        value = "",
                        onValueChange = { }
                    )

                    Row(modifier = Modifier.padding(top = 30.dp)) {
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
                    isEnabled = false
                ) {

                }
            }
        }

        if (isLoading) {
            LoadingDialog()
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun VideoEditor(
    modifier: Modifier = Modifier,
    uri: Uri,
    onClickReSelectVideo: () -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val dataSourceFactory = DefaultDataSource.Factory(context, DefaultDataSource.Factory(context))
            setMediaSource(ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri)))
            videoScalingMode = C.VIDEO_SCALING_MODE_DEFAULT
            repeatMode = Player.REPEAT_MODE_ONE
            prepare()
        }
    }

    var playingState by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PlayerView(context).apply {
                    useController = false
                    player = exoPlayer
                }
            }
        )

        StyledText(
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.TopEnd)
                .clip(shape = RoundedCornerShape(24.dp))
                .alpha(0.5f)
                .border(width = 1.dp, shape = RoundedCornerShape(24.dp), color = Color.White)
                .padding(vertical = 4.dp, horizontal = 12.dp)
                .clickableWithoutRipple { onClickReSelectVideo() },
            text = stringResource(id = R.string.video_re_select),
            style = Typography.labelMedium,
            color = Color.White
        )

        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .size(48.dp)
                .clickableWithoutRipple {
                    if (playingState) exoPlayer.pause() else exoPlayer.play()
                    playingState = !playingState
                },
            painter = painterResource(id = if (playingState) R.drawable.ic_pause else R.drawable.ic_play),
            contentDescription = null
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}