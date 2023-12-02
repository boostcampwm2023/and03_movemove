package com.everyone.movemove_android.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.everyone.domain.model.Ads
import com.everyone.domain.model.Videos
import com.everyone.domain.model.VideosTrend
import com.everyone.movemove_android.R
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.LoadingDialog
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.container.navigation.Destination
import com.everyone.movemove_android.ui.container.navigation.Navigator
import com.everyone.movemove_android.ui.theme.Point
import com.everyone.movemove_android.ui.util.clickableWithoutRipple
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigator: Navigator
) {

    val (state, event, effect) = use(viewModel)

    if (state.isLoading) {
        LoadingDialog()
    } else {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            MultiServiceAds(ads = state.ads)

            Spacer(modifier = Modifier.height(44.dp))
            StyledText(
                modifier = Modifier.padding(start = 16.dp),
                text = stringResource(R.string.trending_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            MoveMoveVideos(
                navigator = navigator,
                videosTrend = state.videosTrend
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
                navigator = navigator,
                videosTrend = state.videosTopRatedChallenge
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
                navigator = navigator,
                videosTrend = state.videosTopRatedOldSchool
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MultiServiceAds(ads: Ads?) {

    ads?.adImages?.let { adImages ->
        val autoPageChangeDelay = 3000L

        val pagerState = rememberPagerState(pageCount = { Int.MAX_VALUE })

        LaunchedEffect(Unit) {
            var initialPage = Int.MAX_VALUE / 2

            while (initialPage % adImages.size != 0) {
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
                    serviceAds = adImages,
                    index = index
                )
            }

            MultiServiceAdsPageNumber(
                modifier = Modifier.align(Alignment.BottomEnd),
                currentPage = pagerState.currentPage,
                serviceAds = adImages
            )
        }
    } ?: run {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            StyledText(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.empty_ads_title),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun MultiServiceAdsItem(
    modifier: Modifier,
    serviceAds: List<String>,
    index: Int
) {
    serviceAds.getOrNull(index % serviceAds.size)?.let { imageUrl ->
        AsyncImage(
            modifier = modifier.fillMaxSize(),
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun MultiServiceAdsPageNumber(
    modifier: Modifier,
    currentPage: Int,
    serviceAds: List<String>,
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
    navigator: Navigator,
    videosTrend: VideosTrend
) {
    videosTrend.videos?.let { videos ->
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
                        navigator.navigateToArgument(
                            key = "videosInfo",
                            value =Pair(videos, it)
                        )
                        navigator.navigateTo(Destination.WATCHING_VIDEO)
                    },
                    videos = videos[it],
                )
            }
        }
    } ?: run {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
        ) {
            StyledText(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.empty_video_title),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun MoveMoveVideo(
    modifier: Modifier,
    videos: Videos,
) {
    videos.video?.let { video ->
        Card(
            modifier = modifier
                .width(150.dp)
                .height(250.dp)
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(size = 8.dp),
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = video.thumbnailImage,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
            )
        }
    }
}