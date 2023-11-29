package com.everyone.data.repository

import com.everyone.data.remote.NetworkHandler
import com.everyone.data.remote.RemoteConstants.LIMIT
import com.everyone.data.remote.RemoteConstants.RANDOM
import com.everyone.data.remote.RemoteConstants.RATING
import com.everyone.data.remote.RemoteConstants.REASON
import com.everyone.data.remote.RemoteConstants.TREND
import com.everyone.data.remote.RemoteConstants.VIDEOS
import com.everyone.data.remote.model.VideosRandomResponse
import com.everyone.data.remote.model.VideosRandomResponse.Companion.toDomainModel
import com.everyone.data.remote.model.VideosTrendResponse
import com.everyone.data.remote.model.VideosTrendResponse.Companion.toDomainModel
import com.everyone.domain.model.VideosRandom
import com.everyone.domain.model.VideosTrend
import com.everyone.domain.model.base.DataState
import com.everyone.domain.model.base.NetworkError
import com.everyone.domain.repository.VideosRepository
import io.ktor.http.HttpMethod
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VideosRepositoryImpl @Inject constructor(
    private val networkHandler: NetworkHandler
) : VideosRepository {

    override suspend fun getVideosRandom(
        limit: String,
        category: String,
    ): Flow<DataState<VideosRandom>> {
        return flow {
            networkHandler.request<VideosRandomResponse>(
                method = HttpMethod.Get,
                url = {
                    path(VIDEOS, RANDOM)
                }
            ).collect { response ->
                response.data?.let {
                    emit(DataState.Success(it.toDomainModel()))
                } ?: run {
                    emit(DataState.Failure(NetworkError(response.statusCode, response.message)))
                }
            }
        }
    }

    override suspend fun putVideosRating(
        id: String,
        rating: String,
        reason: String
    ): Flow<DataState<Unit>> {
        return flow {
            networkHandler.request<Unit>(
                method = HttpMethod.Put,
                url = {
                    path(VIDEOS, id, RATING)
                },
                content = {
                    append(RATING, rating)
                    append(REASON, reason)
                }
            ).collect { response ->
                response.data?.let {
                    emit(DataState.Success(it))
                } ?: run {
                    emit(DataState.Failure(NetworkError(response.statusCode, response.message)))
                }
            }
        }
    }

    override suspend fun getVideosTrend(limit: String): Flow<DataState<VideosTrend>> {
        return flow {
            networkHandler.request<VideosTrendResponse>(
                method = HttpMethod.Get,
                url = {
                    path(VIDEOS, TREND)
                    parameters.append(LIMIT, limit)
                }
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