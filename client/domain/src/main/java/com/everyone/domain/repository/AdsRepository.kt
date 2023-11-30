package com.everyone.domain.repository

import com.everyone.domain.model.Ads
import com.everyone.domain.model.base.DataState
import kotlinx.coroutines.flow.Flow

interface AdsRepository {

    suspend fun getAds(): Flow<DataState<Ads>>

}