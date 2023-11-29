package com.everyone.domain.repository

import com.everyone.domain.model.Ads
import com.everyone.domain.model.VideosRandom
import com.everyone.domain.model.base.DataState
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    suspend fun getAds(): Flow<Ads>

    suspend fun getVideosRandom(
        limit: String,
        category: String,
    ): Flow<DataState<VideosRandom>>

    suspend fun putVideosRating(
        id: String,
        rating: String,
        reason: String
    ): Flow<Unit>

}