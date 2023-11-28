package com.everyone.domain.usecase

import com.everyone.domain.model.VideosRandom
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVideoRandomUseCase @Inject constructor(private val mainRepository: MainRepository) {
    suspend operator fun invoke(
        limit: String,
        category: String
    ): Flow<DataState<VideosRandom>> {
        return mainRepository.getVideosRandom(
            limit = limit,
            category = category
        )
    }
}