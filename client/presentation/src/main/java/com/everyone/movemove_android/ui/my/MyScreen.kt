package com.everyone.movemove_android.ui.my

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.everyone.movemove_android.R
import com.everyone.movemove_android.R.drawable.ic_heart
import com.everyone.movemove_android.R.drawable.img_basic_profile
import com.everyone.movemove_android.R.string.basic_profile_name
import com.everyone.movemove_android.R.string.edit_profile
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.LoadingDialog
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.my.MyContract.Effect.CloseMyScreen
import com.everyone.movemove_android.ui.theme.Typography
import com.everyone.movemove_android.ui.util.clickableWithoutRipple
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MyScreen(
    viewModel: MyViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val (state, event, effect) = use(viewModel)

    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when (effect) {
                is CloseMyScreen -> {
                    if (context is ComponentActivity) {
                        context.finish()
                    }
                }
            }
        }
    }

    if (state.isLoading) {
        LoadingDialog()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(70.dp))

            if (state.profile.profileImageUrl == null) {
                Image(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .size(110.dp)
                        .clip(shape = CircleShape),
                    painter = painterResource(id = img_basic_profile),
                    contentDescription = null
                )
            } else {
                AsyncImage(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .size(110.dp)
                        .clip(shape = CircleShape),
                    model = state.profile.profileImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            StyledText(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                text = state.profile.nickname!!,
                style = Typography.labelLarge
            )


            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickableWithoutRipple { },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile_edit),
                    contentDescription = null,
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.width(12.dp))

                StyledText(
                    text = stringResource(edit_profile),
                    style = Typography.bodyLarge
                )
            }


            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickableWithoutRipple { },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_my_video),
                    contentDescription = null,
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.width(12.dp))

                StyledText(
                    text = stringResource(R.string.my_video),
                    style = Typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(start = 4.dp)
                    .clickableWithoutRipple { },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = ic_heart),
                    contentDescription = null,
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.width(12.dp))

                StyledText(
                    text = stringResource(R.string.my_scored_video),
                    style = Typography.bodyLarge
                )
            }
        }
    }
}
