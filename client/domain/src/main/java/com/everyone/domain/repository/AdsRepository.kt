package com.everyone.domain.repository

import com.everyone.domain.model.Advertisements
import com.everyone.domain.model.base.DataState
import kotlinx.coroutines.flow.Flow

interface AdsRepository {

    fun getAds(): Flow<DataState<Advertisements>>

}