package com.everyone.domain.repository

import com.everyone.domain.model.VideosRandom
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    suspend fun getVideosRandom(
        limit: String,
        category: String,
    ): Flow<VideosRandom>
}