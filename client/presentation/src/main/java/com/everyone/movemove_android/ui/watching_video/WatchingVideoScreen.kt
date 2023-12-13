package com.everyone.movemove_android.ui.watching_video

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.annotation.StringRes
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.everyone.domain.model.Video
import com.everyone.domain.model.Videos
import com.everyone.movemove_android.R
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.LoadingDialog
import com.everyone.movemove_android.ui.MoveMoveErrorScreen
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.profile.ProfileActivity
import com.everyone.movemove_android.ui.watching_video.WatchingVideoContract.*
import com.everyone.movemove_android.ui.watching_video.WatchingVideoContract.Event.*
import com.everyone.movemove_android.ui.watching_video.category.CategoryScreen
import com.everyone.movemove_android.ui.theme.FooterBottomBackgroundInDark
import com.everyone.movemove_android.ui.theme.FooterTopBackgroundInDark
import com.everyone.movemove_android.ui.theme.Point
import com.everyone.movemove_android.ui.theme.Typography
import com.everyone.movemove_android.ui.util.clickableWithoutRipple
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun WatchingVideoScreen(
    viewModel: WatchingVideoViewModel,
    navigateToActivity: (intent: Intent) -> Unit,
) {
    val context = LocalContext.current

    val (state, event, effect) = use(viewModel)

    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when (effect) {
                is Effect.NavigateToProfile -> {
                    navigateToActivity(
                        ProfileActivity.newIntent(
                            context = context,
                            uuid = effect.uuid
                        )
                    )
                }
            }
        }
    }

    Scaffold { paddingValues ->
        if (state.isLoading) {
            LoadingDialog()
        } else {
            if (state.isError) {
                MoveMoveErrorScreen(onClick = { Refresh })
            } else {
                WatchingVideoContent(
                    modifier = Modifier.padding(paddingValues),
                    context = context,
                    state = state,
                    event = event,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WatchingVideoContent(
    modifier: Modifier,
    context: Context,
    state: State,
    event: (Event) -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    state.videos?.let { videosItem ->
        val videoUri = videosItem.map {
            it.video?.let { video ->
                Uri.parse(video.manifest)
            } ?: run {
                null
            }
        }
        val pagerState =
            rememberPagerState(initialPage = state.page, pageCount = { videoUri.size })

        val exoPlayerPair = remember {
            Triple(
                ExoPlayer.Builder(context).build(),
                ExoPlayer.Builder(context).build(),
                ExoPlayer.Builder(context).build()
            )
        }

        Box {
            VerticalPager(
                modifier = modifier.fillMaxSize(),
                state = pagerState
            ) { page ->

                val exoPlayer = when (page % 3) {
                    0 -> exoPlayerPair.first
                    1 -> exoPlayerPair.second
                    else -> exoPlayerPair.third
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    val snackBarState = remember { SnackbarHostState() }

                    videoUri[page]?.let { uri ->
                        VideoPlayer(
                            context = context,
                            exoPlayer = exoPlayer,
                            uri = uri,
                            isScroll = !pagerState.isScrollInProgress
                        )
                    } ?: run { EmptyVideoItem(stringResId = R.string.invald_video_title) }

                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        videosItem[page].video?.let { video ->
                            MoveMoveScoreboard(
                                video = video,
                                event = event,
                                snackBarState = snackBarState
                            )
                        }

                        MoveMoveFooter(
                            videos = videosItem[page],
                            event = event
                        )
                    }

                    MoveMoveSnackBar(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        snackBarState = snackBarState
                    )
                }

                if (!pagerState.isScrollInProgress) {
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
                } else {
                    exoPlayerPair.first.pause()
                    exoPlayerPair.second.pause()
                    exoPlayerPair.third.pause()
                }
            }

            videosItem[pagerState.settledPage].video?.let { video ->
                video.id?.let { id ->
                    event(PutVideosViews(id))
                }
            }

            if (state.isClickedCategory) {
                CategoryScreen()
            } else {
                if (state.videoTab == VideoTab.BOTTOM_TAB) {
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
    } ?: run {
        Box(modifier = Modifier.fillMaxSize()) {
            StyledText(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.empty_video_title),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@SuppressLint("OpaqueUnitKey")
@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(
    context: Context,
    exoPlayer: ExoPlayer,
    uri: Uri,
    isScroll: Boolean
) {
    DisposableEffect(uri) {
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        val source = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri))

        exoPlayer.apply {
            setMediaSource(source)
            prepare()
            playWhenReady = isScroll
            repeatMode = Player.REPEAT_MODE_ONE
        }

        onDispose {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
        }
    }

    AndroidView(factory = {
        PlayerView(context).apply {
            exoPlayer.addListener(object : Player.Listener {
                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    resizeMode = if (videoSize.pixelWidthHeightRatio.toInt() <= 1) {
                        AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                    } else {
                        AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                    }
                }
            })
            hideController()
            useController = false
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoveMoveScoreboard(
    video: Video,
    event: (Event) -> Unit,
    snackBarState: SnackbarHostState
) {
    // TODO : 유저 점수를 받아올 때 null이나 0 ~ 5(Int)를 받아 오게 되는데, Slider에 표시할 때는 0, 0.2, 0.4, 0.6, 0.8, 1f로 가공 해야함!
    val currentRating =
        video.userRating?.let { userRating -> (userRating * 0.2).toFloat() } ?: run { 0f }
    var sliderPosition by remember { mutableFloatStateOf(currentRating) }
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

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
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(shape = CircleShape)
                            .background(color = Color.White)
                    ) {
                        StyledText(
                            modifier = Modifier.align(Alignment.Center),
                            text = (sliderPosition * 5).toInt().toString(),
                            style = Typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Point
                        )
                    }
                },
                steps = 4,
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                },
                onValueChangeFinished = {
                    event(
                        OnClickedVideoRating(
                            id = video.id.toString(),
                            rating = (sliderPosition * 5).toInt().toString(),
                            reason = "테스트" // TODO 임시,,,
                        )
                    )

                    if (snackBarState.currentSnackbarData != null) snackBarState.currentSnackbarData?.dismiss()

                    coroutineScope.launch {
                        snackBarState.showSnackbar(
                            message = context.getString(R.string.rating_video_snackbar_message)
                                .format( (sliderPosition * 5).toInt()),
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    inactiveTrackColor = Color.White
                ),
            )
        }
    }
}

@Composable
fun MoveMoveSnackBar(
    modifier: Modifier,
    snackBarState: SnackbarHostState
) {
    SnackbarHost(
        modifier = modifier,
        hostState = snackBarState
    ) { snackbarData ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 10.dp,
                        horizontal = 16.dp
                    )
            ) {
                StyledText(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = snackbarData.visuals.message,
                    style = Typography.labelMedium,
                    color = Color.White
                )
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterEnd)
                        .clickableWithoutRipple { snackbarData.dismiss() },
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    tint = Point
                )
            }
        }
    }
}

@Composable
fun MoveMoveFooter(
    event: (Event) -> Unit,
    videos: Videos,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FooterTopBackgroundInDark,
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
        MoveMoveFooterContents(
            event = event,
            videos = videos
        )
    }
}

@Composable
fun MoveMoveFooterContents(
    event: (Event) -> Unit,
    videos: Videos
) {
    Column(
        verticalArrangement = Arrangement.Center
    ) {

        videos.uploader?.let { uploader ->
            Row(
                modifier = Modifier.clickableWithoutRipple {
                    uploader.uuid?.let { uuid ->
                        event(OnClickedProfile(uuid))
                    }
                },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    uploader.profileImageUrl?.let {
                        AsyncImage(
                            modifier = Modifier.clip(CircleShape),
                            model = uploader.profileImageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                        )
                    } ?: Image(
                        painter = painterResource(id = R.drawable.img_basic_profile),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                StyledText(
                    text = uploader.nickname.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        videos.video?.let { video ->
            Spacer(modifier = Modifier.height(12.dp))
            StyledText(
                text = video.title.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            StyledText(
                text = video.content.toString(),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun EmptyVideoItem(@StringRes stringResId: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
    ) {
        StyledText(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(stringResId),
            style = MaterialTheme.typography.labelLarge
        )
    }
}