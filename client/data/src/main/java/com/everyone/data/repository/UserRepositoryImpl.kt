package com.everyone.data.repository

import com.everyone.data.local.UserInfoManager
import com.everyone.data.local.UserInfoManager.Companion.KEY_REFRESH_TOKEN
import com.everyone.data.local.UserInfoManager.Companion.KEY_SIGNED_PLATFORM
import com.everyone.data.local.UserInfoManager.Companion.KEY_UUID
import com.everyone.data.remote.NetworkHandler
import com.everyone.data.remote.RemoteConstants.ACCESS_TOKEN
import com.everyone.data.remote.RemoteConstants.AUTH
import com.everyone.data.remote.RemoteConstants.LAST_RATED_AT
import com.everyone.data.remote.RemoteConstants.LIMIT
import com.everyone.data.remote.RemoteConstants.LOGIN
import com.everyone.data.remote.RemoteConstants.NICKNAME
import com.everyone.data.remote.RemoteConstants.PRESIGNED_URL
import com.everyone.data.remote.RemoteConstants.PROFILE
import com.everyone.data.remote.RemoteConstants.PROFILE_EXTENSION
import com.everyone.data.remote.RemoteConstants.PROFILE_IMAGE_EXTENSION
import com.everyone.data.remote.RemoteConstants.RATED
import com.everyone.data.remote.RemoteConstants.SIGN_UP
import com.everyone.data.remote.RemoteConstants.STATUS_MESSAGE
import com.everyone.data.remote.RemoteConstants.UPLOADED
import com.everyone.data.remote.RemoteConstants.USERS
import com.everyone.data.remote.RemoteConstants.UUID
import com.everyone.data.remote.RemoteConstants.VIDEOS
import com.everyone.data.remote.model.ProfileImageUploadUrlResponse
import com.everyone.data.remote.model.ProfileImageUploadUrlResponse.Companion.toDomainModel
import com.everyone.data.remote.model.ProfileResponse
import com.everyone.data.remote.model.ProfileResponse.Companion.toDomainModel
import com.everyone.data.remote.model.UserInfoResponse
import com.everyone.data.remote.model.UserInfoResponse.Companion.toDomainModel
import com.everyone.data.remote.model.VideosListResponse
import com.everyone.data.remote.model.VideosListResponse.Companion.toDomainModel
import com.everyone.data.remote.model.VideosRatedResponse
import com.everyone.data.remote.model.VideosRatedResponse.Companion.toDomainModel
import com.everyone.domain.model.Profile
import com.everyone.domain.model.ProfileImageUploadUrl
import com.everyone.domain.model.UserInfo
import com.everyone.domain.model.VideosList
import com.everyone.domain.model.VideosRated
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
        accessToken: String,
        uuid: String,
        profileImageExtension: String,
        nickname: String,
        statusMessage: String
    ): Flow<DataState<UserInfo>> = flow {
        networkHandler.request<UserInfoResponse>(
            method = HttpMethod.Post,
            isAccessTokenNeeded = false,
            url = { path(AUTH, SIGN_UP) },
            content = {
                append(ACCESS_TOKEN, accessToken)
                append(UUID, uuid)
                append(PROFILE_IMAGE_EXTENSION, profileImageExtension)
                append(NICKNAME, nickname)
                append(STATUS_MESSAGE, statusMessage)
            }
        ).collect { response ->
            response.data?.let {
                emit(DataState.Success(it.toDomainModel()))
            } ?: run {
                emit(response.toFailure())
            }
        }
    }

    override fun getUserProfile(uuid: String): Flow<DataState<Profile>> = flow {
        networkHandler.request<ProfileResponse>(
            method = HttpMethod.Get,
            url = { path(USERS, uuid, PROFILE) }
        ).collect { response ->
            response.data?.let {
                emit(DataState.Success(it.toDomainModel()))
            } ?: run {
                emit(response.toFailure())
            }
        }
    }

    override fun getUUID(): Flow<String?> = userInfoManager.load(KEY_UUID)

    override fun getSignedPlatform(): Flow<String?> = userInfoManager.load(KEY_SIGNED_PLATFORM)

    override fun getProfileImageUploadUrl(
        profileImageExtension: String,
        uuid: String,
        accessToken: String
    ): Flow<DataState<ProfileImageUploadUrl>> = flow {
        networkHandler.request<ProfileImageUploadUrlResponse>(
            method = HttpMethod.Get,
            isAccessTokenNeeded = false,
            url = {
                path(AUTH, SIGN_UP, PRESIGNED_URL, PROFILE)
                parameters.append(PROFILE_EXTENSION, profileImageExtension)
                parameters.append(UUID, uuid)
                parameters.append(ACCESS_TOKEN, accessToken)
            }
        ).collect { response ->
            response.data?.let {
                emit(DataState.Success(it.toDomainModel()))
            } ?: run {
                emit(response.toFailure())
            }
        }
    }

    override fun storeRefreshToken(refreshToken: String): Flow<Boolean> =
        userInfoManager.store(KEY_REFRESH_TOKEN, refreshToken)

    override fun storeUUID(uuid: String): Flow<Boolean> = userInfoManager.store(KEY_UUID, uuid)

    override fun storeSignedPlatform(signedPlatform: String): Flow<Boolean> =
        userInfoManager.store(KEY_SIGNED_PLATFORM, signedPlatform)

    override fun setAccessToken(accessToken: String) {
        networkHandler.setAccessToken(accessToken)
    }

    override fun login(
        accessToken: String,
        uuid: String
    ): Flow<DataState<UserInfo>> = flow {
        networkHandler.request<UserInfoResponse>(
            method = HttpMethod.Post,
            isAccessTokenNeeded = false,
            url = { path(AUTH, LOGIN) },
            content = {
                append(ACCESS_TOKEN, accessToken)
                append(UUID, uuid)
            }
        ).collect { response ->
            response.data?.let {
                emit(DataState.Success(it.toDomainModel()))
            } ?: run {
                emit(response.toFailure())
            }
        }
    }

    override fun getUsersVideosUploaded(
        userId: String,
        limit: String,
        lastId: String
    ): Flow<DataState<VideosList>> = flow {
        networkHandler.request<VideosListResponse>(
            method = HttpMethod.Get,
            url = {
                path(USERS, userId, VIDEOS, UPLOADED)
                parameters.append(LIMIT, limit)
            },
            content = null
        ).collect { response ->
            response.data?.let {
                emit(DataState.Success(it.toDomainModel()))
            } ?: run {
                emit(response.toFailure())
            }
        }
    }

    override fun getUsersVideosRated(
        userId: String,
        limit: String,
        lastRatedAt: String
    ): Flow<DataState<VideosRated>> = flow {
        networkHandler.request<VideosRatedResponse>(
            method = HttpMethod.Get,
            url = {
                path(USERS, userId, VIDEOS, RATED)
                parameters.append(LIMIT, limit)
            },
            content = null
        ).collect { response ->
            response.data?.let {
                emit(DataState.Success(it.toDomainModel()))
            } ?: run {
                emit(response.toFailure())
            }
        }
    }
}