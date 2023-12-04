package com.everyone.data.remote

import android.util.Log
import com.everyone.data.base.ApiResponse
import com.everyone.data.local.UserInfoManager
import com.everyone.data.local.UserInfoManager.Companion.KEY_REFRESH_TOKEN
import com.everyone.data.remote.RemoteConstants.AUTH
import com.everyone.data.remote.RemoteConstants.AUTHORIZATION
import com.everyone.data.remote.RemoteConstants.BEARER
import com.everyone.data.remote.RemoteConstants.REFRESH
import com.everyone.data.remote.RemoteConstants.REFRESH_TOKEN
import com.everyone.data.remote.model.AccessTokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.retry
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.headers
import io.ktor.client.request.put
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import io.ktor.http.ParametersBuilder
import io.ktor.http.URLBuilder
import io.ktor.http.path
import io.ktor.serialization.gson.gson
import io.ktor.util.InternalAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class NetworkHandler @Inject constructor(private val userInfoManager: UserInfoManager) {
    lateinit var accessToken: String
        private set
    var isTokenRefreshing = false

    fun setAccessToken(accessToken: String) {
        this.accessToken = accessToken
    }

    val client: HttpClient
        get() = HttpClient(CIO) {
            install(ContentNegotiation) {
                gson { setPrettyPrinting().create() }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = REQUEST_TIMEOUT
                connectTimeoutMillis = CONNECT_TIMEOUT
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d(LOG_TAG, message)
                    }
                }
            }
        }

    @OptIn(InternalAPI::class)
    inline fun <reified T> request(
        method: HttpMethod,
        isAccessTokenNeeded: Boolean = true,
        crossinline url: URLBuilder.() -> Unit,
        noinline content: (ParametersBuilder.() -> Unit)? = null
    ): Flow<ApiResponse<T>> = flow {
        client.use { client ->
            val request: suspend () -> ApiResponse<T>? = {
                client.request {
                    this.method = method
                    if (isAccessTokenNeeded) {
                        headers {
                            append(
                                name = AUTHORIZATION,
                                value = "$BEARER $accessToken"
                            )
                        }
                    }

                    url {
                        host = BASE_URL
                        url()
                    }

                    content?.let {
                        body = FormDataContent(Parameters.build { content() })
                    }

                    retry {
                        maxRetries = RETRY_COUNT
                        delayMillis { RETRY_DELAY }
                        retryIf { _, response ->
                            response.status.value == UNAUTHORIZED && isTokenRefreshing
                        }
                    }
                }.body()
            }

            request()?.let { responseBody ->
                if (responseBody.statusCode in SUCCESS_RANGE) {
                    emit(responseBody)
                } else {
                    val requestDeferredCall: suspend () -> Unit = {
                        request()?.let { newResponse ->
                            emit(newResponse)
                        } ?: throw Exception(UNCONNECTED_EXCEPTION)
                    }

                    if (responseBody.statusCode == ACCESS_TOKEN_EXPIRED && !isTokenRefreshing) {
                        isTokenRefreshing = true
                        val isAccessTokenRefreshed = refreshAccessToken()
                        if (isAccessTokenRefreshed) {
                            isTokenRefreshing = false
                            requestDeferredCall()
                        } else {
                            // todo : RefreshToken 없을 시 예외 처리
                        }
                    } else if (isTokenRefreshing) {
                        requestDeferredCall()
                    } else {
                        emit(responseBody)
                    }
                }
            } ?: throw Exception(UNCONNECTED_EXCEPTION)
        }
    }

    fun requestSendFileToNCP(
        url: String,
        file: File,
    ): Flow<Int> = flow {
        client.use { client ->
            val response = client.put {
                this.url(url)
                setBody(file.readBytes())
            }

            emit(response.status.value)
        }
    }

    suspend fun refreshAccessToken(): Boolean {
        val refreshToken = userInfoManager.load(KEY_REFRESH_TOKEN).first()
        if (refreshToken != null) {
            val refreshedToken = request<AccessTokenResponse>(
                method = HttpMethod.Get,
                isAccessTokenNeeded = false,
                url = { path(AUTH, REFRESH) },
                content = { append(REFRESH_TOKEN, refreshToken) }
            ).first().data?.accessToken ?: ""

            this.accessToken = refreshedToken
            return refreshedToken.isNotEmpty()
        }

        return false
    }

    companion object {
        const val BASE_URL = "223.130.136.106"
        const val ACCESS_TOKEN_EXPIRED = 3001
        const val RETRY_COUNT = 5
        const val UNAUTHORIZED = 401
        const val RETRY_DELAY = 1000L
        const val UNCONNECTED_EXCEPTION = "UnconnectedException"
        val SUCCESS_RANGE = 200..299
        private const val REQUEST_TIMEOUT = 10000L
        private const val CONNECT_TIMEOUT = 10000L
        private const val LOG_TAG = "KTOR_LOG"
    }
}