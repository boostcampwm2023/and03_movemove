package com.everyone.data.repository

import com.everyone.data.remote.NetworkHandler
import com.everyone.data.remote.RemoteConstants.ADS
import com.everyone.data.remote.model.AdsResponse
import com.everyone.data.remote.model.AdsResponse.Companion.toDomainModel
import com.everyone.domain.model.Ads
import com.everyone.domain.model.base.DataState
import com.everyone.domain.model.base.NetworkError
import com.everyone.domain.repository.AdsRepository
import io.ktor.http.HttpMethod
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AdsRepositoryImpl @Inject constructor(
    private val networkHandler: NetworkHandler
) : AdsRepository {

    override suspend fun getAds(): Flow<DataState<Ads>> {
        return flow {
            networkHandler.request<AdsResponse>(
                method = HttpMethod.Get,
                url = { path(ADS) }
            ).collect { response ->
                response.data?.let {
                    emit(DataState.Success(it.toDomainModel()))
                } ?: run {
                    emit(DataState.Failure(NetworkError(response.statusCode, response.message)))
                }
            }
        }
    }
}