package com.everyone.data.repository

import com.everyone.data.remote.NetworkHandler
import com.everyone.data.remote.model.ProfileResponse
import com.everyone.data.remote.model.ProfileResponse.Companion.toDomainModel
import com.everyone.data.remote.model.UserInfoResponse
import com.everyone.data.remote.model.UserInfoResponse.Companion.toDomainModel
import com.everyone.domain.model.Profile
import com.everyone.domain.model.UserInfo
import com.everyone.domain.model.base.DataState
import com.everyone.domain.model.base.NetworkError
import com.everyone.domain.repository.UserRepository
import io.ktor.http.HttpMethod
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val networkHandler: NetworkHandler) : UserRepository {
    override suspend fun postSignUp(
        profileImage: String,
        accessToken: String,
        uuid: String,
        nickname: String,
        statusMessage: String
    ): Flow<DataState<UserInfo>> {
        return flow {
            networkHandler.request<UserInfoResponse>(
                method = HttpMethod.Post,
                url = { path("auth", "signup") }
            ).collect { response ->
                response.data?.let {
                    emit(DataState.Success(it.toDomainModel()))
                } ?: run {
                    emit(DataState.Failure(NetworkError(response.statusCode, response.message)))
                }
            }
        }
    }

    override fun getUserProfile(userId: String): Flow<DataState<Profile>> {
        return flow {
            networkHandler.request<ProfileResponse>(
                method = HttpMethod.Get,
                url = { path("users", userId, "profile") }
            ).collect { response ->
                response.data?.let {
                    emit(DataState.Success(it.toDomainModel()))
                } ?: run {
                    emit(DataState.Failure(NetworkError(response.statusCode, response.message)))
                }
            }
        }
    }
}