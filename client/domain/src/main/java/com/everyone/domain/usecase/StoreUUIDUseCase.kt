package com.everyone.domain.usecase

import com.everyone.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StoreUUIDUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(uuid: String) : Flow<Boolean> = repository.storeUUID(uuid)
}
