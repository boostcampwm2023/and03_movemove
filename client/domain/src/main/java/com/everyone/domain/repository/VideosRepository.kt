package com.everyone.domain.repository

import com.everyone.domain.model.CreatedVideo
import com.everyone.domain.model.VideoUploadUrl
import com.everyone.domain.model.VideosRandom
import com.everyone.domain.model.VideosTrend
import com.everyone.domain.model.base.DataState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface VideosRepository {
    fun postExtensionInfo(
        videoExtension: String,
        thumbnailExtension: String
    ): Flow<DataState<VideoUploadUrl>>

    fun putFile(
        requestUrl: String,
        file: File
    ): Flow<Int>

    fun postVideoInfo(
        videoId: String,
        title: String,
        content: String,
        category: String,
        videoExtension: String,
        thumbnailExtension: String
    ): Flow<DataState<CreatedVideo>>

    suspend fun getVideosRandom(
        limit: String,
        category: String,
        seed: String
    ): Flow<DataState<VideosRandom>>

    suspend fun putVideosRating(
        id: String,
        rating: String,
        reason: String
    ): Flow<DataState<Unit>>

    fun putVideosViews(
        videoId: String,
        seed: String,
    ): Flow<DataState<Unit>>

    suspend fun getVideosTopRated(category: String): Flow<DataState<VideosTrend>>

    suspend fun getVideosTrend(limit: String): Flow<DataState<VideosTrend>>
}