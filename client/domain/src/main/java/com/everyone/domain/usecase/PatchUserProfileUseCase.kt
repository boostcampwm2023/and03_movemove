package com.everyone.domain.usecase

import com.everyone.domain.model.Profile
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PatchUserProfileUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(
        nickname: String,
        statusMessage: String,
        profileImageExtension: String
    ): Flow<DataState<Profile>> = repository.patchUserProfile(
        nickname = nickname,
        statusMessage = statusMessage,
        profileImageExtension = profileImageExtension
    )
}