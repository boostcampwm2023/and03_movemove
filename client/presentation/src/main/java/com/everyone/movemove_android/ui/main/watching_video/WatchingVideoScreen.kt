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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.everyone.movemove_android.R
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.theme.FooterBottomBackgroundInDark
import com.everyone.movemove_android.ui.theme.FooterMiddleBackgroundInDark
import com.everyone.movemove_android.ui.theme.FooterTopBackgroundInDark


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WatchingVideoScreen() {

    // TODO: 임시 url 수정 필요
    val videoURL = listOf(
        "https://d8mfnyqg1620.edge.naverncp.com/hls/fhnZKnJhDv726qSBreYITcVcI31NkgVYthgsQrtNurQ_/movemove/jdgdown_AVC_SD_1Pass_30fps_1.mp4/index.m3u8",
        "https://d8mfnyqg1620.edge.naverncp.com/hls/fhnZKnJhDv726qSBreYITcVcI31NkgVYthgsQrtNurQ_/movemove/TikTok%20Video%201080x1920%20px,_AVC_HD_1Pass_30fps.mp4,_AVC_SD_1Pass_30fps_1.mp4,.smil/master.m3u8",
        "https://d8mfnyqg1620.edge.naverncp.com/hls/fhnZKnJhDv726qSBreYITcVcI31NkgVYthgsQrtNurQ_/movemove/%EA%B8%B0%EC%84%B1%EC%9A%A9%EC%9D%B4%20%EB%B3%B4%EC%97%AC%EC%A3%BC%EC%97%88%EB%8D%98%20%EC%97%84%EC%B2%AD%EB%82%9C%20%EB%A1%B1%ED%82%A5%20%EB%8A%A5%EB%A0%A5%20%E3%84%B7%E3%84%B7%E3%84%B7,_AVC_HD_1Pass_30fps.mp4,_AVC_SD_1Pass_30fps_1.mp4,.smil/master.m3u8",
        "https://d8mfnyqg1620.edge.naverncp.com/hls/fhnZKnJhDv726qSBreYITcVcI31NkgVYthgsQrtNurQ_/movemove/%EC%9D%B4%EA%B1%B0%20%EB%9A%AB%EC%9D%84%20%EC%88%98%20%EC%9E%88%EC%9D%84%EA%B9%8C%EC%9A%94_,_AVC_HD_1Pass_30fps.mp4,_AVC_SD_1Pass_30fps_1.mp4,.smil/master.m3u8",
        "https://d8mfnyqg1620.edge.naverncp.com/hls/fhnZKnJhDv726qSBreYITcVcI31NkgVYthgsQrtNurQ_/movemove/%EC%BA%89%ED%85%8C...%20%EC%95%84%EB%8B%88%20%EC%A1%B0%EB%82%98%EB%8B%A8%EA%B3%BC%20%EC%B2%BC%EC%8B%9C%20%EA%B2%BD%EA%B8%B0%20%EC%A7%81%EA%B4%80,_AVC_HD_1Pass_30fps.mp4,_AVC_SD_1Pass_30fps_1.mp4,.smil/master.m3u8",
        "https://d8mfnyqg1620.edge.naverncp.com/hls/fhnZKnJhDv726qSBreYITcVcI31NkgVYthgsQrtNurQ_/movemove/lplbisang,_AVC_HD_1Pass_30fps.mp4,_AVC_SD_1Pass_30fps_1.mp4,.smil/master.m3u8"
    )
    val videoUri = videoURL.map { Uri.parse(it) }
    val pagerState = rememberPagerState(pageCount = { videoUri.size })
    val context = LocalContext.current
    val exoPlayerPair = remember {
        Triple(
            ExoPlayer.Builder(context).build(),
            ExoPlayer.Builder(context).build(),
            ExoPlayer.Builder(context).build()
        )
    }

    VerticalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerState
    ) { page ->

        val exoPlayer = when (page % 3) {
            0 -> exoPlayerPair.first
            1 -> exoPlayerPair.second
            else -> exoPlayerPair.third
        }

        Box(modifier = Modifier.fillMaxSize()) {
            VideoPlayer(
                exoPlayer = exoPlayer,
                uri = videoUri[page]
            )
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                MoveMoveFooter()
                Divider()
            }
        }

        when (pagerState.currentPage % 3) {
            0 -> {
                exoPlayerPair.first.play()
                exoPlayerPair.second.pause()
                exoPlayerPair.third.pause()
            }

            1 -> {
                exoPlayerPair.first.pause()
                exoPlayerPair.second.play()
                exoPlayerPair.third.pause()
            }

            2 -> {
                exoPlayerPair.first.pause()
                exoPlayerPair.second.pause()
                exoPlayerPair.third.play()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayerPair.first.release()
            exoPlayerPair.second.release()
            exoPlayerPair.third.release()
        }
    }
}

@SuppressLint("OpaqueUnitKey")
@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(exoPlayer: ExoPlayer, uri: Uri) {
    val context = LocalContext.current

    LaunchedEffect(uri) {
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        val source = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri))

        exoPlayer.apply {
            setMediaSource(source)
            prepare()
            playWhenReady = true
            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    AndroidView(factory = {
        PlayerView(context).apply {
            hideController()
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH

            player = exoPlayer
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
    })
}

@Composable
fun MoveMoveFooter() {
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                alpha = 0.2f,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FooterTopBackgroundInDark,
                        FooterMiddleBackgroundInDark,
                        FooterBottomBackgroundInDark,
                    )
                )
            )
            .padding(
                top = 30.dp,
                start = 16.dp,
                bottom = 18.dp
            ),
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
            style = MaterialTheme.typography.bodySmall
        )
    }
}