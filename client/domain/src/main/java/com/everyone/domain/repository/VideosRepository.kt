package com.everyone.domain.repository

import com.everyone.domain.model.VideosRandom
import com.everyone.domain.model.VideosTrend
import com.everyone.domain.model.base.DataState
import kotlinx.coroutines.flow.Flow

interface VideosRepository {

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

    suspend fun getVideosTopRated(category: String): Flow<DataState<VideosTrend>>

    suspend fun getVideosTrend(limit: String): Flow<DataState<VideosTrend>>

}