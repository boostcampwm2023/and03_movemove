package com.everyone.domain.usecase

import com.everyone.domain.model.VideosList
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.VideosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVideosTrendUseCase @Inject constructor(private val repository: VideosRepository) {
    operator fun invoke(limit: String): Flow<DataState<VideosList>> {
        return repository.getVideosTrend(limit = limit)
    }
}