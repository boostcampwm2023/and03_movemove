package com.everyone.data.repository

import com.everyone.data.local.UserInfoManager
import com.everyone.data.remote.NetworkHandler
import com.everyone.data.remote.RemoteConstants.AUTH
import com.everyone.data.remote.RemoteConstants.PROFILE
import com.everyone.data.remote.RemoteConstants.SIGN_UP
import com.everyone.data.remote.RemoteConstants.USERS
import com.everyone.data.remote.model.ProfileResponse
import com.everyone.data.remote.model.ProfileResponse.Companion.toDomainModel
import com.everyone.data.remote.model.UserInfoResponse
import com.everyone.data.remote.model.UserInfoResponse.Companion.toDomainModel
import com.everyone.domain.model.Profile
import com.everyone.domain.model.UserInfo
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.UserRepository
import io.ktor.http.HttpMethod
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val networkHandler: NetworkHandler,
    private val userInfoManager: UserInfoManager
) : UserRepository {
    override fun postSignUp(
        profileImage: String,
        accessToken: String,
        uuid: String,
        nickname: String,
        statusMessage: String
    ): Flow<DataState<UserInfo>> = flow {
        networkHandler.request<UserInfoResponse>(
            method = HttpMethod.Post,
            url = { path(AUTH, SIGN_UP) }
        ).collect { response ->
            response.data?.let {
                emit(DataState.Success(it.toDomainModel()))
            } ?: run {
                emit(response.toFailure())
            }
        }
    }

    override fun getUserProfile(userId: String): Flow<DataState<Profile>> = flow {
        networkHandler.request<ProfileResponse>(
            method = HttpMethod.Get,
            url = { path(USERS, userId, PROFILE) }
        ).collect { response ->
            response.data?.let {
                emit(DataState.Success(it.toDomainModel()))
            } ?: run {
                emit(response.toFailure())
            }
        }
    }

    override fun getUserId(): Flow<String?> = userInfoManager.load(UserInfoManager.KEY_USER_ID)

    override fun getSignedPlatform(): Flow<String?> = userInfoManager.load(UserInfoManager.KEY_SIGNED_PLATFORM)
}