package com.everyone.domain.usecase

import com.everyone.domain.model.VideosRated
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersVideosRatedUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(
        limit: String,
        userId: String,
        lastRatedAt: String
    ): Flow<DataState<VideosRated>> {
        return repository.getUsersVideosRated(
            limit = limit,
            userId = userId,
            lastRatedAt = lastRatedAt
        )
    }
}