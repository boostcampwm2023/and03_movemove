package com.everyone.domain.usecase

import com.everyone.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStoredUUIDUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(): Flow<String?> = repository.getUUID()
}