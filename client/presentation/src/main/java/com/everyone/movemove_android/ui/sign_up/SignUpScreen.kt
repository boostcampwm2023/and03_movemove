package com.everyone.movemove_android.ui.sign_up

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.everyone.movemove_android.R.drawable.img_basic_profile
import com.everyone.movemove_android.R.drawable.ic_left_arrow
import com.everyone.movemove_android.R.drawable.ic_profile_add
import com.everyone.movemove_android.R.string.complete
import com.everyone.movemove_android.R.string.nickname
import com.everyone.movemove_android.R.string.one_line_introduce
import com.everyone.movemove_android.R.string.sign_up
import com.everyone.movemove_android.ui.MoveMoveTextField
import com.everyone.movemove_android.ui.RoundedCornerButton
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.theme.ProfileAddGray
import com.everyone.movemove_android.ui.theme.Typography

@Composable
fun SignUpScreen() {
    val context = LocalContext.current

    val launch = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        //imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(42.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {

                IconButton(
                    modifier = Modifier.align(alignment = Alignment.CenterStart),
                    onClick = {},
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        painter = painterResource(id = ic_left_arrow),
                        contentDescription = null
                    )
                }

                StyledText(
                    modifier = Modifier.align(alignment = Alignment.Center),
                    text = stringResource(id = sign_up),
                    style = Typography.labelLarge
                )

            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .width(96.dp)
                    .height(98.dp)
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = img_basic_profile),
                    contentDescription = null
                )

                IconButton(
                    modifier = Modifier
                        .align(alignment = Alignment.BottomEnd)
                        .size(30.dp)
                        .padding(
                            bottom = 6.dp,
                            end = 6.dp
                        ),
                    onClick = {},
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = ProfileAddGray,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        painter = painterResource(id = ic_profile_add),
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                StyledText(
                    text = stringResource(id = nickname),
                    style = Typography.labelLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                MoveMoveTextField(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                        .height(40.dp),
                    value = "",
                    onValueChange = { }
                )

                Spacer(modifier = Modifier.height(20.dp))

                StyledText(
                    text = stringResource(id = one_line_introduce),
                    style = Typography.labelLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                //TODO 글이 안써짐
                MoveMoveTextField(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                        .height(40.dp),
                    value = "",
                    onValueChange = { }
                )
            }

        }

        //TODO 버튼 상태 로직 추가해야함
        RoundedCornerButton(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .padding(horizontal = 20.dp)
                .align(Alignment.CenterHorizontally),
            buttonText = stringResource(id = complete),
            isEnabled = true,
        ) {

        }
    }


}