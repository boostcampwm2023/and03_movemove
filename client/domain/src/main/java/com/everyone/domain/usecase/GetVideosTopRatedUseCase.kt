package com.everyone.domain.usecase

import com.everyone.domain.model.VideosTrend
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.VideosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVideosTopRatedUseCase @Inject constructor(private val mainRepository: VideosRepository) {
    suspend operator fun invoke(category: String): Flow<DataState<VideosTrend>> {
        return mainRepository.getVideosTopRated(category = category)
    }
}