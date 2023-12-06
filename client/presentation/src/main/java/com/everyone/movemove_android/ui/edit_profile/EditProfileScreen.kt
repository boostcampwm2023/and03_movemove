package com.everyone.movemove_android.ui.edit_profile

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.everyone.movemove_android.R
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.MoveMoveTextField
import com.everyone.movemove_android.ui.RoundedCornerButton
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Effect.LaunchImageCropper
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Effect.LaunchImagePicker
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnClickSelectImage
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnGetCroppedImage
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnGetUri
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnIntroduceTyped
import com.everyone.movemove_android.ui.edit_profile.EditProfileContract.Event.OnNicknameTyped
import com.everyone.movemove_android.ui.image_cropper.ImageCropperActivity
import com.everyone.movemove_android.ui.image_cropper.ImageCropperActivity.Companion.KEY_CROPPED_IMAGE_URI
import com.everyone.movemove_android.ui.theme.ProfileAddGray
import com.everyone.movemove_android.ui.theme.Typography
import com.everyone.movemove_android.ui.util.clickableWithoutRipple
import com.everyone.movemove_android.ui.util.toImageBitmap
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditProfileScreen(viewModel: EditProfileViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val (state, event, effect) = use(viewModel)
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                event(OnGetUri(it))
            }
        }
    )
    val imageCropperLauncher = rememberLauncherForActivityResult(
        contract = StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.getStringExtra(KEY_CROPPED_IMAGE_URI)?.toUri()?.let { uri ->
                    event(OnGetCroppedImage(uri.toImageBitmap(context.contentResolver)))
                }
            }
        }
    )

    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when (effect) {
                is LaunchImagePicker -> {
                    imageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }

                is LaunchImageCropper -> {
                    imageCropperLauncher.launch(
                        ImageCropperActivity.newIntent(
                            context = context,
                            uri = effect.uri
                        )
                    )
                }
            }
        }
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
                        painter = painterResource(id = R.drawable.ic_left_arrow),
                        contentDescription = null
                    )
                }

                StyledText(
                    modifier = Modifier.align(alignment = Alignment.Center),
                    text = stringResource(id = R.string.edit_profile),
                    style = Typography.labelLarge
                )

            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .size(96.dp)
                    .clickableWithoutRipple { event(OnClickSelectImage) }
            ) {
                state.profileImage?.let { imageBitmap ->
                    Image(
                        modifier = Modifier
                            .clip(CircleShape)
                            .fillMaxSize(),
                        bitmap = imageBitmap,
                        contentDescription = null
                    )
                } ?: run {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(id = R.drawable.img_basic_profile),
                        contentDescription = null
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .align(alignment = Alignment.BottomEnd)
                        .clip(CircleShape)
                        .size(24.dp)
                        .background(color = ProfileAddGray)
                ) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(16.dp),
                        painter = painterResource(id = R.drawable.ic_profile_add),
                        tint = Color.White,
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
                    text = stringResource(id = R.string.nickname),
                    style = Typography.labelLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                MoveMoveTextField(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                        .height(40.dp),
                    value = state.nickname,
                    onValueChange = { event(OnNicknameTyped(it)) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                StyledText(
                    text = stringResource(id = R.string.one_line_introduce),
                    style = Typography.labelLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                MoveMoveTextField(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                        .height(40.dp),
                    value = state.introduce,
                    onValueChange = { event(OnIntroduceTyped(it)) }
                )
            }
        }

        RoundedCornerButton(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .padding(horizontal = 20.dp)
                .align(Alignment.CenterHorizontally),
            buttonText = stringResource(id = R.string.complete),
            isEnabled = state.isEditProfileEnabled,
            onClick = { }
        )

    }
}