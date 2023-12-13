package com.everyone.movemove_android.ui.rating_video

import android.content.Intent
import androidx.annotation.StringRes
import com.everyone.movemove_android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.everyone.domain.model.Video
import com.everyone.domain.model.Videos
import com.everyone.domain.model.VideosList
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.LoadingDialog
import com.everyone.movemove_android.ui.MoveMoveErrorScreen
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.rating_video.RatingVideoContract.*
import com.everyone.movemove_android.ui.rating_video.RatingVideoContract.Event.*
import com.everyone.movemove_android.ui.theme.BorderInDark
import com.everyone.movemove_android.ui.theme.Typography
import com.everyone.movemove_android.ui.util.clickableWithoutRipple
import com.everyone.movemove_android.ui.watching_video.WatchingVideoActivity
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RatingVideoScreen(
    viewModel: RatingVideoViewModel,
    navigateToWatchingVideo: (intent: Intent) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val (state, event, effect) = use(viewModel)

    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when (effect) {
                is Effect.OnClickedBack -> onBack()
                is Effect.OnClickedVideo -> navigateToWatchingVideo(
                    WatchingVideoActivity.newIntent(
                        context = context,
                        videosList = effect.videosList,
                        page = effect.page
                    )
                )
            }
        }
    }

    Scaffold { paddingValues ->
        if (state.isLoading) {
            LoadingDialog()
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                MoveMoveTopBar(event = event)
                Spacer(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(color = BorderInDark)
                )
                if (state.isError) {
                    MoveMoveErrorScreen(onClick = { Refresh })
                } else {
                    MoveMoveVideoGrid(
                        state = state,
                        event = event
                    )
                }
            }
        }
    }
}

@Composable
fun MoveMoveTopBar(event: (Event) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(14.dp)
    ) {
        StyledText(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(R.string.rating_video_title),
            style = Typography.titleMedium,
        )

        Icon(
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterStart)
                .clickableWithoutRipple { event(OnClickedBack) },
            painter = painterResource(id = R.drawable.ic_left_arrow),
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
fun MoveMoveVideoGrid(
    state: State,
    event: (Event) -> Unit,
) {
    state.videosRated?.let { videosRated ->
        videosRated.videos?.let { videos ->
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.Center,
                contentPadding = PaddingValues(8.dp),
            ) {
                items(videos.size) {
                    MoveMoveGridImageItem(
                        modifier = Modifier.clickableWithoutRipple {
                            event(
                                OnClickedVideo(
                                    videosLit = VideosList(
                                        videos.map { videosRatedItem ->
                                            Videos(
                                                videosRatedItem.video,
                                                videosRatedItem.uploader
                                            )
                                        }),
                                    page = it
                                )
                            )
                        },
                        video = videos[it].video,
                    )
                }
            }
        }
    }
}

@Composable
fun MoveMoveGridImageItem(
    modifier: Modifier,
    video: Video?
) {
    video?.let {
        Card(
            modifier = modifier
                .aspectRatio(0.6f)
                .padding(8.dp),
            shape = RoundedCornerShape(size = 8.dp),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = video.thumbnailImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )

                video.title?.let { title ->
                    StyledText(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp),
                        text = title,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                }
            }
        }
    } ?: run {
        EmptyVideoGridItem(modifier = modifier, stringResId = R.string.invald_video_title)
    }
}

@Composable
fun EmptyVideoGridItem(
    modifier: Modifier,
    @StringRes stringResId: Int
) {
    Box(
        modifier = modifier
            .aspectRatio(0.6f)
            .padding(8.dp)
            .background(color = Color.Black),
    ) {
        StyledText(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(stringResId),
            style = MaterialTheme.typography.labelSmall
        )
    }
}