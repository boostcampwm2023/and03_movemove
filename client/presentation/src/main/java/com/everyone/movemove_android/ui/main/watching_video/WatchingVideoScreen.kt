package com.everyone.movemove_android.ui.main.watching_video

import android.annotation.SuppressLint
import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.everyone.movemove_android.R
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.theme.grey300


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WatchingVideoScreen() {
    // TODO: 임시 url 수정 필요
    val videoURL =
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    val videoUri = Uri.parse(videoURL)

    val pagerState = rememberPagerState(pageCount = { 10 })

    VerticalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerState
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            VideoPlayer(uri = videoUri)
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                MoveMoveFooter()
                Divider()
            }
        }
    }
}

@SuppressLint("OpaqueUnitKey")
@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(uri: Uri) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                val defaultDataSourceFactory = DefaultDataSource.Factory(context)
                val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(
                    context,
                    defaultDataSourceFactory
                )
                val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri))

                setMediaSource(source)
                prepare()
            }
    }

    exoPlayer.playWhenReady = true
    exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
    exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

    DisposableEffect(
        AndroidView(factory = {
            PlayerView(context).apply {
                hideController()
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH

                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }
        })
    ) {
        onDispose { exoPlayer.release() }
    }
}

@Composable
fun MoveMoveFooter() {
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                alpha = 0.2f,
                brush = Brush.verticalGradient(
                    // TODO 임시 색상 논의 필요
                    colors = listOf(
                        Color(0xff282828),
                        Color(0xff191919),
                        Color(0xff000000),
                    )
                )
            )
            .padding(top = 30.dp, start = 16.dp, bottom = 18.dp),
    ) {
        MoveMoveFooterContents()
    }
}

@Composable
fun MoveMoveFooterContents() {
    // TODO 임시 값, data class 정의시 수정할 것
    Column(
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .height(24.dp)
                    .width(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_my),
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            StyledText(
                text = "손흥민",
                style = MaterialTheme.typography.bodyMedium
            )

        }
        Spacer(modifier = Modifier.height(12.dp))
        StyledText(
            text = "토트넘 만세",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        StyledText(
            text = "안녕하세요. 반갑습니다. 손흥민입니다. 토트넘만세",
            color = grey300,
            style = MaterialTheme.typography.bodySmall
        )
    }
}