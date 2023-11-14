package com.everyone.movemove_android.ui.main.uploading_video

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.everyone.movemove_android.R
import com.everyone.movemove_android.ui.MoveMoveTextField
import com.everyone.movemove_android.ui.RoundedCornerButton
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.theme.Point
import com.everyone.movemove_android.ui.theme.Typography
import com.everyone.movemove_android.ui.util.addFocusCleaner
import com.everyone.movemove_android.ui.util.clickableWithoutRipple

@Composable
fun UploadingVideoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .addFocusCleaner()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(160.dp),
                    painter = painterResource(id = R.drawable.ic_add_video),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(18.dp))

                StyledText(
                    text = stringResource(id = R.string.add_video_description),
                    style = Typography.labelMedium
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(color = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                StyledText(
                    modifier = Modifier.padding(top = 30.dp),
                    text = stringResource(id = R.string.title),
                    style = Typography.labelLarge
                )

                MoveMoveTextField(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                        .height(40.dp),
                    value = "",
                    onValueChange = { }
                )

                StyledText(
                    modifier = Modifier.padding(top = 30.dp),
                    text = stringResource(id = R.string.video_description),
                    style = Typography.labelLarge
                )

                MoveMoveTextField(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                        .height(40.dp),
                    value = "",
                    onValueChange = { }
                )

                Row(modifier = Modifier.padding(top = 30.dp)) {
                    StyledText(
                        text = stringResource(id = R.string.category),
                        style = Typography.labelLarge
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    StyledText(
                        modifier = Modifier.clickableWithoutRipple { },
                        text = stringResource(id = R.string.select),
                        style = Typography.labelLarge,
                        color = Point
                    )
                }
            }

            RoundedCornerButton(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .padding(horizontal = 24.dp)
                    .align(Alignment.BottomCenter),
                buttonText = stringResource(id = R.string.complete),
                isEnabled = false
            ) {

            }
        }
    }
}