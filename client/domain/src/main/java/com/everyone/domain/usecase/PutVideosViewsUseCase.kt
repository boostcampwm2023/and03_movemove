package com.everyone.domain.usecase

import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.VideosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PutVideosViewsUseCase @Inject constructor(private val repository: VideosRepository) {
    operator fun invoke(
        videoId: String,
        seed: String,
    ): Flow<DataState<Unit>> = repository.putVideosViews(
        videoId = videoId,
        seed = seed,
    )
}