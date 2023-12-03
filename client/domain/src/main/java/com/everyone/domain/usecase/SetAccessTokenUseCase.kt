package com.everyone.domain.usecase

import com.everyone.domain.repository.UserRepository
import javax.inject.Inject

class SetAccessTokenUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(accessToken: String) {
        repository.setAccessToken(accessToken)
    }
}