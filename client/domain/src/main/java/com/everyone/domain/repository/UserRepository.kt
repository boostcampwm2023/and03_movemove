package com.everyone.domain.repository

import com.everyone.domain.model.Profile
import com.everyone.domain.model.UserInfo
import com.everyone.domain.model.base.DataState
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun postSignUp(
        profileImage: String,
        accessToken: String,
        uuid: String,
        nickname: String,
        statusMessage: String
    ): Flow<DataState<UserInfo>>

    fun getUserProfile(userId: String): Flow<DataState<Profile>>
}