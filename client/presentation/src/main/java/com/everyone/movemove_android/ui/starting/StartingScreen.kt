package com.everyone.movemove_android.ui.starting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.everyone.movemove_android.BuildConfig
import com.everyone.movemove_android.R
import com.everyone.movemove_android.base.use
import com.everyone.movemove_android.ui.LoadingDialog
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.UiConstants.GOOGLE
import com.everyone.movemove_android.ui.UiConstants.KAKAO
import com.everyone.movemove_android.ui.container.ContainerActivity
import com.everyone.movemove_android.ui.sign_up.SignUpActivity
import com.everyone.movemove_android.ui.starting.StartingActivity.Companion.SIGN_IN_REQUEST_CODE
import com.everyone.movemove_android.ui.starting.StartingContract.Effect.GoToHomeScreen
import com.everyone.movemove_android.ui.starting.StartingContract.Effect.GoToSignUpScreen
import com.everyone.movemove_android.ui.starting.StartingContract.Effect.LaunchGoogleLogin
import com.everyone.movemove_android.ui.starting.StartingContract.Effect.LaunchKakaoLogin
import com.everyone.movemove_android.ui.starting.StartingContract.Event.OnClickKakaoLogin
import com.everyone.movemove_android.ui.starting.StartingContract.Event.OnSocialLoginSuccess
import com.everyone.movemove_android.ui.starting.StartingContract.Event.OnStarted
import com.everyone.movemove_android.ui.theme.GoogleGray
import com.everyone.movemove_android.ui.theme.KakaoYellow
import com.everyone.movemove_android.ui.theme.StartingDim
import com.everyone.movemove_android.ui.theme.Typography
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.util.UUID

private const val STARTING_VIDEO_NAME = "starting_video"
private const val STARTING_DELAY = 1000L
private const val BUTTON_ANIMATION_DURATION = 1000

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun StartingScreen(viewModel: StartingViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val (state, event, effect) = use(viewModel)
    val kakaoSignInClient = remember { UserApiClient.instance }
    val googleSignInClient = remember {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
                .build()
        )
    }

    val authResultContract = object : ActivityResultContract<Int, Task<GoogleSignInAccount>?>() {
        override fun createIntent(context: Context, input: Int): Intent {
            return googleSignInClient.signInIntent.putExtra("input", input)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount>? {
            return when (resultCode) {
                Activity.RESULT_OK -> GoogleSignIn.getSignedInAccountFromIntent(intent)
                else -> null
            }
        }
    }

    val authResultLauncher = rememberLauncherForActivityResult(
        contract = authResultContract,
        onResult = {
            try {
                val account = it?.getResult(ApiException::class.java)
                account?.let {
                    val googleIdToken = account.idToken
                    val googleId = account.id

                    if (googleId != null && googleIdToken != null) {
                        event(
                            OnSocialLoginSuccess(
                                idToken = googleIdToken,
                                platform = GOOGLE,
                                uuid = UUID(
                                    googleId.substring(0 until 10).toLong(),
                                    googleId.substring(10 until googleId.length).toLong()
                                ).toString()
                                // 구글 ID 는 Long 이 아니라 String 으로 들어오는데, 그 수가 Long 의 최대값을 넘기 때문에 잘라내어 사용합니다.
                            )
                        )
                    } else {
                        // todo : 예외 처리
                    }
                } ?: run {
                    //TODO retry or 카카오로 로그인 안내 문구
                }
            } catch (e: ApiException) {
                //TODO network error
            }
        }
    )

    LaunchedEffect(effect) {
        effect.collectLatest { effect ->
            when (effect) {
                is LaunchKakaoLogin -> {
                    if (kakaoSignInClient.isKakaoTalkLoginAvailable(context)) {
                        kakaoSignInClient.loginWithKakaoTalk(context) { token, error ->
                            if (error != null) {
                                //Fail to Login with kakao application
                                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                    return@loginWithKakaoTalk
                                }
                                kakaoSignInClient.loginWithKakaoAccount(
                                    context = context,
                                    callback = { _, _ -> }
                                )
                            } else {
                                val kakaoIdToken = token?.idToken
                                kakaoSignInClient.me { user, error ->
                                    val userId = user?.id
                                    if (kakaoIdToken != null && userId != null) {
                                        event(
                                            OnSocialLoginSuccess(
                                                idToken = kakaoIdToken,
                                                platform = KAKAO,
                                                uuid = UUID(userId, userId).toString()
                                            )
                                        )
                                    } else {
                                        // todo : 예외 처리
                                    }
                                }
                            }
                        }
                    } else {
                        kakaoSignInClient.loginWithKakaoAccount(
                            context = context,
                            callback = { token, error ->
                                if (error != null) {
                                    //fail without application login
                                } else if (token != null) {
                                    val kakaoIdToken = token.idToken
                                    kakaoSignInClient.me { user, _ ->
                                        val userId = user?.id
                                        if (kakaoIdToken != null && userId != null) {
                                            event(
                                                OnSocialLoginSuccess(
                                                    idToken = kakaoIdToken,
                                                    platform = KAKAO,
                                                    uuid = UUID(userId, userId).toString()
                                                )
                                            )
                                        } else {
                                            // todo : 예외 처리
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                is LaunchGoogleLogin -> {
                    authResultLauncher.launch(SIGN_IN_REQUEST_CODE)
                }

                is GoToSignUpScreen -> {
                    context.startActivity(
                        SignUpActivity.newIntent(
                            context = context,
                            idToken = effect.idToken,
                            platform = effect.platform,
                            uuid = effect.uuid
                        )
                    )
                }

                is GoToHomeScreen -> {
                    context.startActivity(ContainerActivity.newIntent(context))
                    (context as Activity).finish()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(STARTING_DELAY)
        event(OnStarted)
    }

    with(state) {
        Box(modifier = Modifier.fillMaxSize()) {
            val context = LocalContext.current
            val resourceId = context.resources.getIdentifier(STARTING_VIDEO_NAME, "raw", context.packageName)

            val videoDataSource by remember {
                val dataSourceFactory = DefaultDataSource.Factory(context, DefaultDataSource.Factory(context))
                val videoUri = Uri.parse("android.resource://" + context.packageName + "/" + resourceId);
                mutableStateOf(ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri)))
            }

            val exoPlayer = remember {
                ExoPlayer.Builder(context).build().apply {
                    setMediaSource(videoDataSource)
                    videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                    repeatMode = Player.REPEAT_MODE_ONE
                    playWhenReady = true
                    prepare()
                }
            }

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    PlayerView(context).apply {
                        useController = false
                        player = exoPlayer
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    }
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = StartingDim)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier.size(160.dp),
                        painter = painterResource(id = R.drawable.ic_title_white),
                        contentDescription = null
                    )

                    StyledText(
                        text = stringResource(id = R.string.app_description),
                        style = Typography.labelMedium,
                        color = Color.White
                    )
                }

                val loginButtonAlphaState = animateFloatAsState(
                    targetValue = if (isSignUpNeeded) 1f else 0f,
                    animationSpec = tween(durationMillis = BUTTON_ANIMATION_DURATION),
                    label = ""
                )

                if (isSignUpNeeded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .alpha(loginButtonAlphaState.value)
                    ) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            onClick = { event(OnClickKakaoLogin) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KakaoYellow,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    modifier = Modifier
                                        .align(alignment = Alignment.CenterStart)
                                        .size(24.dp),
                                    painter = painterResource(id = R.drawable.ic_kakao),
                                    contentDescription = null,
                                )
                                StyledText(
                                    modifier = Modifier.align(alignment = Alignment.Center),
                                    text = stringResource(R.string.kakao_login),
                                    style = Typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.Black
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            onClick = { authResultLauncher.launch(SIGN_IN_REQUEST_CODE) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoogleGray,
                                contentColor = Color.Black
                            ), shape = RoundedCornerShape(10.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    modifier = Modifier
                                        .align(alignment = Alignment.CenterStart)
                                        .size(24.dp),
                                    painter = painterResource(id = R.drawable.ic_google),
                                    contentDescription = null,
                                )
                                StyledText(
                                    modifier = Modifier.align(alignment = Alignment.Center),
                                    text = stringResource(R.string.google_login),
                                    style = Typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isLoading) {
            LoadingDialog()
        }
    }
}