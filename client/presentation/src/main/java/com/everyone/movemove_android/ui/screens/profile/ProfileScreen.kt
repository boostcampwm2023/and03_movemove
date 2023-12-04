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
import androidx.compose.material3.MaterialTheme
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
import com.everyone.domain.model.Profile
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.LoadingDialog
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.screens.profile.ProfileContract.Event
import com.everyone.movemove_android.ui.screens.profile.ProfileContract.Event.*
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
                MoveMoveProfile(profile = state.profile)
                Spacer(modifier = Modifier.height(24.dp))
                Spacer(
                    modifier = Modifier
                        .height(3.dp)
                        .fillMaxWidth()
                        .background(color = BorderInDark)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (state.videosUploaded.videos.isNullOrEmpty()) {
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
                items(state.videosUploaded.videos!!.chunked(3)) { rowItems ->
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
fun MoveMoveProfile(profile: Profile) {
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
                model = profile.profileImageUrl,
                contentDescription = null,
                modifier = Modifier.clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(12.dp))

        Column {
            StyledText(
                modifier = Modifier,
                text = profile.nickname ?: "",
                style = Typography.titleLarge,
                color = Color.White
            )

            StyledText(
                modifier = Modifier,
                text = profile.statusMessage ?: "",
                style = Typography.titleSmall,
            )
        }
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