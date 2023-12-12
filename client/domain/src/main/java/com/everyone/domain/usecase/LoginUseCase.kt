package com.everyone.domain.usecase

import com.everyone.domain.model.UserInfo
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(
        platform: String,
        idToken: String,
        uuid: String
    ): Flow<DataState<UserInfo>> = repository.login(
        platform = platform,
        idToken = idToken,
        uuid = uuid
    )
}