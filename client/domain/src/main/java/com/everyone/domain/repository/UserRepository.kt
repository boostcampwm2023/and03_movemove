package com.everyone.domain.repository

import com.everyone.domain.model.Profile
import com.everyone.domain.model.ProfileImageUploadUrl
import com.everyone.domain.model.UserInfo
import com.everyone.domain.model.VideosList
import com.everyone.domain.model.VideosRated
import com.everyone.domain.model.VideosUploaded
import com.everyone.domain.model.base.DataState
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun postSignUp(
        platform: String,
        idToken: String,
        uuid: String,
        profileImageExtension: String,
        nickname: String,
        statusMessage: String
    ): Flow<DataState<UserInfo>>

    fun getUserProfile(uuid: String): Flow<DataState<Profile>>

    fun getUUID(): Flow<String?>

    fun getSignedPlatform(): Flow<String?>

    fun getProfileImageUploadUrl(
        profileImageExtension: String,
        uuid: String,
        accessToken: String
    ): Flow<DataState<ProfileImageUploadUrl>>

    fun getPresignedUrlProfile(profileExtension: String): Flow<DataState<ProfileImageUploadUrl>>

    fun storeRefreshToken(uuid: String): Flow<Boolean>

    fun storeUUID(uuid: String): Flow<Boolean>

    fun storeSignedPlatform(signedPlatform: String): Flow<Boolean>

    fun setAccessToken(accessToken: String)

    fun login(
        platform: String,
        idToken: String,
        uuid: String
    ): Flow<DataState<UserInfo>>

    fun getUsersVideosUploaded(
        userId: String,
        limit: String,
        lastId: String
    ): Flow<DataState<VideosList>>
  
      fun patchUserProfile(
        nickname: String,
        statusMessage: String,
        profileImageExtension: String
    ): Flow<DataState<Profile>>

    fun getUsersVideosRated(
        userId: String,
        limit: String,
        lastRatedAt: String
    ): Flow<DataState<VideosRated>>
}