package com.everyone.domain.usecase

import com.everyone.domain.model.Videos
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.VideosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVideoWithIdUseCase @Inject constructor(private val repository: VideosRepository) {
    operator fun invoke(videoId: String): Flow<DataState<Videos>> = repository.getVideoWithId(videoId)
}