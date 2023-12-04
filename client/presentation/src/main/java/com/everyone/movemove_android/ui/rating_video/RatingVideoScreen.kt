package com.everyone.movemove_android.ui.rating_video

import com.everyone.movemove_android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.everyone.domain.model.Videos
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.LoadingDialog
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.rating_video.RatingVideoContract.*
import com.everyone.movemove_android.ui.rating_video.RatingVideoContract.Event.*
import com.everyone.movemove_android.ui.theme.BorderInDark
import com.everyone.movemove_android.ui.theme.Typography
import com.everyone.movemove_android.ui.util.clickableWithoutRipple
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RatingVideoScreen(
    viewModel: RatingVideoViewModel,
    navigateToWatchingVideo: (List<Videos>, Int) -> Unit,
    onBack: () -> Unit
) {

    val (state, event, effect) = use(viewModel)

    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when (effect) {
                is Effect.OnClickedBack -> onBack()
                is Effect.OnClickedVideo -> navigateToWatchingVideo(
                    effect.videosList,
                    effect.page
                )
            }
        }
    }

    Scaffold { paddingValues ->
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

            LazyColumn {
                if (state.videosUploaded.video.isNullOrEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                        ) {
                            StyledText(
                                modifier = Modifier.align(Alignment.Center),
                                text = stringResource(R.string.empty_video_title),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                } else {
                    items(state.videosUploaded.video!!.chunked(3)) { rowItems ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 8.dp,
                                    end = 8.dp,
                                )
                        ) {
                            for (i in 0 until 3) {
                                Box(modifier = Modifier.weight(1f)) {
                                    if (i < rowItems.size) {
                                        MoveMoveGridImageItem(
                                            model = rowItems[i].thumbnailImageUrl!!,
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(0.5.dp))
                    }
                }
            }

            if (state.isLoading) {
                LoadingDialog()
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
fun MoveMoveGridImageItem(
    modifier: Modifier = Modifier,
    model: String
) {
    AsyncImage(
        modifier = modifier
            .aspectRatio(1f)
            .padding(1.dp),
        model = model,
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}