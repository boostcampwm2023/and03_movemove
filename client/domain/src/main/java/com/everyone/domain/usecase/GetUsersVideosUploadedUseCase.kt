package com.everyone.domain.usecase

import com.everyone.domain.model.VideosUploaded
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersVideosUploadedUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(
        limit: String,
        userId: String,
        lastId: String
    ): Flow<DataState<VideosUploaded>> {
        return repository.getUsersVideosUploaded(
            limit = limit,
            userId = userId,
            lastId = lastId
        )
    }
}