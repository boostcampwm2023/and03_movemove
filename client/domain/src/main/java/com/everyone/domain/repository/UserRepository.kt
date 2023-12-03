package com.everyone.domain.repository

import com.everyone.domain.model.Profile
import com.everyone.domain.model.ProfileImageUploadUrl
import com.everyone.domain.model.UserInfo
import com.everyone.domain.model.base.DataState
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun postSignUp(
        accessToken: String,
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

    fun storeRefreshToken(uuid: String): Flow<Boolean>

    fun storeUUID(uuid: String): Flow<Boolean>

    fun storeSignedPlatform(signedPlatform: String): Flow<Boolean>

    fun setAccessToken(accessToken: String)
}