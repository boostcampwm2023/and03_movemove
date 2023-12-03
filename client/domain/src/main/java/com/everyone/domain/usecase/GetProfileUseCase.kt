package com.everyone.domain.usecase

import com.everyone.domain.model.Profile
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(
        uuid: String
    ): Flow<DataState<Profile>> {
        return userRepository.getUserProfile(
            uuid = uuid
        )
    }
}