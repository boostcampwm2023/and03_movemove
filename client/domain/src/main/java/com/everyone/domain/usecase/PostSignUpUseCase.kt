package com.everyone.domain.usecase

import com.everyone.domain.model.UserInfo
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.StartingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostSignUpUseCase @Inject constructor(private val startingRepository: StartingRepository) {
    suspend operator fun invoke(
        profileImage: String,
        accessToken: String,
        uuid: String,
        nickname: String,
        statusMessage: String
    ): Flow<DataState<UserInfo>> {
        return startingRepository.postSignUp(
            profileImage = profileImage,
            accessToken = accessToken,
            uuid = uuid,
            nickname = nickname,
            statusMessage = statusMessage
        )
    }
}