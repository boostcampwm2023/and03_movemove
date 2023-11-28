package com.everyone.data.repository

import com.everyone.data.remote.NetworkHandler
import com.everyone.data.remote.model.VideosRandomResponse
import com.everyone.data.remote.model.VideosRandomResponse.Companion.toDomainModel
import com.everyone.domain.model.VideosRandom
import com.everyone.domain.model.base.DataState
import com.everyone.domain.model.base.NetworkError
import com.everyone.domain.repository.MainRepository
import io.ktor.http.HttpMethod
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(private val networkHandler: NetworkHandler) : MainRepository {

    override suspend fun getVideosRandom(
        limit: String,
        category: String,
    ): Flow<DataState<VideosRandom>> {
        return flow {
            networkHandler.request<VideosRandomResponse>(
                method = HttpMethod.Get,
                url = {
                    path("videos", "random")
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
    ): Flow<Unit> {
        return flow {
            networkHandler.request<VideosRandomResponse>(
                method = HttpMethod.Put,
                url = {
                    path("videos", id, "rating")
                },
                content = {
                    append(PUT_VIDEOS_RATING_RATING, rating)
                    append(PUT_VIDEOS_RATING_REASON, reason)
                }
            ).collect()
        }
    }

    companion object {
        const val PUT_VIDEOS_RATING_RATING = "rating"
        const val PUT_VIDEOS_RATING_REASON = "reason"
    }
}