package com.everyone.domain.usecase

import com.everyone.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StoreSignedPlatformUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(signedPlatform: String): Flow<Boolean> = repository.storeSignedPlatform(signedPlatform)
}