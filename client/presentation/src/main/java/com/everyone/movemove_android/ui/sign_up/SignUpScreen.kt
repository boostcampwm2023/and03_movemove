package com.everyone.movemove_android.ui.sign_up

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
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
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.everyone.movemove_android.R.drawable.ic_left_arrow
import com.everyone.movemove_android.R.drawable.ic_profile_add
import com.everyone.movemove_android.R.drawable.img_basic_profile
import com.everyone.movemove_android.R.string.complete
import com.everyone.movemove_android.R.string.nickname
import com.everyone.movemove_android.R.string.one_line_introduce
import com.everyone.movemove_android.R.string.sign_up
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.ErrorDialog
import com.everyone.movemove_android.ui.MoveMoveTextField
import com.everyone.movemove_android.ui.RoundedCornerButton
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.container.ContainerActivity
import com.everyone.movemove_android.ui.image_cropper.ImageCropperActivity
import com.everyone.movemove_android.ui.image_cropper.ImageCropperActivity.Companion.KEY_CROPPED_IMAGE_URI
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Effect.GoToHomeScreen
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Effect.LaunchImageCropper
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Effect.LaunchImagePicker
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnClickSelectImage
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnClickSignUp
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnErrorDialogDismissed
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnGetCroppedImage
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnGetUri
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnIntroduceTyped
import com.everyone.movemove_android.ui.sign_up.SignUpContract.Event.OnNicknameTyped
import com.everyone.movemove_android.ui.theme.ProfileAddGray
import com.everyone.movemove_android.ui.theme.Typography
import com.everyone.movemove_android.ui.util.clickableWithoutRipple
import com.everyone.movemove_android.ui.util.toImageBitmap
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignUpScreen(viewModel: SignUpViewModel = hiltViewModel()) {
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
                    imageLauncher.launch(PickVisualMediaRequest(ImageOnly))
                }

                is LaunchImageCropper -> {
                    imageCropperLauncher.launch(
                        ImageCropperActivity.newIntent(
                            context = context,
                            uri = effect.uri
                        )
                    )
                }

                is GoToHomeScreen -> {
                    context.startActivity(ContainerActivity.newIntent(context).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
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
                        painter = painterResource(id = ic_left_arrow),
                        contentDescription = null
                    )
                }

                StyledText(
                    modifier = Modifier.align(alignment = Center),
                    text = stringResource(id = sign_up),
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
                        painter = painterResource(id = img_basic_profile),
                        contentDescription = null
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(
                            bottom = 6.dp,
                            end = 6.dp
                        )
                        .clip(CircleShape)
                        .align(alignment = Alignment.BottomEnd)
                        .size(24.dp)
                        .background(color = ProfileAddGray)
                ) {
                    Icon(
                        modifier = Modifier.align(Center),
                        painter = painterResource(id = ic_profile_add),
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
                    text = stringResource(id = nickname),
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
                    text = stringResource(id = one_line_introduce),
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
            buttonText = stringResource(id = complete),
            isEnabled = state.isSignUpEnabled,
            onClick = { event(OnClickSignUp) }
        )
    }

    if (state.isErrorDialogShowing) {
        ErrorDialog(
            text = stringResource(id = state.errorDialogTextResourceId),
            onDismissRequest = { event(OnErrorDialogDismissed) }
        )
    }
}