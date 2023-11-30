package com.everyone.domain.usecase

import com.everyone.domain.model.VideoUploadUrl
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.VideosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostExtensionInfoUseCase @Inject constructor(private val repository: VideosRepository) {
    operator fun invoke(
        videoExtension: String = VIDEO_EXTENSION,
        thumbnailExtension: String = THUMBNAIL_EXTENSION
    ): Flow<DataState<VideoUploadUrl>> = repository.postExtensionInfo(
        videoExtension = videoExtension,
        thumbnailExtension = thumbnailExtension
    )

    companion object {
        private const val VIDEO_EXTENSION = "mp4"
        private const val THUMBNAIL_EXTENSION = "webp"
    }
}