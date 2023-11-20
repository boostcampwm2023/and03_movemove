package com.everyone.movemove_android.ui.main.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.util.clickableWithoutRipple
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        MultiServiceAds()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MultiServiceAds() {

    // TODO 서비스 광고 썸네일 더미
    val serviceAds = listOf(
        "https://blog.kakaocdn.net/dn/cffuLh/btsAuPqk00r/foBKGJJZn1FjWew2fBB7k1/img.png",
        "https://user-images.githubusercontent.com/69616347/212330945-b0deabd7-0a2c-4325-a9a8-e3ed5a8605f0.png",
        "https://user-images.githubusercontent.com/82919343/175826091-6e020fb2-34db-47aa-a4af-d02a2ebe7a8c.png",
        "https://user-images.githubusercontent.com/82919343/172618036-57a39779-3f5f-43c6-9b6d-47432d43c2eb.png",
        "https://camo.githubusercontent.com/467aa234a3ca939f6f06b6f919046467c2894354f9c1f16e1e3c89298f64db33/68747470733a2f2f626c6f672e6b616b616f63646e2e6e65742f646e2f6c376363332f6274724978537a6b334a762f566c4b7545586b63434a5332316c6a3358504154746b2f696d672e706e67",
        "https://user-images.githubusercontent.com/82919343/244449569-0c252b64-d1b9-4ce7-b7d5-010c871276a3.png",
    )

    val autoPageChangeDelay = 3000L

    val pagerState = rememberPagerState(pageCount = { Int.MAX_VALUE })

    LaunchedEffect(Unit) {
        var initialPage = Int.MAX_VALUE / 2

        while (initialPage % serviceAds.size != 0) {
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
                serviceAds = serviceAds,
                index = index
            )
        }

        Box(
            modifier = Modifier
                .padding(
                    end = 8.dp,
                    bottom = 8.dp
                )
                .clip(shape = RoundedCornerShape(16.dp))
                .width(50.dp)
                .background(Color.Black.copy(alpha = 0.3f))
                .align(Alignment.BottomEnd),
            contentAlignment = Alignment.Center,
        ) {
            StyledText(
                text = "${(pagerState.currentPage % serviceAds.size) + 1} / ${serviceAds.size}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
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
            model = imageUrl,
            modifier = modifier.fillMaxSize(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
    }
}