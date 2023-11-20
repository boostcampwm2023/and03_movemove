package com.everyone.movemove_android.ui.login

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.everyone.movemove_android.BuildConfig
import com.everyone.movemove_android.R.drawable
import com.everyone.movemove_android.ui.theme.GoogleGray
import com.everyone.movemove_android.ui.theme.KakaoYellow
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch


@Composable
fun LoginScreen() {

    val context = LocalContext.current
    //Google
    val coroutineScope = rememberCoroutineScope()
    val signInRequestCode = 1
    val googleSignInClient = getGoogleSignInClient(context)
    val authResultLauncher = rememberLauncherForActivityResult(contract = AuthResultContract(googleSignInClient = googleSignInClient), onResult = {
        try {
            val account = it?.getResult(ApiException::class.java)
            if (account == null) {
                Log.d("에러상황", "발생")
            } else {
                coroutineScope.launch {
                    Log.d("구글 로그인 성공", "${account.id}")
                    Log.d("구글 로그인 성공2", "${account.idToken}")
                }
            }
        } catch (e: ApiException) {
            Log.d("에러상황", "발생2")
        }
    })
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 10.dp)
        ) {
            Button(
                onClick = {
                    handleKakaoLogin(context)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp), colors = ButtonDefaults.buttonColors(
                    containerColor = KakaoYellow, contentColor = Color.Black
                ), shape = RoundedCornerShape(10.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Icon(
                        painter = painterResource(id = drawable.ic_kakao_login), contentDescription = "null", modifier = Modifier.align(alignment = Alignment.CenterStart)
                    )
                    Text(
                        //TODO 구글하고 같이 String.xml으로 추출
                        text = "카카오 로그인",
                        modifier = Modifier.align(alignment = Alignment.Center),
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    authResultLauncher.launch(signInRequestCode)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp), colors = ButtonDefaults.buttonColors(
                    containerColor = GoogleGray, contentColor = Color.Black
                ), shape = RoundedCornerShape(10.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = drawable.img_google_login), contentDescription = "null", modifier = Modifier
                            .align(alignment = Alignment.CenterStart)
                            .size(18.dp)
                    )
                    Text(
                        //TODO 구글하고 같이 String.xml으로 추출
                        text = "구글로 로그인",
                        modifier = Modifier.align(alignment = Alignment.Center),
                    )
                }
            }
        }
    }
}

//Google
fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestId().requestIdToken(BuildConfig.GOOGLE_CLIENT_ID).build()
    return GoogleSignIn.getClient(context, signInOptions)
}

class AuthResultContract(private val googleSignInClient: GoogleSignInClient) : ActivityResultContract<Int, Task<GoogleSignInAccount>?>() {
    override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount>? {
        return when (resultCode) {
            Activity.RESULT_OK -> GoogleSignIn.getSignedInAccountFromIntent(intent)
            else -> null
        }
    }

    override fun createIntent(context: Context, input: Int): Intent {
        return googleSignInClient.signInIntent.putExtra("input", input)
    }
}

fun handleKakaoLogin(context: Context) {

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
            UserApiClient.instance.me { user, _ ->
                Log.i(TAG, "계정 uid ${user!!.id}")
            }
        }
    }

// 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오톡으로 로그인 실패", error)

                // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    return@loginWithKakaoTalk
                }

                // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            } else if (token != null) {
                Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
            }
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
    }
}
