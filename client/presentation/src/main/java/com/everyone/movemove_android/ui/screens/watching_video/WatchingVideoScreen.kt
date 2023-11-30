package com.everyone.movemove_android.ui.screens.watching_video

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.Event.OnClickedCategory
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.everyone.domain.model.Videos
import com.everyone.movemove_android.R
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.screens.watching_video.category.CategoryScreen
import com.everyone.movemove_android.ui.theme.FooterBottomBackgroundInDark
import com.everyone.movemove_android.ui.theme.FooterMiddleBackgroundInDark
import com.everyone.movemove_android.ui.theme.FooterTopBackgroundInDark
import com.everyone.movemove_android.ui.theme.Typography
import com.everyone.movemove_android.ui.util.clickableWithoutRipple


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WatchingVideoScreen(
    videos: Videos?,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: WatchingVideoViewModel = hiltViewModel(),
) {

    val (state, event, effect) = use(viewModel)

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

    Box {
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
                    MoveMoveScoreboard()
                    MoveMoveFooter()
                    Divider()
                }
            }

            when (pagerState.settledPage % 3) {
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

        if (state.isClickedCategory) {
            CategoryScreen()
        } else {
            MoveMoveCategory(
                category = state.selectedCategory.displayName,
                modifier = Modifier
                    .padding(
                        start = 21.dp,
                        top = 21.dp
                    )
                    .align(Alignment.TopStart)
                    .clickableWithoutRipple { event(OnClickedCategory) },
            )
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                when (pagerState.settledPage % 3) {
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
            } else if (event == Lifecycle.Event.ON_STOP) {
                exoPlayerPair.first.pause()
                exoPlayerPair.second.pause()
                exoPlayerPair.third.pause()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
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
fun MoveMoveCategory(
    category: String,
    modifier: Modifier
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StyledText(
            text = category,
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
        )

        Icon(
            modifier = Modifier.padding(start = 5.dp),
            painter = painterResource(id = R.drawable.ic_expand_more),
            contentDescription = null
        )
    }

}

@Composable
fun MoveMoveScoreboard() {
    var sliderPosition by remember { mutableFloatStateOf(0.5f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 35.dp,
                end = 35.dp,
            )
            .clip(shape = RoundedCornerShape(12.dp))
            .background(color = Color.Black.copy(alpha = 0.3f))


    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 13.dp,
                    start = 23.dp,
                    end = 23.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            StyledText(
                text = stringResource(R.string.scoreboard_title),
                color = Color.White,
                style = Typography.bodyMedium
            )

            Slider(
                steps = 10,
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    inactiveTrackColor = Color.White
                )
            )
        }
    }
}

@Composable
fun MoveMoveFooter() {
    Box(
        modifier = Modifier
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
