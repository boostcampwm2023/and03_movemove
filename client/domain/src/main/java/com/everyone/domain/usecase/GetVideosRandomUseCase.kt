package com.everyone.domain.usecase

import com.everyone.domain.model.VideosRandom
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.VideosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVideosRandomUseCase @Inject constructor(private val videosRepository: VideosRepository) {
    suspend operator fun invoke(
        limit: String,
        category: String,
        seed: String
    ): Flow<DataState<VideosRandom>> {
        return videosRepository.getVideosRandom(
            limit = limit,
            category = category,
            seed = seed
        )
    }
}