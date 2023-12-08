package com.everyone.movemove_android.ui.screens.home

import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.everyone.domain.model.Ads
import com.everyone.domain.model.Advertisements
import com.everyone.domain.model.Videos
import com.everyone.domain.model.VideosList
import com.everyone.movemove_android.R
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.ErrorDialog
import com.everyone.movemove_android.ui.LoadingDialog
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.screens.home.HomeContract.Effect.NavigateToWatchingVideo
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract
import com.everyone.movemove_android.ui.theme.Point
import com.everyone.movemove_android.ui.util.clickableWithoutRipple
import com.everyone.movemove_android.ui.watching_video.WatchingVideoActivity
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToActivity: (intent: Intent) -> Unit
) {
    val context = LocalContext.current

    val (state, event, effect) = use(viewModel)

    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when (effect) {
                is NavigateToWatchingVideo -> navigateToActivity(
                    WatchingVideoActivity.newIntent(
                        context = context,
                        videosList = effect.videosList,
                        page = effect.page
                    )
                )
            }
        }
    }

    if (state.isErrorDialogShowing) {
        ErrorDialog(
            text = stringResource(id = state.errorDialogTextResourceId),
            onDismissRequest = { event(HomeContract.Event.OnErrorDialogDismissed) }
        )
    }

    if (state.isLoading) {
        LoadingDialog()
    } else {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            MultiServiceAds(advertisements = state.advertisements)

            Spacer(modifier = Modifier.height(44.dp))
            StyledText(
                modifier = Modifier.padding(start = 16.dp),
                text = stringResource(R.string.trending_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            MoveMoveVideos(
                event = event,
                videosList = state.videosTrend
            )

            Spacer(modifier = Modifier.height(36.dp))
            StyledColorText(
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.titleLarge,
                coloredText = stringResource(R.string.challenge_title),
                plainText = stringResource(R.string.top_10_title)
            )
            Spacer(modifier = Modifier.height(24.dp))
            MoveMoveVideos(
                event = event,
                videosList = state.videosTopRatedChallenge
            )

            Spacer(modifier = Modifier.height(36.dp))
            StyledColorText(
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.titleLarge,
                coloredText = stringResource(R.string.old_school_title),
                plainText = stringResource(R.string.top_10_title)
            )
            Spacer(modifier = Modifier.height(24.dp))
            MoveMoveVideos(
                event = event,
                videosList = state.videosTopRatedOldSchool
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MultiServiceAds(advertisements: Advertisements) {

    advertisements.advertisements?.let { advertisementsItem ->
        val autoPageChangeDelay = 3000L

        val pagerState = rememberPagerState(pageCount = { Int.MAX_VALUE })

        LaunchedEffect(Unit) {
            var initialPage = Int.MAX_VALUE / 2

            while (initialPage % advertisementsItem.size != 0) {
                initialPage++
            }
            pagerState.scrollToPage(initialPage)
        }

        LaunchedEffect(pagerState.currentPage) {
            launch {
                while (pagerState.currentPage + 1 < pagerState.pageCount) {
                    delay(autoPageChangeDelay)
                    withContext(NonCancellable) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }
        }

        Box {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                state = pagerState,
            ) { index ->
                MultiServiceAdsItem(
                    modifier = Modifier.clickableWithoutRipple {},
                    serviceAds = advertisementsItem,
                    index = index
                )
            }

            MultiServiceAdsPageNumber(
                modifier = Modifier.align(Alignment.BottomEnd),
                currentPage = pagerState.currentPage,
                serviceAds = advertisementsItem
            )
        }
    } ?: run {
        EmptyContentBox(stringResId = R.string.empty_ads_title)
    }
}

@Composable
fun MultiServiceAdsItem(
    modifier: Modifier,
    serviceAds: List<Ads>,
    index: Int
) {
    serviceAds.getOrNull(index % serviceAds.size)?.let { serviceAdsItem ->
        AsyncImage(
            modifier = modifier.fillMaxSize(),
            model = serviceAdsItem.url,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
        )
    } ?: run {
        EmptyContentBox(stringResId = R.string.empty_ads_title)
    }
}

@Composable
fun MultiServiceAdsPageNumber(
    modifier: Modifier,
    currentPage: Int,
    serviceAds: List<Ads>,
) {
    Box(
        modifier = modifier
            .padding(
                end = 8.dp,
                bottom = 8.dp
            )
            .clip(shape = RoundedCornerShape(16.dp))
            .width(50.dp)
            .background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center,
    ) {
        StyledText(
            text = "${(currentPage % serviceAds.size) + 1} / ${serviceAds.size}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
    }
}

@Composable
fun StyledColorText(
    modifier: Modifier,
    style: TextStyle,
    coloredText: String,
    plainText: String
) {
    Text(
        modifier = modifier,
        style = style,
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Point
                ),
            ) {
                append(coloredText)
            }
            append(plainText)
        },
    )
}

@Composable
fun MoveMoveVideos(
    event: (HomeContract.Event) -> Unit,
    videosList: VideosList,
) {
    videosList.videos?.let { videos ->
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp
            ),
        ) {
            items(videos.size) {
                MoveMoveVideo(
                    modifier = Modifier.clickableWithoutRipple {
                        event(
                            HomeContract.Event.OnClickedVideo(
                                videosList = videosList,
                                page = it
                            )
                        )
                    },
                    videos = videos[it],
                )
            }
        }
    } ?: run {
        EmptyContentBox(stringResId = R.string.empty_video_title)
    }
}

@Composable
fun MoveMoveVideo(
    modifier: Modifier,
    videos: Videos,
) {
    Card(
        modifier = modifier
            .width(150.dp)
            .height(250.dp)
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(size = 8.dp),
    ) {
        videos.video?.let { video ->
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = video.thumbnailImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        } ?: run {
            EmptyContentBox(stringResId = R.string.empty_video_title)
        }
    }
}

@Composable
fun EmptyContentBox(@StringRes stringResId: Int) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        StyledText(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(stringResId),
            style = MaterialTheme.typography.titleSmall
        )
    }
}