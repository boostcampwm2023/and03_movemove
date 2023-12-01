package com.everyone.domain.usecase

import com.everyone.domain.model.CreatedVideo
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.VideosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostVideoInfoUseCase @Inject constructor(private val repository: VideosRepository) {
    operator fun invoke(
        videoId: String,
        title: String,
        content: String,
        category: String,
        videoExtension: String,
        thumbnailExtension: String
    ): Flow<DataState<CreatedVideo>> = repository.postVideoInfo(
        videoId = videoId,
        title = title,
        content = content,
        category = category,
        videoExtension = videoExtension,
        thumbnailExtension = thumbnailExtension
    )
}