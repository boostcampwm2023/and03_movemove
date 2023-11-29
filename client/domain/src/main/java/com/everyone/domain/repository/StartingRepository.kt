package com.everyone.domain.repository

import com.everyone.domain.model.UserInfo
import com.everyone.domain.model.base.DataState
import kotlinx.coroutines.flow.Flow

interface StartingRepository {
    suspend fun postSignUp(
        profileImage: String,
        accessToken: String,
        uuid: String,
        nickname: String,
        statusMessage: String
    ): Flow<DataState<UserInfo>>

}