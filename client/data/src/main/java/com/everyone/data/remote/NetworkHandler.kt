package com.everyone.data.remote

import android.util.Log
import com.everyone.data.base.BaseResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import io.ktor.http.ParametersBuilder
import io.ktor.http.path
import io.ktor.serialization.gson.gson
import io.ktor.util.InternalAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NetworkHandler {
    val client: HttpClient
        get() = HttpClient(CIO) {
            install(ContentNegotiation) {
                gson {
                    setPrettyPrinting().create()
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = REQUEST_TIMEOUT
                connectTimeoutMillis = CONNECT_TIMEOUT
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d(LOG_TAG, message)
                    }
                }
                level = LogLevel.ALL
            }

            install(DefaultRequest) {
                host = BASE_URL
            }
        }

    @OptIn(InternalAPI::class)
    inline fun <reified T> request(
        method: HttpMethod,
        urlParams: UrlParams,
        noinline content: (ParametersBuilder.() -> Unit)?
    ): Flow<BaseResponse<T>> = flow {
        client.use { client ->
            val response: BaseResponse.Success<T> = client.request {
                this.method = method

                url {
                    path(*urlParams.pathArray)
                    urlParams.queryList.forEach { query -> parameters.append(query.first, query.second) }
                }

                content?.let {
                    body = FormDataContent(Parameters.build {
                        content()
                    })
                }
            }.body()

            response.data?.let {
                emit(response)
            } ?: run {
                emit(
                    BaseResponse.Failure(
                        statusCode = response.statusCode,
                        message = response.message
                    )
                )
            }
        }
    }

    companion object {
        const val BASE_URL = "223.130.136.106"
        private const val REQUEST_TIMEOUT = 5000L
        private const val CONNECT_TIMEOUT = 5000L
        private const val LOG_TAG = "KTOR_LOG"
    }
}