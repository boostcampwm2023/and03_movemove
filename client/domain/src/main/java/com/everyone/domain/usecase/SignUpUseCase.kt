package com.everyone.domain.usecase

import com.everyone.domain.model.UserInfo
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(
        platform: String,
        idToken: String,
        uuid: String,
        nickname: String,
        profileImageExtension: String,
        statusMessage: String
    ): Flow<DataState<UserInfo>> {
        return userRepository.postSignUp(
            platform = platform,
            idToken = idToken,
            uuid = uuid,
            profileImageExtension = profileImageExtension,
            nickname = nickname,
            statusMessage = statusMessage
        )
    }
}