package com.everyone.domain.usecase

import com.everyone.domain.model.Profile
import com.everyone.domain.model.UserInfo
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(
        accessToken: String,
        uuid: String
    ): Flow<DataState<UserInfo>> = repository.login(
        accessToken = accessToken,
        uuid = uuid
    )
}