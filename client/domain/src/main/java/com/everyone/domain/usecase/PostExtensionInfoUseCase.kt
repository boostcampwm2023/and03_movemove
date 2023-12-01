package com.everyone.domain.usecase

import com.everyone.domain.model.VideoUploadUrl
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.VideosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostExtensionInfoUseCase @Inject constructor(private val repository: VideosRepository) {
    operator fun invoke(
        videoExtension: String,
        thumbnailExtension: String
    ): Flow<DataState<VideoUploadUrl>> = repository.postExtensionInfo(
        videoExtension = videoExtension,
        thumbnailExtension = thumbnailExtension
    )
}