package com.everyone.movemove_android.ui.screens.profile

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.LoadingDialog
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.screens.profile.ProfileContract.Event
import com.everyone.movemove_android.ui.screens.profile.ProfileContract.Event.*
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoContract
import com.everyone.movemove_android.ui.theme.BorderInDark
import com.everyone.movemove_android.ui.theme.Typography
import com.everyone.movemove_android.ui.util.clickableWithoutRipple
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    navigateToMy: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {

    val (state, event, effect) = use(viewModel)

    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when (effect) {
                is ProfileContract.Effect.NavigateToMy -> navigateToMy()
            }
        }
    }

    // TODO 내가 업로드한 영상
    val preUploadingVideo = listOf(
        "https://blog.kakaocdn.net/dn/cffuLh/btsAuPqk00r/foBKGJJZn1FjWew2fBB7k1/img.png",
        "https://user-images.githubusercontent.com/69616347/212330945-b0deabd7-0a2c-4325-a9a8-e3ed5a8605f0.png",
        "https://user-images.githubusercontent.com/82919343/175826091-6e020fb2-34db-47aa-a4af-d02a2ebe7a8c.png",
        "https://user-images.githubusercontent.com/82919343/172618036-57a39779-3f5f-43c6-9b6d-47432d43c2eb.png",
        "https://camo.githubusercontent.com/467aa234a3ca939f6f06b6f919046467c2894354f9c1f16e1e3c89298f64db33/68747470733a2f2f626c6f672e6b616b616f63646e2e6e65742f646e2f6c376363332f6274724978537a6b334a762f566c4b7545586b63434a5332316c6a3358504154746b2f696d672e706e67",
        "https://user-images.githubusercontent.com/82919343/244449569-0c252b64-d1b9-4ce7-b7d5-010c871276a3.png",
        "https://blog.kakaocdn.net/dn/cffuLh/btsAuPqk00r/foBKGJJZn1FjWew2fBB7k1/img.png",
        "https://user-images.githubusercontent.com/69616347/212330945-b0deabd7-0a2c-4325-a9a8-e3ed5a8605f0.png",
        "https://user-images.githubusercontent.com/82919343/175826091-6e020fb2-34db-47aa-a4af-d02a2ebe7a8c.png",
        "https://user-images.githubusercontent.com/82919343/172618036-57a39779-3f5f-43c6-9b6d-47432d43c2eb.png",
        "https://camo.githubusercontent.com/467aa234a3ca939f6f06b6f919046467c2894354f9c1f16e1e3c89298f64db33/68747470733a2f2f626c6f672e6b616b616f63646e2e6e65742f646e2f6c376363332f6274724978537a6b334a762f566c4b7545586b63434a5332316c6a3358504154746b2f696d672e706e67",
        "https://user-images.githubusercontent.com/82919343/244449569-0c252b64-d1b9-4ce7-b7d5-010c871276a3.png",
        "https://blog.kakaocdn.net/dn/cffuLh/btsAuPqk00r/foBKGJJZn1FjWew2fBB7k1/img.png",
        "https://user-images.githubusercontent.com/69616347/212330945-b0deabd7-0a2c-4325-a9a8-e3ed5a8605f0.png",
        "https://user-images.githubusercontent.com/82919343/175826091-6e020fb2-34db-47aa-a4af-d02a2ebe7a8c.png",
        "https://user-images.githubusercontent.com/82919343/172618036-57a39779-3f5f-43c6-9b6d-47432d43c2eb.png",
        "https://camo.githubusercontent.com/467aa234a3ca939f6f06b6f919046467c2894354f9c1f16e1e3c89298f64db33/68747470733a2f2f626c6f672e6b616b616f63646e2e6e65742f646e2f6c376363332f6274724978537a6b334a762f566c4b7545586b63434a5332316c6a3358504154746b2f696d672e706e67",
        "https://user-images.githubusercontent.com/82919343/244449569-0c252b64-d1b9-4ce7-b7d5-010c871276a3.png",
        "https://blog.kakaocdn.net/dn/cffuLh/btsAuPqk00r/foBKGJJZn1FjWew2fBB7k1/img.png",
        "https://user-images.githubusercontent.com/69616347/212330945-b0deabd7-0a2c-4325-a9a8-e3ed5a8605f0.png",
        "https://user-images.githubusercontent.com/82919343/175826091-6e020fb2-34db-47aa-a4af-d02a2ebe7a8c.png",
        "https://user-images.githubusercontent.com/82919343/172618036-57a39779-3f5f-43c6-9b6d-47432d43c2eb.png",
        "https://camo.githubusercontent.com/467aa234a3ca939f6f06b6f919046467c2894354f9c1f16e1e3c89298f64db33/68747470733a2f2f626c6f672e6b616b616f63646e2e6e65742f646e2f6c376363332f6274724978537a6b334a762f566c4b7545586b63434a5332316c6a3358504154746b2f696d672e706e67",
    )

    Column(Modifier.fillMaxSize()) {
        MoveMoveTopBar(event = event)

        Spacer(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(color = BorderInDark)
        )

        LazyColumn {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                MoveMoveProfile()
                Spacer(modifier = Modifier.height(24.dp))
                Spacer(
                    modifier = Modifier
                        .height(3.dp)
                        .fillMaxWidth()
                        .background(color = BorderInDark)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(preUploadingVideo.chunked(3)) { rowItems ->
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
                                    model = rowItems[i],
                                    onLoading = { event(LoadingStart) },
                                    onSuccess = { event(LoadingEnd) },
                                    onError = { event(LoadingEnd) }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(0.5.dp))
            }
        }

        if (state.isLoading) {
            LoadingDialog()
        }
    }
}

@Composable
fun MoveMoveTopBar(event: (Event) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
    ) {
        StyledText(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(R.string.top_bar_profile_title),
            style = Typography.titleMedium,
        )

        Icon(
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterEnd)
                .clickableWithoutRipple { event(OnClickedMenu) },
            painter = painterResource(id = R.drawable.ic_menu),
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
fun MoveMoveProfile() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(70.dp),
            shape = CircleShape,
        ) {
            AsyncImage(
                model = "https://blog.kakaocdn.net/dn/WJ908/btqB5hqMc9u/gexmVhbL0GdptqAvecZBs0/img.jpg",
                contentDescription = null,
                modifier = Modifier.clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(12.dp))

        Column {
            StyledText(
                modifier = Modifier,
                text = "손흥민",
                style = Typography.titleLarge,
                color = Color.White
            )

            StyledText(
                modifier = Modifier,
                text = "월드클래스 맞습니다.",
                style = Typography.titleSmall,
            )
        }
    }
}

@Composable
fun MoveMoveGridImageItem(
    modifier: Modifier = Modifier,
    model: String,
    onLoading: () -> Unit,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    AsyncImage(
        modifier = modifier
            .aspectRatio(1f)
            .padding(1.dp),
        model = model,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        onLoading = { onLoading() },
        onSuccess = { onSuccess() },
        onError = { onError() }
    )
}